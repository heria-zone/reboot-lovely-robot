package net.heriazone.lovely_robot;

import net.heriazone.lovely_robot.source.*;
import net.heriazone.lovelylib.source.tribute.TributeRobotFamilies;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Main mod class for Tribute variant (Forge loader).
 * <p>
 * <b>Architecture:</b> Serves as entry point for Forge mod initialization,
 * coordinating registration of all mod content (blocks, items, entities,
 * creative tabs) through Forge's event-driven lifecycle.
 * <p>
 * <b>Registration Flow:</b> Constructor registers deferred registers with MOD
 * bus, then lifecycle events (commonSetup, clientSetup) fire for side-specific
 * initialization. Automatic event subscriber classes handle additional events.
 * <p>
 * <b>Mod ID:</b> "lovely_robot" - Must match META-INF/mods.toml entry for Forge
 * to recognize and load the mod.
 */
@Mod(Tribute.MODID)
public class LovelyTribute {

    /**
     * Forge mod constructor - entry point for Forge mod loading.
     * <p>
     * <b>Initialization Strategy:</b> Sets up event listeners and delegates
     * actual initialization to lifecycle events for proper timing.
     */
    public LovelyTribute(FMLJavaModLoadingContext context) {
        IEventBus events = context.getModEventBus();

        TributeConfigs.register(context);
        TributeConfigs.onLoadCallback(TributeRobotFamilies::reloadFromConfig);

        //LovelyBlocks.register(events);
        TributeItems.register(events);
        TributeGroups.register(events);
        TributeEntities.register(events);
        TributeRecipes.register(events);
        TributeCommandArguments.register(events);

        events.addListener(this::commonSetup);
        events.addListener(this::clientSetup);
        TributeGroups.registerItems(events);

        MinecraftForge.EVENT_BUS.register(this);
    } // Constructor: LovelyTribute ()

    // -- Custom Methods --

    /**
     * Common setup phase for both client and server.
     * <p>
     * <b>Timing:</b> Fires after registry events complete but before world
     * loading. Suitable for cross-side initialization (network handlers,
     * data registration).
     * <p>
     * <i>Note:</i> Currently placeholder for future common initialization.
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
    private void clientSetup(final FMLClientSetupEvent event) {} // clientSetup()


} // Class: LovelyTribute