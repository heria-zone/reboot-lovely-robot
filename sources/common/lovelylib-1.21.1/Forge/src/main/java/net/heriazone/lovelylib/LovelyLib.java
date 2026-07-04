package net.heriazone.lovelylib;

import net.heriazone.hzlib.api.services.ForgeServices;
import net.heriazone.hzlib.api.services.Services;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Lovely Lib Forge entry point providing robot entity management and AI systems.</p>
 * <p>
 * <b>Architecture:</b> Serves as the Forge-specific initialization point for Lovely Lib,
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
     * Forge-specific logger for loader-specific operations.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger("Lovely Lib [Forge]");
    
    // -- Constructor --
    
    public LovelyLib() {
        LOGGER.info("Lovely Lib {} initializing for Forge", Lovely.VERSION);

        // Register platform services — done here once so all dependent mods
        // (llovelyr, tlovelyr, rlovelyr) share the same singleton regardless
        // of how many are loaded together.
        Services.setInstance(new ForgeServices());

        // Initialize the core library at constructor time, before any
        // DeferredRegister supplier in dependent mods fires.
        Lovely.initialize();

        // Get the mod event bus and register setup event for loader-specific features
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onCommonSetup);

        LOGGER.info("Lovely Lib Forge constructor complete");
    }
    
    // -- Event Handlers --
    
    /**
     * Handles common setup phase for library initialization.
     * <p>
     * <b>Design Intent:</b> Establishes Forge-specific robot services and integrations
     * that complement the core library functionality.
     * 
     * @param event the common setup event
     */
    private void onCommonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Lovely Lib common setup phase starting");

        event.enqueueWork(this::initializeForgeFeatures);

        LOGGER.info("Lovely Lib common setup phase complete");
    }
    
    // -- Private Methods --
    
    /**
     * Initialize Forge-specific features and integrations.
     * <p>
     * <b>Design Intent:</b> Establishes Forge-specific robot services and integrations
     * that complement the core library functionality.
     */
    private void initializeForgeFeatures() {
        // TODO: Initialize Forge-specific robot entity registration
        // TODO: Set up Forge-specific AI behavior integrations
        // TODO: Register Forge-specific robot management features
        
        LOGGER.debug("Forge-specific features initialized");
    }
    

    
} // Class: LovelyLib