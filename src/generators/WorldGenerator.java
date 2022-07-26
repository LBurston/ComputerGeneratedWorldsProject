package generators;
import features.*;

public class WorldGenerator {

    World world;
    FeatureManager featMan;
    RelationshipGenerator relationGen;

    public WorldGenerator() {
        world = new World();
        featMan = new FeatureManager(world);
        relationGen = new RelationshipGenerator(world, featMan);
    }
}
