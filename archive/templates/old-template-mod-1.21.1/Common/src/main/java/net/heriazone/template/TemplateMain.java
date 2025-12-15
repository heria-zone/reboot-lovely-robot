package net.heriazone.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Main Template mod class providing common initialization and shared functionality.</p>
 * <p>
 * <b>Architecture:</b> Serves as the central coordination point for Template mod functionality
 * across all supported mod loaders. Contains shared initialization logic and provides
 * common utilities that work consistently on Fabric, Forge, and NeoForge.
 * <p>
 * <b>Design Pattern:</b> Singleton-style initialization with loader-agnostic design.
 * Each loader calls init() during their respective initialization phases.
 */
public class TemplateMain {

    // -- Constants --
    
    /**
     * Mod identifier used across all loaders and configurations.
     */
    public static final String MOD_ID = "template";
    
    /**
     * Mod version for runtime identification and compatibility checks.
     */
    public static final String VERSION = "1.0.0";
    
    /**
     * Shared logger instance for consistent logging across all loaders.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger("Template");

    // -- Initialization State --
    
    private static boolean initialized = false;

    // -- Public Methods --

    /**
     * Initialize Template mod functionality across all supported loaders.
     * <p>
     * <b>Thread Safety:</b> Safe to call multiple times, initialization occurs only once.
     * <b>Timing:</b> Called during each loader's initialization phase.
     * <p>
     * <b>Initialization Sequence:</b>
     * 1. Validate initialization state
     * 2. Register mod components
     * 3. Set up cross-loader functionality
     * 4. Mark as initialized
     */
    public static void init() {
        if (initialized) {
            LOGGER.warn("Template already initialized, skipping duplicate initialization");
            return;
        }

        LOGGER.info("Initializing Template mod version {}", VERSION);

        // TODO: Add your mod initialization logic here
        // Examples:
        // - Register items, blocks, entities
        // - Set up configuration
        // - Initialize systems
        // - Register event handlers

        initialized = true;
        LOGGER.info("Template mod initialization complete");
    }

    /**
     * Get the current mod version for runtime checks and logging.
     * 
     * @return mod version string
     */
    public static String getVersion() {
        return VERSION;
    }

    /**
     * Check if the mod has been initialized.
     * <p>
     * <b>Usage:</b> Useful for conditional logic that depends on initialization state.
     * 
     * @return true if init() has been called successfully
     */
    public static boolean isInitialized() {
        return initialized;
    }

} // Class: Template