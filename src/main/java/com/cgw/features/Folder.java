package com.cgw.features;

import com.cgw.relationships.Predicate;

import java.util.ArrayList;

public class Folder extends Feature {
    @Override
    public ArrayList<Predicate> filterSubjectPredicates(ArrayList<Predicate> predicates) {
        return null;
    }

    public Folder(String name) {
        this.setName(name);
    }

}
