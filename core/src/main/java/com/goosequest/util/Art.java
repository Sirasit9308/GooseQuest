package com.goosequest.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * วาดตัวละครด้วยรูปทรงง่าย ๆ (ยังไม่ต้องมีไฟล์รูป)
 * ภายหลังเปลี่ยนเป็น Texture / sprite sheet ได้โดยแก้แค่ไฟล์นี้
 * ต้องเรียกระหว่าง shapes.begin(ShapeType.Filled) ... shapes.end()
 */
public final class Art {
    public static final Color BEAK = Palette.BEAK;

    /** เวลาที่ผ่านไปตั้งแต่เปิดเกม (วินาที) ใช้ทำอนิเมชันเล็ก ๆ เช่นน้ำกระเพื่อม ตัวละครขยับ */
    private static float time = 0f;

    private Art() {}

    // ---------- เวลา / ตัวช่วย ----------

    /** GameManager เรียกทุกเฟรม */
    public static void tick(float dt) { time += dt; }

    public static float time() { return time; }

    /** ขยับขึ้นลงเบา ๆ ตอนยืนเฉย คืนค่าเป็น pixel (ปัดเป็นจำนวนเต็มให้ภาพคม) */
    public static float bob(float phase, float amount) {
        return Math.round((float) Math.sin(time * 3.2f + phase) * amount);
    }

    /** ตัวเลขสุ่มแบบคงที่ตามตำแหน่ง: ช่องเดิมได้ค่าเดิมทุกเฟรม ใช้สุ่มลายหญ้า/ดอกไม้ */
    public static int hash(int a, int b) {
        int h = a * 73856093 ^ b * 19349663;
        h ^= h >>> 13;
        h *= 0x5bd1e995;
        h ^= h >>> 15;
        return h & 0x7fffffff;
    }

    /** เปิดความโปร่งใส ต้องเรียกก่อน shapes.begin() ถ้าจะวาดเงาหรือสีจาง */
    public static void enableBlend() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    /** เงาวงรีใต้ตัวละคร (cx = กึ่งกลาง, y = พื้น) */
    public static void shadow(ShapeRenderer s, float cx, float y, float w) {
        s.setColor(Palette.SHADOW);
        s.ellipse(cx - w / 2, y - w * 0.12f, w, w * 0.3f);
    }

    /** สี่เหลี่ยมมุมมน */
    public static void roundRect(ShapeRenderer s, float x, float y, float w, float h, float r) {
        r = Math.min(r, Math.min(w, h) / 2);
        s.rect(x + r, y, w - 2 * r, h);
        s.rect(x, y + r, w, h - 2 * r);
        s.circle(x + r, y + r, r);
        s.circle(x + w - r, y + r, r);
        s.circle(x + r, y + h - r, r);
        s.circle(x + w - r, y + h - r, r);
    }

    // ---------- ตัวละคร ----------

    /** วาดห่านในกรอบขนาด 32*scale (มีเส้นขอบเข้ม ปีก และหางเพื่อให้ดูมีมิติ) */
    public static void goose(ShapeRenderer s, float x, float y, float scale, boolean faceLeft) {
        float k = scale;
        float o = Math.max(1f, k); // ความหนาเส้นขอบ
        float dir = faceLeft ? -1 : 1;
        float neckX = faceLeft ? x + 7 * k : x + 19 * k;
        float hx = neckX + 3 * k;
        float hy = y + 25 * k;
        float tailX = faceLeft ? x + 27 * k : x + 5 * k;

        // ชั้นที่ 1: เส้นขอบ (วาดรูปเดิมให้ใหญ่ขึ้นนิดหนึ่งด้วยสีเข้ม)
        s.setColor(Palette.OUTLINE);
        s.rect(x + 10 * k - o, y - o, 4 * k + 2 * o, 4 * k + 2 * o);
        s.rect(x + 17 * k - o, y - o, 4 * k + 2 * o, 4 * k + 2 * o);
        s.ellipse(x + 4 * k - o, y + 3 * k - o, 24 * k + 2 * o, 14 * k + 2 * o);
        s.triangle(tailX - 4 * k * dir - o * dir, y + 15 * k + o, tailX + 3 * k * dir, y + 13 * k + o, tailX + 2 * k * dir, y + 7 * k);
        s.rect(neckX - o, y + 12 * k, 6 * k + 2 * o, 12 * k);
        s.circle(hx, hy, 5 * k + o);
        s.triangle(hx + 3 * k * dir, hy + 2 * k + o, hx + 3 * k * dir, hy - 2 * k - o, hx + 10 * k * dir + o * dir, hy);

        // ชั้นที่ 2: สีจริง
        s.setColor(Palette.BEAK);
        s.rect(x + 10 * k, y, 4 * k, 4 * k);
        s.rect(x + 17 * k, y, 4 * k, 4 * k);
        s.setColor(Palette.GOOSE);
        s.triangle(tailX - 4 * k * dir, y + 15 * k, tailX + 3 * k * dir, y + 13 * k, tailX + 2 * k * dir, y + 8 * k);
        s.ellipse(x + 4 * k, y + 3 * k, 24 * k, 14 * k);
        // ท้องสีเข้มขึ้นเล็กน้อย + ปีก
        s.setColor(Palette.GOOSE_SHADE);
        s.ellipse(x + 7 * k, y + 3 * k, 18 * k, 5 * k);
        s.ellipse(faceLeft ? x + 13 * k : x + 8 * k, y + 7 * k, 11 * k, 7 * k);
        s.setColor(Palette.GOOSE);
        s.ellipse(faceLeft ? x + 14 * k : x + 9 * k, y + 9 * k, 9 * k, 4 * k);
        // คอและหัว
        s.rect(neckX, y + 12 * k, 6 * k, 12 * k);
        s.circle(hx, hy, 5 * k);
        // ปาก
        s.setColor(Palette.BEAK);
        s.triangle(hx + 3 * k * dir, hy + 2 * k, hx + 3 * k * dir, hy - 2 * k, hx + 10 * k * dir, hy);
        // ตา + จุดสะท้อนแสง
        s.setColor(Palette.OUTLINE);
        s.circle(hx + 1.5f * k * dir, hy + 1.5f * k, 1.3f * k);
        if (k >= 2) {
            s.setColor(Color.WHITE);
            s.circle(hx + 1.9f * k * dir, hy + 2f * k, 0.45f * k);
        }
    }

    /** วาดศัตรูเป็นก้อนมุมมนพร้อมตาโกรธ มีเส้นขอบ เงาด้านล่าง และไฮไลต์ */
    public static void enemy(ShapeRenderer s, Color body, float x, float y, float size) {
        float m = size * 0.12f;
        float o = Math.max(1f, size / 32f);
        float bx = x + m, by = y + m, bw = size - 2 * m, bh = size - 2 * m;
        float r = size * 0.16f;

        // เท้า
        s.setColor(Palette.OUTLINE);
        s.rect(bx + bw * 0.18f, y + m * 0.35f, bw * 0.2f, m);
        s.rect(bx + bw * 0.62f, y + m * 0.35f, bw * 0.2f, m);
        // ขอบ + ตัว
        roundRect(s, bx - o, by - o, bw + 2 * o, bh + 2 * o, r + o);
        s.setColor(body);
        roundRect(s, bx, by, bw, bh, r);
        // ด้านล่างเข้มขึ้น ด้านบนสว่างขึ้น → ดูกลมมีมิติ
        s.setColor(body.r * 0.78f, body.g * 0.78f, body.b * 0.78f, 1f);
        roundRect(s, bx, by, bw, bh * 0.32f, r);
        s.setColor(Math.min(1f, body.r * 1.18f + 0.06f), Math.min(1f, body.g * 1.18f + 0.06f),
                Math.min(1f, body.b * 1.18f + 0.06f), 1f);
        roundRect(s, bx + bw * 0.12f, by + bh * 0.78f, bw * 0.3f, bh * 0.1f, bh * 0.05f);

        // ตา
        float eye = size * 0.12f;
        float ey = y + size * 0.58f;
        s.setColor(Color.WHITE);
        s.circle(x + size * 0.35f, ey, eye);
        s.circle(x + size * 0.65f, ey, eye);
        s.setColor(Palette.OUTLINE);
        s.circle(x + size * 0.37f, ey - eye * 0.2f, eye * 0.5f);
        s.circle(x + size * 0.63f, ey - eye * 0.2f, eye * 0.5f);
        // คิ้วโกรธ (เอียงเข้าหากลาง)
        float bw2 = size * 0.22f, bt = size * 0.06f, byy = ey + eye * 1.15f;
        s.rectLine(x + size * 0.2f, byy + bt * 1.2f, x + size * 0.2f + bw2, byy, bt);
        s.rectLine(x + size * 0.8f, byy + bt * 1.2f, x + size * 0.8f - bw2, byy, bt);
    }

    // ---------- UI ----------

    /** กล่องข้อความ (ขอบครีม พื้นน้ำเงินเข้ม มุมมน) */
    public static void box(ShapeRenderer s, float x, float y, float w, float h) {
        s.setColor(Palette.OUTLINE);
        roundRect(s, x - 1, y - 1, w + 2, h + 2, 8);
        s.setColor(Palette.UI_BORDER);
        roundRect(s, x, y, w, h, 7);
        s.setColor(Palette.UI_FILL);
        roundRect(s, x + 3, y + 3, w - 6, h - 6, 5);
        s.setColor(Palette.UI_FILL_LIGHT);
        s.rect(x + 8, y + h - 7, w - 16, 2);
    }

    /** หลอด HP/MP */
    public static void bar(ShapeRenderer s, float x, float y, float w, float h, float ratio, Color fill) {
        float rr = Math.max(0f, Math.min(1f, ratio));
        s.setColor(Palette.OUTLINE);
        roundRect(s, x - 1, y - 1, w + 2, h + 2, (h + 2) / 2);
        s.setColor(Palette.BAR_BG);
        roundRect(s, x, y, w, h, h / 2);
        if (rr > 0) {
            s.setColor(fill);
            roundRect(s, x, y, Math.max(h, w * rr), h, h / 2);
            s.setColor(1f, 1f, 1f, 0.28f);
            s.rect(x + h / 2, y + h * 0.6f, Math.max(0, w * rr - h), Math.max(1, h * 0.2f));
        }
    }

    /** ลูกศรชี้ตัวเลือกในเมนู ขยับไปมาเบา ๆ */
    public static void cursor(ShapeRenderer s, float x, float y, float size) {
        float dx = Math.round((float) Math.sin(time * 6f) * 2f);
        s.setColor(Palette.UI_CURSOR);
        s.triangle(x + dx, y, x + dx, y + size, x + dx + size * 0.8f, y + size / 2);
    }
}
