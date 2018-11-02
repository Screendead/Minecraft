package com.screendead.minedaft.world;

import com.screendead.minedaft.graphics.MeshComponent;
import org.joml.Vector3i;

public class Block {
    public boolean[] faces = new boolean[] {
            false, // +Z
            false, // -Z
            false, // +X
            false, // -X
            false, // +Y
            false // -Y
    };

    private BlockType type;
    private Vector3i position;

    public Block(BlockType type, Vector3i position) {
        this.type = type;
        this.position = position;
    }

    public void showFace(int index) {
        faces[index] = true;
    }

    public void hideFace(int index) {
        faces[index] = false;
    }

    public MeshComponent getMeshComponent() {
        return type.getMeshComponent(faces, position.x, position.y, position.z);
    }

    public int getID() {
        return type.getID();
    }

    public String getName() {
        return type.getName();
    }
}