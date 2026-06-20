package net.heriazone.hzlib.api.entity.features.emanation;

/**
 * <p>Predicate that guards whether an {@link EmanationRule} fires in a given context.<p>
 * <p>
 * <b>Composition:</b> Conditions compose naturally via {@link #and}, {@link #or},
 * and {@link #negate}. Use {@link EmanationConditions} for common pre-built conditions.
 * <p>
 * <b>Server-side only.</b> Conditions are evaluated on the server thread.
 */
@FunctionalInterface
public interface EmanationCondition {

    /**
     * Tests whether this condition passes for the given emanation context.
     *
     * @param ctx immutable snapshot of the world at the emanation moment
     * @return true if the rule should fire
     */
    boolean test(EmanationContext ctx);

    /** Returns a condition that passes only when both this and {@code other} pass. */
    default EmanationCondition and(EmanationCondition other) {
        return ctx -> this.test(ctx) && other.test(ctx);
    } // and ()

    /** Returns a condition that passes when either this or {@code other} passes. */
    default EmanationCondition or(EmanationCondition other) {
        return ctx -> this.test(ctx) || other.test(ctx);
    } // or ()

    /** Returns a condition that passes when this condition does not. */
    default EmanationCondition negate() {
        return ctx -> !this.test(ctx);
    } // negate ()

} // Interface: EmanationCondition
