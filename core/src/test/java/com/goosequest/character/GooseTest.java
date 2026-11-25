package com.goosequest.character;

import static org.junit.jupiter.api.Assertions.*;

import com.goosequest.skill.Skill;
import org.junit.jupiter.api.Test;

class GooseTest {
    /** ศัตรูจำลองไว้เป็นเป้าโจมตี */
    private static class Dummy extends Character {
        Dummy() { super("Dummy", 1000, 0, 1, 0); }
    }

    /** สกิลจำลอง ราคา MP กำหนดเองได้ */
    private static Skill skill(String name, int cost) {
        return new Skill() {
            @Override public String getName() { return name; }
            @Override public int getMpCost() { return cost; }
            @Override public String use(Character user, Character target) { return name + " used"; }
        };
    }

    @Test
    void startsWithOneSkill() {
        Goose g = new WarriorGoose();
        assertEquals(1, g.getSkills().size());
        assertSame(g.getSkills().get(0), g.getSkill());
    }

    @Test
    void learnSkillAddsNewAndRejectsDuplicate() {
        Goose g = new WarriorGoose();
        assertTrue(g.learnSkill(skill("Honk Blast", 5)));
        assertFalse(g.learnSkill(skill("Honk Blast", 5)));
        assertEquals(2, g.getSkills().size());
    }

    @Test
    void getSkillsIsReadOnly() {
        Goose g = new WarriorGoose();
        assertThrows(UnsupportedOperationException.class, () -> g.getSkills().add(skill("X", 1)));
    }

    @Test
    void useSkillSpendsMpOfChosenSkill() {
        Goose g = new WarriorGoose();
        g.learnSkill(skill("Cheap", 2));
        int mp = g.getMp();
        assertEquals("Cheap used", g.useSkill(1, new Dummy()));
        assertEquals(mp - 2, g.getMp());
    }

    @Test
    void useSkillWithoutEnoughMpKeepsMp() {
        Goose g = new WarriorGoose();
        g.learnSkill(skill("Pricey", g.getMaxMp() + 1));
        int mp = g.getMp();
        assertFalse(g.canUseSkill(1));
        assertTrue(g.useSkill(1, new Dummy()).startsWith("Not enough MP"));
        assertEquals(mp, g.getMp());
    }

    @Test
    void useSkillWithBadIndexDoesNothing() {
        Goose g = new WarriorGoose();
        int mp = g.getMp();
        assertEquals("No such skill!", g.useSkill(5, new Dummy()));
        assertEquals("No such skill!", g.useSkill(-1, new Dummy()));
        assertFalse(g.canUseSkill(5));
        assertEquals(mp, g.getMp());
    }

    @Test
    void restoreSetsLevelGrowsStatsAndKeepsValues() {
        Goose g = new WarriorGoose();
        int baseMaxHp = g.getMaxHp();
        g.restore(3, 10, 50, 7);
        assertEquals(3, g.getLevel());
        assertTrue(g.getMaxHp() > baseMaxHp);
        assertEquals(10, g.getExp());
        assertEquals(50, g.getHp());
        assertEquals(7, g.getMp());
    }

    @Test
    void restoreClampsTooLargeValues() {
        Goose g = new WarriorGoose();
        g.restore(2, 9999, 9999, 9999);
        assertEquals(g.expToNext() - 1, g.getExp());
        assertEquals(g.getMaxHp(), g.getHp());
        assertEquals(g.getMaxMp(), g.getMp());
    }

    @Test
    void restoreClampsNegativeValues() {
        Goose g = new WarriorGoose();
        g.restore(1, -5, -10, -3);
        assertEquals(0, g.getExp());
        assertEquals(0, g.getHp());
        assertEquals(0, g.getMp());
    }

    @Test
    void restoreIgnoresLevelBelowCurrent() {
        Goose g = new WarriorGoose();
        g.restore(0, 0, 1, 1);
        assertEquals(1, g.getLevel());
    }
}
