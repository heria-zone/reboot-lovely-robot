package net.msymbios.llovelyr.lib.groups;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Forge-specific creative tab registration abstraction.
 * <p>
 * <b>Architecture:</b> Wraps Forge's DeferredRegister system for creative tabs,
 * providing consistent API across mod loaders. Extracts common patterns from
 * tab creation to reduce boilerplate in variant implementations.
 * <p>
 * <b>Design Pattern:</b> Provides protected helper methods that subclasses use
 * to simplify tab registration with consistent builder patterns.
 */
public abstract class InternalGroups {

    // -- Tab Builder Helper Methods --

    /**
     * Creates creative tab builder with component title and item supplier icon.
     * <p>
     * <b>Usage:</b> Foundation for all tab creation. Returns builder for further
     * customization with displayItems() before calling build().
     *
     * @param title the mutable component for tab title
     * @param iconSupplier supplier providing the icon item
     * @return creative tab builder for further configuration
     */
    protected static CreativeModeTab.Builder createTabBuilder(MutableComponent title, Supplier<ItemStack> iconSupplier) {
        return CreativeModeTab.builder()
                .title(title)
                .icon(iconSupplier);
    } // createTabBuilder ()

    // -- Utility Methods --

    /**
     * Creates registry key for creative tab reference.
     * <p>
     * <b>Usage:</b> Required for tab registration and cross-referencing in
     * item definitions or when adding items to vanilla tabs.
     *
     * @param name the resource location for the tab
     * @return registry key for the creative tab
     */
    protected static ResourceKey<CreativeModeTab> createKey(ResourceLocation name) {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, name);
    } // createKey ()

    /**
     * Creates DeferredRegister for creative mode tabs.
     * <p>
     * <b>Usage:</b> Call during class initialization to create the registry.
     *
     * @param modId the mod identifier
     * @return deferred register for creative tabs
     */
    protected static DeferredRegister<CreativeModeTab> createRegister(String modId) {
        return DeferredRegister.create(Registries.CREATIVE_MODE_TAB, modId);
    } // createRegister ()

} // Class: InternalGroups