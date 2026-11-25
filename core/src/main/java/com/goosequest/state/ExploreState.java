package com.goosequest.state;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.goosequest.GameManager;
import com.goosequest.character.Goose;
import com.goosequest.util.Art;
import com.goosequest.util.Keys;
import com.goosequest.util.Palette;
import com.goosequest.world.GameObject;
import com.goosequest.world.Interactable;
import com.goosequest.world.ItemPickup;
import com.goosequest.world.MapEnemy;
import com.goosequest.world.NPC;
import com.goosequest.world.Player;
import com.goosequest.world.TileMap;
import com.goosequest.world.World;

/**
 * [คนที่ 3] โหมดเดินสำรวจแผนที่
 * เดินไปชนวัตถุ → เรียก interact() ของวัตถุนั้น (ไม่ต้องรู้ว่าเป็นอะไร)
 */
public class ExploreState extends GameState {
    private static final float TOUCH_MARGIN = 0.15f; // ช่อง: แตะศัตรู/ไอเทมได้ไกลจากขอบเท่านี้
    private static final float TOUCH_COOLDOWN = 1.5f; // วินาทีที่ไม่ให้ชนซ้ำ (หนีจากการต่อสู้แล้วเดินหนีทัน)
    private static final float TALK_RANGE = 1.5f; // ช่อง: ใกล้ NPC แค่ไหนถึงกด F คุยได้
    private static final float FADE_TIME = 0.35f; // วินาทีที่จอค่อย ๆ สว่างหลังเปลี่ยนแผนที่

    private final World world;
    private final OrthographicCamera worldCam;
    private float touchCooldown = 0f;
    private int lastTileX = Integer.MIN_VALUE; // ช่องที่ห่านยืนเมื่อเฟรมก่อน ไว้ตรวจการก้าวเข้าช่องใหม่
    private int lastTileY = Integer.MIN_VALUE;
    private float fade = 1f; // 1 = มืดสนิท, 0 = เห็นชัด

    public ExploreState(GameManager gm) {
        super(gm);
        this.world = gm.getWorld();
        this.worldCam = new OrthographicCamera();
        worldCam.setToOrtho(false, GameManager.VIEW_W, GameManager.VIEW_H);
    }

    @Override
    public void update(float dt) {
        fade = Math.max(0f, fade - dt / FADE_TIME);
        TileMap map = world.getCurrentMap();
        map.update(dt);

        if (Keys.menu()) {
            gm.push(new MenuState(gm, this));
            return;
        }

        touchCooldown = Math.max(0f, touchCooldown - dt);

        Player p = world.getPlayer();
        p.move(readAxisX(), readAxisY(), dt, map);

        // ก้าวเข้าช่องใหม่ → onStep (เช่น ประตูวาร์ป)
        if (p.getX() != lastTileX || p.getY() != lastTileY) {
            boolean first = lastTileX == Integer.MIN_VALUE;
            lastTileX = p.getX();
            lastTileY = p.getY();
            if (!first) {
                map.getTile(lastTileX, lastTileY).onStep(this);
                if (world.getCurrentMap() != map) return; // วาร์ปไปแล้ว
            }
        }

        // F → คุยกับ NPC ที่อยู่ใกล้ที่สุด
        if (Keys.interact()) {
            GameObject target = map.findNearbyKeyTarget(p, TALK_RANGE);
            if (target instanceof Interactable i) {
                p.faceToward(target.getCenterX(), target.getCenterY());
                i.interact(this);
                return;
            }
        }

        // แตะศัตรู/ไอเทม → ทำงานทันที
        if (touchCooldown <= 0f) {
            GameObject touched = map.findTouching(p, TOUCH_MARGIN);
            if (touched instanceof Interactable i) {
                touchCooldown = TOUCH_COOLDOWN;
                p.faceToward(touched.getCenterX(), touched.getCenterY());
                i.interact(this);
            }
        }
    }

    private float readAxisX() {
        return (Keys.rightHeld() ? 1f : 0f) - (Keys.leftHeld() ? 1f : 0f);
    }

    private float readAxisY() {
        return (Keys.upHeld() ? 1f : 0f) - (Keys.downHeld() ? 1f : 0f);
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
        fade = 1f;
        touchCooldown = 0.5f; // กันชนศัตรูทันทีหลังวาร์ป
        Player p = world.getPlayer();
        lastTileX = p.getX();
        lastTileY = p.getY();
    }

    // ---------- วาดภาพ ----------

    @Override
    public void render() {
        TileMap map = world.getCurrentMap();
        Player p = world.getPlayer();
        float t = GameManager.TILE;

        // กล้องตามห่าน แต่ไม่ให้เลยขอบแผนที่
        worldCam.position.set(
                clampCam(p.getCenterX() * t, map.getWidth() * t, GameManager.VIEW_W),
                clampCam(p.getCenterY() * t, map.getHeight() * t, GameManager.VIEW_H), 0);
        worldCam.update();

        Art.enableBlend();
        gm.shapes.setProjectionMatrix(worldCam.combined);
        gm.shapes.begin(ShapeType.Filled);
        map.draw(gm.shapes, t); // วาดห่านรวมอยู่ใน map.draw (เรียงตามความลึกกับวัตถุอื่น)
        gm.shapes.end();

        if (fade > 0) {
            Art.enableBlend();
            gm.shapes.setProjectionMatrix(uiCam.combined);
            gm.shapes.begin(ShapeType.Filled);
            gm.shapes.setColor(0f, 0f, 0f, fade * fade);
            gm.shapes.rect(0, 0, GameManager.VIEW_W, GameManager.VIEW_H);
            gm.shapes.end();
        }

        drawHud(map, map.findNearbyKeyTarget(p, TALK_RANGE));
    }

    private static float clampCam(float target, float mapSize, float view) {
        if (mapSize <= view) return mapSize / 2;
        return Math.max(view / 2, Math.min(mapSize - view / 2, target));
    }

    private void drawHud(TileMap map, GameObject talkTarget) {
        Goose hero = gm.getHero();
        gm.shapes.setProjectionMatrix(uiCam.combined);
        gm.shapes.begin(ShapeType.Filled);
        Art.box(gm.shapes, 8, GameManager.VIEW_H - 78, 230, 70);
        Art.bar(gm.shapes, 60, GameManager.VIEW_H - 46, 160, 10,
                (float) hero.getHp() / hero.getMaxHp(), Palette.HP);
        Art.bar(gm.shapes, 60, GameManager.VIEW_H - 66, 160, 10,
                (float) hero.getMp() / hero.getMaxMp(), Palette.MP);
        gm.shapes.end();

        gm.batch.setProjectionMatrix(uiCam.combined);
        gm.batch.begin();
        gm.font.setColor(Color.WHITE);
        gm.font.draw(gm.batch, hero.getName() + "  Lv " + hero.getLevel(), 18, GameManager.VIEW_H - 16);
        gm.font.draw(gm.batch, "HP", 18, GameManager.VIEW_H - 36);
        gm.font.draw(gm.batch, "MP", 18, GameManager.VIEW_H - 56);
        gm.font.draw(gm.batch, map.getDisplayName(), GameManager.VIEW_W - 160, GameManager.VIEW_H - 16);
        gm.font.setColor(Color.LIGHT_GRAY);
        gm.font.draw(gm.batch, "Arrows/WASD: move   F: talk   I: bag", GameManager.VIEW_W - 290, 24);
        if (talkTarget instanceof NPC npc) {
            gm.font.setColor(Color.WHITE);
            gm.font.draw(gm.batch, "F: Talk to " + npc.getName(), GameManager.VIEW_W / 2 - 60, 60);
        }
        gm.batch.end();
    }
}
