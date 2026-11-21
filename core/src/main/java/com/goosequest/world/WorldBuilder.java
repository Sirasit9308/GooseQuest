package com.goosequest.world;

import com.badlogic.gdx.graphics.Color;
import com.goosequest.character.Chicken;
import com.goosequest.character.Dog;
import com.goosequest.character.FarmerBoss;
import com.goosequest.character.Fox;
import com.goosequest.character.Gardener;
import com.goosequest.item.Bread;
import com.goosequest.item.GoldenFeather;
import com.goosequest.item.PondWater;
import com.goosequest.world.tile.DoorTile;

import java.util.Map;

/**
 * [คนที่ 3 + 2] สร้างแผนที่ทั้ง 3 ผืน พร้อมวาง NPC ศัตรู และไอเทม
 * อยากแก้ด่านก็แก้ข้อความแผนที่ข้างล่างได้เลย (ทุกแถวต้องยาวเท่ากัน)
 *
 * place(obj, คอลัมน์, แถว) — นับจาก 0 ที่มุมซ้ายบนของข้อความ
 */
public final class WorldBuilder {
    private WorldBuilder() {}

    // ประตูเชื่อมแผนที่: farm ⇄ park ⇄ house
    private static final String[] FARM = {
            "#########################",
            "#.......,,,......FFFFF..#",
            "#..~~~..,........F...F..#",
            "#..~~~..,........F...F..#",
            "#.......,........FF.FF..#",
            "#.......,,,,,,,.........#",
            "#.............,.........#",
            "#..FFFFF......,.........#",
            "#..F...F......,,,,,,,,,,A",
            "#..F...F......,.........#",
            "#..FF.FF......,.........#",
            "#.............,....~~~..#",
            "#.....##......,....~~~..#",
            "#.....##......,.........#",
            "#.............,.........#",
            "#.......................#",
            "#.......................#",
            "#########################",
    };

    private static final String[] PARK = {
            "#########################",
            "#....~~~~.......##......#",
            "#....~~~~...............#",
            "#.....~~...,,,,,,,......#",
            "##.........,.....,......#",
            "#..........,.....,...##.#",
            "#...##.....,.....,......#",
            "#...##.....,.....,......#",
            "a,,,,,,,,,,,.....,,,,,,,B",
            "#................,......#",
            "#....~~~.........,......#",
            "#...~~~~~........,..##..#",
            "#....~~~.........,..##..#",
            "#................,......#",
            "#..##....,,,,,,,,,......#",
            "#..##...................#",
            "#.......................#",
            "#########################",
    };

    private static final String[] HOUSE = {
            "####################",
            "#..................#",
            "#...FFFFFFFFFF.....#",
            "#...F........F.....#",
            "#...F........F.....#",
            "#...F........F.....#",
            "#...FFFFF,FFFF.....#",
            "#........,.........#",
            "b,,,,,,,,,.........#",
            "#..................#",
            "#...~~~......##....#",
            "#...~~~......##....#",
            "#..................#",
            "####################",
    };

    public static World build() {
        World world = new World();

        // ---------- ฟาร์ม (ด่าน 1) ----------
        TileMap farm = TileMap.parse("farm", "Sunny Farm", FARM,
                Map.of('A', new DoorTile("farm_east", "park", "park_west")));
        farm.place(new NPC("Old Duck", Color.YELLOW,
                "Quack! You're finally awake, young goose.",
                "The Farmer stole the Sacred Bread last night!",
                "Head EAST through the park to reach his house.",
                "Watch out for the dogs... and good luck. Quack."), 5, 5);
        farm.place(new ItemPickup(new Bread()), 10, 3);
        farm.place(new MapEnemy(new Dog(), true), 11, 10);
        farm.place(new MapEnemy(new Dog(), true), 20, 14);
        farm.place(new MapEnemy(new Chicken(), false), 19, 2);
        farm.place(new ItemPickup(new GoldenFeather()), 5, 8);
        world.addMap(farm);

        // ---------- สวนสาธารณะ (ด่าน 2) ----------
        TileMap park = TileMap.parse("park", "Honk Park", PARK, Map.of(
                'a', new DoorTile("park_west", "farm", "farm_east"),
                'B', new DoorTile("park_east", "house", "house_west")));
        park.place(new NPC("Swan", Color.WHITE,
                "Oh my, a goose. How... rustic.",
                "The Farmer's house is just past the east gate.",
                "They say he gets FURIOUS when he's hurt. Be ready."), 14, 5);
        park.place(new MapEnemy(new Gardener(), true), 8, 3);
        park.place(new MapEnemy(new Gardener(), true), 14, 12);
        park.place(new MapEnemy(new Fox(), false), 21, 8);
        park.place(new ItemPickup(new PondWater()), 3, 12);
        park.place(new ItemPickup(new Bread()), 22, 15);
        world.addMap(park);

        // ---------- บ้านชาวนา (บอส) ----------
        TileMap house = TileMap.parse("house", "Farmer's Yard", HOUSE,
                Map.of('b', new DoorTile("house_west", "park", "park_east")));
        house.place(new MapEnemy(new FarmerBoss(), false), 9, 6);
        house.place(new ItemPickup(new Bread()), 2, 12);
        house.place(new ItemPickup(new Bread()), 17, 1);
        world.addMap(house);

        world.enter("farm", 8, farm.rowToY(6));
        return world;
    }
}
