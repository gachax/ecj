package ec.gnp.implementations;

import ec.EvolutionState;
import ec.gnp.GnpFunctionResult;
import ec.gnp.GnpIndividual;
import ec.gnp.GnpOneBranchFunction;
import ec.gnp.GnpSubnodeParameter;

import java.io.Serializable;
import java.util.List;

/**
 * Created by g.birkens on 2017.11.29..
 */
public class GnpProcessingFunction extends GnpOneBranchFunction implements Serializable {

    private GnpFunctionResult result = new GnpFunctionResult();

    @Override
    public GnpFunctionResult evaluate(final EvolutionState state,
                                      final int thread,
                                      final GnpIndividual individual,
                                      final Integer evaluationId,
                                      final List<GnpSubnodeParameter> parameters,
                                      Object ... additionalParameters) {

        result.setRewardValue(10.0);

        return result;

    }

    @Override
    public boolean rewardExpected() {
        return true;
    }

    @Override
    public String getName(final List<GnpSubnodeParameter> parameters) {
        return "GnpProcessingFunction";
    }

}
