# LovelyRobot Command Reference

**Version:** Legacy 1.20.1  
**Loaders:** Fabric & Forge  
**Last Updated:** 2024-12-02

## Command Overview

All commands are accessed through the `/llovely` root command and require **OP level 2** (operator permissions).

### Targeting Modes

The command system supports three distinct targeting strategies:

1. **Crosshair Targeting** (`robot`) - Raycast-based selection within 5 blocks
2. **Entity Selector** (`target`) - Minecraft's entity selector for batch operations
3. **Owner Registry** (`owner`) - Direct access via player UUID and robot index

---

## Command Structure

```
/llovely
├── robot          # Crosshair-targeted commands (look at robot)
│   ├── add        # Add values (XP)
│   ├── set        # Set values (combat, attributes, protections, appearance, identifier)
│   ├── get        # Query information (owner)
│   ├── recall     # Teleport robot to you
│   ├── heal       # Heal robot to full health
│   └── stats      # Display comprehensive stats
│
├── target         # Entity selector commands (batch operations)
│   ├── add        # Add values to multiple robots
│   ├── set        # Set values for multiple robots
│   ├── heal       # Heal multiple robots
│   ├── teleport   # Teleport to first robot in selection
│   ├── recall     # Teleport robots to player
│   └── ownership  # Transfer ownership
│
├── owner          # Registry-based commands (by player and index)
│   ├── list       # List owners or player's robots
│   ├── add        # Add values to specific robot
│   ├── set        # Set values for specific robot
│   ├── teleport   # Teleport to robot by index
│   ├── recall     # Teleport robot to owner
│   ├── heal       # Heal specific robot
│   ├── healall    # Heal all robots owned by player
│   ├── stats      # Display stats for specific robot
│   └── transfer   # Transfer robot ownership
│
└── reload         # Reload configuration
```

---

## Crosshair Commands (`/llovely robot`)

These commands target the robot you're looking at within 5 blocks. Requires ownership.

### Combat Management

#### Add Experience
```bash
/llovely robot add combat exp <amount>
```
Adds experience points without validation - surplus triggers automatic level-ups.

**Examples:**
```bash
/llovely robot add combat exp 1000
/llovely robot add combat exp 50000
```

#### Set Experience
```bash
/llovely robot set combat exp <amount>
```
Sets exact XP with validation against current level's maximum.

**Examples:**
```bash
/llovely robot set combat exp 500
```

#### Set Level
```bash
/llovely robot set combat level <level>
```
Sets level directly with validation against robot type's maximum (typically 200).

**Examples:**
```bash
/llovely robot set combat level 100
/llovely robot set combat level 1
```

#### Set All Combat Stats
```bash
/llovely robot set combat all <level> <exp>
```
Atomically sets both level and XP with validation.

**Examples:**
```bash
/llovely robot set combat all 50 1000
/llovely robot set combat all 100 0
```

### Attribute Management

#### Set HP
```bash
/llovely robot set attribute hp <value>
```
Sets maximum health and heals robot to full.

**Examples:**
```bash
/llovely robot set attribute hp 100
/llovely robot set attribute hp 500
```

#### Set Attack
```bash
/llovely robot set attribute attack <value>
```
Sets attack damage attribute.

**Examples:**
```bash
/llovely robot set attribute attack 50
/llovely robot set attribute attack 10
```

#### Set Defense
```bash
/llovely robot set attribute defense <value>
```
Sets armor defense attribute.

**Examples:**
```bash
/llovely robot set attribute defense 30
/llovely robot set attribute defense 0
```

#### Set Speed
```bash
/llovely robot set attribute speed <value>
```
Sets movement speed (value divided by 10 internally).

**Examples:**
```bash
/llovely robot set attribute speed 5    # 0.5 movement speed
/llovely robot set attribute speed 10   # 1.0 movement speed
```

#### Set All Attributes
```bash
/llovely robot set attribute all <hp> <attack> <defense> <speed>
```
Atomically sets all attributes.

**Examples:**
```bash
/llovely robot set attribute all 100 50 30 5
/llovely robot set attribute all 200 100 50 8
```

### Protection Management

#### Set Fire Protection
```bash
/llovely robot set protection fire <level>
```
Sets fire protection (0-80, reduces fire/lava damage).

**Examples:**
```bash
/llovely robot set protection fire 40
/llovely robot set protection fire 80
```

#### Set Fall Protection
```bash
/llovely robot set protection fall <level>
```
Sets fall protection (0-80, reduces fall damage).

**Examples:**
```bash
/llovely robot set protection fall 60
```

#### Set Blast Protection
```bash
/llovely robot set protection blast <level>
```
Sets blast protection (0-80, reduces explosion damage).

**Examples:**
```bash
/llovely robot set protection blast 70
```

#### Set Projectile Protection
```bash
/llovely robot set protection projectile <level>
```
Sets projectile protection (0-80, reduces arrow/projectile damage).

**Examples:**
```bash
/llovely robot set protection projectile 50
```

#### Set All Protections
```bash
/llovely robot set protection all <fire> <fall> <blast> <projectile>
```
Atomically sets all protection levels.

**Examples:**
```bash
/llovely robot set protection all 40 40 40 40
/llovely robot set protection all 80 80 80 80
```

### Appearance & Identity

#### Set Appearance
```bash
/llovely robot set appearance <color>
```
Changes robot's texture/color.

**Available Colors:**
- white, orange, magenta, light_blue, yellow, lime, pink, gray
- light_gray, cyan, purple, blue, brown, green, red, black

**Examples:**
```bash
/llovely robot set appearance red
/llovely robot set appearance purple
/llovely robot set appearance light_blue
```

#### Set Identifier
```bash
/llovely robot set identifier <name>
```
Sets custom name with visibility enabled. Supports spaces.

**Examples:**
```bash
/llovely robot set identifier Guardian
/llovely robot set identifier My Robot
/llovely robot set identifier Elite Guard Bot
```

### Utility Commands

#### Get Owner
```bash
/llovely robot get owner
```
Displays current owner name or "No owner" if untamed.

#### Recall Robot
```bash
/llovely robot recall
```
Teleports robot to your location.

#### Heal Robot
```bash
/llovely robot heal
```
Heals robot to full health.

#### Display Stats
```bash
/llovely robot stats
```
Shows comprehensive stats including level, XP, attributes, and protections.

---

## Target Commands (`/llovely target`)

These commands use Minecraft's entity selector for batch operations. No ownership validation.

### Combat Management

#### Add Experience (Batch)
```bash
/llovely target <selector> add combat exp <amount>
```

**Examples:**
```bash
/llovely target @e[type=llovelyr:vanilla] add combat exp 1000
/llovely target @e[distance=..10] add combat exp 500
```

#### Set Experience (Batch)
```bash
/llovely target <selector> set combat exp <amount>
```
Validates per-robot, skips robots where XP exceeds their level cap.

**Examples:**
```bash
/llovely target @e[type=llovelyr:bunny] set combat exp 500
```

#### Set Level (Batch)
```bash
/llovely target <selector> set combat level <level>
```
Validates per-robot, skips robots where level exceeds their maximum.

**Examples:**
```bash
/llovely target @e[type=llovelyr:dragon] set combat level 100
/llovely target @e[distance=..20] set combat level 50
```

#### Set All Combat Stats (Batch)
```bash
/llovely target <selector> set combat all <level> <exp>
```

**Examples:**
```bash
/llovely target @e[type=llovelyr:vanilla] set combat all 50 1000
```

### Attribute Management (Batch)

#### Set HP (Batch)
```bash
/llovely target <selector> set attribute hp <value>
```

**Examples:**
```bash
/llovely target @e[type=llovelyr:vanilla] set attribute hp 100
```

#### Set Attack (Batch)
```bash
/llovely target <selector> set attribute attack <value>
```

**Examples:**
```bash
/llovely target @e[type=llovelyr:dragon] set attribute attack 50
```

#### Set Defense (Batch)
```bash
/llovely target <selector> set attribute defense <value>
```

**Examples:**
```bash
/llovely target @e[distance=..10] set attribute defense 30
```

#### Set Speed (Batch)
```bash
/llovely target <selector> set attribute speed <value>
```

**Examples:**
```bash
/llovely target @e[type=llovelyr:bunny] set attribute speed 5
```

#### Set All Attributes (Batch)
```bash
/llovely target <selector> set attribute all <hp> <attack> <defense> <speed>
```

**Examples:**
```bash
/llovely target @e[type=llovelyr:dragon] set attribute all 200 100 50 8
/llovely target @e[distance=..20] set attribute all 100 50 30 5
```

### Protection Management (Batch)

#### Set Fire Protection (Batch)
```bash
/llovely target <selector> set protection fire <level>
```

**Examples:**
```bash
/llovely target @e[type=llovelyr:vanilla] set protection fire 40
```

#### Set Fall Protection (Batch)
```bash
/llovely target <selector> set protection fall <level>
```

**Examples:**
```bash
/llovely target @e[type=llovelyr:bunny2] set protection fall 60
```

#### Set Blast Protection (Batch)
```bash
/llovely target <selector> set protection blast <level>
```

**Examples:**
```bash
/llovely target @e[type=llovelyr:dragon] set protection blast 70
```

#### Set Projectile Protection (Batch)
```bash
/llovely target <selector> set protection projectile <level>
```

**Examples:**
```bash
/llovely target @e[type=llovelyr:kitsune] set protection projectile 50
```

#### Set All Protections (Batch)
```bash
/llovely target <selector> set protection all <fire> <fall> <blast> <projectile>
```

**Examples:**
```bash
/llovely target @e[type=llovelyr:vanilla] set protection all 40 40 40 40
/llovely target @e[distance=..20] set protection all 80 80 80 80
```

### Appearance & Identity (Batch)

#### Set Appearance (Batch)
```bash
/llovely target <selector> set appearance <color>
```

**Examples:**
```bash
/llovely target @e[type=llovelyr:vanilla] set appearance red
/llovely target @e[distance=..10] set appearance blue
```

#### Set Identifier (Batch)
```bash
/llovely target <selector> set identifier <name>
```

**Examples:**
```bash
/llovely target @e[type=llovelyr:vanilla] set identifier Guard Bot
/llovely target @e[distance=..5] set identifier Elite Robot
```

### Utility Commands (Batch)

#### Heal Robots (Batch)
```bash
/llovely target <selector> heal
```

**Examples:**
```bash
/llovely target @e[type=llovelyr:vanilla] heal
/llovely target @e[distance=..20] heal
```

#### Teleport to Robot
```bash
/llovely target <selector> teleport <player>
```
Teleports player to first robot in selection.

**Examples:**
```bash
/llovely target @e[type=llovelyr:vanilla,limit=1] teleport @p
```

#### Recall Robots
```bash
/llovely target <selector> recall <player>
```
Teleports all selected robots to player location.

**Examples:**
```bash
/llovely target @e[type=llovelyr:bunny] recall @p
/llovely target @e[distance=..50] recall PlayerName
```

#### Transfer Ownership
```bash
/llovely target <selector> ownership <to_player>
```
Transfers ownership of all selected robots. Unregisters from old owner and re-registers automatically.

**Examples:**
```bash
/llovely target @e[type=llovelyr:vanilla] ownership PlayerName
/llovely target @e[distance=..10] ownership @p
```

---

## Owner Commands (`/llovely owner`)

These commands access robots via registry lookup using player UUID and robot index.

### List Commands

#### List All Owners
```bash
/llovely owner list player
```
Displays all players who own robots with robot counts.

**Output Format:**
```
- PlayerName (5)
- AnotherPlayer (12)
```

#### List Player's Robots
```bash
/llovely owner list robot <player>
```
Displays indexed list of robots owned by player with type and custom name.

**Output Format:**
```
Player: (PlayerName)
[0] Vanilla Robot (Guardian)
[1] Bunny Robot
[2] Dragon Robot (Elite)
[3] Offline/Unloaded (Neko Robot)
```

**Examples:**
```bash
/llovely owner list robot PlayerName
/llovely owner list robot @p
```

### Combat Management (Registry)

#### Add Experience (Registry)
```bash
/llovely owner add combat exp <player> <robot_index> <amount>
```
Tab completion shows player names and robot indices with names.

**Examples:**
```bash
/llovely owner add combat exp PlayerName 0 1000
/llovely owner add combat exp @p 2 500
```

#### Set Experience (Registry)
```bash
/llovely owner set combat exp <player> <robot_index> <amount>
```

**Examples:**
```bash
/llovely owner set combat exp PlayerName 0 500
```

#### Set Level (Registry)
```bash
/llovely owner set combat level <player> <robot_index> <level>
```

**Examples:**
```bash
/llovely owner set combat level PlayerName 0 100
```

#### Set All Combat Stats (Registry)
```bash
/llovely owner set combat all <player> <robot_index> <level> <exp>
```

**Examples:**
```bash
/llovely owner set combat all PlayerName 0 50 1000
```

### Attribute Management (Registry)

#### Set HP (Registry)
```bash
/llovely owner set attribute hp <player> <robot_index> <value>
```

**Examples:**
```bash
/llovely owner set attribute hp PlayerName 0 100
```

#### Set Attack (Registry)
```bash
/llovely owner set attribute attack <player> <robot_index> <value>
```

**Examples:**
```bash
/llovely owner set attribute attack PlayerName 0 50
```

#### Set Defense (Registry)
```bash
/llovely owner set attribute defense <player> <robot_index> <value>
```

**Examples:**
```bash
/llovely owner set attribute defense PlayerName 0 30
```

#### Set Speed (Registry)
```bash
/llovely owner set attribute speed <player> <robot_index> <value>
```

**Examples:**
```bash
/llovely owner set attribute speed PlayerName 0 5
```

#### Set All Attributes (Registry)
```bash
/llovely owner set attribute all <player> <robot_index> <hp> <attack> <defense> <speed>
```

**Examples:**
```bash
/llovely owner set attribute all PlayerName 0 100 50 30 5
```

### Protection Management (Registry)

#### Set Fire Protection (Registry)
```bash
/llovely owner set protection fire <player> <robot_index> <level>
```

**Examples:**
```bash
/llovely owner set protection fire PlayerName 0 40
```

#### Set Fall Protection (Registry)
```bash
/llovely owner set protection fall <player> <robot_index> <level>
```

**Examples:**
```bash
/llovely owner set protection fall PlayerName 0 60
```

#### Set Blast Protection (Registry)
```bash
/llovely owner set protection blast <player> <robot_index> <level>
```

**Examples:**
```bash
/llovely owner set protection blast PlayerName 0 70
```

#### Set Projectile Protection (Registry)
```bash
/llovely owner set protection projectile <player> <robot_index> <level>
```

**Examples:**
```bash
/llovely owner set protection projectile PlayerName 0 50
```

#### Set All Protections (Registry)
```bash
/llovely owner set protection all <player> <robot_index> <fire> <fall> <blast> <projectile>
```

**Examples:**
```bash
/llovely owner set protection all PlayerName 0 40 40 40 40
```

### Appearance & Identity (Registry)

#### Set Appearance (Registry)
```bash
/llovely owner set appearance <player> <robot_index> <color>
```

**Examples:**
```bash
/llovely owner set appearance PlayerName 0 red
```

#### Set Identifier (Registry)
```bash
/llovely owner set identifier <player> <robot_index> <name>
```

**Examples:**
```bash
/llovely owner set identifier PlayerName 0 Guardian
/llovely owner set identifier PlayerName 0 My Elite Robot
```

### Utility Commands (Registry)

#### Teleport to Robot
```bash
/llovely owner teleport <player> <robot_index>
```
Teleports command source to robot. Fails gracefully if robot is offline.

**Examples:**
```bash
/llovely owner teleport PlayerName 0
```

#### Recall Robot
```bash
/llovely owner recall <player> <robot_index>
```
Teleports robot to owner's location.

**Examples:**
```bash
/llovely owner recall PlayerName 0
```

#### Heal Robot
```bash
/llovely owner heal <player> <robot_index>
```
Heals specific robot to full health.

**Examples:**
```bash
/llovely owner heal PlayerName 0
```

#### Heal All Robots
```bash
/llovely owner healall <player>
```
Heals all robots owned by player. Skips offline/unloaded robots automatically.

**Examples:**
```bash
/llovely owner healall PlayerName
/llovely owner healall @p
```

#### Display Stats
```bash
/llovely owner stats <player> <robot_index>
```
Shows comprehensive stats for specific robot. Fails gracefully if offline.

**Examples:**
```bash
/llovely owner stats PlayerName 0
```

#### Transfer Ownership
```bash
/llovely owner transfer <from_player> <robot_index> <to_player>
```
Transfers robot ownership. Unregisters from old owner and re-registers automatically.

**Examples:**
```bash
/llovely owner transfer PlayerName 0 AnotherPlayer
/llovely owner transfer @p 2 @a[limit=1,sort=nearest]
```

---

## Configuration Reload

### Reload Config
```bash
/llovely reload
```
Reloads configuration from disk without server restart.

---

## Advanced Entity Selectors

### Distance Filtering
```bash
# Robots within 10 blocks
/llovely target @e[type=llovelyr:vanilla,distance=..10] set combat level 50

# Robots between 10-20 blocks
/llovely target @e[type=llovelyr:bunny,distance=10..20] heal
```

### Limit Results
```bash
# First 5 robots
/llovely target @e[type=llovelyr:dragon,limit=5] set appearance red

# Nearest 3 robots
/llovely target @e[type=llovelyr:vanilla,sort=nearest,limit=3] heal
```

### Coordinate Filtering
```bash
# Robots near specific coordinates
/llovely target @e[type=llovelyr:dragon,x=100,y=64,z=200,distance=..50] set combat level 200

# Robots in specific region
/llovely target @e[type=llovelyr:vanilla,x=0,y=60,z=0,dx=100,dy=20,dz=100] set protection all 80 80 80 80
```

### Multiple Type Selection
```bash
# All vanilla and bunny robots
/llovely target @e[type=llovelyr:vanilla] set appearance red
/llovely target @e[type=llovelyr:bunny] set appearance blue
```

---

## Tab Completion

The command system provides intelligent tab completion:

### Player Names
- Suggests players who own robots
- Available in all owner commands

### Robot Indices
- Shows first 5 robots with names/types
- Shows total count if more than 5
- Format: `[index] Type (CustomName)` or `[index] Type`

### Suggestion Values
- **Level:** Shows robot's maximum level
- **XP:** Shows maximum XP for current/target level
- **Protection:** Shows maximum protection level per type
- **Colors:** Lists all available texture colors

### Context-Aware Suggestions
- **Crosshair:** Suggests values for robot in crosshair
- **Target:** Suggests safe minimum values for all selected robots
- **Owner:** Suggests values for specific robot by index

---

## Permission Requirements

- **All commands:** Require OP level 2 (operator permissions)
- **Crosshair commands:** Validate ownership (must own robot)
- **Target commands:** No ownership validation (admin operations)
- **Owner commands:** No ownership validation (admin operations)

---

## Error Handling

### Common Error Messages

**"No robots found"**
- No robots matched the selection criteria
- Check entity selector syntax

**"No robot found in crosshair"**
- Not looking at a robot within 5 blocks
- Adjust aim or move closer

**"You don't own [robot name]"**
- Crosshair command requires ownership
- Use target/owner commands if you're an admin

**"Robot not found or offline"**
- Robot is in unloaded chunks or dimension
- Owner commands fail gracefully for offline robots

**"Level X exceeds maximum requirement of Y"**
- Attempted to set level above robot type's maximum
- Check robot type's level cap

**"XP X exceeds maximum requirement of Y"**
- Attempted to set XP above level's maximum
- Use add command for automatic level-ups

---

## Robot Types

Available robot types for entity selectors:

- `llovelyr:vanilla` - Original vanilla robot
- `llovelyr:honey` - Honey variant
- `llovelyr:bunny` - Bunny variant
- `llovelyr:bunny2` - Bunny variant 2
- `llovelyr:dragon` - Dragon variant
- `llovelyr:neko` - Neko variant
- `llovelyr:kitsune` - Kitsune variant

---

## Best Practices

### Quick Operations
Use crosshair commands for single robot operations:
```bash
# Look at robot, then:
/llovely robot set combat level 100
/llovely robot set attribute all 100 50 30 5
/llovely robot heal
```

### Batch Operations
Use target commands for multiple robots:
```bash
# Level up all vanilla robots
/llovely target @e[type=llovelyr:vanilla] set combat level 100

# Max protection for nearby robots
/llovely target @e[distance=..20] set protection all 80 80 80 80
```

### Remote Management
Use owner commands when robots are far away:
```bash
# List player's robots
/llovely owner list robot PlayerName

# Heal all robots
/llovely owner healall PlayerName

# Manage specific robot
/llovely owner set combat all PlayerName 0 100 0
```

### Safe Teleportation
```bash
# Recall robot to you (crosshair)
/llovely robot recall

# Recall multiple robots (target)
/llovely target @e[type=llovelyr:vanilla] recall @p

# Recall specific robot (owner)
/llovely owner recall PlayerName 0
```

---

## Technical Notes

### Crosshair Targeting
- **Range:** 5 block reach distance
- **Method:** Raycast with bounding box intersection
- **Validation:** Ownership required

### Entity Selector
- **Batch Processing:** Operates on all matched entities
- **Validation:** Per-robot validation with skip on failure
- **Feedback:** Shows success/failure counts

### Owner Registry
- **Persistence:** Robots tracked across server restarts
- **Offline Handling:** Commands fail gracefully for offline robots
- **Index Stability:** Indices may change as robots are added/removed

### Atomic Operations
Commands ending with "all" perform atomic operations:
- All values set together
- Validation occurs before any changes
- Rollback on validation failure

---

**Status:** ✅ Fully Implemented  
**Loaders:** Fabric & Forge  
**Version:** Legacy 1.20.1
