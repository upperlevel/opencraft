package xyz.upperlevel.openverse.physic;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.joml.Vector3f;

import static java.lang.Math.abs;

@Accessors(fluent = true)
public class Box {

    @Getter
    @Setter
    public double x, y, z;

    @Getter
    @Setter
    public double width, height, depth;

    public Box() {
    }

    public Box(double width, double height, double depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public Box(double x, double y, double z, double width, double height, double depth) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public Vector3f getPosition() {
        return new Vector3f(
                (float) x,
                (float) y,
                (float) z
        );
    }

    public Vector3f getSize() {
        return new Vector3f(
                (float) width,
                (float) height,
                (float) depth
        );
    }

    public double minX() {
        return x;
    }

    public double minY() {
        return y;
    }

    public double minZ() {
        return z;
    }

    public double maxX() {
        return x + width;
    }

    public double maxY() {
        return y + height;
    }

    public double maxZ() {
        return z + depth;
    }

    public void add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public void sub(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
    }

    public boolean intersect(Box box) {
        return  (abs(x - box.x) * 2 < (width + box.width)) &&
                (abs(y - box.y) * 2 < (height + box.height)) &&
                (abs(z - box.z) * 2 < (depth + box.depth));
    }

    public boolean isIn(double x, double y, double z) {
        return this.x < x && x < maxX() &&
                this.y < y && y < maxY() &&
                this.z < z && z < maxZ();
    }

    public Box copy() {
        return new Box(x, y, z, width, height, depth);
    }
}