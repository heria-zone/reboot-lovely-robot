package net.heriazone.hzlib.api.entity.conditions;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.Objects;
import java.util.Optional;

/**
 * Immutable world snapshot shared by all condition evaluation contexts.
 * <p>
 * <b>Architecture:</b> Captures the four accessors ({@link #getDimension},
 * {@link #getBiome}, {@link #getDayTime}, {@link #isServerSide}) that were
 * previously duplicated on {@code ExchangeContext} and {@code EmanationContext}.
 * Domain subclasses extend this and add only the fields specific to their trigger.
 * <p>
 * <b>Design Decision:</b> Plain abstract class rather than an interface — holds
 * actual fields so subclasses don't repeat the {@code entity} and {@code level}
 * captures. Subclasses call {@code super(entity)} in their constructor and inherit
 * all shared accessors for free.
 * <p>
 * <b>Thread Safety:</b> Contexts are constructed on the server thread and passed
 * only to server-side condition evaluation. Do not cache or share across threads.
 */
public abstract class EntityContext {

    // -- Fields --

    /** The entity whose interaction or state triggered this evaluation. Always non-null. */
    protected final LivingEntity entity;

    /** The level in which the entity exists at evaluation time. Always non-null. */
    protected final Level level;

    // -- Constructor --

    /**
     * Captures the entity and its current level.
     *
     * @param entity the entity being evaluated; must not be null
     */
    protected EntityContext(LivingEntity entity) {
        this.entity = Objects.requireNonNull(entity, "entity must not be null");
        this.level  = entity.level();
    } // Constructor: EntityContext ()

    // -- Accessors --

    /** Returns the entity being evaluated. */
    public LivingEntity getEntity() {
        return entity;
    } // getEntity ()

    /**
     * Returns the level at evaluation time.
     * <p>
     * <i>Note:</i> Subclasses that require {@code ServerLevel} may override with a
     * covariant return — legal Java, preserves the base API for client-side contexts.
     */
    public Level getLevel() {
        return level;
    } // getLevel ()

    /** Returns the block position of the entity at evaluation time. */
    public net.minecraft.core.BlockPos getPos() {
        return entity.blockPosition();
    } // getPos ()

    /** Returns the dimension key of the current level. */
    public ResourceKey<Level> getDimension() {
        return level.dimension();
    } // getDimension ()

    /**
     * Returns the biome key at the entity's position, if available.
     * <p>
     * Returns {@link Optional#empty()} on the client — biome lookup via
     * {@code getBiome()} requires server-side access. Condition authors
     * should guard with {@link #isServerSide()} when needed.
     */
    public Optional<ResourceKey<Biome>> getBiome() {
        return level.getBiome(entity.blockPosition()).unwrapKey();
    } // getBiome ()

    /**
     * Returns the current time of day in ticks, normalised to [0, 23999].
     * <p>
     * Daytime: 0–11999. Sunset/sunrise transitions: 12000–13200 / 22800–23999.
     * Nighttime core: 13200–22800.
     */
    public long getDayTime() {
        return level.getDayTime() % 24000L;
    } // getDayTime ()

    /** Returns {@code true} when evaluated on the server thread. */
    public boolean isServerSide() {
        return !level.isClientSide;
    } // isServerSide ()

} // Class: EntityContext
