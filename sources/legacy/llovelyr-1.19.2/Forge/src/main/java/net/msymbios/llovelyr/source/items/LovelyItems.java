package net.msymbios.llovelyr.source.items;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.msymbios.llovelyr.LovelyLegacy;
import net.msymbios.llovelyr.source.blocks.LovelyBlocks;

public class LovelyItems {

    // -- Variables --

    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LovelyLegacy.MODID);

    // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(LovelyBlocks.EXAMPLE_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));

    // -- Methods --

    public static void register (IEventBus eventBus) {
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(eventBus);
    } // register ()

} // Class: LovelyItems