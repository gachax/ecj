package ec.gnp.selection;

import ec.EvolutionState;
import ec.gnp.GnpInitializer;
import ec.gnp.GnpSubnode;
import ec.util.Parameter;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * Implementation of GnpSubnodeSelector. Selects the GnpSubnode to be evaluated by using epsilon greedy algorithm.
 *
 * @author Gatis Birkens
 */
public class GnpEgreedy implements GnpSubnodeSelector{

    private static final String P_BASE = "eGreedy";
    private static final String P_EPSILON = "epsilon";
    private double eGreedyEpsilon = 0;
    private EvolutionState state;

    /**
     * Selects the GnpSubnode to be evaluated by using epsilon greedy algorithm.
     * @param subnodes list of subnodes at the current GnpNode
     * @param maxQValuedSubnode if known, then it will be faster to provide this, otherwise null
     * @param thread thread number
     * @param exploringEnabled if true, exploring enabled, otherwise no exploring - subnode selection is based only on the highest Q value not the random part of the selection
     * @param exploringProbability if provided, it will be used to determine exploring behavior
     * @return
     */
    @Override
    public GnpSubnode select(ObjectArrayList<GnpSubnode> subnodes, GnpSubnode maxQValuedSubnode, int thread, boolean exploringEnabled, Double exploringProbability) {

        GnpSubnode resultingSubnode = null;

        double eGreedyEpsilonL;

        if (exploringProbability != null) {
            eGreedyEpsilonL = exploringProbability;
        } else {
            eGreedyEpsilonL = eGreedyEpsilon;
        }

        if (!exploringEnabled) {
            eGreedyEpsilonL = 0;
        }

        //if(state.random[thread].nextDouble() < 1 - eGreedyEpsilonL) {
        if(((GnpInitializer)state.initializer).getRandomGenerators().nextDouble(thread) < 1 - eGreedyEpsilonL) {

            if (maxQValuedSubnode != null) {

                resultingSubnode = maxQValuedSubnode;

            } else {

                double maxQ = -Double.MAX_VALUE;

                //resultingSubnode = subnodes.stream().max(Comparator.comparing(GnpSubnode::getQ)).get();

                for (GnpSubnode subnode : subnodes) {

                    if (subnode.getQ() > maxQ) {

                        resultingSubnode = subnode;
                        maxQ = subnode.getQ();

                    }

                }

            }

        } else {

            //resultingSubnode = subnodes.get(state.random[thread].nextInt(subnodes.size()));
            resultingSubnode = subnodes.get(((GnpInitializer)state.initializer).getRandomGenerators().nextInt(thread, subnodes.size()));

        }


        return resultingSubnode;

    }

    @Override
    public void setup(EvolutionState state, Parameter base) {

        this.state = state;
        Parameter eGreedyBase = base.push(P_BASE);
        eGreedyEpsilon = state.parameters.getDouble(eGreedyBase.push(P_EPSILON), null);

    }



    public double geteGreedyEpsilon() {
        return eGreedyEpsilon;
    }

    public void setGreedyEpsilon(double eGreedyEpsilon) {
        this.eGreedyEpsilon = eGreedyEpsilon;
    }

}
