package com.goosequest.character;

import com.badlogic.gdx.graphics.Color;
import com.goosequest.util.Dice;

/** [คนที่ 1] งูเห่า: กัด (BITE) ทุกเทิร์น มีโอกาส 50% ทำให้ติด Poison 3 เทิร์น ไม่ดรอป */
public class Cobra extends Enemy {
    public static final int POISON_TURNS = 3;
    private static final double POISON_CHANCE = 0.5;
    private static final Color COLOR = new Color(0.35f, 0.65f, 0.25f, 1f);

    public Cobra() {
        super("Cobra", 60, 7, 1, 35);
    }

    @Override
    public String takeTurn(Character target) {
        int dmg = target.takeDamage(Dice.vary(getAtk()));
        String msg = getName() + " uses BITE! " + dmg + " damage!";
        if (Dice.chance(POISON_CHANCE) && target.applyStatus(StatusEffect.POISON, POISON_TURNS)) {
            msg += " POISONED for " + POISON_TURNS + " turns!";
        }
        return msg;
    }

    @Override
    public Color getColor() { return COLOR; }
}
