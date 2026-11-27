package com.goosequest.skill;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.graphics.Color;
import com.goosequest.battle.BattleSystem;
import com.goosequest.character.Enemy;
import com.goosequest.character.SwordmanGoose;
import com.goosequest.item.Inventory;
import org.junit.jupiter.api.Test;

class WarCryTest {
    /** ศัตรูอึดและตีเบา จะได้ไม่ตายกลางการทดสอบ */
    private static class Sponge extends Enemy {
        Sponge() { super("Sponge", 100000, 1, 0, 0); }
        @Override public Color getColor() { return Color.WHITE; }
    }

    private final SwordmanGoose hero = new SwordmanGoose();
    private final BattleSystem battle = new BattleSystem(hero, new Sponge(), new Inventory());
    private final int base = hero.getAtk();

    WarCryTest() {
        hero.learnSkill(new WarCry());
    }

    @Test
    void raisesAtkByHalfImmediately() {
        battle.useSkill(1);
        assertEquals(base + base / 2, hero.getAtk());
    }

    @Test
    void lastsThreeTurnsNotCountingTheTurnUsed() {
        battle.useSkill(1);
        battle.attack(); // เทิร์นที่ 1
        assertEquals(base + base / 2, hero.getAtk());
        battle.attack(); // เทิร์นที่ 2
        assertEquals(base + base / 2, hero.getAtk());
        battle.attack(); // เทิร์นที่ 3 ยังได้ ATK x2 ตอนตี แล้วบัฟหมดหลังจบเทิร์น
        assertEquals(base, hero.getAtk());
    }

    @Test
    void reuseDoesNotStackButResetsDuration() {
        battle.useSkill(1);
        battle.attack();
        battle.attack(); // เหลือเทิร์นสุดท้าย
        battle.useSkill(1); // ใช้ซ้ำ
        assertEquals(base + base / 2, hero.getAtk()); // ไม่ซ้อนเป็น x3

        battle.attack();
        battle.attack();
        assertEquals(base + base / 2, hero.getAtk()); // นับใหม่: ยังอยู่หลังผ่านไป 2 เทิร์น
        battle.attack();
        assertEquals(base, hero.getAtk());
    }

    @Test
    void buffIsClearedWhenBattleEnds() {
        battle.useSkill(1);
        for (int i = 0; i < 500 && battle.isOngoing(); i++) battle.flee();
        assertEquals(BattleSystem.Result.FLED, battle.getResult());
        assertEquals(base, hero.getAtk());
    }

    @Test
    void swordmanLearnsWarCryAtLevelThree() {
        SwordmanGoose fresh = new SwordmanGoose();
        while (fresh.getLevel() < 3) fresh.gainExp(1000);
        assertTrue(fresh.getSkills().stream().anyMatch(s -> s instanceof WarCry));
    }
}
