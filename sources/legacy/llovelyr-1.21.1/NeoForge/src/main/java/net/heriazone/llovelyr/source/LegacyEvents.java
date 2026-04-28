package net.heriazone.llovelyr.source;

import net.heriazone.llovelyr.Legacy;
import net.heriazone.lovelylib.common.commands.LovelyCommands;
import net.heriazone.lovelylib.common.entity.RobotEntity;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Centralizes NeoForge event handler registration with side-specific subscribers.
 * <p>
 * <b>Architecture:</b> Uses NeoForge's @EventBusSubscriber annotation for automatic
 * registration, organizing handlers into nested classes by physical side (client/server).
 * Eliminates manual event registration boilerplate.
 * <p>
 * <b>Side Isolation:</b> Client and server events are separated to prevent class
 * loading issues on dedicated servers (client-only classes) and logical clients
 * (server-only events).
 */
@EventBusSubscriber(modid = Legacy.MODID, bus = EventBusSubscriber.Bus.MOD)
public class LegacyEvents {

    // -- Custom Methods --

    /**
     * Registers entity attributes during entity attribute creation event.
     * <p>
     * <b>Timing:</b> Fires during mod construction on MOD bus.
     *
     * @param event entity attribute creation event
     */
    @SubscribeEvent
    public static void onRegisterEntityAttribute(EntityAttributeCreationEvent event) {
        LegacyEntities.registerAttribute(event);
    } // onRegisterEntityAttribute ()

    // -- Nested Classes --

    /**
     * FORGE bus event handlers for gameplay and server events.
     * <p>
     * <b>Bus:</b> Subscribes to FORGE bus for gameplay events (not MOD bus
     * for lifecycle events). Command registration occurs on FORGE bus.
     */
    @EventBusSubscriber(modid = Legacy.MODID, bus = EventBusSubscriber.Bus.GAME)
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
            Legacy.LOGGER.info("Registered LovelyRobotEntity commands");
        } // onRegisterCommands()

        /**
         * Claims accumulated experience when entities die.
         * <p>
         * <b>Architecture:</b> When an entity dies, all robots that damaged it
         * claim their accumulated experience. This prevents farming immortal
         * entities while ensuring robots get credit for kills.
         * <p>
         * <b>Experience System:</b> Robots accumulate exp per hit (based on
         * target max HP), then claim all accumulated exp when target dies.
         *
         * @param event the living death event
         */
        @SubscribeEvent
        public static void onLivingDeath(net.neoforged.neoforge.event.entity.living.LivingDeathEvent event) {
            if (event.getEntity().level().isClientSide) return;

            java.util.UUID deadEntityId = event.getEntity().getUUID();

            // Find all robots in the world and let them claim exp from this entity
            event.getEntity().level().getEntitiesOfClass(RobotEntity.class,
                    event.getEntity().getBoundingBox().inflate(100.0),
                    robot -> true
            ).forEach(robot -> robot.claimAccumulatedExp(deadEntityId));
        } // onLivingDeath()

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
    @EventBusSubscriber(modid = Legacy.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER)
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
            Legacy.LOGGER.info("HELLO FROM SERVER: STARTING");
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
    @EventBusSubscriber(modid = Legacy.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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
            LegacyItems.registerModel(event);
        } // onClientSetup()

        /**
         * Registers entity renderers during renderer registration event.
         * <p>
         * <b>Architecture:</b> Uses EntityRenderersEvent.RegisterRenderers which is
         * the proper event for entity renderer registration in NeoForge 1.21.1+.
         * This event fires at the correct time and on the correct thread.
         * <p>
         * <b>Timing:</b> Fires during client initialization, specifically for
         * registering entity renderers before world rendering begins.
         *
         * @param event the entity renderers registration event
         */
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            LegacyEntities.registerRender(event);
        } // registerRenderers()

    } // Class: ClientEvents

} // Class: LegacyEvents