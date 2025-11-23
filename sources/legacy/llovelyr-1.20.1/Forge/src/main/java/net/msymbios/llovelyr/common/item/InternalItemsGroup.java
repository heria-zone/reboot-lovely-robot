package net.msymbios.llovelyr.common.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Forge-specific creative tab registration abstraction.
 * <p>
 * <b>Architecture:</b> Wraps Forge's DeferredRegister system for creative tabs,
 * providing consistent API across mod loaders. Supports translatable titles and
 * custom icon items.
 * <p>
 * <b>Design Pattern:</b> Uses builder pattern for tab construction, allowing
 * flexible configuration of display properties and item population.
 */
public class InternalItemsGroup {

    // -- Methods --

    /**
     * Registers creative tab with Forge's deferred registration system.
     * <p>
     * <b>Usage:</b> Call during mod initialization before registry events fire.
     *
     * @param register the deferred register instance for creative tabs
     * @param name the registry name for the creative tab
     * @param itemGroup the creative tab instance to register
     * @return registry object wrapping the registered creative tab
     */
    protected static RegistryObject<CreativeModeTab> register(DeferredRegister<CreativeModeTab> register, ResourceLocation name, CreativeModeTab itemGroup) {
        return register.register(name.getPath(), () -> itemGroup);
    } // register()

    /**
     * Creates creative tab with translatable title string.
     * <p>
     * <b>Localization:</b> Title string should be translation key (e.g.,
     * "itemGroup.modid.tabname") resolved from language files.
     *
     * @param title the translatable title key
     * @param icon the item to display as tab icon
     * @return configured creative tab builder result
     */
    protected static CreativeModeTab registerGroup(String title, Item icon) {
        return CreativeModeTab.builder()
            .title(Component.translatable(title))
            .icon(() -> new ItemStack(icon))
            .build();
    } // registerGroup()

    /**
     * Creates creative tab with pre-constructed component title.
     * <p>
     * <b>Usage:</b> Allows custom component styling beyond simple translation.
     *
     * @param title the mutable component for tab title
     * @param icon the item to display as tab icon
     * @return configured creative tab builder result
     */
    protected static CreativeModeTab registerGroup(MutableComponent title, Item icon) {
        return CreativeModeTab.builder()
            .title(title)
            .icon(() -> new ItemStack(icon))
            .build();
    } // registerGroup()

    /**
     * Creates registry key for creative tab reference.
     * <p>
     * <b>Usage:</b> Required for tab registration and cross-referencing in
     * item definitions.
     *
     * @param name the resource location for the tab
     * @return registry key for the creative tab
     */
    protected static ResourceKey<CreativeModeTab> registerKey(ResourceLocation name) {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, name);
    } // registerKey()

} // Class: InternalItemsGroup
