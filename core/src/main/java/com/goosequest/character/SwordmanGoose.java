package com.goosequest.character;

import com.goosequest.skill.WarCry;
import com.goosequest.skill.WingSlap;

/** [คนที่ 1] สายนักรบ: HP และพลังป้องกันสูง */
public class SwordmanGoose extends Goose {
    public SwordmanGoose() {
        super("Sir Honksalot", 120, 20, 14, 5, new WingSlap());
    }

    @Override
    protected void onLevelUp() {
        growStats(15, 2, 3, 1);
        if (getLevel() == SECOND_SKILL_LEVEL) learnSkill(new WarCry());
    }

    @Override
    public String getClassName() { return "Swordman"; }
}
