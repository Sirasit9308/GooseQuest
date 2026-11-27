package com.goosequest.skill;

import com.goosequest.character.Character;
import com.goosequest.character.StatusEffect;

/**
 * [คนที่ 1] เสียงศึก (สกิลที่ 2 ของนักรบ): มอบสถานะ Enrage (ATK +50%) ให้ตัวเอง นาน 3 เทิร์น
 * (ไม่นับเทิร์นที่ใช้) ใช้ซ้ำขณะยังติดอยู่ = ไม่ซ้อนกัน แต่เริ่มนับ 3 เทิร์นใหม่
 */
public class WarCry implements Skill {
    public static final int DURATION = 3;

    @Override
    public String getName() { return "War Cry"; }

    @Override
    public int getMpCost() { return 7; }

    @Override
    public String use(Character user, Character target) {
        boolean already = user.hasStatus(StatusEffect.ENRAGE);
        boolean applied = user.applyStatus(StatusEffect.ENRAGE, DURATION);
        return user.getName() + " uses WAR CRY and becomes ENRAGED! ATK +50% for " + DURATION + " turns!"
                + (already && applied ? " (duration refreshed)" : "");
    }
}
