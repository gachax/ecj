package ec.gnp;

import ec.EvolutionState;
import ec.Evolve;
import ec.Individual;
import ec.gnp.implementations.GnpIntegerAndDoubleGenesSubnodeParameter;
import ec.gnp.implementations.GnpTwoIntegerGenesSubnodeParameter;
import ec.util.Output;
import ec.util.Parameter;
import ec.util.ParameterDatabase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class TestGnp {

    private final String paramsFilePath = System.getProperty("user.dir") + "/ecj/src/test/java/ec/gnp/" + "gnp_test.params";
    private final String delayedRewardsParamsFilePath = System.getProperty("user.dir") + "/ecj/src/test/java/ec/gnp/" + "gnp_test_delayed_rewards.params";
    private final String sarsaEligibilityTracesParamsFilePath = System.getProperty("user.dir") + "/ecj/src/test/java/ec/gnp/" + "gnp_test_sarsa_eligibility_traces.params";
    private final String sarsaEligibilityTracesDelayedRewardsParamsFilePath = System.getProperty("user.dir") + "/ecj/src/test/java/ec/gnp/" + "gnp_test_sarsa_eligibility_traces_delayed_rewards.params";

    private List<EvolutionState> states = new ArrayList<>();
    private EvolutionState state;
    private EvolutionState delayedRewardsState;
    private EvolutionState eligibilityTracesState;
    private EvolutionState eligibilityTracesDelayedRewardsState;
    private final int totalNodeGeneCount = 17;

    @Before
    public void setup() throws IOException {

        //Initialize everything without running the evolution
        ParameterDatabase dbase = new ParameterDatabase(new File(paramsFilePath));
        Output out = Evolve.buildOutput();
        out.getLog(0).silent = true;
        out.getLog(1).silent = true;
        state = Evolve.initialize(dbase, 0, out);
        state.startFresh();
        states.add(state);


        //Initialize everything without running the evolution
        ParameterDatabase delayedRewardsDbase = new ParameterDatabase(new File(delayedRewardsParamsFilePath));
        Output delayedRewardsOut = Evolve.buildOutput();
        delayedRewardsOut.getLog(0).silent = true;
        delayedRewardsOut.getLog(1).silent = true;
        delayedRewardsState = Evolve.initialize(delayedRewardsDbase, 0, delayedRewardsOut);
        delayedRewardsState.startFresh();
        states.add(delayedRewardsState);

        //Initialize everything without running the evolution
        ParameterDatabase eligibilityTracesDbase = new ParameterDatabase(new File(sarsaEligibilityTracesParamsFilePath));
        Output eligibilityTracesOut = Evolve.buildOutput();
        eligibilityTracesOut.getLog(0).silent = true;
        eligibilityTracesOut.getLog(1).silent = true;
        eligibilityTracesState = Evolve.initialize(eligibilityTracesDbase, 0, delayedRewardsOut);
        eligibilityTracesState.startFresh();
        states.add(eligibilityTracesState);

        //Initialize everything without running the evolution
        ParameterDatabase eligibilityTracesDelayedRewardsDbase = new ParameterDatabase(new File(sarsaEligibilityTracesDelayedRewardsParamsFilePath));
        Output eligibilityTracesDelayedRewardsOut = Evolve.buildOutput();
        eligibilityTracesDelayedRewardsOut.getLog(0).silent = true;
        eligibilityTracesDelayedRewardsOut.getLog(1).silent = true;
        eligibilityTracesDelayedRewardsState = Evolve.initialize(eligibilityTracesDelayedRewardsDbase, 0, delayedRewardsOut);
        eligibilityTracesDelayedRewardsState.startFresh();
        states.add(eligibilityTracesDelayedRewardsState);

    }

    @After
    public void cleanup() {

        for (EvolutionState state : states) {
            Evolve.cleanup(state);
        }

    }


    @Test
    public void chromosomeInitializedAccordingToTheSetup() throws IOException {

        int stateCount = 0;

        for (EvolutionState state : states) {

            //System.out.println("State: " + stateCount);
            stateCount++;

            assertEquals(119, state.parameters.getInt(new Parameter("vector.species.genome-size"), null));

            assertEquals(totalNodeGeneCount, state.parameters.getInt(new Parameter("vector.species.chunk-size"), null));

            assertEquals(70, state.parameters.getInt(new Parameter("vector.species.num-segments"), null));

            int s = 0;
            for (int n = 0; n < 7; n++) {

                assertEquals(0 + (n * totalNodeGeneCount), state.parameters.getInt(new Parameter("vector.species.segment." + s + ".start"), null));
                assertEquals("integer-random-walk", state.parameters.getString(new Parameter("vector.species.segment." + s + ".mutation-type"), null));
                assertEquals("0.1", state.parameters.getString(new Parameter("vector.species.segment." + s + ".mutation-prob"), null));
                assertEquals("1", state.parameters.getString(new Parameter("vector.species.segment." + s + ".min-gene"), null));
                assertEquals("2", state.parameters.getString(new Parameter("vector.species.segment." + s + ".max-gene"), null));
                s++;
                assertEquals(1 + (n * totalNodeGeneCount), state.parameters.getInt(new Parameter("vector.species.segment." + s + ".start"), null));
                assertEquals("integer-reset", state.parameters.getString(new Parameter("vector.species.segment." + s + ".mutation-type"), null));
                assertEquals("0.1", state.parameters.getString(new Parameter("vector.species.segment." + s + ".mutation-prob"), null));
                assertEquals("0", state.parameters.getString(new Parameter("vector.species.segment." + s + ".min-gene"), null));
                assertEquals("3", state.parameters.getString(new Parameter("vector.species.segment." + s + ".max-gene"), null));
                s++;
                assertEquals(2 + (n * totalNodeGeneCount), state.parameters.getInt(new Parameter("vector.species.segment." + s + ".start"), null));
                assertEquals("integer-reset", state.parameters.getString(new Parameter("vector.species.segment." + s + ".mutation-type"), null));
                assertEquals("0.1", state.parameters.getString(new Parameter("vector.species.segment." + s + ".mutation-prob"), null));
                assertEquals("0", state.parameters.getString(new Parameter("vector.species.segment." + s + ".min-gene"), null));
                assertEquals("2", state.parameters.getString(new Parameter("vector.species.segment." + s + ".max-gene"), null));
                s++;
                assertEquals(3 + (n * totalNodeGeneCount), state.parameters.getInt(new Parameter("vector.species.segment." + s + ".start"), null));
                assertEquals("integer-reset", state.parameters.getString(new Parameter("vector.species.segment." + s + ".mutation-type"), null));
                assertEquals("0.2", state.parameters.getString(new Parameter("vector.species.segment." + s + ".mutation-prob"), null));
                assertEquals("-1", state.parameters.getString(new Parameter("vector.species.segment." + s + ".min-gene"), null));
                assertEquals("100", state.parameters.getString(new Parameter("vector.species.segment." + s + ".max-gene"), null));
                s++;
                assertEquals(6 + (n * totalNodeGeneCount), state.parameters.getInt(new Parameter("vector.species.segment." + s + ".start"), null));
                assertEquals("gauss", state.parameters.getString(new Parameter("vector.species.segment." + s + ".mutation-type"), null));
                assertEquals("0.1", state.parameters.getString(new Parameter("vector.species.segment." + s + ".mutation-stdev"), null));
                assertEquals("20", state.parameters.getString(new Parameter("vector.species.segment." + s + ".out-of-bounds-retries"), null));
                assertEquals("0.0", state.parameters.getString(new Parameter("vector.species.segment." + s + ".min-gene"), null));
                assertEquals("50.5", state.parameters.getString(new Parameter("vector.species.segment." + s + ".max-gene"), null));
                assertEquals("false", state.parameters.getString(new Parameter("vector.species.segment." + s + ".mutation-bounded"), null));
                s++;
                assertEquals(7 + (n * totalNodeGeneCount), state.parameters.getInt(new Parameter("vector.species.segment." + s + ".start"), null));
                assertEquals("integer-reset", state.parameters.getString(new Parameter("vector.species.segment." + s + ".mutation-type"), null));
                assertEquals("0.1", state.parameters.getString(new Parameter("vector.species.segment." + s + ".mutation-prob"), null));
                assertEquals("0", state.parameters.getString(new Parameter("vector.species.segment." + s + ".min-gene"), null));
                assertEquals("3", state.parameters.getString(new Parameter("vector.species.segment." + s + ".max-gene"), null));
                s++;
                assertEquals(8 + (n * totalNodeGeneCount), state.parameters.getInt(new Parameter("vector.species.segment." + s + ".start"), null));
                assertEquals("integer-reset", state.parameters.getString(new Parameter("vector.species.segment." + s + ".mutation-type"), null));
                assertEquals("0.1", state.parameters.getString(new Parameter("vector.species.segment." + s + ".mutation-prob"), null));
                assertEquals("0", state.parameters.getString(new Parameter("vector.species.segment." + s + ".min-gene"), null));
                assertEquals("2", state.parameters.getString(new Parameter("vector.species.segment." + s + ".max-gene"), null));
                s++;
                assertEquals(9 + (n * totalNodeGeneCount), state.parameters.getInt(new Parameter("vector.species.segment." + s + ".start"), null));
                assertEquals("integer-reset", state.parameters.getString(new Parameter("vector.species.segment." + s + ".mutation-type"), null));
                assertEquals("0.2", state.parameters.getString(new Parameter("vector.species.segment." + s + ".mutation-prob"), null));
                assertEquals("-1", state.parameters.getString(new Parameter("vector.species.segment." + s + ".min-gene"), null));
                assertEquals("100", state.parameters.getString(new Parameter("vector.species.segment." + s + ".max-gene"), null));
                s++;
                assertEquals(12 + (n * totalNodeGeneCount), state.parameters.getInt(new Parameter("vector.species.segment." + s + ".start"), null));
                assertEquals("gauss", state.parameters.getString(new Parameter("vector.species.segment." + s + ".mutation-type"), null));
                assertEquals("0.1", state.parameters.getString(new Parameter("vector.species.segment." + s + ".mutation-stdev"), null));
                assertEquals("20", state.parameters.getString(new Parameter("vector.species.segment." + s + ".out-of-bounds-retries"), null));
                assertEquals("0.0", state.parameters.getString(new Parameter("vector.species.segment." + s + ".min-gene"), null));
                assertEquals("50.5", state.parameters.getString(new Parameter("vector.species.segment." + s + ".max-gene"), null));
                assertEquals("false", state.parameters.getString(new Parameter("vector.species.segment." + s + ".mutation-bounded"), null));
                s++;
                assertEquals(13 + (n * totalNodeGeneCount), state.parameters.getInt(new Parameter("vector.species.segment." + s + ".start"), null));
                assertEquals("integer-random-walk", state.parameters.getString(new Parameter("vector.species.segment." + s + ".mutation-type"), null));
                assertEquals("0.1", state.parameters.getString(new Parameter("vector.species.segment." + s + ".mutation-prob"), null));
                s++;
            }

        }

    }

    @Test
    public void geneMapsMatchTheNodes() {

        for (EvolutionState state : states) {

            GnpInitializer initializer = (GnpInitializer) state.initializer;

            Map<Integer, int[]> nodeGeneMap = ((GnpInitializer) state.initializer).getNodeGeneMap();
            Map<String, int[]> subnodeGeneMap = ((GnpInitializer) state.initializer).getSubnodeGeneMap();
            Map<String, int[]> subnodeParamsGeneMap = ((GnpInitializer) state.initializer).getSubnodeParamsGeneMap();
            Map<String, int[]> branchGeneMap = ((GnpInitializer) state.initializer).getBranchesGeneMap();

            for (int n = 0; n < 7; n++) {

                assertArrayEquals(new int[]{0 + (n * totalNodeGeneCount), 1}, nodeGeneMap.get(n));

                assertArrayEquals(new int[]{1 + (n * totalNodeGeneCount), 2}, subnodeGeneMap.get(initializer.getSubnodeGeneMapKey(n, 0)));
                assertArrayEquals(new int[]{3 + (n * totalNodeGeneCount), 2}, subnodeParamsGeneMap.get(initializer.getSubnodeParamsGeneMapKey(n, 0, 0)));
                assertArrayEquals(new int[]{5 + (n * totalNodeGeneCount), 2}, subnodeParamsGeneMap.get(initializer.getSubnodeParamsGeneMapKey(n, 0, 1)));

                assertArrayEquals(new int[]{7 + (n * totalNodeGeneCount), 2}, subnodeGeneMap.get(initializer.getSubnodeGeneMapKey(n, 1)));
                assertArrayEquals(new int[]{9 + (n * totalNodeGeneCount), 2}, subnodeParamsGeneMap.get(initializer.getSubnodeParamsGeneMapKey(n, 1, 0)));
                assertArrayEquals(new int[]{11 + (n * totalNodeGeneCount), 2}, subnodeParamsGeneMap.get(initializer.getSubnodeParamsGeneMapKey(n, 1, 1)));

                assertArrayEquals(new int[]{13 + (n * totalNodeGeneCount), 1}, branchGeneMap.get(initializer.getBranchGeneMapKey(n, 0)));
                assertArrayEquals(new int[]{14 + (n * totalNodeGeneCount), 1}, branchGeneMap.get(initializer.getBranchGeneMapKey(n, 1)));
                assertArrayEquals(new int[]{15 + (n * totalNodeGeneCount), 1}, branchGeneMap.get(initializer.getBranchGeneMapKey(n, 2)));
                assertArrayEquals(new int[]{16 + (n * totalNodeGeneCount), 1}, branchGeneMap.get(initializer.getBranchGeneMapKey(n, 3)));

            }

        }

    }

    @Test
    public void networkCorrespondsToGenome () {

        for (EvolutionState state : states) {

            for (int i = 0; i < state.population.subpops.get(0).individuals.size(); i++) {

                GnpIndividual ind = (GnpIndividual) state.population.subpops.get(0).individuals.get(i);

                GnpNetwork network = ind.getNetwork();

                System.out.println("---------------------- INDIVIDUAL " + i);
                //System.out.println(ind.stringOutputGenomeVsNetwork());

                //System.out.println(ind.graphOutputNetwork());

                //General checks
                int judgementNodeCount = 0;
                int processingNodeCount = 0;
                for (GnpNode node : network.getNetworkNodes()) {
                    if (node.getType() == GnpNode.JUDGEMENT_NODE) {
                        judgementNodeCount++;
                    }
                    if (node.getType() == GnpNode.PROCESSING_NODE) {
                        processingNodeCount++;
                    }
                }
                assertEquals(judgementNodeCount, state.parameters.getInt(new Parameter("gnp.judgementNodeCount"), null));
                assertEquals(processingNodeCount, state.parameters.getInt(new Parameter("gnp.processingNodeCount"), null));


                for (int n = 0; n < 7; n++) {

                    //System.out.println("Asseritng node: " + n);

                    GnpNode node = network.getNetworkNodes().get(n);
                    assertEquals((int) ind.genome[0 + (n * totalNodeGeneCount)], node.getSubnodeCount());

                    if (node.getType() == GnpNode.JUDGEMENT_NODE) {
                        assertEquals((int) ind.genome[1 + (n * totalNodeGeneCount)], node.getSubnodes().get(0).getFunctionId());
                    }
                    if (node.getType() == GnpNode.PROCESSING_NODE) {
                        assertEquals((int) ind.genome[2 + (n * totalNodeGeneCount)], node.getSubnodes().get(0).getFunctionId());
                    }

                    assertEquals((int) ind.genome[3 + (n * totalNodeGeneCount)], ((GnpTwoIntegerGenesSubnodeParameter) node.getSubnodes().get(0).getSubnodeParameters().get(0)).getI1());
                    assertEquals((int) ind.genome[4 + (n * totalNodeGeneCount)], ((GnpTwoIntegerGenesSubnodeParameter) node.getSubnodes().get(0).getSubnodeParameters().get(0)).getI2());
                    assertEquals((int) ind.genome[5 + (n * totalNodeGeneCount)], ((GnpIntegerAndDoubleGenesSubnodeParameter) node.getSubnodes().get(0).getSubnodeParameters().get(1)).getI1());
                    assertEquals(ind.genome[6 + (n * totalNodeGeneCount)], ((GnpIntegerAndDoubleGenesSubnodeParameter) node.getSubnodes().get(0).getSubnodeParameters().get(1)).getD2(), 0);

                    if (node.getSubnodeCount() > 1) {

                        if (node.getType() == GnpNode.JUDGEMENT_NODE) {
                            assertEquals((int) ind.genome[7 + (n * totalNodeGeneCount)], node.getSubnodes().get(1).getFunctionId());
                        }
                        if (node.getType() == GnpNode.PROCESSING_NODE) {
                            assertEquals((int) ind.genome[8 + (n * totalNodeGeneCount)], node.getSubnodes().get(1).getFunctionId());
                        }

                        assertEquals((int) ind.genome[9 + (n * totalNodeGeneCount)], ((GnpTwoIntegerGenesSubnodeParameter) node.getSubnodes().get(1).getSubnodeParameters().get(0)).getI1());
                        assertEquals((int) ind.genome[10 + (n * totalNodeGeneCount)], ((GnpTwoIntegerGenesSubnodeParameter) node.getSubnodes().get(1).getSubnodeParameters().get(0)).getI2());
                        assertEquals((int) ind.genome[11 + (n * totalNodeGeneCount)], ((GnpIntegerAndDoubleGenesSubnodeParameter) node.getSubnodes().get(1).getSubnodeParameters().get(1)).getI1());
                        assertEquals(ind.genome[12 + (n * totalNodeGeneCount)], ((GnpIntegerAndDoubleGenesSubnodeParameter) node.getSubnodes().get(1).getSubnodeParameters().get(1)).getD2(), 0);

                        if (node.getType() == GnpNode.PROCESSING_NODE) {
                            assertEquals((int) ind.genome[14 + (n * totalNodeGeneCount)], node.getBranches().get(1).getConnectedNodeId());
                        }
                        if (node.getType() == GnpNode.JUDGEMENT_NODE) {
                            assertEquals((int) ind.genome[15 + (n * totalNodeGeneCount)], node.getBranches().get(2).getConnectedNodeId());
                            assertEquals((int) ind.genome[16 + (n * totalNodeGeneCount)], node.getBranches().get(3).getConnectedNodeId());
                        }

                    }

                    if (node.getType() == GnpNode.PROCESSING_NODE) {
                        assertEquals((int) ind.genome[13 + (n * totalNodeGeneCount)], node.getBranches().get(0).getConnectedNodeId());
                    }
                    if (node.getType() == GnpNode.JUDGEMENT_NODE) {
                        assertEquals((int) ind.genome[13 + (n * totalNodeGeneCount)], node.getBranches().get(0).getConnectedNodeId());
                        assertEquals((int) ind.genome[14 + (n * totalNodeGeneCount)], node.getBranches().get(1).getConnectedNodeId());
                    }

                }
            }

        }

    }

    private List<GnpNodeEvaluationResult> makeSureAtLeastOneNotFirstProcessingNode(GnpIndividual ind, List<GnpNodeEvaluationResult> executionPath, EvolutionState state) {
        boolean expectedPath = false;
        while (!expectedPath) {

            executionPath = ind.evaluateLearnExplore(state, 0, null);

            int processingCount = 0;

            for (GnpNodeEvaluationResult result : executionPath) {
                if (result.getEvaluatedNode().getType() == GnpNode.PROCESSING_NODE) {
                    processingCount++;
                }
            }

            if (processingCount > 0
                    && (executionPath.get(0).getEvaluatedNode().getType() != GnpNode.PROCESSING_NODE
                    ||
                    (executionPath.get(0).getEvaluatedNode().getType() == GnpNode.PROCESSING_NODE
                            && processingCount > 1
                    )
            )) {
                expectedPath = true;
            } else {
                ind.reset(state, 0);
            }

        }
        return executionPath;
    }

    //Makes false positive, so many test runs required
    @Test
    public void sarsaLearningPerformed() {

        for (Individual ind : state.population.subpops.get(0).individuals) {

            List<GnpNodeEvaluationResult> executionPath = null;

            //make sure there is at least one processing node executed
            executionPath = makeSureAtLeastOneNotFirstProcessingNode((GnpIndividual) ind, executionPath, state);

            System.out.println(((GnpIndividual)ind).stringOutputExecutionPath(executionPath));

            //all the nodes before processing node should have some Q value calculated
            for (int i = 0; i < executionPath.size(); i++) {

                if (i > 0 && executionPath.get(i).getEvaluatedNode().getType() == GnpNode.PROCESSING_NODE) {

                    assertTrue(executionPath.get(i - 1).getEvaluatedSubnode().getQ() > 0.0);

                }

            }
        }

    }

    //Makes false positive, so many test runs required
    @Test
    public void sarsaDelayedLearningPerformed() {

          for (Individual ind : delayedRewardsState.population.subpops.get(0).individuals) {

              List<GnpNodeEvaluationResult> executionPath = null;

            //make sure there is at least one processing node executed
            executionPath = makeSureAtLeastOneNotFirstProcessingNode((GnpIndividual) ind, executionPath, delayedRewardsState);

            for (Map.Entry<Integer, Integer> entry : ((GnpIndividual) ind).getFunctionExecutionIds().entrySet()) {

                    //System.out.println("Before reward (executionId " + executionId + "): ");
                    //System.out.println(((GnpIndividual) ind).stringOutputExecutionPath(executionPath));
                    ((GnpIndividual) ind).setDelayedReward(entry.getValue(), 10.0);
                    //System.out.println("After reward: ");
                    //System.out.println(((GnpIndividual) ind).stringOutputExecutionPath(executionPath));

            }


            //all the nodes before processing node should have some Q value calculated
            for (int i = 0; i < executionPath.size(); i++) {
                if (i > 0 && executionPath.get(i).getEvaluatedNode().getType() == GnpNode.PROCESSING_NODE) {

                    assertTrue(executionPath.get(i - 1).getEvaluatedSubnode().getQ() > 0.0);

                }
            }
        }

    }

    //Makes false positive, so many test runs required
    @Test
    public void sarsaEligibilityTracesLearningPerformed() {

        for (Individual ind : eligibilityTracesState.population.subpops.get(0).individuals) {

            List<GnpNodeEvaluationResult> executionPath = null;

            //make sure there is at least one processing node executed
            executionPath = makeSureAtLeastOneNotFirstProcessingNode((GnpIndividual) ind, executionPath, eligibilityTracesState);

            //System.out.println(((GnpIndividual) ind).stringOutputExecutionPath(executionPath));
            int lastPnodeIndex = 0;
            for (int i = 0; i < executionPath.size(); i++) {
                if (executionPath.get(i).getEvaluatedNode().getType() == GnpNode.PROCESSING_NODE) {
                    lastPnodeIndex = i;
                }
            }

            for (int i = 0; i <= lastPnodeIndex; i++) {
                assertTrue(executionPath.get(i).getEvaluatedSubnode().getQ() > 0.0);
            }
        }
    }

    //Makes false positive, so many test runs required
    @Test
    public void sarsaDelayedLearningEligibilityTracesPerformed() {

        for (Individual ind : eligibilityTracesDelayedRewardsState.population.subpops.get(0).individuals) {

            List<GnpNodeEvaluationResult> executionPath = null;

            //make sure there is at least one processing node executed
            executionPath = makeSureAtLeastOneNotFirstProcessingNode((GnpIndividual) ind, executionPath, eligibilityTracesDelayedRewardsState);

            for (Map.Entry<Integer, Integer> entry : ((GnpIndividual) ind).getFunctionExecutionIds().entrySet()) {

                //System.out.println("Before reward (executionId " + executionId + "): ");
                //System.out.println(((GnpIndividual) ind).stringOutputExecutionPath(executionPath));
                ((GnpIndividual) ind).setDelayedReward(entry.getValue(), 10.0);
                //System.out.println("After reward: ");
                //System.out.println(((GnpIndividual) ind).stringOutputExecutionPath(executionPath));

            }

            int lastPnodeIndex = 0;
            for (int i = 0; i < executionPath.size(); i++) {
                if (executionPath.get(i).getEvaluatedNode().getType() == GnpNode.PROCESSING_NODE) {
                    lastPnodeIndex = i;
                }
            }

            for (int i = 0; i <= lastPnodeIndex; i++) {
                assertTrue(executionPath.get(i).getEvaluatedSubnode().getQ() > 0.0);
            }
        }

    }

    @Test
    public void writeReadIndividual() throws IOException {

        //perform learning
        Individual ind = eligibilityTracesState.population.subpops.get(0).individuals.get(0);

        List<GnpNodeEvaluationResult> executionPath = null;

        //make sure there is at least one processing node executed
        makeSureAtLeastOneNotFirstProcessingNode((GnpIndividual) ind, executionPath, eligibilityTracesState);

        StringWriter sWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(sWriter);
        ind.printIndividual(state, writer);
        System.out.println(sWriter.toString());

        GnpIndividual newInd = (GnpIndividual) eligibilityTracesState.population.subpops.get(0).species.newIndividual(eligibilityTracesState, new LineNumberReader(new InputStreamReader(new ByteArrayInputStream(sWriter.toString().getBytes()))));

        StringWriter newSWriter = new StringWriter();
        PrintWriter newWriter = new PrintWriter(newSWriter);
        newInd.printIndividual(eligibilityTracesState, newWriter);
        System.out.println("New ind:");
        System.out.println(newSWriter.toString());

        assertEquals(sWriter.toString(), newSWriter.toString());



    }

    @Test
    public void subnodeParameterCloned() {

        //ge thte first node
        GnpIndividual ind = ((GnpIndividual) state.population.subpops.get(0).individuals.get(0));
        GnpNode node = ind.getNetwork().getNetworkNodes().get(0);

        //get the first subnode and GnpIntegerAndDoubleGenesSubnodeParameter of it
        GnpIntegerAndDoubleGenesSubnodeParameter subnodeParameter = ((GnpIntegerAndDoubleGenesSubnodeParameter) node.getSubnodes().get(0).getSubnodeParameters().get(1));

        GnpIntegerAndDoubleGenesSubnodeParameter clonedSubnodeParameter = (GnpIntegerAndDoubleGenesSubnodeParameter) subnodeParameter.copy(ind.genome);

        assertEquals(subnodeParameter.getI1(), clonedSubnodeParameter.getI1());
        assertEquals(subnodeParameter.getD2(), clonedSubnodeParameter.getD2(), 0);

    }

    @Test
    public void gnpNetworkCloned() {

        GnpIndividual ind = (GnpIndividual) state.population.subpops.get(0).individuals.get(0);

        GnpIndividual clonedInd = (GnpIndividual) ind.clone();

        //todo assert all the objects which needed are deep cloned

    }

}
