package net.msymbios.rlovelyr.common.network.messages;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.msymbios.rlovelyr.common.network.Message;
import net.msymbios.rlovelyr.common.network.NetworkHandler;
import net.msymbios.rlovelyr.entity.internal.InternalEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class BlocklingMessage<T extends BlocklingMessage<T>> extends Message {

    // -- Variables --

    /**
     * The blockling.
     */
    @Nullable
    protected InternalEntity entity;

    /**
     * The blockling's entity id.
     */
    protected int blocklingId;

    /**
     * The player's id if we are on the client.
     * Used so we can avoid syncing the same information back to a sending client.
     */
    @Nonnull
    private UUID clientPlayerId = new UUID(0L, 0L);

    /**
     * Determines whether messages received on the server should be synced back to all other clients.
     */
    private boolean syncBackToClients = true;

    // -- Constructors --

    /**
     * @param blockling the blockling.
     */
    protected BlocklingMessage(@Nullable InternalEntity blockling) {
        this.entity = blockling;
        if (blockling != null) {
            blocklingId = blockling.getId();
            if (blockling.level.isClientSide()) clientPlayerId = getClientPlayerId();
        }
    } // Constructor BlocklingMessage ()

    /**
     * @param blockling         the blockling.
     * @param syncBackToClients determines whether messages received on the server should be synced back to all other clients.
     */
    protected BlocklingMessage(@Nullable InternalEntity blockling, boolean syncBackToClients) {
        this(blockling);
        this.syncBackToClients = syncBackToClients;
    } // Constructor BlocklingMessage ()

    // -- Methods --

    /**
     * Encodes the message.
     *
     * @param buf the buffer to encode to.
     */
    public void encode(@Nonnull FriendlyByteBuf buf) {
        buf.writeInt(blocklingId);
        buf.writeUUID(clientPlayerId);
        buf.writeBoolean(syncBackToClients);
    } // encode ()

    /**
     * Decodes the message.
     *
     * @param buf the buffer to decode from.
     */
    public void decode(@Nonnull FriendlyByteBuf buf) {
        blocklingId = buf.readInt();
        clientPlayerId = buf.readUUID();
        syncBackToClients = buf.readBoolean();
    } // decode ()

    /**
     * Handles the message when received on the client/server.
     *
     * @param ctx the network context.
     */
    public void handle(@Nonnull Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            boolean isClient = context.getDirection() == NetworkDirection.PLAY_TO_CLIENT;

            Player player = isClient ? getClientPlayer() : context.getSender();
            Objects.requireNonNull(player, "No player entity found when handling message.");

            entity = (InternalEntity) player.level.getEntity(blocklingId);

            // The client may unload the blockling before the server.
            if (entity == null) return;

            handle(player, entity);

            if (!isClient && syncBackToClients)
                sendToAllClients(entity.level.players().stream().filter(serverPlayer -> serverPlayer.getUUID().equals(clientPlayerId)).collect(Collectors.toList()));

            ctx.get().setPacketHandled(true);
        });

        context.setPacketHandled(true);
    } // handle ()

    /**
     * Handles the message when received on the client/server/
     *
     * @param player    the player.
     * @param blockling the blockling.
     */
    protected abstract void handle(@Nonnull Player player, @Nonnull InternalEntity blockling);

    /**
     * Sends the message either to the server or all player's clients.
     */
    public void sync() {
        NetworkHandler.sync(Objects.requireNonNull(entity).level, this);
    } // sync ()

    /**
     * Sends the message to the server.
     */
    public void sendToServer() {
        NetworkHandler.sendToServer(this);
    } // sendToServer ()

    /**
     * Sends the message to the player's client.
     *
     * @param player the player to send the message to.
     */
    public void sendToClient(Player player) {
        NetworkHandler.sendToClient(player, this);
    } // sendToClient ()

    /**
     * Sends the given message to every player's client except the given players.
     *
     * @param playersToIgnore the players to not send the message to.
     */
    public void sendToAllClients(List<Player> playersToIgnore) {
        NetworkHandler.sendToAllClients(Objects.requireNonNull(entity).level, this, playersToIgnore);
    } // sendToAllClients ()

} // Class BlocklingMessage