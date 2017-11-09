package xyz.upperlevel.openverse.world;

import lombok.Getter;
import lombok.Setter;
import xyz.upperlevel.openverse.world.block.state.BlockState;
import xyz.upperlevel.openverse.world.chunk.Block;
import xyz.upperlevel.openverse.world.chunk.*;

import java.util.ArrayDeque;
import java.util.Queue;

import static java.lang.Math.floor;
import static xyz.upperlevel.openverse.world.chunk.storage.BlockStorage.AIR_STATE;

@Getter
@Setter
public class World {
    private final String name;
    private ChunkPillarProvider chunkPillarProvider;

    public World(String name) {
        this.name = name;
        this.chunkPillarProvider = new SimpleChunkPillarProvider(this);
    }


    /**
     * Gets the {@link ChunkPillar} at the given chunk coordinates.
     */
    public ChunkPillar getChunkPillar(int x, int z) {
        return chunkPillarProvider.getChunkPillar(x, z);
    }

    /**
     * Sets the {@link ChunkPillar} at the given chunk coordinates to the given one.
     */
    public void setChunkPillar(ChunkPillar chunkPillar) {
        chunkPillarProvider.setChunkPillar(chunkPillar);
    }

    /**
     * Unloads the {@link ChunkPillar} at the given chunk coordinates (this implies inside chunks unloading).
     */
    public boolean unloadChunkPillar(int x, int z) {
        return chunkPillarProvider.unloadChunkPillar(x, z);
    }


    /**
     * Gets the {@link Chunk} at the given chunk coordinates.
     */
    public Chunk getChunk(int x, int y, int z) {
        return getChunkPillar(x, z).getChunk(y);
    }

    public Chunk getChunkFromBlock(int blockX, int blockY, int blockZ) {
        return getChunk(blockX >> 4, blockY >> 4, blockZ >> 4);
    }

    /**
     * Sets the {@link Chunk} at the given chunk coordinates to the given one.
     */
    public void setChunk(int x, int y, int z, Chunk chunk) {
        getChunkPillar(x, z).setChunk(y, chunk);
    }

    /**
     * Unloads the {@link Chunk} at the given chunk coordinates.
     */
    public boolean unloadChunk(int x, int y, int z) {
        return getChunkPillar(x, z).unloadChunk(y);
    }


    /**
     * Gets the {@link Block} at the given world coordinates.
     * It will create a new instance of the {@link Block} object.
     */
    public Block getBlock(int x, int y, int z) {
        Chunk chunk = getChunkFromBlock(x, y, z);
        return chunk == null ? null : chunk.getBlock(x & 0xF, y & 0xF, z & 0xF);
    }

    public Block getBlock(double x, double y, double z) {
        return getBlock((int) floor(x), (int) floor(y), (int) floor(z));
    }


    /**
     * Gets the {@link BlockState} at the given coordinates.
     */
    public BlockState getBlockState(int x, int y, int z) {
        Chunk chunk = getChunkFromBlock(x, y, z);
        return chunk == null ? AIR_STATE : chunk.getBlockState(x & 0xF, y & 0xF, z & 0xF);
    }

    /**
     * Sets the {@link BlockState} at the given coordinates to the given one.
     */
    public void setBlockState(int x, int y, int z, BlockState blockState) {
        Chunk chunk = getChunkFromBlock(x, y, z);
        if (chunk != null) {
            chunk.setBlockState(x & 0xF, y & 0xF, z & 0xF, blockState);
        }
    }


    public int getBlockLight(int x, int y, int z) {
        Chunk chunk = getChunkFromBlock(x, y, z);
        return chunk == null ? 0 : chunk.getBlockLight(x & 0xF, y & 0xF, z & 0xF);
    }

    public void setBlockLight(int x, int y, int z, int blockLight) {
        setBlockLight(x, y, z, blockLight, true);
    }

    public void setBlockLight(int x, int y, int z, int blockLight, boolean diffuse) {
        Chunk chunk = getChunkFromBlock(x, y, z);
        if (chunk != null)
            chunk.setBlockLight(x & 0xF, y & 0xF, z & 0xF, blockLight, diffuse);
    }


    public void diffuseBlockLight(int x, int y, int z) {
        Queue<Integer> bfsQueue = new ArrayDeque<>();

        // fills the center block (15, 15, 15) with the full light (15)
        int i = 0x0F_0F_0F_00 | (getBlockLight(x, y, z) & 0xF);
        bfsQueue.add(i);

        while (!bfsQueue.isEmpty()) {
            int j = bfsQueue.poll();

            // light field coordinates
            int lx = (j & 0xFF_00_00_00) >> 24;
            int ly = (j & 0x00_FF_00_00) >> 16;
            int lz = (j & 0x00_00_FF_00) >> 8;

            // world coordinates based on light field
            int wx = lx - 15 + x;
            int wy = ly - 15 + y;
            int wz = lz - 15 + z;

            int lt = getBlockLight(wx, wy, wz);

            for (BlockFace rel : BlockFace.values()) {
                // relative light field coords
                int rlx = lx + rel.offsetX;
                int rly = ly + rel.offsetY;
                int rlz = lz + rel.offsetZ;

                // relative world coords
                int rwx = wx + rel.offsetX;
                int rwy = wy + rel.offsetY;
                int rwz = wz + rel.offsetZ;

                if ((rlx >= 0 && rlx <= 31) && (rly >= 0 && rly <= 31) && (rlz >= 0 && rlz <= 31) && (getBlockLight(rwx, rwy, rwz) + 2) <= lt) {
                    int rlt = lt - 1;
                    setBlockLight(rwx, rwy, rwz, rlt);
                    bfsQueue.add((rlx & 0xFF) << 24 | (rly & 0xFF) << 16 | (rlz & 0xFF) << 8 | (rlt & 0xF));
                }
            }
        }
    }

    public void removeBlockLightDiffusion(int x, int y, int z) {
        Queue<Integer> bfsQueue = new ArrayDeque<>();
        int i = 0x0F0F0F;
        bfsQueue.add(i);

        while (!bfsQueue.isEmpty()) {
            i = bfsQueue.poll();
            int cx = (i & 0xFF0000) >> 16;
            int cy = (i & 0x00FF00) >> 8;
            int cz = (i & 0x0000FF);

            int cwx = cx - 15 + x;
            int cwy = cy - 15 + y;
            int cwz = cz - 15 + z;

            int cll = getBlockLight(cwx, cwy, cwz);
            for (BlockFace r : BlockFace.values()) {
                // Neighbor light field coords
                int nlx = cx + r.offsetX;
                int nly = cy + r.offsetY;
                int nlz = cz + r.offsetZ;

                if ((nlx >= 0 && nlx <= 31) && (nly >= 0 && nly <= 31) && (nlz >= 0 && nlz <= 31)) {
                    // Neighbor world coords
                    int nwx = x + r.offsetX;
                    int nwy = y + r.offsetY;
                    int nwz = z + r.offsetZ;

                    int nll = getBlockLight(nwx, nwy, nwz);
                    int ni = (nlx & 0xFF) << 16 | (nly & 0xFF) << 8 | (nlz & 0xFF);
                    if (nll != 0 && nll < cll) {
                        setBlockLight(nwx, nwy, nwz, 0);
                        bfsQueue.add(ni);
                    } else if (nll >= cll) {
                        bfsQueue.add(ni);
                    }
                }
            }
        }
    }
}