# ADR 001: Generic NBT Transfer Recipe System

**Status**: Implemented  
**Date**: 2025-11-23  
**Decision Makers**: Development Team  
**Context**: Fabric 1.20.1 - LovelyRobot Legacy Mod

---

## Context

Currently, NBT data transfer during crafting is handled by two separate systems:

1. **Custom Recipe (`SpawnEggCraftingRecipe`)** - Transfers NBT for crafting preview (ghost item in result slot)
2. **Mixin Event Handler (`ItemCraftHandler` + `CraftingResultSlotMixin`)** - Transfers NBT when player takes the crafted item

This creates redundancy where:
- NBT transfer logic is duplicated
- Robot core → spawn egg crafting transfers NBT twice (preview + take)
- Dye recipes only work on item take, not in preview
- Maintenance requires updating two separate systems

### Current Issues

1. **Redundancy** - Same NBT transfer happens in two places
2. **Inconsistent Preview** - Dye recipes don't show color in preview
3. **Maintenance Burden** - Changes require updating multiple files
4. **Limited Extensibility** - Hard to add new NBT modification patterns
5. **Hardcoded Logic** - NBT keys and transfer logic are hardcoded

---

## Decision

Implement a **unified, generic NBT transfer recipe system** that:

1. Handles both preview and final crafting in a single place
2. Supports multiple transfer strategies (full copy, additive merge, hybrid)
3. Uses composable modifiers for flexible NBT manipulation
4. Eliminates the need for mixin event handlers
5. Preserves existing NBT data by default (additive behavior)

---

## Architecture

### Core Components

```
┌─────────────────────────────────────────────────────────────┐
│                    Recipe System                             │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────────────────────────────────────────────┐  │
│  │         NbtTransferRecipe (Abstract Base)            │  │
│  │  - Handles generic NBT transfer logic                │  │
│  │  - Delegates to strategy for transfer behavior       │  │
│  └──────────────────────────────────────────────────────┘  │
│                          │                                   │
│         ┌────────────────┴────────────────┐                │
│         │                                  │                │
│  ┌──────▼──────────┐            ┌─────────▼────────┐      │
│  │ SpawnEggCrafting│            │  SpawnEggDye     │      │
│  │     Recipe      │            │     Recipe       │      │
│  │   (Shaped)      │            │  (Shapeless)     │      │
│  └─────────────────┘            └──────────────────┘      │
│                                                               │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                  Strategy System                             │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────────────────────────────────────────────┐  │
│  │      INbtTransferStrategy (Interface)                │  │
│  │  - transferNbt(inventory, result) → ItemStack        │  │
│  └──────────────────────────────────────────────────────┘  │
│                          │                                   │
│         ┌────────────────┼────────────────┐                │
│         │                │                 │                │
│  ┌──────▼──────┐  ┌─────▼─────┐  ┌───────▼────────┐      │
│  │FullNbtCopy  │  │ Additive  │  │    Hybrid      │      │
│  │  Strategy   │  │NbtMerge   │  │NbtStrategy     │      │
│  │             │  │ Strategy  │  │                │      │
│  └─────────────┘  └───────────┘  └────────────────┘      │
│                                                               │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                  Modifier System                             │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────────────────────────────────────────────┐  │
│  │         NbtModifier (Interface)                      │  │
│  │  - apply(inventory, nbt) → void                      │  │
│  └──────────────────────────────────────────────────────┘  │
│                          │                                   │
│         ┌────────────────┼────────────────┐                │
│         │                │                 │                │
│  ┌──────▼──────┐  ┌─────▼─────┐  ┌───────▼────────┐      │
│  │DyeColor     │  │ NameTag   │  │  Enchantment   │      │
│  │ Modifier    │  │ Modifier  │  │   Modifier     │      │
│  └─────────────┘  └───────────┘  └────────────────┘      │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

---

## File Structure

```
sources/legacy/llovelyr-1.20.1/Fabric/src/main/java/net/msymbios/llovelyr/source/

recipes/
├── base/
│   ├── NbtTransferRecipe.java              # Abstract base recipe
│   ├── NbtTransferRecipeSerializer.java    # Generic serializer
│   └── INbtTransferStrategy.java           # Strategy interface
│
├── strategies/
│   ├── FullNbtCopyStrategy.java            # Complete NBT replacement
│   ├── AdditiveNbtMergeStrategy.java       # Preserve + modify
│   └── HybridNbtStrategy.java              # Copy + modify
│
├── modifiers/
│   ├── NbtModifier.java                    # Modifier interface
│   ├── DyeColorModifier.java               # Color modification
│   ├── NameTagModifier.java                # Name modification
│   └── [Future modifiers...]
│
├── spawnegg/
│   ├── SpawnEggCraftingRecipe.java         # Shaped recipe (core → egg)
│   ├── SpawnEggCraftingSerializer.java     # Shaped serializer
│   ├── SpawnEggDyeRecipe.java              # Shapeless recipe (egg + dye)
│   └── SpawnEggDyeSerializer.java          # Shapeless serializer
│
└── LovelyRecipes.java                       # Recipe registry

mixin/
├── IShapedRecipeAccessor.java              # Existing accessor
└── IShapelessRecipeAccessor.java           # New accessor for shapeless
```

---

## Strategy Descriptions

### 1. FullNbtCopyStrategy

**Purpose**: Complete NBT replacement from source to result

**Behavior**:
- Finds source item in crafting grid
- Copies ALL NBT from source to result
- Result gets exact copy of source NBT

**Use Case**: Robot core → spawn egg (transfer everything)

**Example**:
```java
new FullNbtCopyStrategy(LovelyItems.ROBOT_CORE)
```

---

### 2. AdditiveNbtMergeStrategy

**Purpose**: Preserve existing NBT and add/modify specific keys

**Behavior**:
- Starts with base item's existing NBT (if any)
- Applies modifiers that add/overwrite specific keys
- Preserves all unmodified NBT keys
- Never loses data unless explicitly overwritten

**Use Case**: Spawn egg + dye (keep all data, just change color)

**Example**:
```java
new AdditiveNbtMergeStrategy(
    spawnEggItem,
    List.of(new DyeColorModifier(LovelyIdentifier.STAT_COLOR))
)
```

---

### 3. HybridNbtStrategy

**Purpose**: Copy base NBT from source, then apply modifications

**Behavior**:
- Copies ALL NBT from source item
- Then applies modifiers to add/overwrite specific keys
- Combines full copy with selective modification

**Use Case**: Robot core + dye → spawn egg (copy all robot data, override color)

**Example**:
```java
new HybridNbtStrategy(
    LovelyItems.ROBOT_CORE,
    List.of(new DyeColorModifier(LovelyIdentifier.STAT_COLOR))
)
```

---

## Modifier System

### NbtModifier Interface

```java
@FunctionalInterface
public interface NbtModifier {
    /**
     * Applies NBT modifications based on crafting ingredients.
     *
     * @param inventory crafting grid to search for modifier items
     * @param nbt NBT compound to modify
     */
    void apply(RecipeInputInventory inventory, NbtCompound nbt);
}
```

### Key Characteristics

1. **Generic** - No hardcoded NBT keys
2. **Composable** - Multiple modifiers can be chained
3. **Additive** - Modifiers add/overwrite, never erase
4. **Flexible** - Can be classes or lambdas
5. **Conditional** - Can check existing values before applying

---

## Implementation Examples

### Example 1: Simple Dye Recipe

```java
public class SpawnEggDyeRecipe extends ShapelessRecipe {
    private final INbtTransferStrategy strategy;
    
    public SpawnEggDyeRecipe(Identifier id, String group, 
                             CraftingRecipeCategory category,
                             ItemStack result, 
                             DefaultedList<Ingredient> ingredients) {
        super(id, group, category, result, ingredients);
        
        // Additive: Keep all existing NBT, just change color
        this.strategy = new AdditiveNbtMergeStrategy(
            result.getItem(),
            List.of(new DyeColorModifier(LovelyIdentifier.STAT_COLOR))
        );
    }
    
    @Override
    public ItemStack craft(RecipeInputInventory inventory, 
                          DynamicRegistryManager registryManager) {
        ItemStack result = super.craft(inventory, registryManager);
        return strategy.transferNbt(inventory, result);
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return LovelyRecipes.SPAWN_EGG_DYE;
    }
}
```

### Example 2: Robot Core Crafting

```java
public class SpawnEggCraftingRecipe extends ShapedRecipe {
    private final INbtTransferStrategy strategy;
    
    public SpawnEggCraftingRecipe(Identifier id, String group,
                                   CraftingRecipeCategory category,
                                   int width, int height,
                                   DefaultedList<Ingredient> ingredients,
                                   ItemStack result) {
        super(id, group, category, width, height, ingredients, result);
        
        // Full copy: Transfer all NBT from robot core
        this.strategy = new FullNbtCopyStrategy(LovelyItems.ROBOT_CORE);
    }
    
    @Override
    public ItemStack craft(RecipeInputInventory inventory,
                          DynamicRegistryManager registryManager) {
        ItemStack result = super.craft(inventory, registryManager);
        return strategy.transferNbt(inventory, result);
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return LovelyRecipes.SPAWN_EGG_CRAFTING;
    }
}
```

### Example 3: Complex Multi-Modifier

```java
// Spawn egg + dye + name tag + enchanted book
this.strategy = new AdditiveNbtMergeStrategy(
    spawnEggItem,
    List.of(
        new DyeColorModifier(LovelyIdentifier.STAT_COLOR),
        new NameTagModifier(),  // Modifies name + owner
        new EnchantmentModifier(LovelyIdentifier.STAT_LEVEL)
    )
);
```

---

## Recipe JSON Examples

### Shaped Recipe (Robot Core → Spawn Egg)

```json
{
  "type": "llovelyr:spawn_egg_crafting",
  "category": "misc",
  "pattern": [
    "III",
    "ICI",
    "III"
  ],
  "key": {
    "I": {"item": "minecraft:iron_ingot"},
    "C": {"item": "llovelyr:robot_core"}
  },
  "result": {
    "item": "llovelyr:vanilla_spawn",
    "count": 1
  }
}
```

### Shapeless Recipe (Spawn Egg + Dye)

```json
{
  "type": "llovelyr:spawn_egg_dye",
  "category": "misc",
  "ingredients": [
    {"item": "llovelyr:vanilla_spawn"},
    {"tag": "llovelyr:dyes"}
  ],
  "result": {
    "item": "llovelyr:vanilla_spawn",
    "count": 1
  }
}
```

---

## Modifier Implementations

### DyeColorModifier

```java
public class DyeColorModifier implements NbtModifier {
    private final String nbtKey;
    
    public DyeColorModifier(String nbtKey) {
        this.nbtKey = nbtKey;
    }
    
    @Override
    public void apply(RecipeInputInventory inventory, NbtCompound nbt) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem() instanceof DyeItem dye) {
                int colorId = getColorIdFromDye(dye);
                nbt.putInt(nbtKey, colorId);
                break;
            }
        }
    }
    
    private int getColorIdFromDye(DyeItem dye) {
        if (dye == Items.WHITE_DYE) return EntityTexture.WHITE.getId();
        if (dye == Items.ORANGE_DYE) return EntityTexture.ORANGE.getId();
        // ... all 16 colors
        return EntityTexture.WHITE.getId();
    }
}
```

### NameTagModifier

```java
public class NameTagModifier implements NbtModifier {
    @Override
    public void apply(RecipeInputInventory inventory, NbtCompound nbt) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem() == Items.NAME_TAG && stack.hasCustomName()) {
                String name = stack.getName().getString();
                nbt.putString(LovelyIdentifier.STAT_CUSTOM_NAME, name);
                nbt.putString(LovelyIdentifier.STAT_OWNER, "Custom");
                break;
            }
        }
    }
}
```

---

## Migration Path

### Phase 1: Create Base Infrastructure
- [x] Create `INbtTransferStrategy` interface
- [x] Create `NbtModifier` interface
- [x] Create `FullNbtCopyStrategy`
- [x] Create `AdditiveNbtMergeStrategy`
- [ ] Create `HybridNbtStrategy` (deferred - not needed for current use cases)

### Phase 2: Create Modifiers
- [x] Create `DyeColorModifier`
- [ ] Create `NameTagModifier` (future enhancement)
- [ ] Create utility class `DyeColorMapper` (integrated into DyeColorModifier)

### Phase 3: Implement Recipes
- [x] Create new `SpawnEggCraftingRecipe` with strategy
- [x] Create `SpawnEggCraftingSerializer`
- [x] Create `SpawnEggDyeRecipe` with strategy
- [x] Create `SpawnEggDyeSerializer`
- [x] Create `IShapelessRecipeAccessor` mixin

### Phase 4: Update Registry
- [x] Register new recipe serializers in `LovelyRecipes`
- [x] Update recipe JSONs to use new types

### Phase 5: Remove Old System
- [x] Remove `ItemCraftHandler.java`
- [x] Remove `CraftingResultSlotMixin.java`
- [x] Remove `IItemCraftCallback.java` interface
- [x] Remove event registration in `LovelyEvents`

### Phase 6: Testing
- [ ] Test robot core → spawn egg (full NBT copy)
- [ ] Test spawn egg + dye (color change with NBT preservation)
- [ ] Test preview shows correct NBT in both cases
- [ ] Test multiple modifiers work together
- [ ] Test edge cases (no NBT, missing ingredients)

---

## Benefits

### 1. No Redundancy
- Single system handles both preview and final craft
- NBT transfer logic in one place
- Easier to maintain and debug

### 2. Consistent Preview
- All recipes show correct NBT in preview
- Dye recipes now show color before taking item
- Better user experience

### 3. Extensibility
- Easy to add new modifiers
- Easy to add new transfer strategies
- Composable modifiers for complex recipes

### 4. Data Preservation
- Additive by default - never loses data
- Explicit control over what gets overwritten
- Safe for complex NBT structures

### 5. Type Safety
- NBT keys from constants (no magic strings)
- Compile-time checking
- IDE autocomplete support

### 6. Testability
- Each component can be tested independently
- Strategies and modifiers are isolated
- Mock-friendly interfaces

### 7. Flexibility
- Supports shaped and shapeless recipes
- Supports multiple ingredients
- Supports conditional modifications
- Can use classes or lambdas

---

## Consequences

### Positive

1. **Cleaner Codebase** - Removes mixin event system
2. **Better UX** - Preview always shows correct NBT
3. **Maintainable** - Single source of truth for NBT logic
4. **Extensible** - Easy to add new patterns
5. **Reusable** - Strategies work for any item type
6. **Safe** - Additive behavior prevents data loss

### Negative

1. **Initial Complexity** - More classes to understand initially
2. **Migration Effort** - Requires updating existing recipes
3. **Learning Curve** - Team needs to understand strategy pattern

### Risks

1. **Breaking Changes** - Existing recipes need updating
2. **Performance** - Multiple modifiers could be slower (minimal impact)
3. **Compatibility** - Recipe JSON format changes

### Mitigation

1. **Documentation** - Comprehensive docs and examples
2. **Testing** - Thorough testing of all scenarios
3. **Gradual Migration** - Phase-by-phase implementation
4. **Backward Compatibility** - Keep old system until migration complete

---

## Alternatives Considered

### Alternative 1: Keep Current System
**Rejected** - Redundancy and maintenance burden too high

### Alternative 2: Only Use Mixin Events
**Rejected** - Cannot show NBT in crafting preview

### Alternative 3: Hardcode NBT Logic in Each Recipe
**Rejected** - Not reusable, difficult to maintain

### Alternative 4: Use Data-Driven JSON Configuration
**Considered** - Could specify modifiers in JSON, but adds complexity without significant benefit

---

## Future Enhancements

### Potential Modifiers
- `LevelBoostModifier` - Increase robot level with experience bottles
- `ProtectionModifier` - Add protection enchantments
- `OwnerModifier` - Set owner from player head
- `StatsModifier` - Modify multiple stats at once

### Potential Strategies
- `SelectiveNbtCopyStrategy` - Copy only specific NBT keys
- `MergeMultipleSourcesStrategy` - Merge NBT from multiple items
- `ConditionalStrategy` - Apply different strategies based on ingredients

### Recipe Types
- `RobotUpgradeRecipe` - Upgrade existing robots
- `RobotRepairRecipe` - Repair damaged robots
- `RobotCustomizationRecipe` - Complex multi-step customization

---

## References

- Current Implementation: `sources/legacy/llovelyr-1.20.1/Fabric/src/main/java/net/msymbios/llovelyr/source/`
- Mixin Documentation: https://github.com/SpongePowered/Mixin/wiki
- Fabric Recipe API: https://fabricmc.net/wiki/tutorial:recipes
- Strategy Pattern: https://refactoring.guru/design-patterns/strategy

---

## Approval

- [x] Technical Lead Review
- [x] Code Review
- [ ] Testing Complete
- [x] Documentation Complete
- [x] Ready for Implementation

---

**Last Updated**: 2025-11-23  
**Version**: 1.0  
**Status**: Implemented - Awaiting Testing
