# Robot Collision Avoidance - Lightweight Implementation

**Status**: Ready for Implementation
**Created**: 2025-12-07
**Priority**: High (Quality of Life)
**Complexity**: Low
**Related**: `Future_Squad_Coordination_System.md`, `AiFollowOwnerGoal.java`

---

## Purpose

Prevent robots from running over each other when following their owner, creating a more organized and visually appealing follow behavior. This is a lightweight precursor to the full squad formation system.

---

## Problem Statement

When multiple robots follow the same owner, they:
- Stack on top of each other at the owner's position
- Push each other around chaotically
- Create visual clutter and confusion
- Make it difficult to see individual robots
- Appear disorganized and unprofessional

---

## Solution: Proximity-Based Spacing

### Core Concept

Robots detect nearby robots in Follow mode and maintain minimum spacing by adjusting their follow target position. This creates natural spreading without complex formation logic.

### Key Principles

1. **Lightweight**: No registry queries, no squad system dependency
2. **Local Detection**: Use entity collision box scanning (already optimized by Minecraft)
3. **Simple Logic**: If too close to another robot, offset follow position
4. **No Coordination**: Each robot acts independently (emergent behavior)
5. **Backward Compatible**: Works with existing follow AI goal

---

## Implementation Design

### Detection Method

**Use Minecraft's Built-in AABB System**:
```java
// Get nearby robots within detection radius
AABB detectionBox = this.getBoundingBox().inflate(DETECTION_RADIUS);
List<LovelyRobotEntity> nearbyRobots = this.level().getEntitiesOfClass(
    LovelyRobotEntity.class,
    detectionBox,
    robot -> robot != this && 
             robot.isAlive() && 
             robot.getCurrentState() == EntityState.Follow &&
             robot.getOwner() == this.getOwner()
);
```

**Why AABB?**:
- Already optimized by Minecraft's entity tracking
- No custom spatial partitioning needed
- Handles chunk boundaries automatically
- O(1) lookup in most cases

### Spacing Algorithm

**Simple Repulsion Force**:
```java
Vec3 repulsionForce = Vec3.ZERO;

for (LovelyRobotEntity nearbyRobot : nearbyRobots) {
    double distance = this.distanceTo(nearbyRobot);
    
    if (distance < MIN_SPACING) {
        // Calculate repulsion direction (away from nearby robot)
        Vec3 awayVector = this.position().subtract(nearbyRobot.position()).normalize();
        
        // Stronger repulsion when closer
        double repulsionStrength = (MIN_SPACING - distance) / MIN_SPACING;
        
        repulsionForce = repulsionForce.add(awayVector.scale(repulsionStrength));
    }
}

// Apply repulsion to follow target position
Vec3 adjustedTarget = ownerPosition.add(repulsionForce.scale(SPACING_OFFSET));
```

### Integration Point

**Modify `AiFollowOwnerGoal.java`**:
```java
@Override
public void tick() {
    // Existing follow logic...
    Vec3 targetPosition = owner.position();
    
    // NEW: Apply collision avoidance
    if (shouldAvoidCollision()) {
        targetPosition = applyCollisionAvoidance(targetPosition);
    }
    
    // Continue with pathfinding to adjusted target...
    this.navigation.moveTo(targetPosition.x, targetPosition.y, targetPosition.z, this.speedModifier);
}

private boolean shouldAvoidCollision() {
    // Only apply when following (not sitting, not in combat)
    return this.mob.getCurrentState() == EntityState.Follow && 
           !this.mob.isOrderedToSit() &&
           !this.mob.isWary();
}

private Vec3 applyCollisionAvoidance(Vec3 ownerPosition) {
    AABB detectionBox = this.mob.getBoundingBox().inflate(DETECTION_RADIUS);
    List<LovelyRobotEntity> nearbyRobots = this.mob.level().getEntitiesOfClass(
        LovelyRobotEntity.class,
        detectionBox,
        robot -> robot != this.mob && 
                 robot.isAlive() && 
                 robot.getCurrentState() == EntityState.Follow &&
                 robot.getOwner() == this.mob.getOwner()
    );
    
    if (nearbyRobots.isEmpty()) {
        return ownerPosition; // No collision avoidance needed
    }
    
    Vec3 repulsionForce = Vec3.ZERO;
    
    for (LovelyRobotEntity nearbyRobot : nearbyRobots) {
        double distance = this.mob.distanceTo(nearbyRobot);
        
        if (distance < MIN_SPACING && distance > 0.1) { // Avoid division by zero
            Vec3 awayVector = this.mob.position().subtract(nearbyRobot.position()).normalize();
            double repulsionStrength = (MIN_SPACING - distance) / MIN_SPACING;
            repulsionForce = repulsionForce.add(awayVector.scale(repulsionStrength));
        }
    }
    
    // Apply repulsion offset
    return ownerPosition.add(repulsionForce.scale(SPACING_OFFSET));
}
```

---

## Configuration

### Constants

```java
// In SharedConfigs.Common or AiFollowOwnerGoal
public static final double COLLISION_DETECTION_RADIUS = 3.0; // Blocks
public static final double MIN_ROBOT_SPACING = 2.0;          // Blocks
public static final double SPACING_OFFSET = 1.5;             // Repulsion multiplier
```

### Tuning Guidelines

**Detection Radius** (3.0 blocks):
- Too small: Robots still collide before detecting
- Too large: Performance impact, unnecessary calculations
- Sweet spot: 2-4 blocks (2x robot width)

**Min Spacing** (2.0 blocks):
- Too small: Robots still overlap
- Too large: Robots spread too far, lose cohesion
- Sweet spot: 1.5-2.5 blocks (comfortable personal space)

**Spacing Offset** (1.5 multiplier):
- Too small: Weak repulsion, robots drift together
- Too large: Robots bounce away dramatically
- Sweet spot: 1.0-2.0 (smooth, natural movement)

---

## Performance Analysis

### Computational Cost

**Per Robot Per Tick**:
```
1. AABB inflation: O(1)
2. Entity query: O(log n) with spatial partitioning
3. Filter predicate: O(k) where k = nearby entities
4. Distance calculations: O(m) where m = nearby robots in Follow mode
5. Vector math: O(m)

Total: O(log n + m)
```

**Typical Case**:
- n = total entities in chunk (~50-100)
- m = nearby robots following same owner (~2-5)
- **Cost per robot**: ~10-20 operations per tick
- **10 robots**: ~100-200 operations per tick
- **Negligible impact** on modern hardware

### Optimization Strategies

**1. Throttle Updates**:
```java
private int collisionCheckCooldown = 0;

@Override
public void tick() {
    // Only check collision every 5 ticks (0.25s)
    if (collisionCheckCooldown > 0) {
        collisionCheckCooldown--;
        // Use cached target position
    } else {
        collisionCheckCooldown = 5;
        // Recalculate collision avoidance
        cachedTargetPosition = applyCollisionAvoidance(owner.position());
    }
    
    this.navigation.moveTo(cachedTargetPosition.x, cachedTargetPosition.y, cachedTargetPosition.z, this.speedModifier);
}
```

**2. Early Exit**:
```java
// Skip if no nearby robots
if (nearbyRobots.isEmpty()) {
    return ownerPosition;
}

// Skip if only one robot (no collision possible)
if (nearbyRobots.size() == 1 && nearbyRobots.get(0).distanceTo(this.mob) > MIN_SPACING) {
    return ownerPosition;
}
```

**3. Distance Squared**:
```java
// Use distanceToSqr() to avoid sqrt() calculation
double distanceSq = this.mob.distanceToSqr(nearbyRobot);
double minSpacingSq = MIN_SPACING * MIN_SPACING;

if (distanceSq < minSpacingSq) {
    double distance = Math.sqrt(distanceSq); // Only calculate when needed
    // ... repulsion logic
}
```

---

## Edge Cases

### 1. Owner Stationary
**Problem**: Robots converge on owner position
**Solution**: Repulsion forces naturally spread them in circle around owner

### 2. Narrow Passages
**Problem**: Robots can't maintain spacing in 1-block corridors
**Solution**: Disable collision avoidance when path width < MIN_SPACING
```java
if (isInNarrowPassage()) {
    return ownerPosition; // Follow normally, single file
}
```

### 3. Combat Mode
**Problem**: Spacing interferes with combat positioning
**Solution**: Disable collision avoidance when robot is wary (in combat)
```java
if (this.mob.isWary()) {
    return ownerPosition; // Combat takes priority
}
```

### 4. Teleportation
**Problem**: Robots teleport to owner when too far, may stack
**Solution**: After teleport, apply strong initial repulsion
```java
if (justTeleported) {
    // Apply 2x repulsion force for first 20 ticks
    repulsionForce = repulsionForce.scale(2.0);
}
```

### 5. Different Robot Sizes
**Problem**: Larger robots need more spacing
**Solution**: Scale MIN_SPACING by robot bounding box width
```java
double effectiveSpacing = MIN_SPACING * (this.mob.getBbWidth() / 0.6); // 0.6 = default width
```

---

## Testing Checklist

### Functional Tests

- [ ] 2 robots following owner maintain spacing
- [ ] 5 robots following owner spread naturally
- [ ] 10+ robots following owner don't stack
- [ ] Robots reform spacing after teleport
- [ ] Robots follow through narrow passages (single file)
- [ ] Robots ignore spacing during combat
- [ ] Robots with different owners don't affect each other
- [ ] Sitting robots don't participate in collision avoidance
- [ ] Standby robots don't participate in collision avoidance

### Performance Tests

- [ ] No FPS drop with 10 robots following
- [ ] No FPS drop with 20 robots following
- [ ] No lag spikes when robots teleport
- [ ] Smooth movement (no jittering)
- [ ] No pathfinding thrashing

### Visual Tests

- [ ] Robots spread evenly around owner when stationary
- [ ] Robots maintain loose formation when moving
- [ ] No sudden jumps or teleports
- [ ] Natural-looking movement
- [ ] Spacing feels comfortable (not too tight, not too loose)

---

## Future Enhancements

### Phase 2: Registry-Based Awareness
Once the full squad system is implemented, enhance collision avoidance:
```java
// Use registry to find all owner's robots (not just nearby)
List<RobotRegistryEntry> allOwnerRobots = registry.getRobotsByOwner(ownerId);

// Apply formation positions from squad system
if (hasSquadAssignment()) {
    targetPosition = getFormationPosition();
} else {
    targetPosition = applyCollisionAvoidance(ownerPosition);
}
```

### Phase 3: Formation Hints
Even without full squad system, provide basic formation:
```java
// Assign robots to positions around owner based on spawn order
int robotIndex = getRobotIndexForOwner(); // 0, 1, 2, 3...
double angle = (robotIndex * 360.0 / totalRobots) * Math.PI / 180.0;
double radius = MIN_SPACING * 1.5;

Vec3 formationOffset = new Vec3(
    Math.cos(angle) * radius,
    0,
    Math.sin(angle) * radius
);

targetPosition = ownerPosition.add(formationOffset);
```

---

## Implementation Steps

### Step 1: Add Configuration
```java
// In SharedConfigs.Common
public static double CollisionDetectionRadius = 3.0;
public static double MinRobotSpacing = 2.0;
public static double SpacingOffset = 1.5;
public static boolean EnableCollisionAvoidance = true;
```

### Step 2: Modify AiFollowOwnerGoal
```java
// Add collision avoidance methods
private Vec3 cachedTargetPosition = Vec3.ZERO;
private int collisionCheckCooldown = 0;

// Modify tick() to use collision avoidance
// Add helper methods: shouldAvoidCollision(), applyCollisionAvoidance()
```

### Step 3: Test with Multiple Robots
```
1. Spawn 2 robots, verify spacing
2. Spawn 5 robots, verify spreading
3. Spawn 10 robots, verify performance
4. Test edge cases (narrow passages, combat, teleport)
```

### Step 4: Tune Parameters
```
1. Adjust DETECTION_RADIUS for optimal detection
2. Adjust MIN_SPACING for comfortable distance
3. Adjust SPACING_OFFSET for smooth movement
4. Test with different robot counts
```

### Step 5: Document & Release
```
1. Update CURRENT_STATE.md
2. Add to CHANGELOG.md
3. Create user-facing documentation
4. Announce feature in release notes
```

---

## Success Criteria

**Minimum Viable**:
- ✅ Robots don't stack on top of each other
- ✅ Robots maintain visible spacing when following
- ✅ No performance impact with 10 robots

**Ideal**:
- ✅ Natural-looking spread around owner
- ✅ Smooth, non-jittery movement
- ✅ Works seamlessly with existing AI goals
- ✅ Configurable for player preference

---

## Notes

This lightweight implementation provides immediate quality-of-life improvement without the complexity of the full squad system. It's designed to be:
- **Simple**: Minimal code changes
- **Fast**: Negligible performance impact
- **Compatible**: Works with existing systems
- **Extensible**: Foundation for future squad formations

Once the full squad coordination system is implemented, this collision avoidance can be replaced or enhanced with proper formation positioning.

---

**Related Documents**:
- `Future_Squad_Coordination_System.md` - Full squad system vision
- `Squad_Formation_System_Design.md` - Detailed formation implementation
- `AiFollowOwnerGoal.java` - Implementation target
- `CURRENT_STATE.md` - Track implementation progress

