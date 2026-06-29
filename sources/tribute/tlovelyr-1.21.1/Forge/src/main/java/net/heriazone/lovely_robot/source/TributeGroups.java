package net.heriazone.lovely_robot.source;

import net.heriazone.lovely_robot.Tribute;
import net.heriazone.hzlib.api.groups.InternalGroups;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * Manages creative mode tabs for Tribute variant items.
 * <p>
 * <b>Architecture:</b> Uses Forge's DeferredRegister for creative tabs,
 * organizing items into logical groups for creative inventory browsing.
 * Extends InternalGroups for consistent tab creation patterns.
 * <p>
 * <b>Tab Organization:</b> Default tab contains all Tribute variant robots
 * and related items with localized titles. Spawn eggs also appear in vanilla
 * spawn eggs tab for discoverability.
 */
public class TributeGroups extends InternalGroups {

    // -- Registration Methods --

    /**
     * Registers creative tabs with Forge event bus.
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
            event.accept(TributeItems.ROBOT_CORE);
        }
    } // addSpawnEggs ()

    /**
     * Adds all items to mod's default creative tab.
     */
    private static void allItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) event.accept(TributeItems.ROBOT_CORE);
    } // allItems()

} // Class: TributeGroups