package xyz.upperlevel.openverse.client.resource.model.shape;

import org.joml.Matrix4f;
import xyz.upperlevel.openverse.Openverse;
import xyz.upperlevel.openverse.client.resource.ClientResources;
import xyz.upperlevel.openverse.physic.Box;
import xyz.upperlevel.openverse.resource.model.shape.Cube;
import xyz.upperlevel.openverse.util.config.Config;
import xyz.upperlevel.ulge.util.Color;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * A client-side cube has faces and each face has a color and a texture.
 */
public class ClientCube extends Cube implements ClientShape {
    public static final int VERTICES_COUNT = 6 * TexturedCubeFace.VERTICES_COUNT;
    private final Map<CubeFacePosition, TexturedCubeFace> faces = new EnumMap<>(CubeFacePosition.class);

    public ClientCube() {
        super();
        setupFaces();
    }

    public ClientCube(Config config) {
        super(config);
        setupFaces();
        if (config.has("faces")) {
            for (Config faceCfg : config.getConfigList("faces")) {
                CubeFacePosition pos = faceCfg.getEnum("position", CubeFacePosition.class);
                faces.put(pos, new TexturedCubeFace(this, pos, faceCfg));
            }
        }
        if (config.has("texture")) {
            setTexture(((ClientResources) Openverse.resources()).textures().entry(config.getString("texture")));
        }
    }

    private void setupFaces() {
        for (CubeFacePosition position : CubeFacePosition.values())
            faces.put(position, new TexturedCubeFace(this, position));
    }

    public TexturedCubeFace getFace(CubeFacePosition position) {
        return faces.get(position);
    }

    public Collection<TexturedCubeFace> getFaces() {
        return faces.values();
    }

    @Override
    public int getVerticesCount() {
        return VERTICES_COUNT;
    }

    /**
     * Sets given color foreach face.
     *
     * @param color the color
     */
    @Override
    public void setColor(Color color) {
        getFaces().forEach(face -> face.setColor(color));
    }

    /**
     * Sets given texture foreach face.
     *
     * @param texture the texture
     */
    @Override
    public void setTexture(Texture texture) {
        getFaces().forEach(face -> face.setTexture(texture));
    }

    /**
     * Stores vertices for this cube in a buffer
     */
    public int store(Matrix4f in, ByteBuffer buffer) {
        Box box = getBox();
        // moves the model part to its position related to block space
        in.translate(box.getSize().add(box.getPosition()));
        int sz = 0;
        for (TexturedCubeFace face : faces.values())
            sz += face.store(new Matrix4f(in), buffer);
        return sz;
    }
}
