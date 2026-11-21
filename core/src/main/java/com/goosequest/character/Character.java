package com.goosequest.character;

import com.goosequest.util.Dice;

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
    private boolean stunned;

    protected Character(String name, int maxHp, int maxMp, int atk, int def) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.maxMp = maxMp;
        this.mp = maxMp;
        this.atk = atk;
        this.def = def;
    }

    /** โจมตีปกติ ลูกหลาน override ได้ถ้าอยากให้ตีต่างออกไป (Polymorphism) */
    public String attack(Character target) {
        int dmg = target.takeDamage(Dice.vary(atk));
        return name + " attacks " + target.getName() + " for " + dmg + " damage!";
    }

    /** รับดาเมจหลังหักพลังป้องกัน คืนค่าดาเมจที่โดนจริง */
    public int takeDamage(int rawDamage) {
        int dmg = Math.max(1, rawDamage - def);
        hp = Math.max(0, hp - dmg);
        return dmg;
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

    public void buffAtk(int amount) {
        atk += amount;
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
    public int getAtk() { return atk; }
    public int getDef() { return def; }
}
