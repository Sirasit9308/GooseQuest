package com.goosequest.state;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.goosequest.GameManager;
import com.goosequest.character.Goose;
import com.goosequest.character.HonkerGoose;
import com.goosequest.character.NinjaGoose;
import com.goosequest.character.WarriorGoose;
import com.goosequest.util.Art;
import com.goosequest.util.Keys;

import java.util.List;
import java.util.function.Supplier;

/** หน้าแรก: เลือกสายอาชีพห่าน */
public class TitleState extends GameState {
    private final List<Supplier<Goose>> choices = List.of(WarriorGoose::new, HonkerGoose::new, NinjaGoose::new);
    private final List<Goose> previews = List.of(new WarriorGoose(), new HonkerGoose(), new NinjaGoose());
    private int cursor = 0;

    public TitleState(GameManager gm) {
        super(gm);
    }

    @Override
    public void update(float dt) {
        if (Keys.upJust()) cursor = (cursor + choices.size() - 1) % choices.size();
        if (Keys.downJust()) cursor = (cursor + 1) % choices.size();
        if (Keys.confirm()) {
            gm.newGame(choices.get(cursor).get());
            gm.setState(new ExploreState(gm));
        }
    }

    @Override
    public void render() {
        gm.shapes.setProjectionMatrix(uiCam.combined);
        gm.shapes.begin(ShapeType.Filled);
        Art.enableBlend();
        Art.shadow(gm.shapes, 394, 402, 100);
        Art.goose(gm.shapes, 330, 400 + Art.bob(0f, 2f), 4f, false);
        for (int i = 0; i < previews.size(); i++) {
            float y = 300 - i * 70;
            Art.box(gm.shapes, 200, y - 50, 400, 60);
            if (i == cursor) {
                Art.cursor(gm.shapes, 176, y - 31, 14);
            }
        }
        gm.shapes.end();

        gm.batch.setProjectionMatrix(uiCam.combined);
        gm.batch.begin();
        gm.font.setColor(Color.WHITE);
        gm.font.getData().setScale(2.5f);
        gm.font.draw(gm.batch, "GOOSE QUEST", 270, 560);
        gm.font.getData().setScale(1.2f);
        for (int i = 0; i < previews.size(); i++) {
            Goose g = previews.get(i);
            float y = 300 - i * 70;
            gm.font.setColor(i == cursor ? Color.YELLOW : Color.WHITE);
            gm.font.draw(gm.batch, g.getClassName() + "  -  " + g.getName(), 215, y);
            gm.font.setColor(Color.LIGHT_GRAY);
            gm.font.draw(gm.batch, "HP " + g.getMaxHp() + "  MP " + g.getMaxMp() + "  ATK " + g.getAtk()
                    + "  DEF " + g.getDef() + "  |  " + g.getSkill().getName(), 215, y - 22);
        }
        gm.font.setColor(Color.GRAY);
        gm.font.draw(gm.batch, "UP/DOWN to choose, ENTER to start", 260, 40);
        gm.font.getData().setScale(1f);
        gm.batch.end();
    }
}
