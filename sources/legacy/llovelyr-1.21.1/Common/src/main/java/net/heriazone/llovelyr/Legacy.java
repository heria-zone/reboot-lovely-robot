package net.heriazone.llovelyr;

import net.heriazone.lovelylib.Lovely;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Main Lovely Legacy mod class providing common initialization and shared functionality.</p>
 * <p>
 * <b>Architecture:</b> Serves as the central coordination point for Lovely Legacy mod functionality
 * across all supported mod loaders. Contains shared initialization logic and provides
 * common utilities that work consistently on Fabric, Forge, and NeoForge.
 * <p>
 * <b>Design Pattern:</b> Singleton-style initialization with loader-agnostic design.
 * Each loader calls init() during their respective initialization phases.
 */
public class Legacy {

    // -- Constants --
    
    /**
     * Mod identifier used across all loaders and configurations.
     */
    public static final String MODID = "llovelyr";
    
    /**
     * Mod display name for user-facing contexts.
     */
    public static final String MOD_NAME = "Lovely Legacy";

    /**
     * Mod version for runtime identification and compatibility checks.
     */
    public static final String VERSION = "1.0.0";

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

        LOGGER.info("Initializing {} - Enhanced reboot with expanded features and 7 robot types", MOD_NAME);

        // Initialize LovelyLib dependency
        Lovely.initialize();

        // TODO: Add common initialization logic here
        // - Register common content (7 robot types: Vanilla, Honey, Bunny, Bunny2, Dragon, Neko, Kitsune)
        // - Set up cross-loader compatibility
        // - Initialize shared systems
        // - Set up 16x color palette system

        initialized = true;
        LOGGER.info("{} initialization complete", MOD_NAME);
    } // init ()

    /**
     * Performs client-side initialization.
     * <p>
     * <b>Client Integration:</b> Sets up client-specific LovelyLib features
     * such as rendering and input handling.
     */
    public static void initClient() {
        LOGGER.info("Initializing {} client-side features", MOD_NAME);

        // TODO: Initialize client-side LovelyLib integration
        // LovelyLib.getInstance().initClient();

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

} // Class: Legacy