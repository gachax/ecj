package ec.gnp.sarsa;

import ec.EvolutionState;
import ec.gnp.*;
import ec.util.Parameter;

/**
 * SARSA implementation of GnpRewardDistributor
 *
 * @author Gatis Birkens
 */
public class GnpSarsa implements GnpRewardDistributor {

    private static final String P_BASE = "sarsa";
    private static final String P_LEARNING_RATE = "alpha";
    private static final String P_DISCOUNT_RATE = "gamma";
    private Double learningRate;
    private Double discountRate;

    /**
     * Sets the reward to the previous node which lead to the reward at the current node
     * @param reward of type GnpReward
     */
    @Override
    public void distributeReward(GnpReward reward) {

        GnpSubnode previousSubnode = null;

        //set reward to the current subnode
        reward.getSubnode().setQ(reward.getReward());

        if (reward.getExecutionPathUntilReward() != null && !reward.getExecutionPathUntilReward().isEmpty()) {

            int firstEvaluationIdOfThePath = reward.getExecutionPathUntilReward().get(0).getEvaluationId();

            if (reward.getEvaluationId() > firstEvaluationIdOfThePath) {
                previousSubnode = reward.getExecutionPathUntilReward().get(reward.getEvaluationId() - 1 - firstEvaluationIdOfThePath).getEvaluatedSubnode();
            }

        }

        if (previousSubnode != null) {
            //Q(st, at) ← Q(st, at) + α[rt + γQ(st+1, at+1) − Q(st, at)]
            previousSubnode.setQ(previousSubnode.getQ() + learningRate * (reward.getReward() + (discountRate * reward.getSubnode().getQ()) - previousSubnode.getQ()));
        }

    }

    @Override
    public void setup(final EvolutionState state, final Parameter base) {

        Parameter sarsaBase = base.push(P_BASE);
        learningRate = state.parameters.getDouble(sarsaBase.push(P_LEARNING_RATE), null);
        discountRate = state.parameters.getDouble(sarsaBase.push(P_DISCOUNT_RATE), null);

    }

}
