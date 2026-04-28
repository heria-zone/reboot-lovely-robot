package net.heriazone.hzlib.api.animation;

/**
 * Defines how an {@link AnimationPool} selects the next animation to play.
 * <p>
 * <b>Architecture:</b> Strategy is declared on the pool, not on individual animations.
 * All entries in a pool share the same selection strategy.
 * <p>
 * <b>State requirements:</b> {@code RANDOM} and {@code WEIGHTED_RANDOM} are stateless —
 * they require no per-entity tracking. {@code SEQUENTIAL} requires a per-entity index
 * counter (managed by the entity's {@link SequenceState} or equivalent).
 */
public enum SelectionStrategy {

    /**
     * Picks uniformly at random from all entries in the pool.
     * Stateless — no per-entity tracking required.
     * Good for idle variety where all alternatives are equally desirable.
     */
    RANDOM,

    /**
     * Picks randomly with probability proportional to each entry's weight.
     * Stateless — no per-entity tracking required.
     * <p>
     * <b>Example:</b> A pool with entries weighted 80 and 20 will play the first
     * animation 80% of the time and the second 20% of the time.
     * Good for personality moments that should be rare but not absent.
     */
    WEIGHTED_RANDOM,

    /**
     * Cycles through entries in declaration order, advancing on each state entry.
     * Requires a per-entity index counter.
     * <p>
     * <b>Use case:</b> A walk cycle with two alternating steps, or a rest sequence
     * that cycles through three different idle poses in order.
     * <p>
     * <b>Note:</b> The index counter is managed externally (by the entity or the
     * animation controller). The pool itself is stateless.
     */
    SEQUENTIAL

} // Enum: SelectionStrategy
