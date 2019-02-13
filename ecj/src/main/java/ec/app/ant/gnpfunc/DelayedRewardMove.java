package ec.app.ant.gnpfunc;

import ec.EvolutionState;
import ec.app.ant.AntGnp;
import ec.gnp.*;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.util.ArrayList;
import java.util.List;

public class DelayedRewardMove extends GnpOneBranchDelayedRewardFunction {

    private GnpFunctionResult result = new GnpFunctionResult();

    @Override
    public GnpFunctionResult evaluate(final EvolutionState state,
                                         final int thread,
                                         final GnpIndividual individual,
                                         final int evaluationId,
                                         final List<GnpSubnodeParameter> parameters,
                                         Object ... additionalParameters) {

        AntGnp p = (AntGnp) additionalParameters[0];
        switch (p.orientation)
        {
            case AntGnp.O_UP:
                p.posy--;
                if (p.posy<0) p.posy = p.maxy-1;
                break;
            case AntGnp.O_LEFT:
                p.posx--;
                if (p.posx<0) p.posx = p.maxx-1;
                break;
            case AntGnp.O_DOWN:
                p.posy++;
                if (p.posy>=p.maxy) p.posy=0;
                break;
            case AntGnp.O_RIGHT:
                p.posx++;
                if (p.posx>=p.maxx) p.posx=0;
                break;
            default:  // whoa!
                state.output.fatal("Whoa, somehow I got a bad orientation! (" + p.orientation + ")");
                break;
        }

        p.moves++;
        if (p.map[p.posx][p.posy]==AntGnp.FOOD && p.moves < p.maxMoves )
        {
            p.sum++;

            //store this evaluation id so reward can be set later by using this id
            if (p.functionEvaluationIds.containsKey(individual)) {
                p.functionEvaluationIds.get(individual).add(evaluationId);
            } else {
                IntArrayList evalIds = new IntArrayList();
                evalIds.add(evaluationId);
                p.functionEvaluationIds.put(individual, evalIds);
            }

            p.map[p.posx][p.posy]=AntGnp.ATE;
        }

        if (p.moves<p.maxMoves)
        {
            if (++p.pmod > 122 /* ascii z */) p.pmod=97; /* ascii a */
            p.map2[p.posx][p.posy]=p.pmod;
        }

        return result;

    }

    @Override
    public String getName(final List<GnpSubnodeParameter> parameters) {
        return "Move";
    }

    @Override
    public boolean rewardExpected() {
        return true;
    }

}
