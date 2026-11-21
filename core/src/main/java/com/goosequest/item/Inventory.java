package com.goosequest.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * [คนที่ 2] กระเป๋าเก็บไอเทม
 * Encapsulation: ข้างนอกได้ list แบบอ่านอย่างเดียว ต้องเพิ่ม/ลบผ่าน method
 */
public class Inventory {
    public static final int CAPACITY = 12;

    private final List<Item> items = new ArrayList<>();

    /** คืน false ถ้ากระเป๋าเต็ม */
    public boolean add(Item item) {
        if (items.size() >= CAPACITY) return false;
        items.add(item);
        return true;
    }

    public boolean remove(Item item) {
        return items.remove(item);
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public boolean isEmpty() { return items.isEmpty(); }
    public int size() { return items.size(); }
}
