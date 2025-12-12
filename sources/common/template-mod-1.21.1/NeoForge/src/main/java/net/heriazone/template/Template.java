package net.heriazone.template;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.ModLoadingContext;

/**
 * <p>NeoForge entry point for ${mod_name} mod providing loader-specific initialization and event handling.</p>
 * <p>
 * <b>Architecture:</b> Serves as the bridge between NeoForge's @Mod system and the mod's
 * common initialization logic. Handles NeoForge-specific event registration while
 * delegating shared logic to the common TemplateMain class.
 * <p>
 * <b>Event Handling:</b> Registers for FMLCommonSetupEvent to perform initialization
 * during NeoForge's common setup phase, ensuring proper timing with other mods.
 */
@Mod(TemplateMain.MOD_ID)
public class Template {

    /**
     * NeoForge mod constructor and initialization entry point.
     * <p>
     * <b>Execution Context:</b> Called during NeoForge's mod construction phase.
     * Sets up event listeners and prepares for common setup phase.
     */
    public Template() {
        TemplateMain.LOGGER.info("${mod_name} {} initializing for NeoForge", TemplateMain.getVersion());
        
        IEventBus modEventBus = ModLoadingContext.get().getActiveContainer().getEventBus();
        modEventBus.addListener(this::commonSetup);
        
        TemplateMain.LOGGER.info("${mod_name} NeoForge constructor complete");
    }

    /**
     * Handles NeoForge common setup phase for cross-loader initialization.
     * <p>
     * <b>Timing:</b> Called during FMLCommonSetupEvent, after all mods are constructed
     * but before world loading. Ideal for shared initialization logic.
     * 
     * @param event FML common setup event providing setup context
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        TemplateMain.LOGGER.info("${mod_name} common setup phase starting");
        
        // Initialize common mod functionality
        TemplateMain.init();
        
        TemplateMain.LOGGER.info("${mod_name} common setup phase complete");
    }

} // Class: Template