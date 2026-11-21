package com.goosequest.character;

import com.goosequest.skill.Skill;

/**
 * [คนที่ 1] ห่านของผู้เล่น (abstract) มี 3 สายอาชีพสืบทอดต่อ
 * Inheritance: Goose → Character
 */
public abstract class Goose extends Character {
    private final Skill skill;
    private int level = 1;
    private int exp = 0;

    protected Goose(String name, int maxHp, int maxMp, int atk, int def, Skill skill) {
        super(name, maxHp, maxMp, atk, def);
        this.skill = skill;
    }

    public boolean canUseSkill() {
        return getMp() >= skill.getMpCost();
    }

    public String useSkill(Character target) {
        if (!spendMp(skill.getMpCost())) {
            return "Not enough MP for " + skill.getName() + "!";
        }
        return skill.use(this, target);
    }

    /** รับ EXP แล้วเลเวลอัปถ้าครบ คืนข้อความไว้แสดงในบันทึกการต่อสู้ */
    public String gainExp(int amount) {
        exp += amount;
        StringBuilder msg = new StringBuilder("Gained " + amount + " EXP.");
        while (exp >= expToNext()) {
            exp -= expToNext();
            level++;
            onLevelUp();
            msg.append(" LEVEL UP! Now Lv ").append(level).append("!");
        }
        return msg.toString();
    }

    public int expToNext() { return level * 20; }

    /** แต่ละสายเก่งขึ้นไม่เหมือนกัน → คลาสลูกต้องกำหนดเอง */
    protected abstract void onLevelUp();

    /** ชื่อสายอาชีพ ใช้แสดงในหน้าเลือกตัวละคร */
    public abstract String getClassName();

    public Skill getSkill() { return skill; }
    public int getLevel() { return level; }
    public int getExp() { return exp; }
}
