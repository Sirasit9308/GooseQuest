package com.goosequest.character;

import com.badlogic.gdx.graphics.Color;
import com.goosequest.item.Item;
import com.goosequest.item.PondWater;
import com.goosequest.util.Dice;

/** [คนที่ 1] คนสวน: ฉีดน้ำใส่ */
public class Gardener extends Enemy {
    private static final Color COLOR = new Color(0.2f, 0.55f, 0.3f, 1f);

    public Gardener() {
        super("Gardener", 45, 11, 3, 25);
    }

    @Override
    public String takeTurn(Character target) {
        if (Dice.chance(0.25)) {
            int dmg = target.takeDamage(Dice.vary(getAtk() + 5));
            return getName() + " sprays the HOSE! " + dmg + " damage!";
        }
        return super.takeTurn(target);
    }

    @Override
    public Item rollDrop() {
        return Dice.chance(0.5) ? new PondWater() : null;
    }

    @Override
    public Color getColor() { return COLOR; }
}
