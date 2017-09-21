package xyz.upperlevel.openverse.world.block;

import lombok.Getter;
import xyz.upperlevel.openverse.world.block.state.BlockState;
import xyz.upperlevel.openverse.world.block.state.BlockStateRegistry;
import xyz.upperlevel.openverse.world.block.blockentity.BlockEntity;

@Getter
public class BlockType {
    public static final BlockType AIR = new BlockType("air");
    private final String id;
    private int rawId; // todo

    protected final BlockStateRegistry stateRegistry;
    private BlockState defaultBlockState;

    public BlockType(String id) {
        this.id = id;
        this.stateRegistry = createBlockState();
        setDefaultState(stateRegistry.getDefaultState());
    }


    public BlockStateRegistry createBlockState() {
        return BlockStateRegistry.of(this); //Creates a BlockStateRegistry with no propriety
    }

    public BlockState getDefaultState() {
        return defaultBlockState;
    }

    public void setDefaultState(BlockState state) {
        this.defaultBlockState = state;
    }

    public BlockState getBlockState(int meta) {
        return stateRegistry.getState(meta);
    }

    public int getStateMeta(BlockState state) {
        return state.getId();
    }

    public int getFullId(BlockState state) {
        return rawId | (state.getId() & 0xF);
    }


    /**
     * Creates {@link BlockEntity} for this block. If none, returns {@code null}.
     */
    public BlockEntity createBlockEntity(BlockState state) {
        return null;
    }
}