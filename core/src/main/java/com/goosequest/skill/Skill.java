package com.goosequest.skill;

import com.goosequest.character.Character;

/**
 * [คนที่ 1] สกิล (interface)
 * แต่ละสกิล implement use() ต่างกัน → เพิ่มสกิลใหม่ได้โดยไม่ต้องแก้คลาสห่าน
 */
public interface Skill {
    String getName();

    int getMpCost();

    /** ใช้สกิลแล้วคืนข้อความผลลัพธ์ */
    String use(Character user, Character target);
}
