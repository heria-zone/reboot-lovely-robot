# ADR-024: Reboot-Exclusive Robot Families — Prime, Hyperion, Empyrium

**Status:** Accepted
**Date:** 2026-07-02
**Author:** Serge Maia
**Prerequisite:** ADR-023 (RobotDefinitionRegistry) — must be complete and sealed before this ADR executes
**Applies to:** `lovelylib`, `rlovelyr`
**Not for:** `llovelyr`, `tlovelyr`
**Reference source:** `archive/1.20.X/rlovelyr-1.20.4/Forge/`

---

## 1. Context

The 1.20.4 `rlovelyr` codebase (`LovelyRobotEntities.java`, `NativeEntityType.java`,
`LovelyRobotItems.java`) registered three Reboot-exclusive robots — **Prime**, **Hyperion**, and
**Empyrium** — in a separate "Aldarian Tech" creative tab. These three entities have never been
ported to the 1.21.1 codebase.

The 1.21.1 `rlovelyr` is a near-empty stub (`Reboot.java`, `RebootIdentifier.java`). All loader
infrastructure — `RebootEntities`, `RebootItems`, `RebootGroups` — is **fully registry-driven** as
specified in ADR-023. Adding any new Reboot-exclusive robot requires exactly **two code changes** in
`lovelylib` and **zero changes** in any loader file.

### 1.1 What makes these three robots distinct from the shared roster

| Robot | Palette | Blaze Rod behaviour | Item interaction |
|-------|---------|---------------------|-----------------|
| **Prime** | Restricted — 7 named textures | Cycles forward through palette; wraps to first | Blocked for Blaze Rod via `canInteractWithItems`; handled in `handleTexture` |
| **Hyperion** | Restricted — 2 named textures | Toggles between COMMANDER and VALKYRIE; wraps to COMMANDER | Same as Prime |
| **Empyrium** | Single texture — COLD_GOLD only | No cycling | All item interaction returns `PASS` |

All three used `NativeEntityType` and `RobotEntity` in 1.20.4. In 1.21.1 they use `NativeRobotEntity`
and `RobotFamily` via the `RobotDefinitionRegistry`.

### 1.2 Special texture names (1.20.4 EntityTexture IDs 17–25)

The 1.20.4 `EntityTexture` enum defined extended IDs above the standard 16-colour palette:

| ID | Name | Used by |
|----|------|---------|
| 17 | `DARK_MATTER` | Prime (maps to `prime_02.png`) |
| 18 | `SUPERNOVA`   | Prime (maps to `prime_01.png`) |
| 19 | `COLD_GOLD`   | Prime + Empyrium (maps to `prime_00.png` / `empyrium_00.png`) |
| 20 | `EMBRYON`     | Prime (maps to `prime_05.png`) |
| 21 | `DARK_GOLD`   | Prime (maps to `prime_04.png`) |
| 22 | `GOLD_MATTER` | Prime (maps to `prime_03.png`) |
| 23 | `HESTIA`      | Prime (maps to `prime_06.png`) |
| 24 | `COMMANDER`   | Hyperion (maps to `hyperion_00.png`) |
| 25 | `VALKYRIE`    | Hyperion (maps to `hyperion_01.png`) |

The 1.21.1 `EntityTexture` enum **stops at `RANDOM` (ID 16)**. These extended IDs do not exist yet.

---

## 2. Decision

Port Prime, Hyperion, and Empyrium to `rlovelyr-1.21.1` using the ADR-023 pipeline. The work
splits into three phases, ordered by dependency.

**Phase A — lovelylib: extend `EntityTexture` enum**
Add the 9 extended texture constants needed by all three robots. No other lovelylib changes.

**Phase B — lovelylib: declare definitions in `RebootRobotDefinitions`**
Register `Prime`, `Hyperion`, and `Empyrium` as `RobotVariant` static constants and declare their
`RobotEntityDefinition` builder entries. The `featureConfigurator` callback handles each robot's
distinct palette-cycling or interaction-blocking behaviour.

**Phase C — rlovelyr: assets only**
No Java changes in any loader. All three loaders pick up the new variants automatically through
`RobotDefinitionRegistry.getForMod(ModTarget.REBOOT)`. The only deliverable in `rlovelyr` is
resource files: textures, item models, lang keys, and recipes.

---

## 3. Phase A — `EntityTexture` enum extension (`lovelylib`)

**File:** `lovelylib-1.21.1/Common/src/main/java/net/heriazone/lovelylib/common/entity/enums/EntityTexture.java`

Add 9 entries after `RANDOM(16, ...)`. The integer IDs **must not shift** any existing entry —
append only.

```java
// -- Extended Palette (Reboot-exclusive named textures) --
// These IDs are used only by restricted-palette robots (Prime, Hyperion, Empyrium).
// They map to files named {variant}_{id:02d}.png under the variant's texture folder.

DARK_MATTER(17, LovelyConstant.TEX_DARK_MATTER),    // Prime #02 — deep void purple
SUPERNOVA(18,   LovelyConstant.TEX_SUPERNOVA),       // Prime #01 — bright burst white-gold
COLD_GOLD(19,   LovelyConstant.TEX_COLD_GOLD),       // Prime #00 / Empyrium #00 — ice blue-gold
EMBRYON(20,     LovelyConstant.TEX_EMBRYON),         // Prime #05 — embryonic green
DARK_GOLD(21,   LovelyConstant.TEX_DARK_GOLD),       // Prime #04 — dark brass
GOLD_MATTER(22, LovelyConstant.TEX_GOLD_MATTER),     // Prime #03 — molten gold
HESTIA(23,      LovelyConstant.TEX_HESTIA),          // Prime #06 — hearth red
COMMANDER(24,   LovelyConstant.TEX_COMMANDER),       // Hyperion #00 — command blue
VALKYRIE(25,    LovelyConstant.TEX_VALKYRIE);        // Hyperion #01 — valkyrie silver
```

Add the corresponding string constants to `LovelyConstant.java` (they don't exist yet):

```java
// -- Extended Texture Keys (Reboot palette) --
public static final String TEX_DARK_MATTER  = "dark_matter";
public static final String TEX_SUPERNOVA    = "supernova";
public static final String TEX_COLD_GOLD    = "cold_gold";
public static final String TEX_EMBRYON      = "embryon";
public static final String TEX_DARK_GOLD    = "dark_gold";
public static final String TEX_GOLD_MATTER  = "gold_matter";
public static final String TEX_HESTIA       = "hestia";
public static final String TEX_COMMANDER    = "commander";
public static final String TEX_VALKYRIE     = "valkyrie";
```

**Migration note:** `EntityTexture.byId(int)` uses a CODEC array sorted by ID. Appending entries
above ID 16 does not affect any existing byId() call for IDs 0–16.

---

## 4. Phase B — `RobotVariant` + `RebootRobotDefinitions` (`lovelylib`)

### 4.1 Add `RobotVariant` static constants

**File:** `lovelylib-1.21.1/Common/.../common/entity/definition/RobotVariant.java`

Add three constants after the existing built-ins. IDs must be the next available integers.
Current highest built-in ID is `7` (Neko). `Bunny3` is `8` (declared in SharedRobotDefinitions).
Use IDs `9`, `10`, `11`:

```java
// -- Reboot-Exclusive Variants --
public static final RobotVariant Prime    = register(9,  "prime");
public static final RobotVariant Hyperion = register(10, "hyperion");
public static final RobotVariant Empyrium = register(11, "empyrium");
```

> **ID assignment rule:** IDs 0–99 are reserved for lovelylib built-ins (per ADR-023 §3.0).
> Check `RobotVariant.java` for the current highest ID before assigning — do not assume 9/10/11
> are still free if other variants have been added since this ADR was written.

### 4.2 `RebootRobotDefinitions.register()` — Prime

**File:** `lovelylib-1.21.1/Common/.../source/reboot/RebootRobotDefinitions.java`

```java
// ── Prime — restricted 7-colour Aldarian palette, Blaze Rod cycles forward ──
RobotDefinitionRegistry.register(new RobotEntityDefinition.Builder(RobotVariant.Prime)
    .displayName("Prime")
    .forMod(ModTarget.REBOOT)
    .palette(
        EntityTexture.DARK_MATTER,
        EntityTexture.SUPERNOVA,
        EntityTexture.COLD_GOLD,
        EntityTexture.EMBRYON,
        EntityTexture.DARK_GOLD,
        EntityTexture.GOLD_MATTER,
        EntityTexture.HESTIA)
    .stats(200, 30, 8, 1.5f, 6, 1.0f, 0.33f)
    .featureConfigurator((family, cfg) ->
        family.withFeature(BlazeCycleFeature.class,
            new BlazeCycleFeature(
                EntityTexture.DARK_MATTER,   // first — wrap target
                EntityTexture.HESTIA)))      // last  — wraps back to first
    .build());
```

**Stat rationale:** Prime is a high-tier Aldarian robot. Stats sit above the shared roster
(Dragon is the current tank at 30 HP / 8 atk). Adjust in `RebootConfigs` as needed.

### 4.3 `RebootRobotDefinitions.register()` — Hyperion

```java
// ── Hyperion — 2-colour toggle, Blaze Rod switches between COMMANDER and VALKYRIE ──
RobotDefinitionRegistry.register(new RobotEntityDefinition.Builder(RobotVariant.Hyperion)
    .displayName("Hyperion")
    .forMod(ModTarget.REBOOT)
    .palette(
        EntityTexture.COMMANDER,
        EntityTexture.VALKYRIE)
    .stats(200, 32, 9, 1.4f, 7, 1.0f, 0.34f)
    .featureConfigurator((family, cfg) ->
        family.withFeature(BlazeCycleFeature.class,
            new BlazeCycleFeature(
                EntityTexture.COMMANDER,     // first — wrap target
                EntityTexture.VALKYRIE)))    // last  — wraps back to first
    .build());
```

### 4.4 `RebootRobotDefinitions.register()` — Empyrium

```java
// ── Empyrium — single texture, no item interaction of any kind ──
RobotDefinitionRegistry.register(new RobotEntityDefinition.Builder(RobotVariant.Empyrium)
    .displayName("Empyrium")
    .forMod(ModTarget.REBOOT)
    .palette(EntityTexture.COLD_GOLD)        // single texture — no cycling, no dye
    .stats(200, 35, 12, 1.2f, 8, 2.0f, 0.30f)
    .featureConfigurator((family, cfg) ->
        family.withFeature(BlockAllItemInteractionFeature.class,
            new BlockAllItemInteractionFeature()))
    .build());
```

---

## 5. New Features Required in lovelylib

The `featureConfigurator` callbacks above reference two new `RobotFeature` types that do not
yet exist. Both belong in `lovelylib/api/entity/features/` — they encode behaviour that was
previously hardcoded in the 1.20.4 per-entity class overrides.

### 5.1 `BlazeCycleFeature`

**Purpose:** When the owner right-clicks the robot with a Blaze Rod, advance the texture to the
next entry in the robot's registered palette. When the end of the palette is reached, wrap back
to the first texture. Consumes one Blaze Rod (unless the player is in creative mode).

**Maps directly from:** The `handleTexture()` override in `PrimeEntity` and `HyperionEntity`
(1.20.4), both of which also blocked the Blaze Rod in `canInteractWithItems()` to prevent the
default handler from firing first.

```java
package net.heriazone.lovelylib.api.entity.features;

/**
 * Enables Blaze Rod palette cycling for a robot entity.
 * <p>
 * <b>Behaviour:</b> Right-clicking the robot with a Blaze Rod advances the active
 * texture to the next entry in the robot's registered palette. When the last texture
 * is reached, cycling wraps back to firstTexture. Consumes one Blaze Rod per use
 * unless the player is in creative mode.
 * <p>
 * <b>Interaction guard:</b> The feature also suppresses the default
 * canInteractWithItems() Blaze Rod check so the cycle logic is the only handler.
 * <p>
 * <b>Integration:</b> Apply via featureConfigurator in RobotEntityDefinition.Builder.
 * The RobotFamily.handleItemInteraction() dispatch loop must check for this feature
 * before falling through to the default texture-change handler.
 */
public class BlazeCycleFeature extends AbstractRobotFeature {

    private final EntityTexture firstTexture;
    private final EntityTexture lastTexture;

    public BlazeCycleFeature(EntityTexture firstTexture, EntityTexture lastTexture) {
        this.firstTexture = firstTexture;
        this.lastTexture  = lastTexture;
    }

    public EntityTexture getFirstTexture() { return firstTexture; }
    public EntityTexture getLastTexture()  { return lastTexture; }

} // Class: BlazeCycleFeature
```

### 5.2 `BlockAllItemInteractionFeature`

**Purpose:** Marks a robot as non-interactable with any item. `RobotEntity.handleItemInteraction`
must return `InteractionResult.PASS` immediately when this feature is present, bypassing all
standard item handling (texture cycling, dye, food, etc.).

**Maps directly from:** The `handleItemInteraction()` override in `EmpyriumEntity` (1.20.4),
which unconditionally returned `InteractionResult.PASS`.

```java
package net.heriazone.lovelylib.api.entity.features;

/**
 * Marks a robot as non-interactable with any held item.
 * <p>
 * <b>Behaviour:</b> When this feature is present on a RobotFamily, the robot's
 * handleItemInteraction() returns PASS immediately for all item types. The robot
 * cannot be dyed, texture-cycled, or fed by the player. It can still be tamed
 * by other means (spawn, command) if TamingFeature is also configured.
 * <p>
 * <b>Integration:</b> RobotEntity.handleItemInteraction() must check for this
 * feature as the first guard in its dispatch loop.
 */
public class BlockAllItemInteractionFeature extends AbstractRobotFeature {
    // Marker feature — no fields required.
} // Class: BlockAllItemInteractionFeature
```

### 5.3 `RobotEntity` dispatch changes required

Two guard points must be added to `RobotEntity.handleItemInteraction()` in lovelylib:

1. **Check `BlockAllItemInteractionFeature` first** — if present, return `InteractionResult.PASS`
   immediately before any other item handling.
2. **Check `BlazeCycleFeature` on Blaze Rod use** — if present and the held item is a Blaze Rod,
   run the palette-advance logic rather than passing through to the default handler.

Both checks read from `family.getFeature(FeatureClass.class)` — the same pattern used by
`EmanationFeature`, `ExchangeFeature`, etc.

---

## 6. Phase C — rlovelyr Assets (no Java changes)

### 6.1 Textures

All texture files live under `lovelylib-1.21.1/Common/src/main/resources/assets/lovelylib/`.

**Prime — 7 entity textures + 8 item textures:**
```
textures/entity/prime/prime_00.png   (COLD_GOLD)
textures/entity/prime/prime_01.png   (SUPERNOVA)
textures/entity/prime/prime_02.png   (DARK_MATTER)
textures/entity/prime/prime_03.png   (GOLD_MATTER)
textures/entity/prime/prime_04.png   (DARK_GOLD)
textures/entity/prime/prime_05.png   (EMBRYON)
textures/entity/prime/prime_06.png   (HESTIA)

textures/item/prime/prime_00.png  through  prime_06.png
textures/item/prime/prime_16.png   ← random/default icon
```

**Hyperion — 2 entity textures + 3 item textures:**
```
textures/entity/hyperion/hyperion_00.png   (COMMANDER)
textures/entity/hyperion/hyperion_01.png   (VALKYRIE)

textures/item/hyperion/hyperion_00.png
textures/item/hyperion/hyperion_01.png
textures/item/hyperion/hyperion_16.png   ← random/default icon
```

**Empyrium — 1 entity texture + 2 item textures:**
```
textures/entity/empyrium/empyrium_00.png   (COLD_GOLD — only texture)

textures/item/empyrium/empyrium_00.png
textures/item/empyrium/empyrium_16.png   ← icon (can be same as _00 or dedicated)
```

### 6.2 GeckoLib Models & Animations

These live in `lovelylib-1.21.1/Common/src/main/resources/assets/lovelylib/`:
```
geo/prime.default.geo.json
geo/prime.armed.geo.json

geo/hyperion.default.geo.json
geo/hyperion.armed.geo.json

geo/empyrium.default.geo.json
geo/empyrium.armed.geo.json
```

Port from the 1.20.4 assets at `archive/1.20.X/rlovelyr-1.20.4/Forge/src/main/resources/assets/rlovelyr/geo/`.

### 6.3 Item Model JSONs (per loader × 3)

Each of the three loader resource directories needs an item model JSON.
**File path:** `rlovelyr-1.21.1/{Forge|Fabric|NeoForge}/src/main/resources/assets/rlovelyr/models/item/{key}_spawn.json`

**Prime (restricted palette — underscore convention):**
```json
{
  "parent": "item/generated",
  "textures": { "layer0": "lovelylib:item/prime/prime_16" },
  "overrides": [
    { "predicate": { "variant": 17 }, "model": "lovelylib:item/prime/prime_02" },
    { "predicate": { "variant": 18 }, "model": "lovelylib:item/prime/prime_01" },
    { "predicate": { "variant": 19 }, "model": "lovelylib:item/prime/prime_00" },
    { "predicate": { "variant": 20 }, "model": "lovelylib:item/prime/prime_05" },
    { "predicate": { "variant": 21 }, "model": "lovelylib:item/prime/prime_04" },
    { "predicate": { "variant": 22 }, "model": "lovelylib:item/prime/prime_03" },
    { "predicate": { "variant": 23 }, "model": "lovelylib:item/prime/prime_06" }
  ]
}
```

**Hyperion (restricted palette — underscore convention):**
```json
{
  "parent": "item/generated",
  "textures": { "layer0": "lovelylib:item/hyperion/hyperion_16" },
  "overrides": [
    { "predicate": { "variant": 24 }, "model": "lovelylib:item/hyperion/hyperion_00" },
    { "predicate": { "variant": 25 }, "model": "lovelylib:item/hyperion/hyperion_01" }
  ]
}
```

**Empyrium (single texture — underscore convention):**
```json
{
  "parent": "item/generated",
  "textures": { "layer0": "lovelylib:item/empyrium/empyrium_16" },
  "overrides": [
    { "predicate": { "variant": 19 }, "model": "lovelylib:item/empyrium/empyrium_00" }
  ]
}
```

> **Note on predicate values:** The `variant` predicate integer must match the `EntityTexture`
> enum ID of the first (default) texture in the palette. For Prime the default is `DARK_MATTER`
> (ID 17); for Hyperion it is `COMMANDER` (ID 24); for Empyrium it is `COLD_GOLD` (ID 19).
> The item is spawned with its first-palette texture as the default color.

### 6.4 lovelylib item model JSONs (per palette entry)

These live in `lovelylib-1.21.1/Common/src/main/resources/assets/lovelylib/models/item/{key}/`.
One file per active texture ID plus the `_16` random icon. Follow the restricted-palette
underscore convention (e.g. `prime_02.json`, not `prime02.json`).

Each file:
```json
{
  "parent": "item/generated",
  "textures": { "layer0": "lovelylib:item/{key}/{key}_{id:02d}" }
}
```

### 6.5 Lang keys

**File:** `rlovelyr-1.21.1/Common/src/main/resources/assets/rlovelyr/lang/en_us.json`
```json
"item.rlovelyr.prime_spawn":    "Spawn Prime",
"entity.rlovelyr.prime":        "Prime",
"item.rlovelyr.hyperion_spawn": "Spawn Hyperion",
"entity.rlovelyr.hyperion":     "Hyperion",
"item.rlovelyr.empyrium_spawn": "Spawn Empyrium",
"entity.rlovelyr.empyrium":     "Empyrium"
```

**File:** `lovelylib-1.21.1/Common/src/main/resources/assets/lovelylib/lang/en_us.json`
```json
"variant.lovelylib.prime":    "Prime",
"variant.lovelylib.hyperion": "Hyperion",
"variant.lovelylib.empyrium": "Empyrium"
```

### 6.6 Recipes

**File:** `rlovelyr-1.21.1/Common/src/main/resources/data/rlovelyr/recipe/{key}_spawn.json`

All three use the standard Fabricator recipe. Ingredient tier scales with power level:

| Robot | Centre ingredient | Ring ingredients | Rationale |
|-------|-----------------|------------------|-----------|
| Prime | `rlovelyr:robot_core` | `minecraft:blaze_rod` (N) + `minecraft:gold_block` (I) | High tier |
| Hyperion | `rlovelyr:robot_core` | `minecraft:blaze_rod` (N) + `minecraft:netherite_scrap` (I) | Top tier |
| Empyrium | `rlovelyr:robot_core` | `minecraft:netherite_scrap` (N) + `minecraft:ancient_debris` (I) | Apex tier |

Exact pattern and ingredients are your design decision. The recipe `type` must be
`"rlovelyr:lovely_spawn"`.

No dye recipe is needed for Empyrium (single colour). Prime and Hyperion need dye recipes
(`rlovelyr:lovely_spawn_dye`) even though dye outside their palette produces no visual change —
the runtime `RobotFamily.handleItemInteraction` guards against out-of-palette dye application.

---

## 7. `RebootConfigs` — stat defaults

**File:** `lovelylib-1.21.1/Common/.../source/reboot/RebootConfigs.java`

Add to the `static {}` default map. The values here become the in-game config defaults and
the `ConfigAccessLayer` fallback via the definition's `EntityStats`:

```java
// Prime — powerful all-rounder, Aldarian Tech tier
Default.put("prime",    new SharedConfigs.EntityConfigData(200, 30, 8,  1.5f, 6, 1.0f, 0f, 0.33f));

// Hyperion — apex tank, hardest to craft
Default.put("hyperion", new SharedConfigs.EntityConfigData(200, 32, 9,  1.4f, 7, 1.0f, 0f, 0.34f));

// Empyrium — immovable fortress, no interaction
Default.put("empyrium", new SharedConfigs.EntityConfigData(200, 35, 12, 1.2f, 8, 2.0f, 0f, 0.30f));
```

These values are starting points — tune based on playtesting.

---

## 8. Migration (`MigrationStep_V1_1204`)

**File:** `lovelylib-1.21.1/Common/.../entity/data/migration/MigrationStep_V1_1204.java`

The 1.20.4 Forge builds stored `TextureID` as an integer using the enum ordinal. The extended
texture IDs (17–25) map to texture filenames as follows. Add these cases to the migration
step's texture-ID-to-variant-key table:

| Old int TextureID | New TextureVariant string |
|------------------|--------------------------|
| 17 | `"prime"` + file suffix resolved by `EntityTexture.DARK_MATTER` → `"prime_02"` |
| 18 | `"prime_01"` (SUPERNOVA) |
| 19 | `"prime_00"` (COLD_GOLD) or `"empyrium_00"` — disambiguate by entity type key |
| 20 | `"prime_05"` (EMBRYON) |
| 21 | `"prime_04"` (DARK_GOLD) |
| 22 | `"prime_03"` (GOLD_MATTER) |
| 23 | `"prime_06"` (HESTIA) |
| 24 | `"hyperion_00"` (COMMANDER) |
| 25 | `"hyperion_01"` (VALKYRIE) |

The migration step already reads the entity type key from the root compound — use it to
disambiguate `COLD_GOLD` (ID 19) between `prime` and `empyrium`.

---

## 9. Complete Checklist

### lovelylib (`lovelylib-1.21.1/Common/`)

- [ ] `LovelyConstant.java` — 9 new `TEX_*` string constants
- [ ] `EntityTexture.java` — 9 new enum entries (IDs 17–25)
- [ ] `RobotVariant.java` — 3 new static constants (Prime ID 9, Hyperion ID 10, Empyrium ID 11)
- [ ] `RebootRobotDefinitions.java` — 3 builder calls
- [ ] `RebootConfigs.java` — 3 `Default.put()` entries
- [ ] `BlazeCycleFeature.java` — new feature class in `api/entity/features/`
- [ ] `BlockAllItemInteractionFeature.java` — new feature class in `api/entity/features/`
- [ ] `RobotEntity.java` — dispatch guards for both new features
- [ ] `MigrationStep_V1_1204.java` — texture ID mappings for IDs 17–25
- [ ] `en_us.json` (lovelylib lang) — 3 `variant.lovelylib.*` keys

### lovelylib (`lovelylib-1.21.1/Common/` resources)

- [ ] `geo/prime.default.geo.json` + `geo/prime.armed.geo.json`
- [ ] `geo/hyperion.default.geo.json` + `geo/hyperion.armed.geo.json`
- [ ] `geo/empyrium.default.geo.json` + `geo/empyrium.armed.geo.json`
- [ ] `textures/entity/prime/` — 7 PNGs
- [ ] `textures/entity/hyperion/` — 2 PNGs
- [ ] `textures/entity/empyrium/` — 1 PNG
- [ ] `textures/item/prime/` — 8 PNGs (prime_00–06 + prime_16)
- [ ] `textures/item/hyperion/` — 3 PNGs (hyperion_00, hyperion_01, hyperion_16)
- [ ] `textures/item/empyrium/` — 2 PNGs (empyrium_00, empyrium_16)
- [ ] `models/item/prime/` — 8 JSON files (prime_00–06 + prime_16)
- [ ] `models/item/hyperion/` — 3 JSON files
- [ ] `models/item/empyrium/` — 2 JSON files

### rlovelyr (`rlovelyr-1.21.1/`) — Java: **zero changes**

### rlovelyr — resources (Common + per loader)

- [ ] `Common/assets/rlovelyr/lang/en_us.json` — 6 keys (item + entity per robot × 3)
- [ ] `Common/data/rlovelyr/recipe/prime_spawn.json`
- [ ] `Common/data/rlovelyr/recipe/prime_spawn_dye.json`
- [ ] `Common/data/rlovelyr/recipe/hyperion_spawn.json`
- [ ] `Common/data/rlovelyr/recipe/hyperion_spawn_dye.json`
- [ ] `Common/data/rlovelyr/recipe/empyrium_spawn.json` (no dye recipe)
- [ ] `Forge/assets/rlovelyr/models/item/prime_spawn.json`
- [ ] `Forge/assets/rlovelyr/models/item/hyperion_spawn.json`
- [ ] `Forge/assets/rlovelyr/models/item/empyrium_spawn.json`
- [ ] `Fabric/assets/rlovelyr/models/item/prime_spawn.json`
- [ ] `Fabric/assets/rlovelyr/models/item/hyperion_spawn.json`
- [ ] `Fabric/assets/rlovelyr/models/item/empyrium_spawn.json`
- [ ] `NeoForge/assets/rlovelyr/models/item/prime_spawn.json`
- [ ] `NeoForge/assets/rlovelyr/models/item/hyperion_spawn.json`
- [ ] `NeoForge/assets/rlovelyr/models/item/empyrium_spawn.json`

**Total Java files changed:** 8 (all in lovelylib Common)
**Total resource files:** ~47 (models, textures, lang, recipes, geo)
**Loader files changed:** 0

---

## 10. Consequences

### Positive
- Prime, Hyperion, and Empyrium are fully functional across all three loaders with no loader-specific code.
- `BlazeCycleFeature` and `BlockAllItemInteractionFeature` are reusable for future Reboot-exclusive robots.
- The extended `EntityTexture` IDs slot cleanly above the standard 16-colour palette with no
  disruption to existing dye recipes or colour-cycling behaviour.

### Negative / Trade-offs
- `EntityTexture` is an enum — the 9 new entries are lovelylib-built-in only. Addon mods that
  want their own named textures cannot extend this enum without forking. This is acceptable
  for now; a future ADR can migrate to a registry-backed texture system if needed.
- Dye recipes exist for Prime and Hyperion even though out-of-palette dyes do nothing visible.
  This is intentional — the recipe exists as a crafting surface, not as a functional recolour.

### Risks
- Assets must be ported from the 1.20.4 archive manually. The geo format has not changed
  between 1.20.4 and 1.21.1 for GeckoLib, but verify the GeckoLib version in `gradle.properties`
  to confirm compatibility before importing.
