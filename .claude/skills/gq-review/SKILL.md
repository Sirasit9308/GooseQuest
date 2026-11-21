---
name: gq-review
description: รีวิวโค้ดของ GooseQuest ตามกติกาทีม ใช้เมื่อมีคนขอให้รีวิว branch, PR, diff หรืองานก่อนเปิด PR / ก่อน merge เช่น "รีวิว PR #12", "เช็กงานใน branch นี้ก่อน merge", "review my changes" ตรวจทั้งบั๊ก, libGDX, หลัก OOP, กติกาใน AGENTS.md และ checklist ของ ticket แล้วรายงานเป็นภาษาไทย
argument-hint: "[เลข PR หรือชื่อ branch]  (ไม่ใส่ = branch ปัจจุบันเทียบกับ main)"
---

# รีวิวโค้ด GooseQuest

รีวิวงานหนึ่งชิ้น (PR หรือ branch) แล้วรายงานว่า **พร้อม merge หรือยัง** และต้องแก้อะไร
นี่คือการรีวิวอย่างเดียว: **ห้ามแก้โค้ด ห้าม commit ห้าม push ห้าม approve หรือ merge PR**
ทำสิ่งเหล่านั้นก็ต่อเมื่อผู้ใช้สั่งเองหลังเห็นผลรีวิวแล้ว

## 1. หาว่าจะรีวิวอะไร

- **ได้เลข PR มา** (เช่น `12` หรือ `#12`):
  ```bash
  gh pr view 12 --json number,title,body,headRefName,baseRefName,files,commits
  gh pr diff 12
  ```
- **ได้ชื่อ branch มา** หรือ **ไม่ได้อะไรมาเลย** (ใช้ branch ปัจจุบัน):
  ```bash
  git fetch origin
  git log --oneline origin/main..<branch>
  git diff origin/main...<branch>
  ```
  ถ้ารีวิว branch ปัจจุบันและมีไฟล์ที่ยังไม่ commit ให้รวม `git diff` และ `git status --short` ด้วย แล้วบอกในรายงานว่ามีงานที่ยังไม่ commit
- ถ้า diff ว่าง ให้บอกผู้ใช้แล้วหยุด

อ่านไฟล์ที่เปลี่ยนแบบเต็มไฟล์ ไม่ใช่แค่ hunk ใน diff เพราะบั๊กหลายตัวอยู่ที่โค้ดรอบ ๆ ที่ไม่ได้แก้

## 2. อ่านบริบทก่อนตัดสิน

1. อ่าน `AGENTS.md` (กติกา branch, commit, PR และเจ้าของไฟล์) ถือเป็นเกณฑ์หลัก
2. หาเลข ticket จากชื่อ branch หรือชื่อ PR (`GQ-<เลข>`) แล้วเปิด issue เพื่อเอา checklist:
   ```bash
   gh issue list --search "GQ-12 in:title" --state all --json number,title
   gh issue view <number>
   ```
   ถ้าไม่มีเลข ticket ให้ข้ามส่วนตรวจ checklist และบอกในรายงาน
3. ถ้า PR มีคอมเมนต์รีวิวเก่าอยู่ (`gh pr view <n> --comments`) ให้ดูว่าแก้ครบหรือยัง

## 3. Build และ test

```bash
./gradlew build --console=plain        # Windows: gradlew.bat build --console=plain
```

- build หรือ test ไม่ผ่าน = **ต้องแก้ก่อน merge** ให้ใส่ error จริงในรายงาน
- ถ้าเจอ `Unable to delete directory ...\build\...` บน Windows แปลว่ามีโปรแกรม (มักเป็น Java extension ของ VS Code หรือ OneDrive) ล็อกโฟลเดอร์ build อยู่ ไม่ใช่ความผิดของโค้ด
  ให้ copy ไฟล์ที่ git track ไปโฟลเดอร์ชั่วคราวแล้ว build ที่นั่นแทน อย่าปิดโปรแกรมของผู้ใช้เอง
- ถ้าเป็น PR ให้ดูผล CI ด้วย: `gh pr checks <n>`
- งานที่เปลี่ยนภาพหรือการเล่น: build ผ่านไม่ได้แปลว่าเล่นได้ ถ้าเปิดเกมทดสอบไม่ได้ ให้เขียนในรายงานว่า "ยังไม่ได้ทดสอบโดยการเล่นจริง"

## 4. สิ่งที่ต้องตรวจ

ตรวจเฉพาะโค้ดที่ PR นี้เปลี่ยนหรือได้รับผลกระทบ ปัญหาเก่าที่ไม่เกี่ยวให้แยกไว้ท้ายรายงาน

### ก. ความถูกต้อง (สำคัญที่สุด)
- logic ผิด, null, index เกินขอบ, หาร 0, ค่าติดลบที่ไม่ควรติดลบ (HP, MP, ดาเมจ)
- `Character`: ค่าสถานะต้องเปลี่ยนผ่าน method (`takeDamage`, `heal`, `spendMp` ...) เท่านั้น และไม่เกินค่า max
- `Inventory`: เคารพ `CAPACITY` และ `add()` คืน false เมื่อเต็ม ผู้เรียกต้องจัดการกรณีนั้น
- `BattleSystem`: จบการต่อสู้ครบทุกทาง (WON / LOST / FLED) และไม่มีโค้ดวาดภาพ
- stack ของ state ใน `GameManager`: ทุก `push` มี `pop` ที่ตรงกัน ใช้ `setState` เมื่อต้องการล้างทั้ง stack (เช่นไปหน้า End)
- `WorldBuilder` / `TileMap`: ทุกแถวของแผนที่ยาวเท่ากัน วางของบนช่องที่เดินได้ ประตูทั้งสองฝั่งชี้หากันถูก
- พิกัด: `y` นับจากล่างขึ้นบน แปลงแถวข้อความด้วย `rowToY()` เสมอ

### ข. libGDX
- ทุก `Texture`, `Sound`, `Music`, `BitmapFont`, `ShapeRenderer`, `SpriteBatch` ที่สร้างใหม่ต้องถูก `dispose()` และ**ห้ามสร้างใน `render()` / `update()`** ทุกเฟรม (หน่วยความจำรั่ว)
- `shapes.begin()` / `end()` และ `batch.begin()` / `end()` ต้องคู่กัน และห้ามซ้อนกัน
- วาดสีโปร่งใสต้องเรียก `Art.enableBlend()` ก่อน `begin()` (`SpriteBatch.end()` ปิด blend ทิ้ง)
- การเคลื่อนไหวและเวลาใช้ `dt` ไม่ผูกกับจำนวนเฟรม
- ปุ่ม: ใช้ `Keys` ไม่เรียก `Gdx.input` ตรง ๆ ใน state ใช้ `*Just` สำหรับกดครั้งเดียว และ `*Held` สำหรับกดค้าง
- ไฟล์ asset อยู่ใน `assets/` และมีที่มา/license ใน `assets/CREDITS.txt`

### ค. หลัก OOP (วิชานี้ให้คะแนนตรงนี้)
- **Encapsulation:** field เป็น `private` ไม่มี public field ที่แก้ค่าได้ ไม่คืน collection ภายในให้คนนอกแก้ (ใช้ `Collections.unmodifiableList`)
- **Inheritance / Abstraction:** คลาสใหม่ต่อยอดจาก base ที่มีอยู่ (`Enemy`, `Item`, `Tile`, `GameObject`, `GameState`, `Skill`) แทนการเขียนซ้ำ
- **Polymorphism:** ระวัง `instanceof` หรือ `switch` ตามชนิดคลาสที่ควรเป็น method ที่ override ได้ (เพิ่มศัตรูใหม่ไม่ควรต้องแก้ `BattleSystem`)
- **Separation of Concerns:** กติกาเกมไม่ปนกับโค้ดวาดภาพ
- Javadoc ของคลาสใหม่บอกว่าใช้หลัก OOP ข้อไหน (อยู่ใน Definition of Done)

### ง. กติกาทีม (`AGENTS.md`)
- ชื่อ branch: `<feature|fix|chore>/GQ-<เลข>-<คำอธิบาย>` และ 1 branch = 1 ticket
- ข้อความ commit ขึ้นต้นด้วย `GQ-<เลข>:`
- แก้ไฟล์ของคนอื่นตามตารางเจ้าของไฟล์หรือไม่ ถ้าแก้ คำอธิบาย PR ต้องบอกไว้
- เปลี่ยนชื่อหรือพารามิเตอร์ของ method ใน `util/Art.java` ที่ส่วนอื่นเรียกอยู่ = ต้องแก้
- ไม่มีไฟล์ที่ไม่ควร commit: `build/`, `.gradle/`, `.idea/`, `save.json`, ไฟล์ส่วนตัว
- คำอธิบาย PR มีเลข ticket, สิ่งที่เปลี่ยน และวิธีทดสอบ (แนะนำ `Closes #<issue>`)

### จ. checklist ของ ticket
ไล่ทุกข้อใน issue ว่าทำแล้ว / ทำบางส่วน / ยังไม่ทำ โดยอ้างไฟล์และบรรทัดที่เป็นหลักฐาน

## 5. ก่อนเขียนรายงาน: ยืนยันทุกข้อ

- ทุกปัญหาต้องชี้ `ไฟล์:บรรทัด` ได้ และอธิบายได้ว่า **เกิดอะไรขึ้นจริงเมื่อเล่น** (input → ผลที่ผิด)
- ถ้ายืนยันไม่ได้ ให้ลงในหัวข้อ "ข้อสงสัย" แทน อย่าเขียนเหมือนเป็นบั๊กแน่นอน
- อย่ารายงานเรื่องรสนิยม (เว้นวรรค, ชื่อตัวแปรที่อ่านเข้าใจอยู่แล้ว) เป็นปัญหา

## 6. รูปแบบรายงาน (ภาษาไทย)

```markdown
## ผลรีวิว: <ชื่อ PR หรือ branch>
**สรุป:** ✅ พร้อม merge | ⚠️ แก้ก่อน merge | ❌ ยังไม่พร้อม
Build: ผ่าน/ไม่ผ่าน · CI: ผ่าน/ไม่ผ่าน/ไม่มี · ทดสอบโดยเล่นจริง: ทำแล้ว/ยังไม่ได้ทำ

### ต้องแก้ก่อน merge
1. **<ปัญหาสั้น ๆ>** — `path/File.java:42`
   เกิดอะไรขึ้น: ...
   วิธีแก้ที่แนะนำ: ...

### ควรแก้ (ไม่บล็อกการ merge)
- ...

### Checklist ของ GQ-<เลข>
| ข้อ | สถานะ | หลักฐาน |
|---|---|---|

### กติกาทีม
- ชื่อ branch / commit / เจ้าของไฟล์ / คำอธิบาย PR: ...

### ข้อสงสัย
- ...

### ปัญหาเก่าที่เจอระหว่างรีวิว (ไม่เกี่ยวกับ PR นี้)
- ...
```

หัวข้อไหนไม่มีเนื้อหาให้ตัดทิ้ง เรียงปัญหาจากร้ายแรงที่สุดก่อน

## 7. หลังรีวิว

- ถามผู้ใช้ว่าจะให้ทำอะไรต่อ เช่น แก้ให้, โพสต์ผลรีวิวลง PR, หรือเปิด issue สำหรับปัญหาเก่า
- ถ้าผู้ใช้สั่งให้โพสต์ลง PR ใช้คอมเมนต์ธรรมดา ไม่ใช่ approve:
  ```bash
  gh pr review <n> --comment --body-file review.md
  ```
  ห้ามใส่ข้อความแนว "Generated with …" หรือชื่อ AI ในคอมเมนต์
