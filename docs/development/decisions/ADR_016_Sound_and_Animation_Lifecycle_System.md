# ADR 016: Sound & Animation Lifecycle System

**Status**: Accepted  
**Date**: 2026-06-17  
**Decision Makers**: Serge  
**Consulted**: Design session — Monsters & Girls sound mapping, animation lifecycle analysis, GeckoLib API audit  

---

## Context

### The immediate problem

Monsters & Girls has 27 sound events declared in `sounds.json`. Zero of them play in-game. No `SoundEvent` Java constants exist, `MonsterEntity.getAmbientSound()` returns `super.getAmbientSound()` (null), and no entity type has a `SoundFeature` attached.

At the same time, the animation system for special idle animations (`sing`, `sing2`, `wave`) was discovered to have two compounding bugs:
1. All specials were being registered as `PLAY_ONCE` triggerables regardless of their declared `LoopBehavior` — silently discarding the data already present in the `AnimationPool`.
2. `sing2` (and any future `LOOP` special) cannot use the triggerable system at all — GeckoLib triggerables are by definition one-shot and return to the state handler when they complete. A sustained loop requires a different mechanism.

These two problems — sounds and animation lifecycle — are deeply connected. A looping singing animation needs a looping sound that starts and stops with the animation. A one-shot hurt animation fires a one-shot hurt sound. You cannot design the sound system correctly without first resolving the animation lifecycle question.

### Why sounds cannot be designed in isolation

The naive approach — add `SoundEvent` constants and override `getAmbientSound()` — works for standard entity sounds (ambient, hurt, death) but breaks for the animation-linked sounds (`mandrake_song` plays during `sing`/`sing2`; puffball puff sounds play during the puff animation). These sounds have a different trigger model and lifecycle than ambient sounds, and they must be handled differently.

Additionally, the Gourdragora entity has three size variants (mini/default/mega) with three distinct ambient sound pools. The existing `SoundFeature` can only hold one `SoundEvent` per `SoundType` — it has no dimension for variant-keyed lookup. This is not a Gourdragora-specific edge case; it is a pattern that will recur for any entity whose visual form determines its sound identity.

### Why this ADR exists

Both problems are being solved together because:
- The animation lifecycle decision constrains how sounds are triggered
- The `SoundFeature` extension is load-bearing for the entire sound plan
- The design will be copied for every future entity family; inconsistency now compounds forever

---

## Analysis: The Three Animation-Sound Categories

All special animations fall into exactly three behavioural categories, each requiring a distinct implementation:

### Category 1: SNAP (PLAY_ONCE)
*Examples: `sing`, `wave`, `belly`, `hurt`*

Animation plays once from start to finish, returns to locomotion automatically. GeckoLib's triggerable system is the correct API — `triggerableAnim` registers the animation; `triggerAnim` fires it server-side; GeckoLib handles client sync internally.

Sound: play once at trigger time. Sound ends with the animation naturally.

**Current state**: Hurt is handled correctly. Wave, belly, and other PLAY_ONCE specials are also handled correctly since the recent animation system fixes. ✅

### Category 2: SUSTAIN (LOOP)
*Example: `sing2`*

Animation loops indefinitely until externally interrupted (entity moves, enters combat, takes damage). GeckoLib's triggerable system **cannot** handle this — triggerables are one-shot by design.

Sound: a looping sound that must start when the animation starts and stop when the animation stops. This mirrors how vanilla Minecraft handles looping mob sounds via `TickableSound`.

**Current state**: `sing2` was incorrectly declared as `PLAY_ONCE` in both `FLOWER_PROFILE` and `FRUCTUS_PROFILE`. The code discarded its true `LOOP` intent. ❌ — requires fix.

### Category 3: AMBIENT (periodic, no animation)
*Examples: `mandrakesounds`, `slimegirlsounds`, `wisplaughs`*

Standard Minecraft ambient sound tick — `getAmbientSound()` returns a `SoundEvent`, Minecraft schedules it at a random interval. Not linked to any animation.

**Current state**: Returns null for all entities. ❌ — requires implementation.

---

## Decision: Four-Part Architecture

### Part 1 — `SoundFeature` Extension: Variant-Keyed Sounds

**Problem**: The current `SoundFeature` is `Map<SoundType, SoundEvent>` — one sound per type. Gourdragora needs three ambient sounds, one per size variant. This pattern will recur for any entity with per-form audio.

**Decision**: Extend `SoundFeature` with a second dimension — a variant key — while keeping the existing single-key API fully intact as the default.

```
SoundFeature
  Map<SoundType, SoundEvent>                    ← unkeyed (existing, unchanged)
  Map<SoundType, Map<String, SoundEvent>>       ← variant-keyed (new)

getSound(SoundType type)                        ← existing: returns unkeyed entry
getSound(SoundType type, String variantKey)     ← NEW: tries variant, falls back to unkeyed
withSound(SoundType type, SoundEvent)           ← existing, unchanged
withSound(SoundType type, String variantKey, SoundEvent)  ← NEW
```

The variant key is the `MODEL_VARIANT` synced data value — already set correctly by the variant system for all entities.

Usage for Gourdragora:
```java
new SoundFeature()
    .withSound(AMBIENT, "gourdragora_girl_mini",    MonstersSounds.MINI_GOURDRAGORA_AMBIENT)
    .withSound(AMBIENT, "gourdragora_girl_default", MonstersSounds.GOURDRAGORA_AMBIENT)
    .withSound(AMBIENT, "gourdragora_girl_mega",    MonstersSounds.MEGA_GOURDRAGORA_AMBIENT)
    .withSound(ATTACK,  MonstersSounds.GOURDRAGORA_ROAR)
```

Usage for Mandrake (three entity-type variants, each with a different ambient sound):
```java
// MandrakeType CHORUS constructor
new SoundFeature().withSound(AMBIENT, MonstersSounds.MANDRAKE_CHORUS_AMBIENT)
                  .withSound(HURT,    MonstersSounds.MANDRAKE_HURT)
                  .withSound(DEATH,   MonstersSounds.MANDRAKE_DEATH)

// MandrakeType FLOWER constructor
new SoundFeature().withSound(AMBIENT, MonstersSounds.MANDRAKE_FLOWER_AMBIENT)
                  .withSound(HURT,    MonstersSounds.MANDRAKE_HURT)
                  .withSound(DEATH,   MonstersSounds.MANDRAKE_DEATH)
```

Mandrake's three types are separate `NativeEntityType` instances, so each gets its own `SoundFeature` without needing variant keys. The variant-key API is for intra-type variation (same entity type, different visual form, different sound).

**Why Option A (extend SoundFeature) over Option B (field on NativeEntityType)**:  
Option B is simpler for the Mandrake case but siloes the pattern. Option A is reusable for Gourdragora, Puffball (puffed/unpuffed), Warped (multiple skins), and any future entity. The cost is a small amount of extra code in `SoundFeature`. The benefit is that no entity ever needs ad-hoc sound handling outside the feature system.

### Part 2 — `MonstersSounds` Registry: Single Source of Truth

**Problem**: No `SoundEvent` Java constants exist. The `sounds.json` keys are currently dead — nothing in code can reference them.

**Decision**: Create `MonstersSounds.java` in Common as the single authoritative registry of all sound events. Pattern mirrors vanilla's `SoundEvents.java`.

```java
// MonstersSounds.java (Common)
public class MonstersSounds {
    // Mandrake
    public static SoundEvent MANDRAKE_CHORUS_AMBIENT; // mandrakesounds
    public static SoundEvent MANDRAKE_FLOWER_AMBIENT; // mandrake_song
    public static SoundEvent MANDRAKE_FRUCTUS_AMBIENT; // fruity_mandrake
    public static SoundEvent MANDRAKE_HURT;            // mandrakehurt
    public static SoundEvent MANDRAKE_DEATH;           // mandrakedies
    // Mushroom
    public static SoundEvent MUSHROOM_AMBIENT;         // mushroomgirlsounds
    public static SoundEvent MUSHROOM_AMBIENT_WEIRD;   // weirdmushroomgirlsounds
    public static SoundEvent MUSHROOM_GREET;           // mushroomgirlsayshi
    public static SoundEvent MUSHROOM_GREET_WEIRD;     // weirdmushroomgalsayshi
    // ... etc.
    
    public static void register() { /* loader-specific registration */ }
}
```

Loader-specific registration is wired in each loader's init (Fabric `onInitialize`, Forge `FMLCommonSetupEvent`, NeoForge equivalent).

### Part 3 — `MonsterEntity` Standard Sound Wiring

**Problem**: `getAmbientSound()`, `getHurtSound()`, `getDeathSound()` are no-op stubs returning `super`.

**Decision**: Override all three in `MonsterEntity` to read from `SoundFeature`. The variant-aware lookup uses `getModelVariant()` as the variant key — already available on all `InternalEntity` instances.

```java
// MonsterEntity.java

@Override
protected SoundEvent getAmbientSound() {
    if (!isSoundEnabled()) return null;
    return nativeEntity == null ? null :
        nativeEntity.getFeature(SoundFeature.class)
            .map(f -> f.getSound(SoundFeature.SoundType.AMBIENT, getModelVariant()))
            .orElse(null);
}

@Override
protected SoundEvent getHurtSound(DamageSource source) {
    return nativeEntity == null ? null :
        nativeEntity.getFeature(SoundFeature.class)
            .map(f -> f.getSound(SoundFeature.SoundType.HURT))
            .orElse(super.getHurtSound(source));
}

@Override
protected SoundEvent getDeathSound() {
    return nativeEntity == null ? null :
        nativeEntity.getFeature(SoundFeature.class)
            .map(f -> f.getSound(SoundFeature.SoundType.DEATH))
            .orElse(super.getDeathSound());
}
```

`isSoundEnabled()` — the existing `SOUND_ENABLED` synced field on `MonsterEntity` — guards ambient sounds only (hurt/death always play regardless of preference, matching the spirit of the feature toggle).

### Part 4 — Animation Lifecycle: SNAP vs SUSTAIN

**Problem**: The `buildAndRegisterLocomotionController` method registers every special as a `PLAY_ONCE` triggerable, ignoring the `LoopBehavior` already stored in the `AnimationPool`.

**Decision**: Route specials through two separate systems based on their `LoopBehavior`:

#### SNAP path (PLAY_ONCE) — existing triggerable system
No change to the triggerable registration. When `customServerAiStep` selects a special and its pool entry has `LoopBehavior.PLAY_ONCE`, it fires via `triggerAnim` exactly as before.

Sound: `playSound()` is called server-side at the same time as `triggerAnim`, using `SoundFeature.getSound(SoundType.INTERACT, animationName)` — the variant-keyed sound lookup using the animation name as the key.

#### SUSTAIN path (LOOP) — new synced controller

A `SynchedEntityData<String> ACTIVE_LOOP_SPECIAL` field is added to `WildTamableEntity` (empty string = none active).

**Server side**:
- When `customServerAiStep` selects a LOOP special: set `ACTIVE_LOOP_SPECIAL` to the animation name
- Clear `ACTIVE_LOOP_SPECIAL` when: entity moves, enters `combatMode`, or takes damage
- The `idleTicks` counter is also reset on clear

**Client side** (GeckoLib controller registered in `buildAndRegisterLocomotionController`):
A third "Sustained" controller reads `ACTIVE_LOOP_SPECIAL`. When non-empty, it calls `setAndContinue(RawAnimation.begin().thenLoop(animName))` — a standard looping animation driven by the state handler. When empty or the entity is moving, it returns `PlayState.STOP`.

```java
// "Sustained" controller (client-side, registered alongside Locomotion + Attack)
new AnimationController<>(this, "Sustained", 2, state -> {
    String activeSpecial = entityData.get(ACTIVE_LOOP_SPECIAL);
    if (activeSpecial.isEmpty() || state.isMoving()) {
        return PlayState.STOP;
    }
    return state.setAndContinue(RawAnimation.begin().thenLoop(activeSpecial));
})
```

Sound for SUSTAIN: a client-side `TickableSound` (or equivalent) is started and stopped in response to `ACTIVE_LOOP_SPECIAL` changes via a synced data listener. The `AbstractTickableSoundInstance` loops while the special is active and stops when it clears.

**Why a synced field and not a triggerable**: Triggerables are one-shot — they fire and return to the state handler when the animation completes. A LOOP animation never completes; there is no completion callback. The synced field acts as a continuous state signal: the animation plays as long as the state is set, not just for a fixed duration. This is architecturally identical to how vanilla handles sustained sounds on entities.

**Why `ACTIVE_LOOP_SPECIAL` is a `String` and not `Boolean`**: Multiple loop specials may exist in future (Puffball has `puff` and `puff2`). The string directly encodes which animation to play — no secondary lookup needed. Empty string is the sentinel for "none active", consistent with the existing `TEXTURE_VARIANT` / `MODEL_VARIANT` / `ANIMATOR_VARIANT` pattern already used throughout the codebase.

---

## Data Fix: `sing2` Loop Behavior Correction

`sing2` was incorrectly declared as `LoopBehavior.PLAY_ONCE` in `MandrakeType.FLOWER_PROFILE` and `FRUCTUS_PROFILE`. This discarded the true design intent.

**Decision**: Correct the declaration:
```java
.add("sing2", 1, LoopBehavior.LOOP)   // was PLAY_ONCE — corrected
```

The `pickRandomSpecial()` routing logic reads `pool.getLoopBehavior(selectedAnimName)` to determine which path (SNAP or SUSTAIN) to use. The data is the authority; no hardcoded branching by animation name.

---

## Animation-Sound Linkage — How It Works End to End

### SNAP special (e.g., `sing`)

```
Server: customServerAiStep selects "sing" (PLAY_ONCE)
  → triggerAnim("Locomotion", "sing")         — GeckoLib syncs to clients
  → playSound(SoundFeature.INTERACT["sing"])  — server plays once
Client: GeckoLib fires "sing" triggerable     — plays once, returns to locomotion
```

### SUSTAIN special (e.g., `sing2`)

```
Server: customServerAiStep selects "sing2" (LOOP)
  → entityData.set(ACTIVE_LOOP_SPECIAL, "sing2")  — syncs to clients
  → playSound(...)                                — not played server-side; client handles
Client: Sustained controller sees "sing2" → thenLoop("sing2") — loops indefinitely
Client: SynchedEntityData listener sees ACTIVE_LOOP_SPECIAL → starts TickableSound
Server: entity moves or takes damage
  → entityData.set(ACTIVE_LOOP_SPECIAL, "")  — syncs to clients
Client: Sustained controller sees "" → PlayState.STOP — animation stops
Client: TickableSound detects empty → fades out and stops
```

### Standard ambient sound (e.g., `mandrakesounds`)

```
Server: Minecraft ambient tick fires
  → MonsterEntity.getAmbientSound() → SoundFeature.getSound(AMBIENT, modelVariant)
  → Returns MonstersSounds.MANDRAKE_CHORUS_AMBIENT
Server: Minecraft plays the sound
```

---

## Animation-Linked Sound Keys in `SoundFeature`

Animation-linked sounds use `SoundType.INTERACT` with the animation name as the variant key:

```java
new SoundFeature()
    .withSound(AMBIENT,  MonstersSounds.MANDRAKE_FLOWER_AMBIENT)  // mandrake_song (periodic ambient)
    .withSound(HURT,     MonstersSounds.MANDRAKE_HURT)
    .withSound(DEATH,    MonstersSounds.MANDRAKE_DEATH)
    .withSound(INTERACT, "sing",  MonstersSounds.MANDRAKE_FLOWER_AMBIENT)  // also plays on sing
    .withSound(INTERACT, "sing2", MonstersSounds.MANDRAKE_FLOWER_AMBIENT)  // sustain loop
```

The same sound event can appear as both `AMBIENT` (Minecraft's periodic tick) and `INTERACT["sing*"]` (animation-driven). This is intentional — the Flower Mandrake's identity sound is her humming. The two triggers independently play the same sound, which is correct behaviour.

---

## File Structure

### New files

```
HZLib Common:
  net.heriazone.hzlib.api.entity.features.SoundFeature   ← extended (existing)

Monsters & Girls Common:
  net.heriazone.monsters_girls.source.MonstersSounds      ← new

Monsters & Girls Fabric (+ Forge + NeoForge):
  MonstersSounds.register() called from each loader init  ← new call site
```

### Modified files

```
HZLib Common:
  SoundFeature.java                    ← variant-keyed API added

Monsters & Girls Common:
  MonsterEntity.java                   ← getAmbientSound/getHurtSound/getDeathSound wired
  MandrakeType.java                    ← sing2 corrected to LOOP; SoundFeature attached
  MushroomType.java                    ← SoundFeature attached
  SlimeType.java                       ← SoundFeature attached
  WispType.java                        ← SoundFeature attached
  SpookType.java                       ← SoundFeature attached
  GlobberieType.java                   ← SoundFeature attached (death only)
  GourdragoraType.java                 ← SoundFeature attached (variant-keyed ambient)

Monsters & Girls Fabric:
  WildTamableEntity.java               ← ACTIVE_LOOP_SPECIAL synced field
                                          Sustained controller registered
                                          SNAP/SUSTAIN routing in customServerAiStep
                                          INTERACT sound playback at trigger time
```

---

## Implementation Order

```
1. Fix sing2 LoopBehavior in MandrakeType (data correction, unblocks everything else)
2. Extend SoundFeature with variant-keyed API (HZLib Common)
3. Create MonstersSounds registry class (Monsters Common)
4. Wire loader-specific registration (Fabric/Forge/NeoForge)
5. Wire MonsterEntity getAmbientSound/getHurtSound/getDeathSound
6. Add ACTIVE_LOOP_SPECIAL synced field to WildTamableEntity
7. Add Sustained controller in buildAndRegisterLocomotionController
8. Update pickRandomSpecial routing (SNAP vs SUSTAIN dispatch)
9. Add INTERACT sound playback at triggerAnim call sites
10. Attach SoundFeature to each NativeEntityType (per entity family)
```

---

## Consequences

### Positive

- Every future entity family uses exactly the same pattern: attach `SoundFeature`, done
- Variant-keyed sounds handle Gourdragora, future size-variant entities, and per-form audio without any special-casing
- `LoopBehavior` in `AnimationPool` is now the single authoritative source for animation lifecycle — the routing logic reads it, never overrides it
- SUSTAIN animations mirror the vanilla pattern for looping mob sounds — familiar, well-understood, correct
- Sound and animation lifecycle are unified: the same trigger that starts the animation starts the sound

### Negative

- `ACTIVE_LOOP_SPECIAL` adds one `SynchedEntityData<String>` to every `WildTamableEntity` instance, even those with no LOOP specials. The cost is negligible (one String sync per entity per tick when it changes, which is rare)
- The client-side `TickableSound` for SUSTAIN sounds requires a small amount of Fabric-specific client code — it cannot be in Common

### Risks

- **SUSTAIN sound desync**: If the client TickableSound and `ACTIVE_LOOP_SPECIAL` get out of sync (e.g., entity leaves render range), the sound may persist after the animation stops. Standard Minecraft `TickableSound` handles this via `isStopped()` checks each tick — this must be implemented correctly
- **Multiple LOOP specials simultaneously**: The current design allows only one `ACTIVE_LOOP_SPECIAL` at a time. If `pickRandomSpecial()` selects a LOOP special while one is already active, the old one is silently replaced. This is the correct behaviour for idle specials (you don't double-stack them), but should be documented

---

## Related Decisions

- ADR_010: Animation Profile System — established `AnimationProfile`, `AnimationPool`, `LoopBehavior`; this ADR extends the lifecycle model to include SUSTAIN animations
- ADR_011: Variant and Spawn System Refactoring — established `MODEL_VARIANT` synced field; this ADR uses it as the variant key for sound lookup
- ADR_012: InternalEntity Consolidation — established the `withFeature()` / `getFeature()` pattern; `SoundFeature` is attached via this system
- ADR_015: EmanationFeature — parallel design pattern: feature declared on type, per-instance state on entity; `ACTIVE_LOOP_SPECIAL` follows the same model
