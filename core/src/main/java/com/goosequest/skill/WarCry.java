package com.goosequest.skill;

import com.goosequest.character.Character;

/**
 * [คนที่ 1] เสียงศึก (สกิลที่ 2 ของนักรบ): ATK ×2 นาน 3 เทิร์น (ไม่นับเทิร์นที่ใช้)
 * ใช้ซ้ำขณะบัฟยังอยู่ = ไม่ซ้อนกัน แต่เริ่มนับ 3 เทิร์นใหม่
 */
public class WarCry implements Skill {
    public static final String BUFF_ID = "war-cry";
    public static final int DURATION = 3;

    @Override
    public String getName() { return "War Cry"; }

    @Override
    public int getMpCost() { return 7; }

    @Override
    public String use(Character user, Character target) {
        boolean refreshed = user.removeTimedBuff(BUFF_ID);
        // เอาบัฟเดิมออกก่อน จึงคิด ×2 จาก ATK ปกติเสมอ (ไม่เป็น ×3, ×4 เมื่อใช้ซ้ำ)
        int bonus = user.getAtk();
        user.applyTimedBuff(BUFF_ID, bonus, 0, DURATION);
        return user.getName() + " uses WAR CRY! ATK x2 for " + DURATION + " turns!"
                + (refreshed ? " (duration refreshed)" : "");
    }
}
