package net.msymbios.llovelyr.source.events;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.msymbios.llovelyr.LovelyLegacy;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.msymbios.llovelyr.source.entity.LovelyEntities;
import net.msymbios.llovelyr.source.items.LovelyItems;

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
@Mod.EventBusSubscriber(modid = LovelyLegacy.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class LovelyEvents {

    // -- Custom Methods --

    @SubscribeEvent
    public static void onRegisterEntityAttribute(EntityAttributeCreationEvent event) {
        LovelyEntities.registerAttribute(event);
    } // onRegisterEntityAttribute ()

    // -- Nested Classes --

    /**
     * Server-side event handlers for dedicated server lifecycle.
     * <p>
     * <b>Distribution:</b> Only loaded on dedicated servers (Dist.DEDICATED_SERVER),
     * preventing client-only class references from causing crashes.
     * <p>
     * <b>Bus:</b> Subscribes to MOD bus for lifecycle events (not FORGE bus for
     * gameplay events).
     */
    @Mod.EventBusSubscriber(modid = LovelyLegacy.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER)
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
            LovelyLegacy.LOGGER.info("HELLO FROM SERVER: STARTING");
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
    @Mod.EventBusSubscriber(modid = LovelyLegacy.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {

        // -- Custom Methods --

        /**
         * Registers client-side item model predicates during setup phase.
         * <p>
         * <b>Timing:</b> Fires during FMLClientSetupEvent, after item registration
         * but before rendering initialization. Ideal for model predicate registration.
         * <p>
         * <b>Thread Safety:</b> Delegates to LovelyItems.registerModel which uses
         * enqueueWork for main thread execution.
         *
         * @param event the client setup event
         */
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            //LovelyItems.registerModel(event);
            //LovelyEntities.registerRender();
        } // onClientSetup()

    } // Class: ClientEvents

} // Class: LovelyEvents