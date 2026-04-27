# ADR 009: Entity Hierarchy Refactoring for Multi-Mod Support

**Status**: In Progress
**Date**: 2025-12-23
**Decision Makers**: Development Team
**Consulted**: Architecture Review

## Context

The current HZLib InternalEntityType provides a good foundation for entity type definitions, but we need to support two distinct entity categories:

1. **Robot Entities** (Lovely Robot mods): Require level-up systems, enchantment protection, combat stats, experience tracking, and robot-specific behaviors
2. **Monster Entities** (Monsters & Girls mod): Require different stat systems, belly mechanics, state management, and monster-specific behaviors

Currently, all robot-specific functionality is embedded in the Legacy InternalEntity, making it unsuitable for general-purpose use in HZLib. We need to refactor the entity hierarchy to support both robot and monster entities while maintaining the flexibility of the new variant system.

## Decision

We will implement a three-tier entity hierarchy:

```
HZLib InternalEntity (Generic Base)
├── HZLib RobotEntity (Robot-Specific)
│   └── LovelyLib LovelyRobotEntity (Lovely Robot Implementation)
└── HZLib MonsterEntity (Monster-Specific)
    └── MonstersLib MonstersEntity (Monsters & Girls Implementation)
```

### Architecture Overview

#### Tier 1: HZLib InternalEntity (Generic Base)
- **Purpose**: Provides common entity functionality for all HZLib-based mods
- **Features**: Basic variant system integration, NBT handling, common entity behaviors
- **Location**: `sources/common/hzlib-1.21.1/Common/src/main/java/net/heriazone/hzlib/api/entity/InternalEntity.java`

#### Tier 2: HZLib RobotEntity (Robot-Specific)
- **Purpose**: Adds robot-specific functionality (levels, enchantments, protection, combat stats)
- **Features**: Level-up system, enchantment protection, combat stats, experience tracking
- **Location**: `sources/common/hzlib-1.21.1/Common/src/main/java/net/heriazone/hzlib/api/entity/RobotEntity.java`

#### Tier 3: LovelyLib LovelyRobotEntity (Implementation)
- **Purpose**: Replaces current Legacy InternalEntity with LovelyLib-based implementation
- **Features**: Lovely Robot specific behaviors, AI goals, interaction patterns
- **Location**: `sources/common/lovelylib-1.21.1/Common/src/main/java/net/heriazone/lovelylib/api/entity/LovelyRobotEntity.java`

#### Tier 2: HZLib MonsterEntity (Monster-Specific)
- **Purpose**: Adds monster-specific functionality (belly system, states, monster behaviors)
- **Features**: Belly mechanics, entity states, monster-specific interactions
- **Location**: `sources/common/hzlib-1.21.1/Common/src/main/java/net/heriazone/hzlib/api/entity/MonsterEntity.java`

## Implementation Plan

### Phase 6.1: Create HZLib InternalEntity (Generic Base)

**Objective**: Create the generic base entity class that provides common functionality.

**Features to Include**:
- Basic variant system integration (texture, model, animator)
- Common NBT handling patterns
- Basic entity data synchronization
- Common interaction patterns
- Integration with InternalEntityType

**Features to Exclude** (moved to specialized classes):
- Level-up systems
- Enchantment protection
- Combat stats tracking
- Experience tracking
- Robot-specific behaviors
- Monster-specific behaviors

**Implementation Steps**:
1. Create `HZLib InternalEntity` class extending `TamableAnimal`
2. Add basic EntityDataAccessor fields (TEXTURE_ID, MODEL_ID, ANIMATOR_ID)
3. Implement variant system integration using new variant features
4. Add basic NBT serialization/deserialization
5. Implement common interaction patterns
6. Add integration with InternalEntityType for resource access

### Phase 6.2: Create HZLib RobotEntity (Robot-Specific)

**Objective**: Create robot-specific entity class that adds robot functionality.

**Features to Add**:
- Level-up system with experience tracking
- Enchantment protection system (fire, fall, blast, projectile)
- Combat stats management (level, health, attack, defense)
- Experience tracker for preventing infinite exp gain
- Robot-specific EntityDataAccessor fields
- Robot-specific NBT handling

**Robot-Specific Fields**:
```java
// Robot-specific data accessors
protected static final EntityDataAccessor<Integer> ROBOT_LEVEL = SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.INT);
protected static final EntityDataAccessor<Float> EXPERIENCE_POINTS = SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.FLOAT);
protected static final EntityDataAccessor<Float> MAX_HEALTH_POINTS = SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.FLOAT);
protected static final EntityDataAccessor<Float> ATTACK_DAMAGE = SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.FLOAT);
protected static final EntityDataAccessor<Float> DEFENSE_VALUE = SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.FLOAT);

// Protection Stats
protected static final EntityDataAccessor<Integer> FIRE_PROTECTION = SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.INT);
protected static final EntityDataAccessor<Integer> FALL_PROTECTION = SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.INT);
protected static final EntityDataAccessor<Integer> BLAST_PROTECTION = SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.INT);
protected static final EntityDataAccessor<Integer> PROJECTILE_PROTECTION = SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.INT);

// Enchantment Stats
protected static final EntityDataAccessor<Integer> LOOTING_LEVEL = SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.INT);
protected static final EntityDataAccessor<Integer> SHARPNESS_LEVEL = SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.INT);
protected static final EntityDataAccessor<Integer> KNOCKBACK_LEVEL = SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.INT);

// Robot-specific stat objects (using existing data classes)
protected CombatLevelStats combatStats = new CombatLevelStats();
protected ProtectionStats protectionStats = new ProtectionStats();
protected EnchantmentStats enchantmentStats = new EnchantmentStats();
```

**Implementation Steps**:
1. Create `HZLib RobotEntity` class extending `HZLib InternalEntity`
2. Add robot-specific EntityDataAccessor fields
3. Implement level-up system with experience calculations
4. Add enchantment protection system
5. Implement combat stats management
6. Add experience tracker functionality
7. Implement robot-specific NBT handling
8. Add robot-specific interaction patterns

### Phase 6.3: Create HZLib MonsterEntity (Monster-Specific)

**Objective**: Create monster-specific entity class based on Monsters & Girls requirements.

**Features to Add** (from Monsters & Girls InternalEntity):
- Belly system (HAS_BELLY, belly states)
- Entity state management (REST, MOVE, etc.)
- Monster-specific preferences (planting, sound, notifications)
- Monster-specific interaction patterns
- Texture cycling with belly levels

**Monster-Specific Fields**:
```java
// Monster-specific data accessors
protected static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(MonsterEntity.class, EntityDataSerializers.INT);
protected static final EntityDataAccessor<Boolean> HAS_BELLY = SynchedEntityData.defineId(MonsterEntity.class, EntityDataSerializers.BOOLEAN);
protected static final EntityDataAccessor<Boolean> IS_PLANTING_ENABLED = SynchedEntityData.defineId(MonsterEntity.class, EntityDataSerializers.BOOLEAN);
protected static final EntityDataAccessor<Boolean> IS_SOUND_ENABLED = SynchedEntityData.defineId(MonsterEntity.class, EntityDataSerializers.BOOLEAN);
protected static final EntityDataAccessor<Boolean> IS_NOTIFICATION_ON = SynchedEntityData.defineId(MonsterEntity.class, EntityDataSerializers.BOOLEAN);

// Monster-specific behavior fields
protected boolean canPlant = true;
protected boolean hasEffects = false;
```

**Implementation Steps**:
1. Create `HZLib MonsterEntity` class extending `HZLib InternalEntity`
2. Add monster-specific EntityDataAccessor fields
3. Implement belly system mechanics
4. Add entity state management
5. Implement monster-specific preferences
6. Add texture cycling with belly levels
7. Implement monster-specific NBT handling
8. Add monster-specific interaction patterns

### Phase 6.4: Create LovelyLib LovelyRobotEntity

**Objective**: Create the LovelyLib implementation that replaces Legacy InternalEntity.

**Features to Add**:
- Lovely Robot specific behaviors
- AI goal integration
- Lovely Robot specific interaction patterns
- Integration with LovelyLib systems

**Implementation Steps**:
1. Create `LovelyLib LovelyRobotEntity` class extending `HZLib RobotEntity`
2. Add Lovely Robot specific behaviors
3. Implement AI goal integration
4. Add Lovely Robot specific interaction patterns
5. Integrate with LovelyLib configuration system
6. Add Lovely Robot specific NBT extensions

### Phase 6.5: Update Legacy Project

**Objective**: Update Legacy project to use new LovelyRobotEntity instead of old InternalEntity.

**Implementation Steps**:
1. Update all Legacy entity classes to extend `LovelyRobotEntity`
2. Remove old InternalEntity class
3. Update imports and references
4. Test compilation and functionality
5. Validate that all robot functionality works correctly

## Mapping Strategy

### From Legacy InternalEntity to New Hierarchy

**Move to HZLib InternalEntity (Generic Base)**:
- Basic EntityDataAccessor fields (TEXTURE_ID, MODEL_ID, ANIMATOR_ID)
- Basic variant system integration
- Common NBT handling patterns
- Basic interaction framework
- Common entity behaviors

**Move to HZLib RobotEntity (Robot-Specific)**:
- Level-up system (`combatStats`, `expTracker`)
- Enchantment protection (`protectionStats`, `enchantmentStats`)
- Robot-specific EntityDataAccessor fields (LEVEL, EXPERIENCE, PROTECTION_*)
- Robot-specific NBT handlers (`combatStatsNBT`, `protectionStatsNBT`, `enchantmentStatsNBT`)
- Experience tracking logic
- Combat mode handling
- Auto-heal functionality

**Move to LovelyLib LovelyRobotEntity (Implementation)**:
- Lovely Robot specific behaviors
- AI goal integration
- Lovely Robot specific interaction patterns
- Integration with LovelyLib configuration

**Leave in Legacy Project**:
- Legacy-specific implementations
- Legacy-specific configuration integration
- Legacy-specific entity types

### From Monsters & Girls InternalEntity to MonsterEntity

**Move to HZLib MonsterEntity**:
- Belly system (HAS_BELLY, belly mechanics)
- Entity state management (STATE, EntityState enum integration)
- Monster preferences (IS_PLANTING_ENABLED, IS_SOUND_ENABLED, IS_NOTIFICATION_ON)
- Texture cycling with belly levels
- Monster-specific interaction patterns
- Monster-specific NBT handling

## Mojang Mappings Migration

### Issue
The Monsters & Girls 1.20.4 code uses Fabric YARN mappings, but we're now using official Mojang mappings.

### Mapping Changes Required

**Class Name Changes**:
```java
// YARN → Mojang
TameableEntity → TamableAnimal
EntityData → EntityDataAccessor
TrackedData → EntityDataAccessor
TrackedDataHandlerRegistry → EntityDataSerializers
DataTracker → SynchedEntityData
NbtCompound → CompoundTag
```

**Method Name Changes**:
```java
// YARN → Mojang
initDataTracker() → defineSynchedData()
writeCustomDataToNbt() → addAdditionalSaveData()
readCustomDataFromNbt() → readAdditionalSaveData()
interactMob() → mobInteract()
```

**Package Changes**:
```java
// YARN → Mojang
net.minecraft.entity.data.* → net.minecraft.network.syncher.*
net.minecraft.nbt.* → net.minecraft.nbt.*
net.minecraft.text.* → net.minecraft.network.chat.*
```

### Migration Strategy
1. Create mapping conversion utility
2. Update all class references
3. Update all method references
4. Update all package imports
5. Test compilation and functionality

## Benefits

### Separation of Concerns
- **Generic Base**: Common entity functionality shared across all mods
- **Robot-Specific**: Robot functionality isolated from monster functionality
- **Monster-Specific**: Monster functionality isolated from robot functionality
- **Implementation**: Mod-specific implementations build on appropriate base

### Code Reuse
- **HZLib InternalEntity**: Reusable across all entity-based mods
- **HZLib RobotEntity**: Reusable across all robot-based mods
- **HZLib MonsterEntity**: Reusable across all monster-based mods
- **Reduced Duplication**: Common patterns implemented once

### Maintainability
- **Clear Boundaries**: Each tier has well-defined responsibilities
- **Focused Changes**: Changes to robot functionality don't affect monster functionality
- **Easier Testing**: Each tier can be tested independently
- **Better Documentation**: Each tier has focused documentation

### Extensibility
- **New Entity Types**: Easy to add new specialized entity types
- **Custom Implementations**: Mods can create custom implementations
- **Feature Composition**: Features can be mixed and matched
- **Future Growth**: Architecture supports future entity categories

## Risks and Mitigation

### Risk 1: Complex Migration
**Risk**: Moving from single InternalEntity to multi-tier hierarchy is complex
**Probability**: High
**Impact**: Medium
**Mitigation**: 
- Implement incrementally (one tier at a time)
- Maintain backward compatibility during transition
- Comprehensive testing at each step
- Clear migration documentation

### Risk 2: Performance Impact
**Risk**: Additional inheritance layers may impact performance
**Probability**: Low
**Impact**: Low
**Mitigation**:
- Profile performance during implementation
- Optimize hot paths if needed
- Use composition over inheritance where appropriate

### Risk 3: Mapping Migration Issues
**Risk**: YARN to Mojang mapping conversion may introduce bugs
**Probability**: Medium
**Impact**: Medium
**Mitigation**:
- Create comprehensive mapping conversion guide
- Test each converted class individually
- Use automated tools where possible
- Maintain reference implementations

### Risk 4: Breaking Changes
**Risk**: Refactoring may break existing functionality
**Probability**: Medium
**Impact**: High
**Mitigation**:
- Maintain backward compatibility interfaces
- Implement comprehensive test suite
- Gradual migration with fallback options
- Clear deprecation timeline

## Success Criteria

### Technical Success
- [ ] All three tiers compile without errors
- [ ] Robot functionality preserved in RobotEntity
- [ ] Monster functionality implemented in MonsterEntity
- [ ] Legacy project successfully migrated to LovelyRobotEntity
- [ ] All existing tests pass
- [ ] Performance impact < 5%

### Architectural Success
- [ ] Clear separation of concerns achieved
- [ ] Code duplication reduced by > 60%
- [ ] New entity types can be added easily
- [ ] Documentation covers all tiers
- [ ] Migration path documented

### Integration Success
- [ ] HZLib entities work with variant system
- [ ] LovelyLib integration successful
- [ ] Monsters & Girls functionality preserved
- [ ] Multi-loader compatibility maintained
- [ ] Configuration systems integrated

## Implementation Timeline

### Phase 6.1: HZLib InternalEntity (Generic Base)
**Duration**: 2-3 days
**Deliverables**: Generic base entity class with variant system integration

### Phase 6.2: HZLib RobotEntity (Robot-Specific)
**Duration**: 3-4 days
**Deliverables**: Robot-specific entity class with level/enchantment/protection systems

### Phase 6.3: HZLib MonsterEntity (Monster-Specific)
**Duration**: 2-3 days
**Deliverables**: Monster-specific entity class with belly/state systems

### Phase 6.4: LovelyLib LovelyRobotEntity
**Duration**: 2-3 days
**Deliverables**: LovelyLib implementation extending RobotEntity

### Phase 6.5: Legacy Project Migration
**Duration**: 2-3 days
**Deliverables**: Updated Legacy project using new hierarchy

**Total Duration**: 11-16 days
**Target Completion**: January 15, 2026

## Related Decisions

- **ADR_001**: Library Architecture Strategy - Establishes HZLib as general utilities
- **ADR_002**: Lovely Lib Design Decisions - Defines robot-specific library scope
- **ADR_003**: HZ Lib Scope Definition - Defines general utilities scope
- **Dynamic Variant System**: Provides foundation for entity variant management

## Future Considerations

### Additional Entity Types
- **NPCEntity**: For NPC-based mods
- **VehicleEntity**: For vehicle-based mods
- **PetEntity**: For pet-based mods

### Advanced Features
- **Behavior Trees**: Advanced AI behavior systems
- **Component System**: ECS-style component architecture
- **Event System**: Entity event handling framework
- **Networking**: Advanced client-server synchronization

### Performance Optimizations
- **Entity Pooling**: Reuse entity instances
- **Batch Processing**: Process multiple entities together
- **Lazy Loading**: Load entity data on demand
- **Caching**: Cache frequently accessed data

---

**Decision**: Implement three-tier entity hierarchy with HZLib InternalEntity as generic base, specialized RobotEntity and MonsterEntity classes, and mod-specific implementations building on appropriate bases.

**Rationale**: This approach provides clear separation of concerns, enables code reuse across multiple mods, maintains flexibility for future entity types, and supports both robot and monster entity requirements while preserving the benefits of the new variant system.