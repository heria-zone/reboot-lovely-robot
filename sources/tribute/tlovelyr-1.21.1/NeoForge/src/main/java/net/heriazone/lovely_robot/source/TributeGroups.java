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

    // -- Methods --

    /**
     * Registers creative tabs with NeoForge event bus.
     * <p>
     * <b>Timing:</b> Must be called during mod construction before registry events fire.
     *
     * @param eventBus the mod event bus
     */
    public static void register(IEventBus eventBus) {
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
        eventBus.addListener(TributeGroups::allItems);
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
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(TributeItems.BUNNY_SPAWN);
            event.accept(TributeItems.BUNNY2_SPAWN);
            event.accept(TributeItems.HONEY_SPAWN);
            event.accept(TributeItems.VANILLA_SPAWN);
        }
    } // addSpawnEggs ()

    /**
     * Adds all items to mod's default creative tab.
     */
    private static void allItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) event.accept(TributeItems.ROBOT_CORE);
    } // allItems()

} // Class: TributeGroups