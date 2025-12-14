# ADR 002: Lovely Lib Design Decisions

**Status**: Accepted  
**Date**: 2025-12-13  
**Decision Makers**: Solo Developer  
**Consulted**: Legacy 1.21.1 Codebase Analysis  
**Related**: [ADR_001_Library_Architecture_Strategy.md](ADR_001_Library_Architecture_Strategy.md)

## Context

With the Legacy 1.21.1 codebase complete and featuring all planned robot functionality, we need to extract robot-specific shared code into Lovely Lib. The Legacy codebase already has a well-organized structure with `lib/` and `framework/` packages that appear designed for extraction. We need to determine exactly what goes into Lovely Lib, what stays in Legacy, and how to enable cross-variant robot conversion.

## Decision

### Lovely Lib Scope Definition

**Lovely Lib will contain robot-specific shared functionality that enables:**
1. **Cross-variant robot conversion** (primary goal)
2. **Shared robot behavior patterns** across Tribute, Legacy, and Reboot
3. **Common robot infrastructure** (entities, items, recipes, rendering)
4. **Robot-specific utilities** that are not general-purpose

### Package Structure for Lovely Lib

```
net.msymbios.lovelylib/
├── entity/
│   ├── base/                    [BaseRobotEntity - abstract base class]
│   ├── data/                    [Robot data management, NBT handling]
│   ├── features/                [Modular robot features (pickup, drop, loot)]
│   └── helpers/                 [Entity spawn, validation, conversion helpers]
├── items/
│   ├── base/                    [Base robot items (cores, spawn items)]
│   └── helpers/                 [Item spawn, NBT transfer helpers]
├── recipes/
│   ├── base/                    [NBT transfer recipe system]
│   ├── interfaces/              [Recipe interfaces and contracts]
│   ├── modifiers/               [Data modification strategies]
│   ├── serializers/             [Recipe serialization]
│   ├── strategies/              [Transfer strategies (copy, merge, etc.)]
│   └── utils/                   [Recipe utilities]
├── animation/
│   ├── definitions/             [Animation state definitions]
│   ├── controllers/             [Base animation controllers]
│   └── managers/                [Animation state management]
├── rendering/
│   ├── layers/                  [Render layer system (color, emissive, overlay)]
│   ├── context/                 [Render context management]
│   └── interfaces/              [Rendering contracts]
├── registry/
│   ├── managers/                [Robot registry management]
│   └── data/                    [Registry saved data]
├── conversion/                  [NEW - Cross-variant conversion system]
│   ├── interfaces/              [Conversion contracts]
│   ├── strategies/              [Variant-specific conversion logic]
│   └── registry/                [Conversion registry and mapping]
└── services/                    [Platform abstraction services]
```

### What Goes Into Lovely Lib

#### From `lib/` Package (Direct Migration)
**Entity System:**
- `lib/entity/base/BaseRobotEntity.java` → `lovelylib/entity/base/`
- `lib/entity/data/` → `lovelylib/entity/data/`
- `lib/entity/features/` → `lovelylib/entity/features/`
- `lib/entity/helpers/` → `lovelylib/entity/helpers/`

**Items System:**
- `lib/items/base/` → `lovelylib/items/base/`
- `lib/items/helpers/` → `lovelylib/items/helpers/`

**Recipe System (Complete):**
- `lib/recipes/` → `lovelylib/recipes/` (entire package)
  - Base classes, interfaces, modifiers, serializers, strategies, utils

**Animation System:**
- `lib/animation/` → `lovelylib/animation/`
  - AnimationDefinitions, AnimationStateManager, BaseAnimationController, BoneTransformations

**Rendering System:**
- `lib/rendering/` → `lovelylib/rendering/`
  - All render layers, context management, interfaces

**Registry System:**
- `lib/registry/` → `lovelylib/registry/`
  - RobotRegistryManager, RobotRegistrySavedData

**Services:**
- `lib/services/` → `lovelylib/services/`
  - Platform abstraction layer

#### From `framework/` Package (Selective Migration)
**Entity Framework:**
- `framework/entity/data/` → `lovelylib/entity/data/` (merge with lib/entity/data)
- `framework/entity/enums/` → `lovelylib/entity/enums/`
- `framework/entity/type/` → `lovelylib/entity/types/`
- `framework/registry/` → `lovelylib/registry/` (merge with lib/registry)

**Utilities:**
- `framework/utils/ObjectUtil.java` → `lovelylib/utils/` (robot-specific utilities only)
- `framework/utils/Version.java` → Stay in Legacy (mod-specific)

#### New Cross-Variant Conversion System
**Conversion Package (NEW):**
```java
// Conversion interfaces
public interface IRobotConverter {
    boolean canConvert(EntityVariant from, EntityVariant to);
    ConversionResult convert(BaseRobotEntity robot, EntityVariant targetVariant);
}

// Conversion strategies
public class TributeToLegacyConverter implements IRobotConverter { ... }
public class LegacyToRebootConverter implements IRobotConverter { ... }
public class RebootToTributeConverter implements IRobotConverter { ... }

// Conversion registry
public class RobotConversionRegistry {
    public static void registerConverter(EntityVariant from, EntityVariant to, IRobotConverter converter);
    public static ConversionResult convertRobot(BaseRobotEntity robot, EntityVariant targetVariant);
}
```

### What Stays in Legacy

#### Mod-Specific Implementation
**Common Package (Legacy-Specific):**
- `common/entity/` → Legacy-specific entity implementations
- `common/items/custom/` → Legacy-specific items (7 robot types)
- `common/commands/` → Legacy command system
- `common/Configs/` → Legacy configuration
- `common/utils/` → Legacy-specific utilities
- `common/shared/` → Legacy identifiers and resources

**Framework Remnants:**
- `framework/common/InternalIdentifier.java` → Legacy-specific
- `framework/entity/combat/` → Legacy combat system
- `framework/entity/enchantment/` → Legacy enchantment system
- `framework/entity/protection/` → Legacy protection system

#### Legacy-Specific Features
- **7 Robot Types**: Vanilla, Honey, Bunny, Bunny2, Dragon, Neko, Kitsune
- **16-Color System**: Legacy-specific color implementation
- **Level 200 System**: Legacy-specific leveling
- **Protection System**: Enchanted book feeding, adaptive protection
- **Command System**: Legacy-specific admin commands
- **Configuration**: Legacy-specific config options

### Robot Conversion System Design

#### Conversion Process
1. **Extract Robot Data**: Get all robot stats, level, experience, protections, name, owner
2. **Map Variant Differences**: Handle stat scaling, feature availability, level caps
3. **Create Target Robot**: Spawn new robot of target variant with converted data
4. **Transfer Ownership**: Maintain owner relationship and registry entries
5. **Remove Source Robot**: Clean up original robot and update registries

#### Conversion Mapping Examples
```java
// Tribute (4 types) → Legacy (7 types)
TributeVariant.VANILLA → LegacyVariant.VANILLA (direct mapping)
TributeVariant.HONEY → LegacyVariant.HONEY (direct mapping)
TributeVariant.BUNNY → LegacyVariant.BUNNY (direct mapping, could also map to BUNNY2)
// No Dragon, Neko, Kitsune in Tribute - conversion creates with base stats

// Legacy (7 types) → Reboot (7+ types with advanced features)
LegacyVariant.DRAGON → RebootVariant.DRAGON (stat scaling for advanced features)
LegacyVariant.KITSUNE → RebootVariant.KITSUNE (tail progression starts at 1)

// Level/Stat Conversion
Legacy Level 200 → Tribute Level 100 (scale down: level / 2)
Tribute Level 100 → Reboot Level 500 (scale up: level * 5)
```

#### Conversion API
```java
// Usage in mods
@Override
public InteractionResult mobInteract(Player player, InteractionHand hand) {
    ItemStack itemStack = player.getItemInHand(hand);
    
    // Check if holding conversion item (e.g., Nether Star)
    if (itemStack.is(Items.NETHER_STAR)) {
        // Determine target variant (could be from NBT, config, or UI)
        EntityVariant targetVariant = getTargetVariantFromItem(itemStack);
        
        // Attempt conversion
        ConversionResult result = RobotConversionRegistry.convertRobot(this, targetVariant);
        
        if (result.isSuccess()) {
            // Handle successful conversion
            player.sendSystemMessage(Component.literal("Robot converted to " + targetVariant.getName()));
            return InteractionResult.SUCCESS;
        } else {
            // Handle conversion failure
            player.sendSystemMessage(Component.literal("Conversion failed: " + result.getErrorMessage()));
            return InteractionResult.FAIL;
        }
    }
    
    return super.mobInteract(player, hand);
}
```

### Multi-Loader Considerations

#### Platform Services
```java
// Platform-specific implementations
public interface IPlatformServices {
    // Entity registration
    void registerEntityType(String name, EntityType<?> entityType);
    
    // Item registration  
    void registerItem(String name, Item item);
    
    // Recipe registration
    void registerRecipeType(RecipeType<?> recipeType);
    void registerRecipeSerializer(RecipeSerializer<?> serializer);
    
    // Animation support
    boolean isGeckoLibAvailable();
    void registerAnimationController(String name, Object controller);
}
```

#### Loader-Specific Implementations
- **Fabric**: Constructor injection for items, direct GeckoLib integration
- **Forge**: Supplier-based item resolution, event-driven registration
- **NeoForge**: Similar to Forge but with NeoForge-specific APIs

### API Design Principles

#### 1. Backward Compatibility
- Existing Legacy robots continue to work without changes
- Conversion is opt-in, not automatic
- Legacy-specific features remain in Legacy mod

#### 2. Forward Compatibility
- API designed to support future robot variants
- Extensible conversion system
- Modular feature system

#### 3. Data Integrity
- Conversion validates all data before transfer
- Failed conversions don't corrupt source robot
- Comprehensive error reporting and recovery

#### 4. Performance
- Conversion is one-time operation, not continuous
- Registry operations are optimized for lookup speed
- Minimal overhead for non-converting robots

## Consequences

### Positive
- **Cross-Variant Conversion**: Players can convert robots between mod variants
- **Code Reuse**: 60-70% reduction in duplicate code across variants
- **Consistent Behavior**: Shared robot behavior patterns across all variants
- **Easier Maintenance**: Bug fixes and improvements benefit all variants
- **Extensibility**: New robot variants can leverage existing infrastructure

### Negative
- **Complexity**: Additional abstraction layer increases complexity
- **Migration Effort**: Significant work to extract and refactor existing code
- **API Stability**: Changes to Lovely Lib affect all dependent mods
- **Testing Burden**: Must test all conversion combinations
- **Version Coordination**: Library updates must be coordinated across variants

### Risks
- **Conversion Bugs**: Data loss or corruption during robot conversion
- **API Breaking Changes**: Library updates could break dependent mods
- **Performance Impact**: Additional abstraction could impact performance
- **Compatibility Issues**: Different MC versions may have API conflicts

## Alternatives Considered

### Alternative 1: No Cross-Variant Conversion
**Rejected**: This was the primary goal for library extraction. Without conversion capability, the value proposition is significantly reduced.

### Alternative 2: Simple Data Export/Import
**Rejected**: Would require manual file management and wouldn't provide seamless in-game experience.

### Alternative 3: Conversion via External Tool
**Rejected**: Poor user experience and doesn't leverage the shared library architecture.

### Alternative 4: Full Robot Unification
**Rejected**: Would eliminate variant-specific features and uniqueness that players value.

## Implementation Plan

### Phase 1: Core Library Extraction (Sprint 07)
1. Extract `lib/` package to Lovely Lib
2. Extract selected `framework/` components
3. Update Legacy to use Lovely Lib dependency
4. Ensure no regression in Legacy functionality

### Phase 2: Conversion System (Sprint 08)
1. Design and implement conversion interfaces
2. Create basic conversion strategies
3. Implement conversion registry
4. Add conversion API to BaseRobotEntity

### Phase 3: Variant Integration (Sprint 08-09)
1. Create Tribute environment with Lovely Lib
2. Create Reboot environment with Lovely Lib
3. Implement variant-specific conversion logic
4. Test cross-variant conversion scenarios

### Phase 4: Polish and Documentation (Sprint 09)
1. Comprehensive testing of all conversion paths
2. Error handling and edge case management
3. API documentation and usage examples
4. Performance optimization

## Success Criteria

- [ ] Legacy 1.21.1 functions identically after Lovely Lib extraction
- [ ] Tribute and Reboot environments successfully use Lovely Lib
- [ ] Cross-variant robot conversion works for all supported combinations
- [ ] No data loss or corruption during conversion
- [ ] API is well-documented and easy to use
- [ ] Performance impact is minimal (<5% overhead)
- [ ] All tests pass for conversion scenarios

---

**Key Principle**: Lovely Lib enables cross-variant robot conversion while maintaining each variant's unique identity and features. The library provides shared infrastructure without forcing uniformity.