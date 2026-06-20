# ADR 014: FoodFeature — Probabilistic Taming & Feedback System

**Status**: Accepted  
**Date**: 2026-06-12  
**Decision Makers**: Serge  
**Consulted**: Design session — taming feel, vanilla reference, FoodFeature evolution  

---

## Context

### The immediate problem

`FoodFeature` taming worked as a guaranteed, instant tame — hold the right item, right-click, done. No tension, no character to the interaction. This worked while the mod was in early infrastructure but was never the intended feel for companion entities.

The Mushroom Gals should feel like real companions. Taming should take a few attempts. The player should feel something when it fails and something different when it finally works. The mushroom gal should have a reaction either way.

### The vanilla reference

Wolf, cat, and parrot taming in vanilla Java Edition all share the same model:

- **1/3 chance per attempt** — matches wolf and cat exactly
- **One item consumed per attempt**, always, success or failure
- **Heart particles on success**, smoke particles on failure
- Independent attempts — no progress bar, no memory

This is the right baseline. It's familiar, fair, and creates genuine engagement. The question was whether to copy it or build something more flexible.

### Why not just hardcode 1/3

The current ecosystem has entities with different personalities. A cookie-loving companion might be easy to tame (high chance). A rare nether creature might resist for a long time (low chance). Rare treats might be more effective than common foods. These are design decisions that belong in the entity type definition, not in entity code.

Hardcoding 1/3 would make `FoodFeature` correct for exactly one case. A feature system should express intent, not just enable the most common case.

---

## Decision

Extend `FoodFeature` with:

1. **Probabilistic taming** — per-attempt chance, configurable globally or per-item
2. **`TamingFeedback`** — particle and optional sound on success and failure, as an inner class mirroring `ExchangeFeedback`'s structure
3. **`attemptTame()` method** — the single entry point for a taming attempt, owns the complete contract (consume item, roll chance, play feedback, return result)

The taming logic in `InternalEntity.onCommonInteraction` is simplified to just call `attemptTame()` and then `handleTame()` on success.

---

## Probability Model

### Global vs. per-food

Every food item is registered with either a per-item chance or the sentinel `USE_GLOBAL`:

```
withFoods(Items.COOKIE)              → Uses global chance (or vanilla default)
withFood(Items.COOKIE, 0.25f)        → 25% for this item specifically
withFood(Items.CAKE,   0.75f)        → 75% for this item specifically
withGlobalChance(0.5f)               → 50% for all foods without a per-item override
```

Resolution order per attempt:
1. Does this item have a per-item chance? → use it
2. Is a global chance configured? → use it  
3. Neither → use `VANILLA_CHANCE` (1/3)

### The `VANILLA_CHANCE` constant

`FoodFeature.VANILLA_CHANCE = 1.0f / 3.0f`

This is the default when nothing is configured. Existing call sites — `new FoodFeature().withFoods(Items.COOKIE)` — automatically get vanilla-equivalent taming with zero changes.

### Guaranteed taming

Setting `withGlobalChance(1.0f)` makes every attempt succeed. This is an explicit opt-in. No-chance-configured is not the same as guaranteed — it's 1/3.

### Item always consumed

One item is consumed per attempt regardless of outcome. Creative mode is exempt (same rule as everywhere else in HZLib). This matches vanilla exactly and creates real resource cost.

---

## `TamingFeedback` Design

Structured as an inner class of `FoodFeature` following the same pattern as `ExchangeFeedback`:

- Builder-based construction
- Particle type enum backed by `InternalParticle` methods
- Success and failure configured independently
- Sound is optional per outcome — **default is no sound (muted)**
- Particles have defaults — heart on success, ash on failure

### Sound default: muted

Unlike `ExchangeFeedback` where a sound feels natural for a completed exchange, taming feedback sound is more personal — some entities should react audibly, some shouldn't. Forcing a sound default could clash with entity-specific sounds already played by `handleTame()`. So: silence by default, opt in explicitly.

`handleTame()` in `InternalEntity` already plays the totem sound and Poof particles at taming time. `TamingFeedback` covers the attempt feedback (per-roll), not the taming completion feedback. These two are distinct events and should remain separate.

### Particle types

Reuses the same `InternalParticle` delegation pattern as `ExchangeFeedback.ParticleType`. All five types plus `NONE`:

| Type | Effect | Taming use case |
|---|---|---|
| `HEART` | Hearts | Success — the entity bonded |
| `ASH` | Ash cloud | Failure — the entity resisted |
| `HAPPY_VILLAGER` | Green sparkles | Alternative success |
| `POOF` | Poof cloud | Dramatic success |
| `SMOKE` | Smoke | Aggressive failure response |
| `NONE` | Nothing | Silent attempt |

---

## Integration

### `FoodFeature.attemptTame(entity, player, stack)`

The complete taming attempt in one call:
1. Check if item is registered food → return false immediately if not
2. Resolve effective chance for this item
3. Consume one item (unless creative)
4. Roll the chance
5. Play success or failure feedback
6. Return success/failure boolean

### `InternalEntity.onCommonInteraction`

Reduced to the minimum — no item consumption, no particle logic, no chance rolling. All of that is now inside `attemptTame`:

```java
nativeEntity.getFeature(FoodFeature.class).ifPresent(food -> {
    if (!isTame() && food.isFood(stack)) {
        boolean tamed = food.attemptTame(this, player, stack);
        if (tamed) handleTame(player);
    }
});
```

### TemptGoal unchanged

The `TemptGoal` reads from `FoodFeature.getTemptingIngredient()` for AI pathfinding. This is unaffected by taming chance — the entity still walks toward the player holding food regardless of what the chance is configured to.

---

## Consequences

### Positive

- Vanilla-equivalent taming by default — no migration cost for existing usage
- Per-item and global chance configurable in the entity type definition
- Feedback is visual and expressive without requiring any per-mod code
- `attemptTame()` is a clean single-call contract — `InternalEntity` has no taming details
- Sound is opt-in, preventing unintended audio conflicts with `handleTame()`

### Negative

- Taming is now probabilistic by default — existing `MushroomType` entities that expected guaranteed taming will now require multiple cookies. This is intentional but is a behavioral change for any existing tamed entities.

### Migration note

Any entity type currently using `new FoodFeature().withFoods(Items.COOKIE)` will automatically get 1/3 chance taming. To restore guaranteed taming for a specific entity type, add `.withGlobalChance(1.0f)`.

---

## Related Decisions

- ADR_012: InternalEntity Consolidation — established `InternalEntity.onCommonInteraction` where taming now lives
- ADR_013: ExchangeFeature — established `TamingFeedback.ParticleType` design vocabulary and inner-class feedback pattern
