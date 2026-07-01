# Sprint Planning - LovelyRobot Project (Updated)

**Status**: Active
**Last Updated**: 2026-07-01
**Project**: LovelyRobot Multi-Variant Minecraft Mod Ecosystem
**Related Documents**:
- [CURRENT_STATE.md](CURRENT_STATE.md) - Current implementation status
- [ROADMAP.md](ROADMAP.md) - Strategic roadmap 2025-2026
- [Active Sprints](../development/sprints/active/) - Current sprint tasks
- [Archived Sprints](../development/sprints/archive/) - Completed sprints

---

## Purpose

This document tracks sprint planning, execution, and outcomes for the LovelyRobot project aligned with the 2026 roadmap. It provides a tactical view of development cycles, story point tracking, and sprint retrospectives organized around the major phases: Library Foundation, Multi-Version Expansion, Feature Completion, and Ecosystem Integration.

---

## Sprint Cycle Overview

### Sprint Cadence and Duration
**Standard Sprint Length**: 2 weeks (10 working days)  
**Sprint Start**: Monday, 00:00 UTC  
**Sprint End**: Sunday, 23:59 UTC (2 weeks later)  
**Planning Meeting**: Monday of sprint start  
**Review/Retrospective**: Friday of sprint end

### Key Ceremonies and Checkpoints
- **Sprint Planning** (Monday Week 1): Define objectives, scope, and story points
- **Mid-Sprint Check-In** (Friday Week 1): Progress review, blocker identification
- **Sprint Review** (Thursday Week 2): Demo completed work, stakeholder feedback
- **Sprint Retrospective** (Friday Week 2): Process improvement, lessons learned
- **Sprint Close** (Friday Week 2): Archive documentation, update metrics

### Overall Sprint Planning Approach
**Philosophy**: Sustainable velocity with buffer time for unknowns  
**Capacity**: 15-20 story points per 2-week sprint (based on historical velocity)  
**Buffer**: 20% of sprint capacity reserved for technical debt and unknowns  
**Focus**: One major objective per sprint, avoid multitasking across big features

### Resource Allocation Strategy
- **Feature Development**: 50% of capacity
- **Bug Fixes & QA**: 20% of capacity
- **Technical Debt**: 20% of capacity
- **Documentation**: 10% of capacity

### Sprint Naming Convention
**Format**: `SPRINT_[NUMBER]_[PHASE]_[FOCUS_AREA]`  
**Examples**:
- `SPRINT_07_LIBRARY_LOVELY_LIB_CREATION`
- `SPRINT_08_LIBRARY_HZ_LIB_EXTRACTION`
- `SPRINT_15_VERSION_1_20_1_BACKPORT`

---

## Sprint Roadmap (Q1 2026 - Q4 2026)

### Phase 1: Library Foundation (Q1 2026)
**Duration**: 6 weeks (3 sprints)  
**Target Story Points**: 54 points total (18 pts/sprint)

#### Sprint 07: Animation Profile & Variant System Implementation
**Dates**: 2026-04-27 to 2026-05-11
**Story Points**: 46 (54 including asset-blocked Dragon task)
**Theme**: Implement ADR_010 and ADR_011 — animation profile system and variant/spawn refactoring
**Major Deliverables**:
- `AnimationPool`, `AnimationProfile`, `AnimationSequence` in HZLib Common (Track A)
- `SizeVariantFeature` and `initializeSpawnVariants()` hook in HZLib Common (Track A)
- Robot profile replaces `AnimationStateManager` and `AnimationDefinitions` (Track B)
- Gourdragora collapses from 9 to 3 entity type configurations with dynamic hitbox (Track B)
- Mushroom Brown biome-aware texture selection at spawn (Track B)
- Dragon's Fury sequence: **asset-blocked, not started this sprint**

**Source ADRs**: ADR_010, ADR_011
**Task File**: `docs/development/sprints/active/SPRINT_07_TASK.md`

#### Sprint 08: Tribute & Reboot Environment Setup
**Dates**: 2026-01-20 to 2026-02-02  
**Story Points**: 18  
**Theme**: Create new variant environments using Lovely Lib  
**Major Deliverables**:
- Tribute 1.21.1 project with Lovely Lib dependency
- Reboot 1.21.1 project with Lovely Lib dependency
- Legacy refactored to use Lovely Lib
- Multi-loader testing (Forge + Fabric + NeoForge)

#### Sprint 09: Ecosystem Terminology & Rename System
**Dates**: 2026-06-20 to 2026-06-26  
**Story Points**: 10 of 44 (Phase 1 complete; Phases 2–7 deferred)  
**Theme**: Execute ADR_018 — rename all `Internal*` classes across the ecosystem to align with canonical Family / Variant / Appearance terminology  
**Source ADR**: ADR_018  
**Task File**: `docs/development/sprints/archive/[COMPLETED]_SPRINT_09_Ecosystem_Terminology_Rename_Phase1_2026-06-26.md`  
**Major Deliverables**:
- `InternalEntity` → `NativeEntity` (HZLib Common) ✅
- `InternalEntityType<T>` → `NativeEntityFamily<T>` (HZLib Common) ✅
- `InternalLogic` → `EntityLogic`, `InternalParticle` → `EntityParticles` (HZLib Common) ✅
- Common `InternalLayerRenderer<T>` → `LayerRenderPipeline<T>` (HZLib Common) ✅
- HZLib loader renames, LovelyLib renames, Monsters & Girls renames — **deferred**

#### Sprint 10: Monsters & Girls Feature Implementation
**Dates**: 2026-06-20 to 2026-06-26  
**Story Points**: 57  
**Theme**: Belly system redesign, emissive layer standardisation, entity family registration fixes, new Fluffball variant, Puffball puff-jump, Snowball throwing, Molten campfire cooking, Gourdragora hunger watch + spawn weight, Mandrake Fructus ceiling planting  
**Feature Notes**: `docs/development/notes/feature-implementations/MonsterGirls_Feature_Implementation_Notes.md`  
**Task File**: `docs/development/sprints/archive/[COMPLETED]_SPRINT_10_Monsters_Girls_Feature_Implementation_2026-06-26.md`  
**Major Deliverables**:
- `BellyLevel` enum + `BellyFeature` — new HZLib feature replacing the old `HAS_BELLY` boolean ✅
- `BELLY_LEVEL` int on `MonsterEntity` — 5-level progression (SLIM/CHUBBY/TUMMY/INFLATED/CHUNKY) ✅
- Emissive texture standardisation — all emissives moved to `layer/` with `_emissive.png` suffix ✅
- Wisp + Spook + Mushroom Warped — registration fixes, belly overlays, emissive ALWAYS slots ✅
- Puffball emissive + autonomous puff-jump; Fluffball — new flying End mushroom variant ✅
- Snowball Mushroom — `RangedAttackMob` + `RangedAttackGoal` throwing vanilla snowballs ✅
- Mushroom Molten — campfire cooking (4 slots, `CampfireCookingRecipe`, `CookingItemLayer`) ✅
- Gourdragora — spawn weight 40/40/20 (Mini/Default/Big) + pumpkin pie hunger-watch drop ✅
- Mandrake Fructus — ceiling Glow Berry planting via `PlantDirection.UP` ✅
- `RangedAttackFeature` in HZLib Common + `MandrakeSpitProjectile` (Chorus poison spit) ✅
- Friendly-fire prevention — `NativeEntity.wantsToAttack()` + projectile same-owner check ✅

---

### Phase 2: Multi-Version Expansion (Q2 2026)
**Duration**: 12 weeks (6 sprints)  
**Target Story Points**: 96 points total (16 pts/sprint)

#### Sprint 10-11: 1.20.1 Wave (4 weeks)
**Sprint 10 Dates**: 2026-02-17 to 2026-03-02  
**Sprint 11 Dates**: 2026-03-03 to 2026-03-16  
**Story Points**: 16 each (32 total)  
**Theme**: Backport all variants to 1.20.1  
**Major Deliverables**:
- Tribute 1.20.1 (Forge + Fabric)
- Legacy 1.20.1 (Forge + Fabric)
- Reboot 1.20.1 (Forge + Fabric)
- Version compatibility layer implementation
- **✅ Milestone 2: 1.20.1 Version Wave Complete**

#### Sprint 12-13: 1.19.x Versions (4 weeks)
**Sprint 12 Dates**: 2026-03-17 to 2026-03-30  
**Sprint 13 Dates**: 2026-03-31 to 2026-04-13  
**Story Points**: 16 each (32 total)  
**Theme**: Backport to 1.19.4 and 1.19.2  
**Major Deliverables**:
- All variants on 1.19.4 (Forge + Fabric)
- All variants on 1.19.2 (Forge + Fabric)
- Cross-version save data testing

#### Sprint 14-15: Modern Version Completion (4 weeks)
**Sprint 14 Dates**: 2026-04-14 to 2026-04-27  
**Sprint 15 Dates**: 2026-04-28 to 2026-05-11  
**Story Points**: 16 each (32 total)  
**Theme**: Complete 1.16.5 and 1.18.2  
**Major Deliverables**:
- All variants on 1.16.5 (Forge + Fabric)
- All variants on 1.18.2 (Forge + Fabric)
- **✅ Milestone 3: Modern Versions Complete**

#### Sprint 16-18: Legacy Versions (6 weeks)
**Sprint 16 Dates**: 2026-05-12 to 2026-05-25 (1.12.2 research)  
**Sprint 17 Dates**: 2026-05-26 to 2026-06-08 (1.12.2 implementation)  
**Sprint 18 Dates**: 2026-06-09 to 2026-06-22 (1.7.10)  
**Story Points**: 12 each (36 total - reduced due to complexity)  
**Theme**: Support ancient Minecraft versions  
**Major Deliverables**:
- All variants on 1.12.2 (Forge-only)
- All variants on 1.7.10 (Forge-only)
- Simplified feature sets where needed
- **✅ Milestone 4: Legacy Versions Complete**

---

### Phase 3: Feature Completion (Q3 2026)
**Duration**: 16 weeks (8 sprints)  
**Target Story Points**: 215 points total

#### Sprint 11: Entity Data Pipeline — ADR 019 Full Implementation
**Dates**: 2026-06-27 to 2026-07-11  
**Story Points**: 71  
**Theme**: Five-layer NBT persistence pipeline — MC-version-agnostic `DataCompound` abstraction, typed `DataField<T>` handles with no string literals at call sites, `EntityDataSchema` schema-driven serialisation, `MigrationChain` covering all published save formats, `SynchedEntityData` protocol on load, overlay slot NBT persistence fix  
**Source ADR**: ADR_019  
**Task File**: `docs/development/sprints/active/SPRINT_11_TASK.md`  
**Major Deliverables**:
- `DataCompound` interface + `NbtAdapterFactory` — zero MC imports in HZLib Common NBT pipeline
- `CompoundTagDataCompound` 1.21.1 implementation with correct UUID storage
- `DataField<T>` typed handles — string keys encapsulated, compile-time safe at all call sites
- `EntityDataSchema` builder + `MigrationChain` runner — schema-driven write/read, single migration authority
- `MigrationStep_V0_Fabric` — Gen1-Fabric locale strings → stable keys (complete lookup table)
- `MigrationStep_V0_Forge` — Gen1-Forge / Gen2 flat → `EntityData` compound
- `MigrationStep_V1_1204` — 1.20.4 flat int IDs → string keys, full field mapping
- `MigrationStep_V1_MG` — belly/texture ID disambiguation, `ModelID`/`AnimatorID` int → string
- `RobotFields.java` — all robot `DataField<T>` constants in one place
- `RobotFamily.configureSchema()` — schema declaration via field handles
- `NativeEntity` save/load fully pipeline-driven — no `CompoundTag` usage below entry point
- Overlay RANDOM/INTERACTIVE slot NBT persistence fixed
- `CombatStatsNBT`, `ProtectionStatsNBT`, `EnchantmentStatsNBT`, `EntityDataMigration` deleted
- Full validation against 1.20.4 robot saves, Gen1 saves, and corrupt-field handling

#### Sprint 12: Condition Framework & Appearance Architecture — ADR 020 & ADR 021 Full Implementation
**Dates**: 2026-07-12 to 2026-07-25  
**Story Points**: 60  
**Theme**: Base `EntityCondition<C>` / `EntityContext` framework eliminating duplicated combinators and shared factory methods across Exchange, Emanation, and Appearance systems; `ConditionalAppearanceFeature` replacing `BiomeAppearanceFeature` and the hardcoded dye chain; `AbstractVariantFeature<V>` collapsing ~600 lines of duplication across the three independent-axis features; `CompositeAppearanceFeature` rename; formal two-lane appearance architecture with runtime mutual-exclusion enforcement  
**Source ADRs**: ADR_020, ADR_021  
**Task File**: `docs/development/sprints/active/SPRINT_12_TASK.md`  
**Major Deliverables**:
- `EntityCondition<C>`, `EntityContext`, `EntityConditions` — combinators and 11 shared factory methods defined exactly once in HZLib Common
- `ExchangeCondition`, `ExchangeContext`, `ExchangeConditions` — refactored to extend base framework; call sites unchanged
- `EmanationCondition`, `EmanationContext`, `EmanationConditions` — refactored to extend base framework; call sites unchanged
- `AppearanceCondition`, `AppearanceContext`, `AppearanceConditions`, `WeightedAppearancePool`, `AppearanceRule` — full appearance condition type set
- `ConditionalAppearanceFeature` with builder — unified spawn-time and interaction-time appearance resolution
- `AbstractVariantFeature<V>` — shared base for `TextureVariantFeature`, `ModelVariantFeature`, `AnimatorVariantFeature`; each collapses to ~15 lines
- `CompositeAppearanceFeature` (renamed from `AppearanceVariantFeature`) + `ICompositeAppearance` with `getSizeConfig()` default
- `NativeEntity.initializeRandomVariants` — lane-check logic (Lane B / CompositeAppearanceFeature takes precedence; Lane A fallback); `seedOverlaySlots()` extracted
- `BiomeAppearanceFeature.java` deleted
- LovelyLib — 7 robot families wired with 16-dye `ConditionalAppearanceFeature`; `RobotEntity` dye chain replaced with `tryConditionalAppearance()`
- Monsters & Girls — Gourdragora `SizeConfig` references wired into `ICompositeAppearance` entries; biome-selection overrides replaced

#### Sprint 13: Animation System Unification — ADR 022 Full Implementation
**Dates**: 2026-07-26 to 2026-08-08  
**Story Points**: 50  
**Theme**: Close the root animation missing-wire (no `AnimationProfile` on robot variants); eliminate standby flicker with declarative `IdleSlot` system; replace reflection-based bone visibility with `BoneVisibilityFeature`; delete pre-profile dead code across three loaders; implement Tribute AI goals  
**Source ADR**: ADR_022  
**Task File**: `docs/development/sprints/active/SPRINT_13_TASK.md`  
**Prerequisite**: Sprint 12 complete  
**Major Deliverables**:
- `BoneCondition` + `BoneRule` + `BoneVisibilityFeature` — new HZLib Common feature; evaluated in `NativeModel.setCustomAnimations()` each frame; no reflection; no GeckoLib imports in Common
- `headBoneName` field on `NativeEntityFamily` — `NativeModel.resolveHeadBoneName()` reads from family; hardcoded `"head"` eliminated
- `IdleCondition` + `IdleSlot` — new HZLib Common animation types; `idleStationaryTicks` counter on `NativeEntity`; `resolveIdleSlot()` in `AnimationStateManager`; stable threshold eliminates flicker
- `ROBOT_BASE_PROFILE` with idle slots attached in `RobotFamily.configureVariants()` — profile-aware path activated for all robots; `handleStandbyAnimation()` deleted
- `TRIBUTE_PROFILE` (no rest/sit/idle-slots) on Tribute families — Tribute behavior faithful to original
- `BoneVisibilityConditions` utility + `BoneVisibilityFeature` declared on KITSUNE family — tail visibility via lambdas, `TailAnimationUtils` reflection deleted
- `AiTributeReturnToBaseGoal` + `TributeRobotEntity.registerGoals()` override (×3 loaders) — Pre-Publish Checklist item 8
- `RobotAnimation.java` deleted (×3 loaders), `KitsuneModel.java` deleted (×3 loaders)
- Dead enums deleted: `EntityAnimation`, `EntityModel`, `EntityVariantModel`, lovelylib `BoneTransformations`
- All 9 architectural smells from `Animation_Architecture_Audit.md` resolved

#### Sprint 19-20: Core Robot Specializations (4 weeks)
**Sprint 19 Dates**: 2026-06-23 to 2026-07-06 (Honey features)  
**Sprint 20 Dates**: 2026-07-07 to 2026-07-20 (Bunny features)  
**Story Points**: 14 each (28 total)  
**Theme**: Implement Honey and Bunny specialized mechanics  
**Major Deliverables**:
- Honey house worker system (item sorting, chest management)
- Honey bee farming integration
- Bunny speed advantages (movement + attack)
- Bunny2 improvements over Bunny

#### Sprint 21-22: Advanced Robot Mechanics (4 weeks)
**Sprint 21 Dates**: 2026-07-21 to 2026-08-03 (Dragon + Neko)  
**Sprint 22 Dates**: 2026-08-04 to 2026-08-17 (Kitsune)  
**Story Points**: 14 each (28 total)  
**Theme**: Implement Dragon, Neko, Kitsune unique abilities  
**Major Deliverables**:
- Dragon heavy sword combat mechanics
- Neko gauntlet claw system
- Kitsune tail progression (1-9 tails)
- Kitsune buff and support abilities

#### Sprint 23-24: Animation & Visual Polish (4 weeks)
**Sprint 23 Dates**: 2026-08-18 to 2026-08-31 (Animation system)  
**Sprint 24 Dates**: 2026-09-01 to 2026-09-14 (Visual feedback)  
**Story Points**: 14 each (28 total)  
**Theme**: Complete animation system and visual improvements  
**Major Deliverables**:
- Item holding animations (tools, weapons, blocks)
- Interaction animations (loyalty, exhaust, alert)
- Health indicator collar colors (green to red)
- Combat mode visual indicators
- Particle effect enhancements
- **✅ Milestone 5: Specialized Features Complete**

---

### Phase 4: Ecosystem Integration (Q4 2026)
**Duration**: 16 weeks (8 sprints)  
**Target Story Points**: 96 points total (12 pts/sprint)

#### Sprint 25-26: Cross-Mod Systems (4 weeks)
**Sprint 25 Dates**: 2026-09-15 to 2026-09-28 (M&G integration)  
**Sprint 26 Dates**: 2026-09-29 to 2026-10-12 (Developer APIs)  
**Story Points**: 12 each (24 total)  
**Theme**: Cross-mod compatibility and developer tools  
**Major Deliverables**:
- Monsters & Girls integration
- HZ Lib shared utilities finalized
- Developer API documentation
- Integration guides and examples

#### Sprint 27-28: Documentation & Content (4 weeks)
**Sprint 27 Dates**: 2026-10-13 to 2026-10-26 (Wiki)  
**Sprint 28 Dates**: 2026-10-27 to 2026-11-09 (Video content)  
**Story Points**: 12 each (24 total)  
**Theme**: Comprehensive documentation and tutorials  
**Major Deliverables**:
- Complete wiki structure and content
- Robot type guides and mechanic explanations
- Video tutorial series (10+ videos)
- Behind-the-scenes development content

#### Sprint 29-30: Community Features (4 weeks)
**Sprint 29 Dates**: 2026-11-10 to 2026-11-23 (Events)  
**Sprint 30 Dates**: 2026-11-24 to 2026-12-07 (Maintenance planning)  
**Story Points**: 12 each (24 total)  
**Theme**: Community engagement and long-term maintenance  
**Major Deliverables**:
- Community events and contests
- Modpack integration guides
- Maintenance processes established
- Long-term support strategy

#### Sprint 31-32: Final Polish & 2027 Planning (4 weeks)
**Sprint 31 Dates**: 2026-12-08 to 2026-12-21 (Polish + testing)  
**Sprint 32 Dates**: 2026-12-22 to 2027-01-04 (2027 roadmap)  
**Story Points**: 12 each (24 total)  
**Theme**: Final quality pass and future planning  
**Major Deliverables**:
- Final bug sweep and performance optimization
- Documentation review and updates
- 2027 roadmap creation
- Community celebration event
- **✅ Milestone 6: Ecosystem Integration Complete**

---

## Current Sprint

### Sprint 11: Entity Data Pipeline — ADR 019 Full Implementation 🔄
**Status**: 🔄 Active  
**Dates**: 2026-06-27 to 2026-07-11  
**Story Points**: 71 (with M&G phase deferrable to Sprint 12 if schedule pressure)  
**Theme**: Five-layer NBT persistence pipeline — `DataCompound`, typed `DataField<T>` handles, `EntityDataSchema`, `MigrationChain`, full published-save migration coverage  
**Source ADR**: `docs/development/decisions/ADR_019_Entity_Data_Pipeline.md`  
**Task File**: `docs/development/sprints/active/SPRINT_11_TASK.md`

**Sprint phases**:
- **Phase 0** (prerequisite): `DataCompound` interface + `NbtAdapterFactory` + `CompoundTagDataCompound` for 1.21.1 — must publish HZLib snapshot before Phase 1 begins
- **Phase 1**: Pipeline core in HZLib Common — `DataType`, `DataField<T>`, `EntityDataSchema`, `MigrationStep`/`MigrationChain`, full `NativeEntity` save/load wiring, overlay slot NBT fix, delete superseded NBT classes
- **Phase 2**: LovelyLib robot schema — `RobotFields` constants, `RobotFamily.configureSchema()`, three migration steps (V0_Fabric, V0_Forge, V1_1204), remove flat-write overrides from `RobotEntity`, delete `EntityDataMigration`
- **Phase 3** *(can slip to Sprint 12)*: Monsters & Girls schema — `MonsterFields`, `MigrationStep_V1_MG` with belly/texture ID disambiguation, `baseTextureCount` per family
- **Phase 4**: Full validation — 1.20.4 robot saves, Gen1 saves, overlay slot persistence, belly persistence, corrupt field handling, SynchedEntityData authority, McVersion field

**Build dependency**: Phase 0 → Phase 1 → Phase 2 & 3 (parallel) → Phase 4. Do not begin Phase 2 before Phase 1 is complete and HZLib artifact is available.

---

## Next Sprint

### Sprint 12: Condition Framework & Appearance Architecture — ADR 020 & ADR 021 📋
**Status**: 📋 Planned  
**Dates**: 2026-07-12 to 2026-07-25  
**Story Points**: 60  
**Theme**: Base condition/context framework (eliminates duplicated combinators and shared factory methods); `ConditionalAppearanceFeature` (replaces `BiomeAppearanceFeature` and hardcoded dye chain); `AbstractVariantFeature<V>` (collapses ~600 lines across three Lane A features); `CompositeAppearanceFeature` rename; two-lane appearance architecture with formal mutual-exclusion enforcement  
**Source ADRs**: `docs/development/decisions/ADR_020_Conditional_Appearance_Feature.md`, `docs/development/decisions/ADR_021_Composite_Appearance_Feature.md`  
**Task File**: `docs/development/sprints/active/SPRINT_12_TASK.md`

**Sprint steps**:
- **Step 1** (prerequisite): Create `EntityCondition<C>`, `EntityContext`, `EntityConditions` in new HZLib `conditions` package — must compile before anything else begins
- **Step 2**: Migrate `Exchange*` and `Emanation*` condition types to extend the base framework — delete duplicated combinator bodies and shared method implementations; zero call-site changes
- **Step 3**: Implement appearance types — `AppearanceCondition`, `AppearanceContext`, `AppearanceConditions`, `WeightedAppearancePool`, `AppearanceRule`, `ConditionalAppearanceFeature`; wire into `NativeEntity.initializeSpawnVariants`; delete `BiomeAppearanceFeature`
- **Step 4** *(parallel with Step 5)*: Create `AbstractVariantFeature<V>`, simplify `TextureVariantFeature`, `ModelVariantFeature`, `AnimatorVariantFeature` to ~15 lines each
- **Step 5** *(parallel with Step 4)*: Rename `AppearanceVariantFeature` → `CompositeAppearanceFeature`; rename `IAppearanceVariant` → `ICompositeAppearance`; add `getSizeConfig()` default method
- **Step 6**: Update `NativeEntity.initializeRandomVariants` with lane-check logic; extract `seedOverlaySlots()`; add `hasLaneAFeatures()` guard
- **Step 7** *(parallel with Step 8)*: LovelyLib — wire 16-dye `ConditionalAppearanceFeature` on all 7 robot families; replace dye chain in `RobotEntity` with `tryConditionalAppearance()`
- **Step 8** *(parallel with Step 7)*: Monsters & Girls — wire `SizeConfig` into `ICompositeAppearance` entries on `GourdragoraFamily`; replace string-concatenation key construction; migrate any biome-selection overrides
- **Step 9**: Validation — dye interaction, spawn-time biome selection, Lane B composite, composable conditions, weighted pool distribution, Lane A isolation, misconfiguration warning, call-site stability

**Build dependency**: Step 1 → Step 2 → Step 3 → Step 4 & 5 (parallel) → Step 6 → Step 7 & 8 (parallel) → Step 9.

---

## Upcoming Sprints

### Sprint 13: Animation System Unification — ADR 022 Full Implementation 📋
**Status**: 📋 Planned  
**Dates**: 2026-07-26 to 2026-08-08  
**Story Points**: 50  
**Theme**: Close the root missing-wire gap (`AnimationProfile` never attached to robot variants); eliminate standby flicker via declarative `IdleSlot` system; replace reflection-based bone visibility with first-class `BoneVisibilityFeature`; delete all pre-profile-era dead code across three loaders; implement Tribute AI goals (Pre-Publish Checklist items 2, 3, 8)  
**Source ADR**: `docs/development/decisions/ADR_022_Animation_System_Unification.md`  
**Task File**: `docs/development/sprints/active/SPRINT_13_TASK.md`  
**Prerequisite**: Sprint 12 complete (`AnimationProfile` builder must accept `idleSlots`)

**Sprint changes**:
- **Change A** — `BoneVisibilityFeature` in HZLib Common: new `BoneCondition` / `BoneRule` / `BoneVisibilityFeature` types; `NativeModel.setCustomAnimations()` evaluates rules each frame — no reflection, no subclassing
- **Change B** — Head bone name on `NativeEntityFamily`: `headBoneName` field + fluent setter; `NativeModel.resolveHeadBoneName()` reads from family descriptor; eliminates hardcoded `"head"` string
- **Change C** — `AnimationProfile` on all robot variants (root fix): `ROBOT_BASE_PROFILE` attached in `RobotFamily.configureVariants()`; `TRIBUTE_PROFILE` (no rest/sit/idle-slots) on Tribute families; `AnimationStateManager.resolveProfile()` returns non-null for every robot
- **Change D** — `IdleSlot` system + `idleStationaryTicks`: `IdleCondition` + `IdleSlot` in HZLib Common; `idleStationaryTicks` counter on `NativeEntity` replaces `standbyTicks`; `AnimationStateManager.resolveIdleSlot()` with stable threshold comparison; `onIdleSlotChanged()` hook drives `IS_IN_SITTING_POSE` update; `handleStandbyAnimation()` deleted
- **Change E** — Cleanup: delete `RobotAnimation.java` (×3 loaders), `KitsuneModel.java` (×3 loaders), `EntityAnimation.java`, `EntityModel.java`, `EntityVariantModel.java`, lovelylib `BoneTransformations.java`, `TailAnimationUtils.configureTailVisibility()` reflection method
- **Change F** — `BoneVisibilityConditions` utility + `BoneVisibilityFeature` on KITSUNE family; `AiTributeReturnToBaseGoal` (original `BunnyFollowPoint` equivalent); `TributeRobotEntity.registerGoals()` override (all 3 loaders)

**Build dependency**: Change A–B (new HZLib types) → Change D (IdleSlot types + AnimationStateManager wiring) → Change C (profiles + idle-slot declarations) → Change F (LovelyLib wiring) → Change E (deletions) → Validation.

---

## Completed Sprints

### Sprint 10: Monsters & Girls Feature Implementation ✅
**Status**: ✅ Completed  
**Timeframe**: 2026-06-20 to 2026-06-26  
**Completion Date**: 2026-06-26  
**Sprint Type**: Feature Implementation

**Sprint Goal**: Implement the full set of Monsters & Girls features: belly system redesign, emissive layer standardisation, entity family registration fixes, new Fluffball variant, Puffball puff-jump, Snowball throwing, Molten campfire cooking, Gourdragora hunger watch + spawn weight, Mandrake Fructus ceiling planting, and all required HZLib framework additions.

**Story Points**: 57 (12 phases + 2 additional phases for ranged attack and friendly-fire)

**Key Achievements**:
- Emissive texture standardisation — all emissives moved to `layer/` folder with `_emissive.png` suffix
- `BellyLevel` enum + `BellyFeature` — new HZLib feature replacing the old `HAS_BELLY` boolean
- `BELLY_LEVEL` int on `MonsterEntity` — 5-level progression (SLIM/CHUBBY/TUMMY/INFLATED/CHUNKY)
- `RenderConditions.bellyAtLeast()` predicate + `SizeVariantFeature.pickWeightedRandom()` method
- `PlantDirection` enum + `PlantingFeature` ceiling scan for Fructus Cave Vine planting
- Wisp, Spook, Mushroom Warped — registration fixes, belly overlays, emissive ALWAYS slots
- Puffball emissive + autonomous puff-jump (JUMP_BOOST II + SLOW_FALLING, 200-tick cooldown)
- Fluffball — new flying End mushroom variant (`MushroomFamily.FLUFFBALL`, `FlyTamableEntity`)
- Snowball Mushroom — `RangedAttackMob` + `RangedAttackGoal` throwing vanilla snowballs with freeze + slow
- Mushroom Crimson, Soul Wanderer, Molten — emissive wiring and texture path fixes
- Molten Gal campfire cooking — 4 synced `ItemStack` slots, `CampfireCookingRecipe`, `CookingItemLayer`
- Gourdragora — spawn weight 40/40/20 (Mini/Default/Big) + pumpkin pie hunger-watch drop
- Mandrake Fructus — ceiling Glow Berry planting via `PlantDirection.UP`
- `RangedAttackFeature` in HZLib Common — replaces per-entity key-check pattern
- `MandrakeSpitProjectile` — Poison I (7s) + Nausea (5s), friendly-fire suppression
- `NativeEntity.wantsToAttack()` override — same-owner tamed peers never targeted

**Archive**: `docs/development/sprints/archive/[COMPLETED]_SPRINT_10_Monsters_Girls_Feature_Implementation_2026-06-26.md`

---

### Sprint 09: Ecosystem Terminology & Rename System ✅ (Phase 1 Complete)
**Status**: ✅ Completed (Phase 1 — HZLib Common renames only)  
**Timeframe**: 2026-06-20 to 2026-06-26  
**Completion Date**: 2026-06-26  
**Sprint Type**: Architecture / Refactoring

**Sprint Goal**: Execute ADR_018 — rename all `Internal*` classes across HZLib, LovelyLib, and Monsters & Girls so every class name reflects its tier and role.

**Story Points**: 10 of 44 completed (Phase 1 only; Phases 2–7 deferred)

**Key Achievements**:
- `InternalEntity` → `NativeEntity` (HZLib Common) — class, JavaDoc, footer comment
- `InternalEntityType<T>` → `NativeEntityFamily<T>` (HZLib Common) — generic bounds updated across all dependents
- `InternalLogic` → `EntityLogic` (HZLib Common) — all static call sites updated
- `InternalParticle` → `EntityParticles` (HZLib Common) — all static call sites updated
- Common `InternalLayerRenderer<T>` → `LayerRenderPipeline<T>` (HZLib Common) — distinct from loader-specific renderer

**Deferred Work** (carry to next sprint):
- Phase 2: HZLib loaders — `InternalAnimation` → `NativeAnimation`, `InternalModel` → `NativeModel`, `InternalLayerRenderer` → `NativeRenderer` (×3 loaders each)
- Phase 3: LovelyLib Common — `NativeEntityType` → `RobotFamily`, `LovelyRobotType` → `RobotFamilyRegistry`, `EntityVariant` → `RobotVariant`
- Phase 4: LovelyLib loaders — `InternalAnimation` → `RobotAnimation` (×3 loaders)
- Phase 5: LovelyLib registries — `LegacyRobotType` → `LegacyRobotFamilies`, `RebootRobotType` → `RebootRobotFamilies`, `TributeRobotType` → `TributeRobotFamilies`
- Phase 6: Monsters & Girls — `NativeEntityType<T>` → `MonstersFamily<T>` + nine concrete `*Type` → `*Family` classes
- Phase 7: Full build verification + runtime smoke test

**Archive**: `docs/development/sprints/archive/[COMPLETED]_SPRINT_09_Ecosystem_Terminology_Rename_Phase1_2026-06-26.md`

---

### Sprint 08: InternalEntity Consolidation & OverlayFeature ✅
**Status**: ✅ Completed  
**Timeframe**: 2026-04-27 to 2026-06-20  
**Completion Date**: 2026-06-20  
**Sprint Type**: Architecture & Feature

**Sprint Goal**: Execute ADR_012 (InternalEntity Consolidation) and ADR_017 (OverlayFeature)

**Story Points**: 50 (InternalEntity work) + OverlayFeature implementation

**Key Achievements**:
- Rebuilt HZLib `InternalEntityType` — removed dead code, `configureVariants()` as single hook
- Updated lovelylib `NativeEntityType` to extend HZLib's rebuilt type
- Rebuilt HZLib `InternalEntity` with solid foundations (combat timers, NBT, lifecycle)
- Built HZLib `RobotEntity` robot-specific tier (level, exp, protection, enchantments)
- Migrated lovelylib hierarchy to extend HZLib's `RobotEntity`
- Executed rename chain: `LovelyRobotEntity` → `RobotEntity`, loader `RobotEntity` → `BaseRobotEntity`
- Implemented `OverlayFeature` (ADR_017): RANDOM/CONDITIONAL/INTERACTIVE/ALWAYS slot modes
- Gourdragora tinted carving + Jack'o face-cover inversion
- Mandrake Flower RANDOM hairstyle slots
- Mushroom Halloween costume CONDITIONAL slots

**Archive**: `docs/development/sprints/archive/[COMPLETED]_SPRINT_08_InternalEntity_Consolidation_OverlayFeature_2026-06-20.md`

---

### Sprint 07: Animation Profile & Variant System ✅
**Status**: ✅ Completed  
**Timeframe**: 2026-04-27 to 2026-04-27 (single-session sprint)  
**Completion Date**: 2026-04-27  
**Sprint Type**: Architecture

**Sprint Goal**: Implement ADR_010 (Animation Profile System) and ADR_011 (Variant and Spawn System)

**Story Points**: 46 (54 including asset-blocked Dragon's Fury)

**Key Achievements**:
- `AnimationPool`, `AnimationProfile`, `AnimationSequence` in HZLib Common
- `SizeVariantFeature` with per-pose `EntityDimensions` and stat multipliers
- `initializeSpawnVariants()` hook in `InternalEntity.finalizeSpawn()`
- Robot profile replaces `AnimationStateManager` and `AnimationDefinitions` (deleted)
- Gourdragora collapsed from 9 to 3 entity type configurations with dynamic hitbox
- Mushroom Brown biome-aware texture selection at spawn
- Dragon's Fury sequence: asset-blocked, not started

**Archive**: `docs/development/sprints/archive/[COMPLETED]_SPRINT_07_Animation_Profile_Variant_System_2026-04-27.md`

---

### Sprint 06: Shared Library Architecture Foundation ✅
**Status**: ✅ Completed  
**Timeframe**: 2025-12-11 to 2025-12-25  
**Completion Date**: 2025-12-25  
**Sprint Type**: Infrastructure & Planning

**Sprint Goal**: Establish HZLib and LovelyLib environments; create architectural ADRs; fix build systems

**Story Points**: 8 (Holiday sprint, reduced capacity)

**Key Achievements**:
- HZLib and LovelyLib multi-loader environments created and compiling
- ADR_001–005 created (Library Architecture, Design Decisions, Scope, Robot Creator, Config Screen)
- Follow behavior enhancement (position prediction, body-head coordination)
- Combat mode model synchronization fix (armed model now synced client-side)
- Random texture + health bug fixes in Legacy 1.21.1
- Vehicle sitting animation feature

**Archive**: `docs/development/sprints/archive/[COMPLETED]_SPRINT_06_Shared_Library_Architecture_Foundation_2025-12-25.md`

---

### Sprint 05: Enchanted Book Protection Feature ✅
**Status**: ✅ Completed  
**Timeframe**: December 7, 2025 (1 day)  
**Completion Date**: 2025-12-07  
**Sprint Type**: Feature Enhancement

**Sprint Goal**: Implement enchanted book feeding system for robot protection upgrades

**Story Points**: 8 (Planned: 8, Actual: 8, Completion: 100%)

**Deliverables**:
- ✅ EnchantmentProtectionCalculator utility class
- ✅ Enhanced robot interaction system
- ✅ Config integration across all loaders
- ✅ Comprehensive enchantment processing logic
- ✅ User feedback and validation systems

**Velocity**: 8 story points in 1 day (focused implementation)

---

### Sprint 04: Clean Architecture Refactoring ✅
**Status**: ✅ Completed  
**Timeframe**: December 7, 2025 (1 day)  
**Completion Date**: 2025-12-07  
**Sprint Type**: Architecture & Infrastructure

**Sprint Goal**: Refactor 1.21.1 Legacy version with clean architecture

**Story Points**: 15 (Planned: 15, Actual: 15, Completion: 100%)

**Key Achievements**:
- Created feature classes (PickupFeature, DropFeature, LootDropFeature)
- Implemented "me" command variant for convenience
- Created ExperienceTracker to prevent exp farming
- Implemented EntityData consolidation with automatic migration

**Velocity**: 15 story points in 1 day (focused refactoring)

---

### Sprint 03: New Interactive Features ✅
**Status**: ✅ Completed  
**Timeframe**: November 25 - December 6, 2025 (12 days)  
**Completion Date**: 2025-12-07

**Sprint Goal**: Implement quality-of-life features for robot interaction

**Story Points**: 26 (Planned: 26, Actual: 26, Completion: 100%)

**Key Achievements**:
- Robot Retrieval System (Ctrl+Shift interaction)
- Robot Command System (comprehensive admin commands)
- Robot Core Glow Effect (color-coded glowing)
- Smart Core Retrieval (distance-based auto-pickup)

**Velocity**: 26 story points in 12 days (~2.2 pts/day)

---

### Sprint 02: NBT Recipe System ✅
**Status**: ✅ Completed  
**Timeframe**: November 22-23, 2025 (2 days)  
**Completion Date**: 2025-11-23

**Sprint Goal**: Implement generic NBT transfer recipe system

**Story Points**: 10 (Early implementation)

**Velocity**: 10 story points in 2 days (~5 pts/day)

---

### Sprint 01: Vanilla & Bunny2 Implementation ✅
**Status**: ✅ Completed  
**Timeframe**: November 22-25, 2025 (4 days)  
**Completion Date**: 2025-11-25

**Sprint Goal**: Implement first two robot types with complete functionality

**Story Points**: 45

**Velocity**: 45 story points in 4 days (~11 pts/day)

---

## Backlog Management

### Prioritization Approach
**Framework**: MoSCoW Method
- **Must Have**: Core functionality, library foundation, critical bugs
- **Should Have**: Specialized features, multi-loader support, documentation
- **Could Have**: Polish features, advanced animations, extra variants
- **Won't Have**: Scope beyond 2026 roadmap, unproven experimental features

### Backlog Grooming Cadence
**Frequency**: Weekly on Fridays  
**Duration**: 1 hour  
**Participants**: Solo developer + community input  
**Activities**:
- Review and update story points
- Refine upcoming sprint items
- Incorporate community feedback
- Adjust priorities based on roadmap

### Story Point Estimation System
**Scale**: Modified Fibonacci (1, 2, 3, 5, 8, 13, 21)  
**Reference Stories**:
- **1 point**: Simple config change, documentation update
- **2 points**: Small bug fix, minor feature adjustment
- **3 points**: Medium feature, API addition, test suite
- **5 points**: Complex feature, refactoring task
- **8 points**: Major feature, significant system change
- **13 points**: Very complex feature, architectural change
- **21 points**: Epic-level work (should be broken down)

### Ready Criteria for Sprint Items
**Definition of Ready**:
- [ ] User story written with acceptance criteria
- [ ] Story points estimated
- [ ] Dependencies identified
- [ ] Technical approach outlined
- [ ] Risk assessment completed
- [ ] Testability defined

### Backlog Evolution Strategy
**Quarterly Backlog Review**: Align with roadmap phases  
**Monthly Reprioritization**: Adjust based on velocity and feedback  
**Continuous Refinement**: Add new items from community feedback  
**Technical Debt Tracking**: Maintain separate backlog for tech debt

---

## Dependencies and Critical Path

### Inter-Sprint Dependencies

#### Sprint 07 → Sprint 08
**Dependency**: Lovely Lib API must be stable before Tribute/Reboot can use it  
**Risk**: API changes in Sprint 08 require Sprint 07 rework  
**Mitigation**: Thorough design review before Sprint 07 starts

#### Sprint 08 → Sprint 09
**Dependency**: All three variants must work with Lovely Lib before HZ Lib extraction  
**Risk**: Rushed extraction if Sprint 08 overruns  
**Mitigation**: Buffer time in Sprint 09, prioritize working code over perfect code

#### Phase 1 → Phase 2
**Critical Dependency**: Library foundation must be complete before any backporting  
**Risk**: Backporting with unstable library causes rework  
**Mitigation**: Extra testing in Sprint 09, hold Phase 2 start if needed

#### Phase 2 → Phase 3
**Dependency**: All versions must be functional before specialized features  
**Risk**: Feature testing across all versions becomes bottleneck  
**Mitigation**: Implement features on 1.21.1 first, backport in Phase 4 if needed

### External Dependencies and Timelines

#### GeckoLib Stability
**Current Version**: 4.x  
**Risk**: Breaking changes in GeckoLib updates  
**Mitigation**: Pin specific versions, monitor changelog  
**Impact**: Animation system depends entirely on this

#### Mod Loader Updates
**Forge**: Stable, mature  
**Fabric**: Frequent updates, usually backward compatible  
**NeoForge**: Still maturing, potential for breaking changes  
**Mitigation**: Test early with new releases, maintain compatibility layers

#### Community Availability
**Beta Testers**: Volunteer-based, availability varies  
**Content Creators**: Independent schedules  
**Mitigation**: Build engaged community early, multiple testers per version

### Critical Path Analysis

**Critical Path** (No Slack):
1. Sprint 07-09: Library Foundation (6 weeks)
2. Sprint 10-11: 1.20.1 Backport (4 weeks)
3. Sprint 12-18: Remaining Version Backports (12 weeks)
4. Sprint 19-24: Feature Completion (12 weeks)

**Total Critical Path**: 34 weeks (8.5 months)

**Non-Critical Path** (Has Slack):
- Documentation work (can lag features by 1-2 sprints)
- Community features (can be adjusted)
- Cross-mod integration (dependent on M&G timeline)

### Contingency Plans for Dependency Failures

#### If Lovely Lib Extraction Fails (Sprint 07)
**Fallback**: Create lightweight utility library only, keep variants separate  
**Impact**: Less code reduction, more maintenance overhead  
**Timeline**: Can continue with Phase 2, reduced benefits

#### If Version Backport Blocked (Sprint 10-18)
**Fallback**: Focus on most popular versions (1.20.1, 1.19.4, 1.16.5)  
**Impact**: Reduced version coverage, community disappointment  
**Timeline**: Saves 4-6 weeks, can allocate to features

#### If GeckoLib Has Breaking Changes
**Fallback**: Pin to last working version, delay update  
**Impact**: Stuck on older animation API  
**Timeline**: No immediate impact, investigate alternative in Phase 4

---

## Sprint Metrics and Evaluation

### Velocity Measurement Approach
**Calculation**: Sum of story points for completed user stories in sprint  
**Historical Velocity** (2025):
- Sprint 01: 45 points (exceptional, initial implementation)
- Sprint 02: 10 points (focused feature)
- Sprint 03: 26 points (quality-of-life features)
- Sprint 04: 15 points (refactoring)
- Sprint 05: 8 points (targeted feature)

**Sustainable Velocity Target**: 15-20 points per 2-week sprint

### Quality Metrics and Standards

#### Code Quality
- **Code Coverage**: Target 70%+ for critical paths
- **Code Review**: All code self-reviewed before commit
- **Style Compliance**: 100% adherence to project style guide
- **Documentation**: All public APIs documented

#### Bug Metrics
- **Critical Bugs**: Target 0 in production
- **High Priority Bugs**: Resolved within 1 week
- **Medium Bugs**: Resolved within 2 weeks
- **Low Bugs**: Backlogged for future sprints

#### Performance Metrics
- **Build Time**: Target <5 minutes per variant
- **FPS with 10 Robots**: Target >60 FPS
- **Memory Usage**: Monitor and optimize
- **Load Time**: Minimize mod loading impact

### Sprint Success Evaluation Criteria

**Sprint Success** =
- ✅ 85%+ of planned story points completed
- ✅ Zero critical bugs introduced
- ✅ All acceptance criteria met for completed stories
- ✅ Documentation updated
- ✅ Community communication completed

**Sprint Failure** (Requires Retrospective) =
- ❌ <70% of planned story points completed
- ❌ Critical bugs introduced
- ❌ Technical debt significantly increased
- ❌ Team velocity declining for 2+ sprints

### Continuous Improvement Measures

#### Sprint-Level Improvements
- Document lessons learned in sprint retrospectives
- Adjust velocity estimates based on actual completion
- Refine story point estimates based on experience
- Update sprint templates based on what works

#### Phase-Level Improvements
- Review phase outcomes against goals
- Adjust resource allocation for next phase
- Update risk assessments based on learnings
- Refine processes and workflows

### KPIs for Sprint Effectiveness

**Development KPIs**:
- Velocity trend (should be stable 15-20 pts/sprint)
- Story point estimation accuracy (should improve over time)
- Sprint goal achievement rate (target 85%+)
- Technical debt ratio (should not exceed 20% of backlog)

**Quality KPIs**:
- Bug introduction rate (should decrease over time)
- Code coverage trend (should increase)
- Documentation completeness (target 100% for public APIs)
- Performance benchmarks (should meet or exceed targets)

**Community KPIs**:
- Community engagement (Discord activity, GitHub issues)
- Beta tester participation (target 20+ active testers by Q2)
- Content creator activity (target 5+ showcases by Q4)
- User satisfaction (surveys, feedback quality)

---

## Adaptation and Adjustment

### Retrospective Approach

#### Sprint Retrospectives (Every 2 weeks)
**Format**: What Went Well / What Could Improve / Action Items  
**Duration**: 1 hour  
**Participants**: Solo developer + community input (optional)

**Questions**:
1. What went well this sprint?
2. What didn't go well?
3. What should we start doing?
4. What should we stop doing?
5. What should we continue doing?

**Output**: 2-3 concrete action items for next sprint

---

#### Phase Retrospectives (End of each phase)
**Format**: Comprehensive review with metrics  
**Duration**: 2-3 hours  
**Participants**: Solo developer + key community members

**Questions**:
1. Did we achieve phase objectives?
2. Were timeline estimates accurate?
3. What technical challenges did we face?
4. How effective was our communication?
5. What should change for next phase?

**Output**: Updated processes, adjusted roadmap if needed

---

### Mid-Sprint Adjustment Mechanisms

#### Mid-Sprint Check-In (Friday of Week 1)
**Purpose**: Identify blockers early, adjust scope if needed  
**Actions**:
- Review story completion progress
- Identify any blockers or risks
- Adjust scope if significantly behind
- Update community on progress

#### Scope Adjustment Criteria
**Reduce Scope If**:
- <30% of stories completed by mid-sprint
- Critical blocker discovered
- Unexpected external dependency failure
- Personal circumstances require reduced capacity

**How to Adjust**:
1. Move lowest priority stories to backlog
2. Preserve sprint goal if possible
3. Communicate changes to community
4. Document reasons for adjustment

---

### How Learning Feeds Into Future Sprints

#### Technical Learning
- **Document Solutions**: Create ADRs for major decisions
- **Update Libraries**: Incorporate patterns into Lovely Lib/HZ Lib
- **Refine Estimates**: Adjust story points based on actual complexity
- **Share Knowledge**: Blog posts, community discussions

#### Process Learning
- **Update Templates**: Refine sprint task templates
- **Adjust Velocity**: Set realistic targets based on actual completion
- **Improve Planning**: Better story breakdown based on experience
- **Streamline Workflows**: Eliminate inefficient processes

#### Community Learning
- **Incorporate Feedback**: Adjust priorities based on user needs
- **Improve Communication**: Learn what works for community updates
- **Better Beta Testing**: Refine testing processes based on results
- **Feature Validation**: Test assumptions about what users want

---

### Balancing Predictability and Flexibility

#### Fixed Elements (Predictable)
- Sprint length: Always 2 weeks
- Core sprint ceremonies: Planning, check-in, review, retrospective
- Story point scale: Modified Fibonacci
- Phase boundaries: Aligned with roadmap milestones

#### Flexible Elements (Adaptable)
- Story point capacity per sprint (adjust based on velocity)
- Feature priorities within phase (adjust based on feedback)
- Documentation timing (can lag features if needed)
- Community features (adjust based on engagement)

#### Decision Framework for Adaptation

**For Minor Changes** (within sprint):
- Solo developer decision
- Communicate in mid-sprint check-in
- Document in sprint notes

**For Medium Changes** (adjust sprint goals):
- Solo developer decision with community notification
- Explain reasoning in sprint review
- Update backlog accordingly

**For Major Changes** (change phase plan):
- Community input strongly considered
- Transparent communication across all channels
- Update roadmap document
- Hold special community discussion if needed

---

### Replanning Triggers and Approach

#### Trigger 1: Velocity Drop
**Condition**: Velocity <12 points for 2 consecutive sprints  
**Action**: Reduce planned capacity to 12-15 points, investigate causes

#### Trigger 2: Critical Bug Discovery
**Condition**: Bug requires immediate fix, impacts sprint goal  
**Action**: Emergency sprint scope reduction, allocate points to bug fix

#### Trigger 3: External Dependency Failure
**Condition**: GeckoLib breaking change, mod loader issues  
**Action**: Pause affected work, research solution, adjust timeline

#### Trigger 4: Personal Circumstances
**Condition**: Developer availability significantly reduced  
**Action**: Activate Plan D (MVP approach) from roadmap, communicate openly

#### Trigger 5: Community Feedback Pivot
**Condition**: Major feature request or issue from 50+ users  
**Action**: Evaluate impact, potentially reprioritize backlog

---

## Communication and Coordination

### Sprint Status Reporting Approach

#### Weekly Updates (Every Monday)
**Channel**: Discord #announcements  
**Format**: Brief text update  
**Content**:
- Previous week accomplishments
- Current week focus
- Any blockers or challenges
- Community shoutouts

#### Sprint Review (End of Sprint)
**Channel**: Discord + Blog post  
**Format**: Detailed writeup with visuals  
**Content**:
- Sprint goal achievement
- Completed stories and story points
- Velocity trend
- Next sprint preview
- Screenshots/videos of new features

---

### Stakeholder Communication Plan

#### Community Members (Players/Users)
**Frequency**: Weekly updates  
**Channel**: Discord, blog, social media  
**Content**: Feature highlights, progress, what's next

#### Beta Testers
**Frequency**: Per sprint + ad-hoc  
**Channel**: Dedicated Discord channel  
**Content**: Test builds, specific testing requests, bug reporting

#### Content Creators
**Frequency**: Monthly + major releases  
**Channel**: Direct messages, Discord  
**Content**: Early access builds, feature showcases, collaboration opportunities

#### Mod Developers
**Frequency**: Quarterly + API changes  
**Channel**: GitHub, developer Discord channel  
**Content**: API documentation, integration guides, breaking changes

---

### Cross-Team Coordination Mechanisms

**Note**: Currently solo developer, but establishing processes for future

#### Future Multi-Developer Coordination
- **Daily Stand-Ups**: If team grows, 15-minute daily sync
- **Shared Sprint Board**: Kanban board with real-time updates
- **Code Review Process**: Pull request reviews before merge
- **Pair Programming**: For complex architectural decisions

#### Community Contributor Coordination
- **Contribution Guidelines**: Clear docs for how to contribute
- **Issue Labels**: "good first issue", "help wanted", etc.
- **Response Time**: Acknowledge contributions within 24 hours
- **Recognition**: Credit contributors in changelogs and updates

---

### Decision-Making Framework

#### Solo Developer Decisions
**Scope**: Sprint planning, technical implementation, code architecture  
**Process**: Independent decision with documentation  
**Communication**: Explain reasoning in updates

#### Community Input Decisions
**Scope**: Feature priorities, balance changes, major direction  
**Process**: Solicit feedback, consider input, make final call  
**Communication**: Transparent about how feedback influenced decision

#### Blockers and Escalation
**For Critical Blockers**:
1. Document the blocker clearly
2. Assess impact on sprint/phase goals
3. Communicate to community within 24 hours
4. Adjust plan using contingency strategies