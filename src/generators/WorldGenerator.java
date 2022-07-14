package generators;
import features.*;

public class WorldGenerator {

    World world;
    FeatureGenerator featGen;

    public WorldGenerator() {
        world = new World();
        featGen = new FeatureGenerator(world);

    }
}
