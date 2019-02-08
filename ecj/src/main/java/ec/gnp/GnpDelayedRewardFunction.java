package ec.gnp;

/**
 * If this is extended, then reward should not be set within the function itself right at the point of evaluation.
 * Instead, evaluationId (received as a parameter of function's evaluate method) should be noted.
 * Later on that evaluationId can be used to set the reward by using GnpIndividual's setDelayedReward(Integer evaluationId, Double rewardValue) method.
 * Of course the later it's set, the more the learning process is affected, but it all depends on the problem being solved.
 *
 * @author Gatis Birkens
 */
public abstract class GnpDelayedRewardFunction extends GnpFunction{

}
