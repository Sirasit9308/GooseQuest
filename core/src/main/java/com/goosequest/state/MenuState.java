package com.goosequest.state;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.goosequest.GameManager;
import com.goosequest.character.Goose;
import com.goosequest.item.Inventory;
import com.goosequest.item.Item;
import com.goosequest.util.Art;
import com.goosequest.util.Keys;

import java.util.List;

/**
 * [คนที่ 2] เมนูกระเป๋า: ดูสถานะห่าน และใช้ไอเทมนอกการต่อสู้
 * TODO (คนที่ 2): เพิ่มปุ่ม Save / Load
 */
public class MenuState extends GameState {
    private final GameState background;
    private int cursor = 0;
    private String message = "";

    public MenuState(GameManager gm, GameState background) {
        super(gm);
        this.background = background;
    }

    @Override
    public void update(float dt) {
        List<Item> items = gm.getInventory().getItems();
        if (Keys.back() || Keys.menu()) {
            gm.pop();
            return;
        }
        if (items.isEmpty()) return;
        if (Keys.upJust()) cursor = (cursor + items.size() - 1) % items.size();
        if (Keys.downJust()) cursor = (cursor + 1) % items.size();
        if (Keys.confirm()) {
            Item item = items.get(cursor);
            gm.getInventory().remove(item);
            message = item.use(gm.getHero());
            cursor = Math.max(0, Math.min(cursor, gm.getInventory().size() - 1));
        }
    }

    @Override
    public void render() {
        background.render();
        Goose hero = gm.getHero();
        Inventory inv = gm.getInventory();
        List<Item> items = inv.getItems();

        gm.shapes.setProjectionMatrix(uiCam.combined);
        gm.shapes.begin(ShapeType.Filled);
        Art.box(gm.shapes, 100, 80, 600, 440);
        if (!items.isEmpty()) {
            gm.shapes.setColor(Color.YELLOW);
            gm.shapes.rect(370, 412 - cursor * 26, 10, 10);
        }
        gm.shapes.end();

        gm.batch.setProjectionMatrix(uiCam.combined);
        gm.batch.begin();
        gm.font.getData().setScale(1.2f);
        gm.font.setColor(Color.YELLOW);
        gm.font.draw(gm.batch, hero.getName(), 125, 495);
        gm.font.draw(gm.batch, "BAG (" + inv.size() + "/" + Inventory.CAPACITY + ")", 390, 495);
        gm.font.setColor(Color.WHITE);
        String[] stats = {
                "Class: " + hero.getClassName(),
                "Level: " + hero.getLevel(),
                "EXP: " + hero.getExp() + " / " + hero.expToNext(),
                "HP: " + hero.getHp() + " / " + hero.getMaxHp(),
                "MP: " + hero.getMp() + " / " + hero.getMaxMp(),
                "ATK: " + hero.getAtk(),
                "DEF: " + hero.getDef(),
                "Skill: " + hero.getSkill().getName() + " (" + hero.getSkill().getMpCost() + " MP)",
        };
        for (int i = 0; i < stats.length; i++) {
            gm.font.draw(gm.batch, stats[i], 125, 460 - i * 28);
        }
        if (items.isEmpty()) {
            gm.font.draw(gm.batch, "(empty)", 390, 425);
        }
        for (int i = 0; i < items.size(); i++) {
            gm.font.setColor(i == cursor ? Color.YELLOW : Color.WHITE);
            gm.font.draw(gm.batch, items.get(i).getName(), 390, 425 - i * 26);
        }
        gm.font.getData().setScale(1f);
        gm.font.setColor(Color.LIGHT_GRAY);
        if (!items.isEmpty()) {
            gm.font.draw(gm.batch, items.get(cursor).getDescription(), 125, 150);
        }
        gm.font.setColor(Color.GREEN);
        gm.font.draw(gm.batch, message, 125, 125);
        gm.font.setColor(Color.GRAY);
        gm.font.draw(gm.batch, "ENTER: use item    ESC/I: close", 125, 100);
        gm.batch.end();
    }
}
