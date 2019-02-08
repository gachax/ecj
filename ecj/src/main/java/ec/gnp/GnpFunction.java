package ec.gnp;

import ec.EvolutionState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A function to be extended. It implements the custom functionality of the genetic network subnodes.
 *
 * @author Gatis Birkens
 */
public abstract class GnpFunction {

    private Map<String, Integer> branchNames = new HashMap<>();

    /**
     * Called after initialization.
     */
    public void setup(){
        setupBranches();
    }

    /**
     * Called during evaluation of the GN node
     * @param state EvolutionState
     * @param thread thread number
     * @param individual GnpIndividual
     * @param evaluationId unique identifier of the evaluation used to link delayed rewards to the network
     * @param parameters list of GnpSubnodeParameter contains genome based parameters which are passed to the implementing GnpFunction
     * @param additionalParameters can be used to pass custom objects form Problem to the specific GnpFunction when evaluating the GnpIndividual
     * @return GnpFunctionResult
     */
    public abstract GnpFunctionResult evaluate(final EvolutionState state,
                                                  final int thread,
                                                  final GnpIndividual individual,
                                                  final Integer evaluationId,
                                                  final List<GnpSubnodeParameter> parameters,
                                                  Object ... additionalParameters);

    /**
     * Define if the reward from executing the function is expected or not.
     * This is needed for performance reasons as distributing the reward especially delayed one takes resources
     * and by setting this to false a lot of unnecessary calls to reward distributor can be avoided (for example judgement functions in some implementation do not set rewards at all).
     * @return boolean
     */
    public abstract boolean rewardExpected();

    /**
     * The name which is used when printing evaluation results and genetic network information.
     * @param parameters the name might depend on GnpSubnodeParameters, so they are accessible if needed.
     * @return
     */
    public abstract String getName(final List<GnpSubnodeParameter> parameters);

    /**
     * This have to be implemented by adding the names of all possible sunbode GN branches with addBranch(String branchName) method.
     * Branches are linking subnode to other nodes of the network. Branch name is used when printing evaluation results and genetic network information and also must be set
     * as the result of the function evaluation (GnpFunctionResult.setBranch(String branchName)), i.e. the result of the function determines the next node of the network to be evaluated.
     */
    protected abstract void setupBranches();

    /**
     * Adds the branch to all the possible branches of the function.
     * @param branchName the unique name of the branch
     */
    protected void addBranch(String branchName) {
        branchNames.put(branchName, branchNames.size());
    }

    public Map<String, Integer> getBranchNames() {
        return branchNames;
    }

    public String getBranchName(int branchId) {

        String branchName = null;

        for (Map.Entry<String, Integer> entry : branchNames.entrySet()){
            if (entry.getValue() == branchId){
                branchName = entry.getKey();
                break;
            }
        }

        return branchName;

    }



}
