package com.goosequest.world;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.goosequest.util.Palette;
import com.goosequest.world.tile.DoorTile;
import com.goosequest.world.tile.FenceTile;
import com.goosequest.world.tile.GrassTile;
import com.goosequest.world.tile.PathTile;
import com.goosequest.world.tile.Tile;
import com.goosequest.world.tile.TreeTile;
import com.goosequest.world.tile.WaterTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * [คนที่ 3] แผนที่ 1 ผืน = ตารางพื้น (Tile) + วัตถุที่วางอยู่ (GameObject)
 *
 * สร้างจากข้อความ เช่น
 *   '#' ต้นไม้   '.' หญ้า   ',' ทางเดิน   '~' น้ำ   'F' รั้ว
 *   ตัวอักษรอื่นที่ส่งมาใน doors = ประตู
 *
 * พิกัด: x = คอลัมน์, y = นับจากล่างขึ้นบน (แถวบนสุดของข้อความคือ y สูงสุด)
 */
public class TileMap {
    private final String name;
    private final String displayName;
    private final int width;
    private final int height;
    private final Tile[][] tiles; // [x][y]
    private final List<GameObject> objects = new ArrayList<>();
    private Player player;

    private TileMap(String name, String displayName, int width, int height) {
        this.name = name;
        this.displayName = displayName;
        this.width = width;
        this.height = height;
        this.tiles = new Tile[width][height];
    }

    public static TileMap parse(String name, String displayName, String[] rows, Map<java.lang.Character, DoorTile> doors) {
        int h = rows.length;
        int w = rows[0].length();
        TileMap map = new TileMap(name, displayName, w, h);
        for (int row = 0; row < h; row++) {
            if (rows[row].length() != w) {
                throw new IllegalArgumentException(name + ": row " + row + " has length "
                        + rows[row].length() + ", expected " + w);
            }
            for (int col = 0; col < w; col++) {
                char c = rows[row].charAt(col);
                map.tiles[col][map.rowToY(row)] = tileFor(c, doors, name);
            }
        }
        return map;
    }

    private static Tile tileFor(char c, Map<java.lang.Character, DoorTile> doors, String mapName) {
        switch (c) {
            case '#': return new TreeTile();
            case '.': return new GrassTile();
            case ',': return new PathTile();
            case '~': return new WaterTile();
            case 'F': return new FenceTile();
            default:
                DoorTile door = doors.get(c);
                if (door == null) throw new IllegalArgumentException(mapName + ": unknown tile '" + c + "'");
                return door;
        }
    }

    /** แปลงเลขแถวในข้อความ (นับจากบน) เป็นพิกัด y (นับจากล่าง) */
    public int rowToY(int row) { return height - 1 - row; }

    /** วางวัตถุที่ (คอลัมน์, แถว) ตามที่เห็นในข้อความแผนที่ */
    public void place(GameObject obj, int col, int row) {
        int y = rowToY(row);
        if (!inBounds(col, y) || !tiles[col][y].isWalkable()) {
            throw new IllegalArgumentException(name + ": can't place object on (" + col + "," + row + ")");
        }
        obj.setPosition(col, y);
        objects.add(obj);
    }

    public void remove(GameObject obj) { objects.remove(obj); }

    public boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    public Tile getTile(int x, int y) { return tiles[x][y]; }

    /** วัตถุที่ขวางอยู่ในช่องนี้ หรือ null */
    public GameObject getObjectAt(int x, int y) {
        for (GameObject o : objects) {
            if (o.isSolid() && o.getX() == x && o.getY() == y) return o;
        }
        return null;
    }

    /** ช่องนี้เดินเข้าได้ไหม (ในแผนที่ + พื้นเดินได้ + ไม่มีอะไรขวาง + ไม่ใช่ที่ห่านยืน) */
    public boolean canEnter(int x, int y) {
        if (!inBounds(x, y) || !tiles[x][y].isWalkable()) return false;
        if (getObjectAt(x, y) != null) return false;
        return player == null || player.getX() != x || player.getY() != y;
    }

    /** หาตำแหน่งประตูจาก id คืน {x, y} */
    public int[] findDoor(String doorId) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (tiles[x][y] instanceof DoorTile d && d.getId().equals(doorId)) {
                    return new int[]{x, y};
                }
            }
        }
        throw new IllegalArgumentException(name + ": no door with id " + doorId);
    }

    public void update(float dt) {
        for (GameObject o : new ArrayList<>(objects)) o.update(dt, this);
    }

    public void draw(ShapeRenderer s, float t) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y].draw(s, x * t, y * t, t);
            }
        }
        drawEdges(s, t);
        // วาดของที่อยู่ไกล (y สูง) ก่อน ของที่อยู่ใกล้จะได้ทับถูกต้อง
        List<GameObject> sorted = new ArrayList<>(objects);
        sorted.sort((a, b) -> Integer.compare(b.getY(), a.getY()));
        for (GameObject o : sorted) o.draw(s, t);
    }

    /**
     * ขอบระหว่างพื้นต่างชนิด: ริมตลิ่งรอบน้ำ และขอบทางเดิน
     * ทำที่นี่เพราะ Tile แต่ละช่องไม่รู้ว่าช่องข้าง ๆ เป็นอะไร
     */
    private void drawEdges(ShapeRenderer s, float t) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile tile = tiles[x][y];
                float px = x * t, py = y * t;
                if (tile instanceof WaterTile) {
                    for (Direction d : Direction.values()) {
                        if (isWater(x + d.dx, y + d.dy)) continue;
                        s.setColor(Palette.SHORE);
                        edge(s, px, py, t, d, 4);
                        s.setColor(Palette.WATER_LIGHT);
                        edge(s, px + d.dx * -4, py + d.dy * -4, t, d, 1.5f);
                    }
                } else if (tile instanceof PathTile || tile instanceof DoorTile) {
                    for (Direction d : Direction.values()) {
                        if (!inBounds(x + d.dx, y + d.dy)) continue;
                        Tile n = tiles[x + d.dx][y + d.dy];
                        if (n instanceof PathTile || n instanceof DoorTile) continue;
                        s.setColor(Palette.PATH_DARK);
                        edge(s, px, py, t, d, 2);
                    }
                }
            }
        }
    }

    private boolean isWater(int x, int y) {
        return !inBounds(x, y) || tiles[x][y] instanceof WaterTile;
    }

    /** แถบหนา w pixel ชิดขอบช่องด้านทิศ d */
    private static void edge(ShapeRenderer s, float px, float py, float t, Direction d, float w) {
        if (d.dx > 0) s.rect(px + t - w, py, w, t);
        else if (d.dx < 0) s.rect(px, py, w, t);
        else if (d.dy > 0) s.rect(px, py + t - w, t, w);
        else s.rect(px, py, t, w);
    }

    public void setPlayer(Player player) { this.player = player; }
    public String getName() { return name; }
    public String getDisplayName() { return displayName; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public List<GameObject> getObjects() { return java.util.Collections.unmodifiableList(objects); }
}
