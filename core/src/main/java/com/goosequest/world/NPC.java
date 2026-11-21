package com.goosequest.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.goosequest.state.ExploreState;
import com.goosequest.util.Art;
import com.goosequest.util.Palette;

/** [คนที่ 2 + 3] ตัวละครที่คุยด้วยได้ */
public class NPC extends Entity implements Interactable {
    private final String name;
    private final Color color;
    private final String[] lines;

    public NPC(String name, Color color, String... lines) {
        this.name = name;
        this.color = color;
        this.lines = lines;
    }

    @Override
    public void interact(ExploreState state) {
        state.showDialogue(name, lines);
    }

    @Override
    public void draw(ShapeRenderer s, float t) {
        float px = x * t;
        float py = y * t;
        Art.shadow(s, px + t / 2, py + 3, t * 0.7f);
        py += Art.bob(x * 1.7f + y, 1f);
        float o = 1f;
        // เส้นขอบ
        s.setColor(Palette.OUTLINE);
        s.ellipse(px + 5 - o, py + 4 - o, t - 10 + 2 * o, t * 0.5f + 2 * o);
        s.circle(px + t * 0.65f, py + t * 0.7f, t * 0.18f + o);
        s.rect(px + t * 0.78f, py + t * 0.64f - o, t * 0.16f + o, t * 0.08f + 2 * o);
        // ตัว + ปีกสีเข้มขึ้น
        s.setColor(color);
        s.ellipse(px + 5, py + 4, t - 10, t * 0.5f);
        s.circle(px + t * 0.65f, py + t * 0.7f, t * 0.18f);
        s.setColor(color.r * 0.82f, color.g * 0.82f, color.b * 0.82f, 1f);
        s.ellipse(px + 8, py + 7, t * 0.4f, t * 0.22f);
        s.setColor(Art.BEAK);
        s.rect(px + t * 0.78f, py + t * 0.64f, t * 0.16f, t * 0.08f);
        s.setColor(Palette.OUTLINE);
        s.circle(px + t * 0.7f, py + t * 0.75f, 1.5f);
    }

    public String getName() { return name; }
}
