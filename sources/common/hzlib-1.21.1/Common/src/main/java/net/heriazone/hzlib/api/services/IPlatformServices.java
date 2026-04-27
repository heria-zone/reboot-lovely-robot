package net.heriazone.hzlib.api.services;

import java.nio.file.Path;

/**
 * Platform abstraction interface for loader-specific functionality.
 * <p>
 * <b>Architecture:</b> Provides common interface for platform-specific operations
 * that vary between Fabric, Forge, and NeoForge. Enables common code to access
 * platform services without direct loader dependencies.
 * <p>
 * <b>Service Locator Pattern:</b> Implementations are registered during mod
 * initialization and accessed through Services.get(). Each loader provides
 * its own implementation with platform-specific behavior.
 * <p>
 * <b>Design Decision:</b> Minimal interface focused on essential platform
 * differences. Avoids over-abstraction while enabling common code to function
 * across all three loaders.
 */
public interface IPlatformServices {

    // -- Mod Loading Detection --

    /**
     * Checks if a mod with the specified ID is loaded.
     * <p>
     * <b>Platform Differences:</b> Each loader has different mod loading APIs
     * (FabricLoader, ModList, NeoForgeModList). This abstracts the detection
     * mechanism while providing consistent behavior.
     * <p>
     * <b>Use Cases:</b> Optional mod integration, feature gating based on
     * mod presence, compatibility checks during initialization.
     *
     * @param modId the mod identifier to check
     * @return true if the mod is loaded, false otherwise
     */
    boolean isModLoaded(String modId);

    // -- Configuration Management --

    /**
     * Gets the platform-specific configuration directory path.
     * <p>
     * <b>Platform Differences:</b> Configuration systems vary between loaders.
     * Fabric uses custom config libraries, Forge/NeoForge use ForgeConfigSpec.
     * This provides unified access to the config directory.
     * <p>
     * <b>File Location:</b> Returns the directory where mod configuration files
     * should be stored, typically game_directory/config/.
     *
     * @return path to the configuration directory
     */
    Path getConfigDirectory();

    // -- Development Environment Detection --

    /**
     * Determines if the mod is running in a development environment.
     * <p>
     * <b>Development Features:</b> Enables debug logging, development-only
     * features, and testing utilities when running in IDE or development builds.
     * <p>
     * <b>Platform Detection:</b> Each loader has different ways to detect
     * development mode. This abstracts the detection mechanism.
     *
     * @return true if running in development environment, false in production
     */
    boolean isDevelopmentEnvironment();

    // -- Platform Information --

    /**
     * Gets the name of the current mod loader platform.
     * <p>
     * <b>Identification:</b> Returns human-readable platform name for logging,
     * debugging, and platform-specific feature detection.
     * <p>
     * <b>Standard Names:</b> "Fabric", "Forge", "NeoForge" for consistency
     * across logging and error reporting.
     *
     * @return the platform name (e.g., "Fabric", "Forge", "NeoForge")
     */
    String getPlatformName();

} // Interface: IPlatformServices