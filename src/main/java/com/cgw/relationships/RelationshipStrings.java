package com.cgw.relationships;

/**
 * Enum String values of the Relationship Labels to display on the World Wiki and to sort Relationships when displayed.
 */
public enum RelationshipStrings {

    RESIDENCE("Residence: "),
    RULES("Ruler of: "),
    MOTHER("Mother: "),
    FATHER("Father: "),
    PARENT("Parent: "),
    HUSBAND("Husband: "),
    WIFE("Wife: "),
    PARTNER("Partner: "),
    SIBLING("Sibling: "),
    SISTER("Sister: "),
    BROTHER("Brother: "),
    CHILD("Child: "),
    SON("Son: "),
    DAUGHTER("Daughter: "),
    KILLER("Killed by: "),
    KILLED("Victim: "),
    TRADES("Trades with: "),
    RIVAL("Rival of: "),
    RULER("Ruler: "),
    RESIDENT("Resident: ");

    private final String text;

    /**
     * Sets the current text to the one given for Comparators.
     * @param text String value given.
     */
    RelationshipStrings(final String text) {
        this.text = text;
    }

    /**
     * Returns the String of the Relationship Text Value.
     * @return The Relationship Text Value
     */
    @Override
    public String toString() {
        return text;
    }

    /**
     * Returns the RelationshipString enum from the String for Comparators.
     * @param string
     * @return
     */
    public static RelationshipStrings getRelationshipFromString(String string) {
        return switch (string) {
            case "Residence: " -> RESIDENCE;
            case "Ruler of: " -> RULES;
            case "Mother: " -> MOTHER;
            case "Father: " -> FATHER;
            case "Parent: " -> PARENT;
            case "Husband: " -> HUSBAND;
            case "Wife: " -> WIFE;
            case "Partner: " -> PARTNER;
            case "Sibling: " -> SIBLING;
            case "Sister: " -> SISTER;
            case "Brother: " -> BROTHER;
            case "Child: " -> CHILD;
            case "Son: " -> SON;
            case "Daughter: " -> DAUGHTER;
            case "Killed by: " -> KILLER;
            case "Victim: " -> KILLED;
            case "Resident: " -> RESIDENT;
            case "Ruler: " -> RULER;
            case "Trades with: " -> TRADES;
            case "Rival of: " -> RIVAL;
            default -> null;
        };
    }

}
