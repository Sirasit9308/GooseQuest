package com.goosequest.skill;

import com.goosequest.character.Character;
import com.goosequest.util.Dice;

/** [คนที่ 1] ฟาดเงา (สกิลที่ 2 ของนินจา): ดาเมจ 2 เท่าและทะลุพลังป้องกันของศัตรู */
public class ShadowStrike implements Skill {
    @Override
    public String getName() { return "Shadow Strike"; }

    @Override
    public int getMpCost() { return 10; }

    @Override
    public String use(Character user, Character target) {
        // ignoreDef = true: ดาเมจไม่ถูกลดด้วย DEF (คริติคอลคูณเฉพาะดาเมจดิบ)
        int dmg = user.dealDamage(target, Dice.vary(user.getAtk() * 2), true);
        return user.getName() + " uses SHADOW STRIKE! Ignores defense! " + dmg + " damage!";
    }
}
