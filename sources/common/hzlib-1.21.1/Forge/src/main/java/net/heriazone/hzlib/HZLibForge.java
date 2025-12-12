package net.heriazone.hzlib;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>HZ Lib Forge entry point providing foundation utilities for Heria Zone ecosystem.</p>
 * <p>
 * <b>Architecture:</b> Serves as the Forge-specific initialization point for HZ Lib,
 * establishing core services and utilities that other mods can depend upon.
 * <p>
 * <b>Design Intent:</b> Minimal initialization focused on setting up library infrastructure
 * without game-specific functionality, maintaining clean separation between library
 * utilities and mod-specific features.
 * 
 * @version 1.0.0
 * @since 1.21.1
 * @author MSymbios
 */
@Mod("hzlib")
public class HZLibForge {
    
    // -- Constants --
    public static final String MOD_ID = "hzlib";
    public static final Logger LOGGER = LoggerFactory.getLogger("HZ Lib");
    
    // -- Constructor --
    
    public HZLibForge() {
        LOGGER.info("HZ Lib {} initializing for Forge", getClass().getPackage().getImplementationVersion());
        
        // Get the mod event bus
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register setup event
        modEventBus.addListener(this::onCommonSetup);
        
        LOGGER.info("HZ Lib Forge constructor complete");
    }
    
    // -- Event Handlers --
    
    /**
     * Handles common setup phase for library initialization.
     * <p>
     * <b>Design Intent:</b> Establishes foundation services that other mods
     * can rely upon, maintaining minimal footprint and clean initialization.
     * 
     * @param event the common setup event
     */
    private void onCommonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("HZ Lib common setup phase starting");
        
        event.enqueueWork(() -> {
            // Initialize core library services
            initializeLibraryServices();
        });
        
        LOGGER.info("HZ Lib common setup phase complete");
    }
    
    // -- Private Methods --
    
    /**
     * Initializes core library services and utilities.
     * <p>
     * <b>Design Intent:</b> Establishes foundation services that other mods
     * can rely upon, maintaining minimal footprint and clean initialization.
     */
    private void initializeLibraryServices() {
        // Library initialization will be implemented in future sprints
        // This provides the foundation structure for HZ Lib services
        LOGGER.debug("Core library services initialized");
    }
    
} // Class: HZLibForge