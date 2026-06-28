package net.heriazone.hzlib.api.entity.conditions;

/**
 * Typed predicate over an {@link EntityContext} subtype.
 * <p>
 * <b>Architecture:</b> Generic over {@code C} so each domain (Exchange, Emanation,
 * Appearance) gets a context-typed subinterface with zero boilerplate. Combinators
 * return {@code EntityCondition<C>} — not the subtype — which is the intentional
 * trade-off: conditions are terminal expressions passed to rule builders immediately
 * after construction, never chained further.
 * <p>
 * <b>Single source of truth:</b> Before this class, {@code ExchangeCondition} and
 * {@code EmanationCondition} each carried identical {@code and/or/negate} bodies.
 * Domain subinterfaces now extend this and inherit all three with empty bodies.
 * <p>
 * <b>Usage:</b>
 * <pre>{@code
 * AppearanceConditions.inBiome(Biomes.MUSHROOM_FIELDS)
 *     .and(AppearanceConditions.onSpawn())
 *     .negate()
 * }</pre>
 *
 * @param <C> the context type this condition evaluates against
 */
@FunctionalInterface
public interface EntityCondition<C extends EntityContext> {

    /**
     * Tests whether this condition is satisfied for the given context.
     *
     * @param ctx world snapshot at the moment of evaluation
     * @return {@code true} if the condition passes
     */
    boolean test(C ctx);

    /**
     * Returns a composed condition that requires both this and {@code other} to pass.
     *
     * @param other additional condition; evaluated only when this passes (short-circuit)
     * @return composed AND condition
     */
    default EntityCondition<C> and(EntityCondition<C> other) {
        return ctx -> this.test(ctx) && other.test(ctx);
    } // and ()

    /**
     * Returns a composed condition that passes when either this or {@code other} passes.
     *
     * @param other alternative condition; evaluated only when this fails (short-circuit)
     * @return composed OR condition
     */
    default EntityCondition<C> or(EntityCondition<C> other) {
        return ctx -> this.test(ctx) || other.test(ctx);
    } // or ()

    /**
     * Returns a condition that inverts this result.
     *
     * @return negated condition
     */
    default EntityCondition<C> negate() {
        return ctx -> !this.test(ctx);
    } // negate ()

} // Interface: EntityCondition
