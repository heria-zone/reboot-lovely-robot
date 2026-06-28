package net.heriazone.hzlib.api.entity.features;

import net.heriazone.hzlib.api.entity.conditions.EntityContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Immutable snapshot passed to every {@link AppearanceCondition} at evaluation time.
 * <p>
 * <b>Architecture:</b> Extends {@link EntityContext} — biome, dimension, day time,
 * and side checks are inherited. Only appearance-specific fields live here:
 * the interacting player, held item, and spawn reason.
 * <p>
 * <b>Two construction paths:</b>
 * <ul>
 *   <li>{@link #forSpawn} — built in {@code finalizeSpawn}; {@code player} is null,
 *       {@code heldItem} is {@link ItemStack#EMPTY}.</li>
 *   <li>{@link #forInteraction} — built on right-click; {@code spawnReason} is null.</li>
 * </ul>
 * Conditions use {@link #isSpawn()} / {@link #isInteraction()} rather than null-checks.
 */
public final class AppearanceContext extends EntityContext {

    // -- Fields --

    @Nullable private final MobSpawnType spawnReason;
    @Nullable private final Player       player;
    private final ItemStack              heldItem; // never null — EMPTY at spawn

    // -- Constructors (private — use static factories) --

    private AppearanceContext(net.minecraft.world.entity.LivingEntity entity,
                               @Nullable MobSpawnType spawnReason,
                               @Nullable Player player,
                               ItemStack heldItem) {
        super(entity);
        this.spawnReason = spawnReason;
        this.player      = player;
        this.heldItem    = heldItem;
    } // Constructor: AppearanceContext ()

    // -- Static factories --

    /**
     * Creates a spawn-time context.
     * <p>
     * {@code player} will be null; {@code heldItem} will be {@link ItemStack#EMPTY}.
     * Conditions that require a player (e.g. {@link AppearanceConditions#heldItem})
     * will fail gracefully via the {@link ItemStack#EMPTY} guard.
     *
     * @param entity the entity being spawned
     * @param reason the spawn trigger (NATURAL, COMMAND, SPAWNER, etc.)
     */
    public static AppearanceContext forSpawn(net.minecraft.world.entity.LivingEntity entity,
                                              ServerLevelAccessor world,
                                              BlockPos pos,
                                              MobSpawnType reason) {
        return new AppearanceContext(entity, reason, null, ItemStack.EMPTY);
    } // forSpawn ()

    /**
     * Creates an interaction-time context.
     * <p>
     * {@code spawnReason} will be null. Conditions that require spawn data
     * (e.g. {@link AppearanceConditions#naturalSpawn()}) will fail gracefully.
     *
     * @param entity  the entity being interacted with
     * @param player  the interacting player
     * @param held    the item held by the player; pass {@link ItemStack#EMPTY} if empty
     */
    public static AppearanceContext forInteraction(net.minecraft.world.entity.LivingEntity entity,
                                                    Level level,
                                                    BlockPos pos,
                                                    Player player,
                                                    ItemStack held) {
        return new AppearanceContext(entity, null, player, held.isEmpty() ? ItemStack.EMPTY : held);
    } // forInteraction ()

    // -- Appearance-specific accessors --

    /**
     * Returns the spawn reason, or {@code null} for interaction contexts.
     * <p>
     * Use {@link #isSpawn()} as a guard rather than null-checking directly.
     */
    @Nullable
    public MobSpawnType getSpawnReason() {
        return spawnReason;
    } // getSpawnReason ()

    /**
     * Returns the interacting player, or empty for spawn contexts.
     */
    public Optional<Player> getPlayer() {
        return Optional.ofNullable(player);
    } // getPlayer ()

    /**
     * Returns the item held by the player, or {@link ItemStack#EMPTY} at spawn.
     * Never null.
     */
    public ItemStack getHeldItem() {
        return heldItem;
    } // getHeldItem ()

    /** Returns {@code true} when this context was created at spawn time. */
    public boolean isSpawn() {
        return spawnReason != null;
    } // isSpawn ()

    /** Returns {@code true} when this context was created by a player interaction. */
    public boolean isInteraction() {
        return player != null;
    } // isInteraction ()

} // Class: AppearanceContext
