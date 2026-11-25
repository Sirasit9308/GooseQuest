package com.goosequest.world;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * [คนที่ 3] ทุกอย่างที่วางบนแผนที่ (ห่าน, NPC, ศัตรู, ไอเทม)
 * ตำแหน่งเป็นหน่วย "ช่อง" (tile) ไม่ใช่ pixel
 */
public abstract class GameObject {
    protected int x;
    protected int y;

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    /** จุดกึ่งกลางตัววัตถุ หน่วยช่อง (ทศนิยม) ใช้คำนวณระยะ/กล้อง */
    public float getCenterX() { return x + 0.5f; }
    public float getCenterY() { return y + 0.5f; }

    /** ค่า y ที่ใช้เรียงลำดับการวาด (ยิ่งต่ำยิ่งอยู่ใกล้กล้อง วาดทีหลัง) */
    public float getDrawY() { return y; }

    /** เดินทับได้ไหม (ค่าเริ่มต้น: ไม่ได้) */
    public boolean isSolid() { return true; }

    /** เรียกทุกเฟรม ใช้กับของที่ขยับเองได้ */
    public void update(float dt, TileMap map) {}

    /** วาดตัวเอง (ต้องอยู่ระหว่าง shapes.begin/end) */
    public abstract void draw(ShapeRenderer shapes, float tileSize);
}
