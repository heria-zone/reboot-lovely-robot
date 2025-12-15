package net.heriazone.templatelib;

/**
 * <p>Main library class providing core functionality and initialization.</p>
 * <p>
 * <b>Architecture:</b> Serves as the entry point for library functionality across
 * all supported mod loaders. Provides a consistent API that works on Fabric, Forge,
 * and NeoForge platforms.
 * <p>
 * <b>Usage:</b> Initialize the library by calling {@link #initialize()} during your
 * mod's initialization phase. Access library features through the static methods
 * provided by this class.
 * 
 * @author MSymbios
 * @version 1.0.0
 * @since 1.21.1
 */
public class TemplateLibrary {

    // -- Constants --
    
    /**
     * Library version for runtime identification and compatibility checks.
     */
    public static final String VERSION = "1.0.0";
    
    /**
     * Library identifier used across all loaders and configurations.
     */
    public static final String LIBRARY_ID = "templatelib";

    // -- Initialization State --
    
    private static boolean initialized = false;

    // -- Public Methods --

    /**
     * Initialize the library functionality across all supported loaders.
     * <p>
     * <b>Thread Safety:</b> Safe to call multiple times, initialization occurs only once.
     * <b>Timing:</b> Should be called during mod initialization phase.
     * <p>
     * <b>Initialization Sequence:</b>
     * 1. Validate initialization state
     * 2. Set up library components
     * 3. Register cross-loader functionality
     * 4. Mark as initialized
     */
    public static void initialize() {
        if (initialized) {
            return;
        }

        // TODO: Add your library initialization logic here
        // Examples:
        // - Register library components
        // - Set up configuration
        // - Initialize systems
        // - Register event handlers

        initialized = true;
    }

    /**
     * Get the current library version for runtime checks and logging.
     * 
     * @return library version string
     */
    public static String getVersion() {
        return VERSION;
    }

    /**
     * Check if the library has been initialized.
     * <p>
     * <b>Usage:</b> Useful for conditional logic that depends on initialization state.
     * 
     * @return true if initialize() has been called successfully
     */
    public static boolean isInitialized() {
        return initialized;
    }

    /**
     * Get the library identifier.
     * 
     * @return library identifier string
     */
    public static String getLibraryId() {
        return LIBRARY_ID;
    }

} // Class: TemplateLibrary