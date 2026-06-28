package net.heriazone.hzlib.api.entity.features;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Unified appearance selection for spawn-time and interaction-time contexts.
 * <p>
 * <b>Architecture:</b> Replaces {@code BiomeAppearanceFeature} and the hardcoded
 * dye-item chain in LovelyLib. One feature handles all appearance input types —
 * biome, held item, dimension, spawn reason, weather — by routing them through
 * composable {@link AppearanceCondition} predicates over a shared
 * {@link AppearanceContext}.
 * <p>
 * <b>Evaluation:</b> Rules are evaluated in declaration order; the first match wins.
 * Declare most-specific rules first, {@link Builder#withDefault} last. {@code resolve()}
 * returns the {@code defaultVariantKey} (which may be null) when no rule matches.
 * <p>
 * <b>Usage — biome-driven spawn variant:</b>
 * <pre>{@code
 * ConditionalAppearanceFeature.builder()
 *     .when(AppearanceConditions.inBiome(Biomes.TAIGA, Biomes.SNOWY_TAIGA), "mushroom_brown_ruby")
 *     .when(AppearanceConditions.inBiome(Biomes.DARK_FOREST),               "mushroom_brown_scarlatina")
 *     .withDefault("mushroom_brown_boletus")
 *     .build()
 * }</pre>
 * <p>
 * <b>Usage — dye interaction:</b>
 * <pre>{@code
 * ConditionalAppearanceFeature.builder()
 *     .when(AppearanceConditions.heldItem(Items.WHITE_DYE),  "bunny_white")
 *     .when(AppearanceConditions.heldItem(Items.RED_DYE),    "bunny_red")
 *     .build()
 * }</pre>
 * <p>
 * <b>Usage — weighted random within a condition:</b>
 * <pre>{@code
 * ConditionalAppearanceFeature.builder()
 *     .when(AppearanceConditions.inBiome(Biomes.PLAINS),
 *           WeightedAppearancePool.of("wisp_blue", 0.6f).add("wisp_yellow", 0.4f))
 *     .build()
 * }</pre>
 */
public final class ConditionalAppearanceFeature {

    // -- Fields --

    private final List<AppearanceRule> rules;
    @Nullable private final String     defaultVariantKey;

    // -- Constructor --

    private ConditionalAppearanceFeature(List<AppearanceRule> rules, @Nullable String defaultVariantKey) {
        this.rules             = rules;
        this.defaultVariantKey = defaultVariantKey;
    } // Constructor: ConditionalAppearanceFeature ()

    // -- Resolution --

    /**
     * Resolves the appearance variant key for the given context.
     * <p>
     * Evaluates rules in declaration order and returns the first non-null result.
     * Falls back to {@code defaultVariantKey} — which may be null — when no rule matches.
     *
     * @param ctx world snapshot from either spawn or interaction
     * @return variant key, or {@code null} if no rule matched and no default was set
     */
    @Nullable
    public String resolve(AppearanceContext ctx) {
        for (AppearanceRule rule : rules) {
            String key = rule.evaluate(ctx);
            if (key != null) return key;
        }
        return defaultVariantKey;
    } // resolve ()

    // -- Builder --

    /** Returns a new builder for composing a {@link ConditionalAppearanceFeature}. */
    public static Builder builder() {
        return new Builder();
    } // builder ()

    /**
     * Fluent builder for {@link ConditionalAppearanceFeature}.
     * <p>
     * <b>Rule order matters.</b> Rules are evaluated in the order they are added.
     * Declare more-specific conditions before less-specific ones.
     */
    public static final class Builder {

        private final List<AppearanceRule> rules = new ArrayList<>();
        @Nullable private String defaultVariantKey = null;

        private Builder() {} // Constructor: Builder ()

        /**
         * Adds a rule with a single deterministic outcome.
         *
         * @param condition  the gate condition
         * @param variantKey the variant key to return when the condition passes
         * @return this builder for chaining
         */
        public Builder when(AppearanceCondition condition, String variantKey) {
            rules.add(AppearanceRule.of(condition, variantKey));
            return this;
        } // when ()

        /**
         * Adds a rule with a weighted random outcome.
         *
         * @param condition the gate condition
         * @param pool      weighted pool of candidate variant keys
         * @return this builder for chaining
         */
        public Builder when(AppearanceCondition condition, WeightedAppearancePool pool) {
            rules.add(AppearanceRule.of(condition, pool));
            return this;
        } // when ()

        /**
         * Sets the fallback variant key returned when no rule matches.
         * <p>
         * Optional — omit when no fallback is needed and callers handle a null result.
         *
         * @param variantKey fallback variant key
         * @return this builder for chaining
         */
        public Builder withDefault(String variantKey) {
            this.defaultVariantKey = variantKey;
            return this;
        } // withDefault ()

        /** Builds the immutable {@link ConditionalAppearanceFeature}. */
        public ConditionalAppearanceFeature build() {
            return new ConditionalAppearanceFeature(
                    Collections.unmodifiableList(new ArrayList<>(rules)),
                    defaultVariantKey
            );
        } // build ()

    } // Class: Builder

} // Class: ConditionalAppearanceFeature
