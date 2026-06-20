# Monster Girls — Feature Implementation Notes

**Status**: Analysis & Planning
**Date**: 2026-06-17
**Scope**: Mushroom Gals spawn item variants, Belly layers, Costume layers (seasonal), Gourdragora shears carving

---

## Architecture Context

These features all touch a shared stack:

```
MonsterGirl Mod
  ↓ depends on
lovelylib (LovelySpawnItem, BaseSpawnItem, SharedConfigs, ItemSpawnHelper)
  ↓ depends on
hzlib (InternalEntity, InternalEntityType, BaseInternalRenderLayer, ConditionalOverlayLayer,
        TextureVariantFeature, AppearanceVariantFeature, SizeVariantFeature, ExchangeConditions)
```

**Key principle**: HZLib is the single source of truth for all entity variant + rendering
infrastructure. Do not duplicate layer logic, condition logic, or variant resolution in the
monster mod — extend HZLib if a capability is missing, then use it.

---

## Task 1: Variant-Aware Spawn Item for Mushroom Gals

### What it needs to do
When a mushroom gal with a specific appearance variant (e.g. `mushroom_brown_boletus`,
`mushroom_brown_ruby`) is captured into a spawn item, the item's texture should reflect
the captured variant — the same way LovelyRobot spawn items show the robot's dye color.

### How LovelyRobot does it (the model to follow)
1. **Item model JSON** uses `"overrides"` with a `"variant"` predicate (integer 0–15 mapped
   to each dye color). Each predicate points to a model like `lovelylib:item/vanilla/vanilla00`.
2. **Custom item property** registers a float predicate keyed `"variant"` that reads an int
   from the item's `DataComponents.CUSTOM_DATA` NBT. The float encodes the variant index.
3. **Dye recipe** (`*_spawn_dye.json`) — a custom `LovelySpawnDyeRecipe` transfers the
   `TextureVariant` / color NBT from the robot's core item into the spawn item result.
4. **On spawn** — `BaseSpawnItem.useOn()` → `ItemInteractionHelper.handleBlockPlacement()`
   reads the stored variant key from the item's CustomData and calls `setTextureVariant()`
   on the freshly spawned entity.

### What needs to be built for mushroom gals

**In lovelylib (or the monster mod's Common module):**

- `NativeSpawnItem` — a new class parallel to `LovelySpawnItem` for monster girls. It extends
  the loader's spawn egg base and delegates to a `BaseNativeSpawnItem` (or re-uses
  `BaseSpawnItem` if it already handles the variant read path generically).

  > **Architectural check first**: See if `BaseSpawnItem` can be extended directly or if its
  > `ItemInteractionHelper` already reads a `TextureVariant` string. If it does, `NativeSpawnItem`
  > is just a thin wrapper. If it reads an int index, you need the string→int mapping layer.

- **Item property predicate**: Register a custom float property `"variant"` on the item that
  maps the `TextureVariant` string key to a float index. The index list must exactly match the
  order of `"overrides"` entries in the model JSON. Use `TextureVariantFeature` on the entity
  type as the canonical ordered list — do NOT hardcode the order anywhere else.

- **Spawn item model JSON** per entity type that has visual variants (e.g. mushroom). One model
  file per variant (e.g. `mushroom_brown_boletus.json`, `mushroom_brown_ruby.json`) referenced
  via overrides in the root spawn item JSON.

  > **Important**: robots have 16 color variants → 16+1 model JSONs per robot type.
  > Mushroom gals have fewer distinct appearances (biome-based, not dye-based). The number of
  > models equals the number of registered `TextureVariantFeature` variants on that entity type.
  > Keep this as the single source so adding a variant automatically requires a new model — no
  > silent mismatches.

- **On-capture recipe/mechanic**: When the player captures the entity (how capture works in
  the monster mod — net, trap, item?), write the entity's current `getTextureVariant()` string
  into the spawn item's `CustomData`. This is the transfer moment, equivalent to the robot
  core dye recipe. Identify the capture mechanic first; the NBT write should happen at that
  exact point.

**Not needed (already handled by HZLib):**
- Variant selection at spawn (`initializeSpawnVariants` + `BiomeAppearanceFeature`)
- Variant persistence on the entity (`TextureVariant` in `SynchedEntityData` + NBT)
- Variant validation (`isValidTextureVariant`)

### HZLib changes required
Likely none if `ItemInteractionHelper` already handles string-keyed `TextureVariant` reads.
If it only reads int `TextureID`, add a string read path — do it in HZLib, not in the mod.

---

## Task 2: Belly Support via Layers + Config

### What it needs to do
Render an additional belly overlay texture on top of the base entity texture. The max belly
level shown is configurable: `slim` (no belly), `tummy` (slight belly), `chonky` (max belly).
The config controls the ceiling — the entity's actual belly level determines what renders,
capped by the config value.

### Architecture: this is a ConditionalOverlayLayer

`ConditionalOverlayLayer<T>` already does exactly this — it holds a prioritized list of
`(Predicate<T>, texturePath)` pairs and renders the first match. There is **no reason to
create a new layer class**. Wire an existing `ConditionalOverlayLayer` with the right
predicates.

**Design:**

```
ConditionalOverlayLayer<MushroomEntity>
  .addConditionalTexture(entity -> entity.getBellyLevel() >= CHONKY && configAllows(CHONKY),
                         "{mod}:textures/entity/mushroom/mushroom_brown_boletus_belly_chonky.png")
  .addConditionalTexture(entity -> entity.getBellyLevel() >= TUMMY && configAllows(TUMMY),
                         "{mod}:textures/entity/mushroom/mushroom_brown_boletus_belly_tummy.png")
  // default = null texture = no belly rendered
```

The `configAllows(level)` check reads from the config's max belly enum and returns whether
the requested level is within the allowed ceiling.

### Belly level on the entity

Where does `getBellyLevel()` come from? Options:

- **Option A — Static per entity type**: The entity type's `NativeEntityType` declares a
  fixed belly level (e.g. Boletus is always `TUMMY`, Ruby is always `CHONKY`). No runtime
  state. The predicate just checks `nativeEntity.getBellyProfile()` and the config cap.

- **Option B — Dynamic stat** (more flexible): Entity tracks a belly level as a synced int or
  enum stored in `SynchedEntityData` (like `TEXTURE_VARIANT`). Changes over time (feeding,
  growth?). The predicate reads the live value each render tick.

> **Recommendation**: Start with Option A (static per entity type) since belly is an
> appearance descriptor, not a gameplay stat, at least for the first pass. The predicate is
> then purely `entityType.getAppearanceProfile().getBellyLevel()` + config cap — zero new
> synced data needed.

### Config integration

Follow the exact pattern in `LegacyConfigs` and `SharedConfigs`. Add to the monster mod's
config spec:

```java
// In SharedMonsterConfigs (or equivalent cross-loader config):
public enum BellyLevel { SLIM, TUMMY, CHONKY }

// Config entry:
ForgeConfigSpec.ConfigValue<String> MAX_BELLY_LEVEL; // stored as string, parsed to enum
```

Expose a static accessor like `SharedMonsterConfigs.getMaxBellyLevel()` returning the
parsed `BellyLevel` enum. The layer predicate uses this — no direct config spec reads inside
rendering code.

**Bounds**: `BellyLevel` has 3 values. Default: `TUMMY` (middle ground, not hidden, not max).

### HZLib changes required
None. `ConditionalOverlayLayer` + `BaseInternalRenderLayer` cover this entirely.

### Texture file naming convention
One texture per belly level per entity variant:
```
textures/entity/mushroom/mushroom_brown_boletus_belly_tummy.png
textures/entity/mushroom/mushroom_brown_boletus_belly_chonky.png
```
`SLIM` means no overlay texture file needed — just no layer fires.

---

## Task 3: Seasonal Costume Support via Conditional Layers

### What it needs to do
Overlay a costume texture on the entity during a specific real-world month. Defined cases:
- Halloween: month of October
- Christmas: month of December

Future costumes follow the same pattern; this is the foundation set.

### Architecture

This is another `ConditionalOverlayLayer`. The only missing piece is **a month condition**.

### Adding the month condition to HZLib: `ExchangeConditions` or a new helper

`ExchangeConditions` already has `isDaytime()`, `isNighttime()`, `inTimeRange()`, `isRaining()`.
The month check is the same shape — a `Predicate<T>` on the entity — but the predicate needs
access to real-world date, not Minecraft game ticks.

**Where to add it**:

Add to `ExchangeConditions` (or a sibling `RenderConditions` class if you prefer to keep
render-specific predicates separate from exchange-specific ones):

```java
/**
 * Passes when the current real-world month matches any of the given months.
 * Uses java.time.Month. Evaluated at render time — no caching needed since
 * month changes are infrequent and the predicate is stateless.
 *
 * @param months months that satisfy this condition (1 = January ... 12 = December)
 * @return month condition predicate
 */
public static <T extends InternalEntity> Predicate<T> inMonth(java.time.Month... months) {
    Set<java.time.Month> monthSet = new HashSet<>(Arrays.asList(months));
    return entity -> monthSet.contains(java.time.LocalDate.now().getMonth());
} // inMonth ()
```

> **Decision note**: Put this in HZLib as a static utility (same class as `ExchangeConditions`
> or a new `RenderConditions` in `hzlib/api/rendering/`). Do NOT put month-checking logic in
> the monster mod — other mods will need it too (robots could get seasonal costumes later).

### Layer wiring for Mushroom Gals

```java
ConditionalOverlayLayer<MushroomEntity> costumeLayer = new ConditionalOverlayLayer<>(/* no default = no costume off-season */)
    .addConditionalTexture(
        RenderConditions.inMonth(Month.OCTOBER),
        "{mod}:textures/entity/mushroom/mushroom_brown_boletus_costume_halloween.png")
    .addConditionalTexture(
        RenderConditions.inMonth(Month.DECEMBER),
        "{mod}:textures/entity/mushroom/mushroom_brown_boletus_costume_christmas.png");
```

Register this layer in the entity's renderer setup alongside the belly layer.

### Layer ordering matters

Costumes should render **on top of** the belly layer so the costume visually covers the belly.
Register layers in this order:

```
1. Base texture (entity's current TextureVariant)
2. Belly overlay (ConditionalOverlayLayer — belly)
3. Costume overlay (ConditionalOverlayLayer — seasonal)
4. Emissive layer (if any glowing elements)
```

### Texture file naming convention
```
textures/entity/mushroom/mushroom_brown_boletus_costume_halloween.png
textures/entity/mushroom/mushroom_brown_boletus_costume_christmas.png
```

For entities with multiple base variants, each needs its own costume textures (the costume
is drawn on top but must match the base palette). That's N_variants × N_costumes texture files.
If the costume is a flat overlay that works on any base (e.g. a hat-only texture with full
alpha), one texture per costume is sufficient.

### HZLib changes required
One new static method in `ExchangeConditions` (or a new `RenderConditions` utility class):
`inMonth(Month...)`. That's the only addition needed.

---

## Task 4: Gourdragora Carving via Shears (Layer Mechanic)

### What it needs to do
A player can use shears on a Gourdragora to carve it, changing its visual appearance — likely
switching between `uncarved → carved face variant A → carved face variant B → ...`. This is
a **player-initiated interaction that triggers a permanent visual state change**.

### Architecture: ExchangeFeature + AppearanceVariantFeature

Gourdragora already uses `AppearanceVariantFeature` (composite texture+model+animator — see
HZLib docs: "entities like Gourdragora require all three dimensions to change together").
Shears carving is a **state transition on the entity's current AppearanceVariant**, driven
by player interaction.

**Two ways to model this:**

**Option A — ExchangeFeature rule (preferred)**

Add a shears exchange rule on Gourdragora's `NativeEntityType`:

```java
.withFeature(ExchangeFeature.class, new ExchangeFeature()
    .addRule(ExchangeRule.builder()
        .withItem(Items.SHEARS)
        .withCondition(ExchangeConditions.anyPlayer())
        .onFire(ctx -> {
            // Cycle through carve variants on the entity
            ctx.getEntity().cycleCarveVariant();
            // Optionally: play carving sound, spawn wood particle
        })
        .build()))
```

The `ExchangeFeature` system already handles item consumption, cooldowns, and server-side
execution — use it instead of writing a custom `handleSpecificInteractions` branch.

**Option B — Direct handleSpecificInteractions override**

Override `handleSpecificInteractions` in the Gourdragora entity class to detect `ShearsItem`
and call a `cycleCarveVariant()` method. Simpler but bypasses the ExchangeFeature system's
cooldown and condition infrastructure.

> **Recommendation**: Use Option A if the carving has any timing constraints (cooldown, one
> carve per interaction, etc.). Use Option B if it's truly a simple one-shot "use shears →
> change variant" with no rules.

### Entity-side: cycleCarveVariant()

Add a method on the Gourdragora entity class (or `MonsterEntity` base if all monsters should
support this pattern):

```java
/**
 * Cycles the entity to the next appearance variant in the registered sequence.
 * Used for player-driven state changes (e.g., carving with shears).
 */
public void cycleCarveVariant() {
    if (nativeEntity == null) return;
    nativeEntity.getFeature(AppearanceVariantFeature.class).ifPresent(f -> {
        List<IAppearanceVariant> variants = f.getAvailableVariants(nativeEntity.getKey());
        int current = variants.indexOf(/* current appearance */);
        int next = (current + 1) % variants.size();
        IAppearanceVariant nextVariant = variants.get(next);
        setTextureVariant(nextVariant.getTextureKey());
        setModelVariant(nextVariant.getModelKey());
        setAnimatorVariant(nextVariant.getAnimatorKey());
    });
} // cycleCarveVariant()
```

> **Note on current appearance index**: `AppearanceVariantFeature.getAvailableVariants()` returns
> a list. You need to match the current `getTextureVariant()` key against the variants in that
> list to find the current index. Alternatively, store a separate `CARVE_STAGE` synced int if
> the carving stages are ordered and persistent.

### Shears interaction: layer vs. appearance variant

The phrasing "via layers" in the task description might mean the carving result should show
as an additional overlay (the carved face is a layer on top), rather than a full appearance
swap. Evaluate which fits:

- **Full appearance swap** (via `AppearanceVariantFeature`): The entire entity texture changes.
  Appropriate if carved and uncarved Gourdragora look very different overall.

- **Layer overlay** (via `ConditionalOverlayLayer`): Base texture stays the same; a carved face
  overlay texture is shown/hidden based on a `CARVED` flag on the entity. Appropriate if only
  the face area changes and the rest stays the same.

> **Recommendation**: Use a **layer overlay** if the model has a distinct face/top area that
> can be masked. The carved face texture would cover that area with transparency everywhere else.
> Use appearance swap if you have distinct model variants (e.g., different hat cut shapes) that
> can't be handled by a flat texture overlay.

If using a layer:

```java
// On the entity: add a new synced boolean
EntityDataAccessor<Boolean> CARVED = SynchedEntityData.defineId(MonsterEntity.class, EntityDataSerializers.BOOLEAN);

// In the renderer:
ConditionalOverlayLayer<GourdragoraEntity> carveLayer = new ConditionalOverlayLayer<>("")
    .addConditionalTexture(
        entity -> entity.isCarved(),
        "{mod}:textures/entity/gourdragora/gourdragora_carved_face.png");
```

### NBT persistence
The `CARVED` state (or the carve stage index) must be persisted in `addAdditionalSaveData` /
`readAdditionalSaveData`. If using the `TextureVariant` string to encode the carved variant,
it's already persisted for free via `InternalEntity.addAdditionalSaveData`. That's the cleanest
path — define variant keys like `gourdragora_default` and `gourdragora_carved_1`, etc., and
let the existing NBT system handle it.

### HZLib changes required
- Possibly: `ExchangeConditions` check for `Items.SHEARS` (if not already present — check
  `ExchangeConditions.java`). Currently the conditions check ownership, biome, time, weather,
  state — but not held item type. The item matching happens at the rule level in `ExchangeRule`,
  not in conditions. So no HZLib change needed for the shears check itself.
- If `cycleCarveVariant()` is general enough for any monster (not just Gourdragora), add it to
  `MonsterEntity` base class. Otherwise keep it in `GourdragoraEntity`.

---

## Cross-Cutting Concerns

### Single source of truth for variant lists
The `TextureVariantFeature` (or `AppearanceVariantFeature`) registered on the entity type is
the canonical list of all visual variants. The spawn item model JSON `"overrides"` array must
derive its order from that same list — not be a separate hardcoded array. If adding a variant
to the feature auto-adds it to the spawn item model, maintenance is trivial. If they're
manually kept in sync, they will eventually diverge. Write a datagen task or at minimum a
unit test that validates they match.

### Layer registration location
All layers must be registered in the renderer's setup method (client-side, loader-specific).
The conditions and textures themselves (the `ConditionalOverlayLayer` configuration) should be
built in a Common helper class so the logic isn't duplicated across Forge/Fabric/NeoForge
renderer classes.

### Config placement
New monster-girl config entries go in `SharedMonsterConfigs` (cross-loader defaults) and the
loader-specific `MonsterConfigs` (spec registration), mirroring exactly how `SharedConfigs` and
`LegacyConfigs` work. Do **not** add monster-girl config keys to the robot config files.

### HZLib additions summary
| Feature | HZLib change needed |
|---|---|
| Spawn item variants | None if `ItemInteractionHelper` already reads string variants. Add string read path if missing. |
| Belly layers | None — `ConditionalOverlayLayer` covers it. |
| Seasonal costumes | Add `inMonth(Month...)` predicate to `ExchangeConditions` (or new `RenderConditions`). |
| Gourdragora shears | None for shears check (handled in `ExchangeRule`). Add `cycleCarveVariant()` to base if general. |

---

## Open Questions Before Implementation

1. **Capture mechanic**: How does the player capture a monster girl? This determines where to
   write the `TextureVariant` string into the spawn item's `CustomData`. Without this, the
   spawn item variant task can't be completed.

2. **Belly level source**: Static per entity type (appearance profile) or dynamic entity stat?
   Answer determines whether a new `SynchedEntityData` field is needed or not.

3. **Gourdragora carve: layer vs. appearance swap**: Does the carving change only the face
   texture (layer overlay) or the full appearance including model (appearance variant swap)?
   This determines whether to use `ConditionalOverlayLayer` + `CARVED` boolean, or just
   cycle `AppearanceVariantFeature` variants.

4. **Costume texture scope**: Does the Halloween overlay work for all mushroom variants (one
   texture, semi-transparent overlay), or does each variant need a custom costume texture?
   Affects the number of texture files to produce.

5. **Carve stages**: Is Gourdragora carving a binary (uncarved/carved) or does it cycle through
   multiple stages (uncarved → smile face → scary face → star pattern → ...)? This affects
   the variant count and the `cycleCarveVariant()` logic.
