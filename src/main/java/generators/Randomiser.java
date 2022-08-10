package generators;

import java.util.Random;

public class Randomiser {
    private static final int SEED = 13454354; // Default seed to control randomisation
    private static final Random rand = new Random(SEED); // A shared random object
    private static final boolean shareRand = true; // Determines if random generator is shared
    private static final boolean testing = true; // Determines whether testing SEED is used

    /**
     * Constructor for a Randomiser object
     */
    private Randomiser() {
        if (!testing) {
            rand.setSeed(new Random().nextInt());
        }
    }

    /**
     * Provides a random generator based on if it is being shared
     */
    public static Random getRandom() {
        if(shareRand) { return rand; }
        else { return new Random(); }
    }

    /**
     * Resets the randomisation.
     * This will have no effect if randomisation is
     * not through a shared generator.
     */
    public static void reset() {
        if (shareRand) {
            if (testing) { rand.setSeed(SEED);  }
            else { rand.setSeed(new Random().nextInt());}
        }
    }
}
