package com.goosequest.character;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CobraTest {
    @Test
    void hasTheDesignedStats() {
        Cobra c = new Cobra();
        assertEquals(60, c.getMaxHp());
        assertEquals(7, c.getAtk());
        assertEquals(1, c.getDef());
        assertEquals(35, c.getExpReward());
        assertTrue(c.canFlee());
        assertFalse(c.isBoss());
    }

    @Test
    void neverDrops() {
        for (int i = 0; i < 50; i++) assertNull(new Cobra().rollDrop());
    }

    @Test
    void biteSometimesPoisonsForThreeTurns() {
        Cobra cobra = new Cobra();
        Goose hero = new SwordmanGoose();
        int poisoned = 0;
        int runs = 400;
        for (int i = 0; i < runs; i++) {
            hero.clearBuffs();
            hero.heal(hero.getMaxHp());
            int hp = hero.getHp();
            String msg = cobra.takeTurn(hero);
            assertTrue(hero.getHp() < hp, "bite always hurts");
            assertTrue(msg.contains("BITE"));
            if (hero.hasStatus(StatusEffect.POISON)) {
                poisoned++;
                assertEquals(Cobra.POISON_TURNS, hero.getStatusTurns(StatusEffect.POISON));
                assertTrue(msg.contains("POISONED"));
            }
        }
        // โอกาส 50%: ต้องติดบ้างไม่ติดบ้าง (ช่วงกว้างพอไม่ให้เทสต์ flaky)
        assertTrue(poisoned > runs * 0.3 && poisoned < runs * 0.7, "poisoned " + poisoned);
    }
}
