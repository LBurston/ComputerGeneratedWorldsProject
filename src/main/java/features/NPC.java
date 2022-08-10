package features;

import generators.FeatureManager;
import org.apache.commons.lang3.tuple.Triple;
import relationships.Predicate;
import relationships.Relationship;

import java.awt.image.AreaAveragingScaleFilter;
import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class NPC extends Feature {

    private String race;
    private char gender;
    private char ageGroup;
    private int age;
    private boolean isAlive;
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
        relationships = new ArrayList<>();
    }

    public ArrayList<Predicate> filterSubjectPredicates(ArrayList<Predicate> predicates) {
        return FeatureManager.getFeatureManager().filterNPCSubjectPredicates(this, predicates);
    }

    public String toString() {
        return name + " is a " + getAge() + " year-old " + getAgeGroupString() +
                " " + getGenderString() + " " + getRace() + " who is currently " + getAliveString() + ".";
    }

    /* Getters and Setters */

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

    public boolean isNotAlive() {
        return !isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public String getAliveString() {
        if(isAlive) { return "alive"; }
        return "dead";
    }

    /* Relationship Getters */

    public Relationship getSettlementTheyRule() {
        return Objects.requireNonNull(relationships.stream()
                .filter(relationship -> relationship.getLeft().equals("rules"))
                .findFirst().orElse(null)).getRight();
    }

    public Settlement getResidence() {
        Triple<String, Feature, Relationship> residenceRelationship = relationships.stream()
                .filter(relationship -> relationship.getLeft().matches("residence"))
                .findFirst().orElse(null);
        if(Objects.nonNull(residenceRelationship)) {
            return (Settlement) residenceRelationship.getMiddle();
        }
        return null;
    }

    public ArrayList<NPC> getParents() {
        return relationships.stream()
                .filter(relationship ->
                        relationship.getLeft().matches("mother|father|parent"))
                .map(relationship -> (NPC) relationship.getMiddle())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<NPC> getSiblings() {
        return relationships.stream()
                .filter(relationship ->
                        relationship.getLeft().equals("sibling"))
                .map(relationship -> (NPC) relationship.getMiddle())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /* Checkers */
    public boolean isNotAdult() {
        return ageGroup == 'c' || ageGroup == 't';
    }

    public boolean hasBothParents() {
        return numberOfParents() == 2;
    }

    public boolean hasOneParent() {
        return numberOfParents() == 1;
    }

    public boolean hasParentOfSameRace() {
        ArrayList<NPC> parents = relationships.stream()
                .filter(relationship ->
                        relationship.getLeft().matches("mother|father|parent"))
                .map(relationship -> (NPC) relationship.getMiddle())
                .collect(Collectors.toCollection(ArrayList::new));
        for(NPC parent : parents) {
            if (parent.getRace().equals(race)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasFather() {
        return relationships.stream()
                .anyMatch(relationship -> relationship.getLeft().equals("father"));
    }

    public boolean hasMother() {
        return relationships.stream()
                .anyMatch(relationship -> relationship.getLeft().equals("mother"));
    }

    public int numberOfParents() {
        return Math.toIntExact(relationships.stream()
                .filter(relationship ->
                        relationship.getLeft().matches("mother|father|parent")).count());
    }

    public int numberOfChildren() {
        long number = relationships.stream()
                .filter(relationship -> relationship.getLeft().equals("child")).count();
        return Math.toIntExact(number);
    }

    public int numberOfSiblings() {
        long number = relationships.stream()
                .filter(relationship -> relationship.getLeft().equals("sibling")).count();
        return Math.toIntExact(number);
    }

    public boolean hasResidence() {
        return relationships.stream()
                .anyMatch(relationship -> relationship.getLeft().equals("residence"));
    }

    public boolean hasPartner() {
        return relationships.stream()
                .anyMatch(relationship -> relationship.getLeft().equals("partner"));
    }

    public boolean oldEnoughAgeGapForChild(int childAge, int npcAdultAge) {
        return age - childAge >= npcAdultAge;
    }

    public boolean isFamilyMember(NPC npc) {
        return relationships.stream()
                .filter(relationship -> relationship.getMiddle() == npc)
                .anyMatch(relationship -> relationship.getLeft().matches("mother|father|child|sibling"));
    }

    public boolean isPartner(NPC npc) {
        return relationships.stream()
                .filter(relationship -> relationship.getMiddle() == npc)
                .anyMatch(relationship -> relationship.getLeft().equals("partner"));
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
