package com.goosequest.character;

import com.goosequest.skill.ShadowStrike;
import com.goosequest.skill.SneakyPeck;

/** [คนที่ 1] สายนินจา: ตีแรง ติดคริ แต่บาง */
public class ThiefGoose extends Goose {
    public ThiefGoose() {
        super("Shadow Quack", 85, 30, 16, 2, new SneakyPeck());
    }

    @Override
    protected void onLevelUp() {
        growStats(8, 3, 4, 1);
        if (getLevel() == SECOND_SKILL_LEVEL) learnSkill(new ShadowStrike());
    }

    /** Thief คริง่ายกว่าสายอื่น */
    @Override
    public double getCritChance() { return 0.10; }

    @Override
    public String getClassName() { return "Thief"; }
}
