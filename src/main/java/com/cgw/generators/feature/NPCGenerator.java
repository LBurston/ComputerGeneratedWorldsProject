package com.cgw.generators.feature;

import com.cgw.exceptions.GenerationFailureException;
import org.jetbrains.annotations.NotNull;
import com.cgw.relationships.Predicate;
import com.cgw.relationships.Relationship;
import com.cgw.features.*;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

public class NPCGenerator extends FeatureGenerator {

    private static NPCGenerator npcGenerator = null;

    private final HashMap<String, int[]> raceDetails;
    private final ArrayList<String> races;

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

    public static NPCGenerator getNPCGenerator() {
        if(npcGenerator == null) {
            npcGenerator = new NPCGenerator();
        }
        return npcGenerator;
    }

    /**
     * Imports all the txt files into ArrayLists and HashMaps
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
     * Generates a new NPC.
     *
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

    public NPC generateFeatureFromRelationship(@NotNull Relationship relationship, Predicate predicateBtoA) throws GenerationFailureException {
        switch (predicateBtoA.getPredicateString()) {
            case "resident" -> { return generateRandomFeature(); }
            case "ruler", "partner", "killed", "killer" -> {
                try {
                    NPC currentNPC = new NPC();
                    currentNPC.setRace(assignRace());
                    currentNPC.setGender(assignGender());
                    currentNPC.setName(assignFullName(currentNPC.getGender()));

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
                try {
                    NPC subjectNPC = (NPC) relationship.getFeatureA();
                    NPC currentNPC = new NPC();

                    if(subjectNPC.hasParentOfSameRace()) {
                        currentNPC.setRace(subjectNPC.getSiblingsDifferentRace());
                    } else {
                        currentNPC.setRace(subjectNPC.getRace());
                    }

                    if(subjectNPC.hasMother()) {
                        if(randNum.nextInt(5) < 4) {
                            currentNPC.setGender('m');
                        } else { currentNPC.setGender('n');}
                    } else if(subjectNPC.hasFather()) {
                        if(randNum.nextInt(5) < 4) {
                            currentNPC.setGender('f');
                        } else { currentNPC.setGender('n');}
                    } else {
                        currentNPC.setGender(assignGender());
                    }

                    String name = "";
                    int safetyCheck = 500;
                    while(name.isEmpty()) {
                        name = generateFirstName(currentNPC.getGender());
                        name += " " + subjectNPC.getName().split(" ")[1];
                        if(usedNames.contains(name) && safetyCheck > 0) {
                            name = "";
                            safetyCheck--;
                        }
                    }
                    currentNPC.setName(name);

                    int youngestAgeBound = subjectNPC.eldestSiblingAge();
                    int oldestPossibleAdultAge = raceDetails.get(currentNPC.getRace())[2];
                    int oldestPossibleAge = raceDetails.get(currentNPC.getRace())[3];
                    if (youngestAgeBound < oldestPossibleAge) {
                        currentNPC.setAge(randNum.nextInt(youngestAgeBound, oldestPossibleAge));
                    } else if ( youngestAgeBound == oldestPossibleAge) {
                        currentNPC.setAge(oldestPossibleAge);
                    } else {
                        currentNPC.setAge(oldestPossibleAge + randNum.nextInt(oldestPossibleAdultAge));
                        currentNPC.setAlive(false);
                    }

                    if(currentNPC.getAge() < oldestPossibleAdultAge) { currentNPC.setAgeGroup('a'); }
                    else { currentNPC.setAgeGroup('e'); }


                    return currentNPC;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw new GenerationFailureException("Unable to Generate NPC");
                }
            }
            case "child" -> {
                try {
                    NPC subjectNPC = (NPC) relationship.getFeatureA();
                    NPC currentNPC = new NPC();

                    currentNPC.setRace(subjectNPC.getRace());
                    currentNPC.setGender(assignGender());

                    String name = "";
                    int safetyCheck = 500;
                    while(name.isEmpty()) {
                        name = generateFirstName(currentNPC.getGender());
                        name += " " + subjectNPC.getName().split(" ")[1];
                        if(usedNames.contains(name) && safetyCheck > 0) {
                            name = "";
                            safetyCheck--;
                        }
                    }
                    currentNPC.setName(name);

                    int maxAge;
                    if(subjectNPC.hasPartner() && subjectNPC.getPartner().getAge() < subjectNPC.getAge()) {
                        NPC subjectNPCPartner = subjectNPC.getPartner();
                        maxAge = subjectNPCPartner.getAge() - raceDetails.get(subjectNPCPartner.getRace())[1];
                    } else { maxAge = subjectNPC.getAge() - raceDetails.get(subjectNPC.getRace())[1]; }

                    currentNPC.setAge(randNum.nextInt(0, maxAge+1));

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
        }
        return null;
    }

    /**
     * Randomly assigns a Race to the NPC.
     * @return A String of the assigned Race
     */
    private String assignRace() throws GenerationFailureException {
        return races.get(randNum.nextInt(races.size()));
    }

    /**
     * Randomly assigns a gender to the NPC.
     *
     * @return A char to represent the gender
     */
    private char assignGender() {
        int genderNum = randNum.nextInt(100);
        if (genderNum < 45) { return 'f'; }     // Female
        else if (genderNum < 90) { return 'm'; }// Male
        else { return 'n'; }                    // Non-Binary
    }

    /**
     * Assigns a full name to the newly generated NPC based on their Gender
     *
     * @param gender gender of the NPC
     * @return A String of the full name of the NPC
     */
    private String assignFullName(char gender) {
        String name = "";
        int safetyCheck = 5000;
        // Generates name and checks if it has already been generated.
        while (name.isEmpty()) {
            // Assign First Name based on Gender
            if (gender == 'f') {
                name = firstNamesFemale.get(randNum.nextInt(firstNamesFemale.size()));
            } else if (gender == 'm') {
                name = firstNamesMale.get(randNum.nextInt(firstNamesMale.size()));
            } else if (randNum.nextBoolean()) {
                name = firstNamesMale.get(randNum.nextInt(firstNamesMale.size()));
            } else {
                name = firstNamesFemale.get(randNum.nextInt(firstNamesFemale.size()));
            }

            // Assign Last Name
            name += " " + lastNames.get(randNum.nextInt(lastNames.size()));

            // Checks isn't already taken, if it fails safetyCheck times, just returns last generated name
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
     *
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
     *
     * @param race The race of the NPC.
     * @param ageGroup The age group of the NPC.
     * @return The assigned age as an int value.
     */
    private int assignAge(String race, char ageGroup) {
        int lowerBound = 0;
        int upperBound = raceDetails.get(race)[3];
        switch (ageGroup) {
            case 'c' -> {
                lowerBound = 0;
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

    public void addToUsedNames(String newName) {
        usedNames.add(newName);
    }


    /* Getters */

    public HashMap<String, int[]> getRaceDetails() {
        return raceDetails;
    }

    public ArrayList<String> getRaces() {
        return races;
    }

    public ArrayList<String> getFirstNamesFemale() {
        return firstNamesFemale;
    }

    public ArrayList<String> getFirstNamesMale() {
        return firstNamesMale;
    }

    public ArrayList<String> getLastNames() {
        return lastNames;
    }

    public ArrayList<String> getUsedNames() {
        return usedNames;
    }
}
