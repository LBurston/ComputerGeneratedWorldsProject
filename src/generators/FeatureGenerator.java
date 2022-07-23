package generators;

import features.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.nio.file.*;
import java.util.List;
import java.util.Map;

public class FeatureGenerator {

    private World world;
    private final NPCGenerator npcGenerator;
    private final SettlementGenerator settlementGenerator;

    public static void main(String[] args) {
        testSettlementGeneration();
    }

    public static void testSettlementGeneration() {
        FeatureGenerator fg = new FeatureGenerator();
        List<Settlement> settlementList = new ArrayList<>();
        int amount = 100;
        for (int i = 0; i < amount; i++) {
            Settlement testSettlement = fg.settlementGenerator.generateSettlement();
            settlementList.add(testSettlement);
        }
        System.out.printf("%20s %5s %7s %10s", "NAME", "SIZE", "TYPE", "POPULATION");
        System.out.println("\n-------------------------------------------------");
        for(Settlement settlement : settlementList) {
            System.out.printf("%20s %5s %7s %10d",
                    settlement.getName(), settlement.getSizeString(), settlement.getType(), settlement.getPopulation());
            System.out.println();
        }
    }

    public static void testNPCGeneration() {
        FeatureGenerator fg = new FeatureGenerator();
        List<NPC> npcList = new ArrayList<>();
        int amount = 500;
        for (int i = 0; i < amount; i++) {
            NPC testNPC = fg.npcGenerator.generateNPC();
            npcList.add(testNPC);
        }
        System.out.printf("%20s %5s %10s %10s %10s", "NAME", "AGE", "AGE GROUP", "GENDER", "RACE");
        System.out.println("\n----------------------------------------------------------------");

        HashMap<String, Integer> ageGroupCount = new HashMap<>();
        HashMap<String, Integer> raceCount = new HashMap<>();
        HashMap<String, Integer> genderCount = new HashMap<>();

        for(NPC npc : npcList) {
            System.out.format("%20s %5d %10s %10s %10s",
                    npc.getName(), npc.getAge(), npc.getAgeGroupString(), npc.getGenderString(), npc.getRace());
            System.out.println();
            ageGroupCount.merge(npc.getAgeGroupString(), 1, Integer::sum);
            raceCount.merge(npc.getRace(), 1, Integer::sum);
            genderCount.merge(npc.getGenderString(), 1, Integer::sum);
        }
        System.out.println();
        int total = 0;

        for(Map.Entry<String, Integer> entry: ageGroupCount.entrySet()) {
            double percent = (entry.getValue()*100.0f / amount);
            System.out.format("%20s %5f", entry, percent);
            System.out.println();
            total += entry.getValue();
        }
        System.out.println("Total: " + total + "\n");
        total = 0;

        for(Map.Entry<String, Integer> entry: raceCount.entrySet()) {
            double percent = entry.getValue()*100.0f / amount;
            System.out.format("%20s %5f", entry, percent);
            System.out.println();
            total += entry.getValue();
        }
        System.out.println("Total: " + total + "\n");
        total = 0;


        for(Map.Entry<String, Integer> entry: genderCount.entrySet()) {
            double percent = entry.getValue()*100.0f / amount;
            System.out.format("%20s %5f", entry, percent);
            System.out.println();
            total += entry.getValue();
        }
        System.out.println("Total: " + total + "\n");
    }

    /**
     * This constructor is to be used in the full generator
     *
     * @param world The current world that is being generated
     */
    public FeatureGenerator(World world) {
        // This gives the Feature Generator access to the current World
        this.world = world;
        npcGenerator = new NPCGenerator();
        settlementGenerator = new SettlementGenerator();
    }

    /**
     * This constructor is to be used for development and testing
     * separate from the whole generator
     */
    public FeatureGenerator() {
        npcGenerator = new NPCGenerator();
        settlementGenerator = new SettlementGenerator();
    }

//    /**
//     * Recursive method through a root directory and places any .txt file's name and number
//     * of lines into a HashMap to be used for the Random Number Generators
//     *
//     * @param directory The directory in which to look for the files
//     * @return Returns a Hashmap of the file names (excluding the extension)
//     * as a key and the number of lines in the file as the value
//     */
//    private ArrayList<File> searchDirectory(String directory) {
//        ArrayList<File> resources = new ArrayList<>();
//        // try...catch for the given root directory gives a list of all files within
//        try (DirectoryStream<Path> files = Files.newDirectoryStream(Paths.get(directory))) {
//            for(Path path : files) {
//                File file = new File(path.toString());
//                String name = file.getName();
//                // if the file is a txt file, adds it to HashMap, else if it's a directory, recurses.
//                if (name.endsWith(".txt")) {
//                    resources.add(file);
//                } else if (file.isDirectory()) {
//                    resources.addAll(searchDirectory(file.toString()));
//                }
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return resources;
//    }
//
//    /**
//     * Counts all the lines of the given file path
//     *
//     * @param path The path to the file
//     * @return Number of lines in file.
//     */
//    private Integer countResourceLines(Path path) {
//        long lines = 0;
//        try {
//            lines = Files.lines(path).count();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return Math.toIntExact(lines);
//    }
//
//    /**
//     * Used to place the file directory for NPC resources
//     *
//     * @return HashMap of all .txt files paired with number of lines
//     */
//    private ArrayList<File> gatherNPCResources() {
//        return searchDirectory("src/resources/npc/used");
//    }

}
