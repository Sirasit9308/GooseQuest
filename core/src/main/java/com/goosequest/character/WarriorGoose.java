package com.goosequest.character;

import com.goosequest.skill.WingSlap;

/** [คนที่ 1] สายนักรบ: HP และพลังป้องกันสูง */
public class WarriorGoose extends Goose {
    public WarriorGoose() {
        super("Sir Honksalot", 120, 20, 14, 5, new WingSlap());
    }

    @Override
    protected void onLevelUp() {
        growStats(15, 2, 3, 1);
    }

    @Override
    public String getClassName() { return "Warrior"; }
}
