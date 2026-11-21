package com.goosequest.world.tile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.goosequest.util.Art;
import com.goosequest.util.Palette;

public class GrassTile extends Tile {
    @Override
    public boolean isWalkable() { return true; }

    @Override
    public Color getColor() { return Palette.GRASS; }

    @Override
    public void draw(ShapeRenderer s, float px, float py, float t) {
        paint(s, px, py, t, true);
    }

    /**
     * วาดพื้นหญ้า: สุ่มกอหญ้าและดอกไม้ตามตำแหน่ง แผนที่จึงไม่ดูเป็นตารางสีเดียว
     * static เพื่อให้ TreeTile / FenceTile ใช้วาดพื้นใต้ตัวเองได้
     */
    static void paint(ShapeRenderer s, float px, float py, float t, boolean flowers) {
        int h = Art.hash((int) (px / t), (int) (py / t));
        s.setColor(Palette.GRASS);
        s.rect(px, py, t, t);

        // จุดหญ้าสีอ่อน
        if (h % 4 == 0) {
            s.setColor(Palette.GRASS_LIGHT);
            s.rect(px + (h >> 3) % 22 + 4, py + (h >> 7) % 22 + 4, 3, 2);
        }
        // กอหญ้า 0-2 กอ
        int tufts = (h >> 11) % 3;
        s.setColor(Palette.GRASS_DARK);
        for (int i = 0; i < tufts; i++) {
            int hh = Art.hash(h, i);
            float gx = px + 3 + hh % (int) (t - 9);
            float gy = py + 3 + (hh >> 8) % (int) (t - 9);
            s.rect(gx, gy, 1.5f, 4);
            s.rect(gx + 2.5f, gy, 1.5f, 5.5f);
            s.rect(gx + 5, gy, 1.5f, 3.5f);
        }
        // ดอกไม้ (ประมาณ 1 ใน 9 ช่อง)
        if (flowers && (h >> 4) % 9 == 0) {
            Color[] petals = {Palette.FLOWER_WHITE, Palette.FLOWER_YELLOW, Palette.FLOWER_PINK};
            float fx = px + 8 + (h >> 9) % (int) (t - 16);
            float fy = py + 8 + (h >> 14) % (int) (t - 16);
            s.setColor(petals[(h >> 2) % 3]);
            s.circle(fx - 2, fy, 1.8f);
            s.circle(fx + 2, fy, 1.8f);
            s.circle(fx, fy - 2, 1.8f);
            s.circle(fx, fy + 2, 1.8f);
            s.setColor(Palette.GOLD_DARK);
            s.circle(fx, fy, 1.3f);
        }
    }
}
