package com.cgw.features;

import com.cgw.generators.FeatureManager;
import org.apache.commons.lang3.tuple.Triple;
import com.cgw.relationships.Predicate;
import com.cgw.relationships.Relationship;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The Settlement Feature of the World. These are populated places of Residence.
 * @author Luke Burston
 * @author lb800@kent.ac.uk
 * @version 0.1
 * @since 0.1
 */
public class Settlement extends Feature {

    // Attributes of the Settlement.
    private String type;
    private char size;
    private int population;
    private int maxResidents;

    /**
     * Constructor of the Settlement, creating an ArrayList for their Relationships.
     * Attributes are set after initialization as some attributes may require other attributes to set.
     */
    public Settlement() {
        relationships = new ArrayList<>();
    }

    /**
     * Predicates from the Relationship Generator to be filtered by the Feature Manager for this particular Feature.
     * For Example, if this Settlement already has a Ruler, it can't be given another Ruler.
     * @param predicates The list of Predicates to filter.
     * @return The ArrayList of Predicates that can be assigned to this Settlement.
     */
    public ArrayList<Predicate> filterSubjectPredicates(ArrayList<Predicate> predicates) {
        return FeatureManager.getFeatureManager().filterSettlementSubjectPredicates(this, predicates);
    }

    /* Relationship Getters */

    /**
     * Returns all Settlements that this Settlement has a Trading Relationship with.
     * @return An ArrayList of the Settlements that this Settlement has a Trading Relationship with.
     */
    public ArrayList<Settlement> getTradingSettlements() {
        return relationships.stream()
                .filter(relationship -> relationship.getLeft().equals("trades"))
                .map(relationship -> (Settlement) relationship.getMiddle())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns all Settlements that this Settlement has a Rival Relationship with.
     * @return An ArrayList of the Settlements that this Settlement has a Rival Relationship with.
     */
    public ArrayList<Settlement> getRivalSettlements() {
        return relationships.stream()
                .filter(relationship -> relationship.getLeft().equals("rival"))
                .map(relationship -> (Settlement) relationship.getMiddle())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /* Relationship Checkers */

    /**
     * Checks whether this Settlement has an NPC Ruler.
     * @return Boolean of whether this Settlement has an NPC Ruler.
     */
    public boolean hasRuler() {
        return relationships.stream()
                .anyMatch(relationship -> relationship.getLeft().matches("ruler"));
    }

    /**
     * Returns the amount of NPC residents in this Settlement.
     * @return The amount of NPC residents in this Settlement.
     */
    public int numberOfResidents() {
        long number = relationships.stream()
                .filter(relationship -> relationship.getLeft().equals("resident")).count();
        return Math.toIntExact(number);
    }

    /**
     * Returns the amount of Settlements of which this Settlement Trades with.
     * @return The amount of Settlements of which this Settlement Trades with.
     */
    public int numberOfTradingSettlements() {
        long number = relationships.stream()
                .filter(relationship -> relationship.getLeft().equals("trades")).count();
        return Math.toIntExact(number);
    }

    /**
     * Returns the amount of Settlements of which this Settlement is a Rival of.
     * @return The amount of Settlements of which this Settlement is a Rival of.
     */
    public int numberOfRivalSettlements() {
        long number = relationships.stream()
                .filter(relationship -> relationship.getLeft().equals("rival")).count();
        return Math.toIntExact(number);
    }

    /**
     * Returns whether the given Settlement Trades with or is a Rival of this Settlement. Null if neither.
     * @param settlement The Settlement to check against
     * @return String value of their Relationship, or Null.
     */
    public String getTradeOrRival(Settlement settlement) {
        Triple<String, Feature, Relationship> settlementRelationship = relationships.stream()
                .filter(relationship -> relationship.getMiddle() == settlement)
                .filter(relationship -> relationship.getLeft().matches("trades|rival"))
                .findFirst().orElse(null);
        if(Objects.nonNull(settlementRelationship)) {
            return settlementRelationship.getLeft();
        } else { return null; }
    }

    /**
     * Checks whether this Settlement has reached the maximum number of NPCs as Residents within it.
     * @return Boolean of whether this Settlement has reached the maximum number of Residents.
     */
    public boolean hasReachedMaxResidents() {
        return relationships.stream()
                .filter(relationship -> relationship.getLeft().equals("resident")).count() >= maxResidents;
    }

    /**
     * Checks whether this Settlement is the Residence for the given NPC.
     * @param npc The NPC to check against.
     * @return Boolean of whether this Settlement is the Residence for the given NPC.
     */
    public boolean isResidenceOf(NPC npc) {
        return relationships.stream()
                .filter(relationship -> relationship.getMiddle() == npc)
                .anyMatch(relationship -> relationship.getLeft().equals("resident"));
    }

    /* Getters and Setters */

    /**
     * Returns a String of this Settlement's type.
     * @return  A String that represents this Settlement's type.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of this Settlement.
     * @param type A String reference of the type to be set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the character representation of the Settlement's size.
     * @return A char representing the Settlement's size: s = "small", l = "Large", n = "Normal".
     */
    public char getSize() {
        return size;
    }

    /**
     * Returns a String representation of the Settlement's size, for users.
     * @return A String Representing the Settlement's size: s = "small", l = "Large", n = "Normal".
     */
    public String getSizeString() {
        // Converts their size into a String.
        return switch (size) {
            case 's' -> "Small";
            case 'l' -> "Large";
            case 'n' -> "Normal";
            default -> null;
        };
    }

    /**
     * Sets the size of this Settlement.
     * @param size The char representing the Settlement's size: s = "small", l = "Large", n = "Normal".
     */
    public void setSize(char size) {
        this.size = size;
    }

    /**
     * Returns the population of this Settlement as an integer.
     * @return An int that represents the population.
     */
    public int getPopulation() {
        return population;
    }

    /**
     * Sets the population of this Settlement.
     * @param population An int value of the population to be set.
     */
    public void setPopulation(int population) {
        this.population = population;
    }

    /**
     * Returns the maximum amount of NPC Residents for this Settlement, based on type and size.
     * @return An int of the maximum amount of Residents for this Settlement.
     */
    public int getMaxResidents() {
        return maxResidents;
    }

    /**
     * Sets the maximum amount of the NPC Residents for this Settlement, based on type and size.
     * @param maxResidents An int value of the maximum Residents to be set.
     */
    public void setMaxResidents(int maxResidents) {
        this.maxResidents = maxResidents;
    }
}
