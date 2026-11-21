package com.goosequest.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.goosequest.state.ExploreState;
import com.goosequest.util.Art;

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
        s.setColor(color);
        s.ellipse(px + 5, py + 4, t - 10, t * 0.5f);
        s.circle(px + t * 0.65f, py + t * 0.7f, t * 0.18f);
        s.setColor(Art.BEAK);
        s.rect(px + t * 0.78f, py + t * 0.64f, t * 0.16f, t * 0.08f);
        s.setColor(Color.BLACK);
        s.circle(px + t * 0.7f, py + t * 0.75f, 1.5f);
    }

    public String getName() { return name; }
}
