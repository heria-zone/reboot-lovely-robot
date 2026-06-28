package net.heriazone.hzlib.api.entity.features.exchange;

import net.heriazone.hzlib.api.entity.conditions.EntityCondition;
import net.heriazone.hzlib.api.entity.conditions.EntityContext;

/**
 * Typed predicate for {@link ExchangeRule} evaluation.
 * <p>
 * <b>Architecture:</b> Empty extension of {@link EntityCondition} — inherits
 * {@code and/or/negate} and the generic {@code test} signature at zero cost.
 * Use {@link ExchangeConditions} for pre-built conditions or supply a lambda directly.
 * <p>
 * <b>Example:</b>
 * <pre>{@code
 * ExchangeConditions.inBiome(Biomes.FLOWER_FOREST)
 *     .and(ExchangeConditions.ownerOnly())
 * }</pre>
 * <p>
 * <b>Server-side only.</b> Evaluated immediately before a rule fires.
 */
@FunctionalInterface
public interface ExchangeCondition extends EntityCondition<ExchangeContext> {

    // Combinators and test() are inherited from EntityCondition<ExchangeContext>.
    // This interface exists purely to provide the ExchangeContext-typed name used
    // at all call sites — no body needed.

} // Interface: ExchangeCondition
