package net.heriazone.hzlib.api.entity.features;

import net.heriazone.hzlib.api.entity.conditions.EntityConditions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Static factory for {@link AppearanceCondition} predicates.
 * <p>
 * <b>Architecture:</b> Environment conditions delegate to {@link EntityConditions} —
 * one implementation, zero duplication. Only appearance-domain conditions
 * (held item, spawn reason, spawn vs. interaction) live here.
 * <p>
 * Conditions are composable at any granularity:
 * <pre>{@code
 * AppearanceConditions.onSpawn().and(AppearanceConditions.inBiome(Biomes.MUSHROOM_FIELDS))
 * AppearanceConditions.onInteraction().and(AppearanceConditions.heldItem(Items.WHITE_DYE))
 * }</pre>
 * <p>
 * <b>Evaluation order:</b> {@link ConditionalAppearanceFeature} is first-match —
 * declare most-specific rules first, {@code withDefault} last.
 */
public final class AppearanceConditions {

    private AppearanceConditions() {} // non-instantiable

    // -- Environment (delegating to EntityConditions) --

    /**
     * Passes when the entity is in any of the specified biomes.
     * <p>
     * At spawn: resolved from {@code ServerLevelAccessor}. At interaction: from the
     * entity's current position. Returns {@code false} if biome is unavailable.
     */
    @SafeVarargs
    public static AppearanceCondition inBiome(ResourceKey<Biome>... biomes) {
        return ctx -> EntityConditions.inBiome(biomes).test(ctx);
    } // inBiome ()

    /** Passes when the entity is NOT in any of the specified biomes. */
    @SafeVarargs
    public static AppearanceCondition notInBiome(ResourceKey<Biome>... biomes) {
        return ctx -> EntityConditions.notInBiome(biomes).test(ctx);
    } // notInBiome ()

    /** Passes when the context occurs in any of the specified dimensions. */
    @SafeVarargs
    public static AppearanceCondition inDimension(ResourceKey<Level>... dimensions) {
        return ctx -> EntityConditions.inDimension(dimensions).test(ctx);
    } // inDimension ()

    /** Passes during daytime (ticks 0–11999). */
    public static AppearanceCondition isDaytime() {
        return ctx -> EntityConditions.<AppearanceContext>isDaytime().test(ctx);
    } // isDaytime ()

    /** Passes during nighttime (ticks 12000–23999). */
    public static AppearanceCondition isNighttime() {
        return ctx -> EntityConditions.<AppearanceContext>isNighttime().test(ctx);
    } // isNighttime ()

    /**
     * Passes when day time falls within [{@code fromTick}, {@code toTick}] inclusive.
     *
     * @param fromTick start tick (0–23999)
     * @param toTick   end tick (0–23999)
     */
    public static AppearanceCondition inTimeRange(long fromTick, long toTick) {
        return ctx -> EntityConditions.<AppearanceContext>inTimeRange(fromTick, toTick).test(ctx);
    } // inTimeRange ()

    /** Passes when it is raining in the entity's level. */
    public static AppearanceCondition isRaining() {
        return ctx -> EntityConditions.<AppearanceContext>isRaining().test(ctx);
    } // isRaining ()

    /** Passes when it is thundering in the entity's level. */
    public static AppearanceCondition isThundering() {
        return ctx -> EntityConditions.<AppearanceContext>isThundering().test(ctx);
    } // isThundering ()

    /**
     * Passes with the given probability on each independent evaluation.
     *
     * @param probability chance of passing; 0.0 = never, 1.0 = always
     */
    public static AppearanceCondition chance(float probability) {
        return ctx -> EntityConditions.<AppearanceContext>chance(probability).test(ctx);
    } // chance ()

    // -- Appearance-specific --

    /**
     * Passes when the player's held item is one of the specified items.
     * <p>
     * At spawn: {@code heldItem} is {@link net.minecraft.world.item.ItemStack#EMPTY},
     * so this condition always fails — combine with {@link #onInteraction()} for clarity.
     *
     * @param items one or more items that satisfy this condition
     */
    public static AppearanceCondition heldItem(Item... items) {
        Set<Item> set = new HashSet<>(Arrays.asList(items));
        return ctx -> set.contains(ctx.getHeldItem().getItem());
    } // heldItem ()

    /**
     * Passes when the player's held item belongs to the specified item tag.
     * <p>
     * Supports any tag — vanilla or custom mod-defined. Custom tags require only a
     * JSON file under {@code data/<namespace>/tags/item/}, no Java changes.
     *
     * @param tag item tag to check against
     */
    public static AppearanceCondition heldItemTag(TagKey<Item> tag) {
        return ctx -> ctx.getHeldItem().is(tag);
    } // heldItemTag ()

    /** Passes only for spawn-time contexts. Useful to scope biome/dimension rules to spawning. */
    public static AppearanceCondition onSpawn() {
        return AppearanceContext::isSpawn;
    } // onSpawn ()

    /** Passes only for interaction-time contexts. Useful to scope item rules to right-click. */
    public static AppearanceCondition onInteraction() {
        return AppearanceContext::isInteraction;
    } // onInteraction ()

    /**
     * Passes when the spawn reason is one of the specified types.
     * <p>
     * At interaction-time: {@code spawnReason} is null, so this always fails —
     * combine with {@link #onSpawn()} for clarity.
     *
     * @param reasons spawn reasons that satisfy this condition
     */
    public static AppearanceCondition spawnReason(MobSpawnType... reasons) {
        Set<MobSpawnType> set = new HashSet<>(Arrays.asList(reasons));
        return ctx -> set.contains(ctx.getSpawnReason());
    } // spawnReason ()

    /**
     * Passes for natural world spawns: {@link MobSpawnType#NATURAL} and
     * {@link MobSpawnType#CHUNK_GENERATION}.
     * <p>
     * Excludes spawner blocks, commands, and eggs — useful when a biome-driven variant
     * should only apply to wild spawns, not player-placed ones.
     */
    public static AppearanceCondition naturalSpawn() {
        return spawnReason(MobSpawnType.NATURAL, MobSpawnType.CHUNK_GENERATION);
    } // naturalSpawn ()

} // Class: AppearanceConditions
