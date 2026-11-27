package com.goosequest.character;

import com.goosequest.util.Dice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
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
    private final Map<StatusEffect, StatusState> statuses = new EnumMap<>(StatusEffect.class);
    private boolean acting;
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
        statuses.clear();
        acting = false;
    }

    // ---------- สถานะ (Weakness, Poison ฯลฯ) ----------

    /**
     * ติดสถานะ turns เทิร์น ติดซ้ำจะไม่ซ้อนกัน เทียบเวลาที่เหลือกับเวลาใหม่ ใครมากกว่าใช้อันนั้น
     * (เท่ากันคงของเดิม) เวลาลดตอนจบเทิร์นของตัวละครที่ติดสถานะเอง ส่วน Poison ลดตอนเริ่มเทิร์น
     * พร้อมเสียเลือด ถ้าติดตัวเองระหว่างเทิร์นตัวเอง (เช่น Enrage) จบเทิร์นนั้นจะยังไม่ลด
     * จึงได้ครบ turns เทิร์นถัดไป คืน true ถ้าสถานะถูกติดหรือต่อเวลาจริง
     */
    public boolean applyStatus(StatusEffect effect, int turns) {
        if (turns <= 0) return false;
        StatusState old = statuses.get(effect);
        if (old != null && old.remaining() >= turns) return false;
        statuses.put(effect, new StatusState(turns, acting && !effect.isPoison()));
        return true;
    }

    public boolean hasStatus(StatusEffect effect) {
        return statuses.containsKey(effect);
    }

    /** จำนวนเทิร์นที่เหลือของสถานะ (0 ถ้าไม่ได้ติด) */
    public int getStatusTurns(StatusEffect effect) {
        StatusState s = statuses.get(effect);
        return s == null ? 0 : s.turnsLeft;
    }

    /** สถานะที่ติดอยู่ทั้งหมดพร้อมเทิร์นที่เหลือ (อ่านอย่างเดียว) */
    public Map<StatusEffect, Integer> getStatuses() {
        Map<StatusEffect, Integer> copy = new EnumMap<>(StatusEffect.class);
        for (Map.Entry<StatusEffect, StatusState> e : statuses.entrySet()) {
            copy.put(e.getKey(), e.getValue().turnsLeft);
        }
        return Collections.unmodifiableMap(copy);
    }

    /** เปิดเทิร์นโดยไม่ทำพิษ (ใช้ตอนเริ่มสู้ให้เทิร์นแรกของห่านนับว่า "กำลังเล่น") */
    public void beginTurn() {
        acting = true;
    }

    /**
     * เริ่มเทิร์นของตัวละครนี้: ถ้าติด Poison จะเสียเลือด 5% ของ maxHp (ขั้นต่ำ 1 ไม่ลดด้วย DEF)
     * และลดเวลา Poison ลง 1 เทิร์นทันที คืนค่าดาเมจพิษที่เสียจริง (0 ถ้าไม่ติดพิษ)
     */
    public int startTurn() {
        beginTurn();
        int dmg = 0;
        for (Iterator<Map.Entry<StatusEffect, StatusState>> it = statuses.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<StatusEffect, StatusState> entry = it.next();
            if (!entry.getKey().isPoison()) continue;
            int lost = Dice.floorMin1(maxHp * entry.getKey().getPoisonRate());
            int before = hp;
            hp = Math.max(0, hp - lost);
            dmg += before - hp;
            if (--entry.getValue().turnsLeft <= 0) it.remove();
        }
        return dmg;
    }

    /** จบเทิร์นของตัวละครนี้ (หลัง action เสร็จ): ลดเวลาสถานะที่ไม่ใช่ Poison คืนสถานะที่เพิ่งหมด */
    public List<StatusEffect> endTurn() {
        acting = false;
        List<StatusEffect> expired = new ArrayList<>();
        for (Iterator<Map.Entry<StatusEffect, StatusState>> it = statuses.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<StatusEffect, StatusState> entry = it.next();
            if (entry.getKey().isPoison()) continue;
            StatusState s = entry.getValue();
            if (s.skipNextTick) {
                s.skipNextTick = false;
            } else if (--s.turnsLeft <= 0) {
                expired.add(entry.getKey());
                it.remove();
            }
        }
        return expired;
    }

    private static final class StatusState {
        int turnsLeft;
        boolean skipNextTick;

        StatusState(int turnsLeft, boolean skipNextTick) {
            this.turnsLeft = turnsLeft;
            this.skipNextTick = skipNextTick;
        }

        /** จำนวนเทิร์นที่ยังมีผลจริง (อันที่ยังไม่โดนลดในเทิร์นที่ติดนับเพิ่มอีก 1) */
        int remaining() { return turnsLeft + (skipNextTick ? 1 : 0); }
    }

    /** ATK/DEF ฐาน (รวมบัฟคงที่) บวกส่วนที่เพิ่ม/ลดจากสถานะ แต่ละสถานะคิด % จากค่าฐาน ปัดลงขั้นต่ำ 1 */
    private int withStatuses(int base, boolean forAtk) {
        if (base <= 0) return 0;
        int total = base;
        for (StatusEffect effect : statuses.keySet()) {
            double rate = forAtk ? effect.getAtkRate() : effect.getDefRate();
            if (rate == 0) continue;
            int change = Dice.floorMin1(Math.abs(base) * Math.abs(rate));
            total += rate > 0 ? change : -change;
        }
        return Math.max(0, total);
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
    public int getAtk() { return withStatuses(Math.max(0, atk + tempAtk), true); }
    /** DEF รวมบัฟชั่วคราวและสถานะ ถ้าต่ำกว่า 0 นับเป็น 0 */
    public int getDef() { return withStatuses(Math.max(0, def + tempDef), false); }
}
