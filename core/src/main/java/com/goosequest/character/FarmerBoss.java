package com.goosequest.character;

import com.badlogic.gdx.graphics.Color;
import com.goosequest.util.Dice;

/** [คนที่ 1] บอส: ชาวนาขโมยขนมปัง มี 2 เฟส หนีไม่ได้ */
public class FarmerBoss extends Enemy {
    private static final Color COLOR = new Color(0.75f, 0.15f, 0.15f, 1f);
    private static final int ENRAGE_ATK = 6;
    private boolean enraged = false;

    public FarmerBoss() {
        super("THE FARMER", 300, 22, 6, 100);
    }

    @Override
    public String takeTurn(Character target) {
        // เฟส 2: เลือดต่ำกว่าครึ่ง → โกรธ พลังโจมตีเพิ่ม (ใช้ 1 เทิร์น)
        if (!enraged && getHp() < getMaxHp() / 2) {
            enraged = true;
            buffAtk(ENRAGE_ATK);
            return getName() + " is FURIOUS! Pitchfork Fury! (ATK up)";
        }
        if (Dice.chance(enraged ? 0.4 : 0.2)) {
            int dmg = target.takeDamage(Dice.vary(getAtk() * 3 / 2));
            return getName() + " uses PITCHFORK SWEEP! " + dmg + " damage!";
        }
        return super.takeTurn(target);
    }

    /** บอสหนีไม่ได้ในเกมปกติ แต่ถ้ามีการรีเซ็ต ต้องเลิกโกรธและเอาโบนัส ATK ที่ได้ตอนโกรธคืนด้วย */
    @Override
    public void resetAfterFlee() {
        super.resetAfterFlee();
        if (enraged) {
            enraged = false;
            buffAtk(-ENRAGE_ATK);
        }
    }

    @Override
    public boolean canFlee() { return false; }

    @Override
    public boolean isBoss() { return true; }

    @Override
    public Color getColor() { return COLOR; }
}
