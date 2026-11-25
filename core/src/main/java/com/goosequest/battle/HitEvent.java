package com.goosequest.battle;

/**
 * [คนที่ 1] เหตุการณ์ที่เกิดกับตัวละครในหนึ่งการกระทำ (ดาเมจ ฮีล บัฟ ฯลฯ)
 * BattleSystem สร้างขึ้นแล้วให้ BattleState เอาไปวาดเอฟเฟกต์ ตัวคลาสนี้ไม่มีโค้ดวาดภาพ
 *
 * ดาเมจ/ฮีลใช้ amount ส่วนบัฟและสถานะอื่นใช้ label (เช่น "ATK +3") เพื่อให้สกิลที่ไม่เปลี่ยน HP
 * แสดงผลได้เหมือนกัน
 */
public final class HitEvent {
    public enum Kind { DAMAGE, HEAL, BUFF, DEBUFF, STATUS }

    private final Kind kind;
    private final boolean onHero;
    private final int amount;
    private final String label;

    private HitEvent(Kind kind, boolean onHero, int amount, String label) {
        this.kind = kind;
        this.onHero = onHero;
        this.amount = amount;
        this.label = label;
    }

    public static HitEvent damage(boolean onHero, int amount) {
        return new HitEvent(Kind.DAMAGE, onHero, amount, String.valueOf(amount));
    }

    public static HitEvent heal(boolean onHero, int amount) {
        return new HitEvent(Kind.HEAL, onHero, amount, "+" + amount);
    }

    /** เหตุการณ์ที่ไม่เกี่ยวกับ HP เช่น บัฟ ดีบัฟ มึน */
    public static HitEvent other(Kind kind, boolean onHero, String label) {
        return new HitEvent(kind, onHero, 0, label);
    }

    public Kind getKind() { return kind; }
    /** true = เกิดกับห่านของผู้เล่น, false = เกิดกับศัตรู */
    public boolean isOnHero() { return onHero; }
    public int getAmount() { return amount; }
    /** ข้อความที่จะลอยขึ้น */
    public String getLabel() { return label; }
}
