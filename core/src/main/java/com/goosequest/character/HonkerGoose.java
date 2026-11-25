package com.goosequest.character;

import com.goosequest.skill.MegaHonk;
import com.goosequest.skill.SoothingSong;

/** [คนที่ 1] สายนักร้อง: MP เยอะ ร้องจนศัตรูมึน */
public class HonkerGoose extends Goose {
    public HonkerGoose() {
        super("Honkzilla", 100, 40, 12, 3, new MegaHonk());
    }

    @Override
    protected void onLevelUp() {
        growStats(12, 6, 3, 1);
        if (getLevel() == SECOND_SKILL_LEVEL) learnSkill(new SoothingSong());
    }

    @Override
    public String getClassName() { return "Honker"; }
}
