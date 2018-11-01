package com.screendead.minedaft.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    public Vector3f up = new Vector3f(0, 1, 0);
    public Vector3f look, right = new Vector3f();
    Vector3f pos, vel, acc;
    public float horizontal = 0, vertical = 0;
    private Matrix4f lookMatrix;

    public Camera() {
        this(new Vector3f(0, 1, 0));
    }

    public Camera(Vector3f pos) {
        this(pos, new Vector3f(0, 0, -1));
    }

    public Camera(Vector3f pos, Vector3f look) {
        this.pos = pos;
        this.vel = new Vector3f();
        this.acc = new Vector3f();

        this.look = look.normalize();

        up.cross(look, right);
        right.normalize();

        update(0, 0);
    }

    public void update(float dx, float dy) {
        horizontal += -dx / 15.0f;
        vertical += (dy * 2) / 15.0f;

        horizontal = horizontal % 360.0f;
        vertical = constrain(vertical, -89.99f, 89.99f);

        this.look = new Vector3f(0, 0, -1).rotateAxis((float) Math.toRadians(horizontal), up.x, up.y, up.z);

        up.cross(look, right);
        right.normalize();

        this.look.rotateAxis((float) Math.toRadians(vertical), right.x, right.y, right.z);

        vel.add(acc);
        vel.mul(0.92f);
        pos.add(vel);
        acc.zero();

        lookMatrix = new Matrix4f().lookAt(this.pos, this.pos.add(this.look, new Vector3f()), this.up);
    }

    public void move(int walk, int fly, int strafe) {
        acc.add(new Vector3f(look.x, 0, look.z).normalize().mul((float) walk / 30.0f));
        acc.add(new Vector3f(right.x, right.y, right.z).normalize().mul((float) strafe / 30.0f));
        acc.add(new Vector3f(up.x, up.y, up.z).normalize().mul((float) fly / 30.0f));
    }

    private float constrain(float f, float min, float max) {
        return Math.min(Math.max(f, min), max);
    }

    public Matrix4f getMatrix() {
        return lookMatrix;
    }
}
