package net.msymbios.llovelyr.source;

import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.msymbios.llovelyr.LovelyConstant;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Centralizes Forge event handler registration with side-specific subscribers.
 * <p>
 * <b>Architecture:</b> Uses Forge's @EventBusSubscriber annotation for automatic
 * registration, organizing handlers into nested classes by physical side (client/server).
 * Eliminates manual event registration boilerplate.
 * <p>
 * <b>Side Isolation:</b> Client and server events are separated to prevent class
 * loading issues on dedicated servers (client-only classes) and logical clients
 * (server-only events).
 */
@Mod.EventBusSubscriber(modid = LovelyConstant.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class LovelyEvents {

    // -- Custom Methods --

    @SubscribeEvent
    public static void onRegisterEntityAttribute(EntityAttributeCreationEvent event) {
        LovelyEntities.registerAttribute(event);
    } // onRegisterEntityAttribute ()

    // -- Nested Classes --

    /**
     * FORGE bus event handlers for gameplay and server events.
     * <p>
     * <b>Bus:</b> Subscribes to FORGE bus for gameplay events (not MOD bus
     * for lifecycle events). Command registration occurs on FORGE bus.
     */
    @Mod.EventBusSubscriber(modid = LovelyConstant.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {

        // -- Custom Methods --

        /**
         * Registers robot management commands during server initialization.
         * <p>
         * <b>Timing:</b> Fires during RegisterCommandsEvent, after command
         * dispatcher is created but before server accepts connections. Ensures
         * commands are available immediately when server starts.
         * <p>
         * <b>Architecture:</b> Delegates to LovelyCommands for centralized
         * command tree construction, maintaining separation of concerns between
         * event handling and command logic.
         *
         * @param event the command registration event
         */
        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {
            LovelyCommands.register(event.getDispatcher());
            LovelyConstant.LOGGER.info("Registered LovelyRobotEntity commands");
        } // onRegisterCommands()

    } // Class: ForgeEvents

    /**
     * Server-side event handlers for dedicated server lifecycle.
     * <p>
     * <b>Distribution:</b> Only loaded on dedicated servers (Dist.DEDICATED_SERVER),
     * preventing client-only class references from causing crashes.
     * <p>
     * <b>Bus:</b> Subscribes to MOD bus for lifecycle events (not FORGE bus for
     * gameplay events).
     */
    @Mod.EventBusSubscriber(modid = LovelyConstant.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER)
    public static class ServerEvents {

        // -- Custom Methods --

        /**
         * Handles server startup initialization.
         * <p>
         * <b>Timing:</b> Fires after server registries are frozen but before world
         * loading. Suitable for server-side data initialization.
         * <p>
         * <i>Note:</i> Currently placeholder for future server-side logic.
         *
         * @param event the server starting event
         */
        @SubscribeEvent
        public void onServerStarting(ServerStartingEvent event) {
            LovelyConstant.LOGGER.info("HELLO FROM SERVER: STARTING");
        } // onServerStarting()

    } // Class: ServerEvents

    /**
     * Client-side event handlers for rendering and client initialization.
     * <p>
     * <b>Distribution:</b> Only loaded on physical clients (Dist.CLIENT), preventing
     * server crashes from client-only class references.
     * <p>
     * <b>Bus:</b> Subscribes to MOD bus for lifecycle events, ensuring handlers
     * fire during mod initialization phases.
     */
    @Mod.EventBusSubscriber(modid = LovelyConstant.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {

        // -- Custom Methods --

        /**
         * Registers client-side item model predicates during setup phase.
         * <p>
         * <b>Timing:</b> Fires during FMLClientSetupEvent, after item registration
         * but before rendering initialization. Ideal for model predicate registration.
         * <p>
         * <b>Thread Safety:</b> Uses enqueueWork to ensure registration happens
         * on the main thread, as required by Minecraft's rendering system.
         *
         * @param event the client setup event
         */
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LovelyItems.registerModel(event);
        } // onClientSetup()

        /**
         * Registers entity renderers during renderer registration event.
         * <p>
         * <b>Architecture:</b> Uses EntityRenderersEvent.RegisterRenderers which is
         * the proper event for entity renderer registration in Forge 1.20.1+.
         * This event fires at the correct time and on the correct thread.
         * <p>
         * <b>Timing:</b> Fires during client initialization, specifically for
         * registering entity renderers before world rendering begins.
         *
         * @param event the entity renderers registration event
         */
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            LovelyEntities.registerRender(event);
        } // registerRenderers()

    } // Class: ClientEvents

} // Class: LovelyEvents