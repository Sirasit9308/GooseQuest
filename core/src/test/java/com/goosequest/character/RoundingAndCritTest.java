package com.goosequest.character;

import static org.junit.jupiter.api.Assertions.*;

import com.goosequest.skill.SneakyPeck;
import com.goosequest.skill.WarCry;
import com.goosequest.util.Dice;
import org.junit.jupiter.api.Test;

/** กฎปัดเศษแบบปัดลงขั้นต่ำ 1 และระบบคริติคอลของฝั่งห่าน */
class RoundingAndCritTest {
    /** ตัวละครที่กำหนดโอกาสคริเองได้ (1.0 = คริทุกครั้ง, 0 = ไม่คริ) */
    private static Character attacker(int atk, double critChance) {
        return new Character("Attacker", 100, 0, atk, 0) {
            @Override public double getCritChance() { return critChance; }
        };
    }

    private static Character target(int def) {
        return new Character("Target", 1000, 0, 1, def) { };
    }

    @Test
    void floorMin1RoundsDownButNeverBelowOne() {
        assertEquals(1, Dice.floorMin1(0.9));
        assertEquals(1, Dice.floorMin1(1.5));
        assertEquals(26, Dice.floorMin1(26.9));
        assertEquals(1, Dice.floorMin1(0));
    }

    @Test
    void varyNeverGoesBelowOneOrAboveTwentyPercentOverBase() {
        for (int i = 0; i < 500; i++) {
            int v = Dice.vary(10);
            assertTrue(v >= 8 && v <= 12, "got " + v);
            assertTrue(Dice.vary(1) >= 1);
        }
    }

    @Test
    void critDealsOneAndAHalfTimesRoundedDown() {
        Character t = target(0);
        assertEquals(15, attacker(1, 1.0).dealDamage(t, 10));
        assertTrue(t.consumeCritical());
        assertEquals(10, attacker(1, 1.0).dealDamage(target(0), 7)); // 10.5 → 10
    }

    @Test
    void noCritChanceMeansNormalDamage() {
        Character t = target(0);
        assertEquals(10, attacker(1, 0).dealDamage(t, 10));
        assertFalse(t.consumeCritical());
    }

    @Test
    void critMultipliesBeforeDefenseIsSubtracted() {
        assertEquals(15 - 5, attacker(1, 1.0).dealDamage(target(5), 10));
    }

    @Test
    void ignoreDefCritDoesNotMultiplyTheIgnoredDefense() {
        // 10 → 15 แล้วข้าม DEF 5 เท่ากับโดน 15 เต็ม ๆ
        assertEquals(15, attacker(1, 1.0).dealDamage(target(5), 10, true));
        assertEquals(10, attacker(1, 0).dealDamage(target(5), 10, true));
    }

    @Test
    void geeseCritChancesMatchTheRules() {
        assertEquals(0.05, new SwordmanGoose().getCritChance());
        assertEquals(0.05, new HonkGoose().getCritChance());
        assertEquals(0.10, new ThiefGoose().getCritChance());
    }

    @Test
    void enemiesNeverCrit() {
        for (Enemy e : new Enemy[] {new Chicken(), new Dog(), new Fox(), new Gardener(), new FarmerBoss()}) {
            assertEquals(0, e.getCritChance(), e.getName());
        }
    }

    @Test
    void sneakyPeckCritChanceIsFiftyPercent() {
        assertEquals(0.5, SneakyPeck.CRIT_CHANCE);
    }

    @Test
    void warCryBonusIsHalfOfAtkRoundedDownAtLeastOne() {
        Character strong = attacker(15, 0);
        new WarCry().use(strong, target(0));
        assertEquals(15 + 7, strong.getAtk()); // 7.5 → 7

        Character weak = attacker(1, 0);
        new WarCry().use(weak, target(0));
        assertEquals(2, weak.getAtk()); // 0.5 → ขั้นต่ำ 1
    }
}
