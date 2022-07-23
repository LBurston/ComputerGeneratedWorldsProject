package generators;

import features.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class SettlementGenerator {

    private final Random randNum;

    private final HashMap<String, int[]> typeDetails;
    private final ArrayList<String> types;
    private final ArrayList<String> singleNames;
    private final ArrayList<String> prefixNames;
    private final ArrayList<String> suffixNames;

    public SettlementGenerator() {
        randNum = new Random();

        typeDetails = new HashMap<>();
        types = new ArrayList<>();

        singleNames = new ArrayList<>();
        prefixNames = new ArrayList<>();
        suffixNames = new ArrayList<>();

        importResources();
    }

    public void setSeedRandom(long seed) {
        randNum.setSeed(seed);
    }

    private void importResources() {
        String resourceLocation = "src/resources/settlement/";
        BufferedReader reader;
        String currentLine;

        /* Import types */
        try {
            reader = new BufferedReader(new
                    FileReader(resourceLocation + "settlementTypes.txt"));
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
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        /* Import Names */
        try {
            /* Import Single Names */
            reader = new BufferedReader(new
                    FileReader(resourceLocation + "settlementSingleNames.txt"));
            while((currentLine = reader.readLine()) != null) {
                singleNames.add(currentLine);
            }
            /* Import Prefix Names */
            reader = new BufferedReader(new
                    FileReader(resourceLocation + "settlementPrefixNames.txt"));
            while((currentLine = reader.readLine()) != null) {
                prefixNames.add(currentLine);
            }
            /* Import Suffix Names */
            reader = new BufferedReader(new
                    FileReader(resourceLocation + "settlementSuffixNames.txt"));
            while((currentLine = reader.readLine()) != null) {
                suffixNames.add(currentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public Settlement generateSettlement() {
        String type = assignType();
        char size = assignSize(type);
        int population = assignPopulation(type, size);
        String name = assignName();

        Settlement currentSettlement = new Settlement();
        currentSettlement.setType(type);
        currentSettlement.setSize(size);
        currentSettlement.setPopulation(population);
        currentSettlement.setName(name);

        return currentSettlement;
    }

    private String assignType() {
        return types.get(randNum.nextInt(types.size()));
    }

    private char assignSize(String type) {
        int probabilitySmall;

        if (type.equals("Town")) {          // Is a Town
            probabilitySmall = 8;   //  Probability of Small Town
        } else if (type.equals("City")) {   // Is a City
            probabilitySmall = 6;   //  Probability of Small City
        } else { return 'n'; }      // No size

        if (randNum.nextInt(10) < probabilitySmall) {
            return 's';             // Small
        } else { return 'l'; }      // Large
    }

    private int assignPopulation(String type, char size) {
        int lowerBound;
        int upperBound;
        if (size == 'l') {
            lowerBound = typeDetails.get(type)[1];
            upperBound = typeDetails.get(type)[2];
        } else {
            lowerBound = typeDetails.get(type)[0];
            upperBound = typeDetails.get(type)[1];
        }
        return randNum.nextInt(lowerBound, upperBound);
    }

    private String assignName() {
        if(randNum.nextBoolean()) {
            // Removes the name from the list before returning it.
            int index = randNum.nextInt(singleNames.size());
            String name = singleNames.get(index);
            singleNames.remove(index);
            return name;
        } else {
            return prefixNames.get(randNum.nextInt(prefixNames.size())) +
                    suffixNames.get(randNum.nextInt(suffixNames.size()));
        }
    }


    /* Getters */
    public HashMap<String, int[]> getTypeDetails() {
        return typeDetails;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public ArrayList<String> getSingleNames() {
        return singleNames;
    }

    public ArrayList<String> getPrefixNames() {
        return prefixNames;
    }

    public ArrayList<String> getSuffixNames() {
        return suffixNames;
    }
}
