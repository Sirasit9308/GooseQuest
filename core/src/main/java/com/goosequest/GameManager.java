package com.goosequest;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.goosequest.character.Goose;
import com.goosequest.item.Bread;
import com.goosequest.item.Inventory;
import com.goosequest.state.GameState;
import com.goosequest.world.World;
import com.goosequest.world.WorldBuilder;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * ตัวจัดการเกม: เก็บ state ที่กำลังทำงานเป็น stack (State Pattern)
 * และเก็บข้อมูลที่ทุก state ใช้ร่วมกัน (ห่าน, กระเป๋า, โลก)
 *
 * stack ทำให้ซ้อนหน้าจอได้ เช่น Explore → push Battle → pop กลับมา Explore
 */
public class GameManager {
    public static final float VIEW_W = 800f;
    public static final float VIEW_H = 600f;
    public static final float TILE = 32f;

    public final SpriteBatch batch;
    public final ShapeRenderer shapes;
    public final BitmapFont font;

    private final Deque<GameState> states = new ArrayDeque<>();

    private Goose hero;
    private Inventory inventory;
    private World world;

    public GameManager() {
        batch = new SpriteBatch();
        shapes = new ShapeRenderer();
        font = new BitmapFont(); // ฟอนต์ติดมากับ libGDX (อังกฤษเท่านั้น)
    }

    /** เริ่มเกมใหม่ด้วยห่านสายที่เลือก */
    public void newGame(Goose chosenHero) {
        hero = chosenHero;
        inventory = new Inventory();
        inventory.add(new Bread());
        inventory.add(new Bread());
        world = WorldBuilder.build();
    }

    // ---------- จัดการ state ----------
    public void push(GameState s) { states.push(s); }

    public void pop() {
        GameState s = states.pop();
        s.dispose();
    }

    /** ล้าง stack ทั้งหมดแล้วเริ่ม state ใหม่ */
    public void setState(GameState s) {
        while (!states.isEmpty()) pop();
        states.push(s);
    }

    public void update(float dt) {
        if (!states.isEmpty()) states.peek().update(dt);
    }

    public void render() {
        if (!states.isEmpty()) states.peek().render();
    }

    public void dispose() {
        while (!states.isEmpty()) pop();
        batch.dispose();
        shapes.dispose();
        font.dispose();
    }

    public Goose getHero() { return hero; }
    public Inventory getInventory() { return inventory; }
    public World getWorld() { return world; }
}
