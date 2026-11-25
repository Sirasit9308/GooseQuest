package com.goosequest.world;

import com.goosequest.state.ExploreState;

/**
 * [คนที่ 3] ของที่ห่านเดินไปชนแล้วมีอะไรเกิดขึ้น
 *
 * Polymorphism: ExploreState ไม่ต้องรู้ว่าชนอะไร แค่เรียก interact()
 *   NPC        → เปิดบทพูด
 *   MapEnemy   → เริ่มต่อสู้
 *   ItemPickup → เก็บเข้ากระเป๋า
 *
 * ค่าเริ่มต้น: ทำงานทันทีที่ห่านสัมผัส (ศัตรู, ไอเทม)
 * ถ้า needsKey() เป็น true ต้องยืนใกล้ ๆ แล้วกดปุ่ม F (เช่น NPC)
 */
public interface Interactable {
    void interact(ExploreState state);

    default boolean needsKey() { return false; }
}
