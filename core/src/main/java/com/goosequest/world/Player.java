package com.goosequest.world;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.goosequest.util.Art;

/**
 * [คนที่ 3] ตัวห่านบนแผนที่ (ส่วนการเดิน)
 * ค่าสถานะต่อสู้อยู่ใน Goose แยกกัน เพราะเป็นคนละหน้าที่
 *
 * เดินอิสระ (ไม่ติดช่อง) รวมถึงเดินเฉียง: เก็บตำแหน่งเป็นทศนิยมหน่วย "ช่อง"
 * ส่วน getX()/getY() ยังคืนเลขช่องที่ห่านยืนอยู่ ให้โค้ดส่วนอื่นใช้ต่อได้
 */
public class Player extends Entity {
    /** ความเร็วเดิน (ช่องต่อวินาที) */
    public static final float SPEED = 4.5f;
    /** ครึ่งกว้าง/ครึ่งสูงของ hitbox ที่เท้า (หน่วยช่อง) */
    public static final float HIT_HX = 0.3f;
    public static final float HIT_HY = 0.25f;
    /** hitbox อยู่ต่ำกว่ากึ่งกลางรูปห่านเล็กน้อย (เท้า) */
    private static final float HIT_OFFSET_Y = -0.1f;
    /** เดินทีละไม่เกินเท่านี้ต่อรอบ กันทะลุกำแพงตอนเฟรมช้า */
    private static final float MAX_STEP = 0.05f;

    private float fx = 0.5f; // กึ่งกลางรูปห่าน หน่วยช่อง
    private float fy = 0.5f;

    @Override
    public void setPosition(int x, int y) {
        fx = x + 0.5f;
        fy = y + 0.5f;
        syncTile();
    }

    /**
     * เดินตามทิศที่กด (inX, inY เป็น -1/0/1) ทิศเฉียงจะถูกปรับให้เร็วเท่าทิศตรง
     * ชนกำแพงแล้วจะไถลไปตามกำแพงได้ (แยกเช็กแกน X กับ Y)
     */
    public void move(float inX, float inY, float dt, TileMap map) {
        if (inX == 0 && inY == 0) return;
        float len = (float) Math.sqrt(inX * inX + inY * inY);
        inX /= len;
        inY /= len;

        facing = Math.abs(inX) >= Math.abs(inY)
                ? (inX < 0 ? Direction.LEFT : Direction.RIGHT)
                : (inY < 0 ? Direction.DOWN : Direction.UP);

        float dist = SPEED * dt;
        int steps = Math.max(1, (int) Math.ceil(dist / MAX_STEP));
        float sx = inX * dist / steps;
        float sy = inY * dist / steps;
        for (int i = 0; i < steps; i++) {
            tryShift(sx, 0, map);
            tryShift(0, sy, map);
        }
        syncTile();
    }

    private void tryShift(float dx, float dy, TileMap map) {
        if (dx == 0 && dy == 0) return;
        float nx = fx + dx;
        float ny = fy + dy;
        if (map.canStand(nx, ny + HIT_OFFSET_Y, HIT_HX, HIT_HY, fx, fy + HIT_OFFSET_Y)) {
            fx = nx;
            fy = ny;
        }
    }

    /** หันหน้าไปทางจุด (cx, cy) หน่วยช่อง */
    public void faceToward(float cx, float cy) {
        float dx = cx - fx;
        float dy = cy - fy;
        facing = Math.abs(dx) >= Math.abs(dy)
                ? (dx < 0 ? Direction.LEFT : Direction.RIGHT)
                : (dy < 0 ? Direction.DOWN : Direction.UP);
    }

    private void syncTile() {
        x = (int) Math.floor(fx);
        y = (int) Math.floor(fy + HIT_OFFSET_Y);
    }

    /** จุดกึ่งกลาง hitbox (ที่เท้า) หน่วยช่อง */
    public float getHitCenterX() { return fx; }
    public float getHitCenterY() { return fy + HIT_OFFSET_Y; }

    @Override
    public float getCenterX() { return fx; }

    @Override
    public float getCenterY() { return fy; }

    @Override
    public float getDrawY() { return fy - 0.5f; }

    @Override
    public void draw(ShapeRenderer shapes, float t) {
        float px = (fx - 0.5f) * t;
        float py = (fy - 0.5f) * t;
        Art.shadow(shapes, px + t / 2, py + 2, t * 0.75f);
        Art.goose(shapes, px, py + 1 + Art.bob(0f, 1f), t / 32f, facing == Direction.LEFT);
    }
}
