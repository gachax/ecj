package ec.gnp.sarsa;

import ec.EvolutionState;
import ec.gnp.GnpNodeEvaluationResult;
import ec.gnp.GnpReward;
import ec.gnp.GnpRewardDistributor;
import ec.gnp.GnpSubnode;
import ec.util.Parameter;

/**
 * SARSA with eligibility traces implementation of GnpRewardDistributor
 *
 * @author Gatis Birkens
 */
public class GnpSarsaWithEligibilityTraces implements GnpRewardDistributor {

    private static final String P_BASE = "sarsa";
    private static final String P_LEARNING_RATE = "alpha";
    private static final String P_DISCOUNT_RATE = "gamma";
    private static final String P_TRACE_DECAY = "lambda";
    private Double learningRate;
    private Double discountRate;
    private Double traceDecay;

    /**
     * Sets the reward to all of the nodes which lead to the reward at the current node
     * @param reward of type GnpReward
     */
    @Override
    public void distributeReward(GnpReward reward) {

        //set reward to the current subnode
        reward.getSubnode().setQ(reward.getReward());

        if (reward.getExecutionPathUntilReward() != null && !reward.getExecutionPathUntilReward().isEmpty() && reward.getEvaluationId() > 0) {

            int executionsVsPathUntilRewardOffset = reward.getExecutionPathUntilReward().get(0).getEvaluationId();

            GnpSubnode previousSubnode = reward.getExecutionPathUntilReward().get(reward.getEvaluationId() - executionsVsPathUntilRewardOffset -1).getEvaluatedSubnode();

            Double delta = reward.getReward() + discountRate * reward.getSubnode().getQ() - previousSubnode.getQ();
            Double e = 1.0;

            for (int i = reward.getEvaluationId()-1; i >= reward.getExecutionPathUntilReward().get(0).getEvaluationId(); i--) {

                GnpNodeEvaluationResult evalResult = reward.getExecutionPathUntilReward().get(i - executionsVsPathUntilRewardOffset);

                evalResult.getEvaluatedSubnode().setQ(evalResult.getEvaluatedSubnode().getQ() + learningRate * delta * e);
                e = discountRate * traceDecay * e;

            }

        }

    }

    @Override
    public void setup(final EvolutionState state, final Parameter base) {

        Parameter sarsaBase = base.push(P_BASE);
        learningRate = state.parameters.getDouble(sarsaBase.push(P_LEARNING_RATE), null);
        discountRate = state.parameters.getDouble(sarsaBase.push(P_DISCOUNT_RATE), null);
        traceDecay = state.parameters.getDouble(sarsaBase.push(P_TRACE_DECAY), null);

    }

}
