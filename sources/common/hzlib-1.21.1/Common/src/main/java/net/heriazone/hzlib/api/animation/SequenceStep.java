package net.heriazone.hzlib.api.animation;

import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * A single step within an {@link AnimationSequence}.
 * <p>
 * <b>Architecture:</b> Each step declares an animation name and how the sequence
 * advances past it. The sequence controller evaluates the step's advancement
 * condition each tick while the step is active.
 * <p>
 * <b>Advancement rules by {@link LoopBehavior}:</b>
 * <ul>
 *   <li>{@code PLAY_ONCE} — advances when GeckoLib signals animation completion</li>
 *   <li>{@code LOOP_TIMED} — advances after {@link #loopDurationTicks()} ticks</li>
 *   <li>{@code LOOP_UNTIL_SIGNAL} — advances when {@link #exitCondition()} returns {@code true}</li>
 *   <li>{@code LOOP} — loops indefinitely (use only if the sequence has external termination)</li>
 * </ul>
 * <p>
 * <b>Minecraft dependency:</b> {@code Predicate<LivingEntity>} requires Minecraft types.
 * This is acceptable — HZLib Common already imports Minecraft classes extensively.
 */
public final class SequenceStep {

    // -- Fields --

    private final String animationName;
    private final LoopBehavior loopBehavior;
    private final int loopDurationTicks;
    private final Predicate<LivingEntity> exitCondition;

    // -- Constructors --

    /**
     * Creates a {@code PLAY_ONCE} step.
     *
     * @param animationName animation name as it appears in the animation JSON file
     */
    public SequenceStep(String animationName) {
        this(animationName, LoopBehavior.PLAY_ONCE, 0, null);
    } // Constructor: SequenceStep ()

    /**
     * Creates a step with the specified loop behavior.
     * Use for {@code PLAY_ONCE}, {@code HOLD_LAST_FRAME}, or {@code INTERRUPT} steps.
     *
     * @param animationName animation name
     * @param loopBehavior  how this step advances
     */
    public SequenceStep(String animationName, LoopBehavior loopBehavior) {
        this(animationName, loopBehavior, 0, null);
    } // Constructor: SequenceStep ()

    /**
     * Creates a {@code LOOP_TIMED} step.
     *
     * @param animationName    animation name
     * @param loopDurationTicks number of ticks to loop before advancing
     */
    public SequenceStep(String animationName, int loopDurationTicks) {
        this(animationName, LoopBehavior.LOOP_TIMED, loopDurationTicks, null);
    } // Constructor: SequenceStep ()

    /**
     * Creates a {@code LOOP_UNTIL_SIGNAL} step.
     *
     * @param animationName animation name
     * @param exitCondition predicate evaluated each tick; advances when {@code true}
     */
    public SequenceStep(String animationName, Predicate<LivingEntity> exitCondition) {
        this(animationName, LoopBehavior.LOOP_UNTIL_SIGNAL, 0, exitCondition);
    } // Constructor: SequenceStep ()

    /**
     * Full constructor.
     *
     * @param animationName    animation name
     * @param loopBehavior     advancement rule
     * @param loopDurationTicks ticks to loop (used only for {@code LOOP_TIMED})
     * @param exitCondition    predicate (used only for {@code LOOP_UNTIL_SIGNAL})
     * @throws NullPointerException     if animationName or loopBehavior is null
     * @throws IllegalArgumentException if animationName is blank, or loopDurationTicks is negative
     */
    public SequenceStep(String animationName, LoopBehavior loopBehavior,
                        int loopDurationTicks, Predicate<LivingEntity> exitCondition) {
        this.animationName    = Objects.requireNonNull(animationName, "Animation name cannot be null");
        this.loopBehavior     = Objects.requireNonNull(loopBehavior, "LoopBehavior cannot be null");
        this.loopDurationTicks = loopDurationTicks;
        this.exitCondition    = exitCondition;

        if (animationName.isBlank()) {
            throw new IllegalArgumentException("Animation name cannot be blank");
        }
        if (loopDurationTicks < 0) {
            throw new IllegalArgumentException("loopDurationTicks cannot be negative: " + loopDurationTicks);
        }
    } // Constructor: SequenceStep ()

    // -- Accessors --

    /**
     * Returns the animation name as it appears in the animation JSON file.
     *
     * @return animation name
     */
    public String animationName() { return animationName; } // animationName ()

    /**
     * Returns the advancement rule for this step.
     *
     * @return loop behavior
     */
    public LoopBehavior loopBehavior() { return loopBehavior; } // loopBehavior ()

    /**
     * Returns the number of ticks to loop before advancing.
     * Only meaningful when {@link #loopBehavior()} is {@code LOOP_TIMED}.
     *
     * @return loop duration in ticks (0 if not applicable)
     */
    public int loopDurationTicks() { return loopDurationTicks; } // loopDurationTicks ()

    /**
     * Returns the exit condition predicate.
     * Only meaningful when {@link #loopBehavior()} is {@code LOOP_UNTIL_SIGNAL}.
     * <p>
     * <b>Performance note:</b> This predicate is evaluated every tick while the step
     * is active. Keep it lightweight — distance checks and null checks only.
     *
     * @return exit condition predicate, or {@code null} if not applicable
     */
    public Predicate<LivingEntity> exitCondition() { return exitCondition; } // exitCondition ()

    /**
     * Evaluates whether this step should advance given the current entity state.
     * Returns {@code false} for step types that advance via external signals
     * (e.g., {@code PLAY_ONCE} advances when GeckoLib signals completion).
     *
     * @param entity      the entity running the sequence
     * @param ticksInStep number of ticks spent in this step so far
     * @return true if the sequence should advance to the next step
     */
    public boolean shouldAdvance(LivingEntity entity, int ticksInStep) {
        return switch (loopBehavior) {
            case LOOP_TIMED         -> ticksInStep >= loopDurationTicks;
            case LOOP_UNTIL_SIGNAL  -> exitCondition != null && exitCondition.test(entity);
            default                 -> false; // PLAY_ONCE advances via GeckoLib callback
        };
    } // shouldAdvance ()

    // -- Object Overrides --

    @Override
    public String toString() {
        return "SequenceStep{name='" + animationName + "', loop=" + loopBehavior
                + (loopBehavior == LoopBehavior.LOOP_TIMED ? ", ticks=" + loopDurationTicks : "")
                + '}';
    } // toString ()

} // Class: SequenceStep
