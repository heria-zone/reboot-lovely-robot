# Sprint Task: InternalEntity Consolidation

**Status**: 🔄 ACTIVE
**Started**: 2026-04-27
**Target Completion**: 2026-05-25
**Priority**: High
**Complexity**: Very High

## Sprint Goal

Execute ADR_012 — rebuild HZLib's `InternalEntity` as the single shared base for both robot and monster entities, migrate lovelylib's robot hierarchy to the new string-based variant system, and complete the rename chain specified by Serge.

## Strategic Context

**Source ADR**: `docs/development/decisions/ADR_012_InternalEntity_Consolidation.md`

**Why this sprint matters**: The current codebase has two parallel entity hierarchies that share nothing below `TamableAnimal`. Robots are on an old int-based texture system. This sprint creates the clean foundation that all future entity work builds on.

**Prerequisite**: Sprint 07 complete (Animation Profile System + Variant System) ✅

## Objectives

### Phase 0 — Rebuild HZLib `InternalEntityType` (NEW — must happen first)
- [x] Rebuild HZLib `InternalEntityType` — remove dead code, fix structural inconsistencies
- [x] Update lovelylib `NativeEntityType` to extend HZLib's `InternalEntityType`
- [x] Validate monsters_girls `NativeEntityType` still works

### Phase 1 — Rebuild HZLib `InternalEntity`
- [x] Expand HZLib's `InternalEntity` with solid foundations from lovelylib
- [x] Add `combatData` (CombatData) as required field — via `applyBaseAttributes()` / `createAttributes()`
- [x] Add `createAttributes()` static method
- [x] Migrate combat timers, combat mode, auto-heal from lovelylib
- [x] Add NBT backward compat migration (`TextureID` int → `TextureVariant` string)

### Phase 2 — Build HZLib `RobotEntity`
- [x] Create robot-specific tier in HZLib
- [x] Move all robot-specific fields from lovelylib's `InternalEntity`
- [x] `recalculateAttributes()` and registry lifecycle declared abstract (implemented in lovelylib)
- [x] Full NBT serialization for all robot-specific fields

### Phase 3 — Migrate lovelylib
- [x] Delete lovelylib's internal `InternalEntity`
- [x] Make `LovelyRobotEntity` extend HZLib's `RobotEntity` (via lovelylib's `RobotEntity`)
- [x] Rename `LovelyRobotEntity` → `RobotEntity` (lovelylib) — **in progress, class still named LovelyRobotEntity pending full rename**
- [x] Rename loader `RobotEntity` → `BaseRobotEntity`
- [x] Delete existing empty `BaseRobotEntity`
- [x] **Fix: `NOTIFICATION` field removed from `RobotEntity` (was defined on wrong class)**
- [x] **Fix: `recalculateAttributes()` implemented concretely in lovelylib `RobotEntity`**
- [x] **Fix: `registerRobot()`, `ensureRegistered()`, `unregisterRobot()` implemented concretely**
- [x] **Fix: `getCombatFeature()`, `getProtectionFeature()` helpers added**
- [x] **Fix: `getNotification()`/`setNotification()` now delegate to `isNotificationEnabled()`**

### Phase 4 — Migrate 16-Color Palette
- [ ] Update `NativeEntityType.withColorPalette()` to use `TextureVariantFeature`
- [ ] Add NBT backward compatibility migration
- [ ] Update `handleTexture()` dye interaction
- [ ] Update all renderers
- [ ] Update all three mod variants (Legacy, Tribute, Reboot)

### Phase 5 — Update MonsterEntity
- [ ] Validate `MonsterEntity` works with rebuilt base
- [ ] Remove any fields now provided by base
- [ ] Confirm `combatData` initialization correct for monsters

---

## Implementation Tasks

### Phase 0: Rebuild HZLib `InternalEntityType` (NEW — prerequisite for everything)

#### Task 0.1 — Rebuild HZLib `InternalEntityType`
- **Priority**: Critical (blocks all other tasks)
- **Story Points**: 3
- **Location**: `sources/common/hzlib-1.21.1/Common/src/main/java/net/heriazone/hzlib/api/entity/InternalEntityType.java`
- **Description**: The current HZLib `InternalEntityType` is partially migrated with structural inconsistencies. Rebuild it cleanly.
- **Remove**:
  - `addTextures()` and `addAnimations()` abstract methods — dead code, never implemented anywhere
  - `populateTextures(ResourceMap<InternalTextureVariant<?>, ...>)`, `populateModels()`, `populateAnimators()` abstract methods — replaced by `configureVariants()`
  - Enum-keyed resource maps (`ResourceMap<EntityVariantTexture, ...>`) — replaced by feature system
  - Legacy `getTexture(String)`, `getModel(String)`, `getAnimator(String)` methods
- **Keep**:
  - Feature system: `withFeature()`, `getFeature()`, `hasFeature()`
  - `withCombatStats()` — fluent builder
  - `CombatData data` — base stats
  - `key`, `name` fields
  - `createTranslation()` abstract method
  - `configureVariants()` hook — called in constructor
  - Variant access methods: `getTextureVariant()`, `getModelVariant()`, `getAnimatorVariant()`, etc.
- **Acceptance Criteria**:
  - [ ] No `addTextures()` or `addAnimations()` abstract methods
  - [ ] No enum-keyed resource maps
  - [ ] `configureVariants()` is the single hook for variant registration
  - [ ] Feature system intact
  - [ ] HZLib Common compiles with 0 errors

---

#### Task 0.2 — Update lovelylib `NativeEntityType` to Extend HZLib's `InternalEntityType`
- **Priority**: Critical
- **Story Points**: 2
- **Location**: `sources/common/lovelylib-1.21.1/Common/src/main/java/net/heriazone/lovelylib/common/entity/NativeEntityType.java`
- **Description**: Migrate `NativeEntityType` from lovelylib's `InternalEntityType` to HZLib's rebuilt version.
- **Changes**:
  1. Update import: `net.heriazone.lovelylib.hzlib.api.entity.InternalEntityType` → `net.heriazone.hzlib.api.entity.InternalEntityType`
  2. Replace `populateTextures()`, `populateModels()`, `populateAnimators()` implementations with `configureVariants()` override
  3. `configureVariants()` registers `ModelVariantFeature` (`"default"`, `"armed"`) and `AnimatorVariantFeature` (`"default"`)
  4. `withColorPalette()` registers `TextureVariantFeature` (16 color keys) — this is Task 4.1 but the hook is set up here
  5. Delete lovelylib's `InternalEntityType` (`net.heriazone.lovelylib.hzlib.api.entity.InternalEntityType`)
- **`configureVariants()` implementation**:
  ```java
  @Override
  protected void configureVariants() {
      withFeature(ModelVariantFeature.class, new ModelVariantFeature()
          .withVariants(key, "default", "armed")
          .withDefault(key, "default"));
      withFeature(AnimatorVariantFeature.class, new AnimatorVariantFeature()
          .withVariants(key, "default")
          .withDefault(key, "default"));
      // Texture variants registered separately via withColorPalette()
  }
  ```
- **Acceptance Criteria**:
  - [ ] `NativeEntityType` extends `net.heriazone.hzlib.api.entity.InternalEntityType`
  - [ ] `configureVariants()` registers model and animator variants
  - [ ] lovelylib's `InternalEntityType` file deleted
  - [ ] lovelylib Common compiles with 0 errors

---

#### Task 0.3 — Validate monsters_girls `NativeEntityType`
- **Priority**: High
- **Story Points**: 1
- **Location**: `sources/monsters/monsters_girls-1.21.1/Common/src/main/java/net/heriazone/monsters_girls/entity/NativeEntityType.java`
- **Description**: monsters_girls `NativeEntityType` already extends HZLib's `InternalEntityType` and overrides `configureVariants()`. After the rebuild, verify it still compiles and works correctly.
- **Acceptance Criteria**:
  - [ ] monsters_girls Common compiles with 0 errors
  - [ ] monsters_girls Fabric compiles with 0 errors
  - [ ] Gourdragora and Mushroom Brown spawn correctly (regression check)

---

### Phase 1: Rebuild HZLib `InternalEntity`

#### Task 1.1 — Expand HZLib `InternalEntity` with Solid Foundations
- **Priority**: Critical (blocks everything)
- **Story Points**: 8
- **Location**: `sources/common/hzlib-1.21.1/Common/src/main/java/net/heriazone/hzlib/api/entity/InternalEntity.java`
- **Description**: The current HZLib `InternalEntity` has the variant system but lacks the solid foundations from lovelylib. Add the missing pieces.
- **Fields to add**:
  - `combatData` (CombatData) — initialized from `nativeEntity.getData()` in constructor
  - `waryTimer`, `autoHealTimer` (int) — combat mode timers
  - `combatMode`, `autoHeal` (boolean) — combat state flags
- **Methods to add**:
  - `createAttributes(NativeEntityType)` — static, reads from `CombatData`, sets Minecraft attributes
  - `handleCombatMode()` — wary timer management
  - `handleAutoHeal()` — auto-heal logic
  - `handleActivateCombatMode()` — activates combat mode
  - Registry lifecycle stubs: `registerEntity()`, `ensureRegistered()`, `unregisterEntity()` — abstract or empty, implemented in `RobotEntity`
- **NBT**: Add `combatData` serialization to `addAdditionalSaveData()` / `readAdditionalSaveData()`
- **Acceptance Criteria**:
  - [ ] `combatData` initialized from `nativeEntity.getData()` in constructor
  - [ ] `createAttributes()` sets MAX_HEALTH, ATTACK_DAMAGE, ATTACK_SPEED, MOVEMENT_SPEED, ARMOR, ARMOR_TOUGHNESS, KNOCKBACK_RESISTANCE
  - [ ] `handleCombatMode()` and `handleAutoHeal()` work correctly
  - [ ] HZLib Common compiles with 0 errors

---

#### Task 1.2 — Validate MonsterEntity Still Works
- **Priority**: High
- **Story Points**: 2
- **Location**: `sources/monsters/monsters_girls-1.21.1/`
- **Description**: After expanding HZLib's `InternalEntity`, verify `MonsterEntity` still compiles and functions correctly. It should benefit from the new `combatData` field and `createAttributes()` method.
- **Acceptance Criteria**:
  - [ ] monsters_girls builds with 0 errors
  - [ ] `MonsterEntity.updateAttributesFromCombatData()` can be replaced with `createAttributes()` call
  - [ ] Gourdragora and Mushroom Brown spawn correctly

---

### Phase 2: Build HZLib `RobotEntity`

#### Task 2.1 — Create HZLib `RobotEntity`
- **Priority**: Critical
- **Story Points**: 10
- **Location**: `sources/common/hzlib-1.21.1/Common/src/main/java/net/heriazone/hzlib/api/entity/RobotEntity.java`
- **Description**: The existing `RobotEntity.java` in HZLib is a stub. Rebuild it as the robot-specific tier with all fields from lovelylib's `InternalEntity` that are robot-specific.
- **Fields to add** (from lovelylib's `InternalEntity`):
  - `combatLevelStats` (CombatLevelStats)
  - `protectionStats` (ProtectionStats)
  - `enchantmentStats` (EnchantmentStats)
  - `expTracker` (ExperienceTracker)
  - `combatStatsNBT`, `protectionStatsNBT`, `enchantmentStatsNBT` (NBT handlers)
- **EntityDataAccessors to add** (from lovelylib's `LovelyRobotEntity`):
  - `LEVEL`, `EXP`, `MAX_LEVEL` (int)
  - `FIRE_PROTECTION`, `FALL_PROTECTION`, `BLAST_PROTECTION`, `PROJECTILE_PROTECTION` (int)
  - `AUTO_ATTACK` (boolean)
  - `BASE_X`, `BASE_Y`, `BASE_Z` (float)
  - `IS_IN_SITTING_POSE` (boolean)
  - `CURRENT_HEALTH` (float)
- **Methods to add**:
  - `recalculateAttributes()` — feature-based stat calculation
  - Registry lifecycle: `registerRobot()`, `ensureRegistered()`, `unregisterRobot()`
  - All level/exp/protection/enchantment accessors
- **Acceptance Criteria**:
  - [ ] All robot-specific fields present
  - [ ] `recalculateAttributes()` uses `CombatLevelFeature`, `EnchantmentFeature`, `ProtectionFeature`
  - [ ] Registry lifecycle methods work correctly
  - [ ] HZLib Common compiles with 0 errors

---

### Phase 3: Migrate lovelylib

#### Task 3.1 — Delete lovelylib's Internal `InternalEntity`
- **Priority**: Critical
- **Story Points**: 3
- **Location**: `sources/common/lovelylib-1.21.1/Common/src/main/java/net/heriazone/lovelylib/hzlib/api/entity/InternalEntity.java`
- **Description**: Delete lovelylib's own `InternalEntity` and make `LovelyRobotEntity` extend HZLib's `RobotEntity` instead.
- **Steps**:
  1. Update `LovelyRobotEntity` to extend `net.heriazone.hzlib.api.entity.RobotEntity`
  2. Remove all fields now provided by the base classes
  3. Remove duplicate `EntityDataAccessor` definitions
  4. Update constructor to call `super(entityType, level)` (HZLib's constructor signature)
  5. Delete `InternalEntity.java` from lovelylib's internal hzlib copy
- **Acceptance Criteria**:
  - [ ] `LovelyRobotEntity` extends HZLib's `RobotEntity`
  - [ ] No duplicate `EntityDataAccessor` definitions
  - [ ] lovelylib Common compiles with 0 errors

---

#### Task 3.2 — Rename Chain
- **Priority**: High
- **Story Points**: 3
- **Description**: Execute the rename chain specified by Serge.
- **Renames**:
  1. `LovelyRobotEntity` → `RobotEntity` (in `lovelylib.common.entity` package)
  2. Loader `RobotEntity` (Fabric/Forge/NeoForge) → `BaseRobotEntity`
  3. Delete `lovelylib.api.entity.base.BaseRobotEntity` (empty passthrough, no value)
- **Update all references** in:
  - `LegacyEntities.java`, `TributeEntities.java`, `RebootEntities.java`
  - All renderer classes (`RobotRenderer`, `BunnyRenderer`, `KitsuneRenderer`)
  - `InternalAnimation.java` (all three loaders) — currently typed to `LovelyRobotEntity`
  - `LegacyRobotType`, `TributeRobotType`, `RebootRobotType`
  - All import statements across lovelylib and the three mod variants
- **Acceptance Criteria**:
  - [ ] `LovelyRobotEntity` no longer exists — replaced by `RobotEntity`
  - [ ] Loader `RobotEntity` no longer exists — replaced by `BaseRobotEntity`
  - [ ] Old `BaseRobotEntity` deleted
  - [ ] All three mod variants compile with 0 errors

---

### Phase 4: Migrate 16-Color Palette

#### Task 4.1 — Update `NativeEntityType.withColorPalette()`
- **Priority**: High
- **Story Points**: 5
- **Location**: `sources/common/lovelylib-1.21.1/Common/src/main/java/net/heriazone/lovelylib/common/entity/NativeEntityType.java`
- **Description**: Replace the int-based color palette with `TextureVariantFeature` using string keys.
- **Color key mapping** (from `EntityTexture` enum):
  ```
  0=white, 1=orange, 2=magenta, 3=light_blue, 4=yellow, 5=lime,
  6=pink, 7=gray, 8=light_gray, 9=cyan, 10=purple, 11=blue,
  12=brown, 13=green, 14=red, 15=black
  ```
- **`withColorPalette()` new implementation**:
  ```java
  withFeature(TextureVariantFeature.class, new TextureVariantFeature()
      .withVariants(key, "white", "orange", "magenta", "light_blue", "yellow", "lime",
                        "pink", "gray", "light_gray", "cyan", "purple", "blue",
                        "brown", "green", "red", "black")
      .withDefault(key, "white"));
  ```
- **Acceptance Criteria**:
  - [ ] `withColorPalette()` registers 16 string-keyed texture variants
  - [ ] `getColorTexture(EntityTexture)` still works (for backward compat during migration)
  - [ ] lovelylib Common compiles with 0 errors

---

#### Task 4.2 — NBT Backward Compatibility Migration
- **Priority**: High
- **Story Points**: 3
- **Location**: HZLib `InternalEntity.readAdditionalSaveData()`
- **Description**: When loading a robot saved with the old `TextureID` (int) format, convert to the new `TextureVariant` (string) format.
- **Migration logic**:
  ```java
  // In readAdditionalSaveData():
  if (!nbt.contains("TextureVariant") && nbt.contains("TextureID")) {
      int oldId = nbt.getInt("TextureID");
      String newKey = EntityTexture.byId(oldId).getName(); // "white", "orange", etc.
      setTextureVariant(newKey);
  }
  ```
- **Acceptance Criteria**:
  - [ ] Robots saved with `TextureID=5` load with `TextureVariant="lime"` (correct mapping)
  - [ ] Robots saved with `TextureVariant` string load correctly without migration
  - [ ] All 16 colors migrate correctly

---

#### Task 4.3 — Update Dye Interaction and Renderers
- **Priority**: High
- **Story Points**: 4
- **Location**: `LovelyRobotEntity` (renamed to `RobotEntity`), all renderer classes
- **Description**: Update all code that uses `getTextureID()` / `setTexture(EntityTexture)` to use `getTextureVariant()` / `setTextureVariant(String)`.
- **Changes**:
  - `handleTexture()` — dye interaction: `setTexture(EntityTexture.WHITE)` → `setTextureVariant("white")`
  - `handleItemDrop()` — NBT: `nbt.putInt(STAT_COLOR, getTextureID())` → `nbt.putString(STAT_COLOR, getTextureVariant())`
  - All renderer classes that call `getTextureID()` or `getTexture()`
  - `InternalModel.getTextureResource()` — already uses `getCurrentTexture()` which resolves via variant system ✓
- **Acceptance Criteria**:
  - [ ] Dye interaction changes robot color correctly using string keys
  - [ ] Robot drops contain correct color string in NBT
  - [ ] All renderers compile with 0 errors

---

#### Task 4.4 — Update Three Mod Variants
- **Priority**: High
- **Story Points**: 3
- **Location**: `sources/legacy/llovelyr-1.21.1/`, `sources/tribute/tlovelyr-1.21.1/`, `sources/reboot/rlovelyr-1.21.1/`
- **Description**: Update all three mod variants to use the new class names and string-based texture system.
- **Changes per mod**:
  - Update entity registration to use `BaseRobotEntity` (renamed from `RobotEntity`)
  - Update renderer registrations
  - Update any direct references to `LovelyRobotEntity`
- **Acceptance Criteria**:
  - [ ] Legacy builds with 0 errors
  - [ ] Tribute builds with 0 errors
  - [ ] Reboot builds with 0 errors

---

### Phase 5: Validate

#### Task 5.1 — Runtime Validation
- **Priority**: Critical
- **Story Points**: 3
- **Description**: In-game validation that everything works correctly after the migration.
- **Test checklist**:
  - [ ] Spawn all 7 robot types (Legacy) — correct textures, correct stats
  - [ ] Dye interaction changes robot color correctly
  - [ ] Robot drops contain correct color in NBT
  - [ ] Spawn robot, save world, reload — texture and stats preserved
  - [ ] Spawn robot with old save (TextureID int) — migrates correctly to string key
  - [ ] All 7 robot types animate correctly (idle, walk, rest, sit, attack)
  - [ ] Gourdragora spawns correctly (from Sprint 07 — regression check)
  - [ ] Mushroom Brown spawns with correct biome texture (regression check)

---

## Technical Notes

### Constructor Chain
HZLib's `InternalEntity` constructor takes `(EntityType, Level)`. The `nativeEntity` reference is set separately. lovelylib's `RobotEntity` (renamed from `LovelyRobotEntity`) constructor currently takes `(EntityType, Level, NativeEntityType)` and passes `nativeEntity` to the old `InternalEntity`. After migration, the constructor must:
1. Call `super(entityType, level)`
2. Set `this.nativeEntity = nativeEntity`
3. Initialize attributes from `nativeEntity.getData()`

### EntityDataAccessor ID Stability
Removing `TEXTURE_ID` and `MODEL_ID` from lovelylib's `InternalEntity` shifts the `EntityDataAccessor` ID assignments for `LovelyRobotEntity`'s fields. This affects live entities (synced data) but not saved data (NBT). A world reload after the update is required. Document this in the release notes.

### `InternalAnimation` Type Update
The lovelylib `InternalAnimation` classes (Fabric/Forge/NeoForge) are currently typed to `LovelyRobotEntity`. After the rename, they must be updated to `RobotEntity` (the new name for `LovelyRobotEntity`). This is a straightforward find-and-replace.

### `EntityTexture.getName()` Method
The NBT migration requires `EntityTexture.byId(int).getName()` to return the string key (`"white"`, `"orange"`, etc.). Verify this method exists on the `EntityTexture` enum, or add it.

---

## Story Points Summary

| Task | Points | Phase | Status |
|------|--------|-------|--------|
| 0.1 Rebuild HZLib InternalEntityType | 3 | 0 | ⬜ |
| 0.2 Update lovelylib NativeEntityType | 2 | 0 | ⬜ |
| 0.3 Validate monsters_girls NativeEntityType | 1 | 0 | ⬜ |
| 1.1 Expand HZLib InternalEntity | 8 | 1 | ⬜ |
| 1.2 Validate MonsterEntity | 2 | 1 | ⬜ |
| 2.1 Build HZLib RobotEntity | 10 | 2 | ⬜ |
| 3.1 Delete lovelylib InternalEntity | 3 | 3 | ⬜ |
| 3.2 Rename Chain | 3 | 3 | ⬜ |
| 4.1 Update withColorPalette() | 5 | 4 | ⬜ |
| 4.2 NBT Backward Compatibility | 3 | 4 | ⬜ |
| 4.3 Update Dye Interaction + Renderers | 4 | 4 | ⬜ |
| 4.4 Update Three Mod Variants | 3 | 4 | ⬜ |
| 5.1 Runtime Validation | 3 | 5 | ⬜ |
| **Total** | **50** | | |

---

## Dependencies

- Sprint 07 complete ✅ (Animation Profile System, Variant System)
- ADR_012 accepted ✅
- HZLib jar available in lovelylib `libs/` folder ✅

## Risks

### Risk 1: EntityDataAccessor ID Shift
**Probability**: Certain (it will happen)
**Impact**: Medium — live entities in loaded chunks may have wrong synced data
**Mitigation**: Require full world reload after update. Document in release notes.

### Risk 2: NBT Migration Correctness
**Probability**: Low if tested
**Impact**: High — wrong color mapping corrupts robot appearance permanently
**Mitigation**: Test all 16 colors explicitly in Task 5.1.

### Risk 3: Three Mod Variants Scope
**Probability**: Medium — each mod may have unique references
**Impact**: Medium — compilation failures
**Mitigation**: Search for all references to `LovelyRobotEntity` and `RobotEntity` before starting Task 3.2.

## References

- `docs/development/decisions/ADR_012_InternalEntity_Consolidation.md` — full design including InternalEntityType consolidation
- `docs/development/decisions/ADR_009_Entity_Hierarchy_Refactoring.md`
- `docs/development/decisions/ADR_010_Animation_Profile_System.md`
- `docs/development/decisions/ADR_011_Variant_and_Spawn_System_Refactoring.md`
- `temp/27-04-2026 Entity Architecture & Variant System Discussion.md`
- `sources/common/lovelylib-1.21.1/Common/src/main/java/net/heriazone/lovelylib/hzlib/api/entity/InternalEntity.java` — source of solid foundations
- `sources/common/lovelylib-1.21.1/Common/src/main/java/net/heriazone/lovelylib/hzlib/api/entity/InternalEntityType.java` — source of solid type foundations
- `sources/common/hzlib-1.21.1/Common/src/main/java/net/heriazone/hzlib/api/entity/InternalEntity.java` — source of new variant system
- `sources/common/hzlib-1.21.1/Common/src/main/java/net/heriazone/hzlib/api/entity/InternalEntityType.java` — source of configureVariants() hook
- `sources/monsters/monsters_girls-1.21.1/Common/src/main/java/net/heriazone/monsters_girls/entity/MonsterEntity.java` — reference for monster path
- `sources/common/lovelylib-1.21.1/Common/src/main/java/net/heriazone/lovelylib/common/entity/NativeEntityType.java` — robot type base to migrate
- `sources/common/lovelylib-1.21.1/Common/src/main/java/net/heriazone/lovelylib/source/legacy/LegacyRobotType.java` — reference for three mod variants
- `sources/common/lovelylib-1.21.1/Common/src/main/java/net/heriazone/lovelylib/source/reboot/RebootRobotType.java`
- `sources/common/lovelylib-1.21.1/Common/src/main/java/net/heriazone/lovelylib/source/tribute/TributeRobotType.java`

---

## ADR_017 — OverlayFeature Implementation (Completed 2026-06-20)

**Source ADR**: `docs/development/decisions/ADR_017_OverlayFeature_Composable_Visual_Layer_System.md`

This work was carried out across multiple sessions. All implementation tasks are complete and ready for runtime validation.

### Completed Work

#### HZLib Common — `OverlaySlot.java`
- [x] Added `entryDynamic(stageKey, pathResolver)` — dynamic path for INTERACTIVE/RANDOM slots
- [x] Added `entryDynamic(stageKey, pathResolver, colorProvider)` — dynamic path + tint
- [x] Added `entryDynamicConditional(stageKey, pathResolver, condition)` — dynamic path + predicate (named distinctly to avoid Java overload ambiguity with `Function` vs `Predicate`)
- [x] Added `alwaysDynamic(key, pathResolver)` — ALWAYS slot whose texture resolves from entity state at render time
- [x] Added 3-arg private `Entry` constructor `(String, Predicate, Function)` to preserve backward compat with existing 3-arg `new Entry(path, null, null)` call sites
- [x] Fixed `resolveConditional()` to call entity-aware `getTexturePath(entity)` on each entry

#### HZLib Fabric — `OverlayLayer.java`
- [x] Fixed constructor: ALWAYS slot with dynamic path no longer pre-caches `alwaysTexture` (sets null, resolves per frame)
- [x] Fixed `resolveTexturePath()`: RANDOM/INTERACTIVE slots now look up the `Entry` by stage key and call `entry.getTexturePath(entity)` for dynamic path resolution
- [x] Fixed `resolveColor()`: RANDOM/INTERACTIVE slots look up the `Entry` by stage key (not rendered path) so color providers work with dynamic-path entries

#### HZLib Common — `InternalEntity.java` (already complete from prior session)
- [x] `registerOverlayData()` — registers `SynchedEntityData<String>` per persistent slot
- [x] `getOverlaySlot(key)` / `setOverlaySlot(key, path)` / `cycleOverlaySlot(key)`
- [x] `initializeRandomVariants()` — seeds RANDOM slots at spawn
- [x] NBT `addAdditionalSaveData` / `readAdditionalSaveData` with `"OverlaySlots"` compound

#### monsters_girls — `GourdragoraType.java`
- [x] Slot key constants: `SLOT_CARVING`, `SLOT_FACE_COVER`, `SLOT_EMISSIVE`
- [x] `buildTintedOverlay(int tintArgb)` — INTERACTIVE carving (5 stages, grayscale+tint) + ALWAYS emissive; wired on `GOLDEN` (amber `0xFFD4A017`) and `LUMINA` (pale yellow `0xFFF0E68C`)
- [x] `buildJackoOverlay()` — CONDITIONAL face_cover + INTERACTIVE carving (6 stages, full-color) + ALWAYS emissive; wired on `JACKO`
- [x] Dynamic path helpers: `sizeSegment()`, `carvingPath()`, `carvingJackoPath()`, `faceCoverPath()`, `emissivePath()` — all derive size from `MODEL_VARIANT` at render time

#### monsters_girls — `MandrakeType.java`
- [x] Slot key constants: `SLOT_HAIR`, `SLOT_EMISSIVE`
- [x] `FLOWER` variant: RANDOM hair slot (4 options: twintails_blue, twintails_green, flower_crown_green, ponytails_yellow) + ALWAYS emissive
- [x] `CHORUS` and `FRUCTUS` unchanged — no layer assets authored

#### monsters_girls — `MushroomType.java`
- [x] Slot key constants: `SLOT_HAT`, `SLOT_COSTUME`
- [x] `BROWN_GIRL_COSTUME` — RANDOM hat (3 pumpkin colours) + CONDITIONAL costume shown in October; wired on `BROWN`, `INFERNAL`, `MOLTEN`
- [x] `DEFAULT_GIRL_COSTUME` — RANDOM hat (3 witch colours) + CONDITIONAL costume shown in October; wired on `AMANITA`, `CRIMSON`, `SOUL_WANDERER`, `WARPED`
- [x] `INKCAPS`, `PUFFBALL`, `SNOWBALL` unchanged — no layer assets authored

#### monsters_girls — `GourdragoraEntity.java` (Fabric)
- [x] `handleSpecificInteractions()`: shears branch calls `cycleOverlaySlot(SLOT_CARVING)`, plays sound, damages shears 1 durability
- [x] Placeholder sound: `GOURDRAGORA_ROAR` at 0.6f/1.2f — **TODO: replace with dedicated carve sound once audio asset authored**

#### monsters_girls — `MonstersEntities.java` (Fabric)
- [x] `registerRender()` updated: entities with `OverlayFeature` use factory lambdas calling `buildOverlayLayers()` after renderer construction
- [x] Affected: GOURDRAGORA_GOLDEN, GOURDRAGORA_LUMINA, GOURDRAGORA_JACKO, MANDRAKE_FLOWER, MUSHROOM_AMANITA, MUSHROOM_BROWN, MUSHROOM_CRIMSON, MUSHROOM_INFERNAL, MUSHROOM_MOLTEN, MUSHROOM_SOUL_WANDERER, MUSHROOM_WARPED
- [x] `OverlayFeature` import added

### Open Items / Follow-up

- [ ] **Dedicated carve sound**: Author `GOURDRAGORA_CARVE` OGG + register in `MonstersSounds` and `sounds.json`, then update `GourdragoraEntity.handleSpecificInteractions()`
- [ ] **Runtime validation**: Spawn each entity family in-game and verify overlay layers render correctly
- [ ] **Forge/NeoForge renderer registration**: Mirror `registerRender()` changes from Fabric to Forge and NeoForge `MonstersEntities` when those loaders are activated
- [ ] **Robot renderer migration** (planned follow-up per ADR_017): Move robot collar/headphone layer declarations to `OverlayFeature` on each robot `NativeEntityType`, collapse the three renderer subclasses into one

### Modified Files
- `sources/common/hzlib-1.21.1/Common/src/main/java/net/heriazone/hzlib/api/entity/features/overlay/OverlaySlot.java`
- `sources/common/hzlib-1.21.1/Fabric/src/main/java/net/heriazone/hzlib/api/layer/OverlayLayer.java`
- `sources/monsters/monsters_girls-1.21.1/Common/src/main/java/net/heriazone/monsters_girls/entity/custom/GourdragoraType.java`
- `sources/monsters/monsters_girls-1.21.1/Common/src/main/java/net/heriazone/monsters_girls/entity/custom/MandrakeType.java`
- `sources/monsters/monsters_girls-1.21.1/Common/src/main/java/net/heriazone/monsters_girls/entity/custom/MushroomType.java`
- `sources/monsters/monsters_girls-1.21.1/Fabric/src/main/java/net/heriazone/monsters_girls/entity/custom/GourdragoraEntity.java`
- `sources/monsters/monsters_girls-1.21.1/Fabric/src/main/java/net/heriazone/monsters_girls/source/MonstersEntities.java`
