package com.goosequest.world.tile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class FenceTile extends Tile {
    private static final Color GRASS = new Color(0.45f, 0.75f, 0.35f, 1f);
    private static final Color WOOD = new Color(0.55f, 0.35f, 0.15f, 1f);

    @Override
    public boolean isWalkable() { return false; }

    @Override
    public Color getColor() { return WOOD; }

    @Override
    public void draw(ShapeRenderer s, float px, float py, float t) {
        s.setColor(GRASS);
        s.rect(px, py, t, t);
        s.setColor(WOOD);
        s.rect(px, py + t * 0.25f, t, t * 0.12f);
        s.rect(px, py + t * 0.6f, t, t * 0.12f);
        s.rect(px + t * 0.1f, py + 2, t * 0.15f, t * 0.85f);
        s.rect(px + t * 0.75f, py + 2, t * 0.15f, t * 0.85f);
    }
}
