package com.goosequest.state;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Align;
import com.goosequest.GameManager;
import com.goosequest.util.Art;
import com.goosequest.util.Keys;

/**
 * [คนที่ 2] กล่องบทพูด ซ้อนทับบนหน้าจอเดิน
 * กด ENTER เพื่อไปบรรทัดถัดไป จบแล้วกลับไปเดินต่อ
 */
public class DialogueState extends GameState {
    private final GameState background;
    private final String speaker;
    private final String[] lines;
    private int index = 0;

    public DialogueState(GameManager gm, GameState background, String speaker, String... lines) {
        super(gm);
        this.background = background;
        this.speaker = speaker;
        this.lines = lines;
    }

    @Override
    public void update(float dt) {
        if (Keys.confirm()) {
            index++;
            if (index >= lines.length) gm.pop();
        }
    }

    @Override
    public void render() {
        background.render();

        gm.shapes.setProjectionMatrix(uiCam.combined);
        gm.shapes.begin(ShapeType.Filled);
        Art.box(gm.shapes, 20, 20, GameManager.VIEW_W - 40, 130);
        gm.shapes.end();

        gm.batch.setProjectionMatrix(uiCam.combined);
        gm.batch.begin();
        gm.font.getData().setScale(1.3f);
        if (!speaker.isEmpty()) {
            gm.font.setColor(Color.YELLOW);
            gm.font.draw(gm.batch, speaker, 40, 135);
        }
        gm.font.setColor(Color.WHITE);
        if (index < lines.length) {
            gm.font.draw(gm.batch, lines[index], 40, 105, GameManager.VIEW_W - 80, Align.left, true);
        }
        gm.font.getData().setScale(1f);
        gm.font.setColor(Color.GRAY);
        gm.font.draw(gm.batch, "ENTER >", GameManager.VIEW_W - 100, 40);
        gm.batch.end();
    }
}
