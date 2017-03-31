package xyz.upperlevel.opencraft.client.resource.model.impl;

import lombok.Getter;
import lombok.Setter;
import xyz.upperlevel.ulge.util.Color;

public class Vertex {

    @Getter
    @Setter
    private float x, y, z;

    @Getter
    @Setter
    private Color color = Color.WHITE;

    @Getter
    @Setter
    private float u, v;

    public Vertex() {
    }
}
