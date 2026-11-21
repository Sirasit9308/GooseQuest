# กติกาสำหรับ AI agent ใน GooseQuest

ไฟล์นี้บอก agent (Claude Code, Codex, Copilot ฯลฯ) ว่าต้องสร้าง branch และ commit แบบไหน
ทุกคนในทีมใช้กติกาเดียวกัน ถ้า agent ทำผิดกติกานี้ให้หยุดและถามคนในทีมก่อน

## กติกา branch

1. **ห้าม commit หรือ push ตรงเข้า `main`** งานทุกชิ้นต้องอยู่บน branch ของตัวเองแล้วเปิด PR
2. **1 ticket = 1 branch** อย่ารวมงานหลาย ticket ไว้ใน branch เดียว
3. **แตก branch จาก `main` ล่าสุดเสมอ**
   ```bash
   git switch main
   git pull
   git switch -c feature/GQ-12-save-load
   ```
4. **ตั้งชื่อ branch** เป็น `<ประเภท>/GQ-<เลข>-<คำอธิบายสั้น>`
   - ใช้ตัวพิมพ์เล็กภาษาอังกฤษ คั่นคำด้วย `-` ไม่เกิน 4 คำ
   - ประเภท:
     | ประเภท | ใช้เมื่อ | ตัวอย่าง |
     |---|---|---|
     | `feature/` | เพิ่มของใหม่ตาม ticket | `feature/GQ-02-skill-list` |
     | `fix/` | แก้บั๊ก | `fix/GQ-91-door-warp-crash` |
     | `chore/` | งานที่ไม่ใช่ตัวเกม เช่น build, README, UML | `chore/GQ-32-uml-diagram` |
   - ถ้างานไม่มีเลข ticket ให้ละส่วน `GQ-<เลข>` ไป เช่น `feature/graphics-quick-wins`
     และบอกคนในทีมให้เปิด ticket ให้
5. **ถ้าไม่รู้ว่างานนี้เป็น ticket ไหน ให้ถามก่อนสร้าง branch** อย่าเดาเลข ticket

## กติกา commit

- ข้อความ commit ขึ้นต้นด้วยเลข ticket: `GQ-12: add SaveManager`
- 1 commit = 1 เรื่อง ถ้าใน branch มีการแก้หลายเรื่อง ให้แยก commit
- รัน `gradlew.bat build` (Windows) หรือ `./gradlew build` ให้ผ่านก่อน commit
- อย่า commit ไฟล์ใน `.gitignore` (`build/`, `.gradle/`, `.idea/` ฯลฯ) หรือไฟล์เซฟ `save.json`

## ก่อนเปิด PR

- `git fetch` แล้ว merge หรือ rebase `main` ล่าสุดเข้ามา และแก้ conflict ให้เสร็จ
- เล่นเกมจริงตั้งแต่หน้า Title ถึงจุดที่แก้ ต้องไม่ crash
- ในคำอธิบาย PR ให้ใส่เลข ticket, สิ่งที่เปลี่ยน และวิธีทดสอบ
- ต้องมีเพื่อนในทีมอย่างน้อย 1 คนรีวิวก่อน merge ห้ามให้ agent merge PR เอง

## รอบ merge

- **Merge #1:** วันที่ 5 ของสปรินต์
- **Merge #2:** วันที่ 9 ของสปรินต์
- งานที่ไม่ทันรอบ merge ให้คงไว้บน branch ห้ามรีบ merge งานที่ยังไม่เสร็จเข้า `main`

## เจ้าของไฟล์ (ระวังตอนแก้ข้ามส่วน)

| ส่วน | เจ้าของ |
|---|---|
| `character/`, `skill/`, `battle/`, `state/BattleState.java` | คนที่ 1 (ต่อสู้) |
| `item/`, `save/`, `state/DialogueState.java`, `state/MenuState.java` | คนที่ 2 (เนื้อหา) |
| `world/`, `world/tile/`, `state/ExploreState.java`, `util/Art.java`, `util/Palette.java` | คนที่ 3 (โลก) |
| `GameManager.java`, `GooseQuestGame.java`, `build.gradle` | ทั้งทีม (แจ้งในกลุ่มก่อนแก้) |

- `WorldBuilder.java`: คนที่ 3 แก้ข้อความแผนที่ ส่วนคนที่ 2 แก้เฉพาะบรรทัด `place(new NPC…)`
- ห้ามเปลี่ยนชื่อหรือพารามิเตอร์ของ method ใน `util/Art.java` ที่ส่วนอื่นเรียกใช้อยู่
- ถ้างานต้องแก้ไฟล์ของคนอื่น ให้บอกในคำอธิบาย PR ว่าแก้ไฟล์ไหน
