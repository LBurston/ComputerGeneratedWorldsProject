package generators;
import exceptions.GenerationFailureException;
import features.*;
import org.apache.commons.lang3.tuple.Triple;
import relationships.Relationship;

import java.util.ArrayList;

public class WorldGenerator {

	private static WorldGenerator worldGenerator = null;

	private static final FeatureManager featureManager = FeatureManager.getFeatureManager();
	private static final RelationshipGenerator relationshipGenerator = RelationshipGenerator.getRelationshipGenerator();

	private final World world;

	private static final int INITIAL_NPCS = 150;
	private static final int INITIAL_SETTLEMENTS = 10;

	public static void main(String[] args) {
		WorldGenerator wg = getWorldGenerator();
		wg.initialGeneration();
		wg.printOutCurrentWorldState();
	}

	private WorldGenerator() {
		world = new World();
		featureManager.setWorld(world);
		relationshipGenerator.setWorld(world);
	}

	public static WorldGenerator getWorldGenerator() {
		if(worldGenerator == null) {
			worldGenerator = new WorldGenerator();
		}
		return worldGenerator;
	}

	private void initialGeneration() {
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
					relationshipGenerator.postRelationshipCleanUp(featureRelationship);

				} else {
					unfinishedRelationships.add(featureRelationship);
				}
			}
			world.saveUnfinishedRelationships(unfinishedRelationships);
		} catch (GenerationFailureException ex) {
			ex.printStackTrace();
		}

//		ArrayList<Relationship> completedRelationships = relationshipGenerator.generateNewRelationships(world.getFeatures());
//		world.saveRelationships(completedRelationships);


	}

	private void printOutCurrentWorldState() {
		for(Feature feature : world.getFeatures()) {
			System.out.println("--- " + feature.getName() + " ---");
			System.out.println(feature);
			for(Triple<String, Feature, Relationship> relationshipTriple : feature.getRelationships()) {
				System.out.println();
				System.out.println(relationshipTriple.getMiddle().getName() + " is their " + relationshipTriple.getLeft());
			}
			System.out.println();
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			System.out.println();
		}
	}
}
