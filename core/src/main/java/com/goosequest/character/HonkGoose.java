package com.goosequest.character;

import com.goosequest.skill.MegaHonk;
import com.goosequest.skill.Intimidate;

/** [คนที่ 1] สายนักร้อง: MP เยอะ ร้องจนศัตรูมึน */
public class HonkGoose extends Goose {
    public HonkGoose() {
        super("Honkzilla", 100, 40, 12, 3, new MegaHonk());
    }

    @Override
    protected void onLevelUp() {
        growStats(12, 6, 3, 1);
        if (getLevel() == SECOND_SKILL_LEVEL) learnSkill(new Intimidate());
    }

    @Override
    public String getClassName() { return "Honk"; }
}
