package com.goosequest.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * วาดตัวละครด้วยรูปทรงง่าย ๆ (ยังไม่ต้องมีไฟล์รูป)
 * ภายหลังเปลี่ยนเป็น Texture / sprite sheet ได้โดยแก้แค่ไฟล์นี้
 * ต้องเรียกระหว่าง shapes.begin(ShapeType.Filled) ... shapes.end()
 */
public final class Art {
    public static final Color BEAK = new Color(1f, 0.6f, 0.1f, 1f);

    private Art() {}

    /** วาดห่านในกรอบขนาด 32*scale */
    public static void goose(ShapeRenderer s, float x, float y, float scale, boolean faceLeft) {
        float k = scale;
        // เท้า
        s.setColor(BEAK);
        s.rect(x + 10 * k, y, 4 * k, 4 * k);
        s.rect(x + 17 * k, y, 4 * k, 4 * k);
        // ตัว
        s.setColor(Color.WHITE);
        s.ellipse(x + 4 * k, y + 3 * k, 24 * k, 14 * k);
        // คอและหัว
        float neckX = faceLeft ? x + 7 * k : x + 19 * k;
        s.rect(neckX, y + 12 * k, 6 * k, 12 * k);
        float hx = neckX + 3 * k;
        float hy = y + 25 * k;
        s.circle(hx, hy, 5 * k);
        // ปาก
        s.setColor(BEAK);
        float dir = faceLeft ? -1 : 1;
        s.triangle(hx + 3 * k * dir, hy + 2 * k, hx + 3 * k * dir, hy - 2 * k, hx + 10 * k * dir, hy);
        // ตา
        s.setColor(Color.BLACK);
        s.circle(hx + 1.5f * k * dir, hy + 1.5f * k, 1.2f * k);
    }

    /** วาดศัตรูเป็นก้อนสีพร้อมตาโกรธ */
    public static void enemy(ShapeRenderer s, Color body, float x, float y, float size) {
        float m = size * 0.12f;
        s.setColor(body);
        s.rect(x + m, y + m, size - 2 * m, size - 2 * m);
        float eye = size * 0.12f;
        float ey = y + size * 0.6f;
        s.setColor(Color.WHITE);
        s.circle(x + size * 0.35f, ey, eye);
        s.circle(x + size * 0.65f, ey, eye);
        s.setColor(Color.BLACK);
        s.circle(x + size * 0.37f, ey - eye * 0.2f, eye * 0.5f);
        s.circle(x + size * 0.63f, ey - eye * 0.2f, eye * 0.5f);
        // คิ้วโกรธ
        s.rect(x + size * 0.22f, ey + eye * 1.2f, size * 0.2f, size * 0.05f);
        s.rect(x + size * 0.58f, ey + eye * 1.2f, size * 0.2f, size * 0.05f);
    }

    /** กล่องข้อความ (ขอบขาว พื้นน้ำเงินเข้ม) */
    public static void box(ShapeRenderer s, float x, float y, float w, float h) {
        s.setColor(Color.WHITE);
        s.rect(x, y, w, h);
        s.setColor(0.08f, 0.1f, 0.22f, 1f);
        s.rect(x + 3, y + 3, w - 6, h - 6);
    }

    /** หลอด HP/MP */
    public static void bar(ShapeRenderer s, float x, float y, float w, float h, float ratio, Color fill) {
        s.setColor(0.25f, 0.25f, 0.25f, 1f);
        s.rect(x, y, w, h);
        s.setColor(fill);
        s.rect(x, y, w * Math.max(0f, Math.min(1f, ratio)), h);
    }
}
