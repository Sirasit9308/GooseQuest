package com.goosequest.item;

import com.goosequest.character.Character;

/**
 * [คนที่ 2] ไอเทม (abstract) แต่ละชนิดมีผลต่างกันใน use()
 */
public abstract class Item {
    private final String name;
    private final String description;

    protected Item(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /** ใช้ไอเทมกับเป้าหมาย คืนข้อความผลลัพธ์ */
    public abstract String use(Character target);

    public String getName() { return name; }
    public String getDescription() { return description; }
}
