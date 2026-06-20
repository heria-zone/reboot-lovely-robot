# ADR 015: EmanationFeature — Companion Presence System

**Status**: Accepted  
**Date**: 2026-06-14  
**Decision Makers**: Serge  
**Consulted**: Design session — Mushroom Gals combat behaviours, Gourdragora retaliation & survival, Mandrake scream & blessings, HZLib ecosystem design

---

## Context

### The immediate problem

Several entity families needed behaviours that existing features couldn't express:

- **Molten Gal** — sets enemies on fire when she attacks
- **Snowball Gal** — slows and partially freezes enemies when she attacks
- **Soul Wanderer Gal** — inflicts Wither on enemies and Regeneration on herself when attacking; deals 70% max-health bonus damage against undead
- **Gourdragora** — retaliates with a debuff burst when hurt; triggers self-regen at critically low health
- **Mandrake** — screams when hurt, radiating negative effects outward to all nearby entities; blesses the player when given specific items

The naive path would be one entity subclass per family, each overriding `doHurtTarget()`, `hurt()`, and `tick()`. That means five classes and fifteen overrides, all expressing variations of the same pattern.

### Recognising the pattern

Before naming anything, we looked at all three families together and asked what they actually share.

The Molten Gal sets things on fire. The Snowball Gal freezes them. The Soul Wanderer inflicts wither and draws life back. The Gourdragora bites back when hurt. The Mandrake screams. And the Mandrake also blesses you when you give her something she loves.

That last one — the blessing — is what broke the frame of "combat system." Giving a Mandrake a cookie and receiving Regeneration has nothing to do with combat. And yet it fits the same structural pattern as everything else: a moment happens in her life, and something flows outward from her in response.

The pattern, stated as clearly as it can be:

> **A companion reacts to moments in her life — being hurt, attacking, receiving a gift, nearly dying — by channelling her nature outward as effects on the world around her.**

This is not a combat system. It is not a generic effect system. It is a **presence** system. It is about what she is, made visible by the moments that trigger it.

### Naming — why this took time

Once the concept was clear, naming it correctly mattered a lot. The name has to carry the concept without requiring an explanation every time it's used.

**`CombatFeature`** — rejected immediately. The Mandrake's blessing has nothing to do with combat. A name that implies combat would mislead anyone reading code that declares blessings under it.

**`EffectsFeature`** — rejected. Too generic. It describes the output (effects), not the concept (outward expression of inner nature). Every system in the game produces effects at some level.

**`PresenceFeature`** — got close. "She makes her presence felt." The idea is right: she's here, and the world around her responds to that. But "presence" is slightly abstract as a noun — it doesn't inherently suggest triggering or outward movement. It captures the philosophy but not quite the mechanic.

**`NatureFeature`** — also close. "What flows out is her." Her nature, as in her trait, her essence. Strong connection to the idea that the Molten Gal sets things on fire because fire is what she *is*, not because she has a fire attack skill. But "nature" as a name is soft — it could mean anything from personality to biome.

Both Presence and Nature were circling the same idea from different angles:
- Presence: "she's here and the world feels it"
- Nature: "what flows out is her"

What was needed was a word that splits the difference — that captures both "what she is" and "how it flows outward." A name that is concrete enough to not feel like design-philosophy-speak, but poetic enough to fit a companion-mod context.

**`EmanationFeature`** — accepted.

"Emanation" literally means something that flows or issues out from a source. That is almost a dictionary definition of the mechanic: triggered by an inner state, expressed outward into the world. It keeps the "her nature made visible" quality of Nature, and the "the world feels it" quality of Presence, but adds the directional, outward-flowing sense that both were missing. It reads as less abstract once you see it in action — `EmanationTrigger.ON_HURT`, `EmanationEffects.aoe()`, a blessing that flows to the giver.

And it handles both kinds naturally:
- **Blessing** — a benevolent emanation. She was made happy; her happiness reached you.
- **Curse** — a hostile emanation. She was hurt; her pain reached everything nearby.

### Why this belongs in HZLib, not in monsters_girls

The same reasoning that produced `ExchangeFeature` applies here. The bowl-to-stew interaction was the first example of a universal companion exchange pattern — it would have been easy to implement it as a `BowlFeature` in `monsters_girls` and move on, but we recognised it was the first instance of something that would recur across every companion mod in the ecosystem. So we built it in HZLib, named it well, and every mod downstream inherited the vocabulary.

The Molten Gal's fire attack is the first example of a universal companion emanation pattern. If we implement it per-entity in `monsters_girls`, every future companion mod in this ecosystem reimplements it from scratch. The goal is to establish the vocabulary once.

### ON_GIFT — why it's an emanation, not an exchange

The Mandrake's blessing (`ON_GIFT`) could technically be modelled as an `ExchangeFeature` rule with an effect-output supplier rather than an item-output supplier. But that would abuse the exchange concept.

`ExchangeFeature` is about **items flowing between parties**. Economic. The player gives something; the entity gives something back. Calling a blessing an "exchange" misrepresents what's happening.

When you give a Mandrake a cookie, she isn't trading Regeneration for the cookie. She was made happy. Her happiness *radiates outward*. The cookie was a catalyst, not a price. The giver receives the blessing because they are the closest person to that outward flow, not because they paid for it.

That distinction matters for the vocabulary of the ecosystem. If everything that happens during a player interaction is called an "exchange," the word loses meaning. `ExchangeFeature` is for economic interactions. `EmanationFeature` is for everything that flows out of her — including the joy a gift brings.

---

## Decision

Implement `EmanationFeature` as a first-class feature in HZLib's entity feature system, at the same level as `FoodFeature`, `PlantingFeature`, `ExchangeFeature`, and `SpawnFeature`.

`EmanationFeature` lives at `net.heriazone.hzlib.api.entity.features.emanation` and is available to any entity in any HZLib-based mod.

---

## Design

### The fundamental model

An emanation has three parts:

```
trigger      (the moment in her life)
  + condition  (optional — gates whether it fires)
  = effects    (one or more actions applied to the world)
```

Multiple emanations are declared as **rules** on one feature. Unlike `ExchangeFeature`, there is no first-match-wins for most triggers — all matching rules fire independently. The Soul Wanderer fires both her general attack rule (wither + regen) and her undead bonus rule on the same hit. Order matters only for `ON_GIFT`.

### The four triggers

| Trigger | The moment | Wire point in MonsterEntity |
|---|---|---|
| `ON_ATTACK` | She strikes a target and the hit lands | `doHurtTarget()` |
| `ON_HURT` | She takes damage | `hurt()` |
| `ON_GIFT` | A player gives her an item while tamed | `onCommonInteraction()` |
| `ON_THRESHOLD` | Her health crosses a declared value | `aiStep()`, cooldown-guarded |

Triggers map directly to lifecycle methods in `MonsterEntity`. The enum makes the mapping explicit and eliminates any string-keyed dispatch.

### Why data, not subclasses

Option B — one subclass per family overriding `doHurtTarget()` — was explicitly discussed and rejected. It creates n classes for n families, all expressing variations of the same structural idea. More importantly, it hides the intent inside method bodies that require reading to understand.

`EmanationFeature` expresses all of that variation as data on the entity type declaration. No new Java classes are needed when adding a family with different effects. The behaviour is readable at the declaration site — the Soul Wanderer's full combat behaviour is three lines of feature declaration, not a subclass with overrides.

### Effects as functional interfaces

`EmanationEffect` is a `@FunctionalInterface`. This allows effects to compose as lambdas, be passed as arguments, and be wrapped by higher-order factories like `EmanationEffects.aoe(radius, effects...)`. The AoE effect remaps the context for each nearby entity — inner `applyToTarget()` effects resolve correctly without any special entity class.

### The Mandrake scream — canonical AoE use

```java
EmanationEffects.aoe(8.0f,
    EmanationEffects.applyToTarget(MobEffects.POISON,    100, 0),
    EmanationEffects.applyToTarget(MobEffects.CONFUSION, 100, 0),
    EmanationEffects.applyToTarget(MobEffects.HUNGER,    100, 0))
```

One line per effect, zero special entity code. The scream is her pain flowing outward — `EmanationEffects.aoe()` is the mechanical expression of that.

### ON_GIFT — blessings use first-match-wins

`ON_GIFT` is the only trigger with first-match-wins evaluation. A gift has one meaning per item. The entity responds to one blessing, not all simultaneously. Rules are checked in declaration order; the first matching gift rule fires and the rest are skipped. This mirrors `ExchangeFeature`'s rule evaluation, and it's the right model for gifts: she got one thing, she feels one way about it, one emanation flows out.

### Version isolation — EntityUtils and the MobType removal

During implementation, `EmanationConditions.targetIsUndead()` was initially written as:

```java
ctx.target.getMobType() == MobType.UNDEAD
```

This is the correct approach for Minecraft versions prior to 1.21. In 1.21, as part of the enchantment data-driven rework, `MobType` and `LivingEntity#getMobType()` were removed entirely. The old enum (`UNDEAD`, `ARTHROPOD`, `ILLAGER`, `WATER`) is gone — `Smite`, `Bane of Arthropods`, etc. now use entity type tags (`#minecraft:undead`, `#minecraft:arthropod`) rather than hardcoded Java enums.

Rather than patching this one call site and leaving the same problem latent for every other version-sensitive API call, a principled structural fix was made: introduce `EntityUtils`, a thin Java adapter class that isolates all Minecraft API calls that change between versions.

**Feature logic calls `EntityUtils`. Only `EntityUtils` changes per backport.**

The benefits:
- When backporting, one file changes, not every feature condition and effect across the codebase
- Tag-based checks are data-driven — any entity type can be added to or removed from the undead set via datapack JSON without touching Java
- Custom mod tags (`monsters_girls:fey`, etc.) follow the same pattern and integrate naturally with `EmanationConditions.targetHasTag()`

```
hzlib / api / entity / util /
    EntityUtils.java   — stable interface, version-specific implementations documented per-method
```

### EmanationState — why minimal, why on MonsterEntity

`ON_THRESHOLD` fires in `aiStep()` — every tick. Without a per-entity cooldown, health ≤ 3 would trigger Regeneration 20 times per second for as long as health stays critical. `EmanationState` holds that cooldown as an absolute game tick, matching the approach used by `ExchangeState` (server-restart-safe, no countdown drift).

`ON_ATTACK`, `ON_HURT`, and `ON_GIFT` have no cooldown — they fire per event, already rate-limited by the game's own systems (one hit per swing, one hurt per damage application, one interaction per right-click).

`EmanationState` lives on `MonsterEntity`, not `InternalEntity`, because robots don't use emanation mechanics yet. If robots develop emanation behaviours in a future iteration, `EmanationState` moves up the hierarchy at that point.

---

## File Structure

```
net.heriazone.hzlib.api.entity.features.emanation/
  EmanationTrigger.java       — enum: ON_ATTACK, ON_HURT, ON_GIFT, ON_THRESHOLD
  EmanationContext.java       — immutable snapshot at the moment of emanation
  EmanationCondition.java     — @FunctionalInterface, composes with .and() / .or() / .negate()
  EmanationConditions.java    — static factory: targetIsUndead(), healthAtMost(), giftIs()...
  EmanationEffect.java        — @FunctionalInterface: (context) → void
  EmanationEffects.java       — static factory: igniteTarget(), applyToTarget(), aoe()...
  EmanationRule.java          — trigger + condition + effects + cooldown + consumesGift
  EmanationFeature.java       — holds rules, getRulesFor(trigger), stateless
  EmanationState.java         — per-entity: threshold cooldown, NBT-persisted

net.heriazone.hzlib.api.entity.util/
  EntityUtils.java            — version-agnostic adapter: isUndead(), isAquatic(), ignite()...
```

---

## Integration Points

### MonsterEntity — four wire points

```java
// ON_ATTACK
@Override
public boolean doHurtTarget(Entity target) {
    boolean result = super.doHurtTarget(target);
    if (result && !level().isClientSide && target instanceof LivingEntity living) {
        nativeEntity.getFeature(EmanationFeature.class).ifPresent(f ->
            f.getRulesFor(ON_ATTACK).forEach(rule ->
                rule.tryFire(EmanationContext.onAttack(this, living), emanationState)));
    }
    return result;
}

// ON_HURT
@Override
public boolean hurt(DamageSource source, float amount) {
    boolean result = super.hurt(source, amount);
    if (result && !level().isClientSide) {
        LivingEntity attacker = source.getEntity() instanceof LivingEntity l ? l : null;
        nativeEntity.getFeature(EmanationFeature.class).ifPresent(f ->
            f.getRulesFor(ON_HURT).forEach(rule ->
                rule.tryFire(EmanationContext.onHurt(this, attacker), emanationState)));
    }
    return result;
}

// ON_GIFT — in MonsterEntity.onCommonInteraction(), after super (taming food check)
if (isTame() && nativeEntity != null && !stack.isEmpty()) {
    nativeEntity.getFeature(EmanationFeature.class).ifPresent(f -> {
        for (EmanationRule rule : f.getRulesFor(ON_GIFT)) {
            if (rule.tryFire(EmanationContext.onGift(this, player, stack), emanationState)) break;
        }
    });
}

// ON_THRESHOLD — in aiStep(), server-side, runs every tick
nativeEntity.getFeature(EmanationFeature.class).ifPresent(f ->
    f.getRulesFor(ON_THRESHOLD).forEach(rule ->
        rule.tryFire(EmanationContext.onThreshold(this), emanationState)));
```

`EmanationState` is `protected final` on `MonsterEntity`. Persisted under key `"EmanationState"` in `addAdditionalSaveData` / `readAdditionalSaveData`.

### Interaction pipeline order

When a player right-clicks a tamed entity, interactions fire in this order:

1. `ExchangeFeature` — evaluated first (item → item, `handleCommonInteractions` in `InternalEntity`)
2. Taming food (`FoodFeature`) — evaluated second (`super.onCommonInteraction`)
3. `EmanationFeature` ON_GIFT — evaluated third (`MonsterEntity.onCommonInteraction` override)

An entity can have both features simultaneously without conflict. A Mandrake could have an `ExchangeFeature` (bowl → item) and an `EmanationFeature` ON_GIFT (cookie → blessing) at the same time — they serve different interaction semantics and never collide.

### MonstersEffectCallbacks remains necessary

`MonstersEffectCallbacks` handles the player having a custom effect (from stew or potion) and attacking something. `EmanationFeature` handles the companion entity attacking something. These are separate scopes. Both remain necessary. `EmanationFeature` does not replace the callback system — it handles a different actor.

---

## Consequences

### Positive

- Single, well-named implementation for all "companion radiates outward" mechanics — every HZLib mod inherits it
- Behaviour is declared where the entity type is defined — readable without digging into subclasses
- AoE effects compose without a special entity class
- Version-sensitive API calls are isolated in `EntityUtils` — backporting to older Minecraft versions touches one file
- Data-driven entity category checks — custom mod tags work without Java code changes

### Negative

- `ON_THRESHOLD` fires every tick — developers must always declare `.cooldown(ticks)`. Without it the threshold fires 20× per second. This constraint is mitigated by documentation but cannot be enforced at compile time.
- `ON_GIFT` first-match-wins means declaration order matters — a less-specific rule placed before a more-specific one will shadow it silently.

### Risks

- **Missing cooldown on ON_THRESHOLD**: The most likely future bug. Both this ADR and the API reference document it explicitly. A future improvement could log a warning when a threshold rule declares `cooldown == 0`.
- **AoE scream affects the owner**: `aoe()` excludes `ctx.self` but not the owner. If the Mandrake's owner is within 8 blocks when she screams, the owner receives the scream effects. This is intentional — her pain doesn't distinguish allies. It is documented in the API reference with a workaround for cases where owner-shielding is needed.

---

## Related Decisions

- ADR_009: Entity Hierarchy Refactoring — established the `InternalEntity` → `MonsterEntity` hierarchy this feature integrates into
- ADR_012: InternalEntity Consolidation — established `InternalEntity` as the single shared base; `EmanationState` lives on `MonsterEntity` for now
- ADR_013: ExchangeFeature — the companion interaction system this feature complements; see that ADR for the parallel reasoning about why ecosystem features belong in HZLib
- ADR_014: FoodFeature Probabilistic Taming — the taming system that fires before `ON_GIFT` in the interaction pipeline
- ADR_011: Variant and Spawn System Refactoring — established the `withFeature()` / `getFeature()` composition pattern both features use
