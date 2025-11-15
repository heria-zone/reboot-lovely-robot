package net.msymbios.rlovelyr.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.attribute.attributes.EnumAttribute;
import net.msymbios.rlovelyr.entity.attribute.attributes.numbers.FloatAttribute;
import net.msymbios.rlovelyr.entity.attribute.attributes.numbers.IntAttribute;
import net.msymbios.rlovelyr.entity.attribute.attributes.numbers.ModifiableFloatAttribute;
import net.msymbios.rlovelyr.entity.attribute.attributes.numbers.ModifiableIntAttribute;
import net.msymbios.rlovelyr.entity.attribute.messages.IsEnabledMessage;
import net.msymbios.rlovelyr.network.message.NameMessage;

import java.util.ArrayList;
import java.util.List;

public class NetworkHandler {

    // -- Methods --

    /**
     * Initialises all message handlers.
     */
    public static void register() {
        //HANDLER.registerMessage(id++, SetLevelCommandMessage.class, SetLevelCommandMessage::encode, SetLevelCommandMessage::decode, SetLevelCommandMessage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        //HANDLER.registerMessage(id++, SetTypeCommandMessage.class, SetTypeCommandMessage::encode, SetTypeCommandMessage::decode, SetTypeCommandMessage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        //HANDLER.registerMessage(id++, SetXpCommandMessage.class, SetXpCommandMessage::encode, SetXpCommandMessage::decode, SetXpCommandMessage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        registerMessage(IsEnabledMessage.class);
        registerMessage(EnumAttribute.Message.class);
        registerMessage(FloatAttribute.ValueMessage.class);
        registerMessage(ModifiableFloatAttribute.BaseValueMessage.class);
        registerMessage(IntAttribute.ValueMessage.class);
        registerMessage(ModifiableIntAttribute.BaseValueMessage.class);

        //registerMessage(Action.CountMessage.class);
        //registerMessage(BlocklingAttackTargetMessage.class);
        //registerMessage(BlocklingGuiHandler.OpenMessage.class);
        registerMessage(NameMessage.class);
        //registerMessage(BlocklingScaleMessage.class);
        //registerMessage(BlocklingTypeMessage.class);
        //registerMessage(EquipmentInventoryMessage.class);
        //registerMessage(GoalStateMessage.class);
        //registerMessage(SkillStateMessage.class);
        //registerMessage(SkillTryBuyMessage.class);

        //registerMessage(TaskCreateMessage.class);
        //registerMessage(TaskPriorityMessage.class);
        //registerMessage(TaskRemoveMessage.class);
        //registerMessage(TaskCustomNameMessage.class);
        //registerMessage(Property.TaskPropertyMessage.class);
        //registerMessage(TaskSwapPriorityMessage.class);
        //registerMessage(TaskTypeMessage.class);
        //registerMessage(TaskTypeIsUnlockedMessage.class);

        //registerMessage(OrderedItemInfoSet.AddItemInfoInfoMessage.class);
        //registerMessage(OrderedItemInfoSet.RemoveItemInfoInfoMessage.class);
        //registerMessage(OrderedItemInfoSet.MoveItemInfoInfoMessage.class);
        //registerMessage(OrderedItemInfoSet.SetItemInfoInfoMessage.class);
        //registerMessage(OrderedPatrolPointList.AddPatrolPointMessage.class);
        //registerMessage(OrderedPatrolPointList.RemovePatrolPointMessage.class);
        //registerMessage(OrderedPatrolPointList.MovePatrolPointMessage.class);
        //registerMessage(OrderedPatrolPointList.UpdatePatrolPointMessage.class);
        //registerMessage(WhitelistAllMessage.class);
        //registerMessage(WhitelistIsUnlockedMessage.class);
        //registerMessage(WhitelistSingleMessage.class);
        //registerMessage(BlocklingContainerGoal.ContainerGoalContainerAddRemoveMessage.class);
        //registerMessage(BlocklingContainerGoal.ContainerGoalContainerMessage.class);
        //registerMessage(BlocklingContainerGoal.ContainerGoalContainerMoveMessage.class);
    } // register ()

    public static <T extends LovelyRobotMessage<T>> void registerMessage(Class<T> messageType) {
        Identifier channel = getChannel(messageType);
        clientMessage(messageType, channel);
        serverMessages(messageType, channel);
    } // registerMessage ()

    private static <T extends LovelyRobotMessage<T>> void clientMessage(Class<T> messageType, Identifier channel) {
        // Register the packet handler on the client-side
        ClientPlayNetworking.registerGlobalReceiver(channel,
            (client, listener, buf, responseSender) -> {
                try {
                    T message = messageType.newInstance();
                    message.decode(buf);
                    message.handleClient(client, buf);
                } catch (InstantiationException | IllegalAccessException e) {
                    LovelyRobot.LOGGER.warn(e.getLocalizedMessage());
                    return;
                }
            }
        );
    } // registerClientMessage

    private static <T extends LovelyRobotMessage<T>> void serverMessages(Class<T> messageType, Identifier channel) {
        // Register the packet handler on the server-side
        ServerPlayNetworking.registerGlobalReceiver(channel,
                (server,  player, listener, buf, responseSender) -> {
                    try {
                        T message = messageType.newInstance();
                        message.decode(buf);
                        message.handle(server, player, buf);
                    } catch (InstantiationException | IllegalAccessException e) {
                        LovelyRobot.LOGGER.warn(e.getLocalizedMessage());
                        return;
                    }
                }
        );
    } // registerServerMessages ()

    public static <T extends LovelyRobotMessage<T>> Identifier getChannel(Class<T> messageType) {
        if(messageType == IsEnabledMessage.class) return LovelyRobotID.NET_MESSAGE_ENABLED;
        if(messageType == EnumAttribute.Message.class) return LovelyRobotID.NET_MESSAGE_ENUM_ATTRIBUTE;
        if(messageType == FloatAttribute.ValueMessage.class) return LovelyRobotID.NET_MESSAGE_FLOAT_VALUE;
        if(messageType == ModifiableFloatAttribute.BaseValueMessage.class) return LovelyRobotID.NET_MESSAGE_MODIFIABLE_FLOAT;
        if(messageType == IntAttribute.ValueMessage.class) return LovelyRobotID.NET_MESSAGE_INT_VALUE;
        if(messageType == ModifiableIntAttribute.BaseValueMessage.class) return LovelyRobotID.NET_MESSAGE_MODIFIABLE_INT;

        if(messageType == NameMessage.class) return LovelyRobotID.NET_MESSAGE_NAME;
        return null;
    } // getChannel ()

    /**
     * Sends the given message to the given player's client.
     *
     * @param message the message to send.
     */
    public static <T extends LovelyRobotMessage<T>> void sendToClient(Class<T> message) {
        Identifier channel = getChannel(message);
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        ClientPlayNetworking.send(channel, buf);
    } // sendToClient ()

    /**
     * Sends the given message to the server.
     *
     * @param message the message to send.
     */
    public static <T extends LovelyRobotMessage<T>> void sendToServer(ServerPlayerEntity player, Class<T> message) {
        Identifier channel = getChannel(message);
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        ServerPlayNetworking.send(player, channel, buf);
    } // sendToServer ()

    /**
     * Sends the given message to every player's client except the given players.
     *
     * @param world           the world the players are in.
     * @param message         the message to send.
     * @param playersToIgnore the players to not send the message to.
     */
    public static <T extends LovelyRobotMessage<T>> void sendToAllClients(World world, Class<T> message, List<PlayerEntity> playersToIgnore) {
        for (PlayerEntity player : world.getPlayers()) {
            if (!playersToIgnore.contains(player)) {
                sendToClient(message);
            }
        }
    } // sendToAllClients ()

    /**
     * Sends the message either to the server or all player's clients.
     *
     * @param world   the world.
     * @param message the message to send.
     */
    public static <T extends LovelyRobotMessage<T>> void sync(World world,  Class<T> message, Identifier channel) {
        if (world.isClient) sendToClient(message);
        else sendToAllClients(world, message, new ArrayList<>());
    } // sync ()

} // Class NetworkHandler