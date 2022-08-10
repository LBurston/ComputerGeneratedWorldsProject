package features;
import org.jetbrains.annotations.NotNull;
import relationships.Relationship;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class World {

    ArrayList<Feature> features;
    ArrayList<Relationship> relationships;
    ArrayList<Relationship> unfinishedRelationships;

    public World() {
        features = new ArrayList<>();
        relationships = new ArrayList<>();
        unfinishedRelationships = new ArrayList<>();
    }

    public void saveFeatures(@NotNull ArrayList<Feature> newFeatures) {
        for(Feature newFeature: newFeatures) {
            addFeature(newFeature);
        }
    }

    public void saveRelationship(Relationship relationship) {
        addRelationship(relationship);
        relationship.storeRelationshipInFeatures();
    }

    public void saveUnfinishedRelationships(@NotNull ArrayList<Relationship> relationships) {
        for(Relationship relationship : relationships) {
            addUnfinishedRelationship(relationship);
        }
    }

    public void addFeature(Feature feature) {
        features.add(feature);
    }

    public void addRelationship(Relationship relationship) {
        relationships.add(relationship);
    }

    public void addUnfinishedRelationship(Relationship relationship) {
        unfinishedRelationships.add(relationship);
    }

    /* Getters */

    public ArrayList<Feature> getFeatures() {
        return features;
    }

    public ArrayList<? extends Feature> getAllOfSpecificFeature(Class<? extends Feature> specificClass) {
        return features.stream()
                .filter(feature -> feature.getClass() == specificClass)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    //    public ArrayList<NPC> getNpcs() {
//        return npcs;
//    }
//
//    public ArrayList<Settlement> getSettlements() {
//        return settlements;
//    }

    public ArrayList<Relationship> getRelationships() {
        return relationships;
    }
}
