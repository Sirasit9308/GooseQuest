package com.goosequest.character;

import com.badlogic.gdx.graphics.Color;
import com.goosequest.util.Dice;

/** [คนที่ 1] ไก่ดุ: บางครั้งจิกรัว 2 ที */
public class Chicken extends Enemy {
    private static final Color COLOR = new Color(0.95f, 0.85f, 0.6f, 1f);

    public Chicken() {
        super("Angry Chicken", 22, 7, 0, 10);
    }

    @Override
    public String takeTurn(Character target) {
        if (Dice.chance(0.3)) {
            int d1 = target.takeDamage(Dice.vary(getAtk()));
            int d2 = target.takeDamage(Dice.vary(getAtk()));
            return getName() + " uses DOUBLE PECK! " + d1 + " + " + d2 + " damage!";
        }
        return super.takeTurn(target);
    }

    @Override
    public Color getColor() { return COLOR; }
}
