package com.goosequest.world;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.goosequest.util.Art;

/**
 * [คนที่ 3] ตัวห่านบนแผนที่ (ส่วนการเดิน)
 * ค่าสถานะต่อสู้อยู่ใน Goose แยกกัน เพราะเป็นคนละหน้าที่
 */
public class Player extends Entity {
    @Override
    public void draw(ShapeRenderer shapes, float t) {
        float px = x * t;
        float py = y * t;
        Art.shadow(shapes, px + t / 2, py + 2, t * 0.75f);
        Art.goose(shapes, px, py + 1 + Art.bob(0f, 1f), t / 32f, facing == Direction.LEFT);
    }
}
