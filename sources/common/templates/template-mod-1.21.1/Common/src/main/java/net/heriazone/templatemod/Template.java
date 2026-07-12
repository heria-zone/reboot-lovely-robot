package net.heriazone.templatemod;

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
public class Template {

    // -- Constants --
    
    /**
     * Mod identifier used across all loaders and configurations.
     */
    public static final String MOD_ID = "templatemod";
    
    /**
     * Mod display name for user-facing contexts.
     */
    public static final String MOD_NAME = "Template Mod";

    /**
     * Mod version for runtime identification and compatibility checks.
     */
    public static final String VERSION = "1.0.0-dev";

    /**
     * Logger instance for common functionality.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    // -- Initialization State --

    private static boolean initialized = false;

    // -- Custom Methods --
    
    /**
     * Common initialization logic called by all loader-specific entry points.
     * <p>
     * <b>Lifecycle:</b> Invoked during mod construction phase by each loader's
     * main class, ensuring consistent initialization across all platforms.
     * <p>
     * <b>Thread Safety:</b> Safe to call multiple times - initialization is idempotent.
     */
    public static void init() {
        if (initialized) return;

        LOGGER.info("Initializing {} - Template for quick Minecraft mod setup", MOD_NAME);

        // TODO: Add common initialization logic here
        // - Register common content
        // - Set up cross-loader compatibility
        // - Initialize shared systems

        initialized = true;
        LOGGER.info("{} initialization complete", MOD_NAME);
    } // init ()

    /**
     * Performs client-side initialization.
     * <p>
     * <b>Client Integration:</b> Sets up client-specific features
     * such as rendering and input handling.
     */
    public static void initClient() {
        LOGGER.info("Initializing {} client-side features", MOD_NAME);

        // TODO: Initialize client-side features

        LOGGER.info("{} client initialization complete", MOD_NAME);
    } // initClient ()

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