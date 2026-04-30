package net.heriazone.llovelyr.source;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.heriazone.llovelyr.Legacy;
import net.heriazone.llovelyr.LegacyIdentifier;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.hzlib.api.groups.InternalGroups;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

/**
 * Manages creative mode tabs for Legacy variant items.
 * <p>
 * <b>Architecture:</b> Uses Fabric's Registry system for item groups,
 * organizing items into logical groups for creative inventory browsing.
 * <p>
 * <b>Tab Organization:</b> Default tab contains all Legacy variant robots
 * and related items with localized titles.
 */
public class LegacyGroups extends InternalGroups {

    // -- Variables --

    /**
     * Default creative tab for Legacy variant items.
     * <p>
     * <b>Contents:</b> Robot cores, spawn eggs, and related items.
     */
    public static final CreativeModeTab DEFAULT_TAB = register(
            LegacyIdentifier.getId(LovelyConstant.DEFAULT_TAB),
            registerGroup(LegacyIdentifier.getTabTranslation(LovelyConstant.DEFAULT_TAB), LegacyItems.ROBOT_CORE)
    );

    /**
     * Registry key for default tab, used for item group event registration.
     */
    public static final ResourceKey<CreativeModeTab> DEFAULT_KEY = registerKey(LegacyIdentifier.getId(LovelyConstant.DEFAULT_TAB));

    // -- Methods --

    /**
     * Registers creative tabs with Fabric registry.
     * <p>
     * <b>Timing:</b> Must be called during mod initialization.
     */
    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(LegacyGroups::addSpawnEggs);
        ItemGroupEvents.modifyEntriesEvent(LegacyGroups.DEFAULT_KEY).register(LegacyGroups::allItemsEntry);

        Legacy.LOGGER.info("Registering CreativeTabs for: " + Legacy.MODID);
    } // register()

    /**
     * Adds spawn eggs to vanilla spawn eggs creative tab.
     */
    private static void addSpawnEggs(FabricItemGroupEntries entries) {
        entries.accept(LegacyItems.BUNNY_SPAWN);
        entries.accept(LegacyItems.BUNNY2_SPAWN);
        entries.accept(LegacyItems.DRAGON_SPAWN);
        entries.accept(LegacyItems.HONEY_SPAWN);
        entries.accept(LegacyItems.KITSUNE_SPAWN);
        entries.accept(LegacyItems.NEKO_SPAWN);
        entries.accept(LegacyItems.VANILLA_SPAWN);
    } // spawnEggItemsEntry()

    /**
     * Adds all items to mod's default creative tab.
     */
    private static void allItemsEntry(FabricItemGroupEntries entries) {
        entries.accept(LegacyItems.ROBOT_CORE);
        entries.accept(LegacyItems.BUNNY_SPAWN);
        entries.accept(LegacyItems.BUNNY2_SPAWN);
        entries.accept(LegacyItems.DRAGON_SPAWN);
        entries.accept(LegacyItems.HONEY_SPAWN);
        entries.accept(LegacyItems.KITSUNE_SPAWN);
        entries.accept(LegacyItems.NEKO_SPAWN);
        entries.accept(LegacyItems.VANILLA_SPAWN);
    } // allItemsEntry()

} // Class: LegacyGroups