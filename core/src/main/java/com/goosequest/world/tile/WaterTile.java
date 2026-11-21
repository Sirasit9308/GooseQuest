package com.goosequest.world.tile;

import com.badlogic.gdx.graphics.Color;

/** น้ำ: ตอนนี้เดินไม่ได้ (ไอเดียต่อยอด: ให้ห่านว่ายน้ำได้หลังได้ไอเทมพิเศษ) */
public class WaterTile extends Tile {
    private static final Color COLOR = new Color(0.25f, 0.5f, 0.9f, 1f);

    @Override
    public boolean isWalkable() { return false; }

    @Override
    public Color getColor() { return COLOR; }
}
