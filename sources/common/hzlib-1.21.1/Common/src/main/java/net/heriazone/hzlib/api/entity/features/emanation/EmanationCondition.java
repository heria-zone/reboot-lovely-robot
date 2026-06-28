package net.heriazone.hzlib.api.entity.features.emanation;

import net.heriazone.hzlib.api.entity.conditions.EntityCondition;

/**
 * Typed predicate for {@link EmanationRule} evaluation.
 * <p>
 * <b>Architecture:</b> Empty extension of {@link EntityCondition} — inherits
 * {@code and/or/negate} combinators at zero cost. Use {@link EmanationConditions}
 * for pre-built conditions or supply a lambda directly.
 * <p>
 * <b>Server-side only.</b> Evaluated on the server thread at the trigger moment.
 */
@FunctionalInterface
public interface EmanationCondition extends EntityCondition<EmanationContext> {

    // Combinators and test() are inherited from EntityCondition<EmanationContext>.

} // Interface: EmanationCondition
