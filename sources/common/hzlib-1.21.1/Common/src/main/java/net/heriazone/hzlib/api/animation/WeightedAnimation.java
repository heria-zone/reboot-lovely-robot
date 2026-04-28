package net.heriazone.hzlib.api.animation;

import java.util.Objects;

/**
 * A single animation entry within an {@link AnimationPool}.
 * <p>
 * <b>Architecture:</b> Pure data record — no Minecraft or GeckoLib dependency.
 * The {@code name} is the animation name as it appears in the {@code .animation.json}
 * file. The loader-specific {@code InternalAnimation} class turns this name into a
 * GeckoLib {@code RawAnimation}.
 * <p>
 * <b>Weight:</b> Used only when the pool's {@link SelectionStrategy} is
 * {@code WEIGHTED_RANDOM}. Ignored for {@code RANDOM} and {@code SEQUENTIAL}.
 * Higher weight = more frequent selection. A weight of 0 effectively disables
 * the entry for weighted selection.
 */
public final class WeightedAnimation {

    // -- Fields --

    private final String name;
    private final int weight;
    private final LoopBehavior loop;

    // -- Constructor --

    /**
     * Creates a weighted animation entry.
     *
     * @param name   animation name as it appears in the animation JSON file
     * @param weight relative weight for {@code WEIGHTED_RANDOM} selection (ignored otherwise)
     * @param loop   how this animation behaves when it completes
     * @throws NullPointerException     if name or loop is null
     * @throws IllegalArgumentException if name is blank or weight is negative
     */
    public WeightedAnimation(String name, int weight, LoopBehavior loop) {
        this.name = Objects.requireNonNull(name, "Animation name cannot be null");
        this.loop = Objects.requireNonNull(loop, "LoopBehavior cannot be null");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Animation name cannot be blank");
        }
        if (weight < 0) {
            throw new IllegalArgumentException("Weight cannot be negative: " + weight);
        }

        this.weight = weight;
    } // Constructor: WeightedAnimation ()

    /**
     * Creates a weighted animation entry with default weight of 1.
     *
     * @param name animation name as it appears in the animation JSON file
     * @param loop how this animation behaves when it completes
     */
    public WeightedAnimation(String name, LoopBehavior loop) {
        this(name, 1, loop);
    } // Constructor: WeightedAnimation ()

    /**
     * Creates a looping animation entry with default weight of 1.
     * Convenience constructor for the most common case.
     *
     * @param name animation name as it appears in the animation JSON file
     */
    public WeightedAnimation(String name) {
        this(name, 1, LoopBehavior.LOOP);
    } // Constructor: WeightedAnimation ()

    // -- Accessors --

    /**
     * Returns the animation name as it appears in the animation JSON file.
     *
     * @return animation name string
     */
    public String getName() {
        return name;
    } // getName ()

    /**
     * Returns the relative weight for weighted random selection.
     * Ignored when the pool uses {@code RANDOM} or {@code SEQUENTIAL} strategy.
     *
     * @return weight (always &gt;= 0)
     */
    public int getWeight() {
        return weight;
    } // getWeight ()

    /**
     * Returns how this animation behaves when it completes.
     *
     * @return loop behavior
     */
    public LoopBehavior getLoop() {
        return loop;
    } // getLoop ()

    // -- Object Overrides --

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        WeightedAnimation that = (WeightedAnimation) obj;
        return weight == that.weight
                && Objects.equals(name, that.name)
                && loop == that.loop;
    } // equals ()

    @Override
    public int hashCode() {
        return Objects.hash(name, weight, loop);
    } // hashCode ()

    @Override
    public String toString() {
        return "WeightedAnimation{name='" + name + "', weight=" + weight + ", loop=" + loop + '}';
    } // toString ()

} // Class: WeightedAnimation
