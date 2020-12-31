package ec.gnp;

import ec.EvolutionState;
import ec.util.Parameter;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Function library contains instances of the judgement and processing functions. They will be reused in the genetic network.
 *
 * @author Gatis Birkens
 */
public class GnpFunctionLibrary implements Serializable {

    private List<GnpFunction> judgementFunctions = new ArrayList<>(); //instances of the judgement functions
    private List<GnpFunction> processingFunctions = new ArrayList<>(); //instances of the processing functions

    //this is set during the init and used to determine the genome size and node branch offsets in different places
    private int maxJudgementResultCount = 0;

    public void setup(final EvolutionState state, final Parameter base) {

        //instantiate the judgement functions
        for (int i = 0; i < ((GnpInitializer)state.initializer).getJudgementFunctionCount(); i++) {

            GnpFunction function = (GnpFunction) state.parameters.getInstanceForParameter(base.push(GnpInitializer.P_JUDGEMENT_FUNCTION).push(String.valueOf(i)), null, GnpFunction.class);
            function.setup();
            maxJudgementResultCount = Math.max(function.getBranchNames().size(), maxJudgementResultCount);
            judgementFunctions.add(function);
            function.setBranchNames(new Object2IntOpenHashMap<>());

        }

        //instantiate the processing functions
        for (int i = 0; i < ((GnpInitializer)state.initializer).getProcessingFunctionCount(); i++) {

            GnpFunction function = (GnpFunction) state.parameters.getInstanceForParameter(base.push(GnpInitializer.P_PROCESSING_FUNCTION).push(String.valueOf(i)), null, GnpFunction.class);
            processingFunctions.add(function);

        }

    }

    public GnpFunction newJudgementFunctionInstance(int functionId) {

        GnpFunction function = judgementFunctions.get(functionId).lightClone();
        function.setFunctionId(functionId);
        function.setup();

        return function;
    }

    public GnpFunction newProcessingFunctionInstance(int functionId) {

        GnpFunction function = processingFunctions.get(functionId).lightClone();
        function.setFunctionId(functionId);
        function.setup();

        return function;
    }

    int getMaxJudgementResultCount() {
        return maxJudgementResultCount;
    }

    public List<GnpFunction> getProcessingFunctions() {
        return processingFunctions;
    }
}
