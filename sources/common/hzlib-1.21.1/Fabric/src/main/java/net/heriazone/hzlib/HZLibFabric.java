package net.heriazone.hzlib;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>HZ Lib Fabric entry point providing foundation utilities for Heria Zone ecosystem.</p>
 * <p>
 * <b>Architecture:</b> Serves as the Fabric-specific initialization point for HZ Lib,
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
public class HZLibFabric implements ModInitializer {
    
    // -- Constants --
    public static final String MOD_ID = "hzlib";
    public static final Logger LOGGER = LoggerFactory.getLogger("HZ Lib");
    
    // -- ModInitializer Implementation --
    
    @Override
    public void onInitialize() {
        LOGGER.info("HZ Lib {} initializing for Fabric", getClass().getPackage().getImplementationVersion());
        
        // Initialize core library services
        initializeLibraryServices();
        
        LOGGER.info("HZ Lib initialization complete");
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
    
} // Class: HZLibFabric