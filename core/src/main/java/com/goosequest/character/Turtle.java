package com.goosequest.character;

import com.badlogic.gdx.graphics.Color;
import com.goosequest.util.Dice;

/**
 * [คนที่ 1] เต่า: เทิร์นแรกบัฟ Durability ให้ตัวเอง (99 เทิร์น = ทั้งการต่อสู้)
 * เทิร์นอื่นโขกหัว (HEADBUTT) มีโอกาส 25% ทำให้ห่านสตัน ไม่ดรอป
 */
public class Turtle extends Enemy {
    public static final int DURABILITY_TURNS = 99;
    private static final double STUN_CHANCE = 0.25;
    private static final Color COLOR = new Color(0.3f, 0.55f, 0.45f, 1f);
    private boolean shelled = false;

    public Turtle() {
        super("Turtle", 100, 12, 20, 60);
    }

    @Override
    public String takeTurn(Character target) {
        if (!shelled) {
            shelled = true;
            applyStatus(StatusEffect.DURABILITY, DURABILITY_TURNS);
            return getName() + " hides in its shell! (DEF up)";
        }
        int dmg = target.takeDamage(Dice.vary(getAtk()));
        String msg = getName() + " uses HEADBUTT! " + dmg + " damage!";
        if (Dice.chance(STUN_CHANCE)) {
            target.setStunned(true);
            msg += " " + target.getName() + " is STUNNED!";
        }
        return msg;
    }

    /** ศัตรูกลับมาเหมือนยังไม่เคยสู้: ต้องบัฟ Durability ใหม่ในเทิร์นแรกของการต่อสู้รอบหน้า */
    @Override
    public void resetAfterFlee() {
        super.resetAfterFlee();
        shelled = false;
    }

    @Override
    public Color getColor() { return COLOR; }
}
