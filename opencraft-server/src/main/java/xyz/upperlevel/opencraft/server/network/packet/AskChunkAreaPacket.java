package xyz.upperlevel.opencraft.server.network.packet;

import lombok.Getter;
import lombok.Setter;
import xyz.upperlevel.utils.packet.packet.Packet;

public class AskChunkAreaPacket implements Packet {

    @Getter
    @Setter
    private int x, y, z;

    public AskChunkAreaPacket(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}