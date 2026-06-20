package net.heriazone.hzlib.api.entity.features.exchange;

/**
 * <p>Predicate that gates whether an {@link ExchangeRule} may fire.<p>
 * <p>
 * <b>Architecture:</b> Functional interface so conditions can be expressed as lambdas.
 * Static factory methods on {@link ExchangeConditions} provide the common cases.
 * Conditions compose via {@link #and}, {@link #or}, and {@link #negate}.
 * <p>
 * <b>Evaluation:</b> Called server-side only, immediately before an exchange rule fires.
 * Implementations may safely access world state, biome, time, etc.
 * <p>
 * <b>Example:</b>
 * <pre>{@code
 * ExchangeConditions.inBiome(Biomes.FLOWER_FOREST)
 *     .and(ExchangeConditions.ownerOnly())
 * }</pre>
 */
@FunctionalInterface
public interface ExchangeCondition {

    /**
     * Tests whether this condition is satisfied for the given context.
     *
     * @param ctx the interaction context
     * @return true if the condition passes and the rule may proceed
     */
    boolean test(ExchangeContext ctx);

    /**
     * Returns a composed condition that requires both this and {@code other} to pass.
     *
     * @param other additional condition that must also pass
     * @return composed AND condition
     */
    default ExchangeCondition and(ExchangeCondition other) {
        return ctx -> this.test(ctx) && other.test(ctx);
    } // and ()

    /**
     * Returns a composed condition that passes if either this or {@code other} passes.
     *
     * @param other alternative condition
     * @return composed OR condition
     */
    default ExchangeCondition or(ExchangeCondition other) {
        return ctx -> this.test(ctx) || other.test(ctx);
    } // or ()

    /**
     * Returns a condition that inverts this condition's result.
     *
     * @return negated condition
     */
    default ExchangeCondition negate() {
        return ctx -> !this.test(ctx);
    } // negate ()

} // Interface: ExchangeCondition
