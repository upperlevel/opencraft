package xyz.upperlevel.openverse.physic;

import org.joml.Vector3f;
import xyz.upperlevel.openverse.resource.BlockType;
import xyz.upperlevel.openverse.resource.model.Model;
import xyz.upperlevel.openverse.world.Location;
import xyz.upperlevel.openverse.world.World;

import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.max;

public class PhysicEngine {

    private Set<Force> forces = new HashSet<>();

    public PhysicEngine() {
    }

    public void update(BaseEntity entity, World world, long delta) {
        forces.forEach(f -> f.apply(entity, delta));

        Location loc = entity.getLocation();
        float sx = loc.x();
        float sy = loc.y();
        float sz = loc.z();

        Vector3f vel = entity.getVelocity();
        float vx = vel.x;
        float vy = vel.y;
        float vz = vel.z;

        float ex = sx + vx;
        float ey = sy + vy;
        float ez = sz + vz;

        int bx = (int) ex;
        int by = (int) ey;
        int bz = (int) ez;
        BlockType bt = world.getBlock(bx, by, bz).getType();

        if (bt != null && bt.isSolid()) {
            Model s = bt.getModel();

            // block box
            Box bb = s.getBox().copy();
            bb.add(bx, by, bz);

            // entity box
            Box eb = entity.getType().getModel().getBox().copy();
            eb.add(ex, ey, ez);

            // if collides
            if (bb.intersect(eb)) {
                double dx = bb.maxX() - eb.x;
                double tx = dx / (vx == 0 ? Double.POSITIVE_INFINITY : vx);

                double dy = bb.maxY() - eb.y;
                double ty = dy / (vy == 0 ? Double.POSITIVE_INFINITY : vy);

                double dz = bb.maxZ() - eb.z;
                double tz = dz / (vz == 0 ? Double.POSITIVE_INFINITY : vz);

                double t = max(tx, max(ty, tz));

                loc.set((float) (vx * t), (float) (vy * t), (float) (vz * t));
                return;
            }
            loc.set(vx, vy, vz);
        }
    }
}