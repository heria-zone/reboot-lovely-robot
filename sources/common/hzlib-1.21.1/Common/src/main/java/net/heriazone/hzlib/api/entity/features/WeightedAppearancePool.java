package net.heriazone.hzlib.api.entity.features;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Weighted random selection of an appearance variant key.
 * <p>
 * <b>Use case:</b> When one {@link AppearanceRule} condition matches but the outcome
 * should vary — e.g., Wisp spawning in plains biome has a 60/40 chance of blue vs.
 * yellow. Without this, each probability split would need a separate rule with
 * a {@link AppearanceConditions#chance(float)} condition.
 * <p>
 * <b>Normalisation:</b> Weights are normalised at pick time — absolute values do not
 * matter, only their ratios. {@code of("a", 2f).add("b", 1f)} gives the same
 * distribution as {@code of("a", 0.67f).add("b", 0.33f)}.
 * <p>
 * Build via the fluent factory:
 * <pre>{@code
 * WeightedAppearancePool.of("wisp_blue", 0.6f).add("wisp_yellow", 0.4f)
 * }</pre>
 */
public final class WeightedAppearancePool {

    // -- Fields --

    private final List<String> keys;
    private final List<Float>  weights;

    // -- Constructor --

    private WeightedAppearancePool(List<String> keys, List<Float> weights) {
        this.keys    = new ArrayList<>(keys);
        this.weights = new ArrayList<>(weights);
    } // Constructor: WeightedAppearancePool ()

    // -- Factory --

    /**
     * Creates a pool with a single entry. Chain {@link #add} calls to add more.
     *
     * @param variantKey the variant key for this entry
     * @param weight     relative weight; must be positive
     */
    public static WeightedAppearancePool of(String variantKey, float weight) {
        WeightedAppearancePool pool = new WeightedAppearancePool(new ArrayList<>(), new ArrayList<>());
        return pool.add(variantKey, weight);
    } // of ()

    // -- Builder --

    /**
     * Adds an entry to this pool and returns {@code this} for chaining.
     *
     * @param variantKey the variant key for this entry
     * @param weight     relative weight; must be positive
     * @return this pool (fluent)
     */
    public WeightedAppearancePool add(String variantKey, float weight) {
        if (variantKey != null && weight > 0f) {
            keys.add(variantKey);
            weights.add(weight);
        }
        return this;
    } // add ()

    // -- Selection --

    /**
     * Picks one variant key by normalised weighted random.
     * <p>
     * Never returns null when the pool has at least one entry. Returns {@code null}
     * only for an empty pool — callers should guard via {@link AppearanceRule} which
     * always constructs with at least one entry.
     *
     * @return selected variant key, or {@code null} if pool is empty
     */
    public String pick() {
        if (keys.isEmpty()) return null;
        if (keys.size() == 1) return keys.get(0);

        float total = 0f;
        for (float w : weights) total += w;

        float roll = ThreadLocalRandom.current().nextFloat() * total;
        float cumulative = 0f;
        for (int i = 0; i < keys.size(); i++) {
            cumulative += weights.get(i);
            if (roll < cumulative) return keys.get(i);
        }
        return keys.get(keys.size() - 1); // floating-point guard
    } // pick ()

} // Class: WeightedAppearancePool
