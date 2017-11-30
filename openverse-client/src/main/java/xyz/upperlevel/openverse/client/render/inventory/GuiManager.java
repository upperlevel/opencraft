package xyz.upperlevel.openverse.client.render.inventory;

import xyz.upperlevel.event.EventHandler;
import xyz.upperlevel.event.EventPriority;
import xyz.upperlevel.event.Listener;
import xyz.upperlevel.openverse.Openverse;
import xyz.upperlevel.openverse.client.OpenverseClient;
import xyz.upperlevel.openverse.launcher.OpenverseLauncher;
import xyz.upperlevel.openverse.world.entity.player.events.PlayerInventoryCloseEvent;
import xyz.upperlevel.openverse.world.entity.player.events.PlayerInventoryOpenEvent;
import xyz.upperlevel.ulge.gui.GuiViewer;

import static org.lwjgl.opengl.GL11.*;

public class GuiManager implements Listener {
    private GuiViewer viewer = new GuiViewer(OpenverseLauncher.get().getGame().getWindow());

    public GuiManager() {
        Openverse.getEventManager().register(this);
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerOpenInventory(PlayerInventoryOpenEvent event) {
        //Should never happen but you never know
        if (event.getPlayer() != OpenverseClient.get().getPlayer()) return;
        InventoryGui<?> currentGui = OpenverseClient.get().getInventoryGuiRegistry().create(event.getInventory());
        viewer.open(currentGui);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCloseInventory(PlayerInventoryCloseEvent event) {
        //Should never happen but you never know
        if (event.getPlayer() != OpenverseClient.get().getPlayer()) return;
        viewer.close();
    }

    public void render(float partialTicks) {
        if (viewer.getHandle() != null) {
            glClear(GL_DEPTH_BUFFER_BIT);
            glDisable(GL_CULL_FACE);
            viewer.render();
            glEnable(GL_CULL_FACE);
        }
    }
}
