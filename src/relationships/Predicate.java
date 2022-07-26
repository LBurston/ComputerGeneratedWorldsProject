package relationships;

import features.Feature;
import java.util.ArrayList;

public class Predicate {

    Class<? extends Feature> requiredSubject;
    String predicate;
    Class<? extends Feature> requiredObject;
    Boolean isBiDirectional;

    public Predicate(Class<? extends Feature> requiredSubject, String predicate, Class<? extends Feature> requiredObject, Boolean isBiDirectional) {
        this.requiredSubject = requiredSubject;
        this.predicate = predicate;
        this.requiredObject = requiredObject;
        this.isBiDirectional = isBiDirectional;
    }

    /* Getters */

    public Class<? extends Feature> getRequiredSubject() {
        return requiredSubject;
    }

    public String getPredicate() {
        return predicate;
    }

    public Class<? extends Feature> getRequiredObject() {
        return requiredObject;
    }

    public Boolean getBiDirectional() {
        return isBiDirectional;
    }

    public String toString() {
        return "\nRequired Subject: " + requiredSubject +
                "\nPredicate: " + predicate +
                "\nRequired Object: " + requiredObject +
                "\nBiDirectional: " + isBiDirectional;
    }
}
