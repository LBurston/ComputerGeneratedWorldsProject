package com.cgw.generators.feature;

import com.cgw.exceptions.GenerationFailureException;
import com.cgw.features.Feature;
import com.cgw.generators.Randomiser;
import com.cgw.relationships.Predicate;
import com.cgw.relationships.Relationship;

import java.util.Random;

public abstract class FeatureGenerator {

    protected static final Random randNum = Randomiser.getRandom();

    public FeatureGenerator() {}

    abstract protected void importResources();

    abstract public Feature generateRandomFeature() throws GenerationFailureException;

    abstract public Feature generateFeatureFromRelationship(Relationship relationship, Predicate predicateBtoA) throws GenerationFailureException;

    public Random getRandomiser() { return randNum; }
}
