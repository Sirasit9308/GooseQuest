package com.goosequest.item;

import com.goosequest.character.Character;

/** [คนที่ 2] น้ำบ่อ: ฟื้น MP */
public class PondWater extends Item {
    private static final int AMOUNT = 15;

    public PondWater() {
        super("Pond Water", "Restores " + AMOUNT + " MP");
    }

    @Override
    public String use(Character target) {
        int got = target.restoreMp(AMOUNT);
        return target.getName() + " sips Pond Water. +" + got + " MP!";
    }
}
