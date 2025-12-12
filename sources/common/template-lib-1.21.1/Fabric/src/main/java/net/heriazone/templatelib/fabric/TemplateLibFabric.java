package net.heriazone.templatelib.fabric;

import net.heriazone.templatelib.TemplateLibrary;
import net.fabricmc.api.ModInitializer;

/**
 * <p>Fabric-specific initialization for Template Library.</p>
 * <p>
 * <b>Architecture:</b> Serves as the Fabric mod loader entry point, handling
 * Fabric-specific initialization and integration with the common library code.
 * <p>
 * <b>Initialization Flow:</b> Called by Fabric Loader during mod initialization,
 * then delegates to the common library initialization.
 */
public class TemplateLibFabric implements ModInitializer {

    /**
     * Initialize the library for Fabric.
     * <p>
     * <b>Timing:</b> Called during Fabric's mod initialization phase.
     * <b>Thread Context:</b> Runs on the main thread during game startup.
     */
    @Override
    public void onInitialize() {
        // Initialize the common library
        TemplateLibrary.initialize();
    }

} // Class: TemplateLibFabric