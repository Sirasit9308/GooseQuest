package com.goosequest.world.tile;

import com.badlogic.gdx.graphics.Color;

public class PathTile extends Tile {
    private static final Color COLOR = new Color(0.85f, 0.75f, 0.5f, 1f);

    @Override
    public boolean isWalkable() { return true; }

    @Override
    public Color getColor() { return COLOR; }
}
