# FoodFeature — API Reference

**Module**: `net.heriazone.hzlib.api.entity.features`  
**Lives in**: HZLib Common  
**Status**: Active (probabilistic taming added 2026-06-12)  
**See also**: ADR_014 for design rationale

---

## What it is

`FoodFeature` declares what items can tame an entity, how likely each attempt is to succeed, and what feedback plays on success or failure. It also declares tempting items — foods that make untamed entities walk toward the player via `TemptGoal`.

It is registered on entity types via `withFeature(FoodFeature.class, ...)` and evaluated automatically by `InternalEntity.onCommonInteraction` — no per-entity code needed.

---

## Probability model

Taming is probabilistic. One item is always consumed per attempt, success or failure. Attempts are independent — there is no progress bar.

**Default (no chance configured):** 1/3 per attempt — matches vanilla wolf and cat.  
**Guaranteed taming:** set `.withGlobalChance(1.0f)` explicitly.

Resolution order per attempt:
1. Per-item chance (set via `withFood(item, chance)`) → use it
2. Global chance (set via `withGlobalChance(float)`) → use it
3. Neither → `VANILLA_CHANCE` (1/3)

---

## Registration

### Basic — vanilla 1/3 chance

```java
new FoodFeature().withFoods(Items.COOKIE)
```

No migration from old code. Existing `.withFoods()` call sites automatically get vanilla-equivalent taming.

### Global chance

```java
new FoodFeature()
    .withFoods(Items.COOKIE, Items.CAKE)
    .withGlobalChance(0.5f)          // 50% for all foods registered above
```

### Per-food chance

```java
new FoodFeature()
    .withFood(Items.COOKIE, 0.25f)   // cookie: 25% per attempt
    .withFood(Items.CAKE,   0.75f)   // cake: 75% — a better treat
```

### Mixed — global with one override

```java
new FoodFeature()
    .withGlobalChance(0.25f)
    .withFoods(Items.COOKIE)         // 25% — uses global
    .withFood(Items.GOLDEN_APPLE, 1.0f)  // golden apple: always succeeds
```

### Guaranteed tame

```java
new FoodFeature()
    .withFoods(Items.COOKIE)
    .withGlobalChance(1.0f)
```

### With feedback

```java
new FoodFeature()
    .withFoods(Items.COOKIE)
    .withGlobalChance(0.33f)
    .withFeedback(TamingFeedback.builder()
        .onSuccess(TamingFeedback.ParticleType.HEART)
        .onFail(TamingFeedback.ParticleType.ASH)
        .successSound(SoundEvents.PLAYER_LEVELUP, 0.5f, 1.2f)
        .build())
```

---

## Methods

### Food registration

| Method | Description |
|---|---|
| `withFoods(Item...)` | Registers items using global chance (or vanilla default). Zero migration cost for existing call sites. |
| `withFood(Item, float chance)` | Registers one item with an explicit per-item chance (0.0–1.0). Overrides global for this item. |
| `withGlobalChance(float)` | Sets the default chance for all foods without a per-item override. |
| `withFeedback(TamingFeedback)` | Configures visual/audio feedback. Default: heart on success, ash on failure, no sound. |
| `withTemptingItems(Item...)` | Registers items that drive `TemptGoal` (entity walks toward player holding this). Independent of taming chance. |

### Taming

| Method | Description |
|---|---|
| `attemptTame(entity, player, stack)` | Runs one taming attempt: consumes item, rolls chance, plays feedback, returns `true` on success. Server-side only. |
| `getChanceFor(Item)` | Returns the effective taming chance for an item (resolves global/per-item/vanilla default). |

### Queries

| Method | Description |
|---|---|
| `isFood(Item)` | Returns `true` if the item is registered as a taming food. |
| `isFood(ItemStack)` | Convenience overload extracting item from stack. |
| `getFoods()` | Defensive copy of all registered food items. |
| `getTemptingItems()` | Defensive copy of all registered tempting items. |
| `getFoodIngredient()` | `Ingredient` matching all foods — used for AI goals and recipes. |
| `getTemptingIngredient()` | `Ingredient` matching all tempting items — used by `TemptGoal`. |

---

## TamingFeedback

`FoodFeature.TamingFeedback` configures what the player sees and hears on each taming attempt.

**Defaults when not configured:**
- Success → `HEART` particles
- Failure → `ASH` particles
- No sound (either outcome)

`TamingFeedback` covers per-attempt feedback only. `handleTame()` in `InternalEntity` handles the taming-completion feedback (totem sound, Poof particles) separately — these are two distinct events.

### Builder

```java
TamingFeedback.builder()
    .onSuccess(TamingFeedback.ParticleType.HEART)       // particle on success
    .onFail(TamingFeedback.ParticleType.ASH)            // particle on failure
    .successSound(SoundEvents.PLAYER_LEVELUP)           // sound on success (default pitch/volume)
    .successSound(SoundEvents.PLAYER_LEVELUP, 0.5f, 1.2f)  // with custom volume and pitch
    .failSound(SoundEvents.VILLAGER_NO)                 // sound on failure
    .failSound(SoundEvents.VILLAGER_NO, 1.0f, 0.8f)    // with custom volume and pitch
    .build()
```

All builder methods are optional — only configure what you need.

### ParticleType values

| Value | Delegated to | Typical use |
|---|---|---|
| `HEART` | `InternalParticle.Heart()` | Success — bonding, affection |
| `ASH` | `InternalParticle.Ash()` | Failure — rejection |
| `HAPPY_VILLAGER` | `InternalParticle.HappyVillager()` | Alternative success — positive event |
| `POOF` | `InternalParticle.Poof()` | Dramatic success |
| `SMOKE` | `InternalParticle.Smoke()` | Aggressive failure |
| `NONE` | — | Silent attempt |

---

## How taming works at runtime

When a player right-clicks an untamed entity while holding a registered food:

1. `InternalEntity.onCommonInteraction` is called (via the `final` `handleCommonInteractions` pipeline)
2. `FoodFeature` is found on the entity type
3. `food.isFood(stack)` confirms the item
4. `food.attemptTame(entity, player, stack)` runs:
   - Resolves effective chance
   - Consumes one item (unless creative)
   - Rolls `ThreadLocalRandom.nextFloat() < chance`
   - Plays success or failure feedback
   - Returns `true`/`false`
5. If `true` → `entity.handleTame(player)` fires (ownership, Poof particles, registry)
6. Return `PASS` — other interactions may still run

---

## TemptGoal integration

The `TemptGoal` registered in `MonsterEntity.registerNativeGoals()` reads `FoodFeature.getTemptingIngredient()`. This is unaffected by taming chance. The entity still walks toward the player holding a food item regardless of probability settings.

If you want the entity to follow the player with the taming food, add the same item to tempting:

```java
new FoodFeature()
    .withFood(Items.COOKIE, 0.33f)
    .withTemptingItems(Items.COOKIE)   // entity also walks toward player holding a cookie
```

---

## Migration from guaranteed taming

Old code:

```java
new FoodFeature().withFoods(Items.COOKIE)
```

This now gives 1/3 chance taming (vanilla default). To restore guaranteed taming:

```java
new FoodFeature()
    .withFoods(Items.COOKIE)
    .withGlobalChance(1.0f)
```

To keep the behavior but add vanilla-feel taming probability — no code change needed at all.

---

## Constants

| Constant | Value | Description |
|---|---|---|
| `VANILLA_CHANCE` | `1.0f / 3.0f` ≈ 0.333 | Default when no chance is configured. Matches wolf and cat. |
