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
        Goose g = new SwordmanGoose();
        assertEquals(1, g.getSkills().size());
        assertSame(g.getSkills().get(0), g.getSkill());
    }

    @Test
    void learnSkillAddsNewAndRejectsDuplicate() {
        Goose g = new SwordmanGoose();
        assertTrue(g.learnSkill(skill("Honk Blast", 5)));
        assertFalse(g.learnSkill(skill("Honk Blast", 5)));
        assertEquals(2, g.getSkills().size());
    }

    @Test
    void getSkillsIsReadOnly() {
        Goose g = new SwordmanGoose();
        assertThrows(UnsupportedOperationException.class, () -> g.getSkills().add(skill("X", 1)));
    }

    @Test
    void useSkillSpendsMpOfChosenSkill() {
        Goose g = new SwordmanGoose();
        g.learnSkill(skill("Cheap", 2));
        int mp = g.getMp();
        assertEquals("Cheap used", g.useSkill(1, new Dummy()));
        assertEquals(mp - 2, g.getMp());
    }

    @Test
    void useSkillWithoutEnoughMpKeepsMp() {
        Goose g = new SwordmanGoose();
        g.learnSkill(skill("Pricey", g.getMaxMp() + 1));
        int mp = g.getMp();
        assertFalse(g.canUseSkill(1));
        assertTrue(g.useSkill(1, new Dummy()).startsWith("Not enough MP"));
        assertEquals(mp, g.getMp());
    }

    @Test
    void useSkillWithBadIndexDoesNothing() {
        Goose g = new SwordmanGoose();
        int mp = g.getMp();
        assertEquals("No such skill!", g.useSkill(5, new Dummy()));
        assertEquals("No such skill!", g.useSkill(-1, new Dummy()));
        assertFalse(g.canUseSkill(5));
        assertEquals(mp, g.getMp());
    }

    @Test
    void secondSkillIsLearnedAtLevel3ForEveryClass() {
        for (Goose g : new Goose[] {new SwordmanGoose(), new HonkGoose(), new ThiefGoose()}) {
            g.gainExp(20); // Lv 2
            assertEquals(2, g.getLevel());
            assertEquals(1, g.getSkills().size(), g.getClassName());
            g.gainExp(40); // Lv 3
            assertEquals(3, g.getLevel());
            assertEquals(2, g.getSkills().size(), g.getClassName());
        }
    }

    @Test
    void gainExpReportsLearnedSkillOnlyOnce() {
        Goose g = new SwordmanGoose();
        assertFalse(g.gainExp(20).contains("Learned"));
        String msg = g.gainExp(40);
        assertTrue(msg.contains("Learned " + g.getSkills().get(1).getName() + "!"), msg);
        assertFalse(g.gainExp(1).contains("Learned"));
    }

    @Test
    void jumpingSeveralLevelsStillLearnsSecondSkillOnce() {
        Goose g = new ThiefGoose();
        String msg = g.gainExp(1000);
        assertTrue(g.getLevel() > 3);
        assertEquals(2, g.getSkills().size());
        assertEquals(1, msg.split("Learned", -1).length - 1);
    }

    @Test
    void restoreToLevel3LearnsSecondSkillWithoutDuplicates() {
        Goose g = new HonkGoose();
        g.restore(5, 0, 1, 1);
        assertEquals(2, g.getSkills().size());
    }

    @Test
    void secondSkillsHaveDistinctNames() {
        Goose[] geese = {new SwordmanGoose(), new HonkGoose(), new ThiefGoose()};
        java.util.Set<String> names = new java.util.HashSet<>();
        for (Goose g : geese) {
            g.restore(3, 0, 1, 1);
            for (Skill s : g.getSkills()) assertTrue(names.add(s.getName()), s.getName());
        }
    }

    @Test
    void warCryRaisesUserAtkByHalfWithoutHurtingTarget() {
        Goose g = new SwordmanGoose();
        Dummy d = new Dummy();
        g.restore(3, 0, 1, g.getMaxMp());
        int baseAtk = g.getAtk();
        g.useSkill(1, d);
        assertEquals(baseAtk + baseAtk / 2, g.getAtk());
        assertEquals(d.getMaxHp(), d.getHp());
    }

    @Test
    void soothingSongHealsUserWithoutHurtingTarget() {
        Goose g = new HonkGoose();
        Dummy d = new Dummy();
        g.restore(3, 0, 1, g.getMaxMp());
        g.useSkill(1, d);
        assertTrue(g.getHp() > 1);
        assertEquals(d.getMaxHp(), d.getHp());
    }

    @Test
    void shadowStrikeIgnoresDefense() {
        Goose g = new ThiefGoose();
        Character armored = new Character("Tank", 1000, 0, 1, 500) { };
        g.restore(3, 0, 1, g.getMaxMp());
        g.useSkill(1, armored);
        // takeDamage ขั้นต่ำคือ 1 ถ้า DEF ยังกันได้ ดาเมจจะเท่ากับ 1 เท่านั้น
        assertTrue(armored.getMaxHp() - armored.getHp() > 1);
    }

    @Test
    void restoreSetsLevelGrowsStatsAndKeepsValues() {
        Goose g = new SwordmanGoose();
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
        Goose g = new SwordmanGoose();
        g.restore(2, 9999, 9999, 9999);
        assertEquals(g.expToNext() - 1, g.getExp());
        assertEquals(g.getMaxHp(), g.getHp());
        assertEquals(g.getMaxMp(), g.getMp());
    }

    @Test
    void restoreClampsNegativeValues() {
        Goose g = new SwordmanGoose();
        g.restore(1, -5, -10, -3);
        assertEquals(0, g.getExp());
        assertEquals(0, g.getHp());
        assertEquals(0, g.getMp());
    }

    @Test
    void restoreIgnoresLevelBelowCurrent() {
        Goose g = new SwordmanGoose();
        g.restore(0, 0, 1, 1);
        assertEquals(1, g.getLevel());
    }
}
