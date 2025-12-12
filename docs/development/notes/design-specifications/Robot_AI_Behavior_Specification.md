# Robot AI Behavior Specification - Legacy 1.20.1

**Status**: Active Implementation
**Created**: 2025-11-28
**Version**: 1.0
**Target**: Legacy 1.20.1 (Fabric & Forge)

---

## Document Purpose

This document provides the complete behavioral specification for LovelyRobot AI in Legacy 1.20.1, addressing the current issue where robots wander excessively instead of following their owner properly.

---

## Problem Statement

### Current Issues
1. **Excessive Wandering**: Robots wander constantly when near owner instead of staying close
2. **Poor Follow Response**: Robots only follow when far from owner (> 10 blocks)
3. **No Movement Detection**: System doesn't detect if owner is moving or stationary
4. **Combat Radius Issues**: Robots chase enemies beyond reasonable range
5. **Defense Mode Simplicity**: Defense mode only returns to base, no patrol behavior

### Root Cause
The vanilla `FollowOwnerGoal` only activates when distance exceeds minimum threshold. When robot is close to owner, the follow goal becomes inactive, allowing `WanderAroundFarGoal` to take over constantly.

---

## Behavioral Modes

### Mode Overview
```
┌─────────────┐
│   FOLLOW    │ ← Default companion mode
├─────────────┤
│   DEFENSE   │ ← Guard/patrol base location
├─────────────┤
│   STANDBY   │ ← Sit and stay
└─────────────┘
```

---

## Follow Mode - Detailed Specification

### Core Principle
**"Robots should follow their owner closely, only wandering rarely when owner is stationary for extended periods"**

### Behavior States

#### State 1: Owner Moving
```
Trigger: Owner velocity > 0.001 (any movement)
Duration: While owner continues moving

Robot Behavior:
├─ Priority: FOLLOW > COMBAT > LOOK
├─ Follow Distance: FollowDistanceMin to FollowDistanceMax (config)
├─ Movement: Maintain close proximity (2-4 blocks ideal)
├─ Wander: DISABLED
├─ Combat: Only engage enemies within FollowDistanceMax radius
└─ Teleport: If distance > teleport threshold

Implementation Details:
- Check owner velocity every tick
- Reset stationary timer when movement detected
- Path to owner's current position (not predicted)
- Use config speed: MovementFollowOwner
- Teleport if distance > FollowDistanceMax * 2
```

#### State 2: Owner Stationary (0-10 seconds)
```
Trigger: Owner velocity ≤ 0.001
Duration: 0 to 200 ticks (10 seconds)

Robot Behavior:
├─ Priority: STAY_CLOSE > COMBAT > LOOK_AROUND
├─ Position: Within FollowDistanceMin of owner
├─ Movement: Minimal (only if pushed away)
├─ Wander: DISABLED
├─ Combat: Engage enemies within FollowDistanceMax radius
└─ Look: May look at owner or surroundings

Implementation Details:
- Increment stationary timer each tick
- Stay within close range (don't path away)
- Look at owner occasionally
- Ready to follow immediately when owner moves
- No wandering during this phase
```

#### State 3: Owner Stationary (> 10 seconds)
```
Trigger: Owner velocity ≤ 0.001 for > 200 ticks
Duration: Until owner moves

Robot Behavior:
├─ Priority: COMBAT > RARE_WANDER > STAY_CLOSE
├─ Position: Within 5-8 blocks of owner
├─ Wander: ENABLED (rare, conditional)
├─ Combat: Engage enemies within FollowDistanceMax radius
└─ Return: Path back to owner after wander

Wander Conditions:
├─ Chance: 5% per check (1 in 20)
├─ Check Interval: Every 600 ticks (30 seconds)
├─ Cooldown: 1200-1800 ticks (60-90 seconds) after wander
├─ Duration: 200-400 ticks (10-20 seconds)
├─ Radius: 5-8 blocks from owner
└─ Return: Path back to owner's current position

Implementation Details:
- Continue incrementing stationary timer
- Check wander conditions every 30 seconds
- Apply cooldown after each wander
- Short wander duration (10-20 seconds)
- Always return to owner after wander
- Reset cooldown when owner moves
```

### Combat Behavior in Follow Mode

#### Target Selection Priority
```
Auto-Attack ON:
1. Entities attacking owner (HIGHEST PRIORITY)
2. Entities owner is attacking
3. Entities that attacked robot
4. Hostile mobs within range (random chance)

Auto-Attack OFF:
1. Entities attacking owner (ONLY)
2. Entities owner is attacking (ONLY)
3. Entities that attacked robot (ONLY)
4. No autonomous hunting
```

#### Combat Movement Rules
```
Engagement Radius: FollowDistanceMax from owner

Behavior:
├─ Enemy within radius: Engage
├─ Enemy beyond radius: Do NOT chase
├─ Enemy flees beyond radius: Return to owner
├─ During combat: Wander DISABLED
└─ After combat: Resume following owner

Owner Damage Response:
├─ Immediate target switch to attacker
├─ Override current target
├─ Maximum aggression
├─ Ignore radius temporarily (pursue attacker)
└─ Return to owner after attacker defeated
```

#### Combat Radius Visualization
```
When robot cannot chase enemy (beyond radius):
├─ Spawn smoke particles at robot position
├─ Particle count: 5-10
├─ Particle spread: 0.3 radius
├─ Duration: 1 second
└─ Indicates "I can't go further"
```

---

## Defense Mode - Detailed Specification

### Core Principle
**"Robots patrol around a designated base position, alternating between active patrol and stationary guard duty"**

### Patrol State Machine

#### State 1: PATROL
```
Duration: 600-900 ticks (30-45 seconds, random)

Robot Behavior:
├─ Movement: Random walk within BaseDefenceRange
├─ Pattern: Random points with pauses
├─ Pause Duration: 40-80 ticks (2-4 seconds) at each point
├─ Speed: MovementFollowOwner (config)
├─ Look: Scan for threats while moving
└─ Combat: Interrupt patrol to engage

Implementation Details:
- Select random point within BaseDefenceRange of base
- Path to point
- Pause for 2-4 seconds
- Select new random point
- Repeat until timer expires (30-45 seconds)
- Transition to GUARD state
```

#### State 2: GUARD
```
Duration: 400-600 ticks (20-30 seconds, random)

Robot Behavior:
├─ Movement: Return to base position
├─ Position: At base coordinates (BaseX, BaseY, BaseZ)
├─ Stance: Stand still
├─ Look: 360° rotation scan
├─ Rotation Speed: Slow (2-3 RPM)
└─ Combat: Interrupt guard to engage

Implementation Details:
- Path back to base coordinates
- Stop at base position
- Rotate slowly (look around)
- Complete 1-2 full rotations during guard phase
- Transition to PATROL state after timer expires
```

#### State Transitions
```
PATROL → GUARD:
├─ Trigger: Patrol timer expires (30-45s)
├─ Action: Path to base position
└─ Next: Enter GUARD state

GUARD → PATROL:
├─ Trigger: Guard timer expires (20-30s)
├─ Action: Select random patrol point
└─ Next: Enter PATROL state

ANY → COMBAT:
├─ Trigger: Enemy detected within BaseDefenceRange
├─ Action: Engage enemy
├─ After Combat: Resume previous state (PATROL or GUARD)
└─ Timer: Pause state timer during combat
```

### Combat Behavior in Defense Mode

#### Target Selection Priority
```
Same as Follow Mode:
1. Entities attacking owner (HIGHEST)
2. Entities owner is attacking
3. Entities that attacked robot
4. Hostile mobs within range (if auto-attack enabled)
```

#### Combat Movement Rules
```
Engagement Radius: BaseDefenceRange from base position

Behavior:
├─ Enemy within radius: Engage
├─ Enemy beyond radius: Do NOT chase
├─ Enemy flees beyond radius: Return to patrol/guard
├─ During combat: Pause state timer
└─ After combat: Resume patrol/guard (continue timer)

Base Position Enforcement:
├─ If pushed beyond BaseDefenceRange: Return to base
├─ If beyond BaseDefenceWarpRange: Teleport to base
└─ Always maintain proximity to base
```

#### Combat Radius Visualization
```
When robot cannot chase enemy (beyond base radius):
├─ Spawn smoke particles at robot position
├─ Particle count: 5-10
├─ Particle spread: 0.3 radius
├─ Duration: 1 second
└─ Indicates "I can't leave my post"
```

---

## Standby Mode - Detailed Specification

### Core Principle
**"Robot sits in place and does not move unless attacked or owner is attacked"**

### Behavior
```
Robot State:
├─ Position: Sitting (isSitting = true)
├─ Movement: NONE
├─ Wander: DISABLED
├─ Follow: DISABLED
└─ Combat: Only if attacked or owner attacked

Combat Response:
├─ Stand up (isSitting = false)
├─ Engage attacker
├─ After combat: Return to sitting position
└─ Sit down again (isSitting = true)

Owner Damage Response:
├─ Stand up immediately
├─ Engage owner's attacker
├─ Stay near owner during combat
├─ After combat: Return to original position
└─ Sit down again
```

---

## Configuration Values

### New Configuration Options

```java
// Follow Mode - Owner Stationary Detection
public static int OwnerStillThreshold = 200; // ticks (10 seconds)

// Follow Mode - Wander Behavior
public static int WanderCheckInterval = 600; // ticks (30 seconds)
public static double WanderChance = 0.05; // 5% chance
public static double WanderRadiusMin = 5.0; // blocks
public static double WanderRadiusMax = 8.0; // blocks
public static int WanderDurationMin = 200; // ticks (10 seconds)
public static int WanderDurationMax = 400; // ticks (20 seconds)
public static int WanderCooldownMin = 1200; // ticks (60 seconds)
public static int WanderCooldownMax = 1800; // ticks (90 seconds)

// Defense Mode - Patrol Behavior
public static int PatrolDurationMin = 600; // ticks (30 seconds)
public static int PatrolDurationMax = 900; // ticks (45 seconds)
public static int GuardDurationMin = 400; // ticks (20 seconds)
public static int GuardDurationMax = 600; // ticks (30 seconds)
public static int PatrolPauseDurationMin = 40; // ticks (2 seconds)
public static int PatrolPauseDurationMax = 80; // ticks (4 seconds)
public static double GuardRotationSpeed = 0.05; // radians per tick

// Combat - Radius Enforcement
public static boolean EnableCombatRadiusParticles = true;
public static int CombatRadiusParticleCount = 8;
public static double CombatRadiusParticleSpread = 0.3;
```

### Existing Configuration Options (Used)
```java
// From LovelyConfigs.Common:
MovementFollowOwner // Follow speed
MovementMeleeAttack // Attack speed
MovementWanderAround // Wander speed
FollowDistanceMin // Min follow distance (e.g., 2-3 blocks)
FollowDistanceMax // Max follow distance (e.g., 10 blocks)
BaseDefenceRange // Defense patrol radius
BaseDefenceWarpRange // Defense teleport threshold
AttackChance // Auto-attack probability
```

---

## Goal Priority Structure

### Current (Problematic)
```java
this.goalSelector.add(1, new SwimGoal(this));
this.goalSelector.add(2, new SitGoal(this));
this.goalSelector.add(3, new MeleeAttackGoal(this, ...));
this.goalSelector.add(4, new AiFollowOwnerGoal(this, ...));
this.goalSelector.add(4, new AiBaseDefenseGoal(this, ...));
this.goalSelector.add(5, new WanderAroundFarGoal(this, ...)); // ← PROBLEM
this.goalSelector.add(6, new LookAtEntityGoal(this, ...));
this.goalSelector.add(7, new LookAroundGoal(this));
```

### Revised (Fixed)
```java
this.goalSelector.add(1, new SwimGoal(this));
this.goalSelector.add(2, new SitGoal(this));
this.goalSelector.add(3, new MeleeAttackGoal(this, ...));
this.goalSelector.add(4, new AiFollowOwnerGoal(this, ...));        // Enhanced
this.goalSelector.add(4, new AiBaseDefenseGoal(this, ...));        // Enhanced
this.goalSelector.add(6, new AiConditionalWanderGoal(this, ...)); // New, lower priority
this.goalSelector.add(7, new LookAtEntityGoal(this, ...));
this.goalSelector.add(8, new LookAroundGoal(this));
```

**Key Change**: Wander goal moved to priority 6 (lower) and made conditional

---

## Implementation Components

### 1. Enhanced AiFollowOwnerGoal

**New Features**:
- Owner velocity detection
- Stationary timer tracking
- Dynamic follow distance based on owner state
- Combat awareness (disable during combat)
- Tighter following when owner moving

**Key Methods**:
```java
canStart() {
    - Check state (Follow mode only)
    - Check owner exists
    - Detect owner movement
    - Adjust behavior based on stationary time
    - Return true if should follow
}

shouldContinue() {
    - Check state still Follow
    - Check owner still exists
    - Check distance from owner
    - Return true if should keep following
}

tick() {
    - Update owner velocity tracking
    - Update stationary timer
    - Adjust follow distance dynamically
    - Handle teleportation if too far
}
```

### 2. Enhanced AiBaseDefenseGoal

**New Features**:
- Patrol/Guard state machine
- Random point selection within radius
- Pause at patrol points
- 360° rotation during guard phase
- State timer management
- Combat interruption handling

**Key Methods**:
```java
canStart() {
    - Check state (Defense mode only)
    - Check distance from base
    - Return true if should patrol/guard
}

start() {
    - Initialize state (PATROL or GUARD)
    - Set state timer
    - Select first patrol point or return to base
}

tick() {
    - Update state timer
    - Execute current state behavior (PATROL or GUARD)
    - Handle state transitions
    - Check for combat interruptions
}

tickPatrol() {
    - Select random point within BaseDefenceRange
    - Path to point
    - Pause at point
    - Check timer for state transition
}

tickGuard() {
    - Return to base position
    - Rotate slowly (360° scan)
    - Check timer for state transition
}
```

### 3. New AiConditionalWanderGoal

**Features**:
- State-aware (only in Follow mode)
- Owner stationary time check
- Rare activation (5% chance per 30 seconds)
- Wander cooldown system
- Limited radius (5-8 blocks from owner)
- Short duration (10-20 seconds)
- Combat awareness (disable during combat)

**Key Methods**:
```java
canStart() {
    - Check state (Follow mode only)
    - Check owner exists and is stationary > 10s
    - Check wander cooldown expired
    - Check random chance (5%)
    - Check not in combat
    - Return true if all conditions met
}

start() {
    - Set wander duration timer
    - Select random point within radius of owner
    - Start pathfinding
}

tick() {
    - Update wander duration timer
    - Check if duration expired
    - Stop if owner starts moving
}

stop() {
    - Set wander cooldown
    - Path back to owner's current position
}
```

### 4. Combat Radius Enforcement

**Implementation**:
```java
In MeleeAttackGoal or custom attack goal:

shouldContinue() {
    - Get current target
    - Calculate distance from owner (Follow) or base (Defense)
    - If beyond allowed radius:
        - Spawn smoke particles
        - Stop attacking
        - Return false
    - Otherwise continue
}

spawnRadiusParticles() {
    - Use InternalParticle.Smoke(entity)
    - Spawn 8 particles
    - Spread: 0.3 radius
    - Indicates "can't chase further"
}
```

---

## Particle Effects

### Smoke Particles (Combat Radius)

**Usage**: Indicate robot cannot chase enemy beyond allowed radius

**Implementation**:
```java
// Add to InternalParticle class
public static void Smoke(LivingEntity entity) {
    if (entity.getWorld() instanceof ServerWorld serverWorld) {
        serverWorld.spawnParticles(
            ParticleTypes.SMOKE,
            entity.getX(),
            entity.getY() + 0.5,
            entity.getZ(),
            8, // count
            0.3, 0.3, 0.3, // spread
            0.01 // speed
        );
    }
}
```

**Trigger Conditions**:
- Robot in combat
- Target beyond allowed radius (FollowDistanceMax or BaseDefenceRange)
- Robot attempts to chase but is blocked
- Spawn once per second (throttled)

---

## State Transition Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                        FOLLOW MODE                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Owner Moving ──────────────────────────────────────────┐  │
│       │                                                  │  │
│       │ Owner stops                                      │  │
│       ↓                                                  │  │
│  Owner Still (0-10s) ────────────────────────────────────┤  │
│       │                                                  │  │
│       │ Timer > 10s                                      │  │
│       ↓                                                  │  │
│  Owner Still (>10s) ─────────────────────────────────────┤  │
│       │                                                  │  │
│       │ Wander chance (5% per 30s)                      │  │
│       ↓                                                  │  │
│  Wandering (10-20s) ─────────────────────────────────────┤  │
│       │                                                  │  │
│       │ Duration expires OR owner moves                 │  │
│       └──────────────────────────────────────────────────┘  │
│                                                             │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                       DEFENSE MODE                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  PATROL (30-45s) ────────────────────────────────────────┐  │
│       │                                                  │  │
│       │ Timer expires                                    │  │
│       ↓                                                  │  │
│  GUARD (20-30s) ─────────────────────────────────────────┤  │
│       │                                                  │  │
│       │ Timer expires                                    │  │
│       └──────────────────────────────────────────────────┘  │
│                                                             │
│  Combat interrupts either state, resumes after             │
│                                                             │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                       STANDBY MODE                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Sitting ────────────────────────────────────────────────┐  │
│       │                                                  │  │
│       │ Attacked OR owner attacked                       │  │
│       ↓                                                  │  │
│  Combat ─────────────────────────────────────────────────┤  │
│       │                                                  │  │
│       │ Combat ends                                      │  │
│       └──────────────────────────────────────────────────┘  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## Testing Checklist

### Follow Mode Tests
- [ ] Robot follows closely when owner walks
- [ ] Robot follows closely when owner runs
- [ ] Robot stops when owner stops (within 10s)
- [ ] Robot stays close when owner stationary (0-10s)
- [ ] Robot rarely wanders when owner stationary (>10s)
- [ ] Robot returns to owner after wander
- [ ] Robot stops wandering when owner moves
- [ ] Robot teleports when too far behind
- [ ] Robot engages enemies within radius
- [ ] Robot does NOT chase enemies beyond radius
- [ ] Smoke particles appear when blocked from chasing
- [ ] Robot prioritizes owner's attacker

### Defense Mode Tests
- [ ] Robot enters patrol state on activation
- [ ] Robot wanders within base radius during patrol
- [ ] Robot pauses at patrol points
- [ ] Robot transitions to guard state after 30-45s
- [ ] Robot returns to base during guard state
- [ ] Robot rotates 360° during guard state
- [ ] Robot transitions back to patrol after 20-30s
- [ ] Robot engages enemies within base radius
- [ ] Robot does NOT chase enemies beyond base radius
- [ ] Smoke particles appear when blocked from chasing
- [ ] Robot returns to patrol/guard after combat
- [ ] Robot teleports if pushed beyond warp range

### Standby Mode Tests
- [ ] Robot sits when commanded
- [ ] Robot does not wander while sitting
- [ ] Robot does not follow while sitting
- [ ] Robot stands and fights when attacked
- [ ] Robot returns to sitting after combat
- [ ] Robot defends owner when owner attacked
- [ ] Robot returns to sitting after defending owner

### Combat Tests
- [ ] Auto-attack ON: Attacks hostile mobs
- [ ] Auto-attack OFF: Only defends owner/self
- [ ] Owner attacker always prioritized
- [ ] Robot respects combat radius in Follow mode
- [ ] Robot respects combat radius in Defense mode
- [ ] Smoke particles indicate radius limit
- [ ] Robot returns to owner after combat (Follow)
- [ ] Robot resumes patrol after combat (Defense)

### Configuration Tests
- [ ] All new config values load correctly
- [ ] Config changes apply without restart
- [ ] Invalid config values handled gracefully
- [ ] Config values affect behavior as expected

---

## Performance Considerations

### Optimization Strategies

**Owner Velocity Tracking**:
- Cache velocity calculation (don't recalculate every tick)
- Update every 5 ticks (0.25s) instead of every tick
- Use squared distance comparisons (avoid sqrt)

**Stationary Timer**:
- Simple integer increment (minimal overhead)
- Reset on movement detection
- No complex calculations

**Wander Chance**:
- Check every 30 seconds, not every tick
- Use random.nextInt(20) for 5% chance
- Early exit if conditions not met

**Patrol State Machine**:
- Update state every 10 ticks (0.5s)
- Cache patrol points (don't recalculate constantly)
- Reuse pathfinding results

**Particle Effects**:
- Throttle to once per second maximum
- Only spawn on server side
- Minimal particle count (8 particles)

---

## Related Documents

- `Future_Squad_Coordination_System.md` - Advanced multi-robot features
- `CURRENT_STATE.md` - Implementation tracking
- `ARCHITECTURE.md` - System design
- `ROADMAP.md` - Feature timeline

---

## Implementation Notes

**Fabric vs Forge Differences**:
- Goal class names differ (e.g., `SwimGoal` vs `FloatGoal`)
- Particle spawning methods differ slightly
- Navigation classes have minor API differences
- **Solution**: Implement in common package, use loader-specific wrappers if needed

**Backward Compatibility**:
- New config values have sensible defaults
- Existing robots continue working with new behavior
- No NBT structure changes required
- No breaking changes to existing features

**Future Expansion**:
- System designed to support squad coordination
- State machine can be extended with new states
- Goal system can accommodate new behaviors
- Configuration system ready for advanced options

---

**End of Specification**
