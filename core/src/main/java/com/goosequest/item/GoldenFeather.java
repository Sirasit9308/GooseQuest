package com.goosequest.item;

import com.goosequest.character.Character;

/** [คนที่ 2] ขนนกทองคำ: เพิ่มพลังโจมตีถาวร */
public class GoldenFeather extends Item {
    private static final int BONUS = 4;

    public GoldenFeather() {
        super("Golden Feather", "ATK +" + BONUS + " permanently");
    }

    @Override
    public String use(Character target) {
        target.buffAtk(BONUS);
        return target.getName() + " feels mighty! ATK +" + BONUS + "!";
    }
}
