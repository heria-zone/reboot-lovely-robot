package net.heriazone.hzlib.api.animation;

import java.util.Objects;

/**
 * Declares one conditional idle animation tier within an {@link AnimationProfile}.
 * <p>
 * <b>Architecture:</b> {@link AnimationProfile} holds an ordered list of
 * {@code IdleSlot} instances. {@code AnimationStateManager.resolveIdleSlot()}
 * sorts them by priority (descending) and returns the pool name from the first slot
 * whose condition passes and whose activation threshold has been met.
 * <p>
 * <b>Replaces {@code handleStandbyAnimation()}:</b> The server-tick timer logic
 * that previously mutated {@code standbyTicks} and {@code standbyTargetTicks} in
 * {@code RobotEntity} is replaced by the threshold comparison
 * {@code entity.getIdleStationaryTicks() >= activationThresholdTicks}. The comparison
 * is stable once crossed — the same slot wins on every controller tick until the entity
 * moves — eliminating the frame-to-frame flicker caused by the two-clock desync.
 * <p>
 * <b>Slot evaluation order:</b>
 * <ol>
 *   <li>Slots are sorted by {@link #getPriority()} descending — higher priority
 *       evaluated first.</li>
 *   <li>The first slot whose {@link #getCondition()} passes AND whose
 *       {@link #getActivationThresholdTicks()} is met wins.</li>
 *   <li>If no slot matches, the profile's {@code idle} pool is used as fallback.</li>
 * </ol>
 * <p>
 * <b>Usage in {@code ROBOT_BASE_PROFILE}:</b>
 * <pre>{@code
 * AnimationProfile.builder()
 *     .idle("idle")   // priority-0 always-true fallback
 *     .idleSlot(AnimationPool.single("rest"),
 *               entity -> entity.getCurrentState() == EntityState.Standby,
 *               1, 0)
 *     .idleSlot(AnimationPool.single("sit"),
 *               entity -> entity.getCurrentState() == EntityState.Standby,
 *               2, SharedConfigs.Common.StandbyToSitDelayMin)
 *     ...
 * }</pre>
 */
public final class IdleSlot {

    // -- Fields --

    private final AnimationPool pool;
    private final IdleCondition  condition;

    /**
     * Evaluation priority — higher values are evaluated first.
     * Use {@code 0} for the always-true fallback slot.
     */
    private final int priority;

    /**
     * Minimum value of {@code idleStationaryTicks} the entity must reach before
     * this slot activates. {@code 0} activates immediately when the condition is met.
     */
    private final int activationThresholdTicks;

    // -- Constructor --

    /**
     * Creates an idle slot with the given pool, condition, priority, and threshold.
     *
     * @param pool                     animation pool to play when this slot wins
     * @param condition                condition that must pass for this slot to be eligible
     * @param priority                 evaluation order — higher = evaluated first
     * @param activationThresholdTicks ticks the entity must be stationary before activating;
     *                                 {@code 0} activates immediately
     * @throws NullPointerException if pool or condition is null
     */
    public IdleSlot(AnimationPool pool, IdleCondition condition,
                    int priority, int activationThresholdTicks) {
        this.pool                    = Objects.requireNonNull(pool,      "IdleSlot pool must not be null");
        this.condition               = Objects.requireNonNull(condition, "IdleSlot condition must not be null");
        this.priority                = priority;
        this.activationThresholdTicks = Math.max(0, activationThresholdTicks);
    } // Constructor: IdleSlot ()

    // -- Accessors --

    /**
     * Returns the animation pool to play when this slot is selected.
     *
     * @return pool; never {@code null}
     */
    public AnimationPool getPool() {
        return pool;
    } // getPool ()

    /**
     * Returns the condition that must be satisfied for this slot to be eligible.
     *
     * @return condition; never {@code null}
     */
    public IdleCondition getCondition() {
        return condition;
    } // getCondition ()

    /**
     * Returns the evaluation priority. Higher values are evaluated first by
     * {@code AnimationStateManager.resolveIdleSlot()}.
     *
     * @return priority value
     */
    public int getPriority() {
        return priority;
    } // getPriority ()

    /**
     * Returns the minimum {@code idleStationaryTicks} value the entity must have
     * accumulated before this slot can activate.
     * <p>
     * {@code 0} means the slot activates immediately once its condition passes.
     *
     * @return activation threshold in ticks; never negative
     */
    public int getActivationThresholdTicks() {
        return activationThresholdTicks;
    } // getActivationThresholdTicks ()

} // Class: IdleSlot
