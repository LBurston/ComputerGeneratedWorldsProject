package generators;

import java.util.Random;
import java.util.stream.IntStream;

public class Randomiser {
    private static final int SEED = 64566; // Default seed to control randomisation
    private static final Random rand = new Random(SEED); // A shared random object
    private static final boolean testing = true; // Determines if random generator is shared

    /**
     * Constructor for a Randomiser object
     */
    private Randomiser() {
    }

    /**
     * Provides a random generator based on if it is a shared
     * one for testing.
     */
    public static Random getRandom() {
        if(testing) { return rand; }
        else { return new Random(); }
    }

    /**
     * Resets the randomisation.
     * This will have no effect if randomisation is
     * not being tested.
     */
    public static void reset() {
            if (testing) { rand.setSeed(SEED);  }
    }

    /**
     * Takes in a set of weights that determine probability
     * and returns a randomly chosen index that corresponds
     * to where it is called.
     *
     * @param weights An array of weights for probability.
     * @return The index for what is randomly chosen.
     */
    public static int getRandomIndexFromWeighting(int[] weights) {
        // Adds all weights together then saves the value of each weight divided by the sum to a float array
        int weightSum = IntStream.of(weights).sum();
        float[] newWeights = new float[weights.length];
        int index = 0;
        for(int weight : weights) {
            newWeights[index] = (float) weight/weightSum;
            index++;
        }

        float randomFloat = rand.nextFloat();
        for(int choosingIndex = 0; choosingIndex < newWeights.length; choosingIndex++) {
            randomFloat -= newWeights[choosingIndex];
            if(randomFloat <= 0) {
                return choosingIndex;
            }
        }
        return weights.length-1;
    }
}
