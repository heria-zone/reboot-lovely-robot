# New Robot Family Implementation Guide

> ⚠️ **DEPRECATED — Do not follow this guide for new robot families.**
>
> This document describes the pre-ADR-023 scatter-based approach (13+ code locations per entity,
> manual switch statements, per-entity static config fields). It has been superseded in full.
>
> **Current authoritative reference: `docs/development/decisions/ADR-023_Entity-Registration-Consolidation.md`**
>
> Under the ADR-023 architecture, adding a new robot requires **exactly two code changes** in
> `lovelylib` — one `RobotVariant` constant and one `RobotEntityDefinition` builder call. No loader
> files change. The 13-location scatter problem, all four Bunny3 runtime bugs, and the manual switch
> statements described in this guide are eliminated.
>
> This file is preserved as a **historical reference only** — it accurately documents the bugs that
> motivated ADR-023 and remains useful for understanding why the current architecture is structured
> the way it is. Do not use Part 1, Part 2, or Part 3 as implementation instructions.
>
> **For an end-to-end example of the current approach, see:**
> - `docs/development/decisions/ADR_024_Reboot_Exclusive_Robot_Families.md` — Prime, Hyperion, Empyrium port
> - `sources/common/lovelylib-1.21.1/Common/.../source/SharedRobotDefinitions.java` — live examples

---

# [ARCHIVED] New Robot Family Implementation Guide

**Applies to (historical):** Legacy (`llovelyr`) and Reboot (`rlovelyr`) variants  
**Not for:** Tribute (`tlovelyr`) — fixed roster of 4 types, never extended  
**Last Updated:** 2026-06-28  
**Deprecated:** 2026-07-02 — superseded by ADR-023  
**Reference implementation (historical):** Bunny3 — verified against git diff of all 45 changed files  
**Related:** `docs/development/decisions/ADR-023_Entity-Registration-Consolidation.md`

---

## Overview (Historical)

The pre-ADR-023 approach required touching **four distinct layers** across **13 code locations**.
This section is preserved because Part 4 (the bug catalogue) accurately describes the failure modes
that drove the redesign and remains the definitive explanation of why each change in ADR-023 was necessary.

```
Layer 1 — lovelylib/common/entity        Variant enum, palette, family registration
Layer 2 — lovelylib/api + common/configs  ConfigAccessLayer fallback, SharedConfigs, LovelyIdentifier
Layer 3 — {mod}/loader (×3 platforms)    Entity type, spawn item, renderer, creative tab
Layer 4 — {mod}/Common/resources         Lang, recipes, item model predicates
```

A family belonging to both Legacy and Reboot requires changes in **lovelylib + llovelyr + rlovelyr** — three codebases.

---

## Lang Key Architecture — Read This First

Lang keys are split across two locations. Getting this wrong causes untranslated text or missing keys.

| Key prefix | Lives in | Example |
|------------|----------|---------|
| `variant.lovelylib.{key}` | `lovelylib` Common lang | Shown in wary/heal messages, UI selectors |
| `item.{modid}.{key}_spawn` | mod Common lang (`llovelyr` / `rlovelyr`) | Spawn item tooltip |
| `entity.{modid}.{key}` | mod Common lang (`llovelyr` / `rlovelyr`) | Entity display name in mod context |

The `variant.lovelylib.*` key is what `LovelyIdentifier.getVariantTranslation()` resolves — this is the one shown in overhead messages. It must live in lovelylib, not the mod.

---

## Step 0 — Pre-Work: Assets

Have these files present in `lovelylib` **before writing any Java**:

| Asset | Path | Notes |
|-------|------|-------|
| Default geo | `assets/lovelylib/geo/{key}.default.geo.json` | GeckoLib model |
| Armed geo | `assets/lovelylib/geo/{key}.armed.geo.json` | GeckoLib model |
| Entity textures | `assets/lovelylib/textures/entity/{key}/{key}_{id:02d}.png` | One per active color |
| Item textures | `assets/lovelylib/textures/item/{key}/{key}_{id:02d}.png` | One per active color |
| Item random icon | `assets/lovelylib/textures/item/{key}/{key}_16.png` | Default/random display icon |

`{id:02d}` = zero-padded `EntityTexture` enum ID. Reference:

| ID | Name | ID | Name |
|----|----|----|----|
| 00 | white | 08 | light_gray |
| 01 | orange | 09 | cyan |
| 02 | magenta | 10 | purple |
| 03 | light_blue | 11 | blue |
| 04 | yellow | 12 | brown |
| 05 | lime | 13 | green |
| 06 | pink | 14 | red |
| 07 | gray | 15 | black |
| 16 | random (icon only) | — | — |

---

## Part 1 — lovelylib: Layer 1 — Core Registration

### 1.1 `LovelyConstant.java`

**File:** `.../lovelylib/common/shared/LovelyConstant.java`

Three sections:

```java
// "Items / SPAWN" — add after BUNNY2_SPAWN
public static final String {KEY}_SPAWN = "{key}_spawn";

// "Entities" — add after VARIANT_BUNNY2
public static final String VARIANT_{KEY} = "{key}";

// "Robot Variant Arrays" — add ONLY to the mods it belongs to
public static final String[] LEGACY_VARIANTS = { ..., VARIANT_{KEY} };
public static final String[] REBOOT_VARIANTS = { ..., VARIANT_{KEY} };
// Never touch TRIBUTE_VARIANTS
```

Adding to `LEGACY_VARIANTS` / `REBOOT_VARIANTS` is what causes the loader-side config files to auto-generate the in-game config section for the new family.

---

### 1.2 `RobotVariant.java`

**File:** `.../lovelylib/common/entity/enums/RobotVariant.java`

```java
/** Brief description of role and characteristics. */
{Key}(N, LovelyConstant.VARIANT_{KEY}),
```

The `id` (N) **must** be the next available integer after the last existing entry. The `CODEC` array is auto-sorted by ID — no other change needed here.

---

### 1.3 `RobotFamily.java` — restricted palette only

**File:** `.../lovelylib/common/entity/RobotFamily.java`

Skip if using all 16 colors — `withColorPalette(RobotVariant)` handles that.

For a subset of colors, the overloaded method already exists:

```java
public RobotFamily withColorPalette(RobotVariant variant, List<EntityTexture> colors)
```

This restricts both the random spawn pool and the dye-reaction wiring to the listed colors. The default texture falls back to the **first color in the list**, not WHITE.

---

### 1.4 `RobotFamilyRegistry.java` — restricted palette only

**File:** `.../lovelylib/common/entity/RobotFamilyRegistry.java`

The overloaded `create()` already exists:

```java
protected static RobotFamily create(RobotVariant variant, List<EntityTexture> colors)
```

Use this instead of `create(RobotVariant)` when the palette is limited.

---

### 1.5 `LegacyRobotFamilies.java`

**File:** `.../lovelylib/source/legacy/LegacyRobotFamilies.java`

**a) Static field** — full palette:
```java
public static final RobotFamily {KEY} = create(RobotVariant.{Key});
```

**a) Static field** — restricted palette (like Bunny3):
```java
public static final java.util.List<EntityTexture> {KEY}_COLORS = java.util.List.of(
    EntityTexture.LIGHT_BLUE, EntityTexture.YELLOW /*, ... */
);
public static final RobotFamily {KEY} = create(RobotVariant.{Key}, {KEY}_COLORS);
```

**b) `reloadFromConfig()` block** — copy from any existing entry:
```java
// Configure {KEY}
SharedConfigs.EntityConfigData {key} = LegacyConfigs.getEntityConfig(LovelyConstant.VARIANT_{KEY});
{KEY}.withCombatStats({key}.baseHp, {key}.baseAttack, {key}.attackSpeed,
        {key}.baseDefense, {key}.baseToughness, 0F, {key}.movementSpeed)
    .withFeature(LevelFeature.class, new LevelFeature({key}.maxLevel, defaultExpStrategy))
    .withFeature(CombatLevelFeature.class, new CombatLevelFeature(
            {key}.baseHp, {key}.baseAttack, {key}.baseDefense, new LinearAttributeStrategy()))
    .withFeature(EnchantmentFeature.class, defaultEnchantment)
    .withFeature(ProtectionFeature.class, defaultProtection);
```

---

### 1.6 `RebootRobotFamilies.java`

**File:** `.../lovelylib/source/reboot/RebootRobotFamilies.java`

Identical to 1.5, referencing `RebootConfigs` instead of `LegacyConfigs`.

---

### 1.7 `LegacyConfigs.java` (lovelylib) — stat defaults

**File:** `.../lovelylib/source/legacy/LegacyConfigs.java`

Add to the `static {}` block. Constructor: `(maxLevel, baseHp, baseAttack, attackSpeed, baseDefense, baseToughness, knockbackResistance, movementSpeed)`

```java
// {KEY} — brief description
Default.put(LovelyConstant.VARIANT_{KEY}, new SharedConfigs.EntityConfigData(
    200, XX, XX, X.XF, X, X.XF, 0F, 0.XXF
));
```

---

### 1.8 `RebootConfigs.java` (lovelylib) — stat defaults

**File:** `.../lovelylib/source/reboot/RebootConfigs.java`

Same pattern as 1.7. Stats can differ from Legacy if balance philosophy differs.

---

## Part 1 — lovelylib: Layer 2 — Config & Display Pipeline

### 1.9 `SharedConfigs.java` — stat fields  ⚠️ Required for ConfigAccessLayer

**File:** `.../lovelylib/common/configs/SharedConfigs.java`

Add static fields for the new variant's default stats inside the `Common` inner class. Follow the exact naming pattern — `ConfigAccessLayer.getDefaultEntityConfig()` reads from these fields directly.

```java
// {KEY} defaults
public static int   {Key}MaxLevel     = XX;
public static int   {Key}BaseHp       = XX;
public static int   {Key}BaseAttack   = XX;
public static float {Key}AttackSpeed  = X.XF;
public static int   {Key}BaseDefense  = X;
public static float {Key}BaseToughness = X.XF;
public static float {Key}MovementSpeed = 0.XXF;
```

These must match the values in `LegacyConfigs` / `RebootConfigs` (steps 1.7–1.8). They are the in-memory live defaults that `ConfigAccessLayer` reads when no loader-specific cache entry exists yet.

---

### 1.10 `ConfigAccessLayer.java` — fallback switch  ⚠️ Critical — easy to miss

**File:** `.../lovelylib/api/configs/ConfigAccessLayer.java`

Add a `case` to the `getDefaultEntityConfig(String variant)` switch. This is the **last-resort fallback** in the three-step config resolution chain:

1. Serialized config string (Phase 2 — rarely populated)
2. `intConfigCache` / `floatConfigCache` — populated by Forge/NeoForge on load event
3. **This switch** — used by Fabric (which has no load event hook), and as safety net before any loader has loaded config

```java
case LovelyConstant.VARIANT_{KEY} -> new SharedConfigs.EntityConfigData(
    SharedConfigs.Common.{Key}MaxLevel,
    SharedConfigs.Common.{Key}BaseHp,
    SharedConfigs.Common.{Key}BaseAttack,
    SharedConfigs.Common.{Key}AttackSpeed,
    SharedConfigs.Common.{Key}BaseDefense,
    SharedConfigs.Common.{Key}BaseToughness,
    SharedConfigs.Common.{Key}MovementSpeed
);
```

**Without this case:** on Fabric, the entity spawns with zeroed or generic default stats (the `default` arm returns `EntityConfigData.getDefault()`). The bug is silent — no crash, just wrong numbers in-game.

---

### 1.11 `LovelyIdentifier.java` — display name switch  ⚠️ Critical — easy to miss

**File:** `.../lovelylib/common/shared/LovelyIdentifier.java`

Add a `case` to the `getTranslation(RobotVariant variant)` switch:

```java
case {Key} -> getVariantTranslation(LovelyConstant.VARIANT_{KEY});
```

`getVariantTranslation()` resolves to the `variant.lovelylib.{key}` key in the **lovelylib lang file** (step 1.14 below). This switch feeds `displayExtra()` in `RobotEntity`, which builds the name shown in overhead wary/heal messages.

**Without this case:** the `default` arm fires, which returns `getVariantTranslation(VARIANT_VANILLA)` — the robot displays "Vanilla" in all overhead messages regardless of its actual type.

---

### 1.12 `lovelylib` lang — variant display name  ⚠️ Belongs in lovelylib, not the mod

**File:** `sources/common/lovelylib-1.21.1/Common/src/main/resources/assets/lovelylib/lang/en_us.json`

Add the variant translation key. This is the **only** variant key — it lives here, not in `llovelyr` or `rlovelyr`.

```json
"variant.lovelylib.{key}": "{Display Name}"
```

Example for Bunny3: `"variant.lovelylib.bunny3": "Bunny 3.0"`

---

### 1.13 Item model JSONs in lovelylib

**Location:** `sources/common/lovelylib-1.21.1/Common/src/main/resources/assets/lovelylib/models/item/{key}/`

One JSON per active color ID plus the random icon. Each file:

```json
{
  "parent": "item/generated",
  "textures": {
    "layer0": "lovelylib:item/{key}/{key}_{id:02d}"
  }
}
```

- **Full palette (16 colors):** 17 files named `{key}00.json` → `{key}16.json` (no underscore before ID)
- **Restricted palette:** files for active IDs only + `{key}_16.json` (underscore before ID — match the texture filename convention)

Bunny3 example (5 colors + random = 6 files): `bunny3_03.json`, `bunny3_04.json`, `bunny3_05.json`, `bunny3_06.json`, `bunny3_10.json`, `bunny3_16.json`

---


## Part 2 — Mod Loader Changes (Layer 3 + 4)

Repeat for **every loader platform** (Forge, Fabric, NeoForge) the family ships on.  
Shared resources (lang, recipes) live under `{mod}-1.21.1/Common/`.  
Platform-specific files live under `{mod}-1.21.1/{Forge|Fabric|NeoForge}/`.

Substitute: `{Mod}` = `Legacy` or `Reboot` | `{modid}` = `llovelyr` or `rlovelyr`

---

### 2.1 `{Mod}Items.java` — all 3 loader platforms

**Forge / NeoForge** — spawn item field (add after BUNNY2_SPAWN):
```java
// Forge uses RegistryObject, NeoForge uses DeferredItem — same call pattern
public static final RegistryObject<Item> {KEY}_SPAWN = registerItem(
    LovelyConstant.{KEY}_SPAWN, {Mod}Entities.{KEY}::get, Rarity.RARE, 1);
```

**Fabric** — uses direct entity type reference (no `::get`):
```java
public static final Item {KEY}_SPAWN = registerItem(
    LovelyConstant.{KEY}_SPAWN, {Mod}Entities.{KEY}, Rarity.RARE, 1);
```

**Forge / NeoForge** — `registerModel()` (inside `enqueueWork` lambda):
```java
registerModel({Mod}Items.{KEY}_SPAWN.get(), LovelyConstant.ITEM_TAG_VARIANT, LovelyConstant.STAT_COLOR);
```

**Fabric** — `registerModel()` (direct item, no `.get()`):
```java
registerModel({Mod}Items.{KEY}_SPAWN, LovelyConstant.ITEM_TAG_VARIANT, LovelyConstant.STAT_COLOR);
```

---

### 2.2 `{Mod}Entities.java` — all 3 loader platforms

**Forge** — entity type field (add after BUNNY2):
```java
public static final RegistryObject<EntityType<NativeRobotEntity>> {KEY} =
    registerRobot(LovelyConstant.VARIANT_{KEY}, {Mod}RobotFamilies.{KEY});
```

**NeoForge** — uses `DeferredHolder`:
```java
public static final DeferredHolder<EntityType<?>, EntityType<NativeRobotEntity>> {KEY} =
    registerRobot(LovelyConstant.VARIANT_{KEY}, {Mod}RobotFamilies.{KEY});
```

**Fabric** — direct registry, no wrapper:
```java
public static final EntityType<NativeRobotEntity> {KEY} =
    registerRobot(LovelyConstant.VARIANT_{KEY}, {Mod}RobotFamilies.{KEY});
```

**Forge / NeoForge** — `registerAttribute()`:
```java
event.put({KEY}.get(), NativeEntityFamily.createAttributes({Mod}RobotFamilies.{KEY}));
```

**Fabric** — `register()`:
```java
FabricDefaultAttributeRegistry.register({KEY}, NativeEntityFamily.createAttributes({Mod}RobotFamilies.{KEY}));
```

**Forge / NeoForge** — `registerRender()`:
```java
event.registerEntityRenderer({KEY}.get(), NativeRobotRenderer::new);
// Use a custom renderer only if the geo model has features NativeRobotRenderer can't handle
```

**Fabric** — `registerRender()`:
```java
EntityRendererRegistry.register({KEY}, NativeRobotRenderer::new);
```

**Forge / NeoForge** — `registerNativeRobotFeature()` (add after BUNNY2 block):
```java
// {KEY}
{Mod}RobotFamilies.{KEY}
    .withFeature(PickupFeature.class, new PickupFeature({Mod}Items.{KEY}_SPAWN.get()))
    .withFeature(DropFeature.class, new DropFeature({Mod}Items.ROBOT_CORE.get()));
```

**Fabric** — same method, but items are direct (no `.get()`):
```java
// {KEY}
{Mod}RobotFamilies.{KEY}
    .withFeature(PickupFeature.class, new PickupFeature({Mod}Items.{KEY}_SPAWN))
    .withFeature(DropFeature.class, new DropFeature({Mod}Items.ROBOT_CORE));
```

---

### 2.3 `{Mod}Groups.java` — all 3 loader platforms

**Forge / NeoForge** — `DEFAULT_TAB` `displayItems` lambda (add after BUNNY2):
```java
output.accept({Mod}Items.{KEY}_SPAWN.get());
```

**Forge / NeoForge** — `addSpawnEggs()`:
```java
event.accept({Mod}Items.{KEY}_SPAWN);
```

**Fabric** — `allItemsEntry()` (add after BUNNY2):
```java
entries.accept({Mod}Items.{KEY}_SPAWN);
```

**Fabric** — `spawnEggItemsEntry()` (add after BUNNY2):
```java
entries.accept({Mod}Items.{KEY}_SPAWN);
```

---

### 2.4 Mod lang — item and entity keys (Common resources)

**File:** `{modid}-1.21.1/Common/src/main/resources/assets/{modid}/lang/en_us.json`

```json
"item.{modid}.{key}_spawn": "Spawn {Display Name}",
"entity.{modid}.{key}": "{Display Name}"
```

Note: **no `variant.*` key here** — that lives in lovelylib (step 1.12). Adding it to the mod lang is redundant and was removed during Bunny3 implementation.

---

### 2.5 Recipes — Common resources

**Fabricator recipe:** `data/{modid}/recipe/{key}_spawn.json`

```json
{
  "type": "{modid}:lovely_spawn",
  "category": "misc",
  "pattern": ["NIN", "ICI", "NIN"],
  "key": {
    "N": { "item": "minecraft:gold_ingot" },
    "I": { "item": "minecraft:iron_ingot" },
    "C": { "item": "{modid}:robot_core" }
  },
  "result": { "id": "{modid}:{key}_spawn", "count": 1 }
}
```

The ingredient tier (nugget vs ingot vs block) and pattern are balance decisions — use the Fabricator recipe screenshot as source of truth. Bunny3 uses gold ingots (one tier above Bunny2's nuggets).

**Dye recolour recipe:** `data/{modid}/recipe/{key}_spawn_dye.json`

```json
{
  "type": "{modid}:lovely_spawn_dye",
  "category": "misc",
  "ingredients": [
    { "item": "{modid}:{key}_spawn" },
    { "tag": "minecraft:dyes" }
  ],
  "result": { "id": "{modid}:{key}_spawn", "count": 1 }
}
```

Always use the full `minecraft:dyes` tag. Dyes outside the registered palette produce no visual change — filtering is handled at runtime by `ConditionalAppearanceFeature`.

---

### 2.6 Item model JSON — per loader platform

**File:** `{modid}-1.21.1/{Loader}/src/main/resources/assets/{modid}/models/item/{key}_spawn.json`

Each of the three loader directories (Forge, Fabric, NeoForge) needs its own copy. The JSON is identical across all three.

**Full 16-color palette** (default `layer0` = white = `00`):
```json
{
  "parent": "item/generated",
  "textures": { "layer0": "lovelylib:item/{key}/{key}00" },
  "overrides": [
    { "predicate": {"variant": 0},  "model": "lovelylib:item/{key}/{key}00" },
    { "predicate": {"variant": 1},  "model": "lovelylib:item/{key}/{key}01" },
    { "predicate": {"variant": 15}, "model": "lovelylib:item/{key}/{key}15" },
    { "predicate": {"variant": 16}, "model": "lovelylib:item/{key}/{key}16" }
  ]
}
```

**Restricted palette** (default `layer0` = random icon = `_16`):
```json
{
  "parent": "item/generated",
  "textures": { "layer0": "lovelylib:item/{key}/{key}_16" },
  "overrides": [
    { "predicate": {"variant": 3},  "model": "lovelylib:item/{key}/{key}_03" },
    { "predicate": {"variant": 4},  "model": "lovelylib:item/{key}/{key}_04" },
    { "predicate": {"variant": 5},  "model": "lovelylib:item/{key}/{key}_05" },
    { "predicate": {"variant": 6},  "model": "lovelylib:item/{key}/{key}_06" },
    { "predicate": {"variant": 10}, "model": "lovelylib:item/{key}/{key}_10" }
  ]
}
```

The predicate `variant` values are the `EntityTexture` enum IDs for each active color. Full-palette files use no underscore in the model name (`bunny00`); restricted-palette files use an underscore (`bunny3_03`) — match what the texture filenames use.

> **Fabric note:** The predicate registration code uses `Float` internally — JSON values remain integers (`3`, not `3.0`), but the model predicate property must be registered as a float predicate in the Fabric client code.

---


## Part 3 — Complete Checklist

Tick every box before committing. Every unchecked item has a known in-game failure mode.

### lovelylib — Layer 1: Core Registration

- [ ] `LovelyConstant` — `{KEY}_SPAWN` constant, `VARIANT_{KEY}` constant, added to the correct variant arrays only
- [ ] `RobotVariant` — new enum entry with the **next available integer ID** (check existing entries)
- [ ] `LegacyRobotFamilies` — `{KEY}_COLORS` list (if restricted) + `{KEY}` field + `reloadFromConfig()` block (if Legacy)
- [ ] `RebootRobotFamilies` — same pattern referencing `RebootConfigs` (if Reboot)
- [ ] `RobotFamily.withColorPalette(variant, List)` overload — already exists; no action unless adding new palette logic
- [ ] `RobotFamilyRegistry.create(variant, List)` overload — already exists; no action unless adding new palette logic

### lovelylib — Layer 2: Config & Display Pipeline

- [ ] `SharedConfigs` — 7 static fields: `{Key}MaxLevel`, `{Key}BaseHp`, `{Key}BaseAttack`, `{Key}AttackSpeed`, `{Key}BaseDefense`, `{Key}BaseToughness`, `{Key}MovementSpeed` ⚠️
- [ ] `ConfigAccessLayer.getDefaultEntityConfig()` — new `case LovelyConstant.VARIANT_{KEY}` in the switch ⚠️
- [ ] `LovelyIdentifier.getTranslation(RobotVariant)` — new `case {Key}` in the switch ⚠️
- [ ] `LegacyConfigs` (lovelylib) — `Default.put(VARIANT_{KEY}, ...)` in `static {}` (if Legacy)
- [ ] `RebootConfigs` (lovelylib) — `Default.put(VARIANT_{KEY}, ...)` in `static {}` (if Reboot)

### lovelylib — Resources

- [ ] `lovelylib/lang/en_us.json` — `"variant.lovelylib.{key}"` key
- [ ] `models/item/{key}/` — one JSON per active color ID + `_16.json`
- [ ] `textures/entity/{key}/` — PNGs for all active color IDs
- [ ] `textures/item/{key}/` — PNGs for all active color IDs + `_16.png`
- [ ] `geo/` — `{key}.default.geo.json` and `{key}.armed.geo.json`

### Per mod (llovelyr / rlovelyr) × Per loader (Forge / Fabric / NeoForge) — 9 files

- [ ] `{Mod}Items` — spawn item field + `registerModel()` entry (mind Fabric vs Forge/NeoForge API difference)
- [ ] `{Mod}Entities` — entity type field + attribute registration + renderer registration + feature setup
- [ ] `{Mod}Groups` — default tab `displayItems` entry + spawn eggs tab entry

### Per mod — Common resources (2 files each, loader-independent)

- [ ] `lang/en_us.json` — 2 keys: `"item.{modid}.{key}_spawn"` and `"entity.{modid}.{key}"` (no `variant.*` here)
- [ ] `data/{modid}/recipe/{key}_spawn.json`
- [ ] `data/{modid}/recipe/{key}_spawn_dye.json`

### Per mod × Per loader — Item model JSON (3 files per mod = 6 total for Legacy+Reboot)

- [ ] `{modid}-1.21.1/Forge/src/main/resources/assets/{modid}/models/item/{key}_spawn.json`
- [ ] `{modid}-1.21.1/Fabric/src/main/resources/assets/{modid}/models/item/{key}_spawn.json`
- [ ] `{modid}-1.21.1/NeoForge/src/main/resources/assets/{modid}/models/item/{key}_spawn.json`

---

## Part 4 — Pitfalls from Bunny3

These are confirmed in-game bugs encountered during implementation. Each maps directly to a ⚠️ step above.

---

### Bug 1 — Wary/heal messages show "Vanilla"

**Symptom:** Robot displays "Vanilla" in overhead wary/heal messages when it has no custom name set. All other robots display correctly.

**Root cause:** `LovelyIdentifier.getTranslation(RobotVariant variant)` — the switch had no `case {Key}:`. The `default` arm returns `getVariantTranslation(VARIANT_VANILLA)`.

**Fix:** Step 1.11 — add `case {Key} -> getVariantTranslation(LovelyConstant.VARIANT_{KEY});`

**Why easy to miss:** The enum, the family registration, and the entity type are all correct. The bug is isolated to one switch statement in the display utility class, which has no compile-time enforcement that every enum variant is covered.

---

### Bug 2 — Wrong or zero stats on Fabric

**Symptom:** Robot spawns on Fabric with 0 HP, 0 attack, or stats that don't match the config file. Forge/NeoForge work correctly with the same config values.

**Root cause:** `ConfigAccessLayer.getDefaultEntityConfig()` switch had no `case VARIANT_{KEY}:`. The `default` arm returns `EntityConfigData.getDefault()` which has zeroed stubs. Fabric's config system doesn't fire the Forge `ModConfigEvent`, so `intConfigCache` / `floatConfigCache` are never populated before the entity's first tick — making the fallback switch the only source of truth.

**Fix:** Step 1.10 — add the variant's case to the switch, referencing `SharedConfigs.Common.{Key}*` fields.

**Also requires:** Step 1.9 — the `SharedConfigs.Common.{Key}*` fields must exist, otherwise the switch case references undefined symbols.

**Why easy to miss:** Forge works fine because `ConfigAccessLayer.clearCaches()` + `ConfigAccessLayer.updateIntConfigCache()` in the Forge `onLoad()` event populates all values before any entity spawns. The Fabric code path never hits that event, so it falls directly to the `default` arm.

---

### Bug 3 — `variant.*` key in mod lang causes untranslated text

**Symptom:** The overhead message shows a raw translation key like `variant.llovelyr.bunny3` instead of "Bunny 3.0". Or the key is silently ignored.

**Root cause:** `LovelyIdentifier.getVariantTranslation()` generates a `variant.lovelylib.*` key — it always uses the `lovelylib` namespace. A `variant.llovelyr.*` or `variant.rlovelyr.*` key in the mod lang file is never looked up.

**Fix:** Step 1.12 — the `variant.lovelylib.{key}` key must live in **lovelylib** Common lang. Do not add it to the mod lang files.

**Why easy to miss:** The initial implementation added all three keys (`item.*`, `variant.*`, `entity.*`) to the mod lang, mirroring the pattern from older versions of the project that predated the lovelylib lang split.

---

### Bug 4 — Item model missing or shows white square in one loader

**Symptom:** Spawn item has no texture (white/missing) in one specific loader (e.g. Fabric) but looks correct in Forge.

**Root cause:** The item model JSON lives under the loader-specific resources directory, not under Common. If only one or two of the three loader directories received the file, the other platforms don't have the predicate overrides.

**Fix:** Step 2.6 — all three loader directories need their own copy. The JSON content is identical across all three.

---

## Notes

**Renderer selection:** Most families use `NativeRobotRenderer`. Use a dedicated renderer only when the geo model has structural features the standard renderer cannot handle (e.g. Bunny's ear bone, Kitsune's tail unlock). Bunny3 uses `NativeRobotRenderer`.

**Dynamic config generation (Forge/NeoForge):** The loader-side config files iterate over `LovelyConstant.REBOOT_VARIANTS` / `LEGACY_VARIANTS` to auto-generate the in-game config file sections and populate `ConfigAccessLayer` caches. Adding the variant key to those arrays (step 1.1) is the only change needed in the loader config files — no manual config spec block.

**Fabric config:** Uses `SimpleConfig` + `ConfigProvider` (`.properties` file), not Forge's `ModConfigSpec`. Config values are loaded at startup via `register()`. The `ConfigAccessLayer` fallback (step 1.10) is critical on this loader.

**Tribute exclusion:** Never add to `TributeRobotFamilies`, `TributeConfigs`, or `TRIBUTE_VARIANTS`. Tribute is a faithful recreation of the original mod with a fixed roster.

**Multiple Minecraft versions:** Repeat Part 2 for each version's mod folder. The lovelylib changes (Part 1) apply once per lovelylib version and are shared across all MC versions that use that lib version.

**Naming convention — item model files:**
- Full palette: no underscore before ID — `bunny00.json`, `bunny01.json`
- Restricted palette: underscore before ID — `bunny3_03.json`, `bunny3_04.json`

Match the texture filename convention exactly, as the model JSON references the texture by path.
