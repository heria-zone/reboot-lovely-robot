package net.heriazone.hzlib.api.animation;

import java.util.*;

/**
 * A pool of animations for a single entity state, with configurable selection strategy.
 * <p>
 * <b>Architecture:</b> Pure data class — no Minecraft or GeckoLib dependency.
 * The pool selects an animation name (String); the loader-specific
 * {@code InternalAnimation} turns that name into a GeckoLib {@code RawAnimation}.
 * <p>
 * <b>Single-entry pools:</b> A pool with one entry behaves identically to the old
 * hardcoded single-name system. The {@link AnimationProfile.Builder} shorthand
 * {@code .idle("idle")} creates a single-entry LOOP pool automatically.
 * <p>
 * <b>Empty pools:</b> {@link #selectNext} returns {@code null} for empty pools.
 * The locomotion controller falls back to idle when a slot returns null.
 * <p>
 * <b>State:</b> The pool itself is stateless. {@code SEQUENTIAL} strategy requires
 * an external index counter (managed by the entity or animation controller).
 */
public final class AnimationPool implements ISpecialAnimation {

    // -- Fields --

    private final List<WeightedAnimation> animations;
    private final SelectionStrategy strategy;

    // -- Constructor --

    /**
     * Creates an animation pool with the specified entries and strategy.
     *
     * @param animations list of weighted animation entries (defensive copy taken)
     * @param strategy   selection strategy for choosing among entries
     * @throws NullPointerException if animations or strategy is null
     */
    public AnimationPool(List<WeightedAnimation> animations, SelectionStrategy strategy) {
        Objects.requireNonNull(animations, "Animations list cannot be null");
        Objects.requireNonNull(strategy, "SelectionStrategy cannot be null");
        this.animations = Collections.unmodifiableList(new ArrayList<>(animations));
        this.strategy = strategy;
    } // Constructor: AnimationPool ()

    // -- Factory Methods --

    /**
     * Creates a single-entry looping pool.
     * Convenience factory for the most common case.
     *
     * @param animationName animation name
     * @return pool with one LOOP entry and RANDOM strategy
     */
    public static AnimationPool single(String animationName) {
        return new AnimationPool(
                List.of(new WeightedAnimation(animationName, 1, LoopBehavior.LOOP)),
                SelectionStrategy.RANDOM
        );
    } // single ()

    /**
     * Creates a single-entry pool with the specified loop behavior.
     *
     * @param animationName animation name
     * @param loop          loop behavior for this animation
     * @return pool with one entry and RANDOM strategy
     */
    public static AnimationPool single(String animationName, LoopBehavior loop) {
        return new AnimationPool(
                List.of(new WeightedAnimation(animationName, 1, loop)),
                SelectionStrategy.RANDOM
        );
    } // single ()

    // -- Query Methods --

    /**
     * Returns whether this pool has no entries.
     * The locomotion controller falls back to idle when a slot is empty.
     *
     * @return true if the pool contains no animations
     */
    public boolean isEmpty() {
        return animations.isEmpty();
    } // isEmpty ()

    /**
     * Returns the number of entries in this pool.
     *
     * @return entry count
     */
    public int size() {
        return animations.size();
    } // size ()

    /**
     * Returns the selection strategy for this pool.
     *
     * @return selection strategy
     */
    public SelectionStrategy getStrategy() {
        return strategy;
    } // getStrategy ()

    /**
     * Returns an unmodifiable view of all animation entries.
     *
     * @return immutable list of entries
     */
    public List<WeightedAnimation> getAnimations() {
        return animations;
    } // getAnimations ()

    // -- Selection --

    /**
     * Selects the next animation name from this pool.
     * <p>
     * <b>RANDOM:</b> Picks uniformly at random. Ignores weights.
     * <p>
     * <b>WEIGHTED_RANDOM:</b> Picks proportionally to weight values.
     * Entries with weight 0 are never selected.
     * <p>
     * <b>SEQUENTIAL:</b> Uses {@code sequentialIndex} to pick the entry at that
     * position (modulo pool size). The caller is responsible for incrementing
     * the index between calls.
     * <p>
     * <b>Empty pool:</b> Returns {@code null}. The locomotion controller falls
     * back to idle when null is returned.
     *
     * @param random         random source for RANDOM and WEIGHTED_RANDOM strategies
     * @param sequentialIndex current index for SEQUENTIAL strategy (ignored for others)
     * @return selected animation name, or {@code null} if pool is empty
     */
    public String selectNext(Random random, int sequentialIndex) {
        if (animations.isEmpty()) return null;
        if (animations.size() == 1) return animations.get(0).getName();

        return switch (strategy) {
            case RANDOM -> animations.get(random.nextInt(animations.size())).getName();
            case WEIGHTED_RANDOM -> selectWeighted(random);
            case SEQUENTIAL -> animations.get(Math.abs(sequentialIndex) % animations.size()).getName();
        };
    } // selectNext ()

    /**
     * Selects the next animation name using a default sequential index of 0.
     * Convenience overload for callers that don't use SEQUENTIAL strategy.
     *
     * @param random random source
     * @return selected animation name, or {@code null} if pool is empty
     */
    public String selectNext(Random random) {
        return selectNext(random, 0);
    } // selectNext ()

    /**
     * Returns the loop behavior of the most recently selected animation by name.
     * Used by the loader-specific animation controller to determine GeckoLib behavior.
     *
     * @param animationName animation name to look up
     * @return loop behavior, or {@code LoopBehavior.LOOP} if name not found
     */
    public LoopBehavior getLoopBehavior(String animationName) {
        return animations.stream()
                .filter(a -> a.getName().equals(animationName))
                .map(WeightedAnimation::getLoop)
                .findFirst()
                .orElse(LoopBehavior.LOOP);
    } // getLoopBehavior ()

    // -- Private Helpers --

    /**
     * Weighted random selection using cumulative weight distribution.
     * Entries with weight 0 are excluded from selection.
     *
     * @param random random source
     * @return selected animation name, or first entry name if all weights are 0
     */
    private String selectWeighted(Random random) {
        int totalWeight = animations.stream()
                .mapToInt(WeightedAnimation::getWeight)
                .sum();

        if (totalWeight <= 0) {
            // All weights are 0 — fall back to uniform random
            return animations.get(random.nextInt(animations.size())).getName();
        }

        int roll = random.nextInt(totalWeight);
        int cumulative = 0;

        for (WeightedAnimation animation : animations) {
            cumulative += animation.getWeight();
            if (roll < cumulative) {
                return animation.getName();
            }
        }

        // Should never reach here, but return last entry as safety fallback
        return animations.get(animations.size() - 1).getName();
    } // selectWeighted ()

    // -- Builder --

    /**
     * Creates a builder for constructing animation pools fluently.
     *
     * @return new builder instance
     */
    public static Builder builder() {
        return new Builder();
    } // builder ()

    /**
     * Fluent builder for {@link AnimationPool}.
     */
    public static final class Builder {

        private final List<WeightedAnimation> animations = new ArrayList<>();
        private SelectionStrategy strategy = SelectionStrategy.RANDOM;

        private Builder() {} // Constructor: Builder ()

        /**
         * Adds an animation entry with the specified weight and loop behavior.
         *
         * @param name   animation name
         * @param weight relative weight for WEIGHTED_RANDOM selection
         * @param loop   loop behavior
         * @return this builder
         */
        public Builder add(String name, int weight, LoopBehavior loop) {
            animations.add(new WeightedAnimation(name, weight, loop));
            return this;
        } // add ()

        /**
         * Adds an animation entry with the specified weight and LOOP behavior.
         *
         * @param name   animation name
         * @param weight relative weight for WEIGHTED_RANDOM selection
         * @return this builder
         */
        public Builder add(String name, int weight) {
            return add(name, weight, LoopBehavior.LOOP);
        } // add ()

        /**
         * Adds an animation entry with the specified loop behavior and default weight of 1.
         *
         * @param name animation name
         * @param loop loop behavior
         * @return this builder
         */
        public Builder add(String name, LoopBehavior loop) {
            return add(name, 1, loop);
        } // add ()

        /**
         * Adds a looping animation entry with default weight of 1.
         *
         * @param name animation name
         * @return this builder
         */
        public Builder add(String name) {
            return add(name, 1, LoopBehavior.LOOP);
        } // add ()

        /**
         * Sets the selection strategy for this pool.
         *
         * @param strategy selection strategy
         * @return this builder
         */
        public Builder strategy(SelectionStrategy strategy) {
            this.strategy = Objects.requireNonNull(strategy, "Strategy cannot be null");
            return this;
        } // strategy ()

        /**
         * Builds the animation pool.
         *
         * @return new {@link AnimationPool} instance
         */
        public AnimationPool build() {
            return new AnimationPool(animations, strategy);
        } // build ()

    } // Class: Builder

    // -- Object Overrides --

    @Override
    public String toString() {
        return "AnimationPool{strategy=" + strategy + ", size=" + animations.size() + '}';
    } // toString ()

} // Class: AnimationPool
