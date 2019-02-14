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
public class GnpProcessingFunctionDelayedReward extends GnpOneBranchDelayedRewardFunction implements Serializable {

    private GnpFunctionResult result = new GnpFunctionResult();

    @Override
    public GnpFunctionResult evaluate(EvolutionState state,
                                      int thread,
                                      GnpIndividual individual,
                                      final int evaluationId,
                                      ObjectArrayList<GnpSubnodeParameter> parameters,
                                      Object ... additionalParameters) {

        return result;
    }

    @Override
    public boolean rewardExpected() {
        return true;
    }

    @Override
    public String getName(final ObjectArrayList<GnpSubnodeParameter> parameters) {
        return "GnpProcessingFunctionDelayedReward";
    }

}
