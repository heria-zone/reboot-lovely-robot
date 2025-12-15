package net.msymbios.tlovelyr.source.groups;

import net.msymbios.tlovelyr.LovelyTribute;
import net.msymbios.tlovelyr.source.items.DefaultItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class DefaultItemsGroup {

    // -- Variables --

    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LovelyTribute.MODID);

    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> DefaultItems.EXAMPLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(DefaultItems.EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
            }).build());

    // -- Custom Methods --

    public static void register(IEventBus eventBus) {
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(eventBus);
    } // register ()

    public static void registerItems(IEventBus eventBus) {
        // Register the item to a creative tab
        eventBus.addListener(DefaultItemsGroup::addCreative);
    } // addItems ()

    // Add the example block item to the building blocks tab
    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(DefaultItems.EXAMPLE_BLOCK_ITEM);
    } // addCreative ()

} // Class: DefaultItemsGroup