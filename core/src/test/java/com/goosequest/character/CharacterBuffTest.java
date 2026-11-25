package com.goosequest.character;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CharacterBuffTest {
    private static class Dummy extends Character {
        Dummy() { super("Dummy", 1000, 0, 10, 5); }
    }

    private final Character c = new Dummy();

    @Test
    void addBuffRaisesAtkAndDef() {
        c.addBuff(3, 2);
        assertEquals(13, c.getAtk());
        assertEquals(7, c.getDef());
    }

    @Test
    void buffsStack() {
        c.addBuff(3, 0);
        c.addBuff(2, 1);
        assertEquals(15, c.getAtk());
        assertEquals(6, c.getDef());
    }

    @Test
    void clearBuffsRestoresBaseValues() {
        c.addBuff(3, 2);
        c.clearBuffs();
        assertEquals(10, c.getAtk());
        assertEquals(5, c.getDef());
    }

    @Test
    void negativeBuffLowersStats() {
        c.addBuff(-4, -2);
        assertEquals(6, c.getAtk());
        assertEquals(3, c.getDef());
    }

    @Test
    void statsNeverGoBelowZero() {
        c.addBuff(-50, -50);
        assertEquals(0, c.getAtk());
        assertEquals(0, c.getDef());
    }

    @Test
    void clampedStatRecoversWhenDebuffIsPartlyUndone() {
        c.addBuff(-50, 0);
        c.addBuff(45, 0); // รวมสุทธิ -5 จากฐาน 10 → 5 (ไม่ใช่นับจาก 0)
        assertEquals(5, c.getAtk());
    }

    @Test
    void defBuffReducesDamageTaken() {
        c.addBuff(0, 3);
        assertEquals(12, c.takeDamage(20)); // 20 - (5+3)
    }

    @Test
    void permanentBuffAtkSurvivesClearBuffs() {
        c.buffAtk(4);
        c.addBuff(2, 0);
        c.clearBuffs();
        assertEquals(14, c.getAtk());
    }
}
