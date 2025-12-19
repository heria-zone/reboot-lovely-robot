package net.heriazone.llovelyr;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.api.ClientModInitializer;
import net.heriazone.llovelyr.source.*;
import net.heriazone.lovelylib.api.entity.features.PickupFeature;
import net.heriazone.lovelylib.common.commands.LovelyCommandArguments;
import net.heriazone.lovelylib.hzlib.api.entity.features.DropFeature;
import net.heriazone.lovelylib.hzlib.api.services.*;
import net.heriazone.lovelylib.source.legacy.LegacyRobotType;

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
        // Initialize platform services first
        Services.setInstance(new FabricServices());
        //Legacy.LOGGER.info("Initializing Lovely Legacy for Fabric");
        
        // Initialize common functionality
        Legacy.init();

        // Register Fabric-specific features here
        LegacyConfigs.register();
        LegacyRobotType.reloadFromConfig();
        UpdateNativeRobot();
        LegacyGroups.register();
        LegacyItems.register();
        LegacyEvents.register();
        LegacyEntities.register();
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

    // TEMP

    public static void UpdateNativeRobot () {
        // BUNNY
        LegacyRobotType.BUNNY
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.BUNNY_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.BUNNY_SPAWN));

        // BUNNY2
        LegacyRobotType.BUNNY2
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.BUNNY2_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.BUNNY2_SPAWN));

        // DRAGON
        LegacyRobotType.DRAGON
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.DRAGON_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.DRAGON_SPAWN));

        // HONEY
        LegacyRobotType.HONEY
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.HONEY_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.HONEY_SPAWN));

        // KITSUNE
        LegacyRobotType.KITSUNE
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.KITSUNE_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.KITSUNE_SPAWN));

        // NEKO
        LegacyRobotType.NEKO
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.NEKO_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.NEKO_SPAWN));

        // VANILLA
        LegacyRobotType.VANILLA
                .withFeature(PickupFeature.class, new PickupFeature(LegacyItems.VANILLA_SPAWN))
                .withFeature(DropFeature.class, new DropFeature(LegacyItems.VANILLA_SPAWN));
    } // UpdateNativeRobot ()

} // Class: LovelyLegacy