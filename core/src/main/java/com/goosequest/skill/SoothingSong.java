package com.goosequest.skill;

import com.goosequest.character.Character;
import com.goosequest.util.Dice;

/** [คนที่ 1] เพลงกล่อม (สกิลที่ 2 ของนักร้อง): ไม่ทำดาเมจ ฟื้น HP ผู้ใช้ 30% ของ HP สูงสุด */
public class SoothingSong implements Skill {
    @Override
    public String getName() { return "Soothing Song"; }

    @Override
    public int getMpCost() { return 10; }

    @Override
    public String use(Character user, Character target) {
        int healed = user.heal(Dice.floorMin1(user.getMaxHp() * 0.3));
        return user.getName() + " sings a SOOTHING SONG! Recovered " + healed + " HP!";
    }
}
