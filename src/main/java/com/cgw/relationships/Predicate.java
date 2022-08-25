package com.cgw.relationships;

import com.cgw.features.Feature;

/**
 * The Predicate Class for which objects hold information on their required Subject and Object type, their weighting
 * for Randomly choosing, and their Opposite Predicate for the Object Predicate Subject version of a Relationship.
 * @author Luke Burston
 * @author lb800@kent.ac.uk
 * @version 0.1
 * @since 0.1
 */
public class Predicate {

    Class<? extends Feature> requiredSubjectClass;
    String predicate;
    Class<? extends Feature> requiredObjectClass;
    boolean isBiDirectional;
    private final int weight;
    Predicate oppositePredicate;

    /**
     * Constructor for Predicate objects, set through being imported from the txt file
     * in the Relationship Generator.
     * @param requiredSubject The required Class for the Subject.
     * @param predicate The String value of the Predicate.
     * @param requiredObject The required Class for the Object.
     * @param isBiDirectional Whether it is Bidirectional. The Predicate is the same either direction.
     * @param weight The weight for this Predicate in random choosing.
     */
    public Predicate(Class<? extends Feature> requiredSubject, String predicate,
                     Class<? extends Feature> requiredObject, boolean isBiDirectional, int weight) {
        this.requiredSubjectClass = requiredSubject;
        this.predicate = predicate;
        this.requiredObjectClass = requiredObject;
        this.isBiDirectional = isBiDirectional;
        this.weight = weight;
        if (isBiDirectional) {
            oppositePredicate = this;   // References itself if Bidirectional.
        }
    }

    /* Getters & Setters */

    /**
     * Returns the Feature Subclass required for this Predicate Subject.
     * @return Subclass of Feature.
     */
    public Class<? extends Feature> getRequiredSubjectClass() {
        return requiredSubjectClass;
    }

    /**
     * Returns the String value of this Predicate.
     * @return String Value of Predicate.
     */
    public String getPredicateString() {
        return predicate;
    }

    /**
     * Returns the Feature Subclass required for this Predicate Object.
     * @return Subclass of Feature.
     */
    public Class<? extends Feature> getRequiredObjectClass() {
        return requiredObjectClass;
    }

    /**
     * Checks whether this is Bidirectional or not.
     * @return Boolean of whether it is Bidirectional.
     */
    public boolean getBiDirectional() {
        return isBiDirectional;
    }

    /**
     * Returns the weight value of this Predicate.
     * @return The weight value of this Predicate
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Values printed to String.
     * @return
     */
    public String toString() {
        return "\nRequired Subject: " + requiredSubjectClass +
                "\nPredicate: " + predicate +
                "\nRequired Object: " + requiredObjectClass +
                "\nBiDirectional: " + isBiDirectional +
                "\nWeight: " + weight + "%";
    }

    /**
     * Returns the opposite Predicate to this one.
     * @return The opposite Predicate to this one.
     */
    public Predicate getOppositePredicate() {
        return oppositePredicate;
    }

    /**
     * Returns the String Value of the Opposite Predicate.
     * @return The String Value of the Opposite Predicate.
     */
    public String getOppositePredicateString() {
        if(isBiDirectional) {
            return this.getPredicateString();
        } else {
            return oppositePredicate.getPredicateString();
        }
    }

    /**
     * Sets the opposite Predicate to this one. Done outside of Construction for Bidirectional Predicates.
     * @param oppositePredicate
     */
    public void setOppositePredicate(Predicate oppositePredicate) {
        if(!isBiDirectional) {
            this.oppositePredicate = oppositePredicate;
        }
    }
}
