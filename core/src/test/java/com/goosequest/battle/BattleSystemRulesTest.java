package com.goosequest.battle;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.graphics.Color;
import com.goosequest.character.Enemy;
import com.goosequest.character.FarmerBoss;
import com.goosequest.character.Goose;
import com.goosequest.character.WarriorGoose;
import com.goosequest.item.Bread;
import com.goosequest.item.Inventory;
import com.goosequest.skill.Skill;
import com.goosequest.character.Character;
import org.junit.jupiter.api.Test;

/** ทดสอบกติกาการต่อสู้ (ชนะ แพ้ หนี MP) ของ BattleSystem โดยไม่ต้องเปิดหน้าต่างเกม */
class BattleSystemRulesTest {
    private static class TestEnemy extends Enemy {
        TestEnemy(int hp, int atk, int def, int exp) { super("Target", hp, atk, def, exp); }
        @Override public Color getColor() { return Color.WHITE; }
    }

    private static Skill costing(String name, int cost) {
        return new Skill() {
            @Override public String getName() { return name; }
            @Override public int getMpCost() { return cost; }
            @Override public String use(Character user, Character target) { return name + " used"; }
        };
    }

    private final Goose hero = new WarriorGoose();
    private final Inventory inventory = new Inventory();

    private BattleSystem fight(Enemy enemy) {
        return new BattleSystem(hero, enemy, inventory);
    }

    @Test
    void useSkillWithoutEnoughMpIsRejectedAndCostsNoTurn() {
        Enemy enemy = new TestEnemy(1000, 50, 0, 0);
        BattleSystem battle = fight(enemy);
        hero.learnSkill(costing("Pricey", hero.getMaxMp() + 1));
        int heroHp = hero.getHp();
        int mp = hero.getMp();

        assertFalse(battle.useSkill(1));

        assertEquals(mp, hero.getMp());
        assertEquals(heroHp, hero.getHp()); // ศัตรูไม่ได้ตอบโต้
        assertEquals(enemy.getMaxHp(), enemy.getHp());
        assertTrue(battle.isOngoing());
        assertTrue(battle.getRecentLog(1).get(0).contains("Not enough MP"));
        assertTrue(battle.drainEvents().isEmpty());
    }

    @Test
    void useSkillSpendsMpAndPassesTheTurn() {
        BattleSystem battle = fight(new TestEnemy(1000, 50, 0, 0));
        hero.learnSkill(costing("Cheap", 3));
        int mp = hero.getMp();
        int heroHp = hero.getHp();

        assertTrue(battle.useSkill(1));

        assertEquals(mp - 3, hero.getMp());
        assertTrue(hero.getHp() < heroHp); // ศัตรูตีกลับ
    }

    @Test
    void useSkillWithBadIndexIsRejected() {
        BattleSystem battle = fight(new TestEnemy(1000, 50, 0, 0));
        int heroHp = hero.getHp();
        assertFalse(battle.useSkill(9));
        assertEquals(heroHp, hero.getHp());
    }

    @Test
    void defeatingEnemyWinsAndGivesExp() {
        BattleSystem battle = fight(new TestEnemy(1, 1, 0, 7));
        battle.attack();

        assertEquals(BattleSystem.Result.WON, battle.getResult());
        assertFalse(battle.isOngoing());
        assertEquals(7, hero.getExp());
    }

    @Test
    void winningWithEnoughExpLevelsHeroUp() {
        BattleSystem battle = fight(new TestEnemy(1, 1, 0, hero.expToNext()));
        battle.attack();

        assertEquals(BattleSystem.Result.WON, battle.getResult());
        assertEquals(2, hero.getLevel());
    }

    @Test
    void commandsAreIgnoredAfterBattleEnds() {
        Enemy enemy = new TestEnemy(1, 1, 0, 5);
        BattleSystem battle = fight(enemy);
        battle.attack();
        int exp = hero.getExp();
        int heroHp = hero.getHp();
        inventory.add(new Bread());

        battle.attack();
        assertFalse(battle.useSkill(0));
        battle.useItem(inventory.getItems().get(0));
        battle.flee();

        assertEquals(BattleSystem.Result.WON, battle.getResult());
        assertEquals(exp, hero.getExp());
        assertEquals(heroHp, hero.getHp());
        assertEquals(1, inventory.size()); // ขนมปังไม่ถูกใช้
    }

    @Test
    void heroFaintingLosesTheBattle() {
        BattleSystem battle = fight(new TestEnemy(1000, 10000, 0, 0));
        battle.attack();

        assertEquals(BattleSystem.Result.LOST, battle.getResult());
        assertFalse(hero.isAlive());
        assertEquals(0, hero.getHp());
    }

    @Test
    void cannotFleeFromBossAndEnemyDoesNotAct() {
        BattleSystem battle = fight(new FarmerBoss());
        int heroHp = hero.getHp();

        battle.flee();

        assertTrue(battle.isOngoing());
        assertEquals(heroHp, hero.getHp());
        assertTrue(battle.getRecentLog(1).get(0).contains("can't run"));
    }

    @Test
    void useItemNotInInventoryDoesNothing() {
        BattleSystem battle = fight(new TestEnemy(1000, 50, 0, 0));
        hero.takeDamage(hero.getDef() + 30);
        int heroHp = hero.getHp();

        battle.useItem(new Bread());

        assertEquals(heroHp, hero.getHp());
        assertTrue(battle.drainEvents().isEmpty());
    }

    @Test
    void useItemConsumesItFromInventory() {
        BattleSystem battle = fight(new TestEnemy(1000, 1, 0, 0));
        inventory.add(new Bread());

        battle.useItem(inventory.getItems().get(0));

        assertTrue(inventory.isEmpty());
    }
}
