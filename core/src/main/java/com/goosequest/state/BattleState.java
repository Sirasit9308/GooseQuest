package com.goosequest.state;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.goosequest.GameManager;
import com.goosequest.battle.BattleSystem;
import com.goosequest.character.Character;
import com.goosequest.character.Enemy;
import com.goosequest.character.Goose;
import com.goosequest.item.Item;
import com.goosequest.util.Art;
import com.goosequest.util.Keys;
import com.goosequest.util.Palette;
import com.goosequest.world.MapEnemy;

import java.util.List;

/**
 * [คนที่ 1] หน้าจอต่อสู้: รับปุ่ม + วาดภาพ ส่วนกติกาอยู่ใน BattleSystem
 */
public class BattleState extends GameState {
    private enum Mode { COMMAND, ITEM, END }

    private static final String[] COMMANDS = {"Attack", "Skill", "Item", "Run"};

    private final ExploreState explore;
    private final MapEnemy mapEnemy;
    private final BattleSystem battle;
    private Mode mode = Mode.COMMAND;
    private int cursor = 0;

    public BattleState(GameManager gm, ExploreState explore, MapEnemy mapEnemy) {
        super(gm);
        this.explore = explore;
        this.mapEnemy = mapEnemy;
        this.battle = new BattleSystem(gm.getHero(), mapEnemy.getEnemy(), gm.getInventory());
    }

    @Override
    public void update(float dt) {
        switch (mode) {
            case COMMAND -> updateCommand();
            case ITEM -> updateItem();
            case END -> {
                if (Keys.confirm()) finish();
            }
        }
    }

    private void updateCommand() {
        if (Keys.upJust()) cursor = (cursor + COMMANDS.length - 1) % COMMANDS.length;
        if (Keys.downJust()) cursor = (cursor + 1) % COMMANDS.length;
        if (!Keys.confirm()) return;

        switch (cursor) {
            case 0 -> battle.attack();
            case 1 -> battle.useSkill();
            case 2 -> {
                mode = Mode.ITEM;
                cursor = 0;
                return;
            }
            case 3 -> battle.flee();
        }
        if (!battle.isOngoing()) mode = Mode.END;
    }

    private void updateItem() {
        List<Item> items = gm.getInventory().getItems();
        int options = items.size() + 1; // + "Back"
        if (Keys.upJust()) cursor = (cursor + options - 1) % options;
        if (Keys.downJust()) cursor = (cursor + 1) % options;
        if (Keys.back() || (Keys.confirm() && cursor == items.size())) {
            mode = Mode.COMMAND;
            cursor = 2;
            return;
        }
        if (Keys.confirm()) {
            battle.useItem(items.get(cursor));
            mode = battle.isOngoing() ? Mode.COMMAND : Mode.END;
            cursor = 0;
        }
    }

    private void finish() {
        switch (battle.getResult()) {
            case WON -> {
                explore.onEnemyDefeated(mapEnemy);
                if (battle.getEnemy().isBoss()) gm.setState(new EndState(gm, true));
                else gm.pop();
            }
            case LOST -> gm.setState(new EndState(gm, false));
            default -> gm.pop(); // FLED
        }
    }

    // ---------- วาดภาพ ----------

    @Override
    public void render() {
        Goose hero = battle.getHero();
        Enemy enemy = battle.getEnemy();

        gm.shapes.setProjectionMatrix(uiCam.combined);
        Art.enableBlend();
        gm.shapes.begin(ShapeType.Filled);
        drawBackground();

        if (enemy.isAlive()) {
            Art.shadow(gm.shapes, 610, 414, 150);
            Art.enemy(gm.shapes, enemy.getColor(), 540, 400 + Art.bob(1.3f, 3f), 140);
        }
        if (hero.isAlive()) {
            Art.shadow(gm.shapes, 200, 232, 150);
            Art.goose(gm.shapes, 120, 230 + Art.bob(0f, 2f), 5f, false);
        }

        statusPanel(enemy, 40, 470);
        statusPanel(hero, 470, 250);

        // กล่องข้อความ + เมนู
        Art.box(gm.shapes, 10, 10, 560, 180);
        Art.box(gm.shapes, 580, 10, 210, 180);
        if (mode != Mode.END) {
            Art.cursor(gm.shapes, 598, 150 - cursor * 30, 12);
        }
        gm.shapes.end();

        drawText(hero, enemy);
    }

    /** ฉากหลัง: ท้องฟ้าไล่สี เมฆลอย เนินเขา 2 ชั้น และแท่นหญ้าที่ตัวละครยืน */
    private void drawBackground() {
        float w = GameManager.VIEW_W;
        float h = GameManager.VIEW_H;
        // ฟ้าไล่สี (ล่างอ่อน บนเข้ม)
        gm.shapes.rect(0, 200, w, h - 200, Palette.SKY_BOTTOM, Palette.SKY_BOTTOM, Palette.SKY_TOP, Palette.SKY_TOP);
        // เมฆเลื่อนช้า ๆ
        for (int i = 0; i < 4; i++) {
            float cx = ((i * 260 + Art.time() * (8 + i * 3)) % (w + 240)) - 120;
            float cy = 545 - (i % 2) * 45;
            cloud(cx, cy, 1f - i * 0.12f);
        }
        // เนินเขาไกล + ใกล้
        gm.shapes.setColor(Palette.HILL_FAR);
        gm.shapes.ellipse(-120, 280, 520, 200);
        gm.shapes.ellipse(260, 300, 420, 170);
        gm.shapes.setColor(Palette.HILL_NEAR);
        gm.shapes.ellipse(360, 270, 560, 210);
        gm.shapes.ellipse(-80, 260, 420, 150);
        // พื้นหญ้า
        gm.shapes.setColor(Palette.GRASS);
        gm.shapes.rect(0, 200, w, 150);
        gm.shapes.setColor(Palette.GRASS_DARK);
        gm.shapes.rect(0, 346, w, 4);
        // แท่นที่ยืน
        platform(470, 380, 270, 64);
        platform(50, 205, 300, 64);
    }

    private void cloud(float cx, float cy, float k) {
        gm.shapes.setColor(1f, 1f, 1f, 0.85f);
        gm.shapes.ellipse(cx - 50 * k, cy - 14 * k, 100 * k, 30 * k);
        gm.shapes.circle(cx - 18 * k, cy + 4 * k, 20 * k);
        gm.shapes.circle(cx + 14 * k, cy + 8 * k, 24 * k);
    }

    private void platform(float x, float y, float w, float h) {
        gm.shapes.setColor(Palette.GRASS_DARK);
        gm.shapes.ellipse(x, y - 6, w, h);
        gm.shapes.setColor(Palette.GRASS_LIGHT);
        gm.shapes.ellipse(x + 6, y, w - 12, h - 8);
        gm.shapes.setColor(Palette.GRASS);
        gm.shapes.ellipse(x + 16, y + 4, w - 32, h - 18);
    }

    private void statusPanel(Character c, float x, float y) {
        Art.box(gm.shapes, x, y, 290, 80);
        Art.bar(gm.shapes, x + 50, y + 30, 220, 12, (float) c.getHp() / c.getMaxHp(), Palette.HP);
        if (c.getMaxMp() > 0) {
            Art.bar(gm.shapes, x + 50, y + 12, 220, 8, (float) c.getMp() / c.getMaxMp(), Palette.MP);
        }
    }

    private void drawText(Goose hero, Enemy enemy) {
        gm.batch.setProjectionMatrix(uiCam.combined);
        gm.batch.begin();
        gm.font.setColor(Color.WHITE);
        gm.font.draw(gm.batch, enemy.getName() + (enemy.isStunned() ? "  [STUNNED]" : ""), 55, 538);
        gm.font.draw(gm.batch, "HP", 55, 512);
        gm.font.draw(gm.batch, enemy.getHp() + "/" + enemy.getMaxHp(), 230, 538);
        gm.font.draw(gm.batch, hero.getName() + "  Lv " + hero.getLevel(), 485, 318);
        gm.font.draw(gm.batch, "HP", 485, 292);
        gm.font.draw(gm.batch, "MP", 485, 272);
        gm.font.draw(gm.batch, hero.getHp() + "/" + hero.getMaxHp(), 680, 318);

        // บันทึกการต่อสู้
        List<String> log = battle.getRecentLog(5);
        for (int i = 0; i < log.size(); i++) {
            gm.font.draw(gm.batch, log.get(i), 28, 170 - i * 28);
        }

        // เมนู
        if (mode == Mode.COMMAND) {
            for (int i = 0; i < COMMANDS.length; i++) {
                String label = COMMANDS[i];
                if (i == 1) label = hero.getSkill().getName() + " (" + hero.getSkill().getMpCost() + ")";
                gm.font.setColor(i == cursor ? Color.YELLOW : Color.WHITE);
                gm.font.draw(gm.batch, label, 620, 165 - i * 30);
            }
        } else if (mode == Mode.ITEM) {
            List<Item> items = gm.getInventory().getItems();
            // แสดงได้สูงสุด 4 บรรทัด เลื่อนตาม cursor
            int first = Math.max(0, Math.min(cursor - 3, items.size() + 1 - 5));
            for (int row = 0; row < 5 && first + row <= items.size(); row++) {
                int i = first + row;
                String label = i == items.size() ? "Back" : items.get(i).getName();
                gm.font.setColor(i == cursor ? Color.YELLOW : Color.WHITE);
                gm.font.draw(gm.batch, label, 620, 165 - row * 30);
            }
        } else {
            gm.font.setColor(Color.YELLOW);
            gm.font.draw(gm.batch, "ENTER to continue", 600, 100);
        }
        gm.batch.end();
    }
}
