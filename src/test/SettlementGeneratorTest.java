package test;

import exceptions.GenerationFailureException;
import features.Settlement;
import generators.feature.SettlementGenerator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class SettlementGeneratorTest {

    @Test
    void importingResourcesShouldPopulateFields() {
       SettlementGenerator testSettlementGen = new SettlementGenerator();
       assertNotNull(testSettlementGen.getTypes());
       assertNotNull(testSettlementGen.getTypeDetails());
       assertNotNull(testSettlementGen.getSingleNames());
       assertNotNull(testSettlementGen.getPrefixNames());
       assertNotNull(testSettlementGen.getSuffixNames());
   }

    @Test
    void settingSeedShouldGenerateSameSettlement() {
        SettlementGenerator testSettlementGen = new SettlementGenerator();
        Settlement[] testSettlements = new Settlement[2];
        for(int i = 0; i < 2; i++) {
            testSettlementGen.setSeedRandom(2);
            try {
                testSettlements[i] = testSettlementGen.generateSettlement();
            } catch (GenerationFailureException ex) {
                ex.printStackTrace();
            }
        }
        assertEquals(testSettlements[0].getName(), testSettlements[1].getName());
        assertEquals(testSettlements[0].getType(), testSettlements[1].getType());
        assertEquals(testSettlements[0].getSize(), testSettlements[1].getSize());
        assertEquals(testSettlements[0].getSizeString(), testSettlements[1].getSizeString());
        assertEquals(testSettlements[0].getPopulation(), testSettlements[1].getPopulation());
    }

    @Test
    void noDuplicateNamesGeneratedIn1000() {
        SettlementGenerator testSettlementGenerator = new SettlementGenerator();
        ArrayList<String> names = new ArrayList<>();
        HashSet<String> namesCheck = new HashSet<>();
        for(int i = 0; i < 1000; i++) {
            try {
                names.add(testSettlementGenerator.generateSettlement().getName());
            } catch (GenerationFailureException ex) {
                ex.printStackTrace();
            }
        }
        for(String name : names) {
            assertTrue(namesCheck.add(name));
        }
    }
}