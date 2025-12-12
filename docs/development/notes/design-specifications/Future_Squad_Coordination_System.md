# Future Feature: Squad Coordination & Tactical Combat System

**Status**: Concept/Future Implementation
**Created**: 2025-11-28
**Priority**: Post-1.20.1 Core Behavior Implementation
**Related**: Robot AI Behavior, Combat System, Multi-Robot Management

---

## Overview

Advanced multi-robot coordination system enabling tactical combat formations, squad management, and intelligent group behavior. Robots coordinate with each other based on health, role, and combat situation.

---

## Core Concepts

### 1. Squad System

#### Squad Creation & Management
- **Player-Defined Squads**: Players can create named squads and assign robots
- **Squad Size**: Configurable (default: 4-8 robots per squad)
- **Squad Roles**: Leader, Tank, DPS, Support (auto-assigned or manual)
- **Squad Commands**: Group commands affect entire squad simultaneously
- **Visual Identification**: Squad members have matching colored indicators/particles

#### Squad UI/Interaction
```
Potential Implementation:
- Right-click robot with specific item (e.g., banner) to open squad UI
- Assign to existing squad or create new squad
- Set squad formation preference
- View squad status (health, location, state)
- Issue squad-wide commands
```

---

### 2. Health-Based Tactical Behavior

#### Protective Response System

**Trigger**: Robot health drops below threshold (e.g., 30%)

**Behavior Changes**:
```
Low Health Robot:
├─ Retreat to safe distance (behind healthier robots)
├─ Reduce aggression (defensive stance)
├─ Prioritize survival over combat
├─ Signal distress to squad members
└─ Seek owner proximity for protection

Healthy Squad Members:
├─ Detect distressed robot signal
├─ Move to intercept threats
├─ Form protective barrier
├─ Increase aggression toward threats
└─ Cover retreat path
```

**Formation Adjustment**:
```
Normal Formation:
[High HP] [High HP] [High HP]
[Med HP] [Owner] [Med HP]
[Low HP] [Low HP] [Low HP]

Under Attack Formation:
[High HP] [High HP] [High HP]  ← Front line
   [Owner] [Med HP]            ← Protected
      [Low HP]                 ← Rear guard
```

#### Owner Protection Override

**Highest Priority**: Owner takes damage

**Behavior**:
```
ALL robots (regardless of health):
├─ Immediately target owner's attacker
├─ Ignore personal health status
├─ Rush to owner's defense
├─ Form protective circle around owner
└─ Engage attacker with maximum aggression

Health-based positioning still applies:
├─ High HP robots: Front positions (closest to threat)
├─ Low HP robots: Rear positions (still defending, but safer)
└─ Critical HP robots: Stay near owner but avoid direct combat
```

---

### 3. Coordinated Attack Patterns

#### Pull & Hunt Tactic

**Scenario**: Engaging dangerous enemy or group

**Execution**:
```
Phase 1: Aggro Pull
├─ Designated "Scout" robot (fastest/highest HP)
├─ Approaches enemy to trigger aggro
├─ Attacks once to ensure aggro lock
├─ Retreats toward squad position
└─ Enemy follows scout

Phase 2: Ambush Setup
├─ Remaining squad members position in flanking formation
├─ Wait for scout to lead enemy into kill zone
├─ Maintain stealth (no attacks until trigger)
└─ Prepare coordinated strike

Phase 3: Coordinated Strike
├─ Scout reaches squad position
├─ All robots engage simultaneously
├─ Focus fire on single target
├─ Maintain formation during combat
└─ Rotate aggro if scout health drops
```

**Formation Example**:
```
Before Pull:
    [Enemy]
       ↑
    [Scout] ← Pulls aggro
       ↓
   [R] [R] [R] ← Waiting squad
     [Owner]

During Ambush:
    [Enemy] ← Following scout
       ↓
    [Scout] ← Leading back
       ↓
[R] ← [Owner] → [R] ← Flanking positions
       [R]
```

---

### 4. Combat Formations

#### Formation Types

**Line Formation** (Default)
```
[R] [R] [R] [R] [R]
      [Owner]

Use Case: Open combat, equal threat distribution
Advantage: Maximum coverage, even damage distribution
```

**Wedge Formation** (Aggressive)
```
      [R]
    [R] [R]
  [R] [Owner] [R]
    [R] [R]

Use Case: Pushing through enemy lines
Advantage: Concentrated force, breakthrough power
```

**Circle Formation** (Defensive)
```
    [R] [R]
  [R] [Owner] [R]
    [R] [R]

Use Case: Surrounded, protecting owner
Advantage: 360° coverage, no blind spots
```

**Column Formation** (Travel)
```
      [R]
      [R]
   [Owner]
      [R]
      [R]

Use Case: Moving through narrow spaces
Advantage: Efficient pathfinding, quick response
```

**Scatter Formation** (Evasive)
```
[R]     [R]
   [Owner]
[R]     [R]

Use Case: AOE attacks, ranged combat
Advantage: Reduced splash damage, harder to target
```

#### Formation Switching

**Automatic Triggers**:
```
Combat Start → Line Formation
Heavy Damage → Circle Formation (protect owner)
Narrow Path → Column Formation
AOE Detected → Scatter Formation
Boss Fight → Wedge Formation
```

**Manual Control**:
```
Player can command formation change:
- Hotkey/item interaction
- Squad UI selection
- Contextual commands (e.g., "Defend!" → Circle)
```

---

### 5. Role-Based Behavior

#### Tank Role
```
Characteristics:
├─ Highest HP robots
├─ Front-line positioning
├─ Aggro generation priority
├─ Damage mitigation focus
└─ Protect squishier robots

Behavior:
├─ Engage enemies first
├─ Maintain threat on multiple targets
├─ Body-block attacks toward owner/low HP robots
├─ Taunt/draw aggro (if mechanic added)
└─ Last to retreat
```

#### DPS Role
```
Characteristics:
├─ High damage output
├─ Mid-line positioning
├─ Focus fire coordination
├─ Target priority awareness
└─ Balanced HP/damage

Behavior:
├─ Focus single target until defeated
├─ Switch targets on command/priority change
├─ Maintain optimal attack range
├─ Avoid unnecessary damage
└─ Coordinate burst damage windows
```

#### Support Role
```
Characteristics:
├─ Lower HP or specialized function
├─ Rear positioning
├─ Utility focus (future: buffs, healing items)
├─ Situational awareness
└─ Survival priority

Behavior:
├─ Stay near owner
├─ Monitor squad health status
├─ Provide utility (future: item distribution)
├─ Alert on threats
└─ First to retreat if overwhelmed
```

---

### 6. Coordinated Target Selection

#### Focus Fire System

**Concept**: All squad members attack same target for faster elimination

**Implementation**:
```
Target Priority Algorithm:
1. Owner's attacker (HIGHEST)
2. Owner's current target
3. Closest threat to owner
4. Closest threat to low-HP squad member
5. Highest threat level enemy
6. Closest enemy to squad

Squad Coordination:
├─ Leader designates primary target
├─ All DPS robots focus primary
├─ Tanks maintain aggro on secondary threats
├─ Support monitors for priority changes
└─ Switch target when primary defeated
```

#### Target Marking System

**Visual Indicators**:
```
Primary Target: Red particles above enemy
Secondary Target: Orange particles
Ignored Target: Gray particles (too far/low priority)

Squad members see same markers:
├─ Shared vision system
├─ Real-time target updates
├─ Priority changes broadcast to squad
└─ Visual confirmation of coordination
```

---

### 7. Communication & Signals

#### Robot-to-Robot Communication

**Signal Types**:
```
Distress Signal:
├─ Sent when health < 30%
├─ Alerts nearby squad members
├─ Triggers protective behavior
└─ Visual: Red particles, sound effect

Aggro Signal:
├─ Sent when engaging enemy
├─ Alerts squad to combat start
├─ Triggers formation change
└─ Visual: Yellow particles

All Clear Signal:
├─ Sent when combat ends
├─ Triggers formation reset
├─ Resume normal behavior
└─ Visual: Green particles

Owner Danger Signal:
├─ Sent when owner takes damage
├─ HIGHEST PRIORITY
├─ All robots respond immediately
└─ Visual: Bright red particles + sound
```

#### Visual Feedback

**Squad Coordination Indicators**:
```
Formation Lines:
├─ Faint particle lines connecting squad members
├─ Shows formation structure
├─ Color-coded by squad
└─ Only visible to owner

Status Indicators:
├─ Health bar above robot (optional config)
├─ Role icon (tank/dps/support)
├─ Current action (attacking, defending, retreating)
└─ Squad assignment badge
```

---

## Technical Considerations

### Performance

**Optimization Strategies**:
```
Squad Coordination:
├─ Update every 10 ticks (0.5s) not every tick
├─ Limit squad size to prevent lag
├─ Use spatial partitioning for nearby robot detection
├─ Cache formation positions
└─ Throttle particle effects

Communication:
├─ Event-driven signals (not polling)
├─ Radius-based signal propagation
├─ Signal priority queue
└─ Debounce rapid signals
```

### Data Structures

**Squad Data**:
```java
class RobotSquad {
    UUID squadId;
    String squadName;
    UUID ownerId;
    List<UUID> memberIds;
    FormationType currentFormation;
    UUID leaderId;
    Map<UUID, SquadRole> roleAssignments;
    Vec3 formationCenter;
    UUID primaryTarget;
}
```

**Robot Squad Component**:
```java
class SquadComponent {
    UUID assignedSquad;
    SquadRole role;
    Vec3 formationPosition;
    int healthThreshold;
    boolean isDistressed;
    long lastSignalTime;
}
```

---

## Implementation Phases

### Phase 1: Basic Squad System
- Squad creation/assignment
- Squad-wide commands
- Visual identification
- Basic coordination (follow together)

### Phase 2: Health-Based Tactics
- Health monitoring
- Protective behavior
- Formation adjustment
- Retreat mechanics

### Phase 3: Combat Formations
- Formation types implementation
- Automatic formation switching
- Manual formation control
- Position calculation

### Phase 4: Coordinated Attacks
- Focus fire system
- Target marking
- Pull & hunt tactics
- Role-based behavior

### Phase 5: Advanced Communication
- Signal system
- Visual feedback
- Squad coordination indicators
- Performance optimization

---

## Configuration Options

```java
// Squad System
MaxSquadSize = 8
AllowMultipleSquads = true
AutoAssignRoles = true

// Health Thresholds
DistressHealthPercent = 30
CriticalHealthPercent = 15
ProtectiveResponseRange = 16

// Formation
DefaultFormation = LINE
AutoFormationSwitch = true
FormationSpacing = 2.0
FormationUpdateInterval = 10 ticks

// Coordination
EnableFocusFire = true
EnablePullTactics = true
CoordinationRange = 32
SignalPropagationSpeed = 1.0

// Visual
ShowFormationLines = true
ShowHealthBars = true
ShowRoleIcons = true
ParticleEffectDensity = MEDIUM
```

---

## Future Enhancements

### Advanced AI
- Machine learning for optimal formation selection
- Adaptive tactics based on enemy type
- Player behavior learning (preferred formations)

### Expanded Roles
- Healer (distributes healing items)
- Scout (explores ahead, reports threats)
- Engineer (places blocks, creates defenses)
- Archer (ranged combat specialist)

### Squad Abilities
- Combined attacks (multiple robots, one big attack)
- Squad buffs (proximity bonuses)
- Formation-specific abilities
- Ultimate abilities (cooldown-based power moves)

### Integration
- Mod compatibility (other pet/companion mods)
- Multiplayer squad sharing
- Cross-player coordination
- PvP squad battles

---

## Notes

This system represents a significant expansion of robot AI capabilities. Implementation should be incremental, with each phase thoroughly tested before proceeding. Player feedback will be crucial for balancing coordination complexity vs. usability.

**Priority**: Implement after core behavior system is stable and well-tested.

**Dependencies**: 
- Stable follow/wander/combat behavior
- Robust state management
- Performance optimization
- Configuration system expansion

---

**Related Documents**:
- `Robot_AI_Behavior_Specification.md` (to be created)
- `CURRENT_STATE.md` (track implementation progress)
- `ARCHITECTURE.md` (system design integration)
