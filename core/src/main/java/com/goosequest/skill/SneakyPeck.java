package com.goosequest.skill;

import com.goosequest.character.Character;
import com.goosequest.util.Dice;

/** [คนที่ 1] จิกลอบ: 40% ติดคริ 3 เท่า */
public class SneakyPeck implements Skill {
    @Override
    public String getName() { return "Sneaky Peck"; }

    @Override
    public int getMpCost() { return 6; }

    @Override
    public String use(Character user, Character target) {
        boolean crit = Dice.chance(0.4);
        int dmg = target.takeDamage(Dice.vary(user.getAtk() * (crit ? 3 : 1)));
        return user.getName() + " uses SNEAKY PECK! " + (crit ? "CRITICAL! " : "") + dmg + " damage!";
    }
}
