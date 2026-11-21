package com.goosequest.character;

import com.badlogic.gdx.graphics.Color;
import com.goosequest.item.Item;

/**
 * [คนที่ 1] ศัตรู (abstract) แต่ละชนิดกำหนดสี ท่าโจมตี และของที่ดรอปเอง
 */
public abstract class Enemy extends Character {
    private final int expReward;

    protected Enemy(String name, int maxHp, int atk, int def, int expReward) {
        super(name, maxHp, 0, atk, def);
        this.expReward = expReward;
    }

    /** ศัตรูเลือกท่าในเทิร์นตัวเอง ค่าเริ่มต้นคือตีธรรมดา (override ได้) */
    public String takeTurn(Character target) {
        return attack(target);
    }

    /** ของที่ดรอปตอนแพ้ ค่าเริ่มต้นคือไม่ดรอป */
    public Item rollDrop() { return null; }

    public boolean canFlee() { return true; }
    public boolean isBoss() { return false; }
    public int getExpReward() { return expReward; }

    public abstract Color getColor();
}
