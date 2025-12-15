package net.msymbios.llovelyr.lib.services;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

/**
 * Fabric implementation of platform services.
 * <p>
 * <b>Architecture:</b> Implements IPlatformServices interface using Fabric-specific
 * APIs (FabricLoader). Provides access to Fabric's mod loading system,
 * configuration directory, and development environment detection.
 * <p>
 * <b>Fabric APIs:</b> Uses FabricLoader.getInstance() for all platform queries.
 * Fabric provides unified API through single loader instance rather than
 * separate service classes.
 * <p>
 * <b>Initialization:</b> Registered during Fabric mod initialization in
 * LovelyLegacy.onInitialize() before any common code runs.
 */
public class FabricServices implements IPlatformServices {

    // -- Mod Loading Detection --

    /**
     * Checks if a mod is loaded using Fabric's mod container system.
     * <p>
     * <b>Fabric Implementation:</b> Uses FabricLoader.isModLoaded() which
     * queries the mod container registry. Fabric loads all mods during
     * initialization phase.
     * <p>
     * <b>Performance:</b> Fabric caches mod loading state, so this is
     * a fast O(1) lookup operation.
     */
    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    } // isModLoaded()

    // -- Configuration Management --

    /**
     * Gets Fabric's configuration directory path.
     * <p>
     * <b>Fabric Implementation:</b> Uses FabricLoader.getConfigDir() which
     * returns the standard config directory (game_directory/config/).
     * <p>
     * <b>Directory Creation:</b> Fabric ensures the config directory exists
     * before returning the path.
     */
    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    } // getConfigDirectory()

    // -- Development Environment Detection --

    /**
     * Detects Fabric development environment.
     * <p>
     * <b>Fabric Implementation:</b> Uses FabricLoader.isDevelopmentEnvironment()
     * which detects IDE runs, development builds, and fabric-installer setups.
     * <p>
     * <b>Detection Method:</b> Fabric checks for development-specific markers
     * like source directories, development dependencies, and IDE integration.
     */
    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    } // isDevelopmentEnvironment()

    // -- Platform Information --

    /**
     * Returns "Fabric" as the platform identifier.
     * <p>
     * <b>Consistency:</b> Provides standard platform name for logging,
     * debugging, and platform-specific feature detection across the mod.
     */
    @Override
    public String getPlatformName() {
        return "Fabric";
    } // getPlatformName()

} // Class: FabricServices