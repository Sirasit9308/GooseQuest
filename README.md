# 🪿 Goose Quest

เกม RPG ต่อสู้แบบผลัดตา (turn-based) ที่ห่านเดินสำรวจแผนที่ได้ ทำด้วย **Java + libGDX** สำหรับโปรเจกต์วิชา OOP

> ห่านถูกชาวนาขโมย "ขนมปังศักดิ์สิทธิ์" ไป ต้องเดินผ่าน **ฟาร์ม → สวนสาธารณะ → บ้านชาวนา** แล้วชนะบอสให้ได้

---

## วิธีรัน

ต้องมี **JDK 17 ขึ้นไป** (แนะนำ Temurin 17 หรือ 21)

```bash
# Windows
gradlew.bat lwjgl3:run

# macOS / Linux
./gradlew lwjgl3:run
```

รันครั้งแรกจะดาวน์โหลด Gradle และ libGDX อัตโนมัติ ใช้เวลา 1–3 นาที

**เปิดใน IntelliJ IDEA:** File → Open → เลือกโฟลเดอร์ `GooseQuest` แล้วรอ Gradle sync จนเสร็จ
จากนั้นเปิดแท็บ Gradle (ขวามือ) แล้วเลือก `lwjgl3 → Tasks → application → run`

### ปุ่มควบคุม
| ปุ่ม | ทำอะไร |
|---|---|
| ลูกศร / WASD | เดินอิสระ (กดสองปุ่มพร้อมกันเพื่อเดินเฉียง), เลื่อนเมนู |
| F | คุยกับ NPC ที่อยู่ใกล้ |
| ENTER / SPACE / Z | ยืนยัน, ข้อความถัดไป |
| I | เปิดกระเป๋า |
| ESC / X | ย้อนกลับ, ปิดเมนู |

ยืนใกล้ NPC แล้วกด F เพื่อคุย, เดินไปแตะศัตรูเพื่อเริ่มต่อสู้, แตะไอเทม (เพชรสีทอง) เพื่อเก็บ

---

## โครงสร้างโปรเจกต์ และใครรับผิดชอบไฟล์ไหน

```
core/src/main/java/com/goosequest/
├── GooseQuestGame.java       จุดเริ่มเกม (ทุกคน)
├── GameManager.java          stack ของ state + ข้อมูลร่วม (ทุกคน)
│
├── character/   [คนที่ 1]    Character, Goose, 3 สายห่าน, Enemy, ศัตรู 9 ชนิด (รวมบอส)
├── skill/       [คนที่ 1]    Skill (interface), WingSlap, MegaHonk, SneakyPeck
├── battle/      [คนที่ 1]    BattleSystem (กติกาการต่อสู้ ไม่มีโค้ดวาดภาพ)
├── state/BattleState.java  [คนที่ 1]   หน้าจอต่อสู้
│
├── item/        [คนที่ 2]    Item, Bread, PondWater, GoldenFeather, Inventory
├── state/DialogueState.java [คนที่ 2]  กล่องบทพูด
├── state/MenuState.java     [คนที่ 2]  เมนูกระเป๋า + สถานะ
│
├── world/       [คนที่ 3]    GameObject, Entity, Player, NPC, MapEnemy, ItemPickup,
│                             Interactable, TileMap, World, WorldBuilder (สร้างด่าน)
├── world/tile/  [คนที่ 3]    Tile, Grass, Path, Tree, Water, Fence, Door
├── state/ExploreState.java  [คนที่ 3]  โหมดเดินสำรวจ + กล้อง
│
├── state/TitleState, EndState, GameState   (ทุกคน)
└── util/        Dice (สุ่ม), Keys (ปุ่ม), Art (วาดรูปทรง)

lwjgl3/   ตัวเปิดโปรแกรมบนคอม (ไม่ต้องแก้)
```

---

## หลัก OOP ที่ใช้ (เอาไปอธิบายตอนนำเสนอได้เลย)

| หลักการ | ตัวอย่างในโค้ด |
|---|---|
| **Encapsulation** | `Character` เก็บ `hp`, `atk` เป็น `private` ต้องแก้ผ่าน `takeDamage()` / `heal()` เท่านั้น HP จึงไม่ติดลบ · `Inventory.getItems()` คืน list แบบอ่านอย่างเดียว |
| **Inheritance** | `SwordmanGoose → Goose → Character` · `Dog → Enemy → Character` · `Player → Entity → GameObject` · `GrassTile → Tile` |
| **Polymorphism** | `Enemy.takeTurn()` แต่ละศัตรูออกท่าต่างกัน · `Goose.onLevelUp()` แต่ละสายเก่งขึ้นต่างกัน · `Interactable.interact()` ชน NPC = คุย, ชนศัตรู = สู้, ชนไอเทม = เก็บ · `Tile.onStep()` ประตูวาร์ป |
| **Abstraction** | `Character`, `Goose`, `Enemy`, `Item`, `Tile`, `GameObject`, `GameState` เป็น abstract class · `Skill`, `Interactable` เป็น interface |
| **Composition** | `MapEnemy` (ตัวบนแผนที่) เก็บ `Enemy` (ข้อมูลต่อสู้) ไว้ข้างใน · `Goose` มี `Skill` |
| **State Pattern** | `GameManager` เก็บ stack ของ `GameState`: Title / Explore / Dialogue / Menu / Battle / End แล้วเรียกแค่ `update()` กับ `render()` |

**ทำไมแยก `BattleSystem` ออกจาก `BattleState`:** กติกา (ใครตีใคร ใครชนะ) ไม่ผูกกับการวาดภาพ เลยทดสอบได้โดยไม่ต้องเปิดเกม และถ้าจะเปลี่ยนหน้าตาก็ไม่ต้องแตะกติกา (Separation of Concerns)

---

## สมดุลเกมตอนนี้

ทดสอบด้วยการจำลองเล่นจบเกม 2,000 รอบต่อสาย (ฟาร์ม → สวน → บอส สู้ศัตรูทุกตัวบนทาง เก็บไอเทมครบ ไม่หนี ใช้สกิลแรกเมื่อมี MP และกินขนมปังเมื่อ HP ต่ำกว่า 35%) ได้อัตราชนะบอส:

| สาย | อัตราชนะ | ระดับ |
|---|---|---|
| Swordman | ~85% | ปกติ |
| Thief | ~74% | ยาก |
| Honk | ~25% | ยากมาก |

ถ้าผู้เล่นดื่ม Pond Water ตอน MP ไม่พอด้วย: Swordman ~99%, Thief ~85%, Honk ~44%
(ค่าก่อนปรับศัตรูในวิธีจำลองเดียวกัน: Swordman ~99%, Honk ~87%, Thief ~87%)

ตัวเลขทั้งหมดอยู่ใน constructor ของแต่ละคลาส เช่น `super("THE FARMER", 300, 22, 6, 100)` = ชื่อ, HP, ATK, DEF, EXP

---

## สิ่งที่ต้องทำต่อ (TODO) ตามแผน 2 สัปดาห์

**คนที่ 1 (ต่อสู้)**
- [ ] เพิ่มสกิลที่ 2 ให้แต่ละสาย (ปลดล็อกตอน Lv 3) แล้วเปลี่ยน `Goose` ให้เก็บ `List<Skill>`
- [ ] เอฟเฟกต์ตอนโดนตี เช่นตัวสั่นหรือกระพริบ และตัวเลขดาเมจลอยขึ้น
- [x] เพิ่มศัตรูอีก 4 ชนิด (Squirrel, Cobra, Turtle, Worranut) และให้ศัตรูสตันห่านได้

**คนที่ 2 (เนื้อหา)**
- [ ] **Save / Load** ลงไฟล์ (ใช้ `Gdx.files.local("save.json")` คู่กับ `com.badlogic.gdx.utils.Json`)
- [ ] เขียนบทพูดเพิ่ม และเพิ่ม NPC
- [ ] ไอเทมใหม่ เช่น ยาแก้มึน หรือขนนกป้องกัน

**คนที่ 3 (โลก)**
- [ ] เปลี่ยนรูปทรงเป็นรูปภาพ (sprite) แก้ที่ `util/Art.java` และ `Tile.draw()`
- [ ] (ถ้ามีเวลา) ใช้ **Tiled Map Editor** วาดแผนที่แทนข้อความใน `WorldBuilder`
- [ ] อนิเมชันเดินแบบลื่น (เลื่อนทีละ pixel ระหว่างช่อง)

**ทั้งกลุ่ม**
- [ ] วาด UML Class Diagram (IntelliJ: คลิกขวาที่ package → Diagrams → Show Diagram)
- [ ] ใส่เสียง HONK! (`Gdx.audio.newSound(...)`)

---

## เคล็ดลับ

- **ภาษาไทยในเกม:** ฟอนต์ที่ติดมากับ libGDX แสดงได้แค่ภาษาอังกฤษ ถ้าอยากได้ภาษาไทยต้องเพิ่ม `gdx-freetype` และไฟล์ฟอนต์ไทย (.ttf)
- **แก้แผนที่:** แก้ข้อความใน `WorldBuilder.java` ได้เลย ทุกแถวต้องยาวเท่ากัน ถ้าวางของไว้บนต้นไม้หรือน้ำ เกมจะแจ้ง error บอกตำแหน่งตอนเปิด
- **Git:** แต่ละคนแยก branch ตามส่วนของตัวเอง แล้ว merge วันที่ 5 และวันที่ 9 ตามแผน
