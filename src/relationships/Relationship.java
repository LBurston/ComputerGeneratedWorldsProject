package relationships;

import features.Feature;

public class Relationship {

    private Feature subject;
    private Predicate predicate;
    private Feature object;

    /* Constructors */
    public Relationship(Feature subject, Predicate predicate, Feature object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    public Relationship(Feature subject, Predicate predicate) {
        this.subject = subject;
        this.predicate = predicate;
    }

    public Relationship(Predicate predicate, Feature object) {
        this.predicate = predicate;
        this.object = object;
    }

    public Relationship(Predicate predicate) {
        this.predicate = predicate;
    }

    public Relationship(Feature feature, Boolean isSubject) {
        if(isSubject) {
            this.subject = feature;
        } else {
            this.object = feature;
        }
    }



    /* Getters and Setters */
    public void setSubject(Feature subject) {
        this.subject = subject;
    }

    public Feature getSubject() {
        return subject;
    }

    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public void setObject(Feature object) {
        this.object = object;
    }

    public Feature getObject() {
        return object;
    }
}
