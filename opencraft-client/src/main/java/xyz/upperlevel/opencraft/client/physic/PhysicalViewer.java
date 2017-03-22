package xyz.upperlevel.opencraft.client.physic;

import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import xyz.upperlevel.opencraft.client.physic.util.PhysicalFace;
import xyz.upperlevel.ulge.util.math.AngleUtil;

import static xyz.upperlevel.opencraft.client.physic.util.PhysicalFace.*;

public class PhysicalViewer {

    @Getter
    protected float x = 1, y = 7, z = 1;

    @Getter
    protected float yaw, pitch;

    public PhysicalViewer() {
    }

    // position

    public void setX(float x) {
        this.x = x;
        updatePosition();
    }

    public void setY(float y) {
        this.y = y;
        updatePosition();
    }

    public void setZ(float z) {
        this.z = z;
        updatePosition();
    }

    public void addX(float x) {
        this.x += x;
        updatePosition();
    }

    public void addY(float y) {
        this.y += y;
        updatePosition();
    }

    public void addZ(float z) {
        this.z += z;
        updatePosition();
    }

    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        updatePosition();
    }

    public void addPosition(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        updatePosition();
    }

    public void addPosition(Vector3f position) {
        x += position.x;
        y += position.y;
        z += position.z;
        updatePosition();
    }

    public void updatePosition() {
    }

    // speed ---

    @Getter
    @Setter
    public float speedX, speedY, speedZ;

    public void addSpeed(float x, float y, float z) {
        speedX += x;
        speedY += y;
        speedZ += z;
    }

    public void addSpeed(Vector3f value) {
        addSpeed(value.x, value.y, value.z);
    }

    public void setSpeed(float value) {
        speedX = value;
        speedY = value;
        speedZ = value;
    }

    public void setSpeed(float x, float y, float z) {
        speedX = x;
        speedY = y;
        speedZ = z;
    }

    public void setSpeed(Vector3f value) {
        setSpeed(value.x, value.y, value.z);
    }

    // rotation

    public void setYaw(float yaw) {
        this.yaw = (float) AngleUtil.normalizeDegreesAngle(yaw);
        updateRotation();
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;

        if (this.pitch > 90)
            this.pitch = 90;
        if (this.pitch < -90)
            this.pitch = -90;

        updateRotation();
    }

    public void addYaw(float yaw) {
        setYaw(this.yaw + yaw);
        updateRotation();
    }

    public void addPitch(float pitch) {
        setPitch(this.pitch + pitch);
        updateRotation();
    }

    public void addRotation(float yaw, float pitch) {
        addYaw(yaw);
        addPitch(pitch);
        updateRotation();
    }

    public void updateRotation() {
    }

    // camera

    public Matrix4f getOrientation() {
        Matrix4f result = new Matrix4f();
        //result.rotate((float) Math.toRadians(pitch), new Vector3f(1f, 0, 0));
        result.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1f, 0));
        return result;
    }

    public Vector3f getForward() {
        return getOrientation().invert(new Matrix4f()).transformDirection(new Vector3f(0f, 0f, -1f));
    }

    public Vector3f getBackward() {
        return getForward().mul(-1f);
    }

    public Vector3f getRight() {
        return getOrientation().invert(new Matrix4f()).transformDirection(new Vector3f(1f, 0, 0));
    }

    public Vector3f getLeft() {
        return getRight().mul(-1f);
    }

    public Vector3f getUp() {
        return new Vector3f(0, 1f, 0);
    }

    public Vector3f getDown() {
        return new Vector3f(0, -1f, 0);
    }

    public void forward(float sensitivity) {
        addSpeed(getForward().mul(sensitivity));
    }

    public void backward(float sensitivity) {
        addSpeed(getBackward().mul(sensitivity));
    }

    public void right(float sensitivity) {
        addSpeed(getRight().mul(sensitivity));
    }

    public void left(float sensitivity) {
        addSpeed(getLeft().mul(sensitivity));
    }

    public void up(float sensitivity) {
        addSpeed(getUp().mul(sensitivity));
    }

    public void down(float sensitivity) {
        addSpeed(getDown().mul(sensitivity));
    }
}