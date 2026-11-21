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
        Art.goose(shapes, x * t, y * t, t / 32f, facing == Direction.LEFT);
    }
}
