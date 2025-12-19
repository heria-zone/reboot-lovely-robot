package net.heriazone.llovelyr;

import net.heriazone.llovelyr.source.*;
import net.heriazone.lovelylib.api.entity.features.PickupFeature;
import net.heriazone.lovelylib.hzlib.api.entity.features.DropFeature;
import net.heriazone.lovelylib.source.legacy.LegacyRobotType;
import net.heriazone.lovelylib.hzlib.api.services.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.*;

/**
 * Main mod class for Legacy variant (NeoForge loader).
 * <p>
 * <b>Architecture:</b> Serves as entry point for NeoForge mod initialization,
 * coordinating registration of all mod content (blocks, items, entities,
 * creative tabs) through NeoForge's event-driven lifecycle.
 * <p>
 * <b>Registration Flow:</b> Constructor registers deferred registers with MOD
 * bus, then lifecycle events (commonSetup, clientSetup) fire for side-specific
 * initialization. Automatic event subscriber classes handle additional events.
 * <p>
 * <b>Mod ID:</b> "llovelyr" - Must match META-INF/neoforge.mods.toml entry for NeoForge
 * to recognize and load the mod.
 */
@Mod(Legacy.MODID)
public class LovelyLegacy {

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
    public LovelyLegacy(IEventBus modEventBus, ModContainer modContainer) {
        // Initialize platform services first
        Services.setInstance(new NeoForgeServices());

        // Test platform services functionality
        Legacy.LOGGER.info("Platform: {}", Services.get().getPlatformName());
        Legacy.LOGGER.info("Development Environment: {}", Services.get().isDevelopmentEnvironment());
        Legacy.LOGGER.info("Config Directory: {}", Services.get().getConfigDirectory());

        // Register configuration system
        LegacyConfigs.register(modContainer);
        LegacyConfigs.onLoadCallback(LegacyRobotType::reloadFromConfig);

        // Register deferred registers
        LegacyItems.register(modEventBus);
        LegacyGroups.register(modEventBus);
        LegacyEntities.register(modEventBus);
        LegacyRecipes.register(modEventBus);
        LegacyCommandArguments.register(modEventBus);

        // Register lifecycle event listeners
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        // Register creative tab item population
        LegacyGroups.registerItems(modEventBus);

        Legacy.LOGGER.info("LovelyLegacy (NeoForge) initialized");
    } // LovelyLegacy()

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
        LegacyCommandArguments.register(event);
        // Now items are registered and .get() works
        event.enqueueWork(LovelyLegacy::UpdateNativeRobot);
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
        Legacy.LOGGER.info("Client setup complete");
    } // clientSetup()

    // TEMP

    public static void UpdateNativeRobot () {
        // BUNNY
        LegacyRobotType.BUNNY
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.BUNNY_SPAWN.get()))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.BUNNY_SPAWN.get()));

        // BUNNY2
        LegacyRobotType.BUNNY2
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.BUNNY2_SPAWN.get()))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.BUNNY2_SPAWN.get()));

        // DRAGON
        LegacyRobotType.DRAGON
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.DRAGON_SPAWN.get()))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.DRAGON_SPAWN.get()));

        // HONEY
        LegacyRobotType.HONEY
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.HONEY_SPAWN.get()))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.HONEY_SPAWN.get()));

        // KITSUNE
        LegacyRobotType.KITSUNE
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.KITSUNE_SPAWN.get()))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.KITSUNE_SPAWN.get()));

        // NEKO
        LegacyRobotType.NEKO
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.NEKO_SPAWN.get()))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.NEKO_SPAWN.get()));

        // VANILLA
        LegacyRobotType.VANILLA
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.VANILLA_SPAWN.get()))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.VANILLA_SPAWN.get()));
    } // UpdateNativeRobot ()

} // Class: LovelyLegacy