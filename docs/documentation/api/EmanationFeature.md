# EmanationFeature — API Reference

**Module**: `net.heriazone.hzlib.api.entity.features.emanation`  
**Lives in**: HZLib Common  
**Status**: Active (introduced 2026-06-14)  
**See also**: ADR_015 for the full design rationale and the naming discussion

---

## What it is

`EmanationFeature` is the system through which a companion's inner nature makes itself felt in the world.

The concept, stated plainly:

> **A companion reacts to moments in her life — being hurt, attacking, receiving a gift, nearly dying — by channelling her nature outward as effects on the world around her.**

This is not a combat system. Combat implies skills, levels, cooldowns, decisions. This is simpler and deeper than that. The Molten Gal sets things on fire because **fire is what she is** — not because she has a fire attack skill. The Mandrake screams when hurt because her pain doesn't stay inside her — it radiates outward as dread. The Mandrake gives you Regeneration when you give her a cookie because she was made happy, and her happiness flowed outward to the nearest person.

There are two kinds of emanation:

- **Blessing** — a benevolent outflow. She was made happy; her happiness reached you. The item you gave was a catalyst, not a price.
- **Curse** — a hostile outflow. She was hurt or provoked; her pain or anger reached everything nearby.

---

## The name

Before this was called `EmanationFeature`, two other names were seriously considered:

**`PresenceFeature`** — "she makes her presence felt." Right philosophy, slightly abstract. Presence captures the idea that she is here and the world responds to that, but it doesn't carry the sense of outward movement or triggering.

**`NatureFeature`** — "what flows out is her." Strong connection to the idea that her behaviour is an expression of her essence, not a skill. But "nature" as a name is soft — it could mean almost anything.

Both were circling the same idea from different angles. Presence is "she's here and the world feels it." Nature is "what flows out is her." What was needed was a word that splits the difference — concrete enough to not feel like abstract design philosophy, but poetic enough for a companion mod.

**Emanation** is almost a dictionary definition of the mechanic: _something that flows or issues out from a source, triggered by an inner state, expressed outward into the world_. It keeps the "her nature made visible" quality of Nature, and the "the world feels her" quality of Presence, but adds the outward, directional sense that both were missing.

Blessings = benevolent emanations. Curses = hostile ones. It reads as less abstract once you see it in action: `EmanationTrigger.ON_HURT`, `EmanationEffects.aoe()`.

---

## Core concepts

### The four triggers

| Trigger | The moment in her life | Wire point |
|---|---|---|
| `ON_ATTACK` | She strikes a target and the hit lands | `MonsterEntity.doHurtTarget()` |
| `ON_HURT` | She takes damage from an attacker | `MonsterEntity.hurt()` |
| `ON_GIFT` | A player gives her an item while she is tamed | `MonsterEntity.onCommonInteraction()` |
| `ON_THRESHOLD` | Her health crosses a declared value | `MonsterEntity.aiStep()`, cooldown-guarded |

### Rules

An `EmanationRule` is one emanation: a trigger + an optional condition + one or more effects. A rule fires when:
1. Its trigger matches the current moment
2. Its condition passes, or no condition is set
3. Its cooldown has expired, or no cooldown is set (only relevant for `ON_THRESHOLD`)

### All-match vs. first-match

**ON_ATTACK, ON_HURT, ON_THRESHOLD** — all matching rules fire. The Soul Wanderer fires both her general attack rule (wither + regen) and her undead bonus rule on the same hit. They are independent; order doesn't matter.

**ON_GIFT** — first-match-wins. A gift has one meaning per item. The entity responds to one blessing, not all simultaneously. The first matching rule fires; the rest are skipped. Declare more-specific gift rules before less-specific fallbacks.

### Effects are functional

`EmanationEffect` is a `@FunctionalInterface`. Multiple `.effect()` calls on one rule all fire together. Effects can be wrapped by higher-order factories — `EmanationEffects.aoe(radius, effects...)` applies inner effects to all living entities within radius, remapping the context for each.

### Conditions compose

`EmanationCondition` is a `@FunctionalInterface`. Conditions compose:

```java
EmanationConditions.targetIsUndead().and(EmanationConditions.isTamed())
condition.or(other)
condition.negate()
```

### State is per-entity, not per-type

`EmanationFeature` is stateless — declared once on the entity type, shared across all instances. `EmanationState` is the per-entity counterpart: holds the `ON_THRESHOLD` cooldown as an absolute game tick (survives server restarts). Lives on `MonsterEntity` as `protected final EmanationState emanationState`.

### ON_GIFT is an emanation, not an exchange

The ON_GIFT trigger could technically live in `ExchangeFeature`, but calling a blessing an "exchange" misrepresents what happens. `ExchangeFeature` is economic — items flow between parties, there is a price. The Mandrake's cookie blessing is not a trade. She was made happy; her happiness became Regeneration. The gift was a catalyst. The blessing is an emanation of her inner state, not a transaction.

This distinction is intentional vocabulary. Keep it.

---

## Classes

### `EmanationFeature`

The top-level feature. Registered via `withFeature(EmanationFeature.class, ...)` on an entity type.

**Builder:**

| Method | Description |
|---|---|
| `.rule(EmanationRule)` | Adds a rule. Call in declaration order — matters for ON_GIFT |
| `.build()` | Builds the feature. Throws if no rules are declared |

**Runtime:**

| Method | Description |
|---|---|
| `getRulesFor(EmanationTrigger)` | All rules for that trigger, in declaration order. Never null |
| `getAllRules()` | All rules across all triggers |

---

### `EmanationRule`

One rule. One moment in her life, one emanation.

**Builder:**

| Method | Description |
|---|---|
| `.trigger(EmanationTrigger)` | Required |
| `.condition(EmanationCondition)` | Optional — gates the rule |
| `.effect(EmanationEffect)` | Adds one effect. Call multiple times for multiple effects on the same rule |
| `.cooldown(int ticks)` | Only enforced for ON_THRESHOLD. 0 = no cooldown (default) |
| `.consumesGift(boolean)` | ON_GIFT only. When true, one item removed from the giver's hand. Creative-mode givers not charged |
| `.build()` | Throws if no effects are declared |

---

### `EmanationConditions` — static factory

**Target (ON_ATTACK):**

| Factory | Passes when... |
|---|---|
| `targetIsUndead()` | Target's entity type is in `#minecraft:undead` — via `EntityUtils.isUndead()` |
| `targetIsAquatic()` | Target's entity type is in `#minecraft:aquatic` |
| `targetIsArthropod()` | Target's entity type is in `#minecraft:arthropod` |
| `targetIsPlayer()` | Target is a `Player` |
| `targetHasTag(TagKey<EntityType<?>>)` | Target's entity type is in the given tag — vanilla or custom |

**Attacker (ON_HURT):**

| Factory | Passes when... |
|---|---|
| `attackerIsPlayer()` | Attacker is a `Player` |

**Self (any trigger):**

| Factory | Passes when... |
|---|---|
| `isTamed()` | Entity is tamed |
| `healthAtMost(float)` | `self.getHealth() <= value` |
| `healthAtLeast(float)` | `self.getHealth() >= value` |

**Gift (ON_GIFT):**

| Factory | Passes when... |
|---|---|
| `giftIs(Item)` | Gift item matches exactly |
| `giftIsIn(TagKey<Item>)` | Gift item belongs to the tag |
| `giverIsOwner()` | Giver is the entity's owner |

---

### `EmanationEffects` — static factory

**Target effects:**

| Factory | What it does |
|---|---|
| `igniteTarget(int seconds)` | Sets target on fire. Delegates to `EntityUtils.ignite()` — version-isolated |
| `freezeTarget(int ticks)` | Adds freeze ticks to target, capped at the freeze threshold |
| `applyToTarget(Holder<MobEffect>, duration, amplifier)` | Applies mob effect to the target |
| `dealBonusDamage(float fraction)` | Deals `target.getMaxHealth() * fraction` bonus damage |

**Attacker effects:**

| Factory | What it does |
|---|---|
| `applyToAttacker(Holder<MobEffect>, duration, amplifier)` | Applies mob effect to whoever hurt her |

**Giver effects (the blessing):**

| Factory | What it does |
|---|---|
| `applyToGiver(Holder<MobEffect>, duration, amplifier)` | Applies mob effect to the player who gave the item |

**Self effects:**

| Factory | What it does |
|---|---|
| `applyToSelf(Holder<MobEffect>, duration, amplifier)` | Applies mob effect to the entity herself |
| `healSelf(float amount)` | Heals her by a flat amount |

**Area of effect:**

| Factory | What it does |
|---|---|
| `aoe(float radius, EmanationEffect... effects)` | Finds all living entities within radius (excluding self), remaps `ctx.target` to each, applies all inner effects to each |

---

### `EmanationContext`

Immutable snapshot passed to every condition and effect. Use static factories.

| Factory | Non-null fields |
|---|---|
| `EmanationContext.onAttack(self, target)` | `self`, `target`, `level` |
| `EmanationContext.onHurt(self, attacker)` | `self`, `attacker` (nullable if non-entity source), `level` |
| `EmanationContext.onGift(self, giver, gift)` | `self`, `giver`, `gift`, `level` |
| `EmanationContext.onThreshold(self)` | `self`, `level` |

`self` and `level` are always non-null. All other fields depend on the trigger type.

---

### `EntityUtils` — version isolation

Lives at `net.heriazone.hzlib.api.entity.util.EntityUtils`.

In 1.21, Minecraft removed `MobType` and `LivingEntity#getMobType()` as part of the enchantment data-driven rework. The old hardcoded enum (`UNDEAD`, `ARTHROPOD`, `ILLAGER`, `WATER`) is gone — `Smite`, `Bane of Arthropods`, etc. now use entity type tags. This means `getMobType() == MobType.UNDEAD` no longer compiles in 1.21.1.

Rather than patching this one call site and leaving the same problem latent in every future version-sensitive call, all such calls are wrapped in `EntityUtils`. Feature logic never calls Minecraft's volatile API directly — it calls `EntityUtils`. When backporting to an older Minecraft version, only `EntityUtils` changes. Feature conditions and effects remain untouched.

| Method | 1.21.1+ | Pre-1.21 backport |
|---|---|---|
| `isUndead(entity)` | `entity.getType().is(EntityTypeTags.UNDEAD)` | `getMobType() == MobType.UNDEAD` |
| `isAquatic(entity)` | `entity.getType().is(EntityTypeTags.AQUATIC)` | `getMobType() == MobType.WATER` |
| `isArthropod(entity)` | `entity.getType().is(EntityTypeTags.ARTHROPOD)` | `getMobType() == MobType.ARTHROPOD` |
| `isIllager(entity)` | `entity.getType().is(EntityTypeTags.ILLAGER)` | `getMobType() == MobType.ILLAGER` |
| `ignite(entity, seconds)` | `entity.igniteForSeconds(seconds)` | `entity.setSecondsOnFire(seconds)` |

The tag-based approach is also more flexible: any entity type can be added to or removed from `#minecraft:undead` via datapack JSON without touching Java. Custom mod tags work identically:

```java
// Define once in your mod
public static final TagKey<EntityType<?>> FEY = TagKey.create(
    Registries.ENTITY_TYPE,
    ResourceLocation.fromNamespaceAndPath("your_mod", "fey"));

// Use in any condition
.condition(EmanationConditions.targetHasTag(MyEntityTags.FEY))
```

---

## How to add an emanation to an entity type

### Declare the feature — that's it

```java
.withFeature(EmanationFeature.class, EmanationFeature.builder()
    .rule(EmanationRule.builder()
        .trigger(EmanationTrigger.ON_ATTACK)
        .effect(EmanationEffects.igniteTarget(4))
        .build())
    .build())
```

`MonsterEntity`'s wire points pick it up automatically. No entity subclass override needed.

---

## Full entity declarations

### Molten Gal — fire is what she is
```java
.withFeature(EmanationFeature.class, EmanationFeature.builder()
    .rule(EmanationRule.builder()
        .trigger(EmanationTrigger.ON_ATTACK)
        .effect(EmanationEffects.igniteTarget(4))
        .build())
    .build())
```

### Snowball Gal — cold radiates from her touch
```java
.withFeature(EmanationFeature.class, EmanationFeature.builder()
    .rule(EmanationRule.builder()
        .trigger(EmanationTrigger.ON_ATTACK)
        .effect(EmanationEffects.applyToTarget(MobEffects.MOVEMENT_SLOWDOWN, 100, 1))
        .effect(EmanationEffects.freezeTarget(70))
        .build())
    .build())
```

### Soul Wanderer Gal — soul energy flows through her attacks
```java
.withFeature(EmanationFeature.class, EmanationFeature.builder()
    // General attack: wither out, life back in
    .rule(EmanationRule.builder()
        .trigger(EmanationTrigger.ON_ATTACK)
        .effect(EmanationEffects.applyToTarget(MobEffects.WITHER, 60, 0))
        .effect(EmanationEffects.applyToSelf(MobEffects.REGENERATION, 60, 0))
        .build())
    // Undead are already in death's grip — her soul energy tears them apart
    .rule(EmanationRule.builder()
        .trigger(EmanationTrigger.ON_ATTACK)
        .condition(EmanationConditions.targetIsUndead())
        .effect(EmanationEffects.dealBonusDamage(0.7f))
        .build())
    .build())
```

### Gourdragora — she bites back, and survives
```java
// Declared as a shared constant — all three Gourdragora families share this nature
public static final EmanationFeature GOURDRAGORA_EMANATION = EmanationFeature.builder()
    // When hurt: pain radiates out as a retaliatory debuff burst
    .rule(EmanationRule.builder()
        .trigger(EmanationTrigger.ON_HURT)
        .effect(EmanationEffects.applyToAttacker(MobEffects.MOVEMENT_SLOWDOWN, 100, 1))
        .effect(EmanationEffects.applyToAttacker(MobEffects.POISON,            100, 0))
        .effect(EmanationEffects.applyToAttacker(MobEffects.CONFUSION,         100, 0))
        .effect(EmanationEffects.applyToAttacker(MobEffects.WEAKNESS,          100, 0))
        .build())
    // Near death: her will to survive surges inward
    .rule(EmanationRule.builder()
        .trigger(EmanationTrigger.ON_THRESHOLD)
        .condition(EmanationConditions.healthAtMost(3.0f))
        .effect(EmanationEffects.applyToSelf(MobEffects.REGENERATION, 100, 1))
        .cooldown(200) // won't re-fire for 10 s while health stays critical
        .build())
    .build();
```

### Mandrake Flower — scream curse + spring blessings
```java
.withFeature(EmanationFeature.class, EmanationFeature.builder()
    // Curse — her scream fills the air with pollen and dread
    .rule(EmanationRule.builder()
        .trigger(EmanationTrigger.ON_HURT)
        .effect(EmanationEffects.aoe(8.0f,
            EmanationEffects.applyToTarget(MobEffects.POISON,    100, 0),
            EmanationEffects.applyToTarget(MobEffects.CONFUSION, 100, 0),
            EmanationEffects.applyToTarget(MobEffects.HUNGER,    100, 0)))
        .build())
    // Blessings — she was made happy, her joy became your Regeneration
    .rule(EmanationRule.builder()
        .trigger(EmanationTrigger.ON_GIFT)
        .condition(EmanationConditions.giftIs(Items.COOKIE))
        .effect(EmanationEffects.applyToGiver(MobEffects.REGENERATION, 200, 0))
        .consumesGift(true)
        .build())
    .rule(EmanationRule.builder()
        .trigger(EmanationTrigger.ON_GIFT)
        .condition(EmanationConditions.giftIs(Items.BONE_MEAL))
        .effect(EmanationEffects.applyToGiver(MobEffects.STRENGTH, 200, 0))
        .consumesGift(true)
        .build())
    .rule(EmanationRule.builder()
        .trigger(EmanationTrigger.ON_GIFT)
        .condition(EmanationConditions.giftIs(Items.CLAY_BALL))
        .effect(EmanationEffects.applyToGiver(MobEffects.LUCK, 200, 0))
        .consumesGift(true)
        .build())
    .rule(EmanationRule.builder()
        .trigger(EmanationTrigger.ON_GIFT)
        .condition(EmanationConditions.giftIs(Items.WATER_BUCKET))
        .effect(EmanationEffects.applyToGiver(MobEffects.HEALTH_BOOST, 200, 0))
        .consumesGift(true)
        .build())
    .build())
```

---

## Common patterns

### Multiple effects in one rule — one reaction, multiple outputs
```java
// Wither flows out, life flows back in — two effects, one moment
.rule(EmanationRule.builder()
    .trigger(EmanationTrigger.ON_ATTACK)
    .effect(EmanationEffects.applyToTarget(MobEffects.WITHER, 60, 0))
    .effect(EmanationEffects.applyToSelf(MobEffects.REGENERATION, 60, 0))
    .build())
```

### Multiple independent rules on the same trigger — layered nature
```java
// Both fire if target is undead; only the first fires for non-undead
.rule(EmanationRule.builder()
    .trigger(EmanationTrigger.ON_ATTACK)
    .effect(EmanationEffects.applyToTarget(MobEffects.WITHER, 60, 0))
    .build())
.rule(EmanationRule.builder()
    .trigger(EmanationTrigger.ON_ATTACK)
    .condition(EmanationConditions.targetIsUndead())
    .effect(EmanationEffects.dealBonusDamage(0.7f))
    .build())
```

### Threshold with cooldown — fires once, then waits
```java
.rule(EmanationRule.builder()
    .trigger(EmanationTrigger.ON_THRESHOLD)
    .condition(EmanationConditions.healthAtMost(5.0f))
    .effect(EmanationEffects.applyToSelf(MobEffects.ABSORPTION, 200, 1))
    .cooldown(400) // 20 s before it can fire again
    .build())
```

### Gift with multiple blessings flowing at once
```java
.rule(EmanationRule.builder()
    .trigger(EmanationTrigger.ON_GIFT)
    .condition(EmanationConditions.giftIs(Items.CHORUS_FRUIT))
    .effect(EmanationEffects.applyToGiver(MobEffects.REGENERATION, 200, 0))
    .effect(EmanationEffects.applyToGiver(MobEffects.LEVITATION,    60, 0))
    .consumesGift(true)
    .build())
```

### Custom entity type tag
```java
// In your mod — add entities via data/your_mod/tags/entity_type/fey.json
public static final TagKey<EntityType<?>> FEY = TagKey.create(
    Registries.ENTITY_TYPE,
    ResourceLocation.fromNamespaceAndPath("your_mod", "fey"));

.condition(EmanationConditions.targetHasTag(MyEntityTags.FEY))
```

---

## Constraints

### ON_THRESHOLD requires a cooldown

A threshold rule without a cooldown fires every tick — 20 times per second — while health stays below the threshold. Always declare `.cooldown(ticks)`. A cooldown of `200` (10 seconds) is a reasonable default for survival effects.

### ON_GIFT only fires when the entity is tamed

The ON_GIFT wire checks `isTame()` before evaluating rules. An untamed entity doesn't know the giver well enough to feel blessed. This is automatic — no condition needed.

### ON_GIFT is first-match-wins — declaration order matters

Gift rules are checked in order. The first matching rule fires; the rest are skipped. More-specific rules (matching a specific item) must come before less-specific fallbacks.

### AoE scream affects the owner

`EmanationEffects.aoe()` excludes `ctx.self` but not the owner or other nearby tamed companions. If a Mandrake screams while her owner is within 8 blocks, the owner receives the scream effects. This is intentional — her pain doesn't distinguish allies. To shield the owner, implement a custom `EmanationEffect` lambda:

```java
.effect(ctx -> {
    if (!(ctx.self instanceof TamableAnimal t)) return;
    Entity owner = t.getOwner();
    ctx.level.getEntitiesOfClass(LivingEntity.class,
            ctx.self.getBoundingBox().inflate(8.0f),
            e -> e != ctx.self && e.isAlive() && !e.equals(owner))
        .forEach(e -> e.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0)));
})
```

---

## What EmanationFeature is NOT

**Not a combat skill system.** No skill levels, no cooldowns on attack/hurt triggers, no progression. Her nature is constant — it flows every time the moment arises.

**Not a replacement for MonstersEffectCallbacks.** `MonstersEffectCallbacks` handles the *player* carrying a custom effect (from a stew or potion) and attacking something. `EmanationFeature` handles the *companion entity* attacking something. Both scopes are necessary; neither replaces the other.

**Not an ExchangeFeature replacement.** `ExchangeFeature` gives items. `EmanationFeature` ON_GIFT gives effects. A bowl that returns a stew is an exchange. A cookie that returns a blessing is an emanation. They coexist on the same entity without conflict. The distinction is intentional — preserve it.

---

## Interaction pipeline order

When a player right-clicks a tamed entity:

1. `ExchangeFeature` — item in, item out (via `handleCommonInteractions` in `InternalEntity`)
2. Taming food check (`FoodFeature`) — via `super.onCommonInteraction`
3. `EmanationFeature` ON_GIFT — blessing, if any gift rule matches

---

## See also

- `ExchangeFeature.md` — item exchange; complements EmanationFeature
- `FoodFeature.md` — taming; fires before ON_GIFT
- ADR_015 — full design rationale, naming journey, and architecture decisions
- `EntityUtils.java` — version-isolated Minecraft API; backport guide per method
