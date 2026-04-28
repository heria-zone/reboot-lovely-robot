# ADR 010: Animation Profile System

**Status**: Accepted  
**Date**: 2026-04-27  
**Decision Makers**: Mike, Raul, Serge  
**Consulted**: Architecture Review (27-04-2026 Entity Architecture & Variant System Discussion)

## Context

The current animation system in HZLib/LovelyLib hardcodes animation names as constants in `AnimationDefinitions` and maps them to `RawAnimation` objects in `InternalAnimation` via a switch statement. This works for robots because all 7 robot types share one animation file (`default.animation.json`) with the same five animation names (`idle`, `walk`, `rest`, `sit`, `attack`).

This approach breaks down when supporting multiple entity families with different animation sets:

- **Robots**: `idle`, `walk`, `rest`, `sit`, `attack` — all states present, single shared file
- **Gourdragora default**: `idle`, `idle1`, `walk`, `attack`, `hurt`, `ride`, `pose` — no `rest`, has `ride`, has a base layer
- **Gourdragora mini**: `idle`, `walk`, `attack`, `hurt`, `ride` — no `rest`, no `idle1`, no `pose`
- **Bee**: `idle`, `rest` only — no `walk`, no `attack`
- **Wisp**: `idle`, `idle2`, `walk`, `rest`, `attack`, `yipee`, `ride` — has variety pool and entity-specific special
- **Slime**: `idle`, `idle2`, `walk`, `rest`, `ride` — no `attack`

Additionally, the Dragon robot requires a multi-phase sequenced attack (Dragon's Fury) with conditional loops — a pattern that cannot be expressed as a single animation name.

The `AnimationDefinitions` class uses Java reflection to create `RawAnimation` objects from Common without a GeckoLib dependency. This is fragile and hard to debug.

## Decision

We will implement an **Animation Profile System** in HZLib Common that:

1. Replaces hardcoded animation name constants with per-animator-variant configuration
2. Supports animation pools (multiple animations per state, with selection strategies)
3. Supports animation sequences (multi-phase chains with conditional loops)
4. Keeps all GeckoLib construction in loader-specific modules
5. Eliminates the reflection-based `AnimationDefinitions` class

## Architecture

### Core Data Model

```
AnimationProfile                          [HZLib Common]
├── Locomotion slots (AnimationPool per slot)
│   ├── idle     — always present
│   ├── walk     — null for Bee (falls back to idle)
│   ├── rest     — null for Gourdragora (falls back to idle)
│   ├── sit      — robots only (vehicle/standby sitting)
│   ├── ride     — monsters only (mount riding)
│   ├── attack   — null for Bee, Slime
│   └── hurt     — null for robots, Bee, Slime
├── basePoseAnimation (String, optional parallel GeckoLib layer)
└── specialAnimations: Map<String, ISpecialAnimation>
    ├── AnimationPool    — simple interaction animations (wave, yipee)
    └── AnimationSequence — complex multi-phase abilities (Dragon's Fury)
```

### AnimationPool

```java
public class AnimationPool {
    private final List<WeightedAnimation> animations;
    private final SelectionStrategy strategy;

    public String selectNext(Random random, int currentIndex) { ... }
    public boolean isEmpty() { ... }
}

public class WeightedAnimation {
    private final String name;
    private final int weight;        // relative weight for WEIGHTED_RANDOM
    private final LoopBehavior loop;
}

public enum SelectionStrategy {
    RANDOM,           // uniform random selection on each state entry
    WEIGHTED_RANDOM,  // weighted random — higher weight = more frequent
    SEQUENTIAL        // cycle through in order, advancing each state entry
}

public enum LoopBehavior {
    LOOP,               // standard looping animation
    PLAY_ONCE,          // plays once, then stops
    HOLD_LAST_FRAME,    // plays once, freezes on last frame
    INTERRUPT,          // plays once, overrides all other GeckoLib controllers
    LOOP_TIMED,         // loops for N ticks, then advances (sequences only)
    LOOP_UNTIL_SIGNAL   // loops until Predicate<LivingEntity> returns true (sequences only)
}
```

### AnimationSequence

```java
public class AnimationSequence implements ISpecialAnimation {
    private final List<SequenceStep> steps;
    private final String returnToState; // locomotion state to resume after completion

    public static class SequenceStep {
        private final String animationName;
        private final LoopBehavior loopBehavior;
        private final int loopDurationTicks;                  // for LOOP_TIMED
        private final Predicate<LivingEntity> exitCondition;  // for LOOP_UNTIL_SIGNAL
    }
}
```

**Pull model for exit conditions**: `LOOP_UNTIL_SIGNAL` steps carry a `Predicate<LivingEntity>` evaluated by the animation controller each tick. When the predicate returns true, the sequence advances. The AI goal does not need to know about animation state — it simply performs its normal behavior (approaching a target, charging an attack), and the animation system observes the world state independently.

**Why pull over push**: Push requires the AI goal to call `entity.advanceSequence(signal)`, coupling game logic to animation state. Pull keeps the sequence self-contained and testable in isolation.

### Per-Entity Runtime State

Sequences require per-entity state that lives on the entity, not in the profile:

```java
// In InternalEntity (or RobotEntity):
private SequenceState activeSequence = null;  // null = no sequence running
private int currentStepIndex = 0;
private int stepTickCounter = 0;

public void startSequence(String sequenceName) { ... }
public boolean isSequenceRunning() { ... }
```

The locomotion controller checks `isSequenceRunning()` first on every tick. If a sequence is active, it delegates to the sequence controller. When the sequence completes, `returnToState` determines which locomotion state resumes.

### Locomotion Priority Chain

```
isSequenceRunning()?     → sequence controller (overrides everything)
isInVehicle()?           → ride ?? sit ?? idle
isAttacking()?           → attack ?? idle
isMoving()?              → walk ?? idle
isInStandby(sitting)?    → sit ?? rest ?? idle
isInStandby(resting)?    → rest ?? idle
default                  → pick from idle pool (random/weighted)
```

The `??` operator means "if null/empty, fall back to next option." This handles Bee (no walk → idle when moving), Slime (no attack → no attack animation), and robots (no ride → sit when in vehicle).

### AnimatorVariantFeature Extension

The `AnimatorVariantFeature` is extended to carry an `AnimationProfile` alongside the animator file path:

```java
VariantRegistries.ANIMATORS.register(
    new StandardAnimatorVariant(
        "gourdragora_girl_default",
        "Gourdragora Default Animations",
        getAnimatorResource("gourdragora_girl_default"),
        AnimationProfile.builder()
            .idle(pool -> pool.add("idle", 80).add("idle1", 20).strategy(WEIGHTED_RANDOM))
            .walk("walk")
            .attack("attack")
            .hurt("hurt")
            .ride("ride")
            .basePose("pose")
            .build(),
        priority
    )
);
```

### GeckoLib Boundary

`AnimationProfile` and all related classes live in **HZLib Common** and return `String` names only. No GeckoLib imports in Common.

The loader-specific `InternalAnimation` classes:
- Read the profile from the entity's current `AnimatorVariantFeature`
- Call `pool.selectNext()` to get an animation name string
- Construct `RawAnimation` objects from those strings
- Apply `LoopBehavior.INTERRUPT` as GeckoLib's `override_previous_animation: true`
- Create the base pose controller if `basePoseAnimation` is non-null

This eliminates the reflection-based `AnimationDefinitions` class entirely.

## Reference Implementations

### Robot Profile (Baseline — Single-Entry Pools)

```java
AnimationProfile.builder()
    .idle("idle")
    .walk("walk")
    .rest("rest")
    .sit("sit")
    .attack(pool -> pool.add("attack", LoopBehavior.INTERRUPT))
    .build()
```

### Dragon's Fury Sequence

```java
AnimationSequence dragonFury = AnimationSequence.builder()
    .step("attack_prepare",  LoopBehavior.PLAY_ONCE)
    .step("attack_charge",   LoopBehavior.LOOP_TIMED, 40)
    .step("attack_approach", LoopBehavior.LOOP_UNTIL_SIGNAL,
        entity -> entity.getTarget() != null
               && entity.distanceTo(entity.getTarget()) <= 3.0)
    .step("attack_strike",   LoopBehavior.PLAY_ONCE)
    .step("attack_fury",     LoopBehavior.PLAY_ONCE)
    .returnTo("idle")
    .build();

// Dragon profile
AnimationProfile.builder()
    .idle("idle")
    .walk("walk")
    .rest("rest")
    .sit("sit")
    .attack(pool -> pool.add("attack", LoopBehavior.INTERRUPT))
    .special("fury_attack", dragonFury)
    .build()
```

### Gourdragora Default Profile (Variety Pool + Base Layer)

```java
AnimationProfile.builder()
    .idle(pool -> pool.add("idle", 80).add("idle1", 20).strategy(WEIGHTED_RANDOM))
    .walk("walk")
    .attack("attack")
    .hurt("hurt")
    .ride("ride")
    .basePose("pose")
    .build()
```

### Bee Profile (Missing States Fall Back to Idle)

```java
AnimationProfile.builder()
    .idle("idle")
    .rest("rest")
    // walk, attack, hurt, ride — all null, fall back to idle
    .build()
```

## Consequences

### Positive

- **No hardcoded animation names** — each animator variant declares its own animation set
- **Graceful degradation** — missing states fall back to idle rather than silent failure
- **Variety support** — any state can have multiple animations with weighted selection
- **Complex sequences** — Dragon's Fury and future multi-phase abilities are first-class
- **Clean GeckoLib boundary** — Common module has zero GeckoLib dependency
- **Asset contract** — animators know exactly what animations to author per entity type
- **Eliminates reflection** — `AnimationDefinitions` class deleted

### Negative

- **`AnimationSequence` has Minecraft dependency** — `Predicate<LivingEntity>` requires Minecraft types. Acceptable since HZLib Common already imports Minecraft classes extensively.
- **`SEQUENTIAL` strategy requires per-entity state** — the current index must be tracked per entity instance, not in the pool definition. This is a small addition to entity state.
- **Migration cost** — existing `AnimationStateManager` in lovelylib must be replaced. The robot profile is the reference implementation to validate the replacement before adding monster complexity.

### Risks

- **`LOOP_UNTIL_SIGNAL` predicate performance** — evaluated every tick while a sequence step is active. Predicates must be lightweight (distance checks, null checks). Complex world queries should be avoided.
- **Sequence interruption** — if an entity dies or is discarded mid-sequence, the sequence state must be cleaned up. `discard()` and death handlers need to call `clearSequence()`.

## Migration Plan

1. Implement `AnimationPool`, `WeightedAnimation`, `SelectionStrategy`, `LoopBehavior` in HZLib Common
2. Implement `AnimationProfile` with builder in HZLib Common
3. Implement `AnimationSequence` and `SequenceStep` in HZLib Common
4. Extend `AnimatorVariantFeature` to carry `AnimationProfile`
5. Replace `AnimationStateManager` in lovelylib with profile-aware version
6. Update `InternalAnimation` (all loaders) to read profiles and construct `RawAnimation` from strings
7. Delete `AnimationDefinitions`
8. Register robot profile as reference — validate all 7 robot types work correctly
9. Register monster profiles — validate Bee (missing states), Gourdragora (variety pool + base layer), Wisp (variety + special)
10. Implement Dragon's Fury sequence — validate `AnimationSequence` with `LOOP_UNTIL_SIGNAL`
    - **⚠ Blocked on assets**: The Dragon animation files (`attack_prepare`, `attack_charge`, `attack_approach`, `attack_strike`, `attack_fury`) do not exist yet. This step cannot be picked up until those assets are authored. Do not start this step and then stall waiting for files — it should be explicitly marked as asset-blocked in the sprint task.

## Related Decisions

- ADR_009: Entity Hierarchy Refactoring (three-tier hierarchy this system builds on)
- ADR_011: Variant and Spawn System Refactoring (companion ADR for `SizeVariantFeature` and `initializeSpawnVariants`)
