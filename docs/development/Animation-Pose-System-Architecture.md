---
created: 2025-11-28
tags:
  - LovelyRobot
  - Architecture
  - Animation
---

# Animation & Pose System Architecture

## Executive Summary

This document defines the architecture for a flexible, extensible animation and pose system for the LovelyRobot mod. The system is designed to:
- Start with **enum-based states** for immediate implementation
- Evolve toward **data-driven configuration** for maximum flexibility
- Remain **animation-backend agnostic** (GeckoLib initially, custom system later)
- Support **multiple animations per state** with conditional selection
- Handle **pose transitions** with dynamic hitbox adjustments
- Enable **interruption behaviors** for responsive entity interactions

## Project Structure Context

```
reboot-lovely-robot/
├── framework/           # Native Java, no modloader dependencies
│   └── animation/       # Core animation abstractions
├── lib/                 # Loader-specific implementations
│   ├── forge/
│   └── fabric/
├── common/              # Mod-specific shared code (Tribute, Legacy, Reboot)
│   └── animation/       # Mod-specific animation logic
└── sources/             # Version-specific implementations
    ├── tribute/
    ├── legacy/
    └── reboot/
```

## Core Design Principles

1. **Separation of Concerns**: Animation logic separate from rendering backend
2. **State-Based Architecture**: Clear state machine with defined transitions
3. **Conditional Animation Selection**: Multiple animations per state with selection criteria
4. **Hitbox Coupling**: Poses directly control entity collision bounds
5. **Interruptibility**: States can be interrupted based on priority and conditions
6. **Configurability**: Time thresholds and behaviors externalized
7. **Extensibility**: Easy to add new poses, states, and conditions


## Phase 1: Enum-Based Implementation (Current Goal)

### 1.1 Core State Enumeration

Located in: `framework/animation/`

```java
/**
 * Defines the high-level behavioral states of a robot entity.
 * Each state can contain multiple poses with transition logic.
 */
public enum RobotState {
    STANDBY,    // Robot is stationary, not following
    FOLLOW,     // Robot is following owner
    DEFENSE,    // Robot is in combat/guard mode
    WORKING,    // Robot is performing tasks (mining, crafting, etc.)
    DISABLED;   // Robot is inactive/damaged
    
    /**
     * Determines if this state can be interrupted by the target state.
     */
    public boolean canTransitionTo(RobotState target) {
        // Define state transition rules
        return true; // Placeholder
    }
    
    /**
     * Priority level for interruption handling.
     * Higher values = harder to interrupt.
     */
    public int getPriority() {
        switch (this) {
            case DISABLED: return 100;
            case DEFENSE: return 50;
            case WORKING: return 30;
            case FOLLOW: return 20;
            case STANDBY: return 10;
            default: return 0;
        }
    }
}
```

### 1.2 Pose Enumeration

Located in: `framework/animation/`

```java
/**
 * Defines specific poses within states.
 * Each pose has animation, hitbox, and duration properties.
 */
public enum RobotPose {
    // STANDBY poses
    SITTING_IDLE("sit", 1.0f, 0.6f, -1),           // Indefinite duration
    SITTING_REST("rest", 0.8f, 0.4f, -1),          // Smaller hitbox when resting
    
    // FOLLOW poses
    STANDING_IDLE("idle", 1.8f, 0.6f, -1),
    WALKING("walk", 1.8f, 0.6f, -1),
    RUNNING("run", 1.8f, 0.6f, -1),
    
    // DEFENSE poses
    COMBAT_IDLE("combat_idle", 1.8f, 0.6f, -1),
    ATTACKING("attack", 1.8f, 0.6f, -1),
    BLOCKING("block", 1.8f, 0.6f, -1),
    
    // WORKING poses
    MINING("mining", 1.8f, 0.6f, -1),
    CRAFTING("crafting", 1.8f, 0.6f, -1);
    
    private final String animationKey;
    private final float height;
    private final float width;
    private final int defaultDuration; // Ticks, -1 = indefinite
    
    RobotPose(String animationKey, float height, float width, int defaultDuration) {
        this.animationKey = animationKey;
        this.height = height;
        this.width = width;
        this.defaultDuration = defaultDuration;
    }
    
    public String getAnimationKey() { return animationKey; }
    public float getHeight() { return height; }
    public float getWidth() { return width; }
    public int getDefaultDuration() { return defaultDuration; }
    
    /**
     * Gets the hitbox dimensions for this pose.
     */
    public EntityDimensions getDimensions() {
        return EntityDimensions.fixed(width, height);
    }
}
```


### 1.3 Animation Backend Abstraction

Located in: `framework/animation/`

```java
/**
 * Abstraction layer for animation systems.
 * Allows switching between GeckoLib, custom system, or other backends.
 */
public interface IAnimationController {
    
    /**
     * Plays the specified animation.
     * 
     * @param animationKey The animation identifier
     * @param looping Whether the animation should loop
     * @param transitionTicks Smooth transition duration
     */
    void playAnimation(String animationKey, boolean looping, int transitionTicks);
    
    /**
     * Stops the current animation.
     */
    void stopAnimation();
    
    /**
     * Gets the current animation key being played.
     */
    String getCurrentAnimation();
    
    /**
     * Checks if an animation is currently playing.
     */
    boolean isAnimationPlaying();
    
    /**
     * Sets the animation speed multiplier.
     */
    void setAnimationSpeed(float speed);
}
```

```java
/**
 * GeckoLib implementation of the animation controller.
 * Located in: lib/forge/ or lib/fabric/ (loader-specific)
 */
public class GeckoLibAnimationController implements IAnimationController {
    
    private final AnimatableEntity entity;
    private final AnimationController<?> geckoController;
    private String currentAnimation;
    
    public GeckoLibAnimationController(AnimatableEntity entity, String controllerName) {
        this.entity = entity;
        this.geckoController = new AnimationController<>(entity, controllerName, 0, this::predicate);
    }
    
    @Override
    public void playAnimation(String animationKey, boolean looping, int transitionTicks) {
        this.currentAnimation = animationKey;
        geckoController.setAnimation(RawAnimation.begin().then(
            animationKey, 
            looping ? Animation.LoopType.LOOP : Animation.LoopType.PLAY_ONCE
        ));
        geckoController.setAnimationSpeed(1.0);
    }
    
    @Override
    public void stopAnimation() {
        geckoController.stop();
        currentAnimation = null;
    }
    
    @Override
    public String getCurrentAnimation() {
        return currentAnimation;
    }
    
    @Override
    public boolean isAnimationPlaying() {
        return currentAnimation != null;
    }
    
    @Override
    public void setAnimationSpeed(float speed) {
        geckoController.setAnimationSpeed(speed);
    }
    
    private PlayState predicate(AnimationState<?> state) {
        return PlayState.CONTINUE;
    }
}
```


### 1.4 Pose State Manager

Located in: `framework/animation/`

```java
/**
 * Manages the current pose state and handles transitions.
 * This is the core state machine for robot poses.
 */
public class PoseStateManager {
    
    private final IAnimationController animationController;
    private final PoseTransitionController transitionController;
    private final PoseConfiguration config;
    
    private RobotState currentState;
    private RobotPose currentPose;
    private int ticksInCurrentPose;
    private boolean isDirty; // Hitbox needs update
    
    public PoseStateManager(IAnimationController animationController, PoseConfiguration config) {
        this.animationController = animationController;
        this.config = config;
        this.transitionController = new PoseTransitionController(config);
        this.currentState = RobotState.STANDBY;
        this.currentPose = RobotPose.SITTING_IDLE;
        this.ticksInCurrentPose = 0;
        this.isDirty = true;
    }
    
    /**
     * Called every tick to update pose state.
     */
    public void tick(PoseContext context) {
        ticksInCurrentPose++;
        
        // Check for state changes (external triggers)
        RobotState newState = determineState(context);
        if (newState != currentState) {
            transitionToState(newState, context);
            return;
        }
        
        // Check for pose transitions within current state
        RobotPose nextPose = transitionController.getNextPose(
            currentState, 
            currentPose, 
            ticksInCurrentPose, 
            context
        );
        
        if (nextPose != null && nextPose != currentPose) {
            transitionToPose(nextPose);
        }
    }
    
    /**
     * Forces a state change (e.g., player command, AI decision).
     */
    public void setState(RobotState newState, PoseContext context) {
        if (currentState.canTransitionTo(newState)) {
            transitionToState(newState, context);
        }
    }
    
    /**
     * Gets the current pose for hitbox calculations.
     */
    public RobotPose getCurrentPose() {
        return currentPose;
    }
    
    /**
     * Checks if hitbox needs updating.
     */
    public boolean isDirty() {
        return isDirty;
    }
    
    /**
     * Marks hitbox as updated.
     */
    public void clearDirty() {
        isDirty = false;
    }
    
    private void transitionToState(RobotState newState, PoseContext context) {
        currentState = newState;
        
        // Get initial pose for this state
        RobotPose initialPose = transitionController.getInitialPose(newState, context);
        transitionToPose(initialPose);
    }
    
    private void transitionToPose(RobotPose newPose) {
        if (newPose == currentPose) return;
        
        currentPose = newPose;
        ticksInCurrentPose = 0;
        isDirty = true;
        
        // Trigger animation
        animationController.playAnimation(
            newPose.getAnimationKey(),
            true, // Loop by default
            config.getTransitionTicks()
        );
    }
    
    private RobotState determineState(PoseContext context) {
        // This would be called by AI/behavior system
        // For now, return current state
        return currentState;
    }
}
```


### 1.5 Pose Transition Controller

Located in: `framework/animation/`

```java
/**
 * Handles the logic for transitioning between poses within states.
 * Evaluates conditions and time thresholds.
 */
public class PoseTransitionController {
    
    private final PoseConfiguration config;
    
    public PoseTransitionController(PoseConfiguration config) {
        this.config = config;
    }
    
    /**
     * Determines the initial pose when entering a state.
     */
    public RobotPose getInitialPose(RobotState state, PoseContext context) {
        switch (state) {
            case STANDBY:
                return RobotPose.SITTING_IDLE;
            case FOLLOW:
                return context.isMoving() ? RobotPose.WALKING : RobotPose.STANDING_IDLE;
            case DEFENSE:
                return RobotPose.COMBAT_IDLE;
            case WORKING:
                return RobotPose.MINING; // Or based on task
            case DISABLED:
                return RobotPose.SITTING_REST;
            default:
                return RobotPose.STANDING_IDLE;
        }
    }
    
    /**
     * Evaluates if a pose transition should occur.
     * Returns the next pose, or null if no transition.
     */
    public RobotPose getNextPose(RobotState state, RobotPose currentPose, 
                                  int ticksInPose, PoseContext context) {
        
        switch (state) {
            case STANDBY:
                return evaluateStandbyTransition(currentPose, ticksInPose, context);
            case FOLLOW:
                return evaluateFollowTransition(currentPose, ticksInPose, context);
            case DEFENSE:
                return evaluateDefenseTransition(currentPose, ticksInPose, context);
            default:
                return null;
        }
    }
    
    private RobotPose evaluateStandbyTransition(RobotPose current, int ticks, PoseContext context) {
        // SITTING_IDLE -> SITTING_REST after configured time
        if (current == RobotPose.SITTING_IDLE) {
            int threshold = config.getIdleToRestTicks();
            if (ticks >= threshold) {
                return RobotPose.SITTING_REST;
            }
        }
        
        // SITTING_REST -> SITTING_IDLE if disturbed
        if (current == RobotPose.SITTING_REST) {
            if (context.isDisturbanceDetected()) {
                return RobotPose.SITTING_IDLE;
            }
        }
        
        return null;
    }
    
    private RobotPose evaluateFollowTransition(RobotPose current, int ticks, PoseContext context) {
        // Movement-based transitions
        if (context.isMoving()) {
            float speed = context.getMovementSpeed();
            
            if (speed > config.getRunSpeedThreshold()) {
                return RobotPose.RUNNING;
            } else {
                return RobotPose.WALKING;
            }
        } else {
            // Stopped moving
            if (current == RobotPose.WALKING || current == RobotPose.RUNNING) {
                return RobotPose.STANDING_IDLE;
            }
            
            // Could add: STANDING_IDLE -> different idle animations after time
            if (current == RobotPose.STANDING_IDLE && ticks > config.getIdleVariationTicks()) {
                // Return alternate idle animation (future enhancement)
            }
        }
        
        return null;
    }
    
    private RobotPose evaluateDefenseTransition(RobotPose current, int ticks, PoseContext context) {
        // Combat state transitions
        if (context.isAttacking()) {
            return RobotPose.ATTACKING;
        }
        
        if (context.isBlocking()) {
            return RobotPose.BLOCKING;
        }
        
        // Return to combat idle
        if (current == RobotPose.ATTACKING || current == RobotPose.BLOCKING) {
            if (!context.isAttacking() && !context.isBlocking()) {
                return RobotPose.COMBAT_IDLE;
            }
        }
        
        return null;
    }
}
```


### 1.6 Pose Context

Located in: `framework/animation/`

```java
/**
 * Provides contextual information for pose evaluation.
 * Decouples pose logic from entity implementation.
 */
public class PoseContext {
    
    private final Entity entity;
    private final World world;
    
    // Movement state
    private boolean isMoving;
    private float movementSpeed;
    
    // Combat state
    private boolean isAttacking;
    private boolean isBlocking;
    
    // Environmental state
    private boolean isDisturbanceDetected;
    private boolean isOwnerNearby;
    private boolean isInWater;
    private boolean isOnGround;
    
    // Task state
    private String currentTask;
    
    public PoseContext(Entity entity) {
        this.entity = entity;
        this.world = entity.getWorld();
    }
    
    /**
     * Updates context from entity state.
     * Called before pose evaluation.
     */
    public void update() {
        // Update movement
        Vec3d velocity = entity.getVelocity();
        this.movementSpeed = (float) velocity.horizontalLength();
        this.isMoving = movementSpeed > 0.01f;
        
        // Update environmental
        this.isOnGround = entity.isOnGround();
        this.isInWater = entity.isInWater();
        
        // Update disturbance detection
        this.isDisturbanceDetected = detectDisturbance();
        
        // Other updates...
    }
    
    private boolean detectDisturbance() {
        // Check for nearby entities, damage, sounds, etc.
        // This is where you'd implement disturbance logic
        return false; // Placeholder
    }
    
    // Getters
    public boolean isMoving() { return isMoving; }
    public float getMovementSpeed() { return movementSpeed; }
    public boolean isAttacking() { return isAttacking; }
    public boolean isBlocking() { return isBlocking; }
    public boolean isDisturbanceDetected() { return isDisturbanceDetected; }
    public boolean isOwnerNearby() { return isOwnerNearby; }
    public boolean isInWater() { return isInWater; }
    public boolean isOnGround() { return isOnGround; }
    public String getCurrentTask() { return currentTask; }
    
    // Setters for external systems
    public void setAttacking(boolean attacking) { this.isAttacking = attacking; }
    public void setBlocking(boolean blocking) { this.isBlocking = blocking; }
    public void setCurrentTask(String task) { this.currentTask = task; }
    public void setOwnerNearby(boolean nearby) { this.isOwnerNearby = nearby; }
}
```


### 1.7 Pose Configuration

Located in: `framework/animation/`

```java
/**
 * Configuration for pose timing and thresholds.
 * Starts as code-based, will evolve to file-based config.
 */
public class PoseConfiguration {
    
    // Transition timing (in ticks, 20 ticks = 1 second)
    private int transitionTicks = 10;           // Smooth animation transition
    private int idleToRestTicks = 6000;         // 5 minutes in idle before rest
    private int idleVariationTicks = 1200;      // 1 minute before alternate idle
    
    // Movement thresholds
    private float runSpeedThreshold = 0.3f;     // Speed to trigger run animation
    private float walkSpeedThreshold = 0.05f;   // Minimum speed for walk
    
    // Disturbance detection
    private double disturbanceRadius = 8.0;     // Blocks
    private int disturbanceCooldown = 100;      // Ticks before can rest again
    
    // Hitbox transition
    private boolean smoothHitboxTransition = true;
    private int hitboxTransitionTicks = 5;
    
    // Getters and setters
    public int getTransitionTicks() { return transitionTicks; }
    public int getIdleToRestTicks() { return idleToRestTicks; }
    public int getIdleVariationTicks() { return idleVariationTicks; }
    public float getRunSpeedThreshold() { return runSpeedThreshold; }
    public float getWalkSpeedThreshold() { return walkSpeedThreshold; }
    public double getDisturbanceRadius() { return disturbanceRadius; }
    public int getDisturbanceCooldown() { return disturbanceCooldown; }
    public boolean isSmoothHitboxTransition() { return smoothHitboxTransition; }
    public int getHitboxTransitionTicks() { return hitboxTransitionTicks; }
    
    public void setIdleToRestTicks(int ticks) { this.idleToRestTicks = ticks; }
    public void setRunSpeedThreshold(float threshold) { this.runSpeedThreshold = threshold; }
    // Other setters...
}
```

### 1.8 Hitbox Manager

Located in: `framework/animation/`

```java
/**
 * Manages entity hitbox updates based on pose changes.
 * Handles smooth transitions if configured.
 */
public class HitboxManager {
    
    private final Entity entity;
    private final PoseConfiguration config;
    
    private EntityDimensions currentDimensions;
    private EntityDimensions targetDimensions;
    private int transitionTicks;
    private int transitionProgress;
    
    public HitboxManager(Entity entity, PoseConfiguration config) {
        this.entity = entity;
        this.config = config;
        this.currentDimensions = entity.getDimensions(entity.getPose());
    }
    
    /**
     * Updates hitbox based on pose change.
     */
    public void updateForPose(RobotPose pose) {
        EntityDimensions newDimensions = pose.getDimensions();
        
        if (config.isSmoothHitboxTransition()) {
            startTransition(newDimensions);
        } else {
            applyDimensions(newDimensions);
        }
    }
    
    /**
     * Called every tick to handle smooth transitions.
     */
    public void tick() {
        if (transitionProgress > 0) {
            transitionProgress--;
            
            if (transitionProgress == 0) {
                applyDimensions(targetDimensions);
            } else {
                // Interpolate dimensions
                float progress = 1.0f - (transitionProgress / (float) transitionTicks);
                EntityDimensions interpolated = interpolateDimensions(
                    currentDimensions, 
                    targetDimensions, 
                    progress
                );
                applyDimensions(interpolated);
            }
        }
    }
    
    private void startTransition(EntityDimensions target) {
        this.targetDimensions = target;
        this.transitionTicks = config.getHitboxTransitionTicks();
        this.transitionProgress = transitionTicks;
    }
    
    private void applyDimensions(EntityDimensions dimensions) {
        this.currentDimensions = dimensions;
        entity.calculateDimensions();
        entity.setBoundingBox(dimensions.getBoxAt(entity.getPos()));
    }
    
    private EntityDimensions interpolateDimensions(EntityDimensions from, EntityDimensions to, float progress) {
        float width = from.width + (to.width - from.width) * progress;
        float height = from.height + (to.height - from.height) * progress;
        return EntityDimensions.fixed(width, height);
    }
}
```


### 1.9 Entity Integration

Located in: `common/entity/` (mod-specific)

```java
/**
 * Example integration into a robot entity.
 * Shows how the pose system connects to entity lifecycle.
 */
public class RobotEntity extends TamableEntity {
    
    private PoseStateManager poseManager;
    private HitboxManager hitboxManager;
    private PoseContext poseContext;
    private IAnimationController animationController;
    
    public RobotEntity(EntityType<? extends RobotEntity> type, World world) {
        super(type, world);
        initializePoseSystem();
    }
    
    private void initializePoseSystem() {
        // Create animation controller (loader-specific implementation)
        this.animationController = createAnimationController();
        
        // Create configuration
        PoseConfiguration config = new PoseConfiguration();
        
        // Initialize managers
        this.poseContext = new PoseContext(this);
        this.poseManager = new PoseStateManager(animationController, config);
        this.hitboxManager = new HitboxManager(this, config);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (!world.isClient) {
            // Update context with current entity state
            poseContext.update();
            
            // Update pose state machine
            poseManager.tick(poseContext);
            
            // Update hitbox if pose changed
            if (poseManager.isDirty()) {
                hitboxManager.updateForPose(poseManager.getCurrentPose());
                poseManager.clearDirty();
            }
            
            // Tick hitbox transitions
            hitboxManager.tick();
        }
    }
    
    /**
     * External trigger for state changes (e.g., from AI goals).
     */
    public void setRobotState(RobotState state) {
        poseManager.setState(state, poseContext);
    }
    
    /**
     * Gets current pose for rendering or logic.
     */
    public RobotPose getCurrentPose() {
        return poseManager.getCurrentPose();
    }
    
    /**
     * Loader-specific animation controller creation.
     * Implementation in lib/forge/ or lib/fabric/
     */
    protected IAnimationController createAnimationController() {
        // This would be implemented in loader-specific code
        return new GeckoLibAnimationController(this, "main_controller");
    }
    
    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        // Return dimensions based on current robot pose
        if (poseManager != null) {
            return poseManager.getCurrentPose().getDimensions();
        }
        return super.getDimensions(pose);
    }
}
```


## Phase 2: Data-Driven Evolution (Future Goal)

### 2.1 JSON-Based State Configuration

Located in: `data/modid/robot_states/`

```json
{
  "state": "standby",
  "priority": 10,
  "initial_pose": "sitting_idle",
  "poses": [
    {
      "id": "sitting_idle",
      "animation": "sit",
      "hitbox": {
        "width": 0.6,
        "height": 1.0
      },
      "transitions": [
        {
          "target": "sitting_rest",
          "conditions": [
            {
              "type": "time_in_pose",
              "value": 6000
            }
          ]
        }
      ]
    },
    {
      "id": "sitting_rest",
      "animation": "rest",
      "hitbox": {
        "width": 0.6,
        "height": 0.8
      },
      "transitions": [
        {
          "target": "sitting_idle",
          "conditions": [
            {
              "type": "disturbance_detected",
              "value": true
            }
          ]
        }
      ]
    }
  ],
  "can_transition_to": ["follow", "defense", "disabled"]
}
```

### 2.2 Condition System

Located in: `framework/animation/conditions/`

```java
/**
 * Interface for pose transition conditions.
 * Allows data-driven condition evaluation.
 */
public interface IPoseCondition {
    
    /**
     * Evaluates if this condition is met.
     */
    boolean evaluate(PoseContext context, int ticksInPose);
    
    /**
     * Gets the condition type identifier.
     */
    String getType();
}
```

```java
/**
 * Registry for condition types.
 * Allows mods to register custom conditions.
 */
public class PoseConditionRegistry {
    
    private static final Map<String, Function<JsonObject, IPoseCondition>> FACTORIES = new HashMap<>();
    
    static {
        // Register built-in conditions
        register("time_in_pose", TimeInPoseCondition::fromJson);
        register("disturbance_detected", DisturbanceCondition::fromJson);
        register("movement_speed", MovementSpeedCondition::fromJson);
        register("is_attacking", IsAttackingCondition::fromJson);
        register("owner_nearby", OwnerNearbyCondition::fromJson);
        register("health_below", HealthBelowCondition::fromJson);
    }
    
    public static void register(String type, Function<JsonObject, IPoseCondition> factory) {
        FACTORIES.put(type, factory);
    }
    
    public static IPoseCondition create(String type, JsonObject json) {
        Function<JsonObject, IPoseCondition> factory = FACTORIES.get(type);
        if (factory == null) {
            throw new IllegalArgumentException("Unknown condition type: " + type);
        }
        return factory.apply(json);
    }
}
```

### 2.3 Example Condition Implementations

```java
public class TimeInPoseCondition implements IPoseCondition {
    
    private final int requiredTicks;
    
    public TimeInPoseCondition(int requiredTicks) {
        this.requiredTicks = requiredTicks;
    }
    
    @Override
    public boolean evaluate(PoseContext context, int ticksInPose) {
        return ticksInPose >= requiredTicks;
    }
    
    @Override
    public String getType() {
        return "time_in_pose";
    }
    
    public static TimeInPoseCondition fromJson(JsonObject json) {
        int ticks = json.get("value").getAsInt();
        return new TimeInPoseCondition(ticks);
    }
}
```

```java
public class MovementSpeedCondition implements IPoseCondition {
    
    private final float threshold;
    private final String operator; // "greater", "less", "equal"
    
    public MovementSpeedCondition(float threshold, String operator) {
        this.threshold = threshold;
        this.operator = operator;
    }
    
    @Override
    public boolean evaluate(PoseContext context, int ticksInPose) {
        float speed = context.getMovementSpeed();
        
        switch (operator) {
            case "greater": return speed > threshold;
            case "less": return speed < threshold;
            case "equal": return Math.abs(speed - threshold) < 0.01f;
            default: return false;
        }
    }
    
    @Override
    public String getType() {
        return "movement_speed";
    }
    
    public static MovementSpeedCondition fromJson(JsonObject json) {
        float threshold = json.get("threshold").getAsFloat();
        String operator = json.get("operator").getAsString();
        return new MovementSpeedCondition(threshold, operator);
    }
}
```


### 2.4 Animation Variant System

Located in: `framework/animation/`

```java
/**
 * Manages multiple animation variants for a single pose.
 * Allows random selection or condition-based selection.
 */
public class AnimationVariantManager {
    
    private final Map<String, List<AnimationVariant>> variantsByPose = new HashMap<>();
    private final Random random = new Random();
    
    /**
     * Registers an animation variant for a pose.
     */
    public void registerVariant(String poseId, AnimationVariant variant) {
        variantsByPose.computeIfAbsent(poseId, k -> new ArrayList<>()).add(variant);
    }
    
    /**
     * Selects an animation for the given pose and context.
     */
    public String selectAnimation(String poseId, PoseContext context) {
        List<AnimationVariant> variants = variantsByPose.get(poseId);
        if (variants == null || variants.isEmpty()) {
            return poseId; // Fallback to pose ID as animation key
        }
        
        // Filter by conditions
        List<AnimationVariant> eligible = variants.stream()
            .filter(v -> v.meetsConditions(context))
            .collect(Collectors.toList());
        
        if (eligible.isEmpty()) {
            return variants.get(0).getAnimationKey(); // Fallback to first
        }
        
        // Weighted random selection
        return selectWeighted(eligible);
    }
    
    private String selectWeighted(List<AnimationVariant> variants) {
        float totalWeight = variants.stream()
            .map(AnimationVariant::getWeight)
            .reduce(0f, Float::sum);
        
        float value = random.nextFloat() * totalWeight;
        float cumulative = 0f;
        
        for (AnimationVariant variant : variants) {
            cumulative += variant.getWeight();
            if (value <= cumulative) {
                return variant.getAnimationKey();
            }
        }
        
        return variants.get(0).getAnimationKey();
    }
}
```

```java
/**
 * Represents a single animation variant with conditions and weight.
 */
public class AnimationVariant {
    
    private final String animationKey;
    private final float weight;
    private final List<IPoseCondition> conditions;
    
    public AnimationVariant(String animationKey, float weight, List<IPoseCondition> conditions) {
        this.animationKey = animationKey;
        this.weight = weight;
        this.conditions = conditions;
    }
    
    public String getAnimationKey() {
        return animationKey;
    }
    
    public float getWeight() {
        return weight;
    }
    
    public boolean meetsConditions(PoseContext context) {
        return conditions.stream().allMatch(c -> c.evaluate(context, 0));
    }
}
```

### 2.5 JSON Animation Variant Configuration

Located in: `data/modid/animation_variants/`

```json
{
  "pose": "standing_idle",
  "variants": [
    {
      "animation": "idle_1",
      "weight": 50,
      "conditions": []
    },
    {
      "animation": "idle_2_stretch",
      "weight": 20,
      "conditions": [
        {
          "type": "time_in_pose",
          "value": 600
        }
      ]
    },
    {
      "animation": "idle_3_look_around",
      "weight": 30,
      "conditions": [
        {
          "type": "time_in_pose",
          "value": 400
        }
      ]
    }
  ]
}
```


## Implementation Roadmap

### Stage 1: Core Enum-Based System (Immediate)
**Goal**: Get basic pose system working with hardcoded states

**Tasks**:
1. Create `framework/animation/` package structure
2. Implement `RobotState` and `RobotPose` enums
3. Implement `IAnimationController` interface
4. Create `PoseStateManager` with basic state machine
5. Implement `PoseTransitionController` with hardcoded logic
6. Create `PoseContext` for state evaluation
7. Implement `HitboxManager` for dimension updates
8. Create loader-specific `GeckoLibAnimationController` in `lib/`
9. Integrate into `RobotEntity` in `common/`
10. Test STANDBY state: SITTING_IDLE → SITTING_REST transition

**Success Criteria**:
- Robot sits in idle pose
- After configured time, transitions to rest pose
- Hitbox shrinks during transition
- Animation plays smoothly
- System works on both Forge and Fabric

### Stage 2: Multiple States (Short-term)
**Goal**: Implement FOLLOW and DEFENSE states

**Tasks**:
1. Add FOLLOW state transitions in `PoseTransitionController`
2. Implement movement-based pose selection (idle/walk/run)
3. Add DEFENSE state with combat poses
4. Implement state interruption logic
5. Add state priority system
6. Test state transitions (STANDBY → FOLLOW → DEFENSE)
7. Verify hitbox updates across all poses

**Success Criteria**:
- Robot transitions between states based on AI
- Movement speed affects animation (walk vs run)
- Combat triggers defense poses
- Hitboxes update correctly for all poses
- No visual glitches during transitions

### Stage 3: Configuration System (Medium-term)
**Goal**: Externalize timing and thresholds

**Tasks**:
1. Create `PoseConfiguration` class
2. Add config file support (Forge/Fabric config systems)
3. Make all timing values configurable
4. Add config reload support
5. Document configuration options
6. Test with various config values

**Success Criteria**:
- All timing values in config file
- Config changes apply without restart (where possible)
- Server admins can tune behavior
- Config documented in mod documentation

### Stage 4: Animation Variants (Medium-term)
**Goal**: Support multiple animations per pose

**Tasks**:
1. Implement `AnimationVariantManager`
2. Create `AnimationVariant` class
3. Add weighted random selection
4. Integrate with `PoseStateManager`
5. Test with multiple idle animations
6. Add animation cycling logic

**Success Criteria**:
- Multiple animations can play for same pose
- Selection is weighted and random
- Animations cycle naturally
- No animation stuttering

### Stage 5: Data-Driven System (Long-term)
**Goal**: Full JSON-based configuration

**Tasks**:
1. Design JSON schema for states and poses
2. Implement `IPoseCondition` interface
3. Create `PoseConditionRegistry`
4. Implement built-in condition types
5. Create JSON parser for state definitions
6. Create JSON parser for animation variants
7. Implement data pack support
8. Add validation and error handling
9. Create migration tool from enum to JSON
10. Document JSON format and examples

**Success Criteria**:
- States defined entirely in JSON
- Conditions evaluated from JSON config
- Data packs can add/modify states
- Validation catches errors early
- Migration from enum system is smooth


## Advanced Features (Future Enhancements)

### 3.1 Pose Blending

```java
/**
 * Blends between poses for ultra-smooth transitions.
 * Requires custom animation system or advanced GeckoLib usage.
 */
public class PoseBlender {
    
    private RobotPose fromPose;
    private RobotPose toPose;
    private float blendProgress;
    private int blendDuration;
    
    public void startBlend(RobotPose from, RobotPose to, int duration) {
        this.fromPose = from;
        this.toPose = to;
        this.blendDuration = duration;
        this.blendProgress = 0f;
    }
    
    public void tick() {
        if (blendProgress < 1.0f) {
            blendProgress += 1.0f / blendDuration;
            // Apply blended animation weights
        }
    }
}
```

### 3.2 Contextual Animations

```java
/**
 * Plays one-shot animations based on events.
 * Returns to previous pose after completion.
 */
public class ContextualAnimationManager {
    
    public void playContextualAnimation(String animationKey, int priority) {
        // Interrupt current animation if priority allows
        // Play one-shot animation
        // Queue return to previous pose
    }
    
    // Examples:
    // - Wave animation when player interacts
    // - Damage reaction animation
    // - Emote animations
    // - Special ability animations
}
```

### 3.3 Pose Modifiers

```java
/**
 * Modifies poses based on equipment or status effects.
 * Example: Wings change idle pose, armor affects movement.
 */
public class PoseModifier {
    
    private final String id;
    private final Map<RobotPose, RobotPose> poseOverrides;
    private final Map<RobotPose, Float> hitboxScales;
    
    public RobotPose modifyPose(RobotPose original) {
        return poseOverrides.getOrDefault(original, original);
    }
    
    public EntityDimensions modifyDimensions(EntityDimensions original) {
        // Scale or adjust dimensions based on modifier
        return original;
    }
}
```

### 3.4 Animation Events

```java
/**
 * Triggers events at specific animation keyframes.
 * Example: Footstep sounds, particle effects, damage frames.
 */
public interface IAnimationEventListener {
    
    void onAnimationEvent(String eventName, float animationTime);
}

public class AnimationEventManager {
    
    private final Map<String, List<AnimationEvent>> eventsByAnimation = new HashMap<>();
    private final List<IAnimationEventListener> listeners = new ArrayList<>();
    
    public void registerEvent(String animation, float time, String eventName) {
        // Register keyframe event
    }
    
    public void addListener(IAnimationEventListener listener) {
        listeners.add(listener);
    }
    
    public void checkEvents(String currentAnimation, float currentTime) {
        // Fire events when keyframes are reached
    }
}
```

### 3.5 Synchronized Group Animations

```java
/**
 * Synchronizes animations across multiple robots.
 * Example: Formation marching, coordinated attacks.
 */
public class GroupAnimationController {
    
    private final List<RobotEntity> group = new ArrayList<>();
    private String synchronizedAnimation;
    private int syncTick;
    
    public void addToGroup(RobotEntity robot) {
        group.add(robot);
        syncRobot(robot);
    }
    
    public void setSynchronizedAnimation(String animation) {
        this.synchronizedAnimation = animation;
        this.syncTick = 0;
        
        // Apply to all robots in group
        for (RobotEntity robot : group) {
            robot.getAnimationController().playAnimation(animation, true, 0);
        }
    }
    
    private void syncRobot(RobotEntity robot) {
        // Synchronize animation timing
    }
}
```


## Integration with Existing Systems

### 4.1 AI Goal Integration

```java
/**
 * Example AI goal that triggers state changes.
 */
public class FollowOwnerGoal extends Goal {
    
    private final RobotEntity robot;
    
    @Override
    public boolean canStart() {
        return robot.getOwner() != null && robot.squaredDistanceTo(robot.getOwner()) > 144.0;
    }
    
    @Override
    public void start() {
        // Trigger FOLLOW state
        robot.setRobotState(RobotState.FOLLOW);
    }
    
    @Override
    public void stop() {
        // Return to STANDBY state
        robot.setRobotState(RobotState.STANDBY);
    }
    
    @Override
    public void tick() {
        // Movement logic
        // PoseContext will detect movement and update animations automatically
    }
}
```

### 4.2 Combat System Integration

```java
/**
 * Combat goal that uses DEFENSE state.
 */
public class AttackGoal extends MeleeAttackGoal {
    
    private final RobotEntity robot;
    
    @Override
    public void start() {
        super.start();
        robot.setRobotState(RobotState.DEFENSE);
        robot.getPoseContext().setAttacking(true);
    }
    
    @Override
    public void stop() {
        super.stop();
        robot.getPoseContext().setAttacking(false);
        robot.setRobotState(RobotState.STANDBY);
    }
}
```

### 4.3 Task System Integration

```java
/**
 * Task execution that uses WORKING state.
 */
public class MiningTask implements IRobotTask {
    
    @Override
    public void start(RobotEntity robot) {
        robot.setRobotState(RobotState.WORKING);
        robot.getPoseContext().setCurrentTask("mining");
    }
    
    @Override
    public void tick(RobotEntity robot) {
        // Mining logic
        // Pose system will use MINING pose automatically
    }
    
    @Override
    public void stop(RobotEntity robot) {
        robot.getPoseContext().setCurrentTask(null);
        robot.setRobotState(RobotState.STANDBY);
    }
}
```

### 4.4 Player Interaction Integration

```java
/**
 * Handles player interactions that affect poses.
 */
public class RobotInteractionHandler {
    
    public ActionResult onInteract(PlayerEntity player, RobotEntity robot, Hand hand) {
        // Wake up from rest if player interacts
        if (robot.getCurrentPose() == RobotPose.SITTING_REST) {
            robot.getPoseContext().setOwnerNearby(true);
            // This will trigger transition back to SITTING_IDLE
        }
        
        // Other interaction logic...
        return ActionResult.SUCCESS;
    }
}
```


## Testing Strategy

### 5.1 Unit Tests

```java
/**
 * Test pose transitions.
 */
public class PoseTransitionControllerTest {
    
    @Test
    public void testIdleToRestTransition() {
        PoseConfiguration config = new PoseConfiguration();
        config.setIdleToRestTicks(100);
        
        PoseTransitionController controller = new PoseTransitionController(config);
        PoseContext context = new PoseContext(mockEntity);
        
        // Should not transition before threshold
        RobotPose result = controller.getNextPose(
            RobotState.STANDBY, 
            RobotPose.SITTING_IDLE, 
            50, 
            context
        );
        assertNull(result);
        
        // Should transition after threshold
        result = controller.getNextPose(
            RobotState.STANDBY, 
            RobotPose.SITTING_IDLE, 
            100, 
            context
        );
        assertEquals(RobotPose.SITTING_REST, result);
    }
    
    @Test
    public void testMovementSpeedTransitions() {
        PoseConfiguration config = new PoseConfiguration();
        PoseTransitionController controller = new PoseTransitionController(config);
        PoseContext context = new PoseContext(mockEntity);
        
        // Test walk
        context.setMovementSpeed(0.1f);
        RobotPose result = controller.getNextPose(
            RobotState.FOLLOW, 
            RobotPose.STANDING_IDLE, 
            0, 
            context
        );
        assertEquals(RobotPose.WALKING, result);
        
        // Test run
        context.setMovementSpeed(0.4f);
        result = controller.getNextPose(
            RobotState.FOLLOW, 
            RobotPose.WALKING, 
            0, 
            context
        );
        assertEquals(RobotPose.RUNNING, result);
    }
}
```

### 5.2 Integration Tests

```java
/**
 * Test full pose system integration.
 */
public class PoseSystemIntegrationTest {
    
    @Test
    public void testFullStandbySequence() {
        // Create test entity
        RobotEntity robot = createTestRobot();
        
        // Verify initial state
        assertEquals(RobotState.STANDBY, robot.getCurrentState());
        assertEquals(RobotPose.SITTING_IDLE, robot.getCurrentPose());
        
        // Simulate time passing
        for (int i = 0; i < 6000; i++) {
            robot.tick();
        }
        
        // Should have transitioned to rest
        assertEquals(RobotPose.SITTING_REST, robot.getCurrentPose());
        
        // Verify hitbox changed
        EntityDimensions dims = robot.getDimensions(robot.getPose());
        assertEquals(0.8f, dims.height, 0.01f);
    }
}
```

### 5.3 Manual Testing Checklist

**STANDBY State**:
- [ ] Robot sits in idle pose initially
- [ ] After 5 minutes, transitions to rest pose
- [ ] Hitbox shrinks when resting
- [ ] Player interaction wakes robot from rest
- [ ] Damage wakes robot from rest

**FOLLOW State**:
- [ ] Robot stands idle when not moving
- [ ] Walking animation plays when moving slowly
- [ ] Running animation plays when moving fast
- [ ] Smooth transitions between movement speeds
- [ ] Returns to idle when stopped

**DEFENSE State**:
- [ ] Combat idle pose when no target
- [ ] Attack animation plays during attacks
- [ ] Blocking animation when taking damage
- [ ] Returns to combat idle between actions

**State Transitions**:
- [ ] STANDBY → FOLLOW works smoothly
- [ ] FOLLOW → DEFENSE works smoothly
- [ ] DEFENSE → STANDBY works smoothly
- [ ] Hitbox updates correctly during transitions
- [ ] No animation glitches during transitions

**Configuration**:
- [ ] Config file loads correctly
- [ ] Timing values can be changed
- [ ] Changes apply after restart
- [ ] Invalid values are rejected with error message


## Performance Considerations

### 6.1 Optimization Strategies

**Tick Optimization**:
- Only update pose context when needed (not every tick)
- Cache condition evaluations
- Use dirty flags to avoid redundant calculations
- Batch hitbox updates

```java
public class OptimizedPoseStateManager extends PoseStateManager {
    
    private int tickCounter = 0;
    private static final int UPDATE_INTERVAL = 5; // Update every 5 ticks
    
    @Override
    public void tick(PoseContext context) {
        tickCounter++;
        
        // Only update context periodically
        if (tickCounter % UPDATE_INTERVAL == 0) {
            context.update();
        }
        
        // Always increment pose timer
        ticksInCurrentPose++;
        
        // Check transitions less frequently for stable poses
        if (shouldCheckTransitions()) {
            super.tick(context);
        }
    }
    
    private boolean shouldCheckTransitions() {
        // Always check for high-priority states
        if (currentState == RobotState.DEFENSE) {
            return true;
        }
        
        // Check less frequently for stable states
        return tickCounter % UPDATE_INTERVAL == 0;
    }
}
```

**Memory Optimization**:
- Reuse PoseContext objects
- Pool animation variant lists
- Cache dimension calculations
- Lazy-load animation data

**Network Optimization**:
- Only sync state changes, not every tick
- Compress pose data in packets
- Client-side prediction for smooth animations

```java
public class PoseNetworkHandler {
    
    /**
     * Only sends packet when pose actually changes.
     */
    public void syncPoseToClients(RobotEntity robot) {
        if (robot.getPoseManager().isDirty()) {
            RobotPose pose = robot.getCurrentPose();
            
            // Send compact packet
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeVarInt(robot.getId());
            buf.writeByte(pose.ordinal());
            
            // Send to tracking clients
            PlayerLookup.tracking(robot).forEach(player -> {
                ServerPlayNetworking.send((ServerPlayerEntity) player, POSE_SYNC_PACKET, buf);
            });
        }
    }
}
```

### 6.2 Profiling Points

Key areas to monitor:
- `PoseStateManager.tick()` - Should be < 0.1ms per entity
- `PoseContext.update()` - Should be < 0.05ms
- `HitboxManager.tick()` - Should be < 0.02ms
- `AnimationController.playAnimation()` - Should be < 0.1ms


## Migration from GeckoLib to Custom System

### 7.1 Abstraction Benefits

The `IAnimationController` interface allows seamless migration:

```java
// Current: GeckoLib implementation
IAnimationController geckoController = new GeckoLibAnimationController(entity, "main");

// Future: Custom implementation
IAnimationController customController = new CustomAnimationController(entity);

// Pose system doesn't care which is used
PoseStateManager manager = new PoseStateManager(geckoController, config);
// OR
PoseStateManager manager = new PoseStateManager(customController, config);
```

### 7.2 Custom Animation Controller

```java
/**
 * Custom animation system implementation.
 * Replaces GeckoLib with native rendering.
 */
public class CustomAnimationController implements IAnimationController {
    
    private final CustomAnimatableEntity entity;
    private final CustomAnimationPlayer player;
    private String currentAnimation;
    
    public CustomAnimationController(CustomAnimatableEntity entity) {
        this.entity = entity;
        this.player = new CustomAnimationPlayer(entity.getModel());
    }
    
    @Override
    public void playAnimation(String animationKey, boolean looping, int transitionTicks) {
        CustomAnimation animation = entity.getAnimationRegistry().get(animationKey);
        if (animation != null) {
            player.play(animation, looping, transitionTicks);
            currentAnimation = animationKey;
        }
    }
    
    @Override
    public void stopAnimation() {
        player.stop();
        currentAnimation = null;
    }
    
    @Override
    public String getCurrentAnimation() {
        return currentAnimation;
    }
    
    @Override
    public boolean isAnimationPlaying() {
        return player.isPlaying();
    }
    
    @Override
    public void setAnimationSpeed(float speed) {
        player.setSpeed(speed);
    }
}
```

### 7.3 Migration Strategy

**Phase 1: Parallel Implementation**
- Keep GeckoLib working
- Implement custom system alongside
- Add config toggle to switch between systems

**Phase 2: Testing & Refinement**
- Test custom system thoroughly
- Fix rendering issues
- Optimize performance
- Ensure feature parity

**Phase 3: Deprecation**
- Mark GeckoLib implementation as deprecated
- Encourage users to switch to custom system
- Maintain GeckoLib support for one major version

**Phase 4: Removal**
- Remove GeckoLib dependency
- Clean up legacy code
- Update documentation


## Documentation Requirements

### 8.1 Code Documentation

All classes must follow the project coding style guide with JavaDoc comments:

```java
/**
 * <p>Coordinates pose state transitions and animation playback for robot entities.</p>
 * <p>
 * <b>Architecture:</b> Serves as the central state machine for robot poses, managing
 * transitions between behavioral states (STANDBY, FOLLOW, DEFENSE) and their associated
 * poses (sitting, walking, attacking). Decouples animation logic from entity implementation
 * through the IAnimationController abstraction.
 * <p>
 * <b>Design Decision:</b> Uses tick-based evaluation rather than event-driven to ensure
 * consistent timing across server and client. Time-based transitions (e.g., idle to rest)
 * are deterministic and don't require network synchronization.
 * <p>
 * <b>State Impact:</b> Pose changes trigger hitbox updates through the dirty flag system,
 * allowing the entity to defer expensive dimension recalculations until necessary.
 * <p>
 * <b>Performance:</b> O(1) for state transitions, O(n) for condition evaluation where n
 * is the number of active conditions (typically 1-3).
 * 
 * @see IAnimationController
 * @see PoseTransitionController
 * @see HitboxManager
 */
public class PoseStateManager {
    // Implementation
}
```

### 8.2 User Documentation

Create usage guide in `docs/documentation/usage/`:

**File**: `Robot-Animation-System.md`

```markdown
# Robot Animation & Pose System

## Overview
Robots in LovelyRobot have dynamic poses that change based on their behavior and environment.

## States and Poses

### Standby State
When a robot is not following or working:
- **Sitting Idle**: Default sitting pose
- **Sitting Rest**: After 5 minutes of inactivity, robot enters a relaxed rest pose with smaller hitbox

### Follow State
When following the owner:
- **Standing Idle**: Standing still
- **Walking**: Moving at normal speed
- **Running**: Moving at high speed

### Defense State
When in combat:
- **Combat Idle**: Alert stance
- **Attacking**: Attack animation
- **Blocking**: Defensive pose

## Configuration

Edit `config/lovelyrobot-common.toml`:

```toml
[animation]
    # Time in ticks before transitioning from idle to rest (20 ticks = 1 second)
    idleToRestTicks = 6000
    
    # Speed threshold for running animation
    runSpeedThreshold = 0.3
    
    # Smooth hitbox transitions
    smoothHitboxTransition = true
```

## Interactions

- **Waking from Rest**: Interact with a resting robot to wake them
- **Disturbances**: Nearby hostile mobs or damage will interrupt rest
- **Commands**: Use the LovelyRemote to force state changes
```

### 8.3 API Documentation

Create API reference in `docs/documentation/api/`:

**File**: `Animation-API.md`

```markdown
# Animation System API

## For Mod Developers

### Creating Custom States

```java
// Register a custom state
RobotState CUSTOM_STATE = RobotStateRegistry.register("custom_state", 
    new RobotStateBuilder()
        .priority(25)
        .canTransitionTo(RobotState.STANDBY, RobotState.FOLLOW)
        .build()
);
```

### Creating Custom Poses

```java
// Register a custom pose
RobotPose CUSTOM_POSE = RobotPoseRegistry.register("custom_pose",
    new RobotPoseBuilder()
        .animationKey("custom_animation")
        .dimensions(0.6f, 1.5f)
        .build()
);
```

### Adding Custom Conditions

```java
// Register a custom transition condition
PoseConditionRegistry.register("custom_condition", json -> {
    return new CustomCondition(json.get("parameter").getAsString());
});
```

### Listening to Pose Changes

```java
// Add a pose change listener
robot.getPoseManager().addListener((oldPose, newPose) -> {
    System.out.println("Pose changed from " + oldPose + " to " + newPose);
});
```
```

### 8.4 Architecture Decision Record

Create ADR in `docs/development/decisions/`:

**File**: `ADR_001_Animation_Pose_System.md`

```markdown
# ADR 001: Animation & Pose System Architecture

**Status**: Accepted  
**Date**: 2025-11-28  
**Decision Makers**: Development Team  

## Context

Robots need dynamic animations that respond to behavior, with hitbox changes for different poses. The system must:
- Support multiple animations per state
- Handle smooth transitions
- Work across Forge and Fabric
- Be extensible for future features
- Eventually support data-driven configuration

## Decision

Implement a state machine-based pose system with:
1. **Enum-based states** initially, evolving to data-driven
2. **Animation backend abstraction** (IAnimationController) for GeckoLib independence
3. **Separate pose and hitbox management** for clean separation of concerns
4. **Tick-based evaluation** for deterministic behavior
5. **Framework/lib/common/sources** structure for cross-loader support

## Consequences

### Positive
- Clean separation between animation logic and rendering
- Easy to add new poses and states
- Hitbox changes are automatic and smooth
- Works identically on Forge and Fabric
- Can migrate away from GeckoLib in the future
- Extensible through conditions and variants

### Negative
- Initial implementation is enum-based (less flexible)
- Requires careful synchronization between client/server
- Tick-based evaluation has small performance cost
- More complex than simple animation switching

### Risks
- GeckoLib API changes could require adapter updates
- Network synchronization issues if not handled carefully
- Performance impact with many entities (mitigated by optimization)

## Alternatives Considered

### Simple Animation Switching
**Rejected**: No support for complex transitions, hitbox changes, or conditions

### Event-Driven System
**Rejected**: Harder to synchronize, non-deterministic timing, more network traffic

### Direct GeckoLib Integration
**Rejected**: Tightly couples to GeckoLib, makes migration impossible

## Related Decisions
- Animation system architecture (this document)
- Entity framework design (future ADR)
- Data-driven configuration system (future ADR)
```


## Summary

This animation and pose system provides a robust, extensible foundation for robot behavior in the LovelyRobot mod. The architecture balances immediate implementation needs (enum-based states) with long-term goals (data-driven configuration) while maintaining clean separation from rendering backends.

### Key Architectural Decisions

1. **State Machine Pattern**: Clear, predictable behavior with defined transitions
2. **Backend Abstraction**: IAnimationController decouples from GeckoLib
3. **Composition Over Inheritance**: Managers handle specific concerns (pose, hitbox, animation)
4. **Progressive Enhancement**: Start simple (enums), evolve to complex (JSON)
5. **Cross-Loader Design**: Framework/lib/common/sources structure ensures compatibility

### Implementation Priority

**Phase 1 (Immediate)**: Enum-based system with STANDBY state
- Core classes in `framework/animation/`
- GeckoLib adapter in `lib/forge/` and `lib/fabric/`
- Entity integration in `common/entity/`
- Basic hitbox management

**Phase 2 (Short-term)**: Multiple states and movement
- FOLLOW and DEFENSE states
- Movement-based pose selection
- State interruption logic

**Phase 3 (Medium-term)**: Configuration and variants
- External configuration files
- Multiple animations per pose
- Weighted random selection

**Phase 4 (Long-term)**: Data-driven system
- JSON-based state definitions
- Condition system with registry
- Data pack support
- Full extensibility

### Success Metrics

- Robots smoothly transition between poses
- Hitboxes update correctly without glitches
- System works identically on Forge and Fabric
- Performance impact < 0.2ms per entity per tick
- Easy to add new poses and states
- Configuration is intuitive and well-documented

### Next Steps

1. Review this architecture document with the team
2. Create initial package structure in `framework/animation/`
3. Implement core interfaces and enums
4. Create GeckoLib adapter in `lib/`
5. Integrate into existing RobotEntity
6. Test STANDBY state with idle→rest transition
7. Document implementation progress in active TASK.md

---

**Document Version**: 1.0  
**Last Updated**: 2025-11-28  
**Status**: Active Development  
**Related Documents**:
- `docs/development/HZ Development.md`
- `docs/development/Comprehensive Concept Design Document - Reboot LovelyRobot & Cross-Mod Framework.md`
- `docs/development/Assembly Architecture.md`
