# Sprint Task: Monsters & Girls — Missing Blocks, Wood Sets, Hat Items, Jar — ADR-025 Full Implementation

**Status**: 📋 PLANNED
**Started**: —
**Target Completion**: —
**Priority**: High
**Complexity**: High
**Sprint Number**: 16

## Sprint Goal

Implement all missing blocks, wood sets, decorative items, and hat items specified in ADR-025
for `monsters_girls-1.21.1`. By the end of this sprint: three full fungal wood sets (Ender
Puffball, Molten Fungus, Soul Wanderer) are craftable and render correctly; 12 huge decorative
mushroom blocks are placeable; the Jar, two Urns, Ender Moss, and Glow Berry Bush are live;
12 hat items equip in the head slot with correct armor layer textures; the new `MonstersItems`
and `MonstersArmorMaterials` registration classes exist; and all assets are migrated from the
1.20.1 open-source archive.

## Strategic Context

**Source ADR**: `docs/development/decisions/ADR_025_MonstersGirls_Missing_Blocks_Items.md`
**Prerequisite**: `MonstersBlocks.java` and `MonstersConstant.java` already exist — this sprint extends them
**Asset source**: `archive/outsource/monsters-girls-1.20.1/src/main/resources/assets/monsters_girls/`
**Affects**: `monsters_girls-1.21.1` (Common only — all registration uses `BuiltInRegistries`)
**Not for**: Any loader-specific files — all new content registers in Common
**Build dependency**: Phase A → Phase B → Phase C → Phase D → Phase E

---

## Story Points

| Phase | Work | Points |
|-------|------|--------|
| A | Asset migration from 1.20.1 archive | 5 |
| B | New block classes + `MonstersBlocks` wood sets | 8 |
| C | Huge mushrooms, decorations, jar, urns | 4 |
| D | `MonstersArmorMaterials` + `MonstersItems` + hat items | 5 |
| E | Blockstates, block models, item models, loot tables, recipes, lang | 6 |
| F | Render layers, creative tabs, verification | 2 |
| **Total** | | **30** |

> **Note:** 30 points is above standard sprint capacity. Split into two sub-sprints if needed:
> Sub-sprint 16A (Phases A–C, wood sets + decoration blocks) and 16B (Phases D–F, items + hats + resources).

---

## Objectives

### Phase A — Asset migration from 1.20.1 archive
> **Copy all reusable assets first. Every block and item needs textures before blockstate/model JSON can reference them.**
> **Source root**: `archive/outsource/monsters-girls-1.20.1/src/main/resources/assets/monsters_girls/`
> **Target root**: `sources/monsters/monsters_girls-1.21.1/Common/src/main/resources/assets/monsters_girls/`

#### A.1 — Block textures (copy entire `textures/block/` directory)

All block textures from the archive are directly reusable in 1.21.1 (PNG format, same resolution,
same naming convention). Copy all of the following:

- [ ] Copy all **wood set textures** (Ender Puffball, Molten Fungus, Soul Wanderer) — 47 PNG files:
  ```
  ender_puffball_block.png
  ender_puffball_stem.png            ender_puffball_stem_top.png
  ender_puffball_hyphae.png          ender_puffball_hyphae_top.png
  ender_puffball_stripped_stem.png   ender_puffball_stripped_stem_top.png
  ender_puffball_stripped_hyphae.png ender_puffball_stripped_hyphae_top.png
  ender_puffball_planks.png
  ender_puffball_door_bottom.png     ender_puffball_door_top.png
  ender_puffball_trapdoor.png
  shroomlight_ender.png
  molten_fungus_block.png
  molten_fungus_stem.png             molten_fungus_stem_top.png
  molten_fungus_hyphae.png           molten_fungus_hyphae_top.png
  molten_fungus_stripped_stem.png    molten_fungus_stripped_stem_top.png
  molten_fungus_stripped_hyphae.png  molten_fungus_stripped_hyphae_top.png
  molten_fungus_planks.png
  molten_fungus_door_bottom.png      molten_fungus_door_top.png
  molten_fungus_trapdoor.png
  shroomlight_molten.png
  soul_wanderer_block.png
  soul_wanderer_stem.png             soul_wanderer_stem_top.png
  soul_wanderer_hyphae.png           soul_wanderer_hyphae_top.png
  soul_wanderer_stripped_stem.png    soul_wanderer_stripped_stem_top.png
  soul_wanderer_stripped_hyphae.png  soul_wanderer_stripped_hyphae_top.png
  soul_wanderer_planks.png
  soul_wanderer_door_bottom.png      soul_wanderer_door_top.png
  soul_wanderer_trapdoor.png
  shroomlight_soul.png
  ```
- [ ] Copy all **Inkcap mushroom block textures** (3 files):
  ```
  ink_cap_black_mushroom_block.png
  ink_cap_grey_mushroom_block.png
  ink_cap_light_grey_mushroom_block.png
  ```
- [ ] Copy all **huge mushroom/fungus block textures** (12 files):
  ```
  huge_brown_mushroom.png         huge_crimson_fungus.png
  huge_crimson_rare_fungus.png    huge_ender_puffball_mushroom.png
  huge_fly_red_agaric_mushroom.png huge_fly_yellow_agaric_mushroom.png
  huge_infernal_mushroom.png      huge_ink_cap_mushroom.png
  huge_molten_fungus.png          huge_soul_wanderer_mushroom.png
  huge_warped_fungus.png          huge_warped_rare_fungus.png
  ```
- [ ] Copy **decoration block textures** (8 files):
  ```
  ender_moss_side.png   ender_moss_side_overlay.png   ender_moss_top.png
  glow_berry_bush.png   glow_berry_bush_mature.png
  jar.png
  urn_molten.png        urn_crimson.png
  ```

#### A.2 — Item textures (copy to `textures/item/`)

- [ ] Copy **hat item textures** (12 files — already in archive `textures/item/`):
  ```
  hat_mushroom_brown.png           hat_mushroom_crimson.png
  hat_mushroom_crimson_rare.png    hat_mushroom_ender_puffball.png
  hat_mushroom_fly_red_agaric.png  hat_mushroom_fly_yellow_agaric.png
  hat_mushroom_infernal.png        hat_mushroom_ink_cap.png
  hat_mushroom_molten.png          hat_mushroom_soul_wanderer.png
  hat_mushroom_warped.png          hat_mushroom_warped_rare.png
  ```
- [ ] Copy **huge mushroom item textures** (12 files):
  ```
  huge_brown_mushroom.png          huge_crimson_fungus.png
  huge_crimson_rare_fungus.png     huge_ender_puffball_mushroom.png
  huge_fly_red_agaric_mushroom.png huge_fly_yellow_agaric_mushroom.png
  huge_infernal_mushroom.png       huge_ink_cap_mushroom.png
  huge_molten_fungus.png           huge_soul_wanderer_mushroom.png
  huge_warped_fungus.png           huge_warped_rare_fungus.png
  ```
- [ ] Copy **wood-set door item textures** (3 files):
  ```
  ender_puffball_door.png   molten_fungus_door.png   soul_wanderer_door.png
  ```
- [ ] Copy **decoration item textures** (3 files):
  ```
  jar.png   urn_molten.png   urn_crimson.png
  ```
  > **Note:** `jar.png` is already present at `textures/item/jar.png` in the archive. The item
  > texture for the jar block item uses the same PNG as the block texture.

#### A.3 — Copy blockstates from archive (copy entire set)
Source: `archive/.../assets/monsters_girls/blockstates/`
Target: `sources/.../assets/monsters_girls/blockstates/`

The 1.20.1 archive has blockstate JSONs that are directly usable as-is for the directional
and standard blocks. The mod ID is `monsters_girls` in both versions.

- [ ] Copy blockstates for **huge mushrooms** (12 files):
  ```
  huge_brown_mushroom.json          huge_crimson_fungus.json
  huge_crimson_rare_fungus.json     huge_ender_puffball_mushroom.json
  huge_fly_red_agaric_mushroom.json huge_fly_yellow_agaric_mushroom.json
  huge_infernal_mushroom.json       huge_ink_cap_mushroom.json
  huge_molten_fungus.json           huge_soul_wanderer_mushroom.json
  huge_warped_fungus.json           huge_warped_rare_fungus.json
  ```
- [ ] Copy blockstates for **decoration/plant blocks** (5 files):
  ```
  ender_moss.json   glow_berry_bush.json   jar.json
  urn_molten.json   urn_crimson.json
  ```
  > **Wood set blockstates** (stairs, slabs, doors, trapdoors, pillars, etc.) are NOT in the
  > 1.20.1 archive — they must be **written from scratch** in Phase E.

#### A.4 — Copy block models from archive
Source: `archive/.../assets/monsters_girls/models/block/` and `models/custom/`
Target: `sources/.../assets/monsters_girls/models/block/`

- [ ] Copy **block models for huge mushrooms** (12 files from `models/block/`):
  ```
  huge_brown_mushroom.json          huge_crimson_fungus.json
  huge_crimson_rare_fungus.json     huge_ender_puffball_mushroom.json
  huge_fly_red_agaric_mushroom.json huge_fly_yellow_agaric_mushroom.json
  huge_infernal_mushroom.json       huge_ink_cap_mushroom.json
  huge_molten_fungus.json           huge_soul_wanderer_mushroom.json
  huge_warped_fungus.json           huge_warped_rare_fungus.json
  ```
- [ ] Copy **block models for decoration blocks** (5 files from `models/block/`):
  ```
  ender_moss.json   glow_berry_bush.json   glow_berry_bush_mature.json
  jar.json          urn_molten.json        urn_crimson.json
  ```
- [ ] Copy **custom OBJ/geometry model JSONs** (9 files from `models/custom/`):
  ```
  huge_fungus.json           huge_fungus_variant.json   huge_mushroom.json
  jar.json                   mandrake_flower.json
  medium_mushroom.json       small_mushroom.json
  urn_big.json               urn_small.json
  ```
  Target: `sources/.../assets/monsters_girls/models/custom/`

#### A.5 — Copy loot tables from archive
Source: `archive/.../data/monsters_girls/loot_tables/blocks/`
Target: `sources/.../data/monsters_girls/loot_tables/blocks/`

- [ ] Copy `ender_moss.json`, `glow_berry_bush_berries.json`, `urn_crimson.json`, `urn_molten.json`
  > Wood set block loot tables (drop self) must be **written from scratch** in Phase E.


---

### Phase B — New block classes + `MonstersBlocks` wood sets
> **All three wood sets registered in Common. Depends on Phase A textures being in place.**

#### B.1 — Port `DirectionalBlock.java`
- [ ] Create `net.heriazone.monsters_girls.block.DirectionalBlock` — extend `Block`, add
  `HORIZONTAL_FACING` `BlockState` property, `Map<Direction, VoxelShape> shapes` field,
  `codec()` returning `Block.simpleCodec(DirectionalBlock::new)`
- [ ] Override `createBlockStateDefinition()` to register `FACING`
- [ ] Override `getShape()` to look up shape from map by direction
- [ ] Verify no-arg constructor (required by codec) delegates to shape-less variant

#### B.2 — Port `BlockShapes.java`
- [ ] Create `net.heriazone.monsters_girls.block.BlockShapes` with the 8 shape map constants:
  `HUGE_MUSHROOM`, `HUGE_FUNGUS`, `HUGE_FUNGUS_VARIANT`, `MEDIUM_MUSHROOM`, `SMALL_MUSHROOM`,
  `JAR`, `BIG_URN`, `SMALL_URN`
- [ ] Port VoxelShape coordinates from `archive/outsource/monsters-girls-1.20.1/src/main/java/net/msymbios/monsters_girls/block/internal/VoxelCollision.java`
- [ ] Replace Fabric `ShapeContext` with MC 1.21.1 `CollisionContext` if needed

#### B.3 — Port `GlowBerryBushBlock.java`
- [ ] Create `net.heriazone.monsters_girls.block.GlowBerryBushBlock` — extend `BushBlock`
- [ ] Add `codec()` returning `Block.simpleCodec(GlowBerryBushBlock::new)`
- [ ] Override `entityInside()` (1.21.1 name for `onEntityCollision`) — apply Regeneration effect
  to any living entity that walks through it (copy logic from 1.20.1 `GlowBerryBush.java`)

#### B.4 — Add constants to `MonstersConstant.java`
All 87 new string constants from ADR-025 §3 — add in groups matching the ADR:
- [ ] Add Ender Puffball wood set constants (15): `ENDER_PUFFBALL_BLOCK` through `SHROOMLIGHT_ENDER`
- [ ] Add Molten Fungus wood set constants (15): `MOLTEN_FUNGUS_BLOCK` through `SHROOMLIGHT_MOLTEN`
- [ ] Add Soul Wanderer wood set constants (15): `SOUL_WANDERER_BLOCK` through `SHROOMLIGHT_SOUL`
- [ ] Add Inkcap block constants (3): `INK_CAP_BLACK_MUSHROOM_BLOCK`, `INK_CAP_GREY_MUSHROOM_BLOCK`, `INK_CAP_LIGHT_GREY_MUSHROOM_BLOCK`
- [ ] Add huge mushroom constants (12): `HUGE_BROWN_MUSHROOM` through `HUGE_WARPED_RARE_FUNGUS`
- [ ] Add decoration constants (5): `ENDER_MOSS`, `GLOW_BERRY_BUSH`, `JAR`, `URN_MOLTEN`, `URN_CRIMSON`
- [ ] Add hat item constants (12): `HAT_MUSHROOM_BROWN` through `HAT_MUSHROOM_WARPED_RARE`

#### B.5 — Add `public static Block` field declarations to `MonstersBlocks.java`
Add field declarations for all 45 new wood-set blocks + 3 Inkcap + 12 huge + 5 decoration = 65 fields:
- [ ] Declare all Ender Puffball fields (`ENDER_PUFFBALL_BLOCK` through `SHROOMLIGHT_ENDER`)
- [ ] Declare all Molten Fungus fields (`MOLTEN_FUNGUS_BLOCK` through `SHROOMLIGHT_MOLTEN`)
- [ ] Declare all Soul Wanderer fields (`SOUL_WANDERER_BLOCK` through `SHROOMLIGHT_SOUL`)
- [ ] Declare Inkcap, huge, and decoration fields

#### B.6 — Register Ender Puffball wood set in `MonstersBlocks.register()`
Follow ADR-025 §5.2 exactly. Add after existing potted block registrations:
- [ ] `ENDER_PUFFBALL_BLOCK` — `HugeMushroomBlock`, `ENDER_COLOR`
- [ ] `ENDER_PUFFBALL_STEM` / `ENDER_PUFFBALL_HYPHAE` — `RotatedPillarBlock`
- [ ] `ENDER_PUFFBALL_STRIPPED_STEM` / `ENDER_PUFFBALL_STRIPPED_HYPHAE` — `RotatedPillarBlock`
- [ ] `ENDER_PUFFBALL_PLANKS` — `Block`
- [ ] `ENDER_PUFFBALL_STAIRS` — `StairBlock(ENDER_PUFFBALL_PLANKS.defaultBlockState(), ...)`
- [ ] `ENDER_PUFFBALL_SLAB` — `SlabBlock`
- [ ] `ENDER_PUFFBALL_FENCE` / `ENDER_PUFFBALL_FENCE_GATE` — `FenceBlock` / `FenceGateBlock(WoodType.OAK, ...)`
- [ ] `ENDER_PUFFBALL_PRESSURE_PLATE` — `PressurePlateBlock(BlockSetType.OAK, ...)`
- [ ] `ENDER_PUFFBALL_BUTTON` — `ButtonBlock(BlockSetType.OAK, 30, ...)`
- [ ] `ENDER_PUFFBALL_DOOR` — `DoorBlock(BlockSetType.OAK, ...)`
- [ ] `ENDER_PUFFBALL_TRAPDOOR` — `TrapDoorBlock(BlockSetType.OAK, ...)`
- [ ] `SHROOMLIGHT_ENDER` — `Block`, copy from `Blocks.SHROOMLIGHT`

#### B.7 — Register Molten Fungus wood set
Identical pattern to B.6 — substitute `MOLTEN_COLOR`, all `MOLTEN_FUNGUS_*` constants.
No luminance on Molten (unlike Soul Wanderer):
- [ ] Register all 15 Molten Fungus blocks

#### B.8 — Register Soul Wanderer wood set
Identical pattern — substitute `SOUL_COLOR`, add `.lightLevel(state -> LUMINANCE)` to all
non-interactive blocks (all except button and pressure plate):
- [ ] Register all 15 Soul Wanderer blocks (with luminance)


---

### Phase C — Huge mushrooms, decorative blocks, Inkcap variants
> **DirectionalBlock from Phase B must be in place before these registrations.**

#### C.1 — Register Inkcap mushroom block variants (3)
- [ ] `INK_CAP_BLACK_MUSHROOM_BLOCK` — `HugeMushroomBlock`, `MapColor.COLOR_BLACK`
- [ ] `INK_CAP_GREY_MUSHROOM_BLOCK` — `HugeMushroomBlock`, `MapColor.COLOR_GRAY`
- [ ] `INK_CAP_LIGHT_GREY_MUSHROOM_BLOCK` — `HugeMushroomBlock`, `MapColor.COLOR_LIGHT_GRAY`

#### C.2 — Register huge decorative mushroom/fungus blocks (12)
Each uses `DirectionalBlock` + a shape constant from `BlockShapes` per ADR-025 §5.6:
- [ ] `HUGE_BROWN_MUSHROOM` — `DirectionalBlock`, `MapColor.COLOR_BROWN`, `BlockShapes.HUGE_MUSHROOM`
- [ ] `HUGE_CRIMSON_FUNGUS` — `MapColor.COLOR_RED`, `BlockShapes.HUGE_FUNGUS`
- [ ] `HUGE_CRIMSON_RARE_FUNGUS` — `MapColor.FIRE`, `BlockShapes.HUGE_FUNGUS`
- [ ] `HUGE_ENDER_PUFFBALL` — `ENDER_COLOR`, `BlockShapes.HUGE_MUSHROOM`
- [ ] `HUGE_FLY_RED_AGARIC` — `MapColor.COLOR_RED`, `BlockShapes.MEDIUM_MUSHROOM`
- [ ] `HUGE_FLY_YELLOW_AGARIC` — `MapColor.COLOR_YELLOW`, `BlockShapes.MEDIUM_MUSHROOM`
- [ ] `HUGE_INFERNAL_MUSHROOM` — `MapColor.COLOR_ORANGE`, `BlockShapes.HUGE_MUSHROOM`
- [ ] `HUGE_INK_CAP_MUSHROOM` — `MapColor.COLOR_BLACK`, `BlockShapes.HUGE_FUNGUS_VARIANT`
- [ ] `HUGE_MOLTEN_FUNGUS` — `MapColor.GOLD`, `BlockShapes.HUGE_MUSHROOM`
- [ ] `HUGE_SOUL_WANDERER` — `MapColor.DIAMOND`, luminance 5, `BlockShapes.HUGE_FUNGUS`
- [ ] `HUGE_WARPED_FUNGUS` — `MapColor.COLOR_CYAN`, `BlockShapes.SMALL_MUSHROOM`
- [ ] `HUGE_WARPED_RARE_FUNGUS` — `MapColor.WATER`, `BlockShapes.SMALL_MUSHROOM`

#### C.3 — Register Ender Moss
- [ ] `ENDER_MOSS` — `GrassBlock`, `MapColor.COLOR_PURPLE`, strength 3.0/9.0, `SoundType.STONE`

#### C.4 — Register Glow Berry Bush
- [ ] `GLOW_BERRY_BUSH` — `GlowBerryBushBlock`, `SoundType.SWEET_BERRY_BUSH`, no collision, no occlusion

#### C.5 — Register Jar and Urns
- [ ] `JAR` — `DirectionalBlock`, `MapColor.WATER`, strength 1.0/10.0, `SoundType.GLASS`, `BlockShapes.JAR`
- [ ] `URN_MOLTEN` — copy JAR properties, `MOLTEN_COLOR`, `BlockShapes.BIG_URN`
- [ ] `URN_CRIMSON` — copy JAR properties, `MapColor.COLOR_RED`, `BlockShapes.SMALL_URN`

---

### Phase D — `MonstersArmorMaterials`, `MonstersItems`, hat items
> **Blocks must be registered before block items. Armor materials must exist before hat items.**

#### D.1 — Create `MonstersArmorMaterials.java`
File: `Common/src/main/java/net/heriazone/monsters_girls/item/MonstersArmorMaterials.java`
- [ ] Create file with 12 `public static Holder<ArmorMaterial>` fields
- [ ] Implement `register()` static method — calls `Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL, ...)` for each of the 12 materials
- [ ] Each material: `defense = Map.of(ArmorItem.Type.HELMET, 2)`, `enchantability = 28`,
  `equipSound = SoundEvents.ARMOR_EQUIP_ELYTRA`, `toughness = 0.0f`, `knockbackResistance = 0.0f`
- [ ] Layer ID pattern: `MonstersIdentifier.getId(MonstersConstant.HAT_MUSHROOM_{FAMILY})`
- [ ] Repair ingredients per family (see ADR-025 §7.1 table)
- [ ] Verify 12 materials registered: Brown, Crimson, Crimson Rare, Ender Puffball, Fly Red Agaric,
  Fly Yellow Agaric, Infernal, Ink Cap, Molten, Soul Wanderer, Warped, Warped Rare

#### D.2 — Create `MonstersItems.java`
File: `Common/src/main/java/net/heriazone/monsters_girls/source/MonstersItems.java`
- [ ] Create file — all fields `public static Item`, two helpers (`registerBlockItem`, `registerItem`)
  using `Registry.register(BuiltInRegistries.ITEM, MonstersIdentifier.getId(name), item)`
- [ ] `registerBlockItems()` private method — one `BlockItem` per new block (65 block items total)
- [ ] `registerHatItems()` private method — 12 `ArmorItem(MonstersArmorMaterials.{X}, ArmorItem.Type.HELMET, ...)`
- [ ] `register()` public static method calls both
- [ ] Declare all 65 block item fields + 12 hat item fields as `public static Item`

Block items to register (65 total):
- [ ] 15 Ender Puffball wood-set block items
- [ ] 15 Molten Fungus wood-set block items
- [ ] 15 Soul Wanderer wood-set block items
- [ ] 3 Inkcap mushroom block variants
- [ ] 12 Huge mushroom/fungus block items
- [ ] 5 Decoration block items (Ender Moss, Glow Berry Bush, Jar, Urn Molten, Urn Crimson)

#### D.3 — Wire initialization in `Monsters.init()`
- [ ] Add `MonstersArmorMaterials.register()` call after `MonstersBlocks.register()`
- [ ] Add `MonstersItems.register()` call after `MonstersArmorMaterials.register()`
- [ ] Verify call order: Effects → Potions → Sounds → Blocks → ArmorMaterials → Items

#### D.4 — Hat armor layer textures (NEW — not in archive)
The 1.20.1 archive has item PNG icons (`hat_mushroom_brown.png`, etc.) but NOT the armor layer
textures used when the hat is worn on a player model. These must be created:
- [ ] Create 12 armor layer textures at path:
  `assets/monsters_girls/textures/models/armor/{key}_layer_1.png`
  (one per hat, rendered on the player head when equipped)
  - File names: `hat_mushroom_brown_layer_1.png` through `hat_mushroom_warped_rare_layer_1.png`
  > These are new artwork. Port the hat model texture from the GeckoLib model if hats had
  > dedicated GeckoLib rendering in 1.20.1, or create flat head-slot textures matching each hat's icon.


---

### Phase E — Blockstates, block models, item models, loot tables, recipes, lang
> **Largest resource phase. All JSON authoring. Wood sets have no archive equivalent — write from scratch.**

#### E.1 — Blockstate JSONs for wood sets (45 files NEW)
Target: `sources/.../assets/monsters_girls/blockstates/`

Standard patterns — write one file per block following vanilla conventions:
- [ ] **Simple cube blocks** (planks, shroomlight, Inkcap variants, mushroom blocks): single variant, no properties
- [ ] **RotatedPillarBlock** (stem, hyphae, stripped stem, stripped hyphae): variants for `axis=x/y/z`
- [ ] **StairBlock**: variants for `facing × half × shape` (16 combinations)
- [ ] **SlabBlock**: variants for `type=bottom/top/double`
- [ ] **FenceBlock**: multipart with `north/south/east/west` connection properties
- [ ] **FenceGateBlock**: variants for `facing × open × in_wall`
- [ ] **PressurePlateBlock**: variants for `powered=true/false`
- [ ] **ButtonBlock**: variants for `face × facing × powered`
- [ ] **DoorBlock**: variants for `facing × half × hinge × open × powered`
- [ ] **TrapDoorBlock**: variants for `facing × half × open`

Per family (15 blocks × 3 families = 45 files total):
- [ ] All 15 Ender Puffball blockstates
- [ ] All 15 Molten Fungus blockstates
- [ ] All 15 Soul Wanderer blockstates

#### E.2 — Block model JSONs for wood sets (65+ files NEW)
Target: `sources/.../assets/monsters_girls/models/block/`

Each block type needs 1–4 model files depending on complexity:
- [ ] **Planks / shroomlight / mushroom blocks**: single `cube_all` or `cube_column` model
- [ ] **Stem / hyphae**: `cube_column` with side/top textures
- [ ] **Stripped stem / hyphae**: `cube_column` with stripped side/top textures
- [ ] **Stairs**: 4 model files (`stairs`, `stairs_inner`, `stairs_outer`, `stairs_half`)
- [ ] **Slab**: 3 model files (`slab`, `slab_top`, `slab_double`)
- [ ] **Fence**: 3 model files (`fence_post`, `fence_side`, `fence_inventory`)
- [ ] **Fence gate**: 4 model files (`fence_gate`, `fence_gate_open`, `fence_gate_wall`, `fence_gate_wall_open`)
- [ ] **Pressure plate**: 2 model files (`pressure_plate_up`, `pressure_plate_down`)
- [ ] **Button**: 2 model files (`button`, `button_pressed`)
- [ ] **Door**: 4 model files (`door_bottom_left`, `door_bottom_right`, `door_top_left`, `door_top_right`)
- [ ] **Trapdoor**: 4 model files (`trapdoor_bottom`, `trapdoor_top`, `trapdoor_open`, `trapdoor_open`)

#### E.3 — Item model JSONs (80 files — mix of new and archive-adapted)
Target: `sources/.../assets/monsters_girls/models/item/`

- [ ] **Huge mushroom block items** (12): use `"parent": "item/generated"`, texture from `block/` namespace
  (archive has no item model JSONs for huge mushrooms — write from scratch using archive item textures)
- [ ] **Wood-set block items** (45): standard `item/generated` with block texture reference
  (stairs/slabs/fences: use `"parent": "monsters_girls:block/{key}"` inheriting the block model inventory variant)
- [ ] **Door items** (3): `item/generated` using dedicated door item textures
- [ ] **Hat items** (12): `item/generated` using `textures/item/hat_mushroom_{family}.png`
- [ ] **Jar, Urn Molten, Urn Crimson** (3): use archive `ender_moss.json` as reference for `item/generated` pattern
- [ ] **Ender Moss** (1): already in archive at `models/item/ender_moss.json` — verify path and copy
- [ ] **Glow Berry Bush** (1): `item/generated`, texture `textures/item/glow_berry_bush.png`

#### E.4 — Loot tables for wood sets (45 files NEW — drop self)
Target: `sources/.../data/monsters_girls/loot_tables/blocks/`

All wood-set blocks drop themselves (standard `minecraft:drop_item_on_break` pattern).
Doors drop 1 item. Buttons, pressure plates, trapdoors drop themselves.
- [ ] Write 45 loot table JSONs (one per wood-set block) using vanilla `drop_self` pattern:
  ```json
  { "type": "minecraft:block", "pools": [{ "rolls": 1,
    "entries": [{ "type": "minecraft:item", "name": "monsters_girls:{key}" }],
    "conditions": [{ "condition": "minecraft:survives_explosion" }] }] }
  ```
- [ ] Doors loot table — drop 1 item when either half is broken (use `minecraft:door` template)

#### E.5 — Loot tables for Inkcap blocks + huge blocks (15 files NEW)
- [ ] 3 Inkcap mushroom block loot tables (drop self)
- [ ] 12 Huge mushroom block loot tables (drop self, tool required)

#### E.6 — Recipes for wood sets (NEW)
Target: `sources/.../data/monsters_girls/recipe/`
- [ ] **Planks** (3): 1 stem/hyphae → 4 planks (shaped: single ingredient)
- [ ] **Stairs** (3): 6 planks → 4 stairs (L-shaped 3×3 pattern)
- [ ] **Slab** (3): 3 planks → 6 slabs (row pattern)
- [ ] **Fence** (3): 4 planks + 2 sticks → 3 fences
- [ ] **Fence gate** (3): 2 planks + 4 sticks → 1 fence gate
- [ ] **Door** (3): 6 planks → 3 doors (2×3 pattern)
- [ ] **Trapdoor** (3): 6 planks → 2 trapdoors (3×2 pattern)
- [ ] **Pressure plate** (3): 2 planks → 1 pressure plate
- [ ] **Button** (3): 1 plank → 1 button

#### E.7 — Lang keys
Target: `sources/.../assets/monsters_girls/lang/en_us.json`

Add to existing lang file:
- [ ] 45 wood-set block keys: `"block.monsters_girls.{key}": "{Display Name}"`
- [ ] 3 Inkcap block keys
- [ ] 12 huge mushroom block keys
- [ ] 5 decoration block keys (Ender Moss, Glow Berry Bush, Jar, Urn Molten, Urn Crimson)
- [ ] 12 hat item keys: `"item.monsters_girls.hat_mushroom_{family}": "{Family} Hat"`


---

### Phase F — Render layers, creative tabs, data tags, verification

#### F.1 — Render layer registration
The following blocks need `CUTOUT` render layer (transparent pixels in textures):
- [ ] Locate the existing client render layer registration site in `Monsters.initClient()` or a
  `MonstersBlocksClient` class — add all new cutout blocks there
- [ ] Add render layer registration for:
  - All 3 doors (`ENDER_PUFFBALL_DOOR`, `MOLTEN_FUNGUS_DOOR`, `SOUL_WANDERER_DOOR`)
  - All 3 trapdoors
  - All 12 `HUGE_*` blocks
  - `GLOW_BERRY_BUSH`
  - `JAR`, `URN_MOLTEN`, `URN_CRIMSON`

#### F.2 — Minecraft vanilla tags
Target: `sources/.../data/minecraft/tags/blocks/` and `data/minecraft/tags/items/`
- [ ] Add planks to `minecraft:planks` tag (blocks + items)
- [ ] Add slabs to `minecraft:wooden_slabs` (blocks + items)
- [ ] Add stairs to `minecraft:wooden_stairs` (blocks + items)
- [ ] Add fences to `minecraft:wooden_fences` + `minecraft:fences` (blocks + items)
- [ ] Add fence gates to `minecraft:fence_gates` + `minecraft:wooden_fence_gates` (blocks + items)
- [ ] Add buttons to `minecraft:wooden_buttons` + `minecraft:buttons` (blocks + items)
- [ ] Add pressure plates to `minecraft:wooden_pressure_plates` + `minecraft:pressure_plates`
- [ ] Add doors to `minecraft:wooden_doors` + `minecraft:doors`
- [ ] Add trapdoors to `minecraft:wooden_trapdoors` + `minecraft:trapdoors`
- [ ] Add stems/hyphae to `minecraft:logs` + `minecraft:logs_that_burn` (blocks + items)
- [ ] Add huge mushroom blocks to `minecraft:mineable/axe`
- [ ] Add stone-like blocks (`ENDER_MOSS`) to `minecraft:mineable/pickaxe`

#### F.3 — Creative tab placement
- [ ] Add all 65 new block items to the appropriate creative tabs in the M&G creative tab builder:
  - Wood sets → Building Blocks tab
  - Natural blocks (shroomlights, stems, huge mushrooms) → Natural Blocks tab
  - Decoration blocks (jar, urns, glow berry bush) → Decorations tab
  - Hat items → Equipment tab (or M&G default tab)
  > Reference: 1.20.1 `addItemsToBuildingBlocksGroup()` and `addItemsToNaturalGroup()` in
  > `MonstersGirlsBlocks.java` for the correct tab assignments

#### F.4 — In-game verification checklist
- [ ] All 45 wood-set blocks place, render with correct texture, and are mineable with the correct tool
- [ ] Planks craft correctly from stems; stairs/slabs craft from planks
- [ ] Doors open/close; trapdoors open/close; buttons trigger redstone
- [ ] All 12 huge mushroom blocks place with directional facing toward player
- [ ] Jar and Urns place with correct direction and shape
- [ ] Glow Berry Bush applies Regeneration to entities walking through it
- [ ] Ender Moss renders correctly (solid-looking grass with purple tint)
- [ ] All 12 hats equip in the head slot; armor layer texture appears on player model
- [ ] No missing textures (pink/black checkerboard) for any new block or item
- [ ] No log errors related to missing blockstates or model files on startup
- [ ] Creative tabs contain all new items in their correct groups
- [ ] Loot tables verified — all wood-set blocks drop the correct item when broken

---

## Files Created / Modified

### New Java files (Common)
| File | Type |
|------|------|
| `block/DirectionalBlock.java` | New — directional shape block |
| `block/BlockShapes.java` | New — VoxelShape constants |
| `block/GlowBerryBushBlock.java` | New — glow berry bush plant |
| `item/MonstersArmorMaterials.java` | New — 12 armor material holders |
| `source/MonstersItems.java` | New — 77 item registrations |

### Modified Java files (Common)
| File | Change |
|------|--------|
| `MonstersConstant.java` | +87 string constants |
| `source/MonstersBlocks.java` | +65 block field declarations + registrations |
| `Monsters.java` | +2 init calls (ArmorMaterials + Items) |

### Assets migrated from archive (copy tasks — no authoring needed)
| Category | Count | Source |
|----------|-------|--------|
| Block textures (wood sets) | 47 PNG | `archive/.../textures/block/` |
| Block textures (Inkcap variants) | 3 PNG | `archive/.../textures/block/` |
| Block textures (huge mushrooms) | 12 PNG | `archive/.../textures/block/` |
| Block textures (decorations) | 8 PNG | `archive/.../textures/block/` |
| Item textures (hats) | 12 PNG | `archive/.../textures/item/` |
| Item textures (huge mushrooms) | 12 PNG | `archive/.../textures/item/` |
| Item textures (doors + decorations) | 6 PNG | `archive/.../textures/item/` |
| Blockstates (huge mushrooms) | 12 JSON | `archive/.../blockstates/` |
| Blockstates (decorations + jar + urns) | 5 JSON | `archive/.../blockstates/` |
| Block models (huge mushrooms) | 12 JSON | `archive/.../models/block/` |
| Block models (decorations) | 6 JSON | `archive/.../models/block/` |
| Custom geometry models | 9 JSON | `archive/.../models/custom/` |
| Loot tables | 4 JSON | `archive/.../loot_tables/blocks/` |
| **Subtotal** | **148 files** | Copied from archive |

### New resource files (authoring required)
| Category | Count | Notes |
|----------|-------|-------|
| Block textures (fences, buttons, slabs — same as planks) | 0 | Reuse planks/stems textures |
| Blockstates (wood sets) | 45 JSON | Write from scratch |
| Block models (wood sets) | ~80 JSON | Write from scratch |
| Item models (all new items) | 80 JSON | Write from scratch |
| Loot tables (wood sets + Inkcap + huge) | 60 JSON | Write from scratch |
| Recipes (wood sets) | 27 JSON | Write from scratch |
| Armor layer textures (hats) | 12 PNG | New artwork |
| Lang keys added to `en_us.json` | 77 entries | Add to existing file |
| **Subtotal** | **~304 files** | New authoring |

**Total resource files**: ~452
**Total Java files**: 8

---

## Definition of Done

- [ ] All 65 new blocks register without errors on startup
- [ ] All 77 new items register without errors on startup
- [ ] All 12 hats equip correctly with visible armor layer
- [ ] No missing texture errors for any new content
- [ ] All wood-set blocks craft from planks; planks craft from stems
- [ ] All directional blocks (huge mushrooms, jar, urns) face the player on placement
- [ ] Glow Berry Bush effect fires on entity contact
- [ ] All new content accessible from correct creative tabs
- [ ] Vanilla tags populated (planks, slabs, stairs, etc. recognized by other mods)
- [ ] All loot tables verified in-game (blocks drop correct items)
- [ ] Full build passes with no errors or warnings
- [ ] All checklist items in ADR-025 §10 ticked
- [ ] Commits follow `GIT_COMMIT_GUIDELINES.md` — use `FEAT:` prefix for content, `ASSETS:` for asset migration
