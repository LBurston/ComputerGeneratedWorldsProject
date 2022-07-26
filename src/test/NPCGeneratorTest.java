package test;

import features.NPC;
import generators.feature.NPCGenerator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class NPCGeneratorTest {

    @Test
    void importingResourcesShouldPopulateFields() {
        NPCGenerator testNPCGen = new NPCGenerator();
        assertNotNull(testNPCGen.getRaces());
        assertNotNull(testNPCGen.getRaceDetails());
        assertNotNull(testNPCGen.getFirstNamesFemale());
        assertNotNull(testNPCGen.getFirstNamesMale());
        assertNotNull(testNPCGen.getLastNames());
    }

    @Test
    void settingSeedShouldGenerateSameNPC() {
        NPCGenerator testNPCGen = new NPCGenerator();
        NPC[] testNPCs = new NPC[2];
        for(int i = 0; i < 2; i++) {
            testNPCGen.setSeedRandom(2);
            testNPCs[i] = testNPCGen.generateNPC();
        }
        assertEquals(testNPCs[0].getName(), testNPCs[1].getName());
        assertEquals(testNPCs[0].getRace(), testNPCs[1].getRace());
        assertEquals(testNPCs[0].getGender(), testNPCs[1].getGender());
        assertEquals(testNPCs[0].getGenderString(), testNPCs[1].getGenderString());
        assertEquals(testNPCs[0].getAge(), testNPCs[1].getAge());
        assertEquals(testNPCs[0].getAgeGroup(), testNPCs[1].getAgeGroup());
        assertEquals(testNPCs[0].getAgeGroupString(), testNPCs[1].getAgeGroupString());
    }

    @Test
    void noDuplicateNamesGeneratedIn1000() {
        NPCGenerator testNPCGenerator = new NPCGenerator();
        ArrayList<String> names = new ArrayList<>();
        HashSet<String> namesCheck = new HashSet<>();
        for(int i = 0; i < 1000; i++) {
            names.add(testNPCGenerator.generateNPC().getName());
        }
        for(String name : names) {
            assertTrue(namesCheck.add(name));
        }
    }
}