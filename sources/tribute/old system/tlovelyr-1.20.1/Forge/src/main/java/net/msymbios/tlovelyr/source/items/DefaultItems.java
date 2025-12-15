package net.msymbios.tlovelyr.source.items;

import net.msymbios.tlovelyr.LovelyTribute;
import net.msymbios.tlovelyr.source.blocks.DefaultBlocks;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DefaultItems {

    // -- Variables --

    // Create a Deferred Register to hold DefaultItems which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LovelyTribute.MODID);

    // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(DefaultBlocks.EXAMPLE_BLOCK.get(), new Item.Properties()));

    // Creates a new food item with the id "examplemod:example_id", nutrition 1 and saturation 2
    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEat().nutrition(1).saturationMod(2f).build())));

    // -- Custom Methods --

    public static void register(IEventBus eventBus) {
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(eventBus);
    } // register ()

} // Class: DefaultItems