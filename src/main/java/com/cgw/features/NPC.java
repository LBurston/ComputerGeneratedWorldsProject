package com.cgw.features;

import com.cgw.generators.FeatureManager;
import org.apache.commons.lang3.tuple.Triple;
import com.cgw.relationships.Predicate;
import com.cgw.relationships.Relationship;

import java.util.*;
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
        if(isARuler()) {
            return Objects.requireNonNull(relationships.stream()
                    .filter(relationship -> relationship.getLeft().equals("rules"))
                    .findFirst().orElse(null)).getRight();
        } else {return null;}
    }

    public Settlement getResidence() {
        Settlement residence = relationships.stream()
                .filter(relationship -> relationship.getLeft().equals("residence"))
                .map(relationship -> (Settlement) relationship.getMiddle())
                .findFirst().orElse(null);
        if(Objects.nonNull(residence)) {
            return residence;
        }
        return null;
    }

    public NPC getPartner() {
        NPC partner =  relationships.stream()
                .filter(relationship -> relationship.getLeft().equals("partner"))
                .map(relationship -> (NPC) relationship.getMiddle())
                .findFirst().orElse(null);
        if(Objects.nonNull(partner)) {
            return  partner;
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

    public ArrayList<NPC> getChildren() {
        return relationships.stream()
                .filter(relationship -> relationship.getLeft().equals("child"))
                .map(relationship -> (NPC) relationship.getMiddle())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public String getParentsDifferentRace() {
        ArrayList<NPC> parents = getParents();
        for(NPC parent : parents) {
            String parentRace = parent.getRace();
            if(!parentRace.equals(race)) { return parentRace; }
        }
        return race;
    }

    public String getSiblingsDifferentRace() {
        ArrayList<NPC> siblings = getSiblings();
        for(NPC sibling : siblings) {
            String siblingRace = sibling.getRace();
            if(!siblingRace.equals(race)) { return siblingRace; }
        }
        return race;
    }

    public int eldestSiblingAge() {
        int eldestAge = age;
        for(NPC sibling : getSiblings()) {
            int siblingAge = sibling.getAge();
            if (siblingAge > age) {
                eldestAge = siblingAge;
            }
        }
        return eldestAge;
    }

    public HashSet<NPC> getFamilyMembers() {
        ArrayList<Triple<String, Feature, Relationship>> familyRelationships = relationships.stream()
                .filter(relationship -> relationship.getLeft().matches("mother|father|parent|child|sibling"))
                .collect(Collectors.toCollection(ArrayList::new));

        ArrayList<NPC> familyMembers = new ArrayList<>();

        for(int index = 0; index < familyRelationships.size(); index++) {
            Triple<String, Feature, Relationship> currentMemberRelationship = familyRelationships.get(index);
            NPC currentMember = (NPC) currentMemberRelationship.getMiddle();
            familyMembers.add(currentMember);
            String search = "";
            switch(familyRelationships.get(index).getLeft()) {
                case "mother", "father", "parent" -> { search = "mother|father|parent|child|sibling"; }
                case "child", "sibling" -> { search = "child"; }
            }
            String finalSearch = search;
            for(Triple<String, Feature, Relationship> currentMembersFamily : currentMember.getTripleRelationships().stream()
                    .filter(relationships -> relationships.getLeft().matches(finalSearch))
                    .collect(Collectors.toCollection(ArrayList::new))) {
                if(familyRelationships.stream().noneMatch(npc -> npc.getMiddle() == currentMembersFamily.getMiddle())) {
                    familyRelationships.add(currentMembersFamily);
                }
            }
        }
        return new HashSet<>(familyMembers);
    }

    public NPC getOtherParent(NPC parent) {
        if(hasBothParents()) {
            ArrayList<NPC> parents = getParents();
            if(parent == parents.get(0)) { return parents.get(1); }
            else if (parent == parents.get(1)) { return parents.get(0); }
        }
        return null;
    }


    /* Checkers */
    public boolean isNotAdult() {
        return ageGroup == 'c' || ageGroup == 't';
    }

    public boolean hasBothParents() {
        return numberOfParents() == 2;
    }

    public boolean hasOnlyOneParent() {
        return numberOfParents() == 1;
    }

    public boolean hasParentOfSameRace() {
        ArrayList<NPC> parents = getParents();
        for(NPC parent : parents) {
            if (parent.getRace().equals(race)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasParentsOfDifferentRace() {
        return !getParentsDifferentRace().equals(race);
    }

    public boolean hasSiblingsOfDifferentRace() {
        return !getSiblingsDifferentRace().equals(race);
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

    public boolean isFamilyMemberOf(NPC npc) {
        return getFamilyMembers().contains(npc);
    }

    public boolean isPartnerOf(NPC npc) {
        return relationships.stream()
                .filter(relationship -> relationship.getMiddle() == npc)
                .anyMatch(relationship -> relationship.getLeft().equals("partner"));
    }

    public boolean isParentOf(NPC npc) {
        return relationships.stream()
                .filter(relationship -> relationship.getMiddle() == npc)
                .anyMatch(relationship -> relationship.getLeft().equals("child"));
    }

    public boolean isChildOf(NPC npc) {
        return relationships.stream()
                .filter(relationship -> relationship.getMiddle() == npc)
                .anyMatch(relationship -> relationship.getLeft().matches("mother|father|parent"));
    }

    public boolean isSiblingOf(NPC npc) {
        return relationships.stream()
                .filter(relationship -> relationship.getMiddle() == npc)
                .anyMatch(relationship -> relationship.getLeft().equals("sibling"));
    }

    public boolean isARuler() {
        return relationships.stream()
                .anyMatch(relationship -> relationship.getLeft().equals("rules"));
    }

    public boolean isRulerOf(Settlement settlement) {
        return relationships.stream()
                .filter(relationship -> relationship.getMiddle() == settlement)
                .anyMatch(relationship -> relationship.getLeft().equals("rules"));
    }

    public String getLastName() {
        return name.split(" ")[1];
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
