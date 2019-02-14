package ec.gnp.implementations;

import ec.EvolutionState;
import ec.Problem;
import ec.gnp.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

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
                                      final int evaluationId,
                                      final ObjectArrayList<GnpSubnodeParameter> parameters,
                                      Object ... additionalParameters) {

        result.setRewardValue(10.0);

        return result;

    }

    @Override
    public boolean rewardExpected() {
        return true;
    }

    @Override
    public String getName(final ObjectArrayList<GnpSubnodeParameter> parameters) {
        return "GnpProcessingFunction";
    }

}
