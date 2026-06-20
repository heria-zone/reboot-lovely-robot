package net.heriazone.rlovelyr;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.api.ClientModInitializer;
import net.heriazone.lovelylib.common.commands.LovelyCommandArguments;
import net.heriazone.hzlib.api.services.FabricServices;
import net.heriazone.hzlib.api.services.Services;
import net.heriazone.lovelylib.source.reboot.RebootRobotFamilies;
import net.heriazone.rlovelyr.source.*;

/**
 * Main mod class for Reboot variant (Fabric loader).
 * <p>
 * <b>Architecture:</b> Implements Fabric's ModInitializer for mod entry point,
 * coordinating registration of all mod content (items, entities, creative tabs,
 * events) through direct registry calls and event system.
 * <p>
 * <b>Registration Flow:</b> Single onInitialize method handles all registration
 * in explicit order: GeckoLib → creative tabs → items → events → entities.
 * Simpler than Forge's multiphase lifecycle.
 * <p>
 * <b>Mod ID:</b> "rlovelyr" - Must match fabric.mod.json entry for Fabric
 * to recognize and load the mod.
 */
public class LovelyReboot implements ModInitializer, ClientModInitializer {

    /**
     * Mod initialization invoked by Fabric during mod loading phase.
     * <p>
     * <b>Registration Order:</b>
     * 1. GeckoLib animation library initialization
     * 2. Creative tabs (must precede items for tab population)
     * 3. Items (registers with Fabric registry)
     * 4. Event handlers (crafting callbacks, mixins)
     * 5. Entities (robot entity types and attributes)
     * <p>
     * <b>Design Decision:</b> Explicit ordering ensures dependencies are
     * satisfied (e.g., tabs exist before items reference them). Fabric's
     * single-phase initialization requires careful sequencing.
     * <p>
     * <b>GeckoLib:</b> Must initialize before any GeckoLib-dependent classes
     * (animated entities, models) are loaded to prevent animation system errors.
     */
    @Override
    public void onInitialize() {
        // Initialize platform services first
        Services.setInstance(new FabricServices());
        //Reboot.LOGGER.info("Initializing Lovely Reboot for Fabric");

        // Initialize common functionality
        Reboot.init();

        // Register Fabric-specific features here
        RebootConfigs.register();
        RebootRobotFamilies.reloadFromConfig();
        RebootEntities.registerNativeRobotFeature();
        RebootGroups.register();
        RebootItems.register();
        RebootEvents.register();
        RebootEntities.register();
        RebootRecipes.register();
        LovelyCommandArguments.register();

        //Reboot.LOGGER.info("Lovely Reboot Fabric initialization complete");
    } // onInitialize ()

    /**
     * Registers client-side rendering systems.
     * <p>
     * <b>Order:</b> Item models → Entity renderers (ensures item properties exist
     * before entity renderers query them).
     */
    @Override
    public void onInitializeClient() {
        // Initialize client-side functionality
        Reboot.initClient();

        RebootItems.registerModel();
        RebootEntities.registerRender();
    } // onInitializeClient ()

} // Class: LovelyReboot