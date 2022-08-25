package com.cgw.features;

import org.apache.commons.lang3.tuple.Triple;
import com.cgw.relationships.Predicate;
import com.cgw.relationships.Relationship;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Superclass for Features of the World. Sharing a Name Attribute and a Relationships List.
 * @author Luke Burston
 * @author lb800@kent.ac.uk
 * @version 0.1
 * @since 0.1
 */
public abstract class Feature {

    // Relationships, stored as an ArrayList of a Triple Data Structure.
    // The Triple contains the Feature it is linked to, a reference of the Relationship instance of this relationship
    // and a String as a quick reference as to the predicate that this Feature is in relation to the current Feature.
    // e.g. <"mother", Clara Ilipsus, @Relationship#id>: Clara Ilipsus is the current Feature's Mother.
    protected ArrayList<Triple<String, Feature, Relationship>> relationships;
    protected String name;

    public Feature() {}

    /**
     * Required Method for Features to filter the list of possible Predicates
     * to those associated with that Feature Type (Except Folder which return Null).
     * @param predicates The list of Predicates to filter.
     * @return The Filtered Predicate ArrayList.
     */
    abstract public ArrayList<Predicate> filterSubjectPredicates(ArrayList<Predicate> predicates);

    /**
     * Returns the Features Subclass ot get its type.
     * @return A Class instance of a subclass extending Feature.
     */
    public Class<? extends Feature> getSubClass() {
        return this.getClass();
    }

    /**
     * Returns the name of the Feature.
     * @return A string of the Feature's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the Feature's Name. Used when set up and any name changes through Relationships.
     * @param name String of the name to be set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns all Relationships of the Feature as a Triple data Structure;
     * The String Label:    e.g. 'mother'
     * The Feature:         e.g. 'Clara Ilipsus'
     * The Relationship:     e.g. Relationship Instance reference containing both Features and their Predicates.
     * @return An ArrayList of the Triple Data Structure.
     */
    public ArrayList<Triple<String, Feature, Relationship>> getTripleRelationships() {
        return relationships;
    }

    /**
     * Returns the amount of Relationships the Feature has.
     * @return int of the relationships ArrayList size.
     */
    public int numberOfRelationships() {
        return relationships.size();
    }

    /**
     * Adds a new Relationship Triple to the Feature's Relationships containing;
     * The String Label:    e.g. 'mother'
     * The Feature:         e.g. 'Clara Ilipsus'
     * The Relationship:    e.g. Relationship Instance reference containing both Features and their Predicates.
     * @param relationship The Triple Data Structure to be added to relationships.
     */
    public void addRelationship(Triple<String, Feature, Relationship> relationship) {
        relationships.add(relationship);
    }

    /**
     * Checks if the Feature has no Relationships.
     * @return Boolean result of isEmpty on the Feature's Relationships
     */
    public boolean hasNoRelationships() {
        return relationships.isEmpty();
    }

    /**
     * Searches the Relationships to remove a specific Relationship if it no longer exists.
     * @param relationshipToRemove The Relationship instance to be removed.
     */
    public void removeRelationship(Relationship relationshipToRemove) {
        // Creates an Iterator to search through the Relationship.
        Iterator<Triple<String, Feature, Relationship>> relationshipIterator = relationships.iterator();
        // Checks the Relationship value of each Triple for the relationship to remove, ending once located.
        while(relationshipIterator.hasNext()) {
            Triple<String, Feature, Relationship> relationship = relationshipIterator.next();
            if(relationship.getRight() == relationshipToRemove) {
                relationshipIterator.remove();
                break;
            }
        }
    }

    /**
     * Overrides the toString method to return the name of the Feature.
     * @return The name of the Feature as a String.
     */
    public String toString() {
        return name;
    }
}
