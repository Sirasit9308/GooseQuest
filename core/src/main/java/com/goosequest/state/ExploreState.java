package com.goosequest.state;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.goosequest.GameManager;
import com.goosequest.character.Goose;
import com.goosequest.util.Art;
import com.goosequest.util.Keys;
import com.goosequest.world.Direction;
import com.goosequest.world.GameObject;
import com.goosequest.world.Interactable;
import com.goosequest.world.ItemPickup;
import com.goosequest.world.MapEnemy;
import com.goosequest.world.Player;
import com.goosequest.world.TileMap;
import com.goosequest.world.World;

/**
 * [คนที่ 3] โหมดเดินสำรวจแผนที่
 * เดินไปชนวัตถุ → เรียก interact() ของวัตถุนั้น (ไม่ต้องรู้ว่าเป็นอะไร)
 */
public class ExploreState extends GameState {
    private static final float MOVE_DELAY = 0.14f; // วินาทีต่อการเดิน 1 ช่อง

    private final World world;
    private final OrthographicCamera worldCam;
    private float moveTimer = 0f;

    public ExploreState(GameManager gm) {
        super(gm);
        this.world = gm.getWorld();
        this.worldCam = new OrthographicCamera();
        worldCam.setToOrtho(false, GameManager.VIEW_W, GameManager.VIEW_H);
    }

    @Override
    public void update(float dt) {
        TileMap map = world.getCurrentMap();
        map.update(dt);

        if (Keys.menu()) {
            gm.push(new MenuState(gm, this));
            return;
        }

        moveTimer -= dt;
        Direction d = readDirection();
        if (d == null || moveTimer > 0) return;
        moveTimer = MOVE_DELAY;

        Player p = world.getPlayer();
        GameObject target = map.getObjectAt(p.getX() + d.dx, p.getY() + d.dy);
        if (target instanceof Interactable i) {
            p.face(d);
            i.interact(this);
        } else if (p.tryMove(d, map)) {
            map.getTile(p.getX(), p.getY()).onStep(this);
        }
    }

    private Direction readDirection() {
        if (Keys.upHeld()) return Direction.UP;
        if (Keys.downHeld()) return Direction.DOWN;
        if (Keys.leftHeld()) return Direction.LEFT;
        if (Keys.rightHeld()) return Direction.RIGHT;
        return null;
    }

    // ---------- เมธอดที่ Interactable / Tile เรียกใช้ ----------

    public void showDialogue(String speaker, String... lines) {
        gm.push(new DialogueState(gm, this, speaker, lines));
    }

    public void startBattle(MapEnemy enemy) {
        gm.push(new BattleState(gm, this, enemy));
    }

    /** BattleState เรียกเมื่อชนะ: ลบศัตรูออกจากแผนที่ถาวร */
    public void onEnemyDefeated(MapEnemy enemy) {
        world.getCurrentMap().remove(enemy);
    }

    public void pickUp(ItemPickup pickup) {
        if (gm.getInventory().add(pickup.getItem())) {
            world.getCurrentMap().remove(pickup);
            showDialogue("", "You found " + pickup.getItem().getName() + "!",
                    pickup.getItem().getDescription());
        } else {
            showDialogue("", "Your bag is full!");
        }
    }

    public void warp(String targetMap, String targetDoorId) {
        world.warp(targetMap, targetDoorId);
        moveTimer = MOVE_DELAY * 2; // กันเดินต่อทันทีหลังวาร์ป
    }

    // ---------- วาดภาพ ----------

    @Override
    public void render() {
        TileMap map = world.getCurrentMap();
        Player p = world.getPlayer();
        float t = GameManager.TILE;

        // กล้องตามห่าน แต่ไม่ให้เลยขอบแผนที่
        worldCam.position.set(
                clampCam(p.getX() * t + t / 2, map.getWidth() * t, GameManager.VIEW_W),
                clampCam(p.getY() * t + t / 2, map.getHeight() * t, GameManager.VIEW_H), 0);
        worldCam.update();

        gm.shapes.setProjectionMatrix(worldCam.combined);
        gm.shapes.begin(ShapeType.Filled);
        map.draw(gm.shapes, t);
        p.draw(gm.shapes, t);
        gm.shapes.end();

        drawHud(map);
    }

    private static float clampCam(float target, float mapSize, float view) {
        if (mapSize <= view) return mapSize / 2;
        return Math.max(view / 2, Math.min(mapSize - view / 2, target));
    }

    private void drawHud(TileMap map) {
        Goose hero = gm.getHero();
        gm.shapes.setProjectionMatrix(uiCam.combined);
        gm.shapes.begin(ShapeType.Filled);
        Art.box(gm.shapes, 8, GameManager.VIEW_H - 78, 230, 70);
        Art.bar(gm.shapes, 60, GameManager.VIEW_H - 46, 160, 10,
                (float) hero.getHp() / hero.getMaxHp(), Color.RED);
        Art.bar(gm.shapes, 60, GameManager.VIEW_H - 66, 160, 10,
                (float) hero.getMp() / hero.getMaxMp(), Color.SKY);
        gm.shapes.end();

        gm.batch.setProjectionMatrix(uiCam.combined);
        gm.batch.begin();
        gm.font.setColor(Color.WHITE);
        gm.font.draw(gm.batch, hero.getName() + "  Lv " + hero.getLevel(), 18, GameManager.VIEW_H - 16);
        gm.font.draw(gm.batch, "HP", 18, GameManager.VIEW_H - 36);
        gm.font.draw(gm.batch, "MP", 18, GameManager.VIEW_H - 56);
        gm.font.draw(gm.batch, map.getDisplayName(), GameManager.VIEW_W - 160, GameManager.VIEW_H - 16);
        gm.font.setColor(Color.LIGHT_GRAY);
        gm.font.draw(gm.batch, "Arrows/WASD: move   I: bag", GameManager.VIEW_W - 230, 24);
        gm.batch.end();
    }
}
