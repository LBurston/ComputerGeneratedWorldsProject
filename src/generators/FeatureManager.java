package generators;

import exceptions.GenerationFailureException;
import features.*;
import generators.feature.NPCGenerator;
import generators.feature.SettlementGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeatureManager {

    private World world;
    private final NPCGenerator npcGenerator;
    private final SettlementGenerator settlementGenerator;

    public static void main(String[] args) {
        testSettlementGeneration();
    }

    /**
     * This constructor is to be used in the full generator
     *
     * @param world The current world that is being generated
     */
    public FeatureManager(World world) {
        // This gives the Feature Generator access to the current World
        this.world = world;
        npcGenerator = new NPCGenerator();
        settlementGenerator = new SettlementGenerator();
    }

    /**
     * This constructor is to be used for development and testing
     * separate from the whole generator
     */
    public FeatureManager() {
        npcGenerator = new NPCGenerator();
        settlementGenerator = new SettlementGenerator();
    }

    public void generateNPCToWorld() {
        try {
            world.addNPC(npcGenerator.generateNPC());
        } catch (GenerationFailureException ex) {
            ex.printStackTrace();
        }
    }

    public void generateSettlementToWorld() {
        try {
            world.addSettlement(settlementGenerator.generateSettlement());
        } catch (GenerationFailureException ex) {
            ex.printStackTrace();
        }
    }

    /* Testing */
    /**
     * A method to generate a specified amount of Settlements to test the generation.
     */
    public static void testSettlementGeneration() {
        FeatureManager fm = new FeatureManager();
        List<Settlement> settlementList = new ArrayList<>();
        int amount = 100;
        for (int i = 0; i < amount; i++) {
            try {
                Settlement testSettlement = fm.settlementGenerator.generateSettlement();
                settlementList.add(testSettlement);
            } catch (GenerationFailureException ex) {
                ex.printStackTrace();
            }
        }
        System.out.printf("%20s %5s %7s %10s", "NAME", "SIZE", "TYPE", "POPULATION");
        System.out.println("\n-------------------------------------------------");
        for(Settlement settlement : settlementList) {
            System.out.printf("%20s %5s %7s %10d",
                    settlement.getName(), settlement.getSizeString(), settlement.getType(), settlement.getPopulation());
            System.out.println();
        }
        System.out.println(settlementList.get(1).getClass());
    }

    /**
     * A method to generate a specified amount of NPCs to test the generation.
     */
    public static void testNPCGeneration() {
        FeatureManager fm = new FeatureManager();
        List<NPC> npcList = new ArrayList<>();
        int amount = 500;
        for (int i = 0; i < amount; i++) {

            try {
                NPC testNPC = fm.npcGenerator.generateNPC();
                npcList.add(testNPC);
            } catch (GenerationFailureException ex) {
                ex.printStackTrace();
            }
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



}
