package net.msymbios.llovelyr.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.registry.RegistryKey;
import net.msymbios.llovelyr.LovelyLegacy;
import net.msymbios.llovelyr.common.item.InternalItemsGroup;
import net.msymbios.llovelyr.source.configs.LovelyIdentifier;

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
        LovelyLegacy.LOGGER.info("Registering CreativeTabs for: " + LovelyLegacy.MODID);
    } // register()

} // Class: LovelyGroups
