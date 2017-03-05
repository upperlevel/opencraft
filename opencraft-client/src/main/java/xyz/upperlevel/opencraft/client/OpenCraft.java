package xyz.upperlevel.opencraft.client;

import lombok.Getter;
import xyz.upperlevel.gamelauncher.api.Game;
import xyz.upperlevel.opencraft.client.asset.AssetManager;
import xyz.upperlevel.opencraft.client.asset.shape.BlockCubeComponent;
import xyz.upperlevel.opencraft.client.asset.shape.BlockShape;
import xyz.upperlevel.opencraft.client.asset.shape.BlockShapeManager;
import xyz.upperlevel.opencraft.client.asset.shape.Zone3f;
import xyz.upperlevel.opencraft.client.asset.texture.Texture;
import xyz.upperlevel.opencraft.client.asset.texture.TextureManager;
import xyz.upperlevel.ulge.util.Color;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class OpenCraft extends Game {

    private static OpenCraft get = new OpenCraft();

    static {
        {
            TextureManager tm = get.assetManager.getTextureManager();
            Texture texture;

            texture = new Texture("null_texture", new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB) {
                {
                    Graphics2D g = createGraphics();
                    g.setColor(java.awt.Color.WHITE);
                    g.fillRect(0, 0, 1, 1);
                }
            });
            tm.register(texture);

            File desktop = new File(System.getProperty("user.home"), "desktop");
            try {
                texture = new Texture("something_texture", ImageIO.read(new File(desktop, "hello.png")));
                tm.register(texture);
                tm.print(new File(desktop, "current_output.png"));
            } catch (IOException e) {
                throw new IllegalStateException("cannot load image", e);
            }
        }

        {
            BlockShapeManager sm = get.assetManager.getShapeManager();
            BlockShape shape;

            shape = new BlockShape("null_shape");
            sm.register(shape);

            shape = new BlockShape("test_shape");
            shape.add(new BlockCubeComponent(
                    new Zone3f(
                            0,
                            0,
                            0,
                            1f,
                            1f,
                            1f
                    )
            ).setColor(Color.RED)
            .setTexture(get.assetManager.getTextureManager().getTexture("something_texture")));
            sm.register(shape);
        }
    }

    @Getter
    private AssetManager assetManager = new AssetManager();

    @Override
    public void start() {
        get = this;
    }

    @Override
    public void close() {
    }

    public static OpenCraft get() {
        return get;
    }
}
