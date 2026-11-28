package com.goosequest.character;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TurtleTest {
    @Test
    void hasTheDesignedStats() {
        Turtle t = new Turtle();
        assertEquals(100, t.getMaxHp());
        assertEquals(12, t.getAtk());
        assertEquals(20, t.getDef());
        assertEquals(60, t.getExpReward());
        assertTrue(t.canFlee());
        assertFalse(t.isBoss());
    }

    @Test
    void neverDrops() {
        for (int i = 0; i < 50; i++) assertNull(new Turtle().rollDrop());
    }

    @Test
    void firstTurnBuffsDurabilityForTheWholeFightAndDoesNotAttack() {
        Turtle turtle = new Turtle();
        Goose hero = new SwordmanGoose();
        int hp = hero.getHp();

        turtle.takeTurn(hero);

        assertEquals(hp, hero.getHp());
        assertFalse(hero.isStunned());
        assertTrue(turtle.hasStatus(StatusEffect.DURABILITY));
        assertEquals(Turtle.DURABILITY_TURNS, turtle.getStatusTurns(StatusEffect.DURABILITY));
        assertEquals(25, turtle.getDef()); // 20 + 25% = 25
    }

    @Test
    void laterTurnsHeadbuttAndSometimesStun() {
        Turtle turtle = new Turtle();
        Goose hero = new SwordmanGoose();
        turtle.takeTurn(hero); // เทิร์นแรก: บัฟ
        int stuns = 0;
        int runs = 400;
        for (int i = 0; i < runs; i++) {
            hero.heal(hero.getMaxHp());
            hero.setStunned(false);
            int hp = hero.getHp();
            String msg = turtle.takeTurn(hero);
            assertTrue(hero.getHp() < hp, "headbutt always hurts");
            assertTrue(msg.contains("HEADBUTT"));
            assertEquals(Turtle.DURABILITY_TURNS, turtle.getStatusTurns(StatusEffect.DURABILITY));
            if (hero.isStunned()) stuns++;
        }
        assertTrue(stuns > runs * 0.15 && stuns < runs * 0.35, "stuns " + stuns);
    }

    @Test
    void fleeingResetsSoTheNextFightStartsWithTheBuffAgain() {
        Turtle turtle = new Turtle();
        Goose hero = new SwordmanGoose();
        turtle.takeTurn(hero);
        turtle.takeTurn(hero);

        turtle.resetAfterFlee();

        assertFalse(turtle.hasStatus(StatusEffect.DURABILITY));
        assertEquals(20, turtle.getDef());
        int hp = hero.getHp();
        turtle.takeTurn(hero);
        assertEquals(hp, hero.getHp());
        assertTrue(turtle.hasStatus(StatusEffect.DURABILITY));
    }
}
