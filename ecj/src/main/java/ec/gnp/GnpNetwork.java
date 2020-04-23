package ec.gnp;

import ec.EvolutionState;
import ec.util.Code;
import ec.util.DecodeReturn;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Serializable;
import java.util.*;

/**
 * Holds the list of GnpNodes and methods generating the network by initializing nodes based on gene maps defined in GnpInitializer.
 * Generation of the network is called each time genome might have changed or individual has been cloned, to make sure the genome corresponds to the network - a bit risky,
 * but that is the trade-off of being able to re-use mutation/crossover functionality of DoubleVectorIndividual.
 * Restrictions like loops in the network are prevented. It also tries to point each branch to a different node if possible, so two branches of the same node are not pointing to the same node.
 *
 * @author Gatis Birkens
 */
public class GnpNetwork  implements Serializable {

    private ObjectArrayList<GnpNode> networkNodes;
    private int[] startNodeIds;
    private Int2IntOpenHashMap nodeTypes;
    private GnpInitializer init;
    private double[] previousGenome;
    private EvolutionState state;
    private Int2DoubleOpenHashMap subnodeQValues;
    private Int2IntOpenHashMap subnodeFunctionIdValues;

    public void setup(EvolutionState state) {

        init = (GnpInitializer) state.initializer;
        this.state = state;

        this.startNodeIds = new int[init.getStartNodeCount()];
        Arrays.fill(this.startNodeIds, -1);

        subnodeQValues = new Int2DoubleOpenHashMap();
        subnodeFunctionIdValues = new Int2IntOpenHashMap();
        nodeTypes = new Int2IntOpenHashMap();
        networkNodes = new ObjectArrayList<>();

        /*if (networkToCopySetupFrom != null) {

            startNodeId = networkToCopySetupFrom.startNodeId;
            for (Int2IntOpenHashMap.Entry nodeTypeEntry : networkToCopySetupFrom.nodeTypes.int2IntEntrySet()) {
                nodeTypes.put(nodeTypeEntry.getIntKey(), nodeTypeEntry.getIntValue());
            }

        }*/

        if (nodeTypes.isEmpty()) {
            setNodeTypes(state);
        }

        //set the random start nodes
        if (networkNodes != null) {

            for (int i = 0; i < this.startNodeIds.length; i++) {

                if (this.startNodeIds[i] == -1) {
                    this.startNodeIds[i] = init.random[0].nextInt(init.getNodeCount());
                }

                if (init.isStartWithJudgement()) {
                    while (nodeTypes.get(this.startNodeIds[i]) != GnpNode.JUDGEMENT_NODE) {
                        this.startNodeIds[i] = init.random[0].nextInt(init.getNodeCount());
                    }
                }

            }

        }


    }

    public Object copy(double[] genome) {

        GnpNetwork myobj = new GnpNetwork();

        myobj.state = this.state;
        myobj.init = this.init;

        myobj.subnodeQValues = new Int2DoubleOpenHashMap();
        myobj.subnodeFunctionIdValues = new Int2IntOpenHashMap();
        myobj.networkNodes = new ObjectArrayList<>();
        myobj.nodeTypes = new Int2IntOpenHashMap();

        for (GnpNode node : networkNodes) {
            myobj.getNetworkNodes().add((GnpNode) node.copy(genome));
        }

        if (this.previousGenome !=null) {
            myobj.previousGenome = this.previousGenome.clone();
        }

        myobj.startNodeIds = new int[myobj.init.getStartNodeCount()];
        for (int i = 0; i < this.startNodeIds.length; i++) {
            myobj.startNodeIds[i] = this.startNodeIds[i];
        }

        for (Int2IntOpenHashMap.Entry nodeTypeEntry : nodeTypes.int2IntEntrySet()) {
            myobj.nodeTypes.put(nodeTypeEntry.getIntKey(), nodeTypeEntry.getIntValue());
        }

        return myobj;
    }

    private void setNodeTypes(final EvolutionState state) {

        //set random judgement nodes
        for (int j = 0; j < init.getJudgementNodeCount(); j++) {

            int id = init.random[0].nextInt(init.getNodeCount());
            while (nodeTypes.containsKey(id)) {
                id = init.random[0].nextInt(init.getNodeCount());
            }
            nodeTypes.put(id , GnpNode.JUDGEMENT_NODE);

        }

        //the rest are processing nodes
        for (int n = 0; n < init.getNodeCount(); n++) {

            if (!nodeTypes.containsKey(n)){
                nodeTypes.put(n, GnpNode.PROCESSING_NODE);
            }

        }

    }

    private boolean networkChanged(double[] genome) {

        boolean networkChanged = false;

        if (!Arrays.equals(previousGenome, genome)) {
            networkChanged = true;
        }

        return networkChanged;
    }

    private int getRandomWithExclusion(final EvolutionState state, int thread, int start, int end, int... exclude) {
        int random = start + init.random[thread].nextInt(end - start + 1 - exclude.length);
        for (int ex : exclude) {
            if (random < ex) {
                break;
            }
            random++;
        }
        return random;
    }

    private int getSubnodeAttributesMapKey(int nodeId, int subNodeId) {
        return init.getSubnodeGeneMapKey(nodeId, subNodeId);
    }

    public String qValuesToString() {

        StringBuilder s = new StringBuilder();

        int i = 0;

        for (GnpNode node : getNetworkNodes()) {

            for (GnpSubnode subnode : node.getSubnodes()) {

                String key = node.getId() + "|" + subnode.getId();

                s.append(Code.encode(key) + Code.encode(subnode.getQ()));
                i++;
            }

        }

        s.insert(0, Code.encode(i));

        return s.toString();
    }

    public String nodeTypesToString() {

        StringBuilder s = new StringBuilder();

        for (Int2IntOpenHashMap.Entry entry: nodeTypes.int2IntEntrySet()) {

            s.append(Code.encode((entry.getIntKey()) + "|" + entry.getIntValue()));

        }

        return s.toString();
    }

    public String startNodeIdsToString() {

        StringBuilder s = new StringBuilder();

        for (int i = 0; i < startNodeIds.length; i++) {

            s.append(Code.encode(startNodeIds[i]));

        }

        return s.toString();
    }

    /**
     * Generated the network (fills networkNodes list with new node instances).
     * @param state EvolutionState
     * @param thread thread number
     * @param genome genome of the GnpIndividual
     * @param skipFunctionChangedCall in case individual is read in, the call of the subnode's hook (afterSubnodeFunctionChanged) is not needed
     */
    public void generateNetwork(final EvolutionState state, int thread, double[] genome, boolean skipFunctionChangedCall) {

        if (networkChanged(genome)) {

            if (!networkNodes.isEmpty()) {

                subnodeQValues = new Int2DoubleOpenHashMap();
                subnodeFunctionIdValues = new Int2IntOpenHashMap();

                for (GnpNode node : networkNodes) {
                    for (GnpSubnode subnode : node.getSubnodes()) {

                        int key = getSubnodeAttributesMapKey(node.getId(), subnode.getId());

                        //store old subnode Q values to be reused for the re-generated network
                        subnodeQValues.put(key, subnode.getQ());

                        //store old subnode function id values to be reused for the re-generated network, i.e. if function changed, the default parameters are set
                        subnodeFunctionIdValues.put(key, subnode.getFunctionId());

                    }
                }

            }

            networkNodes = new ObjectArrayList<>();

            for (int n = 0; n < init.getNodeCount(); n++) {

                GnpNode node = init.newGnpNodeInstance();
                node.setup(n, genome, nodeTypes.get(n), init.getNodeGeneMap().get(n)[0], state);

                networkNodes.add(node);

                for (int sub = 0; sub < node.getSubnodeCount(); sub++) {

                    GnpSubnode subnode = init.newGnpSubnodeInstance();
                    subnode.setup(node, nodeTypes.get(n), sub, genome, init.getSubnodeGeneMap().get(init.getSubnodeGeneMapKey(n, sub))[0], state, subnodeQValues.get(getSubnodeAttributesMapKey(n, sub)));

                    node.addSubnode(subnode);

                    for (int param = 0; param < init.getSubnodeParemetersCount(); param++) {

                        Object subnodeParameter = init.newGnpSubnodeParameterInstance(param);

                        ((GnpSubnodeParameter) subnodeParameter).setup(param, genome, init.getSubnodeParamsGeneMap().get(init.getSubnodeParamsGeneMapKey(n, sub, param))[0], state);

                        subnode.addSubnodeParameter((GnpSubnodeParameter) subnodeParameter);

                        int functionId = subnode.getFunctionId();

                        //if function changed (or new) - call the hook
                        if (!skipFunctionChangedCall && (subnodeFunctionIdValues.isEmpty() || functionId != subnodeFunctionIdValues.get(getSubnodeAttributesMapKey(n, sub)))) {
                            subnode.afterSubnodeFunctionChanged();
                        }

                    }

                }

                IntArrayList branchedNodes = new IntArrayList();

                //add subnodes to branches
                for (int sub = 0; sub < node.getSubnodeCount(); sub++) {

                    GnpSubnode subnode = node.getSubnodes().get(sub);

                    if (node.getType() == GnpNode.JUDGEMENT_NODE) {

                        for (int b = 0; b < init.getFunctionLibrary().getJudgementFunction(subnode.getFunctionId()).getBranchNames().size(); b++) {

                            GnpBranch branch = new GnpBranch(b + (sub * init.getFunctionLibrary().getMaxJudgementResultCount()), genome, init.getBranchesGeneMap().get(init.getBranchGeneMapKey(n, b + (sub * init.getFunctionLibrary().getMaxJudgementResultCount())))[0]);

                            validateAndModifyTheBranch(state, thread, node, branch, branchedNodes);

                            if (init.getMaxBranchCount() < init.getNodeCount()){
                                branchedNodes.add(branch.getConnectedNodeId());
                            }

                            branch.setSubnode(subnode);
                            node.addBranch(branch);

                        }

                    }

                    if (node.getType() == GnpNode.PROCESSING_NODE) {

                        GnpBranch branch = new GnpBranch(0 + sub, genome, init.getBranchesGeneMap().get(init.getBranchGeneMapKey(n, sub))[0]);

                        validateAndModifyTheBranch(state, thread, node, branch, branchedNodes);

                        if (init.getMaxBranchCount() < init.getNodeCount()){
                            branchedNodes.add(branch.getConnectedNodeId());
                        }

                        branch.setSubnode(subnode);
                        node.addBranch(branch);

                    }

                }

            }

            previousGenome = genome.clone();

        }

    }

    //loops are not allowed, so reset the gene and make sure the branch of the node doesn't point to the node
    //Also, in case there are more nodes than branches, exclude already used nodes, so each branch points to a different node
    private void validateAndModifyTheBranch(EvolutionState state, int thread, GnpNode node, GnpBranch branch, IntArrayList usedNodes) {

        IntArrayList nodesToExclude = new IntArrayList();

        if (!usedNodes.isEmpty()) {

            nodesToExclude.addAll(usedNodes);

        }

        nodesToExclude.add(node.getId());

        Collections.sort(nodesToExclude);

        if (usedNodes.contains(branch.getConnectedNodeId()) || branch.getConnectedNodeId() == node.getId()){

            branch.setConnectedNodeId(getRandomWithExclusion(state,
                    thread,
                    Integer.valueOf(init.getConnectionsSegmentProperites().get(GnpInitializer.P_MIN_GENE)),
                    Integer.valueOf(init.getConnectionsSegmentProperites().get(GnpInitializer.P_MAX_GENE)),
                    nodesToExclude.stream().mapToInt(i -> i).toArray()));

        }

    }

    public ObjectArrayList<GnpNode> getNetworkNodes() {
        return networkNodes;
    }

    public void parseQValues(LineNumberReader reader) throws IOException {

        if (!networkNodes.isEmpty()) {

            String s = reader.readLine();
            DecodeReturn d = new DecodeReturn(s);

            Code.decode(d);
            if (d.type != DecodeReturn.T_INTEGER) {
                state.output.fatal("Individual with subnode Q values :\n" + s + "\n... does not have an integer at the beginning indicating the subnode count.");
            }
            int subnodeCount = (int) (d.l);

            // read in the q
            for (int i = 0; i < subnodeCount; i++) {

                Code.decode(d);
                String[] nodeSubnode = d.s.split("\\|");
                Code.decode(d);
                networkNodes.get(Integer.parseInt(nodeSubnode[0])).getSubnodes().get(Integer.parseInt(nodeSubnode[1])).setQ(d.d);

            }

        }

    }

    public void parseNodeTypes(LineNumberReader reader) throws IOException {


        nodeTypes = new Int2IntOpenHashMap();

        String s = reader.readLine();
        DecodeReturn d = new DecodeReturn(s);

        // read in the node types
        for (int i = 0; i < init.getNodeCount(); i++) {

            Code.decode(d);
            String[] nodeNodeType = d.s.split("\\|");
            nodeTypes.put(Integer.parseInt(nodeNodeType[0]), Integer.parseInt(nodeNodeType[1]));

        }

    }

    public int[] getStartNodeIds() {
        return startNodeIds;
    }

    public void parseStartNodes(LineNumberReader reader) throws IOException {

        startNodeIds = new int[init.getStartNodeCount()];

        String s = reader.readLine();
        DecodeReturn d = new DecodeReturn(s);

        // read in the start node ids
        for (int i = 0; i < startNodeIds.length; i++) {

            Code.decode(d);
            if (d.type != DecodeReturn.T_INTEGER) {
                state.output.fatal("Individual does not have an integer indicating the start node id.");
            }
            startNodeIds[i] = (int) (d.l);

        }

    }
}
