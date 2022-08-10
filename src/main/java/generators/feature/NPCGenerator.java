package generators.feature;

import exceptions.GenerationFailureException;
import features.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class NPCGenerator extends FeatureGenerator{

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
        String resourceLocation = "src/main/resources/npc/";
        BufferedReader reader;
        String currentLine;

        /* Import races */
        try {
            reader = new BufferedReader(new
                    FileReader(resourceLocation + "npcRace.txt"));
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
            reader = new BufferedReader(new
                    FileReader(resourceLocation + "npcFirstNamesFemale.txt"));
            while((currentLine = reader.readLine()) != null) {
                firstNamesFemale.add(currentLine);
            }
            /* Import First Names (Male) */
            reader = new BufferedReader(new
                    FileReader(resourceLocation + "npcFirstNamesMale.txt"));
            while((currentLine = reader.readLine()) != null) {
                firstNamesMale.add(currentLine);
            }
            /* Import Last Names */
            reader = new BufferedReader(new
                    FileReader(resourceLocation + "npcLastNames.txt"));
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
    public NPC generateFeature() throws GenerationFailureException {
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
     * Randomly assigns a Race to the NPC.
     * @return A String of the assigned Race
     */
    private String assignRace() {
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
            if (usedNames.contains(name)) {
                name = "";
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
        int lowerBound;
        int upperBound;
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
            default -> {
                lowerBound = 0;
                upperBound = raceDetails.get(race)[3];
            }
        }
        return randNum.nextInt(lowerBound, upperBound);
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
}
