package com.goosequest.world;

/** [คนที่ 3] ทิศทางเดิน (แกน y ชี้ขึ้น ตามระบบพิกัดของ libGDX) */
public enum Direction {
    UP(0, 1), DOWN(0, -1), LEFT(-1, 0), RIGHT(1, 0);

    public final int dx;
    public final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
}
