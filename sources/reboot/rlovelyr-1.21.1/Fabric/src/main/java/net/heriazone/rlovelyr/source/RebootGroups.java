package net.heriazone.rlovelyr.source;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.heriazone.rlovelyr.Reboot;
import net.heriazone.rlovelyr.RebootIdentifier;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.lovelylib.hzlib.api.groups.InternalGroups;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

/**
 * Manages creative mode tabs for Reboot variant items.
 * <p>
 * <b>Architecture:</b> Uses Fabric's Registry system for item groups,
 * organizing items into logical groups for creative inventory browsing.
 * <p>
 * <b>Tab Organization:</b> Default tab contains all Reboot variant robots
 * and related items with localized titles.
 */
public class RebootGroups extends InternalGroups {

    // -- Variables --

    /**
     * Default creative tab for Reboot variant items.
     * <p>
     * <b>Contents:</b> Robot cores, spawn eggs, and related items.
     */
    public static final CreativeModeTab DEFAULT_TAB = register(
            RebootIdentifier.getId(LovelyConstant.DEFAULT_TAB),
            registerGroup(RebootIdentifier.getTabTranslation(LovelyConstant.DEFAULT_TAB), RebootItems.ROBOT_CORE)
    );

    /**
     * Registry key for default tab, used for item group event registration.
     */
    public static final ResourceKey<CreativeModeTab> DEFAULT_KEY = registerKey(RebootIdentifier.getId(LovelyConstant.DEFAULT_TAB));

    // -- Methods --

    /**
     * Registers creative tabs with Fabric registry.
     * <p>
     * <b>Timing:</b> Must be called during mod initialization.
     */
    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(RebootGroups::addSpawnEggs);
        ItemGroupEvents.modifyEntriesEvent(RebootGroups.DEFAULT_KEY).register(RebootGroups::allItemsEntry);

        Reboot.LOGGER.info("Registering CreativeTabs for: " + Reboot.MODID);
    } // register()

    /**
     * Adds spawn eggs to vanilla spawn eggs creative tab.
     */
    private static void addSpawnEggs(FabricItemGroupEntries entries) {
        entries.accept(RebootItems.BUNNY_SPAWN);
        entries.accept(RebootItems.BUNNY2_SPAWN);
        entries.accept(RebootItems.DRAGON_SPAWN);
        entries.accept(RebootItems.HONEY_SPAWN);
        entries.accept(RebootItems.KITSUNE_SPAWN);
        entries.accept(RebootItems.NEKO_SPAWN);
        entries.accept(RebootItems.VANILLA_SPAWN);
    } // spawnEggItemsEntry()

    /**
     * Adds all items to mod's default creative tab.
     */
    private static void allItemsEntry(FabricItemGroupEntries entries) {
        entries.accept(RebootItems.ROBOT_CORE);
        entries.accept(RebootItems.BUNNY_SPAWN);
        entries.accept(RebootItems.BUNNY2_SPAWN);
        entries.accept(RebootItems.DRAGON_SPAWN);
        entries.accept(RebootItems.HONEY_SPAWN);
        entries.accept(RebootItems.KITSUNE_SPAWN);
        entries.accept(RebootItems.NEKO_SPAWN);
        entries.accept(RebootItems.VANILLA_SPAWN);
    } // allItemsEntry()

} // Class: RebootGroups