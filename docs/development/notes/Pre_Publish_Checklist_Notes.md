# Pre-Publish Checklist — Lovely Recreations

**Status**: Active — Working Notes  
**Last Updated**: 2026-07-01  
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

### 2. Animation System Unification

**What**: Fully unify the animation pipeline under HZLib. LovelyLib registers family
descriptors and conditions; HZLib drives all bone manipulation, idle slot evaluation,
and bone visibility. Eliminate nine architectural smells found by the pre-publish audit.

**Audit**: `docs/development/notes/Animation_Architecture_Audit.md`

**Root cause of all smells**: `RobotFamily.configureVariants()` never attaches an
`AnimationProfile` to the `StandardAnimatorVariant` registration. Because of this, the
entire profile-aware path in `AnimationStateManager` is dead for every robot. All robots
fall back to the hardcoded `isInSittingPose()` branch — the source of the standby flicker
(item 3). Items 2 and 3 are the same problem.

**Six changes (see ADR 022 for full specification):**

**A — `BoneVisibilityFeature` in HZLib** (new feature on `NativeEntityFamily`):
Declarative per-frame bone hiding/showing via `BoneCondition` lambdas. Evaluated inside
`NativeModel.setCustomAnimations()` — no reflection, no model subclasses.
Covers: Kitsune progressive tail unlock, Sentry wing-bone conflict (dragon/honey forms),
any future conditional bone.

**B — `headBoneName` on `NativeEntityFamily`**:
`NativeModel.setCustomAnimations()` reads bone name from the family. Closes the
hardcoded `"head"` string in `NativeModel`.

**C — `AnimationProfile` attached to every `RobotFamily` variant (root fix)**:
Every `StandardAnimatorVariant` registration in `RobotFamily.configureVariants()` must
include a profile. Without this, the entire profile-aware code path is dead for robots.

**D — `IdleSlot` + `IdleCondition` in HZLib + `idleStationaryTicks` on `NativeEntity`**:
Extends `AnimationProfile` with timed conditional idle slots. `NativeEntity` maintains
a non-synced `idleStationaryTicks` counter. `AnimationStateManager.resolveIdleSlot()`
evaluates slots in priority order — the winning slot is structurally stable, eliminating
the flicker. `handleStandbyAnimation()` and its two timer fields are deleted entirely.
Tribute families declare no sit slot — they never show rest or sit anywhere, including
in vehicles (vehicle path updated to return `IDLE` when no sit/ride pool declared).

**E — Delete dead code across LovelyLib (all 3 loaders)**:
- `RobotAnimation.java` — duplicates `NativeAnimation`; deleted, callers updated to `NativeAnimation`
- `KitsuneModel.java` — only existed to call tail reflection; replaced by `BoneVisibilityFeature`
- `EntityAnimation.java`, `EntityModel.java`, `EntityVariantModel.java` — pre-profile dead enums
- `lovelylib/Common/.../api/animation/BoneTransformations.java` — dead duplicate of HZLib version
- `TailAnimationUtils.configureTailVisibility()` — reflection removed; logic moves to `BoneVisibilityConditions`

**F — Item 8 (Tribute AI goals)**: See item 8 below.

**Current state (smells confirmed by audit)**:
- `TailAnimationUtils.configureTailVisibility()` uses Java reflection to call `getBone`/`setHidden` through the loader boundary
- `RobotFamily.configureVariants()` registers `StandardAnimatorVariant` with no profile — all robots are on the dead fallback path
- `RobotAnimation` duplicates `NativeAnimation` across 3 loaders; should not exist
- `KitsuneModel` subclass exists only because bone visibility had no declarative home
- `EntityAnimation`, `EntityModel`, `EntityVariantModel` enums are unreferenced dead code
- `lovelylib/Common/BoneTransformations.java` is an unreferenced duplicate of the HZLib version

**What to do** (20-step ordered plan in ADR):
1. Add `BoneCondition`, `BoneRule`, `BoneVisibilityFeature` to HZLib
2. Update `NativeModel.setCustomAnimations()` to evaluate feature + read head bone name
3. Add `headBoneName` field to `NativeEntityFamily`
4. Add `IdleCondition`, `IdleSlot` to HZLib animation package
5. Add `idleSlots` + `idleSlot()` builder to `AnimationProfile`
6. Add `idleStationaryTicks` counter to `NativeEntity.tick()`
7. Update `AnimationStateManager.getLocomotionAnimation()` — idle slot path + vehicle fallback fix
8. Add `onIdleSlotChanged()` hook to `NativeEntity`; override in `RobotEntity` for hitbox sync
9. Build `ROBOT_BASE_PROFILE` with idle slots; attach in `RobotFamily.configureVariants()`
10. Build `TRIBUTE_PROFILE` (no sit/rest/idle-slots); attach in `TributeRobotFamilies`
11. Declare `BoneVisibilityFeature` on Kitsune family (replaces `TailAnimationUtils` reflection)
12. Declare `BoneVisibilityFeature` on Sentry family (wing-bone conflict — when Sentry is built)
13. Delete `handleStandbyAnimation()` + timer fields from `RobotEntity`; mark schema fields `@Deprecated`
14. Delete `RobotAnimation.java`; update `registerControllers()` to use `NativeAnimation`
15. Delete `KitsuneModel.java`; update Kitsune renderer registration to `NativeRobotModel`
16. Delete `TailAnimationUtils` reflection method + `TailVisibilityConfig`
17. Delete `lovelylib/Common/BoneTransformations.java`
18. Delete `EntityAnimation.java`, `EntityModel.java`, `EntityVariantModel.java`
19. Add `AiTributeReturnToBaseGoal` class
20. Override `registerGoals()` in `TributeRobotEntity` (all loaders)

**ADR**: `docs/development/decisions/ADR_022_Animation_System_Unification.md`

**Locations**:
- HZLib: `sources/common/hzlib-1.21.1/Common/.../animation/` (new types) + `NativeEntity.java` + `NativeModel.java`
- LovelyLib Common: `RobotFamily.java`, `RobotEntity.java`, new `BoneVisibilityConditions.java`, new `AiTributeReturnToBaseGoal.java`
- LovelyLib Loaders: delete `RobotAnimation.java`, `KitsuneModel.java`; update `registerControllers()`

---

### 3. Fix: Standby Mode Animation Flicker

**Status**: Subsumed by item 2. See `docs/development/decisions/ADR_022_Animation_System_Unification.md` — Change C (attach `AnimationProfile`) and Change D (idle slot system) together eliminate the flicker structurally. There is no short-term patch; implement item 2 in full.

**Root cause (confirmed by audit)**: `RobotFamily.configureVariants()` registers
`StandardAnimatorVariant` with no `AnimationProfile`, so `AnimationStateManager.resolveProfile()`
returns `null` for every robot. The system falls back to the `isInSittingPose()` branch which is
driven server-side by `handleStandbyAnimation()` via `SynchedEntityData`. Packet lag between the
server setting `IS_IN_SITTING_POSE` and the client reading it in the GeckoLib controller tick causes
the flicker. Once an `AnimationProfile` is attached (item 2, Change C), the entire `isInSittingPose()`
path is bypassed. Once idle slots are active (item 2, Change D), `handleStandbyAnimation()` is deleted.

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

**What**: Tribute robots should behave similarly to the original LovelyRobot wolf-inspired AI.

**Current state**: `TributeRobotEntity` inherits `registerGoals()` from `RobotEntity`, which
gives it the full Legacy/Reboot goal set including `AiBaseDefenseGoal` (the PATROL→GUARD state
machine with scan patterns). The original mod had no patrol cycle — base defense was simply
"navigate back to `BASE_X/Y/Z`". The original wander used vanilla `EntityAIWanderAvoidWater`
with no owner-stationary detection. `AiPatrolGoal` does not exist in the original source
(`temp/original/`) and should not be created.

**What to do** (covered by ADR 022, Change F):
- Add `AiTributeReturnToBaseGoal` — direct translation of original `EntityAIBunnyFollowPoint`:
  activates in `Defense` state, paths to `BASE_X/Y/Z`, teleports on pathfinding failure past
  warp range. No patrol cycle, no scan patterns.
- Override `registerGoals()` in `TributeRobotEntity` (all three loaders):
  - Uses `AiTributeReturnToBaseGoal` instead of `AiBaseDefenseGoal`
  - Uses vanilla `WaterAvoidingRandomStrollGoal` instead of `AiConditionalWanderGoal`
  - All other goals (`AiFollowOwnerGoal`, look goals, target selectors) unchanged
- This override is part of item 2's step 19–20 in the implementation order.

**ADR**: `docs/development/decisions/ADR_022_Animation_System_Unification.md` — Change F

**Location**: `sources/common/lovelylib-1.21.1/Common/.../entity/goal/AiTributeReturnToBaseGoal.java`
+ all three loader copies of `TributeRobotEntity.java`

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
| 2+3 | Animation system unification (ADR 022) | HZLib + LovelyLib | Yes — flicker visible immediately; profile missing means animation system is broken for all robots |
| 10 | Spawn item tooltip | LovelyLib | Yes — first thing players check |
| 9 | Tribute original textures audit | Tribute | Yes — correctness requirement |
| 8 | Tribute AI goals (ADR 022, Change F) | Tribute | No — part of item 2 implementation steps 19–20 |
| 6 | Collar conditional visibility | LovelyLib | No — polish |
| 4 | Bunny3 → Legacy | Legacy | No — content |
| 5 | Hyperion, Empyrium, Prime + Bunny3 → Reboot | Reboot | No — content |
| 7 | Three wrenches | All mods | No — ecosystem feature |
| 1 | ConditionalAppearanceFeature | HZLib | No — replaces dye system + biome selection |
| 11 | Data pipeline / EntityDataSchema | HZLib + LovelyLib | No — requires ADR first |
| 12 | Two-lane appearance architecture + CompositeAppearanceFeature | HZLib | No — requires ADR first |

---

## Notes

- Item 1 (ConditionalAppearanceFeature) has its ADR: `docs/development/decisions/ADR_020_Conditional_Appearance_Feature.md`. It also covers the base condition/context framework (absorbs former ADR 021) and closes item 9 (Tribute texture audit).
- **Items 2 and 3 are the same problem**, fully resolved by `docs/development/decisions/ADR_022_Animation_System_Unification.md`. The root cause: `RobotFamily.configureVariants()` never attaches an `AnimationProfile` — all nine animation smells trace back to this missing wire. The ADR specifies six changes (A–F) and a 20-step implementation order. The audit findings are preserved in `docs/development/notes/Animation_Architecture_Audit.md`.
- **Item 8 (Tribute AI goals)** is covered by ADR 022 Change F. The original mod had no `AiPatrolGoal` — "patrol" was point-return via `EntityAIBunnyFollowPoint`, now `AiTributeReturnToBaseGoal`. Item 8 is steps 19–20 of the item 2 implementation order.
- Item 11 (Data Pipeline) has its ADR: `docs/development/decisions/ADR_019_Entity_Data_Pipeline.md`.
- Item 12 (Two-lane appearance + CompositeAppearanceFeature) has its ADR: `docs/development/decisions/ADR_021_Composite_Appearance_Feature.md`. It also introduces `AbstractVariantFeature` eliminating ~600 lines of duplication across the three independent-axis features, and renames `AppearanceVariantFeature` to `CompositeAppearanceFeature`.
- Items 4 and 5 both require Bunny3 — implement the shared `RobotVariant.Bunny3` entry once and reuse it across Legacy and Reboot.
- Item 5 (Hyperion, Empyrium, Prime) are direct ports from 1.20.4 — the archive at `archive/1.20.X/rlovelyr-1.20.4/` has the full texture index, model paths, and variant mapping already worked out. No design decisions are needed, just a faithful port plus a stats balance pass.
- The wrench conversion logic (item 7) depends on having a stable family-to-family mapping table, which is easier to define once all families are finalized (after items 4 and 5).
