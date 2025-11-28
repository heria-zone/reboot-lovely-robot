package net.msymbios.llovelyr.common.entity.goal;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.*;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.msymbios.llovelyr.source.entity.LovelyRobot;
import net.msymbios.llovelyr.source.entity.internal.enums.EntityState;
import net.msymbios.llovelyr.source.configs.LovelyConfigs;

import java.util.EnumSet;

/**
 * Enhanced defense mode goal with patrol/guard state machine.
 * <p>
 * <b>Architecture:</b> Implements dynamic defense behavior where robot alternates
 * between active patrol (wandering within base radius) and stationary guard duty
 * (standing at base with 360° rotation scan). Combines navigation, teleportation,
 * and state management for realistic area defense mechanics.
 * <p>
 * <b>Design Decision:</b> Uses base coordinates (stored in entity) rather than
 * owner position, allowing robots to guard specific locations independently.
 * Patrol/guard cycle creates engaging behavior that feels alive and attentive.
 * <p>
 * <b>State Machine:</b> PATROL (30-45s) ↔ GUARD (20-30s). Combat interrupts
 * either state, resuming after threat eliminated. Timers pause during combat.
 * <p>
 * <b>State Dependency:</b> Only executes in Defense state. Automatically enables
 * auto-attack and disables sitting when activated, ensuring combat readiness.
 * <p>
 * <b>Teleportation:</b> If robot exceeds warp range from base, attempts
 * teleportation to prevent getting permanently stuck. Validates teleport
 * destinations for walkability and space.
 * <p>
 * <b>Performance:</b> Updates pathfinding every 10 ticks (0.5s) rather than
 * every tick to reduce computational load during defense patrol.
 */
public class AiBaseDefenseGoal extends Goal {

    // -- Defense State Enum --

    private enum DefenseState {
        PATROL,  // Wandering within base radius
        GUARD    // Standing at base, rotating to scan
    } // Enum: DefenseState

    // -- Fields --

    private final LevelReader world;
    private final LovelyRobot entity;
    private final PathNavigation navigation;
    private final float speed;
    private final float maxDistance;
    private final float minDistance;
    private float oldWaterPathfindingPenalty;
    private int updateCountdownTicks;
    
    // State machine fields
    private DefenseState currentDefenseState = DefenseState.PATROL;
    private int stateTimer = 0;
    private int stateDuration = 0;
    private int patrolPauseTimer = 0;
    private int lookTimer = 0;
    private float guardRotation = 0.0f;
    private BlockPos currentPatrolTarget = null;

    // -- Constructor --

    /**
     * Creates base defense goal with patrol/guard state machine.
     * <p>
     * <b>Navigation Validation:</b> Requires GroundPathNavigation or FlyingPathNavigation
     * to ensure compatible pathfinding. Throws exception for unsupported types
     * (e.g., water-based navigation).
     *
     * @param mob robot entity to control
     * @param speed movement speed multiplier when patrolling
     * @param minDistance minimum distance from base before goal activates (blocks)
     * @param maxDistance maximum distance before teleportation triggers (blocks)
     * @throws IllegalArgumentException if navigation type unsupported
     */
    public AiBaseDefenseGoal(LovelyRobot mob, float speed, float minDistance, float maxDistance) {
        this.entity = mob;
        this.world = mob.level();
        this.speed = speed;
        this.navigation = mob.getNavigation();
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        
        if (!(mob.getNavigation() instanceof GroundPathNavigation) && !(mob.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for AiBaseDefenseGoal");
        }
    } // Constructor: AiBaseDefenseGoal

    // -- Inherited Methods --

    /**
     * Determines if goal can start executing.
     * <p>
     * <b>State Validation:</b> Only activates in Defense state. Automatically
     * disables sitting and enables auto-attack to prepare for combat.
     *
     * @return true if in Defense state
     */
    @Override
    public boolean canUse() {
        if (this.entity.getCurrentState() != EntityState.Defense) return false;
        
        if (this.entity.isInSittingPose()) this.entity.setInSittingPose(false);
        if (!this.entity.getAutoAttack()) this.entity.setAutoAttack(true);
        
        return true;
    } // canUse

    /**
     * Initiates goal execution after canUse approval.
     * <p>
     * <b>State Machine Setup:</b> Initializes patrol state with random duration.
     * Adjusts water pathfinding penalty to allow pathing through water.
     */
    @Override
    public void start() {
        if (this.entity.getCurrentState() != EntityState.Defense) return;
        
        this.updateCountdownTicks = 0;
        this.oldWaterPathfindingPenalty = this.entity.getPathfindingMalus(BlockPathTypes.WATER);
        this.entity.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        
        // Initialize patrol state
        currentDefenseState = DefenseState.PATROL;
        setRandomStateDuration();
        stateTimer = 0;
        patrolPauseTimer = 0;
        guardRotation = entity.getYRot();
    } // start

    /**
     * Halts goal execution when conditions no longer met.
     * <p>
     * <b>Cleanup:</b> Stops navigation and restores original water pathfinding
     * penalty to prevent affecting other goals' pathfinding behavior.
     */
    @Override
    public void stop() {
        this.navigation.stop();
        this.entity.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterPathfindingPenalty);
        currentPatrolTarget = null;
    } // stop

    /**
     * Determines if goal should continue executing.
     * <p>
     * <b>State Check:</b> Continues as long as robot is in Defense state.
     * Disables sitting if somehow enabled during execution.
     *
     * @return true if should continue defense behavior
     */
    @Override
    public boolean canContinueToUse() {
        if (this.entity.getCurrentState() != EntityState.Defense) return false;
        if (this.entity.isInSittingPose()) this.entity.setInSittingPose(false);
        return true;
    } // canContinueToUse

    /**
     * Updates goal state each tick while executing.
     * <p>
     * <b>State Machine:</b> Executes current state behavior (PATROL or GUARD).
     * Handles state transitions, combat interruptions, and teleportation.
     * <p>
     * <b>Combat Handling:</b> Pauses state timer during combat, resumes after.
     */
    @Override
    public void tick() {
        double distanceToBase = this.entity.distanceToSqr(this.entity.getBaseX(), this.entity.getBaseY(), this.entity.getBaseZ());
        
        // Check if too far from base
        if (distanceToBase >= (this.maxDistance * this.maxDistance)) {
            // Only teleport if REALLY far (2x warp range) - otherwise try to walk back
            if (distanceToBase >= (this.maxDistance * this.maxDistance * 4)) {
                tryTeleport(); // Emergency teleport if stuck very far
            } else {
                // Try to walk back to base
                if (--updateCountdownTicks <= 0) {
                    updateCountdownTicks = 10;
                    navigation.moveTo(entity.getBaseX(), entity.getBaseY(), entity.getBaseZ(), speed);
                }
            }
            return;
        }
        
        // Pause state timer during combat
        if (entity.isWary()) {
            return; // In combat, don't update state machine
        }
        
        // Execute current state behavior
        switch (currentDefenseState) {
            case PATROL:
                tickPatrol();
                break;
            case GUARD:
                tickGuard();
                break;
        }
        
        // Update state timer and check for transition
        stateTimer++;
        if (stateTimer >= stateDuration) {
            transitionState();
        }
    } // tick

    // -- State Machine Methods --

    /**
     * Executes patrol state behavior.
     * <p>
     * <b>Behavior:</b> Wanders to random points within base radius, pausing
     * briefly at each point. Creates natural patrol pattern.
     * <p>
     * <b>Pause Mechanic:</b> Pauses for 2-4 seconds at each patrol point before
     * selecting next destination. Prevents constant movement.
     */
    private void tickPatrol() {
        // Check if pausing at patrol point
        if (patrolPauseTimer > 0) {
            patrolPauseTimer--;
            
            // Look around while paused - but only change direction every 20 ticks (1 second)
            if (--lookTimer <= 0) {
                lookTimer = 20; // Reset look timer (1 second between look changes)
                entity.getLookControl().setLookAt(
                    entity.getBaseX() + (entity.getRandom().nextDouble() - 0.5) * minDistance,
                    entity.getBaseY(),
                    entity.getBaseZ() + (entity.getRandom().nextDouble() - 0.5) * minDistance,
                    10.0F,
                    (float) entity.getMaxHeadXRot()
                );
            }
            return;
        }
        
        // Check if need new patrol target
        if (currentPatrolTarget == null || navigation.isDone() || entity.distanceToSqr(currentPatrolTarget.getX(), currentPatrolTarget.getY(), currentPatrolTarget.getZ()) < 4.0) {
            // Select new random patrol point within base radius
            currentPatrolTarget = selectRandomPatrolPoint();
            if (currentPatrolTarget != null) {
                navigation.moveTo(currentPatrolTarget.getX(), currentPatrolTarget.getY(), currentPatrolTarget.getZ(), speed);
                
                // Set random pause duration for when we reach this point
                int pauseRange = LovelyConfigs.PatrolPauseDurationMax - LovelyConfigs.PatrolPauseDurationMin;
                patrolPauseTimer = LovelyConfigs.PatrolPauseDurationMin + entity.getRandom().nextInt(pauseRange + 1);
            }
        }
    } // tickPatrol

    /**
     * Executes guard state behavior.
     * <p>
     * <b>Behavior:</b> Returns to base position and performs slow 360° rotation
     * scan. Creates vigilant guard appearance.
     * <p>
     * <b>Rotation:</b> Rotates at configured speed (default 0.05 radians/tick)
     * for smooth, continuous scanning motion.
     */
    private void tickGuard() {
        // Return to base position if not there
        double distanceToBase = entity.distanceToSqr(entity.getBaseX(), entity.getBaseY(), entity.getBaseZ());
        if (distanceToBase > (minDistance * minDistance)) {
            if (--updateCountdownTicks <= 0) {
                updateCountdownTicks = 10;
                navigation.moveTo(entity.getBaseX(), entity.getBaseY(), entity.getBaseZ(), speed);
            }
        } else {
            // At base, stop moving and rotate
            navigation.stop();
            
            // Perform slow 360° rotation scan
            guardRotation += (float) LovelyConfigs.GuardRotationSpeed;
            if (guardRotation >= Math.PI * 2) {
                guardRotation -= (float) (Math.PI * 2);
            }
            
            // Calculate look target based on rotation
            double lookX = entity.getBaseX() + Math.cos(guardRotation) * 5.0;
            double lookZ = entity.getBaseZ() + Math.sin(guardRotation) * 5.0;
            
            entity.getLookControl().setLookAt(lookX, entity.getBaseY(), lookZ, 10.0F, (float) entity.getMaxHeadXRot());
        }
    } // tickGuard

    /**
     * Transitions between patrol and guard states.
     * <p>
     * <b>Cycle:</b> PATROL → GUARD → PATROL → ...
     * <p>
     * <b>Duration:</b> Each state has random duration within configured range
     * for natural behavior variation.
     */
    private void transitionState() {
        switch (currentDefenseState) {
            case PATROL:
                currentDefenseState = DefenseState.GUARD;
                currentPatrolTarget = null;
                navigation.stop();
                break;
            case GUARD:
                currentDefenseState = DefenseState.PATROL;
                guardRotation = entity.getYRot();
                break;
        }
        
        // Reset timer and set new random duration
        stateTimer = 0;
        setRandomStateDuration();
    } // transitionState

    /**
     * Sets random duration for current state based on configuration.
     * <p>
     * <b>Patrol Duration:</b> 30-45 seconds (configurable)
     * <p>
     * <b>Guard Duration:</b> 20-30 seconds (configurable)
     */
    private void setRandomStateDuration() {
        int durationMin, durationMax;
        
        switch (currentDefenseState) {
            case PATROL:
                durationMin = LovelyConfigs.PatrolDurationMin;
                durationMax = LovelyConfigs.PatrolDurationMax;
                break;
            case GUARD:
                durationMin = LovelyConfigs.GuardDurationMin;
                durationMax = LovelyConfigs.GuardDurationMax;
                break;
            default:
                durationMin = 600;
                durationMax = 900;
        }
        
        int durationRange = durationMax - durationMin;
        stateDuration = durationMin + entity.getRandom().nextInt(durationRange + 1);
    } // setRandomStateDuration

    /**
     * Selects random patrol point within base defense radius.
     * <p>
     * <b>Selection:</b> Generates random point within BaseDefenceRange of base.
     * Validates point is walkable before returning.
     * <p>
     * <b>Attempts:</b> Tries up to 10 times to find valid point. Returns null
     * if no valid point found (robot will retry next tick).
     *
     * @return valid patrol point, or null if none found
     */
    private BlockPos selectRandomPatrolPoint() {
        for (int attempt = 0; attempt < 10; attempt++) {
            int offsetX = entity.getRandom().nextInt((int) (minDistance * 2)) - (int) minDistance;
            int offsetZ = entity.getRandom().nextInt((int) (minDistance * 2)) - (int) minDistance;
            
            BlockPos targetPos = new BlockPos(
                (int) entity.getBaseX() + offsetX,
                (int) entity.getBaseY(),
                (int) entity.getBaseZ() + offsetZ
            );
            
            // Validate point is walkable
            if (canTeleportTo(targetPos)) {
                return targetPos;
            }
        }
        
        return null; // No valid point found, will retry next tick
    } // selectRandomPatrolPoint

    // -- Teleportation Methods --

    /**
     * Attempts to teleport robot near base position.
     * <p>
     * <b>Search Pattern:</b> Tries 10 random positions within 3-block radius
     * of base, checking each for teleportation validity.
     * <p>
     * <b>Early Exit:</b> Returns immediately on first successful teleport to
     * avoid unnecessary position checks.
     */
    private void tryTeleport() {
        for (int i = 0; i < 10; ++i) {
            int j = this.getRandomInt(-3, 3);
            int k = this.getRandomInt(-1, 1);
            int l = this.getRandomInt(-3, 3);
            boolean bl = this.tryTeleportTo((int) this.entity.getBaseX() + j, (int) this.entity.getBaseY() + k, (int) this.entity.getBaseZ() + l);
            if (bl) return;
        }
    } // tryTeleport

    /**
     * Attempts to teleport robot to specific coordinates.
     * <p>
     * <b>Proximity Check:</b> Rejects teleports within 2 blocks of base to
     * prevent micro-teleports when already near destination.
     * <p>
     * <b>Validity Check:</b> Validates destination is walkable and has space
     * for robot's bounding box.
     * <p>
     * <b>Execution:</b> If valid, teleports robot and stops navigation to
     * prevent pathfinding conflicts.
     *
     * @param x target X coordinate
     * @param y target Y coordinate
     * @param z target Z coordinate
     * @return true if teleportation succeeded
     */
    private boolean tryTeleportTo(int x, int y, int z) {
        if (Math.abs((double) x - this.entity.getBaseX()) < 2.0 && Math.abs((double) z - this.entity.getBaseZ()) < 2.0) {
            return false;
        } else if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        } else {
            this.entity.moveTo((double) x + 0.5, (double) y, (double) z + 0.5, this.entity.getYRot(), this.entity.getXRot());
            this.navigation.stop();
            return true;
        }
    } // tryTeleportTo

    /**
     * Validates if position is safe for teleportation.
     * <p>
     * <b>Walkability Check:</b> Uses WalkNodeEvaluator to determine if position
     * is walkable terrain (not lava, void, etc.).
     * <p>
     * <b>Leaves Rejection:</b> Prevents teleporting onto leaves blocks to avoid
     * robots getting stuck in trees.
     * <p>
     * <b>Space Check:</b> Verifies robot's bounding box fits at destination
     * without colliding with blocks or entities.
     *
     * @param pos target position to validate
     * @return true if position is safe for teleportation
     */
    private boolean canTeleportTo(BlockPos pos) {
        BlockPathTypes pathNodeType = WalkNodeEvaluator.getBlockPathTypeStatic(this.world, pos.mutable());
        if (pathNodeType != BlockPathTypes.WALKABLE) {
            return false;
        } else {
            BlockState blockState = this.world.getBlockState(pos.below());
            if (blockState.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockPos = pos.subtract(this.entity.blockPosition());
                return this.world.noCollision(this.entity, this.entity.getBoundingBox().move(blockPos));
            }
        }
    } // canTeleportTo

    /**
     * Generates random integer within inclusive range.
     *
     * @param min minimum value (inclusive)
     * @param max maximum value (inclusive)
     * @return random integer in [min, max]
     */
    private int getRandomInt(int min, int max) {
        return this.entity.getRandom().nextInt(max - min + 1) + min;
    } // getRandomInt

} // Class: AiBaseDefenseGoal
