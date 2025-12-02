package net.msymbios.llovelyr.source;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.RegistryKey;
import net.msymbios.llovelyr.LovelyLegacy;
import net.msymbios.llovelyr.lib.items.InternalItemsGroup;
import net.msymbios.llovelyr.common.shared.LovelyIdentifier;

/**
 * Manages creative mode tabs for Legacy variant items.
 * <p>
 * <b>Architecture:</b> Uses Fabric's Registry system for item groups,
 * organizing items into logical groups for creative inventory browsing.
 * <p>
 * <b>Tab Organization:</b> Default tab contains all Legacy variant robots
 * and related items with localized titles.
 */
public class LovelyGroups extends InternalItemsGroup {

    // -- Variables --

    /**
     * Default creative tab for Legacy variant items.
     * <p>
     * <b>Contents:</b> Robot cores, spawn eggs, and related items.
     */
    public static final ItemGroup DEFAULT_TAB = register(
            LovelyIdentifier.getId(LovelyIdentifier.DEFAULT_TAB),
        registerGroup(LovelyIdentifier.getTabTranslation(LovelyIdentifier.DEFAULT_TAB), LovelyItems.ROBOT_CORE)
    );

    /**
     * Registry key for default tab, used for item group event registration.
     */
    public static final RegistryKey<ItemGroup> DEFAULT_KEY = registerKey(LovelyIdentifier.getId(LovelyIdentifier.DEFAULT_TAB));

    // -- Methods --

    /**
     * Registers creative tabs with Fabric registry.
     * <p>
     * <b>Timing:</b> Must be called during mod initialization.
     */
    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(LovelyGroups::addSpawnEggs);
        ItemGroupEvents.modifyEntriesEvent(LovelyGroups.DEFAULT_KEY).register(LovelyGroups::allItemsEntry);

        LovelyLegacy.LOGGER.info("Registering CreativeTabs for: " + LovelyLegacy.MODID);
    } // register()

    /**
     * Adds spawn eggs to vanilla spawn eggs creative tab.
     */
    private static void addSpawnEggs(FabricItemGroupEntries entries) {
        entries.add(LovelyItems.BUNNY_SPAWN);
        entries.add(LovelyItems.BUNNY2_SPAWN);
        entries.add(LovelyItems.DRAGON_SPAWN);
        entries.add(LovelyItems.HONEY_SPAWN);
        entries.add(LovelyItems.KITSUNE_SPAWN);
        entries.add(LovelyItems.NEKO_SPAWN);
        entries.add(LovelyItems.VANILLA_SPAWN);
    } // spawnEggItemsEntry()

    /**
     * Adds all items to mod's default creative tab.
     */
    private static void allItemsEntry(FabricItemGroupEntries entries) {
        entries.add(LovelyItems.ROBOT_CORE);
        entries.add(LovelyItems.BUNNY_SPAWN);
        entries.add(LovelyItems.BUNNY2_SPAWN);
        entries.add(LovelyItems.DRAGON_SPAWN);
        entries.add(LovelyItems.HONEY_SPAWN);
        entries.add(LovelyItems.KITSUNE_SPAWN);
        entries.add(LovelyItems.NEKO_SPAWN);
        entries.add(LovelyItems.VANILLA_SPAWN);
    } // allItemsEntry()

} // Class: LovelyGroups
