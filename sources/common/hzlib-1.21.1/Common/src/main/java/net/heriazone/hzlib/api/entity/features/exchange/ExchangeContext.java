package net.heriazone.hzlib.api.entity.features.exchange;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.Objects;
import java.util.Optional;

/**
 * <p>Immutable snapshot of the world context at the moment an exchange is attempted.<p>
 * <p>
 * <b>Architecture:</b> Passed to every {@link ExchangeCondition} during rule evaluation.
 * Separates condition logic from entity internals — conditions only see what they need
 * and cannot mutate the entity. Collected once per interaction, reused across all rules
 * in the same evaluation pass.
 * <p>
 * <b>Design Decision:</b> Plain data bag rather than an interface. Conditions are lambdas
 * that capture whatever they need from this context; there is no polymorphism required on
 * the context itself.
 */
public final class ExchangeContext {

    // -- Fields --

    private final TamableAnimal entity;
    private final Player        player;
    private final Level         level;

    // -- Constructor --

    /**
     * Creates a context snapshot for the given interaction.
     *
     * @param entity the entity being interacted with
     * @param player the player performing the interaction
     * @throws NullPointerException if entity or player is null
     */
    public ExchangeContext(TamableAnimal entity, Player player) {
        this.entity = Objects.requireNonNull(entity, "Entity cannot be null");
        this.player = Objects.requireNonNull(player, "Player cannot be null");
        this.level  = entity.level();
    } // Constructor: ExchangeContext ()

    // -- Accessors --

    /**
     * Returns the entity being interacted with.
     *
     * @return entity instance
     */
    public TamableAnimal getEntity() {
        return entity;
    } // getEntity ()

    /**
     * Returns the player performing the interaction.
     *
     * @return player instance
     */
    public Player getPlayer() {
        return player;
    } // getPlayer ()

    /**
     * Returns the world the interaction is occurring in.
     *
     * @return level instance
     */
    public Level getLevel() {
        return level;
    } // getLevel ()

    /**
     * Returns the dimension key of the current world.
     *
     * @return dimension resource key
     */
    public ResourceKey<Level> getDimension() {
        return level.dimension();
    } // getDimension ()

    /**
     * Returns the biome key at the entity's current position, if available.
     * <p>
     * <b>Server-only:</b> Biome lookup via {@code getBiome()} is only available on
     * {@code ServerLevel}. Returns {@link Optional#empty()} on the client side.
     *
     * @return biome key, or empty if not available
     */
    public Optional<ResourceKey<Biome>> getBiome() {
        return level.getBiome(entity.blockPosition()).unwrapKey();
    } // getBiome ()

    /**
     * Returns the time of day in ticks (0–23999).
     *
     * @return day time ticks
     */
    public long getDayTime() {
        return level.getDayTime() % 24000L;
    } // getDayTime ()

    /**
     * Returns whether the entity is tamed and owned by the interacting player.
     *
     * @return true if the entity is tamed by the player
     */
    public boolean isOwnedByPlayer() {
        return entity.isTame()
                && entity.getOwnerUUID() != null
                && entity.getOwnerUUID().equals(player.getUUID());
    } // isOwnedByPlayer ()

    /**
     * Returns whether the interaction is occurring on the server side.
     *
     * @return true if server side
     */
    public boolean isServerSide() {
        return !level.isClientSide;
    } // isServerSide ()

} // Class: ExchangeContext
