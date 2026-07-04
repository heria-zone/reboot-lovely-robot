package net.heriazone.llovelyr;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.api.ClientModInitializer;
import net.heriazone.llovelyr.source.*;
import net.heriazone.lovelylib.Lovely;
import net.heriazone.lovelylib.common.commands.LovelyCommandArguments;
import net.heriazone.lovelylib.source.legacy.LegacyRobotFamilies;

/**
 * Main mod class for Legacy variant (Fabric loader).
 * <p>
 * <b>Architecture:</b> Implements Fabric's ModInitializer for mod entry point,
 * coordinating registration of all mod content (items, entities, creative tabs,
 * events) through direct registry calls and event system.
 * <p>
 * <b>Registration Flow:</b> Single onInitialize method handles all registration
 * in explicit order: GeckoLib → creative tabs → items → events → entities.
 * Simpler than Forge's multiphase lifecycle.
 * <p>
 * <b>Mod ID:</b> "llovelyr" - Must match fabric.mod.json entry for Fabric
 * to recognize and load the mod.
 */
public class LovelyLegacy implements ModInitializer, ClientModInitializer {

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
        // Registry must be sealed before any registration call below.
        Lovely.onInitialize();
        LegacyRobotFamilies.initialize();

        //Legacy.LOGGER.info("Initializing Lovely Legacy for Fabric");
        
        // Initialize common functionality
        Legacy.init();

        // Register Fabric-specific features here
        LegacyConfigs.register();
        LegacyRobotFamilies.reloadFromConfig();
        LegacyGroups.register();
        LegacyEntities.register();
        LegacyItems.register();
        LegacyEvents.register();
        LegacyEntities.registerNativeRobotFeature();
        LegacyRecipes.register();
        LovelyCommandArguments.register();

        //Legacy.LOGGER.info("Lovely Legacy Fabric initialization complete");
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
        Legacy.initClient();

        LegacyItems.registerModel();
        LegacyEntities.registerRender();
    } // onInitializeClient ()

} // Class: LovelyLegacy