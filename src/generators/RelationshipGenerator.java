package generators;

import exceptions.GenerationFailureException;
import features.*;
import relationships.Predicate;
import relationships.Relationship;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RelationshipGenerator {

    private World world;
    private FeatureManager featMan;
    private Random randNum;
    private final ArrayList<Predicate> predicates;

    public static void main(String[] args) {
        // For testing
        FeatureManager fm = new FeatureManager();
        RelationshipGenerator rg = new RelationshipGenerator(fm);
        System.out.println(rg.predicates);
    }

    public RelationshipGenerator(World world, FeatureManager featMan) {
        this.world = world;
        this.featMan = featMan;
        randNum = new Random();
        predicates = new ArrayList<>();
        importPredicates();
    }

    public RelationshipGenerator(FeatureManager featMan) {
        this.featMan = featMan;
        randNum = new Random();
        predicates = new ArrayList<>();
        importPredicates();
    }

    public Relationship generateNewRelationship() {
        try {
            Predicate predicate = predicates.get(randNum.nextInt(predicates.size()));
            ArrayList<? extends Feature> possibleSubjects = new ArrayList<>(filterSubjects(predicate));
            Feature subject;

            if (!possibleSubjects.isEmpty()) {
                subject = possibleSubjects.get(randNum.nextInt(possibleSubjects.size()));
            } else {
                throw new GenerationFailureException("No available subjects");
            }

            ArrayList<? extends Feature> possibleObjects = new ArrayList<>(filterObjects(subject, predicate));
            Feature object;

            if (!possibleObjects.isEmpty()) {
                object = possibleObjects.get(randNum.nextInt(possibleObjects.size()));
            } else {
                throw new GenerationFailureException("No available objects");
            }

            Relationship relationship = new Relationship(subject, predicate, object);
            // You were going to add here the Relationships to the subject and object before returning
            // but you need to sort out Feature superclass methods for that.
            // Then you were going to add it to the World Relationships or something and make it bidirectional.
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ArrayList<? extends Feature> filterSubjects(Predicate predicate) {
        switch (predicate.getPredicate()) {
            case "is married to":
                return world.getNpcs().stream()
                        .filter(npc -> npc.getAgeGroup() == 'a' || npc.getAgeGroup() == 'e')
                        .filter(npc -> {
                            List<Predicate> npcPredicates = npc.getSubjectRelationshipsPredicates().stream()
                                    .filter(npcPredicate -> npcPredicate.getPredicate().equals("is married to"))
                                    .toList();
                            return npcPredicates.isEmpty();
                        })
                        .collect(Collectors.toCollection(ArrayList::new));
            default:
                return null;
        }
    }

    private ArrayList<? extends Feature> filterObjects(Feature subject, Predicate predicate) {
        switch (predicate.getPredicate()) {
            case "is married to":
                return world.getNpcs().stream()
                        .filter(npc -> !(npc == subject))
                        .filter(npc -> npc.getAgeGroup() == 'a' || npc.getAgeGroup() == 'e')
                        .filter(npc -> {
                            List<Predicate> npcPredicates = npc.getSubjectRelationshipsPredicates().stream()
                                    .filter(npcPredicate -> npcPredicate.getPredicate().equals("is married to"))
                                    .toList();
                            return npcPredicates.isEmpty();
                        })
                        .collect(Collectors.toCollection(ArrayList::new));
            default:
                return null;
        }
    }

    private void importPredicates() {
        String fileLocation = "src/resources/predicates/predicates.txt";
        BufferedReader reader;
        String currentLine;

        try {
            reader = new BufferedReader(new FileReader(fileLocation));
            while((currentLine = reader.readLine()) != null) {
                String[] details = currentLine.split("\t");
                predicates.add(new Predicate(
                        stringToClass(details[0]),
                        details[1],
                        stringToClass(details[2]),
                        Boolean.parseBoolean(details[3])
                ));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }

    private Class<? extends Feature> stringToClass(String feature) {
        return switch (feature) {
            case "NPC" -> NPC.class;
            case "Settlement" -> Settlement.class;
            default -> null;
        };
    }

    /**
     * Sets the random number generator to a specific seed
     * @param seed Seed number
     */
    public void setSeedRandom(long seed) {
        randNum.setSeed(seed);
    }
}
