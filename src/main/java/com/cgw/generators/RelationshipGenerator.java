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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * A Generator for Relationships.
 * @author Luke Burston
 * @author lb800@kent.ac.uk
 * @version 0.1
 * @since 0.1
 */
public class RelationshipGenerator {

    // Singleton instance of itself.
    private static RelationshipGenerator relationshipGenerator = null;

    // Feature Manager Singleton and Random.
    private static final FeatureManager featureManager = FeatureManager.getFeatureManager();
    private static final Random randNum = Randomiser.getRandom();

    // The weighting for not choosing a Relationship.
    private static final int NO_RELATIONSHIP_WEIGHT = 20;

    private World world;
    private final ArrayList<Predicate> predicates;  // List of all Predicates of Relationships.
    private final HashMap<String, Predicate> predicateHashMap;  // Predicates Mapped to String value.

    /**
     * Constructor for the Relationship Generator to create Relationships.
     */
    private RelationshipGenerator() {
        predicates = new ArrayList<>();
        predicateHashMap = new HashMap<>();
        importPredicates();
    }

    /**
     * Sets the current World
     * @param world The World.
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * Returns this Generator, or creates a new one if it hasn't been set up yet.
     * @return The Relationship Generator Singleton.
     */
    public static RelationshipGenerator getRelationshipGenerator() {
        if(relationshipGenerator == null) {
            relationshipGenerator = new RelationshipGenerator();
        }
        return relationshipGenerator;
    }

    /**
     * Filters the Predicates by their Subject Feature subclass.
     * @param subjectClass The Feature subclass to filter by.
     * @return Filtered ArrayList of Predicates.
     */
    public ArrayList<Predicate> sortPredicatesBySubjectClass(Class<? extends Feature> subjectClass) {
        return predicates.stream()
                .filter(predicate -> predicate.getRequiredSubjectClass() == subjectClass)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Generates a new Relationship for a given Feature.
     * @param feature The Feature.
     * @return The Relationship Generated
     */
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
                        // If the required Object is an NPC, gets them and filters out any that are incompatible.
                        ArrayList<NPC> objectFeaturesOfCorrectType =
                                featureManager.getNPCFeatures();
                        ArrayList<NPC> possibleObjectFeatures = featureManager.filterNPCsByPredicate(
                                oppositePredicate.getPredicateString(), feature, objectFeaturesOfCorrectType);
                        if (possibleObjectFeatures.isEmpty()) {
                            // If no compatible NPCs found, creates an unfinished Relationship.
                            return new Relationship(feature, chosenPredicate, oppositePredicate);
                        } else {
                            // Chooses an NPC at random and creates the Relationship.
                            int choosingIndex = randNum.nextInt(possibleObjectFeatures.size());
                            NPC chosenObject = possibleObjectFeatures.get(choosingIndex);
                            return new Relationship(feature, chosenObject, chosenPredicate, oppositePredicate);
                        }
                    }
                    case "Settlement" -> {
                        // If the required Object is a Settlement, gets them and filters out any that are incompatible.
                        ArrayList<Settlement> objectFeaturesOfCorrectType =
                                featureManager.getSettlementFeatures();
                        ArrayList<Settlement> possibleObjectFeatures = featureManager.filterSettlementsByPredicate(
                                oppositePredicate.getPredicateString(), feature, objectFeaturesOfCorrectType);
                        if (possibleObjectFeatures.isEmpty()) {
                            // If no compatible Settlements found, creates an unfinished Relationship.
                            return new Relationship(feature, chosenPredicate, oppositePredicate);
                        } else {
                            // Chooses a Settlement at random and creates the Relationship.
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

    /**
     * Imports all Predicates and their details from the txt resource file and creates a new Predicate object for it.
     * This allows for easily adding new Predicates via the txt file.
     */
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
                    predicates.add(new Predicate(               // Creates new Predicate.
                            stringToClass(details[0]),          // The required subject Feature subclass.
                            details[1],                         // The subject to object Predicate string.
                            stringToClass(details[2]),          // The required object Feature subclass.
                            Boolean.parseBoolean(details[3]),   // If it is Bidirectional.
                            Integer.parseInt(details[4])        // The weighting that Predicate has for being chosen.
                    ));
                    oppositePredicates.add(details[5]);         // The opposite Predicate for the other direction.
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

    /**
     * Returns the Subclass associated with a String.
     * @param feature The String name of the Feature type.
     * @return The matching Class.
     */
    private Class<? extends Feature> stringToClass(String feature) {
        return switch (feature) {
            case "NPC" -> NPC.class;
            case "Settlement" -> Settlement.class;
            default -> null;
        };
    }

    /**
     * Returns the Predicate associated with a String.
     * @param string The Predicate String of the Predicate.
     * @return The Predicate Object.
     */
    private @Nullable Predicate getPredicateFromString(String string) {
        for(Predicate predicate : predicates) {
            if(predicate.getPredicateString().equals(string)) {
                return predicate;
            }
        }
        return null;
    }

    /**
     * Once a Relationship has been created, for certain types additional tasks must be carried out.
     * For example, making all Siblings of a new Child-Parent Relationship also a Child of the Parent.
     * @param relationship The completed Relationship.
     * @throws GenerationFailureException Throws if failure in Relationship Generation.
     */
    public void postRelationshipCleanUp(@NotNull Relationship relationship) throws GenerationFailureException {
        Predicate[] relationshipPredicates = relationship.getBothPredicates();
        Unidirectional: for(int index = 0; index < 2; index++) {
            Predicate currentPredicate = relationshipPredicates[index];
            switch (currentPredicate.getPredicateString()) {
                case "ruler" -> {
                    // Makes Ruler a Resident of the Settlement if not already.
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
                    // Changes Child's last name to the Parent's and makes new Child-Parent Relationships for Siblings.
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
                        if (parent.isNotParentOf(sibling)) {
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
                    // Makes all other Siblings of both new Siblings, Siblings with each other, and Children
                    // of the other's Parent(s).
                    Feature[] siblings = relationship.getBothFeatures();
                    NPC npcA = (NPC) siblings[0];
                    NPC npcB = (NPC) siblings[1];
                    ArrayList<NPC> siblingsA = npcA.getSiblings();
                    ArrayList<NPC> siblingsB = npcB.getSiblings();
                    ArrayList<NPC> parentsA = npcA.getParents();
                    ArrayList<NPC> parentsB = npcB.getParents();

                    for(NPC siblingA : siblingsA) {
                        if (npcB != siblingA && npcB.isNotSiblingOf(siblingA)) {
                            Relationship newRelationship =
                                    new Relationship(npcB, siblingA, currentPredicate, currentPredicate);
                            if (newRelationship.isCompleted()) {
                                world.saveRelationship(newRelationship);
                            } else {
                                throw new GenerationFailureException("Null objects in Post Relationship Generation");
                            }
                        }
                        for(NPC siblingB : siblingsB) {
                            if (siblingA != siblingB && siblingB.isNotSiblingOf(siblingA)) {
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
                        if (npcA != siblingB && npcA.isNotSiblingOf(siblingB)) {
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
                    // Makes the victim of a Killer dead and removes their Ruler Relationship, if they have one.
                    NPC victim = (NPC) relationship.getFeatureFromPredicate(currentPredicate);
                    victim.setAlive(false);

                    Relationship rulingRelationship = victim.getSettlementTheyRule();
                    if(rulingRelationship != null) { rulingRelationship.selfDestruct(); }
                }
            }
        }
    }

    /**
     * Method for making all Siblings of and Parents of another, this NPCs as well.
     * @param npcA The NPC to pair to.
     * @param siblingsB The other NPC's Siblings.
     * @param parentsB The other NPC's Parents.
     * @param child The Child Predicate.
     * @param parent The Parent Predicate.
     * @throws GenerationFailureException Throws if failure in Relationship Generation.
     */
    private void pairParentsAndSiblings(NPC npcA, ArrayList<NPC> siblingsB, ArrayList<NPC> parentsB,
                                        Predicate child, Predicate parent) throws GenerationFailureException {
        for(NPC parentB : parentsB) {
            if (parentB.isNotParentOf(npcA)) {
                Relationship newRelationship = new Relationship(npcA, parentB, child, parent);
                if (newRelationship.isCompleted()) {
                    world.saveRelationship(newRelationship);
                } else {
                    throw new GenerationFailureException("Null objects in Post Relationship Generation");
                }
            }
            for(NPC siblingA : siblingsB) {
                if (parentB.isNotParentOf(siblingA)) {
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

    /**
     * Checks if a newly given First name is a duplicate in existence and changes their First name.
     * @param newName The newly given name.
     * @param gender The Gender of the NPC.
     * @return The same or changed name for the NPC.
     */
    private String filterDuplicateNames(String newName, char gender) {
        ArrayList<String> usedNames = NPCGenerator.getNPCGenerator().getUsedNames();
        if(usedNames.contains(newName)) {
            boolean nameSet = false;
            int safetyCheck = 5000;
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
}
