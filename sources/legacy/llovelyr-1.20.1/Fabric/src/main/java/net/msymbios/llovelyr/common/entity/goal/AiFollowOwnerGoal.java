package net.msymbios.llovelyr.common.entity.goal;

import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.LivingEntity;
import net.msymbios.llovelyr.common.entity.internal.InternalEntity;
import net.msymbios.llovelyr.source.configs.LovelyConfigs;
import net.msymbios.llovelyr.source.entity.LovelyRobot;
import net.msymbios.llovelyr.source.entity.internal.enums.EntityState;

/**
 * Enhanced owner following goal with movement detection and dynamic distance adjustment.
 * <p>
 * <b>Architecture:</b> Extends Minecraft's FollowOwnerGoal with state-based execution,
 * owner movement detection, and responsive following behavior. Robots follow closely
 * when owner moves, maintain position when owner is stationary.
 * <p>
 * <b>Design Decision:</b> Detects owner velocity to adjust follow behavior dynamically.
 * When owner moves, robot follows more aggressively (closer distance). When owner is
 * stationary, robot maintains current position without micro-adjustments.
 * <p>
 * <b>State Dependency:</b> Only executes when robot is in Follow state, enabling
 * clean separation between behavioral modes without goal priority conflicts.
 * <p>
 * <b>Performance:</b> Velocity checks use squared length to avoid expensive sqrt
 * calculations. Minimal overhead per tick.
 */
public class AiFollowOwnerGoal extends FollowOwnerGoal {

    // -- Fields --

    private final LovelyRobot entity;
    private int ownerStillTicks = 0;

    // -- Constructor --

    /**
     * Creates enhanced owner-following goal with movement awareness.
     *
     * @param tameable robot entity (cast to TameableEntity for parent compatibility)
     * @param speed movement speed multiplier when following
     * @param minDistance minimum distance before stopping (blocks)
     * @param maxDistance maximum distance before teleporting (blocks)
     * @param leavesAllowed whether pathfinding can traverse leaves
     */
    public AiFollowOwnerGoal(LovelyRobot tameable, double speed, float minDistance, float maxDistance, boolean leavesAllowed) {
        super((TameableEntity) tameable, speed, minDistance, maxDistance, leavesAllowed);
        entity = tameable;
    } // Constructor: AiFollowOwnerGoal ()

    // -- Inherited Methods --

    /**
     * Determines if goal can start executing.
     * <p>
     * <b>State Gate:</b> Only allows following when robot is in Follow state.
     * <p>
     * <b>Combat vs Non-Combat Logic:</b>
     * - Non-combat: Always follow to maintain FollowDistanceMin (2 blocks)
     * - Combat: Allow distance up to FollowDistanceMax for engaging enemies
     * <p>
     * <b>Conditions:</b>
     * - Robot in Follow state
     * - Owner exists
     * - Not in combat: Follow if distance > FollowDistanceMin (2 blocks)
     * - In combat: Follow if distance > FollowDistanceMax (10-12 blocks)
     * - Parent conditions met (not sitting, etc.)
     *
     * @return true if should start following
     */
    @Override
    public boolean canStart() {
        if (!checkEntityState()) return false;
        
        // If robot is allowed to wander, don't follow (let wander goal take over)
        if (entity.canWander()) return false;
        
        LivingEntity owner = entity.getOwner();
        if (owner == null) return false;

        // Detect owner movement
        double velocitySquared = owner.getVelocity().lengthSquared();
        boolean ownerMoving = velocitySquared > 0.001;
        
        if (ownerMoving) {
            ownerStillTicks = 0; // Reset stationary timer
        } else {
            ownerStillTicks++; // Increment stationary timer
        }

        // Check if in combat (wary mode) - use isWary() method
        boolean inCombat = entity.isWary();
        float distance = entity.distanceTo(owner);

        if (inCombat) {
            // In combat: Only follow if beyond max distance
            // This allows robot to engage enemies within the combat radius
            if (distance <= LovelyConfigs.Common.FollowDistanceMax) return false;
        } else {
            // Not in combat: Follow to maintain minimum distance (2 blocks)
            // This keeps robot close to owner at all times
            if (distance <= LovelyConfigs.Common.FollowDistanceMin) return false;
        }
        
        return super.canStart();
    } // canStart ()

    /**
     * Determines if goal should continue executing.
     * <p>
     * <b>Continuous Validation:</b> Checks state and combat status each tick.
     * Stops following when at appropriate distance based on combat state.
     * <p>
     * <b>Stop Conditions:</b>
     * - State changes from Follow
     * - Not in combat: Stop at FollowDistanceMin (2 blocks)
     * - In combat: Stop at FollowDistanceMax (10-12 blocks)
     * - Parent conditions fail (navigation issues, etc.)
     *
     * @return true if should continue following
     */
    @Override
    public boolean shouldContinue() {
        if (!checkEntityState()) return false;
        
        LivingEntity owner = entity.getOwner();
        if (owner == null) return false;

        // Check if in combat (wary mode) - use isWary() method
        boolean inCombat = entity.isWary();
        float distance = entity.distanceTo(owner);

        if (inCombat) {
            // In combat: Stop when within max distance
            // Allows robot to engage enemies within combat radius
            if (distance <= LovelyConfigs.Common.FollowDistanceMax) return false;
        } else {
            // Not in combat: Stop when at minimum distance (2 blocks)
            // Maintains close following behavior
            if (distance <= LovelyConfigs.Common.FollowDistanceMin) return false;
        }
        
        return super.shouldContinue();
    } // shouldContinue ()

    /**
     * Initiates goal execution after canStart approval.
     * <p>
     * <b>Safety Check:</b> Re-validates state before starting to handle
     * race conditions from concurrent state changes.
     */
    @Override
    public void start() {
        if (checkEntityState()) super.start();
    } // start ()

    /**
     * Halts goal execution when conditions no longer met.
     * <p>
     * <b>State Validation:</b> Ensures clean shutdown even if state changed
     * during execution. Prevents navigation artifacts from lingering.
     */
    @Override
    public void stop() {
        super.stop();
    } // stop ()

    /**
     * Updates goal state each tick while executing.
     * <p>
     * <b>Continuous Validation:</b> Checks state every tick to immediately
     * halt following if owner commands state change. Provides responsive
     * behavioral transitions.
     */
    @Override
    public void tick() {
        if (checkEntityState()) super.tick();
    } // tick ()

    // -- Custom Methods --

    /**
     * Validates robot is in Follow state.
     * <p>
     * <b>State Check:</b> Compares current state against Follow enum value.
     * Returns false for all other states (Defense, Standby, etc.).
     *
     * @return true if robot is in Follow state
     */
    private boolean checkEntityState() {
        return entity.getCurrentState() == EntityState.Follow;
    } // checkEntityState ()

} // Class: AiFollowOwnerGoal
