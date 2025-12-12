# Config Validation System Implementation

**Date**: 2025-01-XX  
**Status**: Partially Complete  
**Task**: Add config bounds validation to all loaders

## Overview

Implemented a comprehensive config validation system using `ConfigBounds` class to prevent invalid config values that could break mod functionality or crash the system.

## Completed Work

### 1. ConfigBounds Class (Common Module)
**Location**: `sources/legacy/llovelyr-1.21.1/Common/src/main/java/net/msymbios/llovelyr/common/Configs/ConfigBounds.java`

**Features**:
- Min/max constants for all config values
- Special handling for `OwnerMaxRobotNum` (-1 for unlimited, 0 not allowed, 1+ for specific limits)
- Validation methods: `validateInt()`, `validateFloat()`, `validateDouble()`, `validateOwnerMaxRobot()`
- Comprehensive documentation for all bounds

**Key Bounds**:
- Movement speeds: 0.01F - 2.0F (must be > 0)
- HP/Attack/Defense: 1 - 1000 (must be >= 1)
- Protection limits: 0 - 100 (percentage)
- Experience base: 1 - 10000
- Experience multiplier: 1 - 10

### 2. Fabric Config (COMPLETE)
**Location**: `sources/legacy/llovelyr-1.21.1/Fabric/src/main/java/net/msymbios/llovelyr/source/LovelyConfigs.java`

**Changes**:
- Added `import net.msymbios.llovelyr.common.Configs.ConfigBounds;`
- Updated `loadConfigValues()` method to validate ALL config values using ConfigBounds
- All numeric values now clamped to safe ranges
- Boolean values pass through unchanged

**Example**:
```java
SharedConfigs.Common.OwnerMaxRobotNum = ConfigBounds.validateOwnerMaxRobot(
        config.getOrDefault("owner-max-robot", SharedConfigs.Common.OwnerMaxRobotNum));
SharedConfigs.Common.MovementMeleeAttack = ConfigBounds.validateFloat(
        config.getOrDefault("movement-melee-attack", SharedConfigs.Common.MovementMeleeAttack),
        ConfigBounds.MOVEMENT_SPEED_MIN, ConfigBounds.MOVEMENT_SPEED_MAX);
```

## Remaining Work

### 3. Forge Config (COMPLETE)
**Location**: `sources/legacy/llovelyr-1.21.1/Forge/src/main/java/net/msymbios/llovelyr/source/LovelyConfigs.java`  
**Size**: 792 lines

**Required Changes**:
1. Add import: `import net.msymbios.llovelyr.common.Configs.ConfigBounds;`
2. Change all `.define()` calls to `.defineInRange()` with ConfigBounds
3. Update `onLoad()` method to fetch from SharedConfigs instead of raw values

**Pattern to Follow**:
```java
// OLD:
OWNER_MAX_ROBOT_NUM = BUILDER
        .comment("...")
        .define("owner-max-robot", 30);

// NEW:
OWNER_MAX_ROBOT_NUM = BUILDER
        .comment("...")
        .defineInRange("owner-max-robot", 
                SharedConfigs.Common.OwnerMaxRobotNum,
                ConfigBounds.OWNER_MAX_ROBOT_MIN,
                ConfigBounds.OWNER_MAX_ROBOT_MAX);
```

**Special Case for OwnerMaxRobotNum**:
Since Forge's `defineInRange()` doesn't support -1 as a valid value in a range, we need custom validation:
```java
OWNER_MAX_ROBOT_NUM = BUILDER
        .comment("...", "Set to -1 for unlimited robots.")
        .define("owner-max-robot", SharedConfigs.Common.OwnerMaxRobotNum);

// Then in onLoad():
SharedConfigs.Common.OwnerMaxRobotNum = ConfigBounds.validateOwnerMaxRobot(
        OWNER_MAX_ROBOT_NUM.get());
```

### 4. NeoForge Config (COMPLETE)
**Location**: `sources/legacy/llovelyr-1.21.1/NeoForge/src/main/java/net/msymbios/llovelyr/source/LovelyConfigs.java`  
**Size**: 800 lines

**Required Changes**: Same as Forge (NeoForge uses same config API)

1. Add import: `import net.msymbios.llovelyr.common.Configs.ConfigBounds;`
2. Change all `.define()` calls to `.defineInRange()` with ConfigBounds
3. Update `onLoad()` method to fetch from SharedConfigs instead of raw values

## Implementation Strategy

### For Forge and NeoForge

Due to file size (792-800 lines), systematic approach needed:

1. **Add Import** (1 change)
2. **Update General Section** (~10 config values)
3. **Update Renderer Section** (~3 config values)
4. **Update Experience Section** (~2 config values)
5. **Update Combat Section** (~10 config values)
6. **Update Protection Section** (~4 config values)
7. **Update Smart Core Section** (~2 config values)
8. **Update AI Behavior Section** (~20 config values)
9. **Update Animation Section** (~2 config values)
10. **Update Entity Sections** (~49 config values, 7 entities × 7 stats each)
11. **Update onLoad() Method** (fetch from SharedConfigs with validation)

### Validation Pattern

For each config value:
```java
// Integer with range
.defineInRange("key", SharedConfigs.Common.Value, MIN, MAX)

// Float with range
.defineInRange("key", SharedConfigs.Common.Value, MIN, MAX)

// Boolean (no validation needed)
.define("key", SharedConfigs.Common.Value)

// Special case (OwnerMaxRobotNum)
.define("key", SharedConfigs.Common.Value)
// Then validate in onLoad() with ConfigBounds.validateOwnerMaxRobot()
```

## Benefits

1. **Prevents Crashes**: Invalid values (0 speed, 0 HP) are clamped to safe minimums
2. **Prevents Exploits**: Excessive values (999999 HP) are clamped to reasonable maximums
3. **Single Source of Truth**: ConfigBounds is the only place to change validation rules
4. **Consistent Behavior**: All loaders use same validation logic
5. **User-Friendly**: Invalid values are corrected automatically, not rejected

## Testing Checklist

After implementation:
- [ ] Test with valid config values (should work normally)
- [ ] Test with OwnerMaxRobotNum = -1 (should allow unlimited)
- [ ] Test with OwnerMaxRobotNum = 0 (should clamp to 1)
- [ ] Test with movement speed = 0 (should clamp to 0.01)
- [ ] Test with HP = 0 (should clamp to 1)
- [ ] Test with excessive values (should clamp to max)
- [ ] Test with negative values (should clamp to min)
- [ ] Verify all three loaders behave identically

## Notes

- ConfigBounds uses immutable constants to prevent runtime modification
- Validation is non-destructive (clamps instead of rejecting)
- Special handling for -1 values (unlimited) where applicable
- All bounds documented with rationale

---

**Status**: ✅ ALL LOADERS COMPLETE - Fabric, Forge, and NeoForge all have full config validation implemented and compiling successfully.
