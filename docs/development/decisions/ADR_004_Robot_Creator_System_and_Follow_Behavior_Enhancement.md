# ADR 004: Robot Creator System and Follow Behavior Enhancement

**Status**: Proposed  
**Date**: 2025-01-13  
**Decision Makers**: Development Team  
**Consulted**: Legacy 1.21.1 Codebase Analysis  

## Context

Two critical issues have been identified in the Legacy 1.21.1 robot system:

1. **Security Issue**: Players can spawn robots that don't belong to them by manipulating spawn item data, creating ownership bypass vulnerabilities. Additionally, when players collect defeated robot cores and rebuild them, they cannot spawn those robots since they're not the owners, creating confusion about ownership status.

2. **User Experience Issue**: Robot following behavior feels unnatural and delayed, with robots moving to where the player was rather than where they are, and lacking visual engagement during following.

## Decision

### Issue 1: Robot Creator System

**Implement a comprehensive ownership model** with Creator, Owner, and History concepts:

- **Creator**: Immutable identifier set when robot is first created, persists through all transfers
- **Owner**: Current controller of the robot, can change through ownership transfer mechanics
- **Ownership History**: List of previous owners with timestamps for statistical tracking
- **Spawn Validation**: Current Owner OR Creator can spawn a robot from a spawn item
- **Creator Easter Egg**: Original creator can always interact with their robots, even after ownership transfer
- **Visual Distinction**: Display Creator and ownership history when Creator ≠ Owner
- **Transfer Mechanics**: Programmatic API for blocks and commands to transfer ownership

### Issue 2: Follow Behavior Enhancement

**Enhance AiFollowOwnerGoal** with natural movement patterns:

- **Coordinated Look-At**: Robots look at their follow target with natural body rotation
- **Natural Body-Head Coordination**: Body and head rotate together for natural movement, avoiding awkward head-only turning
- **Position Prediction**: Predict owner movement to reduce lag in following behavior
- **Dynamic Update Frequency**: More frequent pathfinding updates when owner is moving

## Implementation Details

### Creator System Architecture

```java
// New data fields in LovelyIdentifier
public static final String STAT_CREATOR_UUID = "CreatorUUID";
public static final String STAT_OWNERSHIP_HISTORY = "OwnershipHistory";

// Enhanced validation - current owner OR creator can spawn
private static boolean validateOwnershipForSpawn(CompoundTag customTag, Player player) {
    String ownerUUID = customTag.getString(LovelyIdentifier.STAT_OWNER_UUID);
    String creatorUUID = customTag.getString(LovelyIdentifier.STAT_CREATOR_UUID);
    String playerUUID = player.getUUID().toString();
    
    return ownerUUID.isEmpty() || 
           ownerUUID.equals(playerUUID) || 
           creatorUUID.equals(playerUUID); // Creator easter egg
}

// Enhanced tooltip with ownership history
private static void addOwnershipTooltip(List<Component> tooltip, CompoundTag nbt, Player currentPlayer) {
    String creator = nbt.getString(LovelyIdentifier.STAT_CREATOR_UUID);
    String owner = nbt.getString(LovelyIdentifier.STAT_OWNER_UUID);
    String playerUUID = currentPlayer.getUUID().toString();
    
    if (!creator.isEmpty() && !creator.equals(owner)) {
        tooltip.add(Component.literal("Creator: " + getPlayerName(creator)));
        
        // Show owner status - faded/crossed when creator is viewing but not owner
        Component ownerText = Component.literal("Owner: " + getPlayerName(owner));
        if (creator.equals(playerUUID) && !owner.equals(playerUUID)) {
            // Creator viewing robot they no longer own - show owner as inactive
            ownerText = ownerText.withStyle(ChatFormatting.GRAY, ChatFormatting.STRIKETHROUGH);
        }
        tooltip.add(ownerText);
        
        // Show ownership history if present
        addOwnershipHistoryTooltip(tooltip, nbt);
    }
}

// Ownership transfer API
public static void transferOwnership(LovelyRobotEntity robot, Player newOwner) {
    String oldOwner = robot.getOwnerUUID().toString();
    String newOwnerUUID = newOwner.getUUID().toString();
    
    // Add to ownership history
    CompoundTag historyEntry = new CompoundTag();
    historyEntry.putString("owner", oldOwner);
    historyEntry.putLong("timestamp", System.currentTimeMillis());
    
    robot.addOwnershipHistoryEntry(historyEntry);
    robot.setOwner(newOwner);
}
```

### Follow Behavior Enhancement

```java
// Enhanced tick() method in AiFollowOwnerGoal
@Override
public void tick() {
    // Natural coordinated look-at behavior
    // Body rotates toward owner, head follows naturally with body rotation
    Vec3 ownerPos = new Vec3(this.owner.getX(), this.owner.getEyeY(), this.owner.getZ());
    
    // Use body rotation control for natural movement
    entity.getLookControl().setLookAt(
        ownerPos.x, 
        ownerPos.y, 
        ownerPos.z,
        8.0F,  // Moderate turn speed for natural movement
        entity.getMaxHeadXRot()
    );
    
    // Allow body to rotate naturally with head direction
    // This prevents awkward head-only turning while maintaining engagement
    
    // Dynamic path recalculation based on owner movement
    boolean ownerMoving = this.owner.getDeltaMovement().length() > 0.1;
    int updateInterval = ownerMoving ? 3 : 10; // 3 ticks when moving, 10 when still
    
    if (--this.timeToRecalcPath <= 0) {
        this.timeToRecalcPath = this.adjustedTickDelay(updateInterval);
        
        // Position prediction for moving owners
        Vec3 targetPosition = calculatePredictedPosition();
        this.navigation.moveTo(targetPosition.x, targetPosition.y, targetPosition.z, this.speedModifier);
    }
}

// Natural body-head coordination helper
private void coordinateBodyHeadRotation() {
    // Allow body to gradually align with head direction when not actively pathfinding
    // This creates natural "turning to look" behavior instead of owl-like head rotation
    if (this.navigation.isDone() || this.navigation.getPath() == null) {
        float headYaw = entity.getYHeadRot();
        float bodyYaw = entity.getYRot();
        float yawDiff = Mth.wrapDegrees(headYaw - bodyYaw);
        
        // Gradually rotate body toward head direction for natural posture
        if (Math.abs(yawDiff) > 15.0F) {
            entity.setYRot(entity.getYRot() + Math.signum(yawDiff) * 2.0F);
        }
    }
}
```

## Consequences

### Positive

**Security**:
- Prevents unauthorized spawning while allowing creator easter egg access
- Maintains comprehensive audit trail with timestamped ownership history
- Preserves original creator identity and privileges through all transfers
- Provides clear visual feedback about ownership status and history

**User Experience**:
- Natural robot behavior that feels responsive and engaging
- Visual feedback shows robots are actively following
- Reduced perceived lag in robot movement
- Creator easter egg provides emotional connection to original robots
- Rich ownership history adds depth and storytelling to robot items

**Architecture**:
- Builds on existing collision avoidance system
- Maintains backward compatibility with existing robots
- Clean separation between creation rights and ownership rights

### Negative

**Complexity**:
- Adds multiple new data fields requiring migration handling
- Multi-layered ownership model increases conceptual complexity
- Additional validation logic in spawn pathways
- Ownership transfer API requires careful state management
- Ownership history storage and display logic

**Performance**:
- More frequent pathfinding updates increase CPU usage
- Look-at calculations add per-tick overhead
- Position prediction requires velocity calculations

### Risks

**Data Migration**:
- Existing robots without Creator field need default assignment
- Spawn items in player inventories require retroactive Creator assignment
- Ownership history needs to be initialized for existing robots
- Transfer API must handle legacy robots without history data

**Multiplayer Synchronization**:
- Look-at behavior must sync properly across clients
- Prediction algorithms may cause client-server desync if not carefully implemented

## Alternatives Considered

### Creator System Alternatives

**Simple Ownership Validation**: Only validate current owner matches player without Creator tracking
- *Rejected*: Doesn't preserve original creator information for historical context

**Server-Side Spawn Registry**: Track all spawns server-side without item data
- *Rejected*: Breaks item portability and offline functionality

### Follow Behavior Alternatives

**Configurable Look-At**: Make look-at behavior optional via config
- *Rejected*: Natural behavior should be default, adds unnecessary complexity

**Client-Side Prediction**: Handle prediction on client side
- *Rejected*: Could cause desync issues and doesn't address server-side pathfinding

## Related Decisions

- Links to existing collision avoidance system in AiFollowOwnerGoal
- Builds on ItemSpawnHelper validation framework
- Extends LovelyIdentifier data field conventions

## Implementation Priority

1. **Phase 1**: Basic Creator system validation (security critical)
2. **Phase 2**: Ownership transfer API and history tracking
3. **Phase 3**: Creator easter egg implementation
4. **Phase 4**: Follow behavior enhancements (user experience)
5. **Phase 5**: Data migration for existing robots and spawn items

---

**Key Principle**: Security first, then natural user experience. The Creator system prevents exploits while the follow enhancements make robots feel more alive and responsive.