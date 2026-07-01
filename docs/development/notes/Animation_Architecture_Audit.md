# Animation Architecture Audit

**Date**: 2026-07-01
**Scope**: HZLib, LovelyLib, Monsters & Girls — animation system, bone handling,
render pipeline
**Trigger**: Pre-publish checklist items 2 & 3, Sentry robot bone-visibility requirement
**Output**: Feeds into ADR 022 rewrite

---

## What Was Read

| File | Purpose |
|------|---------|
| `hzlib/.../AnimationStateManager.java` | Central animation decision logic |
| `hzlib/.../AnimationProfile.java` | Per-animator-variant slot declarations |
| `hzlib/.../AnimationPool.java` | Pool selection strategies |
| `hzlib/.../BoneTransformations.java` (HZLib) | Pure-math head rotation calc |
| `hzlib/.../NativeAnimation.java` (Forge+Fabric) | GeckoLib boundary — controllers + headAnimation |
| `hzlib/.../NativeModel.java` (Forge) | `setCustomAnimations` — hardcoded `"head"` |
| `lovelylib/.../TailAnimationUtils.java` | Tail visibility — **uses reflection** |
| `lovelylib/.../BoneTransformations.java` (lovelylib copy) | Duplicate of HZLib version |
| `lovelylib/.../RobotAnimation.java` (Forge) | LovelyLib GeckoLib boundary — re-implements controllers |
| `lovelylib/.../NativeRobotModel.java` | Empty — delegates to NativeModel |
| `lovelylib/.../KitsuneModel.java` | Overrides `setCustomAnimations` to add tail |
| `lovelylib/.../NativeRobotRenderer.java` | Layer stack for robots |
| `lovelylib/.../EntityAnimation.java` | Enum — 5 animation states |
| `lovelylib/.../EntityModel.java` | Enum — DEFAULT / ARMED |
| `lovelylib/.../EntityVariantModel.java` | Enum — DEFAULT / ARMED (duplicate concept) |
| `lovelylib/.../RobotFamily.java` | Family descriptor — configureVariants, configureSchema |
| `monsters/.../WildTamableModel.java` | One-liner — extends NativeModel |
| `monsters/.../WildTamableRenderer.java` | Correct pattern — OverlayLayer per slot |
| `monsters/.../GourdragoraFamily.java` | Reference implementation — AnimationProfile with builder |
| `monsters/.../BeeFamily.java` | Simple AnimationProfile usage |

---

## Findings

### SMELL 1 — `TailAnimationUtils.configureTailVisibility()` uses Java reflection (CRITICAL)

**File**: `lovelylib/Common/.../TailAnimationUtils.java`

```java
Object animationProcessor = renderer.getClass()
    .getMethod("getAnimationProcessor").invoke(renderer);
Object baseTail = animationProcessor.getClass()
    .getMethod("getBone", String.class).invoke(animationProcessor, "tail0");
baseTail.getClass().getMethod("setHidden", boolean.class)
    .invoke(baseTail, !config.isBaseTailVisible());
```

This is the root cause of bone-visibility fragility. The Common module cannot import
GeckoLib (that dependency lives in loader modules only), so someone used reflection
to avoid the boundary. The correct pattern — already used by `NativeAnimation.headAnimation()`
in the loader modules — is to **keep bone manipulation in loader-specific code** and pass
only pure-Java values through the Common boundary.

`TailAnimationUtils` currently lives in `lovelylib/Common`. It must move to the
loader-specific animation utility (alongside `RobotAnimation`), or its bone-manipulation
portion must be separated from its calculation logic.

---

### SMELL 2 — `NativeModel.setCustomAnimations()` hardcodes `"head"`

**File**: `hzlib/.../NativeModel.java`

```java
@Override
public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> event) {
    NativeAnimation.headAnimation(this, event, "head");
}
```

`NativeAnimation.headAnimation()` already accepts a `headBone` parameter — it was
designed to be configurable. But the call site hardcodes `"head"`. Families with
a differently-named head bone must override `setCustomAnimations()` in their model
class just to change this string. The bone name should come from the entity's family
descriptor so the generic model can read it without being subclassed.

---

### SMELL 3 — `LovelyLib` re-implements `NativeAnimation` as `RobotAnimation` (DUPLICATION)

**Files**: `lovelylib/{Forge,Fabric,NeoForge}/.../RobotAnimation.java`

`RobotAnimation` duplicates the entire `NativeAnimation` controller factory
(`locomotionAnimation`, `attackAnimation`, `basePoseAnimation`), adds `headAnimation`
and `tailConfigAnimation`, and introduces a separate `resolveLocomotionAnimation()`
that shadows `AnimationStateManager.getLocomotionAnimation()`.

The only things `RobotAnimation` adds that `NativeAnimation` does not have:
- `headAnimation()` — reads `"head"` hardcoded (same as NativeModel)
- `tailConfigAnimation()` — calls `TailAnimationUtils.configureTailVisibility()` which uses reflection

Everything else — `locomotionAnimation()`, `attackAnimation()`, `basePoseAnimation()`,
`buildRawAnimation()`, `resolveLoopBehavior()`, `getProfile()`, `selectFromPool()` — is
copy-pasted from `NativeAnimation` with minor naming differences.

**The correct pattern** is in Monsters & Girls: `WildTamableModel` is a one-liner that
extends `NativeModel`. It calls `NativeAnimation` through `NativeModel.setCustomAnimations`.
No duplication. `RobotAnimation` should not exist in its current form.

---

### SMELL 4 — `KitsuneModel` is the only model subclass, and it only exists to add tail

**File**: `lovelylib/Forge/.../KitsuneModel.java`

```java
public class KitsuneModel extends NativeModel<NativeRobotEntity> {
    @Override
    public void setCustomAnimations(...) {
        super.setCustomAnimations(animatable, instanceId, event);
        RobotAnimation.tailConfigAnimation(animatable, this, event);
    }
}
```

This model subclass exists solely because tail bone visibility has no place to live.
Once tail config is handled declaratively through the family descriptor and applied
inside `NativeModel.setCustomAnimations()` (or a new `NativeModel` hook), `KitsuneModel`
becomes a one-liner identical to `NativeRobotModel` and can be deleted.

---

### SMELL 5 — `EntityAnimation`, `EntityModel`, `EntityVariantModel` enums are dead code

**Files**: `lovelylib/Common/.../enums/EntityAnimation.java`,
`EntityModel.java`, `EntityVariantModel.java`

These three enums enumerate the animation states and model variants that the old
string-based system has completely replaced. None of them are referenced anywhere in
the active source tree. They are leftover from the pre-`AnimationProfile` era and
should be deleted.

---

### SMELL 6 — `RobotFamily.configureSchema()` still registers `STANDBY_TICKS` and `STANDBY_TARGET_TICKS`

**File**: `lovelylib/Common/.../RobotFamily.java`

```java
.register(RobotFields.STANDBY_TICKS)
.register(RobotFields.STANDBY_TARGET_TICKS)
```

These fields exist because `handleStandbyAnimation()` in `RobotEntity` uses them.
Once that method is deleted (replaced by HZLib's idle-slot system), these schema
fields have no writer. They should remain in the schema for load-compatibility
(existing saves may contain them) but be marked deprecated and not written on save.

---

### SMELL 7 — `AnimationProfile` is not attached to `RobotFamily` variants

**File**: `lovelylib/Common/.../RobotFamily.java` — `configureVariants()`

```java
VariantRegistries.ANIMATORS.register(
    new StandardAnimatorVariant(
        defaultAnimKey,
        defaultAnimKey,
        LovelyIdentifier.getId("animations/" + LovelyConstant.ANIM_DEFAULT + ".animation.json").toString(),
        0          // ← no AnimationProfile passed
    )
);
```

Compare to the Monsters & Girls pattern:

```java
AnimationProfile BEE_PROFILE = AnimationProfile.builder()
    .idle("idle").sit("ride").build();
VariantRegistries.ANIMATORS.register(
    new StandardAnimatorVariant("bee_default_default", "Bee Animations",
        getAnimatorResource("bee_girl"), BEE_PROFILE, 1));
```

`RobotFamily` registers `StandardAnimatorVariant` with **no profile**. This means
`AnimationStateManager.resolveProfile()` always falls through its three-step fallback
chain and returns `null` for every robot entity. The entire profile-aware path in
`AnimationStateManager.getLocomotionAnimation()` is dead for robots. Robots fall
back to the hardcoded `"idle"`, `"walk"`, `"rest"`, `"sit"` string constants
via the `isInSittingPose()` branch — the old path that drives the flicker.

This is the root architectural gap that the ADR must close.

---

### SMELL 8 — `handleStandbyAnimation()` drives animation through entity logic

**Confirmed** in `RobotEntity.tick()`:
```java
handleStandbyAnimation();
```

`handleStandbyAnimation()` checks velocity, increments `standbyTicks`, randomises
`standbyTargetTicks`, and calls `setInSittingPose(true/false)`. This drives the
`isInSittingPose()` branch in `AnimationStateManager.getLocomotionAnimation()` —
which is the path that still works for robots because no profile is attached
(smell 7). But this approach has two problems: the state is set server-side via
`SynchedEntityData` and arrives on the client with packet lag, and the animation
controller re-evaluates on every frame producing the flicker.

---

### SMELL 9 — `LovelyLib` has its own `BoneTransformations.java` (dead duplicate)

**File**: `lovelylib/Common/.../api/animation/BoneTransformations.java`

HZLib already has `BoneTransformations.java` with `calculateHeadRotation()`. LovelyLib
has an identical file in its own package. The LovelyLib copy is not referenced by
anything in the current active source (confirmed by grep). It is a dead duplicate that
should be deleted.

---

## What Monsters & Girls Gets Right (Reference Pattern)

The Monsters & Girls codebase is the correct reference because it was built after the
profile system was introduced. Key patterns to replicate in LovelyLib:

| Concern | Monsters & Girls pattern | LovelyLib current state |
|---------|--------------------------|------------------------|
| Model class | `WildTamableModel extends NativeModel` (one-liner) | `NativeRobotModel extends NativeModel` (one-liner, correct) + `KitsuneModel` subclass |
| Renderer | `WildTamableRenderer` + `buildOverlayLayers(feature)` | `NativeRobotRenderer` with hardcoded layers |
| AnimationProfile | Built in `registerVariants()`, passed to `StandardAnimatorVariant` | Not built — `StandardAnimatorVariant` called without profile |
| Bone manipulation | Not needed — no bone-level specials | Reflection in `TailAnimationUtils` |
| Controller factory | Uses `NativeAnimation` from HZLib directly | Duplicates it as `RobotAnimation` |

---

## Sentry Robot: Wing Bone Visibility Requirement

The Sentry robot can mimic other robot appearances. When it takes a "dragon" form, the
`UWings` bone and its sub-bones must be hidden because their texture conflicts with the
honey-form wing texture on `UWingsno`. When it takes a "honey" form, `UWings` must be
hidden and `UWingsno` must be visible.

This is **not an animation concern** — the bones exist and animate in both forms, the
issue is a texture conflict that produces a visual glitch when both bone groups are
visible simultaneously. The fix is a per-appearance-variant bone visibility override
that runs in `setCustomAnimations()` before GeckoLib processes the frame.

### Why existing systems don't cover it

- `AnimationProfile` manages animation slot names — it has no concept of bone visibility.
- `OverlayFeature` manages render-pass layers (textures) — it has no concept of GeoBone
  visibility within the model.
- Neither the tail system nor the head system provides conditional bone hiding by
  appearance variant.

### The correct abstraction: `BoneVisibilityFeature` in HZLib

A new feature on `NativeEntityFamily` that declares which bones should be hidden under
which conditions — evaluated once per render frame in `NativeModel.setCustomAnimations()`.

This is structurally identical to `OverlayFeature` (slot declarations on the family,
evaluated at render time) but operates on GeckoLib bone visibility instead of texture
layers.

It covers three distinct use cases cleanly:
1. **Level-based progressive unlock** — Kitsune tails hidden until level threshold met
2. **Appearance-variant conflict** — Sentry wings hidden based on current texture variant
3. **Any future conditional** — a bone that should be hidden during a specific state, etc.

---

## Summary of Changes Needed

### In HZLib

| Change | Reason |
|--------|--------|
| Add `BoneVisibilityFeature` to entity features | Declarative bone hiding — replaces reflection in TailAnimationUtils AND covers Sentry wings |
| Add `idleStationaryTicks` counter to `NativeEntity` | Feeds idle slot conditions without entity-logic timer code |
| Add `IdleSlot` + `IdleCondition` to animation package | Enables family-declared timed idle transitions |
| Add `idleSlots` to `AnimationProfile` + `idleSlot()` builder | Profile-driven idle variation |
| Update `AnimationStateManager.getLocomotionAnimation()` | Evaluate idle slots; fix vehicle fallback to IDLE when no ride/sit declared |
| Update `NativeModel.setCustomAnimations()` | Read head bone name from family; evaluate BoneVisibilityFeature |

### In LovelyLib Common

| Change | Reason |
|--------|--------|
| Build and attach `AnimationProfile` for each `RobotFamily` in `configureVariants()` | Root fix — robots currently have no profile attached |
| Add `idleSlot` declarations to Legacy/Reboot profiles | Replaces `handleStandbyAnimation()` |
| Declare `BoneVisibilityFeature` on Kitsune family | Replaces reflection-based `TailAnimationUtils.configureTailVisibility()` |
| Delete `handleStandbyAnimation()` + timer fields from `RobotEntity` | Replaced by HZLib idle slot system |
| Mark `STANDBY_TICKS` / `STANDBY_TARGET_TICKS` deprecated in schema | No writer after deletion; keep for load compat |

### In LovelyLib Loader Modules (Forge / Fabric / NeoForge)

| Change | Reason |
|--------|--------|
| Delete `RobotAnimation.java` from all three loaders | Duplicate of `NativeAnimation` — use HZLib directly |
| Delete `KitsuneModel.java` from all three loaders | Only existed to call `TailAnimationUtils`; replaced by `BoneVisibilityFeature` |
| Update `TributeRobotEntity.registerControllers()` to use `NativeAnimation` | Stops using deleted `RobotAnimation` |
| Update `NativeRobotEntity.registerControllers()` similarly | Same |
| Delete `lovelylib/Common/.../api/animation/BoneTransformations.java` | Dead duplicate of HZLib version |
| Delete `EntityAnimation.java`, `EntityModel.java`, `EntityVariantModel.java` | Dead enums |

