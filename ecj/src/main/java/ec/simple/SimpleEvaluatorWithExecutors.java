package ec.simple;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class SimpleEvaluatorWithExecutors extends SimpleEvaluator{

    private static ExecutorService individualEvaluatorExecutorService;

    @Override
    public void setup(EvolutionState state, Parameter base) {

        super.setup(state, base);

        if (state.evalthreads > 1 && individualEvaluatorExecutorService == null) {
            individualEvaluatorExecutorService = Executors.newFixedThreadPool(state.evalthreads);
        }

    }

    @Override
    public void evaluatePopulation(final EvolutionState state) {

        if (state.evalthreads == 1) {

            super.evaluatePopulation(state);

        } else {

            if (numTests > 1) {
                expand(state);
            }

            List<Future<?>> tasks = new ArrayList<>();

            for (int subPop = 0; subPop < state.population.subpops.size(); subPop++) {

                for (int ind = 0; ind < state.population.subpops.get(subPop).individuals.size(); ind++) {

                    Individual individual = state.population.subpops.get(subPop).individuals.get(ind);

                    tasks.add(individualEvaluatorExecutorService.submit(new IndividualEvaluator(state, individual, subPop)));

                }

            }

            for (Future<?> currTask : tasks) {
                try {
                    currTask.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    state.output.fatal(e.getMessage());
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    state.output.fatal(e.getMessage());
                }
            }

            if (numTests > 1) {
                contract(state);
            }

        }

    }

    private class IndividualEvaluator implements Callable {

        private EvolutionState state;
        private Individual ind;
        private int population;


        public IndividualEvaluator(EvolutionState state, Individual ind, int population) {

            this.state = state;
            this.ind = ind;
            this.population = population;

        }

        @Override
        public Object call() throws Exception {

            int threadNumber = (int) Thread.currentThread().getId()%state.evalthreads;

            SimpleProblemForm problem = (SimpleProblemForm)(p_problem.clone());

            ((ec.Problem)problem).prepareToEvaluate(state,threadNumber);

            problem.evaluate(state, ind, population, threadNumber);
            state.incrementEvaluations(1);

            ((ec.Problem)problem).finishEvaluating(state,threadNumber);
            return null;

        }
    }

}
