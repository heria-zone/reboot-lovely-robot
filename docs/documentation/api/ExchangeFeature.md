# ExchangeFeature — API Reference

**Module**: `net.heriazone.hzlib.api.entity.features.exchange`  
**Lives in**: HZLib Common  
**Status**: Active (introduced 2026-06-12)  
**See also**: ADR_013 for design rationale and architecture decisions

---

## What it is

`ExchangeFeature` lets any HZLib entity participate in item exchanges with a player: the player provides one or more items, the entity produces one or more items in return. The outputs can vary based on world context — biome, dimension, time of day, entity state, probability, or any custom predicate.

The bowl-to-stew interaction of the Mushroom Gals is the canonical example: give an empty bowl, receive a stew. In a flower biome the stew is a suspicious stew with a random flower effect; anywhere else it's a plain mushroom stew.

---

## Core concepts

### Rules

An `ExchangeRule` is one exchange mapping: inputs → outputs, guarded by an optional condition.

Rules are evaluated **in declaration order**. The first rule whose full input sequence is matched and whose condition passes fires. Put more-specific rules (with conditions) before less-specific ones (fallbacks).

### Inputs as a sequence

Inputs are an ordered list of items. For a single-item exchange the list has one entry. For multi-item sequences the player must give the items **one right-click at a time, in order**. The entity remembers partial progress per-player via a sequence buffer. Buffers expire automatically after ~5 seconds of inactivity (100 ticks).

Order matters: `[BOWL, ORCHID]` is a different rule than `[ORCHID, BOWL]`.

### Outputs as suppliers

Each output is a `Supplier<ItemStack>` evaluated at give-time. This means outputs can carry NBT data, randomized contents, or anything else computed on demand. Multiple output suppliers per rule are supported — the player receives all of them.

### Conditions

`ExchangeCondition` is a `@FunctionalInterface`. Conditions compose:

```java
ExchangeConditions.ownerOnly()
    .and(ExchangeConditions.inBiome(Biomes.FLOWER_FOREST))
    .and(ExchangeConditions.chance(0.5f))
```

### Cooldown

Per-rule, in ticks. Stored as absolute game tick on the entity so it survives server restarts. `0` means no cooldown. Each rule's cooldown is independent.

### Feedback

Sound, particle, and animation key. Can be declared globally (applies to all rules without their own) or per-rule (overrides global for that rule only).

---

## Classes

### `ExchangeFeature`

The top-level feature registered via `withFeature(ExchangeFeature.class, ...)` on an entity type.

| Method | Description |
|---|---|
| `builder()` | Returns a new `Builder` |
| `tryExchange(entity, player, stack, state)` | Main entry point — called from `InternalEntity`. Returns `SUCCESS`, `CONSUME` (partial sequence), or `PASS` |
| `getRules()` | All declared rules in evaluation order |
| `getGlobalFeedback()` | Global feedback, or null |
| `isOwnerOnly()` | Whether owner-only was injected |

**Builder methods:**

| Method | Description |
|---|---|
| `.ownerOnly()` | Injects `ownerOnly` condition AND-ed onto every rule |
| `.globalFeedback(ExchangeFeedback)` | Sets default feedback for rules without their own |
| `.rule(ExchangeRule)` | Adds a rule (call in priority order, most specific first) |
| `.build()` | Builds the immutable feature |

---

### `ExchangeRule`

One exchange mapping.

**Builder methods:**

| Method | Description |
|---|---|
| `.input(Item)` | Adds a single item to the input sequence |
| `.inputs(Item...)` | Adds multiple items in order |
| `.condition(ExchangeCondition)` | Sets the condition (replaces previous — compose with `.and()` / `.or()` instead) |
| `.output(Supplier<ItemStack>)` | Adds one output supplier |
| `.outputs(Supplier<ItemStack>...)` | Adds multiple output suppliers |
| `.cooldown(int ticks)` | Per-rule cooldown. `0` = no cooldown |
| `.feedback(ExchangeFeedback)` | Rule-specific feedback (overrides global) |
| `.build()` | Builds the rule. Throws if inputs or outputs are empty |

---

### `ExchangeConditions`

Static factory for common conditions.

| Factory | Passes when... |
|---|---|
| `ownerOnly()` | Entity is tamed and the interacting player is the owner |
| `anyPlayer()` | Always passes (explicit no-op counterpart to `ownerOnly`) |
| `inBiome(biomes...)` | Entity is in any of the listed biomes (server-side only) |
| `notInBiome(biomes...)` | Entity is NOT in any of the listed biomes |
| `inDimension(dimension)` | Interaction occurs in the specified dimension |
| `inDimension(dimensions...)` | Interaction occurs in any of the listed dimensions |
| `isDaytime()` | Day time ticks 0–11999 |
| `isNighttime()` | Day time ticks 12000–23999 |
| `inTimeRange(from, to)` | Day time is within the range (inclusive) |
| `entityInState(states...)` | Entity's current `EntityState` is in the list (requires `InternalEntity`) |
| `chance(float)` | Passes with the given probability on each evaluation (0.0–1.0) |
| `isRaining()` | Level is currently raining |
| `isThundering()` | Level is currently thundering |

All conditions compose via `.and()`, `.or()`, `.negate()`.

---

### `ExchangeContext`

Immutable snapshot passed to every condition during evaluation. Contains: entity, player, level, dimension key, optional biome key, day-time ticks, `isOwnedByPlayer()`, `isServerSide()`.

---

### `ExchangeFeedback`

Sound + particle + animation key config.

**Builder methods:**

| Method | Description |
|---|---|
| `.sound(SoundEvent)` | Sound at default volume (1.0) and pitch (1.0) |
| `.sound(SoundEvent, float volume, float pitch)` | Sound with custom volume and pitch |
| `.particle(ParticleType)` | Particle effect from `ExchangeFeedback.ParticleType` enum |
| `.animation(String key)` | GeckoLib animation key; entity reads and triggers it after a successful exchange |
| `.build()` | Builds the feedback config |

**`ParticleType` values** (delegate to `InternalParticle`):

| Value | Visual | Typical use |
|---|---|---|
| `HAPPY_VILLAGER` | Green sparkles | Positive exchange, gift received |
| `HEART` | Hearts | Bonding, affection interaction |
| `ASH` | Ash cloud | Dark or decay interaction |
| `POOF` | Poof cloud | Summoning, transformation |
| `SMOKE` | Smoke | Blocked, refused |

---

### `ExchangeState`

Per-entity runtime state. Lives on every `InternalEntity` as `exchangeState`. You do not create or manage this directly — `InternalEntity` handles it.

**What it tracks:**
- Per-player sequence buffers (cleared on match, timeout, or bad input)
- Per-rule cooldown expiry ticks (persisted to NBT under `"ExchangeState"`)

Sequence buffers expire after 100 ticks (~5 seconds) of player inactivity.

---

## Item delivery rules

1. Try `player.getInventory().add(stack)` — give to inventory if space available
2. If inventory full → drop at entity feet (with default pickup delay)
3. If player is in **creative mode** → always drop at entity feet, regardless of inventory state

Creative mode always drops because vanilla's `Inventory.add()` silently destroys items when the player is in creative — it appears to succeed but the item is gone. This is a known engine issue.

---

## How to add an exchange to an entity type

### Step 1 — Declare the feature on the entity type

```java
// In your entity type class (e.g. MushroomType.YOUR_TYPE)
.withFeature(ExchangeFeature.class, ExchangeFeature.builder()
    .ownerOnly()
    .globalFeedback(ExchangeFeedback.builder()
        .sound(SoundEvents.BUCKET_FILL)
        .particle(ExchangeFeedback.ParticleType.HAPPY_VILLAGER)
        .build())
    .rule(ExchangeRule.builder()
        .inputs(Items.BOWL)
        .output(() -> new ItemStack(Items.MUSHROOM_STEW))
        .cooldown(6000)
        .build())
    .build())
```

That's it. `InternalEntity.handleCommonInteractions()` picks it up automatically.

### Step 2 — No override needed for standard entities

`InternalEntity.handleCommonInteractions` is `final`. The exchange runs automatically before any other common interaction. There is nothing to override for standard entity classes.

If a subclass needs additional common interaction logic, override `onCommonInteraction()`:

```java
@Override
protected InteractionResult onCommonInteraction(Player player, InteractionHand hand, ItemStack stack) {
    // exchange has already run before this is called
    // taming food also runs here in the base implementation (super)
    return super.onCommonInteraction(player, hand, stack);
}
```

Standard entity classes like `MonsterEntity` and `RobotEntity` do not override `onCommonInteraction` at all — the base implementation handles taming via `FoodFeature` automatically.

---

## Common patterns

### Biome-conditional output (more specific rule first)

```java
ExchangeFeature.builder()
    .ownerOnly()
    // Flower biome — gives suspicious stew with random effect
    .rule(ExchangeRule.builder()
        .inputs(Items.BOWL)
        .condition(ExchangeConditions.inBiome(
            Biomes.FLOWER_FOREST, Biomes.SUNFLOWER_PLAINS, Biomes.CHERRY_GROVE))
        .output(() -> buildSuspiciousStew())
        .cooldown(6000)
        .build())
    // Anywhere else — gives plain mushroom stew
    .rule(ExchangeRule.builder()
        .inputs(Items.BOWL)
        .output(() -> new ItemStack(Items.MUSHROOM_STEW))
        .cooldown(6000)
        .build())
    .build()
```

### Nether-dimension exchange

```java
.rule(ExchangeRule.builder()
    .inputs(Items.BOWL)
    .condition(ExchangeConditions.inDimension(Level.NETHER))
    .output(() -> new ItemStack(MonstersItems.NETHER_STEW))
    .cooldown(6000)
    .build())
```

### Multi-item sequence

```java
// Player must give BOWL then BLUE_ORCHID in two separate right-clicks
.rule(ExchangeRule.builder()
    .inputs(Items.BOWL, Items.BLUE_ORCHID)
    .output(() -> buildSpecialStew())
    .cooldown(12000)
    .feedback(ExchangeFeedback.builder()
        .sound(SoundEvents.BOTTLE_FILL)
        .particle(ExchangeFeedback.ParticleType.HEART)
        .animation("special_give")
        .build())
    .build())
```

### Multiple outputs

```java
.rule(ExchangeRule.builder()
    .inputs(Items.BOWL)
    .output(() -> new ItemStack(Items.MUSHROOM_STEW))
    .output(() -> new ItemStack(Items.COOKIE, 2))   // player gets both
    .cooldown(6000)
    .build())
```

### Rare bonus (10% chance)

```java
.rule(ExchangeRule.builder()
    .inputs(Items.BOWL)
    .condition(ExchangeConditions.ownerOnly().and(ExchangeConditions.chance(0.1f)))
    .output(() -> buildRareSuspiciousStew())
    .cooldown(0)  // no cooldown on rare roll — the 10% acts as the gate
    .build())
// Always-fires fallback below this rule
.rule(ExchangeRule.builder()
    .inputs(Items.BOWL)
    .output(() -> new ItemStack(Items.MUSHROOM_STEW))
    .cooldown(6000)
    .build())
```

### No-owner-check exchange (any player can trigger)

```java
ExchangeFeature.builder()
    // no .ownerOnly() call
    .rule(ExchangeRule.builder()
        .inputs(Items.BOWL)
        .output(() -> new ItemStack(Items.MUSHROOM_STEW))
        .cooldown(200)
        .build())
    .build()
```

---

## Amanita Gal — `buildSuspiciousStew()`

The Amanita family uses a static factory on `MushroomType` that picks one of 16 vanilla flower effects at random and writes it to the stew using the 1.21 data-component API:

```java
public static ItemStack buildSuspiciousStew() {
    record Entry(Holder<MobEffect> effect, int durationTicks) {}

    Entry[] pool = {
        new Entry(MobEffects.FIRE_RESISTANCE,   80),   // Allium           (4s)
        new Entry(MobEffects.BLINDNESS,        160),   // Azure Bluet      (8s)
        new Entry(MobEffects.SATURATION,         7),   // Blue Orchid      (0.35s)
        new Entry(MobEffects.JUMP_BOOST,       120),   // Cornflower       (6s)
        new Entry(MobEffects.SATURATION,         7),   // Dandelion        (0.35s)
        new Entry(MobEffects.POISON,           240),   // Lily of the Valley (12s)
        new Entry(MobEffects.FIRE_RESISTANCE,    7),   // Open Eyeblossom  (0.35s)
        new Entry(MobEffects.NAUSEA,           140),   // Closed Eyeblossom (7s)
        new Entry(MobEffects.REGENERATION,     160),   // Oxeye Daisy      (8s)
        new Entry(MobEffects.NIGHT_VISION,     100),   // Poppy            (5s)
        new Entry(MobEffects.NIGHT_VISION,     100),   // Torchflower      (5s)
        new Entry(MobEffects.WEAKNESS,         180),   // Red Tulip        (9s)
        new Entry(MobEffects.WEAKNESS,         180),   // Orange Tulip     (9s)
        new Entry(MobEffects.WEAKNESS,         180),   // White Tulip      (9s)
        new Entry(MobEffects.WEAKNESS,         180),   // Pink Tulip       (9s)
        new Entry(MobEffects.WITHER,           160),   // Wither Rose      (8s)
    };

    Entry chosen = pool[ThreadLocalRandom.current().nextInt(pool.length)];

    ItemStack stew = new ItemStack(Items.SUSPICIOUS_STEW);
    stew.set(
        DataComponents.SUSPICIOUS_STEW_EFFECTS,
        new SuspiciousStewEffects(List.of(
            new SuspiciousStewEffects.Entry(chosen.effect(), chosen.durationTicks())
        ))
    );
    return stew;
}
```

All durations are Java Edition values from the official Minecraft wiki. The pool has 16 entries to match the 16 vanilla flowers — each flower is equally likely.

---

## What ExchangeFeature is NOT

- **Not a cooking system.** Transforming raw food into cooked food (Molten Gal) is a separate `CookingFeature` — the entity applies heat, the player gets back a transformed item. The exchange concept doesn't map cleanly to that because the "input" and "output" are the same item in different states.
- **Not a taming system.** Taming is handled by `FoodFeature` with its own probability model and `TamingFeedback`. See `FoodFeature.md` for the taming API.
- **Not a shop system.** There is no currency, no price list, no economy. The exchange is always free — the bowl is consumed but it is a vessel, not a price.
- **Only fires on tamed entities when `ownerOnly()` is set.** Exchange and taming are fully independent — an exchange rule could technically be declared without `ownerOnly()` and fire on an untamed entity, but the standard use case is owner-only.

---

## Interaction result semantics

| Result | Meaning |
|---|---|
| `SUCCESS` | Full exchange fired — stew produced, bowl consumed, feedback played |
| `CONSUME` | Item accepted as part of a multi-item sequence, waiting for next item |
| `PASS` | Item not recognized, on cooldown (silently), or entity not owned by player |

---

## See also

- `FoodFeature.md` — taming probability, `TamingFeedback`, item consumption on attempts
- ADR_013 — design rationale and architecture decisions for `ExchangeFeature`
- ADR_014 — design rationale for `FoodFeature` probabilistic taming
