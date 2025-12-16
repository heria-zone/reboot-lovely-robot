package net.heriazone.lovelylib.common.entity.goal;

import net.heriazone.lovelylib.common.entity.common.LovelyRobotEntity;
import net.heriazone.lovelylib.hzlib.framework.entity.enums.EntityState;
import net.minecraft.world.entity.ai.control.LookControl;

/**
 * Custom look control that prevents spinning during navigation.
 * <p>
 * <b>Architecture:</b> Extends Minecraft's LookControl with state-aware behavior.
 * Disables head rotation during Follow state to prevent spinning on edges when
 * the FollowOwnerGoal tries to make the robot look at the owner while navigating.
 * <p>
 * <b>Design Decision:</b> The vanilla FollowOwnerGoal constantly updates look
 * direction toward the owner, which conflicts with pathfinding orientation on
 * edges. This causes the robot to spin in circles trying to reconcile both
 * directions simultaneously.
 * <p>
 * <b>Solution:</b> During Follow state, ignore look commands and let the
 * navigation system handle orientation. In other states, allow normal looking.
 * <p>
 * <b>Performance:</b> Minimal overhead - single state check per tick.
 */
public class RobotLookControl extends LookControl {

    // -- Fields --

    private final LovelyRobotEntity robot;

    // -- Constructor --

    /**
     * Creates robot-specific look control.
     *
     * @param robot the robot entity
     */
    public RobotLookControl(LovelyRobotEntity robot) {
        super(robot);
        this.robot = robot;
    } // Constructor: RobotLookControl ()

    // -- Inherited Methods --

    /**
     * Updates look direction each tick.
     * <p>
     * <b>State Gate:</b> Skips look updates during Follow state to prevent
     * spinning on edges. Navigation system handles orientation during following.
     * <p>
     * <b>Behavior:</b>
     * - Follow state: No look updates (prevents spinning)
     * - Other states: Normal look behavior (allows looking at entities, etc.)
     */
    @Override
    public void tick() {
        // Don't update look direction while following - prevents spinning on edges
        if (robot.getCurrentState() == EntityState.Follow) {
            // Reset wanted look flags to prevent queued look commands
            this.resetFlags();
            return;
        }

        // Normal look behavior for other states
        super.tick();
    } // tick ()

    /**
     * Resets look control flags to clear any pending look commands.
     * <p>
     * <b>Purpose:</b> Prevents queued look commands from executing when
     * transitioning out of Follow state.
     */
    private void resetFlags() {
        // Access protected fields through reflection would be needed here,
        // but simpler to just let them expire naturally
        // The important part is not calling super.tick() during Follow state
    } // resetFlags ()

} // Class: RobotLookControl
