package com.cgw.generators;

import com.cgw.exceptions.GenerationFailureException;
import com.cgw.features.*;
import com.cgw.features.World;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.cgw.relationships.Relationship;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Generator Class for the World. Used to call the Feature Manager and Relationship Generator Singleton instances
 * and perform the different parts of the World generation; Initial, Iterative, and Final generations.
 * @author Luke Burston
 * @author lb800@kent.ac.uk
 * @version 0.1
 * @since 0.1
 */
public class WorldGenerator {

	// Singleton instances of itself, the Feature Manager, Relationship Generator.
	private static WorldGenerator worldGenerator = null;
	private static final FeatureManager featureManager = FeatureManager.getFeatureManager();
	private static final RelationshipGenerator relationshipGenerator = RelationshipGenerator.getRelationshipGenerator();

	// Random object to be used. Gets from static class for testing purposes.
	private static final Random randNum = Randomiser.getRandom();

	// Initial Features to be generated in iteration.
	private static final int INITIAL_NPCS = 20;
	private static final int INITIAL_SETTLEMENTS = 1;

	private final World world;	// Model of the World that is Generated, Features and Relationships saved to here.
	private boolean finished;	// Used to check if the generator has finished producing the World.
	private static final boolean testing = false; // Prints details of Generation and World to console when testing.
	private int iterationCounter; // Measure iterations for testing.

	/**
	 * Main method used when only running World Generator with no Application, for internal testing.
	 * @param args The arguments passed.
	 */
	public static void main(String[] args) {
		// StopWatch to measure time to Generate
		StopWatch watch = new StopWatch();
		watch.start();

		WorldGenerator wg = getWorldGenerator();
		World generatedWorld = wg.generateWorldTest();

		if (testing) {
			watch.stop();
			// Various information to print when testing generated results
			wg.printOutCurrentWorldState(generatedWorld);
			wg.printOutFamilyStates(generatedWorld);
			System.out.println();
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			System.out.println("Time to Generate World: " + (watch.getTime(TimeUnit.MILLISECONDS)/1000) + "ms");
			// Information of any killed/killer relationships to check none are generated in research release.
			int victims = (int) generatedWorld.getRelationships().stream()
					.filter(relationship -> relationship.getPredicateAtoB().getPredicateString().matches("killed|killer")).count();
			System.out.println("Victims: " + victims);
		} else {
			watch.stop();
		}
	}

	/**
	 * Constructor for the World Generator. Sets the World for other Generators and ensures finished is set to false.
	 */
	private WorldGenerator() {
		finished = false;
		world = new World();
		featureManager.setWorld(world);
		relationshipGenerator.setWorld(world);
		world.setName(generateName());	// Gives the World a name.
	}

	/**
	 * Used to get the Singleton instance of the World Generator.
	 * @return This Singleton instance of the World Generator.
	 */
	public static WorldGenerator getWorldGenerator() {
		if(worldGenerator == null) {
			worldGenerator = new WorldGenerator();
		}
		return worldGenerator;
	}

	/**
	 * Randomly chooses a World name from the txt file of possible names.
	 * @return A String of the World's name.
	 */
	private String generateName() {
		ArrayList<String> names = new ArrayList<>();
		try {
			// Gets the txt file from resources and adds each line to an ArrayList
			InputStream is = getClass().getResourceAsStream("/worldNames/worldNames.txt");
			assert is != null;
			BufferedReader reader = new BufferedReader(new
					InputStreamReader(is));
			String currentLine;
			while((currentLine = reader.readLine()) != null) {
				names.add(currentLine);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		// Chooses a name at random from the ArrayList.
		return names.get(randNum.nextInt(names.size()));
	}

	/**
	 * Used by the Application to Generate a World.
	 * Does an Initial generation of the starting Features of the World and a potential Relationship for each.
	 * Then passes these into the Iterative Generation that creates New Features if none were made at the end
	 * of the Initial Generation.
	 * This loops making new Relationships and Features until one of the Stopping Criteria has been met.
	 * Once the Stopping Criteria is met, the final Relationship Generations are completed, which
	 * does not make any new Features, only makes more Relationships between existing Features.
	 */
	public void generateWorld() throws GenerationFailureException {
		finished = false;
		ArrayList<Feature> newFeatures = initialGeneration();
		do {
			if(newFeatures == null) {
				throw new GenerationFailureException("List of the new features provided in Iteration was Null.");
			} else if (newFeatures.isEmpty()){
				newFeatures = featureManager.generateFeatures(INITIAL_NPCS, INITIAL_SETTLEMENTS);
			}

			newFeatures = iterativeGeneration(newFeatures);

		} while (stoppingCriteriaNotMet());

		finalGenerations();

		finished = true;
	}


	/**
	 * Used for Testing purposes to return the World for printing information.
	 * @return The World generated.
	 */
	private World generateWorldTest() {
		ArrayList<Feature> newFeatures = initialGeneration();
		do {
			if(newFeatures == null) {
				throw new RuntimeException();
			} else if (newFeatures.isEmpty()){
				newFeatures = featureManager.generateFeatures(INITIAL_NPCS, INITIAL_SETTLEMENTS);
			}

			newFeatures = iterativeGeneration(newFeatures);

		} while (stoppingCriteriaNotMet());

		finalGenerations();
		return world;
	}

	/**
	 * The Initial Generation in making the World. Generates a number of each Feature depending on its Initial value.
	 * @return An ArrayList of new Features Generated from unfinished Relationships
	 * (Nullable in case of Generation Failure).
	 */
	private @Nullable ArrayList<Feature> initialGeneration() {
		if(testing) { iterationCounter = 0; }
		try {
			// Generates a number of Features of each type and saves them to the World.
			world.saveFeatures(featureManager
					.generateFeatures(INITIAL_NPCS,
							INITIAL_SETTLEMENTS));
			// Creates an ArrayList to store any Relationships that are incomplete.
			ArrayList<Relationship> unfinishedRelationships = new ArrayList<>();
			// Each Initial Feature is either given a complete or unfinished Relationship, or none at all.
			for(Feature feature : world.getFeatures()) {
				Relationship featureRelationship = relationshipGenerator.generateNewRelationship(feature);
				if (featureRelationship == null) {
					// Goes to next Feature if no Relationship given
					continue;
				} else if(featureRelationship.isCompleted()) {
					// Saves new Relationship to the World and does any Post Relationship clean-up of the World state.
					world.saveRelationship(featureRelationship);
					relationshipGenerator.postRelationshipCleanUp(featureRelationship);
				} else {
					// If no compatible Feature was found for the Relationship, it is added to unfinished.
					unfinishedRelationships.add(featureRelationship);
				}
			}
			// Saves all unfinished Relationships to the World.
			world.saveUnfinishedRelationships(unfinishedRelationships);
			// Each unfinished Relationship is then potentially completed by generating a new Feature for it.
			// This then returns an ArrayList of any Features that were generated in this process.
			return featureManager.completeUnfinishedRelationships(world.getUnfinishedRelationships());
		} catch (GenerationFailureException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * The Iterative Generation method called each time after the Initial, until the Stopping Criteria is met.
	 * This works similarly to Initial Generation, but passes in New Features Generated from the previous
	 * Generation and also adds in any Features who do not currently have a Relationship.
	 * @param newFeatures The Features passed in to have a Relationship given to them.
	 * @return An ArrayList of any New Features generated from Relationships.
	 */
	private @Nullable ArrayList<Feature> iterativeGeneration(ArrayList<Feature> newFeatures) {
		if(testing) {
			iterationCounter++;
			System.out.println("Iteration: " + (iterationCounter));
		}
		world.saveFeatures(newFeatures);
		// Adds any Feature of the world that is yet to be given a Relationship.
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

	/**
	 * Final Generations for the Generator to complete after Stopping Criteria met. All unfinished Relationships
	 * are removed completely. Any Features with a low amount of Relationships are added to a List and for
	 * 5 iterations are potentially given more Relationships with existing Features.
	 */
	private void finalGenerations() {
		// Removes all unfinished Relationships to clear space.
		Iterator<Relationship> unfinishedRelationshipsIterator = world.getUnfinishedRelationships().iterator();
		while (unfinishedRelationshipsIterator.hasNext()) {
			unfinishedRelationshipsIterator.next();
			unfinishedRelationshipsIterator.remove();
		}

		// Looks for any Features with less than 3 Relationships to try and Link them with other Features.
		ArrayList<Feature> featuresWithLowRelationships = new ArrayList<>();
		for(Feature feature : world.getFeatures()) {
			if(feature.numberOfRelationships() < 3) {
				featuresWithLowRelationships.add(feature);
			}
		}

		// For each low Relationship Feature, it can potentially make 5 new Relationships with other Features.
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

	/**
	 * Checks if Stopping Criteria for Iterative Generation has not been met.
	 * @return Boolean of whether Stopping Criteria has not been met.
	 */
	private boolean stoppingCriteriaNotMet() {
		ArrayList<NPC> npcs = (ArrayList<NPC>) world.getAllOfSpecificFeature(NPC.class);
		ArrayList<Settlement> settlements = (ArrayList<Settlement>) world.getAllOfSpecificFeature(Settlement.class);

		// Checks the various Stopping Criteria which can be added to.
		// Currently, checks if NPCs or Settlements have exceeded their limits,
		// as well as other checks that all have to be met.
		return npcs.size() <= 800 && settlements.size() <= 20 &&
				(!almostEveryNPCHasAResidence(npcs) || !almostAllNPCsHaveMoreThanTwoFamilyMembers(npcs)
						|| !everySettlementHasARuler(settlements));
	}

	/**
	 * Checks if the amount of NPCs that have a Residence exceeds the specified percentage.
	 * @param npcs The NPCs of the World to check.
	 * @return Boolean of whether the amount of NPCs exceeds the specified percentage.
	 */
	private boolean almostEveryNPCHasAResidence(ArrayList<NPC> npcs) {
		double percentage = 0.9;	// Percentage of NPCs required.
		int passingSize = (int) Math.round(npcs.size()*percentage);	// Amount of NPCs required.
		int counter = 0;	// Counts those that have a Residence.
		for(NPC npc : npcs) {
			if(npc.hasResidence()) { counter++; }
		}
		return counter >= passingSize;
	}

	/**
	 * Checks if the amount of NPCs that have more than two Family Members exceeds the specified percentage.
	 * @param npcs The NPCs of the World to check.
	 * @return Boolean of whether the amount of NPCs exceeds the specified percentage.
	 */
	private boolean almostAllNPCsHaveMoreThanTwoFamilyMembers(@NotNull ArrayList<NPC> npcs) {
		double percentage = 0.95;	// Percentage of NPCs required.
		int passingSize = (int) Math.round(npcs.size()*percentage);	// Amount of NPCs required.
		int counter = 0;	// Counts those with more than two Family Members.
		for(NPC npc : npcs) {
			if (npc.getFamilyMembers().size() > 2) {
				counter++;
			}
		}
		return counter >= passingSize;
	}

	/**
	 * Checks if every Settlement has a Ruler.
	 * @param settlements The Settlements of the World to check.
	 * @return Boolean of whether every Settlement has a Ruler.
	 */
	private boolean everySettlementHasARuler(@NotNull ArrayList<Settlement> settlements) {
		for(Settlement settlement : settlements) {
			if(!settlement.hasRuler()) {return false;}
		}
		return true;
	}

	/**
	 * Prints out every Feature of the World and a list of their Relationships
	 * @param world The World to print from.
	 */
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

	/**
	 * Prints out every NPC with their Family Members.
	 * @param world The World to print from.
	 */
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

	/**
	 * Gets the Generated World.
	 * @return The Generated World.
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * Checks if the World is Finished Generating.
	 * @return Boolean of whether the World has finished Generating.
	 */
	public boolean isFinished() {
		return finished;
	}
}