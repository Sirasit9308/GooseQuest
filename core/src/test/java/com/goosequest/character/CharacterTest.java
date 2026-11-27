package com.goosequest.character;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** ทดสอบกติกาพื้นฐานของ Character โดยไม่ต้องเปิดหน้าต่างเกม */
class CharacterTest {
    private static Character dummy(int maxHp, int maxMp, int atk, int def) {
        return new Character("Dummy", maxHp, maxMp, atk, def) { };
    }

    @Test
    void takeDamageNeverMakesHpNegative() {
        Character c = dummy(50, 0, 1, 0);
        c.takeDamage(10000);
        assertEquals(0, c.getHp());
        assertFalse(c.isAlive());
        c.takeDamage(10000); // โดนซ้ำตอนล้มแล้ว HP ต้องยังเป็น 0
        assertEquals(0, c.getHp());
    }

    @Test
    void damageIsAtLeastOneEvenWhenDefenseIsHuge() {
        Character c = dummy(50, 0, 1, 500);
        int dmg = c.takeDamage(3);
        assertEquals(1, dmg);
        assertEquals(49, c.getHp());
    }

    @Test
    void damageIsNeverNegativeForZeroOrNegativeRawDamage() {
        Character c = dummy(50, 0, 1, 0);
        assertTrue(c.takeDamage(0) >= 0);
        assertTrue(c.takeDamage(-20) >= 0);
        assertTrue(c.getHp() <= 50);
    }

    @Test
    void takeDamageSubtractsDefense() {
        Character c = dummy(50, 0, 1, 4);
        assertEquals(6, c.takeDamage(10));
        assertEquals(44, c.getHp());
    }

    @Test
    void healNeverExceedsMaxHpAndReportsActualAmount() {
        Character c = dummy(50, 0, 1, 0);
        c.takeDamage(10);
        assertEquals(10, c.heal(100));
        assertEquals(50, c.getHp());
        assertEquals(0, c.heal(5));
    }

    @Test
    void spendMpReducesMp() {
        Character c = dummy(10, 20, 1, 0);
        assertTrue(c.spendMp(8));
        assertEquals(12, c.getMp());
    }

    @Test
    void spendMpFailsWhenNotEnoughAndKeepsMp() {
        Character c = dummy(10, 5, 1, 0);
        assertFalse(c.spendMp(6));
        assertEquals(5, c.getMp());
        assertTrue(c.spendMp(5)); // พอดีเป๊ะใช้ได้
        assertEquals(0, c.getMp());
    }

    @Test
    void restoreMpNeverExceedsMaxMp() {
        Character c = dummy(10, 20, 1, 0);
        c.spendMp(5);
        assertEquals(5, c.restoreMp(50));
        assertEquals(20, c.getMp());
    }

    @Test
    void levelUpHappensExactlyAtExpToNext() {
        Goose g = new SwordmanGoose();
        int need = g.expToNext();
        g.gainExp(need - 1);
        assertEquals(1, g.getLevel());
        assertEquals(need - 1, g.getExp());
        g.gainExp(1);
        assertEquals(2, g.getLevel());
        assertEquals(0, g.getExp());
    }

    @Test
    void levelUpCarriesOverExtraExp() {
        Goose g = new SwordmanGoose();
        g.gainExp(g.expToNext() + 5);
        assertEquals(2, g.getLevel());
        assertEquals(5, g.getExp());
    }

    @Test
    void levelUpRestoresHpAndMpToFull() {
        Goose g = new SwordmanGoose();
        g.takeDamage(g.getDef() + 20);
        g.spendMp(1);
        g.gainExp(g.expToNext());
        assertEquals(g.getMaxHp(), g.getHp());
        assertEquals(g.getMaxMp(), g.getMp());
    }
}
