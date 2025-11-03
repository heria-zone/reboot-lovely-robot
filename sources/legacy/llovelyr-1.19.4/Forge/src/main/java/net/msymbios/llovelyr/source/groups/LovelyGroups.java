package net.msymbios.llovelyr.source.groups;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.msymbios.llovelyr.source.items.LovelyItems;

public class LovelyGroups {

    // -- Custom Methods --

    public static void registerItems(IEventBus eventBus) {
        // Register the item to a creative tab
        eventBus.addListener(LovelyGroups::addCreative);
    } // addItems ()

    private static void addCreative(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(LovelyItems.EXAMPLE_BLOCK_ITEM);
    } // addCreative ()

} // Class: LovelyGroups