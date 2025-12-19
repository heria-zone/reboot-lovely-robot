package net.heriazone.lovelylib.common.entity.goal;

import net.heriazone.lovelylib.common.entity.LovelyRobotEntity;
import net.heriazone.lovelylib.hzlib.framework.entity.enums.EntityState;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;

/**
 * State-aware random look goal that prevents interference with navigation.
 * <p>
 * <b>Architecture:</b> Extends RandomLookAroundGoal with state-based execution control.
 * Prevents robots from looking around while navigating, which can cause head-turning
 * conflicts with pathfinding goals.
 * <p>
 * <b>Design Decision:</b> Disables random looking during Follow state to prevent conflict
 * with FollowOwnerGoal navigation. Allows looking in Standby and Defense (when guarding)
 * to maintain natural idle behavior.
 * <p>
 * <b>Behavior Impact:</b> Robots will randomly look around when stationary in Standby
 * or Defense states, but not while actively navigating. This maintains natural idle
 * animations without causing navigation issues.
 */
public class AiConditionalRandomLookGoal extends RandomLookAroundGoal {

    // -- Fields --

    private final LovelyRobotEntity entity;

    // -- Constructor --

    /**
     * Creates state-aware random look goal.
     *
     * @param entity robot entity
     */
    public AiConditionalRandomLookGoal(LovelyRobotEntity entity) {
        super(entity);
        this.entity = entity;
    } // Constructor: AiConditionalRandomLookGoal ()

    // -- Inherited Methods --

    /**
     * Determines if goal can start executing.
     * <p>
     * <b>State Gate:</b> Prevents looking during Follow state to avoid navigation conflicts.
     * <p>
     * <b>Movement Check:</b> Also prevents looking when robot is moving, regardless of state.
     * This ensures smooth navigation without head-turning interference.
     * <p>
     * <b>Conditions:</b>
     * - Not in Follow state (prevents head-turning while following owner)
     * - Not moving (prevents interference during navigation)
     * - Parent conditions met (random chance, etc.)
     *
     * @return true if should start looking around
     */
    @Override
    public boolean canUse() {
        // Don't look while following - causes navigation issues
        if (entity.getCurrentState() == EntityState.Follow) return false;

        return super.canUse();
    } // canUse ()

    /**
     * Determines if goal should continue executing.
     * <p>
     * <b>Continuous Validation:</b> Stops looking if robot starts moving or enters Follow state.
     * Provides immediate response to state/movement changes.
     *
     * @return true if should continue looking around
     */
    @Override
    public boolean canContinueToUse() {
        // Stop looking if state changes to Follow
        if (entity.getCurrentState() == EntityState.Follow) return false;

        return super.canContinueToUse();
    } // canContinueToUse ()

} // Class: AiConditionalRandomLookGoal
