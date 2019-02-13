package ec.gnp;

import java.util.ArrayList;
import java.util.List;

/**
 * Reward is an essential part of the learning process of genetic network.
 * It is created during ec.gnp.GnpNode evaluation either:
 * 1) when GnpFunction from (ec.gnp.GnpFunctionLibrary) sets the rewardValue to GnpFunctionResult in case it's not a delayed reward
 * 2) when GnpFunction is an instance of GnpDelayedRewardFunction and the reward is set later, the expected reward is registered at the GnpIndividual
 * It is used by GnpRewardDistributor to set the rewards on subnodes
 *
 * @author Gatis Birkens
 */
public class GnpReward {

    private GnpSubnode subnode;
    private List<GnpNodeEvaluationResult> executionPathUntilReward;
    private int evaluationId;
    private Double rewardValue;

    public GnpReward(GnpSubnode subnode, List<GnpNodeEvaluationResult> executionPath, int evaluationId) {

        this.subnode = subnode;
        this.executionPathUntilReward = new ArrayList<>(executionPath);
        this.evaluationId = evaluationId;

    }

    public GnpReward(GnpSubnode subnode, List<GnpNodeEvaluationResult> executionPath, int evaluationId, Double rewardValue) {
        this.subnode = subnode;
        this.executionPathUntilReward = new ArrayList<>(executionPath);
        this.evaluationId = evaluationId;
        this.rewardValue = rewardValue;
    }

    public Double getReward() {
        return rewardValue;
    }

    public void setRewardValue(Double rewardValue) {

        //TODO
        if (rewardValue.isNaN() || rewardValue.isInfinite()){
            throw new RuntimeException("Ivalid reward value!");
        }
        this.rewardValue = rewardValue;
    }

    public GnpSubnode getSubnode() {
        return subnode;
    }

    public List<GnpNodeEvaluationResult> getExecutionPathUntilReward() {
        return executionPathUntilReward;
    }

    public int getEvaluationId() {
        return evaluationId;
    }
}
