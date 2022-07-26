package features;
import relationships.Relationship;

import java.util.HashSet;

public class World {

    HashSet<NPC> npcs;
    HashSet<Settlement> settlements;
    HashSet<Relationship> relationships;

    public World() {
        npcs = new HashSet<NPC>();
        settlements = new HashSet<Settlement>();
    }

    public void addNPC(NPC npc) {
        npcs.add(npc);
    }

    public void addSettlement(Settlement settlement) {
        settlements.add(settlement);
    }

    public void addRelationship(Relationship relationship) {
        relationships.add(relationship);
    }

    /* Getters */

    public HashSet<NPC> getNpcs() {
        return npcs;
    }

    public HashSet<Settlement> getSettlements() {
        return settlements;
    }

    public HashSet<Relationship> getRelationships() {
        return relationships;
    }
}
