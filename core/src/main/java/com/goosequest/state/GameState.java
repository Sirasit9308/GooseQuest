package com.goosequest.state;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.goosequest.GameManager;

/**
 * หน้าจอ/โหมดของเกม (State Pattern)
 * GameManager เรียกแค่ update() กับ render() ของ state บนสุด
 * แต่ละ state ทำงานต่างกันเอง (Polymorphism)
 */
public abstract class GameState {
    protected final GameManager gm;
    /** กล้องสำหรับ UI: (0,0) = มุมซ้ายล่างของหน้าต่าง */
    protected final OrthographicCamera uiCam;

    protected GameState(GameManager gm) {
        this.gm = gm;
        this.uiCam = new OrthographicCamera();
        uiCam.setToOrtho(false, GameManager.VIEW_W, GameManager.VIEW_H);
    }

    public abstract void update(float dt);

    public abstract void render();

    public void dispose() {}
}
