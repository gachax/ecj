package ec.gnp.implementations;

import ec.EvolutionState;
import ec.Problem;
import ec.gnp.*;

import java.io.Serializable;
import java.util.List;

/**
 * Created by g.birkens on 2017.11.29..
 */
public class GnpJudgementFunction extends GnpFunction implements Serializable {

    private static final String B_R1 = "R1";
    private static final String B_R2 = "R2";

    private GnpFunctionResult result = new GnpFunctionResult();

    @Override
    public GnpFunctionResult evaluate(final EvolutionState state,
                                      final int thread,
                                      final GnpIndividual individual,
                                      final int evaluationId,
                                      final List<GnpSubnodeParameter> parameters,
                                      Object ... additionalParameters) {

        result.setBranch(B_R2);

        return result;

    }

    @Override
    public boolean rewardExpected() {
        return false;
    }

    @Override
    public String getName(final List<GnpSubnodeParameter> parameters) {
        return "GnpJudgementFunction";
    }

    @Override
    protected void setupBranches() {

        addBranch(B_R1);
        addBranch(B_R2);

    }

}
