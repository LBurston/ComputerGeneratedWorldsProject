package generators;

import exceptions.GenerationFailureException;
import features.*;
import generators.feature.NPCGenerator;
import generators.feature.SettlementGenerator;
import org.jetbrains.annotations.NotNull;
import relationships.Predicate;
import relationships.Relationship;

import java.util.*;
import java.util.stream.Collectors;

public class FeatureManager {

    private static FeatureManager featureManager = null;

    private static final RelationshipGenerator relationshipGenerator = RelationshipGenerator.getRelationshipGenerator();
    private static final NPCGenerator npcGenerator = NPCGenerator.getNPCGenerator();
    private static final SettlementGenerator settlementGenerator = SettlementGenerator.getSettlementGenerator();

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
                features.add(npcGenerator.generateFeature());
            } catch (GenerationFailureException ex) {
                ex.printStackTrace();
            }
        }
        for(int i = 0; i < settlements; i++) {
            try {
                features.add(settlementGenerator.generateFeature());
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
                    if (npc == subjectFeature || npc.isNotAdult() || npc.hasPartner() || npc.isFamilyMember(subjectNPC)) {
                        npcIterator.remove();
                    }
                }
            }
            case "parent" -> {
                NPC subjectNPC = (NPC) subjectFeature;
                boolean checkRace = false;
                boolean checkGender = false;
                if (subjectNPC.numberOfParents() == 1) {
                    NPC subjectNPCParent = subjectNPC.getParents().get(0);
                    char subjectNPCParentGender = subjectNPCParent.getGender();
                    if (!subjectNPC.getRace().equals(subjectNPCParent.getRace())) {
                        checkRace = true;
                    }
                    if (subjectNPCParentGender != 'n') {
                        checkGender = true;
                    }
                    while (npcIterator.hasNext()) {
                        NPC npc = npcIterator.next();
                        if (npc == subjectFeature || npc.isNotAdult() || npc.isFamilyMember(subjectNPC) ||
                                npc.isPartner(subjectNPC) || npc.numberOfChildren() >= MAX_CHILDREN ||
                                (checkRace && !npc.getRace().equals(subjectNPC.getRace())) ||
                                (checkGender && subjectNPCParentGender == npc.getGender()) ||
                                npc.oldEnoughAgeGapForChild(subjectNPC.getAge(), getNPCRaceAdultAge(npc))) {
                            npcIterator.remove();
                        }
                    }
                } else {
                    while (npcIterator.hasNext()) {
                        NPC npc = npcIterator.next();
                        if (npc == subjectFeature || npc.isNotAdult() ||
                                npc.isPartner(subjectNPC) || npc.numberOfChildren() >= MAX_CHILDREN ||
                                npc.oldEnoughAgeGapForChild(subjectNPC.getAge(), getNPCRaceAdultAge(npc))) {
                            npcIterator.remove();
                        }
                    }
                }
            }
            case "child" -> {
                NPC subjectNPC = (NPC) subjectFeature;
                char subjectNPCGender = subjectNPC.getGender();
                switch (subjectNPCGender) {
                    case 'm' -> {
                        while(npcIterator.hasNext()) {
                            NPC npc = npcIterator.next();
                            if(npc.hasFather()) {
                                npcIterator.remove();
                                continue;
                            }
                            boolean checkRace = npc.hasOneParent() && !npc.hasParentOfSameRace();
                            if (npc == subjectFeature || npc.isPartner(subjectNPC) ||
                                    npc.isFamilyMember(subjectNPC) || npc.hasBothParents() ||
                                    (checkRace && npc.getRace().equals(subjectNPC.getRace())) ||
                                    subjectNPC.oldEnoughAgeGapForChild(npc.getAge(), getNPCRaceAdultAge(subjectNPC))) {
                                npcIterator.remove();
                            }
                        }
                    }
                    case 'f' -> {
                        while(npcIterator.hasNext()) {
                            NPC npc = npcIterator.next();
                            if(npc.hasMother()) {
                                npcIterator.remove();
                                continue;
                            }
                            boolean checkRace = npc.hasOneParent() && !npc.hasParentOfSameRace();
                            if (npc == subjectFeature || npc.isPartner(subjectNPC) || npc.hasBothParents() ||
                                    (checkRace && npc.getRace().equals(subjectNPC.getRace())) ||
                                    subjectNPC.oldEnoughAgeGapForChild(npc.getAge(), getNPCRaceAdultAge(subjectNPC))) {
                                npcIterator.remove();
                            }
                        }
                    }
                    default -> {
                        while(npcIterator.hasNext()) {
                            NPC npc = npcIterator.next();
                            boolean checkRace = npc.hasOneParent() && !npc.hasParentOfSameRace();
                            if (npc == subjectFeature || npc.isPartner(subjectNPC) || npc.hasBothParents() ||
                                    (checkRace && npc.getRace().equals(subjectNPC.getRace())) ||
                                    subjectNPC.oldEnoughAgeGapForChild(npc.getAge(), getNPCRaceAdultAge(subjectNPC))) {
                                npcIterator.remove();
                            }
                        }
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
                    if(npc == subjectNPC || npc.isFamilyMember(subjectNPC) || npc.isPartner(subjectNPC) ||
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
        return feature.getRelationships().stream()
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

    private @NotNull ArrayList<NPC> getPotentialSiblings(NPC npc1, NPC npc2) {
        ArrayList<NPC> potentialSiblings = new ArrayList<>();
        potentialSiblings.add(npc1);
        potentialSiblings.add(npc2);
        potentialSiblings.addAll(npc1.getSiblings());
        potentialSiblings.addAll(npc2.getSiblings());
        return potentialSiblings;
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
                if(sibling.isPartner(sibling2)) { return false; }
            }
            for (NPC parent : allParents) {
                if(sibling == parent) { return false; }
                if (sibling.isPartner(parent)) { return false; }
            }
        }

        for(NPC parent : allParents) {
            for(NPC parent2 : allParents) {
                if(parent.isFamilyMember(parent2)) { return false; }
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
}

