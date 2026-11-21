package com.goosequest.character;

import com.badlogic.gdx.graphics.Color;
import com.goosequest.item.Bread;
import com.goosequest.item.Item;
import com.goosequest.util.Dice;

/** [คนที่ 1] จิ้งจอก: กัดแรงสองเท่าเป็นบางครั้ง */
public class Fox extends Enemy {
    private static final Color COLOR = new Color(0.95f, 0.45f, 0.1f, 1f);

    public Fox() {
        super("Sly Fox", 40, 13, 2, 30);
    }

    @Override
    public String takeTurn(Character target) {
        if (Dice.chance(0.3)) {
            int dmg = target.takeDamage(Dice.vary(getAtk() * 2));
            return getName() + " uses SLY BITE! " + dmg + " damage!";
        }
        return super.takeTurn(target);
    }

    @Override
    public Item rollDrop() {
        return new Bread();
    }

    @Override
    public Color getColor() { return COLOR; }
}
