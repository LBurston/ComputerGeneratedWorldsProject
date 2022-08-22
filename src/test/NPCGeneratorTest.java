import com.cgw.exceptions.GenerationFailureException;
import com.cgw.features.NPC;
import com.cgw.generators.Randomiser;
import com.cgw.generators.feature.NPCGenerator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class NPCGeneratorTest {

    @Test
    void importingResourcesShouldPopulateFields() {
        NPCGenerator testNPCGen = NPCGenerator.getNPCGenerator();
        assertNotNull(testNPCGen.getRaces());
        assertNotNull(testNPCGen.getRaceDetails());
        assertNotNull(testNPCGen.getFirstNamesFemale());
        assertNotNull(testNPCGen.getFirstNamesMale());
        assertNotNull(testNPCGen.getLastNames());
    }

    @Test
    void resettingSeedShouldGenerateSameNPC() {
        NPCGenerator testNPCGen = NPCGenerator.getNPCGenerator();
        NPC[] testNPCs = new NPC[2];
        for(int i = 0; i < 2; i++) {
            Randomiser.reset();
            try {
                testNPCs[i] = testNPCGen.generateRandomFeature();
            } catch (GenerationFailureException ex) {
                ex.printStackTrace();
            }
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
        NPCGenerator testNPCGenerator = NPCGenerator.getNPCGenerator();
        ArrayList<String> names = new ArrayList<>();
        HashSet<String> namesCheck = new HashSet<>();
        for(int i = 0; i < 1000; i++) {
            try {
                names.add(testNPCGenerator.generateRandomFeature().getName());
            } catch (GenerationFailureException ex) {
                ex.printStackTrace();
            }
        }
        for(String name : names) {
            assertTrue(namesCheck.add(name));
        }
    }
}