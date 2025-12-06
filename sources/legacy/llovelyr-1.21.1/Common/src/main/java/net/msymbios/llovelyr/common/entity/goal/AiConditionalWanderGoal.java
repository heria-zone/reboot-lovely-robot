package net.msymbios.llovelyr.common.entity.goal;

import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.LivingEntity;
import net.msymbios.llovelyr.common.Configs.SharedConfigs;
import net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity;
import net.msymbios.llovelyr.framework.entity.enums.EntityState;

/**
 * State-aware conditional wander goal with owner movement detection.
 * <p>
 * <b>Architecture:</b> Extends vanilla WaterAvoidingRandomStrollGoal with sophisticated
 * conditions that respect robot state, owner movement, and cooldown timers.
 * Ensures robots only wander rarely when owner is stationary for extended periods.
 * <p>
 * <b>Design Decision:</b> Wander is intentionally rare (15% chance per check)
 * to prioritize following behavior. Owner movement immediately cancels wandering,
 * ensuring responsive companion behavior.
 * <p>
 * <b>State Dependency:</b> Only executes in Follow mode. Defense and Standby modes
 * use their own movement logic.
 * <p>
 * <b>Performance:</b> Checks conditions based on owner stationary time rather than
 * fixed intervals, minimizing computational overhead while maintaining responsive behavior.
 */
public class AiConditionalWanderGoal extends WaterAvoidingRandomStrollGoal {

    // -- Fields --

    private final LovelyRobotEntity robot;

    private int ownerStillTicks = 0;
    private int wanderCooldown = 0;
    private int wanderDuration = 0;
    private int wanderDurationTimer = 0;

    private double lastOwnerX = 0;
    private double lastOwnerY = 0;
    private double lastOwnerZ = 0;

    private double wanderStartX = 0;
    private double wanderStartY = 0;
    private double wanderStartZ = 0;
    private float wanderStartYaw = 0;
    private float wanderStartPitch = 0;

    // -- Constructor --

    /**
     * Creates conditional wander goal with state and owner awareness.
     *
     * @param robot robot entity to control
     * @param speed movement speed multiplier when wandering
     */
    public AiConditionalWanderGoal(LovelyRobotEntity robot, double speed) {
        super(robot, speed);
        this.robot = robot;
    } // Constructor: AiConditionalWanderGoal

    // -- Inherited Methods --

    /**
     * Determines if goal can start executing.
     * <p>
     * <b>Conditions Checked:</b>
     * <ul>
     * <li>Robot in Follow state (not Defense or Standby)</li>
     * <li>Owner exists and is alive</li>
     * <li>Owner stationary for > OwnerStillThreshold ticks</li>
     * <li>Wander cooldown expired</li>
     * <li>Random chance met (WanderChance)</li>
     * <li>Robot not in combat</li>
     * <li>Robot close to owner (within wander radius)</li>
     * </ul>
     *
     * @return true if all conditions met and should start wandering
     */
    @Override
    public boolean canUse() {
        // Only wander in Follow state
        if (robot.getCurrentState() != EntityState.Follow) {
            robot.setCanWander(false);
            return false;
        }

        // Check owner exists
        LivingEntity owner = robot.getOwner();
        if (owner == null || !owner.isAlive()) {
            robot.setCanWander(false);
            return false;
        }

        // Update owner movement tracking
        updateOwnerMovement(owner);

        // Owner must be stationary for threshold duration
        if (ownerStillTicks < SharedConfigs.Common.OwnerStillThreshold) {
            robot.setCanWander(false);
            return false;
        }

        // Check wander cooldown
        if (wanderCooldown > 0) {
            wanderCooldown--;
            robot.setCanWander(false);
            return false;
        }

        // Random chance check (15% by default)
        if (robot.getRandom().nextDouble() > SharedConfigs.Common.WanderChance) {
            robot.setCanWander(false);
            return false;
        }

        // Don't wander during combat
        if (robot.isWary()) {
            robot.setCanWander(false);
            return false;
        }

        // Must be close to owner (within wander radius)
        double distanceToOwner = robot.distanceTo(owner);
        if (distanceToOwner > SharedConfigs.Common.WanderRadiusMax) {
            robot.setCanWander(false);
            return false;
        }

        // All conditions met, allow wandering
        robot.setCanWander(true);

        // All conditions met, allow parent to check pathfinding
        return super.canUse();
    } // canUse

    /**
     * Initiates goal execution after canUse approval.
     * <p>
     * <b>Setup:</b> Sets random wander duration, stores start position for return,
     * and enables wander flag. Duration varies between WanderDurationMin and
     * WanderDurationMax for natural behavior variation.
     */
    @Override
    public void start() {
        // Store wander start position and rotation for exact return
        wanderStartX = robot.getX();
        wanderStartY = robot.getY();
        wanderStartZ = robot.getZ();
        wanderStartYaw = robot.getYRot();
        wanderStartPitch = robot.getXRot();

        // Set random wander duration (5-10 seconds by default)
        int durationRange = SharedConfigs.Common.WanderDurationMax - SharedConfigs.Common.WanderDurationMin;
        wanderDuration = SharedConfigs.Common.WanderDurationMin + robot.getRandom().nextInt(durationRange + 1);
        wanderDurationTimer = 0;

        // Enable wander flag
        robot.setCanWander(true);

        super.start();
    } // start

    /**
     * Determines if goal should continue executing.
     * <p>
     * <b>Stop Conditions:</b>
     * <ul>
     * <li>Owner starts moving (responsive cancellation)</li>
     * <li>Wander duration expired</li>
     * <li>Robot enters combat</li>
     * <li>State changes from Follow</li>
     * <li>Parent pathfinding fails</li>
     * </ul>
     *
     * @return true if should continue wandering
     */
    @Override
    public boolean canContinueToUse() {
        // Stop if state changed
        if (robot.getCurrentState() != EntityState.Follow) {
            robot.setCanWander(false);
            return false;
        }

        // Stop if owner starts moving
        LivingEntity owner = robot.getOwner();
        if (owner != null) {
            updateOwnerMovement(owner);
            if (ownerStillTicks == 0) {
                robot.setCanWander(false);
                return false; // Owner moved, stop wandering immediately
            }
        }

        // Stop if duration expired
        wanderDurationTimer++;
        if (wanderDurationTimer >= wanderDuration) {
            robot.setCanWander(false);
            return false;
        }

        // Stop if entered combat
        if (robot.isWary()) {
            robot.setCanWander(false);
            return false;
        }

        // Check parent conditions (pathfinding, etc.)
        return super.canContinueToUse();
    } // canContinueToUse

    /**
     * Updates goal state each tick while executing.
     * <p>
     * <b>Behavior:</b> Continues wandering within limited radius of owner.
     * Constantly monitors for owner movement to enable immediate cancellation.
     */
    @Override
    public void tick() {
        super.tick();

        // Continue tracking owner movement
        LivingEntity owner = robot.getOwner();
        if (owner != null) {
            updateOwnerMovement(owner);
        }
    } // tick

    /**
     * Halts goal execution when conditions no longer met.
     * <p>
     * <b>Cleanup:</b> Sets cooldown timer, disables wander flag, and paths robot
     * back to wander start position. Cooldown prevents immediate re-wandering.
     */
    @Override
    public void stop() {
        super.stop();

        // Disable wander flag
        robot.setCanWander(false);

        // Set random cooldown (20-40 seconds by default)
        int cooldownRange = SharedConfigs.Common.WanderCooldownMax - SharedConfigs.Common.WanderCooldownMin;
        wanderCooldown = SharedConfigs.Common.WanderCooldownMin + robot.getRandom().nextInt(cooldownRange + 1);

        // Path back to wander start position
        robot.getNavigation().moveTo(wanderStartX, wanderStartY, wanderStartZ, SharedConfigs.Common.MovementFollowOwner);

        // Set rotation to original facing direction
        robot.setYRot(wanderStartYaw);
        robot.setXRot(wanderStartPitch);
    } // stop

    // -- Custom Methods --

    /**
     * Updates owner movement tracking and stationary timer.
     * <p>
     * <b>Movement Detection:</b> Compares owner's current position with last
     * known position. Movement threshold is 0.001 blocks to account for
     * floating-point precision and minor position adjustments.
     * <p>
     * <b>Timer Management:</b> Increments stationary timer when owner still,
     * resets to zero when owner moves. This creates responsive behavior where
     * robot immediately stops wandering when owner starts moving.
     *
     * @param owner the robot's owner entity
     */
    private void updateOwnerMovement(LivingEntity owner) {
        double currentX = owner.getX();
        double currentY = owner.getY();
        double currentZ = owner.getZ();

        // Calculate distance moved since last check
        double deltaX = currentX - lastOwnerX;
        double deltaY = currentY - lastOwnerY;
        double deltaZ = currentZ - lastOwnerZ;
        double distanceMoved = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

        // Update last known position
        lastOwnerX = currentX;
        lastOwnerY = currentY;
        lastOwnerZ = currentZ;

        // Check if owner moved (threshold accounts for floating-point precision)
        if (distanceMoved > 0.001) {
            // Owner moved, reset stationary timer and disable wander
            ownerStillTicks = 0;
            robot.setCanWander(false);
        } else {
            // Owner still, increment timer
            ownerStillTicks++;
        }
    } // updateOwnerMovement

} // Class: AiConditionalWanderGoal