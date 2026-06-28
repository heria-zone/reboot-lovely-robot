# Sprint Task: Condition Framework & Appearance Architecture — ADR 020 & ADR 021 Full Implementation

**Status**: ✅ COMPLETED  
**Started**: 2026-06-28  
**Completed**: 2026-06-28  
**Target Completion**: 2026-07-25  
**Priority**: High  
**Complexity**: High  
**Sprint Number**: 12

## Sprint Goal

Implement the base condition/context framework (ADR 020), the full `ConditionalAppearanceFeature`
replacing `BiomeAppearanceFeature`, the `AbstractVariantFeature<V>` shared base eliminating ~600
lines of duplication across the three independent-axis features, the `CompositeAppearanceFeature`
rename, and the formal two-lane appearance architecture with mutual-exclusion enforcement in
`NativeEntity.initializeRandomVariants` (ADR 021).

By the end of this sprint: every appearance selection path — spawn-time biome, interaction-time
dye, weighted random — flows through `ConditionalAppearanceFeature`. The combinators `and/or/negate`
and all nine shared factory methods exist exactly once. The Lane A/B distinction is explicit,
documented, and enforced at runtime.

## Strategic Context

**Source ADRs**:
- `docs/development/decisions/ADR_020_Conditional_Appearance_Feature.md`
- `docs/development/decisions/ADR_021_Composite_Appearance_Feature.md`

**Prerequisite**: Sprint 11 (Entity Data Pipeline — ADR 019) — `NativeEntity` fully pipeline-driven ✅  
**Prerequisite**: Sprint 09 Phase 1 (HZLib rename — `NativeEntity`/`NativeEntityFamily`) ✅  
**Affects**: HZLib Common, LovelyLib Common, Monsters & Girls Common  
**Build dependency**: Step 1 → Step 2 → Step 3 → Step 4 & Step 5 (parallel) → Step 6

---

## Objectives

### Step 1 — Base Condition/Context Framework (HZLib Common — new package)
> **Prerequisite for all subsequent steps. Nothing that uses conditions can compile without this.**

- [x] Create package `net.heriazone.hzlib.api.entity.conditions` in HZLib Common
- [x] Define `EntityCondition<C extends EntityContext>` — `@FunctionalInterface` with `test(C ctx)`, `default and()`, `default or()`, `default negate()`
- [x] Define `EntityContext` — abstract base class with `entity`, `level`, `pos` fields and `getEntity()`, `getLevel()`, `getPos()`, `getDimension()`, `getBiome()`, `getDayTime()`, `isServerSide()` accessors
- [x] Define `EntityConditions` — shared factory with `inBiome()`, `notInBiome()`, `inDimension()`, `isDaytime()`, `isNighttime()`, `inTimeRange()`, `isRaining()`, `isThundering()`, `chance()`, `always()`, `never()` — all generic over `C extends EntityContext`
- [x] Build — verify zero errors in HZLib Common before proceeding — **3 files, 0 diagnostics; Common + Fabric + Forge + NeoForge BUILD SUCCESSFUL**

### Step 2 — Migrate Existing Feature Condition Types (HZLib Common — existing files)
> **Depends on Step 1. Eliminates the first two copies of the duplicated pattern.**

- [x] Refactor `ExchangeCondition` — change `extends` to `EntityCondition<ExchangeContext>`, delete the `and/or/negate` combinator bodies (now inherited)
- [x] Refactor `ExchangeContext` — extend `EntityContext`, call `super(entity)` in constructor, delete the four shared accessors (`getDimension()`, `getBiome()`, `getDayTime()`, `isServerSide()`) now inherited from base; retain `getPlayer()` and `isOwnedByPlayer()`
- [x] Refactor `ExchangeConditions` — replace the nine shared method bodies with one-line delegation wrappers calling `EntityConditions.*`; retain and keep unchanged: `ownerOnly()`, `anyPlayer()`, `entityInState()`
- [x] Repeat the same three-file refactor for `EmanationCondition`, `EmanationContext`, `EmanationConditions`:
  - `EmanationCondition` — empty body extending `EntityCondition<EmanationContext>`
  - `EmanationContext` — extend `EntityContext`, covariant `getLevel()` override returning `ServerLevel` (legal Java), delete four shared accessors, retain static factories and domain fields
  - `EmanationConditions` — all conditions are domain-specific; no shared bodies to delegate; `ctx.self`/`ctx.level` field accesses in `EmanationEffects` and `EmanationRule` updated to `ctx.getEntity()`/`ctx.getLevel()` accessors
- [x] Build — verify zero errors across all HZLib modules — **6 files modified + 2 call-site fixes; Common + Fabric + Forge + NeoForge BUILD SUCCESSFUL**

### Step 3 — `ConditionalAppearanceFeature` + Appearance Types (HZLib Common — new classes)
> **Depends on Step 2.**

- [x] Define `AppearanceCondition` — `@FunctionalInterface` extending `EntityCondition<AppearanceContext>` (empty body — inherits all combinators)
- [x] Define `AppearanceContext` extending `EntityContext`:
  - Fields: `@Nullable MobSpawnType spawnReason`, `@Nullable Player player`, `ItemStack heldItem` (never null — `ItemStack.EMPTY` at spawn)
  - Static factory `forSpawn(LivingEntity entity, ServerLevelAccessor world, BlockPos pos, MobSpawnType reason)`
  - Static factory `forInteraction(LivingEntity entity, Level level, BlockPos pos, Player player, ItemStack held)`
  - Accessors: `getSpawnReason()`, `getPlayer()` (returns `Optional<Player>`), `getHeldItem()`, `isSpawn()`, `isInteraction()`
  - `getDimension/getBiome/getDayTime/isServerSide` — inherited from `EntityContext`
- [x] Define `AppearanceConditions`:
  - Nine delegation wrappers for shared factory methods (call `EntityConditions.*`)
  - Domain-specific: `heldItem(Item... items)`, `heldItemTag(TagKey<Item> tag)`, `onSpawn()`, `onInteraction()`, `spawnReason(MobSpawnType... reasons)`, `naturalSpawn()`
- [x] Define `WeightedAppearancePool`:
  - Static factory `of(String variantKey, float weight)`
  - `add(String variantKey, float weight)` — fluent
  - `pick()` — normalised weighted random, never null when pool is non-empty
- [x] Define `AppearanceRule` — immutable pair of `AppearanceCondition` + `WeightedAppearancePool`:
  - Static factory `of(AppearanceCondition, String variantKey)` — wraps in single-entry pool
  - Static factory `of(AppearanceCondition, WeightedAppearancePool)`
  - `evaluate(AppearanceContext ctx)` — returns `null` when condition fails
- [x] Define `ConditionalAppearanceFeature` with `resolve(AppearanceContext ctx)` iterating rules first-match, falling back to `defaultVariantKey`:
  - Inner `Builder` with `when(condition, variantKey)`, `when(condition, pool)`, `withDefault(variantKey)`, `build()`
  - Static `builder()` entry point
- [x] Delete `BiomeAppearanceFeature.java`
- [x] Update `NativeEntity.initializeSpawnVariants`:
  - Checks for `ConditionalAppearanceFeature`; if present, builds `AppearanceContext.forSpawn()`, calls `feature.resolve()`, applies texture variant; falls through to `initializeRandomVariants()` if result is null
  - Extracted `seedOverlaySlots()` helper — called from both the conditional and random paths
- [x] Add `tryConditionalAppearance(ItemStack heldItem, Player player)` helper to `NativeEntity` — builds `AppearanceContext.forInteraction()`, resolves key, applies texture variant, returns boolean
- [x] Update two javadoc comments that referenced `BiomeAppearanceFeature` (class-level javadoc in `NativeEntity`)
- [x] Build — verify zero errors across all HZLib modules — **9 files created/modified, 1 deleted; Common + Fabric + Forge + NeoForge BUILD SUCCESSFUL**

### Step 4 — `AbstractVariantFeature<V>` + Lane A Simplification (HZLib Common)
> **Depends on Step 3. Can run in parallel with Step 5.**

- [x] Create `AbstractVariantFeature<V extends IVariant>` in `net.heriazone.hzlib.api.entity.features.variants`:
  - Fields: `Map<String, Set<String>> entityVariants`, `Map<String, String> defaultVariants`, `ResourceMap<String, ResourceLocation> additionalResources`
  - Abstract hook: `protected abstract VariantRegistry<V> registry()`
  - Implement all `IVariantFeature<V>` methods once: `getAvailableVariants()`, `getDefaultVariant()`, `getRandomVariant()`, `hasVariant()`, `getVariantCount()`
  - Configuration methods: `withVariant`, `withVariants`, `withDefault` — all returning `AbstractVariantFeature<V>` for fluency
  - Protected `additionalResources()` accessor for legacy resource map
- [x] Refactor `TextureVariantFeature` to extend `AbstractVariantFeature<ITextureVariant>`:
  - Implement `registry()` returning `VariantRegistries.TEXTURES`
  - Implement `getVariantType()` returning `ITextureVariant.class`
  - Retain convenience aliases: `withTexture`, `getTexture`, `hasTexture`, `getRandomTexture`, `withBodyVariants`
  - Deleted ~200 lines of duplicated implementation
- [x] Refactor `ModelVariantFeature` to extend `AbstractVariantFeature<IModelVariant>`:
  - Implement `registry()` returning `VariantRegistries.MODELS`
  - Retain `withModel`, `getModel`, `hasModel`, `getRandomModel`, `withBodyVariants` aliases
- [x] Refactor `AnimatorVariantFeature` to extend `AbstractVariantFeature<IAnimatorVariant>`:
  - Implement `registry()` returning `VariantRegistries.ANIMATORS`
  - Retain `withAnimator`, `getAnimator`, `hasAnimator`, `getRandomAnimator`, `withBodyVariants` aliases
- [x] Build — verify zero errors — **4 files replaced + 1 new; Common + Fabric + Forge + NeoForge BUILD SUCCESSFUL**

### Step 5 — `CompositeAppearanceFeature` Rename + Lane B Formalisation (HZLib Common)
> **Depends on Step 3. Can run in parallel with Step 4.**

- [x] Add `getSizeConfig()` default method to `IAppearanceVariant` → created as `ICompositeAppearance` (new file; `IAppearanceVariant` kept as legacy if needed by other modules)
- [x] Rename `IAppearanceVariant` → `ICompositeAppearance` — new interface created; `IAppearanceVariant` superseded
- [x] Rename `AppearanceVariantFeature` → `CompositeAppearanceFeature` — new class created; deprecated `AppearanceVariantFeature` alias retained as bridge
- [x] Add optional `@Deprecated` type alias `AppearanceVariantFeature extends CompositeAppearanceFeature`
- [x] Update `VariantRegistries.APPEARANCES` — type token updated to `ICompositeAppearance`
- [x] Update `NativeEntity.initializeRandomVariants` — lane-check logic added: Lane B (`CompositeAppearanceFeature`) checked first with misconfiguration warning; Lane A fallback preserved; `hasLaneAFeatures()` private helper added
- [x] Build — verify zero errors — **5 new files + 3 modified; Common + Fabric + Forge + NeoForge BUILD SUCCESSFUL**

### Step 6 — Lane Resolution in `NativeEntity.initializeRandomVariants` (HZLib Common)
> **Depends on Step 4 and Step 5 both complete.**

- [x] Extract `seedOverlaySlots()` helper from current `initializeRandomVariants` — called from both lanes
- [x] Implement lane-check logic:
  - Check `CompositeAppearanceFeature` first (Lane B)
  - If present and Lane A features also detected: log `WARN "[HZLib] Family '{}' declares both CompositeAppearanceFeature and independent axis features. CompositeAppearanceFeature takes precedence."`
  - Lane B path: call `composite.getRandomVariant()`, apply `textureKey`, `modelKey`, `animatorKey`, call `sizeConfig.applyTo(this)` if present, call `seedOverlaySlots()`, return
  - Lane A path (fallback): query `TextureVariantFeature`, `ModelVariantFeature`, `AnimatorVariantFeature` independently, call `seedOverlaySlots()`
- [x] Implement private helper `hasLaneAFeatures()` — checks presence of any of the three independent features
- [x] Build — verify zero errors across all HZLib modules — **NativeEntity.initializeRandomVariants fully wired; Common + Fabric + Forge + NeoForge BUILD SUCCESSFUL**

### Step 7 — LovelyLib Dye Logic Migration (LovelyLib Common)
> **Depends on Step 3 and Step 6.**

- [x] Add `ConditionalAppearanceFeature` registration to each `RobotFamily` subclass with 16 dye-to-variant-key mappings using `AppearanceConditions.heldItem()` — wired directly in `RobotFamily.withColorPalette()`, applied to all robot types via `RobotFamilyRegistry.create()`
- [x] Replace hardcoded dye item-check chain in `RobotEntity.handleTexture` with single call to `tryConditionalAppearance(heldItem, player)`
- [x] Deleted the old 16-branch `if (stack.is(Items.WHITE_DYE)) { setTextureVariant(...); }` chain — all 16 dye items covered by `ConditionalAppearanceFeature` rules on the family
- [x] Build LovelyLib — verify zero errors — **LovelyLib Common + loaders BUILD SUCCESSFUL, dye interaction confirmed**

### Step 8 — Monsters & Girls Biome Family Migration (Monsters & Girls Common)
> **Depends on Step 3 and Step 6. Can run in parallel with Step 7.**

- [x] Identified all families with biome-selection logic — `MushroomFamily` (Amanita, Brown) used `BiomeAppearanceFeature`; `WildTamableEntity` had a hand-rolled `initializeBiomesVariants` override using the deleted class
- [x] Migrated `MushroomFamily.AMANITA` and `MushroomFamily.BROWN` to `ConditionalAppearanceFeature` with `AppearanceConditions.inBiome()` rules — most-specific rules ordered first to avoid broad-set shadowing
- [x] Replaced `WildTamableEntity.initializeSpawnVariants` override — removed `BiomeAppearanceFeature` path, now delegates to base class `ConditionalAppearanceFeature` check via `AppearanceContext.forSpawn()` with authoritative `ServerLevelAccessor`
- [x] Updated `AppearanceContext.forSpawn()` — stores the `ServerLevelAccessor` and `BlockPos` and overrides `getBiome()` to query the authoritative world rather than `entity.level()` (which is stale during `finalizeSpawn`)
- [x] Updated `GourdragoraFamily` — `AppearanceVariantFeature` → `CompositeAppearanceFeature`; `registerVariants()` now uses `GourdragoraAppearance` entries carrying direct `SizeConfig` references
- [x] Created `GourdragoraAppearance` — size-aware `ICompositeAppearance` impl with `getSizeConfig()` returning the matching `SizeConfig`
- [x] Updated `GourdragoraEntity` (Fabric) — `AppearanceVariantFeature` → `CompositeAppearanceFeature`, `IAppearanceVariant` → `ICompositeAppearance`; `getSizeConfig()` used directly instead of secondary feature lookup
- [x] Fixed `IAppearanceVariant` — demoted to deprecated alias extending `ICompositeAppearance`
- [x] Fixed `StandardAppearanceVariant` — updated to implement `ICompositeAppearance`
- [x] Fixed `ExchangeFeature` owner-only injection — replaced unsafe `(ExchangeCondition)` cast on `and()` result with explicit lambda wrapper (runtime `ClassCastException` fix)
- [x] Fixed Amanita rule ordering — jungle/lush/mangrove `pharia` rule moved before broad `ForestBiomesNoConiferous` `red` rule; confirmed spawn behaviour correct in-game
- [x] Build Monsters & Girls — verify zero errors — **Common + Fabric + Forge + NeoForge BUILD SUCCESSFUL, biome-driven spawn confirmed in-game**

### Step 9 — Validation
> **Depends on Step 7 and Step 8 both complete.**

- [x] **Dye interaction**: robot texture variant updates correctly on dye right-click; non-dye items unaffected
- [x] **Spawn-time biome selection**: Mushroom Brown and Amanita spawn with correct variant per biome — including Amanita pharia in jungle after rule-order fix
- [x] **Gourdragora lane B**: all three sizes (mini/default/big) spawn with correct composite appearance (texture, model, scale) via `GourdragoraAppearance.getSizeConfig()`
- [x] **`BiomeAppearanceFeature` absence**: file deleted, no compile errors reference it; `WildTamableEntity` fully migrated
- [x] **Call site stability**: `ExchangeConditions.*` and `EmanationConditions.*` call sites compile and function correctly; runtime `ClassCastException` on `ownerOnly().and()` fixed
- [x] **Rule ordering**: confirmed most-specific-first principle enforced — Amanita pharia/yellow/red ordering verified in-game
- [x] Full build passes — zero errors across all modules (HZLib + LovelyLib + Monsters & Girls)

---

## Implementation Notes

### Build Order Is Strict for Steps 1–3
Step 1 (base framework) must fully compile before Step 2 (migrate Exchange/Emanation) begins.
Step 2 must pass before Step 3 (appearance types) begins — `AppearanceCondition` depends on
`EntityCondition<C>` which in turn requires `EntityContext` to be stable. Steps 4 and 5 can
proceed in parallel once Step 3 is complete.

### `and/or/negate` Return Type Trade-off
The combinators on `EntityCondition<C>` return `EntityCondition<C>`, not the subtype (e.g. not
`ExchangeCondition`). This is the intended trade-off. In practice conditions are terminal
expressions — they are passed to rule builders immediately after construction, never chained
further. If a domain-specific method ever needs to be callable after composition, promote it to
`EntityConditions`. Do not add covariant overrides of the combinators to subinterfaces.

### `EmanationContext.getLevel()` Covariant Override
`EmanationContext` currently uses `ServerLevel` internally. `EntityContext` declares
`getLevel() : Level`. `EmanationContext` must override `getLevel()` with a covariant return of
`ServerLevel` — legal Java, preserves the existing API. Do not change `EntityContext` to use
`ServerLevel` directly: it would break any context that operates on a client-side `Level`.

### `AppearanceContext` Nullable Fields
`player` is null at spawn; `spawnReason` is null at interaction-time. Both are guarded: `getPlayer()`
returns `Optional<Player>`, `isSpawn()` and `isInteraction()` are the preferred guards.
`AppearanceConditions.onSpawn()` and `AppearanceConditions.onInteraction()` use these guards
internally — condition authors never touch the nullable fields directly.

### Condition Evaluation Order
`ConditionalAppearanceFeature` evaluates rules in declaration order, returns on the first match.
Document this clearly on the `Builder` class and in its javadoc: most-specific rules must be
declared first, `withDefault` (if any) is always last. Failure to order rules correctly is a
user error, not a framework error — do not attempt to reorder rules automatically.

### `BiomeAppearanceFeature` — Zero Live Call Sites
Per ADR 020, `BiomeAppearanceFeature` was declared but never registered on any family. The
deletion in Step 3 is a clean removal — no family migration is required for this specific class.
However, check for any hand-rolled biome-selection logic in `initializeSpawnVariants` overrides
across Monsters & Girls families before marking Step 8 complete.

### `AbstractVariantFeature` Fluent Return Type
The configuration methods (`withVariant`, `withVariants`, `withDefault`) return
`AbstractVariantFeature<V>`, not the concrete subtype. For most call sites this is fine — configuration
is done at family declaration time in a single chain that ends before the variable is used.
If any call site requires the concrete type post-chain (e.g. `TextureVariantFeature`-specific method
after `withVariant`), override the method in the subclass with a covariant return.

### Gourdragora `SizeConfig` Wiring (Step 8)
The existing Gourdragora implementation constructs an appearance key by concatenating a size string
with a color string. After Step 8, each `ICompositeAppearance` entry on `GourdragoraFamily` holds
a direct `SizeVariantFeature.SizeConfig` reference. `getSizeConfig()` returns `Optional.of(config)`.
The entity no longer needs to look up the size config separately — the composite entry carries it.
This is a simplification of the existing override, not a behaviour change.

### `@Deprecated` Alias for `AppearanceVariantFeature`
The alias in Step 5 is optional — it exists only if a gradual rollout is needed. If no external
code depends on `AppearanceVariantFeature`, omit the alias and do a direct rename. At the time
of this sprint all consumers are internal to the ecosystem.

---

## Story Points

| Step | Description | Points |
|------|-------------|--------|
| Step 1 — Base framework (HZLib) | 3 new files | 3 |
| Step 2 — Migrate Exchange + Emanation (HZLib) | 6 files refactored | 5 |
| Step 3 — Appearance types + `ConditionalAppearanceFeature` (HZLib) | 7 new files, 2 modified, 1 deleted | 13 |
| Step 4 — `AbstractVariantFeature<V>` + Lane A simplification (HZLib) | 1 new file, 3 refactored | 8 |
| Step 5 — `CompositeAppearanceFeature` rename + Lane B (HZLib) | 3 files modified/renamed | 5 |
| Step 6 — Lane resolution in `initializeRandomVariants` (HZLib) | 1 file modified | 5 |
| Step 7 — LovelyLib dye migration | 7 families + 1 entity class | 8 |
| Step 8 — Monsters & Girls biome migration | N families + Gourdragora | 8 |
| Step 9 — Validation | 10 validation scenarios | 5 |
| **Total** | | **60** |

> Step 8 scope depends on how many M&G families have biome-selection overrides. If the count is
> zero (all were using `BiomeAppearanceFeature` which had no call sites), the Gourdragora work
> alone carries the step and points may be revised down to 5. Assess during Step 8.

---

## Files Created / Modified

### HZLib Common — new
- `sources/common/hzlib-1.21.1/Common/.../conditions/EntityCondition.java`
- `sources/common/hzlib-1.21.1/Common/.../conditions/EntityContext.java`
- `sources/common/hzlib-1.21.1/Common/.../conditions/EntityConditions.java`
- `sources/common/hzlib-1.21.1/Common/.../features/AppearanceCondition.java`
- `sources/common/hzlib-1.21.1/Common/.../features/AppearanceContext.java`
- `sources/common/hzlib-1.21.1/Common/.../features/AppearanceConditions.java`
- `sources/common/hzlib-1.21.1/Common/.../features/WeightedAppearancePool.java`
- `sources/common/hzlib-1.21.1/Common/.../features/AppearanceRule.java`
- `sources/common/hzlib-1.21.1/Common/.../features/ConditionalAppearanceFeature.java`
- `sources/common/hzlib-1.21.1/Common/.../features/variants/AbstractVariantFeature.java`

### HZLib Common — modified
- `sources/common/hzlib-1.21.1/Common/.../features/exchange/ExchangeCondition.java`
- `sources/common/hzlib-1.21.1/Common/.../features/exchange/ExchangeContext.java`
- `sources/common/hzlib-1.21.1/Common/.../features/exchange/ExchangeConditions.java`
- `sources/common/hzlib-1.21.1/Common/.../features/emanation/EmanationCondition.java`
- `sources/common/hzlib-1.21.1/Common/.../features/emanation/EmanationContext.java`
- `sources/common/hzlib-1.21.1/Common/.../features/emanation/EmanationConditions.java`
- `sources/common/hzlib-1.21.1/Common/.../features/variants/IAppearanceVariant.java` — renamed to `ICompositeAppearance.java` + `getSizeConfig()` default method
- `sources/common/hzlib-1.21.1/Common/.../features/variants/AppearanceVariantFeature.java` — renamed to `CompositeAppearanceFeature.java` (+ optional deprecated alias)
- `sources/common/hzlib-1.21.1/Common/.../features/variants/TextureVariantFeature.java` — extends `AbstractVariantFeature`
- `sources/common/hzlib-1.21.1/Common/.../features/variants/ModelVariantFeature.java` — extends `AbstractVariantFeature`
- `sources/common/hzlib-1.21.1/Common/.../features/variants/AnimatorVariantFeature.java` — extends `AbstractVariantFeature`
- `sources/common/hzlib-1.21.1/Common/.../NativeEntity.java` — `initializeSpawnVariants`, `initializeRandomVariants`, `tryConditionalAppearance`, `seedOverlaySlots`, `hasLaneAFeatures`
- `sources/common/hzlib-1.21.1/Common/.../AnimationStateManager.java` — class reference + javadoc update

### HZLib Common — deleted
- `sources/common/hzlib-1.21.1/Common/.../features/BiomeAppearanceFeature.java`

### LovelyLib Common — modified
- Each `*Family.java` (7 robot families) — add `ConditionalAppearanceFeature` with 16 dye rules
- `sources/common/lovelylib-1.21.1/Common/.../RobotEntity.java` — replace dye chain with `tryConditionalAppearance()`

### Monsters & Girls Common — modified
- `GourdragoraFamily.java` — wire `SizeConfig` references into `ICompositeAppearance` entries
- `GourdragoraEntity.java` (or wherever `initializeSpawnVariants` override lives) — replace string-concatenation key construction
- Any family with biome-selection overrides — add `ConditionalAppearanceFeature`, remove override
