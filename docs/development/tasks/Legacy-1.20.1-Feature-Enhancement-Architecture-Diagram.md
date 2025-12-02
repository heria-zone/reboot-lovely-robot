---
created: 2025-11-30
status: Reference
tags:
  - Legacy
  - Architecture
  - Diagram
---

# Legacy 1.20.1 Feature Enhancement - Architecture Diagram

## Layer Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        SOURCE LAYER                              │
│                   (Loader-Specific Implementation)               │
├─────────────────────────────────────────────────────────────────┤
│  NativeRobotType.java                                           │
│  ├─ VANILLA.withFeature(CombatLevelFeature, ...)               │
│  ├─ DRAGON.withFeature(CombatLevelFeature, ...)                │
│  └─ KITSUNE.withFeature(CombatLevelFeature, ...)               │
│                                                                  │
│  LovelyCommands.java                                            │
│  └─ Register commands with Forge/Fabric dispatcher             │
└─────────────────────────────────────────────────────────────────┘
                              ↓ uses
┌─────────────────────────────────────────────────────────────────┐
│                        COMMON LAYER                              │
│                    (Mod-Specific Shared Code)                    │
├─────────────────────────────────────────────────────────────────┤
│  InternalEntity.java                                            │
│  ├─ CombatStats combatStats                                    │
│  ├─ ProtectionStats protectionStats                            │
│  ├─ EnchantmentStats enchantmentStats                          │
│  ├─ recalculateAttributes()                                    │
│  ├─ registerRobot() / unregisterRobot()                        │
│  └─ NBT read/write via stat classes                            │
│                                                                  │
│  LovelyRobotCommands.java                                       │
│  ├─ executeOwnerList()                                         │
│  ├─ executeSetLevel() with validation                          │
│  ├─ executeSetExp() with auto-level-up                         │
│  └─ executeSetProtection() with validation                     │
└─────────────────────────────────────────────────────────────────┘
                              ↓ uses
┌─────────────────────────────────────────────────────────────────┐
│                          LIB LAYER                               │
│                   (Loader-Specific Bridges)                      │
├─────────────────────────────────────────────────────────────────┤
│  CombatLevelFeature.java                                        │
│  ├─ AttributeCalculationStrategy strategy                       │
│  ├─ calculateHp(level)                                          │
│  ├─ calculateAttack(level)                                      │
│  ├─ calculateDefense(level)                                     │
│  ├─ calculateArmor(level)                                       │
│  └─ calculateArmorToughness(level)                              │
│                                                                  │
│  CombatStatsNBT.java                                            │
│  ├─ readFromNBT(CompoundTag)                                   │
│  └─ writeToNBT(CompoundTag)                                    │
│                                                                  │
│  ProtectionStatsNBT.java                                        │
│  ├─ readFromNBT(CompoundTag)                                   │
│  └─ writeToNBT(CompoundTag)                                    │
│                                                                  │
│  EnchantmentStatsNBT.java                                       │
│  ├─ readFromNBT(CompoundTag)                                   │
│  └─ writeToNBT(CompoundTag)                                    │
│                                                                  │
│  RobotRegistryManager.java                                      │
│  ├─ getRegistry(ServerLevel)                                   │
│  └─ clearRegistry(serverId)                                    │
└─────────────────────────────────────────────────────────────────┘
                              ↓ uses
┌─────────────────────────────────────────────────────────────────┐
│                      FRAMEWORK LAYER                             │
│                        (Pure Java)                               │
├─────────────────────────────────────────────────────────────────┤
│  AttributeCalculationStrategy (interface)                       │
│  ├─ calculateHp(level, baseValue)                              │
│  ├─ calculateAttack(level, baseValue)                          │
│  ├─ calculateDefense(level, baseValue)                         │
│  ├─ calculateArmor(defense)                                    │
│  └─ calculateArmorToughness(armorLevel)                        │
│                                                                  │
│  LinearAttributeStrategy (implementation)                       │
│  └─ 2% increase per level (current InternalLogic formula)      │
│                                                                  │
│  ExponentialAttributeStrategy (implementation)                  │
│  └─ Configurable % increase per level                          │
│                                                                  │
│  CombatStats.java                                               │
│  ├─ level, experience, currentHp, maxHp                        │
│  └─ attack, defense                                             │
│                                                                  │
│  ProtectionStats.java                                           │
│  ├─ fireProtection, fallProtection                             │
│  ├─ blastProtection, projectileProtection                      │
│  └─ canUpgradeFire(maxLevel)                                   │
│                                                                  │
│  EnchantmentStats.java                                          │
│  ├─ lootingLevel, sharpnessLevel                               │
│  └─ knockbackLevel                                              │
│                                                                  │
│  OwnerRobotRegistry.java                                        │
│  ├─ Map<UUID, Set<RobotRegistryEntry>>                         │
│  ├─ canSpawnRobot(ownerId, maxRobots)                          │
│  ├─ registerRobot(entry)                                       │
│  ├─ unregisterRobot(robotId)                                   │
│  ├─ getRobotsForOwner(ownerId)                                 │
│  └─ updateRobotPosition(robotId, ...)                          │
│                                                                  │
│  RobotRegistryEntry.java                                        │
│  ├─ robotId, ownerId, robotName, robotType                     │
│  └─ dimension, posX, posY, posZ, lastUpdate                    │
└─────────────────────────────────────────────────────────────────┘
```

## Data Flow Diagrams

### 1. Robot Spawn Flow

```
Player uses spawn item
        ↓
Check registry: canSpawnRobot(player, maxRobots)?
        ↓
    ┌───┴───┐
    NO      YES
    ↓       ↓
  Fail    Spawn robot
          ↓
        setTame(true)
          ↓
        registerRobot()
          ↓
        Registry updated
```

### 2. Level-Up Flow

```
Robot gains experience
        ↓
combatStats.addExperience(amount)
        ↓
Check: canLevelUp()?
        ↓
    ┌───┴───┐
    NO      YES
    ↓       ↓
  Done    tryLevelUp()
          ↓
        combatStats.setLevel(newLevel)
          ↓
        recalculateAttributes()
          ↓
        CombatLevelFeature.calculateHp(level)
        CombatLevelFeature.calculateAttack(level)
        CombatLevelFeature.calculateDefense(level)
        CombatLevelFeature.calculateArmor(level)
        CombatLevelFeature.calculateArmorToughness(level)
          ↓
        Update entity attributes
          ↓
        Sync to client
```

### 3. Command Execution Flow

```
/llovelyr robot stats level 150
        ↓
Parse command
        ↓
Find target robot (raycast)
        ↓
Get robot's max level from RobotEntityType
        ↓
Validate: newLevel <= maxLevel?
        ↓
    ┌───┴───┐
    NO      YES
    ↓       ↓
  Error   setLevel(150)
          ↓
        recalculateAttributes()
          ↓
        Success message
```

### 4. Registry Update Flow

```
Robot ticks (every 20 ticks)
        ↓
updateRegistryPosition()
        ↓
Get registry for current level
        ↓
registry.updateRobotPosition(
    robotId,
    dimension,
    x, y, z
)
        ↓
Registry entry updated
```

### 5. NBT Save/Load Flow

```
Save:
Entity.addAdditionalSaveData(nbt)
        ↓
combatStatsNBT.writeToNBT(nbt)
protectionStatsNBT.writeToNBT(nbt)
enchantmentStatsNBT.writeToNBT(nbt)
        ↓
NBT written to disk

Load:
Entity.readAdditionalSaveData(nbt)
        ↓
combatStatsNBT.readFromNBT(nbt)
protectionStatsNBT.readFromNBT(nbt)
enchantmentStatsNBT.readFromNBT(nbt)
        ↓
recalculateAttributes()
        ↓
Entity restored
```

## Class Relationships

```
RobotEntityType
    ├─ has Feature: LevelFeature
    ├─ has Feature: CombatLevelFeature
    └─ has Feature: [other features]

InternalEntity
    ├─ has: CombatStats
    ├─ has: ProtectionStats
    ├─ has: EnchantmentStats
    ├─ has: CombatStatsNBT
    ├─ has: ProtectionStatsNBT
    ├─ has: EnchantmentStatsNBT
    └─ uses: RobotEntityType

CombatLevelFeature
    ├─ has: AttributeCalculationStrategy
    └─ uses: CombatStats (for calculations)

OwnerRobotRegistry
    ├─ has: Map<UUID, Set<RobotRegistryEntry>>
    └─ has: Map<UUID, RobotRegistryEntry>

RobotRegistryManager
    └─ manages: OwnerRobotRegistry per server
```

## Package Structure

```
net.msymbios.llovelyr/
├── framework/                    [Pure Java - Zero MC deps]
│   ├── entity/
│   │   ├── combat/
│   │   │   ├── AttributeCalculationStrategy.java
│   │   │   ├── LinearAttributeStrategy.java
│   │   │   └── ExponentialAttributeStrategy.java
│   │   └── data/
│   │       ├── CombatStats.java
│   │       ├── ProtectionStats.java
│   │       └── EnchantmentStats.java
│   └── registry/
│       ├── OwnerRobotRegistry.java
│       └── RobotRegistryEntry.java
│
├── lib/                          [Loader-Specific - Minimal MC deps]
│   ├── entity/
│   │   ├── type/
│   │   │   └── features/
│   │   │       └── CombatLevelFeature.java
│   │   └── data/
│   │       ├── CombatStatsNBT.java
│   │       ├── ProtectionStatsNBT.java
│   │       └── EnchantmentStatsNBT.java
│   └── registry/
│       └── RobotRegistryManager.java
│
├── common/                       [Mod-Specific Shared]
│   ├── entity/
│   │   └── internal/
│   │       └── InternalEntity.java
│   └── commands/
│       └── LovelyRobotCommands.java
│
└── source/                       [Loader-Specific Implementation]
    ├── entity/
    │   └── type/
    │       └── NativeRobotType.java
    └── LovelyCommands.java
```

---

**Note**: This architecture ensures clean separation of concerns, testability, and future extractability to external HZLib library.
