package com.goosequest.util;

import com.badlogic.gdx.graphics.Color;

/**
 * ชุดสีของเกม ใช้สีจากที่นี่ที่เดียว ภาพทั้งเกมจะได้โทนเดียวกัน
 * (แทนการใช้ Color.RED / Color.SKY ของ libGDX ที่สดเกินไป)
 */
public final class Palette {
    private Palette() {}

    private static Color rgb(int hex) {
        return new Color(((hex >> 16) & 0xff) / 255f, ((hex >> 8) & 0xff) / 255f, (hex & 0xff) / 255f, 1f);
    }

    // ---------- พื้น ----------
    public static final Color GRASS = rgb(0x6aaa4b);
    public static final Color GRASS_DARK = rgb(0x568f3d);
    public static final Color GRASS_LIGHT = rgb(0x86c15e);
    public static final Color PATH = rgb(0xdcc08a);
    public static final Color PATH_DARK = rgb(0xc4a46e);
    public static final Color PATH_LIGHT = rgb(0xeed7a4);
    public static final Color WATER = rgb(0x3f7fc4);
    public static final Color WATER_DEEP = rgb(0x346cab);
    public static final Color WATER_LIGHT = rgb(0x7fb6e6);
    public static final Color SHORE = rgb(0x4e8a3a);

    // ---------- ต้นไม้ / ไม้ ----------
    public static final Color LEAF_DARK = rgb(0x235c35);
    public static final Color LEAF = rgb(0x2f7a42);
    public static final Color LEAF_LIGHT = rgb(0x4c9a4f);
    public static final Color TRUNK = rgb(0x6b4428);
    public static final Color WOOD = rgb(0xa26d3d);
    public static final Color WOOD_DARK = rgb(0x7a4f2b);

    // ---------- ดอกไม้ ----------
    public static final Color FLOWER_WHITE = rgb(0xf3f0e4);
    public static final Color FLOWER_YELLOW = rgb(0xf2cf4a);
    public static final Color FLOWER_PINK = rgb(0xe88aa8);

    // ---------- ตัวละคร ----------
    public static final Color GOOSE = rgb(0xfbfaf4);
    public static final Color GOOSE_SHADE = rgb(0xd9d6cb);
    public static final Color OUTLINE = rgb(0x2a2430);
    public static final Color BEAK = rgb(0xf29a2e);
    public static final Color SHADOW = new Color(0.05f, 0.08f, 0.05f, 0.28f);

    // ---------- UI ----------
    public static final Color UI_BORDER = rgb(0xf1e9d2);
    public static final Color UI_FILL = rgb(0x1f2440);
    public static final Color UI_FILL_LIGHT = rgb(0x2a3155);
    public static final Color UI_CURSOR = rgb(0xf6c945);
    public static final Color BAR_BG = rgb(0x12152a);
    public static final Color HP = rgb(0xe0504a);
    public static final Color MP = rgb(0x4f9be6);
    public static final Color GOLD = rgb(0xf6c945);
    public static final Color GOLD_DARK = rgb(0xc8901f);

    // ---------- ฉากต่อสู้ ----------
    public static final Color SKY_TOP = rgb(0x7ec3ee);
    public static final Color SKY_BOTTOM = rgb(0xcfeaf7);
    public static final Color HILL_FAR = rgb(0x8cc0a0);
    public static final Color HILL_NEAR = rgb(0x6fae6a);
    public static final Color CLOUD = rgb(0xffffff);
    public static final Color BG = rgb(0x171a2b);
}
