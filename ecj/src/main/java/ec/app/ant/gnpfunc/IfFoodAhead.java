package ec.app.ant.gnpfunc;

import ec.EvolutionState;
import ec.Problem;
import ec.app.ant.AntGnp;
import ec.gnp.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

public class IfFoodAhead extends GnpFunction {

    private static final String TRUE = "T";
    private static final String FALSE = "F";
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
                if (p.map[p.posx][(p.posy-1+p.maxy)%p.maxy]==AntGnp.FOOD)
                    result.setBranch(FALSE);
                else result.setBranch(TRUE);
                break;
            case AntGnp.O_LEFT:
                if (p.map[(p.posx-1+p.maxx)%p.maxx][p.posy]==AntGnp.FOOD)
                    result.setBranch(FALSE);
                else result.setBranch(TRUE);
                break;
            case AntGnp.O_DOWN:
                if (p.map[p.posx][(p.posy+1)%p.maxy]==AntGnp.FOOD)
                    result.setBranch(FALSE);
                else result.setBranch(TRUE);
                break;
            case AntGnp.O_RIGHT:
                if (p.map[(p.posx+1)%p.maxx][p.posy]==AntGnp.FOOD)
                    result.setBranch(FALSE);
                else result.setBranch(TRUE);
                break;
            default:  // whoa!
                state.output.fatal("Whoa, somehow I got a bad orientation! (" + p.orientation + ")");
                break;
        }

        return result;

    }

    @Override
    public boolean rewardExpected() {
        return false;
    }

    @Override
    public String getName(final ObjectArrayList<GnpSubnodeParameter> parameters) {
        return "Food ahead?";
    }

    @Override
    protected void setupBranches() {

        addBranch(TRUE);
        addBranch(FALSE);

    }

}
