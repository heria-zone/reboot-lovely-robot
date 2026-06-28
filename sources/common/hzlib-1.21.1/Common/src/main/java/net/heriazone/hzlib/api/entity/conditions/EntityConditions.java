package net.heriazone.hzlib.api.entity.conditions;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Shared factory for the nine environment conditions that are meaningful across
 * all context types.
 * <p>
 * <b>Architecture:</b> Before this class, {@code ExchangeConditions} and any future
 * domain-specific factory had to duplicate these nine bodies. Domain factories
 * (e.g. {@code AppearanceConditions}) delegate here and add only their own
 * domain-specific methods.
 * <p>
 * All methods are generic over {@code C extends EntityContext} so they return the
 * caller's concrete condition type when used via delegation wrappers:
 * <pre>{@code
 * // In AppearanceConditions:
 * public static AppearanceCondition inBiome(ResourceKey<Biome>... biomes) {
 *     return ctx -> EntityConditions.inBiome(biomes).test(ctx);
 * }
 * }</pre>
 * <p>
 * <b>Server-only note:</b> Biome, weather, and time conditions query server-side
 * state. They are safe because conditions are evaluated server-side only; they
 * silently return the expected neutral value on the client if ever called there.
 */
public final class EntityConditions {

    private EntityConditions() {} // non-instantiable

    // -- Biome --

    /**
     * Passes when the entity is in any of the specified biomes.
     * <p>
     * Returns {@code false} rather than throwing when biome is unavailable
     * (client-side or in unloaded chunks) — correct behaviour since exchanges
     * only fire server-side anyway.
     *
     * @param biomes at least one biome key that satisfies this condition
     */
    @SafeVarargs
    public static <C extends EntityContext> EntityCondition<C> inBiome(ResourceKey<Biome>... biomes) {
        Set<ResourceKey<Biome>> set = new HashSet<>(Arrays.asList(biomes));
        return ctx -> ctx.getBiome().map(set::contains).orElse(false);
    } // inBiome ()

    /**
     * Passes when the entity is NOT in any of the specified biomes.
     *
     * @param biomes biome keys that cause this condition to fail
     */
    @SafeVarargs
    public static <C extends EntityContext> EntityCondition<C> notInBiome(ResourceKey<Biome>... biomes) {
        return EntityConditions.<C>inBiome(biomes).negate();
    } // notInBiome ()

    // -- Dimension --

    /**
     * Passes when the interaction occurs in any of the specified dimensions.
     *
     * @param dimensions dimension keys (e.g. {@code Level.OVERWORLD}, {@code Level.NETHER})
     */
    @SafeVarargs
    public static <C extends EntityContext> EntityCondition<C> inDimension(ResourceKey<Level>... dimensions) {
        Set<ResourceKey<Level>> set = new HashSet<>(Arrays.asList(dimensions));
        return ctx -> set.contains(ctx.getDimension());
    } // inDimension ()

    // -- Time --

    /**
     * Passes during daytime (ticks 0–11999).
     * <p>
     * Daytime is defined as the period before the sun sets. For a finer range
     * use {@link #inTimeRange(long, long)}.
     */
    public static <C extends EntityContext> EntityCondition<C> isDaytime() {
        return ctx -> ctx.getDayTime() < 12000L;
    } // isDaytime ()

    /**
     * Passes during nighttime (ticks 12000–23999).
     */
    public static <C extends EntityContext> EntityCondition<C> isNighttime() {
        return EntityConditions.<C>isDaytime().negate();
    } // isNighttime ()

    /**
     * Passes when the day time falls within [{@code fromTick}, {@code toTick}] inclusive.
     * <p>
     * Both bounds are in the normalised [0, 23999] range. Does not handle midnight-
     * spanning ranges — for those, compose two {@code inTimeRange} calls with {@code or}.
     *
     * @param fromTick start tick, inclusive (0–23999)
     * @param toTick   end tick, inclusive (0–23999)
     */
    public static <C extends EntityContext> EntityCondition<C> inTimeRange(long fromTick, long toTick) {
        return ctx -> {
            long t = ctx.getDayTime();
            return t >= fromTick && t <= toTick;
        };
    } // inTimeRange ()

    // -- Weather --

    /**
     * Passes when it is raining in the entity's level.
     * <p>
     * <i>Note:</i> Returns {@code true} during thunderstorms as well — thunderstorms
     * are a superset of rain. Use {@link #isThundering()} to isolate thunderstorms.
     */
    public static <C extends EntityContext> EntityCondition<C> isRaining() {
        return ctx -> ctx.getLevel().isRaining();
    } // isRaining ()

    /** Passes when it is actively thundering in the entity's level. */
    public static <C extends EntityContext> EntityCondition<C> isThundering() {
        return ctx -> ctx.getLevel().isThundering();
    } // isThundering ()

    // -- Probability --

    /**
     * Passes with the given probability on each independent evaluation.
     * <p>
     * <b>Use case:</b> Rare outputs — e.g., a 10% chance bonus. Combine with
     * deterministic conditions via {@code .and()} to avoid excessive random rolls.
     *
     * @param probability chance of passing; 0.0 = never, 1.0 = always
     */
    public static <C extends EntityContext> EntityCondition<C> chance(float probability) {
        return ctx -> ThreadLocalRandom.current().nextFloat() < probability;
    } // chance ()

    // -- Terminals --

    /** Always passes — useful as an explicit readable default or fallback rule guard. */
    public static <C extends EntityContext> EntityCondition<C> always() {
        return ctx -> true;
    } // always ()

    /** Never passes — useful for temporarily disabling a rule without removing it. */
    public static <C extends EntityContext> EntityCondition<C> never() {
        return ctx -> false;
    } // never ()

} // Class: EntityConditions
