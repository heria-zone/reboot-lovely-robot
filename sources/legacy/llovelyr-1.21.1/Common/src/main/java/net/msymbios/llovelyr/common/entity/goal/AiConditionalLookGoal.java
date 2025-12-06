package net.msymbios.llovelyr.common.entity.goal;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity;
import net.msymbios.llovelyr.framework.entity.enums.EntityState;

/**
 * State-aware look goal that prevents interference with navigation.
 * <p>
 * <b>Architecture:</b> Extends LookAtPlayerGoal with state-based execution control.
 * Prevents robots from constantly looking at entities while navigating, which causes
 * spinning behavior on edges and during following.
 * <p>
 * <b>Design Decision:</b> Disables looking during Follow state to prevent conflict
 * with FollowOwnerGoal navigation. The robot needs to focus on pathfinding rather
 * than constantly turning to look at the owner.
 * <p>
 * <b>Behavior Impact:</b> Robots will only look at entities when stationary or in
 * non-navigation states (Standby, Defense when guarding). This eliminates the
 * spinning issue while maintaining natural looking behavior when appropriate.
 */
public class AiConditionalLookGoal extends LookAtPlayerGoal {

    // -- Fields --

    private final LovelyRobotEntity entity;

    // -- Constructor --

    /**
     * Creates state-aware look goal.
     *
     * @param entity robot entity
     * @param targetType entity type to look at (Player.class, LivingEntity.class, etc.)
     * @param lookDistance maximum distance to look at entities (blocks)
     */
    public AiConditionalLookGoal(LovelyRobotEntity entity, Class<? extends LivingEntity> targetType, float lookDistance) {
        super(entity, targetType, lookDistance);
        this.entity = entity;
    } // Constructor: AiConditionalLookGoal ()

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
     * - Not in Follow state (prevents spinning while following owner)
     * - Not moving (prevents head-turning during navigation)
     * - Parent conditions met (entity in range, etc.)
     *
     * @return true if should start looking
     */
    @Override
    public boolean canUse() {
        // Don't look while following - causes spinning on edges
        if (entity.getCurrentState() == EntityState.Follow) return false;

        // Don't look while moving - interferes with navigation
        if (entity.getDeltaMovement().lengthSqr() > 0.001) return false;

        return super.canUse();
    } // canUse ()

    /**
     * Determines if goal should continue executing.
     * <p>
     * <b>Continuous Validation:</b> Stops looking if robot starts moving or enters Follow state.
     * Provides immediate response to state/movement changes.
     *
     * @return true if should continue looking
     */
    @Override
    public boolean canContinueToUse() {
        // Stop looking if state changes to Follow
        if (entity.getCurrentState() == EntityState.Follow) return false;

        // Stop looking if robot starts moving
        if (entity.getDeltaMovement().lengthSqr() > 0.001) return false;

        return super.canContinueToUse();
    } // canContinueToUse ()

} // Class: AiConditionalLookGoal
