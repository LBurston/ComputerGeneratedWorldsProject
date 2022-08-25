package com.cgw.generators.feature;

import com.cgw.exceptions.GenerationFailureException;
import com.cgw.exceptions.NoMoreNamesException;
import com.cgw.features.*;
import org.jetbrains.annotations.NotNull;
import com.cgw.relationships.Predicate;
import com.cgw.relationships.Relationship;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A Generator for Settlementss.
 * @author Luke Burston
 * @author lb800@kent.ac.uk
 * @version 0.1
 * @since 0.1
 */
public class SettlementGenerator extends FeatureGenerator {

    // Singleton instance of itself.
    private static SettlementGenerator settlementGenerator = null;

    // The imported types and their population thresholds for different sizes.
    private final ArrayList<String> types;
    private final HashMap<String, int[]> typeDetails;

    // The ArrayLists of different types of names
    private final ArrayList<String> singleNames;
    private final ArrayList<String> prefixNames;
    private final ArrayList<String> suffixNames;

    private final ArrayList<String> usedNames;  // Stores assigned names

    // Static values of each Settlement types max residents for Stopping Criteria checks.
    private static final int MAX_HAMLET_RESIDENTS = 20;
    private static final int MAX_VILLAGE_RESIDENTS = 40;
    private static final int MAX_SMALL_TOWN_RESIDENTS = 80;
    private static final int MAX_LARGE_TOWN_RESIDENTS = 100;
    private static final int MAX_SMALL_CITY_RESIDENTS = 150;
    private static final int MAX_LARGE_CITY_RESIDENTS = 200;

    /**
     * Constructor for the Settlement Generator to create a random
     * number generator and to import all the resources.
     */
    private SettlementGenerator() {
        typeDetails = new HashMap<>();
        types = new ArrayList<>();

        singleNames = new ArrayList<>();
        prefixNames = new ArrayList<>();
        suffixNames = new ArrayList<>();

        usedNames = new ArrayList<>();

        importResources();
    }

    /**
     * Returns this Generator, or creates a new one if it hasn't been set up yet.
     * @return The Settlement Generator Singleton.
     */
    public static SettlementGenerator getSettlementGenerator() {
        if(settlementGenerator == null) {
            settlementGenerator = new SettlementGenerator();
        }
        return settlementGenerator;
    }

    /**
     * Imports all the txt files into ArrayLists and HashMaps,
     * by reading the txt file line by line and saving it to its respective field.
     */
    protected void importResources() {
        String resourceLocation = "/settlement/";
        BufferedReader reader;
        String currentLine;

        /* Import types */
        try {
            InputStream is = getClass().getResourceAsStream(resourceLocation+"settlementTypes.txt");
            assert is != null;
            reader = new BufferedReader(new
                    InputStreamReader(is));
            while((currentLine = reader.readLine()) != null) {
                String[] type = currentLine.split("\t");
                int noOfDetails = type.length-1;
                int[] details = new int[noOfDetails];
                for(int detail = 0; detail < noOfDetails; detail++) {
                    details[detail] = Integer.parseInt(type[detail+1]);
                }
                typeDetails.put(type[0], details);
            }
            types.addAll(typeDetails.keySet());
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }

        /* Import Names */
        try {
            /* Import Single Names */
            InputStream is1 = getClass().getResourceAsStream(resourceLocation + "settlementSingleNames.txt");
            assert is1 != null;
            reader = new BufferedReader(new
                    InputStreamReader(is1));
            while((currentLine = reader.readLine()) != null) {
                singleNames.add(currentLine);
            }
            /* Import Prefix Names */
            InputStream is2 = getClass().getResourceAsStream(resourceLocation + "settlementPrefixNames.txt");
            assert is2 != null;
            reader = new BufferedReader(new
                    InputStreamReader(is2));
            while((currentLine = reader.readLine()) != null) {
                prefixNames.add(currentLine);
            }
            /* Import Suffix Names */
            InputStream is3 = getClass().getResourceAsStream(resourceLocation + "settlementSuffixNames.txt");
            assert is3 != null;
            reader = new BufferedReader(new
                    InputStreamReader(is3));
            while((currentLine = reader.readLine()) != null) {
                suffixNames.add(currentLine);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Generates a new Settlement with randomly chosen attributes.
     * @return The generated Settlement.
     */
    public Settlement generateRandomFeature() throws GenerationFailureException {
        try {
            String type = assignType();
            char size = assignSize(type);
            int population = assignPopulation(type, size);
            String name = assignName();
            Settlement currentSettlement = new Settlement();
            currentSettlement.setType(type);
            currentSettlement.setSize(size);
            currentSettlement.setPopulation(population);
            currentSettlement.setName(name);

            int maxResidents = assignMaxResidents(currentSettlement);
            if(maxResidents > 0) {
                currentSettlement.setMaxResidents(assignMaxResidents(currentSettlement));
            } else { throw new GenerationFailureException("Max Residents failed to set"); }

            return currentSettlement;
        } catch (NoMoreNamesException ex) {
            ex.printStackTrace();
            throw new GenerationFailureException
                    ("Unable to generate new Settlement as no new names available");
        }
    }
    /**
     * Generates a compatible Settlement from a given Relationship.
     * As of now, there are no differences between Relationships and the type of Settlement they Generate,
     * but this can be added to when new Relationships are implemented.
     * @param relationship The Relationship to generate from.
     * @param predicateBtoA The Predicate of the Relationship that will be assigned to the Feature.
     * @return The Generated Settlement.
     * @throws GenerationFailureException Exception for Failure to Generate Settlement.
     */
    public Settlement generateFeatureFromRelationship(Relationship relationship, Predicate predicateBtoA) throws GenerationFailureException {
        switch (predicateBtoA.getPredicateString()) {
            case "residence", "rules", "trades", "rival" -> {return generateRandomFeature();}
        }
        return null;
    }

    /**
     * Randomly assigns a type to the Settlement.
     * Currently, even split between Hamlet, Village, Town, & City.
     * @return A String of the Settlement type.
     */
    private String assignType() {
        return types.get(randNum.nextInt(types.size()));
    }

    /**
     * Assigns a size to the Settlement based on its type, randomly.
     * s = 'Small', l = 'Large', n = 'Normal' for Hamlets and Villages.
     * @param type The type of Settlement.
     * @return Character value of the size.
     */
    private char assignSize(String type) {
        int probabilitySmall;

        if (type.equals("Town")) {          // Is a Town
            probabilitySmall = 8;   //  Probability of Small Town
        } else if (type.equals("City")) {   // Is a City
            probabilitySmall = 6;   //  Probability of Small City
        } else { return 'n'; }      // Normal size

        if (randNum.nextInt(10) < probabilitySmall) {
            return 's';             // Small
        } else { return 'l'; }      // Large
    }

    /**
     * Assigns the population based on its size and type randomly
     * then rounds it to the second-largest unit.
     * @param type The type of Settlement
     * @param size The size category of the Settlement
     * @return A rounded number of the population
     */
    private int assignPopulation(String type, char size) {
        int lowerBound;
        int upperBound;
        // If it is large, bounds set on next column along, otherwise the first two
        if (size == 'l') {
            lowerBound = typeDetails.get(type)[1];
            upperBound = typeDetails.get(type)[2];
        } else {
            lowerBound = typeDetails.get(type)[0];
            upperBound = typeDetails.get(type)[1];
        }
        int population = randNum.nextInt(lowerBound,upperBound);

        // Rounds the population at the unit below the largest. e.g. '3268' is rounded to '3300'.
        long round;
        if (population < 1000) {
            round = population/10;
            population = Math.round(round) * 10;
        } else if(population < 10000) {
            round = population/100;
            population = Math.round(round) * 100;
        } else {
            round = population/1000;
            population = Math.round(round) * 1000;
        }
        return population;
    }

    /**
     * Randomly Assigns a name to the Settlement.
     * The name can either be a random pairing of a prefix and suffix
     * or a single name, which is removed from the list if picked.
     * If no new names are able to be generated after a certain amount of time, it throws an Exception.
     * @return A string of the name of the Settlement.
     */
    private String assignName() throws NoMoreNamesException {
        String name = "";
        // If the singleNames list has been emptied, it will always generate a new name.
        if(randNum.nextBoolean() || singleNames.isEmpty()) {
            // Generates name and checks if it has already been generated.
            long startTime = System.currentTimeMillis();
            while(name.isEmpty()) {
                name = prefixNames.get(randNum.nextInt(prefixNames.size())) +
                        suffixNames.get(randNum.nextInt(suffixNames.size()));
                if (usedNames.contains(name)) {
                    name = "";
                    // Has a 3-second time limit in case of an infinite loop.
                    if ((System.currentTimeMillis()-startTime)>3000) {
                        throw new NoMoreNamesException("Generating a new Settlement name has taken too long");
                    }
                }
            }
            usedNames.add(name);
        } else {
            // Removes the name from the list before returning it.
            int index = randNum.nextInt(singleNames.size());
            name = singleNames.get(index);
            singleNames.remove(index);
        }
        return name;
    }

    /**
     * Assigns the maximum residents this Settlement can hold for the Stopping Criteria.
     * @param settlement The Settlement to assign to.
     * @return The maximum value from the Static values in this class.
     */
    private int assignMaxResidents(@NotNull Settlement settlement) {
        switch (settlement.getType()) {
            case "Hamlet":
                return MAX_HAMLET_RESIDENTS;
            case "Village":
                return MAX_VILLAGE_RESIDENTS;
            case "Town":
                if(settlement.getSize() == 's') { return MAX_SMALL_TOWN_RESIDENTS; }
                else { return MAX_LARGE_TOWN_RESIDENTS; }
            case "City":
                if(settlement.getSize() == 's') { return MAX_SMALL_CITY_RESIDENTS; }
                else { return MAX_LARGE_CITY_RESIDENTS; }
            default:
                return 0;
        }
    }
}
