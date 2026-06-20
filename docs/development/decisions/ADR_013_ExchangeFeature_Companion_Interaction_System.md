# ADR 013: ExchangeFeature — Companion Interaction System

**Status**: Accepted  
**Date**: 2026-06-12  
**Decision Makers**: Serge  
**Consulted**: Design session — Mushroom Gals bowl interaction, HZLib ecosystem design  

---

## Context

### The immediate problem

The Mushroom Gals in Monsters & Girls need a bowl interaction: right-clicking a tamed mushroom gal with an empty bowl gives a stew. The stew type depends on the family, and for Amanita it can also depend on the biome the player is standing in (flower biome → suspicious stew, anywhere else → mushroom stew).

A naive implementation — `BowlFeature` holding a stew item reference — would solve the immediate case. But this interaction is one instance of a broader pattern.

### The broader pattern

Every companion-style entity in the Heria Zone ecosystem is eventually going to have an interaction where the player gives something and receives something back. A robot might cook food. A companion might brew a potion. A creature might transform an ingredient. The mushroom gal's bowl is just the first concrete example.

The core concept is **mutual exchange**: the relationship feels real when both sides give something. The player gives an empty vessel; the companion gives back something only she can produce, rooted in her own nature. This idea recurs across every companion mod the ecosystem will ship.

### Why a generic feature in HZLib, not a mod-specific class

If this is implemented as a `BowlFeature` in `monsters_girls`, every other mod that needs the same pattern reimplements it from scratch. More importantly, there is no shared vocabulary for the concept. The goal is to establish that vocabulary once, in HZLib, so every mod downstream inherits it.

---

## Decision

Implement `ExchangeFeature` as a first-class feature in HZLib's entity feature system, at the same level as `FoodFeature`, `PlantingFeature`, and `SpawnFeature`.

`ExchangeFeature` lives at `net.heriazone.hzlib.api.entity.features.exchange` and is available to any entity in any HZLib-based mod.

---

## Design

### The fundamental model

An exchange has three parts:

```
inputs  (ordered sequence of items)
  + condition  (optional — world state gate)
  = outputs    (one or more generated item stacks)
```

Multiple exchanges are declared as **rules** on one feature. Rules are evaluated in declaration order. The first rule whose input sequence is complete and whose condition passes fires. Rules placed earlier act as more-specific overrides; rules placed later act as fallbacks.

### Why inputs are a sequence, not a set

Order matters. `[BOWL, ORCHID]` is a different exchange than `[ORCHID, BOWL]`. The player builds the sequence across multiple interactions, one item per right-click. The entity maintains a per-player buffer that grows until a rule matches or a timeout clears it.

This opens the door to multi-step rituals without any special coding — you just declare the sequence in the rule.

### Why outputs are `Supplier<ItemStack>`, not `Item`

Outputs are evaluated at give-time, not registration time. This is what allows the Amanita Gal to give a `SuspiciousStew` with a randomly chosen flower effect applied via the 1.21 `DataComponents.SUSPICIOUS_STEW_EFFECTS` component. A static `Item` reference cannot carry this. A supplier generates the full stack on demand.

Multiple outputs per rule are supported — the player receives all of them.

### Why conditions are a functional interface

Conditions are `@FunctionalInterface` so they compose naturally:

```java
ExchangeConditions.ownerOnly()
    .and(ExchangeConditions.inBiome(Biomes.FLOWER_FOREST))
```

Built-in factories cover the common cases: `ownerOnly()`, `inBiome()`, `inDimension()`, `isDaytime()`, `isRaining()`, `chance(float)`, `entityInState()`. Custom conditions are one lambda.

### Cooldown is per-rule, not per-entity

Each rule has its own cooldown tracked independently. This allows an entity with two exchange rules — say, a daily stew and an instant snack — to have different cooldowns without them interfering. Cooldown expiry is stored as an absolute game tick on the entity instance, not a countdown, so it survives server restarts correctly via NBT.

### Owner-only is a shorthand, not a special concept

`.ownerOnly()` on the feature builder injects `ExchangeConditions.ownerOnly()` AND-ed onto every rule's condition at construction time. It's syntactic sugar. There is nothing special about it mechanically — it is just a condition like any other.

### Global vs. local feedback

An `ExchangeFeedback` can be declared at two scopes:

- **Global** — applies to any rule that does not declare its own feedback
- **Per-rule** — overrides global for that specific rule only

This avoids repeating the same feedback on every rule while still allowing specific rules to have distinct sounds, particles, or animations.

`ExchangeFeedback` delegates particle effects to `InternalParticle` for consistency. Sounds are played via the level's sound system. Animation keys are stored as strings and read by the entity after a successful exchange — the feature layer has no GeckoLib dependency.

### Item delivery — the creative mode rule

Outputs are added to the player's inventory with `Inventory.add()`. If the inventory is full, the item drops at the entity's feet.

**Creative mode always drops.** Vanilla's creative inventory silently destroys items added to it via `Inventory.add()` without any error — it appears to succeed but the item is gone. This is a known engine issue already observed in LovelyRobot's core drop mechanics. The `ExchangeFeature` forces a drop for all creative-mode players regardless of inventory state.

### Runtime state is separate from feature declaration

`ExchangeFeature` is stateless. It is declared once on the entity type (e.g., `MushroomType.AMANITA`) and shared across all entity instances of that type.

`ExchangeState` is the mutable per-instance counterpart: it holds per-player sequence buffers and per-rule cooldown expiry ticks. One `ExchangeState` lives on every `InternalEntity` instance (always, even if no `ExchangeFeature` is registered — it costs nothing at rest). Cooldowns are persisted via NBT; sequence buffers are intentionally not persisted (they represent in-progress player interactions that have no meaning after a server restart).

---

## File Structure

All classes live in HZLib Common:

```
net.heriazone.hzlib.api.entity.features.exchange/
  ExchangeContext.java      — immutable world-state snapshot passed to conditions
  ExchangeCondition.java    — @FunctionalInterface, composes with .and() / .or() / .negate()
  ExchangeConditions.java   — static factory: ownerOnly, inBiome, inDimension, chance, ...
  ExchangeFeedback.java     — sound + particle + animation config, global/per-rule
  ExchangeRule.java         — one rule: inputs, condition, outputs, cooldown, feedback
  ExchangeFeature.java      — the feature: holds rules, tryExchange(), item delivery
  ExchangeState.java        — per-entity runtime: sequence buffers, cooldown expiry, NBT
```

---

## Integration Points

### InternalEntity

`ExchangeState exchangeState` is declared as a `final` field on `InternalEntity` — always present, zero cost at rest.

`handleCommonInteractions()` in `InternalEntity` is `final` and runs two things in order:
1. Exchange feature (`handleExchangeInteraction`)
2. Taming food (`onCommonInteraction` hook, which calls `FoodFeature.attemptTame`)

The `final` keyword is the structural guarantee — it is physically impossible for a subclass to bypass the exchange. Subclasses add common interaction logic by overriding `onCommonInteraction()`, not `handleCommonInteractions()`.

`addAdditionalSaveData()` and `readAdditionalSaveData()` persist the `ExchangeState` cooldowns under the key `"ExchangeState"`.

### MonsterEntity

`MonsterEntity` has **no** `onCommonInteraction` override. Taming food is handled in `InternalEntity.onCommonInteraction` directly. `MonsterEntity.handleSpecificInteractions` handles belly, state, and preference interactions.

### RobotEntity

`RobotEntity.handleSpecificInteractions` handles the robot command dispatch. It no longer calls `handleCommonInteractions` internally — that was a bug (double-call) that has been fixed. The `final` `handleCommonInteractions` in `InternalEntity.mobInteract` is the only call site.

### Entity type registration

Exchange rules are declared in the entity type class using `withFeature()`:

```java
.withFeature(ExchangeFeature.class, ExchangeFeature.builder()
    .ownerOnly()
    .globalFeedback(...)
    .rule(ExchangeRule.builder()
        .inputs(Items.BOWL)
        .condition(ExchangeConditions.inBiome(Biomes.FLOWER_FOREST))
        .output(() -> MushroomType.buildSuspiciousStew())
        .cooldown(6000)
        .build())
    .rule(ExchangeRule.builder()
        .inputs(Items.BOWL)
        .output(() -> new ItemStack(Items.MUSHROOM_STEW))
        .cooldown(6000)
        .build())
    .build())
```

---

## Known Issues Fixed During Implementation

### Bug: MonsterEntity not calling super

`MonsterEntity.handleCommonInteractions()` was overriding the base class method without calling `super`. This meant the `ExchangeFeature` wiring in `InternalEntity` was never reached. The bowl was being passed to `handleSpecificInteractions()`, which returned `PASS`, and Minecraft's default interaction handling left the entity in a bad state (entity hung, no exchange fired).

**Fix**: `handleCommonInteractions` was made `final` in `InternalEntity`. Subclasses now override `onCommonInteraction()` instead. The exchange is structurally guaranteed to run — it cannot be bypassed by any subclass.

### Bug: RobotEntity calling handleCommonInteractions twice

`RobotEntity.handleSpecificInteractions()` had an internal call to `handleCommonInteractions()` before delegating to `handleInteract()`. The comment said *"Let common interactions run first"*, but `handleCommonInteractions` is already called by `mobInteract` before `handleSpecificInteractions` is reached. This caused the exchange to run twice on every owned-robot interaction.

Before the `ExchangeFeature` existed this was harmless. Once a robot had an `ExchangeFeature`, it would evaluate twice and potentially fire twice or consume items twice.

**Fix**: The internal call was removed from `RobotEntity.handleSpecificInteractions()`. `handleInteract()` is now called directly after the dye check.

### Bug: Cooldown fallthrough consumed the item and left a hanging buffer

When a single-item rule was on cooldown, the original `tryExchange()` fallthrough code consumed the bowl and returned `CONSUME` while leaving `[BOWL]` in the player's sequence buffer. The next interaction added a second BOWL → buffer `[BOWL, BOWL]` → matched nothing → reset. This produced a two-tap delay illusion.

**Fix**: The fallthrough now distinguishes between cooldown-blocked (clear buffer, return `PASS`, don't consume) and partial multi-item sequence (consume, return `CONSUME`).

---

## The Amanita Gal — First Implementation

The Amanita mushroom gal was the first entity to use `ExchangeFeature`. Her bowl interaction demonstrates the biome-conditional output pattern:

```java
// Flower biome → suspicious stew with random flower effect
.rule(ExchangeRule.builder()
    .inputs(Items.BOWL)
    .condition(ExchangeConditions.inBiome(
        Biomes.FLOWER_FOREST, Biomes.SUNFLOWER_PLAINS, Biomes.CHERRY_GROVE))
    .output(() -> MushroomType.buildSuspiciousStew())
    .cooldown(6000)
    .build())

// Anywhere else → plain mushroom stew
.rule(ExchangeRule.builder()
    .inputs(Items.BOWL)
    .output(() -> new ItemStack(Items.MUSHROOM_STEW))
    .cooldown(6000)
    .build())
```

`buildSuspiciousStew()` is a static factory on `MushroomType` that picks one of 16 flower effects at random using `ThreadLocalRandom`, mirrors the complete Java Edition flower→effect table from the wiki, and writes the effect via `DataComponents.SUSPICIOUS_STEW_EFFECTS` (the 1.21 data-component API — not NBT tags).

---

## Consequences

### Positive

- Single implementation for give-and-receive interactions — every HZLib mod inherits it via the feature system
- Stateless declaration, stateful runtime — entity type definitions stay clean; per-instance state is managed automatically
- Sequence support opens multi-step ritual interactions without any special coding
- Biome, dimension, time, weather, entity state, and probability conditions are built in
- Creative mode item delivery bug is handled correctly at the framework level, not per-entity

### Negative

- Every `MonsterEntity` subclass (and any other `InternalEntity` subclass) that overrides `handleCommonInteractions()` must remember to call `super` — this is not enforced by the compiler
- Sequence buffers are per-player per-entity instance, which means memory usage scales with concurrent players interacting with many entities simultaneously (acceptable in practice given short buffer lifetimes and automatic expiry)

### Risks

- **Super call discipline**: The most likely future bug is a new entity subclass overriding `handleCommonInteractions()` without calling `super`. This should be caught in code review. Consider adding a `@MustCallSuper` annotation if the project adopts it.
- **Cooldown on all matching rules**: If a player has two rules that match the same input sequence (one conditioned, one not), both may be on cooldown simultaneously. The fallback rule does not fire even though the input is valid. This is correct behavior but may be surprising — document it clearly when declaring rules.

---

## Related Decisions

- ADR_009: Entity Hierarchy Refactoring — established the `InternalEntity` → `MonsterEntity` hierarchy this feature integrates into
- ADR_012: InternalEntity Consolidation — established `InternalEntity` as the single shared base; `ExchangeState` lives there as a result
- ADR_011: Variant and Spawn System Refactoring — established the `withFeature()` / `getFeature()` pattern this feature uses
