package net.msymbios.rlovelyr.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.client.gui.screen.BlocklingsScreen;
import net.msymbios.rlovelyr.client.gui.screen.screens.StatsScreen;
import net.msymbios.rlovelyr.entity.internal.InterfaceEntity;
import net.msymbios.rlovelyr.network.LovelyRobotMessage;

import java.util.UUID;

/**
 * Used to handle opening screens and containers.
 */
public class LovelyRobotGuiHandler {

    // -- Variables --

    public static final int STATS_ID = 0;
    public static final int TASKS_ID = 1;
    public static final int EQUIPMENT_ID = 2;
    //    public static final int UTILITY_ID = 3;
    public static final int GENERAL_ID = 4;
    public static final int COMBAT_ID = 5;
    public static final int MINING_ID = 6;
    public static final int WOODCUTTING_ID = 7;
    public static final int FARMING_ID = 8;

    private static final Identifier GUI_PACKET_ID = new Identifier("rlovelyr", "open_gui");

    /**
     * The entity.
     */
    public final InterfaceEntity blockling;

    /**
     * The gui id of the most recently open gui.
     */
    private int recentGuiId = STATS_ID;

    /**
     * @param entity the entity.
     */
    public LovelyRobotGuiHandler(InterfaceEntity entity) {
        this.blockling = entity;
    } // Constructor LovelyRobotGuiHandler ()

    // -- Methods --

    /**
     * Opens the given screen on the client.
     */
    public static void openScreen(Screen screen) {
        MinecraftClient.getInstance().setScreen(screen);
        if (screen instanceof BlocklingsScreen) {
            BlocklingsScreen blocklingsScreen = (BlocklingsScreen) screen;

            // If the blockling is dead or dying, properly close the gui in case it was closed without the proper onClose() call.
            // This might happen when configuring a container and the blockling dies.
            if (blocklingsScreen.blockling.isDead()) blocklingsScreen.close();
        }
    } // openScreen ()

    /**
     * Opens the screen on the client with the given gui id.
     *
     * @param guiId     the gui id of the screen to open.
     * @param player    the player opening the screen.
     */
    @Environment(EnvType.CLIENT)
    private void openScreen(int guiId, PlayerEntity player) {
        // Create the screen for this gui id
        Screen screen = createScreen(guiId, player);

        // If there is no screen something has gone wrong
        if (screen != null) {
            MinecraftClient.getInstance().setScreen(screen);
        } else {
            LovelyRobot.LOGGER.warn("No screen exists for gui id: " + guiId);
        }
    } // openScreen ()

    /**
     * Opens the most recent blockling gui.
     * Syncs to the client/server.
     *
     * @param player the player opening the gui.
     */
    public void openGui(PlayerEntity player) {
        openGui(recentGuiId, -1, player);
        if (player.getWorld().isClient) ClientPlayNetworking.send(GUI_PACKET_ID, PacketByteBufs.create().writeInt(blockling.getId()));
    } // openGui ()

    /**
     * Opens the blockling gui based on the given gui id.
     * Syncs to the client/server.
     *
     * @param guiId  the gui id of the gui to open.
     * @param player the player opening the gui.
     */
    public void openGui(int guiId, PlayerEntity player) {
        openGui(guiId, -1, player);
    } // openGui ()

    /**
     * Opens the blockling gui based on the given gui id.
     * Syncs to the client/server if sync is true.
     *
     * @param guiId    the gui id of the gui to open.
     * @param windowId the window id for the container if there is one.
     * @param player   the player opening the gui.
     */
    private void openGui(int guiId, int windowId, PlayerEntity player) {
        if (!blockling.getWorld().isClient()) {
            // Keep track of the gui id for when we just want to open the most recent gui id
            recentGuiId = guiId;

            // Find the next window id
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            //serverPlayer.incrementScreenHandlerSyncId();
            //windowId = serverPlayer.screenHandlerSyncId;

            // Create the container for this gui id
            //AbstractContainerMenu container = createContainer(guiId, windowId, player);

            // If there is a container set the player container to it
            //if (container != null) player.containerMenu = container;


            // Tell the client to open the same container and the corresponding screen
            //OpenMessage message = new OpenMessage(blockling, guiId, windowId, player.getUuid());
           // message.sendToClient(message);
        } else {
            // Keep track of the gui id for when we just want to open the most recent gui id
            recentGuiId = guiId;

            // If the window id is -1 then we need to send the request to the server to handle before we do anything on the client
            if (windowId == -1) {
                //new OpenMessage(blockling, guiId, windowId, player.getUuid()).sendToServer(this);
                return;
            }

            // If we have a window id then we know the server has sent the request, so we can go ahead and open the container/screen
            //AbstractContainerMenu container = createContainer(guiId, windowId, player);

            // If there is a container set the player container to it
            //if (container != null) player.containerMenu = container;

            //openScreen(guiId, player, container);
        }
    } // openGui ()

    /**
     * Creates a new instance of the container with the given id.
     *
     * @param guiId    the id of the container to create.
     * @param windowId the window id to pass to the container.
     * @param player   the player to pass to the container.
     * @return the container or null if the gui does not have a container or the gui id is not recognised.
     */
    /*private AbstractContainerMenu createContainer(int guiId, int windowId, PlayerEntity player) {
        switch (guiId) {
            case EQUIPMENT_ID:
                return new EquipmentContainer(windowId, player, blockling);
            // case UTILITY_ID: return new UtilityContainer(windowId, player, blockling);
        }

        return null;
    } // createContainer ()*/

    /**
     * Creates a new instance of the screen with the given id.
     *
     * @param guiId     the id of the screen to create.
     * @param player    the player to pass to the screen.
     * @return the screen or null if gui id is not recognised.
     */
    @Environment(EnvType.CLIENT)
    private Screen createScreen(int guiId, PlayerEntity player) {
        return switch (guiId) {
            case STATS_ID -> new StatsScreen(blockling);
            // case TASKS_ID -> new TasksScreen(blockling);
            // case EQUIPMENT_ID -> new EquipmentScreen(blockling, (EquipmentContainer) container);
            // case GENERAL_ID -> new SkillsScreen(blockling, BlocklingSkills.Groups.GENERAL, TabbedUIControl.Tab.GENERAL);
            // case COMBAT_ID -> new SkillsScreen(blockling, BlocklingSkills.Groups.COMBAT, TabbedUIControl.Tab.COMBAT);
            // case MINING_ID -> new SkillsScreen(blockling, BlocklingSkills.Groups.MINING, TabbedUIControl.Tab.MINING);
            // case WOODCUTTING_ID -> new SkillsScreen(blockling, BlocklingSkills.Groups.WOODCUTTING, TabbedUIControl.Tab.WOODCUTTING);
            // case FARMING_ID -> new SkillsScreen(blockling, BlocklingSkills.Groups.FARMING, TabbedUIControl.Tab.FARMING);
            default -> null;
        };
    } // createScreen ()

    /**
     * @return the most recently opened gui id.
     */
    public int getRecentGuiId() {
        return recentGuiId;
    }

    // -- Classes --

    /**
     * The message used to sync opening a gui across the client/server.
     */
    public static class OpenMessage extends LovelyRobotMessage<OpenMessage> {

        // -- Variables --

        /**
         * The gui id.
         */
        private int guiId;

        /**
         * The window id.
         */
        private int windowId;

        /**
         * The id of the player opening the gui.
         */
        private UUID playerId;

        // -- Constructors --

        /**
         * Empty constructor used ONLY for decoding.
         */
        public OpenMessage() {
            super(null);
        } // Constructor OpenMessage ()

        /**
         * @param blockling the blockling.
         * @param guiId     the gui id.
         * @param windowId  the window id.
         */
        public OpenMessage(InterfaceEntity blockling, int guiId, int windowId, UUID playerId) {
            super(blockling, false);
            this.guiId = guiId;
            this.windowId = windowId;
            this.playerId = playerId;
        } // Constructor OpenMessage ()

        // -- Inherited Methods --

        @Override
        public void encode(PacketByteBuf buf) {
            super.encode(buf);

            buf.writeInt(guiId);
            buf.writeInt(windowId);
            buf.writeUuid(playerId);
        } // encode ()

        @Override
        public void decode(PacketByteBuf buf) {
            super.decode(buf);

            guiId = buf.readInt();
            windowId = buf.readInt();
            playerId = buf.readUuid();
        } // decode ()

        @Override
        protected void handle(PlayerEntity player, InterfaceEntity entity) {
            if (entity.getWorld().isClient()) {
                entity.guiHandler.openGui(guiId, windowId, player);
            } else {
                PlayerEntity targetedPlayer = entity.getWorld().getPlayers().stream().filter(serverPlayer -> serverPlayer.getUuid().equals(playerId)).findFirst().orElse(null);
                if (targetedPlayer != null) {
                    entity.guiHandler.openGui(guiId, windowId, targetedPlayer);
                } else {
                    LovelyRobot.LOGGER.warn("Tried opening a gui for a player that does not exist on the server with id: " + playerId);
                }
            }
        } // handle ()

    } // Class OpenMessage

} // LovelyRobotGuiHandler