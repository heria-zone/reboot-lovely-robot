package net.heriazone.tlovelyr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Main Lovely Tribute mod class providing common initialization and shared functionality.</p>
 * <p>
 * <b>Architecture:</b> Serves as the central coordination point for Lovely Tribute mod functionality
 * across all supported mod loaders. Contains shared initialization logic and provides
 * common utilities that work consistently on Fabric, Forge, and NeoForge.
 * <p>
 * <b>Design Pattern:</b> Singleton-style initialization with loader-agnostic design.
 * Each loader calls init() during their respective initialization phases.
 */
public class Common {

    // -- Constants --
    
    /**
     * Mod identifier used across all loaders and configurations.
     */
    public static final String MOD_ID = "tlovelyr";
    
    /**
     * Mod version for runtime identification and compatibility checks.
     */
    public static final String VERSION = "1.0.0";
    
    /**
     * Shared logger instance for consistent logging across all loaders.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger("Lovely Tribute");

    // -- Initialization State --
    
    private static boolean initialized = false;

    // -- Public Methods --

    /**
     * Initialize Lovely Tribute mod functionality across all supported loaders.
     * <p>
     * <b>Thread Safety:</b> Safe to call multiple times, initialization occurs only once.
     * <b>Timing:</b> Called during each loader's initialization phase.
     * <p>
     * <b>Initialization Sequence:</b>
     * 1. Validate initialization state
     * 2. Initialize LovelyLib dependency
     * 3. Register robot entities and components
     * 4. Set up tribute-specific functionality
     * 5. Mark as initialized
     */
    public static void init() {
        if (initialized) {
            LOGGER.warn("Lovely Tribute already initialized, skipping duplicate initialization");
            return;
        }

        LOGGER.info("Initializing Lovely Tribute mod version {}", VERSION);

        // TODO: Add LovelyLib initialization call
        // LovelyLibrary.initialize();
        
        // TODO: Register robot entities (Vanilla, Honey, Bunny, Bunny2)
        // TODO: Set up tribute-specific behavior
        // TODO: Initialize robot AI systems

        initialized = true;
        LOGGER.info("Lovely Tribute mod initialization complete");
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

} // Class: Common