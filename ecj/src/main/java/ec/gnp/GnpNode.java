package ec.gnp;

import ec.EvolutionState;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.io.Serializable;

/**
 * Node in the Genetic Network/graph. Node contains of the subnodes (GnpSubnode) and branches (GnpBranch) to other nodes.
 * The main functionality is the evaluate method which is called by the GnpIndividual when evaluating the network.
 *
 * @author Gatis Birkens
 */
public class GnpNode extends GnpNetworkElement implements Serializable {

    public static final int JUDGEMENT_NODE = 1; //node type - judgement
    public static final int PROCESSING_NODE = 2; //node type - processing
    public static final String P_NODE = "node";

    private int type;
    private int subnodeCount = -1;
    private ObjectArrayList<GnpSubnode> subnodes; //the list of subnodes
    private ObjectArrayList<GnpBranch> branches; //the list of branches
    private GnpInitializer init;
    private EvolutionState state;
    private GnpSubnode maxQValuedSubnode;

    /**
     * Called after initialization.
     * @param id unique ID of the node within a network
     * @param genome genome of the GnpIndividual
     * @param type type of the node (judgement or processing)
     * @param startGene the gene index in the genome of the individual at which the node's genes start
     * @param state EvolutionState
     */
    public void setup (int id, double[] genome, int type, int startGene, final EvolutionState state) {

        this.id = id;
        this.genome = genome;
        this.type = type;
        this.startGene = startGene;
        this.init = ((GnpInitializer) state.initializer);
        this.state = state;
        subnodes = new ObjectArrayList<>();
        branches = new ObjectArrayList<>();

    }



    public Object copy(double[] genome) {

        GnpNode myobj = init.newGnpNodeInstance();

        myobj.setup(this.id, genome, this.type, this.startGene, this.state);

        for (GnpSubnode subnode : subnodes) {
            myobj.subnodes.add((GnpSubnode) subnode.copy(myobj, genome));
        }

        if (maxQValuedSubnode != null) {
            myobj.maxQValuedSubnode = myobj.subnodes.get(maxQValuedSubnode.getId());
        }

        for (GnpBranch branch : branches) {
            myobj.branches.add((GnpBranch) branch.copy(genome));
        }

        for (GnpBranch branch : branches) {
            myobj.branches.get(branch.getId()).setSubnode(myobj.subnodes.get(branch.getSubnode().getId()));
        }

        myobj.subnodeCount = subnodeCount;

        return myobj;
    }

    /**
     * Fetches and stores locally the count of the subnodes from genome. This is also used when generating GnpNetwork in order to know how much subnodes to generate.
     * This should never change because in case of network mutation, it's re-generated.
     * @return count of the subnodes
     */
    public int getSubnodeCount() {

        if (subnodeCount == -1) {
            subnodeCount = (int) genome[startGene];
        }

        return subnodeCount;
    }

    public void addSubnode(GnpSubnode subnode) {
        subnodes.add(subnode);
    }

    public void addBranch(GnpBranch branch) {
        branches.add(branch);
    }

    /**
     * Called by the GnpIndividual during evaluation. The procedure of evaluating the node consist of:
     * 1) selecting the subnode to evaluate by using defined GnpSubnodeSelector
     * 2) evaluating the subnode's (GnpSubnode) function (GnpFunction)
     * 3) appending the GnpFunctionResult with necessary information and returning GnpNodeEvaluationResult
     *
     * @param state EvolutionState
     * @param individual GnpIndividual
     * @param executionPath the order in which the nodes/subnodes/functions have been evaluated together with all the results withing the current GnpIndividual evaluation round
     * @param totalEvaluationCount the total evaluation count of the nodes evaluated on the GnpIndividual. Used as unique evaluationId of the subnode function
     * @param thread thread number
     * @param learn if true, rewards are set, otherwise ignored
     * @param explore if true, exploring according to GnpSubnodeSelector, otherwise no exploring (subnode selection is based only on the highest Q value, for example, in GnpEgreedy implementation)
     * @param exploringProbability if provided, it will be used to determine exploring behavior
     * @param additionalParameters additional parameters passed from the Problem
     * @return GnpNodeEvaluationResult
     */
    public synchronized GnpNodeEvaluationResult evaluate(final EvolutionState state,
                                                         GnpIndividual individual,
                                                         ObjectArrayList<GnpNodeEvaluationResult> executionPath,
                                                         int totalEvaluationCount,
                                                         int thread,
                                                         boolean learn,
                                                         boolean explore,
                                                         Double exploringProbability,
                                                         Object ... additionalParameters) {

        GnpSubnode subnode = init.getSubnodeSelector().select(subnodes, maxQValuedSubnode, thread, explore, exploringProbability);

        GnpFunctionResult result;

        if (!explore && subnode.getQ() < 0 && init.isPositiveQPathsOnly()) {
            return null;
        }

        GnpFunction function = null;

        int branchOffset = 0;
        if (type == JUDGEMENT_NODE) {

            function = subnode.getFunction();
            branchOffset = subnode.getId() * init.getFunctionLibrary().getMaxJudgementResultCount();

            if (learn && function instanceof GnpDelayedRewardFunction && function.rewardExpected()) {

                individual.addPendingReward(new GnpReward(subnode, executionPath, totalEvaluationCount));
                individual.addFunctionExecutionId(totalEvaluationCount);

            }

        } else if (type == PROCESSING_NODE) {

            function = subnode.getFunction();
            branchOffset = subnode.getId();

            if (learn && function instanceof GnpDelayedRewardFunction && function.rewardExpected()) {

                individual.addPendingReward(new GnpReward(subnode, executionPath, totalEvaluationCount));
                individual.addFunctionExecutionId(totalEvaluationCount);

            }

        }

        result = function.evaluate(state, thread, individual, totalEvaluationCount, subnode.getSubnodeParameters(), additionalParameters);
        result.setEvaluatedFunction(function);

        if (function instanceof GnpOneBranchFunction) {
            result.setBranch(GnpOneBranchFunction.DEFAULT_BRANCH_NAME);
        }

        if (function instanceof GnpOneBranchDelayedRewardFunction) {
            result.setBranch(GnpOneBranchDelayedRewardFunction.DEFAULT_BRANCH_NAME);
        }

        if (learn && !(function instanceof GnpDelayedRewardFunction) && function.rewardExpected() && result.getRewardValue() != 0) {
            init.getRewardDistributor().distributeReward(new GnpReward(subnode, executionPath, totalEvaluationCount, result.getRewardValue()));
        }

        int functionBranchId =  function.getBranchNames().getInt(result.getBranch());

        int branchId = functionBranchId + branchOffset;

        return new GnpNodeEvaluationResult(result, subnode, branchId);

    }

    public GnpSubnode getMaxQValuedSubnode() {
        return maxQValuedSubnode;
    }

    public void resetMaxQValuedSubnode() {

        for (GnpSubnode subnode : subnodes) {

            if (maxQValuedSubnode == null || subnode.getQ() > maxQValuedSubnode.getQ()) {
                maxQValuedSubnode = subnode;
            }

        }

    }

    public void setMaxQValuedSubnode(GnpSubnode maxQValuedSubnode) {
        this.maxQValuedSubnode = maxQValuedSubnode;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public ObjectArrayList<GnpSubnode> getSubnodes() {
        return subnodes;
    }

    public ObjectArrayList<GnpBranch> getBranches() {
        return branches;
    }

}
