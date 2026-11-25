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

    /**
     * hitbox (กึ่งกลาง cx,cy ครึ่งกว้าง hx ครึ่งสูง hy หน่วยช่อง) ยืนตรงนี้ได้ไหม
     * วัตถุที่ hitbox ทับอยู่แล้วที่ตำแหน่งเดิม (oldCx, oldCy) จะไม่นับ กันห่านติดค้างเวลามีของเดินมาทับ
     */
    public boolean canStand(float cx, float cy, float hx, float hy, float oldCx, float oldCy) {
        int x0 = (int) Math.floor(cx - hx), x1 = (int) Math.floor(cx + hx);
        int y0 = (int) Math.floor(cy - hy), y1 = (int) Math.floor(cy + hy);
        for (int x = x0; x <= x1; x++) {
            for (int y = y0; y <= y1; y++) {
                if (!inBounds(x, y) || !tiles[x][y].isWalkable()) return false;
            }
        }
        for (GameObject o : objects) {
            if (!o.isSolid()) continue;
            if (overlaps(o, cx, cy, hx, hy) && !overlaps(o, oldCx, oldCy, hx, hy)) return false;
        }
        return true;
    }

    /** hitbox ทับช่องของวัตถุ o ไหม */
    private static boolean overlaps(GameObject o, float cx, float cy, float hx, float hy) {
        return cx + hx > o.getX() && cx - hx < o.getX() + 1
                && cy + hy > o.getY() && cy - hy < o.getY() + 1;
    }

    /** วัตถุที่ทำงานเมื่อสัมผัส (ศัตรู, ไอเทม) และห่านแตะอยู่ (ขยาย hitbox ออก margin ช่อง) หรือ null */
    public GameObject findTouching(Player p, float margin) {
        for (GameObject o : objects) {
            if (o instanceof Interactable i && !i.needsKey()
                    && overlaps(o, p.getHitCenterX(), p.getHitCenterY(),
                            Player.HIT_HX + margin, Player.HIT_HY + margin)) {
                return o;
            }
        }
        return null;
    }

    /** วัตถุที่ต้องกด F (เช่น NPC) ที่อยู่ใกล้ห่านที่สุดในระยะ range ช่อง หรือ null */
    public GameObject findNearbyKeyTarget(Player p, float range) {
        GameObject best = null;
        float bestDist = range * range;
        for (GameObject o : objects) {
            if (!(o instanceof Interactable i) || !i.needsKey()) continue;
            float dx = o.getCenterX() - p.getHitCenterX();
            float dy = o.getCenterY() - p.getHitCenterY();
            float d2 = dx * dx + dy * dy;
            if (d2 <= bestDist) {
                bestDist = d2;
                best = o;
            }
        }
        return best;
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
        // ห่านอยู่ในลำดับเดียวกับวัตถุอื่น เดินอ้อมหลัง NPC แล้วจะถูกบังถูกต้อง
        List<GameObject> sorted = new ArrayList<>(objects);
        if (player != null) sorted.add(player);
        sorted.sort((a, b) -> Float.compare(b.getDrawY(), a.getDrawY()));
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
