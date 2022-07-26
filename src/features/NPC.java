package features;

import relationships.Predicate;
import relationships.Relationship;

import java.util.HashSet;

public class NPC extends Feature {

    private String name;
    private String race;
    private char gender;
    private char ageGroup;
    private int age;
    private boolean isAlive;
    private final HashSet<Relationship> subjectRelationships;
    private final HashSet<Relationship> objectRelationships;
//    char hairLength;
//    String hairColour;
//    String eyeColour;
//    String occupation;
//    String alignment;
//    String bond;
//    String ideal;
//    String flaw;
//    String talent;
//    String highAbility;
//    String lowAbility;
//    String quirk;
//    String interaction;

    public NPC() {
        isAlive = true;
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

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public char getAgeGroup() {
        return ageGroup;
    }

    public String getAgeGroupString() {
        return switch (ageGroup) {
            case 'c' -> "child";
            case 't' -> "adolescent";
            case 'a' -> "adult";
            case 'e' -> "elderly";
            default -> null;
        };
    }

    public void setAgeGroup(char ageGroup) {
        this.ageGroup = ageGroup;
    }

    public char getGender() {
        return gender;
    }

    public String getGenderString() {
        return switch (gender) {
            case 'f' -> "female";
            case 'm' -> "male";
            case 'n' -> "non-binary";
            default -> null;
        };
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
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

    //    public char getHairLength() {
//        return hairLength;
//    }
//
//    public void setHairLength(char hairLength) {
//        this.hairLength = hairLength;
//    }
//
//    public String getHairColour() {
//        return hairColour;
//    }
//
//    public void setHairColour(String hairColour) {
//        this.hairColour = hairColour;
//    }
//
//    public String getEyeColour() {
//        return eyeColour;
//    }
//
//    public void setEyeColour(String eyeColour) {
//        this.eyeColour = eyeColour;
//    }
//
//    public String getOccupation() {
//        return occupation;
//    }
//
//    public void setOccupation(String occupation) {
//        this.occupation = occupation;
//    }
//
//    public String getAlignment() {
//        return alignment;
//    }
//
//    public void setAlignment(String alignment) {
//        this.alignment = alignment;
//    }
//
//    public String getBond() {
//        return bond;
//    }
//
//    public void setBond(String bond) {
//        this.bond = bond;
//    }
//
//    public String getIdeal() {
//        return ideal;
//    }
//
//    public void setIdeal(String ideal) {
//        this.ideal = ideal;
//    }
//
//    public String getFlaw() {
//        return flaw;
//    }
//
//    public void setFlaw(String flaw) {
//        this.flaw = flaw;
//    }
//
//    public String getTalent() {
//        return talent;
//    }
//
//    public void setTalent(String talent) {
//        this.talent = talent;
//    }
//
//    public String getHighAbility() {
//        return highAbility;
//    }
//
//    public void setHighAbility(String highAbility) {
//        this.highAbility = highAbility;
//    }
//
//    public String getLowAbility() {
//        return lowAbility;
//    }
//
//    public void setLowAbility(String lowAbility) {
//        this.lowAbility = lowAbility;
//    }
//
//    public String getQuirk() {
//        return quirk;
//    }
//
//    public void setQuirk(String quirk) {
//        this.quirk = quirk;
//    }
//
//    public String getInteraction() {
//        return interaction;
//    }
//
//    public void setInteraction(String interaction) {
//        this.interaction = interaction;
//    }
}
