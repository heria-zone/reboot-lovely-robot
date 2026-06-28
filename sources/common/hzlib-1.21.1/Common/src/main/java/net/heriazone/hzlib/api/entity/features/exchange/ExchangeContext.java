package net.heriazone.hzlib.api.entity.features.exchange;

import net.heriazone.hzlib.api.entity.conditions.EntityContext;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

/**
 * Immutable snapshot of the world at the moment an exchange is attempted.
 * <p>
 * <b>Architecture:</b> Extends {@link EntityContext} — the four shared accessors
 * ({@code getDimension}, {@code getBiome}, {@code getDayTime}, {@code isServerSide})
 * are inherited. Only exchange-specific state lives here: the interacting player
 * and the ownership check.
 * <p>
 * Collected once per interaction and reused across all rules in the evaluation pass.
 */
public final class ExchangeContext extends EntityContext {

    // -- Fields --

    private final Player player;

    // -- Constructor --

    /**
     * Creates a context snapshot for the given interaction.
     *
     * @param entity the entity being interacted with
     * @param player the player performing the interaction
     */
    public ExchangeContext(TamableAnimal entity, Player player) {
        super(entity);
        this.player = Objects.requireNonNull(player, "player must not be null");
    } // Constructor: ExchangeContext ()

    // -- Exchange-specific accessors --

    /**
     * Returns the entity cast to {@link TamableAnimal}.
     * <p>
     * <i>Note:</i> Covariant convenience — base {@link #getEntity()} returns
     * {@code LivingEntity}; exchange rules always deal with tameable entities.
     */
    public TamableAnimal getEntity() {
        return (TamableAnimal) super.getEntity();
    } // getEntity ()

    /** Returns the player performing the interaction. */
    public Player getPlayer() {
        return player;
    } // getPlayer ()

    /**
     * Returns {@code true} when the entity is tamed and owned by the interacting player.
     */
    public boolean isOwnedByPlayer() {
        TamableAnimal tamable = getEntity();
        return tamable.isTame()
                && tamable.getOwnerUUID() != null
                && tamable.getOwnerUUID().equals(player.getUUID());
    } // isOwnedByPlayer ()

} // Class: ExchangeContext
