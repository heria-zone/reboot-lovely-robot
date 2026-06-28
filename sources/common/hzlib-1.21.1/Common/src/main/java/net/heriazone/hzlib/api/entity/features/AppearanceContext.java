package net.heriazone.hzlib.api.entity.features;

import net.heriazone.hzlib.api.entity.conditions.EntityContext;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Immutable snapshot passed to every {@link AppearanceCondition} at evaluation time.
 * <p>
 * <b>Architecture:</b> Extends {@link EntityContext} but overrides {@link #getBiome()}
 * to use the authoritative {@code biomeSource} rather than {@code entity.level()}.
 * At spawn time the entity is not yet fully placed in the world — {@code entity.level()}
 * may return a stale {@code Level} reference before the entity's position is committed,
 * causing biome queries to return incorrect results. The {@code ServerLevelAccessor}
 * passed to {@code finalizeSpawn} is always authoritative for biome data at that call site.
 * <p>
 * <b>Two construction paths:</b>
 * <ul>
 *   <li>{@link #forSpawn} — built in {@code finalizeSpawn}; stores the {@code world}
 *       accessor for biome queries. {@code player} is null, {@code heldItem} is
 *       {@link ItemStack#EMPTY}.</li>
 *   <li>{@link #forInteraction} — built on right-click; uses {@code entity.level()}
 *       for biome (entity is fully placed). {@code spawnReason} is null.</li>
 * </ul>
 * Conditions use {@link #isSpawn()} / {@link #isInteraction()} rather than null-checks.
 */
public final class AppearanceContext extends EntityContext {

    // -- Fields --

    /**
     * Authoritative level reader for biome and world-state queries.
     * At spawn time this is the {@link ServerLevelAccessor} passed to {@code finalizeSpawn}.
     * At interaction time this is the same as {@link EntityContext#level}.
     */
    private final LevelReader biomeSource;

    /** Authoritative position for biome lookup — may differ from entity.blockPosition() at spawn. */
    private final BlockPos spawnPos;

    @Nullable private final MobSpawnType spawnReason;
    @Nullable private final Player       player;
    private final ItemStack              heldItem; // never null — EMPTY at spawn

    // -- Private constructor --

    private AppearanceContext(net.minecraft.world.entity.LivingEntity entity,
                               LevelReader biomeSource,
                               BlockPos spawnPos,
                               @Nullable MobSpawnType spawnReason,
                               @Nullable Player player,
                               ItemStack heldItem) {
        super(entity);
        this.biomeSource = biomeSource;
        this.spawnPos    = spawnPos;
        this.spawnReason = spawnReason;
        this.player      = player;
        this.heldItem    = heldItem;
    } // Constructor: AppearanceContext ()

    // -- Static factories --

    /**
     * Creates a spawn-time context using the authoritative {@link ServerLevelAccessor}.
     * <p>
     * <b>Why {@code world} instead of {@code entity.level()}:</b> During {@code finalizeSpawn}
     * the entity's own level reference may not yet reflect its committed spawn position.
     * The {@code world} parameter is the canonical source for biome data at that call site
     * — passing it here ensures {@code AppearanceConditions.inBiome()} evaluates correctly.
     *
     * @param entity the entity being spawned
     * @param world  the {@link ServerLevelAccessor} from {@code finalizeSpawn} — biome-authoritative
     * @param pos    the block position at which the entity is spawning
     * @param reason the spawn trigger (NATURAL, CHUNK_GENERATION, SPAWNER, etc.)
     */
    public static AppearanceContext forSpawn(net.minecraft.world.entity.LivingEntity entity,
                                              ServerLevelAccessor world,
                                              BlockPos pos,
                                              MobSpawnType reason) {
        return new AppearanceContext(entity, world, pos, reason, null, ItemStack.EMPTY);
    } // forSpawn ()

    /**
     * Creates an interaction-time context.
     * <p>
     * The entity is fully placed in the world — {@code entity.level()} is the correct
     * biome source here. {@code spawnReason} is null; conditions checking spawn data
     * (e.g. {@link AppearanceConditions#naturalSpawn()}) fail gracefully.
     *
     * @param entity  the entity being interacted with
     * @param level   the level (used for day-time and weather queries)
     * @param pos     the position (currently the entity's block position)
     * @param player  the interacting player
     * @param held    the item held by the player; pass {@link ItemStack#EMPTY} if empty
     */
    public static AppearanceContext forInteraction(net.minecraft.world.entity.LivingEntity entity,
                                                    Level level,
                                                    BlockPos pos,
                                                    Player player,
                                                    ItemStack held) {
        return new AppearanceContext(entity, level, pos, null, player,
                held.isEmpty() ? ItemStack.EMPTY : held);
    } // forInteraction ()

    // -- Biome override --

    /**
     * Returns the biome key at the spawn position using the authoritative source.
     * <p>
     * Overrides {@link EntityContext#getBiome()} — the base implementation queries
     * {@code entity.level()} which can be stale during {@code finalizeSpawn}.
     * This implementation queries {@link #biomeSource} at the committed spawn position.
     */
    @Override
    public Optional<ResourceKey<Biome>> getBiome() {
        return biomeSource.getBiome(spawnPos).unwrapKey();
    } // getBiome ()

    /**
     * Returns the spawn position.
     * Overrides {@link EntityContext#getPos()} — at spawn time the entity's
     * {@code blockPosition()} may not yet be committed.
     */
    @Override
    public BlockPos getPos() {
        return spawnPos;
    } // getPos ()

    // -- Appearance-specific accessors --

    /**
     * Returns the spawn reason, or {@code null} for interaction contexts.
     * Use {@link #isSpawn()} as a guard rather than null-checking directly.
     */
    @Nullable
    public MobSpawnType getSpawnReason() {
        return spawnReason;
    } // getSpawnReason ()

    /** Returns the interacting player, or empty for spawn contexts. */
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
