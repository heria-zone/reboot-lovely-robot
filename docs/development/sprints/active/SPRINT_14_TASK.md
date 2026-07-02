# Sprint Task: Entity Registration Consolidation — ADR-023 Full Implementation

**Status**: 🔄 IN PROGRESS
**Started**: 2026-07-02
**Target Completion**: 2026-08-22
**Priority**: High
**Complexity**: High
**Sprint Number**: 14

## Sprint Goal

Implement the full `RobotDefinitionRegistry` architecture defined in ADR-023. The result is a
codebase where adding a new robot entity requires exactly two Java changes: one static constant
in `RobotVariant` and one builder call in `LegacyRobotDefinitions` or `RebootRobotDefinitions`.
All per-entity switch statements, static field groups, array entries, and per-loader field
declarations are eliminated permanently.

By the end of this sprint: `RobotVariant` is a final class (not an enum) with runtime-extensible
registration; `RobotDefinitionRegistry` is the single source of truth for all variant metadata;
`ConfigAccessLayer`, `LovelyIdentifier`, `SharedConfigs`, and both `*Configs` lovelylib-side classes
are permanently stable and require zero edits for any future entity; all four Bunny3 silent bug
classes are structurally eliminated; and the full 30-edit scatter is reduced to 2.

## Strategic Context

**Source ADR**: `docs/development/decisions/ADR-023_Entity-Registration-Consolidation.md`
**Analysis Notes**: `docs/development/notes/ADR-023_Critical_Analysis_Notes.md`
**Prerequisite**: Sprint 11 (Entity Data Pipeline — ADR-019) ✅
**Prerequisite**: Sprint 12 (Condition Framework — ADR-020 & ADR-021) ✅
**Prerequisite**: ADR-019 Phase 0 complete before Phase 3 of this sprint (loader-side) ✅
**Affects**: `lovelylib-1.21.1`, `llovelyr-1.21.1`, `rlovelyr-1.21.1`
**Not for**: `tlovelyr-1.21.1` (Tribute — fixed roster, structurally excluded)
**Build dependency**: Phase 0 → Phase 1 → Phase 2 → Phase 3 → Phase 4

---

## Objectives

### Phase 0 — Pre-migration bug fix (lovelylib only, 1 line)
> **Fix the active Forge/NeoForge stat bug before any other work. Standalone commit.**

- [x] Fix `lovelylib.source.legacy.LegacyConfigs.getEntityConfig()` — change fallback from `EntityConfigData.getDefault()` to `getDefaultConfig(variant)` so Forge/NeoForge reads variant-specific defaults from the `Default` map instead of the generic baseline
- [x] Apply identical fix to `lovelylib.source.reboot.RebootConfigs.getEntityConfig()`
- [ ] Verify on Forge: all 8 robot variants load with correct per-variant stats (not generic defaults) — spot-check Bunny (baseHp=26, attackSpeed=1.7F) vs generic (baseHp=20, attackSpeed=1.5F)
- [ ] Commit with `FIX:` prefix — no other files change in this commit

### Phase 1 — New infrastructure (lovelylib only, no behavior change)
> **All new classes. Registry is populated and sealed but nothing reads from it yet. Existing behavior unchanged.**

- [x] Migrate `RobotVariant` from enum (`common.entity.enums.RobotVariant`) to final class (`common.entity.definition.RobotVariant`):
  - Static constants `Bunny(0)` through `Bunny3(7)` registered via `register(int, String)` — IDs preserved from enum for NBT compatibility
  - Internal `BY_ID` (`LinkedHashMap<Integer, RobotVariant>`) and `BY_NAME` (`LinkedHashMap<String, RobotVariant>`) maps
  - `register(int id, String name)` — synchronized, throws `IllegalArgumentException` on duplicate ID or name; IDs 0–99 reserved for built-ins, addon mods use ≥ 100
  - `getId()`, `getName()` accessors (replace enum `ordinal()` and `name()`)
  - `byId(int)`, `byName(String)` lookup methods — throw `NoSuchElementException` on miss
  - `values()` returning unmodifiable collection in registration order
  - `isKnown(int)`, `isKnown(String)` convenience guards
  - `equals()` using reference equality (singletons), `hashCode()` returning `m_id`, `toString()` returning `"name(id)"`
  - Deleted old `common.entity.enums.RobotVariant` enum file ✅
  - Updated all import sites across lovelylib, llovelyr, rlovelyr, tlovelyr — 9 files migrated from `common.entity.enums.RobotVariant` → `common.entity.definition.RobotVariant` ✅
  - Verified zero enum `switch` statements on `RobotVariant` remain ✅
- [x] Create `net.heriazone.lovelylib.common.entity.definition.ModTarget` enum — `LEGACY`, `REBOOT`, `TRIBUTE`
- [x] Create `net.heriazone.lovelylib.common.entity.definition.EntityStats` — immutable value object, 7 fields (maxLevel, baseHp, baseAttack, attackSpeed, baseDefense, baseToughness, movementSpeed); `toEntityConfigData()` produces 7-parameter `EntityConfigData` (no knockbackResistance)
- [x] Create `net.heriazone.lovelylib.common.entity.definition.RobotEntityDefinition` — fluent builder, immutable after construction:
  - `RendererFactory` inner `@FunctionalInterface` — `Object create(Object context)`
  - `featureConfigurator` field — `BiConsumer<RobotFamily, SharedConfigs.EntityConfigData>`, optional
  - `getVariantKey()` delegates to `variant.getName()` — never uses `name().toLowerCase()`
  - `applyFeatureConfigurator(RobotFamily, EntityConfigData)` — no-op when null
  - Builder validates: `displayName` not null, `mods` not empty, `stats` not null
- [x] Create `net.heriazone.lovelylib.common.entity.definition.RobotDefinitionRegistry` — `LinkedHashMap<RobotVariant, RobotEntityDefinition>`, `seal()` / `register()` / `get()` / `getForMod()` / `getAll()` / `getVariantKeysForMod()` / `getDefaultConfig()` / `getVariantKey()` / `getDisplayTranslationKey()` / `isRegistered()`
- [x] Create `net.heriazone.lovelylib.source.legacy.LegacyRobotDefinitions` — all 8 existing Legacy entities declared with stat values matching current `LegacyConfigs.Default`; Kitsune entry includes `featureConfigurator` for `BoneVisibilityFeature`; Bunny3 uses `.forMods(LEGACY, REBOOT)`
- [x] Create `net.heriazone.lovelylib.source.reboot.RebootRobotDefinitions` — declares only Reboot-exclusive entities (empty for now — Bunny3 via `.forMods(LEGACY, REBOOT)` is the only shared entity)
- [x] Create `Lovely.onInitialize()` in `Lovely.java` — calls `LegacyRobotDefinitions.register()`, `RebootRobotDefinitions.register()`, then `RobotDefinitionRegistry.seal()`; old `initialize()` stub preserved as `@Deprecated` delegate
- [x] Wire `Lovely.onInitialize()` as the very first call in all 6 loader entry points before any registration or event bus wiring:
  - `llovelyr-1.21.1/Forge/LovelyLegacy.java` (constructor)
  - `llovelyr-1.21.1/NeoForge/LovelyLegacy.java` (constructor) ✅
  - `llovelyr-1.21.1/Fabric/LovelyLegacy.java` (`onInitialize()`) ✅
  - `rlovelyr-1.21.1/Forge/LovelyReboot.java` (constructor) ✅
  - `rlovelyr-1.21.1/NeoForge/LovelyReboot.java` (constructor) ✅
  - `rlovelyr-1.21.1/Fabric/LovelyReboot.java` (`onInitialize()`) ✅
- [ ] Full build passes — zero errors across all modules; verify registry seals without `IllegalStateException` on startup

### Phase 2 — Simplify lovelylib consumers (lovelylib only)
> **Depends on Phase 1. Switch statements and static fields removed; config loops go data-driven.**

- [x] Refactor `LovelyIdentifier.getTranslation(RobotVariant)` — replace switch with `RobotDefinitionRegistry.isRegistered()` + `getVariantKey()` lookup; fallback returns `RobotVariant.Vanilla.getName()`
- [x] Refactor `ConfigAccessLayer.getDefaultEntityConfig(String variantKey)` — replace switch with loop over `RobotDefinitionRegistry.getAll()` matching `getVariantKey().equals(variantKey)`
- [x] Refactor `lovelylib.source.legacy.LegacyConfigs` static `{}` block — replace all `Default.put()` entries with a single loop over `RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)`
- [x] Refactor `lovelylib.source.reboot.RebootConfigs` static `{}` block — same pattern with `ModTarget.REBOOT`
- [x] Refactor `LegacyRobotFamilies`:
  - Replace 8 named `public static final RobotFamily` fields with `Map<RobotVariant, RobotFamily> families` (`HashMap`)
  - Add `initialize()` — populates map by iterating `RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)`, using `isFullPalette()` to select `create(variant)` vs `create(variant, getPalette())`
  - Add `get(RobotVariant)` — `requireNonNull` with descriptive message
  - Replace `reloadFromConfig()` body with uniform loop; call `def.applyFeatureConfigurator(family, cfg)` after standard features for each variant
  - Wire `initialize()` call after `Lovely.onInitialize()` in all 6 entry points ✅
- [x] Refactor `RebootRobotFamilies` — identical pattern with `ModTarget.REBOOT` and `RebootConfigs`
- [x] Remove all per-entity deprecated static fields from `SharedConfigs.Common` (the entire `// LEGACY INDIVIDUAL ENTITY CONFIGS (DEPRECATED)` block — 56 fields across 8 variants)
- [x] Remove all per-entity constants from `LovelyConstant` — `{KEY}_SPAWN` string constants, `VARIANT_{KEY}` string constants, `LEGACY_VARIANTS`, `REBOOT_VARIANTS`, `ALL_VARIANTS` arrays; retain `TRIBUTE_VARIANTS`
- [x] Update Fabric-side `LegacyConfigs.buildConfigProvider()` — replace `LovelyConstant.LEGACY_VARIANTS` loop with `RobotDefinitionRegistry.getVariantKeysForMod(ModTarget.LEGACY)`
- [x] Update Fabric-side `LegacyConfigs.loadDynamicEntityConfigs()` — same replacement
- [x] Update Fabric-side `RebootConfigs.buildConfigProvider()` and `loadDynamicEntityConfigs()` — same replacement with `ModTarget.REBOOT`
- [x] Update `ConfigAccessLayer.validateAllConfigs()` — replace `LovelyConstant.ALL_VARIANTS` overload with registry-driven equivalent: `RobotDefinitionRegistry.getAll().stream().map(RobotEntityDefinition::getVariantKey).toArray(String[]::new)`
- [x] Migrate all call sites of `LegacyRobotFamilies.{VARIANT}` → `LegacyRobotFamilies.get(RobotVariant.{Variant})` across lovelylib and all loaders — zero call sites found; migration complete
- [x] Migrate all call sites of `RebootRobotFamilies.{VARIANT}` → `RebootRobotFamilies.get(RobotVariant.{Variant})` — zero call sites found; migration complete
- [ ] Full build passes — zero errors across all three loaders for llovelyr and rlovelyr; verify existing 8 robots still initialize correctly at runtime

### Phase 3 — Simplify loader-side classes (llovelyr + rlovelyr, all 3 loaders)
> **Depends on Phase 2. 6 Items files + 6 Entities files + 6 Groups files migrated to map pattern.**

- [x] Refactor `LegacyItems` — Forge/NeoForge (× 2):
  - Replace per-entity `RegistryObject<Item>` / `DeferredItem<Item>` fields with `Map<RobotVariant, RegistryObject<Item>>` / `Map<RobotVariant, DeferredItem<Item>>` (`HashMap`)
  - Add `registerAll()` — iterates `RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)`, calls `registerSpawnItem(key, supplier, rarity, stackSize)`
  - Add `getSpawnItem(RobotVariant)` — `requireNonNull` with descriptive message
  - Update `registerModel()` — loop replaces per-entity calls
- [x] Refactor `LegacyItems` — Fabric (× 1):
  - Map type `Map<RobotVariant, Item>` (unwrapped — no registry wrapper)
  - `registerAll()` and `getSpawnItem(RobotVariant)` analogous to Forge
- [x] Refactor `LegacyEntities` — Forge/NeoForge (× 2):
  - Replace per-entity `RegistryObject` / `DeferredHolder` fields with map
  - `registerAll()` loop calling `registerRobot(key, family)` from registry
  - `getEntityType(RobotVariant)` accessor
  - `registerAttribute()` — loop
  - `registerRender()` — loop; `def.usesNativeRenderer()` branches `NativeRobotRenderer::new` vs `RendererFactory.create(ctx)`
  - `registerNativeRobotFeature()` — loop attaches PickupFeature + DropFeature
- [x] Refactor `LegacyEntities` — Fabric (× 1):
  - Map type `Map<RobotVariant, EntityType<NativeRobotEntity>>` (unwrapped)
  - Renderer registration via `EntityRendererRegistry.register(type, ...)` with `RendererFactory` branch
- [x] Refactor `LegacyGroups` — all 3 loaders (× 3):
  - `displayItems` and `addSpawnEggs` loops replace per-entity `output.accept()` / `event.accept()` calls
  - Fabric `allItemsEntry()` similarly looped
- [x] Repeat all 6 files for `rlovelyr` (`RebootItems`, `RebootEntities`, `RebootGroups` × 3 loaders)
- [x] Migrate all remaining call sites:
  - `LegacyItems.{VARIANT}_SPAWN` → `LegacyItems.getSpawnItem(RobotVariant.{Variant})` — zero stale references confirmed by grep ✅
  - `LegacyEntities.{VARIANT}` → `LegacyEntities.getEntityType(RobotVariant.{Variant})` — zero stale references confirmed ✅
  - Same for all `Reboot*` equivalents ✅
- [ ] Set up Gradle copy task for item model JSONs — deferred; `// TODO ADR-023: item model triplication — set up Gradle copy task` comment added to build.gradle scope (see Implementation Notes)
- [ ] Full in-game test — Forge, NeoForge, and Fabric loaders for both llovelyr and rlovelyr

### Phase 4 — Proof of concept and cleanup
> **Depends on Phase 3. Validates the new 2-step workflow end-to-end.**

- [ ] Add a temporary test entity to `RobotVariant`: `public static final RobotVariant TestBot = register(8, "testbot");`
- [ ] Add one builder call to `LegacyRobotDefinitions.register()` for `TestBot` — full palette, standard stats, `forMod(ModTarget.LEGACY)`
- [ ] Add minimal assets for `testbot` (copy from `vanilla` as placeholder) — verify the entity spawns in-game on all three loaders with correct name and texture
- [ ] Confirm zero other Java files were modified — if any file required touching, document the gap and fix it before completing this phase
- [ ] Remove the test entity (`TestBot` constant, builder call, and placeholder assets)
- [ ] Update `docs/guidelines/New_Robot_Family_Guide.md` — replace §§1.1–1.11 with a reference to this ADR and the 2-step workflow from ADR-023 §8
- [ ] Final build — zero errors and zero warnings from the new registration pipeline across all modules

---

## Implementation Notes

### `RobotVariant` Map Type for Loader-Side Maps
`RobotVariant` is no longer an enum, so `EnumMap` cannot be used. Replace all `EnumMap<RobotVariant, ...>` with plain `HashMap<RobotVariant, ...>` or `LinkedHashMap<RobotVariant, ...>` (use `LinkedHashMap` when insertion order matters for iteration). `RobotVariant.hashCode()` returns `m_id` — the distribution is suitable for `HashMap` with the small set of 8 built-in variants.

### Kitsune `featureConfigurator` in `LegacyRobotDefinitions`
Kitsune's `BoneVisibilityFeature` configuration must move from the `reloadFromConfig()` loop body in `LegacyRobotFamilies` into the `.featureConfigurator(...)` builder call in `LegacyRobotDefinitions`. The `applyFeatureConfigurator(family, cfg)` call in the loop is a no-op for all other variants. Do not leave a Kitsune-specific `if` block in the loop — that defeats the purpose of the configurator pattern.

### Stat Value Parity During Migration
During Phase 1 (definition builder declaration) the stat values in `LegacyRobotDefinitions` must match exactly those currently in `lovelylib.source.legacy.LegacyConfigs.Default`. Cross-reference the `Default` map values before filling the builder calls. After Phase 2 removes the `Default` map entries, the builder becomes the canonical source — any divergence before Phase 2 completes will produce incorrect default configs.

### `Lovely.onInitialize()` Placement on Forge/NeoForge
On Forge and NeoForge, `Lovely.onInitialize()` must be called in the **mod constructor** before `ITEMS.register(eventBus)` and `ENTITIES.register(eventBus)`. The `DeferredRegister` defers supplier resolution to `RegisterEvent` — but `LegacyRobotFamilies.initialize()` must have already run before those suppliers are invoked. If `Lovely.onInitialize()` is placed after event bus registration, `LegacyRobotFamilies.get(variant)` will throw `NullPointerException` when the deferred supplier fires.

### Fabric `LegacyItems` / `LegacyEntities` Map Types
Fabric registers items and entity types immediately during `onInitialize()`, returning the unwrapped object directly (`Item`, `EntityType<T>`). The map type must be `Map<RobotVariant, Item>` and `Map<RobotVariant, EntityType<NativeRobotEntity>>` — no `RegistryObject` wrapper. The Forge/NeoForge maps use `Map<RobotVariant, RegistryObject<Item>>` and `Map<RobotVariant, RegistryObject<EntityType<NativeRobotEntity>>>`. Do not try to share a common map type across loaders.

### `RendererFactory` Resolution on Forge/NeoForge
The `RobotEntityDefinition.RendererFactory` stores an `Object create(Object context)` factory. In `LegacyEntities.registerRenderers()` on Forge, the cast is:

```java
event.registerEntityRenderer(type,
    ctx -> (EntityRenderer<NativeRobotEntity>) def.getRendererFactory().create(ctx));
```

The `@SuppressWarnings("unchecked")` annotation is required on this method. On Fabric, the equivalent is `EntityRendererRegistry.register(type, ctx -> ...)`. Both patterns are documented in ADR-023 §5.3.

### `RebootRobotDefinitions` Scope
All 8 current variants exist identically in both Legacy and Reboot. Bunny3 is already declared with `.forMods(ModTarget.LEGACY, ModTarget.REBOOT)` in `LegacyRobotDefinitions`. The other 7 variants are currently Legacy-only — they should remain `forMod(ModTarget.LEGACY)` unless there is an explicit decision to add them to Reboot as part of this sprint. `RebootRobotDefinitions.register()` may be empty at the end of this sprint if no Reboot-exclusive variants exist yet; this is valid and expected.

### `TRIBUTE_VARIANTS` Retention
`LovelyConstant.TRIBUTE_VARIANTS` must not be removed. Tribute's Fabric-side `TributeConfigs` uses the same `buildConfigProvider()` / `loadDynamicEntityConfigs()` loop pattern as Legacy but is explicitly excluded from `RobotDefinitionRegistry`. Its array stays until a separate ADR addresses Tribute config.

### `ConfigAccessLayer.validateAllConfigs()` No-Arg Overload
The no-arg overload currently calls `validateAllConfigs(LovelyConstant.ALL_VARIANTS)`. Since `ALL_VARIANTS` is being removed, replace it with:

```java
public static Map<String, Boolean> validateAllConfigs() {
    return validateAllConfigs(
        RobotDefinitionRegistry.getAll().stream()
            .map(RobotEntityDefinition::getVariantKey)
            .toArray(String[]::new));
}
```

### Gradle Copy Task (Phase 3, Optional)
If the Gradle copy task for item model JSONs is deferred, add an explicit `// TODO ADR-023: item model triplication — set up Gradle copy task` comment in each loader's `build.gradle` and document the three file paths that must stay in sync per variant. Do not silently leave it undocumented.

---

## Story Points

| Phase | Description | Points |
|-------|-------------|--------|
| Phase 0 — Pre-migration bug fix | 2 files, 1-line fix each | 2 |
| Phase 1 — New infrastructure (lovelylib) | 7 new files, RobotVariant migration, 6 entry point wires | 21 |
| Phase 2 — Simplify lovelylib consumers | 10 files refactored, 3 removed, call site migration | 18 |
| Phase 3 — Loader-side consolidation (× 6 per mod × 2 mods) | 12 Items/Entities/Groups files + call site migration | 21 |
| Phase 4 — Proof of concept and cleanup | test entity, guide update, final validation | 5 |
| **Total** | | **67** |

> Phase 3 carries the most risk due to the Fabric vs Forge/NeoForge type divergence in map generics and renderer registration. Allocate the first day of Phase 3 to confirming the Forge pattern compiles cleanly before replicating across all 6 × 2 = 12 files.

---

## Files Created / Modified

### lovelylib Common — new
- `sources/common/lovelylib-1.21.1/Common/.../entity/definition/RobotVariant.java`
- `sources/common/lovelylib-1.21.1/Common/.../entity/definition/ModTarget.java`
- `sources/common/lovelylib-1.21.1/Common/.../entity/definition/EntityStats.java`
- `sources/common/lovelylib-1.21.1/Common/.../entity/definition/RobotEntityDefinition.java`
- `sources/common/lovelylib-1.21.1/Common/.../entity/definition/RobotDefinitionRegistry.java`
- `sources/common/lovelylib-1.21.1/Common/.../source/legacy/LegacyRobotDefinitions.java`
- `sources/common/lovelylib-1.21.1/Common/.../source/reboot/RebootRobotDefinitions.java`

### lovelylib Common — modified
- `sources/common/lovelylib-1.21.1/Common/.../Lovely.java` — new `onInitialize()` method
- `sources/common/lovelylib-1.21.1/Common/.../common/shared/LovelyIdentifier.java` — switch → registry lookup
- `sources/common/lovelylib-1.21.1/Common/.../api/configs/ConfigAccessLayer.java` — switch → registry loop; `validateAllConfigs()` no-arg overload
- `sources/common/lovelylib-1.21.1/Common/.../common/configs/SharedConfigs.java` — remove deprecated per-entity static fields block
- `sources/common/lovelylib-1.21.1/Common/.../common/shared/LovelyConstant.java` — remove per-entity constants and variant arrays; retain `TRIBUTE_VARIANTS`
- `sources/common/lovelylib-1.21.1/Common/.../source/legacy/LegacyConfigs.java` — static block → registry loop
- `sources/common/lovelylib-1.21.1/Common/.../source/reboot/RebootConfigs.java` — static block → registry loop
- `sources/common/lovelylib-1.21.1/Common/.../source/legacy/LegacyRobotFamilies.java` — map pattern + `initialize()` + uniform `reloadFromConfig()` loop
- `sources/common/lovelylib-1.21.1/Common/.../source/reboot/RebootRobotFamilies.java` — same

### lovelylib Common — deleted
- `sources/common/lovelylib-1.21.1/Common/.../entity/enums/RobotVariant.java`

### llovelyr-1.21.1 — modified (all 3 loaders)
- `Forge/.../source/LegacyConfigs.java` — `LEGACY_VARIANTS` → `RobotDefinitionRegistry.getVariantKeysForMod(LEGACY)`
- `Forge/.../source/LegacyItems.java` — map pattern + `registerAll()` + `getSpawnItem()`
- `Forge/.../source/LegacyEntities.java` — map pattern + `registerAll()` + `getEntityType()` + loop callbacks
- `Forge/.../source/LegacyGroups.java` — loop callbacks
- `Forge/LovelyLegacy.java` — `Lovely.onInitialize()` first call + `LegacyRobotFamilies.initialize()`
- NeoForge — identical 5 files
- Fabric — identical 5 files (map types differ: `Item` / `EntityType<T>` unwrapped)

### rlovelyr-1.21.1 — modified (all 3 loaders)
- Same 5 files × 3 loaders for `Reboot*` equivalents

### docs — modified
- `docs/guidelines/New_Robot_Family_Guide.md` — replace §§1.1–1.11 with ADR-023 §8 workflow reference
