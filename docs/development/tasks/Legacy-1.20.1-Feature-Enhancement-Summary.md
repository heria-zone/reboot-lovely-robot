---
created: 2025-11-30
status: Summary
tags:
  - Legacy
  - Enhancement
  - Quick-Reference
---

# Legacy 1.20.1 Feature Enhancement - Quick Summary

## What This Proposal Addresses

Your requested features have been refined into a comprehensive architectural proposal:

### 1. CombatLevelFeature, EnchantmentFeature & ProtectionFeature ✅
**Your Request**: "Create a CombatLevelFeature, this is basically a version of the InternalLogic calculate 'attributes' per level"

**Refined Solution**:
- **CombatLevelFeature**: HP, Attack, Defense, Armor calculations with strategy pattern
- **EnchantmentFeature**: Looting, Sharpness, Knockback calculations
- **ProtectionFeature**: Fire, Fall, Blast, Projectile protection limits and auto-unlock
- All follow the same composable feature pattern
- Strategy pattern for different calculation formulas (Linear, Exponential, Custom)
- Attached to RobotEntityType like LevelFeature
- Configurable per robot variant
- Follows HZ Framework → Lib → Common pattern

**Example**:
```java
// Vanilla with all three features
VANILLA
    .withFeature(CombatLevelFeature.class, 
        new CombatLevelFeature(baseHp, baseAttack, baseDefense))
    .withFeature(EnchantmentFeature.class,
        new EnchantmentFeature(lootingEnabled, maxLooting, lootingDivisor))
    .withFeature(ProtectionFeature.class,
        new ProtectionFeature(maxFire, maxFall, maxBlast, maxProjectile));

// Dragon with higher limits and exponential scaling
DRAGON
    .withFeature(CombatLevelFeature.class,
        new CombatLevelFeature(baseHp, baseAttack, baseDefense, 
            new ExponentialAttributeStrategy(1.05)))
    .withFeature(EnchantmentFeature.class,
        new EnchantmentFeature(true, maxLooting + 2, lootingDivisor))
    .withFeature(ProtectionFeature.class,
        new ProtectionFeature(maxFire + 2, maxFall, maxBlast + 1, maxProjectile));
```

### 2. Data Management Refactoring ✓
**Your Request**: "Convert all the InternalLogic into features, we need to add a new type of data, for Enchantments/protections similar to withCombatStats"

**Refined Solution**:
- `CombatStats` class - level, exp, hp, attack, defense
- `ProtectionStats` class - fire, fall, blast, projectile protections
- `EnchantmentStats` class - looting, sharpness, knockback
- Each class handles its own NBT serialization via `IReadWriteNBT`
- Centralized in entity, not scattered across methods

**Example**:
```java
// In InternalEntity
protected CombatStats combatStats;
protected ProtectionStats protectionStats;
protected EnchantmentStats enchantmentStats;

// Simple accessors
public int getLevel() { return combatStats.getLevel(); }
public int getFireProtection() { return protectionStats.getFireProtection(); }
public int getLootingLevel() { return enchantmentStats.getLootingLevel(); }

// Automatic NBT handling
@Override
public void readAdditionalSaveData(CompoundTag nbt) {
    combatStatsNBT.readFromNBT(nbt);
    protectionStatsNBT.readFromNBT(nbt);
    enchantmentStatsNBT.readFromNBT(nbt);
}
```

### 3. Robot Registry System ✓
**Your Request**: "Register and unregister robots, I would create something that can store a register of owner/player to a collection of robots"

**Refined Solution**:
- `OwnerRobotRegistry` - tracks robots per owner
- `RobotRegistryEntry` - stores **WeakReference to LivingEntity** for live data access
- Automatic registration on tame, unregistration on death/removal
- **Live position/health/stats** - no caching needed!
- Spawn limit enforcement
- Enables powerful commands (teleport, recall, heal, stats)

**Key Improvement**: Stores entity reference instead of cached data!

**Example**:
```java
// Check if player can spawn more robots
if (!registry.canSpawnRobot(player.getUUID(), maxRobots)) {
    player.displayClientMessage(
        Component.translatable("message.llovelyr.max_robots_reached", maxRobots),
        true
    );
    return InteractionResult.FAIL;
}

// List all robots with LIVE data
List<RobotRegistryEntry> robots = registry.getRobotsForOwner(player.getUUID());
for (RobotRegistryEntry entry : robots) {
    LivingEntity robot = entry.getEntity(); // Direct entity access!
    double[] pos = entry.getPosition(); // Always current position
    float health = entry.getHealth(); // Always current health
    String name = entry.getRobotName(); // Always current name
}

// Teleport to robot
LivingEntity robot = entry.getEntity();
if (robot != null && entry.isEntityValid()) {
    player.teleportTo(robot.level(), robot.getX(), robot.getY(), robot.getZ(), ...);
}
```

### 4. Command System Overhaul ✓
**Your Request**: "I want to change how the commands work, this would be the new format"

**Refined Solution**:
- `/llovelyr group <selector> <action> [args...]` - existing group commands
- `/llovelyr owner <player> list` - list all robots for player
- `/llovelyr owner <player> robot <index> <action> [args...]` - target by index
- `/llovelyr robot <action> [args...]` - target robot in front of you
- **NEW: Entity reference-enabled commands**:
  - `/llovelyr owner <player> teleport <index>` - Teleport to robot
  - `/llovelyr owner <player> recall <index>` - Recall robot to you
  - `/llovelyr owner <player> heal all` - Heal all robots
  - `/llovelyr owner <player> stats <index>` - Live stats display
- Full validation against config limits (max level, max protections)
- Auto-level-up when setting exp
- Ownership transfer with registry update

**Example Commands**:
```bash
# List all robots for player (with live health!)
/llovelyr owner PlayerName list
# Output: [0] Vanilla (MyRobot) | minecraft:overworld: 100.0, 64.0, 200.0 | ❤ 150/200

# Set level for specific robot (validates against max level)
/llovelyr owner PlayerName robot 0 stats level 150

# Set exp (auto-levels up if enough exp)
/llovelyr robot stats exp 10000

# Set protection (validates against config limit)
/llovelyr robot stats protection fire 5

# Change ownership (updates registry)
/llovelyr robot owner set NewPlayer

# Target robot in front of you
/llovelyr robot design purple

# NEW: Teleport to your robot
/llovelyr owner PlayerName teleport 0

# NEW: Recall robot to you
/llovelyr owner PlayerName recall 0

# NEW: Heal all your robots
/llovelyr owner PlayerName heal all

# NEW: View detailed stats
/llovelyr owner PlayerName stats 0
```


## Key Improvements Over Your Original Ideas

### 1. Strategy Pattern for Calculations
Instead of just moving InternalLogic to a feature, we use strategy pattern:
- Different robots can have different scaling formulas
- Easy to add new calculation strategies
- Config can select which strategy to use
- Testable in isolation

### 2. Proper Data Encapsulation
Instead of scattered get/set methods, we have:
- Dedicated stat classes with clear responsibilities
- Each class handles its own NBT serialization
- Centralized recalculation logic
- Type-safe access

### 3. Registry with Rich Data
Instead of just tracking count, we track:
- Robot UUID, name, type
- Current dimension and position
- Last update timestamp
- Easy to extend with more data (health, state, etc.)

### 4. Command Validation
Instead of just restructuring commands, we add:
- Validation against config limits
- Auto-level-up on exp set
- Better error messages
- Multiple targeting methods (selector, index, raycast)

## Architecture Benefits

### Follows Established Patterns
- HZ Framework (pure Java) → HZ Lib (loader-specific) → Common (mod-shared) → Source (loader-specific)
- Feature-based composition like LevelFeature
- Strategy pattern for extensibility
- Separation of concerns

### Future-Proof
- Easy to extract to external library
- Minimal refactoring needed
- Reusable across other mods
- Follows industry best practices

### Maintainable
- Clear ownership of data
- Testable components
- Well-documented
- Consistent with existing code

## Implementation Timeline

**Total Duration**: 20-27 days

1. **Framework Layer** (2-3 days) - Pure Java classes
2. **Lib Layer** (3-4 days) - Minecraft integration
3. **Common Layer** (4-5 days) - Entity integration
4. **Command System** (4-5 days) - New commands + entity reference commands
5. **Config Integration** (2-3 days) - Config updates
6. **Testing** (3-4 days) - Comprehensive testing
7. **Documentation** (2 days) - Docs and ADRs

## What You Need to Review

### 1. Architecture Decisions
- Is the strategy pattern approach acceptable?
- Do the stat classes cover all needed data?
- Is the registry structure sufficient?
- Are the command formats what you want?

### 2. Implementation Priorities
- Which phase should we start with?
- Are there any features that can be deferred?
- Do you want to implement Forge first, then Fabric?
- Should we do this in parallel with other work?

### 3. Config Changes
New config entries needed:
- `MaxOwnerRobots` (default: 10)
- Base stats per robot type (HP, Attack, Defense)
- Attribute calculation strategy selection (optional)

### 4. Backward Compatibility
- How important is loading old robot data?
- Should we provide a migration tool?
- Can we break compatibility if needed?

## Next Actions

1. **Review Proposal**: Read full proposal in `Legacy-1.20.1-Feature-Enhancement-Proposal.md`
2. **Provide Feedback**: What needs to change?
3. **Approve Architecture**: Green light the approach?
4. **Create Tasks**: Break down into implementation tasks
5. **Start Implementation**: Begin with Framework layer

## Questions for You

1. **Calculation Strategies**: Do you want different robots to use different formulas, or should all use the same?
2. **Registry Data**: What additional data should we track per robot (health, state, equipment)?
3. **Spawn Limits**: Should limits be global, per-dimension, or per-owner?
4. **Command Permissions**: Should some commands require higher permission levels?
5. **Enchantments**: Which enchantments should we support initially?
6. **Protections**: Are the 4 protection types sufficient, or do you want more?

## Files Created

1. **Main Proposal**: `docs/development/tasks/Legacy-1.20.1-Feature-Enhancement-Proposal.md`
   - Complete architectural design
   - Code examples
   - Implementation plan
   - Risk analysis

2. **This Summary**: `docs/development/tasks/Legacy-1.20.1-Feature-Enhancement-Summary.md`
   - Quick overview
   - Key improvements
   - What to review
   - Next actions

## Related Documents

- **HZLib Architecture**: `docs/development/HZLib-EntityType-Architecture.md`
- **Refactoring Plan**: `docs/development/Legacy-1.20.1-Refactoring-Plan.md`
- **Current Tasks**: `.kiro/specs/hzlib-entitytype-legacy-1-20-1/tasks.md`
- **Coding Standards**: `.kiro/steering/project-coding-style.md`

---

**Ready for Review**: Yes
**Awaiting**: Your feedback and approval
**Next Step**: Create detailed implementation tasks after approval
