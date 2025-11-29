package net.msymbios.llovelyr.common.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Fabric-specific creative tab registration abstraction.
 * <p>
 * <b>Architecture:</b> Wraps Fabric's Registry system for item groups, providing
 * consistent API across mod loaders. Supports translatable titles and custom
 * icon items.
 * <p>
 * <b>Design Pattern:</b> Uses builder pattern for tab construction, allowing
 * flexible configuration of display properties and item population.
 */
public class InternalItemsGroup {

    // -- Methods --

    /**
     * Registers item group with Fabric's registry system.
     * <p>
     * <b>Usage:</b> Call during mod initialization phase.
     *
     * @param name the identifier for the item group
     * @param itemGroup the item group instance to register
     * @return the registered item group instance
     */
    protected static ItemGroup register(Identifier name, ItemGroup itemGroup) {
        return Registry.register(Registries.ITEM_GROUP, name, itemGroup);
    } // register()

    /**
     * Creates item group with translatable title string.
     * <p>
     * <b>Localization:</b> Title string should be translation key (e.g.,
     * "itemGroup.modid.tabname") resolved from language files.
     *
     * @param title the translatable title key
     * @param icon the item to display as tab icon
     * @return configured item group builder result
     */
    protected static ItemGroup registerGroup(String title, Item icon) {
        return FabricItemGroup.builder()
            .displayName(Text.translatable(title))
            .icon(() -> new ItemStack(icon))
            .entries(((displayContext, entries) -> {}))
            .build();
    } // registerGroup()

    /**
     * Creates item group with pre-constructed text component.
     * <p>
     * <b>Usage:</b> Allows custom component styling beyond simple translation.
     *
     * @param title the mutable text component for group title
     * @param icon the item to display as tab icon
     * @return configured item group builder result
     */
    protected static ItemGroup registerGroup(MutableText title, Item icon) {
        return FabricItemGroup.builder()
            .displayName(title)
            .icon(() -> new ItemStack(icon))
            .entries(((displayContext, entries) -> {}))
            .build();
    } // registerGroup()

    /**
     * Creates registry key for item group reference.
     * <p>
     * <b>Usage:</b> Required for group registration and cross-referencing in
     * item definitions.
     *
     * @param name the identifier for the group
     * @return registry key for the item group
     */
    protected static RegistryKey<ItemGroup> registerKey(Identifier name) {
        return RegistryKey.of(RegistryKeys.ITEM_GROUP, name);
    } // registerKey()

} // Class: InternalItemsGroup
