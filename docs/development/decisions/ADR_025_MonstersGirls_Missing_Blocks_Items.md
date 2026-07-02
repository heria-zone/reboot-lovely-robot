# ADR-025: Monsters & Girls — Missing Blocks, Wood Sets, Hat Items, and Empty Jar

**Status:** Accepted
**Date:** 2026-07-02
**Author:** Serge Maia
**Applies to:** `monsters_girls-1.21.1` (Common + loaders)
**Reference source:**
- `archive/outsource/monsters-girls-1.20.1/` — open-source 1.20.1 Fabric implementation
- `archive/outsource/source/MonstersGirls_1.3.4.3_1.20.1/` — compiled 1.3.4.3 reference
- `docs/development/notes/Catalogue_Missing_MonstersGirls.md` — full missing item list
**Dependency:** HZLib — used as the shared library for all block/item patterns in 1.21.1

---

## 1. Context

The 1.21.1 rewrite of Monsters & Girls introduced a clean `MonstersFamily<T>` architecture with
HZLib feature composition. However, only the entity families, five custom plant blocks, their potted
variants, and a handful of food/stew items were ported. A large portion of the 1.20.1 content is
missing, specifically:

- **Three fungal wood sets** (Ender Puffball, Molten Fungus, Soul Wanderer) — full vanilla-parity
  building blocks per family
- **Three shroomlights** (one per family, plus slab variants)
- **Three family cap/mushroom blocks** (giant decorative blocks)
- **Inkcap mushroom block variants** (three colour variants)
- **Twelve hat items** (one `ArmorItem` per mushroom/fungus family, head slot only)
- **Ender Moss** (decorative End ground cover block)
- **Glow Berry Bush** (custom plant block)
- **Jar block** (empty — the displayable decorative glass container)
- **Two urn blocks** (Molten and Crimson urns)

This ADR specifies **exactly** how to implement each category in the 1.21.1 codebase, following
the existing patterns established by `MonstersBlocks.java` and the HZLib dependency.

---

## 2. Architecture Context — How 1.21.1 Registers Blocks and Items

Before specifying changes, here is the established 1.21.1 registration pattern.

**`MonstersBlocks.java`** — `Common`, uses `BuiltInRegistries.BLOCK` directly (no loader API):
```java
INKCAP_MUSHROOM = Registry.register(BuiltInRegistries.BLOCK,
    MonstersIdentifier.getId(MonstersConstant.INKCAP_MUSHROOM),
    new MushroomPlantBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BROWN_MUSHROOM)));
```

**`MonstersItems.java`** — does not yet exist in 1.21.1. Must be created, mirroring the
block registration pattern using `BuiltInRegistries.ITEM` with `BlockItem` for block items.

**`MonstersConstant.java`** — holds all string registry keys as `public static final String`.
Every new block and item needs a constant here before anything else.

**`Monsters.init()`** — calls `MonstersBlocks.register()` then (after this ADR)
`MonstersItems.register()`. Order matters: blocks must be registered before their block items.

**Render layers** — The 1.20.1 reference used `BlockRenderLayerMap` (Fabric). In 1.21.1 the
equivalent is registering CUTOUT render layer on the client side. Doors, trapdoors, glass blocks,
plant blocks, and directional decorative blocks all need `CUTOUT`. This is handled in
`Monsters.initClient()` or a dedicated `MonstersBlocksClient.java`.

**`DirectionalBlock`** — the 1.20.1 custom block class used for huge mushrooms, jars, and urns.
It extends vanilla `Block` with a facing `BlockState` property and a custom `VoxelShape` per
direction. This class must be ported to 1.21.1 and placed in `block/` package. The `VoxelCollision`
constants (shape definitions) must also be ported.

---

## 3. New String Constants — `MonstersConstant.java`

Add the following to `MonstersConstant.java` in the `-- BLOCKS --` and `-- ITEMS --` sections.
Group by category, matching the existing style.

### 3.1 Wood set keys (3 families × 13 blocks each = 39)

```java
// -- ENDER PUFFBALL WOOD SET --
public static final String ENDER_PUFFBALL_BLOCK            = "ender_puffball_block";
public static final String ENDER_PUFFBALL_STEM             = "ender_puffball_stem";
public static final String ENDER_PUFFBALL_HYPHAE           = "ender_puffball_hyphae";
public static final String ENDER_PUFFBALL_STRIPPED_STEM    = "ender_puffball_stripped_stem";
public static final String ENDER_PUFFBALL_STRIPPED_HYPHAE  = "ender_puffball_stripped_hyphae";
public static final String ENDER_PUFFBALL_PLANKS           = "ender_puffball_planks";
public static final String ENDER_PUFFBALL_STAIRS           = "ender_puffball_stairs";
public static final String ENDER_PUFFBALL_SLAB             = "ender_puffball_slab";
public static final String ENDER_PUFFBALL_FENCE            = "ender_puffball_fence";
public static final String ENDER_PUFFBALL_FENCE_GATE       = "ender_puffball_fence_gate";
public static final String ENDER_PUFFBALL_DOOR             = "ender_puffball_door";
public static final String ENDER_PUFFBALL_TRAPDOOR         = "ender_puffball_trapdoor";
public static final String ENDER_PUFFBALL_PRESSURE_PLATE   = "ender_puffball_pressure_plate";
public static final String ENDER_PUFFBALL_BUTTON           = "ender_puffball_button";
public static final String SHROOMLIGHT_ENDER               = "shroomlight_ender";

// -- MOLTEN FUNGUS WOOD SET --
public static final String MOLTEN_FUNGUS_BLOCK             = "molten_fungus_block";
public static final String MOLTEN_FUNGUS_STEM              = "molten_fungus_stem";
public static final String MOLTEN_FUNGUS_HYPHAE            = "molten_fungus_hyphae";
public static final String MOLTEN_FUNGUS_STRIPPED_STEM     = "molten_fungus_stripped_stem";
public static final String MOLTEN_FUNGUS_STRIPPED_HYPHAE   = "molten_fungus_stripped_hyphae";
public static final String MOLTEN_FUNGUS_PLANKS            = "molten_fungus_planks";
public static final String MOLTEN_FUNGUS_STAIRS            = "molten_fungus_stairs";
public static final String MOLTEN_FUNGUS_SLAB              = "molten_fungus_slab";
public static final String MOLTEN_FUNGUS_FENCE             = "molten_fungus_fence";
public static final String MOLTEN_FUNGUS_FENCE_GATE        = "molten_fungus_fence_gate";
public static final String MOLTEN_FUNGUS_DOOR              = "molten_fungus_door";
public static final String MOLTEN_FUNGUS_TRAPDOOR          = "molten_fungus_trapdoor";
public static final String MOLTEN_FUNGUS_PRESSURE_PLATE    = "molten_fungus_pressure_plate";
public static final String MOLTEN_FUNGUS_BUTTON            = "molten_fungus_button";
public static final String SHROOMLIGHT_MOLTEN              = "shroomlight_molten";

// -- SOUL WANDERER WOOD SET --
public static final String SOUL_WANDERER_BLOCK             = "soul_wanderer_block";
public static final String SOUL_WANDERER_STEM              = "soul_wanderer_stem";
public static final String SOUL_WANDERER_HYPHAE            = "soul_wanderer_hyphae";
public static final String SOUL_WANDERER_STRIPPED_STEM     = "soul_wanderer_stripped_stem";
public static final String SOUL_WANDERER_STRIPPED_HYPHAE   = "soul_wanderer_stripped_hyphae";
public static final String SOUL_WANDERER_PLANKS            = "soul_wanderer_planks";
public static final String SOUL_WANDERER_STAIRS            = "soul_wanderer_stairs";
public static final String SOUL_WANDERER_SLAB              = "soul_wanderer_slab";
public static final String SOUL_WANDERER_FENCE             = "soul_wanderer_fence";
public static final String SOUL_WANDERER_FENCE_GATE        = "soul_wanderer_fence_gate";
public static final String SOUL_WANDERER_DOOR              = "soul_wanderer_door";
public static final String SOUL_WANDERER_TRAPDOOR          = "soul_wanderer_trapdoor";
public static final String SOUL_WANDERER_PRESSURE_PLATE    = "soul_wanderer_pressure_plate";
public static final String SOUL_WANDERER_BUTTON            = "soul_wanderer_button";
public static final String SHROOMLIGHT_SOUL                = "shroomlight_soul";
```

### 3.2 Inkcap mushroom block variants (3)

```java
public static final String INK_CAP_BLACK_MUSHROOM_BLOCK       = "ink_cap_black_mushroom_block";
public static final String INK_CAP_GREY_MUSHROOM_BLOCK        = "ink_cap_grey_mushroom_block";
public static final String INK_CAP_LIGHT_GREY_MUSHROOM_BLOCK  = "ink_cap_light_grey_mushroom_block";
```

### 3.3 Huge decorative mushroom/fungus blocks (12)

```java
public static final String HUGE_BROWN_MUSHROOM      = "huge_brown_mushroom";
public static final String HUGE_CRIMSON_FUNGUS      = "huge_crimson_fungus";
public static final String HUGE_CRIMSON_RARE_FUNGUS = "huge_crimson_rare_fungus";
public static final String HUGE_ENDER_PUFFBALL      = "huge_ender_puffball";
public static final String HUGE_FLY_RED_AGARIC      = "huge_fly_red_agaric";
public static final String HUGE_FLY_YELLOW_AGARIC   = "huge_fly_yellow_agaric";
public static final String HUGE_INFERNAL_MUSHROOM   = "huge_infernal_mushroom";
public static final String HUGE_INK_CAP_MUSHROOM    = "huge_ink_cap_mushroom";
public static final String HUGE_MOLTEN_FUNGUS       = "huge_molten_fungus";
public static final String HUGE_SOUL_WANDERER       = "huge_soul_wanderer";
public static final String HUGE_WARPED_FUNGUS       = "huge_warped_fungus";
public static final String HUGE_WARPED_RARE_FUNGUS  = "huge_warped_rare_fungus";
```

### 3.4 Decoration blocks (4)

```java
public static final String ENDER_MOSS       = "ender_moss";
public static final String GLOW_BERRY_BUSH  = "glow_berry_bush";
public static final String JAR              = "jar";
public static final String URN_MOLTEN       = "urn_molten";
public static final String URN_CRIMSON      = "urn_crimson";
```

### 3.5 Hat item keys (12)

```java
// -- HATS --
public static final String HAT_MUSHROOM_BROWN        = "hat_mushroom_brown";
public static final String HAT_MUSHROOM_CRIMSON      = "hat_mushroom_crimson";
public static final String HAT_MUSHROOM_CRIMSON_RARE = "hat_mushroom_crimson_rare";
public static final String HAT_MUSHROOM_ENDER_PUFFBALL = "hat_mushroom_ender_puffball";
public static final String HAT_MUSHROOM_FLY_RED_AGARIC    = "hat_mushroom_fly_red_agaric";
public static final String HAT_MUSHROOM_FLY_YELLOW_AGARIC = "hat_mushroom_fly_yellow_agaric";
public static final String HAT_MUSHROOM_INFERNAL     = "hat_mushroom_infernal";
public static final String HAT_MUSHROOM_INK_CAP      = "hat_mushroom_ink_cap";
public static final String HAT_MUSHROOM_MOLTEN       = "hat_mushroom_molten";
public static final String HAT_MUSHROOM_SOUL_WANDERER = "hat_mushroom_soul_wanderer";
public static final String HAT_MUSHROOM_WARPED       = "hat_mushroom_warped";
public static final String HAT_MUSHROOM_WARPED_RARE  = "hat_mushroom_warped_rare";
```

---

## 4. New Block Classes Required

Two custom block classes must be ported from the 1.20.1 source to the 1.21.1 `block/` package.

### 4.1 `DirectionalBlock.java`

**1.20.1 location:** `block/custom/DirectionalBlock.java`
**1.21.1 target:** `net.heriazone.monsters_girls.block.DirectionalBlock`

This block holds a facing `BlockState` property and delegates its `VoxelShape` to a shape map
keyed by direction. Used by huge mushroom blocks, jar, and urns.

**1.21.1 adaptation notes:**
- Replace Fabric `FabricBlockSettings` with vanilla `BlockBehaviour.Properties`
- The `MapCodec` abstract method is required in 1.21.1 — use `Block.simpleCodec(DirectionalBlock::new)`,
  matching the pattern already established in `MushroomPlantBlock`
- `BlockState` facing property is `net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING`
- Shape map type: `Map<Direction, VoxelShape>` passed into the constructor

```java
package net.heriazone.monsters_girls.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

/**
 * A decorative block whose VoxelShape and facing direction are rotation-aware.
 * <p>
 * <b>Usage:</b> Huge mushroom blocks, jar, urn blocks — all directional
 * decorative shapes that face toward the player when placed.
 */
public class DirectionalBlock extends Block {

    public static final MapCodec<DirectionalBlock> CODEC = simpleCodec(DirectionalBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private final Map<Direction, VoxelShape> shapes;

    public DirectionalBlock(BlockBehaviour.Properties properties, Map<Direction, VoxelShape> shapes) {
        super(properties);
        this.shapes = shapes;
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH));
    }

    public DirectionalBlock(BlockBehaviour.Properties properties) {
        this(properties, Map.of()); // shape-less variant for codec
    }

    @Override public MapCodec<? extends Block> codec() { return CODEC; }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level,
                               BlockPos pos, CollisionContext context) {
        Direction dir = state.getValue(FACING);
        return shapes.getOrDefault(dir, super.getShape(state, level, pos, context));
    }

} // Class: DirectionalBlock
```

### 4.2 `BlockShapes.java` (replaces `VoxelCollision`)

**1.20.1 location:** `block/internal/VoxelCollision.java`
**1.21.1 target:** `net.heriazone.monsters_girls.block.BlockShapes`

Port the six shape groups (`HUGE_MUSHROOM`, `HUGE_FUNGUS`, `HUGE_FUNGUS_VARIANT`,
`MEDIUM_MUSHROOM`, `SMALL_MUSHROOM`, `JAR`, `BIG_URN`, `SMALL_URN`) as
`Map<Direction, VoxelShape>` constants. In 1.21.1 `VoxelShape` is in
`net.minecraft.world.phys.shapes.Shapes`.

### 4.3 `GlowBerryBushBlock.java`

**1.20.1 location:** `block/custom/GlowBerryBush.java`
**1.21.1 target:** `net.heriazone.monsters_girls.block.GlowBerryBushBlock`

Extends vanilla `BushBlock`. In 1.20.1 it only overrides `onEntityCollision` to apply
a regeneration effect. In 1.21.1:
- Must implement `codec()` — use `Block.simpleCodec(GlowBerryBushBlock::new)`
- Replace `onEntityCollision` with `entityInside`
- No tick logic — purely static collision-effect block

---

## 5. `MonstersBlocks.java` — New Block Registrations

All additions go into the existing `register()` method in `MonstersBlocks.java`, after the
current potted block registrations. Use the same `registerBlock()` private helper already in
the file.

### 5.1 MapColor constants (add at top of class)

```java
// Thematic map colours — match 1.20.1 reference
private static final MapColor ENDER_COLOR  = MapColor.COLOR_PURPLE;   // pale purple
private static final MapColor MOLTEN_COLOR = MapColor.COLOR_RED;       // dark crimson
private static final MapColor SOUL_COLOR   = MapColor.COLOR_CYAN;      // dark aqua
private static final int      LUMINANCE    = 5;
```

### 5.2 Ender Puffball wood set (15 blocks)

```java
// -- Ender Puffball --
ENDER_PUFFBALL_BLOCK = registerBlock(MonstersConstant.ENDER_PUFFBALL_BLOCK,
    new MushroomBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BROWN_MUSHROOM_BLOCK)
        .mapColor(ENDER_COLOR)));

ENDER_PUFFBALL_STEM = registerBlock(MonstersConstant.ENDER_PUFFBALL_STEM,
    new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_STEM)
        .mapColor(ENDER_COLOR)));

ENDER_PUFFBALL_HYPHAE = registerBlock(MonstersConstant.ENDER_PUFFBALL_HYPHAE,
    new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_HYPHAE)
        .mapColor(ENDER_COLOR)));

ENDER_PUFFBALL_STRIPPED_STEM = registerBlock(MonstersConstant.ENDER_PUFFBALL_STRIPPED_STEM,
    new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_CRIMSON_STEM)
        .mapColor(ENDER_COLOR)));

ENDER_PUFFBALL_STRIPPED_HYPHAE = registerBlock(MonstersConstant.ENDER_PUFFBALL_STRIPPED_HYPHAE,
    new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_CRIMSON_HYPHAE)
        .mapColor(ENDER_COLOR)));

ENDER_PUFFBALL_PLANKS = registerBlock(MonstersConstant.ENDER_PUFFBALL_PLANKS,
    new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
        .mapColor(ENDER_COLOR)));

ENDER_PUFFBALL_STAIRS = registerBlock(MonstersConstant.ENDER_PUFFBALL_STAIRS,
    new StairBlock(ENDER_PUFFBALL_PLANKS.defaultBlockState(),
        BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS).mapColor(ENDER_COLOR)));

ENDER_PUFFBALL_SLAB = registerBlock(MonstersConstant.ENDER_PUFFBALL_SLAB,
    new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB).mapColor(ENDER_COLOR)));

ENDER_PUFFBALL_FENCE = registerBlock(MonstersConstant.ENDER_PUFFBALL_FENCE,
    new FenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE).mapColor(ENDER_COLOR)));

ENDER_PUFFBALL_FENCE_GATE = registerBlock(MonstersConstant.ENDER_PUFFBALL_FENCE_GATE,
    new FenceGateBlock(WoodType.OAK,
        BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE).mapColor(ENDER_COLOR)));

ENDER_PUFFBALL_PRESSURE_PLATE = registerBlock(MonstersConstant.ENDER_PUFFBALL_PRESSURE_PLATE,
    new PressurePlateBlock(BlockSetType.OAK,
        BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE).mapColor(ENDER_COLOR)));

ENDER_PUFFBALL_BUTTON = registerBlock(MonstersConstant.ENDER_PUFFBALL_BUTTON,
    new ButtonBlock(BlockSetType.OAK, 30,
        BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON)));

ENDER_PUFFBALL_DOOR = registerBlock(MonstersConstant.ENDER_PUFFBALL_DOOR,
    new DoorBlock(BlockSetType.OAK,
        BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR).mapColor(ENDER_COLOR)));

ENDER_PUFFBALL_TRAPDOOR = registerBlock(MonstersConstant.ENDER_PUFFBALL_TRAPDOOR,
    new TrapDoorBlock(BlockSetType.OAK,
        BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR).mapColor(ENDER_COLOR)));

SHROOMLIGHT_ENDER = registerBlock(MonstersConstant.SHROOMLIGHT_ENDER,
    new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SHROOMLIGHT).mapColor(ENDER_COLOR)));
```

> **Note on 1.21.1 API:** `StairBlock` takes `(BlockState baseState, BlockBehaviour.Properties)`.
> `FenceGateBlock` takes `(WoodType, BlockBehaviour.Properties)`. `DoorBlock` and `TrapDoorBlock`
> take `(BlockSetType, BlockBehaviour.Properties)`. `PressurePlateBlock` takes
> `(BlockSetType, BlockBehaviour.Properties)`. `ButtonBlock` takes `(BlockSetType, int ticks, BlockBehaviour.Properties)`.
> These constructors changed between 1.20.1 and 1.21.1 — use the vanilla source as reference.

### 5.3 Molten Fungus wood set (15 blocks)

Identical pattern to Ender Puffball. Replace `ENDER_COLOR` with `MOLTEN_COLOR`.
Soul Wanderer emissive blocks need `.lightLevel(state -> LUMINANCE)` on planks, stem, hyphae,
and all derivative blocks. Copy Ender Puffball set exactly, substituting:
- `ENDER_PUFFBALL_` → `MOLTEN_FUNGUS_`
- `MonstersConstant.ENDER_PUFFBALL_*` → `MonstersConstant.MOLTEN_FUNGUS_*`
- `ENDER_COLOR` → `MOLTEN_COLOR`
- `ENDER_PUFFBALL_PLANKS.defaultBlockState()` → `MOLTEN_FUNGUS_PLANKS.defaultBlockState()`

### 5.4 Soul Wanderer wood set (15 blocks)

Same pattern again. Replace `ENDER_COLOR` with `SOUL_COLOR` and add `.lightLevel(state -> LUMINANCE)`
to all blocks that have a luminance in the 1.20.1 reference (all non-button, non-pressure-plate
blocks in the Soul Wanderer set).

### 5.5 Inkcap mushroom blocks (3)

```java
INK_CAP_BLACK_MUSHROOM_BLOCK = registerBlock(MonstersConstant.INK_CAP_BLACK_MUSHROOM_BLOCK,
    new HugeMushroomBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BROWN_MUSHROOM_BLOCK)
        .mapColor(MapColor.COLOR_BLACK)));

INK_CAP_GREY_MUSHROOM_BLOCK = registerBlock(MonstersConstant.INK_CAP_GREY_MUSHROOM_BLOCK,
    new HugeMushroomBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BROWN_MUSHROOM_BLOCK)
        .mapColor(MapColor.COLOR_GRAY)));

INK_CAP_LIGHT_GREY_MUSHROOM_BLOCK = registerBlock(MonstersConstant.INK_CAP_LIGHT_GREY_MUSHROOM_BLOCK,
    new HugeMushroomBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BROWN_MUSHROOM_BLOCK)
        .mapColor(MapColor.COLOR_LIGHT_GRAY)));
```

> `HugeMushroomBlock` is `net.minecraft.world.level.block.HugeMushroomBlock` — the vanilla class
> used for brown/red mushroom blocks. It is the correct 1.21.1 equivalent of 1.20.1's `MushroomBlock`.

### 5.6 Huge decorative mushroom blocks (12)

The `DirectionalBlock` from §4.1 is used here. All twelve reference `BlockShapes` constants
(§4.2). Register after `DirectionalBlock` and `BlockShapes` are in place.

```java
// Use DirectionalBlock + matching shape constant from BlockShapes
HUGE_BROWN_MUSHROOM = registerBlock(MonstersConstant.HUGE_BROWN_MUSHROOM,
    new DirectionalBlock(BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_BROWN)
        .strength(1.0F, 10.0F)
        .sound(SoundType.FUNGUS)
        .noOcclusion(),
        BlockShapes.HUGE_MUSHROOM));

HUGE_CRIMSON_FUNGUS = registerBlock(MonstersConstant.HUGE_CRIMSON_FUNGUS,
    new DirectionalBlock(BlockBehaviour.Properties.ofFullCopy(HUGE_BROWN_MUSHROOM)
        .mapColor(MapColor.COLOR_RED), BlockShapes.HUGE_FUNGUS));

HUGE_CRIMSON_RARE_FUNGUS = registerBlock(MonstersConstant.HUGE_CRIMSON_RARE_FUNGUS,
    new DirectionalBlock(BlockBehaviour.Properties.ofFullCopy(HUGE_BROWN_MUSHROOM)
        .mapColor(MapColor.FIRE), BlockShapes.HUGE_FUNGUS));

HUGE_ENDER_PUFFBALL = registerBlock(MonstersConstant.HUGE_ENDER_PUFFBALL,
    new DirectionalBlock(BlockBehaviour.Properties.ofFullCopy(HUGE_BROWN_MUSHROOM)
        .mapColor(ENDER_COLOR), BlockShapes.HUGE_MUSHROOM));

HUGE_FLY_RED_AGARIC = registerBlock(MonstersConstant.HUGE_FLY_RED_AGARIC,
    new DirectionalBlock(BlockBehaviour.Properties.ofFullCopy(HUGE_BROWN_MUSHROOM)
        .mapColor(MapColor.COLOR_RED), BlockShapes.MEDIUM_MUSHROOM));

HUGE_FLY_YELLOW_AGARIC = registerBlock(MonstersConstant.HUGE_FLY_YELLOW_AGARIC,
    new DirectionalBlock(BlockBehaviour.Properties.ofFullCopy(HUGE_BROWN_MUSHROOM)
        .mapColor(MapColor.COLOR_YELLOW), BlockShapes.MEDIUM_MUSHROOM));

HUGE_INFERNAL_MUSHROOM = registerBlock(MonstersConstant.HUGE_INFERNAL_MUSHROOM,
    new DirectionalBlock(BlockBehaviour.Properties.ofFullCopy(HUGE_BROWN_MUSHROOM)
        .mapColor(MapColor.COLOR_ORANGE), BlockShapes.HUGE_MUSHROOM));

HUGE_INK_CAP_MUSHROOM = registerBlock(MonstersConstant.HUGE_INK_CAP_MUSHROOM,
    new DirectionalBlock(BlockBehaviour.Properties.ofFullCopy(HUGE_BROWN_MUSHROOM)
        .mapColor(MapColor.COLOR_BLACK), BlockShapes.HUGE_FUNGUS_VARIANT));

HUGE_MOLTEN_FUNGUS = registerBlock(MonstersConstant.HUGE_MOLTEN_FUNGUS,
    new DirectionalBlock(BlockBehaviour.Properties.ofFullCopy(HUGE_BROWN_MUSHROOM)
        .mapColor(MapColor.GOLD), BlockShapes.HUGE_MUSHROOM));

HUGE_SOUL_WANDERER = registerBlock(MonstersConstant.HUGE_SOUL_WANDERER,
    new DirectionalBlock(BlockBehaviour.Properties.ofFullCopy(HUGE_BROWN_MUSHROOM)
        .mapColor(MapColor.DIAMOND).lightLevel(state -> LUMINANCE), BlockShapes.HUGE_FUNGUS));

HUGE_WARPED_FUNGUS = registerBlock(MonstersConstant.HUGE_WARPED_FUNGUS,
    new DirectionalBlock(BlockBehaviour.Properties.ofFullCopy(HUGE_BROWN_MUSHROOM)
        .mapColor(MapColor.COLOR_CYAN), BlockShapes.SMALL_MUSHROOM));

HUGE_WARPED_RARE_FUNGUS = registerBlock(MonstersConstant.HUGE_WARPED_RARE_FUNGUS,
    new DirectionalBlock(BlockBehaviour.Properties.ofFullCopy(HUGE_BROWN_MUSHROOM)
        .mapColor(MapColor.WATER), BlockShapes.SMALL_MUSHROOM));
```

### 5.7 Ender Moss

```java
ENDER_MOSS = registerBlock(MonstersConstant.ENDER_MOSS,
    new GrassBlock(BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_PURPLE)
        .strength(3.0F, 9.0F)
        .randomTicks()
        .sound(SoundType.STONE)
        .requiresCorrectToolForDrops()));
```

> `GrassBlock` is used to get the ticking and spread behaviour. It's acceptable to use
> `Block` instead if spreading behaviour is not wanted — check design intent.

### 5.8 Glow Berry Bush

```java
GLOW_BERRY_BUSH = registerBlock(MonstersConstant.GLOW_BERRY_BUSH,
    new GlowBerryBushBlock(BlockBehaviour.Properties.of()
        .randomTicks()
        .sound(SoundType.SWEET_BERRY_BUSH)
        .noOcclusion()
        .noCollission()));
```

### 5.9 Jar and Urns

```java
JAR = registerBlock(MonstersConstant.JAR,
    new DirectionalBlock(BlockBehaviour.Properties.of()
        .mapColor(MapColor.WATER)
        .strength(1.0F, 10.0F)
        .sound(SoundType.GLASS)
        .noOcclusion(),
        BlockShapes.JAR));

URN_MOLTEN = registerBlock(MonstersConstant.URN_MOLTEN,
    new DirectionalBlock(BlockBehaviour.Properties.ofFullCopy(JAR)
        .mapColor(MOLTEN_COLOR), BlockShapes.BIG_URN));

URN_CRIMSON = registerBlock(MonstersConstant.URN_CRIMSON,
    new DirectionalBlock(BlockBehaviour.Properties.ofFullCopy(JAR)
        .mapColor(MapColor.COLOR_RED), BlockShapes.SMALL_URN));
```

---

## 6. `MonstersItems.java` — New File

Create `Common/src/main/java/net/heriazone/monsters_girls/source/MonstersItems.java`.
This file does not exist yet. Model it directly on the existing `MonstersBlocks.java` pattern —
use `BuiltInRegistries.ITEM` with `Registry.register`.

### 6.1 File skeleton

```java
package net.heriazone.monsters_girls.source;

import net.heriazone.monsters_girls.MonstersConstant;
import net.heriazone.monsters_girls.MonstersIdentifier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

/**
 * <p>Registers all custom items for Monsters &amp; Girls.<p>
 * <p>
 * <b>Architecture:</b> Uses vanilla {@link BuiltInRegistries#ITEM} directly —
 * no loader-specific API required. Lives in Common so item references can be
 * used from Common code (creative tab builders, exchange recipes).
 * <p>
 * <b>Initialization:</b> Call {@link #register()} from {@code Monsters.init()},
 * after {@code MonstersBlocks.register()} — block items require their block to
 * already be registered.
 */
public class MonstersItems {

    // -- Block Items — Wood Sets --
    public static Item ENDER_PUFFBALL_BLOCK;
    // ... (all 45 wood-set block items declared as public static fields)

    // -- Block Items — Huge Mushrooms --
    public static Item HUGE_BROWN_MUSHROOM;
    // ... etc.

    // -- Block Items — Decorations --
    public static Item ENDER_MOSS;
    public static Item GLOW_BERRY_BUSH;
    public static Item JAR;
    public static Item URN_MOLTEN;
    public static Item URN_CRIMSON;

    // -- Hat Items --
    public static Item HAT_MUSHROOM_BROWN;
    // ... (12 hat items)

    // -- Registration --

    public static void register() {
        registerBlockItems();
        registerHatItems();
    }

    private static void registerBlockItems() {
        ENDER_PUFFBALL_BLOCK = registerBlockItem(MonstersConstant.ENDER_PUFFBALL_BLOCK,
            MonstersBlocks.ENDER_PUFFBALL_BLOCK);
        // ... one line per block item
    }

    private static void registerHatItems() {
        HAT_MUSHROOM_BROWN = registerItem(MonstersConstant.HAT_MUSHROOM_BROWN,
            new ArmorItem(MonstersArmorMaterials.BROWN_MUSHROOM,
                ArmorItem.Type.HELMET,
                new Item.Properties()));
        // ... one line per hat
    }

    // -- Helpers --

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(BuiltInRegistries.ITEM,
            MonstersIdentifier.getId(name),
            new BlockItem(block, new Item.Properties()));
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM,
            MonstersIdentifier.getId(name), item);
    }

} // Class: MonstersItems
```

### 6.2 Call site — `Monsters.init()`

Add `MonstersItems.register()` to `Monsters.init()` after `MonstersBlocks.register()`:

```java
// After MonstersBlocks.register():
MonstersItems.register();
```

---

## 7. Hat Items — `MonstersArmorMaterials.java`

Create `Common/src/main/java/net/heriazone/monsters_girls/item/MonstersArmorMaterials.java`.

**1.21.1 change:** `ArmorMaterial` is **no longer an enum** in 1.21.1 — it is a record registered
in the `ArmorMaterial` registry via `BuiltInRegistries.ARMOR_MATERIAL`. The 1.20.1 enum approach
does not compile in 1.21.1.

### 7.1 1.21.1 `ArmorMaterial` registration pattern

```java
package net.heriazone.monsters_girls.item;

import net.heriazone.monsters_girls.MonstersIdentifier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Map;

/**
 * <p>Registers armor materials for all mushroom/fungus hat items.<p>
 * <p>
 * <b>Design:</b> All hat materials share the same stats — lightweight head-slot
 * cosmetic items. Durability 13 × multiplier 16 = 208. Defense 2 (head slot only).
 * Enchantability 28 (high — same as leather in enchanting). No toughness,
 * no knockback resistance. Equip sound: elytra equip (soft, thematic).
 * <p>
 * <b>1.21.1 API:</b> ArmorMaterial is a record registered in BuiltInRegistries.ARMOR_MATERIAL.
 * Use Registry.registerForHolder() to get the Holder needed by ArmorItem constructor.
 */
public class MonstersArmorMaterials {

    // -- Shared stats --
    private static final int DURABILITY     = 16;   // multiplier against base durability
    private static final int DEFENSE_HELMET = 2;
    private static final int ENCHANTABILITY = 28;

    // -- Material Holders --
    public static Holder<ArmorMaterial> BROWN_MUSHROOM;
    public static Holder<ArmorMaterial> CRIMSON_FUNGUS;
    public static Holder<ArmorMaterial> CRIMSON_RARE_FUNGUS;
    public static Holder<ArmorMaterial> ENDER_PUFFBALL;
    public static Holder<ArmorMaterial> FLY_RED_AGARIC;
    public static Holder<ArmorMaterial> FLY_YELLOW_AGARIC;
    public static Holder<ArmorMaterial> INFERNAL_MUSHROOM;
    public static Holder<ArmorMaterial> INK_CAP_MUSHROOM;
    public static Holder<ArmorMaterial> MOLTEN_FUNGUS;
    public static Holder<ArmorMaterial> SOUL_WANDERER_FUNGUS;
    public static Holder<ArmorMaterial> WARPED_FUNGUS;
    public static Holder<ArmorMaterial> WARPED_RARE_FUNGUS;

    // -- Registration --

    public static void register() {
        BROWN_MUSHROOM      = registerMaterial(MonstersConstant.HAT_MUSHROOM_BROWN,
                                Ingredient.of(Items.BROWN_MUSHROOM));
        CRIMSON_FUNGUS      = registerMaterial(MonstersConstant.HAT_MUSHROOM_CRIMSON,
                                Ingredient.of(Items.CRIMSON_FUNGUS));
        CRIMSON_RARE_FUNGUS = registerMaterial(MonstersConstant.HAT_MUSHROOM_CRIMSON_RARE,
                                Ingredient.of(Items.CRIMSON_FUNGUS));
        ENDER_PUFFBALL      = registerMaterial(MonstersConstant.HAT_MUSHROOM_ENDER_PUFFBALL,
                                Ingredient.of(Items.CHORUS_FRUIT));
        FLY_RED_AGARIC      = registerMaterial(MonstersConstant.HAT_MUSHROOM_FLY_RED_AGARIC,
                                Ingredient.of(Items.RED_MUSHROOM));
        FLY_YELLOW_AGARIC   = registerMaterial(MonstersConstant.HAT_MUSHROOM_FLY_YELLOW_AGARIC,
                                Ingredient.of(Items.BROWN_MUSHROOM));  // no direct vanilla item
        INFERNAL_MUSHROOM   = registerMaterial(MonstersConstant.HAT_MUSHROOM_INFERNAL,
                                Ingredient.of(Items.NETHERRACK));
        INK_CAP_MUSHROOM    = registerMaterial(MonstersConstant.HAT_MUSHROOM_INK_CAP,
                                Ingredient.of(Items.INK_SAC));
        MOLTEN_FUNGUS       = registerMaterial(MonstersConstant.HAT_MUSHROOM_MOLTEN,
                                Ingredient.of(Items.MAGMA_CREAM));
        SOUL_WANDERER_FUNGUS = registerMaterial(MonstersConstant.HAT_MUSHROOM_SOUL_WANDERER,
                                Ingredient.of(Items.SOUL_SAND));
        WARPED_FUNGUS       = registerMaterial(MonstersConstant.HAT_MUSHROOM_WARPED,
                                Ingredient.of(Items.WARPED_FUNGUS));
        WARPED_RARE_FUNGUS  = registerMaterial(MonstersConstant.HAT_MUSHROOM_WARPED_RARE,
                                Ingredient.of(Items.WARPED_FUNGUS));
    }

    private static Holder<ArmorMaterial> registerMaterial(String key, Ingredient repairIngredient) {
        ArmorMaterial material = new ArmorMaterial(
            Map.of(ArmorItem.Type.HELMET, DEFENSE_HELMET),
            ENCHANTABILITY,
            SoundEvents.ARMOR_EQUIP_ELYTRA,
            () -> repairIngredient,
            List.of(new ArmorMaterial.Layer(MonstersIdentifier.getId(key))),
            0.0f,   // toughness
            0.0f    // knockback resistance
        );
        return Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL,
            MonstersIdentifier.getId(key), material);
    }

} // Class: MonstersArmorMaterials
```

> **Repair ingredients:** The 1.20.1 original left most repair ingredients commented out
> (referencing the mod blocks that didn't exist yet). The values above use the closest available
> vanilla item as a placeholder. Replace with the corresponding custom block item once those are
> registered (e.g. `MonstersItems.HUGE_BROWN_MUSHROOM` for `BROWN_MUSHROOM` material).

### 7.2 Call `MonstersArmorMaterials.register()` from `Monsters.init()`

```java
// Before MonstersItems.register() — materials must exist before hat items
MonstersArmorMaterials.register();
MonstersItems.register();
```

### 7.3 Hat item registration in `MonstersItems`

```java
HAT_MUSHROOM_BROWN = registerItem(MonstersConstant.HAT_MUSHROOM_BROWN,
    new ArmorItem(MonstersArmorMaterials.BROWN_MUSHROOM,
        ArmorItem.Type.HELMET, new Item.Properties()));

HAT_MUSHROOM_CRIMSON = registerItem(MonstersConstant.HAT_MUSHROOM_CRIMSON,
    new ArmorItem(MonstersArmorMaterials.CRIMSON_FUNGUS,
        ArmorItem.Type.HELMET, new Item.Properties()));
// ... repeat for all 12 hats
```

### 7.4 Hat textures (armor layer textures)

In 1.21.1, `ArmorMaterial.Layer` takes a `ResourceLocation` — the hat texture is resolved
from `assets/monsters_girls/textures/models/armor/{key}_layer_1.png`. Provide one texture
per hat. Hat layer textures are separate from item textures.

```
assets/monsters_girls/textures/models/armor/hat_mushroom_brown_layer_1.png
assets/monsters_girls/textures/models/armor/hat_mushroom_crimson_layer_1.png
... (12 total)
```

---

## 8. Client-Side Render Layer Registration

Doors, trapdoors, and all plant/directional blocks with transparent pixels need the `CUTOUT`
render layer. In 1.21.1 the `BlockRenderLayerMap` is a Fabric-only API — on Forge/NeoForge
the equivalent is the `RegisterNamedRenderTypesEvent` / `RenderType.cutout()` block lookup.

### 8.1 Common render layer list

The following blocks need `CUTOUT` registration (identical list to 1.20.1):

```
Doors:     ENDER_PUFFBALL_DOOR, MOLTEN_FUNGUS_DOOR, SOUL_WANDERER_DOOR
Trapdoors: ENDER_PUFFBALL_TRAPDOOR, MOLTEN_FUNGUS_TRAPDOOR, SOUL_WANDERER_TRAPDOOR
Huge:      all 12 HUGE_* blocks
Plants:    GLOW_BERRY_BUSH, ENDER_MOSS
Jars:      JAR, URN_MOLTEN, URN_CRIMSON
```

### 8.2 Where to register

Add a `MonstersBlocksClient.registerRenderLayers()` static method under each loader's
client entry point, or add to the existing `Monsters.initClient()` call chain. Existing
plant blocks (`INKCAP_MUSHROOM`, `MOLTEN_FUNGUS` etc.) already need this — use the same
location they are registered.

---

## 9. `Monsters.init()` — Final Call Order

```java
public static void init() {
    if (initialized) return;

    MonstersEffects.register();         // existing
    MonstersPotions.register();         // existing
    MonstersSounds.register();          // existing
    MonstersBlocks.register();          // existing + new blocks from §5
    MonstersArmorMaterials.register();  // NEW — must precede MonstersItems
    MonstersItems.register();           // NEW
    // Entity families reference blocks via genesis/planting — init after blocks+items

    initialized = true;
}
```

---

## 10. Complete Implementation Checklist

### New Java files (Common)

- [ ] `block/DirectionalBlock.java` — port from 1.20.1, add `codec()` + 1.21.1 API fixes
- [ ] `block/BlockShapes.java` — port `VoxelCollision` constants
- [ ] `block/GlowBerryBushBlock.java` — port from 1.20.1, add `codec()` + `entityInside`
- [ ] `item/MonstersArmorMaterials.java` — new, uses 1.21.1 record-based `ArmorMaterial`
- [ ] `source/MonstersItems.java` — new, mirrors `MonstersBlocks.java` pattern

### Modified Java files (Common)

- [ ] `MonstersConstant.java` — add all string constants from §3
- [ ] `source/MonstersBlocks.java` — add field declarations + registrations from §5
- [ ] `Monsters.java` — add `MonstersArmorMaterials.register()` + `MonstersItems.register()` calls

### Resources

**Block state JSONs** (one per block):
- [ ] `blockstates/` — 45 wood-set blocks + 12 huge + 5 decoration + jar + urns = ~65 files

**Block model JSONs** (one per variant / side):
- [ ] `models/block/` — ~65 model files (cubes, stairs, slabs, doors, trapdoors, pillars)

**Item model JSONs** (one per item):
- [ ] `models/item/` — one file per block item + 12 hat items = ~77 files

**Textures**:
- [ ] `textures/block/` — ~60 texture PNGs (all three wood sets + huge + decorations)
- [ ] `textures/item/` — ~77 item PNGs
- [ ] `textures/models/armor/` — 12 hat layer PNGs

**Loot tables** (for blocks that drop items):
- [ ] `data/monsters_girls/loot_tables/blocks/` — one per block (standard drop-self pattern)
- [ ] Doors + trapdoors: use vanilla door loot table pattern (drop 1 when broken)

**Recipes** (for craftable blocks):
- [ ] `data/monsters_girls/recipe/` — planks from stem/hyphae, stairs/slabs from planks,
  fence/fence gate, door/trapdoor, pressure plate, button (standard vanilla wood recipes)
- [ ] Hat crafting recipes — your design decision (hat-shaped pattern using huge mushroom block?)

**Tags**:
- [ ] `data/minecraft/tags/blocks/` — add wood-set planks to `planks.json`,
  slabs to `wooden_slabs.json`, stairs to `wooden_stairs.json`, etc.
- [ ] Add to `mineable/axe.json` for wood-set blocks, `mineable/pickaxe.json` for stone-like blocks

**Lang keys** (in `en_us.json`):
- [ ] One `block.monsters_girls.{key}` per block
- [ ] One `item.monsters_girls.{key}` per item/hat

---

## 11. Consequences

### Positive
- Brings 1.21.1 content parity with the full 1.20.1 block roster for the three active families
- `MonstersItems.java` creates the missing item registration layer the codebase needed
- `MonstersArmorMaterials.java` uses the correct 1.21.1 record API — forward-compatible
- All blocks follow the existing `MonstersBlocks` pattern — no new registration abstractions needed
- `DirectionalBlock` and `BlockShapes` are reusable for future decorative blocks

### Negative / Trade-offs
- High resource file count (~65 blockstates, ~65 block models, ~77 item models, ~60 textures)
  — this is tedious but mechanical, and most can be generated with datagen
- The 1.21.1 `ArmorMaterial` record API is significantly different from the 1.20.1 enum approach —
  the migration is non-trivial but straightforward once understood
- `GlowBerryBushBlock` and `MushroomPlantBlock` both extend `BushBlock` — in 1.21.1 `BushBlock`
  is abstract, requiring the `codec()` override on every concrete subclass

### Risks
- Block constructor APIs (`DoorBlock`, `FenceGateBlock`, `StairBlock`, etc.) changed between
  1.20.1 and 1.21.1. Verify each constructor signature against the vanilla 1.21.1 source before
  writing code — the patterns in §5 use the correct 1.21.1 signatures based on available knowledge,
  but minor parameter order differences may exist.
- `Ingredient.of(ItemLike)` was renamed to `Ingredient.of(ItemLike...)` in some versions — check
  the actual Minecraft 1.21.1 API for the repair ingredient supplier in `ArmorMaterial`.
