package com.goosequest.item;

import com.goosequest.character.Character;

/** [คนที่ 2] ขนมปัง: ฟื้น HP */
public class Bread extends Item {
    private static final int HEAL = 35;

    public Bread() {
        super("Bread", "Restores " + HEAL + " HP");
    }

    @Override
    public String use(Character target) {
        int healed = target.heal(HEAL);
        return target.getName() + " munches Bread. +" + healed + " HP!";
    }
}
