package com.goosequest.world.tile;

import com.badlogic.gdx.graphics.Color;
import com.goosequest.state.ExploreState;

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

    @Override
    public void onStep(ExploreState state) {
        state.warp(targetMap, targetDoorId);
    }

    public String getId() { return id; }
}
