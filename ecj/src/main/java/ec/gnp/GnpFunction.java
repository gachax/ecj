package ec.gnp;

import ec.EvolutionState;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A function to be extended. It implements the custom functionality of the genetic network subnodes.
 *
 * @author Gatis Birkens
 */
public abstract class GnpFunction implements Cloneable {

    private int functionId;

    private Object2IntOpenHashMap<String> branchNames = new Object2IntOpenHashMap<>();

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
                                               final int evaluationId,
                                               final ObjectArrayList<GnpSubnodeParameter> parameters,
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
    public abstract String getName(final ObjectArrayList<GnpSubnodeParameter> parameters);

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

    public Object2IntOpenHashMap<String> getBranchNames() {
        return branchNames;
    }

    public void setBranchNames(Object2IntOpenHashMap<String> branchNames) {
        this.branchNames = branchNames;
    }

    public String getBranchName(int branchId) {

        String branchName = null;

        for (Object2IntOpenHashMap.Entry<String> entry : branchNames.object2IntEntrySet()){
            if (entry.getIntValue() == branchId){
                branchName = entry.getKey();
                break;
            }
        }

        return branchName;

    }

    public GnpFunction lightClone(){

        try {

            GnpFunction newFunction = (GnpFunction) this.clone();
            newFunction.setBranchNames(new Object2IntOpenHashMap<>());
            return newFunction;

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;

    }

    public int getFunctionId() {
        return functionId;
    }

    public void setFunctionId(int functionId) {
        this.functionId = functionId;
    }
}
