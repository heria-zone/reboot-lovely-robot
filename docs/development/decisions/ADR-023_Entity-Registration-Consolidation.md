# ADR-023: Entity Registration Consolidation — `RobotDefinitionRegistry`

**Status:** Revised  
**Date:** 2026-07-01  
**Revised:** 2026-07-01 — corrections applied per `ADR-023_Critical_Analysis_Notes.md`  
**Author:** Serge Maia  
**Supersedes:** Sections 1.1–1.11 of `New_Robot_Family_Guide.md` (those steps are eliminated)  
**Prerequisite:** ADR-019 (Entity Data Pipeline / `HzCompound` stack) — Phase 0 must be complete before Phase 3 of this ADR  
**Applies to:** `lovelylib`, `llovelyr`, `rlovelyr`  
**Not for:** `tlovelyr` (Tribute — fixed roster, never extended)

---

## 1. Context

### 1.1 The Scatter Problem

Adding a single robot entity currently touches **13 code locations** across **3 codebases**, plus 3–5 asset directories. The mandatory code changes are spread as follows:

```
net.heriazone.lovelylib.common.shared.LovelyConstant      ← 3 additions (spawn key, variant key, array entry)
net.heriazone.lovelylib.common.entity.definition.RobotVariant ← 1 register() call
net.heriazone.lovelylib.common.configs.SharedConfigs       ← 7 static fields (already deprecated)
net.heriazone.lovelylib.api.configs.ConfigAccessLayer     ← 1 switch case
net.heriazone.lovelylib.common.shared.LovelyIdentifier    ← 1 switch case
net.heriazone.lovelylib.source.legacy.LegacyRobotFamilies ← field + reloadFromConfig() block
net.heriazone.lovelylib.source.legacy.LegacyConfigs       ← 1 Default.put() in static block
net.heriazone.lovelylib.source.reboot.RebootRobotFamilies ← same (if Reboot)
net.heriazone.lovelylib.source.reboot.RebootConfigs       ← same (if Reboot)
{mod}/{Loader}/src/.../source/{Mod}Items.java             ← field + registerModel() × 3 loaders
{mod}/{Loader}/src/.../source/{Mod}Entities.java          ← field + attributes + renderer × 3 loaders
{mod}/{Loader}/src/.../source/{Mod}Groups.java            ← 2 entries × 3 loaders
```

For a family that belongs to both Legacy and Reboot, on all three loaders, the count rises to roughly **30 distinct code edits** before touching a single asset file.

### 1.2 Structural Bugs Caused by Scatter

All four confirmed bugs from Bunny3 (`New_Robot_Family_Guide.md § Part 4`) trace directly to scatter:

| Bug | Root Cause | Pattern |
|-----|------------|---------|
| "Vanilla" in overhead messages | Missing `case` in `LovelyIdentifier.getTranslation()` | Switch not exhaustive |
| Zero stats on Forge/NeoForge | `LegacyConfigs.getEntityConfig()` falls back to generic default instead of `Default` map | Disconnected fallback chain |
| `variant.*` key in wrong lang file | Variant display key must live in lovelylib, not the mod | No enforced coupling |
| Missing item model on one loader | Three identical JSON copies, only one or two updated | Manual triplication |

None of these bugs produce a compile error. All are silent in-game failures discovered at runtime.

**Bug 2 clarification:** `LegacyConfigs` (lovelylib-side) maintains two maps — `Default` (pre-populated in the static initializer) and `Entities` (populated at runtime by the mod-side config loader). `getEntityConfig()` reads `Entities` and falls back to `EntityConfigData.getDefault()` — the generic baseline — rather than to the variant-specific `Default` map. On Forge and NeoForge, where the mod-side `loadDynamicEntityConfigs()` never runs, every variant silently gets generic stats. This is a pre-existing active bug addressed in Phase 0 (see §12).

The root pattern: **data that belongs together is stored in separate files with no structural coupling between them.**

### 1.3 What Stays Constant About Every Entity

Stripping away the scatter, the actual information for a robot entity is:

- A variant identity (unique integer ID + key string — both already carried by `RobotVariant`)
- Which mods it ships in (Legacy / Reboot / both)
- Color palette (full 16 or a restricted subset)
- Display name (for the `variant.lovelylib.*` lang key)
- Seven stat defaults (maxLevel, baseHp, baseAttack, attackSpeed, baseDefense, baseToughness, movementSpeed)
- Whether it needs a custom renderer (most use `NativeRobotRenderer`, currently 2 of 8 need custom renderers)
- Optional per-variant feature configuration applied at reload time (e.g. Kitsune's `BoneVisibilityFeature`)

Everything else — the switches, the config fields, the family setup, the loader registrations — is mechanical derivation from these facts.

---

## 2. Decision

Introduce a **`RobotDefinitionRegistry`** in `lovelylib/common/entity/definition/` as the single source of truth for all robot entity definitions. Every robot is declared once as a `RobotEntityDefinition` using a fluent builder. All downstream consumers — `ConfigAccessLayer`, `LovelyIdentifier`, `LegacyRobotFamilies`, and the loader-side Items/Entities/Groups — read from the registry rather than maintaining their own per-entity knowledge.

**After this ADR, adding a new robot requires exactly two code changes:**

1. **`RobotVariant.java`** — one `public static final RobotVariant` constant, registered via `RobotVariant.register(id, "key")`
2. **`LegacyRobotDefinitions.java` or `RebootRobotDefinitions.java`** — one builder call (~8–12 lines)

No other Java file is modified for a new entity.

**On `RobotVariant`:** `RobotVariant` is implemented as a **final class with static constants**, not a Java enum. Built-in variants are declared as `public static final RobotVariant Bunny = register(1, "bunny")`. The `register(int id, String name)` method stores instances in internal `BY_ID` and `BY_NAME` maps, enabling runtime or addon-defined variants without code changes to lovelylib. NBT and network serialization use `getId()` / `byId(int)` — the same integer stability guarantee as an enum ordinal, but without the compile-time constraint.

See §3.0 for the full `RobotVariant` class design.


---

## 3. New Class Architecture

All new classes live in `lovelylib`. The architecture adds a `definition` sub-package to `lovelylib/common/entity/`.

```
net.heriazone.lovelylib.common.entity.definition/
  ├── RobotVariant.java            ← final class; static constants + runtime register()
  ├── ModTarget.java               ← which mod(s) an entity belongs to
  ├── EntityStats.java             ← consolidated stats value object
  ├── RobotEntityDefinition.java   ← descriptor for one robot entity
  └── RobotDefinitionRegistry.java ← central registry and query API

net.heriazone.lovelylib.source.legacy/
  └── LegacyRobotDefinitions.java  ← all Legacy entity declarations (one builder per entity)

net.heriazone.lovelylib.source.reboot/
  └── RebootRobotDefinitions.java  ← all Reboot entity declarations
```

### 3.0 `RobotVariant.java`

`RobotVariant` is migrated from a Java enum (currently in `common.entity.enums`) to a **final class with static constants** in the new `common.entity.definition` package. This change is transparent to all call sites — `RobotVariant.Bunny`, `RobotVariant.byId(int)`, `RobotVariant.getName()` all work identically — but removes the compile-time restriction on new variants. Any addon, datapack bridge, or third-party mod can call `RobotVariant.register(id, "key")` during initialization to introduce new variants that flow through the entire registry pipeline with zero changes to lovelylib.

The integer ID contract is preserved: each variant still carries a unique `int` ID used for NBT and network serialization. The difference is that IDs are now validated at registration time rather than fixed by enum ordinal position.

```java
package net.heriazone.lovelylib.common.entity.definition;

import java.util.*;

/**
 * Identity token for a robot variant.
 * <p>
 * <b>Design:</b> Final class with static constants rather than an enum.
 * Instances are singletons registered in BY_ID and BY_NAME maps. The static
 * constants for built-in variants register themselves at class-load time via
 * the register() factory method, maintaining the same usage pattern as an enum
 * (RobotVariant.Bunny, RobotVariant.Kitsune, etc.).
 * <p>
 * <b>Extensibility:</b> Addon mods call RobotVariant.register(id, "key") during
 * their initializer — before Lovely.onInitialize() seals RobotDefinitionRegistry —
 * to introduce new variants. No lovelylib source changes are needed.
 * <p>
 * <b>Serialization:</b> getId() / byId(int) provide the same integer stability
 * guarantee as an enum ordinal. NBT and network codecs use these methods.
 * Built-in IDs 0–7 are reserved; addon mods must use IDs ≥ 100 to avoid conflicts.
 */
public final class RobotVariant {

    // -- Built-in Variants (static constants, IDs 0–7 reserved) --

    public static final RobotVariant Vanilla = register(0, "vanilla");
    public static final RobotVariant Bunny   = register(1, "bunny");
    public static final RobotVariant Bunny2  = register(2, "bunny2");
    public static final RobotVariant Bunny3  = register(3, "bunny3");
    public static final RobotVariant Dragon  = register(4, "dragon");
    public static final RobotVariant Honey   = register(5, "honey");
    public static final RobotVariant Kitsune = register(6, "kitsune");
    public static final RobotVariant Neko    = register(7, "neko");

    // -- Internal Registries --

    private static final Map<Integer, RobotVariant> BY_ID   = new LinkedHashMap<>();
    private static final Map<String,  RobotVariant> BY_NAME = new LinkedHashMap<>();

    // -- Fields --

    private final int    m_id;
    private final String m_name;

    // -- Constructor --

    private RobotVariant(int id, String name) {
        this.m_id   = id;
        this.m_name = name;
    } // Constructor: RobotVariant()

    // -- Registration --

    /**
     * Registers a new variant and returns it.
     * <p>
     * Built-in variants call this from their static field initialisers.
     * Addon mods call this during their own initialiser, before
     * Lovely.onInitialize() seals RobotDefinitionRegistry.
     * <p>
     * <b>ID convention:</b> IDs 0–99 are reserved for lovelylib built-ins.
     * Addon mods must use IDs ≥ 100.
     *
     * @throws IllegalArgumentException if id or name is already registered
     */
    public static synchronized RobotVariant register(int id, String name) {
        if (BY_ID.containsKey(id))
            throw new IllegalArgumentException(
                "Duplicate RobotVariant id: " + id + " (already registered as '" + BY_ID.get(id).m_name + "')");
        if (BY_NAME.containsKey(name))
            throw new IllegalArgumentException(
                "Duplicate RobotVariant name: '" + name + "'");
        RobotVariant variant = new RobotVariant(id, name);
        BY_ID.put(id, variant);
        BY_NAME.put(name, variant);
        return variant;
    } // register()

    // -- Accessors --

    public int    getId()   { return m_id; }
    public String getName() { return m_name; }

    // -- Lookups --

    /**
     * Looks up a variant by integer ID.
     * Used by NBT/network deserialization.
     *
     * @throws NoSuchElementException if no variant is registered with this id
     */
    public static RobotVariant byId(int id) {
        RobotVariant v = BY_ID.get(id);
        if (v == null)
            throw new NoSuchElementException("Unknown RobotVariant id: " + id);
        return v;
    } // byId()

    /**
     * Looks up a variant by key string.
     * Used by config loading and command arguments.
     *
     * @throws NoSuchElementException if no variant is registered with this name
     */
    public static RobotVariant byName(String name) {
        RobotVariant v = BY_NAME.get(name);
        if (v == null)
            throw new NoSuchElementException("Unknown RobotVariant name: '" + name + "'");
        return v;
    } // byName()

    /** All registered variants in registration order. */
    public static Collection<RobotVariant> values() {
        return Collections.unmodifiableCollection(BY_ID.values());
    } // values()

    public static boolean isKnown(int id)     { return BY_ID.containsKey(id); }
    public static boolean isKnown(String name) { return BY_NAME.containsKey(name); }

    // -- Identity --

    /** Variants are singletons — reference equality is correct and sufficient. */
    @Override public boolean equals(Object o)  { return this == o; }
    @Override public int     hashCode()        { return m_id; }
    @Override public String  toString()        { return m_name + "(" + m_id + ")"; }

} // Class: RobotVariant
```

### 3.1 `ModTarget.java`

```java
package net.heriazone.lovelylib.common.entity.definition;

/**
 * Identifies which mod(s) a robot entity belongs to.
 * <p>
 * <b>Note:</b> TRIBUTE is defined for completeness but is never registered with
 * RobotDefinitionRegistry — Tribute has a fixed roster, uses a separate entity class
 * (TributeRobotEntity), and is never extended. Its config loop uses TRIBUTE_VARIANTS
 * from LovelyConstant directly and is unaffected by this ADR.
 */
public enum ModTarget {
    LEGACY,
    REBOOT,
    TRIBUTE
} // Enum: ModTarget
```

### 3.2 `EntityStats.java`

Replaces the seven scattered `static` fields per entity in `SharedConfigs.Common` (already marked deprecated in the codebase).

```java
package net.heriazone.lovelylib.common.entity.definition;

import net.heriazone.lovelylib.common.configs.SharedConfigs;

/**
 * Immutable value object carrying the seven stat defaults for one robot entity.
 * <p>
 * <b>Architecture:</b> Replaces the per-entity static fields previously required in
 * SharedConfigs.Common (e.g. BunnyMaxLevel, BunnyBaseHp, ...). Stats now travel with
 * the entity definition rather than living in a separate flat class.
 * <p>
 * <b>Note:</b> knockbackResistance is intentionally excluded — it is not a stored config
 * value. It is passed as a literal 0F directly to RobotFamily.withCombatStats() at the
 * call site. EntityConfigData has no knockbackResistance field.
 */
public final class EntityStats {

    // -- Fields --

    public final int   maxLevel;
    public final int   baseHp;
    public final int   baseAttack;
    public final float attackSpeed;
    public final int   baseDefense;
    public final float baseToughness;
    public final float movementSpeed;

    // -- Constructor --

    public EntityStats(int maxLevel, int baseHp, int baseAttack,
                       float attackSpeed, int baseDefense,
                       float baseToughness, float movementSpeed) {
        this.maxLevel      = maxLevel;
        this.baseHp        = baseHp;
        this.baseAttack    = baseAttack;
        this.attackSpeed   = attackSpeed;
        this.baseDefense   = baseDefense;
        this.baseToughness = baseToughness;
        this.movementSpeed = movementSpeed;
    } // Constructor: EntityStats()

    // -- Methods --

    /**
     * Converts to the config data shape that ConfigAccessLayer and RobotFamilies consume.
     * <p>
     * <b>Parameter count:</b> EntityConfigData takes exactly 7 parameters — the same seven
     * fields this class carries. knockbackResistance is not included here; callers pass
     * 0F directly when invoking RobotFamily.withCombatStats().
     */
    public SharedConfigs.EntityConfigData toEntityConfigData() {
        return new SharedConfigs.EntityConfigData(
            maxLevel, baseHp, baseAttack, attackSpeed,
            baseDefense, baseToughness, movementSpeed
        );
    } // toEntityConfigData()

} // Class: EntityStats
```

### 3.3 `RobotEntityDefinition.java`

The complete descriptor for one robot entity. Built with a fluent builder; immutable after construction.

**Corrections from analysis:**
- `getVariantKey()` delegates to `variant.getName()`. `RobotVariant` stores the canonical key string passed to `register(int id, String name)` and exposes it via `getName()`. Never re-derived from a class constant name to avoid breakage on multi-word keys (e.g. a key `"bunny_neo"` must not be derived by lowercasing `"BunnyNeo"`).
- The renderer field uses a `RendererFactory` functional interface instead of `Class<?>`. A raw class reference cannot be passed as a method reference to `event.registerEntityRenderer(type, MyRenderer::new)` without reflection. The factory pattern works directly with all three loaders.
- A `featureConfigurator` callback is added to handle per-variant feature additions (e.g. Kitsune's `BoneVisibilityFeature`) that cannot be expressed in a uniform reload loop.

```java
package net.heriazone.lovelylib.common.entity.definition;

import net.heriazone.lovelylib.common.entity.EntityTexture;
import net.heriazone.lovelylib.common.entity.RobotFamily;
import net.heriazone.lovelylib.common.entity.definition.RobotVariant;
import net.heriazone.lovelylib.common.configs.SharedConfigs;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Describes everything needed to register a robot entity across all layers.
 * <p>
 * <b>Architecture:</b> Single source of truth for one robot entity. All downstream
 * consumers — config defaults, family initialization, loader registration — derive
 * their data from this object rather than maintaining parallel per-entity state.
 * <p>
 * <b>Variant Key:</b> Derived from RobotVariant.getName(), which returns the key
 * string passed to RobotVariant.register(id, name) — e.g. RobotVariant.Bunny3 → "bunny3".
 * Never re-derived from the constant field name to avoid breakage on multi-word keys.
 * <p>
 * <b>Renderer:</b> Stored as a RendererFactory functional interface so it can be
 * passed directly as a method reference (MyRenderer::new) on all loaders without
 * reflection.
 * <p>
 * <b>Feature Configurator:</b> Optional BiConsumer applied after standard features
 * in reloadFromConfig(). Used for variant-specific feature additions (e.g. Kitsune's
 * BoneVisibilityFeature) that cannot be expressed in the uniform reload loop.
 */
public final class RobotEntityDefinition {

    // -- Types --

    /**
     * Factory for creating an entity renderer given the loader's renderer context.
     * Implemented as a method reference: e.g. KitsuneRenderer::new
     *
     * @param <T> the entity type this renderer handles
     */
    @FunctionalInterface
    public interface RendererFactory<T> {
        Object create(Object context);
    } // Interface: RendererFactory

    // -- Constants --

    /** Sentinel: empty list signals "use the full 16-color palette." */
    private static final List<EntityTexture> FULL_PALETTE = Collections.emptyList();

    // -- Fields --

    private final RobotVariant                                    variant;
    private final String                                          displayName;
    private final Set<ModTarget>                                  mods;
    private final List<EntityTexture>                             palette;
    private final EntityStats                                     stats;
    private final RendererFactory<?>                              rendererFactory;
    private final BiConsumer<RobotFamily, SharedConfigs.EntityConfigData> featureConfigurator;

    // -- Constructor --

    private RobotEntityDefinition(Builder b) {
        this.variant             = b.variant;
        this.displayName         = b.displayName;
        this.mods                = Collections.unmodifiableSet(b.mods);
        this.palette             = b.palette == null ? FULL_PALETTE
                                                     : Collections.unmodifiableList(b.palette);
        this.stats               = b.stats;
        this.rendererFactory     = b.rendererFactory;
        this.featureConfigurator = b.featureConfigurator;
    } // Constructor: RobotEntityDefinition()

    // -- Identity --

    public RobotVariant getVariant() { return variant; }

    /**
     * The canonical string key for this entity.
     * <p>
     * Delegates to RobotVariant.getName() — the key string passed to
     * RobotVariant.register(id, name).
     * e.g. RobotVariant.Bunny3 → "bunny3"
     * Replaces LovelyConstant.VARIANT_{KEY}.
     */
    public String getVariantKey() {
        return variant.getName();
    } // getVariantKey()

    /**
     * The spawn item registry name.
     * e.g. "bunny3_spawn"
     * Replaces LovelyConstant.{KEY}_SPAWN.
     */
    public String getSpawnItemKey() {
        return getVariantKey() + "_spawn";
    } // getSpawnItemKey()

    // -- Display --

    /** Human-readable name shown in wary/heal messages and UI. Drives the variant.lovelylib.{key} lang key. */
    public String getDisplayName() { return displayName; }

    // -- Mod Membership --

    public Set<ModTarget> getMods() { return mods; }

    public boolean isForMod(ModTarget mod) { return mods.contains(mod); }

    // -- Palette --

    /** Returns true if this entity uses all 16 colors. */
    public boolean isFullPalette() { return palette.isEmpty(); }

    /** Active colors for a restricted palette. Returns empty list when isFullPalette() is true. */
    public List<EntityTexture> getPalette() { return palette; }

    // -- Stats --

    public EntityStats getStats() { return stats; }

    // -- Renderer --

    /** Returns null if NativeRobotRenderer should be used. */
    public RendererFactory<?> getRendererFactory() { return rendererFactory; }

    public boolean usesNativeRenderer() { return rendererFactory == null; }

    // -- Feature Configurator --

    /**
     * Applies any variant-specific features to the family after the standard reload loop.
     * <p>
     * <b>Design:</b> Most variants need only the standard feature set (LevelFeature,
     * CombatLevelFeature, EnchantmentFeature, ProtectionFeature). Variants that need
     * additional features — e.g. Kitsune's BoneVisibilityFeature — provide a BiConsumer
     * here. The reload loop calls this after applying standard features; it is a no-op
     * for variants where no configurator was set.
     */
    public void applyFeatureConfigurator(RobotFamily family,
                                         SharedConfigs.EntityConfigData cfg) {
        if (featureConfigurator != null) featureConfigurator.accept(family, cfg);
    } // applyFeatureConfigurator()

    public boolean hasFeatureConfigurator() { return featureConfigurator != null; }

    // -- Builder --

    public static final class Builder {

        private final RobotVariant variant;
        private String displayName;
        private final Set<ModTarget> mods = EnumSet.noneOf(ModTarget.class);        private List<EntityTexture> palette = null;
        private EntityStats stats;
        private RendererFactory<?> rendererFactory = null;
        private BiConsumer<RobotFamily, SharedConfigs.EntityConfigData> featureConfigurator = null;

        public Builder(RobotVariant variant) {
            this.variant = variant;
        }

        public Builder displayName(String name) {
            this.displayName = name;
            return this;
        }

        public Builder forMod(ModTarget mod) {
            this.mods.add(mod);
            return this;
        }

        public Builder forMods(ModTarget... targets) {
            this.mods.addAll(Arrays.asList(targets));
            return this;
        }

        /** Omit call entirely to use all 16 colors. Pass specific textures to restrict palette. */
        public Builder palette(EntityTexture... colors) {
            this.palette = colors.length == 0 ? null : Arrays.asList(colors);
            return this;
        }

        public Builder stats(int maxLevel, int baseHp, int baseAttack,
                             float attackSpeed, int baseDefense,
                             float baseToughness, float movementSpeed) {
            this.stats = new EntityStats(maxLevel, baseHp, baseAttack,
                                         attackSpeed, baseDefense, baseToughness, movementSpeed);
            return this;
        }

        public Builder stats(EntityStats stats) {
            this.stats = stats;
            return this;
        }

        /**
         * Specify a custom renderer factory. Omit this call to use NativeRobotRenderer.
         * <p>
         * Pass a constructor reference: .renderer(KitsuneRenderer::new)
         * The factory is invoked by the loader's renderer registration event with the
         * appropriate context object. Currently 2 of 8 variants use custom renderers
         * (BunnyRenderer, KitsuneRenderer).
         */
        public Builder renderer(RendererFactory<?> factory) {
            this.rendererFactory = factory;
            return this;
        }

        /**
         * Specify per-variant feature additions applied after the standard reload loop.
         * <p>
         * Use for features that cannot be expressed uniformly across all variants —
         * e.g. Kitsune's BoneVisibilityFeature, or any future variant-specific feature.
         * The BiConsumer receives the family and the loaded config data.
         */
        public Builder featureConfigurator(
                BiConsumer<RobotFamily, SharedConfigs.EntityConfigData> configurator) {
            this.featureConfigurator = configurator;
            return this;
        }

        public RobotEntityDefinition build() {
            if (displayName == null)
                throw new IllegalStateException("displayName is required for " + variant);
            if (mods.isEmpty())
                throw new IllegalStateException("at least one ModTarget is required for " + variant);
            if (stats == null)
                throw new IllegalStateException("stats are required for " + variant);
            return new RobotEntityDefinition(this);
        } // build()

    } // Class: Builder

} // Class: RobotEntityDefinition
```

### 3.4 `RobotDefinitionRegistry.java`

The central registry. Replaces the three switch statements in `ConfigAccessLayer`, `LovelyIdentifier`, and the per-entity `Default.put()` entries in `*Configs`.

**Note on `Lovely.onInitialize()`:** The initialization entry point described in §6 does not currently exist in `Lovely.java` — only a stub `Lovely.initialize()` is present with TODO comments. Creating a proper `Lovely.onInitialize()` is a Phase 1 build task, not a refactoring of existing infrastructure. See §6 and §12 for details.

```java
package net.heriazone.lovelylib.common.entity.definition;

import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.entity.definition.RobotVariant;

import java.util.*;

/**
 * Central registry for all robot entity definitions.
 * <p>
 * <b>Architecture:</b> Single source of truth for all variant metadata. Replaces:
 * <ul>
 *   <li>LovelyConstant per-entity constants and variant arrays</li>
 *   <li>SharedConfigs per-entity static fields</li>
 *   <li>ConfigAccessLayer.getDefaultEntityConfig() switch statement</li>
 *   <li>LovelyIdentifier.getTranslation() switch statement</li>
 *   <li>LegacyConfigs / RebootConfigs Default.put() static entries</li>
 * </ul>
 * <p>
 * <b>Initialization Order:</b> Definitions must be registered via
 * LegacyRobotDefinitions.register() and RebootRobotDefinitions.register() before
 * any consumer reads from this registry. The entry point is Lovely.onInitialize(),
 * which must be called at the very start of every loader entry point.
 * <p>
 * <b>Fail-Fast:</b> seal() is called after all definitions are registered.
 * Any late registration or access to an unregistered variant throws immediately
 * with a clear message rather than silently returning a wrong value.
 */
public final class RobotDefinitionRegistry {

    // -- Constructor --

    private RobotDefinitionRegistry() {}

    // -- Fields --

    /** Insertion-ordered for deterministic iteration. */
    private static final LinkedHashMap<RobotVariant, RobotEntityDefinition> registry
        = new LinkedHashMap<>();

    private static boolean sealed = false;

    // -- Registration --

    /**
     * Register a definition. Must be called before seal() is invoked.
     *
     * @throws IllegalStateException if the registry is already sealed or the variant is duplicate
     */
    public static void register(RobotEntityDefinition def) {
        if (sealed)
            throw new IllegalStateException(
                "RobotDefinitionRegistry is sealed — cannot register " + def.getVariant()
                + " after initialization is complete.");
        if (registry.containsKey(def.getVariant()))
            throw new IllegalStateException(
                "Duplicate registration for variant: " + def.getVariant());
        registry.put(def.getVariant(), def);
    } // register()

    /**
     * Seals the registry after all definitions have been registered.
     * Prevents accidental late registration from any loader or third-party code.
     */
    public static void seal() {
        sealed = true;
    } // seal()

    // -- Query API --

    /**
     * Returns the definition for a variant.
     *
     * @throws NoSuchElementException if the variant was not registered before seal()
     */
    public static RobotEntityDefinition get(RobotVariant variant) {
        RobotEntityDefinition def = registry.get(variant);
        if (def == null)
            throw new NoSuchElementException(
                "No RobotEntityDefinition registered for: " + variant
                + ". Did you call register() before seal()?");
        return def;
    } // get()

    /** All definitions registered for a given mod, in registration order. */
    public static Collection<RobotEntityDefinition> getForMod(ModTarget mod) {
        List<RobotEntityDefinition> result = new ArrayList<>();
        for (RobotEntityDefinition def : registry.values()) {
            if (def.isForMod(mod)) result.add(def);
        }
        return Collections.unmodifiableList(result);
    } // getForMod()

    /** All registered definitions, in registration order. */
    public static Collection<RobotEntityDefinition> getAll() {
        return Collections.unmodifiableCollection(registry.values());
    } // getAll()

    /**
     * All variant key strings for a given mod — replaces LovelyConstant.LEGACY_VARIANTS,
     * REBOOT_VARIANTS, and ALL_VARIANTS. Also replaces the LEGACY_VARIANTS reference in
     * the Fabric-side LegacyConfigs.buildConfigProvider() and loadDynamicEntityConfigs() loops.
     */
    public static String[] getVariantKeysForMod(ModTarget mod) {
        return getForMod(mod).stream()
            .map(RobotEntityDefinition::getVariantKey)
            .toArray(String[]::new);
    } // getVariantKeysForMod()

    // -- Derived lookups (replace removed switch statements) --

    /**
     * Returns the EntityConfigData defaults for a variant.
     * Replaces ConfigAccessLayer.getDefaultEntityConfig() switch.
     */
    public static SharedConfigs.EntityConfigData getDefaultConfig(RobotVariant variant) {
        return get(variant).getStats().toEntityConfigData();
    } // getDefaultConfig()

    /**
     * Returns the variant key string for a variant.
     * Delegates to RobotEntityDefinition.getVariantKey() which uses RobotVariant.getName().
     */
    public static String getVariantKey(RobotVariant variant) {
        return get(variant).getVariantKey();
    } // getVariantKey()

    /**
     * Returns the lovelylib lang key for a variant's display name.
     * e.g. "variant.lovelylib.bunny3"
     * Used by LovelyIdentifier.getVariantTranslation().
     */
    public static String getDisplayTranslationKey(RobotVariant variant) {
        return "variant.lovelylib." + get(variant).getVariantKey();
    } // getDisplayTranslationKey()

    public static boolean isRegistered(RobotVariant variant) {
        return registry.containsKey(variant);
    } // isRegistered()

} // Class: RobotDefinitionRegistry
```

### 3.5 `LegacyRobotDefinitions.java`

Single declaration file for all Legacy entities. Replaces per-entity additions to `LovelyConstant`, `SharedConfigs`, `LegacyConfigs`, and `LegacyRobotFamilies`.

**On shared entities:** Both `LegacyRobotFamilies` and `RebootRobotFamilies` currently contain the full set of all 8 variants independently — their `reloadFromConfig()` methods are line-for-line identical except for the config source. The `forMods(LEGACY, REBOOT)` pattern consolidates this: a variant declared once with both targets is served to both mods from the same definition. There is no need to declare it twice.

```java
package net.heriazone.lovelylib.source.legacy;

import net.heriazone.hzlib.api.entity.features.BoneVisibilityFeature;
import net.heriazone.lovelylib.common.entity.EntityTexture;
import net.heriazone.lovelylib.common.entity.definition.ModTarget;
import net.heriazone.lovelylib.common.entity.definition.RobotDefinitionRegistry;
import net.heriazone.lovelylib.common.entity.definition.RobotEntityDefinition;
import net.heriazone.lovelylib.common.entity.definition.RobotVariant;
import net.heriazone.lovelylib.common.entity.features.BoneVisibilityConditions;

/**
 * Declares all Legacy (and Legacy+Reboot shared) robot entity definitions.
 * <p>
 * <b>To add a new Legacy entity:</b> add one builder call to register() below.
 * No other file in lovelylib requires modification.
 * <p>
 * <b>Called once</b> by Lovely.onInitialize() before any entity or config registration.
 * <p>
 * <b>Stat values:</b> Must match the values currently in LegacyConfigs.Default during
 * migration. After Phase 2 is complete, this builder is the canonical source and
 * LegacyConfigs derives from it.
 */
public final class LegacyRobotDefinitions {

    // -- Constructor --

    private LegacyRobotDefinitions() {}

    // -- Registration --

    public static void register() {

        // ── Vanilla ───────────────────────────────────────────────────────────
        RobotDefinitionRegistry.register(new RobotEntityDefinition.Builder(RobotVariant.Vanilla)
            .displayName("Vanilla")
            .forMod(ModTarget.LEGACY)
            .stats(200, 25, 5, 1.3f, 5, 0f, 0.32f)
            .build());

        // ── Bunny ─────────────────────────────────────────────────────────────
        RobotDefinitionRegistry.register(new RobotEntityDefinition.Builder(RobotVariant.Bunny)
            .displayName("Bunny")
            .forMod(ModTarget.LEGACY)
            .stats(200, 26, 5, 1.7f, 4, 0f, 0.37f)
            .build());

        // ── Bunny 2 ───────────────────────────────────────────────────────────
        RobotDefinitionRegistry.register(new RobotEntityDefinition.Builder(RobotVariant.Bunny2)
            .displayName("Bunny 2.0")
            .forMod(ModTarget.LEGACY)
            .stats(200, 27, 6, 1.8f, 5, 0f, 0.36f)
            .build());

        // ── Bunny 3 — restricted 5-color pastel palette, shared with Reboot ──
        RobotDefinitionRegistry.register(new RobotEntityDefinition.Builder(RobotVariant.Bunny3)
            .displayName("Bunny 3.0")
            .forMods(ModTarget.LEGACY, ModTarget.REBOOT)
            .palette(
                EntityTexture.LIGHT_BLUE,
                EntityTexture.YELLOW,
                EntityTexture.LIME,
                EntityTexture.PINK,
                EntityTexture.PURPLE)
            .stats(200, 28, 7, 1.9f, 6, 0f, 0.35f)
            .build());

        // ── Dragon ────────────────────────────────────────────────────────────
        RobotDefinitionRegistry.register(new RobotEntityDefinition.Builder(RobotVariant.Dragon)
            .displayName("Dragon")
            .forMod(ModTarget.LEGACY)
            .stats(200, 30, 8, 1.0f, 7, 0f, 0.3f)
            .build());

        // ── Honey ─────────────────────────────────────────────────────────────
        RobotDefinitionRegistry.register(new RobotEntityDefinition.Builder(RobotVariant.Honey)
            .displayName("Honey")
            .forMod(ModTarget.LEGACY)
            .stats(200, 29, 4, 1.1f, 5, 0f, 0.31f)
            .build());

        // ── Kitsune — custom renderer + bone visibility feature ───────────────
        RobotDefinitionRegistry.register(new RobotEntityDefinition.Builder(RobotVariant.Kitsune)
            .displayName("Kitsune")
            .forMod(ModTarget.LEGACY)
            .stats(200, 28, 2, 1.3f, 6, 0f, 0.33f)
            // tail0 is always visible; multi-tail level-unlock is disabled until
            // tail assets are finalised. tail01–tail09 are permanently hidden.
            .featureConfigurator((family, cfg) ->
                family.withFeature(BoneVisibilityFeature.class,
                    BoneVisibilityFeature.builder()
                        .showWhen("tail0",  entity -> true)
                        .hideWhen("tail01", entity -> true)
                        .hideWhen("tail02", entity -> true)
                        .hideWhen("tail03", entity -> true)
                        .hideWhen("tail04", entity -> true)
                        .hideWhen("tail05", entity -> true)
                        .hideWhen("tail06", entity -> true)
                        .hideWhen("tail07", entity -> true)
                        .hideWhen("tail08", entity -> true)
                        .hideWhen("tail09", entity -> true)
                        .build()))
            .build());

        // ── Neko ──────────────────────────────────────────────────────────────
        RobotDefinitionRegistry.register(new RobotEntityDefinition.Builder(RobotVariant.Neko)
            .displayName("Neko")
            .forMod(ModTarget.LEGACY)
            .stats(200, 28, 7, 1.4f, 5, 0f, 0.34f)
            .build());

        // ─────────────────────────────────────────────────────────────────────
        // ADD NEW LEGACY ENTITIES HERE.
        // Full palette (16 colors): omit .palette(...) entirely.
        // Custom renderer:          add .renderer(MyRenderer::new)
        // Extra features at reload: add .featureConfigurator((family, cfg) -> ...)
        // Shared with Reboot:       use .forMods(ModTarget.LEGACY, ModTarget.REBOOT)
        // ─────────────────────────────────────────────────────────────────────

    } // register()

} // Class: LegacyRobotDefinitions
```

`RebootRobotDefinitions.java` follows the same structure. Variants already declared in `LegacyRobotDefinitions` with `.forMods(LEGACY, REBOOT)` must **not** be re-declared there — they are already in the registry. `RebootRobotDefinitions` only declares variants that are exclusive to Reboot.


---

## 4. Simplified Existing Classes

These files are refactored to read from `RobotDefinitionRegistry` instead of maintaining per-entity state. After migration, no further per-entity modifications are ever needed in any of them.

### 4.1 `LovelyConstant.java` — no more per-entity constants

Three categories of per-entity additions are removed:

- `{KEY}_SPAWN` constants → replaced by `RobotDefinitionRegistry.get(variant).getSpawnItemKey()`
- `VARIANT_{KEY}` constants → replaced by `RobotDefinitionRegistry.get(variant).getVariantKey()` (which delegates to `variant.getName()`)
- `LEGACY_VARIANTS`, `REBOOT_VARIANTS`, `ALL_VARIANTS` arrays → replaced by `RobotDefinitionRegistry.getVariantKeysForMod(ModTarget)`

**Note on `LEGACY_VARIANTS` consumers:** This array is used in more places than just the config switch. The Fabric-side `LegacyConfigs.buildConfigProvider()` and `loadDynamicEntityConfigs()` both loop over it to generate and load the `.properties` config file. Both loops must be updated to use `RobotDefinitionRegistry.getVariantKeysForMod(ModTarget.LEGACY)` as part of Phase 2. The same applies to `REBOOT_VARIANTS` in the Fabric-side `RebootConfigs`.

`TRIBUTE_VARIANTS` is intentionally not removed — Tribute's config loop continues to use it directly since Tribute is excluded from `RobotDefinitionRegistry`.

`LovelyConstant` retains only truly constant values that do not vary per entity: item tags, stat config key names (`CONFIG_MAX_LEVEL`, `CONFIG_BASE_HP`, etc.), and any other shared string constants.

```java
// BEFORE — three additions per entity:
public static final String BUNNY3_SPAWN    = "bunny3_spawn";
public static final String VARIANT_BUNNY3  = "bunny3";
public static final String[] LEGACY_VARIANTS = { ..., VARIANT_BUNNY3 };

// AFTER — nothing in LovelyConstant. Resolved at runtime from the registry:
// RobotDefinitionRegistry.get(variant).getSpawnItemKey()       → "bunny3_spawn"
// RobotDefinitionRegistry.get(variant).getVariantKey()         → "bunny3"
// RobotDefinitionRegistry.getVariantKeysForMod(ModTarget.LEGACY) → replaces LEGACY_VARIANTS
```

### 4.2 `SharedConfigs.java` — no more per-entity static fields

The seven static fields per entity in `SharedConfigs.Common` (already marked `// LEGACY INDIVIDUAL ENTITY CONFIGS (DEPRECATED)` in the codebase) are removed entirely. The data now lives in `EntityStats` inside each `RobotEntityDefinition`.

```java
// BEFORE — 7 fields per entity, already deprecated:
public static int   Bunny3MaxLevel      = 200;
public static int   Bunny3BaseHp        = 28;
public static int   Bunny3BaseAttack    = 7;
public static float Bunny3AttackSpeed   = 1.9F;
public static int   Bunny3BaseDefense   = 6;
public static float Bunny3BaseToughness = 0F;
public static float Bunny3MovementSpeed = 0.35F;

// AFTER — nothing. Stats are accessed via:
// RobotDefinitionRegistry.get(variant).getStats().baseHp       (etc.)
// RobotDefinitionRegistry.getDefaultConfig(variant)            (full EntityConfigData)
```

### 4.3 `ConfigAccessLayer.java` — switch replaced by registry lookup

The private `getDefaultEntityConfig(String variantKey)` switch is the only per-entity code in this file. It is replaced by a registry loop. The three-phase fallback chain (serialized → individual keys → defaults) is preserved; only the final fallback changes.

```java
// BEFORE — one case per entity, silent wrong default if forgotten:
private static SharedConfigs.EntityConfigData getDefaultEntityConfig(String variantKey) {
    return switch (variantKey) {
        case LovelyConstant.VARIANT_VANILLA -> new SharedConfigs.EntityConfigData(
            SharedConfigs.Common.VanillaMaxLevel, SharedConfigs.Common.VanillaBaseHp, ...);
        case LovelyConstant.VARIANT_BUNNY3  -> new SharedConfigs.EntityConfigData(
            SharedConfigs.Common.Bunny3MaxLevel, SharedConfigs.Common.Bunny3BaseHp, ...);
        // ... one case per variant ...
        default -> SharedConfigs.EntityConfigData.getDefault(); // ← silent wrong value
    };
}

// AFTER — registry-driven, no per-entity code:
private static SharedConfigs.EntityConfigData getDefaultEntityConfig(String variantKey) {
    for (RobotEntityDefinition def : RobotDefinitionRegistry.getAll()) {
        if (def.getVariantKey().equals(variantKey)) {
            return def.getStats().toEntityConfigData();
        }
    }
    // Only reached for genuinely unknown variants — not a missing registration,
    // which would have caused a fail-fast exception at seal() time.
    return SharedConfigs.EntityConfigData.getDefault();
} // getDefaultEntityConfig()
```

Bug 2 ("zero stats on Forge/NeoForge") is structurally eliminated — there is no switch to forget a case in, and missing registrations fail loudly at startup.

### 4.4 `LovelyIdentifier.java` — switch replaced by registry lookup

```java
// BEFORE — one case per entity, displays "Vanilla" for any unmatched variant:
public static String getTranslation(RobotVariant variant) {
    return switch (variant) {
        case Vanilla -> getVariantTranslation(LovelyConstant.VARIANT_VANILLA);
        case Bunny   -> getVariantTranslation(LovelyConstant.VARIANT_BUNNY);
        // ... one case per variant ...
        default      -> getVariantTranslation(LovelyConstant.VARIANT_VANILLA); // ← Bug 1
    };
}

// AFTER — registry-driven:
public static String getTranslation(RobotVariant variant) {
    if (RobotDefinitionRegistry.isRegistered(variant)) {
        return getVariantTranslation(RobotDefinitionRegistry.getVariantKey(variant));
    }
    // Only reached for a RobotVariant enum value that was never registered —
    // a programmer error that seal() would have already caught at startup.
    return getVariantTranslation(RobotVariant.Vanilla.getName());
} // getTranslation()
```

Bug 1 ("Vanilla" in overhead messages) is structurally eliminated.

### 4.5 `LegacyRobotFamilies.java` — no more per-entity fields or `reloadFromConfig()` blocks

The eight named static fields and eight matching blocks in `reloadFromConfig()` are replaced by a map populated from the registry.

**On the `reloadFromConfig()` loop:** The loop applies the standard feature set uniformly across all variants, then calls `def.applyFeatureConfigurator()` to apply any variant-specific extras. This keeps Kitsune's `BoneVisibilityFeature` (and any future per-variant additions) inside the definition rather than scattered in the loop body.

**Access pattern change:** All callers of `LegacyRobotFamilies.BUNNY3` migrate to `LegacyRobotFamilies.get(RobotVariant.Bunny3)`. This is a one-time migration of call sites.

```java
// BEFORE — one static field per entity:
public static final RobotFamily BUNNY  = create(RobotVariant.Bunny);
public static final RobotFamily BUNNY3 = create(RobotVariant.Bunny3, BUNNY3_COLORS);
// ... six more ...

// BEFORE — one block per entity in reloadFromConfig():
SharedConfigs.EntityConfigData bunny3 = LegacyConfigs.getEntityConfig(LovelyConstant.VARIANT_BUNNY3);
BUNNY3.withCombatStats(bunny3.baseHp, bunny3.baseAttack, bunny3.attackSpeed,
                       bunny3.baseDefense, bunny3.baseToughness, 0F, bunny3.movementSpeed)
      .withFeature(LevelFeature.class, new LevelFeature(bunny3.maxLevel, defaultExpStrategy))
      // ... repeated for every variant ...

// AFTER — map populated from registry, uniform reload loop:
private static final Map<RobotVariant, RobotFamily> families = new EnumMap<>(RobotVariant.class);

public static void initialize() {
    for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
        RobotFamily family = def.isFullPalette()
            ? create(def.getVariant())
            : create(def.getVariant(), def.getPalette());
        families.put(def.getVariant(), family);
    }
} // initialize()

public static RobotFamily get(RobotVariant variant) {
    return Objects.requireNonNull(families.get(variant),
        "No Legacy RobotFamily for: " + variant
        + ". Was LegacyRobotFamilies.initialize() called?");
} // get()

public static void reloadFromConfig() {
    var defaultExpStrategy = new LevelFeature.FormulaExpStrategy(
        level -> SharedConfigs.Common.ExperienceBase + level * SharedConfigs.Common.ExperienceMultiplier);
    var defaultProtection  = new ProtectionFeature(new LevelBasedProtectionStrategy())
        .withMax(SharedConfigs.Common.ProtectionLimitFire,
                 SharedConfigs.Common.ProtectionLimitFall,
                 SharedConfigs.Common.ProtectionLimitBlast,
                 SharedConfigs.Common.ProtectionLimitProjectile);
    var defaultEnchantment = new EnchantmentFeature(new DefaultEnchantmentStrategy())
        .withLooting(SharedConfigs.Common.LootEnchantment,
                     SharedConfigs.Common.MaxLootEnchantment,
                     SharedConfigs.Common.LootEnchantmentLevel);

    for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
        SharedConfigs.EntityConfigData cfg = LegacyConfigs.getEntityConfig(def.getVariantKey());
        get(def.getVariant())
            .withCombatStats(cfg.baseHp, cfg.baseAttack, cfg.attackSpeed,
                             cfg.baseDefense, cfg.baseToughness, 0F, cfg.movementSpeed)
            .withFeature(LevelFeature.class,
                new LevelFeature(cfg.maxLevel, defaultExpStrategy))
            .withFeature(CombatLevelFeature.class,
                new CombatLevelFeature(cfg.baseHp, cfg.baseAttack,
                                       cfg.baseDefense, new LinearAttributeStrategy()))
            .withFeature(EnchantmentFeature.class, defaultEnchantment)
            .withFeature(ProtectionFeature.class, defaultProtection);

        // Apply any variant-specific extras (e.g. Kitsune's BoneVisibilityFeature).
        // No-op for variants without a featureConfigurator.
        def.applyFeatureConfigurator(get(def.getVariant()), cfg);
    }
} // reloadFromConfig()
```

`RebootRobotFamilies.java` follows the identical pattern substituting `ModTarget.REBOOT` and `RebootConfigs`.

### 4.6 `LegacyConfigs.java` (lovelylib-side) — `static {}` block becomes registry-driven

The lovelylib-side `LegacyConfigs.Default` static initializer is replaced by a registry loop. The mod-side Fabric `LegacyConfigs` config file loops (`buildConfigProvider()`, `loadDynamicEntityConfigs()`) replace their `LovelyConstant.LEGACY_VARIANTS` references with `RobotDefinitionRegistry.getVariantKeysForMod(ModTarget.LEGACY)`.

```java
// BEFORE — one Default.put() per entity in the static initializer:
static {
    Default.put(LovelyConstant.VARIANT_BUNNY3, new SharedConfigs.EntityConfigData(
        200, 28, 7, 1.9F, 6, 0F, 0.35F));
    // ... repeated for every variant ...
}

// AFTER — populated from the registry:
static {
    for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
        Default.put(def.getVariantKey(), def.getStats().toEntityConfigData());
    }
}
```

`RebootConfigs.java` follows the identical pattern with `ModTarget.REBOOT`.


---

## 5. Loader-Side Consolidation

The loader-side classes (`{Mod}Items`, `{Mod}Entities`, `{Mod}Groups`) are simplified by replacing explicit per-entity fields with maps keyed by `RobotVariant`, populated in a single initialization loop.

**Loader type divergence:** Forge and NeoForge use `DeferredRegister` with `RegistryObject<T>` wrappers whose suppliers are resolved after the `RegisterEvent` fires. Fabric uses immediate `Registry.register()` during `onInitialize()` and returns unwrapped `Item` / `EntityType<T>` directly. The map patterns below show Forge. Fabric follows the same structure but stores unwrapped types. Each loader's implementation must use its own map generic type — there is no common wrapper across all three.

### 5.1 Access Pattern Migration

| Before (Forge/NeoForge) | After (Forge/NeoForge) |
|-------------------------|------------------------|
| `LegacyItems.BUNNY3_SPAWN` | `LegacyItems.getSpawnItem(RobotVariant.Bunny3)` |
| `LegacyItems.BUNNY3_SPAWN.get()` | `LegacyItems.getSpawnItem(RobotVariant.Bunny3).get()` |
| `LegacyEntities.BUNNY3` | `LegacyEntities.getEntityType(RobotVariant.Bunny3)` |
| `LegacyEntities.BUNNY3.get()` | `LegacyEntities.getEntityType(RobotVariant.Bunny3).get()` |

| Before (Fabric) | After (Fabric) |
|-----------------|----------------|
| `LegacyItems.BUNNY3_SPAWN` | `LegacyItems.getSpawnItem(RobotVariant.Bunny3)` |
| `LegacyEntities.BUNNY3` | `LegacyEntities.getEntityType(RobotVariant.Bunny3)` |

This is a one-time migration of call sites across llovelyr and rlovelyr. After migration, no new entity requires any change in these files.

### 5.2 `{Mod}Items.java` Pattern

**Forge** — map stores `RegistryObject<Item>`:

```java
// BEFORE — one field + one registerModel() call per entity (Forge):
public static final RegistryObject<Item> BUNNY3_SPAWN =
    registerItem(LovelyConstant.BUNNY3_SPAWN, LegacyEntities.BUNNY3::get, Rarity.RARE, 1);

// Inside registerModels():
registerModel(LegacyItems.BUNNY3_SPAWN.get(), ITEM_TAG_VARIANT, STAT_COLOR);

// AFTER — map populated by iterating the registry:
private static final Map<RobotVariant, RegistryObject<Item>> spawnItems =
    new EnumMap<>(RobotVariant.class);

public static void registerAll() {
    for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
        RobotVariant variant = def.getVariant();
        spawnItems.put(variant,
            registerItem(def.getSpawnItemKey(),
                         () -> LegacyEntities.getEntityType(variant).get(),
                         Rarity.RARE, 1));
    }
} // registerAll()

public static RegistryObject<Item> getSpawnItem(RobotVariant variant) {
    return Objects.requireNonNull(spawnItems.get(variant),
        "No spawn item registered for: " + variant);
} // getSpawnItem()

public static void registerModels() {
    for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
        registerModel(getSpawnItem(def.getVariant()).get(), ITEM_TAG_VARIANT, STAT_COLOR);
    }
} // registerModels()
```

**Fabric** — map stores unwrapped `Item` (no `RegistryObject` wrapper):

```java
private static final Map<RobotVariant, Item> spawnItems = new EnumMap<>(RobotVariant.class);

public static void registerAll() {
    for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
        RobotVariant variant = def.getVariant();
        Item item = registerItem(def.getSpawnItemKey(),
                                 LegacyEntities.getEntityType(variant), Rarity.RARE, 1);
        spawnItems.put(variant, item);
    }
} // registerAll()

public static Item getSpawnItem(RobotVariant variant) {
    return Objects.requireNonNull(spawnItems.get(variant),
        "No spawn item registered for: " + variant);
} // getSpawnItem()
```

NeoForge follows the same Forge pattern (`DeferredRegister` / `RegistryObject`).

### 5.3 `{Mod}Entities.java` Pattern

**Forge** — renderer registration uses `RendererFactory` from the definition:

```java
// BEFORE — one field per entity, three separate event callbacks per entity (Forge):
public static final RegistryObject<EntityType<NativeRobotEntity>> BUNNY3 =
    registerRobot(LovelyConstant.VARIANT_BUNNY3, LegacyRobotFamilies.BUNNY3);

// AFTER — map pattern:
private static final Map<RobotVariant, RegistryObject<EntityType<NativeRobotEntity>>> entityTypes =
    new EnumMap<>(RobotVariant.class);

public static void registerAll() {
    for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
        entityTypes.put(def.getVariant(),
            registerRobot(def.getVariantKey(), LegacyRobotFamilies.get(def.getVariant())));
    }
} // registerAll()

public static RegistryObject<EntityType<NativeRobotEntity>> getEntityType(RobotVariant variant) {
    return Objects.requireNonNull(entityTypes.get(variant),
        "No entity type registered for: " + variant);
} // getEntityType()

// Attribute registration — iterates registry:
public static void registerAttributes(EntityAttributeCreationEvent event) {
    for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
        event.put(getEntityType(def.getVariant()).get(),
                  NativeEntityFamily.createAttributes(LegacyRobotFamilies.get(def.getVariant())));
    }
} // registerAttributes()

// Renderer registration — uses RendererFactory from definition (no reflection):
@SuppressWarnings("unchecked")
public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
        EntityType<NativeRobotEntity> type = getEntityType(def.getVariant()).get();
        if (def.usesNativeRenderer()) {
            event.registerEntityRenderer(type, NativeRobotRenderer::new);
        } else {
            // RendererFactory.create() accepts the EntityRendererProvider.Context
            // and returns the renderer — matches the signature registerEntityRenderer expects.
            event.registerEntityRenderer(type,
                ctx -> (EntityRenderer<NativeRobotEntity>) def.getRendererFactory().create(ctx));
        }
    }
} // registerRenderers()

// Feature registration — iterates registry:
public static void registerFeatures() {
    for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
        LegacyRobotFamilies.get(def.getVariant())
            .withFeature(PickupFeature.class,
                new PickupFeature(LegacyItems.getSpawnItem(def.getVariant()).get()))
            .withFeature(DropFeature.class,
                new DropFeature(LegacyItems.ROBOT_CORE.get()));
    }
} // registerFeatures()
```

**Fabric** — map stores unwrapped `EntityType<NativeRobotEntity>`. The renderer registration uses `EntityRendererRegistry.register(type, ctx -> ...)` with the same `RendererFactory` pattern.

### 5.4 `{Mod}Groups.java` Pattern

No per-entity code at all after migration — both event callbacks iterate the registry:

```java
// Forge / NeoForge:
private void buildDefaultTab(CreativeModeTab.Output output) {
    for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
        output.accept(LegacyItems.getSpawnItem(def.getVariant()).get());
    }
} // buildDefaultTab()

private void addSpawnEggs(BuildCreativeModeTabContentsEvent event) {
    for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
        event.accept(LegacyItems.getSpawnItem(def.getVariant()));
    }
} // addSpawnEggs()

// Fabric — same loops, calling ItemGroupEvents or creative tab builder API directly.
```


---

## 6. Initialization Ordering

The registry must be fully populated and sealed before any consumer reads from it. This section describes the required sequence and the work needed to establish it.

### 6.1 The Gap — `Lovely.onInitialize()` Does Not Exist

The initialization entry point this ADR depends on does not currently exist. `Lovely.java` contains only a stub `Lovely.initialize()` that sets an `initialized` flag and logs a message, with TODO comments for "Initialize robot entity registry" and "Register robot types."

**Creating `Lovely.onInitialize()` is a Phase 1 build task, not a refactoring of existing code.** The existing loader entry points (`LovelyLegacy.onInitialize()` on Fabric, `LovelyLegacy` constructor on Forge/NeoForge) call `Legacy.init()` → `LegacyConfigs.register()` → `LegacyRobotFamilies.reloadFromConfig()`. The new sequence must be inserted before this chain.

### 6.2 Static Initialization Ordering Risk on Forge/NeoForge

In the current code, `LegacyRobotFamilies` static fields (`public static final RobotFamily BUNNY = create(...)`) initialize at class-load time — whenever the class is first referenced. Under the new design these become map entries populated in `registerAll()`. If any code references `LegacyEntities.getEntityType(variant)` or `LegacyItems.getSpawnItem(variant)` before `registerAll()` runs, the map lookup returns null and throws.

On Forge/NeoForge this is managed by wiring `registerAll()` to the appropriate registration event (the `DeferredRegister` handles deferred resolution automatically). The lovelylib-side `LegacyRobotFamilies.initialize()` and the registry definitions must complete before the loader fires registration events. The mod constructor is the correct place to call `Lovely.onInitialize()` on Forge/NeoForge — before `ITEMS.register(eventBus)` and `ENTITIES.register(eventBus)`.

### 6.3 Required Lifecycle Sequence

```
── Mod constructor / Fabric onInitialize() entry point ──────────────────────

1. Lovely.onInitialize()                          ← NEW: must be created in Phase 1
   ├── LegacyRobotDefinitions.register()          ← populates registry for Legacy entities
   ├── RebootRobotDefinitions.register()          ← populates registry for Reboot entities
   └── RobotDefinitionRegistry.seal()             ← prevents any late registration

── All consumers below this line can safely read from the registry ──────────

2. LegacyRobotFamilies.initialize()               ← creates RobotFamily map from registry
   RebootRobotFamilies.initialize()

3. lovelylib.source.legacy.LegacyConfigs static { } ← Default map populated from registry
   lovelylib.source.reboot.RebootConfigs static { }

4. Loader registration events (Forge RegisterEvent / Fabric direct calls)
   ├── {Mod}Items.registerAll()                   ← spawn item map populated
   ├── {Mod}Entities.registerAll()                ← entity type map populated
   └── {Mod}Groups registration

5. Config load / reload events
   ├── Fabric-side LegacyConfigs.loadDynamicEntityConfigs()
   │     loops over RobotDefinitionRegistry.getVariantKeysForMod(LEGACY)
   │                                              ← replaces LovelyConstant.LEGACY_VARIANTS
   └── LegacyRobotFamilies.reloadFromConfig()     ← reads from registry + LegacyConfigs.Entities
```

Every loader entry point — Forge, NeoForge, and Fabric — for both `llovelyr` and `rlovelyr` must call `Lovely.onInitialize()` as its very first action before any registration or event bus wiring.


---

## 7. Item Model JSON — Build-Level Solution

Item model JSONs (`{key}_spawn.json` predicate files) are currently triplicated across the three loader resource directories. The content is identical across Forge, NeoForge, and Fabric.

**Recommended solution: Gradle copy task in each mod's `build.gradle`:**

```groovy
// Copy item model JSONs from Common to each loader's resources directory.
// Runs before processResources so they are included in the build.
tasks.named('processForgeResources') {
    dependsOn 'copyItemModels'
}
tasks.register('copyItemModels', Copy) {
    from   "Common/src/main/resources/assets/${modId}/models/item/"
    into   file("Forge/src/main/resources/assets/${modId}/models/item/")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
// Repeat for Fabric and NeoForge, adjusting the target path.
```

With this in place, item model JSONs are maintained in one location (`Common/src/main/resources/assets/{modid}/models/item/`) and distributed automatically. This removes the last source of Bug 4 ("missing model on one loader").

If the Gradle task is deferred, the existing triple-copy approach remains valid — document it explicitly as a known friction point in `New_Robot_Family_Guide.md` until resolved.

---

## 8. New Workflow — Complete Reference

After this ADR is implemented, adding a new robot entity requires exactly this:

### Step 1 — `RobotVariant.java` (lovelylib) [mandatory, 1 line]

```java
// Add a public static final constant. Choose the next available ID above 7
// (IDs 0–7 are reserved for built-ins). The key string is the canonical identifier
// used everywhere downstream — choose it carefully, it drives NBT keys.
public static final RobotVariant Sentry = register(8, "sentry");
```

This is the only change needed in `RobotVariant.java`. No awareness of other files required.

### Step 2 — `LegacyRobotDefinitions.java` or `RebootRobotDefinitions.java` (lovelylib) [mandatory, ~8–12 lines]

```java
// Full palette, standard features:
RobotDefinitionRegistry.register(new RobotEntityDefinition.Builder(RobotVariant.Sentry)
    .displayName("Sentry")
    .forMod(ModTarget.LEGACY)
    .stats(300, 50, 15, 0.9f, 10, 1.0f, 0.28f)
    .build());

// Restricted palette + custom renderer + extra features:
RobotDefinitionRegistry.register(new RobotEntityDefinition.Builder(RobotVariant.Phantom)
    .displayName("Phantom")
    .forMods(ModTarget.LEGACY, ModTarget.REBOOT)
    .palette(EntityTexture.GRAY, EntityTexture.BLACK, EntityTexture.PURPLE)
    .stats(250, 40, 12, 1.1f, 8, 0.5f, 0.31f)
    .renderer(PhantomRenderer::new)
    .featureConfigurator((family, cfg) ->
        family.withFeature(SomeSpecialFeature.class, new SomeSpecialFeature()))
    .build());
```

### Step 3 — Assets [unchanged from current process]

These are not Java code and are unaffected by this ADR:

| Asset | Location | Notes |
|-------|----------|-------|
| Geo models | `lovelylib/geo/{key}.default.geo.json` + `{key}.armed.geo.json` | |
| Entity textures | `lovelylib/textures/entity/{key}/` | One PNG per active color |
| Item textures | `lovelylib/textures/item/{key}/` | One PNG per active color + `_16.png` |
| lovelylib lang | `lovelylib/lang/en_us.json` | `"variant.lovelylib.{key}": "{Display Name}"` |
| Mod lang | `{modid}/Common/lang/en_us.json` | `"item.{modid}.{key}_spawn"` + `"entity.{modid}.{key}"` |
| Recipes | `{modid}/Common/data/{modid}/recipe/` | `{key}_spawn.json` + `{key}_spawn_dye.json` |
| Item model JSON | `{modid}/Common/models/item/` | One file, auto-copied by Gradle task |
| lovelylib item model JSONs | `lovelylib/models/item/{key}/` | One JSON per active color |

---

## 9. Before vs After — Comparison Table

| Location | Before | After |
|----------|--------|-------|
| `LovelyConstant.java` | 3 additions per entity | **Zero changes ever** |
| `RobotVariant.java` | 1 enum entry | **1 static constant** (`register(id, "key")`) |
| `SharedConfigs.java` | 7 static fields (deprecated) | **Zero changes ever** |
| `ConfigAccessLayer.java` | 1 switch case | **Zero changes ever** |
| `LovelyIdentifier.java` | 1 switch case | **Zero changes ever** |
| `LegacyRobotFamilies.java` | 1 static field + 1 `reloadFromConfig()` block | **Zero changes ever** |
| `LegacyConfigs.java` (lovelylib) | 1 `Default.put()` in static block | **Zero changes ever** |
| `RebootRobotFamilies.java` | same as Legacy | **Zero changes ever** |
| `RebootConfigs.java` (lovelylib) | same | **Zero changes ever** |
| `{Mod}Items.java` × 3 loaders | 1 field + 1 `registerModel()` per loader | **Zero changes ever** |
| `{Mod}Entities.java` × 3 loaders | 4 entries per loader (field, attr, render, feature) | **Zero changes ever** |
| `{Mod}Groups.java` × 3 loaders | 2 entries per loader | **Zero changes ever** |
| **Definition file (new)** | — | **1 builder call (~8–12 lines)** |
| **Total code changes** | **~30 edits across 13+ files** | **2 edits in 2 files** |

---

## 10. Eliminated Bug Classes

| Bug | How Eliminated |
|-----|----------------|
| Bug 1 — "Vanilla" in overhead messages | `LovelyIdentifier` no longer has a switch. All variants resolve through the registry. An unregistered variant throws at startup — it cannot silently return a wrong value. |
| Bug 2 — Zero stats on Forge/NeoForge | The `Default`/`Entities` map disconnect in `LegacyConfigs` is fixed in Phase 0 (one-line fallback fix). After Phase 2, `ConfigAccessLayer` and the lovelylib `LegacyConfigs.Default` both derive from the registry — no switch, no disconnected map. |
| Bug 3 — `variant.*` key in wrong lang file | Display name lives in the definition builder. `RobotDefinitionRegistry.getDisplayTranslationKey()` always produces `variant.lovelylib.{key}`, enforcing the correct file structurally. |
| Bug 4 — Missing model on one loader | Resolved by the Gradle copy task. If deferred, the triple-copy friction is explicitly documented as the only remaining manual step. |


---

## 11. Consequences

### Positive

- New entities require changes in exactly 2 Java files, down from 13+. The enum entry is the irreducible serialization anchor; the builder call is all remaining data.
- All four Bunny3 bug classes are structurally eliminated. Missing registrations now fail fast at startup with a clear exception rather than producing silent wrong behavior at runtime.
- `ConfigAccessLayer`, `LovelyIdentifier`, `SharedConfigs`, and both `*Configs` (lovelylib-side) become permanently stable — they never need editing for new entities.
- The definition builder is self-documenting. All facts about an entity — palette, stats, mods, renderer, extra features — are co-located and readable at a glance.
- `RobotDefinitionRegistry.seal()` makes accidental late registration an immediate loud failure.
- The Fabric config loops (`buildConfigProvider`, `loadDynamicEntityConfigs`) become automatically correct for any new entity because they iterate `getVariantKeysForMod()` rather than a manually maintained array.

### Constraints

- `RobotVariant` is a final class with static constants and an internal `BY_ID` / `BY_NAME` registry. Integer IDs must be unique and stable across worlds — built-in IDs 0–7 are reserved; addon mods use IDs ≥ 100. Changing an existing ID requires an NBT migration.
- The access pattern for `LegacyRobotFamilies`, `{Mod}Items`, and `{Mod}Entities` changes from named static fields to map getters (`get(variant)`). This is a one-time migration of all existing call sites across llovelyr and rlovelyr.
- `Lovely.onInitialize()` must be created from scratch (Phase 1) and wired into every loader entry point before any registration or event bus wiring. This is a build task, not a refactoring.
- The `EntityStats` values in the definition builder must match the values currently in `LegacyConfigs.Default` during migration. After Phase 2 completes, the builder is the canonical source and `*Configs` derive from it.
- Loader-side map types diverge: Forge/NeoForge store `RegistryObject<T>` wrappers; Fabric stores unwrapped `T` directly. Each loader's `{Mod}Items` and `{Mod}Entities` must use its own map generic type.

### Future Considerations

- The `RendererFactory` interface currently uses `Object` for both the context and return type to stay loader-agnostic. A typed variant per loader (using the actual `EntityRendererProvider.Context` type) can be introduced as a loader-specific subinterface without changing the lovelylib-side definition API.
- The `featureConfigurator` currently operates at reload time. If feature configuration ever needs to run at initialization (before config values are available), a separate `initConfigurator` callback can be added to the builder following the same pattern.
- The Gradle copy task for item model JSONs could be replaced by a Minecraft data generator once the project adopts the built-in data generation pipeline.

---

## 12. Implementation Phases

### Phase 0 — Pre-migration bug fix (lovelylib only, 1 line)

Fix the `getEntityConfig()` fallback bug in `lovelylib.source.legacy.LegacyConfigs` and `lovelylib.source.reboot.RebootConfigs` before any other work begins. On Forge/NeoForge, `Entities` is never populated, so every variant currently falls back to the generic `EntityConfigData.getDefault()` instead of the per-variant `Default` map.

```java
// In lovelylib.source.legacy.LegacyConfigs (and RebootConfigs):

// BEFORE — wrong fallback, ignores variant-specific Default map:
public static EntityConfigData getEntityConfig(String variant) {
    EntityConfigData config = Entities.get(variant);
    if (config == null) return EntityConfigData.getDefault(); // ← generic baseline
    return config.validateOrDefault();
}

// AFTER — correct fallback, uses variant-specific Default map:
public static EntityConfigData getEntityConfig(String variant) {
    EntityConfigData config = Entities.get(variant);
    if (config == null) return getDefaultConfig(variant); // ← variant-specific values
    return config.validateOrDefault();
}
```

This is a standalone one-line fix. Commit with `FIX:` prefix. No other files change.

### Phase 1 — New infrastructure (lovelylib only, no behavior change)

- [ ] Migrate `RobotVariant` from enum (in `common.entity.enums`) to final class (in `common.entity.definition`) — static constants, `register(int, String)`, `byId()`, `byName()`, `values()`; update all existing import sites across lovelylib, llovelyr, rlovelyr
- [ ] Create `net.heriazone.lovelylib.common.entity.definition.ModTarget`
- [ ] Create `net.heriazone.lovelylib.common.entity.definition.EntityStats`
- [ ] Create `net.heriazone.lovelylib.common.entity.definition.RobotEntityDefinition` (with `RendererFactory` and `featureConfigurator`)
- [ ] Create `net.heriazone.lovelylib.common.entity.definition.RobotDefinitionRegistry`
- [ ] Create `net.heriazone.lovelylib.source.legacy.LegacyRobotDefinitions` with all 8 existing Legacy entities declared — stat values must match current `LegacyConfigs.Default`
- [ ] Create `net.heriazone.lovelylib.source.reboot.RebootRobotDefinitions` for any Reboot-exclusive entities (shared entities already declared with `forMods(LEGACY, REBOOT)` in `LegacyRobotDefinitions`)
- [ ] Create `Lovely.onInitialize()` in `Lovely.java` — calls both definition files then `RobotDefinitionRegistry.seal()`
- [ ] Wire `Lovely.onInitialize()` into every loader entry point (Forge, NeoForge, Fabric × llovelyr + rlovelyr = 6 entry points) as the very first call before any registration or event bus wiring

At this point the registry is populated and sealed, but nothing reads from it yet. Existing behavior is completely unchanged.

### Phase 2 — Simplify lovelylib consumers (lovelylib only)

- [ ] Refactor `LovelyIdentifier.getTranslation()` to use registry lookup (§4.4)
- [ ] Refactor `ConfigAccessLayer.getDefaultEntityConfig()` to use registry loop (§4.3)
- [ ] Refactor `lovelylib.source.legacy.LegacyConfigs` static block to iterate registry (§4.6)
- [ ] Refactor `lovelylib.source.reboot.RebootConfigs` static block to iterate registry (§4.6)
- [ ] Refactor `LegacyRobotFamilies` to map pattern + `initialize()` + uniform `reloadFromConfig()` loop with `applyFeatureConfigurator()` (§4.5)
- [ ] Refactor `RebootRobotFamilies` identically
- [ ] Remove per-entity deprecated static fields from `SharedConfigs.Common` (§4.2)
- [ ] Remove per-entity constants from `LovelyConstant` — `{KEY}_SPAWN`, `VARIANT_{KEY}`, `LEGACY_VARIANTS`, `REBOOT_VARIANTS`, `ALL_VARIANTS` (§4.1); retain `TRIBUTE_VARIANTS`
- [ ] Update Fabric-side `LegacyConfigs.buildConfigProvider()` and `loadDynamicEntityConfigs()` to use `RobotDefinitionRegistry.getVariantKeysForMod(LEGACY)` instead of `LovelyConstant.LEGACY_VARIANTS`
- [ ] Update Fabric-side `RebootConfigs` equivalently
- [ ] Update `ConfigAccessLayer.validateAllConfigs()` — remove the `LovelyConstant.ALL_VARIANTS` overload or redirect it to the registry
- [ ] Migrate all call sites of `LegacyRobotFamilies.{VARIANT}` → `LegacyRobotFamilies.get(RobotVariant.{Variant})`
- [ ] Run all three loaders on existing entities to verify no regressions

### Phase 3 — Simplify loader-side classes (llovelyr + rlovelyr, all loaders)

- [ ] Refactor `{Mod}Items` to map pattern + `registerAll()` loop — Forge/NeoForge (`RegistryObject<Item>`) and Fabric (`Item`) separately (× 3 loaders × 2 mods = 6 files) (§5.2)
- [ ] Refactor `{Mod}Entities` to map pattern + loops for attributes, renderers (using `RendererFactory`), and features (× 6 files) (§5.3)
- [ ] Refactor `{Mod}Groups` to iterate registry in both `buildDefaultTab()` and `addSpawnEggs()` callbacks (× 6 files) (§5.4)
- [ ] Set up Gradle copy task for item model JSONs (§7), or document triple-copy as explicit friction if deferred
- [ ] Migrate all call sites of `LegacyItems.{VARIANT}_SPAWN` → `LegacyItems.getSpawnItem(RobotVariant.{Variant})`
- [ ] Migrate all call sites of `LegacyEntities.{VARIANT}` → `LegacyEntities.getEntityType(RobotVariant.{Variant})`
- [ ] Full in-game test across all three loaders for both llovelyr and rlovelyr

### Phase 4 — Proof of concept

- [ ] Add one new test entity using only the 2-step process (`RobotVariant.register()` constant + builder call)
- [ ] Confirm zero other Java files required modification
- [ ] Remove the test entity
- [ ] Update `New_Robot_Family_Guide.md` to reference this ADR and replace §§1.1–1.11 with the §8 workflow

---

## 13. Updated Checklist (replaces §§1.1–1.11 of `New_Robot_Family_Guide.md`)

After this ADR is implemented, the complete checklist for adding a new entity is:

### lovelylib — Code (2 items, the only mandatory Java changes)

- [ ] `RobotVariant.java` — one new `public static final RobotVariant {Name} = register(N, "{key}");` constant with a unique ID ≥ 8 (IDs 0–7 reserved for built-ins; addon mods use IDs ≥ 100)
- [ ] `LegacyRobotDefinitions.java` and/or `RebootRobotDefinitions.java` — one builder call with `.displayName()`, `.forMod()` or `.forMods()`, optional `.palette()`, `.stats()`, optional `.renderer()`, optional `.featureConfigurator()`

### lovelylib — Resources

- [ ] `lovelylib/lang/en_us.json` — `"variant.lovelylib.{key}": "{Display Name}"`
- [ ] `lovelylib/geo/` — `{key}.default.geo.json` and `{key}.armed.geo.json`
- [ ] `lovelylib/textures/entity/{key}/` — PNGs for all active color IDs
- [ ] `lovelylib/textures/item/{key}/` — PNGs for all active color IDs + `_16.png`
- [ ] `lovelylib/models/item/{key}/` — one JSON per active color ID + `_16.json`

### Per mod — Common resources (loader-independent)

- [ ] `{modid}/Common/lang/en_us.json` — `"item.{modid}.{key}_spawn"` and `"entity.{modid}.{key}"`
- [ ] `{modid}/Common/data/{modid}/recipe/{key}_spawn.json`
- [ ] `{modid}/Common/data/{modid}/recipe/{key}_spawn_dye.json`
- [ ] `{modid}/Common/models/item/{key}_spawn.json` (auto-copied to all loaders by Gradle task; or add manually to all three if task not yet set up)

**Total: 2 code changes + 10 asset/resource files**
_(Previously: ~30 code changes + 10 asset files)_
