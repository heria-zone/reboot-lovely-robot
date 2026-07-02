# ADR-023 Critical Analysis — Insight Notes

**Status:** Analysis Complete  
**Date:** 2026-07-01  
**Author:** Serge Maia (architectural-consolidation review)  
**Subject:** ADR-023 Entity Registration Consolidation — `RobotDefinitionRegistry`  
**Scope:** `hzlib-1.21.1`, `lovelylib-1.21.1`, `llovelyr-1.21.1`, `rlovelyr-1.21.1`, `tlovelyr-1.21.1`

---

## Summary Verdict

The ADR is **directionally correct and worth implementing**. The scatter problem it diagnoses is real, the bug analysis is accurate, and the registry pattern is the right architectural move. However, several of its claims are based on assumptions about what the codebase looks like — not what it actually is. Some of those assumptions simplify the actual situation, and a few would produce broken code if followed literally.

This document catalogs what the ADR got right, what it got wrong by assumption, and what it missed entirely.

---

## 1. What the ADR Got Right

### 1.1 The Scatter Problem Is Real and Accurately Counted

The ADR's scatter map in §1.1 is accurate. Adding a variant does currently require touching:

- `LovelyConstant.java` — spawn key constant, variant key constant, variant array entries
- `RobotVariant.java` — enum entry
- `SharedConfigs.Common` — 7 deprecated static fields
- `ConfigAccessLayer.getDefaultEntityConfig()` — switch case
- `LovelyIdentifier.getTranslation(RobotVariant)` — switch case
- `LegacyRobotFamilies` / `RebootRobotFamilies` — static field + full `reloadFromConfig()` block
- `LegacyConfigs` / `RebootConfigs` — `Default.put()` in static initializer
- `{Mod}Items.java` × 3 loaders — field + `registerModel()` call
- `{Mod}Entities.java` × 3 loaders — field + attribute + renderer + feature entries
- `{Mod}Groups.java` × 3 loaders — `output.accept()` + `addSpawnEggs()` entry

The 30-edit count for a variant shared across both mods on all three loaders is accurate.

### 1.2 The Four Bunny3 Bug Classes Are Confirmed Real

All four are structurally confirmed in the code:

- **Bug 1 ("Vanilla" in overhead messages):** `LovelyIdentifier.getTranslation(RobotVariant)` has a `default` branch that returns `getVariantTranslation(LovelyConstant.VARIANT_VANILLA)`. Any unmatched variant silently shows "Vanilla."

- **Bug 2 (Zero stats on Fabric):** `LegacyConfigs.getEntityConfig()` reads from `Entities` map only. `Entities` is populated by the mod-side Fabric `LegacyConfigs.loadDynamicEntityConfigs()` at init time. If that call is missed, or a variant is absent from the loop, `getEntityConfig()` returns `EntityConfigData.getDefault()` — not the `Default` map values. The `Default` map (pre-populated in the static initializer) is never consulted by `getEntityConfig()`, only by `getDefaultConfig()`. This is a real active bug: the two maps are connected only by the call chain `getDefaultConfig(variant)` used as a fallback in `loadDynamicEntityConfigs()`.

- **Bug 3 (`variant.*` key in wrong lang file):** Confirmed. The `getTranslation(RobotVariant)` path routes through `getVariantTranslation()` which produces `variant.lovelylib.{key}` — the key lives in lovelylib, not in the mod. A variant whose lang key is placed in the mod's lang file silently fails.

- **Bug 4 (Missing item model on one loader):** The triple-copy structure is confirmed. Item model JSON files are maintained independently in three loader resource directories. Forge, Fabric, and NeoForge each carry their own copies.

### 1.3 The Registry + Definition Builder Pattern Is the Right Solution

The `RobotEntityDefinition` builder collecting all nine facts about a variant in one place — palette, stats, mods, display name, renderer — is a well-chosen pattern. It is the correct answer to the scatter problem. The `RobotDefinitionRegistry.seal()` fail-fast mechanism is good defensive design.

### 1.4 tlovelyr Correctly Excluded

The ADR correctly identifies Tribute as "fixed roster, never extended." Tribute is structurally different in every relevant way: `TributeRobotEntity` (not `NativeRobotEntity`), `TributeRobotRenderer` for all variants, `createTribute()` which instantiates a `TributeRobotFamily` subclass, and the `lovely_robot` namespace for textures. None of the consolidation machinery applies to it.

---

## 2. What the ADR Got Wrong — Assumption vs. Reality

### 2.1 `RobotVariant` Already Stores the Key String — `name().toLowerCase()` Is Wrong

The ADR states in §3.3:

> `e.g. RobotVariant.Bunny3 → "bunny3"` — key derived via `variant.name().toLowerCase()`

**Reality:** `RobotVariant` already has `getName()` which returns the key string passed to the constructor — `LovelyConstant.VARIANT_BUNNY3` = `"bunny3"`. The enum constructor signature is `RobotVariant(int id, String name)`. The key string is `m_name`, and `getName()` is already the correct accessor.

`variant.name().toLowerCase()` would *happen to produce the same result* for all current eight variants, because all enum names match their key strings (Bunny → "bunny", Dragon → "dragon", etc.). But this is coincidental alignment, not structural coupling. If someone named a variant `BunnyNeo` with key string `"bunny_neo"`, `name().toLowerCase()` would return `"bunnyneo"` — wrong. The correct implementation is `variant.getName()`.

The `RobotEntityDefinition.getVariantKey()` method should delegate to `variant.getName()`, not re-derive it.

### 2.2 `EntityConfigData` Has 7 Parameters, Not 8

The ADR's `EntityStats.toEntityConfigData()` in §3.2 calls:

```java
return new SharedConfigs.EntityConfigData(
    maxLevel, baseHp, baseAttack, attackSpeed,
    baseDefense, baseToughness, 0F, movementSpeed  // ← 8 args
);
```

**Reality:** The actual `EntityConfigData` constructor takes **7 parameters**:

```java
public EntityConfigData(int maxLevel, int baseHp, int baseAttack, float attackSpeed,
                        int baseDefense, float baseToughness, float movementSpeed)
```

There is no `knockbackResistance` parameter in `EntityConfigData`. The ADR invents an 8-parameter constructor that does not exist. The `0F` knockback value is passed as a separate literal directly in `RobotFamily.withCombatStats()` — it's not a stored config value.

`EntityStats.toEntityConfigData()` must produce a 7-field call, not 8. This is a compilation error if implemented literally.

### 2.3 `LovelyLib.onInitialize()` Does Not Exist

The ADR's §6 initialization lifecycle builds on:

```
1. LovelyLib.onInitialize()
   ├── LegacyRobotDefinitions.register()
   ├── RebootRobotDefinitions.register()
   └── RobotDefinitionRegistry.seal()
```

**Reality:** `Lovely.java` is a stub. It has `Lovely.initialize()` which only sets an `initialized` flag and logs a message. There are TODO comments for "Initialize robot entity registry" and "Register robot types" that are not implemented. No `onInitialize()` method exists.

The real initialization flow is: loader entry points (`LovelyLegacy.onInitialize()` on Fabric, `LovelyLegacy` constructor on Forge) call `Legacy.init()`, then `LegacyConfigs.register()`, then `LegacyRobotFamilies.reloadFromConfig()`.

The ADR's lifecycle diagram describes a future state that must be built from scratch — it is not describing a refactoring of existing infrastructure. This is a significant implementation gap the ADR does not flag.

### 2.4 The Config System Is More Advanced Than the ADR Assumes

The ADR describes `LegacyConfigs` as having a `static {}` block with `Default.put()` entries and nothing else (§4.6). This undersells the actual situation considerably.

**Reality:** `LegacyConfigs` (the lovelylib-side class) does have the `Default.put()` static block. But the mod-side `LegacyConfigs` (in `llovelyr/Fabric/source/`) is a full `SimpleConfig`-based config file system that:

- Generates a `llovelyr.properties` file in the game's `config/` directory
- Loops over `LovelyConstant.LEGACY_VARIANTS` to build config entries dynamically — not per-entity hardcoded
- Loads back from the file on startup and reload
- Stores results in `lovelylib.source.legacy.LegacyConfigs.Entities` for `reloadFromConfig()` to consume

The **Fabric config loop already uses `LovelyConstant.LEGACY_VARIANTS`** — exactly the array the ADR proposes to replace with `RobotDefinitionRegistry.getVariantKeysForMod(LEGACY)`. This means the Fabric config infrastructure is already partially data-driven. The ADR's description of "one `Default.put()` per entity in a static block" applies only to the lovelylib-side stub, not the full config pipeline.

`ConfigAccessLayer` already has a three-phase fallback: serialized config → individual keys → default switch. The switch is in a `private` method `getDefaultEntityConfig()`, not the main public path.

### 2.5 LegacyRobotFamilies and RebootRobotFamilies Are Structurally Identical

The ADR treats them as separate concerns with different entity sets:

> "Entities shared between Legacy and Reboot are declared with `.forMods(LEGACY, REBOOT)` in one file."

**Reality:** Both `LegacyRobotFamilies` and `RebootRobotFamilies` contain identical sets of all 8 variants. Their `reloadFromConfig()` methods are line-for-line identical except for the config source (`LegacyConfigs` vs `RebootConfigs`). Bunny3 is already in both. The "shared entities" concept the ADR proposes doesn't match the current architecture — both mods independently own their full variant set.

The `forMods(LEGACY, REBOOT)` design proposed in §3.5 implies single-declaration shared entities, but the actual code uses separate classes that happen to be identical. The ADR's consolidation approach is valid, but the migration is more nuanced: it's not "move shared entities to one file," it's "collapse two identical files into one registry."

### 2.6 The Loader-Side Map Pattern Diverges Between Fabric and Forge/NeoForge

The ADR's §5.2 shows a map populated with `RegistryObject<Item>` and §5.3 shows `RegistryObject<EntityType<NativeRobotEntity>>`. This is Forge/NeoForge terminology only.

**Reality:**

- **Forge/NeoForge:** `ITEMS` is a `DeferredRegister<Item>`, each item is a `RegistryObject<Item>`, deferred until the `RegisterEvent` fires
- **Fabric:** Items are registered immediately via `Registry.register()` during `onInitialize()` and returned as `Item` directly (no wrapper). Entity types are `EntityType<NativeRobotEntity>` directly.

The ADR's `getSpawnItem(RobotVariant)` returning `RegistryObject<Item>` would not compile on Fabric, where the stored value is `Item`, not `RegistryObject<Item>`. The pattern is correct in intent but needs per-loader type signatures. This is not a minor note — it means the consolidation requires two different generic map types per loader, or a common interface.

### 2.7 The ADR Underestimates the Renderer Dimension

§3.3 presents `renderer(Class<?>)` as an edge case:

> "Use only when the geo model has structural features NativeRobotRenderer cannot handle (e.g. Bunny's ear bone, Kitsune's tail unlock)."

**Reality:** Both `BunnyRenderer` and `KitsuneRenderer` exist and are used in all three loaders for both llovelyr and rlovelyr. That is already **two custom renderers across six files** (2 mods × 3 loaders). The ADR's `renderer(Class<?>)` field correctly captures this, but the framing ("edge case") understates it. Custom renderers are not rare — they exist for 2 of the 8 current variants (25%).

More critically, the ADR does not address how the `renderer(Class<?> rendererClass)` is resolved in the loop. On Forge, `event.registerEntityRenderer(entityType, MyRenderer::new)` uses a method reference. A stored `Class<?>` cannot be directly converted to a renderer factory without reflection or a different mechanism — a `Function<Context, EntityRenderer>` factory lambda (which the ADR itself mentions in §11 future considerations) is the correct design from day one.

---

## 3. What the ADR Missed Entirely

### 3.1 The `RobotVariant` Enum Is Already Doing Part of the Job

`RobotVariant` is not purely a serialization anchor (integer ID). It already carries `m_name` (the key string) and exposes `getName()` and `byName(String)` for lookup. The `CODEC` array enables `byId(int)`.

The ADR proposes deriving all key strings from the registry at runtime, but those strings are already embedded in the enum. The enum is actually a reasonable mini-registry already — it just lacks palette and stats. The insight here is: `getName()` should be used throughout the new design, not re-derived.

### 3.2 `LegacyConfigs.getEntityConfig()` Has a Real Active Bug the ADR Doesn't Diagnose

The `Default` and `Entities` maps in `lovelylib.source.legacy.LegacyConfigs` are effectively disconnected. `getEntityConfig()` reads `Entities`, which is populated by the Fabric-side `loadDynamicEntityConfigs()`. But `getEntityConfig()` falls back to `EntityConfigData.getDefault()` (generic defaults, not variant-specific) if the variant is not in `Entities`. The variant-specific `Default` map is never consulted by `getEntityConfig()`.

This means on Forge/NeoForge, where there is no mod-side `LegacyConfigs.register()` call to populate `Entities`, **every variant gets generic defaults from `EntityConfigData.getDefault()`**, not the carefully tuned per-variant values in the `Default` map. This is a pre-existing bug, independent of the ADR's scope, but the ADR's design would accidentally fix it (by eliminating both maps in favor of the registry defaults).

This bug should be documented as a Phase 0 fix target, not left to be resolved incidentally.

### 3.3 The Initialization Order Problem Is Deeper Than §6 Acknowledges

The ADR's §6 lifecycle diagram describes calling `LegacyRobotDefinitions.register()` before any entity registration. But in the current Forge/NeoForge flow, `LegacyRobotFamilies` static fields are initialized at class-load time — not in a controlled lifecycle callback. The static `public static final RobotFamily BUNNY = create(RobotVariant.Bunny)` runs when the class is first referenced, which could happen before any user-defined initialization sequence.

Under the new design, the equivalent static fields become map-based. If the map is populated in a controlled `registerAll()` call, but the class is accessed before `registerAll()` runs, `getEntityType(variant)` would return null or throw. The ADR's sealed registry helps, but it requires that no code accesses the loader-side entity maps before `registerAll()` completes. On Forge, this means `registerAll()` must be wired to the `RegisterEvent` or called from the mod constructor before event bus registration — not from a free-standing init method.

### 3.4 `LovelyConstant.LEGACY_VARIANTS` Is Consumed in More Places Than the ADR Lists

The ADR proposes replacing `LEGACY_VARIANTS` and `REBOOT_VARIANTS` arrays with `RobotDefinitionRegistry.getVariantKeysForMod()`. But the arrays are consumed in:

- Fabric-side `LegacyConfigs.buildConfigProvider()` — loops over `LEGACY_VARIANTS` to generate config sections per variant
- Fabric-side `LegacyConfigs.loadDynamicEntityConfigs()` — loops over `LEGACY_VARIANTS` to populate `Entities` map
- `ConfigAccessLayer.validateAllConfigs()` — calls `validateAllConfigs(LovelyConstant.ALL_VARIANTS)`

The config file generation loop is a key consumer that the ADR does not mention. After migration, `LEGACY_VARIANTS` must be replaced by `RobotDefinitionRegistry.getVariantKeysForMod(LEGACY)` in all these call sites, not just the three the ADR lists.

### 3.5 The ADR Does Not Address `TRIBUTE_VARIANTS` or `ALL_VARIANTS`

`LovelyConstant` has four variant arrays: `ALL_VARIANTS` (marked TODO: Remove), `TRIBUTE_VARIANTS`, `LEGACY_VARIANTS`, and `REBOOT_VARIANTS`. The ADR only addresses Legacy and Reboot. `TRIBUTE_VARIANTS` is used in `TributeConfigs` (same mod-side Fabric config loop pattern). If the registry is introduced, the Tribute mod should use `RobotDefinitionRegistry.getVariantKeysForMod(TRIBUTE)` too — or Tribute needs a separate non-registry mechanism since the ADR explicitly excludes it from `RobotDefinitionRegistry` (§3.1, `ModTarget.TRIBUTE` defined but "never used").

This creates an inconsistency: three of four `ModTarget` values are handled by the registry; the fourth has a dedicated Tribute config loop that still reads `TRIBUTE_VARIANTS` manually.

### 3.6 `BoneVisibilityFeature` for Kitsune Is Variant-Specific Logic Outside the Definition

The ADR's proposed `reloadFromConfig()` loop in §4.5 is a uniform loop across all Legacy variants:

```java
for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
    // uniform: withCombatStats + withFeature(LevelFeature) + withFeature(CombatLevelFeature)
    //          + withFeature(EnchantmentFeature) + withFeature(ProtectionFeature)
}
```

**Reality:** `KITSUNE`'s config block in `LegacyRobotFamilies.reloadFromConfig()` adds a `BoneVisibilityFeature` after the standard features. This is variant-specific behavior that cannot be expressed in a uniform loop.

The ADR does not show how variant-specific feature additions are handled in the new design. The `RobotEntityDefinition` builder has no mechanism for "extra features applied at reload time." This means the loop cannot be fully uniform — there will still be post-loop per-variant customization code, or the definition must be extended with a feature configurator callback.

The same applies to `DRAGON`'s knockback resistance (0.5F in comments though code currently passes 0F) and any future variant that needs a custom feature beyond the standard set.

---

## 4. The "No More Enums" Goal — Assessment

The ADR maintains `RobotVariant` as an enum, calling it "unavoidable: integer ID must be assigned at compile time for serialization stability." This is correct. The NBT data format stores integers as `TAG_Int`, and Minecraft's entity NBT uses integer type IDs for custom data. Changing from compile-time integer IDs to dynamic runtime IDs would require a world migration for all existing saved entities.

However, the goal of "no more defining enums for bunny, vanilla, ..." means something subtler: **no more enum entries that require cascading changes in 12 other files**. The ADR achieves this. After migration, a new `RobotVariant.Kitsune2(8, "kitsune2")` entry requires only the definition builder — the enum entry is the irreducible minimum.

What *can* be eliminated from the enum:
- The `String name` field is already in the enum. The ADR proposal to derive keys from enum names is wrong (see §2.1), but the solution is that `getName()` already exists and should be used directly.
- The enum entry itself remains necessary. It cannot be replaced by a string registration without breaking saved worlds.

What is achievable: reducing the enum to truly just integer-ID and name, with all other data moving to the definition builder. The current `RobotVariant` already stores only ID and name — this goal is already half-met structurally. The ADR completes it by removing the dependent scattered additions.

---

## 5. Corrected Design Notes

These corrections should be incorporated before implementation begins.

### 5.1 `getVariantKey()` Should Use `variant.getName()`

```java
// Wrong (ADR assumption):
public String getVariantKey() {
    return variant.name().toLowerCase(); // coincidentally works today, breaks on multi-word names
}

// Correct:
public String getVariantKey() {
    return variant.getName(); // already returns "bunny", "dragon", etc. from the enum's m_name field
}
```

### 5.2 `EntityStats.toEntityConfigData()` Must Use 7 Parameters

```java
// Wrong (ADR §3.2 — 8 args, constructor does not exist):
return new SharedConfigs.EntityConfigData(
    maxLevel, baseHp, baseAttack, attackSpeed,
    baseDefense, baseToughness, 0F, movementSpeed
);

// Correct (7 args, matching actual constructor):
return new SharedConfigs.EntityConfigData(
    maxLevel, baseHp, baseAttack, attackSpeed,
    baseDefense, baseToughness, movementSpeed
);
// knockbackResistance is not a config value; it's passed as a literal 0F
// in RobotFamily.withCombatStats() separately
```

### 5.3 The Renderer Field Needs a Factory, Not a Class Reference

```java
// ADR §3.3 — stored as raw Class<?>, cannot be used as renderer factory:
private final Class<?> rendererClass;

// Corrected — use a functional interface:
// Option A: factory lambda (cleaner, no reflection)
@FunctionalInterface
public interface RendererFactory<T extends Entity> {
    EntityRenderer<T> create(EntityRendererProvider.Context ctx);
}
private final RendererFactory<?> rendererFactory; // null = NativeRobotRenderer

// In definition builder:
public Builder renderer(RendererFactory<?> factory) {
    this.rendererFactory = factory;
    return this;
}

// In LegacyRobotDefinitions:
.renderer(KitsuneRenderer::new)
```

### 5.4 Kitsune (and Future Variants) Need a Feature Configurator

The uniform `reloadFromConfig()` loop cannot be fully uniform without a mechanism for per-variant feature additions:

```java
// Add to RobotEntityDefinition.Builder:
public Builder featureConfigurator(BiConsumer<RobotFamily, SharedConfigs.EntityConfigData> configurator) {
    this.featureConfigurator = configurator;
    return this;
}

// In LegacyRobotDefinitions, Kitsune entry:
.featureConfigurator((family, cfg) -> family.withFeature(
    BoneVisibilityFeature.class,
    BoneVisibilityFeature.builder()
        .showWhen("tail0",  e -> true)
        .hideWhen("tail01", e -> true)
        // ...
        .build()
))
```

The loop in `reloadFromConfig()` then calls the configurator after standard features:

```java
def.applyFeatureConfigurator(family, cfg); // no-op if null
```

### 5.5 `Lovely.java` Needs a Real `onInitialize()` — It Is Not a Refactoring

The ADR's §6 lifecycle requires `Lovely.onInitialize()` (or equivalent) to call `LegacyRobotDefinitions.register()` before any entity/item registration. This method does not exist. It must be created as part of Phase 1, not assumed. The Fabric entry points (`LovelyLegacy.onInitialize()`, `LovelyReboot.onInitialize()`) must call it before their own registration sequences.

### 5.6 Fix the LegacyConfigs/RebootConfigs `getEntityConfig()` Bug in Phase 0

Before the ADR refactoring begins, fix `lovelylib.source.legacy.LegacyConfigs.getEntityConfig()` and `RebootConfigs.getEntityConfig()` to fall back to the `Default` map, not `EntityConfigData.getDefault()`:

```java
// Current (bug — ignores Default map):
public static EntityConfigData getEntityConfig(String variant) {
    EntityConfigData config = Entities.get(variant);
    if (config == null) return EntityConfigData.getDefault(); // ← wrong fallback
    return config.validateOrDefault();
}

// Fixed:
public static EntityConfigData getEntityConfig(String variant) {
    EntityConfigData config = Entities.get(variant);
    if (config == null) return getDefaultConfig(variant); // ← uses Default map
    return config.validateOrDefault();
}
```

This is a standalone one-line fix that unblocks correct behavior on Forge/NeoForge today, independent of the ADR's scope.

---

## 6. Implementation Risk Assessment

| Concern | Risk | Notes |
|---------|------|-------|
| `name().toLowerCase()` key derivation | High | Breaks on multi-word names; use `getName()` instead |
| 8-param `toEntityConfigData()` | High | Compilation error; fix to 7 params |
| `Lovely.onInitialize()` not existing | Medium | Phase 1 build item, not a refactor |
| Loader-type divergence (Fabric `Item` vs `RegistryObject<Item>`) | Medium | Map generic type must be per-loader |
| `renderer(Class<?>)` unusable as factory | Medium | Needs `RendererFactory` interface |
| Kitsune `BoneVisibilityFeature` not in uniform loop | Medium | Needs `featureConfigurator` callback |
| LegacyConfigs `getEntityConfig()` fallback bug | Medium | Fix now in Phase 0 before migration |
| Static-init ordering on Forge | Medium | `registerAll()` must be deferred, not static |
| `TRIBUTE_VARIANTS` left unaddressed | Low | Inconsistency, not a blocker |
| `ALL_VARIANTS` (marked TODO: Remove) | Low | Delete when LEGACY/REBOOT arrays go |

---

## 7. What to Do With the Enum — The Real Answer

The user's goal — "eradicate having to define enums for the robots variants" — is best interpreted as: **eliminate the cascade**. You cannot remove the enum entirely without breaking saved worlds (serialization). But you can make the enum entry the *only* change needed.

After the ADR is implemented correctly:

1. Add `Sentry(8, "sentry")` to `RobotVariant` — one line
2. Add a builder call to `LegacyRobotDefinitions` — eight lines

That is the end state. No switch cases, no static fields, no per-entity constants, no array maintenance. The enum entry is the irreducible primitive — it is not bureaucracy, it is the serialization contract. Everything else is eliminated.

The only path beyond this — fully dynamic string-based variant IDs without compile-time enum entries — requires a world migration system and a registry that maps string keys to integer IDs at world-load time. That's a valid future direction (the ADR §11 mentions it). It's not in scope for ADR-023.

---

## 8. Cross-Reference

| ADR Section | Status | Notes |
|-------------|--------|-------|
| §1.1 Scatter Problem | ✅ Accurate | Count is correct |
| §1.2 Bug Root Causes | ✅ Accurate | All four confirmed in code |
| §1.3 Nine Facts Per Entity | ✅ Accurate | Good abstraction |
| §2 Decision | ✅ Sound | Two-step workflow is achievable |
| §3.1 `ModTarget` | ✅ Sound | TRIBUTE defined but not used in registry — see §3.5 above |
| §3.2 `EntityStats` | ⚠️ Wrong constructor | 8 params → 7 params |
| §3.3 `RobotEntityDefinition` | ⚠️ Two issues | `getVariantKey()` wrong; `renderer(Class<?>)` insufficient |
| §3.4 `RobotDefinitionRegistry` | ✅ Sound | Sealed registry design is correct |
| §3.5 `LegacyRobotDefinitions` | ✅ Sound | Structure is right |
| §4.1 `LovelyConstant` removal | ✅ Sound | Replace with `getName()` / registry |
| §4.2 `SharedConfigs` removal | ✅ Sound | Fields already marked deprecated |
| §4.3 `ConfigAccessLayer` refactor | ✅ Sound | Loop replaces switch correctly |
| §4.4 `LovelyIdentifier` refactor | ✅ Sound | Eliminates Bug 1 correctly |
| §4.5 `LegacyRobotFamilies` refactor | ⚠️ Incomplete | Kitsune BoneVisibilityFeature not handled in uniform loop |
| §4.6 `LegacyConfigs` refactor | ✅ Sound | But undersells current state (already partially data-driven) |
| §5.1–5.4 Loader-side | ⚠️ Forge-only types | `RegistryObject<>` wrappers don't apply to Fabric |
| §6 Initialization Lifecycle | ⚠️ Assumes non-existent entry point | `Lovely.onInitialize()` must be created |
| §7 Item Model JSON | ✅ Sound | Gradle copy task is the right solution |
| §9 Before/After Table | ✅ Accurate | Numbers match code reality |
| §10 Eliminated Bug Classes | ✅ Accurate | All four correctly eliminated |
| §12 Phase Plan | ✅ Reasonable | Phase 0 fix for `getEntityConfig()` bug should be added |
