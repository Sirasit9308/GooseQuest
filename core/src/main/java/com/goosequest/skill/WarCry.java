package com.goosequest.skill;

import com.goosequest.character.Character;
import com.goosequest.util.Dice;

/**
 * [คนที่ 1] เสียงศึก (สกิลที่ 2 ของนักรบ): ATK +50% นาน 3 เทิร์น (ไม่นับเทิร์นที่ใช้)
 * ใช้ซ้ำขณะบัฟยังอยู่ = ไม่ซ้อนกัน แต่เริ่มนับ 3 เทิร์นใหม่
 */
public class WarCry implements Skill {
    public static final String BUFF_ID = "war-cry";
    public static final int DURATION = 3;
    public static final double BONUS_RATE = 0.5;

    @Override
    public String getName() { return "War Cry"; }

    @Override
    public int getMpCost() { return 7; }

    @Override
    public String use(Character user, Character target) {
        boolean refreshed = user.removeTimedBuff(BUFF_ID);
        // เอาบัฟเดิมออกก่อน จึงคิด +50% จาก ATK ปกติเสมอ (ไม่ซ้อนเมื่อใช้ซ้ำ) ปัดลงขั้นต่ำ 1
        int bonus = Dice.floorMin1(user.getAtk() * BONUS_RATE);
        user.applyTimedBuff(BUFF_ID, bonus, 0, DURATION);
        return user.getName() + " uses WAR CRY! ATK +50% for " + DURATION + " turns!"
                + (refreshed ? " (duration refreshed)" : "");
    }
}
