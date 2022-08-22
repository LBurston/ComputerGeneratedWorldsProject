package com.cgw.generators;

import com.cgw.exceptions.GenerationFailureException;
import com.cgw.features.*;
import com.cgw.features.World;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.cgw.relationships.Relationship;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class WorldGenerator {

	private static WorldGenerator worldGenerator = null;

	private static final FeatureManager featureManager = FeatureManager.getFeatureManager();
	private static final RelationshipGenerator relationshipGenerator = RelationshipGenerator.getRelationshipGenerator();

	private static final Random randNum = Randomiser.getRandom();

	private final World world;

	private static final int INITIAL_NPCS = 20;
	private static final int INITIAL_SETTLEMENTS = 1;

	private static final boolean testing = true;

	private boolean finished;

	public static void main(String[] args) {
		StopWatch watch = new StopWatch();
		watch.start();

		WorldGenerator wg = getWorldGenerator();
		World generatedWorld = wg.generateWorldTest();

		if (testing) {
			wg.printOutCurrentWorldState(generatedWorld);
			wg.printOutFamilyStates(generatedWorld);
		watch.stop();
		System.out.println();
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("Time to Generate World: " + (watch.getTime(TimeUnit.MILLISECONDS)/1000) + "ms");
		} else {
			watch.stop();
		}
		int victims = (int) generatedWorld.getRelationships().stream()
				.filter(relationship -> relationship.getPredicateAtoB().getPredicateString().matches("killed|killer")).count();
		System.out.println("Victims: " + victims);
	}

	private WorldGenerator() {
		finished = false;
		world = new World();
		featureManager.setWorld(world);
		relationshipGenerator.setWorld(world);
		world.setName(generateName());
	}

	public static WorldGenerator getWorldGenerator() {
		if(worldGenerator == null) {
			worldGenerator = new WorldGenerator();
		}
		return worldGenerator;
	}

	private String generateName() {
		ArrayList<String> names = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/worldNames/worldNames.txt"));
			String currentLine;
			while((currentLine = reader.readLine()) != null) {
				names.add(currentLine);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return names.get(randNum.nextInt(names.size()));
	}

	public void generateWorld() {
		finished = false;
		ArrayList<Feature> newFeatures = initialGeneration();
		do {
			if(newFeatures == null) {
				throw new RuntimeException();
			} else if (newFeatures.isEmpty()){
				newFeatures = featureManager.generateFeatures(INITIAL_NPCS, INITIAL_SETTLEMENTS);
			}

			newFeatures = iterativeGeneration(newFeatures);

		} while (!stoppingCriteriaMet());

		finalGenerations();

		finished = true;
	}

	private World generateWorldTest() {
		ArrayList<Feature> newFeatures = initialGeneration();
		do {
			if(newFeatures == null) {
				throw new RuntimeException();
			} else if (newFeatures.isEmpty()){
				newFeatures = featureManager.generateFeatures(INITIAL_NPCS, INITIAL_SETTLEMENTS);
			}

			newFeatures = iterativeGeneration(newFeatures);

		} while (!stoppingCriteriaMet());

		finalGenerations();
		return world;
	}

	private @Nullable ArrayList<Feature> initialGeneration() {
		try {
			world.saveFeatures(featureManager
					.generateFeatures(INITIAL_NPCS,
							INITIAL_SETTLEMENTS));
			ArrayList<Relationship> unfinishedRelationships = new ArrayList<>();
			for(Feature feature : world.getFeatures()) {
				Relationship featureRelationship = relationshipGenerator.generateNewRelationship(feature);
				if (featureRelationship == null) {
					continue;
				} else if(featureRelationship.isCompleted()) {
					world.saveRelationship(featureRelationship);
					relationshipGenerator.postRelationshipCleanUp(featureRelationship);
				} else {
					unfinishedRelationships.add(featureRelationship);
				}
			}
			world.saveUnfinishedRelationships(unfinishedRelationships);
			return featureManager.completeUnfinishedRelationships(world.getUnfinishedRelationships());
		} catch (GenerationFailureException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private @Nullable ArrayList<Feature> iterativeGeneration(ArrayList<Feature> newFeatures) {
		world.saveFeatures(newFeatures);
        for(Feature feature : world.getFeatures()) {
			if(feature.hasNoRelationships()) { newFeatures.add(feature); }
		}
		try {
			ArrayList<Relationship> unfinishedRelationships = new ArrayList<>();
			for(Feature feature : newFeatures) {
				Relationship featureRelationship = relationshipGenerator.generateNewRelationship(feature);
				if (featureRelationship == null) {
					continue;
				} else if (featureRelationship.isCompleted()) {
					world.saveRelationship(featureRelationship);
					relationshipGenerator.postRelationshipCleanUp(featureRelationship);
				} else {
					unfinishedRelationships.add(featureRelationship);
				}
			}
			world.saveUnfinishedRelationships(unfinishedRelationships);
			return featureManager.completeUnfinishedRelationships(world.getUnfinishedRelationships());
		} catch (GenerationFailureException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private void finalGenerations() {
		Iterator<Relationship> unfinishedRelationshipsIterator = world.getUnfinishedRelationships().iterator();

		while (unfinishedRelationshipsIterator.hasNext()) {
			Relationship relationship = unfinishedRelationshipsIterator.next();
			unfinishedRelationshipsIterator.remove();
		}

		ArrayList<Feature> featuresWithLowRelationships = new ArrayList<>();
		for(Feature feature : world.getFeatures()) {
			if(feature.numberOfRelationships() < 3) {
				featuresWithLowRelationships.add(feature);
			}
		}

		for (int finalFive = 5; finalFive > 0; finalFive--) {
			for(Feature feature : featuresWithLowRelationships) {
				try {
					Relationship featureRelationship = relationshipGenerator.generateNewRelationship(feature);
					if (featureRelationship == null) {
						continue;
					} else if (featureRelationship.isCompleted()) {
						world.saveRelationship(featureRelationship);
						relationshipGenerator.postRelationshipCleanUp(featureRelationship);
					}
				} catch (GenerationFailureException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private boolean stoppingCriteriaMet() {
		ArrayList<NPC> npcs = (ArrayList<NPC>) world.getAllOfSpecificFeature(NPC.class);
		ArrayList<Settlement> settlements = (ArrayList<Settlement>) world.getAllOfSpecificFeature(Settlement.class);

		return npcs.size() > 800 || settlements.size() > 15 ||
				(almostEveryNPCHasAResidence(npcs) && almostAllNPCsHaveMoreThanTwoFamilyMembers(npcs)
						&& everySettlementHasARuler(settlements));
	}

	private boolean almostEveryNPCHasAResidence(ArrayList<NPC> npcs) {
		double percentage = 0.9;
		int passingSize = (int) Math.round(npcs.size()*percentage);
		int counter = 0;
		for(NPC npc : npcs) {
			if(npc.hasResidence()) { counter++; }
		}
		return counter >= passingSize;
	}

	private boolean almostAllNPCsHaveMoreThanTwoFamilyMembers(@NotNull ArrayList<NPC> npcs) {
		double percentage = 0.95;
		int passingSize = (int) Math.round(npcs.size()*percentage);
		int counter = 0;
		for(NPC npc : npcs) {
			if (npc.getFamilyMembers().size() > 2) {
				counter++;
			}
		}
		return counter >= passingSize;
	}

	private boolean everySettlementHasARuler(@NotNull ArrayList<Settlement> settlements) {
		for(Settlement settlement : settlements) {
			if(!settlement.hasRuler()) {return false;}
		}
		return true;
	}

	private void printOutCurrentWorldState(World world) {
		for(Feature feature : world.getFeatures()) {
			System.out.println("--- " + feature.getName() + " ---");
			System.out.println(feature);
			for(Triple<String, Feature, Relationship> relationshipTriple : feature.getTripleRelationships()) {
				System.out.println();
				System.out.println(relationshipTriple.getMiddle().getName() + " is their " + relationshipTriple.getLeft());
			}
			System.out.println();
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			System.out.println();
		}
        System.out.println("## Number of Features ##");
        System.out.println("NPCs:\t" + world.getAllOfSpecificFeature(NPC.class).size());
        System.out.println("Settlements:\t" + world.getAllOfSpecificFeature(Settlement.class).size());
		System.out.println();
		System.out.println("## Number of Relationships ##");
		System.out.println("Relationships:\t" + world.getRelationships().size());
	}

	private void printOutFamilyStates(World world) {
		for (NPC npc : (ArrayList<NPC>) world.getAllOfSpecificFeature(NPC.class)) {
			System.out.println("--- " + npc.getName() + " ---");
			System.out.println(npc);
			System.out.println();
			ArrayList<Triple<String, Feature, Relationship>> npcFamilyMembers = npc.getTripleRelationships().stream()
					.filter(relationship -> relationship.getLeft().matches("mother|father|parent|child|sibling|partner"))
					.collect(Collectors.toCollection(ArrayList::new));
			for(Triple<String, Feature, Relationship> relationshipTriple : npcFamilyMembers) {
				NPC family = (NPC) relationshipTriple.getMiddle();
				System.out.println(relationshipTriple.getLeft() + ": " + family.getName() + " a " +
						family.getAge() + " " + family.getAgeGroupString() + " " + family.getGenderString() +
						 " " + family.getRace() + ".");
			}
			System.out.println();
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			System.out.println();
		}
	}

	public World getWorld() {
		return world;
	}

	public boolean isFinished() {
		return finished;
	}
}