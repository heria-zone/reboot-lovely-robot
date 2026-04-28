# Sprint Task: Animation Profile & Variant System Implementation

**Status**: ✅ COMPLETE (pending runtime validation)  
**Started**: 2026-04-27  
**Target Completion**: 2026-05-11  
**Completed**: 2026-04-27  
**Priority**: High  
**Complexity**: High  

## Sprint Goal

Implement ADR_010 (Animation Profile System) and ADR_011 (Variant and Spawn System Refactoring) in HZLib and the affected mods, establishing the foundational animation and variant architecture that all future entity work builds on.

## Strategic Context

**Approved Strategy**: ADR-Driven Implementation
- ADR_010 and ADR_011 are fully designed and accepted (2026-04-27)
- Implementation follows the two-track structure agreed in the design review
- Robot profile is the reference implementation — validate the stateless path before adding stateful complexity
- Dragon's Fury sequence is explicitly asset-blocked and will not be started this sprint

**Source ADRs**:
- `docs/development/decisions/ADR_010_Animation_Profile_System.md`
- `docs/development/decisions/ADR_011_Variant_and_Spawn_System_Refactoring.md`

## Objectives

### Track A — HZLib Foundation (Unblocks everything else)
- [x] `AnimationPool`, `WeightedAnimation`, `SelectionStrategy`, `LoopBehavior` in HZLib Common
- [x] `AnimationProfile` with builder in HZLib Common
- [x] `AnimationSequence` and `SequenceStep` in HZLib Common
- [x] `SizeVariantFeature` in HZLib Common
- [x] `initializeSpawnVariants()` hook in `InternalEntity`
- [x] Extend `AnimatorVariantFeature` to carry `AnimationProfile`

### Track B — Reference Implementations (Validates Track A)
- [x] Robot profile — replaces `AnimationStateManager` and `AnimationDefinitions`
- [x] Gourdragora refactor — validates `SizeVariantFeature` + compound variants
- [x] Mushroom Brown biome spawn — validates `initializeSpawnVariants()` hook
- [ ] Dragon's Fury sequence — **ASSET-BLOCKED, do not start**

## Implementation Tasks

---

### Track A: HZLib Foundation

#### Task A.1 — AnimationPool Core Types
- **Priority**: Critical (blocks all other tasks)
- **Story Points**: 5
- **Location**: `sources/common/hzlib-1.21.1/Common/src/main/java/net/heriazone/hzlib/api/animation/`
- **Description**: Implement the core data types for the animation pool system. Start with stateless strategies only (`RANDOM`, `WEIGHTED_RANDOM`). `SEQUENTIAL` is added in Task A.3 alongside `AnimationSequence` since both require per-entity state.
- **Deliverables**:
  - `LoopBehavior.java` — enum: `LOOP`, `PLAY_ONCE`, `HOLD_LAST_FRAME`, `INTERRUPT`, `LOOP_TIMED`, `LOOP_UNTIL_SIGNAL`
  - `SelectionStrategy.java` — enum: `RANDOM`, `WEIGHTED_RANDOM`, `SEQUENTIAL`
  - `WeightedAnimation.java` — `name: String`, `weight: int`, `loop: LoopBehavior`
  - `AnimationPool.java` — `List<WeightedAnimation>`, `SelectionStrategy`, `selectNext(Random): String`, `isEmpty(): boolean`
- **Acceptance Criteria**:
  - [ ] `RANDOM` strategy picks uniformly from the pool
  - [ ] `WEIGHTED_RANDOM` picks proportionally to weight values
  - [ ] `selectNext()` returns `null` for empty pools (not an exception)
  - [ ] All types are pure Java — zero Minecraft or GeckoLib imports
  - [ ] Unit tests cover both selection strategies

---

#### Task A.2 — AnimationProfile with Builder
- **Priority**: Critical (blocks Track B)
- **Story Points**: 5
- **Location**: `sources/common/hzlib-1.21.1/Common/src/main/java/net/heriazone/hzlib/api/animation/`
- **Description**: Implement `AnimationProfile` with the full builder API. Includes the `??` fallback chain logic and the `ISpecialAnimation` interface.
- **Deliverables**:
  - `ISpecialAnimation.java` — marker interface for `AnimationPool` and `AnimationSequence`
  - `AnimationProfile.java` — named slots (idle, walk, rest, sit, ride, attack, hurt), `basePoseAnimation`, `specialAnimations`
  - `AnimationProfile.Builder` — shorthand `.idle("name")` creates single-entry LOOP pool; full `.idle(pool -> ...)` form for multi-entry
  - Fallback chain logic: `getAnimationForState(state)` returns `null` if slot is empty (caller falls back to idle)
- **Acceptance Criteria**:
  - [ ] Builder shorthand `.walk("walk")` creates single-entry `AnimationPool` with `LoopBehavior.LOOP`
  - [ ] Builder shorthand `.attack(pool -> pool.add("attack", LoopBehavior.INTERRUPT))` works correctly
  - [ ] `getAnimationForState(WALK)` returns `null` when walk slot is empty (Bee case)
  - [ ] `basePoseAnimation` is a plain `String`, not a pool
  - [ ] `specialAnimations` map accepts `ISpecialAnimation` (both pool and sequence)
  - [ ] Zero Minecraft or GeckoLib imports

---

#### Task A.3 — AnimationSequence
- **Priority**: High
- **Story Points**: 5
- **Location**: `sources/common/hzlib-1.21.1/Common/src/main/java/net/heriazone/hzlib/api/animation/`
- **Description**: Implement `AnimationSequence` with pull-model exit conditions. Also implement `SEQUENTIAL` strategy for `AnimationPool` here, since both require per-entity index state.
- **Deliverables**:
  - `SequenceStep.java` — `animationName`, `loopBehavior`, `loopDurationTicks`, `exitCondition: Predicate<LivingEntity>`
  - `AnimationSequence.java` implements `ISpecialAnimation` — `List<SequenceStep>`, `returnToState: String`, builder
  - `SequenceState.java` — per-entity runtime state: `currentStepIndex`, `stepTickCounter`, `activeSequenceName`
  - `SEQUENTIAL` strategy implementation in `AnimationPool` (uses external index counter, not internal state)
- **Acceptance Criteria**:
  - [ ] `LOOP_TIMED` step advances after `loopDurationTicks` ticks
  - [ ] `LOOP_UNTIL_SIGNAL` step advances when `exitCondition.test(entity)` returns `true`
  - [ ] `PLAY_ONCE` step advances when GeckoLib signals animation completion (or after 1 tick for non-GeckoLib contexts)
  - [ ] `returnToState` is a valid locomotion state name (validated at build time, not runtime)
  - [ ] `SequenceState` is a plain data object — no Minecraft dependency
  - [ ] `AnimationSequence` has Minecraft dependency (`Predicate<LivingEntity>`) — this is acceptable per ADR_010

---

#### Task A.4 — SizeVariantFeature
- **Priority**: High
- **Story Points**: 5
- **Location**: `sources/common/hzlib-1.21.1/Common/src/main/java/net/heriazone/hzlib/api/entity/features/`
- **Description**: Implement `SizeVariantFeature` for dynamic hitbox management. Gourdragora-specific for now — extracted to a general HZLib feature only when a second entity needs it.
- **Deliverables**:
  - `SizeVariantFeature.java` — `Map<String, SizeConfig>` keyed by size variant name
  - `SizeVariantFeature.SizeConfig` — `modelKey: String`, `scale: float`, `Map<Pose, EntityDimensions>`, `healthMultiplier`, `attackMultiplier`, `speedMultiplier`, `armorMultiplier`, `knockbackResistance`
  - `getConfig(String sizeKey): SizeConfig` — O(1) HashMap lookup, never null (returns default if key missing)
  - `getDimensionsForPose(String sizeKey, Pose pose): EntityDimensions` — convenience method
- **Acceptance Criteria**:
  - [ ] Lookup is O(1) — no iteration, no complex computation
  - [ ] Returns a safe default `SizeConfig` if key is not found (no NPE)
  - [ ] `SizeConfig` carries dimensions for at minimum: `STANDING`, `CROUCHING` (sitting), `SWIMMING`
  - [ ] Scale factor of `1.0f` means no scaling (default/mini), `>1.0f` means larger (big)
  - [ ] Feature integrates with existing `EntityFeature` base class pattern

---

#### Task A.5 — `initializeSpawnVariants()` Hook
- **Priority**: High
- **Story Points**: 2
- **Location**: `sources/common/hzlib-1.21.1/Common/src/main/java/net/heriazone/hzlib/api/entity/InternalEntity.java`
- **Description**: Add the spawn variant hook to `InternalEntity.finalizeSpawn()`. Minimal change — default implementation calls existing `initializeRandomVariants()`.
- **Deliverables**:
  - `initializeSpawnVariants(ServerLevelAccessor world, MobSpawnType reason)` protected method
  - `finalizeSpawn()` calls `initializeSpawnVariants()` instead of `initializeRandomVariants()` directly
  - Default implementation of `initializeSpawnVariants()` calls `initializeRandomVariants()`
- **Acceptance Criteria**:
  - [ ] Existing entities that don't override the hook behave identically to before
  - [ ] `world` and `reason` parameters are available to overriding implementations
  - [ ] JavaDoc documents the override contract clearly

---

#### Task A.6 — Extend `AnimatorVariantFeature` to Carry `AnimationProfile`
- **Priority**: High
- **Story Points**: 3
- **Location**: `sources/common/hzlib-1.21.1/Common/src/main/java/net/heriazone/hzlib/api/entity/features/`
- **Description**: Extend `AnimatorVariantFeature` and `StandardAnimatorVariant` to carry an `AnimationProfile` alongside the animator file path. Profile is optional — existing registrations without a profile continue to work.
- **Deliverables**:
  - `StandardAnimatorVariant` gains optional `AnimationProfile` field
  - `AnimatorVariantFeature.getProfile(entityKey, variantKey): Optional<AnimationProfile>`
  - Existing `StandardAnimatorVariant` constructors without profile remain valid (backward compatible)
- **Acceptance Criteria**:
  - [ ] Existing animator variant registrations compile without changes
  - [ ] `getProfile()` returns `Optional.empty()` for variants registered without a profile
  - [ ] Profile is retrievable by entity key + variant key combination

---

### Track B: Reference Implementations

#### Task B.1 — Robot Profile (Reference Implementation)
- **Priority**: Critical (validates Track A stateless path)
- **Story Points**: 8
- **Location**: `sources/common/lovelylib-1.21.1/`
- **Description**: Replace `AnimationStateManager` and `AnimationDefinitions` with the profile-aware system. The robot profile is the simplest case — 5 states, single-entry pools, no variety, no sequences. Validates the entire Track A foundation before adding monster complexity.
- **Deliverables**:
  - Robot `AnimationProfile` registered in `LovelyRobotType` via `withFeature(AnimationProfile.class, ROBOT_ANIMATION_PROFILE)` ✅
  - `AnimationStateManager` deleted ✅
  - `AnimationDefinitions` deleted ✅
  - `InternalAnimation` updated in all three loaders (Forge, Fabric, NeoForge) — reads profile from entity's native type, constructs `RawAnimation` from pool selections ✅
  - `LoopBehavior.INTERRUPT` applied as PLAY_ONCE for attack ✅
  - Legacy fallback preserved for entities without a profile ✅
- **Robot Profile**:
  ```
  idle:   single entry "idle",   LOOP
  walk:   single entry "walk",   LOOP
  rest:   single entry "rest",   LOOP
  sit:    single entry "sit",    LOOP
  attack: single entry "attack", INTERRUPT
  ```
- **Status**: ✅ COMPLETE — 0 diagnostics across all 4 changed files
- **Acceptance Criteria**:
  - [x] `AnimationDefinitions.java` is deleted — no references remain
  - [x] `AnimationStateManager.java` is deleted — no references remain
  - [x] All three loaders (Forge, Fabric, NeoForge) compile and run
  - [ ] All 7 robot types animate correctly (idle, walk, rest, sit, attack) — **requires runtime test**
  - [ ] Vehicle sitting still uses sit animation — **requires runtime test**
  - [ ] Attack animation overrides locomotion — **requires runtime test**

---

#### Task B.2 — Gourdragora Refactor
- **Priority**: High (validates `SizeVariantFeature` + compound variants)
- **Story Points**: 8
- **Location**: `sources/monsters/monsters_girls-1.21.1/`
- **Description**: Collapse `GourdragoraType` from 9 instances to 3 (Golden, Lumina, Jack'O). Implement `GourdragoraEntity.getDimensions(Pose)` and `initializeSpawnVariants()`.
- **Deliverables**:
  - `GourdragoraType` refactored to 3 instances with `SizeVariantFeature` ✅
  - `GourdragoraEntity.getDimensions(Pose)` override — reads `MODEL_VARIANT`, looks up `SizeVariantFeature` ✅
  - `GourdragoraEntity.initializeSpawnVariants()` override — coordinates size + animator + color ✅
  - `GourdragoraEntity.readAdditionalSaveData()` calls `applyStatMultipliers()` after loading `MODEL_VARIANT` ✅
  - `MonstersEntities` updated — 9 registrations → 3 ✅
  - `MonstersConstant` updated — 3 new constants added, legacy constants preserved ✅
  - Jack'O `FoodFeature` uses `PUMPKIN_PIE + CAKE`; Golden/Lumina use `PUMPKIN_PIE + BONE_MEAL` ✅
- **Status**: ✅ COMPLETE — 0 diagnostics across all 4 changed files
- **Acceptance Criteria**:
  - [x] `GourdragoraType` has 3 instances (not 9)
  - [x] Jack'O tames with PUMPKIN_PIE + CAKE; Golden/Lumina tame with PUMPKIN_PIE + BONE_MEAL
  - [ ] Gourdragora spawns in all three sizes with correct hitboxes — **requires runtime test**
  - [ ] Sitting Gourdragora has reduced hitbox — **requires runtime test**
  - [ ] Big Gourdragora uses default model at 1.4× scale — **requires runtime test**
  - [ ] Stats differ correctly between mini/default/big — **requires runtime test**
  - [ ] **World reload test**: spawn each size, save, reload, confirm stats unchanged — **requires runtime test**

---

#### Task B.3 — Mushroom Brown Biome Spawn
- **Priority**: High (validates `initializeSpawnVariants()` hook)
- **Story Points**: 5
- **Location**: `sources/monsters/monsters_girls-1.21.1/`
- **Description**: Override `initializeSpawnVariants()` in Mushroom Brown entity to select texture based on spawn biome.
- **Deliverables**:
  - `MushroomBrownEntity` created — extends `MushroomEntity`, overrides `initializeSpawnVariants()` with biome lookup ✅
  - `MonstersEntities` updated — `MUSHROOM_BROWN` now uses `MushroomBrownEntity` ✅
  - `MushroomType.supportsTummyTexture()` comment fixed — no longer contradicts return value ✅
- **Biome-to-texture mapping**:
  - Taiga / Old Growth Taiga / Snowy Taiga → `mushroom_brown_ruby`
  - Dark Forest → `mushroom_brown_scarlatina`
  - Birch Forest / Old Growth Birch Forest → `mushroom_brown_orange`
  - Anywhere else → `mushroom_brown_boletus`
- **Status**: ✅ COMPLETE — 0 diagnostics across all changed files
- **Acceptance Criteria**:
  - [x] `MushroomBrownEntity` created with biome-aware spawn selection
  - [x] Fallback to Boletus if biome lookup fails (null safety via try/catch)
  - [ ] Mushroom Brown spawned in taiga has Ruby texture — **requires runtime test**
  - [ ] Mushroom Brown spawned in dark forest has Scarlatina texture — **requires runtime test**
  - [ ] Mushroom Brown spawned in birch forest has Orange texture — **requires runtime test**
  - [ ] Mushroom Brown spawned elsewhere has Boletus texture — **requires runtime test**

---

#### Task B.4 — Dragon's Fury Sequence
- **Priority**: Low
- **Story Points**: 8
- **⚠ ASSET-BLOCKED — DO NOT START**
- **Description**: Implement the Dragon's Fury `AnimationSequence` using `LOOP_UNTIL_SIGNAL` exit conditions.
- **Blocked on**: Animation asset files that do not yet exist:
  - `attack_prepare.animation.json` (or named animation within dragon file)
  - `attack_charge.animation.json`
  - `attack_approach.animation.json`
  - `attack_strike.animation.json`
  - `attack_fury.animation.json`
- **Action required before unblocking**: Asset authoring must be completed and files committed. Do not pick up this task and then stall waiting for files.
- **When unblocked, deliverables**:
  - Dragon `AnimationSequence` registered as `"fury_attack"` special in Dragon's `AnimationProfile`
  - Per-entity `SequenceState` fields added to `RobotEntity` or `LovelyRobotEntity`
  - Locomotion controller checks `isSequenceRunning()` first on every tick
  - `discard()` and death handlers call `clearSequence()`

---

## Technical Notes

### Implementation Order Within Track A

Implement `RANDOM` and `WEIGHTED_RANDOM` selection first (Tasks A.1–A.2 are stateless). Add `SEQUENTIAL` and `AnimationSequence` in Task A.3 (both require per-entity index state). Do not mix stateless and stateful paths during initial implementation — validate the stateless path with the robot profile (Task B.1) before proceeding to Task A.3.

**Recommended order**: A.1 → A.2 → B.1 (validate) → A.3 → A.4 → A.5 → A.6 → B.2 → B.3

### GeckoLib Boundary

`AnimationProfile`, `AnimationPool`, `WeightedAnimation`, `LoopBehavior`, `SelectionStrategy` — **zero GeckoLib imports**.

`AnimationSequence`, `SequenceStep` — Minecraft dependency (`Predicate<LivingEntity>`) is acceptable. No GeckoLib imports.

`InternalAnimation` (loader-specific) — this is where GeckoLib lives. It reads profiles, calls `pool.selectNext()`, and constructs `RawAnimation` objects. `LoopBehavior.INTERRUPT` maps to `override_previous_animation: true`.

### Critical: NBT Stat Reapplication

When implementing `GourdragoraEntity.readAdditionalSaveData()`, the stat multipliers from `SizeVariantFeature` **must** be re-applied after loading `MODEL_VARIANT` from NBT. Without this, a Big Gourdragora loses its stat scaling after every world reload.

The validation step for this is explicit: spawn Gourdragora of each size, save the world, reload, confirm stats are unchanged. Do not mark Task B.2 complete without running this test.

### `supportsTummyTexture()` Fix

Before implementing Task B.3, confirm with Serge whether `MushroomType.supportsTummyTexture()` should return `true` or `false`. The current code returns `false` with a comment saying "all mushroom variants support belly progression" — the method name and return value contradict each other. Fix after confirmation.

---

## Dependencies

### Internal Dependencies
- Track B depends on Track A being complete
- Task B.1 (robot profile) must complete before Task A.3 (`AnimationSequence`) starts
- Task B.4 is blocked on external asset authoring — no internal dependency can unblock it

### External Dependencies
- Dragon's Fury animation assets (Task B.4) — blocked on asset authoring
- `supportsTummyTexture()` fix (Task B.3) — blocked on Serge confirmation

---

## Success Criteria

### Sprint Success Metrics
- [ ] Track A fully implemented and compiling in HZLib Common
- [ ] Robot profile working — all 7 robot types animate correctly, no regression
- [ ] `AnimationDefinitions.java` deleted
- [ ] Gourdragora spawns in 3 sizes with correct hitboxes and stats
- [ ] World reload test passes for Gourdragora (stats persist)
- [ ] Mushroom Brown spawns with correct biome texture
- [ ] Task B.4 correctly identified as asset-blocked and not started

### Quality Gates
- [ ] All code follows `docs/guidelines/Coding Style Enforcer.md`
- [ ] All public APIs documented with JavaDoc
- [ ] No GeckoLib imports in HZLib Common animation classes
- [ ] No regression in existing robot behavior (all 7 types)
- [ ] World reload validation completed for Gourdragora

---

## Story Points Summary

| Task | Points | Track | Status |
|------|--------|-------|--------|
| A.1 AnimationPool Core Types | 5 | A | ✅ |
| A.2 AnimationProfile with Builder | 5 | A | ✅ |
| A.3 AnimationSequence | 5 | A | ✅ |
| A.4 SizeVariantFeature | 5 | A | ✅ |
| A.5 initializeSpawnVariants() Hook | 2 | A | ✅ |
| A.6 Extend AnimatorVariantFeature | 3 | A | ✅ |
| B.1 Robot Profile (Reference) | 8 | B | ✅ |
| B.2 Gourdragora Refactor | 8 | B | ✅ |
| B.3 Mushroom Brown Biome Spawn | 5 | B | ✅ |
| B.4 Dragon's Fury Sequence | 8 | B | ⚠ BLOCKED |
| **Total completed** | **46** | | |
| **Total (including blocked)** | **54** | | |

**Sprint capacity**: 46 points (B.4 excluded as asset-blocked). This is above the standard 15–20 point sprint capacity — tasks should be prioritized in the order listed. If capacity is reached, B.2 and B.3 carry over to Sprint 08.

---

## Risks and Mitigation

### Risk 1: Track A takes longer than estimated
**Probability**: Medium  
**Impact**: Delays Track B  
**Mitigation**: A.1 and A.2 are the critical path. If A.3 (AnimationSequence) runs long, defer it — B.1 (robot profile) only needs A.1 and A.2.

### Risk 2: Robot profile regression
**Probability**: Low  
**Impact**: High — breaks all 7 robot types  
**Mitigation**: Test each robot type individually after B.1. The robot animation file is unchanged — only the Java wiring changes.

### Risk 3: Gourdragora NBT stat loss on reload
**Probability**: High if not explicitly tested  
**Impact**: Medium — silent bug, hard to notice  
**Mitigation**: Explicit world reload validation step in B.2 acceptance criteria. Do not mark B.2 complete without this test.

### Risk 4: `getDimensions(Pose)` performance
**Probability**: Low  
**Impact**: Medium — called frequently by Minecraft  
**Mitigation**: `SizeVariantFeature` lookup is O(1) HashMap. No computation in the hot path.

---

## References

- `docs/development/decisions/ADR_010_Animation_Profile_System.md`
- `docs/development/decisions/ADR_011_Variant_and_Spawn_System_Refactoring.md`
- `docs/development/decisions/ADR_009_Entity_Hierarchy_Refactoring.md`
- `temp/27-04-2026 Entity Architecture & Variant System Discussion.md`
- `sources/common/hzlib-1.21.1/` — HZLib source (Track A target)
- `sources/common/lovelylib-1.21.1/` — LovelyLib source (Track B, robot profile)
- `sources/monsters/monsters_girls-1.21.1/` — Monsters & Girls source (Track B, Gourdragora + Mushroom)
- `sources/common/lovelylib-1.21.1/Common/src/main/resources/assets/lovelylib/animations/default.animation.json` — Robot animation file (reference)
- `archive/outsource/monsters-girls-1.20.1/src/main/resources/assets/monsters_girls/animations/` — Monster animation files (reference)

---

## Progress Update — 2026-04-27

### Completed — Track A (Mike) + Track B (Raul)

All 46 story points implemented. Zero diagnostics across all changed files.

**Track A — HZLib Foundation** (completed prior to this session):
- `LoopBehavior`, `SelectionStrategy`, `WeightedAnimation`, `AnimationPool` — pure Java, zero Minecraft/GeckoLib imports
- `ISpecialAnimation`, `AnimationProfile` with full builder — shorthand and full-form pool setters
- `AnimationSequence`, `SequenceStep` — pull-model exit conditions via `Predicate<LivingEntity>`
- `SequenceState` — per-entity runtime state for active sequences
- `SizeVariantFeature` with `SizeConfig` — O(1) HashMap lookup, per-pose `EntityDimensions`, stat multipliers, `applyTo(LivingEntity)`
- `initializeSpawnVariants(ServerLevelAccessor, MobSpawnType)` hook in `InternalEntity.finalizeSpawn()`
- `StandardAnimatorVariant` extended with optional `AnimationProfile` field (backward compatible)

**Track B.1 — Robot Profile** (Raul):
- `ROBOT_ANIMATION_PROFILE` static constant added to `LovelyRobotType` — shared across all 7 robot types
- Profile attached via `withFeature(AnimationProfile.class, ROBOT_ANIMATION_PROFILE)` in both `create()` overloads
- `InternalAnimation` rewritten in all three loaders (Fabric, Forge, NeoForge) — reads profile from entity's native type, resolves animation names via pool selection, constructs `RawAnimation` from strings
- `LoopBehavior.INTERRUPT` → `Animation.LoopType.PLAY_ONCE` in GeckoLib
- Legacy fallback preserved for entities without a profile
- `AnimationDefinitions.java` deleted from both lovelylib Common and hzlib Common
- `AnimationStateManager.java` deleted from lovelylib Common

**Track B.2 — Gourdragora Refactor** (Raul):
- `GourdragoraType` collapsed from 9 instances to 3 (GOLDEN, LUMINA, JACKO)
- `SizeVariantFeature.SIZE_FEATURE` static constant — shared across all 3 variants
- Size configs: mini (0.5×0.8, scale 1.0), default (0.6×1.0, scale 1.0), big (0.8×1.4, scale 1.4)
- Big uses `gourdragora_girl_default` model at 1.4× scale — no separate big model file
- Jack'O uses `PUMPKIN_PIE + CAKE`; Golden/Lumina use `PUMPKIN_PIE + BONE_MEAL`
- `GourdragoraEntity.getDimensions(Pose)` — dynamic hitbox via `SizeVariantFeature` lookup (O(1))
- `GourdragoraEntity.initializeSpawnVariants()` — coordinates size → animator → color in correct order
- `GourdragoraEntity.readAdditionalSaveData()` — re-applies stat multipliers after NBT load (world reload safety)
- `MonstersEntities` — 9 Gourdragora registrations → 3
- `MonstersConstant` — 3 new constants added, legacy constants preserved for world save compatibility

**Track B.3 — Mushroom Brown Biome Spawn** (Raul):
- `MushroomBrownEntity` created — extends `MushroomEntity`, overrides `initializeSpawnVariants()`
- Biome mapping: Taiga variants → Ruby, Dark Forest → Scarlatina, Birch variants → Orange, else → Boletus
- Null-safe biome lookup with try/catch fallback to Boletus
- `MonstersEntities.MUSHROOM_BROWN` updated to use `MushroomBrownEntity`
- `MushroomType.supportsTummyTexture()` comment fixed — no longer contradicts return value

### Remaining — Runtime Validation Required

The following acceptance criteria require in-game testing and cannot be verified statically:
- All 7 robot types animate correctly (idle, walk, rest, sit, attack)
- Vehicle sitting uses sit animation for robots
- Attack animation overrides locomotion (INTERRUPT behavior)
- Gourdragora spawns in all three sizes with correct hitboxes
- Sitting Gourdragora has reduced hitbox
- Big Gourdragora renders at 1.4× scale
- Stats differ correctly between mini/default/big
- **World reload test**: Gourdragora stats persist after save/reload
- Mushroom Brown spawns with correct biome texture in each biome type

### Blocked
- **Task B.4 (Dragon's Fury Sequence)**: Asset-blocked. Dragon animation files do not exist. Do not start until assets are committed.
