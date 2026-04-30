package net.heriazone.lovely_robot.source;

import net.heriazone.lovely_robot.Tribute;
import net.heriazone.lovely_robot.TributeIdentifier;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.hzlib.api.groups.InternalGroups;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Manages creative mode tabs for Tribute variant items (NeoForge).
 * <p>
 * <b>Architecture:</b> Uses NeoForge's DeferredRegister for creative tabs,
 * organizing items into logical groups for creative inventory browsing.
 * Extends InternalGroups for consistent tab creation patterns.
 * <p>
 * <b>Tab Organization:</b> Default tab contains all Tribute variant robots
 * and related items with localized titles. Spawn eggs also appear in vanilla
 * spawn eggs tab for discoverability.
 */
public class TributeGroups extends InternalGroups {

    // -- Variables --

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = createRegister(Tribute.MODID);

    /**
     * Default creative tab for Tribute variant items.
     * <p>
     * <b>Contents:</b> Robot cores, spawn eggs, and related items.
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DEFAULT_TAB = CREATIVE_MODE_TABS.register(
            LovelyConstant.DEFAULT_TAB,
            () -> createTabBuilder(
                    TributeIdentifier.getTabTranslation(LovelyConstant.DEFAULT_TAB),
                    () -> new ItemStack(TributeItems.ROBOT_CORE.get())
            )
                    .displayItems((parameters, output) -> {
                        output.accept(TributeItems.ROBOT_CORE.get());
                        output.accept(TributeItems.BUNNY_SPAWN.get());
                        output.accept(TributeItems.BUNNY2_SPAWN.get());
                        output.accept(TributeItems.HONEY_SPAWN.get());
                        output.accept(TributeItems.VANILLA_SPAWN.get());
                    })
                    .build()
    );

    // -- Registration Methods --

    /**
     * Registers creative tabs with NeoForge event bus.
     * <p>
     * <b>Timing:</b> Must be called during mod construction before registry events fire.
     *
     * @param eventBus the mod event bus
     */
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
        Tribute.LOGGER.info("Registering CreativeTabs: " + Tribute.MODID);
    } // register()

    /**
     * Registers spawn eggs to vanilla creative tabs.
     * <p>
     * <b>Cross-Tab Population:</b> Adds Tribute spawn eggs to vanilla spawn eggs
     * tab for better discoverability alongside vanilla mob eggs.
     *
     * @param eventBus the mod event bus
     */
    public static void registerItems(IEventBus eventBus) {
        eventBus.addListener(TributeGroups::addSpawnEggs);
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
            event.accept(TributeItems.BUNNY_SPAWN);
            event.accept(TributeItems.BUNNY2_SPAWN);
            event.accept(TributeItems.HONEY_SPAWN);
            event.accept(TributeItems.VANILLA_SPAWN);
        }
    } // addSpawnEggs ()

} // Class: TributeGroups