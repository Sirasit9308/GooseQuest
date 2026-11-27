package com.goosequest.skill;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.graphics.Color;
import com.goosequest.battle.BattleSystem;
import com.goosequest.character.Enemy;
import com.goosequest.character.HonkGoose;
import com.goosequest.character.StatusEffect;
import com.goosequest.item.Inventory;
import org.junit.jupiter.api.Test;

class IntimidateTest {
    private static class Sponge extends Enemy {
        Sponge() { super("Sponge", 100000, 1, 0, 0); }
        @Override public Color getColor() { return Color.WHITE; }
    }

    private final HonkGoose hero = new HonkGoose();
    private final Sponge enemy = new Sponge();
    private final BattleSystem battle = new BattleSystem(hero, enemy, new Inventory());

    IntimidateTest() {
        hero.learnSkill(new Intimidate());
    }

    @Test
    void appliesWeaknessAndVulnerableToEnemyOnly() {
        battle.useSkill(1);
        assertTrue(enemy.hasStatus(StatusEffect.WEAKNESS));
        assertTrue(enemy.hasStatus(StatusEffect.VULNERABLE));
        assertFalse(hero.hasStatus(StatusEffect.WEAKNESS));
        assertFalse(hero.hasStatus(StatusEffect.VULNERABLE));
    }

    @Test
    void lastsThreeEnemyTurns() {
        battle.useSkill(1); // ศัตรูเดินต่อทันที 1 เทิร์น (ถูกกดแล้ว) เหลือ 2
        assertEquals(2, enemy.getStatusTurns(StatusEffect.WEAKNESS));
        assertEquals(2, enemy.getStatusTurns(StatusEffect.VULNERABLE));
        battle.attack();
        battle.attack();
        assertFalse(enemy.hasStatus(StatusEffect.WEAKNESS));
        assertFalse(enemy.hasStatus(StatusEffect.VULNERABLE));
    }

    @Test
    void honkLearnsIntimidateAtLevelThree() {
        HonkGoose fresh = new HonkGoose();
        while (fresh.getLevel() < 3) fresh.gainExp(1000);
        assertTrue(fresh.getSkills().stream().anyMatch(s -> s instanceof Intimidate));
    }
}
