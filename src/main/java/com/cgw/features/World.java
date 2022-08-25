package com.cgw.features;

import com.cgw.relationships.Predicate;
import org.jetbrains.annotations.NotNull;
import com.cgw.relationships.Relationship;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * The World Model Class, which extends Features for use in the JavaFX TreeView.
 * Contains the World's name, all Features within it,
 * all fully completed Relationships, and those waiting to assign a second Feature.
 */
public class World extends Feature{

    private final ArrayList<Feature> features;
    private final ArrayList<Relationship> relationships;
    private final ArrayList<Relationship> unfinishedRelationships;

    /**
     * Constructor of the World, creating all the ArrayLists.
     */
    public World() {
        features = new ArrayList<>();
        relationships = new ArrayList<>();
        unfinishedRelationships = new ArrayList<>();
    }

    /**
     * Returns Null as World is not a Feature of the World, but extends Feature for JavaFX TreeView functionality.
     * @param predicates The list of Predicates to filter.
     * @return Null.
     */
    @Override
    public ArrayList<Predicate> filterSubjectPredicates(ArrayList<Predicate> predicates) {
        return null;
    }

    /**
     * Passes each Feature to the method to add to World Features.
     * @param newFeatures ArrayList of the new Features to be added to the World.
     */
    public void saveFeatures(@NotNull ArrayList<Feature> newFeatures) {
        for(Feature newFeature: newFeatures) {
            saveFeature(newFeature);
        }
    }

    /**
     * Adds individual Feature to the World Features, if not already there.
     * @param feature Feature to be added to the World.
     */
    public void saveFeature(Feature feature) {
        if(!features.contains(feature)) {
            features.add(feature);
        }
    }

    /**
     * Passes Relationship to the add Relationship method and calls its method to
     * store the Relationship in both Features as a Triple Data Structure.
     * @param relationship The Relationship to be saved.
     */
    public void saveRelationship(Relationship relationship) {
        addRelationship(relationship);
        relationship.storeRelationshipInFeatures();
    }

    /**
     * Adds individual Relationship to the World Features , if not already there.
     * @param relationship The Relationship to be added to the World.
     */
    public void addRelationship(Relationship relationship) {
        if (!relationships.contains(relationship)) {
            relationships.add(relationship);
        }
    }

    /**
     * Passes each unfinished Relationship to the add unfinished Relationship method.
     * This is when no suitable Features were found and the generator chose not to create a new feature.
     * @param relationships ArrayList of unfinished Relationships to be added to the World.
     */
    public void saveUnfinishedRelationships(@NotNull ArrayList<Relationship> relationships) {
        for(Relationship relationship : relationships) {
            addUnfinishedRelationship(relationship);
        }
    }

    /**
     * Adds individual unfinished Relationship to the World, if not already there.
     * @param relationship The unfinished Relationship to be added.
     */
    public void addUnfinishedRelationship(Relationship relationship) {
        if (!unfinishedRelationships.contains(relationship)) {
            unfinishedRelationships.add(relationship);
        }
    }

    /**
     * Removes unfinished Relationship from the World if its internal iteration timer runs out.
     * @param relationship Unfinished Relationship to be removed.
     */
    public void removeUnfinishedRelationship(Relationship relationship) {
        unfinishedRelationships.remove(relationship);
    }

    /**
     * Goes through all Relationships in unfinished Relationships, to check if any have been completed,
     * and removes those that have been.
     */
    public void clearCompletedRelationshipsFromUnfinished() {
        unfinishedRelationships.removeIf(Relationship::isCompleted);
    }

    /* Getters */

    /**
     * Returns all Features of the World.
     * @return ArrayList of Features of the World.
     */
    public ArrayList<Feature> getFeatures() {
        return features;
    }

    /**
     * Returns all Features of the World of the given subclass. Usually done through passing a generic Features class.
     * @param specificClass The subclass to filter by.
     * @return ArrayList of all Features of the World of the given subclass.
     */
    public ArrayList<? extends Feature> getAllOfSpecificFeature(Class<? extends Feature> specificClass) {
        return features.stream()
                .filter(feature -> feature.getClass() == specificClass)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Specifically returns all NPCs within this World's Features.
     * @return ArrayList of all the NPCs of the World.
     */
    public ArrayList<NPC> getAllNPCs() {
        return features.stream()
                .filter(feature -> feature.getSubClass() == NPC.class)
                .map(npc -> (NPC) npc)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Specifically returns all Settlements within this World's Features.
     * @return ArrayList of all the Settlements of the world.
     */
    public ArrayList<Settlement> getAllSettlements() {
        return features.stream()
                .filter(feature -> feature.getSubClass() == Settlement.class)
                .map(settlement -> (Settlement) settlement)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns all completed Relationships of the World.
     * @return ArrayList of all completed Relationships.
     */
    public ArrayList<Relationship> getRelationships() {
        return relationships;
    }

    /**
     * Returns all unfinished Relationships of the World.
     * @return ArrayList of all unfinished Relationships.
     */
    public ArrayList<Relationship> getUnfinishedRelationships() { return unfinishedRelationships; }
}
