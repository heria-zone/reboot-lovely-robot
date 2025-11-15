package net.msymbios.rlovelyr.common.item;

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

public class InternalItemsGroup {

    // -- Methods --

    /**
     * Registers an item group with the specified name.
     *
     * @param name The name of the item group
     * @param itemGroup The item group to register
     * @return The registered item group
     * */
    protected static ItemGroup register(Identifier name, ItemGroup itemGroup) {
        // Register the item group using the provided name and item group
        return Registry.register(Registries.ITEM_GROUP, name, itemGroup);
    } // register ()

    /**
     * Registers an item group with the specified translatable name.
     *
     * @param title The translatable name of the item group
     * @return The registered item group
     * */
    protected static ItemGroup registerGroup(String title, Item icon) {
        // Define the item group with the provided title and icon
        return FabricItemGroup.builder().displayName(Text.translatable(title)).icon(() -> new ItemStack(icon)).entries(((displayContext, entries) -> {})).build();
    } // registerGroup ()

    /**
     * Registers an item group with the specified translatable component.
     *
     * @param title The translatable component of the item group
     * @return The registered item group
     * */
    protected static ItemGroup registerGroup(MutableText title, Item icon) {
        // Define the item group with the provided title and icon
        return FabricItemGroup.builder().displayName(title).icon(() -> new ItemStack(icon)).entries(((displayContext, entries) -> {})).build();
    } // registerGroup ()

    /**
     * Registers a registry key for an item group with the specified name.
     *
     * @param name The name of the item group
     * @return The registered registry key for the item group
     */
    protected static RegistryKey<ItemGroup> registerKey(Identifier name) {
        // Create a registry key for the item group with the provided name
        return RegistryKey.of(RegistryKeys.ITEM_GROUP, name);
    } // registerKey ()

} // InternalItemsGroup