package com.cgw.relationships;

import com.cgw.features.Feature;
import com.cgw.features.NPC;
import com.cgw.generators.WorldGenerator;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

/**
 * The Relationship Class for which objects represent the connection between two Features with a link.
 * @author Luke Burston
 * @author lb800@kent.ac.uk
 * @version 0.1
 * @since 0.1
 */
public class Relationship {

    private final Feature featureA;
    private Feature featureB;
    private final Predicate predicateAtoB;
    private final Predicate predicateBtoA;
    private boolean lock;
    private int unfinishedTimer;


    /**
     * Constructor for an unfinished Relationship, in which a second Feature has yet to be chosen/Generated.
     * @param featureA One of the Features of the Relationship.
     * @param predicateAtoB The Predicate for which Feature A is the Subject and Feature B is the Object.
     * @param predicateBtoA The Predicate for which Feature B is the Subject and Feature A is the Object.
     */
    public Relationship(Feature featureA, Predicate predicateAtoB, Predicate predicateBtoA) {
        this.featureA = featureA;
        this.predicateAtoB = predicateAtoB;
        this.predicateBtoA = predicateBtoA;
        lock = false; // Editable to be able to assign Feature B
        unfinishedTimer = 3; // When this reaches zero, Relationship self destructs.
    }

    /**
     * Constructor for a completed Relationship pairing between two Features.
     * @param featureA One of the Features of the Relationship.
     * @param featureB One of the Features of the Relationship.
     * @param predicateAtoB The Predicate for which Feature A is the Subject and Feature B is the Object.
     * @param predicateBtoA The Predicate for which Feature B is the Subject and Feature A is the Object.
     */
    public Relationship(Feature featureA, Feature featureB, Predicate predicateAtoB, Predicate predicateBtoA) {
        this.featureA = featureA;
        this.featureB = featureB;
        this.predicateAtoB = predicateAtoB;
        this.predicateBtoA = predicateBtoA;
        if(noNulls()) {
            // Checks that all parts of the Relationship contain an Object.
            lock = true; // Makes this Relationship uneditable.
        }
        unfinishedTimer = 0; // Irrelevant for completed Relationship.
    }

    /* Checkers */

    /**
     * Checks if the unfinished Relationship is out of time to be completed.
     * @return Boolean check of if it is out of time.
     */
    public boolean isOutOfTime() {
        return unfinishedTimer <= 0;
    }

    /**
     * Checks Relationship has been completely set up.
     * @return Boolean value of the lock.
     */
    public boolean isCompleted() {
        return lock;
    }

    /**
     * Checks none of the core parts of the Relationship holds a null value.
     * @return Boolean of whether Relationship holds no null values
     */
    private boolean noNulls() {
        return featureA != null || featureB != null || predicateAtoB != null || predicateBtoA != null;
    }

    /* Getters and Setters */

    /**
     * Countdowns the unfinished Timer by 1.
     */
    public void unfinishedCountdown() {
        unfinishedTimer--;
    }

    /**
     * Returns both Predicates as an Array.
     * @return Predicate Array of both predicates.
     */
    public Predicate[] getBothPredicates() {
        Predicate[] predicates = new Predicate[2];
        predicates[0] = predicateAtoB;
        predicates[1] = predicateBtoA;
        return predicates;
    }

    /**
     * Sets the Second Feature of an unfinished Relationship to complete it if not locked and returns it if possible.
     * @param feature The second Feature to be set.
     * @return The Feature if it is possible to be set, Null if Relationship is already locked.
     */
    public Feature setSecondFeature(Feature feature) {
        if(!lock) {
            featureB = feature;
            if(noNulls()) {
                lock = true;
                return feature;
            }
        }
        return null;
    }

    /**
     * Returns the opposite Feature to the one given in this Relationship.
     * @param feature The Feature to get the other of.
     * @return The other Feature.
     */
    public Feature getOtherFeature(Feature feature) {
        if (feature == featureA) { return featureB; }
        else if (feature == featureB){ return featureA; }
        else { return null; } // Returns Null if given Feature is not a part of this Relationship.
    }

    /**
     * Returns the opposite Predicate to the one given in this Relationship.
     * @param predicate The Predicate to get the other of.
     * @return The other Predicate.
     */
    public Predicate getOtherPredicate(Predicate predicate) {
        if (predicate == predicateAtoB) { return predicateBtoA; }
        else if (predicate == predicateBtoA) { return predicateAtoB; }
        else { return null; } // Returns Null if given Predicate is not a part of this Relationship.
    }

    /**
     * Returns both Features as an Array.
     * @return An Array of the Features.
     */
    public Feature[] getBothFeatures() {
        Feature[] features = new Feature[2];
        features[0] = featureA;
        features[1] = featureB;
        return features;
    }

    /**
     * Returns the Subject Feature of the given Predicate for Unidirecitonal Predicates.
     * @param predicate The Predicate to get the Subject of.
     * @return The subject Feature, or Null if Predicate not part of the Relationship or is Bidirectional.
     */
    public Feature getFeatureFromPredicate(@NotNull Predicate predicate) {
        if (predicate.isBiDirectional) { return null; }
        else if (predicate == predicateAtoB) { return featureA; }
        else if (predicate == predicateBtoA) { return featureB; }
        else { return null; }
    }

    /**
     * Returns Feature A of the Relationship.
     * @return Feature A of the Relationship.
     */
    public Feature getFeatureA() {
        return featureA;
    }

    /**
     * Returns Feature B of the Relationship.
     * @return Feature B of the Relationship.
     */
    public Feature getFeatureB() {
        return featureB;
    }

    /**
     * Returns the Predicate for Feature A to B
     * @return The Predicate object.
     */
    public Predicate getPredicateAtoB() {
        return predicateAtoB;
    }

    /**
     * Returns the Predicate for Feature B to A
     * @return The Predicate object.
     */
    public Predicate getPredicateBtoA() {
        return predicateBtoA;
    }

    /* Store Relationship in Features */

    /**
     * Adds a Triple Data Structure to each Feature, containing the Predicate String, the other Feature,
     * and a reference to this Relationship.
     */
    public void storeRelationshipInFeatures() {
        if (lock) { // Checks this Relationship is completed.
            if (predicateAtoB.getPredicateString().equals("parent") ||
                predicateBtoA.getPredicateString().equals("parent")) {
                storeParentRelationshipInFeatures();
                // If the Relationship is Parent-Child, stores the Gender type as Predicate String instead.
            } else {
                featureA.addRelationship(Triple.of(predicateBtoA.getPredicateString(), featureB, this));
                featureB.addRelationship(Triple.of(predicateAtoB.getPredicateString(), featureA, this));
            }
        }
    }

    /**
     * Adds a Triple Data Structure to the Parent and Child of the Relationship, so that the Parent has a gender
     * specific label stored within the Child's Relationships.
     */
    private void storeParentRelationshipInFeatures() {
        String predicateBtoAString = predicateBtoA.getPredicateString();
        String predicateAtoBString = predicateAtoB.getPredicateString();
        if (predicateBtoAString.equals("parent")) {
            NPC npcB = (NPC) featureB;
            switch (npcB.getGender()) {
                case 'm' -> featureA.addRelationship(Triple.of("father", featureB, this));
                case 'f' -> featureA.addRelationship(Triple.of("mother", featureB, this));
                case 'n' -> featureA.addRelationship(Triple.of("parent", featureB, this));
            }
            featureB.addRelationship(Triple.of(predicateAtoBString, featureA, this));
        } else {
            featureA.addRelationship(Triple.of(predicateBtoAString, featureB, this));
            NPC npcA = (NPC) featureA;
            switch (npcA.getGender()) {
                case 'm' -> featureB.addRelationship(Triple.of("father", featureA, this));
                case 'f' -> featureB.addRelationship(Triple.of("mother", featureA, this));
                case 'n' -> featureB.addRelationship(Triple.of("parent", featureA, this));
            }
        }
    }

    /**
     * Removes all references to this Relationship, in both Features and the World.
     */
    public void selfDestruct() {
        featureA.removeRelationship(this);
        if(lock) { // Only needs to do these if it has been completed.
            featureB.removeRelationship(this);
            WorldGenerator.getWorldGenerator().getWorld().removeRelationship(this);
        }

    }
}
