package com.goosequest.skill;

import com.goosequest.character.Character;
import com.goosequest.util.Dice;

/** [คนที่ 1] ร้องสุดเสียง: ดาเมจ 1.5 เท่า + 50% ศัตรูมึน 1 เทิร์น */
public class MegaHonk implements Skill {
    @Override
    public String getName() { return "Mega Honk"; }

    @Override
    public int getMpCost() { return 8; }

    @Override
    public String use(Character user, Character target) {
        int dmg = user.dealDamage(target, Dice.vary(Dice.floorMin1(user.getAtk() * 1.5)));
        String msg = user.getName() + ": HOOOOONK!!! " + dmg + " damage!";
        if (Dice.chance(0.5)) {
            target.setStunned(true);
            msg += " " + target.getName() + " is stunned!";
        }
        return msg;
    }
}
