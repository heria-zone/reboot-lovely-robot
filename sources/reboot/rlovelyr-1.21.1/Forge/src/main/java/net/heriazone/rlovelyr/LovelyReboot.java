package net.heriazone.rlovelyr;

import net.heriazone.hzlib.api.services.ForgeServices;
import net.heriazone.hzlib.api.services.Services;
import net.heriazone.lovelylib.Lovely;
import net.heriazone.lovelylib.source.reboot.RebootRobotFamilies;
import net.heriazone.rlovelyr.source.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Main mod class for Reboot variant (Forge loader).
 * <p>
 * <b>Architecture:</b> Serves as entry point for Forge mod initialization,
 * coordinating registration of all mod content (blocks, items, entities,
 * creative tabs) through Forge's event-driven lifecycle.
 * <p>
 * <b>Registration Flow:</b> Constructor registers deferred registers with MOD
 * bus, then lifecycle events (commonSetup, clientSetup) fire for side-specific
 * initialization. Automatic event subscriber classes handle additional events.
 * <p>
 * <b>Mod ID:</b> "rlovelyr" - Must match META-INF/mods.toml entry for Forge
 * to recognize and load the mod.
 */
@Mod(Reboot.MODID)
public class LovelyReboot {

    /**
     * Forge mod constructor - entry point for Forge mod loading.
     * <p>
     * <b>Initialization Strategy:</b> Sets up event listeners and delegates
     * actual initialization to lifecycle events for proper timing.
     */
    public LovelyReboot(FMLJavaModLoadingContext context) {
        IEventBus events = context.getModEventBus();

        // Registry must be sealed before any DeferredRegister supplier fires.
        Lovely.onInitialize();
        RebootRobotFamilies.initialize();

        // Initialize platform services first
        Services.setInstance(new ForgeServices());

        // Test platform services functionality
        Reboot.LOGGER.info("Platform: {}", Services.get().getPlatformName());
        Reboot.LOGGER.info("Development Environment: {}", Services.get().isDevelopmentEnvironment());
        Reboot.LOGGER.info("Config Directory: {}", Services.get().getConfigDirectory());

        RebootConfigs.register(context);
        RebootConfigs.onLoadCallback(RebootRobotFamilies::reloadFromConfig);

        //LovelyBlocks.register(events);
        RebootItems.register(events);
        RebootGroups.register(events);
        RebootEntities.register(events);
        RebootRecipes.register(events);
        RebootCommandArguments.register(events);

        events.addListener(this::commonSetup);
        events.addListener(this::clientSetup);
        RebootGroups.registerItems(events);

        MinecraftForge.EVENT_BUS.register(this);
    } // Constructor: LovelyReboot ()

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
        RebootCommandArguments.register(event);
        // Now items are registered and .get() works
        event.enqueueWork(RebootEntities::registerNativeRobotFeature);
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

} // Class: LovelyReboot