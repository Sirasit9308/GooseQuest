package com.goosequest.world;

/** [คนที่ 3] วัตถุที่เดินได้ */
public abstract class Entity extends GameObject {
    protected Direction facing = Direction.DOWN;

    /** หันไปทิศ d แล้วลองเดิน 1 ช่อง คืน true ถ้าเดินได้ */
    public boolean tryMove(Direction d, TileMap map) {
        facing = d;
        int nx = x + d.dx;
        int ny = y + d.dy;
        if (!map.canEnter(nx, ny)) return false;
        x = nx;
        y = ny;
        return true;
    }

    public void face(Direction d) { facing = d; }
    public Direction getFacing() { return facing; }
}
