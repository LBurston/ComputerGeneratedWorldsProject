package com.cgw.generators.feature;

import com.cgw.exceptions.GenerationFailureException;
import org.jetbrains.annotations.NotNull;
import com.cgw.relationships.Predicate;
import com.cgw.relationships.Relationship;
import com.cgw.features.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A Generator for NPCs.
 * @author Luke Burston
 * @author lb800@kent.ac.uk
 * @version 0.1
 * @since 0.1
 */
public class NPCGenerator extends FeatureGenerator {

    // Singleton instance of itself.
    private static NPCGenerator npcGenerator = null;

    // The imported races and details of their age group thresholds.
    private final ArrayList<String> races;
    private final HashMap<String, int[]> raceDetails;

    // The ArrayLists of different types of names.
    private final ArrayList<String> firstNamesFemale;
    private final ArrayList<String> firstNamesMale;
    private final ArrayList<String> lastNames;

    private final ArrayList<String> usedNames;  // Stores assigned names

    /**
     * Constructor for the NPC Generator to create a random
     * number generator and to import all the resources.
     */
    private NPCGenerator() {
        raceDetails = new HashMap<>();
        races = new ArrayList<>();

        firstNamesFemale = new ArrayList<>();
        firstNamesMale = new ArrayList<>();
        lastNames = new ArrayList<>();

        usedNames = new ArrayList<>();

        importResources();
    }

    /**
     * Returns this Generator, or creates a new one if it hasn't been set up yet.
     * @return The NPC Generator Singleton.
     */
    public static NPCGenerator getNPCGenerator() {
        if(npcGenerator == null) {
            npcGenerator = new NPCGenerator();
        }
        return npcGenerator;
    }

    /**
     * Imports all the Resource txt files into ArrayLists and HashMaps,
     * by reading the txt file line by line and saving it to its respective field.
     */
    protected void importResources() {
        String resourceLocation = "/npc/";
        BufferedReader reader;
        String currentLine;

        /* Import races */
        try {
            InputStream is = getClass().getResourceAsStream(resourceLocation + "npcRace.txt");
            assert is != null;
            reader = new BufferedReader(new
                    InputStreamReader(is));
            while((currentLine = reader.readLine()) != null) {
                String[] race = currentLine.split("\t");
                int noOfDetails = race.length-1;
                int[] details = new int[noOfDetails];
                for(int detail = 0; detail < noOfDetails; detail++) {
                    details[detail] = Integer.parseInt(race[detail+1]);
                }
                raceDetails.put(race[0], details);
            }
            races.addAll(raceDetails.keySet());
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }


        /* Import names */
        try {
            /* Import First Names (Female) */
            InputStream is1 = getClass().getResourceAsStream(resourceLocation + "npcFirstNamesFemale.txt");
            assert is1 != null;
            reader = new BufferedReader(new
                    InputStreamReader(is1));
            while((currentLine = reader.readLine()) != null) {
                firstNamesFemale.add(currentLine);
            }
            /* Import First Names (Male) */
            InputStream is2 = getClass().getResourceAsStream(resourceLocation + "npcFirstNamesMale.txt");
            assert is2 != null;
            reader = new BufferedReader(new
                    InputStreamReader(is2));
            while((currentLine = reader.readLine()) != null) {
                firstNamesMale.add(currentLine);
            }
            /* Import Last Names */
            InputStream is3 = getClass().getResourceAsStream(resourceLocation + "npcLastNames.txt");
            assert is3 != null;
            reader = new BufferedReader(new
                    InputStreamReader(is3));
            while((currentLine = reader.readLine()) != null) {
                lastNames.add(currentLine);
            }
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Generates a new NPC with randomly chosen attributes.
     * @return The generated NPC.
     */
    public NPC generateRandomFeature() throws GenerationFailureException {
        try {
            NPC currentNPC = new NPC();
            currentNPC.setRace(assignRace());
            currentNPC.setGender(assignGender());
            currentNPC.setName(assignFullName(currentNPC.getGender()));
            currentNPC.setAgeGroup(assignAgeGroup());
            currentNPC.setAge(assignAge(currentNPC.getRace(), currentNPC.getAgeGroup()));

            return currentNPC;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new GenerationFailureException("Unable to Generate NPC");
        }
    }

    /**
     * Generates a compatible NPC from a given Relationship.
     * @param relationship The Relationship to generate from.
     * @param predicateBtoA The Predicate of the Relationship that will be assigned to the Feature.
     * @return The Generated NPC.
     * @throws GenerationFailureException Exception for Failure to Generate NPC.
     */
    public NPC generateFeatureFromRelationship(@NotNull Relationship relationship, Predicate predicateBtoA) throws GenerationFailureException {
        switch (predicateBtoA.getPredicateString()) {
            case "resident" -> { return generateRandomFeature(); }
            case "ruler", "partner", "killed", "killer" -> {
                try {
                    // For generating a Ruler, Partner, Victim, or Killer.
                    NPC currentNPC = new NPC();
                    currentNPC.setRace(assignRace());
                    currentNPC.setGender(assignGender());
                    currentNPC.setName(assignFullName(currentNPC.getGender()));

                    // Ensures an adult is generated.
                    char chosenAgeGroup = '0';
                    if(randNum.nextBoolean()) { chosenAgeGroup = 'a'; }
                    else { chosenAgeGroup = 'e'; }
                    currentNPC.setAgeGroup(chosenAgeGroup);

                    currentNPC.setAge(assignAge(currentNPC.getRace(), currentNPC.getAgeGroup()));

                    return currentNPC;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw new GenerationFailureException("Unable to Generate NPC");
                }
            }
            case "parent" -> {
                // For Generating a Parent.
                try {
                    NPC child = (NPC) relationship.getFeatureA();
                    NPC currentNPC = new NPC();

                    // Checks if child has a Parent of the same race already.
                    // If so, checks if any of their Siblings are of a different race,
                    // to make sure all siblings have one parent of their race. Otherwise, makes them the same race.
                    if(child.hasParentOfSameRace()) {
                        currentNPC.setRace(child.getSiblingsDifferentRace());
                    } else {
                        currentNPC.setRace(child.getRace());
                    }

                    // Checks if child has a male or female Parent already, to make the Generated Parent
                    // either the opposite gender or non-binary. Otherwise, chooses randomly.
                    if(child.hasMother()) {
                        if(randNum.nextInt(5) < 4) {
                            currentNPC.setGender('m');
                        } else { currentNPC.setGender('n');}
                    } else if(child.hasFather()) {
                        if(randNum.nextInt(5) < 4) {
                            currentNPC.setGender('f');
                        } else { currentNPC.setGender('n');}
                    } else {
                        currentNPC.setGender(assignGender());
                    }

                    // Generates a new name with the last name of the Child.
                    // Checks 5000 times if generated name does not already exist.
                    String name = "";
                    int safetyCheck = 5000;
                    while(name.isEmpty()) {
                        name = generateFirstName(currentNPC.getGender());
                        name += " " + child.getName().split(" ")[1];
                        if(usedNames.contains(name) && safetyCheck > 0) {
                            name = "";
                            safetyCheck--;
                        }
                    }
                    currentNPC.setName(name);

                    // Gives the new Parent an age that is old enough to be the Parent of the oldest Sibling.
                    int eldestSiblingAge = child.eldestSiblingAge();
                    int youngestPossibleAdultAge = raceDetails.get(currentNPC.getRace())[1];
                    int oldestPossibleAdultAge = raceDetails.get(currentNPC.getRace())[2];
                    int oldestPossibleAge = raceDetails.get(currentNPC.getRace())[3];
                    if (eldestSiblingAge < oldestPossibleAge - eldestSiblingAge) {
                        // If the eldest Siblings age is younger than the generated NPC's older possible age,
                        // then the age set is chosen between the youngest Possible age plus the eldest Sibling's age,
                        // and the oldest possible age the NPC can be alive.
                        currentNPC.setAge(randNum.nextInt(youngestPossibleAdultAge + eldestSiblingAge, oldestPossibleAge));
                    } else if ( youngestPossibleAdultAge == oldestPossibleAge) {
                        // If instead it is exactly, it just sets the age as the eldest possible.
                        currentNPC.setAge(oldestPossibleAge);
                    } else {
                        // If it is not possible to generate an alive NPC with a suitable age for the eldest
                        // Sibling of the Child, then gives them an age above their eldest possible age and the
                        // eldest Sibling's age.
                        currentNPC.setAge(oldestPossibleAge + 1 + randNum.nextInt(eldestSiblingAge));
                        currentNPC.setAlive(false);
                    }

                    // Sets their age group depending on the age given.
                    if(currentNPC.getAge() < oldestPossibleAdultAge) { currentNPC.setAgeGroup('a'); }
                    else { currentNPC.setAgeGroup('e'); }

                    return currentNPC;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw new GenerationFailureException("Unable to Generate NPC");
                }
            }
            case "child" -> {
                // For Generating a Child.
                try {
                    NPC parent = (NPC) relationship.getFeatureA();
                    NPC currentNPC = new NPC();

                    currentNPC.setRace(parent.getRace());
                    currentNPC.setGender(assignGender());

                    // Generates a new name with the last name of the Parent.
                    // Checks 5000 times if generated name does not already exist.
                    String name = "";
                    int safetyCheck = 5000;
                    while(name.isEmpty()) {
                        name = generateFirstName(currentNPC.getGender());
                        name += " " + parent.getName().split(" ")[1];
                        if(usedNames.contains(name) && safetyCheck > 0) {
                            name = "";
                            safetyCheck--;
                        }
                    }
                    currentNPC.setName(name);

                    // Sets the age of the Child to be less than that of the Parent's age minus their adult threshold.
                    int maxAge = parent.getAge() - raceDetails.get(parent.getRace())[1];
                    currentNPC.setAge(randNum.nextInt(0, maxAge+1));

                    // Loops through the age ranges to find the correct age range.
                    int[] maxAges = raceDetails.get(currentNPC.getRace());
                    int age = currentNPC.getAge();
                    int count = 0;
                    for(int ageRange : maxAges) {
                        if(age-ageRange < 0) {
                            switch(count) {
                                case 0 -> { currentNPC.setAgeGroup('c'); }
                                case 1 -> { currentNPC.setAgeGroup('t'); }
                                case 2 -> { currentNPC.setAgeGroup('a'); }
                                case 3 -> { currentNPC.setAgeGroup('e'); }
                            }
                            break;
                        }
                        count++;
                    }

                    return currentNPC;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw new GenerationFailureException("Unable to Generate NPC");
                }
            }
            case "sibling" -> {
                // For Generating a Sibling.
                try {
                    NPC originalSibling = (NPC) relationship.getFeatureA();
                    NPC currentNPC = new NPC();

                    currentNPC.setRace(originalSibling.getRace());  // Sets race to be the same as the Sibling.
                    currentNPC.setGender(assignGender());

                    // Generates a new name with the last name of the Parent.
                    // Checks 5000 times if generated name does not already exist.
                    String name = "";
                    int safetyCheck = 5000;
                    while(name.isEmpty()) {
                        name = generateFirstName(currentNPC.getGender());
                        name += " " + originalSibling.getName().split(" ")[1];
                        if(usedNames.contains(name) && safetyCheck > 0) {
                            name = "";
                            safetyCheck--;
                        }
                    }
                    currentNPC.setName(name);

                    // Sets age group to be the same as the Sibling.
                    currentNPC.setAgeGroup(originalSibling.getAgeGroup());
                    // Gets the youngest possible age in its age group.
                    int maxAgeGroupIndex = 0;
                    int youngestPossibleAge = 0;
                    int[] currentNPCRaceDetails = raceDetails.get(currentNPC.getRace());
                    switch (currentNPC.getAgeGroup()) {
                        case 't' -> {
                            maxAgeGroupIndex = 1;
                            youngestPossibleAge = currentNPCRaceDetails[0];
                        }
                        case 'a' -> {
                            maxAgeGroupIndex = 2;
                            youngestPossibleAge = currentNPCRaceDetails[1];
                        }
                        case 'e' -> {
                            maxAgeGroupIndex = 3;
                            youngestPossibleAge = currentNPCRaceDetails[2];
                        }
                    }

                    if(originalSibling.numberOfParents() > 0) {
                        // If the Sibling has any Parents, gets the age of the one who most recently became an adult.
                        // Also gets that age at which they would have turned an adult.
                        ArrayList<NPC> newParents = originalSibling.getParents();
                        int parentClosestToAdultThresholdAge = Integer.MAX_VALUE;
                        int adultThresholdOfParentClosest = 0;
                        for(NPC newParent : newParents) {
                            if(newParent.getAge() - (raceDetails.get(newParent.getRace())[1])
                                    < parentClosestToAdultThresholdAge) {
                                parentClosestToAdultThresholdAge = newParent.getAge();
                                adultThresholdOfParentClosest = raceDetails.get(newParent.getRace())[1];
                            }
                        }
                        int oldestPossibleAge = parentClosestToAdultThresholdAge - adultThresholdOfParentClosest;

                        if(youngestPossibleAge == oldestPossibleAge) {
                            // If there is only one age they can be, sets it to that
                            currentNPC.setAge(
                                    randNum.nextInt(youngestPossibleAge));
                        } else if (oldestPossibleAge < currentNPCRaceDetails[maxAgeGroupIndex]){
                            // If the oldestPossibleAge from Parents is younger than that max for their age group,
                            // sets the eldest possible age as age group.
                            currentNPC.setAge(
                                    randNum.nextInt(youngestPossibleAge, oldestPossibleAge));
                        } else {
                            // Otherwise, gives them an age within their age group.
                            currentNPC.setAge(
                                    randNum.nextInt(youngestPossibleAge, currentNPCRaceDetails[maxAgeGroupIndex]));
                        }
                    } else {
                        // If no pre-existing parent, the age given is between its age groups thresholds.
                        currentNPC.setAge(
                                randNum.nextInt(youngestPossibleAge, currentNPCRaceDetails[maxAgeGroupIndex]));
                    }

                    return currentNPC;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw new GenerationFailureException("Unable to Generate NPC");
                }
            }
        }
        return null;
    }

    /**
     * Randomly assigns a Race to the NPC.
     * @return A String of the assigned Race.
     */
    private String assignRace() throws GenerationFailureException {
        return races.get(randNum.nextInt(races.size()));
    }

    /**
     * Randomly assigns a gender to the NPC.
     * @return A char to represent the gender.
     */
    private char assignGender() {
        int genderNum = randNum.nextInt(100);
        if (genderNum < 45) { return 'f'; }     // Female
        else if (genderNum < 90) { return 'm'; }// Male
        else { return 'n'; }                    // Non-Binary
    }

    /**
     * Assigns a full name to the newly generated NPC based on their Gender.
     * @param gender gender of the NPC.
     * @return A String of the full name of the NPC.
     */
    private String assignFullName(char gender) {
        String name = "";
        int safetyCheck = 5000;
        // Generates name and checks if it has already been generated.
        while (name.isEmpty()) {
            // Assign First Name based on Gender.
            if (gender == 'f') {
                name = firstNamesFemale.get(randNum.nextInt(firstNamesFemale.size()));
            } else if (gender == 'm') {
                name = firstNamesMale.get(randNum.nextInt(firstNamesMale.size()));
            } else if (randNum.nextBoolean()) { // If Non-Binary randomly chooses from either list.
                name = firstNamesMale.get(randNum.nextInt(firstNamesMale.size()));
            } else {
                name = firstNamesFemale.get(randNum.nextInt(firstNamesFemale.size()));
            }

            // Assign Last Name
            name += " " + lastNames.get(randNum.nextInt(lastNames.size()));

            // Checks isn't already taken, if it fails 5000 times, just returns last generated name.
            if (usedNames.contains(name) && safetyCheck > 0) {
                name = "";
                safetyCheck--;
            }
        }
        usedNames.add(name);
        return name;
    }

    /**
     * Randomly assigns an age group to the NPC.
     * @return A char to represent the age group.
     */
    private char assignAgeGroup() {
        int ageNum = randNum.nextInt(100);
        if (ageNum < 80) { return 'a'; }       // Adult
        else if (ageNum < 95) { return 'e'; }  // Elderly
        else if (ageNum < 99) { return 't'; }  // Adolescent/Teen
        else { return 'c'; }                   // Child
    }

    /**
     * Assigns the age to the NPC based on their race and age group.
     * @param race The race of the NPC.
     * @param ageGroup The age group of the NPC.
     * @return The assigned age as an int value.
     */
    private int assignAge(String race, char ageGroup) {
        int lowerBound = 0;
        int upperBound = raceDetails.get(race)[3];
        switch (ageGroup) {
            case 'c' -> {
                upperBound = raceDetails.get(race)[0];
            }
            case 't' -> {
                lowerBound = raceDetails.get(race)[0];
                upperBound = raceDetails.get(race)[1];
            }
            case 'a' -> {
                lowerBound = raceDetails.get(race)[1];
                upperBound = raceDetails.get(race)[2];
            }
            case 'e' -> {
                lowerBound = raceDetails.get(race)[2];
                upperBound = raceDetails.get(race)[3];
            }
        }
        return randNum.nextInt(lowerBound, upperBound);
    }

    /**
     * Generates a random first name based on the given Gender.
     * @param gender The given Gender.
     * @return The first name.
     */
    public String generateFirstName(char gender) {
        if (gender == 'f') {
            return firstNamesFemale.get(randNum.nextInt(firstNamesFemale.size()));
        } else if (gender == 'm') {
            return firstNamesMale.get(randNum.nextInt(firstNamesMale.size()));
        } else if (randNum.nextBoolean()) {
            return firstNamesMale.get(randNum.nextInt(firstNamesMale.size()));
        } else {
            return firstNamesFemale.get(randNum.nextInt(firstNamesFemale.size()));
        }
    }

    /**
     * Adds to the ArrayList of used names, to avoid duplicates
     * @param newName The new name being added.
     */
    public void addToUsedNames(String newName) {
        usedNames.add(newName);
    }

    /* Getters */

    /**
     * Returns the race details HashMap of each race as a key and their age threshold array as the value.
     * @return HashMap of the race details.
     */
    public HashMap<String, int[]> getRaceDetails() {
        return raceDetails;
    }

    /**
     * Returns all the names currently assigned in the World.
     * @return An ArrayList of the used names.
     */
    public ArrayList<String> getUsedNames() {
        return usedNames;
    }
}
