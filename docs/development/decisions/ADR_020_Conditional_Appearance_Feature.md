# ADR 020: Condition & Context Framework + ConditionalAppearanceFeature

**Status**: Proposed  
**Date**: 2026-06-25  
**Decision Makers**: Project Lead  
**Scope**: HZLib Common — base condition/context infrastructure + unified appearance selection  
**Supersedes**: `BiomeAppearanceFeature` (deleted, not deprecated); absorbs ADR 021 (merged here)

---

## Context

### Two Problems, One Root Cause

**Problem 1 — Appearance selection proliferation**

Two appearance selection mechanisms exist or are planned, each solving the same problem with a different hardcoded input type:

- **`BiomeAppearanceFeature`** (exists, unused in any family): maps `ResourceKey<Biome>` → variant key at spawn time
- **`ItemAppearanceFeature`** (planned): maps `Item` → variant key on right-click

These are structurally identical — both are context-to-key resolvers. Implementing them separately produces two parallel classes, diverging APIs, duplicated builder patterns, and two separate feature lookups in `NativeEntity.initializeSpawnVariants`. Each new input type (dimension, time, weather) would require yet another class.

**Problem 2 — Condition/context duplication already in the codebase**

The pattern for solving problem 1 already exists: `ExchangeFeature` uses a composable `ExchangeCondition` predicate over an `ExchangeContext` data bag. `EmanationFeature` independently re-implemented the exact same pattern. Measuring the duplication between the two live systems:

| Duplicated element | Exchange | Emanation | Appearance (planned) |
|--------------------|----------|-----------|----------------------|
| `and/or/negate` combinators | ✅ | ✅ | would be 3rd copy |
| `getDimension()` / `getDayTime()` / `getBiome()` / `isServerSide()` on context | ✅ | ✅ | would be 3rd copy |
| `inBiome`, `notInBiome`, `inDimension` factory methods | ✅ | ✅ | would be 3rd copy |
| `isDaytime`, `isNighttime`, `inTimeRange` factory methods | ✅ | ✅ | would be 3rd copy |
| `isRaining`, `isThundering`, `chance` factory methods | ✅ | ✅ | would be 3rd copy |

9 factory methods and the 3 combinators are character-for-character identical across both systems. Adding `AppearanceConditions` without a base framework would make it three copies of everything.

**`RenderConditions` is deliberately out of scope.** It uses `Predicate<T extends NativeEntity>`, operates client-side per render frame, and inspects entity state rather than world context. Its own javadoc already documents this separation. It does not participate in this hierarchy.

### Current State of `BiomeAppearanceFeature`

Declared but never registered on any family class. Referenced only in two javadoc comments. Zero call sites — a clean delete.

---

## Decision

Two decisions, one coherent scope:

1. **Extract a base condition/context framework** into a new `net.heriazone.hzlib.api.entity.conditions` package. `ExchangeCondition`, `ExchangeContext`, `EmanationCondition`, `EmanationContext` become thin extensions of these bases. Combinators and shared factory methods are defined exactly once.

2. **Replace `BiomeAppearanceFeature` with `ConditionalAppearanceFeature`** — a single feature that handles all appearance selection (biome, item, dimension, time, weather, any future context) through composable conditions built on the same base framework.

These two decisions are inseparable: the base framework eliminates duplication across existing systems *and* provides the foundation for the appearance feature. Implementing one without the other leaves the job half done.

---

## Architecture — Part 1: Base Framework

### Package

```
net.heriazone.hzlib.api.entity.conditions
├── EntityCondition<C extends EntityContext>    ← base functional interface
├── EntityContext                               ← abstract base context class  
└── EntityConditions                            ← shared factory methods
```

### `EntityContext` — base context class

Holds every field universal to all context types. Subclasses extend with feature-specific fields.

```java
public abstract class EntityContext {

    protected final LivingEntity entity;
    protected final Level        level;
    protected final BlockPos     pos;

    protected EntityContext(LivingEntity entity) {
        this.entity = Objects.requireNonNull(entity);
        this.level  = entity.level();
        this.pos    = entity.blockPosition();
    }

    public LivingEntity              getEntity()     { return entity; }
    public Level                     getLevel()      { return level; }
    public BlockPos                  getPos()        { return pos; }
    public ResourceKey<Level>        getDimension()  { return level.dimension(); }
    public Optional<ResourceKey<Biome>> getBiome()  { return level.getBiome(pos).unwrapKey(); }
    public long                      getDayTime()    { return level.getDayTime() % 24000L; }
    public boolean                   isServerSide()  { return !level.isClientSide; }

} // Class: EntityContext
```

### `EntityCondition<C>` — base condition interface

Generic over the context subtype. Combinators defined exactly once, inherited by all feature condition interfaces.

```java
@FunctionalInterface
public interface EntityCondition<C extends EntityContext> {

    boolean test(C ctx);

    default EntityCondition<C> and(EntityCondition<C> other) {
        return ctx -> this.test(ctx) && other.test(ctx);
    }

    default EntityCondition<C> or(EntityCondition<C> other) {
        return ctx -> this.test(ctx) || other.test(ctx);
    }

    default EntityCondition<C> negate() {
        return ctx -> !this.test(ctx);
    }

} // Interface: EntityCondition
```


### `EntityConditions` — shared factory

All methods that operate on `EntityContext` fields only. Feature-specific factories delegate here and add domain-specific methods on top. Consumers always use the typed factory (`ExchangeConditions`, `AppearanceConditions`, etc.) — never this class directly.

```java
public final class EntityConditions {

    private EntityConditions() {}

    @SafeVarargs
    public static <C extends EntityContext> EntityCondition<C> inBiome(ResourceKey<Biome>... biomes) {
        Set<ResourceKey<Biome>> set = Set.of(biomes);
        return ctx -> ctx.getBiome().map(set::contains).orElse(false);
    }

    @SafeVarargs
    public static <C extends EntityContext> EntityCondition<C> notInBiome(ResourceKey<Biome>... biomes) {
        return EntityConditions.<C>inBiome(biomes).negate();
    }

    @SafeVarargs
    public static <C extends EntityContext> EntityCondition<C> inDimension(ResourceKey<Level>... dims) {
        Set<ResourceKey<Level>> set = Set.of(dims);
        return ctx -> set.contains(ctx.getDimension());
    }

    public static <C extends EntityContext> EntityCondition<C> isDaytime()   { return ctx -> ctx.getDayTime() < 12000L; }
    public static <C extends EntityContext> EntityCondition<C> isNighttime() { return EntityConditions.<C>isDaytime().negate(); }

    public static <C extends EntityContext> EntityCondition<C> inTimeRange(long from, long to) {
        return ctx -> { long t = ctx.getDayTime(); return t >= from && t <= to; };
    }

    public static <C extends EntityContext> EntityCondition<C> isRaining()    { return ctx -> ctx.getLevel().isRaining(); }
    public static <C extends EntityContext> EntityCondition<C> isThundering() { return ctx -> ctx.getLevel().isThundering(); }

    public static <C extends EntityContext> EntityCondition<C> chance(float p) {
        return ctx -> ThreadLocalRandom.current().nextFloat() < p;
    }

    public static <C extends EntityContext> EntityCondition<C> always() { return ctx -> true; }
    public static <C extends EntityContext> EntityCondition<C> never()  { return ctx -> false; }

} // Class: EntityConditions
```

### How Existing Feature Types Change

Each existing condition interface becomes a one-line extension. Each existing context class drops 4 methods. Each existing factory replaces 9 method bodies with 9 one-line delegation wrappers.

**`ExchangeCondition`** — before: full interface with duplicated combinators (~40 lines). After:
```java
@FunctionalInterface
public interface ExchangeCondition extends EntityCondition<ExchangeContext> {
    // inherits test(), and(), or(), negate() — nothing to declare
} // Interface: ExchangeCondition
```

**`ExchangeContext`** — before: declares `getDimension/getBiome/getDayTime/isServerSide` independently. After:
```java
public final class ExchangeContext extends EntityContext {
    private final Player player;

    public ExchangeContext(TamableAnimal entity, Player player) {
        super(entity); // base handles level, pos, biome, dimension, dayTime, isServerSide
        this.player = Objects.requireNonNull(player);
    }

    public Player  getPlayer()       { return player; }
    public boolean isOwnedByPlayer() { ... }
} // Class: ExchangeContext
```

**`ExchangeConditions`** — before: 9 shared method implementations (~120 lines). After:
```java
// Shared methods — one-line delegation wrappers
public static ExchangeCondition inBiome(ResourceKey<Biome>... b) { return EntityConditions.inBiome(b)::test; }
public static ExchangeCondition isDaytime()   { return EntityConditions.isDaytime()::test; }
// ... 7 more delegation wrappers

// Exchange-specific — unchanged
public static ExchangeCondition ownerOnly()   { return ExchangeContext::isOwnedByPlayer; }
public static ExchangeCondition anyPlayer()   { return ctx -> true; }
public static ExchangeCondition entityInState(EntityState... states) { ... }
```

The same transformation applies identically to `EmanationCondition`, `EmanationContext`, `EmanationConditions`.

> **Note on `EmanationContext`**: it currently uses `ServerLevel` as a field. `EntityContext` uses `Level` for generality. `EmanationContext` overrides `getLevel()` with a covariant `ServerLevel` return — legal in Java, preserves the existing API.

> **Note on combinator return type**: `and/or/negate` return `EntityCondition<C>`, not the subtype (e.g. not `ExchangeCondition`). In practice this is never a problem — conditions are always terminal expressions passed to a rule builder, never chained further. If a feature-specific method ever needs to be callable post-composition, promote it to `EntityConditions`.

---

## Architecture — Part 2: ConditionalAppearanceFeature

### Package layout for appearance types

```
net.heriazone.hzlib.api.entity.features
├── ConditionalAppearanceFeature    ← the feature, attached to NativeEntityFamily
├── AppearanceRule                  ← one condition + one outcome, immutable
├── AppearanceCondition             ← extends EntityCondition<AppearanceContext>
├── AppearanceConditions            ← delegates shared 9 to EntityConditions, adds item/spawn/interaction
├── AppearanceContext               ← extends EntityContext, adds player, heldItem, spawnReason
└── WeightedAppearancePool          ← randomised outcome within one rule
```

`BiomeAppearanceFeature.java` is deleted.


### `AppearanceCondition`

```java
@FunctionalInterface
public interface AppearanceCondition extends EntityCondition<AppearanceContext> {
    // inherits test(), and(), or(), negate() from EntityCondition<AppearanceContext>
} // Interface: AppearanceCondition
```

### `AppearanceContext`

Two static factory methods — one for spawn-time, one for interaction-time. Both produce the same type so conditions need no knowledge of which trigger fired.

```java
public final class AppearanceContext extends EntityContext {

    @Nullable private final MobSpawnType spawnReason;
    @Nullable private final Player       player;
    private final ItemStack              heldItem;   // EMPTY on spawn, never null

    // -- Factory methods --

    public static AppearanceContext forSpawn(ServerLevelAccessor world, BlockPos pos, MobSpawnType reason) { ... }
    public static AppearanceContext forInteraction(Level level, BlockPos pos, Player player, ItemStack held) { ... }

    // -- Appearance-specific accessors --

    public MobSpawnType        getSpawnReason() { return spawnReason; }   // nullable
    public Optional<Player>    getPlayer()      { return Optional.ofNullable(player); }
    public ItemStack           getHeldItem()    { return heldItem; }
    public boolean             isSpawn()        { return spawnReason != null; }
    public boolean             isInteraction()  { return player != null; }

    // getDimension / getBiome / getDayTime / isServerSide — inherited from EntityContext

} // Class: AppearanceContext
```

### `AppearanceConditions`

```java
public final class AppearanceConditions {

    private AppearanceConditions() {}

    // -- Delegated to EntityConditions (typed re-export) --
    public static AppearanceCondition inBiome(ResourceKey<Biome>... b) { return EntityConditions.inBiome(b)::test; }
    public static AppearanceCondition notInBiome(ResourceKey<Biome>... b) { return EntityConditions.notInBiome(b)::test; }
    public static AppearanceCondition inDimension(ResourceKey<Level>... d) { return EntityConditions.inDimension(d)::test; }
    public static AppearanceCondition isDaytime()            { return EntityConditions.isDaytime()::test; }
    public static AppearanceCondition isNighttime()          { return EntityConditions.isNighttime()::test; }
    public static AppearanceCondition inTimeRange(long f, long t) { return EntityConditions.inTimeRange(f, t)::test; }
    public static AppearanceCondition isRaining()            { return EntityConditions.isRaining()::test; }
    public static AppearanceCondition isThundering()         { return EntityConditions.isThundering()::test; }
    public static AppearanceCondition chance(float p)        { return EntityConditions.chance(p)::test; }
    public static AppearanceCondition always()               { return EntityConditions.always()::test; }

    // -- Appearance-specific --

    public static AppearanceCondition heldItem(Item... items) {
        Set<Item> set = Set.of(items);
        return ctx -> set.contains(ctx.getHeldItem().getItem());
    }

    public static AppearanceCondition heldItemTag(TagKey<Item> tag) {
        return ctx -> ctx.getHeldItem().is(tag);
    }

    public static AppearanceCondition onSpawn()        { return AppearanceContext::isSpawn; }
    public static AppearanceCondition onInteraction()  { return AppearanceContext::isInteraction; }

    public static AppearanceCondition spawnReason(MobSpawnType... reasons) {
        Set<MobSpawnType> set = Set.of(reasons);
        return ctx -> set.contains(ctx.getSpawnReason());
    }

    public static AppearanceCondition naturalSpawn() {
        return spawnReason(MobSpawnType.NATURAL, MobSpawnType.CHUNK_GENERATION);
    }

} // Class: AppearanceConditions
```

### `WeightedAppearancePool`

```java
public final class WeightedAppearancePool {

    public static WeightedAppearancePool of(String variantKey, float weight) { ... }
    public WeightedAppearancePool add(String variantKey, float weight) { ... }
    public String pick() { ... } // normalised weighted random, never null if pool non-empty

} // Class: WeightedAppearancePool
```

### `AppearanceRule`

```java
public final class AppearanceRule {

    private final AppearanceCondition    condition;
    private final WeightedAppearancePool pool;

    static AppearanceRule of(AppearanceCondition condition, String variantKey) {
        return new AppearanceRule(condition, WeightedAppearancePool.of(variantKey, 1f));
    }

    static AppearanceRule of(AppearanceCondition condition, WeightedAppearancePool pool) {
        return new AppearanceRule(condition, pool);
    }

    @Nullable
    public String evaluate(AppearanceContext ctx) {
        return condition.test(ctx) ? pool.pick() : null;
    }

} // Class: AppearanceRule
```

### `ConditionalAppearanceFeature`

```java
public final class ConditionalAppearanceFeature {

    private final List<AppearanceRule> rules;
    private final String               defaultVariantKey;

    @Nullable
    public String resolve(AppearanceContext ctx) {
        for (AppearanceRule rule : rules) {
            String key = rule.evaluate(ctx);
            if (key != null) return key;
        }
        return defaultVariantKey;
    }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final List<AppearanceRule> rules = new ArrayList<>();
        private String defaultVariantKey = null;

        public Builder when(AppearanceCondition condition, String variantKey) {
            rules.add(AppearanceRule.of(condition, variantKey)); return this;
        }
        public Builder when(AppearanceCondition condition, WeightedAppearancePool pool) {
            rules.add(AppearanceRule.of(condition, pool)); return this;
        }
        public Builder withDefault(String variantKey) {
            this.defaultVariantKey = variantKey; return this;
        }
        public ConditionalAppearanceFeature build() {
            return new ConditionalAppearanceFeature(List.copyOf(rules), defaultVariantKey);
        }
    }

} // Class: ConditionalAppearanceFeature
```


### Integration with `NativeEntity`

```java
// NativeEntity.initializeSpawnVariants — handles appearance automatically, no overrides needed
protected void initializeSpawnVariants(ServerLevelAccessor world, MobSpawnType reason) {
    nativeEntity.getFeature(ConditionalAppearanceFeature.class).ifPresentOrElse(feature -> {
        AppearanceContext ctx = AppearanceContext.forSpawn(world, blockPosition(), reason);
        String key = feature.resolve(ctx);
        if (key != null) {
            setTextureVariant(key);
            nativeEntity.getFeature(AppearanceVariantFeature.class).ifPresent(avf -> {
                var appearance = avf.getVariant(nativeEntity.getKey(), key);
                if (appearance != null) {
                    setModelVariant(appearance.getModelKey());
                    setAnimatorVariant(appearance.getAnimatorKey());
                }
            });
        } else {
            initializeRandomVariants();
        }
    }, this::initializeRandomVariants);
}
```

For **interaction-time** appearance (replaces dye logic in LovelyLib):
```java
private boolean tryConditionalAppearance(ItemStack heldItem, Player player) {
    return nativeEntity.getFeature(ConditionalAppearanceFeature.class).map(feature -> {
        AppearanceContext ctx = AppearanceContext.forInteraction(level(), blockPosition(), player, heldItem);
        String key = feature.resolve(ctx);
        if (key != null) { setTextureVariant(key); return true; }
        return false;
    }).orElse(false);
}
```

---

## Complete Package Layout (Final State)

```
net.heriazone.hzlib.api.entity.conditions           ← NEW
├── EntityCondition<C>     @FunctionalInterface — and/or/negate defined once
├── EntityContext          abstract — entity, level, pos, biome, dimension, dayTime, isServerSide
└── EntityConditions       shared factory — inBiome, inDimension, time, weather, chance, always/never

net.heriazone.hzlib.api.entity.features.exchange    ← MIGRATED (call sites unchanged)
├── ExchangeCondition      extends EntityCondition<ExchangeContext>  (empty body)
├── ExchangeContext        extends EntityContext  — adds: player, isOwnedByPlayer
└── ExchangeConditions     delegates shared 9 + owns: ownerOnly, anyPlayer, entityInState

net.heriazone.hzlib.api.entity.features.emanation   ← MIGRATED (call sites unchanged)
├── EmanationCondition     extends EntityCondition<EmanationContext>  (empty body)
├── EmanationContext       extends EntityContext  — adds: target, attacker, giver, gift, trigger
└── EmanationConditions    delegates shared 9 + owns: targetIsUndead, isTamed, healthAtMost, etc.

net.heriazone.hzlib.api.entity.features              ← NEW (appearance types)
├── AppearanceCondition    extends EntityCondition<AppearanceContext>  (empty body)
├── AppearanceContext      extends EntityContext  — adds: player, heldItem, spawnReason, isSpawn, isInteraction
├── AppearanceConditions   delegates shared 9 + owns: heldItem, heldItemTag, onSpawn, onInteraction, spawnReason
├── AppearanceRule         condition + WeightedAppearancePool pair
├── WeightedAppearancePool weighted random key selection
└── ConditionalAppearanceFeature  rule list + default + resolve() + builder

net.heriazone.hzlib.api.rendering                    ← UNCHANGED
└── RenderConditions       Predicate<T extends NativeEntity> — client-side, out of scope
```

---

## Declaration Examples

### Replace `BiomeAppearanceFeature` (Monsters & Girls mushroom family)

```java
// Before
withFeature(BiomeAppearanceFeature.class, BiomeAppearanceFeature.builder()
    .withMapping("mushroom_brown_ruby",       Biomes.TAIGA, Biomes.SNOWY_TAIGA)
    .withMapping("mushroom_brown_scarlatina", Biomes.DARK_FOREST)
    .withDefault("mushroom_brown_boletus")
    .build())

// After — one-to-one replacement
withFeature(ConditionalAppearanceFeature.class, ConditionalAppearanceFeature.builder()
    .when(AppearanceConditions.inBiome(Biomes.TAIGA, Biomes.SNOWY_TAIGA), "mushroom_brown_ruby")
    .when(AppearanceConditions.inBiome(Biomes.DARK_FOREST),               "mushroom_brown_scarlatina")
    .withDefault("mushroom_brown_boletus")
    .build())
```

### Replace dye logic (LovelyLib RobotFamily)

```java
withFeature(ConditionalAppearanceFeature.class, ConditionalAppearanceFeature.builder()
    .when(AppearanceConditions.heldItem(Items.WHITE_DYE),      "bunny_white")
    .when(AppearanceConditions.heldItem(Items.ORANGE_DYE),     "bunny_orange")
    .when(AppearanceConditions.heldItem(Items.MAGENTA_DYE),    "bunny_magenta")
    // ... 13 more dyes
    .withDefault("bunny_white")
    .build())
```

### Biome + item combined (composable conditions)

```java
withFeature(ConditionalAppearanceFeature.class, ConditionalAppearanceFeature.builder()
    .when(AppearanceConditions.onSpawn().and(AppearanceConditions.inBiome(Biomes.SNOWY_PLAINS)), "kitsune_arctic")
    .when(AppearanceConditions.onSpawn().and(AppearanceConditions.inBiome(Biomes.DESERT)),       "kitsune_sand")
    .when(AppearanceConditions.onInteraction().and(AppearanceConditions.heldItemTag(DyeTags.DYES)), buildDyePool())
    .withDefault("kitsune_default")
    .build())
```

### Weighted random within a condition

```java
.when(AppearanceConditions.inBiome(Biomes.PLAINS),
      WeightedAppearancePool.of("wisp_girl_blue", 0.6f).add("wisp_girl_yellow", 0.4f))
```

---

## Migration from `BiomeAppearanceFeature`

Zero family call sites. The migration is:
1. Delete `BiomeAppearanceFeature.java`
2. Update one-line javadoc reference in `AnimationStateManager`
3. Update one-line javadoc reference in `NativeEntity`

---

## Consequences

### Positive
- **Combinators defined once** — `and/or/negate` in `EntityCondition<C>`. Every current and future condition type inherits for free.
- **9 shared factory methods defined once** — in `EntityConditions`. Feature factories become thin wrappers + domain-specific methods.
- **4 shared context accessors defined once** — in `EntityContext`. Zero reimplementation per feature.
- **New condition systems cost almost nothing** — empty interface + context subclass + factory with only domain methods.
- **One feature covers all appearance selection** — biome, item, dimension, time, weather, composable. No proliferation.
- **Zero entity-class overrides for appearance** — base class handles everything via `ConditionalAppearanceFeature`.
- **Call sites unchanged** — all existing `ExchangeConditions.*` and `EmanationConditions.*` calls compile without modification.

### Trade-offs
- **`and/or/negate` return `EntityCondition<C>`, not the subtype** — result of composition is `EntityCondition<ExchangeContext>`, not `ExchangeCondition`. In practice this is never a problem — conditions are always terminal expressions. If a feature-specific method ever needs to be callable after composition, promote it to `EntityConditions`.
- **`EmanationContext` uses `ServerLevel`, `EntityContext` uses `Level`** — resolved via covariant override of `getLevel()` in `EmanationContext`. Legal Java, preserves existing API.
- **`AppearanceContext` carries nullable fields** — `player` is null at spawn, `spawnReason` is null at interaction. Guarded via `Optional` accessors and `isSpawn()`/`isInteraction()` guards on conditions.

### Risks
- **Condition evaluation order matters** — declare most-specific rules first, `withDefault` last. Documented on the builder.
- **No call site changes needed for existing code** — this is a pure internal refactor for exchange/emanation. No behaviour changes.

---

## Implementation Plan (ordered)

**Step 1 — Base framework (HZLib, new package)**
1. Create `EntityCondition<C>` interface
2. Create `EntityContext` abstract class
3. Create `EntityConditions` factory

**Step 2 — Migrate existing feature types (HZLib, existing files)**
4. `ExchangeCondition` → extend `EntityCondition<ExchangeContext>`, delete `and/or/negate` body
5. `ExchangeContext` → extend `EntityContext`, delete 4 shared methods, call `super(entity)`
6. `ExchangeConditions` → replace 9 method bodies with delegation wrappers
7. Repeat steps 4–6 for `Emanation*` equivalents
8. Build — verify zero errors across all modules

**Step 3 — Appearance feature (HZLib, new classes)**
9. `AppearanceCondition` — extends `EntityCondition<AppearanceContext>` (empty body)
10. `AppearanceContext` — extends `EntityContext`, adds `player`, `heldItem`, `spawnReason`, two factory methods
11. `AppearanceConditions` — delegates shared 9 + adds `heldItem`, `heldItemTag`, `onSpawn`, `onInteraction`
12. `WeightedAppearancePool`
13. `AppearanceRule`
14. `ConditionalAppearanceFeature` + builder
15. Delete `BiomeAppearanceFeature.java`
16. Update `NativeEntity.initializeSpawnVariants`
17. Update two javadoc comments

**Step 4 — LovelyLib (dye logic replacement)**
18. Add `ConditionalAppearanceFeature` to each `RobotFamily` with 16 dye mappings
19. Replace hardcoded dye chain in `RobotEntity.handleItemInteraction` with `tryConditionalAppearance()`

**Step 5 — Monsters & Girls (biome families)**
20. Add `ConditionalAppearanceFeature` to any family using biome selection via overrides
21. Remove override methods

---

## Related Documents
- `docs/development/notes/Pre_Publish_Checklist_Notes.md` — items 1 and 9 (both resolved)
- `sources/common/hzlib-1.21.1/.../features/exchange/ExchangeCondition.java` — migrated in step 2
- `sources/common/hzlib-1.21.1/.../features/exchange/ExchangeContext.java` — migrated in step 2
- `sources/common/hzlib-1.21.1/.../features/exchange/ExchangeConditions.java` — migrated in step 2
- `sources/common/hzlib-1.21.1/.../features/emanation/EmanationCondition.java` — migrated in step 2
- `sources/common/hzlib-1.21.1/.../features/emanation/EmanationContext.java` — migrated in step 2
- `sources/common/hzlib-1.21.1/.../features/emanation/EmanationConditions.java` — migrated in step 2
- `sources/common/hzlib-1.21.1/.../features/BiomeAppearanceFeature.java` — deleted in step 3
