package com.goosequest.battle;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.graphics.Color;
import com.goosequest.battle.HitEvent.Kind;
import com.goosequest.character.Character;
import com.goosequest.character.Enemy;
import com.goosequest.character.Goose;
import com.goosequest.character.SwordmanGoose;
import com.goosequest.item.Bread;
import com.goosequest.item.Inventory;
import com.goosequest.skill.Skill;
import com.goosequest.skill.WarCry;
import com.goosequest.util.Dice;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BiFunction;

class BattleSystemTest {
    /** ศัตรูอึดและตีเบา จะได้ไม่ตายกลางการทดสอบ */
    private static class Sponge extends Enemy {
        Sponge() { super("Sponge", 1000, 1, 0, 0); }
        @Override public Color getColor() { return Color.WHITE; }
    }

    /** สกิลจำลองที่ทำอะไรก็ได้กับผู้ใช้/เป้าหมาย (MP 0) */
    private static Skill skill(BiFunction<Character, Character, String> effect) {
        return new Skill() {
            @Override public String getName() { return "Test"; }
            @Override public int getMpCost() { return 0; }
            @Override public String use(Character user, Character target) { return effect.apply(user, target); }
        };
    }

    private final Goose hero = new SwordmanGoose();
    private final Enemy enemy = new Sponge();
    private final Inventory inventory = new Inventory();
    private final BattleSystem battle = new BattleSystem(hero, enemy, inventory);

    @Test
    void attackRecordsDamageToEnemyThenCounterattackOnHero() {
        int enemyHp = enemy.getHp();
        battle.attack();

        List<HitEvent> events = battle.drainEvents();
        assertEquals(2, events.size());
        assertEquals(Kind.DAMAGE, events.get(0).getKind());
        assertFalse(events.get(0).isOnHero());
        assertEquals(enemyHp - enemy.getHp(), events.get(0).getAmount());
        assertEquals(Kind.DAMAGE, events.get(1).getKind());
        assertTrue(events.get(1).isOnHero());
    }

    @Test
    void healRecordsHealEventOnHero() {
        hero.takeDamage(hero.getDef() + 40); // ดาเมจจริง 40 มากกว่าที่ขนมปังฟื้น (35)
        int hp = hero.getHp();
        inventory.add(new Bread());
        battle.useItem(inventory.getItems().get(0));

        HitEvent first = battle.drainEvents().get(0);
        assertEquals(Kind.HEAL, first.getKind());
        assertTrue(first.isOnHero());
        assertEquals(35, first.getAmount());
        assertEquals("+35", first.getLabel());
        assertTrue(hero.getHp() > hp);
    }

    @Test
    void healAtFullHpRecordsNoHealEvent() {
        inventory.add(new Bread());
        battle.useItem(inventory.getItems().get(0));

        for (HitEvent e : battle.drainEvents()) {
            assertNotEquals(Kind.HEAL, e.getKind());
        }
    }

    @Test
    void buffSkillWithoutHpChangeStillRecordsEvent() {
        hero.learnSkill(skill((user, target) -> {
            user.buffAtk(3);
            return "buffed";
        }));
        battle.useSkill(1);

        HitEvent first = battle.drainEvents().get(0);
        assertEquals(Kind.BUFF, first.getKind());
        assertTrue(first.isOnHero());
        assertEquals("ATK +3", first.getLabel());
    }

    @Test
    void tempDefBuffRecordsEventAndIsClearedWhenBattleIsWon() {
        int baseDef = hero.getDef();
        hero.learnSkill(skill((user, target) -> {
            user.addBuff(0, 4);
            target.takeDamage(10000); // ล้มศัตรูในเทิร์นเดียว
            return "buffed and won";
        }));
        battle.useSkill(1);

        assertEquals(BattleSystem.Result.WON, battle.getResult());
        assertEquals(baseDef, hero.getDef());
        boolean sawDefBuff = battle.drainEvents().stream()
                .anyMatch(e -> e.getKind() == Kind.BUFF && "DEF +4".equals(e.getLabel()));
        assertTrue(sawDefBuff);
    }

    @Test
    void buffsAreClearedWhenHeroLoses() {
        int baseAtk = hero.getAtk();
        hero.addBuff(5, 0);
        enemy.addBuff(5, 0);
        hero.takeDamage(10000 + hero.getDef());
        battle.attack(); // ห่านที่ล้มอยู่ยังตีได้ในเทสต์ ศัตรูตอบโต้แล้วเช็กผลแพ้

        assertEquals(BattleSystem.Result.LOST, battle.getResult());
        assertEquals(baseAtk, hero.getAtk());
        assertEquals(1, enemy.getAtk()); // ATK เดิมของ Sponge
    }

    @Test
    void buffsAreClearedWhenBattleEndsByFleeing() {
        int baseAtk = hero.getAtk();
        int baseDef = hero.getDef();
        hero.addBuff(5, 5);
        // หนีวนไปจนกว่าจะสำเร็จ (Sponge ตีเบามาก ห่านไม่ล้มก่อน)
        for (int i = 0; i < 500 && battle.isOngoing(); i++) {
            battle.flee();
        }

        assertEquals(BattleSystem.Result.FLED, battle.getResult());
        assertEquals(baseAtk, hero.getAtk());
        assertEquals(baseDef, hero.getDef());
    }

    @Test
    void stunRecordsStatusEventAndSkipsEnemyTurn() {
        hero.learnSkill(skill((user, target) -> {
            target.setStunned(true);
            return "stunned";
        }));
        battle.useSkill(1);

        List<HitEvent> events = battle.drainEvents();
        assertEquals(1, events.size());
        assertEquals(Kind.STATUS, events.get(0).getKind());
        assertFalse(events.get(0).isOnHero());
        assertEquals("STUN", events.get(0).getLabel());
    }

    @Test
    void criticalHitIsFlaggedOnlyOnTheEventThatWasCritical() {
        hero.learnSkill(skill((user, target) -> {
            target.takeDamage(10, true);
            return "crit";
        }));
        battle.useSkill(1);

        List<HitEvent> events = battle.drainEvents();
        assertTrue(events.get(0).isCritical());
        assertFalse(events.get(0).isOnHero());
        // ศัตรูตีกลับด้วยดาเมจธรรมดา ต้องไม่ติดคริจากครั้งก่อน
        assertTrue(events.get(1).isOnHero());
        assertFalse(events.get(1).isCritical());
    }

    @Test
    void normalDamageIsNotCritical() {
        // ห่านมีโอกาสคริ 5% ถ้าไม่ล็อก seed test นี้จะสุ่มพังเองเป็นบางครั้ง
        Dice.setSeed(1L);
        battle.attack();
        for (HitEvent e : battle.drainEvents()) assertFalse(e.isCritical());
    }

    @Test
    void drainEventsClearsTheList() {
        battle.attack();
        assertFalse(battle.drainEvents().isEmpty());
        assertTrue(battle.drainEvents().isEmpty());
    }
}
