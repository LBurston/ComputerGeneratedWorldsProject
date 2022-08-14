package features;

import org.apache.commons.lang3.tuple.Triple;
import relationships.Predicate;
import relationships.Relationship;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class Feature {

    protected ArrayList<Triple<String, Feature, Relationship>> relationships;
    protected String name;

    public Feature() {}

    abstract public ArrayList<Predicate> filterSubjectPredicates(ArrayList<Predicate> predicates);

    public Class<? extends Feature> getSubClass() {
        return this.getClass();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Triple<String, Feature, Relationship>> getRelationships() {
        return relationships;
    }

    public int numberOfRelationships() {
        return relationships.size();
    }

    public void addRelationship(Triple<String, Feature, Relationship> relationship) {
        relationships.add(relationship);
    }

    public boolean hasNoRelationships() {
        return relationships.isEmpty();
    }

    public void removeRelationship(Relationship relationshipToRemove) {
        Iterator<Triple<String, Feature, Relationship>> relationshipIterator = relationships.iterator();
        while(relationshipIterator.hasNext()) {
            Triple<String, Feature, Relationship> relationship = relationshipIterator.next();
            if(relationship.getRight() == relationshipToRemove) {
                relationshipIterator.remove();
                break;
            }
        }
    }

}
