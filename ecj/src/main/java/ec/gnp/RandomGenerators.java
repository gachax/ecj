package ec.gnp;

import ec.EvolutionState;
import ec.Evolve;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;

import java.util.SplittableRandom;

public class RandomGenerators {

    private SplittableRandom splittableRandoms[];
    private MersenneTwisterFast mersenneTwisterFast[];
    private final EvolutionState state;

    private static final String P_SPLITTABLE_RANDOM = "useSplittableRandom";

    private boolean useSplittableRandom;

    public RandomGenerators(final EvolutionState state, Parameter defaultBase) {

        this.state = state;
        useSplittableRandom = state.parameters.getBoolean(defaultBase.push(P_SPLITTABLE_RANDOM), null,false);

        setupRandomGenerators();

    }

    private void setupRandomGenerators() {

        if (useSplittableRandom) {

            splittableRandoms = new SplittableRandom[state.breedthreads > state.evalthreads ? state.breedthreads : state.evalthreads];
            int time = (int) (System.currentTimeMillis());
            for (int i = 0; i < splittableRandoms.length; i++) {
                splittableRandoms[i] = new SplittableRandom(Evolve.determineSeed(state.output, state.parameters, new Parameter(Evolve.P_SEED).push("" + i), time + i, splittableRandoms.length * state.randomSeedOffset, false));
            }

        } else {
            mersenneTwisterFast = state.random;
        }

    }

    public double nextDouble(int generatorIndex) {

        if (useSplittableRandom) {
            return splittableRandoms[generatorIndex].nextDouble();
        } else {
            return mersenneTwisterFast[generatorIndex].nextDouble();
        }

    }

    public int nextInt(int generatorIndex) {

        if (useSplittableRandom) {
            return splittableRandoms[generatorIndex].nextInt();
        } else {
            return mersenneTwisterFast[generatorIndex].nextInt();
        }

    }

    public int nextInt(int generatorIndex, int bound) {

        if (useSplittableRandom) {
            return splittableRandoms[generatorIndex].nextInt(bound);
        } else {
            return mersenneTwisterFast[generatorIndex].nextInt(bound);
        }

    }

}
