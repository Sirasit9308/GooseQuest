package com.goosequest.battle;

import com.goosequest.character.Character;
import com.goosequest.character.Enemy;
import com.goosequest.character.Goose;
import com.goosequest.item.Inventory;
import com.goosequest.item.Item;
import com.goosequest.util.Dice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * [คนที่ 1] กติกาการต่อสู้แบบผลัดตา (ไม่มีโค้ดวาดภาพเลย)
 * แยกออกจาก BattleState เพื่อให้ทดสอบได้โดยไม่ต้องเปิดหน้าต่างเกม
 *
 * ลำดับ: ผู้เล่นเลือกคำสั่ง → ถ้าศัตรูยังไม่ตาย ศัตรูเล่นเทิร์นตัวเอง → วนใหม่
 */
public class BattleSystem {
    public enum Result { ONGOING, WON, LOST, FLED }

    private final Goose hero;
    private final Enemy enemy;
    private final Inventory inventory;
    private final List<String> log = new ArrayList<>();
    private final List<HitEvent> events = new ArrayList<>();
    private Result result = Result.ONGOING;

    public BattleSystem(Goose hero, Enemy enemy, Inventory inventory) {
        this.hero = hero;
        this.enemy = enemy;
        this.inventory = inventory;
        log.add(enemy.getName() + " blocks your path!");
    }

    // ---------- คำสั่งของผู้เล่น ----------

    public void attack() {
        if (!isOngoing()) return;
        log.add(track(() -> hero.attack(enemy)));
        finishPlayerTurn();
    }

    /** คืน false ถ้า MP ไม่พอ (ไม่เสียเทิร์น) */
    public boolean useSkill(int index) {
        if (!isOngoing()) return false;
        if (!hero.canUseSkill(index)) {
            log.add("Not enough MP!");
            return false;
        }
        log.add(track(() -> hero.useSkill(index, enemy)));
        finishPlayerTurn();
        return true;
    }

    public void useItem(Item item) {
        if (!isOngoing() || !inventory.remove(item)) return;
        log.add(track(() -> item.use(hero)));
        finishPlayerTurn();
    }

    public void flee() {
        if (!isOngoing()) return;
        if (!enemy.canFlee()) {
            log.add("You can't run from this fight!");
            return;
        }
        if (Dice.chance(0.6)) {
            log.add("You waddled away safely!");
            result = Result.FLED;
        } else {
            log.add("Couldn't escape!");
            enemyTurn();
        }
    }

    // ---------- เหตุการณ์สำหรับเอฟเฟกต์ ----------

    /** สถานะที่เปรียบเทียบก่อน/หลังการกระทำ เพื่อรู้ว่าอะไรเปลี่ยนไป */
    private record Snapshot(int hp, int atk, int def, boolean stunned) {
        static Snapshot of(Character c) {
            return new Snapshot(c.getHp(), c.getAtk(), c.getDef(), c.isStunned());
        }
    }

    /**
     * รันการกระทำแล้วบันทึกว่าตัวละครสองฝั่งเปลี่ยนอะไรไปบ้าง
     * วิธีนี้ไม่ต้องให้ Skill / Item / Enemy ส่งตัวเลขกลับมาเอง สกิลใหม่ (เช่นบัฟ) จึงได้เอฟเฟกต์ทันที
     */
    private String track(Supplier<String> action) {
        Snapshot heroBefore = Snapshot.of(hero);
        Snapshot enemyBefore = Snapshot.of(enemy);
        String message = action.get();
        diff(true, heroBefore, Snapshot.of(hero));
        diff(false, enemyBefore, Snapshot.of(enemy));
        return message;
    }

    private void diff(boolean onHero, Snapshot before, Snapshot after) {
        if (after.hp() < before.hp()) {
            events.add(HitEvent.damage(onHero, before.hp() - after.hp()));
        } else if (after.hp() > before.hp()) {
            events.add(HitEvent.heal(onHero, after.hp() - before.hp()));
        }
        statEvent(onHero, "ATK", after.atk() - before.atk());
        statEvent(onHero, "DEF", after.def() - before.def());
        if (!before.stunned() && after.stunned()) {
            events.add(HitEvent.other(HitEvent.Kind.STATUS, onHero, "STUN"));
        }
    }

    private void statEvent(boolean onHero, String stat, int change) {
        if (change == 0) return;
        HitEvent.Kind kind = change > 0 ? HitEvent.Kind.BUFF : HitEvent.Kind.DEBUFF;
        String sign = change > 0 ? " +" : " ";
        events.add(HitEvent.other(kind, onHero, stat + sign + change));
    }

    /** เหตุการณ์ที่เกิดขึ้นตั้งแต่ครั้งก่อนที่เรียก (เรียงตามลำดับเวลา) แล้วล้างทิ้ง */
    public List<HitEvent> drainEvents() {
        List<HitEvent> copy = new ArrayList<>(events);
        events.clear();
        return copy;
    }

    // ---------- ภายใน ----------

    private void finishPlayerTurn() {
        if (!enemy.isAlive()) {
            win();
        } else {
            enemyTurn();
        }
    }

    private void enemyTurn() {
        if (enemy.isStunned()) {
            enemy.setStunned(false);
            log.add(enemy.getName() + " is stunned and can't move!");
        } else {
            log.add(track(() -> enemy.takeTurn(hero)));
        }
        if (!hero.isAlive()) {
            result = Result.LOST;
            log.add(hero.getName() + " fainted...");
        }
    }

    private void win() {
        result = Result.WON;
        log.add(enemy.getName() + " was defeated!");
        log.add(hero.gainExp(enemy.getExpReward()));
        Item drop = enemy.rollDrop();
        if (drop != null) {
            inventory.add(drop);
            log.add(enemy.getName() + " dropped " + drop.getName() + "!");
        }
    }

    // ---------- getter ----------

    public boolean isOngoing() { return result == Result.ONGOING; }
    public Result getResult() { return result; }
    public Goose getHero() { return hero; }
    public Enemy getEnemy() { return enemy; }
    public Inventory getInventory() { return inventory; }

    /** ข้อความล่าสุด n บรรทัด */
    public List<String> getRecentLog(int n) {
        int from = Math.max(0, log.size() - n);
        return Collections.unmodifiableList(log.subList(from, log.size()));
    }
}
