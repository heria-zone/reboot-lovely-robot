# Pre-Publish Checklist — Lovely Recreations

**Status**: Active — Working Notes  
**Last Updated**: 2026-06-25  
**Scope**: Features and fixes needed before the initial public release of Tribute, Legacy, and Reboot  
**Related Sprint**: Post-Sprint 10 / Pre-Release

---

## Current Family Roster (1.21.1)

Before adding new types, this is what exists today:

| Family | Tribute | Legacy | Reboot |
|--------|---------|--------|--------|
| Bunny | ✅ | ✅ | ✅ |
| Bunny2 | ✅ | ✅ | ✅ |
| Honey | ✅ | ✅ | ✅ |
| Vanilla | ✅ | ✅ | ✅ |
| Dragon | ❌ | ✅ | ✅ |
| Kitsune | ❌ | ✅ | ✅ |
| Neko | ❌ | ✅ | ✅ |
| Bunny3 | ❌ | ❌ | ❌ |
| Hyperion | ❌ | ❌ | ❌ |
| Empyrium | ❌ | ❌ | ❌ |
| Prime | ❌ | ❌ | ❌ |

> **Note on 1.20.4 archive**: `HYPERION` (variants: Commander, Valkyrie), `EMPYRIUM` (variant: ColdGold), and `PRIME` (7 texture variants: DarkMatter, Supernova, ColdGold, Embryon, DarkGold, GoldMatter, Hestia) all existed in the 1.20.4 archive (`archive/1.20.X/rlovelyr-1.20.4/`) but were never ported to 1.21.1. These are the premium/advanced families to be added to Reboot.

---

## Checklist

### 1. ConditionalAppearanceFeature

**What**: A single unified feature in HZLib that replaces both `BiomeAppearanceFeature` (deleted) and the planned `ItemAppearanceFeature` (never built). It is a composable condition-to-variant-key resolver that handles biome, item, dimension, time, weather, and any future context type through the same API.

**Why**: `BiomeAppearanceFeature` and `ItemAppearanceFeature` are the same pattern with different input types. Keeping them separate produces two parallel classes, two feature lookups in `NativeEntity`, and no composability between them. One context bag + composable predicates covers all cases.

**Current state**: `BiomeAppearanceFeature` exists but has zero usage in any family class — it is a clean delete. The dye interaction in `RobotEntity` is a hardcoded chain — replaced by a `ConditionalAppearanceFeature` declaration on each `RobotFamily`.

**What to do**:
- Implement `ConditionalAppearanceFeature` and its supporting types (`AppearanceCondition`, `AppearanceContext`, `AppearanceConditions`, `AppearanceRule`, `WeightedAppearancePool`) following the same architecture as `ExchangeCondition`/`ExchangeContext`/`ExchangeConditions` already in HZLib
- Delete `BiomeAppearanceFeature.java`
- Wire `NativeEntity.initializeSpawnVariants` to read `ConditionalAppearanceFeature` in the base class
- Declare dye mappings on each `RobotFamily` in LovelyLib and remove the hardcoded dye chain from `RobotEntity`

**ADR**: `docs/development/decisions/ADR_020_Conditional_Appearance_Feature.md`

**Location**: `sources/common/hzlib-1.21.1/Common/src/main/java/net/heriazone/hzlib/api/entity/features/`

**Location**: `sources/common/hzlib-1.21.1/Common/src/main/java/net/heriazone/hzlib/api/entity/features/`

---

### 2. Animation Improvements — LovelyLib on top of HZLib

**What**: LovelyLib should fully adopt the HZLib animation system as its foundation, then extend it with robot-specific capabilities that HZLib intentionally does not cover.

**Design intent — two separate concerns**:

**A — LovelyLib extensions over HZLib (robot-specific, not HZLib's responsibility)**:
- **Named bone pass-through**: allow the family declaration to specify a bone name for the head (and other special bones). The animation layer reads this at runtime and drives that bone independently (e.g. head tracking toward the owner or a look target). HZLib has `BoneTransformations` and `TailAnimationUtils` as utilities but does not wire them to a family-level config — LovelyLib needs to do this.
- **Tail animation system**: Kitsune and any future tailed family need tail bones animated separately from the body. LovelyLib should declare a tail bone config on the family descriptor (bone name(s), oscillation parameters) and drive it through a dedicated animation step that HZLib does not provide.

**B — HZLib animation system improvement (benefits all mods, not just LovelyLib)**:
- **Conditional idle animation slots**: extend the HZLib animation state machine to support multiple idle-state animations declared on the family, each with a condition and an optional time threshold. The system evaluates conditions in priority order each tick and plays the first one that passes. Example family declaration:
  ```
  idleSlot: play "idle_stand"  → condition: always            (priority 0 — fallback)
  idleSlot: play "idle_sit"    → condition: stationary for ≥ N ticks (priority 1)
  idleSlot: play "idle_look"   → condition: owner nearby, random 5%/tick (priority 2)
  ```
  The system picks the highest-priority slot whose condition is met, transitions once, and holds until the condition breaks — no per-entity timer code needed.
- **This is the intended long-term replacement for the standby REST→SIT bespoke timer in `RobotEntity`** (item 3). Once this system exists, the `handleStandbyAnimation()` tick counter and all its logic get deleted and replaced by a two-line family declaration: `idle_stand` always, `idle_sit` after N ticks stationary. Item 3 is a short-term patch; this is the proper fix.
- The standby case is the primary driver for this feature but the system is general — any family can declare its own idle variation logic without touching entity code.

**Current state**:
- HZLib has `BoneTransformations.java` and `TailAnimationUtils.java` as low-level utilities but nothing wires them to a family descriptor
- LovelyLib has `BoneTransformations`, `TailAnimationUtils`, `EntityAnimation`, `EntityAnimator` enums and the standby tick logic in `RobotEntity` — all ad-hoc, nothing declarative
- No conditional idle slot system exists anywhere in the stack

**What to do**:
- Design the conditional animation slot API in HZLib first (needs its own ADR — architectural impact is broad)
- Add `headBone` and `tailBones` config fields to `RobotFamily` in LovelyLib
- Wire `BoneTransformations` / `TailAnimationUtils` to read from those fields in the animation step

**Location**: `sources/common/hzlib-1.21.1/` (conditional slot system) + `sources/common/lovelylib-1.21.1/Common/.../animation/` (bone config + tail system)

---

### 3. Fix: Standby Mode Animation Flicker

**What**: When a robot enters standby mode, the idle animation flickers between the `stand` and `rest` states.

**Relationship to item 2**: The conditional idle slot system in item 2B is the proper architectural replacement for the bespoke standby timer. Once that system is built, `handleStandbyAnimation()` and its tick counter get deleted entirely — the REST→SIT transition becomes a two-line family declaration. **This item (3) is only the short-term patch** to make the behaviour correct while item 2B is being designed and built.

**Current state**: `handleStandbyAnimation()` in `RobotEntity.java` manages the REST → SIT transition using a tick counter called from `aiStep()`. The flicker suggests the animation state is being set incorrectly — two possible root causes to investigate before touching anything:

1. **State written every tick instead of on transition**: the animation state (REST/SIT) may be set unconditionally on every tick rather than only when it *changes*. Guard it so the animation trigger fires once on state entry, not on every tick the entity remains in that state.

2. **`aiStep` server-only timing mismatch**: `aiStep()` only runs on the server. If `handleStandbyAnimation()` drives animation state from inside `aiStep()`, the state change is server-authoritative while the animation renderer runs client-side — the client may not receive the update at the right frame, causing the flicker. Check whether this call should live in a tick method that runs on both sides, or whether the resulting state needs to be synced to the client explicitly.

**Reference fix**: The same issue was solved in Monsters & Girls — check how `AnimationStateManager` guards state transitions there (Sprint 10 context).

**What to do**:
- Confirm which tick method `handleStandbyAnimation()` is called from and whether it runs client-side, server-side, or both
- If the `aiStep` mismatch is the cause, move the call or sync the resulting animation state
- If it is a re-write-every-tick issue, add a transition guard
- Once item 2B (conditional idle slots) is implemented, delete `handleStandbyAnimation()` entirely and replace with a family-level declaration

**Location**: `sources/common/lovelylib-1.21.1/Common/.../entity/RobotEntity.java` — `handleStandbyAnimation()`

---

### 4. Add Bunny3 to Legacy

**What**: A new Bunny 3.0 family variant added to Legacy only.

**Scope**: Two colors for the initial release. More colors can follow post-launch.

**Current state**: `RobotVariant` enum has `Bunny` (id=0) and `Bunny2` (id=1). `Bunny3` does not exist.

**What to do**:
- Add `Bunny3` to the `RobotVariant` enum in LovelyLib Common
- Add `BUNNY3` to `LegacyRobotFamilies` with two texture variants
- Register `BUNNY3` entity type, spawn item, and renderer in the Legacy loader classes (`LegacyEntities`, `LegacyItems`)
- Add textures and model (can reuse/adapt Bunny2 model if design is similar)
- Wire config entry in `LegacyConfigs`

**Location**: `sources/common/lovelylib-1.21.1/` and `sources/legacy/llovelyr-1.21.1/`

---

### 5. Add Hyperion, Empyrium, Prime + Bunny3 to Reboot

**What**: Port the three premium/advanced families from the 1.20.4 archive into Reboot 1.21.1, plus Bunny3.

**Background from 1.20.4 archive** (`archive/1.20.X/rlovelyr-1.20.4/`):
- `HYPERION` — two texture variants: `Commander` (`_00.png`) and `Valkyrie` (`_01.png`)
- `EMPYRIUM` — one texture variant: `ColdGold` (`_00.png`)
- `PRIME` — seven texture variants: `DarkMatter` (`_02`), `Supernova` (`_01`), `ColdGold` (`_00`), `Embryon` (`_05`), `DarkGold` (`_04`), `GoldMatter` (`_03`), `Hestia` (`_06`)
- All three shared the same base stats as `VANILLA` in 1.20.4 (their own balance pass will be needed for 1.21.1)
- Models were at `geo/hyperion.geo.json`, `geo/empyrium.geo.json`, `geo/prime.geo.json`
- Animations defaulted to `animations/default.animation.json`

**What to do**:
- Add `Hyperion`, `Empyrium`, `Prime`, and `Bunny3` to the `RobotVariant` enum in LovelyLib Common
- Add `HYPERION`, `EMPYRIUM`, `PRIME`, and `BUNNY3` to `RebootRobotFamilies` with their texture variant sets (see archive for exact mapping)
- Add `BUNNY3` to `RebootRobotFamilies` (same variant as Legacy, item 4)
- Register entity types, spawn items, and renderers in Reboot loader classes (`source/LegacyEntities`, items, groups)
- Port or recreate models and textures from the 1.20.4 archive into the 1.21.1 resource structure
- Wire config entries in `RebootConfigs` for the new families
- Balance pass on stats (1.20.4 used vanilla stats as placeholder)

**Location**: `sources/common/lovelylib-1.21.1/` and `sources/reboot/rlovelyr-1.21.1/`  
**Reference**: `archive/1.20.X/rlovelyr-1.20.4/Forge/src/main/java/net/msymbios/rlovelyr/entity/internal/NativeEntityType.java`

---

### 6. Collar Straps — Conditional Visibility

**What**: Add a new static collar straps layer that only shows when another Lovely Recreations mod instance is also installed.

**Clarification — the collar has two distinct parts**:
- **Bell** (dynamic) — the existing `DynamicColorLayer` with `LovelyResource.GENERAL_LAYER_COLLAR_DYE`. This changes color based on health (green → yellow → red). **Do not touch this layer.**
- **Straps** (static) — the collar straps texture. This is the new layer to add. It does not change color or react to health; it is simply shown or hidden.

**Why**: The straps are a cross-mod visual cue that signals a robot belongs to the Lovely Recreations ecosystem. If only one mod is installed in isolation, the straps have no cross-mod meaning and should be hidden.

**Current state**: Only the bell/health layer exists. There is no straps layer at all.

**What to do**:
- Add a new static texture layer for the collar straps (separate texture asset needed)
- Gate this layer's rendering behind a runtime check: is any other Lovely mod loaded? (check for `llovelyr`, `tlovelyr`, or `rlovelyr` mod IDs via the mod loader's mod list API)
- The check runs once at mod init and caches the result — not per-frame
- The existing bell/health `DynamicColorLayer` is untouched

**Location**: `sources/common/lovelylib-1.21.1/` — renderer (`NativeRobotRenderer`) and a new utility or platform service for the mod-presence check

---

### 7. Three Wrenches — Cross-Ecosystem Robot Pickup

**What**: Three wrench items (one per mod: Tribute, Legacy, Reboot) that allow players to pick up a robot. Each wrench is bound to its mod's ecosystem and can convert a robot between the Lovely Recreations mods.

**Current state**: No wrench item exists in 1.21.1. `PickupFeature` exists and handles the pickup-to-item conversion, but there is no dedicated wrench item.

**What to do**:
- Create `TributeWrench`, `LegacyWrench`, and `RebootWrench` items (or a single `LovelyWrench` with a mod-ID property)
- Right-click a robot with the wrench → picks it up as a spawn item bound to that wrench's ecosystem
- If the robot belongs to a different mod's family, convert it to the closest equivalent family in the wrench's ecosystem on pickup (e.g. Reboot wrench on a Tribute Bunny → produces a Reboot Bunny spawn item)
- Register items in the respective mod's item registries and creative tabs

**Location**: `sources/common/lovelylib-1.21.1/` (shared wrench logic) + per-mod loader registries

---

### 8. Tribute — AI Goals (Wolf-Like Behaviour)

**What**: Tribute robots should behave similarly to the original LovelyRobot wolf-inspired AI, including patrol.

**Current state**: LovelyLib provides `AiBaseDefenseGoal`, `AiFollowOwnerGoal`, `AiAutoAttackGoal`, `AiConditionalWanderGoal`. No patrol goal exists anywhere in the 1.21.1 codebase.

**What to do**:
- Implement an `AiPatrolGoal` that mimics vanilla wolf patrol behaviour (scan around owner's last position, return to owner if they move too far)
- Wire `AiPatrolGoal` specifically to Tribute robot entities — it should not affect Legacy or Reboot
- Reference the original LovelyRobot source (archive) for the exact patrol parameters and distances
- Consider whether patrol replaces or supplements `AiBaseDefenseGoal` for Tribute

**Location**: `sources/common/lovelylib-1.21.1/Common/.../entity/goal/` (new goal class) + Tribute entity registration

---

### 9. Tribute — Only Original Textures Available

**What**: Tribute robots should only offer the original 4 color/texture options from the original LovelyRobot mod. No expanded palette.

**Current state**: `TributeRobotFamilies` currently only has the 4 original families (Bunny, Bunny2, Honey, Vanilla) — this is already correct. The texture variant registration per family must also be audited to ensure only vanilla-compatible textures are exposed.

**What to do**:
- Audit `TributeRobotFamilies` texture registrations to confirm no extra textures are registered beyond the originals
- Ensure the color command and dye interaction for Tribute entities is locked to the original color set
- Document the constraint explicitly in `TributeRobotFamilies` via a comment

---

### 10. Spawn Item Tooltip

**What**: The spawn item (robot core / spawn egg equivalent) should show a tooltip similar to the original LovelyRobot mod: name, owner, type, color, level.

**Current state**: `TooltipUtils` exists with all the necessary helper methods (`addNameTooltip`, `addOwnerTooltip`, `addTypeTooltip`, `addColorTooltip`, `addLevelTooltip`). It is unclear whether `BaseSpawnItem` calls all of them — the original mod's `SpawnItem.appendHoverText()` in archive/1.18.X calls all five in sequence.

**What to do**:
- Verify `BaseSpawnItem.appendHoverText()` calls all five tooltip methods from `TooltipUtils`
- If any are missing, wire them in
- Ensure the tooltip format matches the original for Tribute (parity requirement)

**Location**: `sources/common/lovelylib-1.21.1/Common/.../items/base/BaseSpawnItem.java` + `TooltipUtils`

---

### 11. Data Pipeline for Entities

**What**: A formal data registry system where each entity family declares which data fields it uses. The entity only reads/writes what is declared — no loose strings scattered across NBT read/write code.

**Background**: `EntityDataMigration` has a `TODO` comment: *"This needs to migrate the current published mod"*. Beyond migration, the broader concept is to replace ad-hoc NBT string keys with a registry-driven approach — similar to how `BiomeAppearanceFeature` replaced hardcoded biome chains.

**Current state**: 
- `EntityDataMigration` handles old-format → new-format NBT migration but the migration for the current published mod is not complete
- Data fields are managed through scattered `SynchedEntityData` accessors with hardcoded string keys in `addAdditionalSaveData` / `readAdditionalSaveData`
- No per-family data declaration exists

**What to do**:
- Define an `EntityDataSchema` or similar concept where each `RobotFamily` (or `NativeEntityFamily`) declares its data fields by key + type
- Entities use the schema to read/write NBT — no hardcoded string keys outside the schema
- Complete `EntityDataMigration` to cover the currently published 1.20.4 save format → 1.21.1 format
- This work likely needs an ADR before implementation (architectural impact is high)

**ADR**: `docs/development/decisions/ADR_019_Entity_Data_Pipeline.md`

**Location**: `sources/common/hzlib-1.21.1/` and `sources/common/lovelylib-1.21.1/`

---

### 12. Two-Lane Appearance Architecture + CompositeAppearanceFeature

**What**: Formalise that the Appearance tier has two distinct, mutually-exclusive implementation patterns — Independent Axes (Lane A) and Composite Bundle (Lane B) — and make that distinction explicit in the codebase. Rename `AppearanceVariantFeature` to `CompositeAppearanceFeature`. Extract `AbstractVariantFeature` as a shared base for the three Lane A features.

**The two lanes**:
- **Lane A — Independent axes**: `TextureVariantFeature` + `ModelVariantFeature` + `AnimatorVariantFeature` vary independently. Used when dimensions are combinatorial — robots with 16 colors × 2 model states don't need all 32 combinations declared explicitly.
- **Lane B — Composite**: `CompositeAppearanceFeature` bundles texture + model + animator + optional size config into explicitly-named appearance entries. Used when dimensions are fully coupled and no free combination is valid — Gourdragora (size × color) is the canonical case.

**Current state**: `AppearanceVariantFeature` exists but is named ambiguously (conflicts with Terminology's Variant tier), sits alongside the three independent features with nothing enforcing mutual exclusion, and `SizeVariantFeature` is orphaned between the two. `TextureVariantFeature`, `ModelVariantFeature`, `AnimatorVariantFeature` are ~300 lines each of copy-pasted identical logic.

**What to do**:
- Extract `AbstractVariantFeature<V>` base class — all three Lane A features collapse to ~15 lines each
- Rename `AppearanceVariantFeature` → `CompositeAppearanceFeature`, `IAppearanceVariant` → `ICompositeAppearance`
- Add `getSizeConfig()` to `ICompositeAppearance` (default returns `Optional.empty()`)
- Update `NativeEntity.initializeRandomVariants` to check Lane B first, fall through to Lane A if absent, log a warning if both are declared
- Update `AnimationStateManager` class name references
- Migrate Gourdragora string-key construction to use `CompositeAppearanceFeature.getVariant()` directly

**ADR**: `docs/development/decisions/ADR_021_Composite_Appearance_Feature.md`

**Location**: `sources/common/hzlib-1.21.1/Common/src/main/java/net/heriazone/hzlib/api/entity/features/variants/`

---

## Priority Ordering (Suggested)

| # | Item | Scope | Blocking? |
|---|------|-------|-----------|
| 3 | Standby flicker fix | LovelyLib | Yes — visual bug visible immediately |
| 10 | Spawn item tooltip | LovelyLib | Yes — first thing players check |
| 9 | Tribute original textures audit | Tribute | Yes — correctness requirement |
| 8 | Tribute AI goals (patrol) | Tribute | No — polish |
| 6 | Collar conditional visibility | LovelyLib | No — polish |
| 4 | Bunny3 → Legacy | Legacy | No — content |
| 5 | Hyperion, Empyrium, Prime + Bunny3 → Reboot | Reboot | No — content |
| 7 | Three wrenches | All mods | No — ecosystem feature |
| 1 | ConditionalAppearanceFeature | HZLib | No — replaces dye system + biome selection |
| 2 | Animation improvements | LovelyLib | No — post-launch polish |
| 11 | Data pipeline / EntityDataSchema | HZLib + LovelyLib | No — requires ADR first |
| 12 | Two-lane appearance architecture + CompositeAppearanceFeature | HZLib | No — requires ADR first |

---

## Notes

- Item 1 (ConditionalAppearanceFeature) has its ADR: `docs/development/decisions/ADR_020_Conditional_Appearance_Feature.md`. It also covers the base condition/context framework (absorbs former ADR 021) and closes item 9 (Tribute texture audit).
- Item 11 (Data Pipeline) has its ADR: `docs/development/decisions/ADR_019_Entity_Data_Pipeline.md`.
- Item 12 (Two-lane appearance + CompositeAppearanceFeature) has its ADR: `docs/development/decisions/ADR_021_Composite_Appearance_Feature.md`. It also introduces `AbstractVariantFeature` eliminating ~600 lines of duplication across the three independent-axis features, and renames `AppearanceVariantFeature` to `CompositeAppearanceFeature`.
- Items 4 and 5 both require Bunny3 — implement the shared `RobotVariant.Bunny3` entry once and reuse it across Legacy and Reboot.
- Item 5 (Hyperion, Empyrium, Prime) are direct ports from 1.20.4 — the archive at `archive/1.20.X/rlovelyr-1.20.4/` has the full texture index, model paths, and variant mapping already worked out. No design decisions are needed, just a faithful port plus a stats balance pass.
- The wrench conversion logic (item 7) depends on having a stable family-to-family mapping table, which is easier to define once all families are finalized (after items 4 and 5).
