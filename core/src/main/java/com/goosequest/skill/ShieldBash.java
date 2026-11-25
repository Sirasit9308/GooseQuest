package com.goosequest.skill;

import com.goosequest.character.Character;
import com.goosequest.util.Dice;

/** [คนที่ 1] กระแทกโล่ (สกิลที่ 2 ของนักรบ): ดาเมจเท่า ATK + 70% ศัตรูมึน 1 เทิร์น */
public class ShieldBash implements Skill {
    @Override
    public String getName() { return "Shield Bash"; }

    @Override
    public int getMpCost() { return 7; }

    @Override
    public String use(Character user, Character target) {
        int dmg = target.takeDamage(Dice.vary(user.getAtk()));
        String msg = user.getName() + " uses SHIELD BASH! " + dmg + " damage!";
        if (Dice.chance(0.7)) {
            target.setStunned(true);
            msg += " " + target.getName() + " is stunned!";
        }
        return msg;
    }
}
