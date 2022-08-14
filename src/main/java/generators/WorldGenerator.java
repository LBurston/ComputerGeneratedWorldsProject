package generators;
import exceptions.GenerationFailureException;
import features.*;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import relationships.Relationship;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class WorldGenerator {

	private static WorldGenerator worldGenerator = null;

	private static final FeatureManager featureManager = FeatureManager.getFeatureManager();
	private static final RelationshipGenerator relationshipGenerator = RelationshipGenerator.getRelationshipGenerator();

	private final World world;

	private static final int INITIAL_NPCS = 10;
	private static final int INITIAL_SETTLEMENTS = 2;

	public static void main(String[] args) {
		WorldGenerator wg = getWorldGenerator();
		ArrayList<Feature> newFeatures = wg.initialGeneration();
		do {
			if(newFeatures == null) {
				throw new RuntimeException();
			} else if (newFeatures.isEmpty()){
				newFeatures = featureManager.generateFeatures(INITIAL_NPCS, INITIAL_SETTLEMENTS);
			}

			newFeatures = wg.iterativeGeneration(newFeatures);

		} while (!wg.stoppingCriteriaMet());
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

	private boolean stoppingCriteriaMet() {
		ArrayList<NPC> npcs = (ArrayList<NPC>) world.getAllOfSpecificFeature(NPC.class);
		ArrayList<Settlement> settlements = (ArrayList<Settlement>) world.getAllOfSpecificFeature(Settlement.class);

		return npcs.size() > 800 || settlements.size() > 25 ||
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
