package com.goosequest.character;

import com.goosequest.skill.SneakyPeck;

/** [คนที่ 1] สายนินจา: ตีแรง ติดคริ แต่บาง */
public class NinjaGoose extends Goose {
    public NinjaGoose() {
        super("Shadow Quack", 85, 30, 16, 2, new SneakyPeck());
    }

    @Override
    protected void onLevelUp() {
        growStats(8, 3, 4, 1);
    }

    @Override
    public String getClassName() { return "Ninja"; }
}
