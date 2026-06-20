package net.heriazone.hzlib.api.animation;

import org.jetbrains.annotations.Nullable;

/**
 * Per-entity runtime state for an active {@link AnimationSequence}.
 * <p>
 * <b>Architecture:</b> The sequence definition ({@link AnimationSequence}) is stateless
 * and shared. This object holds the mutable runtime state for one entity instance
 * running that sequence. It lives on the entity (in {@code RobotEntity} or
 * {@code NativeEntity}) as a nullable field.
 * <p>
 * <b>Lifecycle:</b>
 * <ul>
 *   <li>Created when a sequence is triggered via {@code startSequence()}</li>
 *   <li>Updated each tick by the animation controller</li>
 *   <li>Cleared (set to null) when the sequence completes or is interrupted</li>
 * </ul>
 * <p>
 * <b>No Minecraft dependency:</b> Pure Java data object.
 */
public final class SequenceState {

    // -- Fields --

    private final String sequenceName;
    private final AnimationSequence sequence;
    private int currentStepIndex;
    private int stepTickCounter;
    private boolean stepAnimationComplete;

    // -- Constructor --

    /**
     * Creates a new sequence state at step 0.
     *
     * @param sequenceName name of the sequence (for debugging and logging)
     * @param sequence     the sequence definition being run
     */
    public SequenceState(String sequenceName, AnimationSequence sequence) {
        this.sequenceName          = sequenceName;
        this.sequence              = sequence;
        this.currentStepIndex      = 0;
        this.stepTickCounter       = 0;
        this.stepAnimationComplete = false;
    } // Constructor: SequenceState ()

    // -- Accessors --

    /**
     * Returns the name of the active sequence.
     *
     * @return sequence name
     */
    public String getSequenceName() { return sequenceName; } // getSequenceName ()

    /**
     * Returns the sequence definition.
     *
     * @return sequence
     */
    public AnimationSequence getSequence() { return sequence; } // getSequence ()

    /**
     * Returns the current step index (0-based).
     *
     * @return current step index
     */
    public int getCurrentStepIndex() { return currentStepIndex; } // getCurrentStepIndex ()

    /**
     * Returns the current step definition.
     *
     * @return current step
     */
    public SequenceStep getCurrentStep() {
        return sequence.getStep(currentStepIndex);
    } // getCurrentStep ()

    /**
     * Returns the number of ticks spent in the current step.
     *
     * @return tick counter for current step
     */
    public int getStepTickCounter() { return stepTickCounter; } // getStepTickCounter ()

    /**
     * Returns whether the current step's animation has signaled completion.
     * Used for {@code PLAY_ONCE} steps where GeckoLib signals when the animation ends.
     *
     * @return true if the animation has completed
     */
    public boolean isStepAnimationComplete() { return stepAnimationComplete; } // isStepAnimationComplete ()

    // -- Mutation --

    /**
     * Increments the tick counter for the current step.
     * Called once per tick by the animation controller.
     */
    public void tickStep() {
        stepTickCounter++;
    } // tickStep ()

    /**
     * Signals that the current step's animation has completed (for PLAY_ONCE steps).
     * Called by the GeckoLib animation controller when the animation ends.
     */
    public void signalAnimationComplete() {
        stepAnimationComplete = true;
    } // signalAnimationComplete ()

    /**
     * Advances to the next step, resetting per-step counters.
     *
     * @return true if there is a next step, false if the sequence is complete
     */
    public boolean advance() {
        currentStepIndex++;
        stepTickCounter = 0;
        stepAnimationComplete = false;
        return currentStepIndex < sequence.getStepCount();
    } // advance ()

    /**
     * Returns whether the sequence has completed all steps.
     *
     * @return true if all steps have been executed
     */
    public boolean isComplete() {
        return currentStepIndex >= sequence.getStepCount();
    } // isComplete ()

    // -- Object Overrides --

    @Override
    public String toString() {
        return "SequenceState{sequence='" + sequenceName
                + "', step=" + currentStepIndex + "/" + sequence.getStepCount()
                + ", ticks=" + stepTickCounter + '}';
    } // toString ()

} // Class: SequenceState
