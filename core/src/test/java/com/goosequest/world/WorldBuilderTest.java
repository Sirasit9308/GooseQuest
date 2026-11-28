package com.goosequest.world;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** แผนที่ต้องสร้างได้โดยไม่ error (วางของผิดช่อง เช่น บนต้นไม้หรือน้ำ จะ throw ตอนสร้าง) */
class WorldBuilderTest {
    @Test
    void buildsAllMapsWithoutError() {
        assertDoesNotThrow(WorldBuilder::build);
    }
}
