package com.goosequest.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.goosequest.item.Item;
import com.goosequest.state.ExploreState;
import com.goosequest.util.Art;
import com.goosequest.util.Palette;

/** [คนที่ 3] ไอเทมที่วางอยู่บนพื้น เดินชนแล้วเก็บ */
public class ItemPickup extends GameObject implements Interactable {
    private final Item item;

    public ItemPickup(Item item) {
        this.item = item;
    }

    @Override
    public void interact(ExploreState state) {
        state.pickUp(this);
    }

    /** เพชรสีทองลอยขึ้นลง มีเงาและประกาย */
    @Override
    public void draw(ShapeRenderer s, float t) {
        float time = Art.time();
        float lift = (float) Math.sin(time * 2.5f + x) * 2f;
        float cx = x * t + t / 2;
        float cy = y * t + t / 2 + 2 + lift;
        float r = t * 0.24f;
        Art.shadow(s, cx, y * t + 5, t * (0.42f - lift * 0.02f));
        s.setColor(Palette.OUTLINE);
        diamond(s, cx, cy, r + 1.5f);
        s.setColor(Palette.GOLD);
        diamond(s, cx, cy, r);
        s.setColor(Palette.GOLD_DARK);
        s.triangle(cx, cy - r, cx - r, cy, cx + r, cy);
        s.setColor(Color.WHITE);
        s.triangle(cx - r * 0.55f, cy + r * 0.05f, cx - r * 0.1f, cy + r * 0.5f, cx - r * 0.2f, cy + r * 0.05f);
        // ประกายกะพริบเป็นช่วง ๆ
        float tw = (float) Math.sin(time * 3f + x * 1.3f + y);
        if (tw > 0.6f) {
            float k = (tw - 0.6f) * 10f;
            s.rect(cx + r * 0.7f - k / 2, cy + r * 0.8f, k, 1.5f);
            s.rect(cx + r * 0.7f - 0.75f, cy + r * 0.8f - k / 2 + 0.75f, 1.5f, k);
        }
    }

    private static void diamond(ShapeRenderer s, float cx, float cy, float r) {
        s.triangle(cx, cy + r, cx - r, cy, cx + r, cy);
        s.triangle(cx, cy - r, cx - r, cy, cx + r, cy);
    }

    public Item getItem() { return item; }
}
