package net.heriazone.lovely_robot.source;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.heriazone.lovely_robot.Tribute;
import net.heriazone.hzlib.api.groups.InternalGroups;
import net.minecraft.world.item.CreativeModeTabs;

/**
 * Manages creative mode tabs for Tribute variant items.
 * <p>
 * <b>Architecture:</b> Uses Fabric's Registry system for item groups,
 * organizing items into logical groups for creative inventory browsing.
 * <p>
 * <b>Tab Organization:</b> Default tab contains all Tribute variant robots
 * and related items with localized titles.
 */
public class TributeGroups extends InternalGroups {

    // -- Methods --

    /**
     * Registers creative tabs with Fabric registry.
     * <p>
     * <b>Timing:</b> Must be called during mod initialization.
     */
    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(TributeGroups::addSpawnEggs);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(TributeGroups::allItems);

        Tribute.LOGGER.info("Registering CreativeTabs for: " + Tribute.MODID);
    } // register()

    /**
     * Adds spawn eggs to vanilla spawn eggs creative tab.
     */
    private static void addSpawnEggs(FabricItemGroupEntries entries) {
        entries.accept(TributeItems.BUNNY_SPAWN);
        entries.accept(TributeItems.BUNNY2_SPAWN);
        entries.accept(TributeItems.HONEY_SPAWN);
        entries.accept(TributeItems.VANILLA_SPAWN);
    } // spawnEggItemsEntry()

    /**
     * Adds all items to mod's default creative tab.
     */
    private static void allItems(FabricItemGroupEntries entries) {
        entries.accept(TributeItems.ROBOT_CORE);
    } // allItems()

} // Class: TributeGroups