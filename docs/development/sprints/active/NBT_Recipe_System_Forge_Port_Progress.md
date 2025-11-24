# NBT Recipe System - Forge 1.20.1 Port Progress

**Date**: 2025-11-23  
**Task**: Port Fabric NBT Transfer Recipe System to Forge 1.20.1  
**Reference**: ADR_001_Generic_NBT_Transfer_Recipe_System.md

---

## Overview

Porting the generic NBT transfer recipe system from Fabric to Forge to enable:
- Robot core → spawn egg crafting with full NBT transfer
- Spawn egg + dye recipes with color modification while preserving NBT data
- Preview support showing correct NBT in crafting result slot

---

## Implementation Progress

### ✅ Phase 1: Core Infrastructure (COMPLETE)

**Interfaces**
- [x] `INbtTransferStrategy` - Strategy interface for NBT transfer behavior
  - Location: `source/recipes/interfaces/INbtTransferStrategy.java`
  - Adapted: `RecipeInputInventory` → `CraftingContainer`
  
- [x] `INbtModifier` - Modifier interface for NBT modifications
  - Location: `source/recipes/interfaces/INbtModifier.java`
  - Adapted: `NbtCompound` → `CompoundTag`

**Strategies**
- [x] `FullNbtCopyStrategy` - Complete NBT replacement from source to result
  - Location: `source/recipes/internal/strategies/FullNbtCopyStrategy.java`
  - Use case: Robot core → spawn egg
  - Adapted: `inventory.size()` → `inventory.getContainerSize()`
  - Adapted: `stack.hasNbt()` → `stack.hasTag()`
  
- [x] `AdditiveNbtMergeStrategy` - Preserve existing NBT and apply modifiers
  - Location: `source/recipes/internal/strategies/AdditiveNbtMergeStrategy.java`
  - Use case: Spawn egg + dye
  - Adapted: Same as FullNbtCopyStrategy

**Modifiers**
- [x] `DyeColorModifier` - Maps dye items to EntityTexture color IDs
  - Location: `source/recipes/internal/modifiers/DyeColorModifier.java`
  - Maps all 16 dye colors to EntityTexture enum values
  - Generic design: Takes NBT key as parameter

---

### ✅ Phase 2: Recipe Classes (COMPLETE)

**Custom Recipes**
- [x] `LovelySpawnRecipe` - Shaped recipe for robot core → spawn egg
  - Extends: `ShapedRecipe`
  - Strategy: `FullNbtCopyStrategy`
  - Overrides: `assemble()` method (Forge equivalent of `craft()`)
  - Location: `source/recipes/custom/LovelySpawnRecipe.java`
  
- [x] `LovelySpawnDyeRecipe` - Shapeless recipe for spawn egg + dye
  - Extends: `ShapelessRecipe`
  - Strategy: `AdditiveNbtMergeStrategy` with `DyeColorModifier`
  - Overrides: `assemble()` method
  - Location: `source/recipes/custom/LovelySpawnDyeRecipe.java`

---

### ✅ Phase 3: Recipe Serializers (COMPLETE)

**Serializers**
- [x] `LovelySpawnRecipeSerializer` - Serializer for shaped spawn egg recipe
  - Implements: `RecipeSerializer<LovelySpawnRecipe>`
  - Delegates pattern parsing to vanilla `ShapedRecipe` serializer
  - Handles JSON deserialization via `fromJson()`
  - Handles network sync via `fromNetwork()` and `toNetwork()`
  - Location: `source/recipes/custom/LovelySpawnRecipeSerializer.java`
  
- [x] `LovelySpawnDyeRecipeSerializer` - Serializer for shapeless dye recipe
  - Implements: `RecipeSerializer<LovelySpawnDyeRecipe>`
  - Parses ingredients directly from JSON array
  - Handles JSON deserialization via `fromJson()`
  - Handles network sync via `fromNetwork()` and `toNetwork()`
  - Location: `source/recipes/custom/LovelySpawnDyeRecipeSerializer.java`

---

### ✅ Phase 4: Recipe Registry (COMPLETE)

**Registry**
- [x] Created `LovelyRecipes.java` to register new recipe types
  - Registered `SPAWN_EGG_CRAFTING` serializer
  - Registered `SPAWN_EGG_DYE` serializer
  - Uses Forge's `DeferredRegister<RecipeSerializer<?>>`
  - Integrated with `LovelyLegacy.java` main mod class
  - Location: `source/recipes/LovelyRecipes.java`

---

### ✅ Phase 5: Recipe Data Files (COMPLETE)

**JSON Recipes**
- [x] `data/llovelyr/recipes/vanilla_spawn.json`
  - Type: `llovelyr:lovely_spawn`
  - Pattern: Iron ingots + robot core
  - Category: misc
  
- [x] `data/llovelyr/recipes/vanilla_spawn_dye.json`
  - Type: `llovelyr:lovely_spawn_dye`
  - Ingredients: Spawn egg + dye tag
  - Category: misc
  
- [x] `data/llovelyr/recipes/bunny2_spawn.json`
  - Type: `llovelyr:lovely_spawn`
  - Pattern: Iron ingots + gold nuggets + robot core
  - Category: misc
  
- [x] `data/llovelyr/recipes/bunny2_spawn_dye.json`
  - Type: `llovelyr:lovely_spawn_dye`
  - Ingredients: Spawn egg + dye tag
  - Category: misc

**Tags**
- [x] `data/llovelyr/tags/items/dyes.json` - Tag for all 16 dye colors

---

### ✅ Phase 6: Mixin Accessors (NOT NEEDED)

**Decision**
- [x] Assessed mixin requirement for Forge
  - Fabric used `IShapedRecipeAccessor` for pattern parsing
  - Forge provides direct access via `RecipeSerializer.SHAPED_RECIPE.fromJson()`
  - **Decision**: No mixins needed - delegated to vanilla serializer
  - Cleaner implementation without mixin dependency

---

## Key Differences: Fabric vs Forge

### API Differences

| Fabric | Forge | Notes |
|--------|-------|-------|
| `RecipeInputInventory` | `CraftingContainer` | Crafting grid inventory |
| `NbtCompound` | `CompoundTag` | NBT data structure |
| `craft()` | `assemble()` | Recipe crafting method |
| `inventory.size()` | `inventory.getContainerSize()` | Get inventory size |
| `inventory.getStack(i)` | `inventory.getItem(i)` | Get item at index |
| `stack.hasNbt()` | `stack.hasTag()` | Check if NBT exists |
| `stack.getNbt()` | `stack.getTag()` | Get NBT compound |
| `stack.setNbt()` | `stack.setTag()` | Set NBT compound |

### Recipe Registration

**Fabric**:
```java
Registry.register(Registries.RECIPE_SERIALIZER, id, serializer);
```

**Forge**:
```java
DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = 
    DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
    
RECIPE_SERIALIZERS.register("spawn_egg_crafting", () -> serializer);
```

---

## Testing Checklist

### Unit Testing
- [ ] Test `FullNbtCopyStrategy` with robot core NBT
- [ ] Test `AdditiveNbtMergeStrategy` with spawn egg NBT
- [ ] Test `DyeColorModifier` with all 16 dye colors
- [ ] Test NBT preservation (name, owner, level, protections)

### Integration Testing
- [ ] Test robot core → spawn egg crafting
- [ ] Test spawn egg + dye crafting
- [ ] Test preview shows correct NBT in result slot
- [ ] Test final crafted item matches preview
- [ ] Test multiple dye applications preserve other NBT
- [ ] Test crafting without NBT (fresh items)

### Edge Cases
- [ ] Empty robot core (no NBT)
- [ ] Spawn egg with partial NBT
- [ ] Multiple dyes in grid (should use first found)
- [ ] Invalid dye color handling

---

## Known Issues / Decisions

### Issue 1: Recipe Serializer Approach
**Status**: Pending  
**Decision Needed**: Whether to extend vanilla serializers or create custom ones  
**Options**:
1. Extend `ShapedRecipe.Serializer` and `ShapelessRecipe.Serializer`
2. Create fully custom serializers
3. Use Forge's recipe builder API

**Recommendation**: Extend vanilla serializers for simplicity

### Issue 2: Mixin Requirement
**Status**: Pending  
**Decision Needed**: Whether Forge requires accessor mixins  
**Context**: Fabric needed accessors to access protected recipe fields  
**Action**: Assess during serializer implementation

---

## Files Created

### Forge Implementation
```
sources/legacy/llovelyr-1.20.1/Forge/src/main/java/net/msymbios/llovelyr/source/

recipes/
├── LovelyRecipes.java                     ✅ COMPLETE
│
├── interfaces/
│   ├── INbtTransferStrategy.java          ✅ COMPLETE
│   └── INbtModifier.java                  ✅ COMPLETE
│
├── internal/
│   ├── strategies/
│   │   ├── FullNbtCopyStrategy.java       ✅ COMPLETE
│   │   └── AdditiveNbtMergeStrategy.java  ✅ COMPLETE
│   │
│   └── modifiers/
│       └── DyeColorModifier.java          ✅ COMPLETE
│
└── custom/
    ├── LovelySpawnRecipe.java             ✅ COMPLETE
    ├── LovelySpawnDyeRecipe.java          ✅ COMPLETE
    ├── LovelySpawnRecipeSerializer.java   ✅ COMPLETE
    └── LovelySpawnDyeRecipeSerializer.java ✅ COMPLETE
```

### Recipe Data Files
```
sources/legacy/llovelyr-1.20.1/Forge/src/main/resources/data/llovelyr/

recipes/
├── vanilla_spawn.json                     ✅ COMPLETE
├── vanilla_spawn_dye.json                 ✅ COMPLETE
├── bunny2_spawn.json                      ✅ COMPLETE
└── bunny2_spawn_dye.json                  ✅ COMPLETE

tags/items/
└── dyes.json                              ✅ COMPLETE
```

### Integration
```
sources/legacy/llovelyr-1.20.1/Forge/src/main/java/net/msymbios/llovelyr/

LovelyLegacy.java                          ✅ UPDATED (recipe registration)
```

---

## Next Steps

1. ✅ ~~Create `LovelySpawnRecipe` and `LovelySpawnDyeRecipe` classes~~
2. ✅ ~~Implement recipe serializers~~
3. ✅ ~~Update `LovelyRecipes` registry~~
4. ✅ ~~Create recipe JSON files~~
5. **Current**: Test complete system in-game
6. **Then**: Verify NBT transfer works correctly
7. **Finally**: Document any issues and create fixes

---

## Estimated Completion

- **Core Infrastructure**: ✅ 100% (5/5 files)
- **Recipe Classes**: ✅ 100% (2/2 files)
- **Serializers**: ✅ 100% (2/2 files)
- **Registry**: ✅ 100% (1/1 files)
- **Data Files**: ✅ 100% (5/5 files)
- **Testing**: ⏳ 0%

**Overall Progress**: ~85% complete (pending testing)

---

## References

- ADR: `docs/development/decisions/ADR_001_Generic_NBT_Transfer_Recipe_System.md`
- Fabric Implementation: `sources/legacy/llovelyr-1.20.1/Fabric/src/main/java/net/msymbios/llovelyr/source/recipes/`
- Forge Recipe API: https://docs.minecraftforge.net/en/1.20.x/resources/server/recipes/

---

**Last Updated**: 2025-11-23  
**Status**: Implementation Complete - Ready for Testing
