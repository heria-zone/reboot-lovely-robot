package net.heriazone.tlovelyr.neoforge;

import net.heriazone.tlovelyr.common.LovelyTributeCommon;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>NeoForge entry point for Lovely Tribute mod.<p>
 * <p>
 * <b>Architecture:</b> Serves as the NeoForge-specific initialization layer,
 * delegating core functionality to the common module while handling
 * NeoForge-specific mod lifecycle events.
 * <p>
 * <b>Initialization:</b> Automatically invoked by NeoForge mod loader during
 * mod construction phase, ensuring proper setup before game initialization.
 */
@Mod(LovelyTributeCommon.MOD_ID)
public class LovelyTribute {

    /**
     * NeoForge-specific logger for loader-specific operations.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger("Lovely Tribute");

    /**
     * NeoForge mod constructor - automatically called by NeoForge mod loader.
     * <p>
     * <b>Lifecycle:</b> Invoked during mod construction phase, before common setup.
     * Delegates to common initialization to maintain cross-loader consistency.
     * 
     * @param modEventBus the mod event bus for registering event handlers
     */
    public LovelyTribute(IEventBus modEventBus) {
        LOGGER.info("Constructing Tribute for NeoForge");
        
        // Register setup event
        modEventBus.addListener(this::onCommonSetup);
        
        LOGGER.info("Lovely Tribute NeoForge constructor complete");
    }

    /**
     * Handles common setup phase for mod initialization.
     * <p>
     * <b>Design Intent:</b> Establishes NeoForge-specific features and integrations
     * that complement the core mod functionality.
     * 
     * @param event the common setup event
     */
    private void onCommonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Lovely Tribute NeoForge common setup starting");
        
        event.enqueueWork(() -> {
            // Initialize the common mod functionality
            LovelyTributeCommon.init();
        });
        
        LOGGER.info("Lovely Tribute NeoForge common setup complete");
    }

} // Class: LovelyTribute