package features;
import org.jetbrains.annotations.NotNull;
import relationships.Relationship;

import java.util.ArrayList;
import java.util.Iterator;
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
            saveFeature(newFeature);
        }
    }

    public void saveRelationship(Relationship relationship) {
        addRelationship(relationship);
        relationship.storeRelationshipInFeatures();
    }

    public void saveRelationships(ArrayList<Relationship> relationships) {
        for(Relationship relationship : relationships) {
            saveRelationship(relationship);
        }
    }


    public void saveUnfinishedRelationships(@NotNull ArrayList<Relationship> relationships) {
        for(Relationship relationship : relationships) {
            addUnfinishedRelationship(relationship);
        }
    }

    public void saveFeature(Feature feature) {
        features.add(feature);
    }

    public void addRelationship(Relationship relationship) {
        relationships.add(relationship);
    }

    public void addUnfinishedRelationship(Relationship relationship) {
        unfinishedRelationships.add(relationship);
    }

    public void clearCompletedRelationshipsFromUnfinished() {
        unfinishedRelationships.removeIf(Relationship::isCompleted);
    }

    public void removeUnfinishedRelationship(Relationship relationship) {
        unfinishedRelationships.remove(relationship);
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

    public ArrayList<Relationship> getRelationships() {
        return relationships;
    }

    public ArrayList<Relationship> getUnfinishedRelationships() { return unfinishedRelationships; }
}
