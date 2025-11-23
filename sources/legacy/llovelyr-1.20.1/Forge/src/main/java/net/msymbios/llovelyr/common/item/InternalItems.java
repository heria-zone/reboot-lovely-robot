package net.msymbios.llovelyr.common.item;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Forge-specific item registration abstraction with model predicate support.
 * <p>
 * <b>Architecture:</b> Wraps Forge's DeferredRegister system to provide
 * consistent registration API across mod loaders. Handles both standard items
 * and block items with unified interface.
 * <p>
 * <b>Model Predicates:</b> Supports NBT-based model switching for dynamic
 * item appearances (e.g., robot color variants stored in item NBT).
 */
public class InternalItems {

    // -- Methods --

    /**
     * Registers item with Forge's deferred registration system.
     * <p>
     * <b>Usage:</b> Call during mod initialization before registry events fire.
     * 
     * @param register the deferred register instance
     * @param name the registry name for the item
     * @param item the item instance to register
     * @return registry object wrapping the registered item
     */
    protected static RegistryObject<Item> register(DeferredRegister<Item> register, ResourceLocation name, Item item) {
        return register.register(name.getPath(), () -> item);
    } // register()

    /**
     * Registers block item with default properties.
     * <p>
     * <b>Usage:</b> Automatically creates BlockItem wrapper for placeable blocks.
     * 
     * @param register the deferred register instance
     * @param name the registry name for the block item
     * @param block the block to create item for
     * @return registry object wrapping the registered block item
     */
    protected static RegistryObject<Item> register(DeferredRegister<Item> register, ResourceLocation name, Block block) {
        return register.register(name.getPath(), () -> new BlockItem(block, new Item.Properties()));
    } // register()

    /**
     * Registers client-side model predicate for dynamic item appearance.
     * <p>
     * <b>NBT-Based Switching:</b> Reads integer value from item NBT to select
     * model variant. Converts integer (0-16) to float for predicate matching.
     * <p>
     * <b>Model Selection:</b> Item models should define overrides with integer
     * predicates (0-16) directly. The predicate returns the integer as a float.
     * <p>
     * <i>Note:</i> Must be called on client-side only during FMLClientSetupEvent.
     * 
     * @param item the item to register predicate for
     * @param tag the resource location identifier for the predicate
     * @param key the NBT key to read value from
     */
    protected static void registerModel(Item item, ResourceLocation tag, String key) {
        ItemProperties.register(item, tag, (stack, world, entity, seed) -> {
            if (stack.hasTag() && stack.getTag().contains(key)) {
                return (float) stack.getTag().getInt(key);
            }
            return 16.0f;
        });
    } // registerModel()

    /**
     * Convenience overload accepting string tag converted to ResourceLocation.
     * 
     * @param item the item to register predicate for
     * @param tag the string identifier (converted to ResourceLocation)
     * @param key the NBT key to read value from
     */
    protected static void registerModel(Item item, String tag, String key) {
        registerModel(item, new ResourceLocation(tag), key);
    } // registerModel()

} // Class: InternalItems