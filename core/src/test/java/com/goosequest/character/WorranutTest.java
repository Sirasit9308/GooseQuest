package com.goosequest.character;

import static org.junit.jupiter.api.Assertions.*;

import com.goosequest.item.PondWater;
import org.junit.jupiter.api.Test;

class WorranutTest {
    private final Goose hero = new SwordmanGoose();

    /** ให้ Worranut ออกท่า 1 ครั้งแล้วคืนข้อความ (ล้างสถานะ/ฮีลห่านก่อนทุกครั้งเพื่อดูผลของท่าเดียว) */
    private String turn(Worranut w) {
        hero.clearBuffs();
        hero.heal(hero.getMaxHp());
        return w.takeTurn(hero);
    }

    @Test
    void hasTheDesignedStats() {
        Worranut w = new Worranut();
        assertEquals(180, w.getMaxHp());
        assertEquals(18, w.getAtk());
        assertEquals(5, w.getDef());
        assertEquals(55, w.getExpReward());
        assertTrue(w.canFlee());
        assertFalse(w.isBoss());
    }

    @Test
    void alwaysDropsPondWater() {
        for (int i = 0; i < 50; i++) assertInstanceOf(PondWater.class, new Worranut().rollDrop());
    }

    @Test
    void licksOnTurns1And4And7AndNeverOtherwise() {
        Worranut w = new Worranut();
        for (int round = 0; round < 20; round++) {
            for (int t = 1; t <= 9; t++) {
                String msg = turn(w);
                boolean lick = t % 3 == 1;
                assertEquals(lick, msg.contains("LICK"), "turn " + t + ": " + msg);
                assertEquals(lick, hero.hasStatus(StatusEffect.WEAKNESS), "turn " + t);
                assertEquals(lick, hero.hasStatus(StatusEffect.VULNERABLE), "turn " + t);
                if (lick) {
                    assertEquals(Worranut.LICK_TURNS, hero.getStatusTurns(StatusEffect.WEAKNESS));
                    assertEquals(Worranut.LICK_TURNS, hero.getStatusTurns(StatusEffect.VULNERABLE));
                }
            }
            w.resetAfterFlee(); // นับ 1..9 ใหม่ทุกรอบ
        }
    }

    @Test
    void lickDealsHalfAtkDamage() {
        // ATK 18 → 50% = 9, แกว่ง ±20% = 7..10 ก่อนหัก DEF ของห่าน
        int maxRaw = 10;
        for (int i = 0; i < 100; i++) {
            Worranut w = new Worranut();
            int before = hero.getMaxHp();
            hero.clearBuffs();
            hero.heal(before);
            w.takeTurn(hero);
            int lost = before - hero.getHp();
            assertTrue(lost >= 1 && lost <= maxRaw, "lost " + lost);
        }
    }

    @Test
    void otherTurnsMixNormalAttackAndTailSlap() {
        Worranut w = new Worranut();
        int normal = 0;
        int slap = 0;
        for (int i = 0; i < 600; i++) {
            String msg = turn(w);
            if (i % 3 == 0) continue; // เทิร์น LICK
            if (msg.contains("TAIL SLAP")) slap++;
            else if (msg.contains("attacks")) normal++;
            else fail("unexpected move: " + msg);
        }
        double ratio = slap / (double) (slap + normal);
        assertTrue(ratio > 0.2 && ratio < 0.4, "tail slap ratio " + ratio);
    }

    @Test
    void fleeingRestartsTheCountFromLick() {
        Worranut w = new Worranut();
        turn(w); // LICK
        turn(w); // เทิร์นที่ 2

        w.resetAfterFlee();

        assertTrue(turn(w).contains("LICK"));
    }
}
