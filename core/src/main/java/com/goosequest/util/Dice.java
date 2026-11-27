package com.goosequest.util;

import java.util.Random;

/** ตัวช่วยสุ่มตัวเลข ใช้ทั้งเกม */
public final class Dice {
    private static Random random = new Random();

    private Dice() {}

    /**
     * กฎปัดเศษของเกม: ปัดลงเสมอ แต่ไม่ต่ำกว่า 1 (0.9 → 1, 1.5 → 1, 26.9 → 26)
     * ใช้กับดาเมจและบัฟทุกที่ที่คำนวณแล้วได้เลขไม่ลงตัว
     */
    public static int floorMin1(double value) {
        return Math.max(1, (int) Math.floor(value));
    }

    /** สุ่มค่าให้แกว่ง ±20% จากค่าฐาน เช่น 10 → 8..12 (ปัดลงตามกฎ floorMin1) */
    public static int vary(int base) {
        double factor = 0.8 + random.nextDouble() * 0.4;
        return floorMin1(base * factor);
    }

    /** คืน true ด้วยความน่าจะเป็น p (0.0 - 1.0) */
    public static boolean chance(double p) {
        return random.nextDouble() < p;
    }

    /** สุ่มเลขจำนวนเต็มในช่วง [0, bound) */
    public static int next(int bound) {
        return random.nextInt(bound);
    }

    /** ใช้ตอนทดสอบ ให้ผลสุ่มออกมาเหมือนเดิมทุกครั้ง */
    public static void setSeed(long seed) {
        random = new Random(seed);
    }
}
