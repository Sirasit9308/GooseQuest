package com.goosequest.character;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.graphics.Color;
import com.goosequest.battle.BattleSystem;
import com.goosequest.item.Inventory;
import org.junit.jupiter.api.Test;

class StatusEffectTest {
    private static class Dummy extends Character {
        Dummy() { super("Dummy", 100, 0, 20, 10); }
    }

    /** ศัตรูอึด ตีเบา ไม่ตายกลางการทดสอบ */
    private static class Sponge extends Enemy {
        Sponge() { super("Sponge", 100000, 1, 0, 0); }
        @Override public Color getColor() { return Color.WHITE; }
    }

    private final Character c = new Dummy();

    @Test
    void statEffectsUseTwentyFivePercentAndFiftyPercent() {
        c.applyStatus(StatusEffect.WEAKNESS, 2);
        assertEquals(15, c.getAtk());
        c.applyStatus(StatusEffect.VULNERABLE, 2);
        assertEquals(8, c.getDef()); // 10 - 2.5 → ลด 2
        c.clearBuffs();

        c.applyStatus(StatusEffect.STRENGTH, 2);
        c.applyStatus(StatusEffect.DURABILITY, 2);
        assertEquals(25, c.getAtk());
        assertEquals(12, c.getDef());
        c.clearBuffs();

        c.applyStatus(StatusEffect.ENRAGE, 2);
        assertEquals(30, c.getAtk());
    }

    @Test
    void reapplyingKeepsWhicheverDurationIsLonger() {
        c.applyStatus(StatusEffect.POISON, 5);
        assertFalse(c.applyStatus(StatusEffect.POISON, 2)); // สั้นกว่า: คงของเดิม
        assertEquals(5, c.getStatusTurns(StatusEffect.POISON));
        assertFalse(c.applyStatus(StatusEffect.POISON, 5)); // เท่ากัน: คงของเดิม
        assertTrue(c.applyStatus(StatusEffect.POISON, 7)); // ยาวกว่า: เปลี่ยน
        assertEquals(7, c.getStatusTurns(StatusEffect.POISON));
    }

    @Test
    void reapplyingRefreshesInsteadOfStacking() {
        c.applyStatus(StatusEffect.STRENGTH, 3);
        c.applyStatus(StatusEffect.STRENGTH, 3);
        assertEquals(25, c.getAtk());
        assertEquals(3, c.getStatusTurns(StatusEffect.STRENGTH));
    }

    @Test
    void poisonHitsFivePercentOfMaxHpAtTurnStartAndCountsDownThen() {
        c.applyStatus(StatusEffect.POISON, 2);
        assertEquals(5, c.startTurn());
        assertEquals(95, c.getHp());
        assertEquals(1, c.getStatusTurns(StatusEffect.POISON));
        c.endTurn(); // ท้ายเทิร์นไม่ลดพิษซ้ำ
        assertEquals(1, c.getStatusTurns(StatusEffect.POISON));
        assertEquals(5, c.startTurn());
        assertFalse(c.hasStatus(StatusEffect.POISON));
        assertEquals(0, c.startTurn());
    }

    @Test
    void nonPoisonStatusCountsDownAtEndOfOwnTurn() {
        c.applyStatus(StatusEffect.WEAKNESS, 2);
        c.startTurn();
        assertTrue(c.endTurn().isEmpty());
        c.startTurn();
        assertEquals(java.util.List.of(StatusEffect.WEAKNESS), c.endTurn());
        assertEquals(20, c.getAtk());
    }

    @Test
    void selfAppliedStatusDoesNotLoseTimeInTheTurnItWasUsed() {
        c.startTurn();
        c.applyStatus(StatusEffect.ENRAGE, 3);
        c.endTurn();
        assertEquals(3, c.getStatusTurns(StatusEffect.ENRAGE));
    }

    @Test
    void poisonAndWeaknessFollowTheTurnOrderInBattle() {
        Goose hero = new SwordmanGoose();
        Enemy enemy = new Sponge();
        // ติดสถานะก่อนเปิดเทิร์น (เหมือนโดนติดจากเทิร์นของอีกฝั่ง ไม่ใช่ติดตัวเองระหว่างเทิร์น)
        hero.applyStatus(StatusEffect.POISON, 2);
        hero.applyStatus(StatusEffect.WEAKNESS, 2);
        enemy.applyStatus(StatusEffect.POISON, 2);
        BattleSystem battle = new BattleSystem(hero, enemy, new Inventory());
        int heroPoison = Math.max(1, (int) Math.floor(hero.getMaxHp() * 0.05));
        int enemyPoison = Math.max(1, (int) Math.floor(enemy.getMaxHp() * 0.05));

        // ห่านเริ่มเทิร์นแรกโดยยังไม่เสียเลือด (พิษทำงานตอนเริ่มเทิร์นถัดไป)
        battle.attack();
        // หลังห่าน action: weakness ลด 1, ศัตรูโดนพิษ + พิษลด 1 ก่อนตี, แล้วห่านเข้าเทิร์นใหม่โดนพิษ
        assertEquals(1, hero.getStatusTurns(StatusEffect.WEAKNESS));
        assertEquals(1, enemy.getStatusTurns(StatusEffect.POISON));
        assertEquals(1, hero.getStatusTurns(StatusEffect.POISON));
        assertTrue(hero.getMaxHp() - hero.getHp() >= heroPoison);
        assertTrue(enemy.getMaxHp() - enemy.getHp() >= enemyPoison);
        assertTrue(battle.getRecentLog(10).stream().anyMatch(l -> l.equals("Sponge suffers " + enemyPoison + " poison damage!")));
        assertTrue(battle.getRecentLog(10).stream().anyMatch(l -> l.equals(hero.getName() + " suffers " + heroPoison + " poison damage!")));
    }

    @Test
    void statusesAreClearedWhenBattleEnds() {
        Goose hero = new SwordmanGoose();
        Enemy enemy = new Sponge();
        BattleSystem battle = new BattleSystem(hero, enemy, new Inventory());
        hero.applyStatus(StatusEffect.STRENGTH, 5);
        for (int i = 0; i < 500 && battle.isOngoing(); i++) battle.flee();
        assertFalse(hero.hasStatus(StatusEffect.STRENGTH));
    }
}
