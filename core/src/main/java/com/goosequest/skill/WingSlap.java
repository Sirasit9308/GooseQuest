package com.goosequest.skill;

import com.goosequest.character.Character;
import com.goosequest.util.Dice;

/** [คนที่ 1] ตบปีก: ดาเมจ 2 เท่า */
public class WingSlap implements Skill {
    @Override
    public String getName() { return "Wing Slap"; }

    @Override
    public int getMpCost() { return 6; }

    @Override
    public String use(Character user, Character target) {
        int dmg = target.takeDamage(Dice.vary(user.getAtk() * 2));
        return user.getName() + " uses WING SLAP! " + dmg + " damage!";
    }
}
