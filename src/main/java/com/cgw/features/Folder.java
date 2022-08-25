package com.cgw.features;

import com.cgw.relationships.Predicate;

import java.util.ArrayList;

/**
 * The Folder Class is exclusively used in the JavaFX TreeView in order to add a directory for
 * all Feature Subtypes, that has no actual link to a Feature within the World. e.g. 'NPCs' or 'Settlements'
 * @author Luke Burston
 * @author lb800@kent.ac.uk
 * @version 0.1
 * @since 0.1
 */
public class Folder extends Feature {

    /**
     * Returns Null as Folder is not a Feature of the World, but extends Feature for JavaFX TreeView functionality.
     * @param predicates The list of Predicates to filter.
     * @return Null.
     */
    @Override
    public ArrayList<Predicate> filterSubjectPredicates(ArrayList<Predicate> predicates) {
        return null;
    }

    /**
     * Constructor of a Folder object, setting its name.
     * @param name The text used for the Folder within the TreeView.
     */
    public Folder(String name) {
        this.setName(name);
    }

}
