# ADR 022: Animation System Unification

**Status**: Accepted
**Date**: 2026-07-01
**Decision Makers**: Development Team
**Related Checklist Items**: Pre-Publish Checklist items 2, 3, 8
**Audit Source**: `docs/development/notes/Animation_Architecture_Audit.md`
**Related Documents**:
- `ADR_010_Animation_Profile_System.md`
- `ADR_016_Sound_and_Animation_Lifecycle_System.md`
- `ADR_021_Composite_Appearance_Feature.md`

---

## Context

The audit (`Animation_Architecture_Audit.md`) found nine architectural smells across
HZLib and LovelyLib. The core problem is a single missing wire: **`RobotFamily.configureVariants()`
never attaches an `AnimationProfile` to its `StandardAnimatorVariant` registration**.
Because of this, `AnimationStateManager.resolveProfile()` returns `null` for every robot
entity. The entire profile-aware animation path is dead for robots. They fall back to the
hardcoded `isInSittingPose()` branch — which is driven by `handleStandbyAnimation()`, which
is the source of the standby flicker (checklist item 3).

The remaining smells are consequences of that missing wire, plus accumulated debt from the
pre-profile era:

| # | Smell | Root cause |
|---|-------|-----------|
| 1 | `TailAnimationUtils` uses Java reflection to call `getBone`/`setHidden` | Common cannot import GeckoLib — no clean bone API exists |
| 2 | `NativeModel.setCustomAnimations()` hardcodes `"head"` | No family-level bone name declaration |
| 3 | `RobotAnimation` duplicates `NativeAnimation` across 3 loaders | Needed to add tail/head calls that had no home |
| 4 | `KitsuneModel` subclass exists only to call tail visibility | Tail has no declarative home |
| 5 | `EntityAnimation`, `EntityModel`, `EntityVariantModel` enums — dead | Pre-profile era remnants |
| 6 | `STANDBY_TICKS` / `STANDBY_TARGET_TICKS` in schema with no future writer | Will outlive `handleStandbyAnimation()` |
| 7 | No `AnimationProfile` on any robot `StandardAnimatorVariant` | Root missing wire |
| 8 | `handleStandbyAnimation()` drives animation through entity tick logic | Compensating for missing profile |
| 9 | Duplicate `BoneTransformations.java` in lovelylib/Common | Copy-paste before HZLib version existed |

The Sentry robot (new family, not yet built) additionally requires conditional bone
visibility based on which appearance variant is active — the wing-bone conflict between
dragon and honey forms. This cannot be expressed by `AnimationProfile`, `OverlayFeature`,
or the tail system. It requires a new first-class feature.

**Items 2 and 3 from the checklist are the same problem.** Item 3 (standby flicker) only
exists because item 2 (animation improvements) was never completed properly. The correct
fix addresses both at once by closing the missing-wire gap and introducing the minimal
new HZLib APIs needed to make it declarative.

---

## Decision

Five changes, ordered by dependency. Each change is self-contained and shippable
independently, but together they eliminate all nine smells.

---

## Change A — `BoneVisibilityFeature` in HZLib (new feature)

**Location**: `sources/common/hzlib-1.21.1/Common/.../features/BoneVisibilityFeature.java`

### What it is

A new `NativeEntityFamily` feature that declares which model bones should be hidden
under which runtime conditions. Conditions are evaluated in `NativeModel.setCustomAnimations()`
each frame — exactly where GeckoLib bone state is applied.

This covers three distinct use cases with one API:
1. **Level-progressive unlock** — Kitsune tails visible only above a level threshold
2. **Appearance-variant conflict** — Sentry wings: `UWings` hidden in dragon form,
   `UWingsno` hidden in honey form
3. **Any future conditional bone** — state-dependent geometry without a model-file change

### API

```java
// net.heriazone.hzlib.api.entity.features.BoneVisibilityFeature
public final class BoneVisibilityFeature implements NativeFeature {

    private final List<BoneRule> rules;

    public BoneVisibilityFeature(List<BoneRule> rules) {
        this.rules = List.copyOf(rules);
    }

    public List<BoneRule> getRules() { return rules; }

    // -- Builder --

    public static Builder builder() { return new Builder(); }

    public static final class Builder {

        private final List<BoneRule> rules = new ArrayList<>();

        /**
         * Hides the named bone (and all its children) when the condition returns true.
         *
         * @param boneName  exact bone name as declared in the .geo.json file
         * @param condition evaluated each frame; receives the entity being rendered
         */
        public Builder hideWhen(String boneName, BoneCondition condition) {
            rules.add(new BoneRule(boneName, condition, true));
            return this;
        }

        /**
         * Shows the named bone when the condition returns true.
         * Useful when the default is hidden and needs to be selectively revealed.
         */
        public Builder showWhen(String boneName, BoneCondition condition) {
            rules.add(new BoneRule(boneName, condition, false));
            return this;
        }

        public BoneVisibilityFeature build() {
            return new BoneVisibilityFeature(rules);
        }
    }
} // Class: BoneVisibilityFeature
```

```java
// net.heriazone.hzlib.api.entity.features.BoneRule
public record BoneRule(
    String boneName,
    BoneCondition condition,
    boolean hideWhenTrue   // true = hide when condition passes, false = show when condition passes
) {}
```

```java
// net.heriazone.hzlib.api.entity.features.BoneCondition
@FunctionalInterface
public interface BoneCondition {
    /**
     * Evaluated client-side each render frame.
     * Must be fast — no world queries, no allocations.
     *
     * @param entity the entity being rendered
     * @return whether this rule's hide/show action should apply
     */
    boolean test(NativeEntity entity);
} // Interface: BoneCondition
```

### Wire-up in `NativeModel.setCustomAnimations()`

`NativeModel` already calls `NativeAnimation.headAnimation()`. Add one more call after it:

```java
@Override
public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> event) {
    // Head tracking — bone name from family descriptor (see Change B)
    String headBone = resolveHeadBoneName(animatable);
    NativeAnimation.headAnimation(this, event, headBone);

    // Conditional bone visibility declared on the family
    if (animatable.nativeEntity != null) {
        animatable.nativeEntity.getFeature(BoneVisibilityFeature.class)
            .ifPresent(feature -> applyBoneVisibility(feature, animatable));
    }
}

private void applyBoneVisibility(BoneVisibilityFeature feature, T animatable) {
    for (BoneRule rule : feature.getRules()) {
        GeoBone bone = getAnimationProcessor().getBone(rule.boneName());
        if (bone == null) continue;
        boolean conditionMet = rule.condition().test(animatable);
        // hideWhenTrue=true  → hide if condition met
        // hideWhenTrue=false → show (un-hide) if condition met
        bone.setHidden(rule.hideWhenTrue() == conditionMet);
    }
}

private String resolveHeadBoneName(T animatable) {
    // See Change B — reads from family descriptor
    return "head"; // placeholder until Change B lands
}
```

No reflection. No subclassing. GeckoLib types stay in loader modules where they belong.


### `BoneVisibilityFeature` declarations

**Kitsune** (replaces reflection in `TailAnimationUtils.configureTailVisibility()`):

```java
// In LegacyRobotFamilies / RebootRobotFamilies for the KITSUNE family
.withFeature(BoneVisibilityFeature.class, BoneVisibilityFeature.builder()
    // Base tail — visible only below first level threshold
    .showWhen("tail0",  e -> e instanceof RobotEntity r && r.getCurrentLevel() < levelPerTail(r))
    .hideWhen("tail0",  e -> e instanceof RobotEntity r && r.getCurrentLevel() >= levelPerTail(r))
    // Tails 1–8 — visible when level >= threshold for that tail index
    .showWhen("tail01", e -> e instanceof RobotEntity r && r.getCurrentLevel() >= levelPerTail(r) * 1)
    .showWhen("tail02", e -> e instanceof RobotEntity r && r.getCurrentLevel() >= levelPerTail(r) * 2)
    // ... tail03–tail08 follow the same pattern ...
    .hideWhen("tail01", e -> e instanceof RobotEntity r && r.getCurrentLevel() < levelPerTail(r) * 1)
    // ... matching hide rules ...
    // Final tail — requires max level
    .showWhen("tail09", e -> e instanceof RobotEntity r && r.getCurrentLevel() >= r.getMaxLevel())
    .hideWhen("tail09", e -> e instanceof RobotEntity r && r.getCurrentLevel() < r.getMaxLevel())
    .build())
```

In practice, a `BoneVisibilityConditions` utility class in LovelyLib provides
factory methods for the common cases:

```java
// net.heriazone.lovelylib.common.entity.features.BoneVisibilityConditions
public final class BoneVisibilityConditions {

    /** Show this tail bone index when the robot's level crosses the per-tail threshold. */
    public static BoneCondition tailVisible(int tailIndex) {
        return entity -> {
            if (!(entity instanceof RobotEntity r)) return false;
            int maxLevel = (r.nativeEntity instanceof RobotFamily rf) ? rf.getMaxLevel() : 0;
            if (maxLevel <= 0) return false;
            int threshold = (maxLevel / 8) * tailIndex;
            return r.getCurrentLevel() >= threshold;
        };
    }

    /** Show this bone when the entity's active texture variant key equals the given key. */
    public static BoneCondition textureVariantIs(String variantKey) {
        return entity -> variantKey.equals(entity.getTextureVariant());
    }

    /** Hide this bone when the entity's active texture variant key equals the given key. */
    public static BoneCondition textureVariantIsNot(String variantKey) {
        return entity -> !variantKey.equals(entity.getTextureVariant());
    }
}
```

**Sentry** (wing bone conflict — declared once on the Sentry family):

```java
// In RebootRobotFamilies for the SENTRY family
.withFeature(BoneVisibilityFeature.class, BoneVisibilityFeature.builder()
    // Dragon form: hide UWings (and sub-bones) — texture conflicts with UWingsno
    .hideWhen("UWings",   BoneVisibilityConditions.textureVariantIs("sentry_dragon"))
    // Honey form: hide UWingsno, leave UWings visible
    .hideWhen("UWingsno", BoneVisibilityConditions.textureVariantIs("sentry_honey"))
    .build())
```

No model override. No renderer override. No subclass. The Sentry family declaration
carries its own bone-conflict rules; `NativeModel.setCustomAnimations()` evaluates
them for every entity that has `BoneVisibilityFeature` declared.

---

## Change B — Head bone name on `NativeEntityFamily`

**Location**: `sources/common/hzlib-1.21.1/Common/.../entity/NativeEntityFamily.java`

Add a single field:

```java
/** Head bone name for look-tracking in setCustomAnimations(). Defaults to "head". */
private String headBoneName = "head";

public String getHeadBoneName()                { return headBoneName; }
public NativeEntityFamily<?> headBone(String n){ this.headBoneName = n; return this; }
```

Update `NativeModel.resolveHeadBoneName()` to read it:

```java
private String resolveHeadBoneName(T animatable) {
    return (animatable.nativeEntity != null)
        ? animatable.nativeEntity.getHeadBoneName()
        : "head";
}
```

This closes smell 2. All existing families implicitly use `"head"` — zero migration.
Future families with a different head bone name configure it in their family declaration.

---

## Change C — `AnimationProfile` attached to every `RobotFamily` variant (root fix)

**Location**: `sources/common/lovelylib-1.21.1/Common/.../entity/RobotFamily.java`
— `configureVariants()`

This is the root missing wire. `StandardAnimatorVariant` currently receives no profile:

```java
// BEFORE — profile missing
VariantRegistries.ANIMATORS.register(
    new StandardAnimatorVariant(defaultAnimKey, defaultAnimKey,
        LovelyIdentifier.getId("animations/default.animation.json").toString(),
        0)   // ← no profile
);
```

Every robot family needs a profile. The base profile shared across all robots:

```java
// AFTER — base profile always attached
private static final AnimationProfile ROBOT_BASE_PROFILE = AnimationProfile.builder()
    .idle("idle")
    .walk("walk")
    .rest("rest")
    .sit("sit")
    .attack(pool -> pool.add("attack", LoopBehavior.INTERRUPT))
    .build();

VariantRegistries.ANIMATORS.register(
    new StandardAnimatorVariant(defaultAnimKey, defaultAnimKey,
        LovelyIdentifier.getId("animations/default.animation.json").toString(),
        ROBOT_BASE_PROFILE,   // ← profile now attached
        0)
);
```

With the profile attached, `AnimationStateManager.resolveProfile()` returns it instead
of `null`. The entire profile-aware code path activates for robots. The hardcoded
`isInSittingPose()` fallback branch in `getLocomotionAnimation()` is no longer the
active path.

**Consequence for the vehicle-riding path**: The base profile declares a `sit` pool.
When `entity.getVehicle() != null` the manager returns `"sit"`. Tribute families
override this by declaring a profile with **no `sit` pool** — the vehicle branch then
falls through to `IDLE` (see vehicle fallback fix in Change D).


---

## Change D — `IdleSlot` system in HZLib + `idleStationaryTicks` on `NativeEntity`

**Location**: `sources/common/hzlib-1.21.1/Common/.../animation/` + `NativeEntity.java`

### Why this is needed

Once robots have an `AnimationProfile` (Change C), the `rest`/`sit` split in
`getLocomotionAnimation()` must be driven by something other than `handleStandbyAnimation()`.
The `isInSittingPose()` branch must eventually be replaced. The idle slot system is the
correct replacement: it moves the "how long has the entity been stationary" concern from
entity tick logic into the animation resolution layer, which is where it belongs.

### `IdleCondition` + `IdleSlot` (HZLib Common)

Two new pure-Java types in `net.heriazone.hzlib.api.animation`:

```java
@FunctionalInterface
public interface IdleCondition {
    /**
     * Evaluated each locomotion controller tick (client-side).
     * Must be fast — no world queries, no allocations.
     */
    boolean test(NativeEntity entity);
}
```

```java
public final class IdleSlot {
    private final AnimationPool pool;
    private final IdleCondition condition;
    private final int priority;                   // higher = evaluated first; 0 = fallback
    private final int activationThresholdTicks;   // ticks condition must hold before activating

    // Constructor + accessors (standard)
}
```

### `idleStationaryTicks` on `NativeEntity`

A single non-synced int field on `NativeEntity`, incremented each tick the entity is
not moving and not in a vehicle, reset on first movement or state change:

```java
// In NativeEntity
private int idleStationaryTicks = 0;

public int  getIdleStationaryTicks()    { return idleStationaryTicks;  }
public void resetIdleStationaryTicks()  { idleStationaryTicks = 0;     }

// Called from tick() on both sides:
boolean isMovingNow = getDeltaMovement().lengthSqr() > 0.0001 || getVehicle() != null;
if (isMovingNow) resetIdleStationaryTicks();
else             idleStationaryTicks++;
```

This replaces `standbyTicks` in `RobotEntity`. It lives in `NativeEntity` because the
concept — how long has this entity been idle — is not robot-specific.

### `AnimationProfile` gains `idleSlots`

```java
// In AnimationProfile.Builder:
private final List<IdleSlot> idleSlots = new ArrayList<>();

public Builder idleSlot(AnimationPool pool, IdleCondition condition,
                        int priority, int activationThresholdTicks) {
    idleSlots.add(new IdleSlot(pool, condition, priority, activationThresholdTicks));
    return this;
}

// AnimationProfile exposes:
public List<IdleSlot> getIdleSlots() { return Collections.unmodifiableList(idleSlots); }
```

The existing `.idle("idle_stand")` shorthand implicitly creates a priority-0,
always-true, zero-threshold slot. This guarantees every profile has a fallback.

### Updated `AnimationStateManager.getLocomotionAnimation()`

```java
public static String getLocomotionAnimation(NativeEntity entity, boolean isMoving) {
    AnimationProfile profile = resolveProfile(entity);

    // Vehicle riding — use ride pool, fall back to sit, fall back to IDLE
    // (not SIT constant — families without sit/ride declared should show idle in vehicles)
    if (entity.getVehicle() != null) {
        if (profile != null) {
            String r = selectFromSlot(profile.getRide(), null);
            if (r != null) return r;
            String s = selectFromSlot(profile.getSit(), null);
            if (s != null) return s;
        }
        return IDLE;  // fallback — not SIT, so families without sit don't show sit in vehicles
    }

    if (isMoving) {
        return selectFromSlot(profile != null ? profile.getWalk() : null, WALK);
    }

    // Idle / standby path — evaluate idle slots if declared
    if (profile != null && !profile.getIdleSlots().isEmpty()) {
        return resolveIdleSlot(entity, profile);
    }

    // Legacy fallback for entities without idle slots (old isInSittingPose path)
    if (entity.getCurrentState() == EntityState.Standby) {
        if (entity.isInSittingPose()) {
            return selectFromSlot(profile != null ? profile.getSit() : null, SIT);
        }
        return selectFromSlot(profile != null ? profile.getRest() : null, REST);
    }

    return selectFromSlot(profile != null ? profile.getIdle() : null, IDLE);
}

private static String resolveIdleSlot(NativeEntity entity, AnimationProfile profile) {
    List<IdleSlot> sorted = profile.getIdleSlots().stream()
        .sorted(Comparator.comparingInt(IdleSlot::getPriority).reversed())
        .toList();

    for (IdleSlot slot : sorted) {
        if (slot.getCondition().test(entity)
                && entity.getIdleStationaryTicks() >= slot.getActivationThresholdTicks()) {
            String name = slot.getPool().selectNext(RANDOM);
            return name != null ? name : IDLE;
        }
    }
    return selectFromSlot(profile.getIdle(), IDLE);
}
```

**Stability guarantee**: `idleStationaryTicks` grows monotonically during idle.
The threshold comparison `>= N` is stable once crossed — the same slot wins on
every controller tick until the entity moves. `selectNext(RANDOM)` fires once at
activation boundary, not every frame. Flicker eliminated structurally.

### Legacy/Reboot profile declarations with idle slots

```java
// For all Legacy and Reboot families:
private static final AnimationProfile ROBOT_BASE_PROFILE = AnimationProfile.builder()
    .idle("idle")   // priority-0 always-true fallback
    .idleSlot(
        AnimationPool.single("rest"),
        entity -> entity.getCurrentState() == EntityState.Standby,
        /* priority */            1,
        /* thresholdTicks */      0    // activates immediately on Standby entry
    )
    .idleSlot(
        AnimationPool.single("sit"),
        entity -> entity.getCurrentState() == EntityState.Standby,
        /* priority */            2,
        /* thresholdTicks */      SharedConfigs.Common.StandbyToSitDelayMin
    )
    .walk("walk")
    .rest("rest")   // kept for legacy fallback path compatibility
    .sit("sit")     // kept for vehicle and legacy fallback path compatibility
    .attack(pool -> pool.add("attack", LoopBehavior.INTERRUPT))
    .build();
```

- On Standby entry → `idleStationaryTicks` resets → `rest` slot (priority 1, threshold 0)
  activates immediately.
- After `StandbyToSitDelayMin` ticks → `sit` slot (priority 2) activates, overriding rest.
- On movement → `idleStationaryTicks` resets → both conditions fail → `idle` (priority 0).

### `handleStandbyAnimation()` deletion

Once idle slots are active, `handleStandbyAnimation()` in `RobotEntity` and its two
timer fields (`standbyTicks`, `standbyTargetTicks`) are deleted. `IS_IN_SITTING_POSE`
is kept for hitbox purposes — it is updated via `onIdleSlotChanged()` hook:

```java
// NativeEntity — new hook, called from resolveIdleSlot() when the winning slot changes
protected void onIdleSlotChanged(IdleSlot previousSlot, IdleSlot newSlot) {
    // No-op in base. Overridden in RobotEntity:
}

// RobotEntity override:
@Override
protected void onIdleSlotChanged(IdleSlot previousSlot, IdleSlot newSlot) {
    boolean nowSitting = newSlot != null && newSlot.getPool().getAnimations()
        .stream().anyMatch(a -> a.getName().equals("sit"));
    boolean wasSitting = previousSlot != null && previousSlot.getPool().getAnimations()
        .stream().anyMatch(a -> a.getName().equals("sit"));
    if (nowSitting && !wasSitting)  { setInSittingPose(true);  refreshDimensions(); }
    if (!nowSitting && wasSitting)  { setInSittingPose(false); refreshDimensions(); }
}
```

The `STANDBY_TICKS` and `STANDBY_TARGET_TICKS` schema fields are retained in
`RobotFamily.configureSchema()` as load-only entries (read but never written) so
existing saves with these fields don't produce migration errors. They are marked
`@Deprecated` in `RobotFields`.

### Tribute — no rest or sit

Tribute `AnimationProfile` declares **no idle slots** and no `sit` pool:

```java
private static final AnimationProfile TRIBUTE_PROFILE = AnimationProfile.builder()
    .idle("idle")    // the only idle state — no rest, no sit
    .walk("walk")
    .attack(pool -> pool.add("attack", LoopBehavior.INTERRUPT))
    .build();
```

- No idle slots → `resolveIdleSlot()` is never called → always returns `"idle"` in standby.
- No `sit` pool → vehicle path returns `IDLE` (not `"sit"`) per the updated vehicle branch.
- No code changes in `TributeRobotEntity`. No overrides. No no-op methods.


---

## Change E — Delete `RobotAnimation`, `KitsuneModel`, dead enums, duplicate `BoneTransformations`

This change consolidates the loader-side animation code so LovelyLib matches the
Monsters & Girls pattern: **no duplication of `NativeAnimation`**.

### Delete `RobotAnimation.java` (all 3 loaders)

`RobotAnimation` re-implements `NativeAnimation` controllers and adds two bone calls
(`headAnimation`, `tailConfigAnimation`) that will be moved into `NativeModel` by
Changes A and B. After those changes land, `RobotAnimation` is an exact subset of
`NativeAnimation` and is deleted.

**Migration**: Update `TributeRobotEntity.registerControllers()` and
`NativeRobotEntity.registerControllers()` (all loaders) to call `NativeAnimation`
directly:

```java
// BEFORE (RobotAnimation — deleted)
@Override
public void registerControllers(AnimatableManager.ControllerRegistrar reg) {
    reg.add(RobotAnimation.locomotionAnimation(this));
    reg.add(RobotAnimation.attackAnimation(this));
}

// AFTER (NativeAnimation — HZLib)
@Override
public void registerControllers(AnimatableManager.ControllerRegistrar reg) {
    reg.add(NativeAnimation.locomotionAnimation(this));
    reg.add(NativeAnimation.attackAnimation(this));
}
```

### Delete `KitsuneModel.java` (all 3 loaders)

`KitsuneModel` only existed to call `RobotAnimation.tailConfigAnimation()`, which
called `TailAnimationUtils.configureTailVisibility()`, which used reflection.
After Change A (`BoneVisibilityFeature` in `NativeModel.setCustomAnimations()`),
bone visibility for Kitsune is handled automatically. `KitsuneModel` becomes identical
to `NativeRobotModel` and is deleted.

**Migration**: Update the Kitsune entity/renderer registration (all loaders) to use
`NativeRobotModel` instead of `KitsuneModel`.

### Delete dead enum files (LovelyLib Common)

| File | Reason |
|------|--------|
| `EntityAnimation.java` | Pre-profile animation state enum — zero references in active code |
| `EntityModel.java` | Pre-profile model variant enum — zero references in active code |
| `EntityVariantModel.java` | Duplicate of EntityModel concept — zero references |

### Delete `lovelylib/Common/.../api/animation/BoneTransformations.java`

Dead duplicate of `hzlib/.../animation/BoneTransformations.java`. Zero references
in active lovelylib source. Delete; callers in lovelylib (none currently) would import
the HZLib version instead.

### Delete `TailAnimationUtils.configureTailVisibility()` reflection method

After `BoneVisibilityFeature` (Change A) handles Kitsune tail visibility declaratively,
the reflection-based `configureTailVisibility()` method in `TailAnimationUtils` is
deleted. `TailAnimationUtils.calculateTailVisibility()` and `TailVisibilityConfig` are
also deleted — their logic moves into the lambda conditions on `BoneVisibilityFeature`.

If the pure-Java level-threshold calculation is still useful as a utility, it moves into
`BoneVisibilityConditions.tailVisible(int tailIndex)` (shown in Change A).

---

## Change F — Item 8: Tribute AI Goals

`TributeRobotEntity` overrides `registerGoals()` with the original LovelyRobot goal set.

### New class: `AiTributeReturnToBaseGoal`

**Location**: `sources/common/lovelylib-1.21.1/Common/.../entity/goal/AiTributeReturnToBaseGoal.java`

Direct translation of `EntityAIBunnyFollowPoint` from `temp/original/` into the
1.21.1 goal API. Activates in `EntityState.Defense`, paths to `BASE_X/Y/Z`, and
teleports to a valid adjacent block if pathfinding fails and distance exceeds warp range.

```java
/**
 * Tribute defense goal — returns robot to its saved base coordinates.
 * <p>
 * Faithful recreation of the original LovelyRobot {@code EntityAIBunnyFollowPoint}.
 * Intentionally does not implement the Legacy/Reboot PATROL→GUARD cycle
 * ({@link AiBaseDefenseGoal}) — that state machine is a Legacy/Reboot feature.
 */
public class AiTributeReturnToBaseGoal extends Goal {
    private final RobotEntity entity;
    private final PathNavigation navigation;
    private final double speed;
    private final float minDistance;   // activate threshold
    private final float warpDistance;  // teleport threshold
    private int recalcCountdown = 0;
    private float oldWaterCost;

    @Override
    public boolean canUse() {
        return entity.getCurrentState() == EntityState.Defense
            && !entity.isOrderedToSit()
            && entity.distanceToSqr(entity.getBaseX(), entity.getBaseY(), entity.getBaseZ())
               >= minDistance * minDistance;
    }

    @Override
    public void tick() {
        entity.getLookControl().setLookAt(entity.getBaseX(), entity.getBaseY(),
            entity.getBaseZ(), 10f, (float) entity.getMaxHeadXRot());

        if (--recalcCountdown <= 0) {
            recalcCountdown = 10;
            double distSq = entity.distanceToSqr(
                entity.getBaseX(), entity.getBaseY(), entity.getBaseZ());
            if (!navigation.moveTo(entity.getBaseX(), entity.getBaseY(),
                    entity.getBaseZ(), speed) && distSq >= warpDistance * warpDistance) {
                tryTeleportToBase();  // 5×5 solid-block scan from original
            }
        }
    }
    // canContinueToUse, start, stop, tryTeleportToBase — standard implementations
}
```

### `TributeRobotEntity.registerGoals()` override (all loaders)

```java
@Override
protected void registerGoals() {
    goalSelector.addGoal(1, new FloatGoal(this));
    goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
    goalSelector.addGoal(3, new MeleeAttackGoal(this,
        SharedConfigs.Common.MovementMeleeAttack, true));

    // Follow owner — wolf-style, Follow state
    goalSelector.addGoal(4, new AiFollowOwnerGoal(this,
        SharedConfigs.Common.MovementFollowOwner,
        SharedConfigs.Common.FollowDistanceMin,
        SharedConfigs.Common.FollowDistanceMax));

    // Return to base — Defense state (original BunnyFollowPoint equivalent)
    goalSelector.addGoal(4, new AiTributeReturnToBaseGoal(this,
        SharedConfigs.Common.MovementFollowOwner,
        SharedConfigs.Common.BaseDefenceRange,
        SharedConfigs.Common.BaseDefenceWarpRange));

    // Vanilla wander — no owner-stationary detection (matches original mod behavior)
    goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this,
        SharedConfigs.Common.MovementWanderAround));

    goalSelector.addGoal(7, new AiConditionalLookGoal(this,
        Player.class, SharedConfigs.Common.LookRange));
    goalSelector.addGoal(7, new AiConditionalLookGoal(this,
        LivingEntity.class, SharedConfigs.Common.LookRange));
    goalSelector.addGoal(8, new AiConditionalRandomLookGoal(this));

    targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
    targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
    targetSelector.addGoal(3, new HurtByTargetGoal(this));
    targetSelector.addGoal(4, new AiAutoAttackGoal<>(this, Mob.class,
        SharedConfigs.Common.AttackChance, true, false,
        e -> e instanceof Monster && !(e instanceof Creeper)));
}
```

**Why `WaterAvoidingRandomStrollGoal` instead of `AiConditionalWanderGoal`**:
The original mod used vanilla wander with no owner-stationary detection. The
owner-tracking, cooldown, and duration logic in `AiConditionalWanderGoal` are
Legacy/Reboot additions. Using vanilla wander for Tribute preserves the original feel.

**Why not `AiBaseDefenseGoal`**: Its PATROL→GUARD state machine with scan patterns
and 30–45s cycle is a Legacy/Reboot feature. Tribute gets point-return only.

---

## Implementation Order

Steps are ordered by dependency. Each step is independently shippable.

```
Step 1  Add BoneCondition + BoneRule + BoneVisibilityFeature to HZLib Common
Step 2  Update NativeModel.setCustomAnimations() to evaluate BoneVisibilityFeature
           and read head bone name from family (headBoneName field)
Step 3  Add headBoneName field to NativeEntityFamily
Step 4  Add IdleCondition + IdleSlot to HZLib Common animation package
Step 5  Add idleSlots list + idleSlot() builder method to AnimationProfile
Step 6  Add idleStationaryTicks to NativeEntity.tick()
Step 7  Update AnimationStateManager.getLocomotionAnimation():
           a. Vehicle branch fallback → IDLE when no ride/sit pool declared
           b. Add resolveIdleSlot() and idle-slot evaluation path
           c. Keep legacy isInSittingPose() branch for entities without idle slots
Step 8  Add onIdleSlotChanged() hook to NativeEntity; override in RobotEntity
           to update IS_IN_SITTING_POSE and refresh dimensions
Step 9  Build ROBOT_BASE_PROFILE (with idle slots) and attach it in RobotFamily.configureVariants()
Step 10 Build TRIBUTE_PROFILE (no sit/rest/idle-slots) and attach in TributeRobotFamilies
Step 11 Declare BoneVisibilityFeature on KITSUNE family
Step 12 Declare BoneVisibilityFeature on SENTRY family (when Sentry is built)
Step 13 Delete handleStandbyAnimation() + standbyTicks + standbyTargetTicks from RobotEntity
           Mark STANDBY_TICKS / STANDBY_TARGET_TICKS as @Deprecated in RobotFields
Step 14 Delete RobotAnimation.java (all 3 loaders)
           Update NativeRobotEntity + TributeRobotEntity registerControllers() → NativeAnimation
Step 15 Delete KitsuneModel.java (all 3 loaders)
           Update Kitsune renderer registration → NativeRobotModel
Step 16 Delete TailAnimationUtils.configureTailVisibility() + calculateTailVisibility()
           Delete TailVisibilityConfig inner class
           If BoneVisibilityConditions.tailVisible() is sufficient, delete TailAnimationUtils entirely
Step 17 Delete lovelylib/Common/.../api/animation/BoneTransformations.java
Step 18 Delete EntityAnimation.java, EntityModel.java, EntityVariantModel.java
Step 19 Add AiTributeReturnToBaseGoal class
Step 20 Override registerGoals() in TributeRobotEntity (all loaders)
```

---

## Architecture After This ADR

```
HZLib Common
├── AnimationProfile          (idle, walk, rest, sit, ride, attack, hurt, idleSlots)
├── IdleSlot + IdleCondition  (NEW — timed conditional idle animation)
├── BoneTransformations       (pure-math head rotation calc)
├── AnimationStateManager     (resolveProfile, getLocomotionAnimation, resolveIdleSlot)
├── BoneVisibilityFeature     (NEW — declarative per-frame bone hiding/showing)
├── BoneCondition + BoneRule  (NEW — condition contracts for bone visibility)
└── NativeEntity              (idleStationaryTicks counter, onIdleSlotChanged hook)

HZLib Loader (Forge/Fabric/NeoForge)
├── NativeAnimation           (controller factories — locomotion, attack, basePose)
│                              headAnimation(renderer, event, headBone)
└── NativeModel               (setCustomAnimations → headAnimation + BoneVisibilityFeature eval)

LovelyLib Common
├── RobotFamily               (headBoneName, AnimationProfile with idle slots, BoneVisibilityFeature)
├── BoneVisibilityConditions  (factory methods: tailVisible, textureVariantIs, textureVariantIsNot)
├── AiTributeReturnToBaseGoal (new goal — original BunnyFollowPoint equivalent)
└── [EntityAnimation/EntityModel/EntityVariantModel deleted]

LovelyLib Loader (Forge/Fabric/NeoForge)
├── NativeRobotModel          (extends NativeModel — one-liner, same as WildTamableModel)
├── NativeRobotEntity         (registerControllers → NativeAnimation.locomotionAnimation)
├── TributeRobotEntity        (registerControllers → NativeAnimation; registerGoals → override)
└── [RobotAnimation deleted, KitsuneModel deleted]
```

---

## Summary of Smells Resolved

| Smell | Resolution |
|-------|-----------|
| 1. Reflection in TailAnimationUtils | Replaced by BoneVisibilityFeature (Change A) |
| 2. Hardcoded `"head"` in NativeModel | headBoneName on NativeEntityFamily (Change B) |
| 3. RobotAnimation duplicates NativeAnimation | RobotAnimation deleted (Change E) |
| 4. KitsuneModel exists only for tail | KitsuneModel deleted (Change E) |
| 5. EntityAnimation/EntityModel/EntityVariantModel dead | Deleted (Change E) |
| 6. STANDBY_TICKS schema fields without writer | Marked @Deprecated, kept read-only (Change D) |
| 7. No AnimationProfile on robot variants | ROBOT_BASE_PROFILE attached (Change C) |
| 8. handleStandbyAnimation() in entity tick | Deleted, replaced by idle slots (Change D) |
| 9. Duplicate BoneTransformations in lovelylib | Deleted (Change E) |
| Sentry wing conflict (new) | BoneVisibilityFeature on Sentry family (Change A) |
