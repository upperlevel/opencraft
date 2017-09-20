package xyz.upperlevel.openverse.client.render.block;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.upperlevel.openverse.util.config.Config;
import xyz.upperlevel.openverse.world.block.BlockType;
import xyz.upperlevel.openverse.world.block.property.BlockProperty;
import xyz.upperlevel.openverse.world.block.state.BlockState;
import xyz.upperlevel.openverse.world.block.state.BlockStateRegistry;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class BlockModelMapper {
    private final BlockType type;
    private final Map<BlockState, BlockModel> models = new HashMap<>();


    @SuppressWarnings("unchecked")
    public BlockModelMapper(BlockType type, Config config) {
        this.type = type;

        BlockStateRegistry reg = type.getStateRegistry();
        BlockModel model = new BlockModel();

        Config def = config.getConfigRequired("defaults");
        model.with(BlockModelRegistry.getModel(Paths.get(def.getStringRequired("model"))));

        List<Config> vars = config.getConfigList("variants");
        if (vars != null) {
            for (Config varCfg : vars) {
                model = model.copy();
                Map<String, Object> stateMap = varCfg.getSection("state");
                if (stateMap != null) {
                    BlockState state = reg.getDefaultState();
                    for (Map.Entry<String, Object> prop : stateMap.entrySet()) {
                        state.with(reg.getProperty(prop.getKey()), prop.getValue());
                    }
                    Path path = Paths.get(varCfg.getStringRequired("model"));
                    model.with(BlockModelRegistry.load(path.toFile()));
                    models.put(state, model);
                }
            }
        }
    }

    /**
     * Retrieves the model for the given {@link BlockState}.
     *
     * @param state the state
     * @return the model
     */
    public BlockModel getModel(BlockState state) {
        return models.get(state);
    }
}