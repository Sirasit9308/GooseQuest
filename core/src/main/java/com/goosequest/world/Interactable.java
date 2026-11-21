package com.goosequest.world;

import com.goosequest.state.ExploreState;

/**
 * [คนที่ 3] ของที่ห่านเดินไปชนแล้วมีอะไรเกิดขึ้น
 *
 * Polymorphism: ExploreState ไม่ต้องรู้ว่าชนอะไร แค่เรียก interact()
 *   NPC        → เปิดบทพูด
 *   MapEnemy   → เริ่มต่อสู้
 *   ItemPickup → เก็บเข้ากระเป๋า
 */
public interface Interactable {
    void interact(ExploreState state);
}
