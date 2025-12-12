package net.msymbios.tlovelyr;

import net.msymbios.tlovelyr.TributeMain;
import net.fabricmc.api.ModInitializer;

/**
 * <p>Fabric entry point for Lovely Tribute mod providing loader-specific initialization.</p>
 * <p>
 * <b>Architecture:</b> Serves as the bridge between Fabric's ModInitializer system
 * and the mod's common initialization logic. Handles Fabric-specific setup while
 * delegating shared logic to the common Common class.
 * <p>
 * <b>Initialization Flow:</b> Fabric calls onInitialize() during mod loading,
 * which triggers common mod setup and any Fabric-specific configuration.
 */
public class Tribute implements ModInitializer {

    /**
     * Fabric mod initialization entry point.
     * <p>
     * <b>Execution Context:</b> Called during Fabric's mod loading phase on the main thread.
     * All Fabric-specific setup and common mod initialization occurs here.
     */
    @Override
    public void onInitialize() {
        TributeMain.LOGGER.info("Lovely Tribute {} initializing for Fabric", TributeMain.getVersion());
        
        // Initialize common mod functionality
        TributeMain.init();
        
        TributeMain.LOGGER.info("Lovely Tribute initialization complete");
    }

} // Class: Tribute