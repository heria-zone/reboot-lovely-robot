package net.msymbios.rlovelyr.common.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.config.LovelyRobotID;

public class InternalItems {

    // -- Methods --

    protected static Item register(Identifier name, Item item) {
        return Registry.register(Registries.ITEM, name, item);
    } // register ()

    protected static Item register(Identifier name, Block block) {
        return Registry.register(Registries.ITEM, name, new BlockItem(block, new FabricItemSettings()));
    } // register

    protected static void registerModel(Item item, String tag, String key) {
        // Use ModelPredicateProviderRegistry to register custom model predicate
        // Check if item stack has the tag and return the value for the model predicate
        ModelPredicateProviderRegistry.register(item, LovelyRobotID.getId(tag), (stack, world, entity, seed) -> stack.getNbt() != null && stack.getNbt().contains(key) ? stack.getNbt().getInt(key) : 16);
    } // registerModel ()

    protected static void registerModel(Item item, Identifier tag, String key) {
        // Use ModelPredicateProviderRegistry to register custom model predicate
        // Check if item stack has the tag and return the value for the model predicate
        ModelPredicateProviderRegistry.register(item, tag, (stack, world, entity, seed) -> stack.getNbt() != null && stack.getNbt().contains(key) ? stack.getNbt().getInt(key) : 16);
    } // registerModel ()

} // InternalItems