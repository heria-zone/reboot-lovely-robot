package net.heriazone.tlovelyr.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.heriazone.tlovelyr.Common;

/**
 * <p>Forge-specific initialization and event handling for the Lovely Tribute mod.</p>
 * <p>
 * <b>Forge Integration:</b> Implements Forge's mod loading lifecycle through
 * the @Mod annotation and FML event system. Coordinates with common module
 * while handling Forge-specific registration and event management.
 * <p>
 * <b>Event Coordination:</b> Manages Forge event bus registration and
 * delegates lifecycle events to appropriate handlers.
 */
@Mod(Common.MOD_ID)
public class LovelyTribute {

    /**
     * Forge mod constructor - entry point for Forge mod loading.
     * <p>
     * <b>Initialization Strategy:</b> Sets up event listeners and delegates
     * actual initialization to lifecycle events for proper timing.
     */
    public LovelyTribute() {
        Common.LOGGER.info("Constructing Lovely Tribute for Forge");
        
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register lifecycle event handlers
        modEventBus.addListener(this::onCommonSetup);
        
        // Register client setup only on client side
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(this::onClientSetup);
        }
        
        // Register with Forge event bus for game events
        MinecraftForge.EVENT_BUS.register(this);
        
        Common.LOGGER.info("Lovely Tribute Forge constructor complete");
    }

    /**
     * Handles Forge common setup lifecycle event.
     * <p>
     * <b>Setup Phase:</b> Called during FMLCommonSetupEvent to initialize
     * mod functionality that's available on both client and server.
     * 
     * @param event FML common setup event
     */
    private void onCommonSetup(final FMLCommonSetupEvent event) {
        Common.LOGGER.info("Lovely Tribute Forge common setup starting");
        
        // Initialize common functionality
        Common.init();
        
        // TODO: Forge-specific common setup
        // Register Forge-specific features here
        
        Common.LOGGER.info("Lovely Tribute Forge common setup complete");
    }

    /**
     * Handles Forge client setup lifecycle event.
     * <p>
     * <b>Client Setup:</b> Called during FMLClientSetupEvent to initialize
     * client-only functionality such as rendering and input handling.
     * 
     * @param event FML client setup event
     */
    private void onClientSetup(final FMLClientSetupEvent event) {
        Common.LOGGER.info("Lovely Tribute Forge client setup starting");
        
        // TODO: Initialize client-side functionality
        // Common.initClient();
        
        // TODO: Forge-specific client setup
        // Register client-side features here
        
        Common.LOGGER.info("Lovely Tribute Forge client setup complete");
    }

} // Class: LovelyTribute