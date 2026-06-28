package net.heriazone.hzlib.api.entity.features;

import net.heriazone.hzlib.api.entity.conditions.EntityCondition;

/**
 * Typed predicate for {@link AppearanceRule} evaluation.
 * <p>
 * <b>Architecture:</b> Empty extension of {@link EntityCondition} — inherits
 * {@code and/or/negate} combinators at zero cost. Use {@link AppearanceConditions}
 * for pre-built conditions or supply a lambda directly.
 * <p>
 * Works at both spawn-time (biome, dimension, spawn reason) and interaction-time
 * (held item, player) — the same condition type covers both lanes.
 */
@FunctionalInterface
public interface AppearanceCondition extends EntityCondition<AppearanceContext> {

    // Combinators and test() are inherited from EntityCondition<AppearanceContext>.

} // Interface: AppearanceCondition
