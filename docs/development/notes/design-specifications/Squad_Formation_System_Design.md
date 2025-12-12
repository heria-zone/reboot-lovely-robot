# Squad Formation System - Comprehensive Design

**Status**: Design Phase
**Created**: 2025-12-07
**Priority**: Post-Core Features (Phase 2)
**Complexity**: High
**Dependencies**: Collision Avoidance, Registry System, Advanced AI
**Related**: `Future_Squad_Coordination_System.md`, `Robot_Collision_Avoidance_Implementation.md`

---

## Document Purpose

This document provides the **complete technical design** for the squad formation system, building upon the conceptual vision in `Future_Squad_Coordination_System.md` and the lightweight collision avoidance in `Robot_Collision_Avoidance_Implementation.md`.

---

## System Architecture

### Component Hierarchy

```
SquadManager (Server-Side Singleton)
├── SquadRegistry (Per-World)
│   ├── Squad (Per-Squad Instance)
│   │   ├── SquadData (Persistent State)
│   │   ├── FormationController (Positioning Logic)
│   │   ├── TacticsController (Behavior Coordination)
│   │   └── CommunicationHub (Signal Broadcasting)
│   └── RobotSquadComponent (Per-Robot Component)
│       ├── SquadMembership (Assignment Data)
│       ├── FormationPosition (Target Position)
│       └── TacticalState (Role, Health Status, Signals)
└── SquadCommands (Player Interface)
    ├── SquadCreation
    ├── SquadAssignment
    ├── FormationControl
    └── TacticalCommands
```

---

## Core Components

### 1. Squad Data Structure

```java
/**
 * Represents a squad of robots with shared coordination.
 * <p>
 * <b>Architecture:</b> Central coordination point for all squad members.
 * Manages formation, tactics, and communication between robots.
 * <p>
 * <b>Persistence:</b> Saved to world data, survives server restarts.
 * <p>
 * <b>Thread Safety:</b> All mutations synchronized, safe for async access.
 */
public class Squad {
    
    // -- Identity --
    
    private final UUID squadId;
    private String squadName;
    private final UUID ownerId;
    
    // -- Membership --
    
    private final List<UUID> memberIds = new ArrayList<>();
    private final Map<UUID, SquadRole> roleAssignments = new HashMap<>();
    private UUID leaderId; // First robot or manually assigned
    private int maxSize = 8; // Configurable
    
    // -- Formation --
    
    private FormationType currentFormation = FormationType.LINE;
    private Vec3 formationCenter; // Owner position or designated point
    private double formationSpacing = 2.0;
    private final Map<UUID, Vec3> formationPositions = new HashMap<>();
    
    // -- Tactics --
    
    private UUID primaryTarget; // Focus fire target
    private TacticalMode tacticalMode = TacticalMode.STANDARD;
    private final Map<UUID, TacticalState> memberStates = new HashMap<>();
    
    // -- Communication --
    
    private final Queue<SquadSignal> signalQueue = new ConcurrentLinkedQueue<>();
    private long lastCoordinationUpdate = 0;
    
    // -- Methods --
    
    public void addMember(UUID robotId, SquadRole role) { /* ... */ }
    public void removeMember(UUID robotId) { /* ... */ }
    public void updateFormation(FormationType formation) { /* ... */ }
    public void calculateFormationPositions() { /* ... */ }
    public void broadcastSignal(SquadSignal signal) { /* ... */ }
    public void updateTacticalStates() { /* ... */ }
    
} // Class: Squad
```

### 2. Robot Squad Component

```java
/**
 * Component attached to each robot for squad coordination.
 * <p>
 * <b>Architecture:</b> Lightweight component storing squad membership
 * and tactical state. References Squad instance for coordination logic.
 * <p>
 * <b>Integration:</b> Accessed by AI goals to modify behavior based on
 * squad assignment and formation position.
 */
public class RobotSquadComponent {
    
    // -- Squad Membership --
    
    private UUID assignedSquadId;
    private SquadRole role = SquadRole.UNASSIGNED;
    
    // -- Formation --
    
    private Vec3 formationPosition = Vec3.ZERO;
    private double formationTolerance = 1.0; // How close to maintain position
    
    // -- Tactical State --
    
    private TacticalState state = TacticalState.NORMAL;
    private int healthThreshold = 30; // Percent for distress
    private boolean isDistressed = false;
    
    // -- Communication --
    
    private long lastSignalTime = 0;
    private SquadSignal lastReceivedSignal = null;
    
    // -- Methods --
    
    public Squad getSquad(Level level) {
        return SquadManager.getInstance(level).getSquad(assignedSquadId);
    }
    
    public boolean hasSquadAssignment() {
        return assignedSquadId != null;
    }
    
    public void updateFormationPosition(Vec3 position) { /* ... */ }
    public void sendSignal(SquadSignal signal) { /* ... */ }
    public void receiveSignal(SquadSignal signal) { /* ... */ }
    
} // Class: RobotSquadComponent
```

### 3. Formation Controller

```java
/**
 * Calculates formation positions for squad members.
 * <p>
 * <b>Architecture:</b> Pure calculation logic, no state. Takes squad data
 * and formation type, returns position map for all members.
 * <p>
 * <b>Design Decision:</b> Stateless design enables easy testing and
 * formation switching without side effects.
 */
public class FormationController {
    
    /**
     * Calculates formation positions for all squad members.
     * <p>
     * <b>Algorithm:</b> Based on formation type, assigns each robot a
     * position relative to formation center (usually owner position).
     * <p>
     * <b>Role Consideration:</b> Tanks get front positions, supports get
     * rear positions, DPS fills middle.
     *
     * @param squad the squad to calculate positions for
     * @param center the formation center point (owner position)
     * @param facing the direction formation faces (owner look vector)
     * @return map of robot UUID to formation position
     */
    public static Map<UUID, Vec3> calculateFormationPositions(
            Squad squad,
            Vec3 center,
            Vec3 facing) {
        
        FormationType formation = squad.getCurrentFormation();
        List<UUID> members = squad.getMemberIds();
        Map<UUID, SquadRole> roles = squad.getRoleAssignments();
        double spacing = squad.getFormationSpacing();
        
        return switch (formation) {
            case LINE -> calculateLineFormation(members, roles, center, facing, spacing);
            case WEDGE -> calculateWedgeFormation(members, roles, center, facing, spacing);
            case CIRCLE -> calculateCircleFormation(members, roles, center, spacing);
            case COLUMN -> calculateColumnFormation(members, roles, center, facing, spacing);
            case SCATTER -> calculateScatterFormation(members, roles, center, spacing);
            case CUSTOM -> calculateCustomFormation(squad, center, facing);
        };
    }
    
    /**
     * Line formation: Robots spread in horizontal line.
     * <p>
     * <b>Layout:</b>
     * <pre>
     * [T] [D] [D] [S] [D] [T]
     *         [Owner]
     * </pre>
     * <p>
     * <b>Role Positioning:</b>
     * - Tanks (T) on flanks
     * - DPS (D) in middle
     * - Supports (S) near center
     */
    private static Map<UUID, Vec3> calculateLineFormation(
            List<UUID> members,
            Map<UUID, SquadRole> roles,
            Vec3 center,
            Vec3 facing,
            double spacing) {
        
        Map<UUID, Vec3> positions = new HashMap<>();
        
        // Sort members by role priority (Tank > DPS > Support)
        List<UUID> sortedMembers = sortByRolePriority(members, roles);
        
        int totalMembers = sortedMembers.size();
        double lineWidth = (totalMembers - 1) * spacing;
        double startOffset = -lineWidth / 2.0;
        
        // Calculate perpendicular vector (right side of facing direction)
        Vec3 right = new Vec3(-facing.z, 0, facing.x).normalize();
        
        for (int i = 0; i < totalMembers; i++) {
            UUID robotId = sortedMembers.get(i);
            double offset = startOffset + (i * spacing);
            Vec3 position = center.add(right.scale(offset));
            positions.put(robotId, position);
        }
        
        return positions;
    }
    
    /**
     * Wedge formation: Robots form V-shape pointing forward.
     * <p>
     * <b>Layout:</b>
     * <pre>
     *       [T]
     *     [D] [D]
     *   [D] [Owner] [D]
     *     [S] [S]
     * </pre>
     * <p>
     * <b>Role Positioning:</b>
     * - Tanks (T) at tip
     * - DPS (D) on sides
     * - Supports (S) at rear
     */
    private static Map<UUID, Vec3> calculateWedgeFormation(
            List<UUID> members,
            Map<UUID, SquadRole> roles,
            Vec3 center,
            Vec3 facing,
            double spacing) {
        
        Map<UUID, Vec3> positions = new HashMap<>();
        
        // Sort: Tanks first, then DPS, then Supports
        List<UUID> sortedMembers = sortByRolePriority(members, roles);
        
        Vec3 forward = facing.normalize();
        Vec3 right = new Vec3(-facing.z, 0, facing.x).normalize();
        
        int row = 0;
        int posInRow = 0;
        
        for (UUID robotId : sortedMembers) {
            // Calculate row and position within row
            int rowSize = row + 1;
            double rowOffset = -row * spacing; // Negative = forward
            double sideOffset = (posInRow - rowSize / 2.0) * spacing;
            
            Vec3 position = center
                    .add(forward.scale(rowOffset))
                    .add(right.scale(sideOffset));
            
            positions.put(robotId, position);
            
            // Move to next position
            posInRow++;
            if (posInRow >= rowSize) {
                row++;
                posInRow = 0;
            }
        }
        
        return positions;
    }
    
    /**
     * Circle formation: Robots surround owner in protective circle.
     * <p>
     * <b>Layout:</b>
     * <pre>
     *     [T] [D]
     *   [D] [Owner] [D]
     *     [S] [T]
     * </pre>
     * <p>
     * <b>Role Positioning:</b>
     * - Tanks (T) at cardinal directions (N, S, E, W)
     * - DPS (D) at diagonals
     * - Supports (S) fill remaining positions
     */
    private static Map<UUID, Vec3> calculateCircleFormation(
            List<UUID> members,
            Map<UUID, SquadRole> roles,
            Vec3 center,
            double spacing) {
        
        Map<UUID, Vec3> positions = new HashMap<>();
        
        int totalMembers = members.size();
        double radius = spacing * 1.5; // Slightly larger for circle
        
        // Assign tanks to cardinal directions first
        List<UUID> tanks = filterByRole(members, roles, SquadRole.TANK);
        List<UUID> dps = filterByRole(members, roles, SquadRole.DPS);
        List<UUID> supports = filterByRole(members, roles, SquadRole.SUPPORT);
        
        int angleStep = 360 / totalMembers;
        int currentAngle = 0;
        
        // Place tanks at cardinal directions (0°, 90°, 180°, 270°)
        for (UUID tankId : tanks) {
            double angleRad = Math.toRadians(currentAngle);
            Vec3 position = center.add(
                    Math.cos(angleRad) * radius,
                    0,
                    Math.sin(angleRad) * radius
            );
            positions.put(tankId, position);
            currentAngle += 90;
        }
        
        // Fill remaining positions with DPS and supports
        currentAngle = 45; // Start at diagonals
        for (UUID robotId : dps) {
            if (!positions.containsKey(robotId)) {
                double angleRad = Math.toRadians(currentAngle);
                Vec3 position = center.add(
                        Math.cos(angleRad) * radius,
                        0,
                        Math.sin(angleRad) * radius
                );
                positions.put(robotId, position);
                currentAngle += angleStep;
            }
        }
        
        for (UUID robotId : supports) {
            if (!positions.containsKey(robotId)) {
                double angleRad = Math.toRadians(currentAngle);
                Vec3 position = center.add(
                        Math.cos(angleRad) * radius,
                        0,
                        Math.sin(angleRad) * radius
                );
                positions.put(robotId, position);
                currentAngle += angleStep;
            }
        }
        
        return positions;
    }
    
    // Helper methods
    
    private static List<UUID> sortByRolePriority(List<UUID> members, Map<UUID, SquadRole> roles) {
        return members.stream()
                .sorted((a, b) -> {
                    SquadRole roleA = roles.getOrDefault(a, SquadRole.UNASSIGNED);
                    SquadRole roleB = roles.getOrDefault(b, SquadRole.UNASSIGNED);
                    return Integer.compare(roleA.getPriority(), roleB.getPriority());
                })
                .toList();
    }
    
    private static List<UUID> filterByRole(List<UUID> members, Map<UUID, SquadRole> roles, SquadRole targetRole) {
        return members.stream()
                .filter(id -> roles.getOrDefault(id, SquadRole.UNASSIGNED) == targetRole)
                .toList();
    }
    
} // Class: FormationController
```

---

## Enums & Data Types

### Formation Types

```java
public enum FormationType {
    LINE(0, "Line", "Horizontal spread, balanced coverage"),
    WEDGE(1, "Wedge", "V-formation, aggressive push"),
    CIRCLE(2, "Circle", "Defensive ring, 360° protection"),
    COLUMN(3, "Column", "Single file, narrow passages"),
    SCATTER(4, "Scatter", "Spread out, AOE evasion"),
    CUSTOM(5, "Custom", "Player-defined positions");
    
    private final int id;
    private final String displayName;
    private final String description;
    
    // Constructor, getters, byId() method...
}
```

### Squad Roles

```java
public enum SquadRole {
    UNASSIGNED(0, "Unassigned", 99),
    TANK(1, "Tank", 1),        // Priority 1 (front)
    DPS(2, "DPS", 2),           // Priority 2 (middle)
    SUPPORT(3, "Support", 3);   // Priority 3 (rear)
    
    private final int id;
    private final String displayName;
    private final int priority; // Lower = higher priority (front positions)
    
    // Constructor, getters, byId() method...
}
```

### Tactical States

```java
public enum TacticalState {
    NORMAL(0, "Normal", "Standard behavior"),
    DISTRESSED(1, "Distressed", "Low health, retreating"),
    PROTECTING(2, "Protecting", "Defending distressed ally"),
    AGGRESSIVE(3, "Aggressive", "Increased combat focus"),
    RETREATING(4, "Retreating", "Falling back to owner"),
    COORDINATED_ATTACK(5, "Coordinated Attack", "Executing tactic");
    
    private final int id;
    private final String displayName;
    private final String description;
    
    // Constructor, getters, byId() method...
}
```

### Squad Signals

```java
public class SquadSignal {
    
    public enum SignalType {
        DISTRESS,           // Low health, need help
        AGGRO,              // Engaging enemy
        ALL_CLEAR,          // Combat ended
        OWNER_DANGER,       // Owner taking damage
        TARGET_MARKED,      // Focus fire target
        FORMATION_CHANGE,   // New formation assigned
        RETREAT,            // Fall back command
        ADVANCE             // Push forward command
    }
    
    private final SignalType type;
    private final UUID senderId;
    private final long timestamp;
    private final Vec3 location;
    private final UUID targetId; // For TARGET_MARKED
    private final Object data;   // Additional signal data
    
    // Constructor, getters...
}
```

---

## AI Goal Integration

### Modified Follow Goal

```java
/**
 * Enhanced follow goal with squad formation support.
 * <p>
 * <b>Architecture:</b> Extends existing AiFollowOwnerGoal with formation
 * positioning. Falls back to collision avoidance if no squad assigned.
 */
public class AiSquadFollowGoal extends AiFollowOwnerGoal {
    
    private final LovelyRobotEntity robot;
    private RobotSquadComponent squadComponent;
    
    @Override
    public void tick() {
        LivingEntity owner = this.robot.getOwner();
        if (owner == null) return;
        
        Vec3 targetPosition;
        
        // Check for squad assignment
        if (squadComponent != null && squadComponent.hasSquadAssignment()) {
            // Use formation position from squad
            Squad squad = squadComponent.getSquad(this.robot.level());
            if (squad != null) {
                // Update formation center to owner position
                squad.setFormationCenter(owner.position());
                squad.calculateFormationPositions();
                
                // Get this robot's assigned formation position
                targetPosition = squadComponent.getFormationPosition();
                
                // If too far from formation position, path to it
                double distanceToFormation = this.robot.position().distanceTo(targetPosition);
                if (distanceToFormation > squadComponent.getFormationTolerance()) {
                    this.navigation.moveTo(
                            targetPosition.x,
                            targetPosition.y,
                            targetPosition.z,
                            this.speedModifier
                    );
                }
            } else {
                // Squad not found, fall back to collision avoidance
                targetPosition = applyCollisionAvoidance(owner.position());
                this.navigation.moveTo(
                        targetPosition.x,
                        targetPosition.y,
                        targetPosition.z,
                        this.speedModifier
                );
            }
        } else {
            // No squad assignment, use collision avoidance
            targetPosition = applyCollisionAvoidance(owner.position());
            this.navigation.moveTo(
                    targetPosition.x,
                    targetPosition.y,
                    targetPosition.z,
                    this.speedModifier
            );
        }
    }
    
    // Collision avoidance fallback (from lightweight implementation)
    private Vec3 applyCollisionAvoidance(Vec3 ownerPosition) {
        // ... existing collision avoidance logic
    }
}
```

---

## Performance Optimization

### Update Throttling

```java
/**
 * Squad coordination updates are expensive. Throttle to acceptable rate.
 */
public class Squad {
    
    private static final int COORDINATION_UPDATE_INTERVAL = 10; // ticks (0.5s)
    private int ticksSinceLastUpdate = 0;
    
    public void tick() {
        ticksSinceLastUpdate++;
        
        if (ticksSinceLastUpdate >= COORDINATION_UPDATE_INTERVAL) {
            ticksSinceLastUpdate = 0;
            
            // Perform expensive operations
            calculateFormationPositions();
            updateTacticalStates();
            processSignalQueue();
        }
    }
}
```

### Spatial Caching

```java
/**
 * Cache formation positions until formation changes or center moves significantly.
 */
public class Squad {
    
    private Map<UUID, Vec3> cachedFormationPositions = new HashMap<>();
    private Vec3 lastFormationCenter = Vec3.ZERO;
    private FormationType lastFormationType = FormationType.LINE;
    
    public void calculateFormationPositions() {
        Vec3 currentCenter = this.formationCenter;
        
        // Check if recalculation needed
        boolean centerMoved = currentCenter.distanceTo(lastFormationCenter) > 1.0;
        boolean formationChanged = this.currentFormation != lastFormationType;
        
        if (!centerMoved && !formationChanged) {
            return; // Use cached positions
        }
        
        // Recalculate
        cachedFormationPositions = FormationController.calculateFormationPositions(
                this,
                currentCenter,
                getOwnerFacing()
        );
        
        lastFormationCenter = currentCenter;
        lastFormationType = this.currentFormation;
        
        // Distribute positions to members
        for (UUID memberId : memberIds) {
            RobotSquadComponent component = getRobotComponent(memberId);
            if (component != null) {
                component.updateFormationPosition(cachedFormationPositions.get(memberId));
            }
        }
    }
}
```

---

## Configuration

```java
// In SharedConfigs.Common

// Squad System
public static boolean EnableSquadSystem = true;
public static int MaxSquadSize = 8;
public static boolean AllowMultipleSquads = true;
public static boolean AutoAssignRoles = true;

// Formation
public static FormationType DefaultFormation = FormationType.LINE;
public static boolean AutoFormationSwitch = true;
public static double FormationSpacing = 2.0;
public static double FormationTolerance = 1.0;
public static int FormationUpdateInterval = 10; // ticks

// Tactics
public static int DistressHealthPercent = 30;
public static int CriticalHealthPercent = 15;
public static double ProtectiveResponseRange = 16.0;
public static boolean EnableFocusFire = true;
public static boolean EnableProtectiveBehavior = true;

// Communication
public static double SignalPropagationRange = 32.0;
public static int SignalQueueMaxSize = 50;
public static int SignalProcessingRate = 5; // signals per tick

// Visual
public static boolean ShowFormationLines = true;
public static boolean ShowHealthBars = true;
public static boolean ShowRoleIcons = true;
public static ParticleEffectDensity ParticleEffects = ParticleEffectDensity.MEDIUM;
```

---

## Implementation Phases

### Phase 1: Foundation (Week 1-2)
- [ ] Create Squad, RobotSquadComponent classes
- [ ] Implement SquadManager singleton
- [ ] Add squad creation/assignment commands
- [ ] Basic squad membership tracking
- [ ] NBT persistence for squad data

### Phase 2: Formation System (Week 3-4)
- [ ] Implement FormationController
- [ ] Add formation calculation algorithms (Line, Wedge, Circle)
- [ ] Integrate with AiFollowOwnerGoal
- [ ] Formation position caching
- [ ] Formation switching logic

### Phase 3: Tactical Behavior (Week 5-6)
- [ ] Health monitoring system
- [ ] Distress signal implementation
- [ ] Protective behavior (healthy robots defend distressed)
- [ ] Owner protection override
- [ ] Role-based positioning

### Phase 4: Coordination (Week 7-8)
- [ ] Focus fire system
- [ ] Target marking
- [ ] Signal broadcasting/receiving
- [ ] Coordinated attack patterns
- [ ] Formation auto-switching

### Phase 5: Polish & Optimization (Week 9-10)
- [ ] Performance profiling
- [ ] Update throttling optimization
- [ ] Visual feedback (particles, indicators)
- [ ] Configuration tuning
- [ ] Comprehensive testing

---

## Testing Strategy

### Unit Tests
- Formation position calculations
- Role priority sorting
- Signal queue processing
- NBT serialization/deserialization

### Integration Tests
- Squad creation/deletion
- Robot assignment/removal
- Formation switching
- Tactical state transitions

### Performance Tests
- 10 squads of 8 robots each (80 robots)
- Formation recalculation frequency
- Signal processing throughput
- Memory usage over time

### User Experience Tests
- Formation visual clarity
- Response to player commands
- Tactical behavior effectiveness
- Configuration flexibility

---

## Success Metrics

**Functional**:
- ✅ Squads maintain formation while following owner
- ✅ Robots respond to distress signals
- ✅ Focus fire coordination works
- ✅ Formation switching is smooth

**Performance**:
- ✅ No FPS drop with 80 robots (10 squads)
- ✅ Formation updates < 1ms per squad
- ✅ Signal processing < 0.5ms per tick
- ✅ Memory usage < 10MB for 100 squads

**User Experience**:
- ✅ Formations are visually clear
- ✅ Commands are intuitive
- ✅ Tactical behavior feels intelligent
- ✅ Configuration is flexible

---

## Future Enhancements

### Advanced Formations
- Custom player-defined formations
- Dynamic formation morphing (smooth transitions)
- Terrain-adaptive formations
- Formation templates (save/load)

### Advanced Tactics
- Pull & hunt coordination
- Flanking maneuvers
- Kiting strategies
- Ambush setups

### Squad Abilities
- Combined attacks (multiple robots, one big attack)
- Squad buffs (proximity bonuses)
- Formation-specific abilities
- Ultimate abilities (cooldown-based)

### Integration
- Mod compatibility (other pet/companion mods)
- Multiplayer squad sharing
- Cross-player coordination
- PvP squad battles

---

## Related Documents

- `Future_Squad_Coordination_System.md` - Conceptual vision
- `Robot_Collision_Avoidance_Implementation.md` - Lightweight precursor
- `Robot_AI_Behavior_Specification.md` - AI behavior foundation
- `CURRENT_STATE.md` - Implementation tracking
- `ARCHITECTURE.md` - System integration

---

**Notes**:

This design builds upon the lightweight collision avoidance system, providing a clear upgrade path from simple spacing to full formation coordination. The architecture is designed to be:
- **Modular**: Each component has clear responsibilities
- **Extensible**: Easy to add new formations and tactics
- **Performant**: Optimized for large robot counts
- **Configurable**: Players can customize behavior

Implementation should be incremental, with each phase thoroughly tested before proceeding. Player feedback will be crucial for balancing coordination complexity vs. usability.

