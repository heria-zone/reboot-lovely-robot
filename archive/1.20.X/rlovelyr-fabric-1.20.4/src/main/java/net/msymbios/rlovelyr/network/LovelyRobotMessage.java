package net.msymbios.rlovelyr.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.internal.InterfaceEntity;

import java.util.Objects;
import java.util.UUID;

public abstract class LovelyRobotMessage<T extends LovelyRobotMessage<T>> extends Message<Message> {

    // -- Variables --

    /**
     * The blockling.
     */
    protected InterfaceEntity blockling;

    /**
     * The blockling's entity id.
     */
    protected int blocklingId;

    /**
     * The player's id if we are on the client.
     * Used so we can avoid syncing the same information back to a sending client.
     */
    private UUID clientPlayerId = new UUID(0L, 0L);

    /**
     * Determines whether messages received on the server should be synced back to all other clients.
     */
    private boolean syncBackToClients = true;

    public static final Identifier LOVELY_ROBOT_MESSAGE_ID = new Identifier("rlovelyr", "lovely_robot_message");

    // -- Constructors --

    protected LovelyRobotMessage(InterfaceEntity entity) {
        this.blockling = entity;
        if (entity != null) {
            blocklingId = entity.getId();
            if (entity.getWorld().isClient()) clientPlayerId = getClientPlayerId();
        }
    } // Constructor LovelyRobotMessage ()

    protected LovelyRobotMessage(InterfaceEntity blockling, boolean syncBackToClients) {
        this(blockling);
        this.syncBackToClients = syncBackToClients;
    } // Constructor LovelyRobotMessage ()

    // -- Methods --

    public void encode(PacketByteBuf buf) {
        buf.writeInt(blocklingId);
        buf.writeUuid(clientPlayerId);
        buf.writeBoolean(syncBackToClients);
    } // encode ()

    public void decode(PacketByteBuf buf) {
        blocklingId = buf.readInt();
        clientPlayerId = buf.readUuid();
        syncBackToClients = buf.readBoolean();
    } // decode ()

    public void handle(MinecraftServer server, ServerPlayerEntity player, PacketByteBuf buf) {
        InterfaceEntity blockling = (InterfaceEntity) player.getWorld().getEntityById(blocklingId);

        if (blockling == null) return;

        handle(player, blockling);

        if (syncBackToClients) {
            for (ServerPlayerEntity otherPlayer : server.getPlayerManager().getPlayerList()) {
                if (!otherPlayer.getUuid().equals(clientPlayerId)) {
                    ServerPlayNetworking.send(otherPlayer, LOVELY_ROBOT_MESSAGE_ID, (PacketByteBuf) buf.copy());
                }
            }
        }
    } // handle ()

    public void handleClient(MinecraftClient client, PacketByteBuf buf) {
        InterfaceEntity entity = (InterfaceEntity) client.world.getEntityById(blocklingId);
        if (entity == null) return;
        handle(client.player, entity);
    } // handleClient ()

    protected abstract void handle(PlayerEntity player, InterfaceEntity entity);

    public void sync() {
        PacketByteBuf buf = PacketByteBufs.create();
        encode(buf);
        ServerPlayNetworking.send((ServerPlayerEntity) Objects.requireNonNull(Objects.requireNonNull(blockling.getWorld().getServer()).getPlayerManager().getPlayer(clientPlayerId)), LOVELY_ROBOT_MESSAGE_ID, buf);
    } // sync ()

    public void sendToServer(Identifier channel) {
        NetworkHandler.sendToServer((ServerPlayerEntity) Objects.requireNonNull(Objects.requireNonNull(blockling.getWorld().getServer()).getPlayerManager().getPlayer(clientPlayerId)), this, channel);
    } // sendToServer ()

    /**
     * Sends the message to the player's client.
     *
     */
    public <T extends LovelyRobotMessage<T>> void  sendToClient(Class<T> message) {
        NetworkHandler.sendToClient(message);
    } // sendToClient ()

} // LovelyRobotMessage