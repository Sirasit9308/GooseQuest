package com.goosequest.character;

import com.goosequest.skill.Skill;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * [คนที่ 1] ห่านของผู้เล่น (abstract) มี 3 สายอาชีพสืบทอดต่อ
 * Inheritance: Goose → Character
 */
public abstract class Goose extends Character {
    /** เลเวลที่ปลดล็อกสกิลที่ 2 ของทุกสาย (แก้ที่เดียวถ้าอยากเปลี่ยน) */
    protected static final int SECOND_SKILL_LEVEL = 3;

    private final List<Skill> skills = new ArrayList<>();
    private int level = 1;
    private int exp = 0;

    protected Goose(String name, int maxHp, int maxMp, int atk, int def, Skill firstSkill) {
        super(name, maxHp, maxMp, atk, def);
        skills.add(firstSkill);
    }

    /** เรียนสกิลใหม่ คืน false ถ้ามีสกิลนี้อยู่แล้ว (เทียบจากชื่อ) */
    public boolean learnSkill(Skill skill) {
        for (Skill s : skills) {
            if (s.getName().equals(skill.getName())) return false;
        }
        skills.add(skill);
        return true;
    }

    public boolean canUseSkill(int index) {
        return index >= 0 && index < skills.size() && getMp() >= skills.get(index).getMpCost();
    }

    public String useSkill(int index, Character target) {
        if (index < 0 || index >= skills.size()) {
            return "No such skill!";
        }
        Skill skill = skills.get(index);
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
            int skillsBefore = skills.size();
            onLevelUp();
            msg.append(" LEVEL UP! Now Lv ").append(level).append("!");
            for (int i = skillsBefore; i < skills.size(); i++) {
                msg.append(" Learned ").append(skills.get(i).getName()).append("!");
            }
        }
        return msg.toString();
    }

    /**
     * คืนสถานะตอนโหลดเซฟ ควรเรียกกับห่านที่เพิ่งสร้าง (Lv 1)
     * เลเวลที่เพิ่มจะเรียก onLevelUp() ทีละขั้น เพื่อให้สถานะและสกิลโตเท่าตอนเล่นจริง
     * ส่วน exp, hp, mp ถูกจำกัดให้อยู่ในช่วงที่ถูกต้อง
     */
    public void restore(int savedLevel, int savedExp, int savedHp, int savedMp) {
        while (level < savedLevel) {
            level++;
            onLevelUp();
        }
        exp = Math.max(0, Math.min(expToNext() - 1, savedExp));
        setVitals(savedHp, savedMp);
    }

    public int expToNext() { return level * 20; }

    /** แต่ละสายเก่งขึ้นไม่เหมือนกัน → คลาสลูกต้องกำหนดเอง */
    protected abstract void onLevelUp();

    /** ชื่อสายอาชีพ ใช้แสดงในหน้าเลือกตัวละคร */
    public abstract String getClassName();

    public List<Skill> getSkills() { return Collections.unmodifiableList(skills); }
    /** สกิลแรก (สกิลเริ่มต้น) ไว้ให้หน้าจออื่นที่แสดงสกิลเดียวใช้ */
    public Skill getSkill() { return skills.get(0); }
    public int getLevel() { return level; }
    public int getExp() { return exp; }
}
