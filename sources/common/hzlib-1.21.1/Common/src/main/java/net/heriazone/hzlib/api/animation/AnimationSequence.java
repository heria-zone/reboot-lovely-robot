package net.heriazone.hzlib.api.animation;

import net.minecraft.world.entity.LivingEntity;

import java.util.*;
import java.util.function.Predicate;

/**
 * A multi-phase animation chain with conditional loop steps.
 * <p>
 * <b>Architecture:</b> Registered as a named special in {@link AnimationProfile}.
 * When triggered, the entity's sequence controller runs through the steps in order.
 * Each step loops according to its {@link LoopBehavior} until the advancement
 * condition is met, then the controller moves to the next step.
 * <p>
 * <b>Pull model:</b> {@code LOOP_UNTIL_SIGNAL} steps carry a
 * {@code Predicate<LivingEntity>} evaluated each tick by the sequence controller.
 * The AI goal does not need to know about animation state — it performs its normal
 * behavior and the animation system observes world state independently.
 * <p>
 * <b>Per-entity state:</b> The sequence definition is stateless. Runtime state
 * (current step index, tick counter) lives in {@link SequenceState} on the entity.
 * <p>
 * <b>Example — Dragon's Fury:</b>
 * <pre>{@code
 * AnimationSequence dragonFury = AnimationSequence.builder()
 *     .step("attack_prepare",  LoopBehavior.PLAY_ONCE)
 *     .step("attack_charge",   40)  // LOOP_TIMED, 2 seconds
 *     .step("attack_approach", entity -> entity.getTarget() != null
 *                                     && entity.distanceTo(entity.getTarget()) <= 3.0)
 *     .step("attack_strike",   LoopBehavior.PLAY_ONCE)
 *     .step("attack_fury",     LoopBehavior.PLAY_ONCE)
 *     .returnTo("idle")
 *     .build();
 * }</pre>
 */
public final class AnimationSequence implements ISpecialAnimation {

    // -- Fields --

    private final List<SequenceStep> steps;
    private final String returnToState;

    // -- Constructor --

    private AnimationSequence(Builder builder) {
        this.steps        = Collections.unmodifiableList(new ArrayList<>(builder.steps));
        this.returnToState = builder.returnToState;

        if (steps.isEmpty()) {
            throw new IllegalArgumentException("AnimationSequence must have at least one step");
        }
    } // Constructor: AnimationSequence ()

    // -- Accessors --

    /**
     * Returns the ordered list of steps in this sequence.
     *
     * @return immutable list of steps
     */
    public List<SequenceStep> getSteps() {
        return steps;
    } // getSteps ()

    /**
     * Returns the step at the given index.
     *
     * @param index step index (0-based)
     * @return step at that index
     * @throws IndexOutOfBoundsException if index is out of range
     */
    public SequenceStep getStep(int index) {
        return steps.get(index);
    } // getStep ()

    /**
     * Returns the total number of steps in this sequence.
     *
     * @return step count
     */
    public int getStepCount() {
        return steps.size();
    } // getStepCount ()

    /**
     * Returns the locomotion state to resume after the sequence completes.
     * Corresponds to a slot name in {@link AnimationProfile} (e.g., {@code "idle"}).
     *
     * @return return state name
     */
    public String getReturnToState() {
        return returnToState;
    } // getReturnToState ()

    /**
     * Returns whether the given step index is the last step in the sequence.
     *
     * @param stepIndex current step index
     * @return true if this is the final step
     */
    public boolean isLastStep(int stepIndex) {
        return stepIndex >= steps.size() - 1;
    } // isLastStep ()

    // -- Builder --

    /**
     * Creates a new builder for constructing an {@link AnimationSequence}.
     *
     * @return new builder instance
     */
    public static Builder builder() {
        return new Builder();
    } // builder ()

    /**
     * Fluent builder for {@link AnimationSequence}.
     */
    public static final class Builder {

        private final List<SequenceStep> steps = new ArrayList<>();
        private String returnToState = "idle";

        private Builder() {} // Constructor: Builder ()

        /**
         * Adds a {@code PLAY_ONCE} step.
         *
         * @param animationName animation name
         * @return this builder
         */
        public Builder step(String animationName) {
            steps.add(new SequenceStep(animationName, LoopBehavior.PLAY_ONCE));
            return this;
        } // step ()

        /**
         * Adds a step with the specified loop behavior.
         *
         * @param animationName animation name
         * @param loopBehavior  advancement rule
         * @return this builder
         */
        public Builder step(String animationName, LoopBehavior loopBehavior) {
            steps.add(new SequenceStep(animationName, loopBehavior));
            return this;
        } // step ()

        /**
         * Adds a {@code LOOP_TIMED} step.
         *
         * @param animationName    animation name
         * @param loopDurationTicks ticks to loop before advancing
         * @return this builder
         */
        public Builder step(String animationName, int loopDurationTicks) {
            steps.add(new SequenceStep(animationName, loopDurationTicks));
            return this;
        } // step ()

        /**
         * Adds a {@code LOOP_UNTIL_SIGNAL} step with a pull-model exit condition.
         * <p>
         * The predicate is evaluated each tick while this step is active.
         * Keep it lightweight — distance checks and null checks only.
         *
         * @param animationName animation name
         * @param exitCondition predicate; sequence advances when this returns {@code true}
         * @return this builder
         */
        public Builder step(String animationName, Predicate<LivingEntity> exitCondition) {
            steps.add(new SequenceStep(animationName, exitCondition));
            return this;
        } // step ()

        /**
         * Adds a fully specified step.
         *
         * @param step pre-built sequence step
         * @return this builder
         */
        public Builder step(SequenceStep step) {
            steps.add(Objects.requireNonNull(step, "Step cannot be null"));
            return this;
        } // step ()

        /**
         * Sets the locomotion state to resume after the sequence completes.
         * Defaults to {@code "idle"} if not specified.
         *
         * @param stateName locomotion state name (must match a slot in the entity's profile)
         * @return this builder
         */
        public Builder returnTo(String stateName) {
            this.returnToState = Objects.requireNonNull(stateName, "Return state cannot be null");
            return this;
        } // returnTo ()

        /**
         * Builds the animation sequence.
         *
         * @return new {@link AnimationSequence} instance
         * @throws IllegalArgumentException if no steps have been added
         */
        public AnimationSequence build() {
            return new AnimationSequence(this);
        } // build ()

    } // Class: Builder

    // -- Object Overrides --

    @Override
    public String toString() {
        return "AnimationSequence{steps=" + steps.size() + ", returnTo='" + returnToState + "'}";
    } // toString ()

} // Class: AnimationSequence
