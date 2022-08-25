package com.cgw.generators;

import java.util.Random;
import java.util.stream.IntStream;

/**
 * A Static Randomiser Class for getting a new Random object.
 * Has a field for testing and a Seed, which allows for testing by giving all generators the same
 * Random object with the set Seed, producing the exact same generated World each time (Provided nothing else changes).
 * @author Luke Burston
 * @author lb800@kent.ac.uk
 * @version 0.1
 * @since 0.1
 */
public class Randomiser {
    private static final boolean testing = false;           // Determines if random generator is shared for testing.
    private static final int SEED = 272727;                 // Default seed to control randomisation.
    private static final Random rand = new Random(SEED);    // A shared random object.

    /**
     * Constructor for a Randomiser object.
     */
    private Randomiser() {
    }

    /**
     * Provides a random generator based on whether it is in testing mode.
     * @return A Random object, the same if in testing mode.
     */
    public static Random getRandom() {
        // If the Generator is being tested, the same static Random object is given each call.
        if(testing) { return rand; }
        else { return new Random(); }
    }

    /**
     * Takes in a set of weights that determines that index's
     * probability of being chosen and returns the randomly chosen index.
     * @param weights An array of weights for probability.
     * @return The index that has been randomly chosen.
     */
    public static int getRandomIndexFromWeighting(int[] weights) {
        // Adds all weights together then saves the value of each weight divided by the sum to a float array.
        // The sum of this float array adds to 1.
        int weightSum = IntStream.of(weights).sum();
        float[] newWeights = new float[weights.length];
        int index = 0;
        for(int weight : weights) {
            newWeights[index] = (float) weight/weightSum;
            index++;
        }

        // Creates a random float between 0-1, then iterates through the float array, taking each away from
        // the random float until one of them reduces it below 0, and picks that one.
        float randomFloat = rand.nextFloat();
        for(int choosingIndex = 0; choosingIndex < newWeights.length; choosingIndex++) {
            randomFloat -= newWeights[choosingIndex];
            if(randomFloat <= 0) {
                return choosingIndex;
            }
        }
        // In case this for some reason does not get reduced below 0, returns the last value.
        return weights.length-1;
    }
}
