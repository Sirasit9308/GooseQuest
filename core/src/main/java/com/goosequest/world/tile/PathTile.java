package com.goosequest.world.tile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.goosequest.util.Art;
import com.goosequest.util.Palette;

public class PathTile extends Tile {
    @Override
    public boolean isWalkable() { return true; }

    @Override
    public Color getColor() { return Palette.PATH; }

    @Override
    public void draw(ShapeRenderer s, float px, float py, float t) {
        paint(s, px, py, t);
    }

    /** ทางดิน + ก้อนกรวดสุ่มตามตำแหน่ง (ขอบทางวาดใน TileMap เพราะต้องรู้ช่องข้าง ๆ) */
    static void paint(ShapeRenderer s, float px, float py, float t) {
        int h = Art.hash((int) (px / t) + 101, (int) (py / t) + 7);
        s.setColor(Palette.PATH);
        s.rect(px, py, t, t);
        s.setColor(Palette.PATH_DARK);
        s.rect(px + 4 + h % 20, py + 5 + (h >> 5) % 20, 3, 2);
        if ((h >> 10) % 2 == 0) s.rect(px + 6 + (h >> 12) % 18, py + 4 + (h >> 16) % 22, 2, 2);
        s.setColor(Palette.PATH_LIGHT);
        s.rect(px + 5 + (h >> 18) % 20, py + 6 + (h >> 22) % 18, 2, 1.5f);
    }
}
