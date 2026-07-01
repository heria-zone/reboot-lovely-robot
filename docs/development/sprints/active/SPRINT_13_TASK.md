# Sprint Task: Animation System Unification — ADR 022 Full Implementation

**Status**: 🔄 IN PROGRESS — Implementation Complete, Validation Pending  
**Target Start**: 2026-07-26  
**Target Completion**: 2026-08-08  
**Priority**: High  
**Complexity**: High  
**Sprint Number**: 13  
**Change E Completed**: 2026-07-01  
**Change F Completed**: 2026-07-01

## Sprint Goal

Implement the full animation system unification defined in ADR 022. The result is a production-ready
animation architecture that closes the root missing-wire gap (no `AnimationProfile` on robot variants),
eliminates the standby flicker through a declarative `IdleSlot` system, replaces reflection-based
bone visibility with a first-class `BoneVisibilityFeature`, and deletes all pre-profile-era dead code
across three loaders. Pre-publish checklist items 2 (animation improvements) and 3 (standby flicker)
are both resolved by this sprint — they share the same root cause.

## Strategic Context

**Source ADR**: `docs/development/decisions/ADR_022_Animation_System_Unification.md`  
**Prerequisite**: Sprint 11 (Entity Data Pipeline — `NativeEntity`, `NativeEntityFamily` shape finalized) ✅  
**Prerequisite**: Sprint 12 (Condition Framework — `AnimationProfile` builder must accept `idleSlots`) 📋  
**Affects**: HZLib Common, HZLib Loaders (×3), LovelyLib Common, LovelyLib Loaders (×3)  
**Audit Source**: `docs/development/notes/Animation_Architecture_Audit.md`  
**Build dependency**: Step 1–3 (HZLib new types) → Step 4–8 (HZLib wiring) → Step 9–12 (LovelyLib profiles + features) → Step 13–18 (deletions) → Step 19–20 (Tribute AI) → Validation

---

## Objectives

### Change A — `BoneVisibilityFeature` in HZLib (new feature)
> **New first-class feature for declarative, frame-evaluated conditional bone hiding/showing.**

- [x] Create `BoneCondition.java` — `@FunctionalInterface`, method `boolean test(NativeEntity entity)`, package `net.heriazone.hzlib.api.entity.features`
- [x] Create `BoneRule.java` — Java `record` with fields `String boneName`, `BoneCondition condition`, `boolean hideWhenTrue`
- [x] Create `BoneVisibilityFeature.java` — implements `NativeFeature`; holds `List<BoneRule>`; exposes `builder()` factory; `Builder.hideWhen(boneName, condition)` and `Builder.showWhen(boneName, condition)` fluent methods; `build()` returns immutable feature instance
- [x] Update `NativeModel.setCustomAnimations()` (all 3 loaders) — add `applyBoneVisibility()` call after `headAnimation()`; iterate `BoneRule` list; retrieve `GeoBone` via `getAnimationProcessor().getBone()`; evaluate condition; call `bone.setHidden(rule.hideWhenTrue() == conditionMet)`; skip gracefully if bone name not found in model

### Change B — Head bone name on `NativeEntityFamily`
> **Eliminates the hardcoded `"head"` string in `NativeModel.setCustomAnimations()`.**

- [x] Add `headBoneName` field to `NativeEntityFamily` — `private String headBoneName = "head"` (default covers all existing families with zero migration)
- [x] Add `getHeadBoneName()` accessor and `headBone(String n)` fluent setter on `NativeEntityFamily`
- [x] Update `NativeModel.resolveHeadBoneName()` (all 3 loaders) — reads from `animatable.nativeEntity.getHeadBoneName()` when entity reference is non-null; falls back to `"head"` otherwise

### Change C — `AnimationProfile` attached to every `RobotFamily` variant (root fix)
> **Closes the root missing wire. Activates the entire profile-aware animation path for robots.**

- [x] Define `ROBOT_BASE_PROFILE` constant in `RobotFamily` — builder declares `idle("idle")`, `walk("walk")`, `rest("rest")`, `sit("sit")`, `attack(pool → pool.add("attack", LoopBehavior.INTERRUPT))`, plus the two idle slots (see Change D Step 9 below)
- [x] Update `RobotFamily.configureVariants()` — pass `ROBOT_BASE_PROFILE` as the profile argument to every `StandardAnimatorVariant` registration via `buildAnimatorProfile()` hook; `AnimationStateManager.resolveProfile()` now returns non-null for all robot entities
- [x] Define `TRIBUTE_PROFILE` in `RobotFamily` — builder declares `idle("rest")`, `walk("walk")`, `attack(pool → pool.add("attack", LoopBehavior.INTERRUPT"))` only; **no** idle slots; `"rest"` is the upright stand-at-ease pose — Tribute robots hold this indefinitely in Standby and never transition to the floor-sit; used by `TributeRobotFamily` subclass which overrides `buildAnimatorProfile()` to return it; `TributeRobotFamilies` uses `createTribute()` factory to instantiate `TributeRobotFamily` instead of `RobotFamily`

### Change D — `IdleSlot` system in HZLib + `idleStationaryTicks` on `NativeEntity`
> **Replaces `handleStandbyAnimation()` tick-driven logic with a declarative animation-layer idiom. Eliminates the standby flicker structurally.**

- [x] Create `IdleCondition.java` — `@FunctionalInterface` in `net.heriazone.hzlib.api.animation`; method `boolean test(NativeEntity entity)`; JavaDoc notes it must be fast (no world queries, no allocations)
- [x] Create `IdleSlot.java` — final class in `net.heriazone.hzlib.api.animation`; fields: `AnimationPool pool`, `IdleCondition condition`, `int priority` (higher = evaluated first; 0 = fallback), `int activationThresholdTicks`; standard constructor + accessors
- [x] Add `idleSlots` list + `idleSlot()` builder method to `AnimationProfile` — `idleSlot(AnimationPool pool, IdleCondition condition, int priority, int activationThresholdTicks)` appends to list; `getIdleSlots()` returns unmodifiable view; existing `.idle(name)` shorthand implicitly registers a priority-0, always-true, zero-threshold slot (guaranteed fallback)
- [x] Add `idleStationaryTicks` to `NativeEntity` — `private int idleStationaryTicks = 0`; `getIdleStationaryTicks()` / `resetIdleStationaryTicks()` accessors; `tickIdleCounter()` increments each tick entity is not moving and not in vehicle; `currentIdleSlot` tracking field + `getCurrentIdleSlot()` / `setCurrentIdleSlot()` for slot-change detection; `tickIdleCounter()` called from `RobotEntity.tick()`
- [x] Update `AnimationStateManager.getLocomotionAnimation()` — three coordinated changes:
  - (a) Vehicle branch: fall back to `IDLE` (not `SIT` constant) when no `ride` or `sit` pool is declared — Tribute families correctly show idle while riding
  - (b) Add `resolveIdleSlot()` helper — sort idle slots by priority descending; evaluate condition + threshold check in order; call `onIdleSlotChanged` when winner changes; return `IDLE` if no slot matches
  - (c) Keep legacy `isInSittingPose()` branch as unchanged fallback for entities that have no idle slots declared in their profile
- [x] Add `onIdleSlotChanged()` hook to `NativeEntity` — `protected void onIdleSlotChanged(IdleSlot previousSlot, IdleSlot newSlot) {}` (no-op base); called from `resolveIdleSlot()` when the winning slot changes between ticks
- [x] Override `onIdleSlotChanged()` in `RobotEntity` — update `IS_IN_SITTING_POSE` based on whether the new slot's pool contains `"sit"` (the floor-sit animation that requires the smaller hitbox); call `refreshDimensions()` on sit/unsit transition; `handleStandbyAnimation()` replaced with `@Deprecated` no-op
- [x] Attach idle slots to `ROBOT_BASE_PROFILE`:
  - Slot 1: `AnimationPool.single("rest")`, condition `entity.getCurrentState() == EntityState.Standby`, priority `1`, threshold `0` (activates immediately on Standby entry — `"rest"` is the upright stand-at-ease pose)
  - Slot 2: `AnimationPool.single("sit")`, condition `entity.getCurrentState() == EntityState.Standby`, priority `2`, threshold `SharedConfigs.Common.StandbyToSitDelayMin` (overrides `"rest"` after delay — `"sit"` is the floor-sit pose that shrinks the hitbox)

### Change E — Delete `RobotAnimation`, `KitsuneModel`, dead enums, duplicate `BoneTransformations`
> **Consolidates loader-side animation code to match the Monsters & Girls pattern — no duplication of `NativeAnimation`.**

- [x] Delete `RobotAnimation.java` (all 3 loaders) — update `NativeRobotEntity.registerControllers()` and `TributeRobotEntity.registerControllers()` in all loaders to call `NativeAnimation.locomotionAnimation(this)` and `NativeAnimation.attackAnimation(this)` directly
- [x] Delete `KitsuneModel.java` (all 3 loaders) — update Kitsune entity/renderer registration in all loaders to use `NativeRobotModel` instead
- [x] Delete dead enum files from LovelyLib Common: `EntityAnimation.java`, `EntityModel.java`, `EntityVariantModel.java` (all have zero references in active code; pre-profile-era remnants)
- [x] Delete `lovelylib/Common/.../api/animation/BoneTransformations.java` — dead duplicate of `hzlib/.../animation/BoneTransformations.java`; zero references in active LovelyLib source
- [x] Delete `TailAnimationUtils.configureTailVisibility()`, `TailAnimationUtils.calculateTailVisibility()`, and the `TailVisibilityConfig` inner class — logic replaced by `BoneVisibilityFeature` lambda conditions; if no remaining methods survive in `TailAnimationUtils`, delete the entire class
- [x] Delete `handleStandbyAnimation()` from `RobotEntity` and its two timer fields `standbyTicks` + `standbyTargetTicks` — mark `STANDBY_TICKS` and `STANDBY_TARGET_TICKS` constants in `RobotFields` as `@Deprecated` (retained as load-only schema fields so existing saves do not produce migration errors)

### Change F — `BoneVisibilityFeature` Declarations + Tribute AI Goals
> **Wire the new features onto existing and new families; implement checklist item 8.**

- [x] Create `BoneVisibilityConditions.java` in LovelyLib Common (`net.heriazone.lovelylib.common.entity.features`) — factory methods: `tailVisible(int tailIndex)` (level-threshold condition for Kitsune tails), `textureVariantIs(String variantKey)`, `textureVariantIsNot(String variantKey)`
- [x] Declare `BoneVisibilityFeature` on the KITSUNE family in `LegacyRobotFamilies` and `RebootRobotFamilies` — `showWhen` / `hideWhen` rules for `tail0` through `tail09` using `BoneVisibilityConditions.tailVisible(index)`; replaces the reflection path in `TailAnimationUtils.configureTailVisibility()`
- [x] Create `AiTributeReturnToBaseGoal.java` in LovelyLib Common (`net.heriazone.lovelylib.common.entity.goal`) — direct translation of original `EntityAIBunnyFollowPoint`: `canUse()` checks `EntityState.Defense` + `!isOrderedToSit()` + distance >= `minDistance`; `tick()` calls `getLookControl().setLookAt()` and recalculates path every 10 ticks; `tryTeleportToBase()` scans 5×5 for a solid-floor adjacent block; `canContinueToUse()`, `start()`, `stop()` — standard implementations
- [x] Override `registerGoals()` in `TributeRobotEntity` (all 3 loaders) — goal priority order: 1 Float, 2 SitWhenOrderedTo, 3 MeleeAttack, 4 AiFollowOwnerGoal (Follow state), 4 AiTributeReturnToBaseGoal (Defense state), 6 WaterAvoidingRandomStrollGoal (vanilla wander — **not** `AiConditionalWanderGoal`), 7 AiConditionalLookGoal (×2), 8 AiConditionalRandomLookGoal; targetSelector: OwnerHurtByTarget, OwnerHurtTarget, HurtByTarget, AiAutoAttackGoal (Mob/Monster excluding Creeper)

### Validation
> **Confirm each smell is resolved and no regressions introduced.**

- [ ] **Profile activation**: verify `AnimationStateManager.resolveProfile()` returns non-null for a Legacy robot, a Reboot robot, and a Tribute robot after Change C
- [ ] **Standby flicker**: enter Standby mode with a Legacy robot — confirm `rest` animation plays immediately and `sit` transitions in after `StandbyToSitDelayMin` ticks with no frame-to-frame flickering between states
- [ ] **Vehicle riding**: ride a Legacy robot in a vehicle — confirm it plays `sit`; ride a Tribute robot — confirm it plays `idle` (not `sit`)
- [ ] **Kitsune tails**: verify tail bones appear/disappear correctly at each level threshold without reflection; confirm `TailAnimationUtils.configureTailVisibility()` is fully deleted
- [ ] **Head tracking**: confirm head bone tracking still works for all robot families after `resolveHeadBoneName()` reads from family descriptor
- [ ] **Tribute AI**: test Tribute robot in Defense mode — confirm it paths to base coordinates and teleports when blocked; confirm wander behavior matches original (no owner-stationary check)
- [ ] **Loader coverage**: confirm all three loaders (Forge, Fabric, NeoForge) compile cleanly with `RobotAnimation` deleted and `KitsuneModel` deleted
- [ ] **Deprecated fields**: load a save file that contains `STANDBY_TICKS` / `STANDBY_TARGET_TICKS` NBT keys — verify entity loads without error and deprecated fields are silently ignored
- [ ] **Dead code deleted**: verify zero references to `EntityAnimation`, `EntityModel`, `EntityVariantModel`, lovelylib `BoneTransformations`, `handleStandbyAnimation`, `TailVisibilityConfig` remain in the codebase
- [ ] Full build passes across all HZLib and LovelyLib modules — 0 errors, 0 warnings from animation system

---

## Implementation Notes

### Step Ordering Constraint
Steps 1–3 (HZLib new types: `BoneCondition`, `BoneRule`, `BoneVisibilityFeature`, `IdleCondition`, `IdleSlot`) must compile before any LovelyLib work begins. Steps 4–8 (HZLib wiring: `NativeModel`, `AnimationStateManager`, `NativeEntity`) can proceed once new types exist. Deletions (Steps 13–18) are safest last — they confirm every reference has already been replaced.

### Reflection Elimination
`TailAnimationUtils.configureTailVisibility()` uses Java reflection to call `getBone` and `setHidden` because LovelyLib Common cannot import GeckoLib (a loader-specific dependency). `BoneVisibilityFeature` solves this at the design level: the feature is declared in Common with pure-Java lambda conditions; `NativeModel.setCustomAnimations()` in the loader module holds the only GeckoLib call site. No reflection, no `Class.forName`, no method handle workarounds.

### Flicker Root Cause
The standby flicker exists because `handleStandbyAnimation()` mutates `standbyTicks` and `standbyTargetTicks` on every server tick, while the animation controller evaluates `isInSittingPose()` on every client frame. The two clocks are not synchronized. `idleStationaryTicks` eliminates the problem by making the threshold comparison (`>= N`) stable once crossed — the same idle slot wins on every controller tick until the entity moves.

### `STANDBY_TICKS` Schema Retention
Do **not** remove `STANDBY_TICKS` and `STANDBY_TARGET_TICKS` from `RobotFamily.configureSchema()`. They must remain as read-only (load-only) entries so existing save files with those keys do not produce schema warnings or migration errors on first load after the update. Mark them `@Deprecated` in `RobotFields` to signal they are no longer written.

### Tribute Isolation
Tribute's `TRIBUTE_PROFILE` intentionally declares no idle slots. This is not an omission — it faithfully reproduces the original LovelyRobot behavior where robots hold the stand-at-ease (`"rest"`) pose indefinitely and never transition to the floor-sit. The profile uses `idle("rest")` so the `"rest"` animation plays as the Standby idle without any slot machinery. Adding `AiConditionalWanderGoal` instead of `WaterAvoidingRandomStrollGoal` would add the owner-stationary detection that is a Legacy/Reboot-only feature. Tribute gets vanilla wander.

### Animation Name Clarification
The animation names are the authoritative source of truth. The canonical mapping confirmed from the animation file (Blockbench screenshot):

| Animation name | Visual | State context | Hitbox change |
|---|---|---|---|
| `"idle"` | Normal active idle — robot stands still, ready to act | Follow, Defense, any non-Standby state when not walking or attacking | None |
| `"rest"` | **Stand-at-ease / awaiting orders** — upright but relaxed, arms at sides | Standby only — slot 1, threshold `0`, activates immediately | None |
| `"sit"` | **Floor-sit** — robot visually sits down on the ground | Standby only — slot 2, threshold `StandbyToSitDelayMin`, activates after delay | Shrinks hitbox (drives `IS_IN_SITTING_POSE`) |
| `"walk"` | Walking / navigating | Any state | None |
| `"attack"` | Combat swing | Any state | None |

`"idle"` and `"rest"` are both upright, but serve different states: `"idle"` = active non-Standby pause; `"rest"` = Standby stand-at-ease. They are distinct so artists can give them different expressions.

`IS_IN_SITTING_POSE` is set to `true` when the `"sit"` (floor-sit) slot wins — the name is accurate. The hitbox shrinks because the robot is physically lower to the ground.

### Sentry Family Note
Step 12 (declare `BoneVisibilityFeature` on SENTRY family) is listed in ADR 022 but is deferred until the Sentry family is built. The infrastructure (Change A) must be in place for Sentry's future implementation. No Sentry-specific code is written in this sprint.

---

## Story Points

| Change | Steps | Points |
|--------|-------|--------|
| Change A — `BoneVisibilityFeature` (HZLib new types + model wiring) | 4 tasks | 8 |
| Change B — Head bone name on `NativeEntityFamily` | 3 tasks | 3 |
| Change C — `AnimationProfile` on all robot variants (root fix) | 3 tasks | 5 |
| Change D — `IdleSlot` system + `idleStationaryTicks` + `AnimationStateManager` update | 8 tasks | 13 |
| Change E — Delete `RobotAnimation`, `KitsuneModel`, dead enums, duplicate `BoneTransformations` | 6 tasks | 8 |
| Change F — `BoneVisibilityFeature` declarations + Tribute AI goals | 4 tasks | 8 |
| Validation | 10 tasks | 5 |
| **Total** | | **50** |

---

## Files Created / Modified

### HZLib Common — new
- `sources/common/hzlib-1.21.1/Common/.../entity/features/BoneCondition.java`
- `sources/common/hzlib-1.21.1/Common/.../entity/features/BoneRule.java`
- `sources/common/hzlib-1.21.1/Common/.../entity/features/BoneVisibilityFeature.java`
- `sources/common/hzlib-1.21.1/Common/.../animation/IdleCondition.java`
- `sources/common/hzlib-1.21.1/Common/.../animation/IdleSlot.java`

### HZLib Common — modified
- `sources/common/hzlib-1.21.1/Common/.../animation/AnimationProfile.java` — `idleSlots` list + `idleSlot()` builder method + `getIdleSlots()`
- `sources/common/hzlib-1.21.1/Common/.../animation/AnimationStateManager.java` — `getLocomotionAnimation()` vehicle fallback fix + `resolveIdleSlot()` + legacy branch preserved
- `sources/common/hzlib-1.21.1/Common/.../NativeEntity.java` — `idleStationaryTicks` counter + `onIdleSlotChanged()` hook
- `sources/common/hzlib-1.21.1/Common/.../NativeEntityFamily.java` — `headBoneName` field + accessor + fluent setter

### HZLib Loaders — modified (Forge, Fabric, NeoForge)
- `sources/.../NativeModel.java` (×3) — `setCustomAnimations()` + `applyBoneVisibility()` + `resolveHeadBoneName()` reads from family

### LovelyLib Common — new
- `sources/common/lovelylib-1.21.1/Common/.../entity/features/BoneVisibilityConditions.java`
- `sources/common/lovelylib-1.21.1/Common/.../entity/goal/AiTributeReturnToBaseGoal.java`

### LovelyLib Common — modified
- `sources/common/lovelylib-1.21.1/Common/.../entity/RobotFamily.java` — `ROBOT_BASE_PROFILE` with idle slots + `configureVariants()` profile attachment
- `sources/common/lovelylib-1.21.1/Common/.../entity/RobotEntity.java` — `onIdleSlotChanged()` override; `handleStandbyAnimation()` + `standbyTicks` + `standbyTargetTicks` deleted
- `sources/common/lovelylib-1.21.1/Common/.../entity/families/LegacyRobotFamilies.java` — `BoneVisibilityFeature` on KITSUNE
- `sources/common/lovelylib-1.21.1/Common/.../entity/families/RebootRobotFamilies.java` — `BoneVisibilityFeature` on KITSUNE
- `sources/common/lovelylib-1.21.1/Common/.../entity/families/TributeRobotFamilies.java` — `TRIBUTE_PROFILE` attached to Tribute variants
- `sources/common/lovelylib-1.21.1/Common/.../data/RobotFields.java` — `STANDBY_TICKS` + `STANDBY_TARGET_TICKS` marked `@Deprecated`

### LovelyLib Common — deleted
- `sources/common/lovelylib-1.21.1/Common/.../api/animation/EntityAnimation.java`
- `sources/common/lovelylib-1.21.1/Common/.../api/animation/EntityModel.java`
- `sources/common/lovelylib-1.21.1/Common/.../api/animation/EntityVariantModel.java`
- `sources/common/lovelylib-1.21.1/Common/.../api/animation/BoneTransformations.java`
- `sources/common/lovelylib-1.21.1/Common/.../entity/animation/TailAnimationUtils.java` (if no surviving methods remain after `configureTailVisibility` deletion)

### LovelyLib Loaders — modified (Forge, Fabric, NeoForge)
- `sources/.../NativeRobotEntity.java` (×3) — `registerControllers()` → `NativeAnimation` directly
- `sources/.../TributeRobotEntity.java` (×3) — `registerControllers()` → `NativeAnimation` directly; `registerGoals()` override added

### LovelyLib Loaders — deleted (Forge, Fabric, NeoForge)
- `sources/.../RobotAnimation.java` (×3)
- `sources/.../KitsuneModel.java` (×3)
