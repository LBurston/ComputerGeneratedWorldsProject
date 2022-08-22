package com.cgw.relationships;

import com.cgw.features.Feature;
import com.cgw.features.NPC;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

public class Relationship {

    private final Feature featureA;
    private Feature featureB;
    private final Predicate predicateAtoB;
    private final Predicate predicateBtoA;
    private boolean lock;
    private int unfinishedTimer;
    

    /* Constructors */
    public Relationship(Feature featureA, Predicate predicateAtoB, Predicate predicateBtoA) {
        this.featureA = featureA;
        this.predicateAtoB = predicateAtoB;
        this.predicateBtoA = predicateBtoA;
        lock = false;
        unfinishedTimer = 3;
    }

    public Relationship(Feature featureA, Feature featureB, Predicate predicateAtoB, Predicate predicateBtoA) {
        this.featureA = featureA;
        this.featureB = featureB;
        this.predicateAtoB = predicateAtoB;
        this.predicateBtoA = predicateBtoA;
        if(noNulls()) {
            lock = true;
        }
        unfinishedTimer = 0;
    }

    /* Checkers */

    public boolean isOutOfTime() {
        return unfinishedTimer <= 0;
    }

    public boolean isCompleted() {
        return lock;
    }

    private boolean noNulls() {
        return featureA != null || featureB != null || predicateAtoB != null || predicateBtoA != null;
    }

    /* Getters and Setters */

    public void unfinishedCountdown() {
        unfinishedTimer--;
    }

    public Predicate[] getBothPredicates() {
        Predicate[] predicates = new Predicate[2];
        predicates[0] = predicateAtoB;
        predicates[1] = predicateBtoA;
        return predicates;
    }

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

    public Feature getOtherFeature(Feature feature) {
        if (feature == featureA) { return featureB; }
        else if (feature == featureB){ return featureA; }
        else { return null; }
    }

    public Predicate getOtherPredicate(Predicate predicate) {
        if (predicate == predicateAtoB) { return predicateBtoA; }
        else if (predicate == predicateBtoA) { return predicateAtoB; }
        else { return null; }
    }

    public Feature[] getBothFeatures() {
        Feature[] features = new Feature[2];
        features[0] = featureA;
        features[1] = featureB;
        return features;
    }

    public Predicate getPredicateFrom(Feature feature) {
        if (feature == featureA) { return predicateAtoB; }
        else if (feature == featureB){ return predicateBtoA; }
        else { return null; }
    }

    public Feature getFeatureFromPredicate(@NotNull Predicate predicate) {
        if (predicate.isBiDirectional) { return null; }
        else if (predicate == predicateAtoB) { return featureA; }
        else if (predicate == predicateBtoA) { return featureB; }
        else { return null; }
    }

    public Feature getFeatureA() {
        return featureA;
    }

    public Feature getFeatureB() {
        return featureB;
    }

    public Predicate getPredicateAtoB() {
        return predicateAtoB;
    }

    public Predicate getPredicateBtoA() {
        return predicateBtoA;
    }

    /* Store Relationship in Features */

    public void storeRelationshipInFeatures() {
        if (lock) {
            if (predicateAtoB.getPredicateString().equals("parent") ||
                predicateBtoA.getPredicateString().equals("parent")) {
                storeParentRelationshipInFeatures();
            } else {
                featureA.addRelationship(Triple.of(predicateBtoA.getPredicateString(), featureB, this));
                featureB.addRelationship(Triple.of(predicateAtoB.getPredicateString(), featureA, this));
            }
        }
    }

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

    public void selfDestruct() {
        featureA.removeRelationship(this);
        if(lock) {
            featureB.removeRelationship(this);
        }
    }
}