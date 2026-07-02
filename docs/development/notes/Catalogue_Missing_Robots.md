# Missing Robot Catalogue — Lovely Reboot (rlovelyr)

**Status**: Planning  
**Last Updated**: 2026-07-02  
**Author(s)**: Serge Maia  
**Related Documents**:
- `docs/guidelines/New_Robot_Family_Guide.md` — full 45-file implementation checklist
- `archive/1.20.X/rlovelyr-1.20.4/` — reference implementation source
- `sources/reboot/rlovelyr-1.21.1/` — target codebase (currently empty stub)

---

## Purpose

This catalogue documents the five robot families that must be implemented (or ported) in the `rlovelyr-1.21.1` codebase. Three are **ports from 1.20.4** (Prime, Hyperion, Empyrium); two are **entirely new robots** with no prior implementation (Stinger, Sentry).

The current state of `rlovelyr-1.21.1` is a stub — only `Reboot.java`, `RebootIdentifier.java`, and `package-info.java` exist. The full entity pipeline (entities, items, configs, resources) must be built from scratch following the `New_Robot_Family_Guide.md`.

---

## Current State — rlovelyr-1.21.1

| Layer | Status |
|-------|--------|
| `Reboot.java` main class | Stub (init() is a no-op) |
| Entities | None registered |
| Items | None registered |
| Configs | None |
| Resources (lang, models, textures) | None |
| Recipes | None |

All 5 robots below are **fully missing** from the 1.21.1 codebase.

---

## Reference — 1.20.4 Robot Roster

For context, the complete 1.20.4 roster (Forge) before porting:

**Standard Tier** (7 robots — also exist in llovelyr):
Bunny, Bunny2, Dragon, Honey, Kitsune, Neko, Vanilla

**Aldarian Tech Tier** (3 robots — rlovelyr-exclusive, in their own creative tab):
Prime, Hyperion, Empyrium

**New Robots** (2 robots — no prior codebase, design below):
Stinger, Sentry

---

## Robot 1 — Prime

**Key**: `prime`  
**Variant Enum**: `RobotVariant.Prime`  
**Origin**: Port from 1.20.4  
**Tier / Creative Tab**: Aldarian Tech  
**Status**: Missing from 1.21.1

### 1.20.4 Behaviour Summary

- Uses a **restricted colour palette**: 7 textures mapped to named slots
  - `DARK_MATTER` → `prime_02.png`
  - `SUPERNOVA`   → `prime_01.png`
  - `COLD_GOLD`   → `prime_00.png`
  - `EMBRYON`     → `prime_05.png`
  - `DARK_GOLD`   → `prime_04.png`
  - `GOLD_MATTER` → `prime_03.png`
  - `HESTIA`      → `prime_06.png`
- Blaze Rod cycles through the palette: each use steps forward one texture, wrapping back to `DARK_MATTER`
- `canInteractWithItems` blocks Blaze Rod from reaching the default handler (texture cycling is handled entirely in `handleTexture`)
- Stats in 1.20.4 use Vanilla defaults (same config block) — rebalance for 1.21.1 as needed
- Drop item: `prime_spawn`

### What Needs Building (1.21.1)

Follow `New_Robot_Family_Guide.md` fully. Key decisions for Prime:

| Decision Point | Value |
|----------------|-------|
| Colour palette | Restricted — 7 named colours (not the standard 16) |
| Texture cycling | Blaze Rod interaction — cycle through palette in order |
| Config tier | Reboot-exclusive — `RebootConfigs` + `RebootRobotFamilies` |
| Spawn egg | `prime_spawn` item, Rarity.RARE |
| Creative tab | Aldarian Tech tab (separate from standard tab) |
| Stats baseline | Port from 1.20.4; adjust balance if needed |

### Assets Required

```
textures/entity/prime/prime_00.png  (COLD_GOLD)
textures/entity/prime/prime_01.png  (SUPERNOVA)
textures/entity/prime/prime_02.png  (DARK_MATTER)
textures/entity/prime/prime_03.png  (GOLD_MATTER)
textures/entity/prime/prime_04.png  (DARK_GOLD)
textures/entity/prime/prime_05.png  (EMBRYON)
textures/entity/prime/prime_06.png  (HESTIA)
textures/item/prime/prime_00.png through prime_06.png
textures/item/prime/prime_16.png    (random/default icon)
geo/prime.default.geo.json
geo/prime.armed.geo.json
```

---

## Robot 2 — Hyperion

**Key**: `hyperion`  
**Variant Enum**: `RobotVariant.Hyperion`  
**Origin**: Port from 1.20.4  
**Tier / Creative Tab**: Aldarian Tech  
**Status**: Missing from 1.21.1

### 1.20.4 Behaviour Summary

- Uses a **restricted colour palette**: 2 textures
  - `COMMANDER` → `hyperion_00.png`
  - `VALKYRIE`  → `hyperion_01.png`
- Blaze Rod cycles between the two — wraps from VALKYRIE back to COMMANDER
- `canInteractWithItems` blocks Blaze Rod from the default handler; texture logic is self-contained in `handleTexture`
- Stats in 1.20.4 use Vanilla defaults — rebalance for 1.21.1 as needed
- Drop item: `hyperion_spawn`

### What Needs Building (1.21.1)

| Decision Point | Value |
|----------------|-------|
| Colour palette | Restricted — 2 named colours (Commander, Valkyrie) |
| Texture cycling | Blaze Rod — toggle between the two |
| Config tier | Reboot-exclusive — `RebootConfigs` + `RebootRobotFamilies` |
| Spawn egg | `hyperion_spawn` item, Rarity.RARE |
| Creative tab | Aldarian Tech tab |
| Stats baseline | Port from 1.20.4; adjust balance if needed |

### Assets Required

```
textures/entity/hyperion/hyperion_00.png  (COMMANDER)
textures/entity/hyperion/hyperion_01.png  (VALKYRIE)
textures/item/hyperion/hyperion_00.png
textures/item/hyperion/hyperion_01.png
textures/item/hyperion/hyperion_16.png    (random/default icon)
geo/hyperion.default.geo.json
geo/hyperion.armed.geo.json
```

---

## Robot 3 — Empyrium

**Key**: `empyrium`  
**Variant Enum**: `RobotVariant.Empyrium`  
**Origin**: Port from 1.20.4  
**Tier / Creative Tab**: Aldarian Tech  
**Status**: Missing from 1.21.1

### 1.20.4 Behaviour Summary

- Uses a **single texture** (no palette cycling):
  - `COLD_GOLD` → `empyrium_00.png`
- `handleItemInteraction` returns `PASS` — Empyrium cannot be interacted with via items (no texture cycling, no dye)
- `canInteractWithItems` blocks Blaze Rod
- Stats in 1.20.4 use Vanilla defaults — rebalance for 1.21.1 as needed
- Drop item: `empyrium_spawn`

### What Needs Building (1.21.1)

| Decision Point | Value |
|----------------|-------|
| Colour palette | Single colour only — no cycling, no dye reaction |
| Item interaction | Disabled (returns PASS for all item interactions) |
| Config tier | Reboot-exclusive — `RebootConfigs` + `RebootRobotFamilies` |
| Spawn egg | `empyrium_spawn` item, Rarity.RARE |
| Creative tab | Aldarian Tech tab |
| Stats baseline | Port from 1.20.4; consider making stats distinctly higher to justify its locked appearance |

### Assets Required

```
textures/entity/empyrium/empyrium_00.png  (COLD_GOLD — only texture)
textures/item/empyrium/empyrium_00.png
textures/item/empyrium/empyrium_16.png    (icon — same as _00 or a dedicated icon)
geo/empyrium.default.geo.json
geo/empyrium.armed.geo.json
```

---

## Robot 4 — Stinger ⭐ NEW

**Key**: `stinger`  
**Variant Enum**: `RobotVariant.Stinger`  
**Origin**: New design — no prior implementation  
**Tier / Creative Tab**: Aldarian Tech (proposed) or Standard (your call)  
**Status**: Does not exist anywhere — must be designed and built

### Design Specification

**Stinger is the first Reboot robot to attack from a distance.**

This is the primary design distinction. All existing robots (Vanilla, Bunny, Kitsune, etc.) attack in melee. Stinger introduces a **ranged attack mode** — firing projectiles at enemies rather than closing to melee range.

| Attribute | Proposed Value | Notes |
|-----------|---------------|-------|
| Combat style | Ranged (projectile-based) | Core mechanic — first in rlovelyr |
| Melee fallback | Optional close-range attack if target is too near | Prevents dead zone at zero range |
| Projectile type | Robot-themed energy bolt / needle | Custom projectile class needed |
| Range | ~12–16 blocks | Similar to Skeleton bow range |
| Fire rate | Moderate (not rapid-fire) | Balance against melee robots |
| Stats | Moderate HP, lower armour | Compensated by keeping distance |
| Colour palette | Full 16 or restricted — your design | Recommend full 16 for flexibility |

#### New Systems Required

Stinger requires **new engine work** beyond the standard robot checklist. These are additions to the base `NativeRobotEntity` / `RobotFamily` feature pipeline:

1. **`RangedAttackFeature`** (or equivalent)
   - Declare ranged capability on the `RobotFamily` definition
   - AI goal: `RangedAttackGoal` (or lovelylib equivalent) — replaces/supplements the melee goal
   - Goal priority ordering: ranged goal at higher priority than melee

2. **Projectile entity class**
   - `StingerBoltEntity` (or a shared `RobotProjectile` class if other ranged robots are planned)
   - Registered as an `EntityType` in `RebootEntities`
   - Needs renderer (can be a simple arrow-style or custom geo renderer)
   - Damage sourced from the robot's current attack stat

3. **Animation**
   - Needs a distinct ranged-attack animation in the GeckoLib animator
   - Suggested: `attack_ranged` animation state separate from the melee `attack` state
   - The lovelylib `AnimationProfile` must expose a `rangedAttack` slot

### Implementation Checklist Additions (beyond standard guide)

- [ ] Design ranged attack AI goal (RangedAttackGoal or custom)
- [ ] Implement projectile entity (`StingerBoltEntity` or shared `RobotProjectile`)
- [ ] Register projectile entity type in all three loaders
- [ ] Add projectile renderer (all three loaders)
- [ ] Add `attack_ranged` animation slot to `AnimationProfile` in lovelylib
- [ ] Add ranged capability flag/feature to `RobotFamily`
- [ ] Wire ranged goal to the entity AI during `registerNativeRobotFeature()`
- [ ] All standard 45-step checklist items (same as any new family)

### Assets Required

```
textures/entity/stinger/stinger_00.png through stinger_15.png  (or restricted subset)
textures/item/stinger/stinger_00.png through stinger_15.png
textures/item/stinger/stinger_16.png    (random/default icon)
geo/stinger.default.geo.json
geo/stinger.armed.geo.json
geo/stinger_bolt.geo.json               (projectile — if custom model)
animations/stinger_bolt.animation.json  (if custom projectile animation)
```

---

## Robot 5 — Sentry ⭐ NEW

**Key**: `sentry`  
**Variant Enum**: `RobotVariant.Sentry`  
**Origin**: New design — no prior implementation  
**Tier / Creative Tab**: Aldarian Tech (proposed)  
**Status**: Does not exist anywhere — must be designed and built

### Design Specification

**Sentry is the first Reboot robot to equip a gear item (the Gauntlet).**

This is the primary design distinction. Sentry introduces a **gear slot system** — the ability to hold and use an equippable item (the Gauntlet, to be provided separately) that modifies combat behaviour, appearance, or stats.

| Attribute | Proposed Value | Notes |
|-----------|---------------|-------|
| Combat style | Melee (enhanced by gauntlet) | Standard melee baseline |
| Unique mechanic | Equips the Gauntlet gear item | First gear-capable robot |
| Gauntlet effect | TBD — your design (damage boost, AoE, special attack?) | Gauntlet item provided separately |
| Gauntlet slot | Dedicated gear slot on the entity | New NBT/SynchedData field needed |
| Visual indicator | Gauntlet appears on the model when equipped | Overlay layer or bone attachment |
| Stats | High HP and armour — heavy fighter profile | Compensates for slower/deliberate style |
| Colour palette | Full 16 or restricted — your design | |

#### New Systems Required

Sentry requires **new engine work** beyond the standard robot checklist:

1. **Gear slot system**
   - `GearFeature` (or `GearSlotFeature`) on `RobotFamily`
   - Declares which gear items the robot can accept
   - Stores equipped gear in `SynchedEntityData` (synced to client for rendering)
   - Serialized to/from NBT for persistence

2. **Gauntlet item interaction**
   - Right-clicking Sentry with the Gauntlet equips it
   - Right-clicking again (or shift-right-click) unequips and returns it to the player
   - Interaction handled in `handleItemInteraction` override or via a `GearInteractionFeature`

3. **Gear stat modifier**
   - When Gauntlet is equipped: apply stat multipliers (attack boost, etc.)
   - When unequipped: revert to base stats
   - Consider using the existing `EmanationFeature` / attribute modifier approach, or a dedicated `GearStatModifier`

4. **Visual rendering**
   - Gauntlet rendered as an overlay layer or bone attachment on the entity model
   - `OverlayFeature` with an `ALWAYS` slot when gear is equipped (conditional on gear data)
   - Or: a dedicated bone in the GeckoLib model (`right_hand_gear` bone) driven by `GearEquippedFeature`

### Implementation Checklist Additions (beyond standard guide)

- [ ] Design and implement `GearFeature` in lovelylib
- [ ] Implement gear slot NBT serialization / SynchedEntityData
- [ ] Implement gear equip/unequip interaction logic
- [ ] Implement gear stat modifier (attack, armour, or special when equipped)
- [ ] Implement gear visual layer (overlay or bone attachment)
- [ ] Implement Gauntlet item class (provided separately — wire to Sentry's gear slot)
- [ ] Register Gauntlet item in `RebootItems` for all loaders
- [ ] Add gear-conditional animation (optional: different attack animation when gauntlet equipped)
- [ ] All standard 45-step checklist items (same as any new family)

### Assets Required

```
textures/entity/sentry/sentry_00.png through sentry_15.png  (or restricted subset)
textures/item/sentry/sentry_00.png through sentry_15.png
textures/item/sentry/sentry_16.png    (random/default icon)
geo/sentry.default.geo.json
geo/sentry.armed.geo.json
textures/entity/sentry/sentry_gauntlet_equipped.png   (overlay when gauntlet on)
   -- OR --
geo/sentry_gauntlet_bone.geo.json                     (if using bone attachment)
```

---

## Implementation Order (Recommended)

Given the system dependencies, implement in this sequence:

```
1. Prime        — Straightforward port, restricted palette, no new systems
2. Hyperion     — Straightforward port, simplest palette (2 textures)
3. Empyrium     — Straightforward port, single texture, no interactions
4. Stinger      — Requires new ranged attack system before entity can be finished
5. Sentry       — Requires new gear slot system; highest complexity
```

Prime, Hyperion, and Empyrium can be completed as a batch using the standard guide alone.  
Stinger and Sentry each require a lovelylib engine contribution before the robot itself is done.

---

## Standard Guide Reference

Every robot (ports and new) must complete the full checklist in `New_Robot_Family_Guide.md`.  
Key files per robot (45 files total for a full Forge + Fabric + NeoForge implementation):

| Layer | Files |
|-------|-------|
| lovelylib `LovelyConstant` | 1 |
| lovelylib `RobotVariant` | 1 |
| lovelylib `RebootRobotFamilies` | 1 |
| lovelylib `RebootConfigs` | 1 |
| lovelylib `SharedConfigs` | 1 |
| lovelylib `ConfigAccessLayer` | 1 |
| lovelylib `LovelyIdentifier` | 1 |
| lovelylib lang + models/item + textures | ~20 asset files |
| rlovelyr per loader × 3 (Items, Entities, Groups) | 9 |
| rlovelyr Common lang + recipes | 3 |
| rlovelyr per loader item model JSON × 3 | 3 |

**Stinger adds**: projectile entity + renderer (×3 loaders), lovelylib `AnimationProfile` ranged slot, `RangedAttackFeature` — estimate ~8 additional files.

**Sentry adds**: gear feature system, gear interaction, gear visual, Gauntlet item (×3 loaders) — estimate ~12 additional files.
