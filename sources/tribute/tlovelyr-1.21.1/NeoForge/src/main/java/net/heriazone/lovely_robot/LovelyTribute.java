package net.heriazone.lovely_robot;

import net.heriazone.lovely_robot.source.*;
import net.heriazone.lovelylib.source.tribute.TributeRobotFamilies;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Main mod class for Tribute variant (NeoForge loader).
 * <p>
 * <b>Architecture:</b> Serves as entry point for NeoForge mod initialization,
 * coordinating registration of all mod content (blocks, items, entities,
 * creative tabs) through NeoForge's event-driven lifecycle.
 * <p>
 * <b>Registration Flow:</b> Constructor registers deferred registers with MOD
 * bus, then lifecycle events (commonSetup, clientSetup) fire for side-specific
 * initialization. Automatic event subscriber classes handle additional events.
 * <p>
 * <b>Mod ID:</b> "lovely_robot" - Must match META-INF/neoforge.mods.toml entry for NeoForge
 * to recognize and load the mod.
 */
@Mod(Tribute.MODID)
public class LovelyTribute {

    // -- Constructor --

    /**
     * Mod constructor invoked by NeoForge during mod loading phase.
     * <p>
     * <b>Registration Order:</b>
     * 1. Configuration system
     * 2. Lifecycle event listeners (commonSetup, clientSetup)
     * 3. Deferred registers (items, creative tabs, entities, recipes, commands)
     * 4. FORGE event bus registration (gameplay events)
     * 5. Creative tab item population
     * <p>
     * <b>Event Buses:</b> Uses MOD bus for registration events, FORGE bus for
     * gameplay events. Separation prevents registration timing issues.
     * <p>
     * <b>NeoForge Pattern:</b> Constructor receives IEventBus and ModContainer
     * parameters automatically from NeoForge's dependency injection system.
     *
     * @param modEventBus NeoForge mod event bus for registration
     * @param modContainer mod container for configuration registration
     */
    public LovelyTribute(IEventBus modEventBus, ModContainer modContainer) {
        // Register configuration system
        TributeConfigs.register(modContainer);
        TributeConfigs.onLoadCallback(TributeRobotFamilies::reloadFromConfig);

        // Register deferred registers
        TributeItems.register(modEventBus);
        TributeGroups.register(modEventBus);
        TributeEntities.register(modEventBus);
        TributeRecipes.register(modEventBus);
        TributeCommandArguments.register(modEventBus);

        // Register lifecycle event listeners
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        // Register creative tab item population
        TributeGroups.registerItems(modEventBus);

        Tribute.LOGGER.info("LovelyTribute (NeoForge) initialized");
    } // LovelyTribute()

    // -- Custom Methods --

    /**
     * Common setup phase for both client and server.
     * <p>
     * <b>Timing:</b> Fires after registry events complete but before world
     * loading. Suitable for cross-side initialization (network handlers,
     * data registration).
     * <p>
     * <b>Thread Safety:</b> Uses enqueueWork to ensure command argument
     * registration happens on the main thread.
     *
     * @param event the common setup event
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        TributeCommandArguments.register(event);
        // Now items are registered and .get() works
        event.enqueueWork(TributeEntities::registerNativeRobotFeature);
    } // commonSetup()

    /**
     * Client-only setup phase for rendering and client-side systems.
     * <p>
     * <b>Timing:</b> Fires after common setup on physical clients only.
     * Suitable for renderer registration, keybind setup, client-only handlers.
     * <p>
     * <i>Note:</i> Currently placeholder for future client initialization.
     *
     * @param event the client setup event
     */
    private void clientSetup(final FMLClientSetupEvent event) {
        Tribute.LOGGER.info("Client setup complete");
    } // clientSetup()

} // Class: LovelyTribute