package com.goosequest.state;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.goosequest.GameManager;
import com.goosequest.battle.BattleSystem;
import com.goosequest.battle.HitEvent;
import com.goosequest.character.Character;
import com.goosequest.character.Enemy;
import com.goosequest.character.Goose;
import com.goosequest.item.Item;
import com.goosequest.skill.Skill;
import com.goosequest.util.Art;
import com.goosequest.util.Keys;
import com.goosequest.util.Palette;
import com.goosequest.world.MapEnemy;

import java.util.ArrayList;
import java.util.List;

/**
 * [คนที่ 1] หน้าจอต่อสู้: รับปุ่ม + วาดภาพ ส่วนกติกาอยู่ใน BattleSystem
 */
public class BattleState extends GameState {
    private enum Mode { COMMAND, SKILL, ITEM, ANIM, END }

    private static final String[] COMMANDS = {"Attack", "Skill", "Item", "Run"};

    private static final float SHAKE_TIME = 0.3f;    // ตัวที่โดนตีสั่น/กระพริบ
    private static final float FLOAT_TIME = 0.8f;    // ตัวเลขลอยขึ้นแล้วจาง
    private static final float EVENT_GAP = 0.4f;     // เว้นระยะระหว่างเหตุการณ์ในเทิร์นเดียวกัน
    private static final float SKIP_AFTER = 0.5f;    // รอเกินนี้แล้วกดข้ามได้
    private static final float FLOAT_RISE = 50f;
    private static final float FLOAT_SCALE = 2.2f;   // ขนาดตัวเลขปกติ (ฟอนต์ปกติ = 1)
    private static final float CRIT_SCALE = 3.4f;    // คริติคอลใหญ่กว่าและเป็นสีแดง
    private static final float STATUS_ROW_H = 38f;   // ความสูงแถวไอคอนสถานะใต้กรอบ HP
    private static final float MP_ROW_H = 18f;       // ที่ที่หลอด MP กินในกรอบ
    private static final float ICON_SIZE = 24f;
    private static final float ICON_STEP = 46f;      // ระยะห่างต่อไอคอน (6 สถานะยังพอดีกรอบ)

    /** เอฟเฟกต์หนึ่งอันที่กำลังเล่น: รอ delay ก่อนแล้วนับ age */
    private static final class Fx {
        final HitEvent event;
        float delay;
        float age;

        Fx(HitEvent event, float delay) {
            this.event = event;
            this.delay = delay;
        }

        boolean started() { return delay <= 0; }
    }

    private final List<Fx> effects = new ArrayList<>();
    private float animTimer;
    private Mode afterAnim = Mode.COMMAND;

    private final ExploreState explore;
    private final MapEnemy mapEnemy;
    private final BattleSystem battle;
    private final StatusIcons statusIcons;
    private Mode mode = Mode.COMMAND;
    private int cursor = 0;

    public BattleState(GameManager gm, ExploreState explore, MapEnemy mapEnemy) {
        super(gm);
        this.explore = explore;
        this.mapEnemy = mapEnemy;
        this.battle = new BattleSystem(gm.getHero(), mapEnemy.getEnemy(), gm.getInventory());
        this.statusIcons = new StatusIcons();
    }

    @Override
    public void dispose() {
        statusIcons.dispose();
    }

    @Override
    public void update(float dt) {
        updateEffects(dt);
        switch (mode) {
            case COMMAND -> updateCommand();
            case SKILL -> updateSkill();
            case ITEM -> updateItem();
            case ANIM -> updateAnim(dt);
            case END -> {
                if (Keys.confirm()) finish();
            }
        }
    }

    // ---------- เอฟเฟกต์ ----------

    /** ดึงเหตุการณ์จาก BattleSystem มาเข้าคิวเล่น ถ้ามีให้พักเมนูไว้ก่อนไปโหมด next */
    private void beginAnim(Mode next) {
        List<HitEvent> events = battle.drainEvents();
        if (events.isEmpty()) {
            mode = next;
            return;
        }
        float delay = 0;
        for (HitEvent e : events) {
            effects.add(new Fx(e, delay));
            delay += EVENT_GAP;
        }
        animTimer = 0;
        afterAnim = next;
        mode = Mode.ANIM;
    }

    /** เอฟเฟกต์เดินต่อแม้ผู้เล่นกดข้ามไปแล้ว */
    private void updateEffects(float dt) {
        for (Fx fx : effects) {
            if (fx.started()) fx.age += dt;
            else fx.delay -= dt;
        }
        effects.removeIf(fx -> fx.age >= FLOAT_TIME);
    }

    private void updateAnim(float dt) {
        animTimer += dt;
        boolean skip = animTimer >= SKIP_AFTER && Keys.confirm();
        if (effects.isEmpty() || skip) mode = afterAnim;
    }

    /** ตัวที่โดนดาเมจกำลังสั่นอยู่ไหม คืนเวลาที่ผ่านไป (หรือ -1 ถ้าไม่สั่น) */
    private float hitAge(boolean onHero) {
        for (Fx fx : effects) {
            if (fx.event.isOnHero() == onHero
                    && fx.event.getKind() == HitEvent.Kind.DAMAGE
                    && fx.started() && fx.age < SHAKE_TIME) {
                return fx.age;
            }
        }
        return -1f;
    }

    private float shakeOffset(float age) {
        if (age < 0) return 0;
        return (float) Math.sin(age * 70f) * 8f * (1f - age / SHAKE_TIME);
    }

    /** กระพริบ: ซ่อนตัวเป็นช่วง ๆ ระหว่างสั่น */
    private boolean blinkHidden(float age) {
        return age >= 0 && ((int) (age / 0.05f)) % 2 == 1;
    }

    private void updateCommand() {
        if (Keys.upJust()) cursor = (cursor + COMMANDS.length - 1) % COMMANDS.length;
        if (Keys.downJust()) cursor = (cursor + 1) % COMMANDS.length;
        if (!Keys.confirm()) return;

        switch (cursor) {
            case 0 -> battle.attack();
            case 1 -> {
                mode = Mode.SKILL;
                cursor = 0;
                return;
            }
            case 2 -> {
                mode = Mode.ITEM;
                cursor = 0;
                return;
            }
            case 3 -> battle.flee();
        }
        beginAnim(battle.isOngoing() ? Mode.COMMAND : Mode.END);
    }

    private void updateSkill() {
        List<Skill> skills = battle.getHero().getSkills();
        int options = skills.size() + 1; // + "Back"
        if (Keys.upJust()) cursor = (cursor + options - 1) % options;
        if (Keys.downJust()) cursor = (cursor + 1) % options;
        if (Keys.back() || (Keys.confirm() && cursor == skills.size())) {
            mode = Mode.COMMAND;
            cursor = 1;
            return;
        }
        if (Keys.confirm()) {
            // MP ไม่พอ → ไม่เสียเทิร์น อยู่ในเมนูสกิลต่อ
            if (!battle.useSkill(cursor)) return;
            beginAnim(battle.isOngoing() ? Mode.COMMAND : Mode.END);
            cursor = 0;
        }
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
            beginAnim(battle.isOngoing() ? Mode.COMMAND : Mode.END);
            cursor = 0;
        }
    }

    private void finish() {
        battle.leaveBattle(); // ศัตรูที่เราหนีมาจะฟื้นตอนนี้ ไม่ใช่ตอนกดหนี (กันตัวเลข HP กระโดดบนจอ)
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

        float enemyHit = hitAge(false);
        float heroHit = hitAge(true);
        if (enemy.isAlive()) {
            Art.shadow(gm.shapes, 610, 414, 150);
            if (!blinkHidden(enemyHit)) {
                Art.enemy(gm.shapes, enemy.getColor(), 540 + shakeOffset(enemyHit), 400 + Art.bob(1.3f, 3f), 140);
            }
        }
        if (hero.isAlive()) {
            Art.shadow(gm.shapes, 200, 232, 150);
            if (!blinkHidden(heroHit)) {
                Art.goose(gm.shapes, 120 + shakeOffset(heroHit), 230 + Art.bob(0f, 2f), 5f, false);
            }
        }

        statusPanel(enemy, 40, 470);
        statusPanel(hero, 470, 250);

        // กล่องข้อความ + เมนู
        Art.box(gm.shapes, 10, 10, 560, 180);
        Art.box(gm.shapes, 580, 10, 210, 180);
        if (mode != Mode.END && mode != Mode.ANIM) {
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

    /** y คือขอบล่างของส่วนบน (ชื่อ + HP/MP) ส่วนแถวไอคอนสถานะต่อลงไปข้างล่าง STATUS_ROW_H */
    private void statusPanel(Character c, float x, float y) {
        float shift = panelShift(c);
        Art.box(gm.shapes, x, y - STATUS_ROW_H + shift, 290, 80 + STATUS_ROW_H - shift);
        Art.bar(gm.shapes, x + 50, y + 30, 220, 12, (float) c.getHp() / c.getMaxHp(), Palette.HP);
        if (c.getMaxMp() > 0) {
            Art.bar(gm.shapes, x + 50, y + 12, 220, 8, (float) c.getMp() / c.getMaxMp(), Palette.MP);
        }
    }

    /** ตัวที่ไม่มี MP (ศัตรู) ไม่ต้องเว้นที่หลอด MP กรอบจึงสั้นลงเท่านี้ */
    private static float panelShift(Character c) {
        return c.getMaxMp() > 0 ? 0f : MP_ROW_H;
    }

    /** แถวไอคอนสถานะ: ไอคอนแต่ละอันตามด้วยจำนวนเทิร์นที่เหลือ (ต้องเรียกตอน batch เปิดอยู่) */
    private void drawStatusIcons(Character c, float x, float y) {
        float cx = x + 14;
        float iconY = y - STATUS_ROW_H + panelShift(c) + 8;
        for (var entry : c.getStatuses().entrySet()) {
            gm.batch.draw(statusIcons.get(entry.getKey()), cx, iconY, ICON_SIZE, ICON_SIZE);
            gm.font.draw(gm.batch, String.valueOf(entry.getValue()), cx + ICON_SIZE + 3, iconY + ICON_SIZE - 5);
            cx += ICON_STEP;
        }
    }

    private void drawText(Goose hero, Enemy enemy) {
        gm.batch.setProjectionMatrix(uiCam.combined);
        gm.batch.begin();
        gm.font.setColor(Color.WHITE);
        gm.font.draw(gm.batch, enemy.getName() + (enemy.isStunned() ? "  [STUNNED]" : ""), 55, 538);
        gm.font.draw(gm.batch, "HP", 55, 512);
        gm.font.draw(gm.batch, enemy.getHp() + "/" + enemy.getMaxHp(), 230, 538);
        gm.batch.setColor(Color.WHITE);
        drawStatusIcons(enemy, 40, 470);
        drawStatusIcons(hero, 470, 250);
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
                gm.font.setColor(i == cursor ? Color.YELLOW : Color.WHITE);
                gm.font.draw(gm.batch, label, 620, 165 - i * 30);
            }
        } else if (mode == Mode.SKILL) {
            List<Skill> skills = hero.getSkills();
            // แสดงได้สูงสุด 5 บรรทัด เลื่อนตาม cursor
            int first = Math.max(0, Math.min(cursor - 4, skills.size() + 1 - 5));
            for (int row = 0; row < 5 && first + row <= skills.size(); row++) {
                int i = first + row;
                String label = i == skills.size()
                        ? "Back"
                        : skills.get(i).getName() + " (" + skills.get(i).getMpCost() + ")";
                boolean usable = i == skills.size() || hero.canUseSkill(i);
                gm.font.setColor(i == cursor ? Color.YELLOW : usable ? Color.WHITE : Color.GRAY);
                gm.font.draw(gm.batch, label, 620, 165 - row * 30);
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
        } else if (mode == Mode.END) {
            gm.font.setColor(Color.YELLOW);
            gm.font.draw(gm.batch, "ENTER to continue", 600, 100);
        }
        drawFloatingText();
        gm.font.setColor(Color.WHITE);
        gm.batch.end();
    }

    /** ตัวเลขดาเมจ/ฮีล/บัฟที่ลอยขึ้นเหนือตัวละครแล้วจางหาย (ต้องเรียกตอน batch เปิดอยู่) */
    private void drawFloatingText() {
        for (Fx fx : effects) {
            if (!fx.started()) continue;
            float t = fx.age / FLOAT_TIME;
            float baseX = fx.event.isOnHero() ? 170 : 590;
            float baseY = fx.event.isOnHero() ? 340 : 560;
            float x = baseX;
            float y = baseY + t * FLOAT_RISE;
            float alpha = 1f - t;
            String text = fx.event.getLabel();
            boolean crit = fx.event.isCritical();

            gm.font.getData().setScale(crit ? CRIT_SCALE : FLOAT_SCALE);
            float shadow = crit ? 4 : 3;
            gm.font.setColor(0f, 0f, 0f, alpha); // ขอบดำให้อ่านง่ายบนฉากสว่าง
            gm.font.draw(gm.batch, text, x + shadow, y - shadow);
            gm.font.setColor(floatColor(fx.event.getKind(), crit, alpha));
            gm.font.draw(gm.batch, text, x, y);
        }
        gm.font.getData().setScale(1f);
    }

    private Color floatColor(HitEvent.Kind kind, boolean crit, float alpha) {
        return switch (kind) {
            case DAMAGE -> crit ? new Color(1f, 0.1f, 0.1f, alpha) : new Color(1f, 1f, 1f, alpha);
            case HEAL -> new Color(0.3f, 1f, 0.4f, alpha);
            case BUFF -> new Color(0.4f, 0.85f, 1f, alpha);
            case DEBUFF -> new Color(1f, 0.6f, 0.2f, alpha);
            case STATUS -> new Color(1f, 0.9f, 0.2f, alpha);
        };
    }
}
