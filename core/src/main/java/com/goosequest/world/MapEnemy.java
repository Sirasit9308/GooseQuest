package com.goosequest.world;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.goosequest.character.Enemy;
import com.goosequest.state.ExploreState;
import com.goosequest.util.Art;
import com.goosequest.util.Dice;

/**
 * [คนที่ 3] ศัตรูที่ยืนอยู่บนแผนที่ เดินไปชนแล้วเข้าหน้าต่อสู้
 * เก็บ Enemy (ข้อมูลต่อสู้) ไว้ข้างใน = Composition
 */
public class MapEnemy extends Entity implements Interactable {
    private static final int WANDER_RADIUS = 2;

    private final Enemy enemy;
    private final boolean wanders;
    private int homeX;
    private int homeY;
    private float timer = 1f;

    public MapEnemy(Enemy enemy, boolean wanders) {
        this.enemy = enemy;
        this.wanders = wanders;
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        homeX = x;
        homeY = y;
    }

    /** เดินวนแบบสุ่มรอบจุดเกิด */
    @Override
    public void update(float dt, TileMap map) {
        if (!wanders) return;
        timer -= dt;
        if (timer > 0) return;
        timer = 1f + Dice.next(100) / 100f;

        Direction d = Direction.values()[Dice.next(4)];
        int nx = x + d.dx;
        int ny = y + d.dy;
        if (Math.abs(nx - homeX) <= WANDER_RADIUS && Math.abs(ny - homeY) <= WANDER_RADIUS) {
            tryMove(d, map);
        }
    }

    @Override
    public void interact(ExploreState state) {
        state.startBattle(this);
    }

    @Override
    public void draw(ShapeRenderer s, float t) {
        Art.enemy(s, enemy.getColor(), x * t, y * t, t);
    }

    public Enemy getEnemy() { return enemy; }
}
