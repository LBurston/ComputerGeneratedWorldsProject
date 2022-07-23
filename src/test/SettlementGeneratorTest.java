package test;

import features.Settlement;
import generators.SettlementGenerator;
import org.junit.jupiter.api.Test;
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
            testSettlements[i] = testSettlementGen.generateSettlement();
        }
        assertEquals(testSettlements[0].getName(), testSettlements[1].getName());
        assertEquals(testSettlements[0].getType(), testSettlements[1].getType());
        assertEquals(testSettlements[0].getSize(), testSettlements[1].getSize());
        assertEquals(testSettlements[0].getSizeString(), testSettlements[1].getSizeString());
        assertEquals(testSettlements[0].getPopulation(), testSettlements[1].getPopulation());
    }
}