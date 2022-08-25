package com.cgw.features;

import com.cgw.generators.FeatureManager;
import org.apache.commons.lang3.tuple.Triple;
import com.cgw.relationships.Predicate;
import com.cgw.relationships.Relationship;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The NPC Feature of the World, which stands for 'Non-Player Character'. These are the People of the World.
 * @author Luke Burston
 * @author lb800@kent.ac.uk
 * @version 0.1
 * @since 0.1
 */
public class NPC extends Feature {

    // Attributes of the NPC.
    private String race;
    private char gender;
    private char ageGroup;
    private int age;
    private boolean isAlive;

    // Attributes yet to be implemented.
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

    /**
     * Constructor of the NPC, setting them as Alive and creating a new ArrayList for their Relationships.
     * Attributes are set after initialization as some attributes may require other attributes to set.
     */
    public NPC() {
        isAlive = true;
        relationships = new ArrayList<>();
    }

    /**
     * Predicates from the Relationship Generator to be filtered by the Feature Manager for this particular Feature.
     * For Example, if this NPC's age group is not at least an adult, being a Parent is removed as a possibility.
     * @param predicates The list of Predicates to filter.
     * @return The ArrayList of Predicates that can be assigned to this NPC.
     */
    public ArrayList<Predicate> filterSubjectPredicates(ArrayList<Predicate> predicates) {
        return FeatureManager.getFeatureManager().filterNPCSubjectPredicates(this, predicates);
    }

    /* Relationship Getters */

    /**
     * Searches Relationships for the Settlement this NPC rules and returns their Relationship instance.
     * @return The Relationship reference between this NPC and the Settlement they rule, or null if none.
     */
    public Relationship getSettlementTheyRule() {
        if(isARuler()) {
            return Objects.requireNonNull(relationships.stream()
                    .filter(relationship -> relationship.getLeft().equals("rules"))
                    .findFirst().orElse(null)).getRight();
        } else {return null;}
    }

    /**
     * Searches Relationships for the Settlement this NPC lives in and returns it.
     * @return The Settlement this NPC lives in, or null if none.
     */
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

    /**
     * Searches Relationships for the NPC this NPC is a Partner to and returns.
     * @return The NPC this NPC is a Partner to, or null if none.
     */
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

    /**
     * Searches Relationships for any Parent labels, places those NPCs in an ArrayList and returns it.
     * @return An ArrayList of NPCs that are a Parent to this NPC.
     */
    public ArrayList<NPC> getParents() {
        return relationships.stream()
                .filter(relationship ->
                        relationship.getLeft().matches("mother|father|parent"))
                .map(relationship -> (NPC) relationship.getMiddle())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Searches Relationships for any Sibling labels, places those NPCs in an ArrayList and returns it.
     * @return An ArrayList of NPCs that are a Sibling to this NPC.
     */
    public ArrayList<NPC> getSiblings() {
        return relationships.stream()
                .filter(relationship ->
                        relationship.getLeft().equals("sibling"))
                .map(relationship -> (NPC) relationship.getMiddle())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Searches Relationships for any Child labels, places those NPCs in an ArrayList and returns it.
     * @return An ArrayList of NPCs that are a Child to this NPC.
     */
    public ArrayList<NPC> getChildren() {
        return relationships.stream()
                .filter(relationship -> relationship.getLeft().equals("child"))
                .map(relationship -> (NPC) relationship.getMiddle())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Gets this NPCs Parents and if it has a Parent of a different race, returns that String,
     * otherwise it returns its own race String. For use in keeping family races consistent.
     * @return The String of its Parent's different race, or if none, its own race.
     */
    public String getParentsDifferentRace() {
        ArrayList<NPC> parents = getParents();
        for(NPC parent : parents) {
            String parentRace = parent.getRace();
            if(!parentRace.equals(race)) { return parentRace; }
        }
        return race;
    }

    /**
     * Gets this NPCs Siblings and if it has a Sibling of a different race, returns that String,
     * otherwise it returns its own race String. For use in keeping family races consistent.
     * @return The String of its Sibling's different race, or if none, its own race.
     */
    public String getSiblingsDifferentRace() {
        ArrayList<NPC> siblings = getSiblings();
        for(NPC sibling : siblings) {
            String siblingRace = sibling.getRace();
            if(!siblingRace.equals(race)) { return siblingRace; }
        }
        return race;
    }

    /**
     * Returns the Age of this NPC's eldest Sibling.
     * For use in ensuring Parents would not have had a Child before Adulthood.
     * @return The int Age value of this NPC's eldest Sibling.
     */
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

    /**
     * Returns a HashSet of all blood relatives of this NPC.
     * @return A HashSet of all blood relatives of this NPC.
     */
    public HashSet<NPC> getFamilyMembers() {
        // First gets all immediate Family Member relationships; Parents, Children, and Siblings.
        ArrayList<Triple<String, Feature, Relationship>> familyRelationships = relationships.stream()
                .filter(relationship -> relationship.getLeft().matches("mother|father|parent|child|sibling"))
                .collect(Collectors.toCollection(ArrayList::new));

        ArrayList<NPC> familyMembers = new ArrayList<>();

        // Loops through all Family Member relationships, which can increase during, but not indefinitely.
        for(int index = 0; index < familyRelationships.size(); index++) {
            Triple<String, Feature, Relationship> currentMemberRelationship = familyRelationships.get(index);
            NPC currentMember = (NPC) currentMemberRelationship.getMiddle();    // Gets the NPC from the Relationship.
            familyMembers.add(currentMember);   // Adds them to an ArrayList of Family Members to return.
            String search = ""; // Initializing the String Regex to use for finding this Member's Family Members.

            // Depending on what this Relationship is, determines which of their Family Members will be added.
            // Any Parent relationship should also include any of their immediate Family as they are blood related.
            // Any other type of Relationship will only then search for their Children. Siblings would already have
            // been included and blood related parents will be too. The other parent is not a blood relative so is not
            // needed. Due to age limits on both NPC generation and assigning Parents, there will only be so many
            // parent Relationships in a family tree going up, and so will then only get their children from there on.
            switch(familyRelationships.get(index).getLeft()) {
                case "mother", "father", "parent" -> { search = "mother|father|parent|child|sibling"; }
                case "child", "sibling" -> { search = "child"; }
            }
            String finalSearch = search;
            // Now loops through this current Family Member's own Family Members as dictated by the above filter,
            // and for each one, checks if it hasn't already been added to the list of those to be returned, and adds.
            for(Triple<String, Feature, Relationship> currentMembersFamily :
                    currentMember.getTripleRelationships().stream()
                    .filter(relationships -> relationships.getLeft().matches(finalSearch))
                    .collect(Collectors.toCollection(ArrayList::new))) {
                if(familyRelationships.stream().noneMatch(npc -> npc.getMiddle() == currentMembersFamily.getMiddle())) {
                    familyRelationships.add(currentMembersFamily);
                }
            }
        }
        // Returns a HashSet of the ArrayList, as an extra barrier to prevent duplicate family members being included.
        HashSet<NPC> family = new HashSet<>(familyMembers);
        family.remove(this);
        return family;
    }

    /**
     * Checks if this NPC has two Parents and returns the opposite of the one given,
     * providing that is a Parent of this NPC. Otherwise, returns null.
     * @param parent One of this NPC's Parents.
     * @return The other Parent of this NPC.
     */
    public NPC getOtherParent(NPC parent) {
        if(hasBothParents()) {
            ArrayList<NPC> parents = getParents();
            if(parent == parents.get(0)) { return parents.get(1); }
            else if (parent == parents.get(1)) { return parents.get(0); }
        }
        return null;
    }


    /* Relationship Checkers */

    /**
     * Checks whether this NPC is dead.
     * @return Boolean of whether this NPC is dead.
     */
    public boolean isNotAlive() {
        return !isAlive;
    }

    /**
     * Checks whether this NPC is not an Adult or Elderly.
     * @return Boolean of whether this NPC is a Child or Adolescent.
     */
    public boolean isNotAdult() {
        return ageGroup == 'c' || ageGroup == 't';
    }

    /**
     * Checks whether this NPC has two Parents.
     * @return Boolean of whether this NPC has exactly two Parents.
     */
    public boolean hasBothParents() {
        return numberOfParents() == 2;
    }

    /**
     * Checks whether this NPC has exactly one Parent
     * @return Boolean of whether this NPC has exactly one Parent.
     */
    public boolean hasOnlyOneParent() {
        return numberOfParents() == 1;
    }

    /**
     * Checks whether this NPC has at least one Parent of the same race.
     * @return Boolean of whether this NPC has at least one Parent of the same race.
     */
    public boolean hasParentOfSameRace() {
        ArrayList<NPC> parents = getParents();
        for(NPC parent : parents) {
            if (parent.getRace().equals(race)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether this NPC has any Parents of a different race.
     * @return Boolean of whether this NPC has any Sibling of a different race.
     */
    public boolean hasParentsOfDifferentRace() {
        return !getParentsDifferentRace().equals(race);
    }

    /**
     * Checks whether this NPC has any Siblings of a different race.
     * @return Boolean of whether this NPC has any Sibling of a different race.
     */
    public boolean hasSiblingsOfDifferentRace() {
        return !getSiblingsDifferentRace().equals(race);
    }

    /**
     * Checks if this NPC has a male Parent.
     * @return Boolean of whether this NPC has a male Parent.
     */
    public boolean hasFather() {
        return relationships.stream()
                .anyMatch(relationship -> relationship.getLeft().equals("father"));
    }

    /**
     * Checks if this NPC has a female Parent.
     * @return Boolean of whether this NPC has a female Parent.
     */
    public boolean hasMother() {
        return relationships.stream()
                .anyMatch(relationship -> relationship.getLeft().equals("mother"));
    }

    /**
     * Checks and returns the amount of Parents this NPC has.
     * @return The amount of Parents this NPC has.
     */
    public int numberOfParents() {
        return Math.toIntExact(relationships.stream()
                .filter(relationship ->
                        relationship.getLeft().matches("mother|father|parent")).count());
    }

    /**
     * Checks and returns the amount of Children this NPC has.
     * @return The amount of Children this NPC has.
     */
    public int numberOfChildren() {
        long number = relationships.stream()
                .filter(relationship -> relationship.getLeft().equals("child")).count();
        return Math.toIntExact(number);
    }

    /**
     * Checks and returns the amount of Siblings this NPC has.
     * @return The amount of Siblings this NPC has.
     */
    public int numberOfSiblings() {
        long number = relationships.stream()
                .filter(relationship -> relationship.getLeft().equals("sibling")).count();
        return Math.toIntExact(number);
    }

    /**
     * Checks whether this NPC has a place of Residence.
     * @return Boolean of whether this NPC has a place of Residence.
     */
    public boolean hasResidence() {
        return relationships.stream()
                .anyMatch(relationship -> relationship.getLeft().equals("residence"));
    }

    /**
     * Checks whether this NPC has a Partner
     * @return Boolean of whether this NPC has a Partner.
     */
    public boolean hasPartner() {
        return relationships.stream()
                .anyMatch(relationship -> relationship.getLeft().equals("partner"));
    }

    /**
     * Checks whether this NPC was not an Adult when the Child was born,
     * based on the Child's age and this NPC's race's adult age threshold.
     * @param childAge The current age of the Child.
     * @param npcAdultAge The threshold age of this NPC's race's Adulthood age.
     * @return Boolean of whether this NPC was not an Adult when the Child was born.
     */
    public boolean notOldEnoughAgeGapForChild(int childAge, int npcAdultAge) {
        return age - childAge < npcAdultAge;
    }

    /**
     * Checks whether this NPC is a blood relative of the given NPC.
     * @param npc The NPC to check against.
     * @return Boolean of whether this NPC is a blood relative of the given NPC.
     */
    public boolean isFamilyMemberOf(NPC npc) {
        return getFamilyMembers().contains(npc);
    }

    /**
     * Checks whether this NPC is the Partner of the given NPC.
     * @param npc The NPC to check against.
     * @return Boolean of whether this NPC is the Partner of the given NPC.
     */
    public boolean isPartnerOf(NPC npc) {
        return relationships.stream()
                .filter(relationship -> relationship.getMiddle() == npc)
                .anyMatch(relationship -> relationship.getLeft().equals("partner"));
    }

    /**
     * Checks whether this NPC is not a Parent of the given NPC.
     * @param npc The NPC to check against.
     * @return Boolean of whether this NPC is not the Parent of the given NPC.
     */
    public boolean isNotParentOf(NPC npc) {
        return relationships.stream()
                .filter(relationship -> relationship.getMiddle() == npc)
                .noneMatch(relationship -> relationship.getLeft().equals("child"));
    }

    /**
     * Checks whether this NPC is not a Child of the given NPC.
     * @param npc The NPC to check against.
     * @return Boolean of whether this NPC is not a Child of the given NPC.
     */
    public boolean isNotChildOf(NPC npc) {
        return relationships.stream()
                .filter(relationship -> relationship.getMiddle() == npc)
                .noneMatch(relationship -> relationship.getLeft().matches("mother|father|parent"));
    }

    /**
     * Checks whether this NPC is not a Sibling of the given NPC.
     * @param npc The NPC to check against.
     * @return Boolean of whether this NPC is not a Sibling of the given NPC.
     */
    public boolean isNotSiblingOf(NPC npc) {
        return relationships.stream()
                .filter(relationship -> relationship.getMiddle() == npc)
                .noneMatch(relationship -> relationship.getLeft().equals("sibling"));
    }

    /**
     * Checks whether this NPC is a Ruler of a Settlement.
     * @return Boolean of whether this NPC is a Ruler of a Settlement.
     */
    public boolean isARuler() {
        return relationships.stream()
                .anyMatch(relationship -> relationship.getLeft().equals("rules"));
    }

    /**
     * Checks whether this NPC is the Ruler of the given Settlement.
     * @param settlement The Settlement to check against.
     * @return Boolean of whether this NPC is the Ruler of the given Settlement.
     */
    public boolean isRulerOf(Settlement settlement) {
        return relationships.stream()
                .filter(relationship -> relationship.getMiddle() == settlement)
                .anyMatch(relationship -> relationship.getLeft().equals("rules"));
    }

    /**
     * Returns the last name of this NPC by splitting its name by a space and returning the second String.
     * @return The String of this NPC's Last Name.
     */
    public String getLastName() {
        return name.split(" ")[1];
    }

    /* Getters and Setters */

    /**
     * Returns a String of this NPC's race.
     * @return A String that represents this NPC's race
     */
    public String getRace() {
        return race;
    }

    /**
     * Sets the race of this NPC.
     * @param race A String reference of the race to be set.
     */
    public void setRace(String race) {
        this.race = race;
    }

    /**
     * Returns the age of the NPC as an integer of years.
     * @return An int that represents their age in years.
     */
    public int getAge() {
        return age;
    }

    /**
     * Sets the age of this NPC.
     * @param age An int of the age to be set in years.
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Returns the character representation of the NPC's age group of their race.
     * @return A char representing the NPC's age group: c = "child", t = "adolescent", a = "adult", e = "elderly".
     */
    public char getAgeGroup() {
        return ageGroup;
    }

    /**
     * Returns a String representation of the NPC's age group, for users.
     * @return A String representing the NPC's age group: c = "child", t = "adolescent", a = "adult", e = "elderly".
     */
    public String getAgeGroupString() {
        // Converts their age group into a String.
        return switch (ageGroup) {
            case 'c' -> "child";
            case 't' -> "adolescent";
            case 'a' -> "adult";
            case 'e' -> "elderly";
            default -> null;
        };
    }

    /**
     * Sets the NPC's age group.
     * @param ageGroup The character representing the NPC's age group:
     *                 c = "child", t = "adolescent", a = "adult", e = "elderly".
     */
    public void setAgeGroup(char ageGroup) {
        this.ageGroup = ageGroup;
    }

    /**
     * Returns a character representation of the NPC's gender.
     * @return A char representing the NPC's gender: m = "male", f = "female", n = "non-binary".
     */
    public char getGender() {
        return gender;
    }

    /**
     * Returns a String representation of the NPC's gender, for users.
     * @return A String representing the NPC's gender: m = "male", f = "female", n = "non-binary".
     */
    public String getGenderString() {
        return switch (gender) {
            case 'f' -> "female";
            case 'm' -> "male";
            case 'n' -> "non-binary";
            default -> null;
        };
    }

    /**
     * Sets the NPC's gender.
     * @param gender The character representing the NPC's gender: m = "male", f = "female", n = "non-binary".
     */
    public void setGender(char gender) {
        this.gender = gender;
    }

    /**
     * Sets the NPC's 'Alive' status to the given Boolean value.
     * @param alive Boolean value to set if the NPC is Alive.
     */
    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    /**
     * Returns a String representation of the NPC's status.
     * (Not used for the research, due to potential sensitive topics).
     * @return A String representing the NPC's status.
     */
    public String getAliveString() {
        if(isAlive) { return "alive"; }
        return "dead";
    }


    // Getters and Setters for unused Attributes.
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
