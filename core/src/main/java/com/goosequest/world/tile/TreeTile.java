package com.goosequest.world.tile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.goosequest.util.Art;
import com.goosequest.util.Palette;

public class TreeTile extends Tile {
    @Override
    public boolean isWalkable() { return false; }

    @Override
    public Color getColor() { return Palette.LEAF; }

    /** ต้นไม้ 3 ชั้น (เข้ม-กลาง-สว่าง) + เงาบนพื้น ขนาดและตำแหน่งต่างกันเล็กน้อยแต่ละต้น */
    @Override
    public void draw(ShapeRenderer s, float px, float py, float t) {
        int h = Art.hash((int) (px / t) + 17, (int) (py / t) + 3);
        float cx = px + t / 2 + (h % 3 - 1) * 1.5f;
        float size = 1f + ((h >> 3) % 3) * 0.04f;

        GrassTile.paint(s, px, py, t, false);
        Art.shadow(s, cx, py + 4, t * 0.8f);
        s.setColor(Palette.TRUNK);
        s.rect(cx - t * 0.08f, py + 3, t * 0.16f, t * 0.32f);
        s.setColor(Palette.LEAF_DARK);
        s.circle(cx, py + t * 0.58f, t * 0.4f * size);
        s.setColor(Palette.LEAF);
        s.circle(cx - t * 0.04f, py + t * 0.63f, t * 0.32f * size);
        s.setColor(Palette.LEAF_LIGHT);
        s.circle(cx - t * 0.12f, py + t * 0.72f, t * 0.13f * size);
    }
}
