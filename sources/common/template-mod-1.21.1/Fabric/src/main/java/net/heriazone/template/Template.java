package net.heriazone.template;

import net.fabricmc.api.ModInitializer;
/**
 * <p>Fabric entry point for ${mod_name} mod providing loader-specific initialization.</p>
 * <p>
 * <b>Architecture:</b> Serves as the bridge between Fabric's ModInitializer system
 * and the mod's common initialization logic. Handles Fabric-specific setup while
 * delegating shared logic to the common TemplateMain class.
 * <p>
 * <b>Initialization Flow:</b> Fabric calls onInitialize() during mod loading,
 * which triggers common mod setup and any Fabric-specific configuration.
 */
public class Template implements ModInitializer {

    /**
     * Fabric mod initialization entry point.
     * <p>
     * <b>Execution Context:</b> Called during Fabric's mod loading phase on the main thread.
     * All Fabric-specific setup and common mod initialization occurs here.
     */
    @Override
    public void onInitialize() {
        TemplateMain.LOGGER.info("${mod_name} {} initializing for Fabric", TemplateMain.getVersion());
        
        // Initialize common mod functionality
        TemplateMain.init();
        
        TemplateMain.LOGGER.info("${mod_name} initialization complete");
    }

} // Class: Template