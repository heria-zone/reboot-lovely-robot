# ADR 011: Variant and Spawn System Refactoring

**Status**: Accepted  
**Date**: 2026-04-27  
**Decision Makers**: Mike, Raul, Serge  
**Consulted**: Architecture Review (27-04-2026 Entity Architecture & Variant System Discussion)

## Context

The current variant and spawn systems have several structural problems identified during the entity architecture review:

**Problem 1 — Gourdragora over-registration**: The current `GourdragoraType` has 9 static instances (GOLDEN_MINI, GOLDEN_DEFAULT, GOLDEN_BIG, LUMINA_MINI, etc.), treating size as a type. This is wrong — size is an entity appearance, not an entity variant. The 9 instances should collapse to 3 entity variant configurations (Golden, Lumina, Jack'O), each with 3 entity appearances (Mini, Default, Big).

**Problem 2 — Size inference via string-sniffing**: `GourdragoraType` infers size from `key.endsWith("_mini")` / `key.endsWith("_big")`. This is fragile — the size information is implicit in the key name rather than explicit in the type.

**Problem 3 — Biome-aware spawn selection not implemented**: `MushroomType.BROWN` has 4 texture variants (Boletus, Ruby, Scarlatina, Orange) registered, but `initializeRandomVariants()` in `InternalEntity` picks randomly. There is no biome-aware selection. The Mushroom Brown should spawn with a texture determined by the biome it spawns in.

**Problem 4 — Entity type definitions in Fabric module**: `MushroomType`, `GourdragoraType`, and entity type definitions live in the Fabric module. They belong in Common, with loader-specific wiring only for registration and rendering.

**Problem 5 — `supportsTummyTexture()` contradiction**: `MushroomType.supportsTummyTexture()` returns `false` with a comment saying "all mushroom variants support belly progression." The method name and return value contradict the comment.

**Problem 6 — Jack'O behavioral difference unmodeled**: Jack'O Gourdragora uses different taming items (`CAKE` + `CANDIES`) than Golden/Lumina (`CAKE` + `BONE_MEAL`). By the project's own terminology, this makes Jack'O an Entity Variant (behavioral difference), not merely an Entity Appearance with a spawn restriction. The current flat structure does not express this.

## Terminology (Locked)

These terms are used consistently throughout this ADR and all related documentation:

- **Entity Family** — The conceptual creature. One family = one registered `EntityType<>`. Example: Gourdragora, Mushroom Brown, Bunny Robot.
- **Entity Variant** — A meaningful configuration within a family that differs in stats, behaviors, or spawn conditions. Example: Gourdragora Golden vs. Jack'O (different taming items). Robot Bunny vs. Dragon (different stats and abilities).
- **Entity Appearance** — A visual change within the same entity variant. Changes texture, model, hitbox, or scale — but not fundamental identity, stats, or behavior class. Example: Gourdragora Mini/Default/Big (different size). Mushroom Brown Boletus/Ruby/Scarlatina/Orange (different texture by biome). Robot 16 dye colors.

## Decision

### Decision 1: `SizeVariantFeature` for Dynamic Hitbox

Entities with size-based entity appearances (Gourdragora) will use a `SizeVariantFeature` that carries per-size configuration. The entity reads its current `MODEL_VARIANT` key and applies the corresponding dimensions dynamically.

**Why Option B (dynamic via `getDimensions(Pose)`) over Option A (baked at spawn)**:
- Sitting/moving/special-state hitbox requirements make dynamic reading mandatory regardless of whether size ever changes post-spawn
- A sitting Gourdragora Big should not have a standing hitbox
- Serge confirmed Option B

```java
public class SizeVariantFeature extends EntityFeature {
    // key → SizeConfig
    private final Map<String, SizeConfig> sizeConfigs;

    public static class SizeConfig {
        private final String modelKey;          // which model to use
        private final float scale;              // renderer scale factor (1.0 = no scale)
        private final Map<Pose, EntityDimensions> dimensions; // per-pose hitboxes
        private final float healthMultiplier;   // stat scaling
        private final float attackMultiplier;
        private final float speedMultiplier;
        private final float armorMultiplier;
        private final float knockbackResistance;
    }
}
```

**Hitbox per pose** — the `SizeConfig` carries a `Map<Pose, EntityDimensions>` so that standing, sitting, and riding all have correct hitboxes for each size.

**Scale factor** — Gourdragora Big uses the same model as Default (`gourdragora_girl_default.geo.json`) but rendered at a larger scale. The `SizeConfig` carries a `scale` float that the renderer applies. This matches how vanilla Slime handles size — same model, different scale.

**Stat application** — applied once at spawn via `finalizeSpawn()`. Size is immutable after spawn (Serge confirmed: the player tames whatever size they find). Stats are baked into entity attributes at spawn time. If a future mechanic allows size progression (mini → default → big), this decision is revisited.

### Decision 2: `GourdragoraType` Collapse

Collapse from 9 static instances to 3 Entity Variant instances (Golden, Lumina, Jack'O). Each carries:
- Its own `FoodFeature` (Jack'O uses different taming items)
- A shared `SizeVariantFeature` pool (mini/default/big)
- Its own `TextureVariantFeature` (color textures per size)

```java
// Before: 9 instances
GourdragoraType.GOLDEN_MINI, GOLDEN_DEFAULT, GOLDEN_BIG,
GourdragoraType.LUMINA_MINI, LUMINA_DEFAULT, LUMINA_BIG,
GourdragoraType.JACKO_MINI, JACKO_DEFAULT, JACKO_BIG

// After: 3 instances
GourdragoraType.GOLDEN  // FoodFeature: CAKE + BONE_MEAL
GourdragoraType.LUMINA  // FoodFeature: CAKE + BONE_MEAL
GourdragoraType.JACKO   // FoodFeature: CAKE + CANDIES, October-only spawn
```

Each instance carries a `SizeVariantFeature` with mini/default/big configurations. The entity's `MODEL_VARIANT` key holds the current size (`"mini"`, `"default"`, `"big"`), set at spawn and immutable.

**Jack'O as Entity Variant**: Jack'O has different taming items than Golden/Lumina. By the project's terminology, this is a behavioral difference that makes Jack'O an Entity Variant. The October-only spawn restriction is a spawn condition on the entity type, not a property of the entity itself — handled in spawn registration, not in the type definition.

### Decision 3: `initializeSpawnVariants()` Hook

Add a protected hook to `InternalEntity.finalizeSpawn()`:

```java
// In InternalEntity
@Override
public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty,
                                    MobSpawnType reason, SpawnGroupData entityData) {
    initializeSpawnVariants(world, reason);
    return super.finalizeSpawn(world, difficulty, reason, entityData);
}

/**
 * Override to implement context-aware variant selection at spawn time.
 * Default implementation calls initializeRandomVariants() for random selection.
 * Override for biome-aware, size-coordinated, or other context-driven selection.
 */
protected void initializeSpawnVariants(ServerLevelAccessor world, MobSpawnType reason) {
    initializeRandomVariants(); // existing behavior preserved
}
```

**Why a hook over a generic `IVariantSelector` interface**: We have two concrete use cases (Gourdragora size coordination, Mushroom Brown biome selection). A hook is the minimal correct solution. The `IVariantSelector` abstraction is added when a third use case emerges.

### Decision 4: Gourdragora Spawn Initialization

`GourdragoraEntity` overrides `initializeSpawnVariants()` to coordinate size and animator selection:

```java
@Override
protected void initializeSpawnVariants(ServerLevelAccessor world, MobSpawnType reason) {
    // 1. Pick size randomly (or based on spawn conditions)
    String size = pickRandomSize(); // "mini", "default", or "big"
    setModelVariant(size);

    // 2. Derive animator from size (mini uses different animator)
    String animatorKey = nativeEntity.getKey() + "_" + size + "_default";
    setAnimatorVariant(animatorKey);

    // 3. Pick color independently (texture variant)
    initializeRandomTextureVariant();

    // 4. Apply size stats from SizeVariantFeature
    nativeEntity.getFeature(SizeVariantFeature.class).ifPresent(feature -> {
        SizeVariantFeature.SizeConfig config = feature.getConfig(size);
        if (config != null) applyStatMultipliers(config);
    });
}
```

The three variant dimensions (color, size, animator) are not independent — animator must match size. The hook ensures they are set in the correct coordinated order.

### Decision 5: Mushroom Brown Biome-Aware Spawn

`MushroomBrownEntity` (or the equivalent in the 1.21.1 system) overrides `initializeSpawnVariants()` to select texture based on spawn biome:

```java
@Override
protected void initializeSpawnVariants(ServerLevelAccessor world, MobSpawnType reason) {
    // Biome-to-texture mapping for Mushroom Brown
    ResourceKey<Biome> biome = world.getBiome(blockPosition()).unwrapKey().orElse(null);

    String textureKey;
    if (biome != null) {
        if (isConiferousBiome(biome))    textureKey = "mushroom_brown_ruby";
        else if (isDarkForest(biome))    textureKey = "mushroom_brown_scarlatina";
        else if (isBirchForest(biome))   textureKey = "mushroom_brown_orange";
        else                             textureKey = "mushroom_brown_boletus"; // anywhere else
    } else {
        textureKey = "mushroom_brown_boletus"; // fallback
    }

    setTextureVariant(textureKey);
}
```

**Spawn-time selection, not dynamic**: Texture is locked to the spawn biome. This is consistent with how Minecraft handles biome-specific variants (axolotl colors, etc.) and avoids the complexity of tracking biome changes post-spawn.

### Decision 6: Entity Type Definitions Moved to Common

`MushroomType`, `GourdragoraType`, and all entity type definitions move from the Fabric module to Common. Loader-specific modules retain only registration wiring (entity type registration, attribute registration, renderer registration).

### Decision 7: Fix `supportsTummyTexture()`

`MushroomType.supportsTummyTexture()` currently returns `false` with a comment saying "all mushroom variants support belly progression." The return value is wrong. Fix: return `true` for variants that support belly progression, `false` for those that don't. The method name and return value must agree.

## Consequences

### Positive

- **Registry clarity** — 3 Gourdragora entity type registrations instead of 9 (or 1 with compound variants)
- **Explicit size** — `SizeVariantFeature` makes size configuration explicit, no string-sniffing
- **Correct hitboxes** — dynamic `getDimensions(Pose)` gives correct hitbox for every state
- **Biome-aware spawning** — Mushroom Brown spawns with the correct regional texture
- **Jack'O correctly modeled** — behavioral difference (taming items) expressed as Entity Variant
- **Common module ownership** — entity type definitions in the right place
- **Coordinated variant selection** — Gourdragora size/animator/color set in correct order

### Negative

- **Migration cost** — existing Gourdragora entity classes and registrations must be refactored
- **`SizeVariantFeature` is Gourdragora-specific for now** — extracted to a general HZLib feature only when a second entity needs it
- **Biome lookup at spawn** — `world.getBiome(blockPosition())` is a registry lookup; acceptable at spawn time, not acceptable every tick

### Risks

- **`getDimensions(Pose)` called frequently** — Minecraft calls this on every pose change and dimension refresh. The `SizeVariantFeature` lookup must be O(1) (HashMap by key). No complex computation in this path.
- **Stat application at spawn** — if stats are baked at spawn and the entity is later loaded from NBT, the stats must be re-applied from the saved `MODEL_VARIANT` key. The `readAdditionalSaveData()` override must call `applyStatMultipliers()` after loading the model variant.

## Migration Plan

1. Move `MushroomType`, `GourdragoraType` from Fabric module to Common
2. Implement `SizeVariantFeature` in HZLib Common (Gourdragora-specific for now)
3. Add `initializeSpawnVariants()` hook to `InternalEntity.finalizeSpawn()`
4. Refactor `GourdragoraType` — collapse 9 instances to 3, add `SizeVariantFeature`
5. Implement `GourdragoraEntity.getDimensions(Pose)` override — reads `MODEL_VARIANT`, looks up `SizeVariantFeature`, returns correct `EntityDimensions` for the current `Pose`
6. Implement `GourdragoraEntity.initializeSpawnVariants()` override — coordinates size, animator, and color selection in the correct order, then calls `applyStatMultipliers()` from `SizeVariantFeature`
   - **⚠ NBT load requirement**: `readAdditionalSaveData()` must also call `applyStatMultipliers()` after loading `MODEL_VARIANT` from NBT. Stats are baked at spawn but must be re-applied on world reload from the saved `MODEL_VARIANT` key. Missing this step causes stats to reset to defaults on every world load.
7. Implement `MushroomBrownEntity.initializeSpawnVariants()` with biome lookup
8. Fix `supportsTummyTexture()` return value
9. Update entity type registrations to reflect new structure
10. Validate: spawn Gourdragora of each size, confirm hitbox and stats correct for standing, sitting, and riding poses
11. Validate: reload world with Gourdragora entities present, confirm stats persist correctly (catches the NBT reapplication requirement)
12. Validate: spawn Mushroom Brown in each biome type, confirm correct texture

## Reference Implementations

**Gourdragora** — reference implementation for compound variants + size + coordinated spawn selection.

**Mushroom Brown** — reference implementation for biome-aware spawn variant selection.

Both are implemented before any other entity uses these patterns.

## Related Decisions

- ADR_009: Entity Hierarchy Refactoring (three-tier hierarchy)
- ADR_010: Animation Profile System (companion ADR — `SizeVariantFeature` drives model selection that `AnimationProfile` uses for animator selection)
