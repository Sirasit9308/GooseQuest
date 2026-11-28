package com.goosequest.character;

import static org.junit.jupiter.api.Assertions.*;

import com.goosequest.item.Bread;
import org.junit.jupiter.api.Test;

class SquirrelTest {
    @Test
    void hasTheDesignedStats() {
        Squirrel s = new Squirrel();
        assertEquals(15, s.getMaxHp());
        assertEquals(10, s.getAtk());
        assertEquals(0, s.getDef());
        assertEquals(10, s.getExpReward());
        assertTrue(s.canFlee());
        assertFalse(s.isBoss());
    }

    @Test
    void alwaysDropsExactlyOneBread() {
        for (int i = 0; i < 50; i++) {
            assertInstanceOf(Bread.class, new Squirrel().rollDrop());
        }
    }

    @Test
    void takeTurnIsABasicAttack() {
        Goose hero = new SwordmanGoose();
        int hp = hero.getHp();
        String msg = new Squirrel().takeTurn(hero);
        assertTrue(hero.getHp() < hp);
        assertTrue(msg.contains("attacks"));
        assertTrue(hero.getStatuses().isEmpty());
    }
}
