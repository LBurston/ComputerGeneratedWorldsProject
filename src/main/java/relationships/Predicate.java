package relationships;

import features.Feature;
import java.util.ArrayList;

public class Predicate {

    Class<? extends Feature> requiredSubjectClass;
    String predicate;
    Class<? extends Feature> requiredObjectClass;
    Boolean isBiDirectional;
    private final int weight;
    Predicate oppositePredicate;

    public Predicate(Class<? extends Feature> requiredSubject, String predicate, Class<? extends Feature> requiredObject, Boolean isBiDirectional, int weight) {
        this.requiredSubjectClass = requiredSubject;
        this.predicate = predicate;
        this.requiredObjectClass = requiredObject;
        this.isBiDirectional = isBiDirectional;
        this.weight = weight;
        if (isBiDirectional) {
            oppositePredicate = this;
        }
    }

    public boolean matchesPredicate(Predicate predicateToCompare) {
        return (this == predicateToCompare);
    }

    /* Getters & Setters */

    public Class<? extends Feature> getRequiredSubjectClass() {
        return requiredSubjectClass;
    }

    public String getPredicateString() {
        return predicate;
    }

    public Class<? extends Feature> getRequiredObjectClass() {
        return requiredObjectClass;
    }

    public Boolean getBiDirectional() {
        return isBiDirectional;
    }

    public int getWeight() {
        return weight;
    }

    public String toString() {
        return "\nRequired Subject: " + requiredSubjectClass +
                "\nPredicate: " + predicate +
                "\nRequired Object: " + requiredObjectClass +
                "\nBiDirectional: " + isBiDirectional +
                "\nWeight: " + weight + "%";
    }

    public Predicate getOppositePredicate() {
        return oppositePredicate;
    }

    public String getOppositePredicateString() {
        if(isBiDirectional) {
            return this.getPredicateString();
        } else {
            return oppositePredicate.getPredicateString();
        }
    }

    public void setOppositePredicate(Predicate oppositePredicate) {
        if(!isBiDirectional) {
            this.oppositePredicate = oppositePredicate;
        }
    }
}
