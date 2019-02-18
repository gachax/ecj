package ec.gnp;

import ec.EvolutionState;
import ec.util.Code;
import ec.util.DecodeReturn;
import ec.util.Parameter;
import ec.vector.DoubleVectorIndividual;
import ec.vector.VectorIndividual;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Extends the DoubleVectorIndividual so it's based on the genome defined as an array and all the mutation/crossover functionality of DoubleVectorIndividual can be re-used.
 * On top of the gene array a corresponding GnpNetwork is generated and kept up to date, in case of changing genes.
 * Individual keeps the track of it's evaluations and stores the execution paths with results so they can be used in reward distribution and reporting.
 * Evaluation depends on the network parameters like judgement and processing node count and evaluation time constraints set up in properties file, but in general it starts at the
 * start node of the network, evaluates node by node until it runs out of time.
 * There are 3 main methods which should be used when evaluating the individual:
 * 1) evaluateLearnExplore - learn by setting the rewards which means setting Q values of GnpSubnodes and explore the network according to the GnpSubnodeSelector implementation. Used when training the individual.
 * 2) evaluateDontLearnDontExplore - skip learning and exploring - meaning the network will be executed based on previously found highest Q values of the subnodes. This can be used when describing individuals to avoid unexpected results
 * 3) evaluateLearnDontExplore - learn, but skip exploring - the network will be executed based on previously found highest Q values of the subnodes, but they are subject to change due to the learning (reward setting). Can be used after the evolution process to provide an online learning possibility.
 *
 * @author Gatis Birkens
 */
public class GnpIndividual extends DoubleVectorIndividual implements Serializable {

    private GnpNetwork network;
    private EvolutionState state;
    protected GnpInitializer init = null;
    private int totalEvaluationCount;
    //a path in the network of the current evaluation
    private ObjectArrayList<GnpNodeEvaluationResult> executionPath;

    //all the delayed (in case of Delayed Reward functions) rewards which are not set yet. <Integer ID, named differently at different places, but its the same - totalEvaluationCount/functionExecutionId/evaluationId, GnpReward - reward with all the necessary parameters to be able to distribute it>
    protected Int2ObjectOpenHashMap<GnpReward> pendingRewards;

    //a parameter which keeps track of the changes in the genome to determine whether the network conforms to the genome. It's also used to indicate that data from previous evaluations should be reset.
    protected boolean initialize = true;

    //if gnp.storeAllExecPaths is set it will contain all the executionPaths until cleared - might be useful in case of some debugging.
    private ObjectArrayList<ObjectArrayList<GnpNodeEvaluationResult>> allExecPaths;

    //stores the execution ids of the functions executed

    private Int2IntOpenHashMap functionExecutionIds;
    //private ConcurrentSkipListMap<Integer, Integer> functionExecutionIds = new ConcurrentSkipListMap<>();

    private static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    private static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }

    /**
     * Called after the initialization
     * @param state EvolutionState
     * @param base Parameter
     */
    @Override
    public void setup(final EvolutionState state, final Parameter base) {

        super.setup(state, base);
        this.state = state;
        this.network = new GnpNetwork();
        this.network.setup(state);
        this.init = (GnpInitializer) state.initializer;
        initialize = true;

    }

    @Override
    public Object clone()
    {
        GnpIndividual myobj = (GnpIndividual) (super.clone());

        myobj.network = (GnpNetwork) this.network.copy(myobj.genome);

        myobj.clearEvaluations();

        return myobj;
    }

    /**
     * Evaluate the network. Learn by setting the rewards which means setting Q values of GnpSubnodes and explore the network according to the GnpSubnodeSelector implementation.
     * Used when training the individual.
     * @param state EvolutionState
     * @param thread thread number
     * @param exploringProbability if provided, it will be used to determine exploring behavior
     * @param additionalParameters additional parameters passed from Problem
     * @return LinkedHashMap<Integer, GnpNodeEvaluationResult> which is the executionPath taken during the evaluation
     */
    public ObjectArrayList<GnpNodeEvaluationResult> evaluateLearnExplore(EvolutionState state,
                                                                    int thread,
                                                                    Double exploringProbability,
                                                                    Object ... additionalParameters){
        return evaluate(state, thread, true, true, exploringProbability, additionalParameters);
    }

    /**
     * Evaluate the network. Skip learning and exploring - meaning the network will be executed based on previously found highest Q values of the subnodes.
     * This can be used when describing individuals to avoid unexpected results
     * @param state EvolutionState
     * @param thread thread number
     * @param additionalParameters additional parameters passed from Problem
     * @return List<GnpNodeEvaluationResult> which is the executionPath taken during the evaluation
     */
    public ObjectArrayList<GnpNodeEvaluationResult> evaluateDontLearnDontExplore(EvolutionState state,
                                                                            int thread,
                                                                            Object ... additionalParameters){
        return evaluate(state, thread, false, false, null, additionalParameters);
    }

    /**
     * Evaluate the network. Learn, but skip exploring - the network will be executed based on previously found highest Q values of the subnodes, but they are subject to change due to the learning (reward setting).
     * Can be used after the evolution process to provide an online learning possibility.
     * @param state EvolutionState
     * @param thread thread number
     * @param additionalParameters additional parameters passed from Problem
     * @return ObjectArrayList<GnpNodeEvaluationResult> which is the executionPath taken during the evaluation
     */
    public ObjectArrayList<GnpNodeEvaluationResult> evaluateLearnDontExplore(EvolutionState state,
                                                                                int thread,
                                                                                Object ... additionalParameters){
        return evaluate(state, thread, true, false, null, additionalParameters);
    }

    /**
     * As individual stores a lot of information about the execution of it's network, and there is no clear way of telling if all the evaluations of the individual are performed and
     * whether or not the information from the evaluation will be used by the evaluator, the cleanup is left on the shoulders of the one evaluating this individual (mostly the Problem).
     * If this is not called after the evaluation of the individual is finished, it  may lead to a memory leak and potentially unpredictable behavior of reward distribution
     * in case the individual is evaluated more than once in different environments.
     * The best place to call this from is at the end of the evaluate method of the Problem (example can be found in AntGnp problem) which evaluated the whole individual.
     * It should not be called in between the evaluation calls to this individual within one run of Problem.evaluate().
     *
     * It can be considered to place this call in a specific problem implementation of Gnp by overriding the evaluate method of Problem.
     */
    public void afterEvaluation() {
        executionPath = new ObjectArrayList<>();
        clearEvaluations();
    }

    /**
     * Clears the data from previous evaluations. Called from afterEvaluation and evaluate if the individual should be initialized.
     */
    private void clearEvaluations() {

        pendingRewards = new Int2ObjectOpenHashMap<>();
        totalEvaluationCount = 0;
        clearFunctionExecutions();
        allExecPaths = new ObjectArrayList<>();
        functionExecutionIds = new Int2IntOpenHashMap();

    }

    /**
     *
     * @param state EvolutionState
     * @param thread thread number
     * @param learn Learn by setting the rewards which means setting Q values of GnpSubnodes
     * @param explore Explore the network according to the GnpSubnodeSelector implementation
     * @param exploringProbability if provided, it will be used to determine exploring behavior
     * @param additionalParameters additional parameters passed from Problem
     * @return ObjectArrayList<GnpNodeEvaluationResult> which is the executionPath taken during the evaluation
     */
    private ObjectArrayList<GnpNodeEvaluationResult> evaluate(EvolutionState state,
                                                                    int thread,
                                                                    boolean learn,
                                                                    boolean explore,
                                                                    Double exploringProbability,
                                                                    Object ... additionalParameters){

        if (initialize) {

            network.generateNetwork(state,  thread, genome, false);
            clearEvaluations();
            initialize = false;

        }

        int remainingTime = init.getMaxTime();
        GnpNodeEvaluationResult result;

        GnpNode currentNode = network.getStartNode();

        executionPath = new ObjectArrayList<>();

        while (remainingTime > 0) {

            if (currentNode.getType() == GnpNode.JUDGEMENT_NODE) {
                remainingTime -= init.getJudgementTime();
            }

            if (currentNode.getType() == GnpNode.PROCESSING_NODE) {
                remainingTime -= init.getProcessingTime();
            }

            result = currentNode.evaluate(state, this, executionPath, totalEvaluationCount, thread, learn, explore, exploringProbability, additionalParameters);

            result.setEvaluatedNode(currentNode);
            result.setEvaluationId(totalEvaluationCount);

            executionPath.add(result);

            totalEvaluationCount++;

            currentNode = network.getNetworkNodes().get(currentNode.getBranches().get(result.getBranchId()).getConnectedNodeId());

        }

        if (init.isStoreAllExecPaths()) {
            allExecPaths.add(executionPath);
        }

        return executionPath;

    }

    /**
     * regirter pedning reward
     * @param reward GnpReward
     */
    void addPendingReward(GnpReward reward) {
        pendingRewards.put(reward.getEvaluationId(), reward);
    }

    /**
     * If delayed reward functionality is used, then rewards can be set later on by calling this method.
     * @param evaluationId the id of the evaluation, which can be obtained in evaluate method of the GnpFunction
     * @param rewardValue reward amount to be distributed
     */
    public void setDelayedReward(int evaluationId, Double rewardValue) {

        GnpReward reward = pendingRewards.get(evaluationId);

        if (reward != null) {

            if (rewardValue != null && rewardValue != 0) {

                reward.setRewardValue(rewardValue);

                init.getRewardDistributor().distributeReward(reward);

            }

            pendingRewards.remove(evaluationId);

            clearFunctionExecution(evaluationId);

        }
        /*else {
            throw new RuntimeException("Pending reward with evaluation id: " + evaluationId + " not found!");
        }*/

    }

    /**
     * String representation of the execution path returned by the evaluation methods.
     *
     * @param executionPath execution path returned by the evaluation methods
     * @return String representation of the execution path
     */
    public String stringOutputExecutionPath(List<GnpNodeEvaluationResult> executionPath) {

        StringBuilder executionPathSb = new StringBuilder();

        int i = 0;
        for (GnpNodeEvaluationResult result : executionPath) {

            int type = result.getEvaluatedNode().getType();
            String typ = null;

            if (type == GnpNode.JUDGEMENT_NODE) {
                typ = "J ";
            }
            if (type == GnpNode.PROCESSING_NODE) {
                typ = "P ";
            }

            executionPathSb.append(padLeft(typ + String.valueOf(result.getEvaluatedNode().getId()),6));
            executionPathSb.append(" (sn " + padLeft(String.valueOf(result.getEvaluatedSubnode().getId()), 4) +
                    ", Q atExec = " + String.format("%10.3e", result.getExecTimeQVlue()) +
                    ", Q = " + String.format("%10.3e", result.getEvaluatedSubnode().getQ()));
            executionPathSb.append(", f = ");

            if (type == GnpNode.JUDGEMENT_NODE) {
                executionPathSb.append(padRight(init.getFunctionLibrary().getJudgementFunction(result.getEvaluatedSubnode().getFunctionId()).getName(result.getEvaluatedSubnode().getSubnodeParameters()), 15));
            }

            if (type == GnpNode.PROCESSING_NODE) {
                executionPathSb.append(padRight(init.getFunctionLibrary().getProcessingFunction(result.getEvaluatedSubnode().getFunctionId()).getName(result.getEvaluatedSubnode().getSubnodeParameters()), 15));
            }

            executionPathSb.append(")");

            i++;
            if (i < executionPath.size()) {
                executionPathSb.append(" --> ");
            }
        }

        return executionPathSb.toString();

    }

    /**
     * Can be used for debugging purposes to see the actual genome representation as the network. Poor performance due to multiple loops over the genome.
     * @return
     */
    public String stringOutputGenomeVsNetwork() {

        StringBuilder sb = new StringBuilder();

        if (network.getNetworkNodes().isEmpty()) {
            network.generateNetwork(state,  0, genome, false);
        }

        if (!network.getNetworkNodes().isEmpty()) {

            for (int gIndex = 0; gIndex < genome.length; gIndex++) {

                sb.append(padRight(gIndex + ", " + genome[gIndex] + ": ", 30));
                //try to find matching genes in gene maps
                nodeLoop:
                for (int n = 0; n < init.getNodeCount(); n++) {

                    GnpNode node = network.getNetworkNodes().get(n);

                    for (int i = init.getNodeGeneMap().get(n)[0]; i < init.getNodeGeneMap().get(n)[0] + init.getNodeGeneMap().get(n)[1]; i++) {

                        if (gIndex == i) {

                            String type = null;
                            if (node.getType() == GnpNode.JUDGEMENT_NODE) {
                                type = " type JUDGEMENT";
                            }
                            if (node.getType() == GnpNode.PROCESSING_NODE) {
                                type = " type PROCESSING";
                            }
                            sb.append("node " + n + ", " + type + ", subnode count: " + node.getSubnodeCount() + ", subnode list size: " + node.getSubnodes().size());
                            break nodeLoop;

                        }

                    }

                    for (int sub = 0; sub < node.getSubnodes().size(); sub++) {

                        GnpSubnode subnode = node.getSubnodes().get(sub);

                        for (int i = init.getSubnodeGeneMap().get(init.getSubnodeGeneMapKey(n, sub))[0];
                             i < init.getSubnodeGeneMap().get(init.getSubnodeGeneMapKey(n, sub))[0] + init.getSubnodeGeneMap().get(init.getSubnodeGeneMapKey(n, sub))[1];
                             i++) {

                            if (gIndex == i) {
                                sb.append("node " + n + " subnode " + sub);

                                if (node.getType() == GnpNode.JUDGEMENT_NODE && gIndex == subnode.getStartGene()) {

                                    GnpFunction function = init.getFunctionLibrary().getJudgementFunction(subnode.getFunctionId());
                                    sb.append(", " + function.getName(subnode.getSubnodeParameters()) + "(" + subnode.getFunctionId() + ")");

                                }

                                if (node.getType() == GnpNode.PROCESSING_NODE  && gIndex == subnode.getStartGene() + 1) {

                                    GnpFunction function = init.getFunctionLibrary().getProcessingFunction(subnode.getFunctionId());
                                    sb.append(", " + function.getName(subnode.getSubnodeParameters()) + "(" + subnode.getFunctionId() + ")");

                                }

                                sb.append(", Q " + subnode.getQ());

                                break nodeLoop;
                            }

                        }

                        for (int subParam = 0; subParam < subnode.getSubnodeParameters().size(); subParam++) {

                            for (int i = init.getSubnodeParamsGeneMap().get(init.getSubnodeParamsGeneMapKey(n, sub, subParam))[0];
                                 i < init.getSubnodeParamsGeneMap().get(init.getSubnodeParamsGeneMapKey(n, sub, subParam))[0] + init.getSubnodeParamsGeneMap().get(init.getSubnodeParamsGeneMapKey(n, sub, subParam))[1];
                                 i++) {

                                if (gIndex == i) {
                                    sb.append("node " + n + " subnode " + sub + " parameter " + subParam);
                                    break nodeLoop;
                                }

                            }

                        }

                    }

                    for (int b = 0; b < node.getBranches().size(); b++) {

                        for (int i = init.getBranchesGeneMap().get(init.getBranchGeneMapKey(n, b))[0];
                             i < init.getBranchesGeneMap().get(init.getBranchGeneMapKey(n, b))[0] + init.getBranchesGeneMap().get(init.getBranchGeneMapKey(n, b))[1];
                             i++) {

                            if (gIndex == i) {

                                sb.append("node " + n + ", branch " + b + ", subnode: " + node.getBranches().get(b).getSubnode().getId() + ", connected node: " + node.getBranches().get(b).getConnectedNodeId());

                                break nodeLoop;

                            }

                        }

                    }

                }

                sb.append(System.lineSeparator());

            }
        }

        return sb.toString();

    }

    /**
     * .dot graph representation of the execution path returned by the evaluation methods.
     *
     * @param executionPath execution path returned by the evaluation methods
     * @return .dot graph of the execution path
     */
    public String graphOutputExecutionPath(List<GnpNodeEvaluationResult> executionPath) {

        List<GnpNode> renderedNodes = new ArrayList<>();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("digraph g {");
        stringBuilder.append("node [shape=record] [style=rounded];");

        for (GnpNodeEvaluationResult result : executionPath) {

            if (!renderedNodes.contains( result.getEvaluatedNode())) {

                renderNode(stringBuilder, result.getEvaluatedNode(), result);
                renderedNodes.add(result.getEvaluatedNode());

            }

        }

        //connections
        int i = 1;
        for (GnpNodeEvaluationResult result : executionPath) {

            GnpNode currNode = result.getEvaluatedNode();

            stringBuilder.append(System.lineSeparator());

            Double reward = result.getFunctionResult().getRewardValue();
            String rw = "";
            if (reward != null && reward != 0.0){
                rw =  "(r" + reward + ")";
            }

            if (i < executionPath.size()) {
                stringBuilder.append("n" + currNode.getId() + ":b" + result.getBranchId() + " -> n"
                        + currNode.getBranches().get(result.getBranchId()).getConnectedNodeId() + ":n" + currNode.getBranches().get(result.getBranchId()).getConnectedNodeId()
                        + " [label=<<FONT POINT-SIZE=\"10\">" + i + rw + "</FONT>>];");
            }

            i++;

        }

        stringBuilder.append("}");
        return stringBuilder.toString();

    }

    /**
     * The network as represented as .dot graph
     * @return .dot graph
     */
    public String graphOutputNetwork() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("digraph g {");
        //stringBuilder.append("graph [ordering=out];");
        stringBuilder.append("node [shape=record] [style=rounded];");

        for (GnpNode currNode : network.getNetworkNodes()) {

            renderNode(stringBuilder, currNode, null);

        }

        //connections
        for (GnpNode currNode : network.getNetworkNodes()) {

            for (int b = 0; b < currNode.getBranches().size(); b++) {

                GnpBranch branch = currNode.getBranches().get(b);

                if (branch.getConnectedNodeId() != -1) {

                    stringBuilder.append(System.lineSeparator());
                    stringBuilder.append("n" + currNode.getId() + ":b" + branch.getId() + " -> n" + branch.getConnectedNodeId() + ":n" + branch.getConnectedNodeId() + ";");

                }

            }

        }

        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    private void renderNode(StringBuilder stringBuilder, GnpNode currNode, GnpNodeEvaluationResult result) {
        stringBuilder.append(System.lineSeparator());

        String nodeType = null;
        if (currNode.getType() == GnpNode.JUDGEMENT_NODE) {
            nodeType = "J";
        }
        if (currNode.getType() == GnpNode.PROCESSING_NODE) {
            nodeType = "P";
        }

        stringBuilder.append(" n" + currNode.getId());

        if (currNode.getId() == network.getStartNode().getId()) {
            stringBuilder.append(" [color=blue]");
        }

        stringBuilder.append(" [label=\"<n" + currNode.getId() + "> " + nodeType + currNode.getId());

        for (int sub = 0; sub < currNode.getSubnodes().size(); sub++) {

            GnpSubnode subnode = currNode.getSubnodes().get(sub);

            GnpFunction function = null;

            int branchOffset = 0;

            if (currNode.getType() == GnpNode.JUDGEMENT_NODE) {
                function = init.getFunctionLibrary().getJudgementFunction(subnode.getFunctionId());
                branchOffset = sub * init.getFunctionLibrary().getMaxJudgementResultCount();
            }
            if (currNode.getType() == GnpNode.PROCESSING_NODE) {
                function = init.getFunctionLibrary().getProcessingFunction(subnode.getFunctionId());
                branchOffset = sub;
            }

            if (result != null) {
                stringBuilder.append("| sn" + subnode.getId() + " q=" + String.format("%10.3e", subnode.getQ()) + ". " + function.getName(subnode.getSubnodeParameters()) + " |{");
            } else {
                stringBuilder.append("| sn" + subnode.getId() + ". " + function.getName(subnode.getSubnodeParameters()) + " |{");
            }

            for (int b = 0; b < function.getBranchNames().size(); b++) {

                if (b > 0) {
                    stringBuilder.append("|");
                }

                int branchId = currNode.getBranches().get(b + branchOffset).getId();
                if (result != null && result.getBranchId() == branchId) {

                    stringBuilder.append("<b" + branchId + "> * " + function.getBranchName(b) + " " + currNode.getBranches().get(b + branchOffset).getConnectedNodeId());

                } else {
                    stringBuilder.append("<b" + branchId + "> " + function.getBranchName(b) + " " + currNode.getBranches().get(b + branchOffset).getConnectedNodeId());
                }

                if (b == function.getBranchNames().size() - 1) {
                    stringBuilder.append("}");
                }

            }

        }

        stringBuilder.append("\"];");
    }


    /**
     * Add funciton executionId to functionExecutionIds - used during evaluation of GnpNode
     * @param functionExecutionId
     */
    public void addFunctionExecutionId(int functionExecutionId) {
        functionExecutionIds.put(functionExecutionId, functionExecutionId);
    }

    public void clearFunctionExecutions() {
        functionExecutionIds = new Int2IntOpenHashMap();
    }

    public void clearFunctionExecution(int functionExecutionId) {
        functionExecutionIds.remove(functionExecutionId);
    }

    public Int2IntOpenHashMap getFunctionExecutionIds() {
        return functionExecutionIds;
    }

    public void clearExecutionPaths() {
        allExecPaths = new ObjectArrayList<>();
    }

    public ObjectArrayList<ObjectArrayList<GnpNodeEvaluationResult>> getAllExecPaths() {
        return allExecPaths;
    }

    public GnpNetwork getNetwork() {
        network.generateNetwork(state, 0, genome, false);
        return network;
    }

    @Override
    public void reset(EvolutionState state, int thread) {

        super.reset(state, thread);
        initialize = true;

    }

    @Override
    public void defaultCrossover(EvolutionState state, int thread, VectorIndividual ind) {
        super.defaultCrossover(state, thread, ind);
        initialize = true;
        ((GnpIndividual)ind).setInitialize(true);
    }

    @Override
    public void defaultMutate(EvolutionState state, int thread) {
        super.defaultMutate(state, thread);
        initialize = true;
    }

    @Override
    public void setGenome(Object gen){
        super.setGenome(gen);
        initialize = true;
    }

    @Override
    public void join(Object[] pieces){
        super.join(pieces);
        initialize = true;
    }

    @Override
    protected void parseGenotype(final EvolutionState state,
                                 final LineNumberReader reader) throws IOException {
        super.parseGenotype(state, reader);
        initialize = true;
    }

    @Override
    public void readGenotype(final EvolutionState state,
                             final DataInput dataInput) throws IOException {
        super.readGenotype(state, dataInput);
        initialize = true;
    }

    @Override
    public void clamp() {
        super.clamp();
        initialize = true;
    }

    @Override
    public void setGenomeLength(int len) {
        super.setGenomeLength(len);
        initialize = true;
    }


    @Override
    public void printIndividualForHumans(EvolutionState state, int log) {
        super.printIndividualForHumans(state, log);
        state.output.println(network.nodeTypesToString(), log);
        if (network.getNetworkNodes().isEmpty()) {
            network.generateNetwork(state,  0, genome, false);
        }
        state.output.println(Code.encode(network.getStartNode().getId()), log);
        state.output.println(network.qValuesToString(), log);
    }

    @Override
    public void printIndividual(EvolutionState state, int log) {
        super.printIndividual(state, log);
        if (network.getNetworkNodes().isEmpty()) {
            network.generateNetwork(state,  0, genome, false);
        }
        state.output.println(network.nodeTypesToString(), log);
        state.output.println(Code.encode(network.getStartNode().getId()), log);
        state.output.println(network.qValuesToString(), log);
    }

    @Override
    public void printIndividual(EvolutionState state, PrintWriter writer) {
        super.printIndividual(state, writer);
        if (network.getNetworkNodes().isEmpty()) {
            network.generateNetwork(state,  0, genome, false);
        }
        writer.println(network.nodeTypesToString());
        writer.println(Code.encode(network.getStartNode().getId()));
        writer.println(network.qValuesToString());
    }

    public void setInitialize(boolean initialize) {
        this.initialize = initialize;
    }

    @Override
    public void readIndividual(EvolutionState state, LineNumberReader reader) throws IOException {
        super.readIndividual(state, reader);
        network.parseNodeTypes(reader);
        network.generateNetwork(state, 0 , genome, true);

        String s = reader.readLine();
        DecodeReturn d = new DecodeReturn(s);
        Code.decode(d);
        if (d.type != DecodeReturn.T_INTEGER) {
            state.output.fatal("Individual does not have an integer indicating the start node id.");
        }
        network.setStartNodeId((int) (d.l));
        network.parseQValues(reader);
    }
}
