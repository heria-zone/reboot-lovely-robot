package net.heriazone.rlovelyr;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.api.ClientModInitializer;

/**
 * <p>Fabric-specific initialization for the Lovely Reboot mod.</p>
 * <p>
 * <b>Loader Integration:</b> Implements Fabric's ModInitializer interface
 * to bootstrap the mod within the Fabric ecosystem while delegating
 * core functionality to the common module.
 * <p>
 * <b>LovelyLib Coordination:</b> Ensures proper initialization order
 * with LovelyLib Fabric components.
 */
public class LovelyReboot implements ModInitializer, ClientModInitializer {

    /**
     * Fabric mod initialization entry point.
     * <p>
     * <b>Initialization Order:</b> Called by Fabric loader during mod loading phase.
     * Delegates to common initialization while handling Fabric-specific setup.
     */
    @Override
    public void onInitialize() {
        Reboot.LOGGER.info("Initializing Lovely Reboot for Fabric");
        
        // Initialize common functionality
        Reboot.init();
        
        // TODO: Fabric-specific initialization
        // Register Fabric-specific features here

        Reboot.LOGGER.info("Lovely Reboot Fabric initialization complete");
    } // onInitialize ()

    /**
     * Fabric client-side initialization entry point.
     * <p>
     * <b>Client Setup:</b> Handles client-only initialization such as
     * rendering registration and input handling setup.
     */
    @Override
    public void onInitializeClient() {
        Reboot.LOGGER.info("Initializing Lovely Reboot client for Fabric");
        
        // TODO: Initialize client-side functionality
        // Common.initClient();
        
        // TODO: Fabric-specific client initialization
        // Register client-side features here

        Reboot.LOGGER.info("Lovely Reboot Fabric client initialization complete");
    } // onInitializeClient ()

} // Class: LovelyReboot