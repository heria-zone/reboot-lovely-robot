package net.heriazone.hzlib.api.services;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

/**
 * Forge implementation of platform services.
 * <p>
 * <b>Architecture:</b> Implements IPlatformServices interface using Forge-specific
 * APIs (ModList, FMLLoader, FMLPaths). Provides access to Forge's mod loading
 * system, configuration directory, and development environment detection.
 * <p>
 * <b>Forge APIs:</b> Uses multiple Forge service classes for different
 * functionality. ModList for mod queries, FMLPaths for directories,
 * FMLLoader for environment detection.
 * <p>
 * <b>Initialization:</b> Registered during Forge mod initialization in
 * LovelyLegacy constructor before deferred register setup.
 */
public class ForgeServices implements IPlatformServices {

    // -- Mod Loading Detection --

    /**
     * Checks if a mod is loaded using Forge's ModList system.
     * <p>
     * <b>Forge Implementation:</b> Uses ModList.get().isLoaded() which
     * queries the mod container registry. Forge loads mods during
     * construction and registration phases.
     * <p>
     * <b>Performance:</b> Forge caches mod loading state in ModList,
     * making this a fast O(1) lookup operation.
     */
    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    } // isModLoaded()

    // -- Configuration Management --

    /**
     * Gets Forge's configuration directory path.
     * <p>
     * <b>Forge Implementation:</b> Uses FMLPaths.CONFIGDIR.get() which
     * returns the standard config directory (game_directory/config/).
     * <p>
     * <b>Directory Management:</b> Forge ensures the config directory
     * exists and is writable before returning the path.
     */
    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    } // getConfigDirectory()

    // -- Development Environment Detection --

    /**
     * Detects Forge development environment.
     * <p>
     * <b>Forge Implementation:</b> Uses FMLLoader.isProduction() inverted
     * to detect development mode. Forge distinguishes between production
     * builds and development/IDE environments.
     * <p>
     * <b>Detection Method:</b> Forge checks for production markers like
     * obfuscated code, distribution packaging, and runtime environment.
     */
    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    } // isDevelopmentEnvironment()

    // -- Platform Information --

    /**
     * Returns "Forge" as the platform identifier.
     * <p>
     * <b>Consistency:</b> Provides standard platform name for logging,
     * debugging, and platform-specific feature detection across the mod.
     */
    @Override
    public String getPlatformName() {
        return "Forge";
    } // getPlatformName()

} // Class: ForgeServices