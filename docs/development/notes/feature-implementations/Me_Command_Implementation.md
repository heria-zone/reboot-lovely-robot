# "me" Command Variant Implementation

**Date**: 2025-12-07  
**Status**: COMPLETED  
**Type**: Optional Enhancement - Convenience Feature

## Overview

Implemented a complete "me" command variant that auto-detects the command sender, eliminating the need to specify player names when managing your own robots.

## Motivation

The existing owner command system requires typing the player name:
```
/llovely owner <player> <robot_index> <command>
```

For players managing their own robots, this is verbose. The "me" variant provides a cleaner UX:
```
/llovely me <command> <robot_index>
```

## Implementation Details

### Core Architecture

**Location**: `sources/legacy/llovelyr-1.21.1/Common/src/main/java/net/msymbios/llovelyr/common/commands/NativeCommands.java`

Added `ME_SELECTOR` using the existing `RobotSelector` interface:
```java
private static final RobotSelector ME_SELECTOR = ctx -> {
    Player player = ctx.getSource().getPlayerOrException();
    int index = IntegerArgumentType.getInteger(ctx, "robot_index");
    LovelyRobotEntity robot = getOwnerRobotByIndex(player, index);
    return robot != null ? List.of(robot) : List.of();
};
```

### Command Executors

Implemented complete set of "me" command executors:

**Combat Management**:
- `executeMeAddExp()` - Add experience points
- `executeMeSetExp()` - Set exact experience
- `executeMeSetLevel()` - Set level
- `executeMeSetAllCombat()` - Set level and exp atomically

**Attribute Management**:
- `executeMeSetHP()` - Set max health
- `executeMeSetAttack()` - Set attack damage
- `executeMeSetDefense()` - Set armor defense
- `executeMeSetSpeed()` - Set movement speed
- `executeMeSetAllAttributes()` - Set all attributes atomically

**Protection Management**:
- `executeMeSetFireProtection()` - Set fire protection
- `executeMeSetFallProtection()` - Set fall protection
- `executeMeSetBlastProtection()` - Set blast protection
- `executeMeSetProjectileProtection()` - Set projectile protection
- `executeMeSetAllProtections()` - Set all protections atomically

**Utility Commands**:
- `executeMeSetAppearance()` - Change robot color/texture
- `executeMeSetIdentifier()` - Set custom name
- `executeMeTeleport()` - Teleport to robot
- `executeMeRecall()` - Recall robot to sender
- `executeMeHeal()` - Heal specific robot
- `executeMeHealAll()` - Heal all owned robots
- `executeMeStats()` - Display robot stats
- `executeMeList()` - List all owned robots

### Suggestion Methods

Added context-aware suggestion methods for better UX:

- `suggestMeRobotIndices()` - Suggests valid robot indices with names
- `suggestMeMaxLevel()` - Suggests max level for selected robot
- `suggestMeMaxExp()` - Suggests max XP for current level
- `suggestMeMaxExpForLevel()` - Suggests max XP for target level
- `suggestMeMaxProtection()` - Suggests max protection levels by type

### Command Registration

**Forge**: `sources/legacy/llovelyr-1.21.1/Forge/src/main/java/net/msymbios/llovelyr/source/LovelyCommands.java`
**Fabric**: `sources/legacy/llovelyr-1.21.1/Fabric/src/main/java/net/msymbios/llovelyr/source/LovelyCommands.java`

Added `buildMeCommands()` and related builder methods:
- `buildMeAddCommands()` - Combat exp addition
- `buildMeSetCommands()` - All set operations
- `buildMeAddCombatCommands()` - Combat exp commands
- `buildMeSetCombatCommands()` - Combat level/exp commands
- `buildMeSetAttributeCommands()` - Attribute commands
- `buildMeSetProtectionCommands()` - Protection commands
- `buildMeSetAppearanceCommands()` - Appearance commands
- `buildMeSetIdentifierCommands()` - Name commands

## Command Structure

```
/llovely me
├── list                                    - List your robots
├── add
│   └── combat
│       └── exp <index> <xp>               - Add XP
├── set
│   ├── combat
│   │   ├── all <index> <level> <exp>     - Set level and exp
│   │   ├── exp <index> <xp>              - Set exact exp
│   │   └── level <index> <level>         - Set level
│   ├── attribute
│   │   ├── all <index> <hp> <atk> <def> <spd>
│   │   ├── hp <index> <value>
│   │   ├── attack <index> <value>
│   │   ├── defense <index> <value>
│   │   └── speed <index> <value>
│   ├── protection
│   │   ├── all <index> <fire> <fall> <blast> <proj>
│   │   ├── fire <index> <level>
│   │   ├── fall <index> <level>
│   │   ├── blast <index> <level>
│   │   └── projectile <index> <level>
│   ├── appearance <index> <color>
│   └── identifier <index> <name>
├── teleport <index>                       - Teleport to robot
├── recall <index>                         - Recall robot to you
├── heal <index>                           - Heal specific robot
├── healall                                - Heal all robots
└── stats <index>                          - View robot stats
```

## Benefits

1. **Cleaner UX**: No need to type player name for own robots
2. **Faster Commands**: Shorter command syntax
3. **Feature Parity**: Complete equivalence with owner commands
4. **Context-Aware**: Smart suggestions based on robot capabilities
5. **Ownership Validation**: Automatically validates sender owns the robot

## Design Decisions

### Why Not Replace Owner Commands?

The owner commands remain necessary for:
- Server administrators managing other players' robots
- Automated systems using command blocks
- Cross-player robot management

The "me" variant is purely a convenience addition, not a replacement.

### Why Require Robot Index?

Even though we could operate on "all owned robots", requiring an index:
- Prevents accidental bulk operations
- Provides explicit control
- Matches the owner command pattern
- Allows targeted operations

The `healall` command is an exception as healing is non-destructive.

### Why Auto-Detect Sender?

Using `ctx.getSource().getPlayerOrException()` instead of requiring a player argument:
- Eliminates typing player name
- Prevents impersonation (sender is always authenticated)
- Simplifies command syntax
- Matches user mental model ("my robots")

## Testing Recommendations

1. **Basic Operations**: Test all command variants with valid indices
2. **Invalid Indices**: Test with out-of-bounds indices
3. **No Robots**: Test when player owns no robots
4. **Offline Robots**: Test with robots in unloaded chunks
5. **Permissions**: Verify OP level 2 requirement
6. **Suggestions**: Verify context-aware suggestions work correctly

## Future Enhancements

Potential additions based on user feedback:
- `/llovely me summon <index>` - Alternative to recall
- `/llovely me follow <index>` - Toggle follow mode
- `/llovely me sit <index>` - Toggle sit mode
- `/llovely me rename <index> <name>` - Alias for identifier

## Files Modified

1. `sources/legacy/llovelyr-1.21.1/Common/src/main/java/net/msymbios/llovelyr/common/commands/NativeCommands.java`
   - Added `ME_SELECTOR`
   - Added 20+ executor methods
   - Added 5 suggestion methods

2. `sources/legacy/llovelyr-1.21.1/Forge/src/main/java/net/msymbios/llovelyr/source/LovelyCommands.java`
   - Added `buildMeCommands()` and 8 builder methods
   - Registered in command dispatcher

3. `sources/legacy/llovelyr-1.21.1/Fabric/src/main/java/net/msymbios/llovelyr/source/LovelyCommands.java`
   - Added `buildMeCommands()` and 8 builder methods
   - Registered in command dispatcher

## Conclusion

The "me" command variant successfully provides a cleaner, more intuitive interface for players managing their own robots while maintaining full feature parity with the owner command system. The implementation leverages the existing command infrastructure, requiring minimal code duplication and maintaining consistency with established patterns.

---

**Implementation Time**: ~1 hour  
**Lines Added**: ~500 (including documentation)  
**Complexity**: Low (leverages existing infrastructure)  
**User Impact**: High (significant UX improvement)
