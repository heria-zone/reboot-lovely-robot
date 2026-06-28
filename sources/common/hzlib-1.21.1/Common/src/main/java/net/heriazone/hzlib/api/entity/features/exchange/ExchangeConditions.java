package net.heriazone.hzlib.api.entity.features.exchange;

import net.heriazone.hzlib.api.entity.NativeEntity;
import net.heriazone.hzlib.api.entity.conditions.EntityConditions;
import net.heriazone.hzlib.framework.entity.enums.EntityState;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Static factory for {@link ExchangeCondition} predicates.
 * <p>
 * <b>Architecture:</b> Environment conditions ({@code inBiome}, {@code inDimension},
 * time, weather, chance) delegate to {@link EntityConditions} — one implementation,
 * zero duplication. Only exchange-specific conditions live here.
 * <p>
 * All conditions are stateless and composable:
 * <pre>{@code
 * ExchangeConditions.ownerOnly()
 *     .and(ExchangeConditions.inBiome(Biomes.FLOWER_FOREST, Biomes.CHERRY_GROVE))
 * }</pre>
 */
public final class ExchangeConditions {

    private ExchangeConditions() {} // non-instantiable

    // -- Ownership --

    /**
     * Passes only when the interacting player owns the entity.
     * <p>
     * <i>Note:</i> Untamed entities always fail. Ownership is determined by UUID match.
     */
    public static ExchangeCondition ownerOnly() {
        return ExchangeContext::isOwnedByPlayer;
    } // ownerOnly ()

    /**
     * Passes for any player regardless of ownership.
     * <p>
     * Explicit counterpart to {@link #ownerOnly()} — makes intent readable at rule
     * declaration sites rather than relying on the absence of a condition.
     */
    public static ExchangeCondition anyPlayer() {
        return ctx -> true;
    } // anyPlayer ()

    // -- Entity State --

    /**
     * Passes when the entity's behavioral state matches any of the specified states.
     * <p>
     * <i>Note:</i> Non-{@link NativeEntity} entities always fail — they have no
     * behavioral state.
     *
     * @param states allowed states
     */
    public static ExchangeCondition entityInState(EntityState... states) {
        Set<EntityState> stateSet = new HashSet<>(Arrays.asList(states));
        return ctx -> ctx.getEntity() instanceof NativeEntity internal
                && stateSet.contains(internal.getCurrentState());
    } // entityInState ()

    // -- Environment (delegating to EntityConditions) --

    /**
     * Passes when the entity is in any of the specified biomes.
     *
     * @param biomes at least one biome key that satisfies this condition
     */
    @SafeVarargs
    public static ExchangeCondition inBiome(ResourceKey<Biome>... biomes) {
        return ctx -> EntityConditions.inBiome(biomes).test(ctx);
    } // inBiome ()

    /**
     * Passes when the entity is NOT in any of the specified biomes.
     *
     * @param biomes biome keys that cause this condition to fail
     */
    @SafeVarargs
    public static ExchangeCondition notInBiome(ResourceKey<Biome>... biomes) {
        return ctx -> EntityConditions.notInBiome(biomes).test(ctx);
    } // notInBiome ()

    /**
     * Passes when the interaction occurs in any of the specified dimensions.
     *
     * @param dimensions dimension keys (e.g. {@code Level.OVERWORLD})
     */
    @SafeVarargs
    public static ExchangeCondition inDimension(ResourceKey<Level>... dimensions) {
        return ctx -> EntityConditions.inDimension(dimensions).test(ctx);
    } // inDimension ()

    /** Passes during daytime (ticks 0–11999). */
    public static ExchangeCondition isDaytime() {
        return ctx -> EntityConditions.<ExchangeContext>isDaytime().test(ctx);
    } // isDaytime ()

    /** Passes during nighttime (ticks 12000–23999). */
    public static ExchangeCondition isNighttime() {
        return ctx -> EntityConditions.<ExchangeContext>isNighttime().test(ctx);
    } // isNighttime ()

    /**
     * Passes when the day time falls within [{@code fromTick}, {@code toTick}] inclusive.
     *
     * @param fromTick start tick, inclusive (0–23999)
     * @param toTick   end tick, inclusive (0–23999)
     */
    public static ExchangeCondition inTimeRange(long fromTick, long toTick) {
        return ctx -> EntityConditions.<ExchangeContext>inTimeRange(fromTick, toTick).test(ctx);
    } // inTimeRange ()

    /** Passes when it is raining in the entity's level. */
    public static ExchangeCondition isRaining() {
        return ctx -> EntityConditions.<ExchangeContext>isRaining().test(ctx);
    } // isRaining ()

    /** Passes when it is thundering in the entity's level. */
    public static ExchangeCondition isThundering() {
        return ctx -> EntityConditions.<ExchangeContext>isThundering().test(ctx);
    } // isThundering ()

    /**
     * Passes with the given probability on each independent evaluation.
     *
     * @param probability chance of passing; 0.0 = never, 1.0 = always
     */
    public static ExchangeCondition chance(float probability) {
        return ctx -> EntityConditions.<ExchangeContext>chance(probability).test(ctx);
    } // chance ()

} // Class: ExchangeConditions
