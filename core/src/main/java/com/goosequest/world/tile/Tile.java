package com.goosequest.world.tile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.goosequest.state.ExploreState;

/**
 * [คนที่ 3] พื้นแต่ละช่องของแผนที่
 * แต่ละชนิดบอกเองว่าเดินได้ไหม และเกิดอะไรขึ้นเมื่อเหยียบ
 */
public abstract class Tile {
    public abstract boolean isWalkable();

    public abstract Color getColor();

    /** เรียกเมื่อห่านเดินมาหยุดบนช่องนี้ ค่าเริ่มต้นคือไม่มีอะไร */
    public void onStep(ExploreState state) {}

    /** วาดสี่เหลี่ยมเต็มช่อง คลาสลูก override เพื่อแต่งเพิ่มได้ */
    public void draw(ShapeRenderer s, float px, float py, float t) {
        s.setColor(getColor());
        s.rect(px, py, t, t);
    }
}
