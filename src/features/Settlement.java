package features;

import relationships.Predicate;
import relationships.Relationship;

import java.util.HashSet;

public class Settlement extends Feature {

    private String name;
    private String type;
    private char size;
    private int population;
    private final HashSet<Relationship> subjectRelationships;
    private final HashSet<Relationship> objectRelationships;
    //private char government;

    public Settlement() {
        subjectRelationships = new HashSet<Relationship>();
        objectRelationships = new HashSet<Relationship>();
    }

    /* Getters and Setters */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public char getSize() {
        return size;
    }

    public String getSizeString() {
        return switch (size) {
            case 's' -> "small";
            case 'l' -> "large";
            case 'n' -> "n/a";
            default -> null;
        };
    }

    public void setSize(char size) {
        this.size = size;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public HashSet<Relationship> getSubjectRelationships() {
        return subjectRelationships;
    }

    public HashSet<Predicate> getSubjectRelationshipsPredicates() {
        HashSet<Predicate> predicates = new HashSet<Predicate>();
        for(Relationship relationship : subjectRelationships) {
            predicates.add(relationship.getPredicate());
        }
        return predicates;
    }

    public void addSubjectRelationship(Relationship relationship) {
        subjectRelationships.add(relationship);
    }

    public HashSet<Relationship> getObjectRelationships() {
        return objectRelationships;
    }

    public HashSet<Predicate> getObjectRelationshipsPredicates() {
        HashSet<Predicate> predicates = new HashSet<Predicate>();
        for(Relationship relationship : objectRelationships) {
            predicates.add(relationship.getPredicate());
        }
        return predicates;
    }

    public void addObjectRelationship(Relationship relationship) {
        objectRelationships.add(relationship);
    }

//    public char getGovernment() {
//        return government;
//    }
//
//    public void setGovernment(char government) {
//        this.government = government;
//    }
}
