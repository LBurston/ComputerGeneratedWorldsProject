package generators.feature;

import exceptions.GenerationFailureException;
import features.Feature;
import generators.Randomiser;

import java.util.Random;

public abstract class FeatureGenerator {

    protected static final Random randNum = Randomiser.getRandom();

    public FeatureGenerator() {}

    abstract protected void importResources();

    abstract public Feature generateFeature() throws GenerationFailureException;
}
