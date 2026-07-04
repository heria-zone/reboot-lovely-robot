package net.heriazone.lovelylib;

import net.heriazone.hzlib.api.services.NeoForgeServices;
import net.heriazone.hzlib.api.services.Services;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Lovely Lib NeoForge entry point providing robot entity management and AI systems.</p>
 * <p>
 * <b>Architecture:</b> Serves as the NeoForge-specific initialization point for Lovely Lib,
 * establishing robot functionality and AI systems that robot mods can depend upon.
 * <p>
 * <b>Design Intent:</b> Provides robot-specific utilities and enhanced functionality
 * for the LovelyRobot mod ecosystem, maintaining clean separation between core library
 * services and loader-specific implementations.
 * 
 * @version 1.0.0
 * @since 1.21.1
 * @author MSymbios
 */
@Mod("lovelylib")
public class LovelyLib {
    
    // -- Constants --
    
    /**
     * NeoForge-specific logger for loader-specific operations.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger("Lovely Lib [NeoForge]");
    
    // -- Constructor --
    
    public LovelyLib(IEventBus modEventBus) {
        LOGGER.info("Lovely Lib {} initializing for NeoForge", Lovely.VERSION);

        // Register platform services — done here once so all dependent mods
        // (llovelyr, tlovelyr, rlovelyr) share the same singleton regardless
        // of how many are loaded together.
        Services.setInstance(new NeoForgeServices());

        // Initialize the core library at constructor time, before any
        // DeferredRegister supplier in dependent mods fires.
        Lovely.initialize();

        // Register setup event for loader-specific features
        modEventBus.addListener(this::onCommonSetup);

        LOGGER.info("Lovely Lib NeoForge constructor complete");
    }
    
    // -- Event Handlers --
    
    /**
     * Handles common setup phase for library initialization.
     * <p>
     * <b>Design Intent:</b> Establishes NeoForge-specific robot services and integrations
     * that complement the core library functionality.
     * 
     * @param event the common setup event
     */
    private void onCommonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Lovely Lib common setup phase starting");

        event.enqueueWork(this::initializeNeoForgeFeatures);

        LOGGER.info("Lovely Lib common setup phase complete");
    }
    
    // -- Private Methods --
    
    /**
     * Initialize NeoForge-specific features and integrations.
     * <p>
     * <b>Design Intent:</b> Establishes NeoForge-specific robot services and integrations
     * that complement the core library functionality.
     */
    private void initializeNeoForgeFeatures() {
        // TODO: Initialize NeoForge-specific robot entity registration
        // TODO: Set up NeoForge-specific AI behavior integrations
        // TODO: Register NeoForge-specific robot management features
        
        LOGGER.debug("NeoForge-specific features initialized");
    }
    

    
} // Class: LovelyLib