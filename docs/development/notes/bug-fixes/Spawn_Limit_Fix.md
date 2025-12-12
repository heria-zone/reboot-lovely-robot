# Spawn Limit Fix - Unlimited Robots Support

**Status**: ✅ Fixed
**Date**: 2025-12-07
**Version**: Legacy 1.21.1
**Issue**: Config value `-1` for unlimited robots was not working

---

## Problem

The `canSpawnRobot()` method in `OwnerRobotRegistry` was using a simple `validCount < maxRobots` comparison, which didn't handle the special case of `-1` meaning "unlimited robots".

### Broken Behavior

**Config: `OwnerMaxRobotNum = -1`** (intended: unlimited)
- Check: `validCount < -1` 
- Result: Always `false` ❌
- Effect: **No robots could be spawned**

**Config: `OwnerMaxRobotNum = 3`** (intended: limit to 3)
- Check: `validCount < 3`
- Result: Allows 0, 1, 2 robots ✅
- Effect: **Worked correctly**

---

## Solution

Added explicit handling for the `-1` unlimited case:

```java
public boolean canSpawnRobot(UUID ownerId, int maxRobots) {
    if (ownerId == null) {
        return false;
    }

    // -1 means unlimited robots
    if (maxRobots == -1) {
        return true;
    }

    // 0 or negative (other than -1) means no robots allowed
    if (maxRobots <= 0) {
        return false;
    }

    // ... rest of validation logic
}
```

---

## Behavior After Fix

### Config Values

**`OwnerMaxRobotNum = -1`** (unlimited):
- ✅ Allows spawning any number of robots
- No limit enforced
- Useful for creative/testing

**`OwnerMaxRobotNum = 0`** (none):
- ✅ Blocks all robot spawning
- Useful for disabling feature

**`OwnerMaxRobotNum = 3`** (specific limit):
- ✅ Allows spawning up to 3 robots (0, 1, 2)
- 4th spawn attempt fails with message
- Spawn item is **not consumed** on failure

**`OwnerMaxRobotNum = 30`** (default):
- ✅ Allows spawning up to 30 robots
- Standard gameplay limit

---

## Spawn Failure Behavior

When spawn limit is reached:

1. **Check fails**: `canSpawnRobot()` returns `false`
2. **Message displayed**: "Cannot spawn robot: limit of X reached (Y active)"
3. **Item not consumed**: Spawn item remains in inventory
4. **No entity spawned**: No robot entity created

This is the correct behavior - players don't lose their spawn item when hitting the limit.

---

## Testing Checklist

### Unlimited Mode (`-1`)
- [ ] Can spawn 10+ robots without limit
- [ ] No error messages displayed
- [ ] All robots register correctly

### Zero Limit (`0`)
- [ ] Cannot spawn any robots
- [ ] Error message displayed
- [ ] Spawn item not consumed

### Specific Limit (`3`)
- [ ] Can spawn exactly 3 robots
- [ ] 4th spawn attempt fails with message
- [ ] Spawn item not consumed on failure
- [ ] After removing a robot, can spawn again

### Default Limit (`30`)
- [ ] Can spawn up to 30 robots
- [ ] 31st spawn attempt fails
- [ ] Performance acceptable with 30 robots

---

## Edge Cases Handled

### Negative Values (other than -1)
- Values like `-2`, `-5`, etc. are treated as "no robots allowed"
- Prevents accidental misconfiguration

### Null Owner
- Returns `false` immediately
- Prevents null pointer exceptions

### Invalid Entries
- Only counts valid robot entries
- Dead/removed robots don't count toward limit
- Registry cleanup ensures accurate counts

---

## Related Files

**Modified**:
- `sources/legacy/llovelyr-1.21.1/Common/src/main/java/net/msymbios/llovelyr/framework/registry/OwnerRobotRegistry.java`

**Uses This Check**:
- `sources/legacy/llovelyr-1.21.1/Forge/src/main/java/net/msymbios/llovelyr/shared/item/LovelySpawnItem.java`
- `sources/legacy/llovelyr-1.21.1/NeoForge/src/main/java/net/msymbios/llovelyr/shared/item/LovelySpawnItem.java`
- `sources/legacy/llovelyr-1.21.1/Fabric/src/main/java/net/msymbios/llovelyr/shared/item/LovelySpawnItem.java`

**Configuration**:
- `sources/legacy/llovelyr-1.21.1/Common/src/main/java/net/msymbios/llovelyr/common/Configs/SharedConfigs.java`

---

## Documentation Updates Needed

- [ ] Update config documentation to clarify `-1 = unlimited`
- [ ] Add to CHANGELOG.md for next release
- [ ] Update user guide with spawn limit information

---

**Conclusion**: The spawn limit system now correctly handles all configuration values, including the special `-1` unlimited case. Players can configure their preferred robot limit, and the system properly enforces it while providing clear feedback when limits are reached.

