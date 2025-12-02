# Legacy 1.20.1 - Forge & Fabric Loader Comparison Catalog

**Status**: Active  
**Last Updated**: 2025-12-01  
**Purpose**: Comprehensive catalog of all classes in Legacy 1.20.1 for both Forge and Fabric loaders, organized by cleanup complexity to guide manual code synchronization.

---

## Executive Summary

### Statistics
- **Total Unique Classes**: 94
- **Classes in Both Loaders**: 91
- **Fabric-Only Classes**: 3
- **Forge-Only Classes**: 3

### Loader-Specific Differences

#### Fabric-Only Files
1. `config/internal/ConfigProvider.java` - Fabric config provider
2. `config/internal/SimpleConfig.java` - Fabric config system
3. `lib/mixin/IShapedRecipeAccessor.java` - Fabric mixin accessor

#### Forge-Only Files
1. `common/commands/ColorArgumentTypeInfo.java` - Forge command argument info
2. `source/LovelyBlocks.java` - Forge block registration (unused in Fabric)
3. `source/LovelyClient.java` - Fabric client initialization (different from Forge events)
4. `source/LovelyGenerator.java` - Fabric data generation

---

## Cleanup Complexity Classification

### Priority 1: HIGH COMPLEXITY - Loader-Specific Implementations
**Estimated Time**: 4-6 hours per class  
**Characteristics**: Heavy loader API usage, event systems, registration mechanisms

#### Main Entry Points
| Class | Package | Description | Loader Differences |
|-------|---------|-------------|-------------------|
| `LovelyLegacy` | `net.msymbios.llovelyr` | Mod entry point | **CRITICAL**: Fabric uses `ModInitializer`, Forge uses `@Mod` annotation with constructor. Different event bus systems. |
| `LovelyConfigs` | `source` | Configuration system | **CRITICAL**: Fabric uses custom SimpleConfig, Forge uses ForgeConfigSpec. Completely different APIs. |
| `LovelyEvents` | `source` | Event registration | **HIGH**: Fabric uses Fabric API events, Forge uses EventBus. Different event handling patterns. |
| `LovelyCommands` | `source` | Command registration | **MEDIUM**: Both use Brigadier but registration timing differs. |
| `LovelyCommandArguments` | `common.commands` | Custom command arguments | **MEDIUM**: Forge requires ColorArgumentTypeInfo, Fabric doesn't. |

#### Registration Systems
| Class | Package | Description | Loader Differences |
|-------|---------|-------------|-------------------|
| `LovelyEntities` | `source` | Entity registration | **HIGH**: Forge uses DeferredRegister, Fabric uses direct Registry calls. |
| `LovelyItems` | `source` | Item registration | **HIGH**: Forge uses DeferredRegister, Fabric uses direct Registry calls. |
| `LovelyGroups` | `source` | Creative tab registration | **HIGH**: Forge uses DeferredRegister with event-based item population, Fabric uses FabricItemGroup. |
| `LovelyRecipes` | `source` | Recipe serializer registration | **MEDIUM**: Different registration APIs but similar structure. |

---

### Priority 2: MEDIUM COMPLEXITY - Abstraction Layer Classes
**Estimated Time**: 2-3 hours per class  
**Characteristics**: Minimal loader-specific code, mostly business logic with some API calls

#### Entity System Core
| Class | Package | Description | Sync Status |
|-------|---------|-------------|-------------|
| `RobotEntityType` | `common.entity.type` | Entity type wrapper | Needs review for loader-specific entity type creation |
| `NativeRobotType` | `source.entity.type` | Native robot type definitions | Should be identical, verify config loading |
| `InternalEntity` | `common.entity.internal` | Base entity logic | Should be identical, verify attribute system |
| `InternalLogic` | `common.entity.internal` | Entity behavior logic | Should be identical |
| `InternalAnimation` | `common.entity.internal` | Animation controller | Should be identical (GeckoLib) |
| `InternalModel` | `common.entity.internal` | Model base class | Should be identical (GeckoLib) |
| `InternalLayer` | `common.entity.internal` | Render layer base | Should be identical (GeckoLib) |
| `InternalParticle` | `common.entity.internal` | Particle effects | Verify particle spawning APIs |

#### Entity Implementations
| Class | Package | Description | Sync Status |
|-------|---------|-------------|-------------|
| `LovelyRobot` | `source.entity.common` | Base robot entity | Should be identical, verify NBT and networking |
| `VanillaEntity` | `source.entity.custom.vanilla` | Vanilla robot entity | Should be identical |
| `VanillaModel` | `source.entity.custom.vanilla` | Vanilla robot model | Should be identical (GeckoLib) |
| `VanillaLayer` | `source.entity.custom.vanilla` | Vanilla robot layer | Should be identical (GeckoLib) |
| `VanillaRenderer` | `source.entity.custom.vanilla` | Vanilla robot renderer | Should be identical (GeckoLib) |
| `Bunny2Entity` | `source.entity.custom.bunny2` | Bunny2 robot entity | Should be identical |
| `Bunny2Model` | `source.entity.custom.bunny2` | Bunny2 robot model | Should be identical (GeckoLib) |
| `Bunny2Layer` | `source.entity.custom.bunny2` | Bunny2 robot layer | Should be identical (GeckoLib) |
| `Bunny2Renderer` | `source.entity.custom.bunny2` | Bunny2 robot renderer | Should be identical (GeckoLib) |

#### AI Goals
| Class | Package | Description | Sync Status |
|-------|---------|-------------|-------------|
| `AiAutoAttackGoal` | `common.entity.goal` | Auto-attack AI | Should be identical |
| `AiBaseDefenseGoal` | `common.entity.goal` | Defense mode AI | Should be identical |
| `AiConditionalWanderGoal` | `common.entity.goal` | Conditional wandering AI | Should be identical |
| `AiFollowOwnerGoal` | `common.entity.goal` | Follow owner AI | Should be identical |

---

### Priority 3: LOW COMPLEXITY - Pure Logic Classes
**Estimated Time**: 30 minutes - 1 hour per class  
**Characteristics**: No loader-specific code, pure Java logic, data structures, enums

#### Framework - Entity Data
| Class | Package | Description | Sync Status |
|-------|---------|-------------|-------------|
| `CombatStats` | `framework.entity.data` | Combat statistics data | Should be identical |
| `EnchantmentStats` | `framework.entity.data` | Enchantment statistics | Should be identical |
| `ProtectionStats` | `framework.entity.data` | Protection statistics | Should be identical |
| `CombatStatsNBT` | `lib.entity.data` | Combat stats NBT serialization | Should be identical |
| `EnchantmentStatsNBT` | `lib.entity.data` | Enchantment stats NBT | Should be identical |
| `ProtectionStatsNBT` | `lib.entity.data` | Protection stats NBT | Should be identical |

#### Framework - Combat Strategies
| Class | Package | Description | Sync Status |
|-------|---------|-------------|-------------|
| `AttributeCalculationStrategy` | `framework.entity.combat` | Attribute calculation interface | Should be identical |
| `LinearAttributeStrategy` | `framework.entity.combat` | Linear attribute scaling | Should be identical |
| `ExponentialAttributeStrategy` | `framework.entity.combat` | Exponential attribute scaling | Should be identical |

#### Framework - Enchantment Strategies
| Class | Package | Description | Sync Status |
|-------|---------|-------------|-------------|
| `EnchantmentCalculationStrategy` | `framework.entity.enchantment` | Enchantment calculation interface | Should be identical |
| `DefaultEnchantmentStrategy` | `framework.entity.enchantment` | Default enchantment logic | Should be identical |

#### Framework - Protection Strategies
| Class | Package | Description | Sync Status |
|-------|---------|-------------|-------------|
| `ProtectionCalculationStrategy` | `framework.entity.protection` | Protection calculation interface | Should be identical |
| `LevelBasedProtectionStrategy` | `framework.entity.protection` | Level-based protection | Should be identical |

#### Framework - Enums
| Class | Package | Description | Sync Status |
|-------|---------|-------------|-------------|
| `EntityHand` | `framework.entity.enums` | Hand enum | Should be identical |
| `EntityState` | `framework.entity.enums` | Entity state enum | Should be identical |
| `EntityTexture` | `framework.entity.enums` | Texture enum | Should be identical |
| `EntityVariantAnimator` | `framework.entity.enums` | Variant animator enum | Should be identical |
| `EntityVariantModel` | `framework.entity.enums` | Variant model enum | Should be identical |
| `EntityVariantTexture` | `framework.entity.enums` | Variant texture enum | Should be identical |

#### Framework - Type System
| Class | Package | Description | Sync Status |
|-------|---------|-------------|-------------|
| `CombatData` | `framework.entity.type` | Combat data container | Should be identical |
| `ResourceMap` | `framework.entity.type` | Resource mapping | Should be identical |

#### Framework - Registry
| Class | Package | Description | Sync Status |
|-------|---------|-------------|-------------|
| `OwnerRobotRegistry` | `framework.registry` | Owner-robot relationship registry | Should be identical |
| `RobotRegistryEntry` | `framework.registry` | Registry entry data | Should be identical |
| `RobotRegistryManager` | `lib.registry` | Registry manager | Should be identical |

#### Framework - Utilities
| Class | Package | Description | Sync Status |
|-------|---------|-------------|-------------|
| `ObjectUtil` | `framework.utils` | Object utility methods | Should be identical |
| `Version` | `framework.utils` | Version handling | Should be identical |
| `InternalIdentifier` | `framework.common` | Internal identifier utility | Should be identical |

#### Common - Enums
| Class | Package | Description | Sync Status |
|-------|---------|-------------|-------------|
| `EntityAnimation` | `common.entity.enums` | Animation enum | Should be identical |
| `EntityAnimator` | `common.entity.enums` | Animator enum | Should be identical |
| `EntityModel` | `common.entity.enums` | Model enum | Should be identical |
| `EntityVariant` | `common.entity.enums` | Variant enum | Should be identical |

#### Common - Commands
| Class | Package | Description | Sync Status |
|-------|---------|-------------|-------------|
| `ColorArgumentType` | `common.commands` | Color command argument | Should be identical |
| `LovelyRobotCommands` | `common.commands` | Robot commands | Should be identical |

#### Common - Items
| Class | Package | Description | Sync Status |
|-------|---------|-------------|-------------|
| `LovelyCoreItem` | `common.items.custom` | Core item | Verify tooltip rendering |
| `LovelySpawnItem` | `common.items.custom` | Spawn egg item | Verify spawn mechanics |
| `TooltipUtils` | `common.items.utils` | Tooltip utilities | Verify text component APIs |

#### Common - Recipes
| Class | Package | Description | Sync Status |
|-------|---------|-------------|-------------|
| `LovelySpawnRecipe` | `common.recipes` | Spawn recipe | Should be identical |
| `LovelySpawnRecipeSerializer` | `common.recipes` | Spawn recipe serializer | Should be identical |
| `LovelySpawnDyeRecipe` | `common.recipes` | Dye recipe | Should be identical |
| `LovelySpawnDyeRecipeSerializer` | `common.recipes` | Dye recipe serializer | Should be identical |

#### Common - Shared
| Class | Package | Description | Sync Status |
|-------|---------|-------------|-------------|
| `LovelyIdentifier` | `common.shared` | Identifier wrapper | Verify ResourceLocation creation |
| `LovelyResource` | `common.shared` | Resource wrapper | Verify ResourceLocation usage |
| `Utility` | `common.utils.internal` | General utilities | Should be identical |

#### Lib - Entity Type Features
| Class | Package | Description | Sync Status |
|-------|---------|-------------|-------------|
| `InternalEntityType` | `lib.entity.type` | Internal entity type system | Verify entity type creation |
| `AnimationFeature` | `lib.entity.type.features` | Animation feature | Should be identical |
| `CombatLevelFeature` | `lib.entity.type.features` | Combat level feature | Should be identical |
| `EnchantmentFeature` | `lib.entity.type.features` | Enchantment feature | Should be identical |
| `FoodFeature` | `lib.entity.type.features` | Food feature | Should be identical |
| `LevelFeature` | `lib.entity.type.features` | Level feature | Should be identical |
| `ProtectionFeature` | `lib.entity.type.features` | Protection feature | Should be identical |
| `SoundFeature` | `lib.entity.type.features` | Sound feature | Verify sound event APIs |
| `SpawnFeature` | `lib.entity.type.features` | Spawn feature | Should be identical |
| `TextureVariantFeature` | `lib.entity.type.features` | Texture variant feature | Should be identical |

#### Lib - Items
| Class | Package | Description | Sync Status |
|-------|---------|-------------|-------------|
| `InternalItems` | `lib.items` | Internal item utilities | Verify item property registration |
| `InternalItemsGroup` | `lib.items` | Internal item group utilities | Verify creative tab APIs |

#### Lib - Recipes
| Class | Package | Description | Sync Status |
|-------|---------|-------------|-------------|
| `INbtModifier` | `lib.recipes.interfaces` | NBT modifier interface | Should be identical |
| `INbtTransferStrategy` | `lib.recipes.interfaces` | NBT transfer strategy interface | Should be identical |
| `DyeColorModifier` | `lib.recipes.modifiers` | Dye color modifier | Should be identical |
| `AdditiveNbtMergeStrategy` | `lib.recipes.strategies` | Additive NBT merge | Should be identical |
| `FullNbtCopyStrategy` | `lib.recipes.strategies` | Full NBT copy | Should be identical |

#### Lib - Utilities
| Class | Package | Description | Sync Status |
|-------|---------|-------------|-------------|
| `IReadWriteNBT` | `lib.utils.interfaces` | NBT read/write interface | Should be identical |

---

## Detailed Cleanup Roadmap

### Phase 1: Configuration System (HIGH PRIORITY)
**Goal**: Unify configuration approach or clearly separate loader-specific implementations

1. **LovelyConfigs** - 4-6 hours
   - Fabric: Uses custom `SimpleConfig` + `ConfigProvider`
   - Forge: Uses `ForgeConfigSpec` with builder pattern
   - **Decision Needed**: Keep separate or create abstraction layer?
   - **Recommendation**: Keep separate due to fundamental API differences, but ensure identical config values and structure

### Phase 2: Registration Systems (HIGH PRIORITY)
**Goal**: Ensure identical registration outcomes despite different APIs

2. **LovelyEntities** - 2-3 hours
   - Verify all entity types registered in both loaders
   - Ensure attributes match exactly
   - Check spawn egg registration

3. **LovelyItems** - 2-3 hours
   - Verify all items registered in both loaders
   - Ensure item properties match
   - Check creative tab assignment

4. **LovelyGroups** - 2-3 hours
   - Verify creative tabs match
   - Ensure item ordering is consistent
   - Check tab icons

5. **LovelyRecipes** - 1-2 hours
   - Verify recipe serializers registered
   - Ensure recipe logic is identical

### Phase 3: Event Systems (HIGH PRIORITY)
**Goal**: Ensure identical gameplay behavior despite different event APIs

6. **LovelyEvents** - 3-4 hours
   - Map Fabric events to Forge events
   - Verify all event handlers present in both
   - Test event firing timing

7. **LovelyCommands** - 1-2 hours
   - Verify command registration timing
   - Ensure commands work identically

### Phase 4: Entity System (MEDIUM PRIORITY)
**Goal**: Ensure entities behave identically

8. **Entity Base Classes** - 4-6 hours total
   - `InternalEntity`, `InternalLogic`, `InternalAnimation`
   - Verify attribute system
   - Check NBT serialization
   - Test networking (client-server sync)

9. **Robot Implementations** - 2-3 hours total
   - `LovelyRobot`, `VanillaEntity`, `Bunny2Entity`
   - Verify behavior consistency
   - Check AI goal registration

10. **Rendering** - 2-3 hours total
    - All Model, Layer, Renderer classes
    - Verify GeckoLib integration
    - Check texture loading

### Phase 5: Framework & Library (LOW PRIORITY)
**Goal**: Verify pure logic classes are identical

11. **Data Structures** - 2-3 hours total
    - All stats classes, NBT serialization
    - Run diff tools to find discrepancies

12. **Strategy Classes** - 1-2 hours total
    - Combat, enchantment, protection strategies
    - Should be byte-for-byte identical

13. **Enums** - 30 minutes
    - Quick verification all enums match

14. **Utilities** - 1 hour
    - Verify utility methods identical

### Phase 6: Items & Recipes (MEDIUM PRIORITY)
**Goal**: Ensure crafting and items work identically

15. **Item Classes** - 2-3 hours total
    - `LovelyCoreItem`, `LovelySpawnItem`
    - Verify tooltips render correctly
    - Check item behavior

16. **Recipe Classes** - 1-2 hours total
    - Verify recipe logic identical
    - Test in-game crafting

---

## Synchronization Checklist

### For Each Class Pair:
- [ ] Open both Fabric and Forge versions side-by-side
- [ ] Compare imports - note loader-specific APIs
- [ ] Compare class structure (fields, methods, inner classes)
- [ ] Compare JavaDoc documentation
- [ ] Identify loader-specific code blocks
- [ ] Verify business logic is identical
- [ ] Check for version-specific workarounds
- [ ] Update documentation to match
- [ ] Test in-game behavior

### Red Flags to Watch For:
- Different field initialization order
- Different method signatures
- Missing methods in one loader
- Different constant values
- Different NBT key names
- Different networking approaches
- Different event handling
- Different resource location formats

---

## Testing Strategy

### Unit Testing
- Framework classes should have identical unit tests
- Strategy classes should produce identical outputs
- Data serialization should be cross-compatible

### Integration Testing
- Spawn robots in both loaders - verify identical stats
- Test all AI behaviors - should look identical
- Test crafting recipes - should work identically
- Test commands - should behave identically
- Test config changes - should affect both identically

### Cross-Loader Compatibility
- NBT data should be readable by both loaders
- Config structure should be equivalent
- Save data should be compatible

---

## Estimated Total Time

| Phase | Estimated Time |
|-------|---------------|
| Phase 1: Configuration | 4-6 hours |
| Phase 2: Registration | 8-12 hours |
| Phase 3: Events | 4-6 hours |
| Phase 4: Entities | 8-12 hours |
| Phase 5: Framework | 4-6 hours |
| Phase 6: Items/Recipes | 3-5 hours |
| **Total** | **31-47 hours** |

---

## Notes

### Loader-Specific Patterns to Preserve

**Fabric Patterns:**
- Direct registry access via `Registry.register()`
- Fabric API event system
- Custom config system (SimpleConfig)
- Mixin usage for recipe access

**Forge Patterns:**
- DeferredRegister for registration
- EventBus system (@SubscribeEvent)
- ForgeConfigSpec for configuration
- Event-based lifecycle

### Common Pitfalls
1. **Timing Issues**: Forge and Fabric have different initialization phases
2. **Event Differences**: Same gameplay event, different API
3. **Registry Differences**: Same outcome, different registration method
4. **Config Reload**: Forge has built-in reload, Fabric needs manual implementation

### Maintenance Strategy
- Keep loader-specific code in `source/` package
- Keep shared logic in `common/`, `framework/`, `lib/` packages
- Document loader differences in class JavaDoc
- Use consistent naming across loaders
- Maintain parallel test suites

---

**End of Catalog**
