package net.heriazone.llovelyr.source;

import net.heriazone.llovelyr.Legacy;
import net.heriazone.llovelyr.LegacyIdentifier;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.lovelylib.hzlib.api.groups.InternalGroups;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Manages creative mode tabs for Legacy variant items.
 * <p>
 * <b>Architecture:</b> Uses Forge's DeferredRegister for creative tabs,
 * organizing items into logical groups for creative inventory browsing.
 * Extends InternalGroups for consistent tab creation patterns.
 * <p>
 * <b>Tab Organization:</b> Default tab contains all Legacy variant robots
 * and related items with localized titles. Spawn eggs also appear in vanilla
 * spawn eggs tab for discoverability.
 */
public class LegacyGroups extends InternalGroups {

    // -- Variables --

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = createRegister(Legacy.MODID);

    /**
     * Default creative tab for Legacy variant items.
     * <p>
     * <b>Contents:</b> Robot cores, spawn eggs, and related items.
     */
    public static final RegistryObject<CreativeModeTab> DEFAULT_TAB = CREATIVE_MODE_TABS.register(
            LovelyConstant.DEFAULT_TAB,
            () -> createTabBuilder(
                    LegacyIdentifier.getTabTranslation(LovelyConstant.DEFAULT_TAB),
                    () -> new ItemStack(LegacyItems.ROBOT_CORE.get())
            )
                    .displayItems((parameters, output) -> {
                        output.accept(LegacyItems.ROBOT_CORE.get());
                        output.accept(LegacyItems.BUNNY_SPAWN.get());
                        output.accept(LegacyItems.BUNNY2_SPAWN.get());
                        output.accept(LegacyItems.DRAGON_SPAWN.get());
                        output.accept(LegacyItems.HONEY_SPAWN.get());
                        output.accept(LegacyItems.KITSUNE_SPAWN.get());
                        output.accept(LegacyItems.NEKO_SPAWN.get());
                        output.accept(LegacyItems.VANILLA_SPAWN.get());
                    })
                    .build()
    );

    // -- Registration Methods --

    /**
     * Registers creative tabs with Forge event bus.
     * <p>
     * <b>Timing:</b> Must be called during mod construction before registry events fire.
     *
     * @param eventBus the mod event bus
     */
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
        Legacy.LOGGER.info("Registering CreativeTabs: " + Legacy.MODID);
    } // register()

    /**
     * Registers spawn eggs to vanilla creative tabs.
     * <p>
     * <b>Cross-Tab Population:</b> Adds Legacy spawn eggs to vanilla spawn eggs
     * tab for better discoverability alongside vanilla mob eggs.
     *
     * @param eventBus the mod event bus
     */
    public static void registerItems(IEventBus eventBus) {
        eventBus.addListener(LegacyGroups::addSpawnEggs);
    } // registerItems ()

    /**
     * Adds spawn eggs to vanilla spawn eggs creative tab.
     * <p>
     * <b>Event Handler:</b> Listens for BuildCreativeModeTabContentsEvent to
     * populate vanilla tabs with mod items.
     *
     * @param event the tab contents building event
     */
    private static void addSpawnEggs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(LegacyItems.BUNNY_SPAWN);
            event.accept(LegacyItems.BUNNY2_SPAWN);
            event.accept(LegacyItems.DRAGON_SPAWN);
            event.accept(LegacyItems.HONEY_SPAWN);
            event.accept(LegacyItems.KITSUNE_SPAWN);
            event.accept(LegacyItems.NEKO_SPAWN);
            event.accept(LegacyItems.VANILLA_SPAWN);
        }
    } // addSpawnEggs ()

} // Class: LegacyGroups