package com.cgw.generators.feature;

import com.cgw.exceptions.GenerationFailureException;
import com.cgw.features.Feature;
import com.cgw.generators.Randomiser;
import com.cgw.relationships.Predicate;
import com.cgw.relationships.Relationship;

import java.util.Random;

/**
 * The Superclass of the different Feature Generators.
 * @author Luke Burston
 * @author lb800@kent.ac.uk
 * @version 0.1
 * @since 0.1
 */
public abstract class FeatureGenerator {

    /**
     * Gets a Random object from the Randomiser.
     */
    protected static final Random randNum = Randomiser.getRandom();

    /**
     * Constructor of the Feature Generator, but is not called.
     */
    public FeatureGenerator() {}

    /**
     * Required method for Subclasses for importing their resources.
     */
    abstract protected void importResources();

    /**
     * Required method for Subclasses that Generates a Feature, returns it and can throw an Exception.
     * @return The Generated Feature.
     * @throws GenerationFailureException Exception for Failure to Generate.
     */
    abstract public Feature generateRandomFeature() throws GenerationFailureException;

    /**
     * Required method for Subclasses that Generates a compatible Feature from a given Relationship.
     * @param relationship The Relationship to generate from.
     * @param predicateBtoA The Predicate of the Relationship that will be assigned to the Feature.
     * @return The generated Feature.
     * @throws GenerationFailureException Exception for Failure to Generate.
     */
    abstract public Feature generateFeatureFromRelationship(Relationship relationship, Predicate predicateBtoA) throws GenerationFailureException;
}
