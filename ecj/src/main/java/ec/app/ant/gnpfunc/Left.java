package ec.app.ant.gnpfunc;

import ec.EvolutionState;
import ec.Problem;
import ec.app.ant.AntGnp;
import ec.gnp.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

public class Left extends GnpOneBranchFunction {

    private GnpFunctionResult result = new GnpFunctionResult();

    @Override
    public GnpFunctionResult evaluate(final EvolutionState state,
                                         final int thread,
                                         final GnpIndividual individual,
                                         final int evaluationId,
                                         final ObjectArrayList<GnpSubnodeParameter> parameters,
                                         Object ... additionalParameters) {

        AntGnp p = (AntGnp) additionalParameters[0];
        switch (p.orientation)
        {
            case AntGnp.O_UP:
                p.orientation = AntGnp.O_LEFT;
                break;
            case AntGnp.O_LEFT:
                p.orientation = AntGnp.O_DOWN;
                break;
            case AntGnp.O_DOWN:
                p.orientation = AntGnp.O_RIGHT;
                break;
            case AntGnp.O_RIGHT:
                p.orientation = AntGnp.O_UP;
                break;
            default:  // whoa!
                state.output.fatal("Whoa, somehow I got a bad orientation! (" + p.orientation + ")");
                break;
        }
        p.moves++;

        return result;

    }

    @Override
    public boolean rewardExpected() {
        return false;
    }

    @Override
    public String getName(final ObjectArrayList<GnpSubnodeParameter> parameters) {
        return "Trun left";
    }

}