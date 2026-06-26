# Sprint Task: Monsters & Girls Feature Implementation

**Status**: 🔄 ACTIVE  
**Started**: 2026-06-20  
**Target Completion**: 2026-07-18  
**Priority**: High  
**Complexity**: High  

## Sprint Goal

Implement the full set of Monsters & Girls features planned in the design session: belly system redesign, emissive layer standardisation, entity family config fixes, new Fluffball variant, Puffball puff-jump, Snowball snowball throwing, Molten Gal campfire cooking, Gourdragora hunger watch + spawn weight, Mandrake Fructus ceiling planting, Mandrake Chorus poison spit, friendly-fire prevention, and all required HZLib framework additions that support them.

## Strategic Context

**Feature Notes**: `docs/development/notes/feature-implementations/MonsterGirls_Feature_Implementation_Notes.md`  
**Prerequisite**: Sprint 09 Phase 1 (HZLib rename) ✅

---

## Objectives

### Phase 1 — Emissive standardisation (prerequisite for all rendering work)
- [x] Rename all `_emission.png` files in `layer/` to `_emissive.png`
- [x] Move all emissive textures from `entity/` folders to their `layer/` counterparts
- [x] Update all family class texture path strings to match new locations and names
- [x] Wire ALWAYS emissive `OverlaySlot` in every family class that has a `layer/` emissive texture (Wisp ×3, Spook ×3, Mushroom Crimson, Molten, Puffball, Snowball, Soul Wanderer, Warped, Globberie ×3)

### Phase 2 — Belly system (monsters mod only)

> **Architecture decision**: `BellyLevel` enum and `BellyFeature` class live entirely in the monsters mod (`net.heriazone.monsters_girls`). The only HZLib touch is a thin seam — `NativeEntity.getBellyLevel()` returning `0` by default, overridden by `MonsterEntity` — so `RenderConditions.bellyAtLeast()` can predicate on belly level without any mod dependency entering HZLib.

- [x] Add `getBellyLevel()` default hook to `NativeEntity` (HZLib — returns `0`)
- [x] Add `RenderConditions.bellyAtLeast(int)` predicate (HZLib — uses `entity.getBellyLevel()`)
- [x] Create `BellyLevel` enum in monsters mod (`SLIM`, `CHUBBY`, `TUMMY`, `INFLATED`, `CHUNKY`)
- [x] Implement `BellyFeature` in monsters mod — declares belly overlay textures per level, config cap
- [x] Replace `HAS_BELLY` boolean with `BELLY_LEVEL` (int) synced field on `MonsterEntity`; rewrite `handleBellyProgression()` gated on `BellyFeature`; NBT migration (`HasBelly` → `BellyLevel`)
- [x] Wire `BellyFeature` + CONDITIONAL belly `OverlaySlot` on `WispFamily` (TUMMY cap, one texture per color)
- [x] Wire `BellyFeature` + CONDITIONAL belly `OverlaySlot` on `SpookFamily` (SLIM + TUMMY cap, two textures per color)
- [x] Wire `BellyFeature` + CONDITIONAL belly `OverlaySlot` on `MushroomFamily.WARPED` (CHUBBY + TUMMY cap, two textures per color)
- [x] Add `spawnWeight` field to `SizeVariantFeature.SizeConfig` + `pickWeightedRandom()` method
- [x] Add `PlantDirection` enum + ceiling planting logic to `PlantingFeature`
- [x] Expand overlay slot pool on `MonsterEntity` from 2 to 3 (belly needs an additional persistent slot for some entities)
  > **Decision**: No expansion needed. Belly (CONDITIONAL) and emissive (ALWAYS) are stateless — 0 pool entries consumed. Max persistent slots per entity type remains 1. Pool size 2 is sufficient for Sprint 10.

### Phase 3 — Wisp family fixes + features
- [x] Fix texture variant keys to match actual filenames (`wisp_girl_blue`, not `wisp_girl_blue_default`)
- [x] Remove belly textures from `TextureVariantFeature` (belly is now an overlay layer)
- [x] Add `BellyFeature` — TUMMY level only (`wisp_{color}_belly_tummy.png`)
- [x] Add `ALWAYS` emissive overlay slot per color (`wisp_{color}_emissive.png`)
- [x] Wire `OverlayFeature` with belly CONDITIONAL + emissive ALWAYS slots
- [x] Register renderer with overlay layers in `MonstersEntities`

### Phase 4 — Spook family fixes + features
- [x] Fix texture variant keys (`spook_girl_cream`, not `spook_girl_cream_default`)
- [x] Remove belly textures from `TextureVariantFeature`
- [x] Add `BellyFeature` — SLIM + TUMMY levels per color
- [x] Add `ALWAYS` emissive slot — cream/teal share `spook_girl_emissive.png`, peach uses `spook_girl_peach_emissive.png`
- [x] Wire `OverlayFeature` with belly CONDITIONAL + emissive ALWAYS slots
- [x] Register renderer with overlay layers in `MonstersEntities`

### Phase 5 — Mushroom Warped family registration + features
- [x] Add full texture/model/animator variant registrations (currently missing entirely)
- [x] Add `BellyFeature` — CHUBBY + TUMMY levels per color (`mushroom_warped_{color}_belly_chubby.png`, `_tummy.png`)
- [x] Add `ALWAYS` emissive slot per color (`mushroom_warped_{color}_emissive.png`)
- [x] Wire `OverlayFeature` with belly CONDITIONAL + emissive ALWAYS slots
- [x] Register renderer with overlay layers in `MonstersEntities`

### Phase 6 — Puffball emissive + Fluffball new variant
- [x] Add `ALWAYS` emissive overlay slot to `PUFFBALL` per texture variant (`mushroom_puffball_{variant}_emissive.png`)
- [x] Register renderer with overlay layers in `MonstersEntities`
- [x] Add puff-jump autonomous mechanic — applies `JUMP_BOOST` II + `SLOW_FALLING`, triggers `wave` animation, 200-tick cooldown, fires when tamed owner is nearby with upward velocity
- [x] Create `MushroomFamily.FLUFFBALL` — new static instance, End biome flying variant, `ender_puffball_girl.png` base texture, `enderpuffball_emissive.png` ALWAYS slot
- [x] Register `MUSHROOM_FLUFFBALL` in `MonstersEntities` with `createFlyingAttributes` + `FlyTamableEntity`
- [x] Wire `FLUFFBALL` emissive (`enderpuffball_emissive.png`) — reuses `puffballEmissivePath()` via `"mushroom_puffball"` texture variant key
- [x] Add Fluffball fly capability (`FlyTamableEntity` — `FlyingPathNavigation` + `WaterAvoidingRandomFlyingGoal` inherited)

### Phase 7 — Snowball Mushroom: throw snowballs
- [x] Register renderer with overlay layers in `MonstersEntities`
- [x] Create `MushroomSnowball` projectile — applies freeze + slow on entity hit (mirrors `EmanationFeature` values)
- [x] Register `MUSHROOM_SNOWBALL_PROJECTILE` entity type in `MonstersEntities`
- [x] `MushroomEntity` implements `RangedAttackMob` — `performRangedAttack()` throws `MushroomSnowball` with vanilla Snow Golem arc trajectory
- [x] `RangedAttackGoal` (interval 20–40 t, range 15 blocks) + `NearestAttackableTargetGoal<Monster>` registered in `MushroomEntity.registerGoals()`, gated on `"mushroom_snowball"` family key — all other variants unaffected
- [x] Melee fallback: `MeleeAttackGoal` (inherited, priority 2) activates when target closes past ranged range

### Phase 8 — Mushroom Crimson + Soul Wanderer + Molten emissive
- [x] Move `mushroom_crimson_{variant}_emissive.png` to `layer/mushroom_crimson/`
- [x] Move `mushroom_soul_wanderer_emissive.png` to `layer/mushroom_soul_wanderer/`
- [x] Add `ALWAYS` emissive overlay slots to `MushroomFamily.CRIMSON` variants
- [x] Add `ALWAYS` emissive overlay slot to `MushroomFamily.SOUL_WANDERER`
- [x] Move `mushroom_molten_emissive.png` to `layer/mushroom_molten/`, wire `ALWAYS` slot on `MOLTEN`

### Phase 9 — Molten Gal campfire cooking (4 slots)
- [x] Add 4 synced `ItemStack` fields + 4 `int` cook timers to `MushroomMoltenEntity` subclass
- [x] `aiStep()` tick logic — increment timers, detect `CampfireCookingRecipe` completion, drop cooked result, emit `Smoke` particle
- [x] `handleSpecificInteractions()` — right-click with raw food places into first empty slot; right-click empty hand extracts cooked item
- [x] NBT serialisation for all 4 slots + timers
- [x] `CookingItemLayer` — implements `IInternalRenderLayer<WildTamableEntity>`, renders item stacks above head using `ItemRenderer`, narrowed to `MushroomMoltenEntity` via `instanceof`
- [x] Register `CookingItemLayer` for MOLTEN in `MonstersEntities`

### Phase 10 — Gourdragora: hunger watch + spawn weight
- [x] Add `spawnWeight` to `GourdragoraFamily.SIZE_FEATURE` — Mini 0.4, Default 0.4, Big 0.2
- [x] Override `initializeSpawnVariants()` in `GourdragoraEntity` to use `SizeVariantFeature.pickWeightedRandom()`
- [x] Add pumpkin pie hunger-watch tick logic in `GourdragoraEntity.aiStep()` — checks owner hunger < 11, 2% chance per tick, 60s cooldown, drops `PUMPKIN_PIE`
- [x] NBT serialisation for `hungerWatchCooldown`

### Phase 11 — Mandrake Fructus: ceiling Glow Berry planting
- [x] Add `PlantDirection.UP` support to `PlantingFeature.PlantItem`
- [x] Update `tryPlantAt()` in `MonsterEntity` to support ceiling scan — checks solid block above, places in air below it
- [x] Add `PlantingFeature` to `MandrakeFamily.FRUCTUS` with `CAVE_VINES`, `direction(UP)`, allowed ceiling blocks

### Phase 13 — Mandrake Chorus: poison spit (ranged attack)
- [x] Introduce `RangedAttackFeature` in HZLib Common — replaces the per-entity key-check pattern with a family-level declaration
- [x] Migrate `MushroomFamily.SNOWBALL` to use `RangedAttackFeature` — remove key-check from `MushroomEntity.registerGoals()`
- [x] Move `RangedAttackMob` + `RangedAttackGoal` registration from `MushroomEntity` into `WildTamableEntity.registerGoals()` driven by `RangedAttackFeature`
- [x] Create `MandrakeSpitProjectile` — extends `ThrowableItemProjectile`, uses `LlamaSpitRenderer` (white orb), applies Poison level 1 / 7 s + Nausea 5 s on hit, same-owner check blocks hit
- [x] Register `MANDRAKE_CHORUS_SPIT` entity type + `ThrownItemRenderer` (slimeball sprite) in `MonstersEntities`
- [x] Wire `MandrakeFamily.CHORUS` with `RangedAttackFeature` — interval 30–60 t, range 12 blocks, factory → `MandrakeSpitProjectile`
- [x] Update `MushroomSnowball.onHitEntity()` to skip same-owner peers (friendly fire prevention — projectile side)
- [x] Update `MandrakeSpitProjectile.onHitEntity()` with same-owner check (friendly fire prevention — projectile side)

### Phase 14 — Friendly fire prevention (NativeEntity)
- [x] Override `wantsToAttack(LivingEntity target, LivingEntity owner)` in `NativeEntity` — return `false` when target is a `TamableAnimal` tamed by the same owner UUID
- [x] Verify: two tamed entities with the same owner do not target each other via `NearestAttackableTargetGoal`, `OwnerHurtTargetGoal`, or `HurtByTargetGoal`

### Phase 12 — Validation
- [ ] Full build passes (0 errors) across all modules
- [ ] Runtime smoke test — spawn each affected entity, verify overlay layers render correctly
- [ ] Verify belly progression (apple / feather) works on Wisp, Spook, Mushroom Warped
- [ ] Verify Puffball puff-jump triggers autonomously
- [ ] Verify Snowball Mushroom throws snowballs at hostile mobs
- [ ] Verify Molten Gal accepts raw food, cooks, drops cooked result
- [ ] Verify Gourdragora Big spawns less frequently than Mini/Default
- [ ] Verify Fructus plants Cave Vines on valid ceiling blocks

---

## Implementation Tasks

### Task 1.1 — Emissive texture standardisation
- **Priority**: Critical — all other tasks reference the correct paths
- **Story Points**: 3
- **Scope**: Rename + move files, update all Java path strings

**Files to rename** (in `layer/`, wrong suffix `_emission` → `_emissive`):

| Current path | New path |
|---|---|
| `layer/mandrake_flower/mandrake_flower_emission.png` | `layer/mandrake_flower/mandrake_flower_emissive.png` |
| `layer/mushroom_warped/mushroom_warped_blue_emission.png` | `layer/mushroom_warped/mushroom_warped_blue_emissive.png` |
| `layer/mushroom_warped/mushroom_warped_green_emission.png` | `layer/mushroom_warped/mushroom_warped_green_emissive.png` |

**Files to move** (in `entity/`, should be in `layer/`) + rename where suffix is `_emission`:

| Current path | New path |
|---|---|
| `entity/mushroom_crimson/mushroom_crimson_red_emission.png` | `layer/mushroom_crimson/mushroom_crimson_red_emissive.png` |
| `entity/mushroom_crimson/mushroom_crimson_pink_emission.png` | `layer/mushroom_crimson/mushroom_crimson_pink_emissive.png` |
| `entity/mushroom_molten/mushroom_molten_emission.png` | `layer/mushroom_molten/mushroom_molten_emissive.png` |
| `entity/mushroom_puffball/mushroom_puffball_default_emission.png` | `layer/mushroom_puffball/mushroom_puffball_default_emissive.png` |
| `entity/mushroom_puffball/mushroom_puffball_pale_emission.png` | `layer/mushroom_puffball/mushroom_puffball_pale_emissive.png` |
| `entity/mushroom_puffball/_/enderpuffball_emission.png` | `layer/mushroom_puffball/enderpuffball_emissive.png` |
| `entity/mushroom_snowball/mushroom_snowball_emissive.png` | `layer/mushroom_snowball/mushroom_snowball_emissive.png` *(move only)* |
| `entity/mushroom_soul_wanderer/mushroom_soul_wanderer_emission.png` | `layer/mushroom_soul_wanderer/mushroom_soul_wanderer_emissive.png` |
| `entity/globberie/globberie_default_emissive.png` | `layer/globberie/globberie_default_emissive.png` *(move only)* |

> `amplectobelua_emission.png` and bed `_emission` files are WIP / placeholder assets — leave in place, they are not registered in any family class yet.

**Java path string updates**: Search all `*Family.java` files for `_emission` and `entity/...emiss` strings and update to the new canonical paths.

- **Acceptance Criteria**:
  - [x] No emissive textures remain in `entity/` folders (except WIP `_` subfolders)
  - [x] All emissive layer textures use `_emissive.png` suffix
  - [x] All Java path references updated
  - [x] Full build passes after rename

---

### Task 2.1 — `BellyLevel` enum + `BellyFeature`
- **Priority**: Critical — gates all belly overlay work
- **Story Points**: 3
- **Location**: `sources/common/hzlib-1.21.1/Common/src/main/java/net/heriazone/hzlib/api/entity/features/`

`BellyLevel.java`:
```java
public enum BellyLevel {
    SLIM(0), CHUBBY(1), TUMMY(2), INFLATED(3), CHUNKY(4);
    private final int level;
    BellyLevel(int level) { this.level = level; }
    public int getLevel() { return level; }
    public static BellyLevel byLevel(int level) { ... } // safe lookup with SLIM fallback
}
```

`BellyFeature.java`:
```java
public class BellyFeature {
    private final BellyLevel maxLevel;           // config cap declared on family
    private final Map<BellyLevel, String> textures; // level → overlay texture path

    public BellyFeature maxLevel(BellyLevel max) { ... }
    public BellyFeature texture(BellyLevel level, String texturePath) { ... }
    public boolean hasTextureFor(BellyLevel level) { ... }
    public String getTexturePath(BellyLevel level) { ... }
    public BellyLevel getMaxLevel() { return maxLevel; }
}
```

- **Acceptance Criteria**:
  - [ ] `BellyLevel` and `BellyFeature` compile in HZLib Common
  - [ ] `BellyFeature` fluent builder works

---

### Task 2.2 — `BELLY_LEVEL` on `MonsterEntity`, replace `HAS_BELLY`
- **Priority**: Critical
- **Story Points**: 3
- **Location**: `MonsterEntity.java`

Replace:
```java
protected static final EntityDataAccessor<Boolean> HAS_BELLY = ...;
```
With:
```java
protected static final EntityDataAccessor<Integer> BELLY_LEVEL = 
    SynchedEntityData.defineId(MonsterEntity.class, EntityDataSerializers.INT);
```

Add `getBellyLevel()`, `setBellyLevel(int)`, clamp to `[0, 4]`.

Rewrite `handleBellyProgression()`:
- Gate: `if (!nativeEntity.hasFeature(BellyFeature.class)) return false`
- Apple: `setBellyLevel(min(current + 1, feature.getMaxLevel().getLevel()))` + consume item + play sound
- Feather: `setBellyLevel(max(current - 1, 0))` + **do NOT consume item** + play sound

NBT migration: if loading old `HasBelly=true` → set `BELLY_LEVEL = 2` (TUMMY). If `HasBelly=false` → `BELLY_LEVEL = 0`.

- **Acceptance Criteria**:
  - [ ] `HAS_BELLY` removed, `BELLY_LEVEL` in its place
  - [ ] Apple consumed, feather not consumed
  - [ ] Old saves with `HasBelly` load without crash

---

### Task 2.3 — `RenderConditions.bellyAtLeast()` + `SizeVariantFeature.spawnWeight`
- **Priority**: High
- **Story Points**: 2

`RenderConditions`:
```java
public static <T extends MonsterEntity> Predicate<T> bellyAtLeast(BellyLevel level) {
    return entity -> entity.getBellyLevel() >= level.getLevel();
}
```

`SizeVariantFeature.SizeConfig.Builder`:
```java
private int spawnWeight = 1;
public Builder spawnWeight(int weight) { this.spawnWeight = weight; return this; }
```

`SizeVariantFeature`:
```java
public SizeConfig pickWeightedRandom() {
    int total = configs.values().stream().mapToInt(SizeConfig::getSpawnWeight).sum();
    int roll = ThreadLocalRandom.current().nextInt(total);
    int cursor = 0;
    for (SizeConfig config : configs.values()) {
        cursor += config.getSpawnWeight();
        if (roll < cursor) return config;
    }
    return defaultConfig;
}
```

- **Acceptance Criteria**:
  - [ ] `bellyAtLeast()` predicate compiles and is usable in `OverlaySlot.conditional()`
  - [ ] `pickWeightedRandom()` distributes according to declared weights

---

### Task 2.4 — `PlantingFeature` ceiling direction
- **Priority**: High
- **Story Points**: 2

Add `PlantDirection` enum (`DOWN`, `UP`) to `PlantingFeature.PlantItem`. Update `tryPlantAt()` in `MonsterEntity`:

```java
// UP direction: find solid ceiling, place vine in air block below it
if (entry.getDirection() == PlantDirection.UP) {
    BlockState ceiling = level().getBlockState(pos.above());
    BlockState target  = level().getBlockState(pos);
    if (entry.isValidCeiling(ceiling) && target.isAir()) {
        level().setBlock(pos, entry.getPlant().defaultBlockState(), 3);
        return true;
    }
}
```

- **Acceptance Criteria**:
  - [ ] `PlantDirection.UP` works for `CAVE_VINES` placement
  - [ ] Existing DOWN direction unchanged

---

### Task 3.1–3.3 — Wisp family full fix
- **Story Points**: 3

```java
// TextureVariantFeature — correct keys matching actual filenames
.withVariants("wisp_girl_blue", "wisp_girl_blue")  // single base texture
.withDefault("wisp_girl_blue", "wisp_girl_blue")

// BellyFeature
.withFeature(BellyFeature.class, new BellyFeature()
    .maxLevel(BellyLevel.TUMMY)
    .texture(BellyLevel.TUMMY, "monsters_girls:textures/layer/wisp/wisp_blue_belly_tummy.png"))

// OverlayFeature — belly CONDITIONAL + emissive ALWAYS
.withFeature(OverlayFeature.class, new OverlayFeature()
    .addSlot(OverlaySlot.conditional("belly",
        OverlaySlot.entry("monsters_girls:textures/layer/wisp/wisp_blue_belly_tummy.png",
            RenderConditions.bellyAtLeast(BellyLevel.TUMMY))))
    .addSlot(OverlaySlot.always("emissive",
        "monsters_girls:textures/layer/wisp/wisp_blue_emissive.png")))
```

(Repeat per color: blue, green, yellow)

- **Acceptance Criteria**:
  - [ ] Wisp renders correct base texture
  - [ ] Belly overlay appears at TUMMY level
  - [ ] Emissive renders at full brightness unconditionally

---

### Task 4.1–4.3 — Spook family full fix
- **Story Points**: 3

Belly: SLIM + TUMMY per color.
Emissive: cream/teal → `spook_girl_emissive.png`, peach → `spook_girl_peach_emissive.png`.

```java
.withFeature(BellyFeature.class, new BellyFeature()
    .maxLevel(BellyLevel.TUMMY)
    .texture(BellyLevel.SLIM,  "monsters_girls:textures/layer/spook/spook_cream_belly_slim.png")
    .texture(BellyLevel.TUMMY, "monsters_girls:textures/layer/spook/spook_cream_belly_tummy.png"))

.withFeature(OverlayFeature.class, new OverlayFeature()
    .addSlot(OverlaySlot.conditional("belly",
        OverlaySlot.entry("...spook_cream_belly_tummy.png", RenderConditions.bellyAtLeast(BellyLevel.TUMMY)),
        OverlaySlot.entry("...spook_cream_belly_slim.png",  RenderConditions.bellyAtLeast(BellyLevel.SLIM))))
    .addSlot(OverlaySlot.always("emissive", "...spook_girl_emissive.png")))
```

- **Acceptance Criteria**:
  - [ ] SLIM level shows slim belly overlay
  - [ ] TUMMY level shows tummy overlay (higher-priority entry wins)
  - [ ] Emissive renders unconditionally

---

### Task 5.1–5.3 — Mushroom Warped full registration
- **Story Points**: 4

Currently `MushroomFamily.WARPED` has no variant registrations. Add full `registerWarpedVariants()` static block mirroring the pattern of other mushroom families. Belly: CHUBBY + TUMMY per color. Emissive: per color.

```java
.withFeature(BellyFeature.class, new BellyFeature()
    .maxLevel(BellyLevel.TUMMY)
    .texture(BellyLevel.CHUBBY, "...mushroom_warped_blue_belly_chubby.png")
    .texture(BellyLevel.TUMMY,  "...mushroom_warped_blue_belly_tummy.png"))
```

- **Acceptance Criteria**:
  - [ ] WARPED spawns with correct texture
  - [ ] CHUBBY and TUMMY belly overlays render correctly

---

### Task 6.1–6.3 — Puffball emissive + puff-jump + Fluffball
- **Story Points**: 5

Puffball emissive: add `ALWAYS` slot per variant.

Puff-jump (autonomous, `aiStep()` server side):
```java
if (puffCooldown <= 0 && isTame() && getOwner() != null) {
    if (shouldPuff()) { // e.g., owner is jumping or climbing
        addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST,    60, 1));
        addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 80, 0));
        // trigger "wave" animation via AnimationProfile special pool
        puffCooldown = 200; // 10s
    }
}
```

Fluffball: new static instance, flying AI, End biome, `ender_puffball_girl.png` + `enderpuffball_emissive.png`.

- **Acceptance Criteria**:
  - [ ] Puffball emissive renders
  - [ ] Puffball puffs autonomously near owner when appropriate
  - [ ] Fluffball spawns in End, flies, has correct textures

---

### Task 7.1 — Snowball Mushroom: snowball throwing
- **Story Points**: 3
- **Reference**: `SnowGolemEntity.performRangedAttack()` in vanilla

```java
// In MushroomEntity or MushroomSnowballEntity:
@Override
public void performRangedAttack(LivingEntity target, float distanceFactor) {
    Snowball snowball = new Snowball(level(), this);
    double dx = target.getX() - getX();
    double dy = target.getEyeY() - (getEyeY() - 0.1);
    double dz = target.getZ() - getZ();
    double dist = Math.sqrt(dx*dx + dz*dz);
    snowball.shoot(dx, dy + dist * 0.2, dz, 1.6f, 12.0f);
    level().addFreshEntity(snowball);
    playSound(SoundEvents.SNOWBALL_THROW, 0.5f, 0.4f / (getRandom().nextFloat() * 0.4f + 0.8f));
}
```

Wire `RangedAttackGoal` targeting hostile mobs in `MushroomSnowballEntity.registerGoals()`.

- **Acceptance Criteria**:
  - [ ] Mushroom Snowball throws snowballs at hostile mobs
  - [ ] Snowballs freeze + slow on hit (via existing EmanationFeature ON_ATTACK)
  - [ ] Sound plays on throw

---

### Task 9.1 — Molten Gal campfire cooking
- **Story Points**: 8

New `MushroomMoltenEntity` subclass (or field additions to `MushroomEntity` gated by family check).

4 cook slots — synced `ItemStack` + cook timer per slot. `CampfireCookingRecipe` resolution via `level().getRecipeManager()`. 

- **Acceptance Criteria**:
  - [ ] 4 items can be placed simultaneously
  - [ ] Cooked result drops after vanilla campfire cook time
  - [ ] Smoke particle emits while cooking
  - [ ] Items float visually above her head
  - [ ] NBT saves and loads cook state

---

### Task 10.1 — Gourdragora spawn weight + hunger watch
- **Story Points**: 4

Add `spawnWeight` to `SIZE_FEATURE`: Mini=40, Default=40, Big=20.
Override `initializeSpawnVariants()` in `GourdragoraEntity` to call `feature.pickWeightedRandom()`.
Add `hungerWatchCooldown` field + tick logic in `aiStep()`.

- **Acceptance Criteria**:
  - [x] Big Gourdragora spawns ~20% of the time over a large sample
  - [x] Pumpkin pie drops when owner hunger < 11, with cooldown

---

### Task 11.1 — Mandrake Fructus ceiling planting
- **Story Points**: 3

- **Acceptance Criteria**:
  - [x] Fructus plants `CAVE_VINES` on valid ceiling blocks in Lush Caves and Flower Forest
  - [x] Existing DOWN planting on other Mandrake variants unchanged

---

### Task 13.1 — `RangedAttackFeature` (HZLib Common)
- **Priority**: Critical — gates Chorus spit and consolidates the Snowball key-check
- **Story Points**: 3
- **Location**: `sources/common/hzlib-1.21.1/Common/src/main/java/net/heriazone/hzlib/api/entity/features/RangedAttackFeature.java`

Replaces the `"mushroom_snowball"` key-check hack in `MushroomEntity.registerGoals()` with a proper family-level declaration. Any future entity that needs ranged attack simply adds `.withFeature(RangedAttackFeature.class, ...)` to its family — no entity-class override needed.

```java
public class RangedAttackFeature {
    private final BiFunction<EntityType<? extends ThrowableProjectile>, Level, ThrowableProjectile> projectileFactory;
    private final EntityType<? extends ThrowableProjectile> projectileType;
    private final int  minInterval;  // ticks
    private final int  maxInterval;  // ticks
    private final float attackRange; // blocks

    // Fluent builder ...
    public static RangedAttackFeature of(EntityType<? extends ThrowableProjectile> type,
                                         BiFunction<EntityType<?>, Level, ThrowableProjectile> factory) { ... }
    public RangedAttackFeature interval(int min, int max) { ... }
    public RangedAttackFeature range(float blocks) { ... }
}
```

`WildTamableEntity.registerGoals()` reads `RangedAttackFeature` from `nativeEntity` and, if present, replaces the melee attack goal with `RangedAttackGoal` + `NearestAttackableTargetGoal<Monster>`.

- **Acceptance Criteria**:
  - [ ] `RangedAttackFeature` compiles in HZLib Common with no monsters mod dependency
  - [ ] `WildTamableEntity.registerGoals()` auto-wires `RangedAttackGoal` when feature is present
  - [ ] `MushroomFamily.SNOWBALL` migrated — key-check removed from `MushroomEntity`

---

### Task 13.2 — `MandrakeSpitProjectile`
- **Priority**: High
- **Story Points**: 3
- **Location**: `sources/monsters/.../Common/.../entity/projectile/MandrakeSpitProjectile.java`

Extends `ThrowableItemProjectile`. Renders as a slimeball item sprite (or the llama spit `LlamaSpit` render type if available — see note). Applies on entity hit:
- `POISON` level 1 / 140 ticks (7 s)
- `CONFUSION` (Nausea) level 0 / 100 ticks (5 s)
- Same-owner check: if the target is a `TamableAnimal` with the same owner UUID as the thrower, the hit is suppressed entirely

```java
@Override
protected void onHitEntity(EntityHitResult result) {
    super.onHitEntity(result);
    if (!(result.getEntity() instanceof LivingEntity target)) return;
    // Same-owner check — friendly fire prevention
    if (getOwner() instanceof TamableAnimal thrower
            && target instanceof TamableAnimal peer
            && thrower.getOwnerUUID() != null
            && thrower.getOwnerUUID().equals(peer.getOwnerUUID())) return;
    target.addEffect(new MobEffectInstance(MobEffects.POISON,    140, 1));
    target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0));
}
```

> **Render note**: `LlamaSpit` is a dedicated entity class with its own renderer (`LlamaSpitRenderer`). The cleanest approach is to register `MANDRAKE_CHORUS_SPIT` with `LlamaSpitRenderer` — this gives the correct llama-spit visual (a white orb) for free. Alternatively, `ThrownItemRenderer` with `Items.SLIME_BALL` gives a slimeball appearance. Decision: **use `LlamaSpitRenderer`** — it matches the "spit" feel exactly and requires no custom renderer.

- **Acceptance Criteria**:
  - [ ] Projectile renders as llama spit (white orb in flight)
  - [ ] Poison level 1 + Nausea applied on hit
  - [ ] Same-owner hit is suppressed

---

### Task 13.3 — Wire `MandrakeFamily.CHORUS` + register projectile
- **Priority**: High
- **Story Points**: 2
- **Location**: `MandrakeFamily.java`, `MonstersEntities.java`

```java
// MandrakeFamily.CHORUS — add RangedAttackFeature
.withFeature(RangedAttackFeature.class, RangedAttackFeature
        .of(MonstersEntities.MANDRAKE_CHORUS_SPIT,
            (type, level) -> new MandrakeSpitProjectile(type, level))
        .interval(30, 60)
        .range(12.0f))
```

`MonstersEntities`:
```java
// Projectile entity type — same dimensions as LlamaSpit (0.25 × 0.25)
public static final EntityType<MandrakeSpitProjectile> MANDRAKE_CHORUS_SPIT = Registry.register(...);
// Renderer — reuse vanilla LlamaSpitRenderer
EntityRendererRegistry.register(MANDRAKE_CHORUS_SPIT,
        ctx -> new net.minecraft.client.renderer.entity.LlamaSpitRenderer(ctx));
```

- **Acceptance Criteria**:
  - [ ] Chorus spits at hostile mobs at range 12, interval 30–60 t
  - [ ] `ThrownItemRenderer` / `LlamaSpitRenderer` registered — no null renderer crash

---

### Task 13.4 — Friendly fire prevention (projectile side)
- **Priority**: High
- **Story Points**: 1

Update `MushroomSnowball.onHitEntity()` to suppress the hit when thrower and target share the same owner UUID — same pattern as `MandrakeSpitProjectile`. Both projectile classes are in Common so the check is identical.

```java
// In both MushroomSnowball and MandrakeSpitProjectile onHitEntity():
if (getOwner() instanceof TamableAnimal thrower
        && result.getEntity() instanceof TamableAnimal peer
        && thrower.getOwnerUUID() != null
        && thrower.getOwnerUUID().equals(peer.getOwnerUUID())) return;
```

- **Acceptance Criteria**:
  - [ ] Snowball thrown by Snowball Mushroom does not apply effects to a same-owner peer
  - [ ] Spit thrown by Chorus Mandrake does not apply effects to a same-owner peer

---

### Task 14.1 — Friendly fire prevention (targeting side, NativeEntity)
- **Priority**: High
- **Story Points**: 2
- **Location**: `sources/common/hzlib-1.21.1/Common/.../NativeEntity.java`

Override `wantsToAttack(LivingEntity target, LivingEntity owner)` in `NativeEntity`. This is the hook `NearestAttackableTargetGoal` calls to validate each candidate target — returning `false` prevents the goal from ever acquiring the peer as a target.

```java
@Override
public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
    // Suppress targeting of same-owner tamed peers — covers NearestAttackableTargetGoal,
    // OwnerHurtTargetGoal, and HurtByTargetGoal without per-goal overrides.
    if (isTame() && getOwnerUUID() != null
            && target instanceof net.minecraft.world.entity.animal.Animal tamable) {
        if (tamable instanceof net.minecraft.world.entity.TamableAnimal t
                && getOwnerUUID().equals(t.getOwnerUUID())) {
            return false;
        }
    }
    return super.wantsToAttack(target, owner);
}
```

- **Acceptance Criteria**:
  - [ ] Two tamed entities with the same owner never acquire each other as AI targets
  - [ ] Untamed entities are unaffected — this override only fires when `isTame()` is true

---

## Story Points Summary

| Task | Points | Phase | Status |
|---|---|---|---|
| 1.1 Emissive standardisation | 3 | 1 | ✅ |
| 2.1 BellyLevel + BellyFeature | 3 | 2 | ✅ |
| 2.2 BELLY_LEVEL on MonsterEntity | 3 | 2 | ✅ |
| 2.3 RenderConditions + SizeVariantFeature weights | 2 | 2 | ⬜ |
| 2.4 PlantingFeature ceiling direction | 2 | 2 | ⬜ |
| 3.1–3.3 Wisp family fixes | 3 | 3 | ✅ |
| 4.1–4.3 Spook family fixes | 3 | 4 | ✅ |
| 5.1–5.3 Mushroom Warped registration | 4 | 5 | ✅ |
| 6.1–6.3 Puffball + Fluffball | 5 | 6 | ✅ |
| 7.1 Snowball throwing | 3 | 7 | ✅ |
| 8.1–8.3 Crimson + Soul Wanderer + Molten emissive | 2 | 8 | ✅ |
| 9.1 Molten campfire cooking | 8 | 9 | ✅ |
| 10.1 Gourdragora weight + hunger watch | 4 | 10 | ✅ |
| 11.1 Fructus ceiling planting | 3 | 11 | ✅ |
| 12.x Validation | 5 | 12 | ⬜ |
| 13.1 RangedAttackFeature + Snowball migration | 3 | 13 | ✅ |
| 13.2 MandrakeSpitProjectile | 3 | 13 | ✅ |
| 13.3 Chorus family wiring + registration | 2 | 13 | ✅ |
| 13.4 Friendly fire — projectile side | 1 | 13 | ✅ |
| 14.1 Friendly fire — NativeEntity targeting | 2 | 14 | ✅ |
| **Total** | **68** | | |

---

## Dependencies

- Sprint 09 Phase 1 complete ✅ (HZLib entity rename)
- Feature notes updated ✅ (`MonsterGirls_Feature_Implementation_Notes.md`)

## Risks

### Risk 1: Overlay pool size
`MonsterEntity` currently declares 2 overlay pool slots. Belly adds a persistent slot on entities that already have 1 (e.g. Mandrake Flower has hair + emissive — but emissive is ALWAYS so it's not persistent; Gourdragora has carving which is 1 persistent slot). Belt check: any entity with **both** a belly slot AND an existing persistent slot (carving, hair) needs pool size ≥ 2 — we are already at 2. Pool expansion to 3 may be needed for future entities.
**Mitigation**: Audit pool usage per entity type before Task 2.2.

### Risk 2: `BELLY_LEVEL` ID shift
Removing `HAS_BELLY` (Boolean) and adding `BELLY_LEVEL` (Integer) changes `SynchedEntityData` accessor IDs for `MonsterEntity` subclasses. Old saves will desync. NBT migration (`HasBelly` → `BELLY_LEVEL`) handles persistence but live entities on a running server need a reload.
**Mitigation**: Document as a breaking change in CHANGELOG. Require world reload after update.

### Risk 3: Molten campfire cooking scope
The `CookingItemLayer` renderer is loader-specific and touches `ItemRenderer` — high risk of API differences between Fabric/Forge/NeoForge. May need separate implementations per loader.
**Mitigation**: Implement on Fabric first, validate approach, then port.

### Risk 4: `RangedAttackFeature` projectile factory signature
The projectile factory in `RangedAttackFeature` references `ThrowableProjectile` — a Minecraft class — so HZLib already has a Minecraft dependency (it does, throughout). The factory is `BiFunction<EntityType, Level, ThrowableProjectile>`, which is a functional interface with no loader-specific types. This is safe in Common.
**Mitigation**: Keep the factory signature generic. If a loader-specific projectile type is needed, the factory lambda captures the loader-specific type at declaration site (in the family class, which is loader-agnostic anyway).

### Risk 5: `LlamaSpitRenderer` availability
`LlamaSpitRenderer` is a vanilla Fabric-accessible class. It renders `LlamaSpit` entities as a white textured orb. Registering it for `MandrakeSpitProjectile` should work out-of-the-box. If the renderer's constructor signature differs between Minecraft versions, fall back to `ThrownItemRenderer` with `Items.SLIME_BALL` sprite.
**Mitigation**: Verify constructor signature before wiring. Fall back documented above.

---

## References

- `docs/development/notes/feature-implementations/MonsterGirls_Feature_Implementation_Notes.md`
- `docs/development/decisions/ADR_017_OverlayFeature_Composable_Visual_Layer_System.md`
- `docs/development/decisions/ADR_018_Ecosystem_Terminology_and_Rename_System.md`
