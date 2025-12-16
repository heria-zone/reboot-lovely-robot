package net.heriazone.llovelyr.source;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.heriazone.llovelyr.Legacy;
import net.heriazone.llovelyr.LegacyIdentifier;
import net.heriazone.lovelylib.hzlib.api.groups.InternalGroups;
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
public class LovelyGroups extends InternalGroups {

    // -- Variables --

    /**
     * Default creative tab for Legacy variant items.
     * <p>
     * <b>Contents:</b> Robot cores, spawn eggs, and related items.
     */
    public static final CreativeModeTab DEFAULT_TAB = register(
            LegacyIdentifier.getId(LegacyIdentifier.DEFAULT_TAB),
            registerGroup(LegacyIdentifier.getTabTranslation(LegacyIdentifier.DEFAULT_TAB), LovelyItems.ROBOT_CORE)
    );

    /**
     * Registry key for default tab, used for item group event registration.
     */
    public static final ResourceKey<CreativeModeTab> DEFAULT_KEY = registerKey(LegacyIdentifier.getId(LegacyIdentifier.DEFAULT_TAB));

    // -- Methods --

    /**
     * Registers creative tabs with Fabric registry.
     * <p>
     * <b>Timing:</b> Must be called during mod initialization.
     */
    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(LovelyGroups::addSpawnEggs);
        ItemGroupEvents.modifyEntriesEvent(LovelyGroups.DEFAULT_KEY).register(LovelyGroups::allItemsEntry);

        Legacy.LOGGER.info("Registering CreativeTabs for: " + Legacy.MODID);
    } // register()

    /**
     * Adds spawn eggs to vanilla spawn eggs creative tab.
     */
    private static void addSpawnEggs(FabricItemGroupEntries entries) {
        entries.accept(LovelyItems.BUNNY_SPAWN);
        entries.accept(LovelyItems.BUNNY2_SPAWN);
        entries.accept(LovelyItems.DRAGON_SPAWN);
        entries.accept(LovelyItems.HONEY_SPAWN);
        entries.accept(LovelyItems.KITSUNE_SPAWN);
        entries.accept(LovelyItems.NEKO_SPAWN);
        entries.accept(LovelyItems.VANILLA_SPAWN);
    } // spawnEggItemsEntry()

    /**
     * Adds all items to mod's default creative tab.
     */
    private static void allItemsEntry(FabricItemGroupEntries entries) {
        entries.accept(LovelyItems.ROBOT_CORE);
        entries.accept(LovelyItems.BUNNY_SPAWN);
        entries.accept(LovelyItems.BUNNY2_SPAWN);
        entries.accept(LovelyItems.DRAGON_SPAWN);
        entries.accept(LovelyItems.HONEY_SPAWN);
        entries.accept(LovelyItems.KITSUNE_SPAWN);
        entries.accept(LovelyItems.NEKO_SPAWN);
        entries.accept(LovelyItems.VANILLA_SPAWN);
    } // allItemsEntry()

} // Class: LovelyGroups