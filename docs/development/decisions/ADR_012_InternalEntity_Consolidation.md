# ADR 012: InternalEntity Consolidation — New Shared Base for Robots and Monsters

**Status**: Accepted
**Date**: 2026-04-27
**Decision Makers**: Serge, Mike, Raul
**Consulted**: Architecture Review (27-04-2026 Entity Architecture & Variant System Discussion)

## Context

The codebase currently has two separate `InternalEntity` implementations that share `TamableAnimal` as their only common root:

**lovelylib's `InternalEntity`** (`net.heriazone.lovelylib.hzlib.api.entity.InternalEntity`):
- Solid, battle-tested implementation with complete NBT handling, combat stats, state management, registry lifecycle, and all robot behaviors
- Uses the **old int-based system**: `TEXTURE_ID` (int), `MODEL_ID` (int) — 16-color palette keyed by `EntityTexture` enum
- Contains robot-specific fields that don't belong in a shared base (leveling, protection, enchantments)
- `LovelyRobotEntity` extends this directly

**HZLib's `InternalEntity`** (`net.heriazone.hzlib.api.entity.InternalEntity`):
- Uses the **new string-based variant system**: `TEXTURE_VARIANT`, `MODEL_VARIANT`, `ANIMATOR_VARIANT` (String keys)
- Has the `initializeSpawnVariants()` hook (ADR_011)
- Monsters & Girls `MonsterEntity` already extends this
- Missing the solid foundations from lovelylib: no combat stats, no wary/autoHeal timers, no registry lifecycle

The result is two parallel hierarchies that cannot share code, and the robot system is on an old data model that needs to be replaced.

## Decision

**Rebuild HZLib's `InternalEntity` from scratch**, combining:
- The solid foundations of lovelylib's `InternalEntity` (complete NBT, combat stats, state management, registry lifecycle, interaction framework)
- The new string-based variant system already in HZLib (`TEXTURE_VARIANT`, `MODEL_VARIANT`, `ANIMATOR_VARIANT`)

This creates a single shared base that both `RobotEntity` (HZLib, robot-specific tier) and `MonsterEntity` (monsters_girls) inherit from.

## Target Hierarchy

```
TamableAnimal (Minecraft)
  ↓
HZLib InternalEntity  ← REBUILT: solid foundations + new string variant system
  │
  ├── HZLib RobotEntity  ← robot-specific tier (level, protection, enchantments)
  │     ↓
  │   LovelyLib RobotEntity  ← renamed from LovelyRobotEntity (AI goals, behaviors)
  │     ↓
  │   LovelyLib BaseRobotEntity  ← renamed from loader RobotEntity (GeckoLib bridge)
  │     ↓
  │   Tribute / Legacy / Reboot instances
  │
  └── MonsterEntity (monsters_girls)  ← already correct, minor updates only
        ↓
      WildTamableEntity / actual monster instances
```

## Rename Chain

Per Serge's specification:
- `LovelyRobotEntity` → **`RobotEntity`** (the lovelylib robot base — the big class with all robot logic)
- Current loader `RobotEntity` → **`BaseRobotEntity`** (the thin GeckoLib bridge)
- Existing `lovelylib.api.entity.base.BaseRobotEntity` → **deleted** (empty passthrough, no value)

## Field Map — New HZLib `InternalEntity`

### Shared base (everything both robots and monsters need):

| Field | Type | Source | Notes |
|---|---|---|---|
| `TEXTURE_VARIANT` | `String` | HZLib (keep) | Replaces lovelylib's `TEXTURE_ID` (int) |
| `MODEL_VARIANT` | `String` | HZLib (keep) | Replaces lovelylib's `MODEL_ID` (int) |
| `ANIMATOR_VARIANT` | `String` | HZLib (keep) | New — lovelylib didn't have this |
| `STATE` | `int` | HZLib (keep) | Same semantics as lovelylib's — one definition |
| `NOTIFICATION_ENABLED` | `boolean` | HZLib (rename from `NOTIFICATION`) | Same concept |
| `combatData` | `CombatData` | HZLib (already exists) | Base HP/attack/speed/armor — no leveling. Required by all entities for `createAttributes()` |
| `waryTimer` | `int` | lovelylib | Both robots and monsters use combat mode |
| `autoHealTimer` | `int` | lovelylib | Both use auto-heal |
| `combatMode` | `boolean` | lovelylib | Both use this |
| `autoHeal` | `boolean` | lovelylib | Both use this |
| `nativeEntity` | `InternalEntityType<?>` | Both | Already in HZLib |

### Moves to HZLib `RobotEntity` (robot-specific):

| Field | Type | Source |
|---|---|---|
| `LEVEL` | `int` | lovelylib `LovelyRobotEntity` |
| `EXP` | `int` | lovelylib `LovelyRobotEntity` |
| `MAX_LEVEL` | `int` | lovelylib `LovelyRobotEntity` |
| `FIRE/FALL/BLAST/PROJECTILE_PROTECTION` | `int` | lovelylib `LovelyRobotEntity` |
| `AUTO_ATTACK` | `boolean` | lovelylib `LovelyRobotEntity` |
| `BASE_X/Y/Z` | `float` | lovelylib `LovelyRobotEntity` |
| `IS_IN_SITTING_POSE` | `boolean` | lovelylib `LovelyRobotEntity` |
| `CURRENT_HEALTH` | `float` | lovelylib `LovelyRobotEntity` |
| `combatLevelStats` | `CombatLevelStats` | lovelylib `InternalEntity` |
| `protectionStats` | `ProtectionStats` | lovelylib `InternalEntity` |
| `enchantmentStats` | `EnchantmentStats` | lovelylib `InternalEntity` |
| `expTracker` | `ExperienceTracker` | lovelylib `InternalEntity` |

### Stays in `MonsterEntity` (already correct):

| Field | Type |
|---|---|
| `HAS_BELLY` | `boolean` |
| `PLANTING_ENABLED` | `boolean` |
| `SOUND_ENABLED` | `boolean` |
| Belly progression logic | — |

## The 16-Color Migration

The robot texture system migrates from int IDs to string keys. Behavior is identical — 16 colors, dye interaction, NBT-based texture selection.

**Before**: `TEXTURE_ID = 5` → `EntityTexture.byId(5)` → `EntityTexture.MAGENTA` → texture path

**After**: `TEXTURE_VARIANT = "magenta"` → `TextureVariantFeature` → texture path

**`NativeEntityType.withColorPalette()` becomes**:
```java
withFeature(TextureVariantFeature.class, new TextureVariantFeature()
    .withVariants(key,
        "white", "orange", "magenta", "light_blue", "yellow", "lime",
        "pink", "gray", "light_gray", "cyan", "purple", "blue",
        "brown", "green", "red", "black")
    .withDefault(key, "white"));
```

**NBT backward compatibility**: When loading, if `TextureID` (int) is present and `TextureVariant` (string) is absent, convert: `EntityTexture.byId(nbt.getInt("TextureID")).getName()` → set as `TEXTURE_VARIANT`. One-time migration in `readAdditionalSaveData()`.

**Dye interaction**: `handleTexture()` changes from `setTexture(EntityTexture.WHITE)` to `setTextureVariant("white")`. Same behavior.

**Model variant**: `MODEL_ID` (int, Default/Armed) → `MODEL_VARIANT` (String, `"default"`/`"armed"`). Same two states, different data type.

## Why Not Option B (Additive Extension)

Option B (make lovelylib's `InternalEntity` extend HZLib's) was rejected because:
1. It leaves the old int-based system in place — the exact problem we're solving
2. It creates a deeper inheritance chain with duplicate field definitions (`STATE` conflict)
3. It doesn't achieve the clean architecture Serge requires

The goal is one solid `InternalEntity` in HZLib that both hierarchies use. Option B produces two `InternalEntity` classes in the hierarchy, which is worse than the current state.

## CombatData as Base Requirement

`CombatData` (already in HZLib framework) contains: `maxHealth`, `attackDamage`, `attackSpeed`, `armor`, `armorToughness`, `knockbackResistance`, `moveSpeed`. No level, no experience.

Every entity — robot or monster — needs these base stats to call `createAttributes()`. This is the correct shared base. `CombatLevelStats` (level-based scaling) belongs only in `RobotEntity`.

## Implementation Plan

### Phase 1: Rebuild HZLib `InternalEntity`
1. Expand HZLib's `InternalEntity` with fields from lovelylib's version (combat timers, registry lifecycle, interaction framework, complete NBT)
2. Keep string-based variant system (`TEXTURE_VARIANT`, `MODEL_VARIANT`, `ANIMATOR_VARIANT`)
3. Add `combatData` (CombatData) as required field — initialized from `nativeEntity.getData()`
4. Add `createAttributes()` static method that reads from `CombatData`
5. Migrate `waryTimer`, `autoHealTimer`, `combatMode`, `autoHeal` from lovelylib

### Phase 2: Build HZLib `RobotEntity`
1. Extend HZLib's new `InternalEntity`
2. Add all robot-specific fields: level, exp, protection, sitting pose, health sync
3. Add `combatLevelStats`, `protectionStats`, `enchantmentStats`, `expTracker`
4. Implement `recalculateAttributes()` using feature system
5. Implement registry lifecycle (registerRobot, ensureRegistered, unregisterRobot)

### Phase 3: Migrate lovelylib
1. Delete lovelylib's internal `InternalEntity` (`net.heriazone.lovelylib.hzlib.api.entity.InternalEntity`)
2. Make `LovelyRobotEntity` extend HZLib's `RobotEntity` instead
3. Remove fields now provided by the base classes
4. Rename `LovelyRobotEntity` → `RobotEntity` (lovelylib package)
5. Rename loader `RobotEntity` → `BaseRobotEntity`
6. Delete existing `lovelylib.api.entity.base.BaseRobotEntity` (empty, no value)

### Phase 4: Migrate 16-Color Palette
1. Update `NativeEntityType.withColorPalette()` to use `TextureVariantFeature` with string keys
2. Add NBT backward compatibility migration in `readAdditionalSaveData()`
3. Update `handleTexture()` (dye interaction) to use string keys
4. Update all renderers that call `getTextureID()` to use `getTextureVariant()`
5. Update all three mod variants (Legacy, Tribute, Reboot)

### Phase 5: Update MonsterEntity
1. `MonsterEntity` already extends HZLib's `InternalEntity` — minimal changes
2. Remove any fields now provided by the rebuilt base
3. Validate that `combatData` initialization works correctly for monsters

## Migration Notes

### NBT Save Compatibility
- Robots saved with `TextureID` (int) will be migrated on first load to `TextureVariant` (string)
- All other NBT keys remain unchanged
- `CombatData` fields are already saved by `MonsterEntity` — no change needed there

### EntityDataAccessor ID Stability
- Removing `TEXTURE_ID` and `MODEL_ID` from lovelylib's `InternalEntity` changes the `EntityDataAccessor` ID assignments for `LovelyRobotEntity`'s fields
- This is a breaking change for existing worlds — the NBT migration handles texture, but the synced data IDs will shift
- **Mitigation**: The NBT migration ensures data is preserved on load. The synced data IDs only matter for live entities (not saved data), so a world reload after the update is sufficient

### Three Mod Variants
Legacy, Tribute, and Reboot all use `RobotEntity` (loader class) directly — they don't subclass it further. The rename from loader `RobotEntity` → `BaseRobotEntity` requires updating:
- `LegacyEntities.java`, `TributeEntities.java`, `RebootEntities.java` — entity type registration
- All renderer classes that reference `RobotEntity`
- All import statements

## Consequences

### Positive
- Single `InternalEntity` in HZLib — one source of truth for all entity base behavior
- Robots on the new string-based variant system — consistent with monsters
- `CombatData` as the universal base stats — `createAttributes()` works the same for all entities
- Clean hierarchy — no duplicate field definitions, no parallel trees
- Future entity types (NPCs, vehicles, pets) can extend HZLib's `InternalEntity` directly

### Negative
- Significant migration scope — touches lovelylib, all three mod variants, and monsters_girls
- NBT migration required for existing robot saves (texture ID → string key)
- `EntityDataAccessor` ID shift requires world reload after update

### Risks
- **NBT migration correctness**: The `TextureID` → `TextureVariant` migration must handle all 16 colors correctly. Test with robots of each color before release.
- **EntityDataAccessor ID shift**: If a player loads a world mid-session (without full reload), synced data may be misread. Require full restart after update.
- **Three mod variants**: All three must be updated simultaneously — partial updates will cause compilation failures.

## Related Decisions

- ADR_009: Entity Hierarchy Refactoring (original three-tier design — this ADR supersedes the implementation plan in ADR_009)
- ADR_010: Animation Profile System (animation system built on top of this hierarchy)
- ADR_011: Variant and Spawn System Refactoring (string-based variant system this ADR adopts)


---

## InternalEntityType Consolidation

The `InternalEntityType` situation mirrors the `InternalEntity` problem exactly — two implementations, one needed.

### Current State

**lovelylib's `InternalEntityType`** (`net.heriazone.lovelylib.hzlib.api.entity.InternalEntityType`):
- Resource maps keyed by **enum types**: `ResourceMap<EntityVariantTexture, ResourceLocation>`, `ResourceMap<EntityVariantModel, ResourceLocation>`, `ResourceMap<EntityVariantAnimator, ResourceLocation>`
- Abstract methods: `populateTextures()`, `populateModels()`, `populateAnimators()`, `createTranslation()`
- Feature system: `withFeature()`, `getFeature()`, `hasFeature()` — Class-keyed map
- `withCombatStats()` — fluent builder for base stats
- `CombatData data` — base stats container
- **Solid, battle-tested** — `NativeEntityType` extends this and it works

**HZLib's `InternalEntityType`** (`net.heriazone.hzlib.api.entity.InternalEntityType`):
- Resource maps keyed by **strings**: `ResourceMap<String, ResourceLocation>` — the new system
- Abstract methods: `populateTextures(ResourceMap<InternalTextureVariant<?>, ...>)` — **type mismatch with field**
- `configureVariants()` hook — called in constructor, overridden by subclasses
- Same feature system, same `withCombatStats()`, same `CombatData`
- Has `addTextures()` and `addAnimations()` abstract methods — **never implemented anywhere, dead code**
- **Partially migrated** — has TODO comments, structural inconsistencies

### The Concrete Target

```
HZLib InternalEntityType<T>  ← REBUILT: solid feature system + configureVariants() hook
  │  - key, name, CombatData data
  │  - withFeature(), getFeature(), hasFeature()
  │  - withCombatStats()
  │  - configureVariants() — hook, called in constructor
  │  - getTextureVariant(), getModelVariant(), getAnimatorVariant()
  │  - createTranslation() abstract
  │
  ├── lovelylib NativeEntityType  ← extends HZLib's InternalEntityType
  │     - overrides configureVariants() → registers TextureVariantFeature with 16 colors
  │     - withColorPalette() → populates TextureVariantFeature
  │     - withCombatStats() inherited
  │     - LevelFeature attached in constructor
  │
  └── monsters_girls NativeEntityType  ← already extends HZLib's InternalEntityType
        - overrides configureVariants() → registers texture/model/animator variants
        - already correct, minimal changes
```

### Decision

**Rebuild HZLib's `InternalEntityType` from scratch**, combining:
- The solid feature system and `withCombatStats()` from lovelylib's version
- The `configureVariants()` hook and string-based variant access methods from HZLib's version

### What the Rebuilt HZLib `InternalEntityType` Contains

**Keep from both (identical)**:
- Feature system: `withFeature()`, `getFeature()`, `hasFeature()`
- `withCombatStats()` — fluent builder
- `CombatData data` — base stats
- `key`, `name` fields
- `createTranslation()` abstract method

**Keep from HZLib (new system)**:
- `configureVariants()` hook — called in constructor, overridden by subclasses
- Variant access methods: `getTextureVariant(entityKey, variantKey)`, `getModelVariant()`, `getAnimatorVariant()`, `getDefaultTextureVariant()`, etc.
- String-keyed resource maps: `ResourceMap<String, ResourceLocation>` (or remove entirely if features handle all resolution)

**Remove**:
- Enum-keyed resource maps (`ResourceMap<EntityVariantTexture, ...>`) — replaced by feature system
- `populateTextures()`, `populateModels()`, `populateAnimators()` abstract methods — replaced by `configureVariants()`
- `addTextures()` and `addAnimations()` abstract methods — dead code, never implemented
- Legacy `getTexture(String)`, `getModel(String)`, `getAnimator(String)` methods — replaced by variant access methods

### Impact on `NativeEntityType`

**lovelylib's `NativeEntityType`** currently:
- Extends lovelylib's `InternalEntityType`
- Implements `populateTextures()`, `populateModels()`, `populateAnimators()` — puts enum keys into resource maps
- Calls `withColorPalette()` which populates `Map<EntityTexture, ResourceLocation>`

**After migration**:
- Extends HZLib's `InternalEntityType`
- Overrides `configureVariants()` instead of `populate*()` methods
- `configureVariants()` registers `ModelVariantFeature` (`"default"`, `"armed"`) and `AnimatorVariantFeature` (`"default"`)
- `withColorPalette()` registers `TextureVariantFeature` (16 color keys)

**monsters_girls `NativeEntityType`**:
- Already extends HZLib's `InternalEntityType` ✓
- Already overrides `configureVariants()` ✓
- Minimal changes — just verify it still compiles after the rebuild

### Model and Color as Independent Variant Dimensions

Robots have two orthogonal variant dimensions:
- **Model state**: `"default"` (unarmed) or `"armed"` (combat mode) — driven by `combatMode` flag
- **Color**: 16 dye colors (`"white"`, `"orange"`, etc.) — driven by player dye interaction

`NativeEntityType.configureVariants()` registers model and animator variants:
```java
@Override
protected void configureVariants() {
    // Model variants: default and armed
    withFeature(ModelVariantFeature.class, new ModelVariantFeature()
        .withVariants(key, "default", "armed")
        .withDefault(key, "default"));

    // Animator variants: default only (robots share one animation file)
    withFeature(AnimatorVariantFeature.class, new AnimatorVariantFeature()
        .withVariants(key, "default")
        .withDefault(key, "default"));
}
```

`NativeEntityType.withColorPalette()` registers texture variants:
```java
public NativeEntityType withColorPalette(EntityVariant variant) {
    TextureVariantFeature feature = new TextureVariantFeature();
    for (EntityTexture color : EntityTexture.VALUES) {
        if (color != EntityTexture.RANDOM) {
            String colorKey = color.Name(); // "white", "orange", etc.
            ResourceLocation path = /* texture path for this color */;
            feature.withVariant(key, colorKey);
            VariantRegistries.TEXTURES.register(
                new StandardTextureVariant(colorKey, colorKey, path.toString(), color.getId())
            );
        }
    }
    feature.withDefault(key, EntityTexture.WHITE.Name());
    withFeature(TextureVariantFeature.class, feature);
    return this;
}
```

The two features are independent — model state doesn't affect color, color doesn't affect model.

### Implementation Order

Task 0.1 (rebuild `InternalEntityType`) must happen **before** Task 1.1 (expand `InternalEntity`) because `InternalEntity` depends on `InternalEntityType` via the `nativeEntity` field.

**New Phase 0** in Sprint 08:
- Task 0.1: Rebuild HZLib `InternalEntityType` (3 story points)
- Task 0.2: Update lovelylib `NativeEntityType` to extend HZLib's version (2 story points)
- Task 0.3: Validate monsters_girls `NativeEntityType` still works (1 story point)

**Total**: 47 + 6 = **53 story points** (including the `InternalEntityType` work).

### Risk Assessment

**Lower risk than `InternalEntity`** because:
- No `EntityDataAccessor` fields — pure configuration data
- No NBT migration needed
- No synced data concerns
- The feature system is identical in both versions — just the resource map keys change

**Higher impact than expected** because:
- `NativeEntityType` is used by all three mod variants (Legacy, Tribute, Reboot)
- The `populateTextures()` → `configureVariants()` change touches the core of how robot types are defined
- The `withColorPalette()` migration (Task 4.1) depends on this being done first

### Recommendation

Add Phase 0 to Sprint 08 as the first phase. Update ADR_012 with the full `InternalEntityType` section. Update the story point total to 53.

The scope is large but it's the right atomic unit — doing `InternalEntityType` without `InternalEntity` leaves the codebase in a worse state (two type systems, one entity system). They must migrate together.

— Raul