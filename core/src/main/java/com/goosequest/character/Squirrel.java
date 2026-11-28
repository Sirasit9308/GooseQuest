package com.goosequest.character;

import com.badlogic.gdx.graphics.Color;
import com.goosequest.item.Bread;
import com.goosequest.item.Item;

/** [คนที่ 1] กระรอก: ตัวเล็กเลือดน้อยแต่ตีแรง ตีธรรมดาอย่างเดียว ดรอปขนมปังเสมอ */
public class Squirrel extends Enemy {
    private static final Color COLOR = new Color(0.65f, 0.4f, 0.25f, 1f);

    public Squirrel() {
        super("Squirrel", 40, 15, 0, 10);
    }

    @Override
    public Item rollDrop() {
        return new Bread();
    }

    @Override
    public Color getColor() { return COLOR; }
}
