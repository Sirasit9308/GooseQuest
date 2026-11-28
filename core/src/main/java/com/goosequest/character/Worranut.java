package com.goosequest.character;

import com.badlogic.gdx.graphics.Color;
import com.goosequest.item.Item;
import com.goosequest.item.PondWater;
import com.goosequest.util.Dice;

/**
 * [คนที่ 1] Worranut: ทุก 3 เทิร์น (เทิร์นที่ 1, 4, 7, ...) ใช้ LICK ดาเมจ 50% ATK
 * แปะ Weakness + Vulnerable 3 เทิร์น เทิร์นอื่นสุ่ม ตีธรรมดา 70% / TAIL SLAP (ATK x2) 30%
 * ดรอป PondWater เสมอ
 */
public class Worranut extends Enemy {
    public static final int LICK_EVERY = 3;
    public static final int LICK_TURNS = 3;
    private static final double TAIL_SLAP_CHANCE = 0.3;
    private static final Color COLOR = new Color(0.55f, 0.35f, 0.7f, 1f);
    /** จำนวนเทิร์นที่ออกท่าไปแล้วในการต่อสู้นี้ (เทิร์นที่โดนสตันไม่นับ เพราะไม่ได้ออกท่า) */
    private int turnsTaken = 0;

    public Worranut() {
        super("Worranut", 100, 11, 4, 55);
    }

    @Override
    public String takeTurn(Character target) {
        boolean lick = turnsTaken % LICK_EVERY == 0;
        turnsTaken++;
        if (lick) {
            int dmg = target.takeDamage(Dice.vary(Dice.floorMin1(getAtk() * 0.5)));
            target.applyStatus(StatusEffect.WEAKNESS, LICK_TURNS);
            target.applyStatus(StatusEffect.VULNERABLE, LICK_TURNS);
            return getName() + " uses LICK! " + dmg + " damage! WEAKNESS and VULNERABLE for "
                    + LICK_TURNS + " turns!";
        }
        if (Dice.chance(TAIL_SLAP_CHANCE)) {
            int dmg = target.takeDamage(Dice.vary(getAtk() * 2));
            return getName() + " uses TAIL SLAP! " + dmg + " damage!";
        }
        return super.takeTurn(target);
    }

    @Override
    public Item rollDrop() {
        return new PondWater();
    }

    /** ศัตรูกลับมาเหมือนยังไม่เคยสู้: เริ่มนับจาก LICK ใหม่ */
    @Override
    public void resetAfterFlee() {
        super.resetAfterFlee();
        turnsTaken = 0;
    }

    @Override
    public Color getColor() { return COLOR; }
}
