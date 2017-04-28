package xyz.upperlevel.openverse.launcher;

import xyz.upperlevel.openverse.launcher.scenes.SingleplayerUniverseScene;
import xyz.upperlevel.ulge.game.Scene;
import xyz.upperlevel.ulge.game.Stage;

public class OpenverseDirector extends Stage {

    public OpenverseDirector() {
    }

    @Override
    public void onEnable(Scene prev) {
        stage(new SingleplayerUniverseScene());
        super.onEnable(prev);
    }
}