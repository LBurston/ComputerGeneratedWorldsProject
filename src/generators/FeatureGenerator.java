package generators;

import features.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.nio.file.*;

public class FeatureGenerator {

    World world;
    HashMap<String, Integer> npcResources;

    public static void main(String[] args) {
        FeatureGenerator fg = new FeatureGenerator();
        for (int i = 0; i < 100; i++) {
            NPC testNPC = fg.generateNPC();
            char genderChar = testNPC.getGender();
            String gender = "";
            if (genderChar == 'm') {
                gender = "male";
            } else if (genderChar == 'f') {
                gender = "female";
            } else {
                gender = "non-binary";
            }
            System.out.println(testNPC.getName() + " is a " + gender + " " + testNPC.getRace());
        }

    }

    /**
     * This constructor is to be used in the full generator
     *
     * @param world The current world that is being generated
     */
    public FeatureGenerator(World world) {
        // This gives the Feature Generator access to the current World
        this.world = world;
        npcResources = gatherNPCResources();
    }

    /**
     * This constructor is to be used for development and testing
     * separate from the whole generator
     */
    public FeatureGenerator() {
        npcResources = gatherNPCResources();
    }

    /**
     * Recursive method through a root directory and places any .txt file's name and number
     * of lines into a HashMap to be used for the Random Number Generators
     *
     * @param directory The directory in which to look for the files
     * @return Returns a Hashmap of the file names (excluding the extension)
     * as a key and the number of lines in the file as the value
     */
    private HashMap<String, Integer> searchDirectory(String directory) {
        HashMap<String, Integer> resources = new HashMap<>();
        // try...catch for the given root directory gives a list of all files within
        try (DirectoryStream<Path> files = Files.newDirectoryStream(Paths.get(directory))) {
            for(Path path : files) {
                File file = new File(path.toString());
                String name = file.getName();
                // if the file is a txt file, adds it to HashMap, else if it's a directory, recurses.
                if (name.endsWith(".txt")) {
                    resources.put(name.substring(0, name.length() - 4), countResourceLines(path));
                } else if (file.isDirectory()) {
                    resources.putAll(searchDirectory(file.toString()));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return resources;
    }

    /**
     * Counts all the lines of the given file path
     *
     * @param path The path to the file
     * @return Number of lines in file.
     */
    private Integer countResourceLines(Path path) {
        long lines = 0;
        try {
            lines = Files.lines(path).count();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Math.toIntExact(lines);
    }

    /**
     * Used to place the file directory for NPC resources
     *
     * @return HashMap of all .txt files paired with number of lines
     */
    private HashMap<String, Integer> gatherNPCResources() {
        return searchDirectory("src/resources/npc/used");
    }

    /**
     * This method creates a new NPC object, randomly assigning its attributes, and returns it
     *
     * @return The generated NPC
     */
    public NPC generateNPC() {
        return new NPC(npcResources);
    }

}
