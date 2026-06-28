package net.heriazone.hzlib.api.entity.features;

import org.jetbrains.annotations.Nullable;

/**
 * Immutable pair of an {@link AppearanceCondition} and a {@link WeightedAppearancePool}.
 * <p>
 * <b>Architecture:</b> A rule is the atom of the appearance system — one condition
 * gate, one outcome. {@link ConditionalAppearanceFeature} holds an ordered list of
 * rules and returns the first match.
 * <p>
 * Rules are created via the static factories rather than a builder — they carry
 * only two fields and fluency adds no clarity at this granularity.
 */
public final class AppearanceRule {

    // -- Fields --

    private final AppearanceCondition    condition;
    private final WeightedAppearancePool pool;

    // -- Constructor --

    private AppearanceRule(AppearanceCondition condition, WeightedAppearancePool pool) {
        this.condition = condition;
        this.pool      = pool;
    } // Constructor: AppearanceRule ()

    // -- Factories --

    /**
     * Creates a rule with a single deterministic outcome.
     *
     * @param condition  the gate condition
     * @param variantKey the variant key to return when the condition passes
     */
    public static AppearanceRule of(AppearanceCondition condition, String variantKey) {
        return new AppearanceRule(condition, WeightedAppearancePool.of(variantKey, 1f));
    } // of ()

    /**
     * Creates a rule with a weighted random outcome.
     * <p>
     * The pool is evaluated only if the condition passes — the random roll is
     * not wasted on failing rules.
     *
     * @param condition the gate condition
     * @param pool      weighted pool of candidate variant keys
     */
    public static AppearanceRule of(AppearanceCondition condition, WeightedAppearancePool pool) {
        return new AppearanceRule(condition, pool);
    } // of ()

    // -- Evaluation --

    /**
     * Evaluates this rule against the given context.
     *
     * @param ctx world snapshot at the moment of appearance selection
     * @return the selected variant key if the condition passes, {@code null} otherwise
     */
    @Nullable
    public String evaluate(AppearanceContext ctx) {
        return condition.test(ctx) ? pool.pick() : null;
    } // evaluate ()

} // Class: AppearanceRule
