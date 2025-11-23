package net.msymbios.llovelyr.common.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

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
     * @param name the registry identifier for the item
     * @param item the item instance to register
     * @return the registered item instance
     */
    protected static Item register(Identifier name, Item item) {
        return Registry.register(Registries.ITEM, name, item);
    } // register()

    /**
     * Registers block item with default Fabric properties.
     * <p>
     * <b>Usage:</b> Automatically creates BlockItem wrapper for placeable blocks.
     * 
     * @param name the registry identifier for the block item
     * @param block the block to create item for
     * @return the registered block item instance
     */
    protected static Item register(Identifier name, Block block) {
        return Registry.register(Registries.ITEM, name, new BlockItem(block, new FabricItemSettings()));
    } // register()

    /**
     * Registers client-side model predicate for specific item.
     * <p>
     * <b>NBT-Based Switching:</b> Reads integer value from item NBT to select
     * model variant. Returns 16 as default when NBT key missing (fallback model).
     * <p>
     * <b>Model Selection:</b> Item models should define overrides matching NBT
     * values (0-15 for colors, 16 for random/default).
     * <p>
     * <i>Note:</i> Must be called on client-side only during initialization.
     * 
     * @param item the item to register predicate for
     * @param tag the identifier for the predicate
     * @param key the NBT key to read value from
     */
    protected static void registerModel(Item item, Identifier tag, String key) {
        ModelPredicateProviderRegistry.register(item, tag, (stack, world, entity, seed) ->
                stack.getNbt() != null && stack.getNbt().contains(key) ? stack.getNbt().getInt(key) : 16
        );
    } // registerModel()

    /**
     * Registers client-side model predicate using string tag.
     * <p>
     * <b>Usage:</b> Convenience overload that converts string to Identifier.
     * 
     * @param item the item to register predicate for
     * @param tag the string identifier (converted to Identifier)
     * @param key the NBT key to read value from
     */
    protected static void registerModel(Item item, String tag, String key) {
        registerModel(item, new Identifier(tag), key);
    } // registerModel()

    /**
     * Registers global model predicate provider for all items.
     * <p>
     * <b>Architecture:</b> Registers predicate at registry level rather than
     * per-item, allowing centralized model switching logic. All items using
     * this tag will share the same NBT-based model selection behavior.
     * <p>
     * <b>Usage Pattern:</b> Call once during client init to establish global
     * predicate, then register individual items to use it.
     * <p>
     * <i>Note:</i> Must be called on client-side only during initialization.
     * 
     * @param tag the identifier for the predicate
     * @param key the NBT key to read value from
     */
    protected static void registerModel(Identifier tag, String key) {
        ModelPredicateProviderRegistry.register(tag, (stack, world, entity, seed) ->
                stack.getNbt() != null && stack.getNbt().contains(key) ? stack.getNbt().getInt(key) : 16
        );
    } // registerModel()

} // Class: InternalItems