package ec.gnp.selection;

import ec.EvolutionState;
import ec.gnp.GnpSubnode;
import ec.util.Parameter;

import java.util.ArrayList;

/**
 * Implementations should select the GnpSubnode to be evaluated from the list of the GnpSubnodes
 *
 * @author Gatis Birkens
 */
public interface GnpSubnodeSelector {

    /**
     * Selects the GnpSubnode for evaluation
     * @param subnodes list of subnodes at the current GnpNode
     * @param thread thread number
     * @param exploringEnabled if true, exploring enabled, otherwise no exploring (subnode selection is based only on the highest Q value, for example, in GnpEgreedy implementation)
     * @param exploringProbability if provided, it will be used to determine exploring behavior
     * @return GnpSubnode
     */
    GnpSubnode select(ArrayList<GnpSubnode> subnodes, int thread, boolean exploringEnabled, Double exploringProbability);

    /**
     * Called after initialization
     * @param state EvolutionState
     * @param base Parameter
     */
    void setup(final EvolutionState state, final Parameter base);

}
