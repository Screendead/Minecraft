package com.screendead.minedaft.world;

import com.screendead.minedaft.graphics.MeshComponent;

public class World {
    private Chunk[][] chunks;
    private int xSize, zSize;

    public World(int xSize, int zSize) {
        this.xSize = xSize;
        this.zSize = zSize;

        chunks = new Chunk[xSize][zSize];

        long ns, totalns = System.nanoTime();
        for (int x = 0; x < chunks.length; x++) {
            for (int z = 0; z < chunks[0].length; z++) {
                ns = System.nanoTime();
                chunks[x][z] = new Chunk(x, z);
                ns = System.nanoTime() - ns;
                System.out.println("Chunk " + (x * zSize + z + 1) + "/" + (xSize * zSize) + " generated in " + (float) Math.floor((float) ns / 1000.0f) / 1000.0f + "ms ...");
            }
        }

        generateMeshes();
        totalns = System.nanoTime() - totalns;
        System.out.println("World generation (" + xSize * zSize + " chunks) took " + (float) Math.floor((float) totalns / 1000000.0f) / 1000.0f + "s");
    }

    public void render() {
        for (int x = 0; x < chunks.length; x++) {
            for (int z = 0; z < chunks[0].length; z++) {
                chunks[x][z].render();
            }
        }
    }

    private void generateMeshes() {
        long ns;
        for (int x = 0; x < chunks.length; x++) {
            for (int z = 0; z < chunks[0].length; z++) {
                ns = System.nanoTime();
                generateMesh(x, z);
                ns = System.nanoTime() - ns;
                System.out.println("Mesh for chunk " + (x * zSize + z + 1) + "/" + (xSize * zSize) + " generated in " + (float) Math.floor((float) ns / 1000.0f) / 1000.0f + "ms ...");
            }
        }
    }

    private void generateMesh(int x, int z) {
        MeshComponent m = new MeshComponent(new float[] {}, new float[] {}, new float[] {}, new int[] {});

        for (int k = 255; k >= 0; k--) {
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    if (chunks[x][z].getBlock(i, k, j) == BlockType.AIR.getID()) continue;
                    int mh = Chunk.flatten(i, j, 0);
                    if (chunks[x][z].maxHeight[mh] < k) chunks[x][z].maxHeight[mh] = k;

                    int index = Chunk.flatten(i, j, k);

                    chunks[x][z].blocks[index].setShaded(k < chunks[x][z].maxHeight[mh]);
                    if ((chunks[x][z].blocks[index].getType() != BlockType.BEDROCK) && getBlock(x, z, i, k + 1, j) == BlockType.AIR.getID())
                        chunks[x][z].blocks[index].setType(BlockType.GRASS);
                    if ((chunks[x][z].blocks[index].getType() != BlockType.BEDROCK) &&
                            (getBlock(x, z, i, k + 1, j) == BlockType.GRASS.getID() ||
                                    getBlock(x, z, i, k + 2, j) == BlockType.GRASS.getID() ||
                                    getBlock(x, z, i, k + 3, j) == BlockType.GRASS.getID() ||
                                    getBlock(x, z, i, k + 4, j) == BlockType.GRASS.getID()))
                        chunks[x][z].blocks[index].setType(BlockType.DIRT);

                    // TODO: Make this happen twice at the same time, 3 times per block rather than 6
                    if (getBlock(x, z, i, k, j + 1) == BlockType.AIR.getID()) chunks[x][z].blocks[index].showFace(0);
                    if (getBlock(x, z, i, k, j - 1) == BlockType.AIR.getID()) chunks[x][z].blocks[index].showFace(1);
                    if (getBlock(x, z, i + 1, k, j) == BlockType.AIR.getID()) chunks[x][z].blocks[index].showFace(2);
                    if (getBlock(x, z, i - 1, k, j) == BlockType.AIR.getID()) chunks[x][z].blocks[index].showFace(3);
                    if (getBlock(x, z, i, k + 1, j) == BlockType.AIR.getID()) chunks[x][z].blocks[index].showFace(4);
                    if (getBlock(x, z, i, k - 1, j) == BlockType.AIR.getID()) chunks[x][z].blocks[index].showFace(5);

                    boolean cont = false;
                    for (int a = 0; a < 6; a++) {
                        if (chunks[x][z].blocks[index].faces[a]) {
                            cont = true;
                            break;
                        }
                    }

                    if (cont) m.combine(chunks[x][z].blocks[index].getMeshComponent());
                }
            }
        }

        chunks[x][z].mesh = m.toMesh();
    }

    private int getBlock(int cx, int cz, int x, int y, int z) {
        if (cx < 0 || cx >= chunks.length || cz < 0 || cz >= chunks[0].length) return BlockType.AIR.getID();
        if (chunks[cx][cz].isEmpty() || (y < 0) || (y > 255)) return BlockType.AIR.getID();
        if (x == 16) return getBlock(cx + 1, cz, 0, y, z);
        if (x == -1) return getBlock(cx - 1, cz, 15, y, z);
        if (z == 16) return getBlock(cx, cz + 1, x, y, 0);
        if (z == -1) return getBlock(cx, cz - 1, x, y, 15);
        return chunks[cx][cz].getBlock(x, y, z);
    }

    public void cleanup() {
        for (int x = 0; x < chunks.length; x++) {
            for (int z = 0; z < chunks[0].length; z++) {
                chunks[x][z].cleanup();
            }
        }
    }
}
