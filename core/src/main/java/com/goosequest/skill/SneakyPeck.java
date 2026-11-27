package com.goosequest.skill;

import com.goosequest.character.Character;
import com.goosequest.util.Dice;

/** [คนที่ 1] จิกลอบ: 50% ติดคริ 3 เท่า (คริของสกิลเอง ไม่ซ้อนกับคริติคอลทั่วไป) */
public class SneakyPeck implements Skill {
    public static final double CRIT_CHANCE = 0.5;

    @Override
    public String getName() { return "Sneaky Peck"; }

    @Override
    public int getMpCost() { return 6; }

    @Override
    public String use(Character user, Character target) {
        boolean crit = Dice.chance(CRIT_CHANCE);
        int dmg = target.takeDamage(Dice.vary(user.getAtk() * (crit ? 3 : 1)), crit);
        return user.getName() + " uses SNEAKY PECK! " + (crit ? "CRITICAL! " : "") + dmg + " damage!";
    }
}
