package com.goosequest.character;

/**
 * [คนที่ 1] สถานะที่ติดตัวละครได้ทั้งห่านและศัตรู (ใช้กับ Character.applyStatus)
 *
 * atkRate / defRate คือสัดส่วนที่เพิ่ม (+) หรือลด (-) จาก ATK / DEF ฐาน
 * poisonRate คือสัดส่วนของ maxHp ที่เสียตอนเริ่มเทิร์น (0 = ไม่ใช่พิษ)
 */
public enum StatusEffect {
    WEAKNESS("Weakness", -0.25, 0, 0),
    VULNERABLE("Vulnerable", 0, -0.25, 0),
    POISON("Poison", 0, 0, 0.05),
    STRENGTH("Strength", 0.25, 0, 0),
    DURABILITY("Durability", 0, 0.25, 0),
    ENRAGE("Enrage", 0.5, 0, 0);

    private final String label;
    private final double atkRate;
    private final double defRate;
    private final double poisonRate;

    StatusEffect(String label, double atkRate, double defRate, double poisonRate) {
        this.label = label;
        this.atkRate = atkRate;
        this.defRate = defRate;
        this.poisonRate = poisonRate;
    }

    public String getLabel() { return label; }
    public double getAtkRate() { return atkRate; }
    public double getDefRate() { return defRate; }
    public double getPoisonRate() { return poisonRate; }
    public boolean isPoison() { return poisonRate > 0; }
}
