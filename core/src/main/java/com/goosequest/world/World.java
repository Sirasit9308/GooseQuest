package com.goosequest.world;

import java.util.HashMap;
import java.util.Map;

/**
 * [คนที่ 3] โลกทั้งใบ = แผนที่ทุกผืน + ห่าน + แผนที่ที่อยู่ตอนนี้
 * แผนที่ทุกผืนถูกเก็บไว้ในหน่วยความจำ ศัตรูที่แพ้ไปแล้วจึงไม่กลับมาเกิดใหม่
 */
public class World {
    private final Map<String, TileMap> maps = new HashMap<>();
    private final Player player = new Player();
    private TileMap current;

    public void addMap(TileMap map) {
        maps.put(map.getName(), map);
    }

    /** ย้ายห่านไปแผนที่ name ที่ช่อง (x, y) */
    public void enter(String name, int x, int y) {
        if (current != null) current.setPlayer(null);
        current = maps.get(name);
        if (current == null) throw new IllegalArgumentException("No map named " + name);
        current.setPlayer(player);
        player.setPosition(x, y);
    }

    /** วาร์ปผ่านประตู: ไปโผล่หน้าประตูปลายทาง 1 ช่องตามทิศที่ห่านเดิน */
    public void warp(String targetMap, String targetDoorId) {
        TileMap target = maps.get(targetMap);
        int[] door = target.findDoor(targetDoorId);
        Direction d = player.getFacing();
        enter(targetMap, door[0] + d.dx, door[1] + d.dy);
    }

    public TileMap getCurrentMap() { return current; }
    public Player getPlayer() { return player; }
}
