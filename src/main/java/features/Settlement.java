package features;

import generators.FeatureManager;
import org.apache.commons.lang3.tuple.Triple;
import relationships.Predicate;
import relationships.Relationship;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public class Settlement extends Feature {

    private String type;
    private char size;
    private int population;
    private int maxResidents;

    public Settlement() {
        relationships = new ArrayList<>();
    }

    public ArrayList<Predicate> filterSubjectPredicates(ArrayList<Predicate> predicates) {
        return FeatureManager.getFeatureManager().filterSettlementSubjectPredicates(this, predicates);
    }

    public String toString() {
        if(size == 'n') {
            return name + " is a " + getType() + ", with a population of " + getPopulation() + ".";
        }
        return name + " is a " + getSizeString() + " " + getType() + ", with a population of " + getPopulation() + ".";
    }

    /* Relationship Getters */

    public ArrayList<Settlement> getTradingSettlements() {
        return relationships.stream()
                .filter(relationship -> relationship.getLeft().equals("trades"))
                .map(relationship -> (Settlement) relationship.getMiddle())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Settlement> getRivalSettlements() {
        return relationships.stream()
                .filter(relationship -> relationship.getLeft().equals("rival"))
                .map(relationship -> (Settlement) relationship.getMiddle())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /* Checkers */

    public boolean hasRuler() {
        return relationships.stream()
                .anyMatch(relationship -> relationship.getLeft().matches("ruler"));
    }

    public int numberOfResidents() {
        long number = relationships.stream()
                .filter(relationship -> relationship.getLeft().equals("resident")).count();
        return Math.toIntExact(number);
    }

    public int numberOfTradingSettlements() {
        long number = relationships.stream()
                .filter(relationship -> relationship.getLeft().equals("trades")).count();
        return Math.toIntExact(number);
    }

    public int numberOfRivalSettlements() {
        long number = relationships.stream()
                .filter(relationship -> relationship.getLeft().equals("rival")).count();
        return Math.toIntExact(number);
    }

    public String getTradeOrRival(Settlement settlement) {
        Triple<String, Feature, Relationship> settlementRelationship = relationships.stream()
                .filter(relationship -> relationship.getMiddle() == settlement)
                .filter(relationship -> relationship.getLeft().matches("trades|rival"))
                .findFirst().orElse(null);
        if(Objects.nonNull(settlementRelationship)) {
            return settlementRelationship.getLeft();
        } else { return null; }
    }

    /* Getters and Setters */

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public char getSize() {
        return size;
    }

    public String getSizeString() {
        return switch (size) {
            case 's' -> "small";
            case 'l' -> "large";
            case 'n' -> "";
            default -> null;
        };
    }

    public void setSize(char size) {
        this.size = size;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public int getMaxResidents() {
        return maxResidents;
    }

    public void setMaxResidents(int maxResidents) {
        this.maxResidents = maxResidents;
    }
}
