package generators.feature;

import exceptions.GenerationFailureException;
import features.Feature;
import generators.Randomiser;
import relationships.Predicate;
import relationships.Relationship;

import java.util.Random;

public abstract class FeatureGenerator {

    protected static final Random randNum = Randomiser.getRandom();

    public FeatureGenerator() {}

    abstract protected void importResources();

    abstract public Feature generateRandomFeature() throws GenerationFailureException;

    abstract public Feature generateFeatureFromRelationship(Relationship relationship, Predicate predicateBtoA) throws GenerationFailureException;

    public Random getRandomiser() { return randNum; }
}
