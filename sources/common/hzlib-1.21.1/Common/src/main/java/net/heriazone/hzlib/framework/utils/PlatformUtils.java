package net.heriazone.hzlib.framework.utils;

import net.heriazone.hzlib.api.services.Services;

import java.nio.file.Path;

/**
 * Utility class demonstrating platform services usage in common code.
 * <p>
 * <b>Architecture:</b> Shows how common code can access platform-specific
 * functionality through the Services abstraction without direct loader
 * dependencies. Enables cross-platform compatibility.
 * <p>
 * <b>Design Pattern:</b> Static utility methods that delegate to platform
 * services. Provides convenient API for common platform operations while
 * maintaining abstraction layer.
 * <p>
 * <b>Usage Example:</b> Common code can call PlatformUtils.isModLoaded("jei")
 * without knowing whether it's running on Fabric, Forge, or NeoForge.
 */
public class PlatformUtils {

    // -- Mod Integration Utilities --

    /**
     * Checks if JEI (Just Enough Items) mod is loaded.
     * <p>
     * <b>Integration:</b> Enables optional JEI recipe integration when
     * the mod is present. Common code can safely check for JEI without
     * platform-specific dependencies.
     * <p>
     * <b>Mod ID:</b> Uses "jei" which is consistent across all loaders.
     *
     * @return true if JEI is loaded, false otherwise
     */
    public static boolean isJEILoaded() {
        return Services.get().isModLoaded("jei");
    } // isJEILoaded()

    /**
     * Checks if REI (Roughly Enough Items) mod is loaded.
     * <p>
     * <b>Integration:</b> Enables optional REI recipe integration when
     * the mod is present. Alternative to JEI on Fabric.
     * <p>
     * <b>Mod ID:</b> Uses "roughlyenoughitems" which is the standard REI mod ID.
     *
     * @return true if REI is loaded, false otherwise
     */
    public static boolean isREILoaded() {
        return Services.get().isModLoaded("roughlyenoughitems");
    } // isREILoaded()

    /**
     * Checks if any recipe viewing mod is available.
     * <p>
     * <b>Integration:</b> Enables recipe integration features when any
     * compatible recipe viewing mod is present. Covers JEI (Forge/NeoForge)
     * and REI (Fabric) automatically.
     *
     * @return true if JEI or REI is loaded, false otherwise
     */
    public static boolean hasRecipeViewerMod() {
        return isJEILoaded() || isREILoaded();
    } // hasRecipeViewerMod()

    // -- Configuration Utilities --

    /**
     * Gets the full path to the mod's configuration file.
     * <p>
     * <b>Cross-Platform:</b> Works consistently across all loaders by using
     * platform services to get the config directory. Each loader handles
     * config directory location differently.
     * <p>
     * <b>File Location:</b> Returns config/llovelyr.properties on all platforms.
     *
     * @return path to the mod's configuration file
     */
    public static Path getModConfigPath() { // TODO: Review this method to be more generic
        return Services.get().getConfigDirectory().resolve("llovelyr.properties");
    } // getModConfigPath()

    // -- Development Utilities --

    /**
     * Checks if debug features should be enabled.
     * <p>
     * <b>Development Mode:</b> Enables debug logging, development-only features,
     * and testing utilities when running in development environment.
     * <p>
     * <b>Production Safety:</b> Automatically disables debug features in
     * production builds to prevent performance impact and log spam.
     *
     * @return true if debug features should be enabled, false otherwise
     */
    public static boolean isDebugMode() {
        return Services.get().isDevelopmentEnvironment();
    } // isDebugMode()

    /**
     * Gets platform-specific information for logging and debugging.
     * <p>
     * <b>Diagnostics:</b> Provides platform identification for error reports,
     * crash logs, and debugging information. Helps identify platform-specific
     * issues during development and support.
     *
     * @return formatted platform information string
     */
    public static String getPlatformInfo() {
        return String.format("Platform: %s, Development: %s, Config: %s",
                Services.get().getPlatformName(),
                Services.get().isDevelopmentEnvironment(),
                Services.get().getConfigDirectory()
        );
    } // getPlatformInfo()

    // -- Validation Utilities --

    /**
     * Validates that platform services are properly initialized.
     * <p>
     * <b>Initialization Check:</b> Ensures platform services are registered
     * before common code attempts to use them. Useful for early validation
     * during mod startup.
     * <p>
     * <b>Error Prevention:</b> Prevents runtime errors from uninitialized
     * services by providing early detection of initialization problems.
     *
     * @throws IllegalStateException if platform services aren't initialized
     */
    public static void validatePlatformServices() {
        if (!Services.isRegistered()) {
            throw new IllegalStateException(
                    "Platform services not initialized. " +
                            "This indicates a mod initialization order problem."
            );
        }
    } // validatePlatformServices()

} // Class: PlatformUtils