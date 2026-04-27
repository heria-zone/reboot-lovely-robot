package net.heriazone.hzlib.api.items;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;

/**
 * Fabric-specific item registration abstraction with model predicate support.
 * <p>
 * <b>Architecture:</b> Wraps Fabric's Registry system to provide consistent
 * registration API across mod loaders. Handles both standard items and block
 * items with unified interface.
 * <p>
 * <b>Model Predicates:</b> Supports NBT-based model switching for dynamic
 * item appearances (e.g., robot color variants stored in item NBT).
 */
public class InternalItems {

    // -- Methods --

    /**
     * Registers item with Fabric's registry system.
     * <p>
     * <b>Usage:</b> Call during mod initialization phase.
     *
     * @param name the registry resourceLocation for the item
     * @param item the item instance to register
     * @return the registered item instance
     */
    protected static Item register(ResourceLocation name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, name, item);
    } // register()

    /**
     * Registers block item with default Fabric properties.
     * <p>
     * <b>Usage:</b> Automatically creates BlockItem wrapper for placeable blocks.
     *
     * @param name the registry resourceLocation for the block item
     * @param block the block to create item for
     * @return the registered block item instance
     */
    protected static Item register(ResourceLocation name, Block block) {
        return Registry.register(BuiltInRegistries.ITEM, name, new BlockItem(block, new Item.Properties()));
    } // register()

    /**
     * Registers client-side model predicate for specific item.
     * <p>
     * <b>NBT-Based Switching:</b> Reads integer value from item NBT to select
     * model variant. Normalizes to 0.0-1.0 range for proper predicate matching.
     * <p>
     * <b>Model Selection:</b> Item models should define overrides with normalized
     * values (0/17, 1/17, 2/17, etc.) for colors 0-16.
     * <p>
     * <i>Note:</i> Must be called on client-side only during initialization.
     *
     * @param item the item to register predicate for
     * @param tag the resourceLocation for the predicate
     * @param key the NBT key to read value from
     */
    protected static void registerModel(Item item, ResourceLocation tag, String key) {
        ItemProperties.register(item, tag, (stack, world, entity, seed) -> {
            // In 1.21.1, custom data is stored in DataComponents.CUSTOM_DATA
            if (stack.has(DataComponents.CUSTOM_DATA)) {
                CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
                if (customData != null && customData.contains(key)) {
                    return (float) customData.copyTag().getInt(key) / 17F;
                }
            }
            return 16F / 17F;
        });
    } // registerModel()

    /**
     * Convenience overload accepting string tag converted to ResourceLocation.
     *
     * @param item the item to register predicate for
     * @param tag the string identifier (converted to ResourceLocation)
     * @param key the component key to read value from
     */
    protected static void registerModel(Item item, String tag, String key) {
        registerModel(item, ResourceLocation.withDefaultNamespace(tag), key);
    } // registerModel ()

} // Class: InternalItems