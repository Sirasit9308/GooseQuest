package com.goosequest.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.goosequest.item.Item;
import com.goosequest.state.ExploreState;

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

    @Override
    public void draw(ShapeRenderer s, float t) {
        float cx = x * t + t / 2;
        float cy = y * t + t / 2;
        float r = t * 0.25f;
        s.setColor(Color.GOLD);
        s.triangle(cx, cy + r, cx - r, cy, cx + r, cy);
        s.triangle(cx, cy - r, cx - r, cy, cx + r, cy);
    }

    public Item getItem() { return item; }
}
