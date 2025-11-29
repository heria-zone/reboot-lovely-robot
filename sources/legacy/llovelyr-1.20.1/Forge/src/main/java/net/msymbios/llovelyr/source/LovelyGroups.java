package net.msymbios.llovelyr.source;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.msymbios.llovelyr.LovelyLegacy;
import net.msymbios.llovelyr.common.shared.LovelyIdentifier;

/**
 * Manages creative mode tabs for Legacy variant items.
 * <p>
 * <b>Architecture:</b> Uses Forge's DeferredRegister for creative tabs,
 * organizing items into logical groups for creative inventory browsing.
 * <p>
 * <b>Tab Organization:</b> Default tab contains all Legacy variant robots
 * and related items with localized titles.
 */
public class LovelyGroups {

    // -- Variables --

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LovelyLegacy.MODID);

    /**
     * Default creative tab for Legacy variant items.
     * <p>
     * <b>Contents:</b> Robot cores, spawn eggs, and related items.
     */
    public static final RegistryObject<CreativeModeTab> DEFAULT_TAB = CREATIVE_MODE_TABS.register(LovelyIdentifier.DEFAULT_TAB, () -> CreativeModeTab.builder()
            .title(LovelyIdentifier.getTabTranslation(LovelyIdentifier.DEFAULT_TAB))
            .icon(() -> new ItemStack(LovelyItems.ROBOT_CORE.get()))
            .displayItems((parameters, output) -> {
                output.accept(LovelyItems.ROBOT_CORE.get());
                output.accept(LovelyItems.VANILLA_SPAWN.get());
                output.accept(LovelyItems.BUNNY2_SPAWN.get());
            })
            .build()
    );

    // -- Custom Methods --

    /**
     * Registers creative tabs with Forge event bus.
     * <p>
     * <b>Timing:</b> Must be called during mod construction before registry events fire.
     *
     * @param eventBus the mod event bus
     */
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
        LovelyLegacy.LOGGER.info("Registering CreativeTabs for: " + LovelyLegacy.MODID);
    } // register()

    public static void registerItems(IEventBus eventBus) {
        // Register the item to a creative tab
        eventBus.addListener(LovelyGroups::addSpawnEggs);
    } // addItems ()

    // Add the example block item to the building blocks tab
    private static void addSpawnEggs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS){
            event.accept(LovelyItems.BUNNY2_SPAWN);
            event.accept(LovelyItems.VANILLA_SPAWN);
        }
    } // addSpawnEggs ()

} // Class: LovelyGroups