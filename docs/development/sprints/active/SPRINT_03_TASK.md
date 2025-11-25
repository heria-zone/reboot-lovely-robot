# Sprint 03 Task - New Interactive Features Implementation

**Status**: Active
**Sprint**: Sprint 03
**Start Date**: 2025-11-25
**Target End Date**: 2025-12-06
**Variant**: Legacy
**Version**: MC 1.20.1
**Loaders**: Forge + Fabric
**Related Documents**:
- [SPRINT_PLANNING.md](../../../workflow/SPRINT_PLANNING.md)
- [CURRENT_STATE.md](../../../workflow/CURRENT_STATE.md)
- [November 2025 Development Checklist](../../tasks/November-2025-Development-Checklist.md)

---

## Sprint Goal

Implement new interactive features for Legacy 1.20.1 robots to enhance player experience and control.

---

## Sprint Context

**Previous Sprints**:
- **Sprint 01**: Vanilla & Bunny2 Robot Full Implementation (Completed 2025-11-25)
- **Sprint 02**: NBT Recipe System Implementation (Completed 2025-11-23)

**Current Sprint**: Sprint 03 - New Interactive Features

---

## Unforeseen Work

### Discovery Context
**Discovery Date**: 2025-11-25  
**Discovered By**: User request  
**Reason**: User identified valuable quality-of-life features not in original planning  

These features were not part of the original Sprint 03 plan (which was intended for remaining robot types), but provide significant value to the player experience and should be implemented before expanding robot types.

**Impact on Planning**:
- Original Sprint 03 (Remaining Robot Types) postponed to Sprint 04
- These QoL features take priority due to user request and immediate value

---

## Tasks

### Task 1: Robot Retrieval System (Stick Interaction)
**Priority**: High  
**Status**: Not Started  
**Estimated Effort**: 4-6 hours  
**Story Points**: 5

**Description**:
Implement ability for robot owners to convert their robots back into spawn items by interacting with a stick, preserving all robot data (level, protections, name, color, etc.).

**Acceptance Criteria**:
- [ ] Right-click robot with stick while owner
- [ ] Robot entity despawns
- [ ] LovelySpawnItem appears in player inventory
- [ ] All NBT data preserved (name, level, XP, protections, color, owner)
- [ ] Particle effect on conversion
- [ ] Sound effect on conversion
- [ ] Works for all robot types (Vanilla, Bunny2)
- [ ] Proper permission check (only owner can retrieve)
- [ ] Works in both Forge and Fabric

**Implementation Notes**:
- Add to `LovelyRobot.mobInteract()` method
- Check for stick item: `Items.STICK`
- Verify player is owner: `isOwnedBy(player)`
- Create spawn item with full NBT: `LovelySpawnItem.createWithNBT()`
- Transfer all data: level, XP, protections, color, name, owner UUID
- Add to player inventory: `player.getInventory().add()`
- Spawn particles: `ParticleTypes.POOF` or custom
- Play sound: `SoundEvents.ITEM_PICKUP` or custom
- Remove entity: `this.discard()`

**Related Components**:
- `LovelyRobot.java` - Add stick interaction handler
- `LovelySpawnItem.java` - May need `createFromEntity()` helper method
- `InternalParticle.java` - Particle effects
- Existing NBT serialization methods

**Testing**:
- Test with level 1 robot
- Test with max level robot
- Test with all protection types
- Test with custom name
- Test with all color variants
- Test inventory full scenario
- Test non-owner interaction (should fail)
- Test in multiplayer

---

### Task 2: Robot Command System
**Priority**: High
**Status**: Not Started
**Estimated Effort**: 8-12 hours
**Story Points**: 13

**Description**:
Create comprehensive command system for managing robot stats, enchantments, protections, design, ownership, and names.

**Acceptance Criteria**:
- [ ] `/lovelyrobot stats set <target> <stat> <value>` - Set individual stat
- [ ] `/lovelyrobot stats set <target> all <hp> <attack> <defense> <speed>` - Set all stats
- [ ] `/lovelyrobot enchant set <target> <enchantment> <level>` - Set individual enchantment
- [ ] `/lovelyrobot enchant set <target> all <looting>` - Set all enchantments
- [ ] `/lovelyrobot protection set <target> <type> <level>` - Set individual protection
- [ ] `/lovelyrobot protection set <target> all <fire> <fall> <blast> <projectile>` - Set all protections
- [ ] `/lovelyrobot design set <target> <color>` - Set robot color
- [ ] `/lovelyrobot owner get <target>` - Get robot owner
- [ ] `/lovelyrobot owner set <target> <player>` - Set robot owner
- [ ] `/lovelyrobot name set <target> <name>` - Set robot name
- [ ] Permission checks (OP level 2 or higher)
- [ ] Target selector support (@e[type=llovelyr:vanilla], @e[type=llovelyr:bunny2])
- [ ] Feedback messages for all commands
- [ ] Error handling for invalid values
- [ ] Works in both Forge and Fabric

**Command Structure**:
```
/lovelyrobot <category> <action> <target> [parameters...]

Categories:
- stats: Manage robot statistics
- enchant: Manage robot enchantments
- protection: Manage robot protections
- design: Manage robot appearance
- owner: Manage robot ownership
- name: Manage robot name

Actions:
- set: Set value(s)
- get: Get value(s) (where applicable)

Target:
- Entity selector (@e, @p, @a, @r, @s)
- UUID
- "all" for all stats/protections/enchantments
```

**Implementation Notes**:
- Create `LovelyRobotCommand.java` in `source/commands/`
- Register command in mod initialization
- Use Brigadier command system
- Implement argument types for stats, enchantments, protections, colors
- Add permission checks: `source.hasPermission(2)`
- Implement entity selector parsing
- Add validation for stat ranges (level 1-200, protections 0-max, etc.)
- Send feedback messages to command source
- Update robot NBT data
- Sync changes to clients in multiplayer

**Subcommands to Implement**:

1. **Stats Commands**:
   - `stats set <target> hp <value>` - Set max health
   - `stats set <target> attack <value>` - Set attack damage
   - `stats set <target> defense <value>` - Set defense value
   - `stats set <target> speed <value>` - Set movement speed
   - `stats set <target> level <value>` - Set level (recalculates stats)
   - `stats set <target> xp <value>` - Set experience points
   - `stats set <target> all <hp> <attack> <defense> <speed>` - Set all at once

2. **Enchantment Commands**:
   - `enchant set <target> looting <level>` - Set looting level
   - `enchant set <target> all <looting>` - Set all enchantments

3. **Protection Commands**:
   - `protection set <target> fire <level>` - Set fire protection
   - `protection set <target> fall <level>` - Set fall protection
   - `protection set <target> blast <level>` - Set blast protection
   - `protection set <target> projectile <level>` - Set projectile protection
   - `protection set <target> all <fire> <fall> <blast> <projectile>` - Set all protections

4. **Design Commands**:
   - `design set <target> <color>` - Set robot color (16 dye colors)

5. **Owner Commands**:
   - `owner get <target>` - Display robot owner
   - `owner set <target> <player>` - Transfer ownership

6. **Name Commands**:
   - `name set <target> <name>` - Set custom name

**Related Components**:
- New: `LovelyRobotCommand.java` - Command registration and handlers
- New: `commands/` package structure
- `LovelyRobot.java` - Add setter methods if needed
- `LovelyConfigs.java` - Validation ranges
- `EntityTexture.java` - Color enum
- NBT serialization methods

**Testing**:
- Test each command individually
- Test with entity selectors (@e, @p, etc.)
- Test with multiple robots selected
- Test permission checks
- Test invalid values (out of range)
- Test invalid targets
- Test in multiplayer (sync verification)
- Test command autocomplete
- Test feedback messages

---

### Task 3: Robot Core Glow Effect
**Priority**: Medium
**Status**: Not Started
**Estimated Effort**: 2-3 hours
**Story Points**: 3

**Description**:
Add glowing outline effect to dropped robot cores so players can easily locate them, especially through walls.

**Acceptance Criteria**:
- [ ] Robot cores have glowing effect when dropped
- [ ] Glow visible through walls (like spectral arrow effect)
- [ ] Glow color matches robot color variant
- [ ] Effect persists until core is picked up
- [ ] Works for all robot types
- [ ] Performance optimized (no lag with multiple cores)
- [ ] Works in both Forge and Fabric

**Implementation Notes**:
- Modify `LovelyCoreItem.java` or entity drop logic
- Use Minecraft's glowing effect: `Entity.setGlowingTag(true)`
- Set glow color based on robot color variant
- Apply effect when core is dropped as item entity
- Consider using `ItemEntity` with custom renderer
- Alternative: Custom render layer for glowing effect
- Ensure effect is client-side visible

**Technical Approaches**:
1. **Approach A**: Set glowing tag on ItemEntity
   - Pros: Simple, uses vanilla system
   - Cons: May not support custom colors easily

2. **Approach B**: Custom ItemEntity renderer
   - Pros: Full control over glow appearance
   - Cons: More complex implementation

3. **Approach C**: Particle effects around core
   - Pros: Highly visible, customizable
   - Cons: May impact performance with many cores

**Recommended**: Start with Approach A, fallback to B if color customization needed

**Related Components**:
- `LovelyCoreItem.java` - Core item class
- `LovelyRobot.java` - Drop logic in `dropCustomDeathLoot()`
- Rendering system for item entities
- `EntityTexture.java` - Color variants

**Testing**:
- Test core drop from robot death
- Test glow visibility through walls
- Test with all color variants
- Test with multiple cores nearby
- Test performance with 10+ cores
- Test in different lighting conditions
- Test in multiplayer

---

### Task 4: Smart Core Retrieval System
**Priority**: Medium
**Status**: Not Started
**Estimated Effort**: 3-4 hours
**Story Points**: 5

**Description**:
Implement distance-based core retrieval where robot cores automatically appear in owner's inventory if they die within a configurable range, otherwise drop with glow effect.

**Acceptance Criteria**:
- [ ] Configurable distance threshold (default: 16 blocks)
- [ ] If owner within range: core goes directly to inventory
- [ ] If owner beyond range: core drops with glow effect
- [ ] Particle effect indicates auto-retrieval
- [ ] Sound effect on auto-retrieval
- [ ] Message to owner on auto-retrieval
- [ ] Works for all robot types
- [ ] Configuration option to enable/disable feature
- [ ] Configuration option to set distance threshold
- [ ] Works in both Forge and Fabric

**Implementation Notes**:
- Add config values to `LovelyConfigs.java`:
  - `enableSmartCoreRetrieval` (boolean, default: true)
  - `smartCoreRetrievalDistance` (double, default: 16.0)
- Modify `LovelyRobot.dropCustomDeathLoot()` or death handling
- Check if owner is online and within range
- Calculate distance: `owner.distanceTo(robot)`
- If within range:
  - Create core item with NBT
  - Add to owner inventory: `owner.getInventory().add()`
  - Spawn particles at robot death location
  - Play sound for owner
  - Send message: "Your robot's core has been retrieved"
- If beyond range:
  - Drop core normally with glow effect (Task 3)
  - Send message: "Your robot's core has been dropped at [coordinates]"

**Configuration**:
```java
// In LovelyConfigs.java
public static final ForgeConfigSpec.BooleanValue ENABLE_SMART_CORE_RETRIEVAL;
public static final ForgeConfigSpec.DoubleValue SMART_CORE_RETRIEVAL_DISTANCE;

// In config builder
ENABLE_SMART_CORE_RETRIEVAL = builder
    .comment("Enable automatic core retrieval when robot dies near owner")
    .define("enableSmartCoreRetrieval", true);

SMART_CORE_RETRIEVAL_DISTANCE = builder
    .comment("Maximum distance for automatic core retrieval (in blocks)")
    .defineInRange("smartCoreRetrievalDistance", 16.0, 0.0, 128.0);
```

**Related Components**:
- `LovelyRobot.java` - Death handling
- `LovelyConfigs.java` - Configuration values
- `LovelyCoreItem.java` - Core item creation
- `InternalParticle.java` - Particle effects
- Task 3 (Robot Core Glow Effect) - Fallback behavior

**Testing**:
- Test with owner within range (various distances)
- Test with owner exactly at threshold distance
- Test with owner beyond range
- Test with owner offline
- Test with full inventory (should drop if no space)
- Test config enable/disable
- Test config distance adjustment
- Test with multiple robots dying simultaneously
- Test in multiplayer
- Test particle and sound effects

---

## Sprint Summary

### Total Story Points: 26
- Task 1 (Robot Retrieval): 5 points
- Task 2 (Command System): 13 points
- Task 3 (Core Glow Effect): 3 points
- Task 4 (Smart Core Retrieval): 5 points

### Estimated Time: 17-25 hours
- Task 1: 4-6 hours
- Task 2: 8-12 hours
- Task 3: 2-3 hours
- Task 4: 3-4 hours

### Dependencies
- Task 3 must be completed before Task 4 (glow effect is fallback)
- Task 1 and Task 2 are independent
- All tasks depend on existing robot system (Sprint 01)
- All tasks depend on NBT system (Sprint 02)

### Risk Assessment
- **Low Risk**: Tasks 1, 3, 4 (straightforward implementations)
- **Medium Risk**: Task 2 (command system complexity, Brigadier learning curve)

---

## Implementation Strategy

### Phase 1: Core Features (Days 1-2)
1. Implement Task 1 (Robot Retrieval) - Quick win
2. Implement Task 3 (Core Glow Effect) - Foundation for Task 4

### Phase 2: Smart Systems (Days 3-4)
3. Implement Task 4 (Smart Core Retrieval) - Builds on Task 3

### Phase 3: Command System (Days 5-7)
4. Implement Task 2 (Command System) - Most complex, needs dedicated time

### Phase 4: Testing & Polish (Days 8-10)
5. Comprehensive in-game testing
6. Bug fixes
7. Documentation updates
8. Multiplayer testing

---

## Success Criteria

### Technical
- [ ] All code follows project coding style guide
- [ ] All public methods have JavaDoc comments
- [ ] No critical bugs
- [ ] Works in both Forge and Fabric
- [ ] Multiplayer compatible
- [ ] Performance acceptable

### Functional
- [ ] All acceptance criteria met for each task
- [ ] Features work as described
- [ ] User experience is smooth and intuitive
- [ ] Error handling is robust

### Documentation
- [ ] CURRENT_STATE.md updated with new features
- [ ] SPRINT_PLANNING.md updated with progress
- [ ] Code comments explain non-obvious logic
- [ ] Configuration options documented

---

## Notes

### Design Decisions
- **Stick for retrieval**: Consistent with existing stick interaction (stats display)
- **Command system**: Provides admin/creative control without breaking survival balance
- **Glow effect**: Improves quality of life, prevents lost robots
- **Smart retrieval**: Balances convenience with risk/reward

### Future Enhancements (Not in this sprint)
- Command to teleport robot to owner
- Command to heal robot
- Command to set robot home location
- GUI for robot management
- Robot whistle item for召唤
- Robot backpack/inventory system

---

## Daily Progress Log

### 2025-11-25 (Day 1)
**Status**: Sprint started, tasks documented
**Progress**: 
- Sprint 02 task file created
- Features documented following maintenance guide
- Unforeseen work properly categorized
- Implementation strategy defined

**Next Steps**:
- Begin Task 1 implementation (Robot Retrieval)
- Set up testing environment

**Blockers**: None

---

**Last Updated**: 2025-11-25
**Sprint**: Sprint 03
**Sprint Status**: Active
**Completion**: 0% (0/4 tasks complete)
**Previous Sprint**: Sprint 02 - NBT Recipe System (Completed 2025-11-23)
