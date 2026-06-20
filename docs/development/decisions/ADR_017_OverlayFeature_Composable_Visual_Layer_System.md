# ADR 017: OverlayFeature — Composable Visual Layer System

**Status**: Accepted  
**Date**: 2026-06-17  
**Decision Makers**: Serge  
**Consulted**: Design session — Monster Girls visual layer needs, Mandragora hairstyle composition, Gourdragora carving mechanic, Gourdragora variant-tinted carvings, Jack'o face-cover inversion, seasonal costume system, OverlayFeature architecture discussion

---

## Context

### The immediate problem

Four visual requirements arrived simultaneously from the Monster Girls roadmap:

- **Mushroom Gals** — belly overlay (slim / tummy / chonky), configurable ceiling
- **All entities** — seasonal costumes (Halloween in October, Christmas in December), overlaid on top of the base appearance
- **Mandragora** — multiple hairstyle options per body variant, randomly selected at spawn, without requiring a separate full texture per body+hair combination
- **Gourdragora** — player-carved face patterns, cycled with shears, persistent across sessions

The naive path for each of these is a different approach: config-gated conditional textures in the renderer for belly, month-checking predicates for costumes, more texture files per variant for hairstyles, a new entity state flag for carvings. That produces five different ad-hoc mechanisms, all living in different places, with no shared vocabulary.

Before implementing any of them, we recognised they are all expressions of the same concept.

### Recognising the pattern

Belly, costumes, hair, and carvings share a structural shape:

> **An additional texture layer renders on top of the base appearance. Which texture (if any) renders is determined by a rule — a condition, a random choice, a player action, or an unconditional always-on.**

The only differences are *how the active texture is selected* and *whether that selection is persistent state*. Everything else — the idea of "a texture layer keyed to the entity type, resolved at render time" — is identical.

This is the same recognition that produced `EmanationFeature`: before that, the Molten Gal's fire, the Mandrake's scream, and the Mandrake's blessing looked like three different things. They weren't. The same recognition now applies to visual layers.

### The current layer system and its gap

HZLib already has a rendering layer system: `IInternalRenderLayer`, `BaseInternalRenderLayer`, `ConditionalOverlayLayer`, `EmissiveLayer`, `DynamicColorLayer`, `InternalLayerRenderer`. These classes are well-designed and should remain unchanged. They are the *execution* layer — they know how to draw.

The gap is the *declaration* layer. Currently, which layers an entity renders is declared inside renderer constructors — client-side, loader-specific Java:

```java
// NativeRobotRenderer constructor (Forge)
addLayer(new BaseTextureLayer<>(this));
addLayer(new DynamicColorLayer<>(this, COLLAR_MASK, DynamicColorLayer::healthGradientColor));
addLayer(new HeadphoneOverlayLayer<>(this, GENERAL_LAYER_EMPTY)
    .addConditionalTexture(entity -> entity.getCurrentState() == EntityState.Defense, HEADPHONES_DEFENSE)
    .addConditionalTexture(...));
```

The same block is duplicated across `NativeRobotRenderer`, `BunnyRenderer`, `KitsuneRenderer` — Forge, NeoForge, and Fabric each. The entity type definition in Common knows nothing about the layers it renders. The two sides are disconnected and can silently diverge.

For Monster Girls, every entity family would need its own renderer subclass with its own hardcoded layer setup — belly predicates, month-checking predicates, hair choice logic — none of it co-located with the entity type it describes, all of it triplicated across loaders.

### Why hairstyles change the texture model

The current variant system treats a variant as an atomic texture swap. `mandragora_chorus` means one texture file with body, hair, and all other detail painted together. This works when appearances are fully independent.

Hairstyles break that assumption. The Chorus Mandragora and the Flower Mandragora have the same body forms with different hair. If the body and hair are one texture, you need `chorus × N_hairstyles` separate texture files. Three body variants × four hairstyles = twelve texture files. Add a new hairstyle: twelve becomes fifteen. This is texture file explosion.

The correct decomposition is:

```
mandragora_chorus (body texture, hair area transparent)
  + mandragora_hair_straight (hair texture, body area transparent)
  = final render
```

The hair is an overlay layer, not a variant dimension. The body texture does not know which hair is shown. The hair texture does not know which body is shown. They compose at render time.

This decomposition is not novel — it is how the existing headphone and collar layers work for robots. The robots have a base texture and separate overlay layers for headphones, collar, and state indicators. The same pattern applies to any texture region that should vary independently.

### Why hairstyles need persistent synced state

Conditional layers (belly, costumes) are computed every render tick from world state. They need no entity data — the condition is stateless.

Hair is different. The hairstyle is chosen *randomly at spawn*, and that choice must survive chunk reload and server restart. An entity spawned with straight hair must still have straight hair when you return tomorrow. This requires the choice to be stored in synced entity data and persisted to NBT, exactly like `TEXTURE_VARIANT`.

This is the key distinction that drives the three slot types described below.

### Gourdragora carving variants — color tint vs. face cut

Gourdragora has three variants relevant to carvings: Golden, Lumina, and Jack'o. They share the same carving *shapes* (smile, scary, star) but render them in fundamentally different ways.

**Golden and Lumina** — the carving pattern is the same geometry, different color. Authoring one full-color texture per shape × per variant would be four shapes × two variants = eight texture files. The `DynamicColorLayer` pattern already solves this: author one grayscale pattern per shape (white where the lines are, transparent everywhere else), then tint it at render time with a fixed ARGB color looked up from the entity's appearance variant. That reduces eight files to four — one per shape, regardless of color. Adding a new variant color costs zero texture files.

This requires INTERACTIVE slots to carry an optional color provider alongside the texture pool. `OverlaySlot.entry` gains an optional `Function<T, Integer> colorProvider` field (null = no tint = white). When `buildLayers()` materialises the slot, it produces a `DynamicColorLayer` instead of a plain `BaseInternalRenderLayer` whenever a color provider is present.

**Jack'o** — carvings are not recolored, they are *cut*. The carved regions show through to nothing (or an inner dark/glow texture). True GPU-level alpha subtraction from an already-rendered surface is not achievable through GeckoLib's `reRender` + `RenderType` system without custom shaders. However, the same visual result is achievable through layer composition:

1. The Jack'o base texture has the face region **permanently transparent** — the face area is simply not painted.
2. A `CONDITIONAL` slot called `"face_cover"` renders a texture that fills that transparent region, simulating an uncarved face. Its condition is `entity.getOverlaySlot("carving").isEmpty()` — it renders only when no carving is active.
3. The `INTERACTIVE` carving slot renders the carved face art over the transparent gap when active.

The result: uncarved Jack'o = base + face cover = solid pumpkin head. Carved Jack'o = base + carving pattern = face cover suppressed, carved art fills the gap. The "cut" illusion comes entirely from pre-authored transparency in the base texture combined with mutually exclusive conditional layers — no special blend mode required.

A slot's condition predicate can freely reference another slot's state via `entity.getOverlaySlot(key)` because slot state is read from `SynchedEntityData`, not from the current frame's render pipeline. There is no ordering dependency between `"face_cover"` and `"carving"` evaluation.

### Why ExchangeFeature is wrong for Gourdragora carvings

The initial implementation note suggested using `ExchangeFeature` to handle shears-based carving. On reflection, this misrepresents the mechanic.

`ExchangeFeature` is an economic interaction: items flow between parties, optional sequences, cooldowns, item consumption/production. The Mandrake stew recipe is an exchange. The Mushroom Gal giving back a material is an exchange.

Gourdragora carving is not an exchange. There is no item sequence. Nothing is given back. The shears take durability damage and the face pattern changes — exactly like shearing a sheep or carving a vanilla pumpkin. The interaction is **tool use that modifies entity appearance in-place**, cyclically.

This belongs in the entity's `handleSpecificInteractions()`, not in `ExchangeFeature`. The carving pattern state belongs in `OverlayFeature` as an INTERACTIVE slot.

---

## Decision

Implement `OverlayFeature` as a first-class feature in HZLib's entity feature system, at the same level as `EmanationFeature`, `ExchangeFeature`, and `SoundFeature`.

`OverlayFeature` lives at `net.heriazone.hzlib.api.entity.features.overlay` and is available to any entity in any HZLib-based mod.

The feature serves two responsibilities simultaneously:

1. **Declaration** — declares which overlay slots an entity type has, how each slot selects its texture, and in what order layers render
2. **Persistence contract** — exposes which slots require persistent synced entity data, so the entity base class knows what to store in `SynchedEntityData` and NBT

---

## Design

### The fundamental model

An overlay slot has three parts:

```
slot key    (identity — "hair", "belly", "costume", "carving", "emissive")
  + mode      (how the active texture is selected)
  = texture   (zero or one texture path active at any moment)
```

Multiple slots are declared on one `OverlayFeature`. Slots render in declaration order. A slot that selects no texture for the current entity state contributes nothing — no render pass.

### The three slot modes

| Mode | Selected when | Persistent | Examples |
|---|---|---|---|
| `RANDOM` | Once at `finalizeSpawn` | **Yes** — synced string | Mandragora hair |
| `CONDITIONAL` | Every render tick, first match wins | No — computed live | Belly, seasonal costumes, Jack'o face cover |
| `INTERACTIVE` | Explicit player action, cycled | **Yes** — synced string | Gourdragora carving |
| `ALWAYS` | Unconditionally | No — no state needed | Emissive glow, always-on detail |

`RANDOM` and `INTERACTIVE` require persistent state. `CONDITIONAL` and `ALWAYS` do not. The feature exposes `getPersistentSlots()` returning only the slots that need synced data — the entity base class iterates this to register `SynchedEntityData` accessors and include them in NBT save/load.

`OverlaySlot.entry` carries an optional `Function<T, Integer> colorProvider`. When non-null, `buildLayers()` materialises the slot as a `DynamicColorLayer` (grayscale mask + runtime tint) instead of a plain texture layer. When null, a plain `BaseInternalRenderLayer` is used. This covers Golden/Lumina carving patterns: one grayscale texture per shape, tinted per variant at render time.

### Slot declaration API

```java
.withFeature(OverlayFeature.class, new OverlayFeature()
    // ALWAYS — unconditional emissive glow
    .addSlot("emissive", OverlaySlot.always("{mod}:textures/entity/mushroom/mushroom_glow.png"))

    // CONDITIONAL — belly overlay, first match wins
    .addSlot("belly", OverlaySlot.conditional(
        OverlaySlot.entry("{mod}:textures/entity/mushroom/mushroom_belly_chonky.png",
                          e -> bellyLevel(e) >= CHONKY && configAllows(CHONKY)),
        OverlaySlot.entry("{mod}:textures/entity/mushroom/mushroom_belly_tummy.png",
                          e -> bellyLevel(e) >= TUMMY  && configAllows(TUMMY))))

    // CONDITIONAL — seasonal costume, first match wins
    .addSlot("costume", OverlaySlot.conditional(
        OverlaySlot.entry("{mod}:textures/entity/mushroom/mushroom_costume_halloween.png",
                          RenderConditions.inMonth(Month.OCTOBER)),
        OverlaySlot.entry("{mod}:textures/entity/mushroom/mushroom_costume_christmas.png",
                          RenderConditions.inMonth(Month.DECEMBER))))

    // RANDOM — hair chosen once at spawn, persistent
    .addSlot("hair", OverlaySlot.random(
        "{mod}:textures/entity/mandragora/mandragora_hair_straight.png",
        "{mod}:textures/entity/mandragora/mandragora_hair_curly.png",
        "{mod}:textures/entity/mandragora/mandragora_hair_short.png",
        "{mod}:textures/entity/mandragora/mandragora_hair_long.png"))

    // INTERACTIVE — carving cycled by player with shears (Golden/Lumina: grayscale + tint)
    .addSlot("carving", OverlaySlot.interactive(
        OverlaySlot.entry(""),                                     // index 0: uncarved
        OverlaySlot.entry("gourdragora_carving_smile_mask.png",
                          e -> GourdragoraType.getCarvingColor(e)),
        OverlaySlot.entry("gourdragora_carving_scary_mask.png",
                          e -> GourdragoraType.getCarvingColor(e)),
        OverlaySlot.entry("gourdragora_carving_star_mask.png",
                          e -> GourdragoraType.getCarvingColor(e))))

    // Jack'o variant — face region is transparent on base texture.
    // face_cover fills it when uncarved; carving fills it when carved.
    // Both slots reference carving state — no ordering dependency (state is in SynchedEntityData).
    .addSlot("face_cover", OverlaySlot.conditional(
        OverlaySlot.entry("jacko_face_cover.png",
                          entity -> entity.getOverlaySlot("carving").isEmpty())))
    .addSlot("carving", OverlaySlot.interactive(
        OverlaySlot.entry(""),                                     // index 0: uncarved
        OverlaySlot.entry("jacko_carving_smile.png"),
        OverlaySlot.entry("jacko_carving_scary.png"),
        OverlaySlot.entry("jacko_carving_star.png")))
    .addSlot("carving_glow", OverlaySlot.conditional(
        OverlaySlot.entry("jacko_carving_glow.png",
                          entity -> !entity.getOverlaySlot("carving").isEmpty()))))
```

### Renderer integration — buildLayers()

The renderer no longer hardcodes layer setup. It asks the entity type:

```java
// In MonsterRenderer constructor (any loader)
addLayer(new BaseTextureLayer<>(this));

entity.nativeEntity.getFeature(OverlayFeature.class)
    .ifPresent(f -> f.buildLayers(this).forEach(this::addLayer));
```

`buildLayers(renderer)` materialises `IInternalRenderLayer<T>` instances from the slot declarations, in declaration order. Each slot mode maps to an existing layer class:

| Slot mode | Color provider? | Materialised as |
|---|---|---|
| `ALWAYS` | — | `BaseInternalRenderLayer` with `renderCondition = entity -> true` |
| `CONDITIONAL` | — | `ConditionalOverlayLayer` with the declared predicate entries |
| `RANDOM` | No | `BaseInternalRenderLayer` reading `entity.getOverlaySlot(key)` |
| `RANDOM` | Yes | `DynamicColorLayer` reading `entity.getOverlaySlot(key)` + color provider |
| `INTERACTIVE` | No | `BaseInternalRenderLayer` reading `entity.getOverlaySlot(key)` |
| `INTERACTIVE` | Yes | `DynamicColorLayer` reading `entity.getOverlaySlot(key)` + color provider |

The existing layer classes are unchanged. `OverlayFeature` is a declaration layer that drives them — not a replacement.

### Entity data — persistent slots

For `RANDOM` and `INTERACTIVE` slots, the entity needs synced string fields storing the currently active texture path (empty string = no overlay). Rather than adding a fixed number of `SynchedEntityData` accessors to `InternalEntity`, the entity base reads the feature at construction time:

```java
// In MonsterEntity (or InternalEntity if robots adopt OverlayFeature later):
protected void registerOverlayData() {
    if (nativeEntity == null) return;
    nativeEntity.getFeature(OverlayFeature.class)
        .ifPresent(f -> f.getPersistentSlots().forEach(this::registerOverlaySlot));
}
```

Each persistent slot key maps to a `SynchedEntityData<String>` accessor stored in a `Map<String, EntityDataAccessor<String>>` on the entity. Accessors are looked up by slot key at read/write time.

Public entity API:

```java
// Read (called by renderer to resolve texture path)
public String getOverlaySlot(String slotKey)

// Write (called at spawn for RANDOM, called by interaction handler for INTERACTIVE)
public void setOverlaySlot(String slotKey, String texturePath)
```

NBT:

```java
// In addAdditionalSaveData:
CompoundTag overlayTag = new CompoundTag();
persistentSlotKeys.forEach(key -> overlayTag.putString(key, getOverlaySlot(key)));
nbt.put("OverlaySlots", overlayTag);

// In readAdditionalSaveData:
if (nbt.contains("OverlaySlots")) {
    CompoundTag overlayTag = nbt.getCompound("OverlaySlots");
    overlayTag.getAllKeys().forEach(key -> setOverlaySlot(key, overlayTag.getString(key)));
}
```

### RANDOM slot — spawn initialisation

`initializeSpawnVariants` in `InternalEntity` already handles `TextureVariantFeature`, `ModelVariantFeature`, and `AnimatorVariantFeature`. The same hook initialises RANDOM overlay slots:

```java
// In initializeSpawnVariants (MonsterEntity or InternalEntity):
nativeEntity.getFeature(OverlayFeature.class).ifPresent(f ->
    f.getSlots().stream()
        .filter(s -> s.getMode() == SlotMode.RANDOM)
        .forEach(s -> setOverlaySlot(s.getKey(), s.pickRandom())));
```

Each RANDOM slot picks uniformly from its pool. The chosen path is stored via `setOverlaySlot`, synced to client, and persisted to NBT.

### INTERACTIVE slot — Gourdragora shears

Carving is handled in `GourdragoraEntity.handleSpecificInteractions()`:

```java
if (stack.getItem() instanceof ShearsItem) {
    if (!level().isClientSide) {
        nativeEntity.getFeature(OverlayFeature.class).ifPresent(f -> {
            OverlaySlot carving = f.getSlot("carving");
            if (carving != null) {
                String next = carving.cycleNext(getOverlaySlot("carving"));
                setOverlaySlot("carving", next);
                playSound(MonstersSounds.GOURDRAGORA_CARVE, 1.0f, 1.0f);
            }
        });
        stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
    }
    return InteractionResult.sidedSuccess(level().isClientSide);
}
```

`cycleNext(currentPath)` returns the next path in the pool, wrapping at the end. When the pool's first entry is an empty string (uncarved), the cycle starts uncarved → smile → scary → star → uncarved. The empty string is a valid state meaning "show no overlay" — it does not render.

### CONDITIONAL slot — belly and costumes

Belly and costume predicates need access to entity state (belly level, current real-world month). Month checking is a new static utility:

```java
// In RenderConditions (new class) or ExchangeConditions (existing):
public static <T extends InternalEntity> Predicate<T> inMonth(java.time.Month... months) {
    Set<Month> set = Set.of(months);
    return entity -> set.contains(LocalDate.now().getMonth());
}
```

This is placed in a new `RenderConditions` class in `hzlib/api/rendering/` rather than `ExchangeConditions`. `ExchangeConditions` is specifically for the exchange system's condition API — predicates that test whether an exchange should fire. `RenderConditions` is for predicates that test whether a layer should render. The two contexts differ in what they guard even if the predicate shape is similar.

The belly predicate reads from the monster mod's config. Config reads inside predicates are fine — the predicate is evaluated every render tick on the client, where the config is always loaded and the read is O(1).

### Texture file naming — hairstyle decomposition and face-cover convention

For entities with RANDOM hair slots, the base variant texture has the hair region transparent. Hair texture files have the body region transparent. This is a pure texture authoring convention — nothing in code enforces it, but nothing in code can violate it either. The layers compose correctly as long as the textures are authored correctly.

For entities with a `"face_cover"` + INTERACTIVE carving pattern (Jack'o), the base texture has the face region permanently transparent. The face cover texture fills that region. Both the face cover and the carving patterns must be authored to precisely match that transparent region — they are covering the same pixel area with mutually exclusive conditions.

Naming convention:

```
Mandragora — base textures (hair area transparent):
  entity/mandragora/mandragora_chorus.png
  entity/mandragora/mandragora_flower.png
  entity/mandragora/mandragora_fructus.png

Mandragora — hair overlay textures (body area transparent):
  entity/mandragora/mandragora_hair_straight.png
  entity/mandragora/mandragora_hair_curly.png
  entity/mandragora/mandragora_hair_short.png
  entity/mandragora/mandragora_hair_long.png

Gourdragora Golden/Lumina — grayscale carving masks (tinted at render time):
  entity/gourdragora/gourdragora_carving_smile_mask.png
  entity/gourdragora/gourdragora_carving_scary_mask.png
  entity/gourdragora/gourdragora_carving_star_mask.png

Gourdragora Jack'o — base texture (face area transparent) + cover + full-color carvings:
  entity/gourdragora/jacko_base.png              (face region transparent)
  entity/gourdragora/jacko_face_cover.png        (fills transparent region when uncarved)
  entity/gourdragora/jacko_carving_smile.png
  entity/gourdragora/jacko_carving_scary.png
  entity/gourdragora/jacko_carving_star.png
  entity/gourdragora/jacko_carving_glow.png      (optional inner glow, emissive)
```

Texture count summary:
- Mandragora: 3 body + 4 hair = **7 files** (vs. 12 if combined)
- Gourdragora Golden + Lumina: 3 grayscale masks = **3 files** (vs. 6 if per-variant colored)
- Gourdragora Jack'o: 1 base + 1 cover + 3 carvings + 1 glow = **6 files** (self-contained)

### Robots — migration path

The existing `NativeRobotRenderer` / `BunnyRenderer` / `KitsuneRenderer` constructors currently hardcode their layer setup with `addLayer(new DynamicColorLayer(...))` and `addLayer(new HeadphoneOverlayLayer(...))`. These are not migrated in this ADR.

The migration is straightforward when desired: move the robot type's collar and headphone layer declarations into an `OverlayFeature` on the robot's `NativeEntityType`, and replace the constructor's hardcoded `addLayer` calls with `f.buildLayers(this).forEach(this::addLayer)`. The per-entity type differences between Bunny, Kitsune, and generic robots (different collar textures, different headphone overlay textures) become slots on each entity type rather than separate renderer subclasses.

This consolidation would collapse the three renderer subclasses into one. It is left as a follow-up, not a prerequisite.

---

## File Structure

### New files

```
HZLib Common:
  net.heriazone.hzlib.api.entity.features.overlay/
    OverlayFeature.java     — holds slots, buildLayers(), getPersistentSlots()
    OverlaySlot.java        — slot declaration: mode + pool + cycleNext() + pickRandom()
    SlotMode.java           — enum: ALWAYS, CONDITIONAL, RANDOM, INTERACTIVE

  net.heriazone.hzlib.api.rendering/
    RenderConditions.java   — static factory: inMonth(), isDaytime() (if not moved from ExchangeConditions)
```

### Modified files

```
HZLib Common:
  InternalEntity.java      — getOverlaySlot(), setOverlaySlot(), registerOverlayData()
                             initializeSpawnVariants() extended for RANDOM slots
                             addAdditionalSaveData / readAdditionalSaveData extended

Monsters & Girls Common:
  MonsterEntity.java       — calls registerOverlayData() in constructor
  GourdragoraEntity.java   — handleSpecificInteractions() shears branch
  MushroomType.java        — OverlayFeature with belly + costume slots
  MandragoraType.java      — OverlayFeature with hair slot (base textures updated to have transparent hair region)
  GourdragoraType.java     — OverlayFeature with carving slots (Golden/Lumina: tinted mask;
                             Jack'o: face_cover + carving + carving_glow)

Monsters & Girls Forge / Fabric / NeoForge:
  MonsterRenderer.java     — addLayer replaced with f.buildLayers(this).forEach(this::addLayer)
```

---

## Implementation Order

```
1. Add OverlaySlot, SlotMode (data classes — no dependencies)
2. Add OverlayFeature with buildLayers(), getPersistentSlots()
3. Add RenderConditions with inMonth()
4. Extend InternalEntity: getOverlaySlot / setOverlaySlot / registerOverlayData / NBT
5. Extend initializeSpawnVariants for RANDOM slots
6. Wire MonsterEntity: call registerOverlayData() in constructor
7. Wire MonsterRenderer: replace hardcoded addLayer calls with buildLayers()
8. Declare OverlayFeature on MushroomType (belly + costume slots)
9. Decompose Mandragora textures (separate body + hair), declare OverlayFeature on MandragoraType
10. Declare OverlayFeature on GourdragoraType (carving slot)
11. Add shears branch in GourdragoraEntity.handleSpecificInteractions()
12. Migrate robot renderers (optional follow-up)
```

---

## Consequences

### Positive

- Entity type declarations in Common are the single source of truth for what layers an entity renders — the renderer just executes them
- Adding a new overlay (belly, hair, costume, carving) to an entity touches one file: the entity type definition. No renderer changes needed
- Texture count scales linearly with independent dimensions rather than multiplicatively — four hairstyles × three body variants = seven files, not twelve
- Gourdragora carving shapes are shared across color variants via grayscale mask + runtime tint — N carving shapes × M color variants costs N texture files, not N×M. Jack'o's face-cover inversion adds one extra texture (the cover) regardless of how many carving shapes exist
- `RANDOM`, `CONDITIONAL`, `INTERACTIVE`, and `ALWAYS` are explicit modes — the intent of each slot is clear from its declaration
- The existing layer classes (`ConditionalOverlayLayer`, `EmissiveLayer`, `DynamicColorLayer`, `BaseInternalRenderLayer`) are unchanged — `OverlayFeature` is a declaration layer above them, not a replacement
- Robot renderers can be migrated to the same pattern, collapsing three renderer subclasses into one (planned follow-up)

### Negative

- `InternalEntity` gains a `Map<String, EntityDataAccessor<String>>` for persistent overlay slots. This is dynamic rather than the fixed static `EntityDataAccessor` fields used elsewhere in the entity — it requires slightly more defensive null handling and cannot be fully type-checked at compile time
- `buildLayers(renderer)` is called in the renderer constructor. If `nativeEntity` is not set at that point (unlikely but possible depending on subclass construction order), the build produces no layers silently. This must be documented

### Risks

- **Texture authoring convention not enforced**: Hair-area transparency, face-region transparency (Jack'o base), and face-cover pixel alignment are pure conventions. Misaligned textures show as visual gaps or overlaps at runtime with no error. Texture authoring guidelines should be documented alongside entity type declarations
- **Persistent slot key typo**: If `getOverlaySlot("harir")` is called instead of `"hair"`, the map returns null and the renderer shows no hair, with no exception. This is mitigated by declaring slot keys as constants on the entity type (e.g., `MandragoraType.SLOT_HAIR = "hair"`) rather than using raw strings at call sites
- **RANDOM slot on non-spawning entities** (loaded from NBT): If an entity is loaded from NBT before `OverlayFeature` was added (old save), its overlay slot keys are absent from the `"OverlaySlots"` compound. `readAdditionalSaveData` falls back gracefully to the feature's default (first RANDOM pool entry, or empty string for INTERACTIVE/CONDITIONAL). This is safe but means old-save entities may have unexpected default hair until their data is re-initialised

---

## Related Decisions

- ADR_011: Variant and Spawn System Refactoring — established `TEXTURE_VARIANT` / `MODEL_VARIANT` synced fields and `initializeSpawnVariants`; RANDOM overlay slots follow the same spawn-time initialisation pattern
- ADR_012: InternalEntity Consolidation — established `InternalEntity` as the shared base and the `withFeature()` / `getFeature()` pattern; `OverlayFeature` integrates via this system
- ADR_013: ExchangeFeature — the companion interaction system; Gourdragora shears is explicitly **not** an exchange
- ADR_015: EmanationFeature — parallel design: feature declared on type, behaviour declared as data; `OverlayFeature` follows the same composition model
- ADR_016: Sound and Animation Lifecycle System — established the pattern of features driving runtime behaviour from entity type declarations; `OverlayFeature` extends this to the rendering domain
