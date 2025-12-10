package net.msymbios.llovelyr.lib.services;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

/**
 * NeoForge implementation of platform services.
 * <p>
 * <b>Architecture:</b> Implements IPlatformServices interface using NeoForge-specific
 * APIs (ModList, FMLLoader, FMLPaths). Provides access to NeoForge's mod loading
 * system, configuration directory, and development environment detection.
 * <p>
 * <b>NeoForge APIs:</b> Uses NeoForge service classes which are similar to
 * Forge but in different packages (net.neoforged vs net.minecraftforge).
 * Functionality is largely equivalent to Forge implementation.
 * <p>
 * <b>Initialization:</b> Registered during NeoForge mod initialization in
 * LovelyLegacy constructor before deferred register setup.
 */
public class NeoForgeServices implements IPlatformServices {

    // -- Mod Loading Detection --

    /**
     * Checks if a mod is loaded using NeoForge's ModList system.
     * <p>
     * <b>NeoForge Implementation:</b> Uses ModList.get().isLoaded() which
     * queries the mod container registry. NeoForge loads mods during
     * construction and registration phases.
     * <p>
     * <b>Performance:</b> NeoForge caches mod loading state in ModList,
     * making this a fast O(1) lookup operation.
     */
    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    } // isModLoaded()

    // -- Configuration Management --

    /**
     * Gets NeoForge's configuration directory path.
     * <p>
     * <b>NeoForge Implementation:</b> Uses FMLPaths.CONFIGDIR.get() which
     * returns the standard config directory (game_directory/config/).
     * <p>
     * <b>Directory Management:</b> NeoForge ensures the config directory
     * exists and is writable before returning the path.
     */
    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    } // getConfigDirectory()

    // -- Development Environment Detection --

    /**
     * Detects NeoForge development environment.
     * <p>
     * <b>NeoForge Implementation:</b> Uses FMLLoader.isProduction() inverted
     * to detect development mode. NeoForge distinguishes between production
     * builds and development/IDE environments.
     * <p>
     * <b>Detection Method:</b> NeoForge checks for production markers like
     * obfuscated code, distribution packaging, and runtime environment.
     */
    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    } // isDevelopmentEnvironment()

    // -- Platform Information --

    /**
     * Returns "NeoForge" as the platform identifier.
     * <p>
     * <b>Consistency:</b> Provides standard platform name for logging,
     * debugging, and platform-specific feature detection across the mod.
     */
    @Override
    public String getPlatformName() {
        return "NeoForge";
    } // getPlatformName()

} // Class: NeoForgeServices