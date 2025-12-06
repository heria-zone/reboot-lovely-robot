package net.msymbios.llovelyr.lib.groups;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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
public class InternalGroups {

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
    protected static CreativeModeTab register(ResourceLocation name, CreativeModeTab itemGroup) {
        return Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, name, itemGroup);
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
    protected static CreativeModeTab registerGroup(String title, Item icon) {
        return FabricItemGroup.builder()
                .title(Component.translatable(title))
                .icon(() -> new ItemStack(icon))
                .displayItems((displayContext, entries) -> {})
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
    protected static CreativeModeTab registerGroup(MutableComponent title, Item icon) {
        return FabricItemGroup.builder()
                .title(title)
                .icon(() -> new ItemStack(icon))
                .displayItems((displayContext, entries) -> {})
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
    protected static ResourceKey<CreativeModeTab> registerKey(ResourceLocation name) {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, name);
    } // registerKey()

} // Class: InternalGroups
