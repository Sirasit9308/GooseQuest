package com.goosequest.state;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.goosequest.GameManager;
import com.goosequest.util.Art;
import com.goosequest.util.Keys;

/** หน้าจบเกม: ชนะบอส หรือ แพ้ */
public class EndState extends GameState {
    private final boolean victory;

    public EndState(GameManager gm, boolean victory) {
        super(gm);
        this.victory = victory;
    }

    @Override
    public void update(float dt) {
        if (Keys.confirm()) gm.setState(new TitleState(gm));
    }

    @Override
    public void render() {
        gm.shapes.setProjectionMatrix(uiCam.combined);
        gm.shapes.begin(ShapeType.Filled);
        if (victory) {
            Art.goose(gm.shapes, 320, 220, 5f, false);
            gm.shapes.setColor(0.9f, 0.7f, 0.3f, 1f); // ขนมปังที่ได้คืน
            gm.shapes.ellipse(440, 200, 90, 40);
        } else {
            Art.goose(gm.shapes, 320, 220, 5f, true);
        }
        gm.shapes.end();

        gm.batch.setProjectionMatrix(uiCam.combined);
        gm.batch.begin();
        gm.font.getData().setScale(2.5f);
        gm.font.setColor(victory ? Color.YELLOW : Color.RED);
        gm.font.draw(gm.batch, victory ? "VICTORY! HONK HONK!" : "YOU FAINTED...", victory ? 190 : 250, 500);
        gm.font.getData().setScale(1.3f);
        gm.font.setColor(Color.WHITE);
        gm.font.draw(gm.batch, victory ? "The Sacred Bread is yours again." : "The Farmer keeps the bread... for now.",
                victory ? 260 : 230, 430);
        gm.font.getData().setScale(1f);
        gm.font.setColor(Color.GRAY);
        gm.font.draw(gm.batch, "ENTER to return to title", 310, 60);
        gm.batch.end();
    }
}
