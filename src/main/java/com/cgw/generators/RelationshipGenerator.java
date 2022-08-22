package com.cgw.generators;

import com.cgw.exceptions.GenerationFailureException;
import com.cgw.features.*;
import com.cgw.features.World;
import com.cgw.generators.feature.NPCGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.cgw.relationships.Predicate;
import com.cgw.relationships.Relationship;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class RelationshipGenerator {

    private static RelationshipGenerator relationshipGenerator = null;

    private static final FeatureManager featureManager = FeatureManager.getFeatureManager();
    private static final Random randNum = Randomiser.getRandom();

    private static final int NO_RELATIONSHIP_WEIGHT = 20;

    private World world;
    private final ArrayList<Predicate> predicates;
    private final HashMap<String, Predicate> predicateHashMap;

    private RelationshipGenerator() {
        predicates = new ArrayList<>();
        predicateHashMap = new HashMap<>();
        importPredicates();
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public static RelationshipGenerator getRelationshipGenerator() {
        if(relationshipGenerator == null) {
            relationshipGenerator = new RelationshipGenerator();
        }
        return relationshipGenerator;
    }

    public ArrayList<Predicate> sortPredicatesBySubjectClass(Class<? extends Feature> subjectClass) {
        return predicates.stream()
                .filter(predicate -> predicate.getRequiredSubjectClass() == subjectClass)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Relationship generateNewRelationship(@NotNull Feature feature) {
        // Filters out any Predicate that isn't for a subject with the same Feature subclass
        ArrayList<Predicate> subjectPredicates = sortPredicatesBySubjectClass(feature.getClass());
        // Passes Predicates to the Feature, which passes to the correct method in the Feature Manager
        // for that subclass to filter out incompatible relationships with that Feature
        ArrayList<Predicate> possiblePredicates = feature.filterSubjectPredicates(subjectPredicates);

        // If there are no compatible Predicates, moves onto next Feature, otherwise:
        if(!possiblePredicates.isEmpty()) {
            int predicateSize = possiblePredicates.size();
            // An array for Predicates weighting, plus an extra for not creating a Relationship
            int[] weightPool = new int[predicateSize + 1];
            int predicateIndex = 0;
            // Saves each Predicates weighting for being chosen into the array
            for(Predicate predicate : possiblePredicates) {
                weightPool[predicateIndex] = predicate.getWeight(); // Change this to a method to adjust weight based on feature & its relationships //
                predicateIndex++;
            }
            // Saves the weight of not choosing a relationship to the end of the array
            // (Affected by the feature's current number of relationships)
            weightPool[predicateSize] = NO_RELATIONSHIP_WEIGHT * feature.numberOfRelationships();

            // Adds all weights together then saves the value of each weight divided by the sum to a float array
            // This allows for dynamically choosing the Predicate, changing weighting depending on what other
            // Predicates can be selected. Randomly chooses a float between 0-1, then one by one each weight
            // value is taken away until one reduces it below 0, giving the randomly chosen Predicate
            int chosenIndex = Randomiser.getRandomIndexFromWeighting(weightPool);
            if(chosenIndex == predicateSize) {
                return null;
            }
            Predicate chosenPredicate = possiblePredicates.get(chosenIndex);

            // Checks chosenPredicate has been set and isn't null
            if(Objects.nonNull(chosenPredicate)) {
                Predicate oppositePredicate = chosenPredicate.getOppositePredicate();
                Class<? extends Feature> requiredFeatureClass = chosenPredicate.getRequiredObjectClass();
                switch (requiredFeatureClass.getSimpleName()) {
                    case "NPC" -> {
                        ArrayList<NPC> objectFeaturesOfCorrectType =
                                featureManager.getNPCFeatures();
                        ArrayList<NPC> possibleObjectFeatures = featureManager.filterNPCsByPredicate(
                                oppositePredicate.getPredicateString(), feature, objectFeaturesOfCorrectType);
                        if (possibleObjectFeatures.isEmpty()) {
                            return new Relationship(feature, chosenPredicate, oppositePredicate);
                        } else {
                            int choosingIndex = randNum.nextInt(possibleObjectFeatures.size());
                            NPC chosenObject = possibleObjectFeatures.get(choosingIndex);
                            return new Relationship(feature, chosenObject, chosenPredicate, oppositePredicate);
                        }
                    }
                    case "Settlement" -> {
                        ArrayList<Settlement> objectFeaturesOfCorrectType =
                                featureManager.getSettlementFeatures();
                        ArrayList<Settlement> possibleObjectFeatures = featureManager.filterSettlementsByPredicate(
                                oppositePredicate.getPredicateString(), feature, objectFeaturesOfCorrectType);
                        if (possibleObjectFeatures.isEmpty()) {
                            return new Relationship(feature, chosenPredicate, oppositePredicate);
                        } else {
                            int choosingIndex = randNum.nextInt(possibleObjectFeatures.size());
                            Settlement chosenObject = possibleObjectFeatures.get(choosingIndex);
                            return new Relationship(feature, chosenObject, chosenPredicate, oppositePredicate);
                        }
                    }
                }
            }
        }
        return null;
    }

    private void importPredicates() {
        BufferedReader reader;
        String currentLine;

        try {
            InputStream is = getClass().getResourceAsStream("/predicates/predicates.txt");
            assert is != null;
            reader = new BufferedReader(new
                    InputStreamReader(is));
            ArrayList<String> oppositePredicates = new ArrayList<>();
            while((currentLine = reader.readLine()) != null) {
                if (!currentLine.isBlank()) {
                    String[] details = currentLine.split("\t");
                    predicates.add(new Predicate(
                            stringToClass(details[0]),
                            details[1],
                            stringToClass(details[2]),
                            Boolean.parseBoolean(details[3]),
                            Integer.parseInt(details[4])
                    ));
                    oppositePredicates.add(details[5]);
                }
            }
            for (int index = 0; index < oppositePredicates.size(); index++) {
                Predicate currentPredicate = predicates.get(index);
                if(!currentPredicate.getBiDirectional()) {

                    Predicate oppositePredicate = getPredicateFromString(oppositePredicates.get(index));
                    if(Objects.nonNull(oppositePredicate)) {
                        currentPredicate.setOppositePredicate(oppositePredicate);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }

        for(Predicate predicate : predicates) {
            predicateHashMap.put(predicate.getPredicateString(), predicate);
        }

    }

    private Class<? extends Feature> stringToClass(String feature) {
        return switch (feature) {
            case "NPC" -> NPC.class;
            case "Settlement" -> Settlement.class;
            default -> null;
        };
    }

    private @Nullable Predicate getPredicateFromString(String string) {
        for(Predicate predicate : predicates) {
            if(predicate.getPredicateString().equals(string)) {
                return predicate;
            }
        }
        return null;
    }

    public void postRelationshipCleanUp(@NotNull Relationship relationship) throws GenerationFailureException {
        Predicate[] relationshipPredicates = relationship.getBothPredicates();
        Unidirectional: for(int index = 0; index < 2; index++) {
            Predicate currentPredicate = relationshipPredicates[index];
            switch (currentPredicate.getPredicateString()) {
                case "ruler" -> {
                    NPC newResident = (NPC) relationship.getFeatureFromPredicate(currentPredicate);
                    Settlement settlement = (Settlement) relationship.getOtherFeature(newResident);
                    if(!settlement.isResidenceOf(newResident)) {
                        Predicate predAtoB = predicateHashMap.get("resident");
                        Predicate predBtoA = predicateHashMap.get("residence");

                        Relationship newRelationship = new Relationship(newResident, settlement, predAtoB, predBtoA);
                        if (newRelationship.isCompleted()) {
                            world.saveRelationship(newRelationship);
                        } else {
                            throw new GenerationFailureException("Null objects in Post Relationship Generation");
                        }
                    }
                }
                case "parent" -> {
                    NPC parent = (NPC) relationship.getFeatureFromPredicate(currentPredicate);
                    NPC child = (NPC) relationship.getOtherFeature(parent);
                    Predicate childPredicate = relationship.getOtherPredicate(currentPredicate);

                    ArrayList<NPC> childSiblings = child.getSiblings();
                    String parentLastName;
                    if(child.hasOnlyOneParent()) {
                        parentLastName = parent.getName().split(" ")[1];
                    } else {
                        ArrayList<NPC> parents = child.getParents();
                        parentLastName = parents.get(randNum.nextInt(2)).getName().split(" ")[1];
                    }
                    String newChildName = child.getName().split(" ")[0] + " " + parentLastName;

                    child.setName(filterDuplicateNames(newChildName, child.getGender()));

                    for(NPC sibling : childSiblings) {
                        if (!parent.isParentOf(sibling)) {
                            Relationship newRelationship =
                                    new Relationship(parent, sibling, currentPredicate, childPredicate);
                            if (newRelationship.isCompleted()) {
                                String newSiblingName = sibling.getName().split(" ")[0] + " " + parentLastName;
                                sibling.setName(filterDuplicateNames(newSiblingName, sibling.getGender()));
                                world.saveRelationship(newRelationship);
                            } else {
                                throw new GenerationFailureException("Null objects in Post Relationship Generation");
                            }
                        }
                    }

                    NPC childParent = child.getOtherParent(parent);
                    if (childParent != null && !parent.hasPartner() && !childParent.hasPartner()
                            && randNum.nextInt(10) < 7) {
                            Predicate partner = getPredicateFromString("partner");
                            Relationship newRelationship = new Relationship(parent, childParent, partner, partner);
                        if (newRelationship.isCompleted()) {
                            world.saveRelationship(newRelationship);
                        } else {
                            throw new GenerationFailureException("Null objects in Post Relationship Generation");
                        }
                    }
                }
                case "sibling" -> {
                    Feature[] siblings = relationship.getBothFeatures();
                    NPC npcA = (NPC) siblings[0];
                    NPC npcB = (NPC) siblings[1];
                    ArrayList<NPC> siblingsA = npcA.getSiblings();
                    ArrayList<NPC> siblingsB = npcB.getSiblings();
                    ArrayList<NPC> parentsA = npcA.getParents();
                    ArrayList<NPC> parentsB = npcB.getParents();

                    for(NPC siblingA : siblingsA) {
                        if (npcB != siblingA && !npcB.isSiblingOf(siblingA)) {
                            Relationship newRelationship =
                                    new Relationship(npcB, siblingA, currentPredicate, currentPredicate);
                            if (newRelationship.isCompleted()) {
                                world.saveRelationship(newRelationship);
                            } else {
                                throw new GenerationFailureException("Null objects in Post Relationship Generation");
                            }
                        }
                        for(NPC siblingB : siblingsB) {
                            if (siblingA != siblingB && !siblingB.isSiblingOf(siblingA)) {
                                Relationship newRelationship =
                                        new Relationship(siblingA, siblingB, currentPredicate, currentPredicate);
                                if (newRelationship.isCompleted()) {
                                    world.saveRelationship(newRelationship);
                                } else {
                                    throw new GenerationFailureException("Null objects in Post Relationship Generation");
                                }
                            }
                        }
                    }
                    for(NPC siblingB : siblingsB) {
                        if (npcA != siblingB && !npcA.isSiblingOf(siblingB)) {
                            Relationship newRelationship =
                                    new Relationship(npcA, siblingB, currentPredicate, currentPredicate);
                            if (newRelationship.isCompleted()) {
                                world.saveRelationship(newRelationship);
                            } else {
                                throw new GenerationFailureException("Null objects in Post Relationship Generation");
                            }
                        }
                    }
                    Predicate child = predicateHashMap.get("child");
                    Predicate parent = predicateHashMap.get("parent");
                    pairParentsAndSiblings(npcB, siblingsB, parentsA, child, parent);
                    pairParentsAndSiblings(npcA, siblingsB, parentsB, child, parent);

                    if(parentsA.size() == 1 && parentsB.size() == 1) {
                        NPC parentA = parentsA.get(0);
                        NPC parentB = parentsB.get(0);
                        if(!parentA.hasPartner() && !parentB.hasPartner()
                            && !parentA.isPartnerOf(parentB)) {
                            Predicate partner = predicateHashMap.get("partner");
                            Relationship newRelationship =
                                new Relationship(parentsA.get(0), parentsB.get(0), partner, partner);
                            if (newRelationship.isCompleted()) {
                                world.saveRelationship(newRelationship);
                            } else {
                                throw new GenerationFailureException("Null objects in Post Relationship Generation");
                            }
                        }
                    }

                    break Unidirectional;
                }
                case "killed" -> {
                    NPC victim = (NPC) relationship.getFeatureFromPredicate(currentPredicate);
                    victim.setAlive(false);

                    Relationship rulingRelationship = victim.getSettlementTheyRule();
                    if(rulingRelationship != null) { rulingRelationship.selfDestruct(); }
                }
            }
        }
    }

    private void pairParentsAndSiblings(NPC npcA, ArrayList<NPC> siblingsB, ArrayList<NPC> parentsB, Predicate child, Predicate parent) throws GenerationFailureException {
        for(NPC parentB : parentsB) {
            if (!parentB.isParentOf(npcA)) {
                Relationship newRelationship = new Relationship(npcA, parentB, child, parent);
                if (newRelationship.isCompleted()) {
                    world.saveRelationship(newRelationship);
                } else {
                    throw new GenerationFailureException("Null objects in Post Relationship Generation");
                }
            }
            for(NPC siblingA : siblingsB) {
                if (!parentB.isParentOf(siblingA)) {
                    Relationship newRelationship = new Relationship(siblingA, parentB, child, parent);
                    if (newRelationship.isCompleted()) {
                        world.saveRelationship(newRelationship);
                    } else {
                        throw new GenerationFailureException("Null objects in Post Relationship Generation");
                    }
                }
            }
        }
    }

    private String filterDuplicateNames(String newName, char gender) {
        ArrayList<String> usedNames = NPCGenerator.getNPCGenerator().getUsedNames();
        if(usedNames.contains(newName)) {
            boolean nameSet = false;
            int safetyCheck = 500;
            String lastName = newName.split(" ")[1];
            String changedName = newName;
            while (!nameSet && safetyCheck > 0) {
                safetyCheck--;
                changedName = NPCGenerator.getNPCGenerator().generateFirstName(gender)
                        + " " + lastName;
                if(!usedNames.contains(changedName)) {
                    nameSet = true;
                }
            }
            NPCGenerator.getNPCGenerator().addToUsedNames(changedName);
            return changedName;
        }
        NPCGenerator.getNPCGenerator().addToUsedNames(newName);
        return newName;
    }

    /**
     * Sets the random number generator to a specific seed
     * @param seed Seed number
     */
    public void setSeedRandom(long seed) {
        randNum.setSeed(seed);
    }
}
