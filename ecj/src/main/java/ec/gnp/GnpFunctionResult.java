package ec.gnp;

/**
 * The result of the function determines the next node of the network to be evaluated and the reward to be distributed, if the function reward is expected and it's not a delayed reward function.
 * It also stores the function which was evaluated.
 * GnpFunction must also set the resulting branch (if it's not an implementation of GnpOneBranchFunction which has only one branch - like all the Processing functions in most of the cases at least)
 *
 * @author Gatis Birkens
 */
public class GnpFunctionResult {

    //The resulting branch name of the GnpFunction
    private String branch;

    //Reward value
    private Double rewardValue;

    //Function which was evaluated to get this result
    private GnpFunction evaluatedFunction;

    public Double getRewardValue() {
        return rewardValue;
    }

    public void setRewardValue(Double rewardValue) {
        this.rewardValue = rewardValue;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public GnpFunction getEvaluatedFunction() {
        return evaluatedFunction;
    }

    public void setEvaluatedFunction(GnpFunction evaluatedFunction) {
        this.evaluatedFunction = evaluatedFunction;
    }

}
