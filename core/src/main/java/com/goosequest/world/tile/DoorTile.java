package com.goosequest.world.tile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.goosequest.state.ExploreState;
import com.goosequest.util.Art;
import com.goosequest.util.Palette;

/**
 * [คนที่ 3] ทางออกไปแผนที่อื่น เหยียบแล้ววาร์ป
 * เชื่อมกันด้วย id: ประตู "farm_east" พาไปโผล่ที่ประตู "park_west" เป็นต้น
 */
public class DoorTile extends Tile {
    private static final Color COLOR = new Color(0.55f, 0.4f, 0.25f, 1f);

    private final String id;
    private final String targetMap;
    private final String targetDoorId;

    public DoorTile(String id, String targetMap, String targetDoorId) {
        this.id = id;
        this.targetMap = targetMap;
        this.targetDoorId = targetDoorId;
    }

    @Override
    public boolean isWalkable() { return true; }

    @Override
    public Color getColor() { return COLOR; }

    /** ทางดินต่อออกนอกแผนที่ + ประกายสีทองกะพริบ บอกว่าเดินผ่านไปได้ */
    @Override
    public void draw(ShapeRenderer s, float px, float py, float t) {
        PathTile.paint(s, px, py, t);
        float a = 0.35f + 0.35f * (float) Math.sin(Art.time() * 4f);
        Color g = Palette.GOLD;
        s.setColor(g.r, g.g, g.b, a);
        s.circle(px + t / 2, py + t / 2, t * 0.3f);
        s.setColor(1f, 1f, 1f, a);
        s.circle(px + t / 2, py + t / 2, t * 0.12f);
    }

    @Override
    public void onStep(ExploreState state) {
        state.warp(targetMap, targetDoorId);
    }

    public String getId() { return id; }
}
