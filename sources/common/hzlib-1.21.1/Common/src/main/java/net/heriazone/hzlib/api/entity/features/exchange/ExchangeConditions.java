package net.heriazone.hzlib.api.entity.features.exchange;

import net.heriazone.hzlib.api.entity.NativeEntity;
import net.heriazone.hzlib.framework.entity.enums.EntityState;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>Static factory class for common {@link ExchangeCondition} implementations.<p>
 * <p>
 * <b>Architecture:</b> All methods return {@link ExchangeCondition} instances that can
 * be chained via {@code .and()}, {@code .or()}, and {@code .negate()}. Conditions are
 * stateless and reusable across multiple rules and entity types.
 * <p>
 * <b>Usage pattern:</b>
 * <pre>{@code
 * ExchangeConditions.ownerOnly()
 *     .and(ExchangeConditions.inBiome(Biomes.FLOWER_FOREST, Biomes.CHERRY_GROVE))
 * }</pre>
 */
public final class ExchangeConditions {

    // -- Constructor --

    private ExchangeConditions() {} // Static utility class

    // -- Ownership --

    /**
     * Passes only when the interacting player owns the entity.
     * <p>
     * <b>Note:</b> The entity must be tamed and its owner UUID must match the player.
     * Untamed entities always fail this condition.
     *
     * @return owner-only condition
     */
    public static ExchangeCondition ownerOnly() {
        return ExchangeContext::isOwnedByPlayer;
    } // ownerOnly ()

    /**
     * Passes for any player — entity does not need to be tamed.
     * Effectively a no-op condition, useful as an explicit readable counterpart to
     * {@link #ownerOnly()} when declaring rules that should work for everyone.
     *
     * @return always-true condition
     */
    public static ExchangeCondition anyPlayer() {
        return ctx -> true;
    } // anyPlayer ()

    // -- Biome --

    /**
     * Passes when the entity is located in any of the specified biomes.
     * <p>
     * <b>Server-only:</b> Biome lookup is not available client-side. This condition
     * always fails on the client, which is correct — exchanges only fire server-side.
     *
     * @param biomes biomes that satisfy this condition (at least one)
     * @return biome condition
     */
    @SafeVarargs
    public static ExchangeCondition inBiome(ResourceKey<Biome>... biomes) {
        Set<ResourceKey<Biome>> biomeSet = new HashSet<>(Arrays.asList(biomes));
        return ctx -> ctx.getBiome().map(biomeSet::contains).orElse(false);
    } // inBiome ()

    /**
     * Passes when the entity is NOT in any of the specified biomes.
     *
     * @param biomes biomes that cause this condition to fail
     * @return negated biome condition
     */
    @SafeVarargs
    public static ExchangeCondition notInBiome(ResourceKey<Biome>... biomes) {
        return inBiome(biomes).negate();
    } // notInBiome ()

    // -- Dimension --

    /**
     * Passes when the interaction occurs in the specified dimension.
     *
     * @param dimension dimension resource key (e.g., {@code Level.OVERWORLD}, {@code Level.NETHER})
     * @return dimension condition
     */
    public static ExchangeCondition inDimension(ResourceKey<Level> dimension) {
        return ctx -> dimension.equals(ctx.getDimension());
    } // inDimension ()

    /**
     * Passes when the interaction occurs in any of the specified dimensions.
     *
     * @param dimensions dimensions that satisfy this condition
     * @return dimension condition
     */
    @SafeVarargs
    public static ExchangeCondition inDimension(ResourceKey<Level>... dimensions) {
        Set<ResourceKey<Level>> dimSet = new HashSet<>(Arrays.asList(dimensions));
        return ctx -> dimSet.contains(ctx.getDimension());
    } // inDimension ()

    // -- Time --

    /**
     * Passes during daytime (ticks 0–12000).
     *
     * @return daytime condition
     */
    public static ExchangeCondition isDaytime() {
        return ctx -> ctx.getDayTime() < 12000L;
    } // isDaytime ()

    /**
     * Passes during nighttime (ticks 12000–23999).
     *
     * @return nighttime condition
     */
    public static ExchangeCondition isNighttime() {
        return isDaytime().negate();
    } // isNighttime ()

    /**
     * Passes when the day time is within the specified range (inclusive).
     *
     * @param fromTick start tick (0–23999)
     * @param toTick   end tick (0–23999)
     * @return time range condition
     */
    public static ExchangeCondition inTimeRange(long fromTick, long toTick) {
        return ctx -> {
            long time = ctx.getDayTime();
            return time >= fromTick && time <= toTick;
        };
    } // inTimeRange ()

    // -- Entity State --

    /**
     * Passes when the entity's current behavioral state matches any of the specified states.
     * <p>
     * <b>Note:</b> Only applies to entities that extend {@link NativeEntity} and expose a
     * behavioral state. Non-NativeEntity entities always fail this condition.
     *
     * @param states allowed states
     * @return entity state condition
     */
    public static ExchangeCondition entityInState(EntityState... states) {
        Set<EntityState> stateSet = new HashSet<>(Arrays.asList(states));
        return ctx -> {
            if (ctx.getEntity() instanceof NativeEntity internal) {
                return stateSet.contains(internal.getCurrentState());
            }
            return false;
        };
    } // entityInState ()

    // -- Probability --

    /**
     * Passes with the specified probability on each evaluation.
     * <p>
     * <b>Use case:</b> Adds randomness — e.g., a rare bonus output that only fires 10%
     * of the time. Combine with other conditions via {@code .and()}.
     *
     * @param probability chance of passing (0.0 = never, 1.0 = always)
     * @return probability condition
     */
    public static ExchangeCondition chance(float probability) {
        return ctx -> ThreadLocalRandom.current().nextFloat() < probability;
    } // chance ()

    // -- Weather --

    /**
     * Passes when it is currently raining in the level.
     *
     * @return raining condition
     */
    public static ExchangeCondition isRaining() {
        return ctx -> ctx.getLevel().isRaining();
    } // isRaining ()

    /**
     * Passes when it is currently thundering in the level.
     *
     * @return thundering condition
     */
    public static ExchangeCondition isThundering() {
        return ctx -> ctx.getLevel().isThundering();
    } // isThundering ()

} // Class: ExchangeConditions
