package net.heriazone.lovelylib.hzlib.api.items;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.CustomData;
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
    } // register ()

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
    } // register ()

    /**
     * Registers client-side model predicate for dynamic item appearance.
     * <p>
     * <b>Component-Based Switching:</b> Reads integer value from custom data component
     * to select model variant. Minecraft 1.21.1+ uses data components instead of NBT.
     * <p>
     * <b>Model Selection:</b> Item models should define overrides with integer
     * predicates (0-16) directly. The predicate returns the integer as a float.
     * <p>
     * <b>Migration Note:</b> In 1.21.1, NBT tags were replaced with typed data components.
     * Custom data is now stored in DataComponents.CUSTOM_DATA component as a CustomData object.
     * <p>
     * <i>Note:</i> Must be called on client-side only during FMLClientSetupEvent.
     *
     * @param item the item to register predicate for
     * @param tag the resource location identifier for the predicate
     * @param key the component key to read value from (stored in custom data)
     */
    protected static void registerModel(Item item, ResourceLocation tag, String key) {
        ItemProperties.register(item, tag, (stack, world, entity, seed) -> {
            // In 1.21.1, custom data is stored in DataComponents.CUSTOM_DATA
            if (stack.has(DataComponents.CUSTOM_DATA)) {
                CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
                if (customData != null && customData.contains(key)) {
                    return (float) customData.copyTag().getInt(key);
                }
            }
            return 16F;
        });
    } // registerModel ()

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