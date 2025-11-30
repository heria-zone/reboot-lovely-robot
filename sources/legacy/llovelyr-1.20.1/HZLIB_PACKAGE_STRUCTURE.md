# HZLib EntityType Package Structure

## Overview

This document describes the package structure for the HZLib EntityType Architecture implemented in Legacy LovelyRobot 1.20.1. This structure is designed for easy extraction into an external HZLib library in the future.

## Package Hierarchy

### Framework Layer (Pure Java)
**Package**: `net.msymbios.llovelyr.framework.entity.type`

**Purpose**: Pure Java data structures with zero Minecraft dependencies for maximum portability and testability.

**Location**:
- Fabric: `sources/legacy/llovelyr-1.20.1/Fabric/src/main/java/net/msymbios/llovelyr/framework/entity/type/`
- Forge: `sources/legacy/llovelyr-1.20.1/Forge/src/main/java/net/msymbios/llovelyr/framework/entity/type/`

**Components** (to be implemented):
- `EntityTypeData` - Container for combat stats
- `ResourceMap<K, V>` - Generic resource mapping with caching
- `TextureVariant` - Enum for texture types
- `ModelVariant` - Enum for model types
- `AnimatorVariant` - Enum for animator types

**Extraction Notes**:
- This layer has ZERO Minecraft dependencies
- Can be extracted as-is to external library
- Identical implementation across Fabric and Forge

### Lib Core Layer (Loader-Specific)
**Package**: `net.msymbios.llovelyr.lib.entity.type`

**Purpose**: Base entity type with Minecraft integration, providing feature system and resource management.

**Location**:
- Fabric: `sources/legacy/llovelyr-1.20.1/Fabric/src/main/java/net/msymbios/llovelyr/lib/entity/type/`
- Forge: `sources/legacy/llovelyr-1.20.1/Forge/src/main/java/net/msymbios/llovelyr/lib/entity/type/`

**Components** (to be implemented):
- `InternalEntityType<T>` - Abstract base class with feature system

**Extraction Notes**:
- Requires loader-specific implementations (Fabric Identifier vs Forge ResourceLocation)
- Public API should remain identical across loaders
- Minimal loader-specific dependencies

### Feature Modules Layer
**Package**: `net.msymbios.llovelyr.lib.entity.type.features`

**Purpose**: Optional, composable functionality that can be attached to entity types.

**Location**:
- Fabric: `sources/legacy/llovelyr-1.20.1/Fabric/src/main/java/net/msymbios/llovelyr/lib/entity/type/features/`
- Forge: `sources/legacy/llovelyr-1.20.1/Forge/src/main/java/net/msymbios/llovelyr/lib/entity/type/features/`

**Components** (to be implemented):
- `LevelFeature` - Leveling system with configurable max levels and XP strategies
- `FoodFeature` - Food items and tempting items management
- `SoundFeature` - Sound event mapping for entity actions
- `AnimationFeature` - GeckoLib animation management
- `SpawnFeature` - Spawn configuration (weight, group size, biomes)
- `TextureVariantFeature` - Additional texture variants management

**Extraction Notes**:
- Most features are pure Java or have minimal Minecraft dependencies
- Can be extracted with minimal modifications
- Identical implementation across loaders where possible

## Mod Implementation Layer
**Package**: `net.msymbios.llovelyr.common.entity.type` and `net.msymbios.llovelyr.source.entity.type`

**Purpose**: Robot-specific implementations using HZLib base + features.

**Components** (to be implemented):
- `RobotEntityType` - Robot-specific entity type with 16-color palette
- `NativeRobotType` - Registry of robot types with config integration

**Extraction Notes**:
- This layer stays in the mod
- Uses HZLib as a dependency after extraction

## Extraction Strategy

### Phase 1: Validate in Legacy 1.20.1
1. Implement all layers directly in Legacy 1.20.1 codebase
2. Test thoroughly with both Fabric and Forge
3. Validate architecture works in real-world context
4. Identify any issues or needed adjustments

### Phase 2: Extract Framework Layer
1. Create external HZLib project
2. Copy `framework/entity/type/` package
3. Verify zero Minecraft dependencies
4. Create unit tests
5. Publish as library artifact

### Phase 3: Extract Lib Core Layer
1. Create loader-specific modules in HZLib
2. Copy `lib/entity/type/` package (excluding features)
3. Adjust imports to use HZLib framework
4. Maintain identical public API across loaders
5. Publish loader-specific artifacts

### Phase 4: Extract Feature Modules
1. Copy `lib/entity/type/features/` package
2. Adjust imports to use HZLib framework and lib
3. Test feature composability
4. Publish as part of lib artifacts

### Phase 5: Update Legacy 1.20.1
1. Add HZLib as dependency
2. Remove extracted code
3. Update imports to use HZLib
4. Verify functionality unchanged
5. Keep mod-specific implementations (RobotEntityType, NativeRobotType)

## Directory Structure for Future HZLib

```
hzlib/
├── hzlib-framework/              [Pure Java, no dependencies]
│   └── src/main/java/
│       └── net/msymbios/hzlib/framework/entity/type/
│           ├── EntityTypeData.java
│           ├── ResourceMap.java
│           ├── TextureVariant.java
│           ├── ModelVariant.java
│           └── AnimatorVariant.java
│
├── hzlib-fabric/                 [Fabric-specific]
│   └── src/main/java/
│       └── net/msymbios/hzlib/lib/entity/type/
│           ├── InternalEntityType.java
│           └── features/
│               ├── LevelFeature.java
│               ├── FoodFeature.java
│               ├── SoundFeature.java
│               ├── AnimationFeature.java
│               ├── SpawnFeature.java
│               └── TextureVariantFeature.java
│
└── hzlib-forge/                  [Forge-specific]
    └── src/main/java/
        └── net/msymbios/hzlib/lib/entity/type/
            ├── InternalEntityType.java
            └── features/
                ├── LevelFeature.java
                ├── FoodFeature.java
                ├── SoundFeature.java
                ├── AnimationFeature.java
                ├── SpawnFeature.java
                └── TextureVariantFeature.java
```

## Design Principles

### Separation of Concerns
- **Framework**: Pure data structures, no game logic
- **Lib**: Game integration, minimal loader dependencies
- **Features**: Composable functionality, independent modules
- **Mod**: Specific implementations, uses HZLib as foundation

### Portability
- Framework layer is 100% portable (pure Java)
- Lib layer has minimal loader-specific code
- Features are mostly portable with small adjustments
- Clear boundaries enable easy extraction

### Maintainability
- Identical public APIs across loaders
- Clear package hierarchy
- Well-documented extraction strategy
- Tested in real-world context before extraction

## References

- **Architecture Document**: `docs/development/HZLib-EntityType-Architecture.md`
- **Design Document**: `.kiro/specs/hzlib-entitytype-legacy-1-20-1/design.md`
- **Requirements Document**: `.kiro/specs/hzlib-entitytype-legacy-1-20-1/requirements.md`
- **Implementation Tasks**: `.kiro/specs/hzlib-entitytype-legacy-1-20-1/tasks.md`

## Version History

- **2025-11-29**: Initial package structure created for Legacy 1.20.1
  - Created framework/entity/type/ packages (Fabric & Forge)
  - Created lib/entity/type/ packages (Fabric & Forge)
  - Created lib/entity/type/features/ packages (Fabric & Forge)
  - Documented extraction strategy
