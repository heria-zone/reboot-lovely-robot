# Monsters & Girls — Feature Implementation Notes

**Status**: Active — Sprint 10  
**Last Updated**: 2026-06-20  
**Scope**: Belly system, emissive layers, entity family fixes, new variants, special mechanics  
**Sprint**: `docs/development/sprints/active/SPRINT_10_TASK.md`

---

## Architecture Context

The full stack this sprint touches:

```
Monsters & Girls mod
  ↓ depends on
LovelyLib (RobotFamily, RobotFamilyRegistry, shared configs)
  ↓ depends on
HZLib (NativeEntity, NativeEntityFamily, OverlayFeature, BellyFeature [new],
        TextureVariantFeature, SizeVariantFeature [extended],
        PlantingFeature [extended], RenderConditions [extended],
        ExchangeFeature, EmanationFeature, SoundFeature)
```

**Core principle**: HZLib is the single source of truth for all entity variant and rendering infrastructure. Features are declared on the family descriptor (`NativeEntityFamily` subclass) — the renderer reads them, the entity executes them. Nothing is hardcoded in renderers or entity classes that belongs on the family.

---

## Status Overview

| Feature | Status | Sprint |
|---|---|---|
| Belly system (2-state, boolean) | ✅ Superseded by redesign | — |
| Seasonal costumes (OverlayFeature RANDOM slot) | ✅ Done — Mushroom families | Sprint 08 |
| Gourdragora carving (OverlayFeature INTERACTIVE) | ✅ Done | Sprint 08 |
| Mandrake Flower hair (OverlayFeature RANDOM) | ✅ Done | Sprint 08 |
| Gourdragora Golden/Lumina carving tint colours | ✅ Done — GOLDEN `0xFF6D320A`, LUMINA `0xFF675032` | Sprint 08 |
| Belly system redesign (5-level, BellyFeature) | 🔄 Sprint 10 | Sprint 10 |
| Emissive layer standardisation | 🔄 Sprint 10 | Sprint 10 |
| Wisp family fixes + belly + emissive | 🔄 Sprint 10 | Sprint 10 |
| Spook family fixes + belly + emissive | 🔄 Sprint 10 | Sprint 10 |
| Mushroom Warped registration + belly + emissive | 🔄 Sprint 10 | Sprint 10 |
| Puffball emissive + puff-jump | 🔄 Sprint 10 | Sprint 10 |
| Fluffball new variant | 🔄 Sprint 10 | Sprint 10 |
| Snowball Mushroom — throw snowballs | 🔄 Sprint 10 | Sprint 10 |
| Mushroom Crimson / Soul Wanderer / Molten emissive | 🔄 Sprint 10 | Sprint 10 |
| Molten Gal campfire cooking (4 slots) | 🔄 Sprint 10 | Sprint 10 |
| Gourdragora hunger watch (pumpkin pie drop) | 🔄 Sprint 10 | Sprint 10 |
| Gourdragora spawn weight (40/40/20) | 🔄 Sprint 10 | Sprint 10 |
| Mandrake Fructus ceiling planting (Glow Berry) | 🔄 Sprint 10 | Sprint 10 |
| Mandrake Chorus poison spit (ranged attack) | 🔄 Sprint 10 | Sprint 10 |
| `RangedAttackFeature` — HZLib consolidation | 🔄 Sprint 10 | Sprint 10 |
| Friendly fire prevention (same-owner peers) | 🔄 Sprint 10 | Sprint 10 |
| Spawn item variant support | ⏳ Backlog | — |

---

## ✅ Completed Features

### Seasonal Costumes — OverlayFeature RANDOM slot

All Mushroom families that use the `mushroom_brown_girl` or `mushroom_girl_default` model group share a pre-declared `OverlayFeature` instance (`BROWN_GIRL_COSTUME`, `DEFAULT_GIRL_COSTUME`). The costume slot is RANDOM — one of three Halloween hat textures is chosen once at spawn, stored in `SynchedEntityData`, and rendered only in October via `RenderConditions.inMonth(Month.OCTOBER)`.

**Design decision**: A single RANDOM slot that picks the hat colour and self-gates with `withRenderCondition()` is simpler than a two-slot (RANDOM pick + CONDITIONAL gate) approach. The chosen hat value is persisted all year; the condition just suppresses it off-season. No wasted slot for out-of-season months.

---

### Gourdragora Carving — OverlayFeature INTERACTIVE slot

Three Gourdragora families (GOLDEN, LUMINA, JACKO) each declare an `OverlayFeature` via `buildTintedOverlay(tintArgb)` or `buildJackoOverlay()`.

- **Golden/Lumina**: INTERACTIVE carving slot with 5 dynamic entries + an ALWAYS emissive. Each carving entry uses `OverlaySlot.entryDynamic()` to resolve a size-appropriate texture path at render time (strips `"gourdragora_girl_"` prefix from `MODEL_VARIANT` to get size segment). A colour provider `e -> tintArgb` tints the grayscale mask at render time — one texture set serves both colour families.
  - GOLDEN tint: `0xFF6D320A` (dark amber)
  - LUMINA tint: `0xFF675032` (pale ochre)

- **Jack'o**: Full-colour carving textures (no tint). Base texture has the face region permanently transparent. A CONDITIONAL `face_cover` slot fills it when uncarved; the INTERACTIVE carving slot fills it when carved. Both are dynamic path resolvers. An ALWAYS emissive slot renders the Jack'o glow.

**Design decision**: Grayscale mask + runtime ARGB tint (via `DynamicColorLayer`) is the correct approach for reusing one carving asset set across multiple colour families. Authoring separate full-colour textures per family would be N×M texture explosion. One mask × N tints = N files.

---

### Mandrake Flower hair — OverlayFeature RANDOM slot

FLOWER variant declares a RANDOM `SLOT_HAIR` slot with 4 hair overlay textures + an ALWAYS `SLOT_EMISSIVE`. The hair is chosen once at spawn and persisted. The base texture has the hair region transparent — the overlay fills it.

---

## 🔄 Sprint 10 — Feature Designs

---

### Feature 1: Belly System Redesign

#### Why the old system was replaced

The original `HAS_BELLY` boolean mutated `TEXTURE_VARIANT` directly via string surgery — looking for `_tummy`/`_default` substrings in the key and replacing them. This conflates two independent appearance dimensions: the base variant (which biome texture the entity has) and the belly state. A Wisp with a `wisp_girl_yellow` base texture that gains belly should keep `TEXTURE_VARIANT = "wisp_girl_yellow"` and render a belly **overlay** on top — not switch its texture key.

The old approach also only supports two states (with/without belly) and hardcodes the belly into the texture file rather than treating it as a composable layer.

#### New architecture

**`BellyLevel` enum** (HZLib): `SLIM(0)`, `CHUBBY(1)`, `TUMMY(2)`, `INFLATED(3)`, `CHUNKY(4)`.

**`BellyFeature`** (HZLib): Attached to a `NativeEntityFamily`. Declares which belly levels have authored textures and what the config-enforced maximum is. Absence of a `BellyFeature` on the family = entity does not respond to apple/feather at all. This is the gate — no flag, no magic boolean, just feature presence.

```java
withFeature(BellyFeature.class, new BellyFeature()
    .maxLevel(BellyLevel.TUMMY)                // config cap for this family
    .texture(BellyLevel.SLIM,  "...slim.png")  // optional — no entry = level renders nothing
    .texture(BellyLevel.TUMMY, "...tummy.png"))
```

**`BELLY_LEVEL` int** (MonsterEntity): Replaces `HAS_BELLY` boolean. Synced int 0–4. `handleBellyProgression()` gates on `BellyFeature` presence, increments/decrements clamped to `[0, maxLevel]`.

**Item rules**:
- Apple → increment, consume item, play sound
- Feather → decrement, **do not consume**, play sound

The feather is not consumed because decreasing belly is a deliberate owner choice, not a feeding interaction. Consuming the feather would punish the player for accidentally over-feeding.

**Gap rule**: Levels without a declared texture simply render nothing (the CONDITIONAL slot fires no texture). Progression still steps through all levels — only the visual is absent for undeclared levels. So `SLIM → CHUBBY → TUMMY` works even if CHUBBY has no texture on a given entity: at CHUBBY the belly layer is invisible, but apple/feather still track the level correctly.

**Overlay wiring** (per entity family):

```java
.withFeature(OverlayFeature.class, new OverlayFeature()
    // CONDITIONAL belly — first entry whose predicate passes wins (highest level first)
    .addSlot(OverlaySlot.conditional("belly",
        OverlaySlot.entry("...tummy.png", RenderConditions.bellyAtLeast(BellyLevel.TUMMY)),
        OverlaySlot.entry("...slim.png",  RenderConditions.bellyAtLeast(BellyLevel.SLIM))))
    // ALWAYS emissive — unconditional glow layer
    .addSlot(OverlaySlot.always("emissive", "...emissive.png")))
```

Highest declared level is listed first — CONDITIONAL slots return the first match, so TUMMY wins over SLIM when both predicates pass.

**NBT migration**: Old saves with `HasBelly=true` → `BELLY_LEVEL = 2` (TUMMY). `HasBelly=false` → `BELLY_LEVEL = 0` (SLIM).

#### Belly level per entity

| Entity | Belly levels with textures | Max config cap |
|---|---|---|
| Wisp (all colours) | TUMMY | TUMMY |
| Spook (all colours) | SLIM, TUMMY | TUMMY |
| Mushroom Warped (all colours) | CHUBBY, TUMMY | TUMMY |

SLIM for Spook renders a subtle belly outline. SLIM for other entities has no texture authored → renders nothing (level 0 = no overlay = natural default appearance).

---

### Feature 2: Emissive Layer Standardisation

#### The problem

Emissive textures were scattered across two locations with two naming conventions:
- Some lived in `entity/{entity}/` folders (wrong — emissives are overlay layers, not base textures)
- Some used `_emission.png` suffix, others used `_emissive.png` suffix (inconsistent)

This was discovered during the Sprint 10 design session audit.

#### The canonical standard

**Location**: All emissive textures live in `textures/layer/{entity}/`  
**Suffix**: Always `_emissive.png`

This mirrors how Gourdragora, Wisp, and Spook already handle their emissive layers — those were authored correctly and form the standard others must follow.

#### Files requiring action

**In `layer/` but wrong suffix** (`_emission` → `_emissive`):
- `layer/mandrake_flower/mandrake_flower_emission.png`
- `layer/mushroom_warped/mushroom_warped_blue_emission.png`
- `layer/mushroom_warped/mushroom_warped_green_emission.png`

**In `entity/` and wrong suffix** (move to `layer/` + rename):
- `entity/mushroom_crimson/mushroom_crimson_red_emission.png`
- `entity/mushroom_crimson/mushroom_crimson_pink_emission.png`
- `entity/mushroom_molten/mushroom_molten_emission.png`
- `entity/mushroom_puffball/mushroom_puffball_default_emission.png`
- `entity/mushroom_puffball/mushroom_puffball_pale_emission.png`
- `entity/mushroom_puffball/_/enderpuffball_emission.png`
- `entity/mushroom_soul_wanderer/mushroom_soul_wanderer_emission.png`

**In `entity/` correct suffix** (move to `layer/` only):
- `entity/mushroom_snowball/mushroom_snowball_emissive.png`
- `entity/globberie/globberie_default_emissive.png`

#### How emissive textures work in GeckoLib

An emissive texture is a separate PNG with identical UV layout to the base texture. Only the glowing regions are painted — everything else is fully transparent (`alpha = 0`). The renderer draws this layer using `RenderType.eyes()` (or equivalent full-brightness render type), which bypasses the world lighting calculation and renders the region at full brightness regardless of ambient light level. This gives the appearance of self-illumination.

In this project, emissives are wired as `OverlayFeature` `ALWAYS` slots. The `ALWAYS` mode is rendered unconditionally on top of all other layers using the emissive render type. The layer system in `NativeRenderer` (formerly `InternalLayerRenderer`) handles the render type selection when materialising an ALWAYS slot that carries no predicate.

---

### Feature 3: Wisp Family Registration Fix

**Problem**: `WispFamily.configureVariants()` registered texture keys like `wisp_girl_green_default` but the actual file on disk is `wisp_girl_green.png`. Every texture lookup was pointing at a non-existent file. The belly system was also incorrectly wired via `TextureVariantFeature` entries rather than overlay layers.

**Fix**:
- Base texture key: `wisp_girl_{color}` (matches filename exactly)
- Belly overlay: CONDITIONAL slot via `BellyFeature` + `OverlayFeature`
- Emissive: ALWAYS slot → `layer/wisp/wisp_{color}_emissive.png`
- Remove helper methods `getTextureVariantForBellyState()` and `supportsTummyTexture()` — these are obsolete once `BellyFeature` owns the belly concern

---

### Feature 4: Spook Family Registration Fix

**Problem**: Same pattern as Wisp — keys referenced `spook_girl_cream_default` but file is `spook_girl_cream.png`.

**Fix**:
- Base texture key: `spook_girl_{color}`
- Belly overlay: SLIM + TUMMY levels per color (Spook has two belly assets)
- Emissive: cream/teal share `spook_girl_emissive.png`, peach has `spook_girl_peach_emissive.png`
- Remove obsolete helper methods

**Design note on SLIM**: Spook's SLIM level has an authored texture — a subtle, lightly-defined belly outline. This makes SLIM meaningful for Spook rather than invisible. When belly level is 0 (SLIM) and a SLIM texture exists, the overlay renders. This is correct — the predicate `bellyAtLeast(BellyLevel.SLIM)` passes at any level ≥ 0, which is always. So listing SLIM as the lowest-priority CONDITIONAL entry means it renders whenever belly is active at all, with TUMMY overriding it when level ≥ 2.

---

### Feature 5: Mushroom Warped Registration

**Problem**: `MushroomFamily.WARPED` had no variant registrations at all — no `registerWarpedVariants()` static block, no texture/model/animator entries. The entity existed in registration but would crash or render with a missing texture.

**Fix**: Add full registration following the pattern of other mushroom families. Warped uses the `mushroom_girl_default` model group. Two colours: blue, green. Belly: CHUBBY + TUMMY per colour. Emissive: per colour.

---

### Feature 6: Puffball Emissive + Puff-Jump + Fluffball

#### Puffball emissive

Puffball already has `mushroom_puffball_default_emissive.png` and `mushroom_puffball_pale_emissive.png` (after standardisation). Wire as ALWAYS overlay slots, one per texture variant.

#### Puffball puff-jump (autonomous)

The `mushroom_puffball_girl.animation.json` has a `wave` animation that scales the `belly3` bone up, giving a visual puff effect. The `belly` animation (2.08s) is a longer idle special that shows belly movement.

**Mechanic**: Autonomous puff activated in `aiStep()` on the server side. Trigger condition: entity's owner is nearby and has positive upward velocity (jumping or climbing). Effect: `JUMP_BOOST` level 1 for 3 seconds + `SLOW_FALLING` for 4 seconds applied to self (which benefits the owner if they're riding or nearby). The `wave` animation plays via the `AnimationProfile` special pool.

**Cooldown**: 200 ticks (10 seconds) prevents spam. The puff is not player-triggered — it reads environmental signals and acts when appropriate, fitting the Puffball's character as an airborne support mushroom.

#### Fluffball — new variant

Fluffball is the flying subspecies of the End Mushroom family, analogous to how Mushroom Brown has multiple biome-based colour variants. Both Puffball and Fluffball are `MushroomFamily` instances sharing the same entity class (`MushroomEntity`).

**Fluffball assets** (from `entity/mushroom_puffball/_/`):
- Base texture: `ender_puffball_girl.png`
- Emissive: `enderpuffball_emissive.png`
- Inflated textures: `ender_puffball_girl_inflated_a.png`, `ender_puffball_girl_inflated_4_a.png` — these suggest a texture-variant swap for the inflated state

**Fluffball fly**: Uses `FlyingPathNavigation` and flying AI goals. Spawns in End biomes. Registered with `createFlyingAttributes()`.

---

### Feature 7: Snowball Mushroom — Throw Snowballs

**Problem**: `MushroomFamily.SNOWBALL` has `EmanationFeature` for freeze/slow on attack (melee), but the description says she should throw snowballs like a Snow Golem. Snowball throwing has never been implemented.

**Architecture**: Implement `RangedAttackMob` interface on `MushroomEntity` (or a `MushroomSnowballEntity` subclass). Wire `RangedAttackGoal` targeting hostile mobs. `performRangedAttack()` fires a vanilla `Snowball` projectile.

**Design decision on melee vs. ranged**: Keep the existing `EmanationFeature` ON_ATTACK (melee freeze/slow) as a fallback when targets are at close range. The `RangedAttackGoal` keeps Snowball at distance — she prefers throwing. If a target closes in before she can escape, melee activates. This dual behaviour fits the character without requiring a new feature — it emerges from the goal priority ordering.

---

### Feature 8: Molten Gal Campfire Cooking

#### Design

The vanilla `CampfireBlockEntity` pattern maps directly onto Molten Gal:
- 4 cook slots (same as campfire) — items placed on her head cook over time
- Cook timers tick in `aiStep()`, server side
- `CampfireCookingRecipe` resolution via `level().getRecipeManager().getAllRecipesFor(RecipeType.CAMPFIRE_COOKING)` — no custom recipe type needed
- When a slot's timer reaches the recipe's `cookingTime`, the cooked result is dropped and the slot cleared
- Smoke particles emit above her head while cooking

#### Interaction
- Right-click with raw food → place in first empty slot, consume item
- Right-click empty hand → take first completed cooked item from slot (if done); otherwise no-op

#### Renderer
`CookingItemLayer` (loader-specific) renders up to 4 `ItemStack` instances positioned above the entity's head using `ItemRenderer.renderStatic()`. Position offset is relative to the head bone.

#### Design decision on scope
This is a per-entity special mechanic, not a reusable HZLib feature. The campfire cook state is specific to Molten Gal and nothing else in the ecosystem uses it. Therefore it lives in `MushroomEntity` (or a `MushroomMoltenEntity` subclass) and `CookingItemLayer` — not in a new HZLib feature class. The rule: only extract to HZLib when ≥2 consumers exist.

---

### Feature 9: Gourdragora Spawn Weight (40/40/20)

**Problem**: `SizeVariantFeature.SizeConfig` has no spawn weight field. `GourdragoraEntity.initializeSpawnVariants()` selects a size randomly with equal probability — Big has the same spawn chance as Mini and Default, which is not the intended design.

**Architecture**: Add `spawnWeight(int)` to `SizeConfig.Builder`. Add `pickWeightedRandom()` to `SizeVariantFeature`. `GourdragoraEntity` overrides `initializeSpawnVariants()` to call `feature.pickWeightedRandom()` instead of `feature.getConfig(randomKey)`.

**Weights**: Mini=40, Default=40, Big=20 → Big spawns half as often as Mini or Default.

**Design decision**: The weight lives on `SizeConfig` (on the family descriptor) rather than in the entity's `initializeSpawnVariants()` override. This keeps the data close to the other size configuration and makes the weighting self-documenting. Any future entity with size variants can declare weights the same way.

---

### Feature 10: Gourdragora Hunger Watch

When a tamed Gourdragora's owner has hunger below 11 food points, she occasionally drops a Pumpkin Pie. This is an ambient caretaking behaviour — she notices her owner is hungry and provides food.

**Implementation**: Server-side tick in `GourdragoraEntity.aiStep()`. Checks `owner.getFoodData().getFoodLevel() < 11`. On match: 2% chance per tick, 60-second cooldown between drops. Drops at the entity's feet with standard pickup delay.

**Why 2% / 60s**: Low probability prevents pie spam. A player with low hunger for 50 ticks (~2.5 seconds of checking) has ~64% chance of getting a pie. The 60s cooldown prevents multiple drops in quick succession when the player stays hungry.

---

### Feature 11: Mandrake Fructus — Ceiling Glow Berry Planting

**Problem**: `PlantingFeature` only supports ground planting (scan same-Y block, check surface below, place on top). Glow Berry Bushes (`CAVE_VINES`) are ceiling vines — they attach to the underside of a solid block and grow downward. The current planting logic cannot handle this.

**Architecture**: Add `PlantDirection` enum (`DOWN` = ground default, `UP` = ceiling) to `PlantingFeature.PlantItem`. Update `tryPlantAt()` in `MonsterEntity` to branch on direction:

```
UP direction:
  - Check: block at pos.above() is solid and in allowedBlocks list
  - Check: block at pos is air
  - Place: CAVE_VINES at pos (hangs from above naturally)
```

The `allowedBlocks` for ceiling planting represent valid ceiling surfaces (stone, deepslate, dripstone).

**Design decision on search radius**: Fructus uses a larger search radius than other Mandrakes to account for the fact that she needs to find ceiling blocks above her, which are typically higher up in cave environments. The `PlantingFeature.getSearchRadius()` value can be set independently per `PlantItem` entry.

---

## Cross-Cutting Architectural Notes

### Single source of truth for texture paths

Every texture path declared in a family class must exactly match a file on disk. The emissive standardisation task is a prerequisite for all family fixes — family class path strings cannot be updated until the files exist at their canonical locations.

The audit pattern to enforce this: after every texture path change in a family class, verify the file exists at that path in the resources folder before committing.

### Overlay slot ordering

All entity types with both belly and emissive overlays follow this layer order:

```
1. Base texture     (TextureVariantFeature — no layer, this is the entity's main texture)
2. Belly overlay    (OverlayFeature CONDITIONAL — renders 0 or 1 belly texture)
3. Seasonal costume (OverlayFeature RANDOM + RenderCondition — renders in-season only)
4. Emissive         (OverlayFeature ALWAYS — renders at full brightness, unconditionally)
```

Emissive is always last (topmost layer) so it renders over everything. Costumes render over belly so seasonal outfits cover the belly area cleanly.

### BellyFeature as the interaction gate

No entity responds to apple/feather unless it has a `BellyFeature`. This means:
- Gourdragora, Mandrake, Bee, Globberie, etc. — no `BellyFeature` → apple/feather do nothing special
- Wisp, Spook, Mushroom Warped → have `BellyFeature` → belly interaction active

This is the correct pattern from the architectural-consolidation principle: the capability is declared on the family, not hardcoded as an entity class conditional. Adding belly to a new entity type is one line: `withFeature(BellyFeature.class, new BellyFeature()...)`.

### Persistent overlay slot pool size

`MonsterEntity` declares a pool of 2 `SynchedEntityData<String>` accessors (`OVERLAY_SLOT_0`, `OVERLAY_SLOT_1`) for persistent overlay slots (RANDOM + INTERACTIVE). CONDITIONAL and ALWAYS slots are stateless and don't consume pool slots.

Current usage audit:
- Mandrake Flower: RANDOM hair (1 slot) + ALWAYS emissive (0 slots) → pool needed: 1
- Gourdragora: INTERACTIVE carving (1 slot) + CONDITIONAL face_cover (0) + ALWAYS emissive (0) → pool needed: 1
- Mushroom (costume): RANDOM hat (1 slot) → pool needed: 1
- Wisp/Spook/Warped (belly + emissive): CONDITIONAL belly (0) + ALWAYS emissive (0) → pool needed: **0**

Belly is CONDITIONAL (no persistent state — predicate is evaluated live from `BELLY_LEVEL` int). Emissive is ALWAYS. Neither consumes a pool slot. Pool size 2 remains sufficient for Sprint 10.

### Fluffball vs. Puffball — same entity class

Both are `MushroomFamily` instances using `MushroomEntity`. The distinction is in the family feature configuration: Fluffball has `createFlyingAttributes()` and flying AI goals; Puffball has ground attributes and the puff-jump mechanic. This mirrors how Mushroom Brown variants share a class but differ in biome appearance — same class, different family configuration.

### Molten campfire cooking — not a HZLib feature

The campfire cook mechanic is specific to Molten Gal. Extracting it to HZLib would create premature abstraction (only one consumer currently). It lives in the entity class. If a second entity needs similar cooking behaviour in the future, the refactoring cost is low.

### Snowball throwing — RangedAttackMob + existing EmanationFeature

The `EmanationFeature` ON_ATTACK (freeze/slow) already fires on melee attack. Adding `RangedAttackMob` gives Snowball a ranged option. These two systems are independent — the emanation fires when `doHurtTarget()` is called (which snowball impact triggers), and the ranged goal keeps her at distance. The combination produces the intended behaviour without modifying the emanation system.

---

## Open Questions (Carried Forward)

1. **Spawn item variant support** — how does the player capture a monster girl? The capture mechanic determines where to write `TextureVariant` into the spawn item. Deferred to a later sprint; the system is not started.

2. **Fluffball inflated textures** — `ender_puffball_girl_inflated_a.png` and `ender_puffball_girl_inflated_4_a.png` exist in the `_` subfolder. These appear to be intended for a visual texture swap when Fluffball uses her fly/puff ability. Should this be a `TextureVariantFeature` swap triggered by the ability, or an `OverlayFeature` INTERACTIVE slot? Decision deferred to implementation.

3. **Belly config scope** — should `BellyFeature.maxLevel` be a per-entity family constant (declared in code) or a runtime config value? Current design treats it as a code constant on the family. If players or server admins need to adjust belly caps, a config layer over `BellyFeature.maxLevel` would be needed. Deferred.

4. **Spook teal emissive** — `spook_girl_emissive.png` is shared by cream and teal variants. Does teal warrant its own emissive asset, or is the shared one intentional? Check with asset author before wiring.

---

### Feature 12: `RangedAttackFeature` — HZLib Consolidation

#### Why it exists

After implementing Snowball Mushroom's ranged attack (Phase 7), the pattern was:
- `MushroomEntity` implements `RangedAttackMob`
- `registerGoals()` has a `"mushroom_snowball".equals(nativeEntity.getKey())` key-check
- `RangedAttackGoal` wired only inside that branch

When Mandrake Chorus also needed ranged attack, this pattern would have been duplicated verbatim in `MandrakeEntity`. Two entity classes doing the same boilerplate is the consolidation signal.

#### Architecture

`RangedAttackFeature` (HZLib Common) is a capability declared on the family descriptor:

```java
withFeature(RangedAttackFeature.class, RangedAttackFeature
        .of(MonstersEntities.MUSHROOM_SNOWBALL_PROJECTILE,
            (type, level) -> new MushroomSnowball(type, level))
        .interval(20, 40)   // min/max ticks between throws
        .range(15.0f))      // blocks
```

`WildTamableEntity.registerGoals()` reads this feature and, when present:
1. Adds `RangedAttackGoal` at priority 2 (same as melee — ranged prefers distance, melee activates on close)
2. Adds `NearestAttackableTargetGoal<Monster>` at priority 4

No entity-class override is needed. Adding ranged attack to any future entity is one `withFeature` call.

#### Migration: Snowball Mushroom

`MushroomEntity` is simplified — the key-check and explicit goal registration are removed. `MushroomFamily.SNOWBALL` gets `withFeature(RangedAttackFeature.class, ...)` instead. `MushroomEntity` no longer implements `RangedAttackMob` directly — `WildTamableEntity` does, delegating `performRangedAttack()` to the feature's factory.

**Design decision on `performRangedAttack` placement**: The actual throw logic (arc correction, sound) lives in `WildTamableEntity.performRangedAttack()`, not in the projectile class. The projectile class handles only what happens *on impact*. This keeps the throw feel consistent — the arc correction formula is defined once.

---

### Feature 13: Mandrake Chorus — Poison Spit

#### Character design

Mandrake Chorus is an End-biome variant. She is volatile and unsettling — her EmanationFeature already emits Wither + Levitation + Nausea in an AoE when hurt. Her ranged attack extends this character: she spits at hostile mobs, poisoning and nauseating them from a distance before they reach her.

#### Projectile: `MandrakeSpitProjectile`

Extends `ThrowableItemProjectile`. Registered with `LlamaSpitRenderer` for the llama-spit visual — a white orb in flight, which fits the "wet glob" feel of a spit attack.

**Effect on hit:**
- `POISON` level 1 / 140 ticks (7 seconds) — meaningful sustained damage
- `CONFUSION` (Nausea) level 0 / 100 ticks (5 seconds) — screen wobble, consistent with Chorus's End-biome disorienting character

**Why these values:** Poison level 1 deals 1.5 HP/s, for ~10 HP total over 7s. That's meaningful against mid-tier mobs without being one-shot. Nausea is purely cosmetic damage — a disorienting debuff that fits Chorus's character without being overpowered. Both values are distinct from the Snowball's freeze/slow (MOVEMENT_SLOWDOWN + freeze) — the two ranged attackers feel different in combat.

#### Render

`LlamaSpitRenderer` is the correct choice:
- It renders a spinning textured quad (the llama spit visual)
- It requires no custom texture — it uses the same white orb that vanilla llamas use
- It matches the "spit" animation feel better than a thrown-item sprite

If `LlamaSpitRenderer` constructor signature differs on any future Minecraft version, fall back to `ThrownItemRenderer` with `Items.SLIME_BALL`.

#### `RangedAttackFeature` values for Chorus

| Parameter | Value | Rationale |
|---|---|---|
| Min interval | 30 ticks (1.5 s) | Faster than Snowball — Chorus is more aggressive |
| Max interval | 60 ticks (3 s) | Avoids spam while feeling active |
| Attack range | 12 blocks | Slightly shorter than Snowball (15) — her End biome is smaller, denser |

---

### Feature 14: Friendly Fire Prevention

#### Problem

`NearestAttackableTargetGoal<Monster>` targets any entity that is an instance of `Monster`. If two tamed entities share an owner, one can target the other when the target happens to be a Monster subclass or extends it. `OwnerHurtTargetGoal` also causes retaliation when the owner accidentally hits a peer.

Additionally, once a projectile is in flight, it can hit a same-owner peer even if the targeting system never acquired them — the projectile has no knowledge of the thrower's owner at impact time.

#### Two-layer fix

**Layer 1 — Targeting (NativeEntity):** Override `wantsToAttack(LivingEntity target, LivingEntity owner)` in `NativeEntity`. This is the single hook that `NearestAttackableTargetGoal`, `OwnerHurtTargetGoal`, and `HurtByTargetGoal` all call internally. Returning `false` prevents any goal from ever acquiring the peer as a target — including retaliatory goals.

The check is:
1. `this.isTame()` — only applies to tamed entities
2. `this.getOwnerUUID() != null` — must have a known owner
3. `target instanceof TamableAnimal peer` — target must be a tamable entity
4. `this.getOwnerUUID().equals(peer.getOwnerUUID())` — same owner UUID

Untamed entities are entirely unaffected. The check is O(1) and runs in the AI goal tick — negligible cost.

**Layer 2 — Projectile impact:** `MushroomSnowball.onHitEntity()` and `MandrakeSpitProjectile.onHitEntity()` each perform the same same-owner check before applying effects. If the check fails, `return` is called before `super.onHitEntity()` (which deals vanilla knockback). This suppresses both the effects *and* the vanilla knockback on same-owner peers.

#### Why both layers

The targeting block prevents *intentional* attacks from being acquired. The projectile block prevents *accidental* hits from a projectile already in flight when a target became a peer (e.g. player tames an entity mid-combat). Belt-and-suspenders — both are cheap and together give a complete guarantee.

#### Where this lives

`wantsToAttack()` override is in `NativeEntity` (HZLib Common) — it covers every entity that extends it, current and future, without any per-entity boilerplate. The projectile checks are in the two projectile classes in the monsters mod Common package — colocated with the effect application code they guard.

---

## Cross-Cutting Architectural Notes (updated)

### Ranged attack — from key-check to feature

The initial Snowball implementation used a `"mushroom_snowball".equals(nativeEntity.getKey())` guard in `MushroomEntity.registerGoals()`. This worked for one entity. The moment a second entity (Chorus) needed the same pattern, the duplication signal fired.

The consolidated pattern: `RangedAttackFeature` on the family descriptor → `WildTamableEntity.registerGoals()` reads it → `RangedAttackGoal` wired automatically. Adding ranged attack to any entity is one line on the family. This is the correct architectural progression: start simple (key-check), recognize the pattern when it repeats, extract at that point — not earlier.

### Friendly fire — capability declared once, everywhere

`wantsToAttack()` in `NativeEntity` is the single source of truth for the same-owner rule. It applies to all HZLib-managed entities without any per-entity or per-goal registration. The rule is: if both entities are tamed and share an owner UUID, they are allies — no targeting, no retaliatory goals, no projectile effects. This is the correct place for a universal rule: the base class, not per-entity or per-goal.
