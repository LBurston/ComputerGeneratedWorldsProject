package com.cgw.generators;

import com.cgw.exceptions.GenerationFailureException;
import com.cgw.features.*;
import com.cgw.generators.feature.NPCGenerator;
import com.cgw.generators.feature.SettlementGenerator;
import com.cgw.features.World;
import org.jetbrains.annotations.NotNull;
import com.cgw.relationships.Predicate;
import com.cgw.relationships.Relationship;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The Feature Manager Class controls the separate Feature Generators and provides methods for
 * filtering Features through subclasses and Predicates.
 * @author Luke Burston
 * @author lb800@kent.ac.uk
 * @version 0.1
 * @since 0.1
 */
public class FeatureManager {

    // Singleton instance of itself.
    private static FeatureManager featureManager = null;

    // Singleton instances of Generators and Randomiser.
    private static final RelationshipGenerator relationshipGenerator = RelationshipGenerator.getRelationshipGenerator();
    private static final NPCGenerator npcGenerator = NPCGenerator.getNPCGenerator();
    private static final SettlementGenerator settlementGenerator = SettlementGenerator.getSettlementGenerator();
    private static final Random randNum = Randomiser.getRandom();

    private World world;

    /* NPC Relationship Limits */
    private static final int MAX_SIBLINGS = 5;
    private static final int MAX_CHILDREN = 6;

    /* Settlement Relationship Limits */
    private static final int MAX_TRADES = 6;
    private static final int MAX_RIVALS = 3;


    public static void main(String[] args) {

    }

    /**
     * Constructor for the Feature Manager.
     */
    private FeatureManager() {}

    /**
     * Sets the current World.
     * @param world World to be set.
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * Returns this Manager, or creates a new one if it hasn't been set up yet.
     * @return The Feature Manager Singleton
     */
    public static FeatureManager getFeatureManager() {
        if (featureManager == null) {
            featureManager = new FeatureManager();
        }
        return featureManager;
    }

    /**
     * Main Generating function for Initial Generation or when no new Features were generated
     * in the previous Iteration. Takes in the amount of each Feature type to generate.
     * @param npcs Amount of NPCs to Generate.
     * @param settlements Amount of Settlements to Generate.
     * @return An ArrayList of the generated Features.
     */
    public ArrayList<Feature> generateFeatures(int npcs, int settlements) {
        // Any errors in Generating should be caught and moved on from to not crash the App.
        ArrayList<Feature> features = new ArrayList<>();
        for(int i = 0; i < npcs; i++) {
            try {
                features.add(npcGenerator.generateRandomFeature());
            } catch (GenerationFailureException ex) {
                ex.printStackTrace();
            }
        }
        for(int i = 0; i < settlements; i++) {
            try {
                features.add(settlementGenerator.generateRandomFeature());
            } catch (GenerationFailureException ex) {
                ex.printStackTrace();
            }
        }
        return features;
    }

    /**
     * Filters which Predicates are applicable to the given NPC who is the Subject of the Relationship.
     * In other words, the first Feature to be selected for the Relationship.
     * @param npc The NPC to check against.
     * @param predicates ArrayList of Predicates to check for.
     * @return Filtered ArrayList of Predicates applicable to the NPC.
     */
    public ArrayList<Predicate> filterNPCSubjectPredicates(NPC npc, ArrayList<Predicate> predicates) {
        // Creates the new ArrayList to be added to, and an Iterator to cycle through the original ArrayList.
        ArrayList<Predicate> filteredPredicates = new ArrayList<>(predicates);
        Iterator<Predicate> iterator = filteredPredicates.iterator();
        while (iterator.hasNext()) {
            Predicate predicate = iterator.next();
            String predicateString = predicate.getPredicateString();
            switch (predicateString) {
                case "resident" -> {
                    if (featureHasSameRelationship(npc, predicate.getOppositePredicateString())) {
                        iterator.remove();
                        // Removes if already a Resident.
                    }
                }
                case "ruler" -> {
                    if (npc.isNotAlive() || npc.isNotAdult() ||
                            featureHasSameRelationship(npc, predicate.getOppositePredicateString())) {
                        iterator.remove();
                        // Removes if dead, not an adult, or already a Ruler.
                    }
                }
                case "partner" -> {
                    if (npc.isNotAdult() || featureHasSameRelationship(npc, predicateString)) {
                        iterator.remove();
                        // Removes if not an adult, or already has a Partner.
                    }
                }
                case "parent" -> {
                    if (npc.isNotAdult() || npc.numberOfChildren() >= MAX_CHILDREN) {
                        iterator.remove();
                        // Removes if not an adult, or if they have reached the Max number of Children.
                    }
                }
                case "child" -> {
                    if (npc.hasBothParents()) {
                        iterator.remove();
                        // Removes if already has two Parents.
                    }
                }
                case "sibling" -> {
                    if (npc.numberOfSiblings() >= MAX_SIBLINGS) {
                        iterator.remove();
                        // Removes if they have reached the Max number of Siblings.
                    }
                }
                case "killed" -> {
                    if (npc.isNotAdult() || npc.isNotAlive()) {
                        iterator.remove();
                        // Removes if not an adult, or already not alive.
                    }
                }
                case "killer" -> {
                    if (npc.isNotAdult()) {
                        iterator.remove();
                        // Removes if not an adult.
                    }
                }
            }
        }
        return filteredPredicates;
    }

    /**
     * Filters which NPCs are applicable to be the Object of the given Relationship, based on the opposite
     * Predicate and the Subject Feature of the Relationship.
     * @param predicate The Predicate applied from the Object to the Subject.
     * @param subjectFeature The Subject of the Relationship
     * @param npcs An ArrayList of NPCs to choose from.
     * @return Filtered ArrayList of compatible NPCs
     */
    public ArrayList<NPC> filterNPCsByPredicate(@NotNull String predicate, Feature subjectFeature, ArrayList<NPC> npcs){
        ArrayList<NPC> filteredNPCs = new ArrayList<>(npcs);
        Iterator<NPC> npcIterator = filteredNPCs.iterator();

        // Cycles and filters Features depending on what the Predicate is.
        switch (predicate) {
            case "resident" -> {
                while (npcIterator.hasNext()) {
                    NPC npc = npcIterator.next();
                    if (npc.hasResidence()) {
                        npcIterator.remove();
                        // Removes if already a Resident.
                    }
                }
            }
            case "ruler" -> {
                while(npcIterator.hasNext()) {
                    NPC npc = npcIterator.next();
                    if (npc.hasResidence() && !subjectFeature.equals(npc.getResidence())) {
                        npcIterator.remove();
                        // Removed if is a Resident of different Settlement.
                    }
                }
            }
            case "partner" -> {
                NPC subjectNPC = (NPC) subjectFeature;
                while(npcIterator.hasNext()) {
                    NPC npc = npcIterator.next();
                    if (npc == subjectNPC || npc.isNotAdult() || npc.hasPartner() || npc.isFamilyMemberOf(subjectNPC)) {
                        npcIterator.remove();
                        // Removes if itself, not an adult, already has a Partner, or is a blood relative of.
                    }
                }
            }
            case "parent" -> {
                NPC childNPC = (NPC) subjectFeature;
                // Sets whether additional checks need to be made,
                // depending on if subject Child already has another Parent.
                boolean checkRace = false;
                String race = "";
                boolean checkGender = false;
                char gender = '0';
                boolean checkParentFamily = false;
                NPC childNPCParent = null;
                HashSet<NPC> childFamilyMembers = childNPC.getFamilyMembers();
                if(childNPC.hasOnlyOneParent()) {
                    childNPCParent = childNPC.getParents().get(0);
                    race = childNPCParent.getRace();
                    gender = childNPCParent.getGender();
                    if (!childNPC.getRace().equals(race)) {
                        checkRace = true;
                        // If the other Parent is a different race to Child, the chosen NPC must be the same race.
                    }
                    if (gender != 'n') {
                        checkGender = true;
                        // If the other Parent has a binary gender, the chosen NPC must not be the same.
                    }
                    checkParentFamily = true;
                    // Checks NPC is not a blood relative of other Parent.
                }

                while(npcIterator.hasNext()) {
                    NPC npc = npcIterator.next();
                    if (npc == childNPC || npc.isNotAdult() || !npc.isNotParentOf(childNPC) ||
                            npc.numberOfChildren() >= MAX_CHILDREN || npc.isPartnerOf(childNPC) ||
                            npc.notOldEnoughAgeGapForChild(childNPC.getAge(), getNPCRaceAdultAge(npc)) ||
                            !compatibleFamilyParent(npc, childNPC, childFamilyMembers) ||
                            (checkRace && !npc.getRace().equals(race)) || (checkGender && npc.getGender() == gender ||
                            (checkParentFamily && childNPCParent.isFamilyMemberOf(npc)))) {
                        npcIterator.remove();
                        // Removes if they are the same, is not an adult, is already the Parent of,
                        // they have max children, is the Child's Partner, wasn't an adult when Child born,
                        // isn't a compatible Parent, if other Parent is different race they aren't the same as Child,
                        // if other Parent is binary gender and is the same gender, if other Parent is blood relative.
                    }
                }
            }
            case "child" -> {
                NPC parentNPC = (NPC) subjectFeature;
                NPC parentNPCPartner = null;
                boolean parentHasPartner = parentNPC.hasPartner();
                if(parentHasPartner) {
                    parentNPCPartner = parentNPC.getPartner();
                }
                char parentNPCGender = parentNPC.getGender();
                while(npcIterator.hasNext()) {
                    NPC npc = npcIterator.next();
                    // Sets whether additional checks need to be made,
                    // depending on if Child already has one Parent.
                    boolean checkRace = false;
                    boolean checkGender = false;
                    boolean checkParentFamily = false;
                    char npcParentGender = 'n';
                    if (npc.hasOnlyOneParent()) {
                        if (!npc.hasParentOfSameRace()) { checkRace = true; }
                        // If the other Parent is a different race, the chosen NPC must be the same race.
                        npcParentGender = npc.getParents().get(0).getGender();
                        if (npcParentGender != 'n') { checkGender = true; }
                        // If the other Parent has a binary gender, the chosen NPC must not be the same.
                        checkParentFamily = true;
                        // Checks other Parent is not a blood relative of Parent.
                    }

                    if(npc == parentNPC || npc.hasBothParents() ||
                            npc.isPartnerOf(parentNPC) || !parentNPC.isNotParentOf(npc) ||
                            parentNPC.notOldEnoughAgeGapForChild(npc.getAge(), getNPCRaceAdultAge(parentNPC)) ||
                            !compatibleFamilyChild(npc, parentNPC, parentHasPartner, parentNPCPartner) ||
                            (checkRace && !npc.getRace().equals(parentNPC.getRace())) ||
                            (checkGender && npcParentGender == parentNPCGender) ||
                            (checkParentFamily && parentNPC.isFamilyMemberOf(npc.getParents().get(0)))) {
                        npcIterator.remove();
                        // Removes if the same, already has two Parents, is the Partner of, is already their Parent,
                        // is not old enough, isn't a compatible family member, if other Parent of different race
                        // they aren't the same, if other Parent has binary gender and is the same, if other Parent
                        // a blood relative.
                    }
                }
            }
            case "sibling" -> {
                NPC subjectNPC = (NPC) subjectFeature;
                // Gets the other Sibling's Parents and Siblings for checks
                ArrayList<NPC> subjectNPCParents = new ArrayList<>(subjectNPC.getParents());
                ArrayList<NPC> subjectNPCAndSiblings = new ArrayList<>(subjectNPC.getSiblings());
                subjectNPCAndSiblings.add(subjectNPC);
                while(npcIterator.hasNext()) {
                    NPC npc = npcIterator.next();
                    if(npc == subjectNPC || npc.isFamilyMemberOf(subjectNPC) || npc.isPartnerOf(subjectNPC) ||
                            !compatibleFamilySiblings(npc, subjectNPCParents, subjectNPCAndSiblings)) {
                        npcIterator.remove();
                        // Removes if the same, is already a Family Member, is a Partner of,
                        // or is not compatible family.
                    }
                }
            }
            case "killed" -> {
                while(npcIterator.hasNext()) {
                    NPC npc = npcIterator.next();
                    if (npc == subjectFeature || npc.isNotAdult() || npc.isNotAlive()) {
                        npcIterator.remove();
                        // Removes if same, not an adult, or is already dead.
                    }
                }
            }
            case "killer" -> {
                while(npcIterator.hasNext()) {
                    NPC npc = npcIterator.next();
                    if (npc == subjectFeature || npc.isNotAdult()) {
                        npcIterator.remove();
                        // Removes if the same or not an adult.
                    }
                }
            }
        }
        return filteredNPCs;
    }

    /**
     * Filters which Predicates are applicable to the given Settlement who is the Subject of the Relationship.
     * In other words, the first Feature to be selected for the Relationship.
     * @param settlement The Settlement to check against.
     * @param predicates ArrayList of Predicates to check for.
     * @return Filtered ArrayList of Predicates applicable to the NPC.
     */
    public ArrayList<Predicate> filterSettlementSubjectPredicates(Settlement settlement, ArrayList<Predicate> predicates) {
        ArrayList<Predicate> filteredPredicates = new ArrayList<>(predicates);
        Iterator<Predicate> iterator = filteredPredicates.iterator();
        while (iterator.hasNext()) {
            Predicate predicate = iterator.next();
            String predicateString = predicate.getPredicateString();
            switch (predicateString) {
                case "residence" -> {
                    if (settlement.numberOfResidents() >= settlement.getMaxResidents()) {
                        iterator.remove();
                        // Removes if it has max Residents.
                    }
                }
                case "rules" -> {
                    if (settlement.hasRuler()) {
                        iterator.remove();
                        // Removes if already has a Ruler.
                    }
                }
                case "trades" -> {
                    if (settlement.numberOfTradingSettlements() >= MAX_TRADES) {
                        iterator.remove();
                        // Removes if it has reached max Trading Settlements.
                    }
                }
                case "rival" -> {
                    if (settlement.numberOfRivalSettlements() >= MAX_RIVALS) {
                        iterator.remove();
                        // Removes if it has reached max Rival Settlements.
                    }
                }
            }
        }
        return filteredPredicates;
    }

    /**
     * Filters which Settlements are applicable to be the Object of the given Relationship, based on the opposite
     * Predicate and the Subject Feature of the Relationship.
     * @param predicate The Predicate applied from the Object to the Subject.
     * @param subjectFeature The Subject of the Relationship
     * @param settlements An ArrayList of Settlements to choose from.
     * @return Filtered ArrayList of compatible Settlements
     */
    public ArrayList<Settlement> filterSettlementsByPredicate(@NotNull String predicate, Feature subjectFeature, ArrayList<Settlement> settlements) {
        ArrayList<Settlement> filteredSettlements = new ArrayList<>(settlements);
        Iterator<Settlement> settlementIterator = filteredSettlements.iterator();

        switch (predicate) {
            case "residence" -> {
                while(settlementIterator.hasNext()) {
                    Settlement settlement = settlementIterator.next();
                    if(settlement.numberOfResidents() >= settlement.getMaxResidents()) {
                        settlementIterator.remove();
                        // Removes if reached max residents.
                    }
                }
            }
            case "rules" -> {
                NPC subjectNPC = (NPC) subjectFeature;
                Settlement subjectResidence = subjectNPC.getResidence();
                while(settlementIterator.hasNext()) {
                    Settlement settlement = settlementIterator.next();
                    if(settlement.hasRuler() || (subjectResidence != null && subjectResidence != settlement)) {
                        settlementIterator.remove();
                        // Removed if already has a ruler, or if the subject doesn't live here.
                    }
                }
            }
            case "trades" -> {
                Settlement subjectSettlement = (Settlement) subjectFeature;
                while(settlementIterator.hasNext()) {
                    Settlement settlement = settlementIterator.next();
                    if(settlement == subjectFeature || settlement.getTradeOrRival(subjectSettlement) != null ||
                        settlement.numberOfTradingSettlements() >= MAX_TRADES) {
                        settlementIterator.remove();
                        // Removes if the same, if they already Trade or are Rivals, or if it has max Trades.
                    }
                }
            }
            case "rival" -> {
                Settlement subjectSettlement = (Settlement) subjectFeature;
                while(settlementIterator.hasNext()) {
                    Settlement settlement = settlementIterator.next();
                    if(settlement == subjectFeature || settlement.getTradeOrRival(subjectSettlement) != null ||
                        settlement.numberOfRivalSettlements() >= MAX_RIVALS) {
                        settlementIterator.remove();
                        // Removes if the same, if they already Trade or are Rivals, or if it has max Rivals.
                    }
                }
            }
        }
        return filteredSettlements;
    }

    /**
     * Checks if Feature already has a Relationship type.
     * @param feature The Feature to check.
     * @param predicate The Relationship type, e.g. "ruler" if they are already a ruler.
     * @return Boolean of whether they have the Relationship.
     */
    private boolean featureHasSameRelationship(@NotNull Feature feature, String predicate) {
        return feature.getTripleRelationships().stream()
                .anyMatch(relationship -> relationship.getLeft().equals(predicate));
    }

    /**
     * Returns all NPCs of the World.
     * @return ArrayList of all NPCs.
     */
    public ArrayList<NPC> getNPCFeatures() {
        return world.getFeatures().stream()
                .filter(feature -> feature.getClass().equals(NPC.class))
                .map(feature -> (NPC) feature)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns all Settlements of the World.
     * @return ArrayList of all Settlements.
     */
    public ArrayList<Settlement> getSettlementFeatures() {
        return world.getFeatures().stream()
                .filter(feature -> feature.getClass().equals(Settlement.class))
                .map(feature -> (Settlement) feature)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns the age at which the given NPC's race reaches Adulthood.
     * @param npc The NPC to check.
     * @return The Adulthood threshold.
     */
    private int getNPCRaceAdultAge(@NotNull NPC npc) {
        return npcGenerator.getRaceDetails().get(npc.getRace())[1];
    }

    /**
     * Checks whether all the Siblings ages in a potential merge are within two age groups that are next to each other.
     * @param potentialSiblings The merged list of NPCs that might become Siblings
     * @return Boolean of whether the age groups of the potential Siblings are compatible.
     */
    private boolean compatibleSiblingsAge(@NotNull ArrayList<NPC> potentialSiblings) {
        HashSet<Character> ageGroups = new HashSet<>(); // Set of all age groups
        for(NPC npc : potentialSiblings) {
            // Adds NPC's age group to the set then checks if set contains any incompatible groups.
            char currentNPCAgeGroup = npc.getAgeGroup();
            switch (currentNPCAgeGroup) {
                case 'c' -> { if (ageGroups.contains('a') || ageGroups.contains('e')) { return false; }}
                case 't' -> { if (ageGroups.contains('e')) { return false; }}
                case 'a' -> { if (ageGroups.contains('c')) { return false; }}
                case 'e' -> { if (ageGroups.contains('c') || ageGroups.contains('t')) { return false; }}
            }
            ageGroups.add(currentNPCAgeGroup);
        }
        return true;
    }

    /**
     * Checks if the Parent is compatible with the Child's Family.
     * Slightly different to compatibleFamilyChild as to not have to retrieve Child's Family each time.
     * @param parent The potential Parent.
     * @param child The potential Child.
     * @param childFamilyMembers Family Members of the Child.
     * @return Boolean of whether they are compatible.
     */
    private boolean compatibleFamilyParent(NPC parent, NPC child, HashSet<NPC> childFamilyMembers) {
        if(parent.isFamilyMemberOf(child)) { return false; }
        if(parent.hasPartner()) {   // If Parent has a partner, checks Child isn't it, or not another Family Member.
            if(parent.isPartnerOf(child)) { return false; }
            for(NPC familyMember : childFamilyMembers) {
                if(parent.isPartnerOf(familyMember)) { return false;}
            }
        }
        // Check potential Parent was old enough for all of the Siblings that will also become their Children.
        int parentAdultAge = getNPCRaceAdultAge(parent);
        for(NPC sibling : child.getSiblings()) {
            if(parent.notOldEnoughAgeGapForChild(sibling.getAge(), parentAdultAge)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the Child, and it's Family is compatible with the Parent.
     * Slightly different to compatibleFamilyParent as to not have to retrieve Parent's partner each time.
     * @param child The potential Child.
     * @param parent The potential Parent.
     * @param parentHasPartner Whether checks of Parents partner need to be made.
     * @param parentPartner The Partner (Can be Null)
     * @return Boolean of whether they are compatible.
     */
    private boolean compatibleFamilyChild(NPC child, NPC parent, boolean parentHasPartner, NPC parentPartner) {
        if(child.isFamilyMemberOf(parent)) { return false; }
        if(parentHasPartner) {  // If Parent has a partner, checks Child isn't it, or not another Family Member.
            if(child == parentPartner) { return false; }
            HashSet<NPC> childFamilyMembers = child.getFamilyMembers();
            for(NPC familyMember : childFamilyMembers) {
                if (familyMember == parentPartner) {
                    return false;
                }
            }
        }
        // Checks the Parent is old enough for each potential new Child.
        int parentAdultAge = getNPCRaceAdultAge(parent);
        for(NPC sibling : child.getSiblings()) {
            if(parent.notOldEnoughAgeGapForChild(sibling.getAge(), parentAdultAge)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the Families of two potential Siblings are compatible.
     * @param npc The NPC which will change each iteration of checks against Subject.
     * @param subjectParents List of the subject Sibling's Parents.
     * @param subjectAndSiblings List of the subject Sibling and their pre-existing Siblings.
     * @return
     */
    private boolean compatibleFamilySiblings(@NotNull NPC npc, ArrayList<NPC> subjectParents, ArrayList<NPC> subjectAndSiblings) {
        ArrayList<NPC> allParents = new ArrayList<>(subjectParents);
        allParents.addAll(npc.getParents());
        int allParentsSize = allParents.size();
        if(allParentsSize > 2) { return false; }    // Makes sure there aren't too many Parents.
                                                    // Siblings must have the same Parents.
        ArrayList<NPC> allSiblings = new ArrayList<>(subjectAndSiblings);
        allSiblings.add(npc);
        allSiblings.addAll(npc.getSiblings());  // Merges all siblings to one list.
        if(allSiblings.size() > MAX_SIBLINGS + 1) { return false; } // Makes sure Siblings count doesn't exceed limit.

        for(NPC sibling : allSiblings) {
            for(NPC sibling2: allSiblings) {
                if(sibling == sibling2) { continue; }
                if(sibling.isPartnerOf(sibling2)) { return false; }
                // Makes sure none of the potential Siblings are Partners.
            }
            for (NPC parent : allParents) {
                if(sibling == parent) { return false; }
                if (sibling.isPartnerOf(parent)) { return false; }
                // Makes sure none of the Siblings are a Parent of another nor a Partner.
            }
        }

        for(NPC parent : allParents) {
            for(NPC parent2 : allParents) {
                if(parent == parent2) {continue;}
                if(parent.isFamilyMemberOf(parent2)) { return false; }
                // If two Parents, checks they aren't blood relatives.
            }
        }

        // Checks that the immediate potential Family of Parents and Children is not made up of more than two Races.
        HashSet<String> allRaces = new HashSet<>();
        for(NPC parent : allParents) { allRaces.add(parent.getRace()); }
        int parentsRacesSize = allRaces.size();
        for(NPC sibling : allSiblings) { allRaces.add(sibling.getRace()); }
        int allRacesSize = allRaces.size();

        if(allRacesSize > 2 || (allParentsSize != parentsRacesSize && allParentsSize == allRacesSize)) { return false; }

        // Checks age groups of potential Siblings.
        if(!compatibleSiblingsAge(allSiblings)) { return false; }

        // Makes sure the eldest Sibling is not too old for each Parent.
        if (allParentsSize > 0) {
            int oldestSiblingAge = 0;
            for(NPC sibling : allSiblings) {
                int age = sibling.getAge();
                if(age > oldestSiblingAge) { oldestSiblingAge = age; }
            }
            for(NPC parent : allParents) {
                if(parent.notOldEnoughAgeGapForChild(oldestSiblingAge, getNPCRaceAdultAge(parent))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Manages the process of Generating a new Feature for an unfinished Relationship.
     * @param unfinishedRelationships Unfinished Relationships in the World.
     * @return An ArrayList of the New Features that have been Generated.
     * @throws GenerationFailureException Throws if Failure in Generation.
     */
    public ArrayList<Feature> completeUnfinishedRelationships(@NotNull ArrayList<Relationship> unfinishedRelationships)
            throws GenerationFailureException {
        ArrayList<Feature> newFeatures = new ArrayList<>();
        Iterator<Relationship> relationshipIterator = unfinishedRelationships.iterator();
        while(relationshipIterator.hasNext()) {
            Relationship relationship = relationshipIterator.next();
            // Makes sure the Subject of the Relationship can still be the Subject
            if(isRelationshipStillPossible(relationship)) {
                if(randNum.nextBoolean()) {     // 50% chance to Generate a new Feature.
                    Predicate predicateBtoA = relationship.getPredicateBtoA();
                    switch (predicateBtoA.getRequiredSubjectClass().getSimpleName()) {
                        case "NPC" -> {
                            Feature newNPC = relationship.setSecondFeature(npcGenerator.
                                    generateFeatureFromRelationship(relationship, predicateBtoA));
                            if(newNPC != null) {
                                world.saveFeature(newNPC);
                                newFeatures.add(newNPC);
                            }
                        }
                        case "Settlement" -> {
                            Feature newSettlement = relationship.setSecondFeature(settlementGenerator.
                                    generateFeatureFromRelationship(relationship, predicateBtoA));
                            if(newSettlement != null) {
                                world.saveFeature(newSettlement);
                                newFeatures.add(newSettlement);
                            }
                        }
                    }
                    if (relationship.isCompleted()) {
                        world.saveRelationship(relationship);
                        relationshipGenerator.postRelationshipCleanUp(relationship);
                    } else {
                        // Counts Relationship timer down, removing it if not complete after 3 Generations.
                        relationship.unfinishedCountdown();
                        if(relationship.isOutOfTime()) {
                            relationshipIterator.remove();
                            world.removeUnfinishedRelationship(relationship);
                        }
                    }
                } else {
                    relationship.unfinishedCountdown();
                    if(relationship.isOutOfTime()) {
                        relationshipIterator.remove();
                        world.removeUnfinishedRelationship(relationship);
                    }
                }
            } else {
                // Removes if no longer possible.
                relationshipIterator.remove();
                world.removeUnfinishedRelationship(relationship);
            }
        }
        world.clearCompletedRelationshipsFromUnfinished();
        return newFeatures;
    }

    /**
     * Checks if the Relationship is still possible for the Subject Feature.
     * @param relationship The Relationship to check.
     * @return Boolean of whether the Relationship is still possible.
     */
    public boolean isRelationshipStillPossible(@NotNull Relationship relationship) {
        Feature feature = relationship.getFeatureA();
        Predicate predicate = relationship.getPredicateAtoB();
        switch(predicate.getPredicateString()) {
            case "resident" -> {
                NPC npc = (NPC) feature;
                if(npc.hasResidence()) { return false; }
            }
            case "ruler" -> {
                NPC npc = (NPC) feature;
                if(npc.hasResidence() || npc.isARuler()) { return false; }
            }
            case "partner" -> {
                NPC npc = (NPC) feature;
                if(npc.hasPartner()) { return false; }
            }
            case "parent" -> {
                NPC npc = (NPC) feature;
                if(npc.numberOfChildren() >= MAX_CHILDREN) { return false; }
            }
            case "child" -> {
                NPC npc = (NPC) feature;
                if(npc.hasBothParents()) { return false; }
            }
            case "sibling" -> {
                NPC npc = (NPC) feature;
                if(npc.numberOfSiblings() >= MAX_SIBLINGS) { return false; }
            }
            case "killed" -> {
                NPC npc = (NPC) feature;
                if(npc.isNotAlive()) { return false; }
            }
            case "residence" -> {
                Settlement settlement = (Settlement) feature;
                if(settlement.hasReachedMaxResidents()) { return false; }
            }
            case "rules" -> {
                Settlement settlement = (Settlement) feature;
                if(settlement.hasRuler()) { return false; }
            }
            case "trades" -> {
                Settlement settlement = (Settlement) feature;
                if(settlement.getTradingSettlements().size() >= MAX_TRADES) { return false; }
            }
            case "rival" -> {
                Settlement settlement = (Settlement) feature;
                if(settlement.getRivalSettlements().size() >= MAX_RIVALS) { return false; }
            }
        }
        return true;
    }
}

