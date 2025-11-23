package net.msymbios.llovelyr.common.entity.goal;

import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.msymbios.llovelyr.common.entity.internal.InternalEntity;
import net.msymbios.llovelyr.source.entity.LovelyRobot;
import net.msymbios.llovelyr.source.entity.internal.enums.EntityState;

/**
 * State-aware owner following goal for robot entities.
 * <p>
 * <b>Architecture:</b> Extends Minecraft's FollowOwnerGoal with state-based
 * execution gating, ensuring robots only follow owners when in Follow state.
 * Prevents following during Defense, Standby, or other behavioral modes.
 * <p>
 * <b>Design Decision:</b> Wraps all lifecycle methods (canStart, start, stop, tick)
 * with state validation rather than just canStart. Ensures goal immediately
 * halts if state changes mid-execution (e.g., owner commands "stay" while
 * robot is pathfinding).
 * <p>
 * <b>State Dependency:</b> Only executes when robot is in Follow state,
 * enabling clean separation between behavioral modes without goal priority
 * conflicts.
 */
public class AiFollowOwnerGoal extends FollowOwnerGoal {

    // -- Fields --

    /**
     * Robot entity reference for state queries.
     */
    private final LovelyRobot entity;

    // -- Constructor --

    /**
     * Creates owner-following goal with state-based execution control.
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
    } // Constructor: AiFollowOwnerGoal

    // -- Inherited Methods --

    /**
     * Determines if goal can start executing.
     * <p>
     * <b>State Gate:</b> Only allows following when robot is in Follow state.
     * Prevents goal from starting during Defense, Standby, or other modes.
     *
     * @return true if in Follow state and parent conditions met
     */
    @Override
    public boolean canStart() {
        if (!checkEntityState()) return false;
        return super.canStart();
    } // canStart

    /**
     * Initiates goal execution after canStart approval.
     * <p>
     * <b>Safety Check:</b> Re-validates state before starting to handle
     * race conditions from concurrent state changes.
     */
    @Override
    public void start() {
        if (checkEntityState()) super.start();
    } // start

    /**
     * Halts goal execution when conditions no longer met.
     * <p>
     * <b>State Validation:</b> Ensures clean shutdown even if state changed
     * during execution. Prevents navigation artifacts from lingering.
     */
    @Override
    public void stop() {
        if (checkEntityState()) super.stop();
    } // stop

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
    } // tick

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
    } // checkEntityState

} // Class: AiFollowOwnerGoal
