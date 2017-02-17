package world;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import xyz.upperlevel.opencraft.renderer.texture.TextureFragment;
import xyz.upperlevel.opencraft.renderer.texture.Textures;
import xyz.upperlevel.ulge.util.Color;
import xyz.upperlevel.ulge.util.Colors;

import java.nio.ByteBuffer;

enum QuadVertexPosition {
    TOP_LEFT {
        @Override
        public BlockVertex createVertex(BlockFace face) {
            return new BlockVertex(face)
                    .setX(-1f)
                    .setY(1f);
        }
    },
    TOP_RIGHT {
        @Override
        public BlockVertex createVertex(BlockFace face) {
            return new BlockVertex(face)
                    .setX(1f)
                    .setY(1f)
                    .setU(1f);
        }
    },
    BOTTOM_LEFT {
        @Override
        public BlockVertex createVertex(BlockFace face) {
            return new BlockVertex(face)
                    .setX(-1f)
                    .setY(-1f)
                    .setV(1f);
        }
    },
    BOTTOM_RIGHT {
        @Override
        public BlockVertex createVertex(BlockFace face) {
            return new BlockVertex(face)
                    .setX(1f)
                    .setY(-1f)
                    .setU(1f)
                    .setV(1f);
        }
    };

    public abstract BlockVertex createVertex(BlockFace face);
}

@Accessors(chain = true)
public final class BlockFace {

    public static final int SIZE = BlockVertex.SIZE * 4;

    @Getter
    private BlockComponent component;

    @Getter
    private BlockFacePosition position;

    @Getter
    private Zone3f zone;

    @Getter
    @Setter
    @NonNull
    private TextureFragment texture = Textures.NULL;

    @Getter
    private BlockVertex[] vertices = new BlockVertex[4];

    {
        for (int i = 0; i < vertices.length; i++)
            vertices[i] = QuadVertexPosition.values()[i].createVertex(this);
    }

    private Matrix4f transformation;

    public BlockFace(BlockComponent component, BlockFacePosition position) {
        this.component = component;
        this.zone = position.obtainZone(component.getZone());
        this.position = position;
    }

    public BlockVertex getVertex(QuadVertexPosition position) {
        return vertices[position.ordinal()];
    }

    public void setColor(Color color) {
        for (BlockVertex blockVertex : vertices)
            blockVertex.setColor(color);
    }

    protected int setupBuffer(ByteBuffer buffer, Matrix4f matrix) {
        Zone3f compZone = component.getZone();

        Matrix4f m = position.rotateToCubeRotation(new Matrix4f()
                .translate(position.getDirection().mul(compZone.getSize()))
                .scale(compZone.getSize()));

        // top left
        BlockVertexBufferStorer.setPosition(buffer, m.transformPosition(-1f, 1f, 0f, new Vector3f()))
        BlockVertexBufferStorer.setColor(buffer, Colors.WHITE)
        BlockVertexBufferStorer.setTextureCoords(buffer, texture.getMinU(), texture.getMinV());


        buffer.position(m.transformPosition(-1f, 1f, 0f, new Vector3f()))
                .color(Colors.WHITE)
                .texture(texture.getMinU(), texture.getMinV());
        // bottom left
        buffer.position(m.transformPosition(-1f, -1f, 0f, new Vector3f()))
                .color(Colors.WHITE)
                .texture(texture.getMinU(), texture.getMaxV());
        // bottom right
        buffer.position(m.transformPosition(1f, -1f, 0f, new Vector3f()))
                .color(Colors.WHITE)
                .texture(texture.getMaxU(), texture.getMaxV());
        // top right
        buffer.position(m.transformPosition(1f, 1f, 0f, new Vector3f()))
                .color(Colors.WHITE)
                .texture(texture.getMaxU(), texture.getMinV());

        System.out.println(texture.toString());
        return 4;
    }

    @Override
    public int hashCode() {
        return position.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof BlockFace && position == ((BlockFace) object).position;
    }
}