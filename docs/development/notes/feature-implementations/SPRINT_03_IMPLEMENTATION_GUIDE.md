# Sprint 03 Implementation Guide

**Sprint**: Sprint 03 - New Interactive Features
**Date**: 2025-11-25
**Previous Sprint**: Sprint 02 - NBT Recipe System (Completed 2025-11-23)
**Variant**: Legacy
**Version**: MC 1.20.1
**Loaders**: Forge + Fabric

---

## Quick Reference

### Tasks Overview
1. **Robot Retrieval System** (5 pts, 4-6 hrs) - Stick interaction to convert robot → spawn item
2. **Robot Command System** (13 pts, 8-12 hrs) - Comprehensive admin commands
3. **Robot Core Glow Effect** (3 pts, 2-3 hrs) - Glowing outline on dropped cores
4. **Smart Core Retrieval** (5 pts, 3-4 hrs) - Distance-based auto-retrieval

**Total**: 26 story points, 17-25 hours estimated

---

## Implementation Order

### Phase 1: Quick Wins (Days 1-2)
**Goal**: Implement straightforward features first

#### Task 1: Robot Retrieval System
**File**: `sources/legacy/llovelyr-1.20.1/Forge/src/main/java/net/msymbios/llovelyr/common/entity/LovelyRobot.java`

**Implementation Steps**:
1. Locate `mobInteract()` method
2. Add stick detection after existing interactions:
```java
// After existing interactions (book, sword, compass, etc.)
if (itemStack.is(Items.STICK)) {
    if (this.isOwnedBy(player)) {
        // Create spawn item with full NBT
        ItemStack spawnItem = createSpawnItemFromEntity();
        
        // Add to player inventory
        if (!player.getInventory().add(spawnItem)) {
            // Drop if inventory full
            player.drop(spawnItem, false);
        }
        
        // Particle effect
        this.level().addParticle(ParticleTypes.POOF, 
            this.getX(), this.getY() + 0.5, this.getZ(),
            0.0, 0.0, 0.0);
        
        // Sound effect
        this.level().playSound(null, this.blockPosition(), 
            SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 
            1.0F, 1.0F);
        
        // Remove entity
        this.discard();
        
        return InteractionResult.SUCCESS;
    }
    return InteractionResult.FAIL; // Not owner
}
```

3. Create helper method `createSpawnItemFromEntity()`:
```java
private ItemStack createSpawnItemFromEntity() {
    // Get appropriate spawn item based on robot type
    Item spawnItem = this.getVariant() == EntityVariant.VANILLA 
        ? LovelyItems.VANILLA_SPAWN.get() 
        : LovelyItems.BUNNY2_SPAWN.get();
    
    ItemStack stack = new ItemStack(spawnItem);
    CompoundTag nbt = new CompoundTag();
    
    // Write all robot data to NBT
    this.writeNBT(nbt); // Use existing NBT serialization
    
    stack.setTag(nbt);
    return stack;
}
```

**Testing Checklist**:
- [ ] Works with Vanilla robot
- [ ] Works with Bunny2 robot
- [ ] Preserves level and XP
- [ ] Preserves all protections
- [ ] Preserves custom name
- [ ] Preserves color variant
- [ ] Preserves owner UUID
- [ ] Handles full inventory (drops item)
- [ ] Only owner can retrieve
- [ ] Particle effect appears
- [ ] Sound effect plays

---

#### Task 3: Robot Core Glow Effect
**File**: `sources/legacy/llovelyr-1.20.1/Forge/src/main/java/net/msymbios/llovelyr/common/entity/LovelyRobot.java`

**Implementation Steps**:
1. Locate `dropCustomDeathLoot()` method or death handling
2. Modify core drop logic:
```java
@Override
protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
    super.dropCustomDeathLoot(source, looting, recentlyHit);
    
    // Create core item
    ItemStack coreStack = new ItemStack(LovelyItems.ROBOT_CORE.get());
    CompoundTag nbt = new CompoundTag();
    this.writeNBT(nbt);
    coreStack.setTag(nbt);
    
    // Drop as item entity
    ItemEntity itemEntity = new ItemEntity(
        this.level(), 
        this.getX(), 
        this.getY(), 
        this.getZ(), 
        coreStack
    );
    
    // Add glowing effect
    itemEntity.setGlowingTag(true);
    
    // Optional: Set custom glow color based on robot color
    // This may require custom rendering
    
    this.level().addFreshEntity(itemEntity);
}
```

**Alternative Approach** (if color customization needed):
Create custom ItemEntity class with custom renderer for colored glow.

**Testing Checklist**:
- [ ] Core drops on robot death
- [ ] Core has glowing effect
- [ ] Glow visible through walls
- [ ] Works with all robot types
- [ ] Works with all color variants
- [ ] No performance issues with multiple cores
- [ ] Glow persists until pickup

---

### Phase 2: Smart Systems (Days 3-4)

#### Task 4: Smart Core Retrieval
**Files**: 
- `sources/legacy/llovelyr-1.20.1/Forge/src/main/java/net/msymbios/llovelyr/source/configs/LovelyConfigs.java`
- `sources/legacy/llovelyr-1.20.1/Forge/src/main/java/net/msymbios/llovelyr/common/entity/LovelyRobot.java`

**Implementation Steps**:

1. **Add Configuration** (`LovelyConfigs.java`):
```java
public static ForgeConfigSpec.BooleanValue ENABLE_SMART_CORE_RETRIEVAL;
public static ForgeConfigSpec.DoubleValue SMART_CORE_RETRIEVAL_DISTANCE;

// In builder section
ENABLE_SMART_CORE_RETRIEVAL = BUILDER
    .comment("Enable automatic core retrieval when robot dies near owner")
    .define("enableSmartCoreRetrieval", true);

SMART_CORE_RETRIEVAL_DISTANCE = BUILDER
    .comment("Maximum distance for automatic core retrieval (in blocks)")
    .defineInRange("smartCoreRetrievalDistance", 16.0, 0.0, 128.0);
```

2. **Modify Death Logic** (`LovelyRobot.java`):
```java
@Override
protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
    super.dropCustomDeathLoot(source, looting, recentlyHit);
    
    // Create core item
    ItemStack coreStack = new ItemStack(LovelyItems.ROBOT_CORE.get());
    CompoundTag nbt = new CompoundTag();
    this.writeNBT(nbt);
    coreStack.setTag(nbt);
    
    // Check if smart retrieval is enabled
    if (LovelyConfigs.ENABLE_SMART_CORE_RETRIEVAL.get()) {
        Player owner = this.getOwner();
        
        if (owner != null && owner.isAlive()) {
            double distance = this.distanceTo(owner);
            double maxDistance = LovelyConfigs.SMART_CORE_RETRIEVAL_DISTANCE.get();
            
            if (distance <= maxDistance) {
                // Auto-retrieve to inventory
                if (owner.getInventory().add(coreStack)) {
                    // Success - spawn particles and send message
                    this.level().addParticle(ParticleTypes.HAPPY_VILLAGER,
                        this.getX(), this.getY() + 0.5, this.getZ(),
                        0.0, 0.1, 0.0);
                    
                    owner.displayClientMessage(
                        Component.literal("Your robot's core has been retrieved"), 
                        true
                    );
                    
                    return; // Don't drop core
                }
            } else {
                // Beyond range - notify owner
                owner.displayClientMessage(
                    Component.literal("Your robot's core has been dropped at " + 
                        this.blockPosition().toShortString()), 
                    false
                );
            }
        }
    }
    
    // Fallback: Drop core with glow effect (Task 3 implementation)
    ItemEntity itemEntity = new ItemEntity(
        this.level(), 
        this.getX(), 
        this.getY(), 
        this.getZ(), 
        coreStack
    );
    itemEntity.setGlowingTag(true);
    this.level().addFreshEntity(itemEntity);
}
```

**Testing Checklist**:
- [ ] Auto-retrieval works within range
- [ ] Core drops beyond range
- [ ] Config enable/disable works
- [ ] Config distance adjustment works
- [ ] Handles full inventory (drops if no space)
- [ ] Handles offline owner (drops core)
- [ ] Particle effect on auto-retrieval
- [ ] Message sent to owner (both cases)
- [ ] Works in multiplayer

---

### Phase 3: Command System (Days 5-7)

#### Task 2: Robot Command System
**Files**: 
- New: `sources/legacy/llovelyr-1.20.1/Forge/src/main/java/net/msymbios/llovelyr/source/commands/LovelyRobotCommand.java`
- `sources/legacy/llovelyr-1.20.1/Forge/src/main/java/net/msymbios/llovelyr/LovelyLegacy.java` (register command)

**Implementation Steps**:

1. **Create Command Class**:
```java
package net.msymbios.llovelyr.source.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.msymbios.llovelyr.common.entity.LovelyRobot;

public class LovelyRobotCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("lovelyrobot")
                .requires(source -> source.hasPermission(2)) // OP level 2
                .then(statsCommands())
                .then(enchantCommands())
                .then(protectionCommands())
                .then(designCommands())
                .then(ownerCommands())
                .then(nameCommands())
        );
    }
    
    // Stats commands
    private static ArgumentBuilder<CommandSourceStack, ?> statsCommands() {
        return Commands.literal("stats")
            .then(Commands.literal("set")
                .then(Commands.argument("target", EntityArgument.entities())
                    .then(Commands.literal("level")
                        .then(Commands.argument("value", IntegerArgumentType.integer(1, 200))
                            .executes(ctx -> setLevel(ctx))
                        )
                    )
                    .then(Commands.literal("xp")
                        .then(Commands.argument("value", IntegerArgumentType.integer(0))
                            .executes(ctx -> setXP(ctx))
                        )
                    )
                    // Add more stat commands...
                )
            );
    }
    
    // Implementation methods
    private static int setLevel(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "target");
        int level = IntegerArgumentType.getInteger(ctx, "value");
        int count = 0;
        
        for (Entity entity : entities) {
            if (entity instanceof LovelyRobot robot) {
                robot.setLevel(level);
                count++;
            }
        }
        
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set level to " + level + " for " + count + " robot(s)"),
            true
        );
        
        return count;
    }
    
    // Add more command implementations...
}
```

2. **Register Command** (`LovelyLegacy.java`):
```java
@SubscribeEvent
public static void onRegisterCommands(RegisterCommandsEvent event) {
    LovelyRobotCommand.register(event.getDispatcher());
}
```

**Command Structure**:
```
/lovelyrobot stats set <target> level <value>
/lovelyrobot stats set <target> xp <value>
/lovelyrobot stats set <target> hp <value>
/lovelyrobot stats set <target> attack <value>
/lovelyrobot stats set <target> defense <value>
/lovelyrobot stats set <target> speed <value>
/lovelyrobot stats set <target> all <hp> <attack> <defense> <speed>

/lovelyrobot enchant set <target> looting <level>
/lovelyrobot enchant set <target> all <looting>

/lovelyrobot protection set <target> fire <level>
/lovelyrobot protection set <target> fall <level>
/lovelyrobot protection set <target> blast <level>
/lovelyrobot protection set <target> projectile <level>
/lovelyrobot protection set <target> all <fire> <fall> <blast> <projectile>

/lovelyrobot design set <target> <color>

/lovelyrobot owner get <target>
/lovelyrobot owner set <target> <player>

/lovelyrobot name set <target> <name>
```

**Testing Checklist**:
- [ ] All commands registered
- [ ] Permission checks work
- [ ] Entity selectors work (@e, @p, etc.)
- [ ] Multiple robots can be targeted
- [ ] Invalid values rejected
- [ ] Feedback messages display
- [ ] Changes persist (NBT saved)
- [ ] Changes sync in multiplayer
- [ ] Autocomplete works
- [ ] Help text displays

---

## Fabric Implementation Notes

For each task, after Forge implementation:

1. **Check API Differences**:
   - Fabric uses different event systems
   - Command registration may differ
   - Configuration system is different (Fabric API config)

2. **Create Fabric Equivalents**:
   - Port Forge code to Fabric API
   - Test both loaders independently
   - Ensure feature parity

3. **Common Code**:
   - Move shared logic to `Common/` if possible
   - Use loader-specific wrappers for platform code

---

## Testing Strategy

### Unit Testing (Per Task)
- Test each feature individually
- Verify all acceptance criteria
- Test edge cases and error conditions

### Integration Testing (After All Tasks)
- Test features working together
- Test with multiple robots
- Test in various scenarios (combat, exploration, etc.)

### Multiplayer Testing
- Test client-server synchronization
- Test with multiple players
- Test permission systems

### Performance Testing
- Test with 10+ robots
- Test with multiple cores dropped
- Profile for lag or memory issues

---

## Documentation Updates

After implementation, update:

1. **CURRENT_STATE.md**:
   - Add new features to component catalog
   - Update feature completeness percentages
   - Document new configuration options

2. **SPRINT_PLANNING.md**:
   - Update sprint progress
   - Add retrospective notes
   - Document velocity

3. **November 2025 Checklist**:
   - Mark tasks as complete
   - Update progress percentages

4. **CHANGELOG.md** (when releasing):
   - Document new features
   - Note configuration options
   - Provide usage examples

---

## Commit Strategy

Follow GIT_COMMIT_GUIDELINES.md:

```
FEAT: Add robot retrieval system with stick interaction
FEAT: Implement comprehensive robot command system
FEAT: Add glowing effect to dropped robot cores
FEAT: Implement smart core retrieval with distance check
DOCS: Update sprint documentation for new features
TEST: Add tests for new interactive features
```

---

## Success Metrics

### Sprint Success Criteria
- [ ] All 4 tasks completed
- [ ] All acceptance criteria met
- [ ] Code follows style guide 100%
- [ ] Works in both Forge and Fabric
- [ ] Multiplayer compatible
- [ ] No critical bugs
- [ ] Documentation updated

### Quality Metrics
- [ ] JavaDoc comments on all public methods
- [ ] Error handling for all edge cases
- [ ] Configuration options for customization
- [ ] User feedback (messages, particles, sounds)
- [ ] Performance acceptable (no lag)

---

## Risk Mitigation

### Potential Issues

1. **Command System Complexity**
   - **Risk**: Brigadier learning curve
   - **Mitigation**: Start with simple commands, reference Minecraft source code
   - **Fallback**: Implement basic commands first, expand later

2. **Glow Effect Customization**
   - **Risk**: May not support custom colors easily
   - **Mitigation**: Start with vanilla glow, add custom rendering if needed
   - **Fallback**: Use particle effects instead

3. **Multiplayer Synchronization**
   - **Risk**: Changes may not sync properly
   - **Mitigation**: Test thoroughly, use proper packet handling
   - **Fallback**: Force client refresh on changes

4. **Fabric API Differences**
   - **Risk**: Fabric implementation may differ significantly
   - **Mitigation**: Research Fabric equivalents early
   - **Fallback**: Implement Forge first, port to Fabric after

---

## Next Steps After Sprint 03

### Sprint 04: Remaining Robot Types
- Implement Honey robot
- Implement Bunny robot
- Implement Dragon robot
- Implement Neko robot
- Implement Kitsune robot

### Future Enhancements
- Robot GUI for management
- Robot whistle item
- Robot backpack/inventory
- Robot home location system
- Robot teleport command
- Robot heal command

---

**Document Status**: Active Implementation Guide
**Last Updated**: 2025-11-25
**Sprint**: Sprint 03
**Previous Sprint**: Sprint 02 - NBT Recipe System (Completed 2025-11-23)
**Next Review**: Daily during sprint
