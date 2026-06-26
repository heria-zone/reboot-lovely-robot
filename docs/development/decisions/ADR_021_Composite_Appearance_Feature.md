# ADR 021: Two-Lane Appearance Architecture & CompositeAppearanceFeature

**Status**: Proposed  
**Date**: 2026-06-25  
**Decision Makers**: Project Lead  
**Scope**: HZLib Common — `api/entity/features/variants`  
**Renames**: `AppearanceVariantFeature` → `CompositeAppearanceFeature`

---

## Context

### The Current State

The Appearance tier is implemented through four independent features, each registered separately on a family descriptor:

| Feature | Responsibility | Registry |
|---------|---------------|---------|
| `TextureVariantFeature` | Texture palette — 16 colors, seasonal swaps, etc. | `VariantRegistries.TEXTURES` |
| `ModelVariantFeature` | Model state — default vs. armed, size variants | `VariantRegistries.MODELS` |
| `AnimatorVariantFeature` | Animation file selection | `VariantRegistries.ANIMATORS` |
| `AppearanceVariantFeature` | Composite bundle — texture + model + animator as one unit | `VariantRegistries.APPEARANCES` |
| `SizeVariantFeature` | Per-size hitbox, scale, and **stat multipliers** | — (no global registry) |

### The Problem Is Not Scatter — It Is Ambiguity

Scatter alone is not wrong. The real problem is that there are two distinct, valid, incompatible usage patterns and nothing in the codebase makes them explicit or enforces which one an entity should use:

**Pattern A — Independent axes**: texture, model, and animator vary independently. A Bunny robot can be any of 16 colors AND either armed or unarmed. These are orthogonal dimensions that combine combinatorially — 16 colors × 2 model states = 32 combinations, none of which need to be declared explicitly. `TextureVariantFeature` + `ModelVariantFeature` + `AnimatorVariantFeature` handle this correctly and efficiently.

**Pattern B — Coupled bundle**: all visual dimensions of a named appearance are inseparable. `gourdragora_golden_big` is one specific thing — one texture, one model, one scale. There is no `gourdragora_golden_big` that uses the `gourdragora_golden_mini` model. These cannot be expressed as independent axes without risk of mismatched combinations (big texture + mini model). `AppearanceVariantFeature` was built for exactly this.

The problems caused by this ambiguity:

1. **`AppearanceVariantFeature` sits alongside the three independent features** rather than replacing them for the entities it serves. Nothing prevents an entity from having both, and `NativeEntity.initializeRandomVariants` queries all four independently, creating potential for incoherence — a texture from one feature with a model from another.

2. **`SizeVariantFeature` is orphaned between tiers.** It carries both visual data (hitbox, scale — Appearance tier) and gameplay data (stat multipliers — Variant tier). Nothing formally connects it to either lane. When an entity uses size-based appearance (Gourdragora), the connection between `SizeVariantFeature.SizeConfig` and the corresponding `AppearanceVariantFeature` entry is maintained by hand-written string keys, not by structure.

3. **`AppearanceVariantFeature` is misnamed.** The "Variant" in the name conflicts with the established Terminology where Variant is the tier *above* Appearance. A developer reading `AppearanceVariantFeature` cannot tell whether this is a feature that manages the Variant tier or the Appearance tier. It manages the Appearance tier — the name should say so.

4. **Structural duplication inside the three independent features.** `TextureVariantFeature`, `ModelVariantFeature`, and `AnimatorVariantFeature` are character-for-character identical except for the type tokens. Every method — `withVariant`, `withVariants`, `withDefault`, `getAvailableVariants`, `getDefaultVariant`, `getRandomVariant`, `hasVariant`, `getVariantCount` — is copy-pasted three times with only the interface name changed. `IVariantFeature<T>` was meant to extract this but it only defines the contract — not the shared implementation.

### Measuring the Duplication

Each of the three independent features contains:
- 2 `HashMap` fields with identical construction
- 1 `ResourceMap` field (legacy)
- 8 `IVariantFeature<T>` method implementations — identical logic, different type tokens
- 3 `withVariant`/`withVariants`/`withDefault` configuration methods — identical logic
- 3 legacy methods (`withTexture`/`withModel`/`withAnimator`, `getTexture`/`getModel`/`getAnimator`, `hasTexture`/`hasModel`/`hasAnimator`) — identical structure

That is approximately 300 lines of logic duplicated three times — ~900 lines where ~400 suffice.

---

## Decision

Three decisions, one coherent scope:

1. **Formalise the two-lane architecture.** Make the Independent Axes Lane and the Composite Lane explicit, documented, and mutually exclusive per entity. `NativeEntity.initializeRandomVariants` checks for the composite lane first and only falls through to independent axes if the composite is absent.

2. **Rename `AppearanceVariantFeature` to `CompositeAppearanceFeature`** to align with Terminology. "Composite" states what it does — bundles multiple appearance dimensions into one coherent unit. "Appearance" states which tier it manages.

3. **Extract `AbstractVariantFeature<T, V>` as a shared base** for `TextureVariantFeature`, `ModelVariantFeature`, and `AnimatorVariantFeature`, eliminating the ~600 lines of duplication across the three independent-axis features.

`SizeVariantFeature` is **not merged** into `CompositeAppearanceFeature`. Its stat multipliers are Variant-tier data, not Appearance-tier data. The connection between a size configuration and a composite appearance entry is structural — `CompositeAppearanceFeature` holds a reference to the applicable `SizeVariantFeature.SizeConfig` within each `ICompositeAppearance` entry, rather than the two features being collapsed into one.

---

## Architecture

### The Two Lanes — Formal Definition

```
Lane A — Independent Axes (combinatorial)
├── TextureVariantFeature    → texture key (e.g. "bunny_white", "bunny_orange")
├── ModelVariantFeature      → model key   (e.g. "bunny_default", "bunny_armed")
└── AnimatorVariantFeature   → animator key (e.g. "robot_default")

  Each axis varies independently. NxMxP combinations, none declared explicitly.
  Used by: all robots, most monsters with simple palettes.

Lane B — Composite (fully coupled)
└── CompositeAppearanceFeature → named appearance bundles
       e.g. "gourdragora_golden_big" = { texture, model, animator, sizeConfig }

  All dimensions move together. Every combination declared explicitly.
  Used by: Gourdragora (size × color fully coupled), any entity where
  the combination space is small and combinations are not free.
```

**Mutual exclusion rule**: a family declares either Lane A OR Lane B. Declaring both is an error. `NativeEntity.initializeRandomVariants` enforces this at runtime with a logged warning if both are present, defaulting to Lane B.

---

### Part 1 — `AbstractVariantFeature<T, V>` (eliminates duplication)

New abstract base in `net.heriazone.hzlib.api.entity.features.variants`.

```java
/**
 * <p>Shared implementation base for single-axis appearance variant features.<p>
 * <p>
 * <b>Architecture:</b> Eliminates the ~300-line implementation duplicated across
 * {@link TextureVariantFeature}, {@link ModelVariantFeature}, and
 * {@link AnimatorVariantFeature}. Each subclass provides only the registry
 * accessor and the typed return — all query and configuration logic lives here.
 *
 * @param <V> the variant interface type (ITextureVariant, IModelVariant, IAnimatorVariant)
 */
public abstract class AbstractVariantFeature<V extends IVariant>
        implements IVariantFeature<V> {

    // -- Fields --

    private final Map<String, Set<String>> entityVariants  = new HashMap<>();
    private final Map<String, String>      defaultVariants = new HashMap<>();
    private final ResourceMap<String, ResourceLocation> additionalResources = new ResourceMap<>();

    // -- Abstract hook --

    /** Returns the global registry to resolve variant keys against. */
    protected abstract VariantRegistry<V> registry();

    // -- IVariantFeature -- (implemented once, inherited by all three subclasses)

    @Override
    public Collection<V> getAvailableVariants(String entityKey) {
        Set<String> enabled = entityVariants.get(entityKey);
        if (enabled == null || enabled.isEmpty()) return Collections.emptyList();
        return enabled.stream()
                .map(key -> registry().get(key))
                .filter(Optional::isPresent).map(Optional::get)
                .filter(v -> v.isAvailable(entityKey))
                .collect(Collectors.toList());
    }

    @Override
    public V getDefaultVariant(String entityKey) {
        String defaultKey = defaultVariants.get(entityKey);
        if (defaultKey != null) {
            Optional<V> found = registry().get(defaultKey);
            if (found.isPresent() && found.get().isAvailable(entityKey)) return found.get();
        }
        return getAvailableVariants(entityKey).stream()
                .max(Comparator.comparingInt(IVariant::getPriority)).orElse(null);
    }

    @Override
    public V getRandomVariant(String entityKey) {
        List<V> available = new ArrayList<>(getAvailableVariants(entityKey));
        if (available.isEmpty()) return null;
        return available.get(new Random().nextInt(available.size()));
    }

    @Override
    public boolean hasVariant(String entityKey, String variantKey) {
        Set<String> enabled = entityVariants.get(entityKey);
        if (enabled == null || !enabled.contains(variantKey)) return false;
        return registry().get(variantKey).map(v -> v.isAvailable(entityKey)).orElse(false);
    }

    @Override
    public int getVariantCount(String entityKey) { return getAvailableVariants(entityKey).size(); }

    // -- Configuration (fluent, self-typed via subclass cast) --

    public AbstractVariantFeature<V> withVariant(String entityKey, String variantKey) {
        if (entityKey != null && variantKey != null)
            entityVariants.computeIfAbsent(entityKey, k -> new HashSet<>()).add(variantKey);
        return this;
    }

    public AbstractVariantFeature<V> withVariants(String entityKey, String... variantKeys) {
        if (entityKey != null && variantKeys != null)
            Collections.addAll(entityVariants.computeIfAbsent(entityKey, k -> new HashSet<>()), variantKeys);
        return this;
    }

    public AbstractVariantFeature<V> withDefault(String entityKey, String variantKey) {
        if (entityKey != null && variantKey != null) defaultVariants.put(entityKey, variantKey);
        return this;
    }

    // -- Legacy resource map (used by withTexture/withModel/withAnimator) --

    protected ResourceMap<String, ResourceLocation> additionalResources() {
        return additionalResources;
    }

} // Class: AbstractVariantFeature
```

#### Subclasses After Extraction

Each subclass reduces to ~15 lines — the registry hook, the type token, and any legacy-named convenience methods:

```java
public final class TextureVariantFeature
        extends AbstractVariantFeature<ITextureVariant> {

    @Override protected VariantRegistry<ITextureVariant> registry() {
        return VariantRegistries.TEXTURES;
    }
    @Override public Class<ITextureVariant> getVariantType() { return ITextureVariant.class; }

    // Convenience alias kept for readability at call sites
    public TextureVariantFeature withTexture(String key, ResourceLocation loc) {
        additionalResources().put(key, loc); return this;
    }
    public ResourceLocation getTexture(String key) { return additionalResources().get(key); }

} // Class: TextureVariantFeature
```

`ModelVariantFeature` and `AnimatorVariantFeature` follow the exact same pattern with their respective registry references and `withModel`/`withAnimator` aliases.

---

### Part 2 — `CompositeAppearanceFeature` (Lane B)

Renamed from `AppearanceVariantFeature`. The rename is the only public API change — all method signatures are preserved. Internal structure gains one addition: `SizeConfig` reference on `ICompositeAppearance`.

#### `ICompositeAppearance` — updated interface

```java
public interface ICompositeAppearance extends IVariant {

    /** Texture resource key for this appearance (e.g. "gourdragora_golden_big"). */
    String getTextureKey();

    /** Model resource key for this appearance. */
    String getModelKey();

    /** Animator resource key for this appearance. */
    String getAnimatorKey();

    /**
     * Optional size configuration for this appearance.
     * Present when the appearance is tied to a specific size (Gourdragora Big, Mini, Default).
     * Absent (empty) for appearances not driven by size (most robots, flat-palette monsters).
     * <p>
     * <b>Design:</b> Holds the SizeConfig reference rather than duplicating its fields,
     * so hitbox dimensions, scale, and stat multipliers are accessed from a single source
     * of truth. The entity calls sizeConfig.applyTo(entity) at spawn and on NBT load.
     */
    Optional<SizeVariantFeature.SizeConfig> getSizeConfig();

} // Interface: ICompositeAppearance
```

#### `CompositeAppearanceFeature` — class declaration

```java
/**
 * <p>Lane B appearance feature — bundles texture, model, animator, and optional size
 * configuration into named, explicitly-declared appearance entries.<p>
 * <p>
 * <b>When to use:</b> When the appearance dimensions are fully coupled — no combination
 * of texture × model × animator is valid unless it was explicitly declared. Gourdragora
 * (size × color tightly coupled) is the canonical example.
 * <p>
 * <b>When NOT to use:</b> When appearance dimensions vary independently (robots with
 * 16 color × 2 model state combinations). Use {@link TextureVariantFeature} +
 * {@link ModelVariantFeature} + {@link AnimatorVariantFeature} (Lane A) instead.
 * <p>
 * <b>Mutual exclusion:</b> A family should declare either this feature OR the Lane A
 * features — not both. {@code NativeEntity.initializeRandomVariants} checks for this
 * feature first and skips Lane A if it is present.
 * <p>
 * <b>Replaces:</b> {@code AppearanceVariantFeature} — renamed for Terminology alignment.
 * All method signatures are identical. Callers update the class name only.
 */
public final class CompositeAppearanceFeature
        implements IVariantFeature<ICompositeAppearance> {
    // ... same implementation as AppearanceVariantFeature, with:
    // - class name changed to CompositeAppearanceFeature
    // - getVariantType() returns ICompositeAppearance.class
    // - getVariant(entityKey, variantKey) preserved
    // - VariantRegistries.APPEARANCES unchanged
} // Class: CompositeAppearanceFeature
```

---

### Part 3 — `NativeEntity.initializeRandomVariants` — Lane Resolution

The base implementation is updated to be lane-aware. This is the single place where the mutual-exclusion rule is enforced:

```java
protected void initializeRandomVariants() {
    if (nativeEntity == null) return;

    // -- Lane B: Composite (checked first) --
    Optional<CompositeAppearanceFeature> compositeOpt =
            nativeEntity.getFeature(CompositeAppearanceFeature.class);

    if (compositeOpt.isPresent()) {
        // Warn if Lane A features are also present — misconfiguration
        if (hasLaneAFeatures()) {
            LOGGER.warn("[HZLib] Family '{}' declares both CompositeAppearanceFeature and " +
                    "independent axis features. CompositeAppearanceFeature takes precedence.",
                    nativeEntity.getKey());
        }
        CompositeAppearanceFeature composite = compositeOpt.get();
        ICompositeAppearance appearance = composite.getRandomVariant(nativeEntity.getKey());
        if (appearance != null) {
            setTextureVariant(appearance.getTextureKey());
            setModelVariant(appearance.getModelKey());
            setAnimatorVariant(appearance.getAnimatorKey());
            // Apply size config if present — hitbox + stats
            appearance.getSizeConfig().ifPresent(sc -> sc.applyTo(this));
        }
        // Seed RANDOM overlay slots regardless of lane
        seedOverlaySlots();
        return; // Lane B handled — skip Lane A entirely
    }

    // -- Lane A: Independent axes (fallback when no composite feature) --
    nativeEntity.getFeature(TextureVariantFeature.class).ifPresent(f -> {
        ITextureVariant v = f.getRandomVariant(nativeEntity.getKey());
        if (v != null) setTextureVariant(v.getKey());
    });
    nativeEntity.getFeature(ModelVariantFeature.class).ifPresent(f -> {
        IModelVariant v = f.getRandomVariant(nativeEntity.getKey());
        if (v != null) setModelVariant(v.getKey());
    });
    nativeEntity.getFeature(AnimatorVariantFeature.class).ifPresent(f -> {
        IAnimatorVariant v = f.getRandomVariant(nativeEntity.getKey());
        if (v != null) setAnimatorVariant(v.getKey());
    });
    seedOverlaySlots();

} // initializeRandomVariants ()

private boolean hasLaneAFeatures() {
    return nativeEntity.getFeature(TextureVariantFeature.class).isPresent()
            || nativeEntity.getFeature(ModelVariantFeature.class).isPresent()
            || nativeEntity.getFeature(AnimatorVariantFeature.class).isPresent();
}
```

---

### Part 4 — `SizeVariantFeature` Boundary (Unchanged)

`SizeVariantFeature` is **not merged** into `CompositeAppearanceFeature`. The rationale:

- `SizeVariantFeature.SizeConfig` carries stat multipliers (`healthMultiplier`, `attackMultiplier`, etc.) and hitbox dimensions. Stat multipliers are Variant-tier data — they affect gameplay, not just visuals. Merging them into a visual feature would blur the tier boundary the Terminology worked to establish.
- The connection is structural, not positional. `ICompositeAppearance.getSizeConfig()` holds a reference to the `SizeConfig` relevant to that appearance entry. The entity accesses it through the appearance — `sizeConfig.applyTo(entity)` — without `SizeVariantFeature` needing to know about `CompositeAppearanceFeature` and vice versa.
- Families that do not use size-based appearances (all robots, most monsters) continue to use `SizeVariantFeature` without `CompositeAppearanceFeature`. The two features remain independently composable.

---

## Package Layout (Final State)

```
net.heriazone.hzlib.api.entity.features.variants
│
├── AbstractVariantFeature<V>      ← NEW — shared impl base for Lane A features
│
├── TextureVariantFeature          ← SIMPLIFIED — extends AbstractVariantFeature<ITextureVariant>
├── ModelVariantFeature            ← SIMPLIFIED — extends AbstractVariantFeature<IModelVariant>
├── AnimatorVariantFeature         ← SIMPLIFIED — extends AbstractVariantFeature<IAnimatorVariant>
│
├── CompositeAppearanceFeature     ← RENAMED from AppearanceVariantFeature
│
└── IVariantFeature<T>             ← UNCHANGED — contract interface

net.heriazone.hzlib.api.entity.variants.interfaces
├── ICompositeAppearance           ← UPDATED — adds getSizeConfig()
├── ITextureVariant                ← UNCHANGED
├── IModelVariant                  ← UNCHANGED
├── IAnimatorVariant               ← UNCHANGED
└── IVariant                       ← UNCHANGED
```

---

## Migration

### `AppearanceVariantFeature` → `CompositeAppearanceFeature`

A class rename. All method signatures are identical — callers update the type name only. A `@Deprecated` type alias can be provided for one version if needed:

```java
/** @deprecated Use {@link CompositeAppearanceFeature} */
@Deprecated
public final class AppearanceVariantFeature extends CompositeAppearanceFeature {}
```

### `TextureVariantFeature` / `ModelVariantFeature` / `AnimatorVariantFeature`

Internal refactor only — no public API changes. All `withVariant`, `withVariants`, `withDefault`, `getAvailableVariants`, `getRandomVariant` signatures are identical. Callers are unaffected.

### `IAppearanceVariant` → `ICompositeAppearance`

The interface rename requires updating implementors. `getSizeConfig()` is added with a default implementation returning `Optional.empty()` so existing implementations compile without change:

```java
default Optional<SizeVariantFeature.SizeConfig> getSizeConfig() {
    return Optional.empty();
}
```

---

## Consequences

### Positive

- **Two patterns are explicit and enforced.** Every family declaration is unambiguously in one lane. `initializeRandomVariants` never produces an incoherent texture + model combination.
- **`CompositeAppearanceFeature` is self-describing.** The name communicates tier (Appearance) and pattern (Composite) — no confusion with the Variant tier.
- **~600 lines of duplication eliminated.** `TextureVariantFeature`, `ModelVariantFeature`, `AnimatorVariantFeature` each collapse to ~15 lines. All logic lives in `AbstractVariantFeature`.
- **`SizeConfig` is connected to composite appearances structurally**, not by hand-matched string keys. The entity does not need to know which size feature entry corresponds to which appearance entry — the composite entry carries the reference.
- **No call site changes for Lane A families.** `withVariant`, `withVariants`, `withDefault` on all three features are identical in signature. Existing family declarations compile without modification.
- **`ICompositeAppearance.getSizeConfig()` defaults to empty.** Existing `IAppearanceVariant` implementations do not need to change to compile.

### Trade-offs

- **`AbstractVariantFeature` fluent methods return `AbstractVariantFeature<V>`, not the subtype.** Same combinator return-type trade-off as ADR 020/021. Mitigation: subclasses override with covariant returns if call-site chaining is needed.
- **Lane B entries must be declared explicitly.** There is no way to express "all 16 colors for all 3 size variants" in Lane B without 48 entries. This is intentional — if a family can use the combinatorial model, it belongs in Lane A.

### Risks

- **`AnimationStateManager` references `AnimatorVariantFeature` and `AppearanceVariantFeature` by name** in its fallback chain comments and resolution logic. Both references need updating — `AppearanceVariantFeature` → `CompositeAppearanceFeature`. The logic itself is unchanged.
- **Gourdragora `initializeSpawnVariants` override** currently reads `SizeVariantFeature` via `pickWeightedRandom()` and then constructs an appearance key by string concatenation. After this ADR, it should instead call `CompositeAppearanceFeature.getVariant(entityKey, sizeConfig.getSizeKey())` which returns the fully-resolved `ICompositeAppearance` — no string surgery. This is a simplification, not a breaking change.

---

## Implementation Plan

**Step 1 — `AbstractVariantFeature<V>` (HZLib, new class)**
1. Create `AbstractVariantFeature<V extends IVariant>` with shared fields and all `IVariantFeature<V>` implementations
2. Refactor `TextureVariantFeature` to extend it — delete ~250 lines of duplicated logic, keep `withTexture`/`getTexture` aliases
3. Repeat for `ModelVariantFeature` and `AnimatorVariantFeature`
4. Build — verify zero errors

**Step 2 — `ICompositeAppearance` + `CompositeAppearanceFeature` (HZLib, rename + extend)**
5. Add `getSizeConfig()` default method to `IAppearanceVariant` → rename interface to `ICompositeAppearance`
6. Rename `AppearanceVariantFeature` to `CompositeAppearanceFeature`
7. Add optional `@Deprecated` alias for `AppearanceVariantFeature` if rollout needs to be gradual
8. Update `VariantRegistries.APPEARANCES` key type reference if needed

**Step 3 — Lane resolution in `NativeEntity` (HZLib)**
9. Update `initializeRandomVariants` with lane-check logic and `seedOverlaySlots()` extraction
10. Update `AnimationStateManager` comments and class references

**Step 4 — Gourdragora migration (Monsters & Girls)**
11. Replace string-concatenation appearance key construction with `CompositeAppearanceFeature.getVariant()` call
12. Wire `SizeConfig` references into `ICompositeAppearance` entries on `GourdragoraFamily`

---

## Related Documents

- `docs/documentation/TERMINOLOGY.md` — Appearance tier description updated to reference both lanes and `CompositeAppearanceFeature`
- `ADR_017_OverlayFeature_Composable_Visual_Layer_System.md` — overlay slots are a third appearance mechanism, orthogonal to both lanes, unchanged by this ADR
- `ADR_020_Conditional_Appearance_Feature.md` — `ConditionalAppearanceFeature` resolves to a variant key; `initializeSpawnVariants` then applies that key through whichever lane the family uses
- `sources/common/hzlib-1.21.1/.../features/variants/AppearanceVariantFeature.java` — renamed to `CompositeAppearanceFeature`
- `sources/common/hzlib-1.21.1/.../features/variants/TextureVariantFeature.java` — simplified
- `sources/common/hzlib-1.21.1/.../features/variants/ModelVariantFeature.java` — simplified
- `sources/common/hzlib-1.21.1/.../features/variants/AnimatorVariantFeature.java` — simplified
