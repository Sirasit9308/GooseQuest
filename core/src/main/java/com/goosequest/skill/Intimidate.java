package com.goosequest.skill;

import com.goosequest.character.Character;
import com.goosequest.character.StatusEffect;

/**
 * [คนที่ 1] ขู่ (สกิลที่ 2 ของนักร้อง): ไม่ทำดาเมจ แปะ Weakness และ Vulnerable ให้ศัตรู นาน 3 เทิร์น
 * (ไม่นับเทิร์นที่ใช้) ใช้ซ้ำขณะยังติดอยู่ = ไม่ซ้อนกัน แต่เริ่มนับ 3 เทิร์นใหม่
 */
public class Intimidate implements Skill {
    public static final int DURATION = 3;

    @Override
    public String getName() { return "Intimidate"; }

    @Override
    public int getMpCost() { return 10; }

    @Override
    public String use(Character user, Character target) {
        target.applyStatus(StatusEffect.WEAKNESS, DURATION);
        target.applyStatus(StatusEffect.VULNERABLE, DURATION);
        return user.getName() + " INTIMIDATES " + target.getName() + "! WEAKNESS and VULNERABLE for "
                + DURATION + " turns!";
    }
}
