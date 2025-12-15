package net.heriazone.templatemod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.api.ClientModInitializer;

/**
 * <p>Fabric-specific initialization for the Template mod.</p>
 * <p>
 * <b>Loader Integration:</b> Implements Fabric's ModInitializer interface
 * to bootstrap the mod within the Fabric ecosystem while delegating
 * core functionality to the common module.
 * <p>
 * <b>Template Usage:</b> Customize this class to add Fabric-specific features.
 */
public class TemplateMod implements ModInitializer, ClientModInitializer {

    /**
     * Fabric mod initialization entry point.
     * <p>
     * <b>Initialization Order:</b> Called by Fabric loader during mod loading phase.
     * Delegates to common initialization while handling Fabric-specific setup.
     */
    @Override
    public void onInitialize() {
        Template.LOGGER.info("Initializing Template Mod for Fabric");
        
        // Initialize common functionality
        Template.init();
        
        // TODO: Fabric-specific initialization
        // Register Fabric-specific features here

        Template.LOGGER.info("Template Mod Fabric initialization complete");
    } // onInitialize ()

    /**
     * Fabric client-side initialization entry point.
     * <p>
     * <b>Client Setup:</b> Handles client-only initialization such as
     * rendering registration and input handling setup.
     */
    @Override
    public void onInitializeClient() {
        Template.LOGGER.info("Initializing Template Mod client for Fabric");
        
        // Initialize client-side functionality
        Template.initClient();
        
        // TODO: Fabric-specific client initialization
        // Register client-side features here

        Template.LOGGER.info("Template Mod Fabric client initialization complete");
    } // onInitializeClient ()

} // Class: TemplateMod