package net.heriazone.hzlib.api.animation;

/**
 * Defines how an animation behaves when it completes or when it should advance.
 * <p>
 * <b>Architecture:</b> Used by both {@link WeightedAnimation} (for pool entries) and
 * {@link SequenceStep} (for sequence steps). The GeckoLib-specific behavior for each
 * value is applied in loader-specific {@code InternalAnimation} classes — this enum
 * carries no GeckoLib dependency.
 * <p>
 * <b>Design Decision:</b> {@code INTERRUPT} is a first-class value rather than a flag
 * because it changes the fundamental controller behavior (overrides all other controllers),
 * not just the loop type. The robot attack animation requires this.
 * <p>
 * <b>Sequence-only values:</b> {@code LOOP_TIMED} and {@code LOOP_UNTIL_SIGNAL} are
 * meaningful only inside an {@link AnimationSequence}. Using them in a standalone
 * {@link AnimationPool} is a configuration error — the pool has no mechanism to
 * advance the step.
 */
public enum LoopBehavior {

    /**
     * Animation loops indefinitely until the entity changes state.
     * Standard behavior for locomotion animations (idle, walk, rest, sit, ride).
     */
    LOOP,

    /**
     * Animation plays once, then the controller stops.
     * Standard behavior for one-shot reactions (hurt, interaction specials).
     */
    PLAY_ONCE,

    /**
     * Animation plays once, then freezes on the last frame.
     * Useful for hold poses that persist until explicitly cleared.
     */
    HOLD_LAST_FRAME,

    /**
     * Animation plays once and overrides all other GeckoLib controllers.
     * <p>
     * <b>GeckoLib mapping:</b> {@code override_previous_animation: true}.
     * Required for attack animations that must interrupt locomotion mid-swing.
     * The robot attack animation uses this value.
     */
    INTERRUPT,

    /**
     * Animation loops for a fixed number of ticks, then the sequence advances.
     * <p>
     * <b>Sequence-only.</b> The tick count is defined in {@link SequenceStep#loopDurationTicks()}.
     * Using this in a standalone {@link AnimationPool} has no effect.
     */
    LOOP_TIMED,

    /**
     * Animation loops until a {@code Predicate<LivingEntity>} returns {@code true}.
     * <p>
     * <b>Sequence-only.</b> The predicate is defined in {@link SequenceStep#exitCondition()}.
     * Evaluated every tick by the sequence controller. Predicates must be lightweight
     * (distance checks, null checks) — avoid complex world queries.
     * <p>
     * <b>Pull model:</b> The AI goal does not signal the animation system. The animation
     * system observes world state independently via the predicate.
     */
    LOOP_UNTIL_SIGNAL

} // Enum: LoopBehavior
