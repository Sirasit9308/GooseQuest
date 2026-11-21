package com.goosequest.world.tile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.goosequest.util.Art;
import com.goosequest.util.Palette;

/** น้ำ: ตอนนี้เดินไม่ได้ (ไอเดียต่อยอด: ให้ห่านว่ายน้ำได้หลังได้ไอเทมพิเศษ) */
public class WaterTile extends Tile {
    @Override
    public boolean isWalkable() { return false; }

    @Override
    public Color getColor() { return Palette.WATER; }

    /** น้ำมีคลื่นเล็ก ๆ เลื่อนไปมาตามเวลา (ริมตลิ่งวาดใน TileMap) */
    @Override
    public void draw(ShapeRenderer s, float px, float py, float t) {
        int h = Art.hash((int) (px / t) + 31, (int) (py / t) + 57);
        float time = Art.time();
        s.setColor(Palette.WATER);
        s.rect(px, py, t, t);
        if (h % 3 == 0) {
            s.setColor(Palette.WATER_DEEP);
            s.ellipse(px + 4 + h % 10, py + 6 + (h >> 4) % 12, 14, 6);
        }
        s.setColor(Palette.WATER_LIGHT);
        float phase = (h % 628) / 100f;
        float w1 = (float) Math.sin(time * 1.6f + phase) * 3f;
        float w2 = (float) Math.sin(time * 1.3f + phase + 2f) * 3f;
        s.rect(px + 6 + w1, py + t * 0.66f, 8, 1.5f);
        s.rect(px + 16 + w2, py + t * 0.3f, 7, 1.5f);
    }
}
