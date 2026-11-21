package com.goosequest.character;

import com.badlogic.gdx.graphics.Color;
import com.goosequest.item.Bread;
import com.goosequest.item.Item;
import com.goosequest.util.Dice;

/** [คนที่ 1] หมาเฝ้าฟาร์ม */
public class Dog extends Enemy {
    private static final Color COLOR = new Color(0.6f, 0.4f, 0.2f, 1f);

    public Dog() {
        super("Farm Dog", 30, 9, 1, 15);
    }

    @Override
    public Item rollDrop() {
        return Dice.chance(0.5) ? new Bread() : null;
    }

    @Override
    public Color getColor() { return COLOR; }
}
