package net.heriazone.llovelyr;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * <p>Forge-specific initialization and event handling for the Lovely Legacy mod.</p>
 * <p>
 * <b>Forge Integration:</b> Implements Forge's mod loading lifecycle through
 * the @Mod annotation and FML event system. Coordinates with common module
 * while handling Forge-specific registration and event management.
 * <p>
 * <b>Event Coordination:</b> Manages Forge event bus registration and
 * delegates lifecycle events to appropriate handlers.
 */
@Mod(Legacy.MOD_ID)
public class LovelyLegacy {

    /**
     * Forge mod constructor - entry point for Forge mod loading.
     * <p>
     * <b>Initialization Strategy:</b> Sets up event listeners and delegates
     * actual initialization to lifecycle events for proper timing.
     */
    public LovelyLegacy() {
        Legacy.LOGGER.info("Constructing Lovely Legacy for Forge");
        
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register lifecycle event handlers
        modEventBus.addListener(this::onCommonSetup);
        
        // Register client setup only on client side
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(this::onClientSetup);
        }
        
        // Register with Forge event bus for game events
        MinecraftForge.EVENT_BUS.register(this);

        Legacy.LOGGER.info("Lovely Legacy Forge constructor complete");
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
        Legacy.LOGGER.info("Lovely Legacy Forge common setup starting");
        
        // Initialize common functionality
        Legacy.init();
        
        // TODO: Forge-specific common setup
        // Register Forge-specific features here

        Legacy.LOGGER.info("Lovely Legacy Forge common setup complete");
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
        Legacy.LOGGER.info("Lovely Legacy Forge client setup starting");
        
        // Initialize client-side functionality
        Legacy.initClient();
        
        // TODO: Forge-specific client setup
        // Register client-side features here

        Legacy.LOGGER.info("Lovely Legacy Forge client setup complete");
    }

} // Class: LovelyLegacy