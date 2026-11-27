package com.goosequest.character;

import com.goosequest.util.Dice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * [คนที่ 1] ตัวละครทุกตัวในเกม ทั้งห่านและศัตรู
 *
 * Abstraction  : เป็น abstract class สร้าง object ตรง ๆ ไม่ได้
 * Encapsulation: ค่าสถานะเป็น private ทั้งหมด แก้ได้ผ่าน method เท่านั้น
 *                (HP จึงไม่มีทางติดลบหรือเกิน maxHp)
 */
public abstract class Character {
    private final String name;
    private int maxHp;
    private int hp;
    private int maxMp;
    private int mp;
    private int atk;
    private int def;
    private int tempAtk;
    private int tempDef;
    private final Map<String, TimedBuff> timedBuffs = new LinkedHashMap<>();
    private boolean stunned;
    private boolean lastHitCritical;

    protected Character(String name, int maxHp, int maxMp, int atk, int def) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.maxMp = maxMp;
        this.mp = maxMp;
        this.atk = atk;
        this.def = def;
    }

    /** ตัวคูณดาเมจตอนคริติคอล (150% = แรงขึ้นจากเดิม 50%) */
    public static final double CRIT_MULTIPLIER = 1.5;

    /** โอกาสคริติคอลของตัวละคร ค่าเริ่มต้นคือไม่มี (ศัตรูไม่คริ) ห่านจะ override */
    public double getCritChance() { return 0; }

    /** โจมตีปกติ ลูกหลาน override ได้ถ้าอยากให้ตีต่างออกไป (Polymorphism) */
    public String attack(Character target) {
        int dmg = dealDamage(target, Dice.vary(getAtk()));
        return name + " attacks " + target.getName() + (target.lastHitCritical ? " CRITICAL!" : "")
                + " for " + dmg + " damage!";
    }

    /** ทำดาเมจใส่เป้าหมายโดยสุ่มคริติคอลตาม getCritChance() คืนดาเมจที่โดนจริง */
    public int dealDamage(Character target, int rawDamage) {
        return dealDamage(target, rawDamage, false);
    }

    /**
     * เหมือนด้านบน ถ้า ignoreDef = true ดาเมจจะไม่ถูกลดด้วย DEF ของเป้าหมาย
     * คริติคอลคูณเฉพาะดาเมจดิบ (ปัดลงขั้นต่ำ 1) ไม่คูณ DEF ที่ข้าม
     */
    public int dealDamage(Character target, int rawDamage, boolean ignoreDef) {
        boolean crit = getCritChance() > 0 && Dice.chance(getCritChance());
        int raw = crit ? Dice.floorMin1(rawDamage * CRIT_MULTIPLIER) : rawDamage;
        if (ignoreDef) raw += target.getDef();
        return target.takeDamage(raw, crit);
    }

    /** รับดาเมจหลังหักพลังป้องกัน คืนค่าดาเมจที่โดนจริง */
    public int takeDamage(int rawDamage) {
        return takeDamage(rawDamage, false);
    }

    /** เหมือนด้านบน แต่บอกได้ว่าโดนคริติคอล (ให้หน้าจอต่อสู้แสดงตัวเลขแบบพิเศษ) */
    public int takeDamage(int rawDamage, boolean critical) {
        int dmg = Math.max(1, rawDamage - getDef());
        hp = Math.max(0, hp - dmg);
        lastHitCritical = critical;
        return dmg;
    }

    /** ครั้งล่าสุดที่โดนดาเมจเป็นคริติคอลไหม อ่านแล้วล้างค่าทิ้ง */
    public boolean consumeCritical() {
        boolean was = lastHitCritical;
        lastHitCritical = false;
        return was;
    }

    /** ฟื้น HP คืนค่าที่ฟื้นได้จริง */
    public int heal(int amount) {
        int before = hp;
        hp = Math.min(maxHp, hp + amount);
        return hp - before;
    }

    public int restoreMp(int amount) {
        int before = mp;
        mp = Math.min(maxMp, mp + amount);
        return mp - before;
    }

    public boolean spendMp(int amount) {
        if (mp < amount) return false;
        mp -= amount;
        return true;
    }

    /** ตั้ง HP/MP ตรง ๆ (ใช้ตอนโหลดเซฟ) ค่าจะถูกจำกัดให้อยู่ในช่วง 0..max เสมอ */
    protected void setVitals(int newHp, int newMp) {
        hp = Math.max(0, Math.min(maxHp, newHp));
        mp = Math.max(0, Math.min(maxMp, newMp));
    }

    /** บัฟถาวร (เช่น Golden Feather) ไม่ถูกล้างเมื่อจบการต่อสู้ */
    public void buffAtk(int amount) {
        atk += amount;
    }

    /** บัฟชั่วคราวจนจบการต่อสู้ รับค่าติดลบได้ (ดีบัฟ) ล้างด้วย clearBuffs() */
    public void addBuff(int atkAmount, int defAmount) {
        tempAtk += atkAmount;
        tempDef += defAmount;
    }

    public void clearBuffs() {
        tempAtk = 0;
        tempDef = 0;
        timedBuffs.clear();
    }

    /**
     * บัฟแบบนับเทิร์น: ใช้ id เดิมซ้ำจะไม่ซ้อนกัน แต่แทนที่ของเดิมแล้วเริ่มนับใหม่
     * นับเฉพาะเทิร์นหลังจากเทิร์นที่ใช้ (turns = 3 → มีผลต่ออีก 3 เทิร์นถัดไป)
     * หมดเวลาโดย tickBuffs() ที่ BattleSystem เรียกตอนจบทุกรอบ
     */
    public void applyTimedBuff(String id, int atkAmount, int defAmount, int turns) {
        // +1 เพราะรอบที่ใช้สกิลก็ถูก tick ด้วย แต่ไม่ถือเป็นหนึ่งในจำนวนเทิร์นที่ได้
        timedBuffs.put(id, new TimedBuff(atkAmount, defAmount, turns + 1));
    }

    /** ยกเลิกบัฟแบบนับเทิร์นก่อนหมดเวลา คืน true ถ้ามีบัฟนั้นอยู่ */
    public boolean removeTimedBuff(String id) {
        return timedBuffs.remove(id) != null;
    }

    public boolean hasTimedBuff(String id) {
        return timedBuffs.containsKey(id);
    }

    /** จบหนึ่งรอบ: ลดเวลาบัฟทุกอัน คืน id ของบัฟที่เพิ่งหมด */
    public List<String> tickBuffs() {
        List<String> expired = new ArrayList<>();
        Iterator<Map.Entry<String, TimedBuff>> it = timedBuffs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, TimedBuff> entry = it.next();
            if (--entry.getValue().turnsLeft <= 0) {
                expired.add(entry.getKey());
                it.remove();
            }
        }
        return expired;
    }

    private int timedAtk() {
        int sum = 0;
        for (TimedBuff b : timedBuffs.values()) sum += b.atk;
        return sum;
    }

    private int timedDef() {
        int sum = 0;
        for (TimedBuff b : timedBuffs.values()) sum += b.def;
        return sum;
    }

    private static final class TimedBuff {
        final int atk;
        final int def;
        int turnsLeft;

        TimedBuff(int atk, int def, int turnsLeft) {
            this.atk = atk;
            this.def = def;
            this.turnsLeft = turnsLeft;
        }
    }

    /** ให้คลาสลูกเพิ่มสถานะได้ตอนเลเวลอัป (protected = เฉพาะลูกหลาน) */
    protected void growStats(int hpUp, int mpUp, int atkUp, int defUp) {
        maxHp += hpUp;
        maxMp += mpUp;
        atk += atkUp;
        def += defUp;
        hp = maxHp;
        mp = maxMp;
    }

    public boolean isAlive() { return hp > 0; }
    public boolean isStunned() { return stunned; }
    public void setStunned(boolean stunned) { this.stunned = stunned; }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getMp() { return mp; }
    public int getMaxMp() { return maxMp; }
    /** ATK รวมบัฟชั่วคราว ถ้าต่ำกว่า 0 นับเป็น 0 */
    public int getAtk() { return Math.max(0, atk + tempAtk + timedAtk()); }
    /** DEF รวมบัฟชั่วคราว ถ้าต่ำกว่า 0 นับเป็น 0 */
    public int getDef() { return Math.max(0, def + tempDef + timedDef()); }
}
