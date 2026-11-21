package com.goosequest.world.tile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class TreeTile extends Tile {
    private static final Color GRASS = new Color(0.45f, 0.75f, 0.35f, 1f);
    private static final Color LEAVES = new Color(0.1f, 0.4f, 0.15f, 1f);
    private static final Color TRUNK = new Color(0.4f, 0.25f, 0.1f, 1f);

    @Override
    public boolean isWalkable() { return false; }

    @Override
    public Color getColor() { return LEAVES; }

    @Override
    public void draw(ShapeRenderer s, float px, float py, float t) {
        s.setColor(GRASS);
        s.rect(px, py, t, t);
        s.setColor(TRUNK);
        s.rect(px + t * 0.42f, py + 2, t * 0.16f, t * 0.35f);
        s.setColor(LEAVES);
        s.circle(px + t / 2, py + t * 0.6f, t * 0.38f);
    }
}
