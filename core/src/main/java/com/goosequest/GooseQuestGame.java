package com.goosequest;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ScreenUtils;
import com.goosequest.state.TitleState;
import com.goosequest.util.Palette;

/** ตัวเกมหลักที่ libGDX เรียกใช้ ส่งงานทั้งหมดต่อให้ GameManager */
public class GooseQuestGame extends ApplicationAdapter {
    private GameManager gm;

    @Override
    public void create() {
        gm = new GameManager();
        gm.setState(new TitleState(gm));
    }

    @Override
    public void render() {
        ScreenUtils.clear(Palette.BG);
        gm.update(Gdx.graphics.getDeltaTime());
        gm.render();
    }

    @Override
    public void dispose() {
        gm.dispose();
    }
}
