# Missing Items & Blocks Catalogue — Monsters & Girls (monsters_girls-1.21.1)

**Status**: Planning
**Last Updated**: 2026-07-02
**Author(s)**: Kiro
**Related Documents**:
- `archive/outsource/source/MonstersGirls_1.3.4.3_1.20.1/` — reference source (compiled classes)
- `archive/outsource/monsters-girls-1.20.1/` — reference source (open source)
- `sources/monsters/monsters_girls-1.21.1/` — target codebase

---

## Purpose

This catalogue tracks every item, block, and decoration block that exists in the 1.3.4.3 reference codebase
but has **not yet been implemented** in the 1.21.1 rewrite. Use it as an implementation checklist —
tick items off as they are added.

---

## What Is Already Implemented in 1.21.1

### Blocks

Five mushroom plant blocks (genesis/planting targets) + five potted variants. Registered in `MonstersBlocks`.

| Block | Registry Key | Notes |
|-------|-------------|-------|
| Inkcap Mushroom | `inkcap_mushroom` | Plant block, MushroomPlantBlock |
| Molten Fungus | `molten_fungus` | Plant block, light 4 |
| Ender Mushroom | `ender_mushroom` | Plant block |
| Snowball Mushroom | `snowball_mushroom` | Plant block |
| Soul Wanderer Fungus | `soul_wanderer_fungus` | Plant block, light 4 |
| Potted Inkcap Mushroom | `potted_inkcap_mushroom` | FlowerPotBlock |
| Potted Molten Fungus | `potted_molten_fungus` | FlowerPotBlock, light 4 |
| Potted Ender Mushroom | `potted_ender_mushroom` | FlowerPotBlock |
| Potted Snowball Mushroom | `potted_snowball_mushroom` | FlowerPotBlock |
| Potted Soul Wanderer Fungus | `potted_soul_wanderer_fungus` | FlowerPotBlock, light 4 |

### Items (Food)

| Item | Notes |
|------|-------|
| Spectral Cake | `spectral_cake` — treat, nutrition 1, alwaysEdible |
| Candies | `candies` — treat, nutrition 1, alwaysEdible |
| Stew — Nether | `stew_nether` — Infernal/Crimson/Warped Gals exchange |
| Stew — Poison | `stew_poison` — Inkcap Gal exchange |
| Stew — Molten | `stew_molten` — Molten Gal exchange |
| Stew — Puffball | `stew_puffball` — Puffball Gal exchange |
| Stew — Snowball | `stew_snowball` — Snowball Gal exchange |
| Stew — Soul Wanderer | `stew_soul_wanderer` — Soul Wanderer Gal exchange |

### Items (Other)

| Item | Notes |
|------|-------|
| Genesis Powder | `powder_genesis` — triggers genesis on mushroom blocks |

---

## Section 1 — Missing Items

### 1.1 Hat Items (Wearable Armour)

All hat items existed in 1.3.4.3 as custom `ArmorItem` (head slot) with GeckoLib-rendered models.
Each hat is associated with a specific mushroom/fungus family and appears on the entity at a certain
relationship level (or is obtainable via drop/trade).

| Item | 1.3.4.3 Class | Associated Family | Status |
|------|--------------|-------------------|--------|
| Brown Mushroom Hat | `BrownMushroomHatItem` | Brown Mushroom Gal | ❌ Missing |
| Red Mushroom Hat | `RedMushroomHatItem` | Amanita / Red Mushroom | ❌ Missing |
| Infernal Mushroom Hat | `InfernaMushroomHatItem` | Infernal Gal | ❌ Missing |
| Crimson Hat | `CrimsonHatItem` | Crimson Gal | ❌ Missing |
| Warped Fungus Hat | `WarpedFungusHatItem` | Warped Gal | ❌ Missing |
| Inkcap Hat | `InkCapHatItem` | Inkcap Gal | ❌ Missing |
| Snowball Mushroom Hat | `SnowballHatItem` | Snowball Gal | ❌ Missing |
| Soul Wanderer Hat | `SoulWandererHatItem` | Soul Wanderer Gal | ❌ Missing |
| Ender Puffball Hat | `EnderPuffballHatItem` | Puffball Gal | ❌ Missing |
| Molten Fungus Hat | `MoltenFungusHatItem` | Molten Gal | ❌ Missing |
| Mizuno Crimson Hat | `MizunoCrimsonHatItem` | Rare Crimson variant | ❌ Missing |
| Mizuno Warped Hat | `MizunoWarpedHatItem` | Rare Warped variant | ❌ Missing |
| Yellow Agaric Cap | `YellowAgaricCapItem` | Fly Agaric / Amanita yellow | ❌ Missing |
| Airborne Ender Puffball Cap | `AirborneEnderPuffballCapItem` | Fluffball / flying puffball | ❌ Missing |

**Implementation notes:**
- Each hat needs: Java item class, `ArmorMaterial` registration, GeckoLib model (`.geo.json`),
  item texture PNG, `models/item/` JSON, lang key.
- The `$1` / `$2` inner-class suffixes in the compiled source indicate anonymous `ArmorMaterial`
  or renderer subclasses — check the 1.20.1 open source for the actual material definitions.
- Mizuno variants (`MizunoCrimsonHat`, `MizunoWarpedHat`) likely require dedicated textures
  separate from their standard counterparts.

---

### 1.2 Jar Items (Spawn / Display Items)

Jar items were portable spawn containers — right-clicking placed the entity or used as a display
item. Each is linked to a specific creature family.

| Item | 1.3.4.3 Class | Creature | Status |
|------|--------------|---------|--------|
| Brown Mushroom Gal in a Jar | `MushroomgirlinaJarItem` | Brown Mushroom Gal | ❌ Missing |
| Crimson Gal in a Jar | `CrimsonGirlinaJarItem` | Crimson Gal | ❌ Missing |
| Rare Crimson Gal in a Jar | `RareCrimsonGalInaJarItem` | Rare Crimson Gal | ❌ Missing |
| Rare Warped Gal in a Jar | `RareWarpedgirlinaJarItem` | Rare Warped Gal | ❌ Missing |
| Warped Gal in a Jar | `WarpedgirlinajarItem` | Warped Gal | ❌ Missing |
| Ender Puffball Gal in a Jar | `EnderPuffballGalInaJarItem` | Puffball Gal | ❌ Missing |
| Infernal Mushroom Gal in a Jar | `InfernalMushroomGalInaJarItem` | Infernal Gal | ❌ Missing |
| Molten Mushroom Gal in a Jar | `MoltenMushroomGalInaJarItem` | Molten Gal | ❌ Missing |
| Soul Wanderer Mushroom Gal in a Jar | `SoulWandererMushroomGirlInaJarItem` | Soul Wanderer Gal | ❌ Missing |
| Fly Agaric in a Jar | `FlyAlexgaricInaJarItem` | Fly Agaric | ❌ Missing |
| Brown Mushroom Gal in a Jar (alt) | `BrownMushroomGirlInBedItem` (bed variant) | Brown Gal | ❌ Missing |

**Implementation notes:**
- These may be reimplemented as spawn items (like genesis powder applied to a jar block),
  or as decorative placeable items that spawn a display-mode entity.
- The `JarTerrariumBlock` (see Section 2.3) is the block-form counterpart — consider
  whether jar items place a `JarTerrariumBlock` or summon the entity directly.
- All jar item textures exist in the 1.3.4.3 assets (`jar.png`, `jar2.png` … `jar8.png`,
  `jaralex.png`). They share a small set of jar shape PNGs with different entity-overlay renders.

---

### 1.3 Bed / Sleeping Items

"In Bed" items placed or summoned entities in a sleeping state (display/decoration use).

| Item | 1.3.4.3 Class | Notes | Status |
|------|--------------|-------|--------|
| Tiny Bed | `TinyBedItem` | Placed `TinyBedEntityEntity` — a tiny decorative bed | ❌ Missing |
| Snowball Mushroom Girl in Bed | `SnowballMushroomGirlInBedItem` | Snowball Gal sleeping display | ❌ Missing |
| Inkcap Mushroom Girl in Bed | `InkCapMushroomGirlInBedItem` | Inkcap Gal sleeping display | ❌ Missing |
| Airborne Ender Puffball Girl in Bed | `AirborneEnderPuffballGirlInBedItem` | Airborne/Fluffball sleeping display | ❌ Missing |

---

### 1.4 Consumable / Interaction Items

| Item | 1.3.4.3 Class | Function | Status |
|------|--------------|---------|--------|
| Sticky Goo (Item) | `StickyGooItemItem` | Throwable / placeable sticky goo | ❌ Missing |
| Sticky Goo Projectile | `StickyGooProjectileItem` | Projectile form of sticky goo | ❌ Missing |
| Cold Spit | `ColdSpitItem` | Projectile from Snowball Gal ranged attack | ❌ Missing |
| Ender Willow Fruit | `EnderWillowFruitItem` | Harvestable from Ender Willow tree | ❌ Missing |
| Red Station Carpet | `RedStationCarpetItem` | Placeable carpet item | ❌ Missing |
| Monster Girls Bestiary | `MonsterGirlsBestiaryItem` | Bestiary book item | ❌ Missing |

**Notes:**
- `StickyGooProjectileItem` / `ColdSpitItem` are projectile item forms fired by entities —
  these require companion `StickyGooProjectileEntity` / `ColdSpitEntity` (see Section 3).
- `MonsterGirlsBestiaryItem` had a custom GUI screen (`MonsterGirlsBestiaryItem$1` inner class).
  Decide whether to reimplement the full bestiary UI or simplify to a written book.
- `RedStationCarpetItem` relates to `StationCarpetEntity` — a decorative entity that
  renders as a carpet. Item and entity are tightly coupled.

---

### 1.5 Items Already Implemented (for reference — do not re-add)

| Item | Status |
|------|--------|
| Genesis Powder | ✅ Done (`GenesisPowderItem`) |
| Spectral Cake | ✅ Done (`SpectralCakeItem` via `MonstersFood`) |
| Candies | ✅ Done (`CandiesItem` via `MonstersFood`) |
| Stew — Nether | ✅ Done (`MonstersStewItem`) |
| Stew — Poison | ✅ Done |
| Stew — Molten | ✅ Done |
| Stew — Puffball | ✅ Done |
| Stew — Snowball | ✅ Done |
| Stew — Soul Wanderer | ✅ Done |

---

## Section 2 — Missing Blocks

### 2.1 Snowball Mushroom Wood Set

The Snowball Mushroom family had a full wood/fungal set in 1.3.4.3. None of these structural
blocks exist in the 1.21.1 codebase beyond the plant block itself.

| Block | 1.3.4.3 Class | Notes | Status |
|-------|--------------|-------|--------|
| Snowball Mushroom Block | `SnowballMushroomBlock` | Full mushroom block | ❌ Missing |
| Block of Snowball Mushroom | `BlockOfSnowballMushroomBlock` | Compressed block | ❌ Missing |
| Snowball Log | `SnowballLogBlock` | Log/stem variant | ❌ Missing |
| Snowball Wood | `SnowballWoodBlock` | Bark-all-sides log | ❌ Missing |
| Stripped Snowball Log | `SnowballStrippedLogBlock` | Stripped log | ❌ Missing |
| Snowball Stem Slab | `SnowballStemSlabBlock` | Half-stem slab | ❌ Missing |
| Snowball Planks | `SnowballPlanksBlock` | Wood planks | ❌ Missing |
| Snowball Slab | `SnowballSlabBlock` | Plank slab | ❌ Missing |
| Snowball Stairs | `SnowballStairsBlock` | Plank stairs | ❌ Missing |
| Snowball Fence | `SnowballFenceBlock` | Plank fence | ❌ Missing |
| Snowball Fence Gate | `SnowballFenceGateBlock` | Fence gate | ❌ Missing |
| Snowball Door | `SnowballDoorsBlock` | Door | ❌ Missing |
| Snowball Trapdoor | `SnowballTrapdoorBlock` | Trapdoor | ❌ Missing |
| Snowball Button | `SnowballButtonBlock` | Button | ❌ Missing |
| Snowball Pressure Plate | `SnowballPressurePlateBlock` | Pressure plate | ❌ Missing |
| Snowball Fungal Slab | `SnowballFungalSlabBlock` | Fungal half-slab | ❌ Missing |
| Snowball Shroomlight | `SnowballShroomlightBlock` | Light-emitting block | ❌ Missing |
| Snowball Shroomlight Slab | `SnowballShroomlightSlabBlock` | Half-shroomlight slab | ❌ Missing |

---

### 2.2 Soul Wanderer Wood Set

| Block | 1.3.4.3 Class | Notes | Status |
|-------|--------------|-------|--------|
| Soul Wanderer Mushroom Block | `SoulWandererMushroomBlock` | Full mushroom block | ❌ Missing |
| Soul Wanderer Block | `SoulWandererBlockBlock` | Compressed soul block | ❌ Missing |
| Soul Wanderer Log | `SoulWandererLogBlock` | Log | ❌ Missing |
| Soul Wanderer Wood | `SoulWandererWoodBlock` | Bark-all-sides log | ❌ Missing |
| Stripped Soul Wanderer Log | `SoulWandererStrippedLogBlock` | Stripped log | ❌ Missing |
| Soul Wanderer Stem | `SoulWandererStemBlock` | Stem variant | ❌ Missing |
| Soul Wanderer Stem Slab | `SoulWandererStemSlabBlock` | Half-stem slab | ❌ Missing |
| Soul Wanderer Fungal Slab | `SoulWandererFungalSlabBlock` | Fungal slab | ❌ Missing |
| Soul Wanderer Planks | `SoulWandererPlanksBlock` | Planks | ❌ Missing |
| Soul Wanderer Slab | `SoulWandererSlabBlock` | Plank slab | ❌ Missing |
| Soul Wanderer Stairs | `SoulWandererStairsBlock` | Plank stairs | ❌ Missing |
| Soul Wanderer Fence | `SoulWandererFenceBlock` | Fence | ❌ Missing |
| Soul Wanderer Fence Gate | `SoulWandererFenceGateBlock` | Fence gate | ❌ Missing |
| Soul Wanderer Door | `SoulWandererDoorsBlock` | Door | ❌ Missing |
| Soul Wanderer Trapdoor | `SoulWandererTrapdoorBlock` | Trapdoor | ❌ Missing |
| Soul Wanderer Button | `SoulWandererButtonBlock` | Button | ❌ Missing |
| Soul Wanderer Pressure Plate | `SoulWandererPressurePlateBlock` | Pressure plate | ❌ Missing |
| Soullight | `SoullightBlock` | Light-emitting block | ❌ Missing |
| Soullight Slab | `SoullightSlabBlock` | Half-soullight slab | ❌ Missing |

---

### 2.3 Molten Fungus Wood Set

| Block | 1.3.4.3 Class | Notes | Status |
|-------|--------------|-------|--------|
| Molten Fungus Block | `MoltenFungusBlockBlock` | Full fungus block | ❌ Missing |
| Molten Fungus Log | `MoltenMushroomLogBlock` | Log | ❌ Missing |
| Molten Fungus Wood | `MoltenMushroomWoodBlock` | Bark-all-sides log | ❌ Missing |
| Stripped Molten Log | `MoltenFungusStrippedLogBlock` | Stripped log | ❌ Missing |
| Molten Stem | `MoltenStemBlock` | Stem | ❌ Missing |
| Molten Stem Slab | `MoltenStemSlabBlock` | Half-stem slab | ❌ Missing |
| Molten Fungal Slab | `MoltenFungalSlabBlock` | Fungal slab | ❌ Missing |
| Molten Planks | `MoltenMushroomPlanksBlock` | Planks | ❌ Missing |
| Molten Slab | `MoltenMushroomSlabBlock` | Plank slab | ❌ Missing |
| Molten Stairs | `MoltenMushroomStairsBlock` | Plank stairs | ❌ Missing |
| Molten Fence | `MoltenMushroomFenceBlock` | Fence | ❌ Missing |
| Molten Fence Gate | `MoltenMushroomFenceGateBlock` | Fence gate | ❌ Missing |
| Molten Door | `MoltenFungusDoorsBlock` | Door | ❌ Missing |
| Molten Trapdoor | `MoltenFungusTrapdoorBlock` | Trapdoor | ❌ Missing |
| Molten Button | `MoltenMushroomButtonBlock` | Button | ❌ Missing |
| Molten Pressure Plate | `MoltenMushroomPressurePlateBlock` | Pressure plate | ❌ Missing |
| Molten Shroomlight | `MoltenShroomlightBlock` | Light-emitting block | ❌ Missing |
| Molten Shroomlight Slab | `MoltenShroomlightSlabBlock` | Half-shroomlight slab | ❌ Missing |

---

### 2.4 Ender Puffball Wood Set

| Block | 1.3.4.3 Class | Notes | Status |
|-------|--------------|-------|--------|
| Ender Puffball Mushroom (big) | `BigEnderPuffballBlock` | Giant puffball block | ❌ Missing |
| Block of Ender Puffball | `BlockofEnderPuffbalBlock` | Compressed block | ❌ Missing |
| Ender Puffball Log | `EnderPuffballLogBlock` | Log | ❌ Missing |
| Ender Puffball Wood | `EnderPuffballWoodBlock` | Bark-all-sides log | ❌ Missing |
| Stripped Ender Puffball Log | `EnderPuffballStrippedLogBlock` | Stripped log | ❌ Missing |
| Ender Puffball Stem Slab | `EnderPuffballStemSlabBlock` | Half-stem slab | ❌ Missing |
| Ender Puffball Fungal Slab | `EnderPuffballFungalSlabBlock` | Fungal slab | ❌ Missing |
| Ender Puffball Planks | `EnderPuffballPlanksBlock` | Planks | ❌ Missing |
| Ender Puffball Slab | `EnderPuffballSlabBlock` | Plank slab | ❌ Missing |
| Ender Puffball Stairs | `EnderPuffballStairsBlock` | Plank stairs | ❌ Missing |
| Ender Puffball Fence | `EnderPuffballFenceBlock` | Fence | ❌ Missing |
| Ender Puffball Fence Gate | `EnderPuffballFenceGateBlock` | Fence gate | ❌ Missing |
| Ender Puffball Door | `EnderPuffballDoorsBlock` | Door | ❌ Missing |
| Ender Puffball Trapdoor | `EnderPuffballTrapdoorBlock` | Trapdoor | ❌ Missing |
| Ender Puffball Button | `EnderPuffballButtonBlock` | Button | ❌ Missing |
| Ender Puffball Pressure Plate | `EnderPuffballPressurePlateBlock` | Pressure plate | ❌ Missing |
| End Puffball Block | `EndPuffballBlock` | Alternate end variant | ❌ Missing |
| Ender Shroomlight | `EnderShroomlightBlock` | Light-emitting block | ❌ Missing |
| Ender Shroomlight Slab | `EnderShroomlightSlabBlock` | Half-shroomlight slab | ❌ Missing |
| Potted Ender Puffball | `PottedEnderPuffballBlock` | FlowerPot variant | ❌ Missing |

---

### 2.5 Ender Willow Tree Set

The Ender Willow was a custom End-dimension tree in 1.3.4.3 with a full wood set.

| Block | 1.3.4.3 Class | Notes | Status |
|-------|--------------|-------|--------|
| Ender Willow Log | `EnderWillowLogBlock` | Log | ❌ Missing |
| Ender Willow Wood | `EnderWillowWoodBlock` | Bark-all-sides log | ❌ Missing |
| Stripped Ender Willow Log | `EnderWillowStrippedLogBlock` | Stripped log | ❌ Missing |
| Ender Willow Leaves | `EnderWillowLeavesBlock` | Leaf block (loot: ender willow fruit) | ❌ Missing |
| Ender Willow Sapling | `EnderWillowSaplingBlock` | Sapling | ❌ Missing |
| Ender Willow Planks | `EnderWillowPlanksBlock` | Planks | ❌ Missing |
| Ender Willow Slab | `EnderWillowSlabBlock` | Plank slab | ❌ Missing |
| Ender Willow Stairs | `EnderWillowStairsBlock` | Plank stairs | ❌ Missing |
| Ender Willow Fence | `EnderWillowFenceBlock` | Fence | ❌ Missing |
| Ender Willow Fence Gate | `EnderWillowFenceGateBlock` | Fence gate | ❌ Missing |
| Ender Willow Door | `EnderWillowDoorBlock` | Door | ❌ Missing |
| Ender Willow Trapdoor | `EnderWillowTrapdoorBlock` | Trapdoor | ❌ Missing |
| Ender Willow Button | `EnderWillowButtonBlock` | Button | ❌ Missing |
| Ender Willow Pressure Plate | `EnderWillowPressurePlateBlock` | Pressure plate | ❌ Missing |
| Ender Mushroom Stem | `EnderMushroomStemBlock` | Stem block (part of End tree) | ❌ Missing |

**Notes:** The Ender Willow Leaves drop `EnderWillowFruitItem` (see Section 1.4).
The tree also requires a world generation structure / feature for placement in the End biome.

---

### 2.6 Big Mushroom / Fungus Blocks (Giant Variants)

Each mushroom family had a giant block form that spawned in the world or was placed decoratively.

| Block | 1.3.4.3 Class | Family | Status |
|-------|--------------|--------|--------|
| Big Brown Mushroom Block | `BigBrownMushroomBlock` | Brown | ❌ Missing |
| Big Red Mushroom Block | `BigRedMushroomBlock` | Amanita / Red | ❌ Missing |
| Big Crimson Fungus Block | `BigCrimsonFungusBlock` | Crimson | ❌ Missing |
| Big Rare Crimson Fungus Block | `BigRareCrimsonFungusBlock` | Rare Crimson | ❌ Missing |
| Big Rare Warped Fungus Block | `BigRareWarpedFungusBlock` | Rare Warped | ❌ Missing |
| Big Warped Fungus Block | `BigWarpedFungusBlock` | Warped | ❌ Missing |
| Big Infernal Mushroom Block | `BigInfernalMushroomBlock` | Infernal | ❌ Missing |
| Big Inkcap Mushroom Block | `BigInkCapMushroomBlock` | Inkcap | ❌ Missing |
| Big Molten Fungus Block | `BigMoltenFungusBlock` | Molten | ❌ Missing |
| Big Snowball Mushroom Block | `BigSnowballMushroomBlock` | Snowball | ❌ Missing |
| Big Soul Wanderer Block | `BigSoulWandererBlock` | Soul Wanderer | ❌ Missing |
| Big Yellow Mushroom Block | `BigYellowMushroomBlock` | Amanita yellow | ❌ Missing |
| Big Airborne Ender Puffball Block | `BigAirborneEnderPuffballBlock` | Fluffball / Puffball | ❌ Missing |
| Giant Puffball Block | `GiantpuffballblockBlock` | Puffball | ❌ Missing |

---

### 2.7 Inkcap Mushroom Stages

The Inkcap mushroom had three visual growth stages as separate blocks.

| Block | 1.3.4.3 Class | Notes | Status |
|-------|--------------|-------|--------|
| Inkcap Stage 1 | `Inkcap1Block` | Young inkcap | ❌ Missing |
| Inkcap Stage 2 | `Inkcap2Block` | Mid-growth inkcap | ❌ Missing |
| Inkcap Stage 3 | `Inkcap3Block` | Full inkcap | ❌ Missing |
| Inkcap Mushroom Block | `InkCapMushroomBlock` | Large inkcap block | ❌ Missing |
| Potted Inkcap | `PottedInkCapBlock` | FlowerPot inkcap | ❌ Missing |

**Note:** `PottedInkCapBlock` is distinct from `POTTED_INKCAP_MUSHROOM` already registered in
`MonstersBlocks` — verify whether these are the same pot or a pot for the large mushroom block form.

---

### 2.8 Decoration & Special Blocks

| Block | 1.3.4.3 Class | Notes | Status |
|-------|--------------|-------|--------|
| Urn 1 | `Urn1Block` | Decorative urn variant 1 | ❌ Missing |
| Urn 2 | `Urn2Block` | Decorative urn variant 2 | ❌ Missing |
| Jar Terrarium | `JarTerrariumBlock` | Interactive terrarium — holds jar entities | ❌ Missing |
| Sticky Goo Block | `BlockOfStickyGooBlock` | Compressed sticky goo | ❌ Missing |
| Sticky Goo (placed) | `StickyGooBlock` | Placed sticky goo surface | ❌ Missing |
| Globberie Cocoon | `GlobberieCocoonBlock` | Globberie spawn cocoon | ❌ Missing |
| Mandrake Plant | `MandrakeBlock` | Mandrake growing block | ❌ Missing |
| Glow Berry Bush (lit) | `GlowBerryBushBlock` | Glow berry bush with light | ❌ Missing |
| Glow Berry Bush (unlit) | `GlowBerryBushUnlitBlock` | Glow berry bush without light | ❌ Missing |
| Ender Blossom | `EnderBlossomBlock` | Decorative End flower | ❌ Missing |
| Ender Ginger | `EnderGingerBlock` | End plant / decoration | ❌ Missing |
| Ender Sprouts | `EnderSproutsBlock` | End ground plant | ❌ Missing |
| Ender Moss | `EnderMossBlock` | End ground cover (has loot table) | ❌ Missing |

---

### 2.9 Potted Variants (Additional)

| Block | 1.3.4.3 Class | Notes | Status |
|-------|--------------|-------|--------|
| Potted Molten Fungus (block-form) | `PottedMoltenFungusBlock` | Pot for large molten form | ❌ Missing |
| Potted Snowball Mushroom (block-form) | `PottedSnowballMushroomBlock` | Pot for large snowball form | ❌ Missing |
| Potted Soul Wanderer (block-form) | `PottedSoulWandererBlock` | Pot for soul wanderer large form | ❌ Missing |

**Note:** The 1.21.1 codebase already has the five `POTTED_*` blocks for the plant (small) forms.
These entries are for potted versions of the **larger block forms** (big mushroom / fungus blocks)
that may be a separate registry entry rather than the same `FlowerPotBlock` instance.
Verify against the 1.20.1 open source to confirm whether these are truly distinct.

---

### 2.10 Blocks Already Implemented (for reference — do not re-add)

| Block | Status |
|-------|--------|
| `inkcap_mushroom` (plant) | ✅ Done |
| `molten_fungus` (plant) | ✅ Done |
| `ender_mushroom` (plant) | ✅ Done |
| `snowball_mushroom` (plant) | ✅ Done |
| `soul_wanderer_fungus` (plant) | ✅ Done |
| `potted_inkcap_mushroom` | ✅ Done |
| `potted_molten_fungus` | ✅ Done |
| `potted_ender_mushroom` | ✅ Done |
| `potted_snowball_mushroom` | ✅ Done |
| `potted_soul_wanderer_fungus` | ✅ Done |

---

## Section 3 — Missing Entities (Non-Family)

These entities existed in 1.3.4.3 but are **not** family-type companions — they are utility,
decoration, or projectile entities that need separate implementation.

| Entity | 1.3.4.3 Class | Notes | Status |
|--------|--------------|-------|--------|
| Sticky Goo Projectile | `StickyGooProjectileEntity` | Ranged attack projectile (Globberie) | ❌ Missing |
| Cold Spit | `ColdSpitEntity` | Ranged attack projectile (Snowball Gal) | ❌ Missing |
| Tiny Bed | `TinyBedEntityEntity` | Decorative placed entity (tiny bed) | ❌ Missing |
| Snowball In Bed | `SnowballInBedEntity` | Snowball Gal sleeping display entity | ❌ Missing |
| Inkcap In Bed | `InkCapBedEntity` | Inkcap Gal sleeping display entity | ❌ Missing |
| Airborne Puffball In Bed | `AirbornePuffballInBedEntity` | Fluffball sleeping display entity | ❌ Missing |
| Brown Jar | `BrownJarEntity` | Jar display entity (Brown Gal in jar) | ❌ Missing |
| Crimson Jar | `CrimsonJarEntity` | Jar display entity | ❌ Missing |
| Rare Crimson Jar | `RareCrimsonJarEntity` | Jar display entity | ❌ Missing |
| Rare Warped Jar | `RareWarpedJarEntity` | Jar display entity | ❌ Missing |
| Warped Jar | `WarpedJarEntity` | Jar display entity | ❌ Missing |
| Puffball Jar | `PuffballJarEntity` | Jar display entity | ❌ Missing |
| Red Jar | `RedJarEntity` | Jar display entity | ❌ Missing |
| Infernal Jar | `InfernalJarEntity` | Jar display entity | ❌ Missing |
| Molten Jar | `MoltenJarEntity` | Jar display entity | ❌ Missing |
| Soul Wanderer Jar | `SoulWandererJarEntity` | Jar display entity | ❌ Missing |
| Alex Jar | `AlexJarEntity` | Jar display entity (Fly Agaric) | ❌ Missing |
| Station Carpet | `StationCarpetEntity` | Decorative placed carpet entity | ❌ Missing |
| Invisible Entity | `InvisibleEntityEntity` | Utility / trigger entity | ❌ Missing |

**Notes:**
- The "Jar" entities are the entity forms placed by jar items — they render the creature inside
  a glass jar with a custom model. All share a similar base entity pattern.
- `StickyGooProjectileEntity` is required for `GlobberieFamily` ranged attack to work.
- `ColdSpitEntity` is required for `SnowballMushroomGirl` ranged attack to work.
- `StationCarpetEntity` and `RedStationCarpetItem` are tightly coupled — implement together.

---

## Section 4 — Missing Entity Families (Creatures)

These creature families existed in 1.3.4.3 but have **not been ported** to the 1.21.1 family
system. The 1.21.1 codebase uses a `MonstersFamily<T>` architecture — each entry here represents
a new `XFamily.java` class that needs to be written.

| Family | 1.3.4.3 Entity Class(es) | Notes | Status |
|--------|--------------------------|-------|--------|
| Warped Mushroom Gal | `WarpedMushroomGalEntity` + `WarpedMushroomGalBigSpotsEntity` | Two variants | ❌ Missing |
| Rare Crimson Gal | `CrimsonMushroomGirlBigspotsEntity` | Rare / big spots variant | ❌ Missing |
| Rare Warped Gal | `WarpedMushroomGalBigSpotsEntity` | Rare / big spots variant | ❌ Missing |
| Fly Agaric (FlyAlex) | `FlyAlexgaricEntity` | Yellow agaric mushroom girl | ❌ Missing |
| Poofed Endershroom | `PoofedEndershroomEntity` | End mushroom variant | ❌ Missing |
| Peach | `PeachEntity` | Standalone peach creature | ❌ Missing |

**Notes:**
- Warped Gal was absent from the 1.21.1 `MushroomFamily` definitions — only Crimson was ported
  to the Nether mushroom group. Warped needs its own family entry alongside Crimson.
- Rare Crimson / Rare Warped are palette/visual variants — may be implemented as `TextureVariant`
  entries on the existing `CrimsonFamily` / future `WarpedFamily` rather than separate families.
- `FlyAlexgaricEntity` (Fly Agaric / Yellow Agaric) is a distinct mushroom-girl type with its own
  model and animations — needs a new `FlyAgaricFamily` or addition to `MushroomFamily`.
- `PeachEntity` is a standalone creature unrelated to mushrooms — needs its own `PeachFamily.java`.
- `PoofedEndershroomEntity` may overlap with the existing `FluffballFamily` — verify before
  creating a new family; it may just be a missing texture variant.

---

## Section 5 — Missing Potions & Effects

| Effect | 1.3.4.3 Class | Notes | Status |
|--------|--------------|-------|--------|
| Blazing | referenced in stew descriptions | Custom mob effect from Molten stew | ❌ Verify |
| Puffy | referenced in stew descriptions | Custom mob effect from Puffball stew | ❌ Verify |
| Chilly | referenced in stew descriptions | Custom mob effect from Snowball stew | ❌ Verify |
| Soul Wanderer's Touch | referenced in stew descriptions | Custom mob effect | ❌ Verify |
| Poisonous (custom) | referenced in stew descriptions | May be vanilla Poison — verify | ❌ Verify |

**Note:** The `MonstersEffects.java` file in 1.21.1 exists but its content was not fully inspected.
Verify whether these custom effects are already registered there before adding them.

---

## Section 6 — Priority & Implementation Order

Items and blocks are grouped by implementation complexity and dependency chains.

### Tier 1 — Low complexity, high value (implement first)

These have no new systems — just registration, model, and texture work.

- All hat items (Section 1.1) — standard `ArmorItem`, existing pattern
- Jar items (Section 1.2) — simple items that either place blocks or summon entities
- Bed items (Section 1.3) — same as jar items
- Urn 1 & 2 (Section 2.8) — static decoration blocks, no logic
- Inkcap stages 1–3 (Section 2.7) — simple cross-sprite blocks like vanilla mushrooms
- Glow Berry Bush variants (Section 2.8) — copy vanilla glow berry block logic
- Ender Blossom / Ginger / Sprouts (Section 2.8) — static plant blocks
- Mandrake Plant block (Section 2.8) — single-block plant

### Tier 2 — Medium complexity (implement after Tier 1)

These need additional registration work but follow clear existing patterns.

- Big mushroom blocks for each family (Section 2.6) — copy vanilla big mushroom block
- All wood set blocks per family (Sections 2.1–2.5) — 4 full wood sets, repetitive but well-defined
- Sticky Goo Block (Section 2.8) — needs custom placement/physics behavior
- Globberie Cocoon (Section 2.8) — needs spawn logic on break
- Jar entity displays (Section 3) — simple display entities with GeckoLib rendering

### Tier 3 — Higher complexity, new systems needed

- `StickyGooProjectileEntity` + `ColdSpitEntity` (Section 3) — projectile system, need `RangedAttackGoal` on Globberie and Snowball Gal
- `JarTerrariumBlock` (Section 2.8) — interactive block with entity storage, BlockEntity needed
- Warped Gal / Rare variants (Section 4) — new `WarpedFamily` + rare sub-variants
- `FlyAgaricFamily` (Section 4) — new mushroom family with likely unique model
- `PeachFamily` (Section 4) — entirely standalone creature, new model/animator
- Ender Willow full tree (Section 2.5) — needs world generation feature for End biome
- Bestiary item (Section 1.4) — needs GUI screen implementation
- Custom mob effects (Section 5) — verify what's already in `MonstersEffects.java`

---

## Quick Summary Counts

| Category | Count Missing |
|----------|--------------|
| Hat items | 14 |
| Jar items | 11 |
| Bed / sleeping items | 4 |
| Consumable / special items | 6 |
| Snowball wood set blocks | 18 |
| Soul Wanderer wood set blocks | 19 |
| Molten Fungus wood set blocks | 18 |
| Ender Puffball wood set blocks | 20 |
| Ender Willow tree blocks | 15 |
| Big mushroom / fungus blocks | 14 |
| Inkcap stage blocks | 5 |
| Decoration / special blocks | 13 |
| Additional potted variants | 3 |
| Non-family entities | 20 |
| Missing creature families | 6 |
| Potions / effects (to verify) | 5 |
| **Total estimated missing** | **~191** |

---

*This catalogue was generated by cross-referencing the compiled class files in
`archive/outsource/source/MonstersGirls_1.3.4.3_1.20.1/` against the current
Java source and resource files in `sources/monsters/monsters_girls-1.21.1/`.
Always verify against the open-source reference at
`archive/outsource/monsters-girls-1.20.1/` for implementation details
before coding each entry.*
