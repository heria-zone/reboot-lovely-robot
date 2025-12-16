package net.heriazone.llovelyr;

import net.heriazone.llovelyr.source.*;
import net.heriazone.lovelylib.common.entity.common.LovelyRobotType;
import net.heriazone.lovelylib.hzlib.api.services.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Main mod class for Legacy variant (Forge loader).
 * <p>
 * <b>Architecture:</b> Serves as entry point for Forge mod initialization,
 * coordinating registration of all mod content (blocks, items, entities,
 * creative tabs) through Forge's event-driven lifecycle.
 * <p>
 * <b>Registration Flow:</b> Constructor registers deferred registers with MOD
 * bus, then lifecycle events (commonSetup, clientSetup) fire for side-specific
 * initialization. Automatic event subscriber classes handle additional events.
 * <p>
 * <b>Mod ID:</b> "llovelyr" - Must match META-INF/mods.toml entry for Forge
 * to recognize and load the mod.
 */
@Mod(Legacy.MODID)
public class LovelyLegacy {

    /**
     * Forge mod constructor - entry point for Forge mod loading.
     * <p>
     * <b>Initialization Strategy:</b> Sets up event listeners and delegates
     * actual initialization to lifecycle events for proper timing.
     */
    public LovelyLegacy(FMLJavaModLoadingContext context) {
        IEventBus events = context.getModEventBus();

        // Initialize platform services first
        Services.setInstance(new ForgeServices());

        // Test platform services functionality
        Legacy.LOGGER.info("Platform: {}", Services.get().getPlatformName());
        Legacy.LOGGER.info("Development Environment: {}", Services.get().isDevelopmentEnvironment());
        Legacy.LOGGER.info("Config Directory: {}", Services.get().getConfigDirectory());

        LovelyConfigs.register(context);
        LovelyConfigs.onLoadCallback(LovelyRobotType::reloadFromConfig);

        //LovelyBlocks.register(events);
        LovelyItems.register(events);
        LovelyGroups.register(events);
        LovelyEntities.register(events);
        LovelyRecipes.register(events);
        LovelyCommandArguments.register(events);

        events.addListener(this::commonSetup);
        events.addListener(this::clientSetup);
        LovelyGroups.registerItems(events);

        MinecraftForge.EVENT_BUS.register(this);
    } // Constructor: LovelyLegacy ()

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
        LovelyCommandArguments.register(event);
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

} // Class: LovelyLegacy