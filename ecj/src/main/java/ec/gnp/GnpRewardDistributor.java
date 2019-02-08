package ec.gnp;

import ec.EvolutionState;
import ec.util.Parameter;

/**
 * Reward.rewardValue is the main part of the learning process of genetic network.
 * When reward is received, it must be assigned to the corresponding nodes.
 * Implementation examples are ec.gnp.sarsa.GnpSarsa and ec.gnp.sarsa.GnpSarsaWithEligibilityTraces
 *
 * @author Gatis Birkens
 */
public interface GnpRewardDistributor {

    /**
     * Called when reward is received at some point during the execution/evaluation of the network.
     * @param reward of type GnpReward
     */
    void distributeReward(GnpReward reward);

    /**
     * Called during the init part, so the reward distributor can access the state and parameters
     * @param state ec.EvolutionState
     * @param base ec.util.Parameter
     */
    void setup(final EvolutionState state, final Parameter base);

}
