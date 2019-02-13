package ec.gnp.implementations;

import ec.EvolutionState;
import ec.Problem;
import ec.gnp.*;

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
                                      List<GnpSubnodeParameter> parameters,
                                      Object ... additionalParameters) {

        return result;
    }

    @Override
    public boolean rewardExpected() {
        return true;
    }

    @Override
    public String getName(final List<GnpSubnodeParameter> parameters) {
        return "GnpProcessingFunctionDelayedReward";
    }

}
