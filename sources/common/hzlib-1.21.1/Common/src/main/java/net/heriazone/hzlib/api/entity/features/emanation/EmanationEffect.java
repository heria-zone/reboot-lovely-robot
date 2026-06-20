package net.heriazone.hzlib.api.entity.features.emanation;

/**
 * <p>A single action executed when an {@link EmanationRule} fires.<p>
 * <p>
 * <b>Design:</b> Functional interface so effects compose cleanly and can be
 * declared as lambdas or method references. Multiple effects can be declared on
 * one rule — all execute together when the rule fires.
 * <p>
 * <b>Server-side only.</b> Effects are always executed on the server thread.
 */
@FunctionalInterface
public interface EmanationEffect {

    /**
     * Executes this effect in the given context.
     *
     * @param ctx immutable snapshot of the world at the emanation moment
     */
    void apply(EmanationContext ctx);

} // Interface: EmanationEffect
