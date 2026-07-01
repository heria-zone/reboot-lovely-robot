package net.heriazone.hzlib.api.animation;

import net.heriazone.hzlib.api.entity.NativeEntity;

/**
 * Per-tick predicate for {@link IdleSlot} evaluation inside
 * {@link AnimationStateManager#getLocomotionAnimation}.
 * <p>
 * <b>Architecture:</b> Evaluated server-side once per locomotion-controller tick
 * (not every render frame) by {@code AnimationStateManager.resolveIdleSlot()}.
 * The entity reference provides read access to synced state — current behavioral
 * state, level, sitting pose — which is sufficient for all known idle-slot conditions.
 * <p>
 * <b>Design Decision:</b> Takes {@link NativeEntity} directly rather than a context
 * wrapper. Idle conditions depend only on entity state already available on the
 * server — wrapping it in a context object adds indirection with no benefit.
 * Contrast with {@link net.heriazone.hzlib.api.entity.features.AppearanceCondition},
 * which needs biome, spawn reason, and held-item context not available on the entity.
 * <p>
 * <b>Performance:</b> Must be fast and allocation-free — called every tick for every
 * idle slot declared on every active entity that is not moving. No world queries,
 * no new object creation.
 */
@FunctionalInterface
public interface IdleCondition {

    /**
     * Evaluates this condition against the given entity on the current server tick.
     * <p>
     * <i>Note:</i> Must be allocation-free. Avoid world access and blocking operations.
     *
     * @param entity the entity whose idle state is being evaluated
     * @return {@code true} if this condition is satisfied
     */
    boolean test(NativeEntity entity);

} // Interface: IdleCondition
