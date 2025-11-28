package net.msymbios.llovelyr;

import net.msymbios.llovelyr.common.util.ObjectUtil;
import net.msymbios.llovelyr.common.util.internal.Version;
import net.msymbios.llovelyr.source.blocks.LovelyBlocks;
import net.msymbios.llovelyr.source.commands.ColorArgumentType;
import net.msymbios.llovelyr.source.commands.ColorArgumentTypeInfo;
import net.msymbios.llovelyr.source.configs.LovelyConfigs;
import net.msymbios.llovelyr.source.entity.LovelyEntities;
import net.msymbios.llovelyr.source.entity.internal.NativeEntityType;
import net.msymbios.llovelyr.source.groups.LovelyGroups;
import net.msymbios.llovelyr.source.items.LovelyItems;
import net.msymbios.llovelyr.source.recipes.LovelyRecipes;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

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
@Mod(LovelyLegacy.MODID)
public class LovelyLegacy {

    // -- Constants --

    /**
     * Mod identifier used across all registrations and resource locations.
     * <p>
     * <b>Naming Convention:</b> "llovelyr" = Legacy Lovely Robot variant.
     */
    public static final String MODID = "llovelyr";

    /**
     * Mod version for NBT migration and compatibility tracking.
     * <p>
     * <b>Dynamic Resolution:</b> Reads version from package specification metadata
     * (populated by build system from mods.toml), falling back to "99999.0.0.0"
     * for development environments where package metadata is unavailable.
     * <p>
     * <b>Usage:</b> Stored in entity NBT data to enable version-aware deserialization
     * when entity structure changes between releases. Supports backward compatibility
     * by allowing migration logic based on data version.
     * <p>
     * <b>Fallback Behavior:</b> Development fallback "99999.0.0.0" ensures dev builds
     * always appear "newer" than release builds, preventing false migration triggers
     * during testing.
     * <p>
     * <i>Note:</i> Version format follows semantic versioning (major.minor.patch.build)
     * matching mods.toml specification for consistency with Forge mod metadata.
     */
    public static final Version VERSION = new Version(ObjectUtil.coalesce(LovelyLegacy.class.getPackage().getSpecificationVersion(), "99999.0.0.0"));

    /**
     * SLF4J logger for mod-wide logging with automatic mod name prefixing.
     */
    public static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Deferred register for command argument types.
     * <p>
     * <b>Architecture:</b> Registers custom argument types with Minecraft's command
     * system, enabling client-server synchronization of command arguments.
     */
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES =
        DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, MODID);

    /**
     * Registered ColorArgumentType for robot color selection commands.
     * <p>
     * <b>Registration:</b> Enables ColorArgumentType to be used in commands with
     * proper client-server synchronization.
     */
    public static final RegistryObject<ArgumentTypeInfo<?, ?>> COLOR_ARGUMENT_TYPE =
        COMMAND_ARGUMENT_TYPES.register("color", () -> new ColorArgumentTypeInfo());

    // -- Constructor --

    /**
     * Mod constructor invoked by Forge during mod loading phase.
     * <p>
     * <b>Registration Order:</b>
     * 1. Lifecycle event listeners (commonSetup, clientSetup)
     * 2. Deferred registers (blocks, items, creative tabs)
     * 3. FORGE event bus registration (gameplay events)
     * 4. Creative tab item population
     * 5. Configuration system
     * <p>
     * <b>Event Buses:</b> Uses MOD bus for registration events, FORGE bus for
     * gameplay events. Separation prevents registration timing issues.
     *
     * @param context Forge mod loading context providing event bus access
     */
    public LovelyLegacy(FMLJavaModLoadingContext context) {
        IEventBus eventBus = context.getModEventBus();
        GeckoLib.initialize();

        LovelyConfigs.register(context);
        LovelyConfigs.onLoadCallback(NativeEntityType::register);

        LovelyBlocks.register(eventBus);
        LovelyItems.register(eventBus);
        LovelyGroups.register(eventBus);
        LovelyEntities.register(eventBus);
        LovelyRecipes.RECIPE_SERIALIZERS.register(eventBus);
        COMMAND_ARGUMENT_TYPES.register(eventBus);

        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::clientSetup);
        LovelyGroups.registerItems(eventBus);

        MinecraftForge.EVENT_BUS.register(this);
    } // LovelyLegacy()

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
        LOGGER.info("HELLO FROM COMMON: SETUP");
        
        // Register ColorArgumentType with ArgumentTypeInfos for client-server sync
        event.enqueueWork(() -> {
            ArgumentTypeInfos.registerByClass(
                ColorArgumentType.class, 
                (ArgumentTypeInfo<ColorArgumentType, ?>) COLOR_ARGUMENT_TYPE.get()
            );
            LOGGER.info("Registered ColorArgumentType");
        });
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
        LOGGER.info("HELLO FROM CLIENT: SETUP");
    } // clientSetup()

} // Class: LovelyLegacy