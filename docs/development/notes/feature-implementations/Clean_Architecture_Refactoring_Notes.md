# Clean Architecture Refactoring - Implementation Notes

**Date**: 2025-12-07
**Sprint**: SPRINT_01
**Status**: In Progress

## Overview

Refactoring 1.21.1 Legacy version to implement clean architecture with improved feature-based system, better data management, and enhanced command functionality.

## Completed Components

### Phase 1: Feature Classes ✓

#### PickupFeature
**Location**: `sources/legacy/llovelyr-1.21.1/Common/src/main/java/net/msymbios/llovelyr/lib/entity/features/PickupFeature.java`

**Purpose**: Defines item conversion when entity is picked up or captured.

**Key Features**:
- Configurable pickup item per entity type
- Flexible NBT packaging strategies (default, custom, or no stats)
- Entity-centric data interpretation (entity reads its own NBT)
- Support for instance-level overrides

**API**:
```java
// Create feature with default stat preservation
PickupFeature feature = new PickupFeature(Items.SPAWN_EGG);

// Create pickup item with entity data
ItemStack pickupItem = feature.createPickupItem(entity);

// Apply pickup data to spawned entity
feature.applyPickupData(itemStack, entity);
```

#### DropFeature
**Location**: `sources/legacy/llovelyr-1.21.1/Common/src/main/java/net/msymbios/llovelyr/lib/entity/features/DropFeature.java`

**Purpose**: Defines core item drop when entity dies.

**Key Features**:
- Configurable drop item and count
- Dynamic drop count via IntSupplier
- Optional stat preservation in dropped item
- Separate from loot drops (handled by LootDropFeature)

**API**:
```java
// Simple drop feature (1 item, stats preserved)
DropFeature feature = new DropFeature(Items.ROBOT_CORE);

// Dynamic drop count
DropFeature feature = new DropFeature(
    Items.ROBOT_CORE,
    () -> entity.getLevel() > 50 ? 2 : 1,
    true
);

// Create drop stack
ItemStack dropStack = feature.createDropStack();
```

#### LootDropFeature
**Location**: `sources/legacy/llovelyr-1.21.1/Common/src/main/java/net/msymbios/llovelyr/lib/entity/features/LootDropFeature.java`

**Purpose**: Defines additional loot drops on entity death with flexible configuration.

**Key Features**:
- Per-item configuration (count range, drop chance, conditions)
- Variety limiting to prevent loot explosion
- Builder pattern for easy configuration
- Support for conditional drops

**API**:
```java
// Build loot table
LootDropFeature feature = new LootDropFeature()
    .addLoot(Items.DIAMOND, 1, 3, 0.1f)  // 10% chance, 1-3 diamonds
    .addLoot(Items.EMERALD, 1, 1, 0.05f) // 5% chance, 1 emerald
    .withMaxVariety(3);                   // Max 3 different items

// Generate drops
List<ItemStack> drops = feature.generateDrops(random);
```

### Phase 4: Experience Tracking System ✓

#### ExperienceTracker
**Location**: `sources/legacy/llovelyr-1.21.1/Common/src/main/java/net/msymbios/llovelyr/lib/entity/data/ExperienceTracker.java`

**Purpose**: Prevents infinite exp gain from immortal entities by accumulating exp per entity and only awarding on death.

**Key Features**:
- Thread-safe accumulation using ConcurrentHashMap
- Time-based purging of stale entries (default 5 minutes)
- Prevents exp farming from invulnerable entities
- Automatic cleanup to prevent memory leaks

**API**:
```java
// Create tracker
ExperienceTracker tracker = new ExperienceTracker();

// Accumulate exp on hit
tracker.accumulateExp(targetEntityUUID, expAmount);

// Claim exp on entity death
int totalExp = tracker.claimExp(targetEntityUUID);

// Periodic cleanup (call every 20 ticks)
tracker.purgeStaleEntries();
```

**Usage Pattern**:
```java
// In handleAttackTarget()
expTracker.accumulateExp(target.getUUID(), expAmount);

// In entity death event handler
int accumulatedExp = expTracker.claimExp(deadEntity.getUUID());
robot.addExperience(accumulatedExp);
```

### Phase 5: Data Consolidation System ✓

#### EntityData
**Location**: `sources/legacy/llovelyr-1.21.1/Common/src/main/java/net/msymbios/llovelyr/lib/entity/data/EntityData.java`

**Purpose**: Consolidated container for all custom entity data under single "EntityData" NBT compound.

**Key Features**:
- Groups CombatStats, ProtectionStats, EnchantmentStats
- Versioned data format for future migrations
- Type-safe accessors
- Validation methods
- Deep copy support

**API**:
```java
// Create entity data
EntityData data = new EntityData();

// Access stat objects
CombatStats combat = data.getCombatStats();
ProtectionStats protection = data.getProtectionStats();
EnchantmentStats enchantment = data.getEnchantmentStats();

// Serialize to NBT
CompoundTag nbt = data.toNBT();

// Deserialize from NBT
EntityData loaded = EntityData.fromNBT(nbt);

// Validate
boolean valid = data.validate();

// Deep copy
EntityData copy = data.copy();
```

#### EntityDataMigration
**Location**: `sources/legacy/llovelyr-1.21.1/Common/src/main/java/net/msymbios/llovelyr/lib/entity/data/EntityDataMigration.java`

**Purpose**: Handles backward compatibility by migrating from legacy scattered NBT fields to new EntityData structure.

**Key Features**:
- Automatic format detection
- One-way migration (old → new)
- Preserves all existing data
- Validation of migrated data
- Migration reporting for debugging

**API**:
```java
// Migrate entity data
EntityData data = EntityDataMigration.migrate(nbt, version);

// Check if legacy format
boolean isLegacy = EntityDataMigration.isLegacyFormat(nbt);

// Get migration report
String report = EntityDataMigration.createMigrationReport(nbt);
```

**Migration Mapping**:
```
Legacy Format → New Format
--------------------------
Level → CombatStats.level
Exp → CombatStats.experience
FireProtection → ProtectionStats.fireProtection
FallProtection → ProtectionStats.fallProtection
BlastProtection → ProtectionStats.blastProtection
ProjectileProtection → ProtectionStats.projectileProtection
```

#### Stat Class Enhancements
**Locations**:
- `sources/legacy/llovelyr-1.21.1/Common/src/main/java/net/msymbios/llovelyr/framework/entity/data/CombatStats.java`
- `sources/legacy/llovelyr-1.21.1/Common/src/main/java/net/msymbios/llovelyr/framework/entity/data/ProtectionStats.java`
- `sources/legacy/llovelyr-1.21.1/Common/src/main/java/net/msymbios/llovelyr/framework/entity/data/EnchantmentStats.java`

**Added Methods**:
- `copy()` - Deep copy for snapshots
- `toString()` - Human-readable representation

## Remaining Work

### Phase 1: Feature Integration

#### Tasks
- [ ] Add PickupFeature, DropFeature, LootDropFeature to NativeEntityType configuration
- [ ] Update `handleItemDrop()` in LovelyRobotEntity to use PickupFeature + DropFeature
- [ ] Integrate LootDropFeature into entity death logic
- [ ] Test feature configuration per robot type

#### Implementation Notes
```java
// In NativeEntityType configuration
robotType
    .withFeature(PickupFeature.class, new PickupFeature(spawnItem))
    .withFeature(DropFeature.class, new DropFeature(coreItem, 1, true))
    .withFeature(LootDropFeature.class, new LootDropFeature()
        .addLoot(Items.DIAMOND, 1, 2, 0.1f)
    );

// In handleItemDrop()
if (nativeEntity.hasFeature(PickupFeature.class)) {
    PickupFeature pickup = nativeEntity.getFeature(PickupFeature.class).get();
    ItemStack dropItem = pickup.createPickupItem(this);
    // Spawn item in world
}
```

### Phase 2: Command System

#### Tasks
- [ ] Analyze existing command structure
- [ ] Create MeCommandHandler for "/llovely me" commands
- [ ] Implement auto-detection of command sender
- [ ] Update command registration
- [ ] Test with various robot commands

#### Design Notes
```
Current: /llovely owner <player> <command> <args>
New:     /llovely me <command> <args>

Auto-detects sender, queries registry for their robots
No need to specify player name
```

### Phase 3: NBT Data Flow

#### Tasks
- [ ] Create ISpawnItemNBT interface in Common
- [ ] Update Fabric LovelySpawnItem to implement interface
- [ ] Update Forge LovelySpawnItem to implement interface
- [ ] Integrate with PickupFeature for data transfer
- [ ] Test spawn item → entity data flow

#### Implementation Notes
```java
// ISpawnItemNBT interface
public interface ISpawnItemNBT {
    CompoundTag extractEntityData(ItemStack stack);
    void injectEntityData(ItemStack stack, CompoundTag data);
}

// In spawn item use()
CompoundTag entityData = extractEntityData(stack);
Entity entity = spawnEntity(world, pos);
if (entity instanceof InternalEntity internal) {
    internal.readFromNBT(entityData, version);
}
```

### Phase 4: Experience System Integration

#### Tasks
- [ ] Add ExperienceTracker field to InternalEntity
- [ ] Update `handleAttackTarget()` to accumulate exp instead of immediate award
- [ ] Implement entity death listener to claim accumulated exp
- [ ] Add periodic purge call in tick() method
- [ ] Test with immortal entities (Mumummum)

#### Implementation Notes
```java
// In InternalEntity
protected ExperienceTracker expTracker = new ExperienceTracker();

// In handleAttackTarget()
if (target instanceof LivingEntity living) {
    int expAmount = calculateExp(living);
    expTracker.accumulateExp(target.getUUID(), expAmount);
}

// In tick() - purge every 20 ticks
if (tickCount % 20 == 0) {
    expTracker.purgeStaleEntries();
}

// On entity death event
int accumulatedExp = expTracker.claimExp(deadEntity.getUUID());
if (accumulatedExp > 0) {
    addExperience(accumulatedExp);
}
```

### Phase 5: NBT Method Updates

#### Tasks
- [ ] Update InternalEntity.addAdditionalSaveData() to use EntityData
- [ ] Update InternalEntity.readAdditionalSaveData() with migration support
- [ ] Update LovelyRobotEntity NBT methods similarly
- [ ] Test with old robot saves (migration)
- [ ] Test with new robot saves (direct load)
- [ ] Verify no data loss during migration

#### Implementation Notes
```java
// In addAdditionalSaveData()
EntityData entityData = new EntityData(combatStats, protectionStats, enchantmentStats);
entityData.toParentNBT(dataNBT);

// In readAdditionalSaveData()
EntityData entityData = EntityDataMigration.migrate(dataNBT, version);
this.combatStats = entityData.getCombatStats();
this.protectionStats = entityData.getProtectionStats();
this.enchantmentStats = entityData.getEnchantmentStats();
recalculateAttributes();
```

### Phase 6: Integration & Testing

#### Tasks
- [ ] Full integration test with all features
- [ ] Test robot spawning from item with NBT
- [ ] Test robot pickup preserving stats
- [ ] Test exp accumulation with immortal entities
- [ ] Test migration from old saves
- [ ] Test "me" commands
- [ ] Test loot drops
- [ ] Update CURRENT_STATE.md with new components

## Architecture Decisions

### Feature System Design
- **Decision**: Use existing feature system rather than creating parallel systems
- **Rationale**: Maintains consistency, reduces code duplication, leverages existing infrastructure
- **Impact**: All new features follow same pattern as LevelFeature, CombatLevelFeature, etc.

### Entity-Centric Data Interpretation
- **Decision**: Entity interprets its own NBT data, features only package/extract
- **Rationale**: Separation of concerns, entity knows its own structure
- **Impact**: PickupFeature doesn't need to know entity internals

### Experience Tracking
- **Decision**: Accumulate exp per attacked entity, award only on death
- **Rationale**: Prevents exp farming from immortal entities
- **Impact**: Requires death event listener, periodic cleanup

### Data Migration Strategy
- **Decision**: One-way migration (old → new), maintain backward compatibility
- **Rationale**: Prevents data loss, enables future cleanup of legacy code
- **Impact**: Migration code will remain until legacy format support is dropped

### Command Design
- **Decision**: "/llovely me" auto-detects sender, no player specification needed
- **Rationale**: Simpler UX, most common use case
- **Impact**: Requires registry query by sender UUID

## Testing Strategy

### Unit Tests
- [ ] PickupFeature NBT packaging
- [ ] DropFeature count calculation
- [ ] LootDropFeature drop generation
- [ ] ExperienceTracker accumulation and purging
- [ ] EntityData serialization/deserialization
- [ ] EntityDataMigration format detection and migration

### Integration Tests
- [ ] Feature integration with NativeEntityType
- [ ] Entity spawn with NBT data
- [ ] Entity pickup preserving stats
- [ ] Exp accumulation across multiple hits
- [ ] Migration from old save files

### Manual Tests
- [ ] Spawn robot from item with custom stats
- [ ] Pick up robot, verify stats preserved
- [ ] Hit immortal entity repeatedly, verify no exp gain
- [ ] Kill entity after hits, verify accumulated exp awarded
- [ ] Load old world, verify robots migrate correctly
- [ ] Use "/llovely me" commands
- [ ] Kill high-level robot, verify loot drops

## Performance Considerations

### ExperienceTracker
- **Memory**: O(n) where n = number of attacked entities
- **Purge**: O(n) iteration every 5 minutes
- **Mitigation**: Configurable timeout, automatic cleanup

### EntityData
- **Serialization**: O(1) for each stat object
- **Migration**: O(1) field extraction
- **Impact**: Negligible performance impact

### Feature System
- **Lookup**: O(1) via Class-keyed map
- **Impact**: No performance degradation

## Known Issues

None currently identified.

## Future Enhancements

### Potential Improvements
1. **Loot Conditions**: Add entity-aware predicates (level-based drops)
2. **Experience Multipliers**: Configurable exp rates per entity type
3. **Drop Modifiers**: Looting enchantment affecting loot drops
4. **Pickup Restrictions**: Ownership checks for pickup
5. **Data Compression**: Compress NBT for large stat sets

### Extensibility Points
- PickupFeature: Custom NBT packagers
- LootDropFeature: Custom drop conditions
- EntityData: Additional stat objects
- Migration: Version-specific migrations

---

**Last Updated**: 2025-12-07
**Next Review**: After Phase 1 integration complete
