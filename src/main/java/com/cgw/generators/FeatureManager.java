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

public class FeatureManager {

    private static FeatureManager featureManager = null;

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
     * This constructor is to be used in the full generator
     */
    private FeatureManager() {

    }

    public void setWorld(World world) {
        this.world = world;
    }

    public static FeatureManager getFeatureManager() {
        if (featureManager == null) {
            featureManager = new FeatureManager();
        }
        return featureManager;
    }

    public ArrayList<Feature> generateFeatures(int npcs, int settlements) {
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

    public ArrayList<Predicate> filterNPCSubjectPredicates(NPC npc, ArrayList<Predicate> predicates) {
        ArrayList<Predicate> filteredPredicates = new ArrayList<>(predicates);
        Iterator<Predicate> iterator = filteredPredicates.iterator();
        while (iterator.hasNext()) {
            Predicate predicate = iterator.next();
            String predicateString = predicate.getPredicateString();
            switch (predicateString) {
                case "resident" -> {
                    if (featureHasSameRelationship(npc, predicate.getOppositePredicateString())) {
                        iterator.remove();
                    }
                }
                case "ruler" -> {
                    if (npc.isNotAlive() || npc.isNotAdult() ||
                            featureHasSameRelationship(npc, predicate.getOppositePredicateString())) {
                        iterator.remove();
                    }
                }
                case "partner" -> {
                    if (npc.isNotAdult() || featureHasSameRelationship(npc, predicateString)) {
                        iterator.remove();
                    }
                }
                case "parent" -> {
                    if (npc.isNotAdult() || npc.numberOfChildren() >= MAX_CHILDREN) {
                        iterator.remove();
                    }
                }
                case "child" -> {
                    if (npc.hasBothParents()) {
                        iterator.remove();
                    }
                }
                case "sibling" -> {
                    if (npc.numberOfSiblings() >= MAX_SIBLINGS) {
                        iterator.remove();
                    }
                }
                case "killed" -> {
                    if (npc.isNotAdult() || npc.isNotAlive()) {
                        iterator.remove();
                    }
                }
                case "killer" -> {
                    if (npc.isNotAdult()) {
                        iterator.remove();
                    }
                }
            }
        }
        return filteredPredicates;
    }

    public ArrayList<NPC> filterNPCsByPredicate(@NotNull String predicate, Feature subjectFeature, ArrayList<NPC> npcs) {
        ArrayList<NPC> filteredNPCs = new ArrayList<>(npcs);
        Iterator<NPC> npcIterator = filteredNPCs.iterator();

        switch (predicate) {
            case "resident" -> {
                while (npcIterator.hasNext()) {
                    NPC npc = npcIterator.next();
                    if (npc.hasResidence()) {
                        npcIterator.remove();
                    }
                }
            }
            case "ruler" -> {
                while(npcIterator.hasNext()) {
                    NPC npc = npcIterator.next();
                    if (npc.hasResidence() && !subjectFeature.equals(npc.getResidence())) {
                        npcIterator.remove();
                    }
                }
            }
            case "partner" -> {
                NPC subjectNPC = (NPC) subjectFeature;
                while(npcIterator.hasNext()) {
                    NPC npc = npcIterator.next();
                    if (npc == subjectNPC || npc.isNotAdult() || npc.hasPartner() || npc.isFamilyMemberOf(subjectNPC)) {
                        npcIterator.remove();
                    }
                }
            }
            case "parent" -> {
                NPC childNPC = (NPC) subjectFeature;
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
                    }
                    if (gender != 'n') {
                        checkGender = true;
                    }
                    checkParentFamily = true;
                }

                while(npcIterator.hasNext()) {
                    NPC npc = npcIterator.next();
                    if (npc == childNPC || npc.isNotAdult() ||
                            npc.numberOfChildren() >= MAX_CHILDREN || npc.isPartnerOf(childNPC) ||
                            !npc.oldEnoughAgeGapForChild(childNPC.getAge(), getNPCRaceAdultAge(npc)) ||
                            !compatibleFamilyParent(npc, childNPC, childFamilyMembers) ||
                            (checkRace && !npc.getRace().equals(race)) || (checkGender && npc.getGender() == gender ||
                            (checkParentFamily && childNPCParent.isFamilyMemberOf(npc)))) {
                        npcIterator.remove();
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
                    boolean checkRace = false;
                    boolean checkGender = false;
                    boolean checkParentFamily = false;
                    char npcParentGender = 'n';
                    if (npc.hasOnlyOneParent()) {
                        if (!npc.hasParentOfSameRace()) { checkRace = true; }
                        npcParentGender = npc.getParents().get(0).getGender();
                        if (npcParentGender != 'n') { checkGender = true; }
                        checkParentFamily = true;
                    }

                    if(npc == parentNPC || npc.hasBothParents() || npc.isPartnerOf(parentNPC) ||
                            !parentNPC.oldEnoughAgeGapForChild(npc.getAge(), getNPCRaceAdultAge(parentNPC)) ||
                            !compatibleFamilyChild(npc, parentNPC, parentHasPartner, parentNPCPartner) ||
                            (checkRace && !npc.getRace().equals(parentNPC.getRace())) ||
                            (checkGender && npcParentGender == parentNPCGender) ||
                            (checkParentFamily && parentNPC.isFamilyMemberOf(npc.getParents().get(0)))) {
                        npcIterator.remove();
                    }
                }
            }
            case "sibling" -> {
                NPC subjectNPC = (NPC) subjectFeature;
                ArrayList<NPC> subjectNPCParents = new ArrayList<>(subjectNPC.getParents());
                ArrayList<NPC> subjectNPCAndSiblings = new ArrayList<>(subjectNPC.getSiblings());
                subjectNPCAndSiblings.add(subjectNPC);
                while(npcIterator.hasNext()) {
                    NPC npc = npcIterator.next();
                    if(npc == subjectNPC || npc.isFamilyMemberOf(subjectNPC) || npc.isPartnerOf(subjectNPC) ||
                            !compatibleFamilySiblings(npc, subjectNPCParents, subjectNPCAndSiblings)) {
                        npcIterator.remove();
                    }
                }
            }
            case "killed" -> {
                while(npcIterator.hasNext()) {
                    NPC npc = npcIterator.next();
                    if (npc == subjectFeature || npc.isNotAdult() || npc.isNotAlive()) {
                        npcIterator.remove();
                    }
                }
            }
            case "killer" -> {
                while(npcIterator.hasNext()) {
                    NPC npc = npcIterator.next();
                    if (npc == subjectFeature || npc.isNotAdult()) {
                        npcIterator.remove();
                    }
                }
            }
        }
        return filteredNPCs;
    }

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
                    }
                }
                case "rules" -> {
                    if (settlement.hasRuler()) {
                        iterator.remove();
                    }
                }
                case "trades" -> {
                    if (settlement.numberOfTradingSettlements() >= MAX_TRADES) {
                        iterator.remove();
                    }
                }
                case "rival" -> {
                    if (settlement.numberOfRivalSettlements() >= MAX_RIVALS) {
                        iterator.remove();
                    }
                }
            }
        }
        return filteredPredicates;
    }

    public ArrayList<Settlement> filterSettlementsByPredicate(@NotNull String predicate, Feature subjectFeature, ArrayList<Settlement> settlements) {
        ArrayList<Settlement> filteredSettlements = new ArrayList<>(settlements);
        Iterator<Settlement> settlementIterator = filteredSettlements.iterator();

        switch (predicate) {
            case "residence" -> {
                while(settlementIterator.hasNext()) {
                    Settlement settlement = settlementIterator.next();
                    if(settlement.numberOfResidents() >= settlement.getMaxResidents()) {
                        settlementIterator.remove();
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
                    }
                }
            }
        }
        return filteredSettlements;
    }

    private boolean featureHasSameRelationship(@NotNull Feature feature, String predicate) {
        return feature.getTripleRelationships().stream()
                .anyMatch(relationship -> relationship.getLeft().equals(predicate));
    }

    public ArrayList<NPC> getNPCFeatures() {
        return world.getFeatures().stream()
                .filter(feature -> feature.getClass().equals(NPC.class))
                .map(feature -> (NPC) feature)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Settlement> getSettlementFeatures() {
        return world.getFeatures().stream()
                .filter(feature -> feature.getClass().equals(Settlement.class))
                .map(feature -> (Settlement) feature)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private int getNPCRaceAdultAge(@NotNull NPC npc) {
        return npcGenerator.getRaceDetails().get(npc.getRace())[1];
    }

    private boolean compatibleSiblingsAge(@NotNull ArrayList<NPC> potentialSiblings) {
        ArrayList<Character> ageGroups = new ArrayList<>();
        for(NPC npc : potentialSiblings) {
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

    private boolean compatibleFamilyParent(NPC parent, NPC child, HashSet<NPC> childFamilyMembers) {
        if(parent.isFamilyMemberOf(child)) { return false; }
        if(parent.hasPartner()) {
            if(parent.isPartnerOf(child)) { return false; }
            for(NPC familyMember : childFamilyMembers) {
                if(parent.isPartnerOf(familyMember)) { return false;}
            }
        }
        int parentAdultAge = getNPCRaceAdultAge(parent);
        for(NPC sibling : child.getSiblings()) {
            if(!parent.oldEnoughAgeGapForChild(sibling.getAge(), parentAdultAge)) {
                return false;
            }
        }
        return true;
    }

    private boolean compatibleFamilyChild(NPC child, NPC parent, boolean parentHasPartner, NPC parentPartner) {
        if(child.isFamilyMemberOf(parent)) { return false; }
        if(parentHasPartner) {
            if(child == parentPartner) { return false; }
            HashSet<NPC> childFamilyMembers = child.getFamilyMembers();
            for(NPC familyMember : childFamilyMembers) {
                if (familyMember == parentPartner) {
                    return false;
                }
            }
        }
        int parentAdultAge = getNPCRaceAdultAge(parent);
        for(NPC sibling : child.getSiblings()) {
            if(!parent.oldEnoughAgeGapForChild(sibling.getAge(), parentAdultAge)) {
                return false;
            }
        }
        return true;
    }

    private boolean compatibleFamilySiblings(@NotNull NPC npc, ArrayList<NPC> subjectParents, ArrayList<NPC> subjectAndSiblings) {
        ArrayList<NPC> allParents = new ArrayList<>(subjectParents);
        allParents.addAll(npc.getParents());
        int allParentsSize = allParents.size();
        if(allParentsSize > 2) { return false; }

        ArrayList<NPC> allSiblings = new ArrayList<>(subjectAndSiblings);
        allSiblings.add(npc);
        allSiblings.addAll(npc.getSiblings());
        if(allSiblings.size() > MAX_SIBLINGS + 1) { return false; }

        for(NPC sibling : allSiblings) {
            for(NPC sibling2: allSiblings) {
                if(sibling == sibling2) { continue; }
                if(sibling.isPartnerOf(sibling2)) { return false; }
            }
            for (NPC parent : allParents) {
                if(sibling == parent) { return false; }
                if (sibling.isPartnerOf(parent)) { return false; }
            }
        }

        for(NPC parent : allParents) {
            for(NPC parent2 : allParents) {
                if(parent.isFamilyMemberOf(parent2)) { return false; }
            }
        }

        HashSet<String> allRaces = new HashSet<>();
        for(NPC parent : allParents) { allRaces.add(parent.getRace()); }
        int parentsRacesSize = allRaces.size();
        for(NPC sibling : allSiblings) { allRaces.add(sibling.getRace()); }
        int allRacesSize = allRaces.size();

        if(allRacesSize > 2 || (allParentsSize != parentsRacesSize && allParentsSize == allRacesSize)) { return false; }

        if(!compatibleSiblingsAge(allSiblings)) { return false; }

        if (allParentsSize > 0) {
            int oldestSiblingAge = 0;
            for(NPC sibling : allSiblings) {
                int age = sibling.getAge();
                if(age > oldestSiblingAge) { oldestSiblingAge = age; }
            }
            for(NPC parent : allParents) {
                if(!parent.oldEnoughAgeGapForChild(oldestSiblingAge, getNPCRaceAdultAge(parent))) {
                    return false;
                }
            }
        }
        return true;
    }

    public ArrayList<Feature> completeUnfinishedRelationships(@NotNull ArrayList<Relationship> unfinishedRelationships) throws GenerationFailureException {
        ArrayList<Feature> newFeatures = new ArrayList<>();
        Iterator<Relationship> relationshipIterator = unfinishedRelationships.iterator();
        while(relationshipIterator.hasNext()) {
            Relationship relationship = relationshipIterator.next();
            if(isRelationshipStillPossible(relationship)) {
                if(randNum.nextBoolean()) {
                    Predicate predicateBtoA = relationship.getPredicateBtoA();
                    switch (predicateBtoA.getRequiredSubjectClass().getSimpleName()) {
                        case "NPC" -> {
                            Feature newNPC = relationship.setSecondFeature(npcGenerator.generateFeatureFromRelationship(relationship, predicateBtoA));
                            if(newNPC != null) {
                                world.saveFeature(newNPC);
                                newFeatures.add(newNPC);
                            }
                        }
                        case "Settlement" -> {
                            Feature newSettlement = relationship.setSecondFeature(settlementGenerator.generateFeatureFromRelationship(relationship, predicateBtoA));
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
                relationshipIterator.remove();
                world.removeUnfinishedRelationship(relationship);
            }
        }
        world.clearCompletedRelationshipsFromUnfinished();
        return newFeatures;
    }

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

