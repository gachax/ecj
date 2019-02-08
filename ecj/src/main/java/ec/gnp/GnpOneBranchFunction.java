package ec.gnp;

/**
 * GnpFunction with just one possible branch, so there is no need to setup branches when extending this class and also no need to set the branch during evaluation.
 *
 * @author Gatis Birkens
 */
public abstract class GnpOneBranchFunction extends GnpFunction{

    public static final String DEFAULT_BRANCH_NAME = "n";

    @Override
    protected void setupBranches() {
        addBranch(DEFAULT_BRANCH_NAME);
    }

}
