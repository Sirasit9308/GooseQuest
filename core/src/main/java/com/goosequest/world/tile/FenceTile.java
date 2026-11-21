package com.goosequest.world.tile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.goosequest.util.Palette;

public class FenceTile extends Tile {
    @Override
    public boolean isWalkable() { return false; }

    @Override
    public Color getColor() { return Palette.WOOD; }

    /** รั้วไม้: เสา 2 ต้น ราว 2 เส้น มีเงาและด้านล่างสีเข้มให้ดูนูน */
    @Override
    public void draw(ShapeRenderer s, float px, float py, float t) {
        GrassTile.paint(s, px, py, t, false);
        s.setColor(Palette.SHADOW);
        s.rect(px, py + 2, t, 3);
        // ราว
        rail(s, px, py + t * 0.25f, t);
        rail(s, px, py + t * 0.6f, t);
        // เสา
        post(s, px + t * 0.1f, py + 2, t);
        post(s, px + t * 0.75f, py + 2, t);
    }

    private static void rail(ShapeRenderer s, float px, float y, float t) {
        s.setColor(Palette.WOOD_DARK);
        s.rect(px, y - 1, t, t * 0.14f);
        s.setColor(Palette.WOOD);
        s.rect(px, y + 1, t, t * 0.1f);
    }

    private static void post(ShapeRenderer s, float x, float y, float t) {
        float w = t * 0.15f;
        s.setColor(Palette.WOOD_DARK);
        s.rect(x - 1, y, w + 2, t * 0.85f);
        s.setColor(Palette.WOOD);
        s.rect(x, y + 1, w, t * 0.82f);
        s.setColor(Palette.WOOD_DARK);
        s.rect(x, y + t * 0.83f - 2, w, 2);
    }
}
