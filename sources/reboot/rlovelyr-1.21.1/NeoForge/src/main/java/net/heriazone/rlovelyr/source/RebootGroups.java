package net.heriazone.rlovelyr.source;

import net.heriazone.rlovelyr.Reboot;
import net.heriazone.rlovelyr.RebootIdentifier;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.hzlib.api.groups.InternalGroups;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Manages creative mode tabs for Reboot variant items (NeoForge).
 * <p>
 * <b>Architecture:</b> Uses NeoForge's DeferredRegister for creative tabs,
 * organizing items into logical groups for creative inventory browsing.
 * Extends InternalGroups for consistent tab creation patterns.
 * <p>
 * <b>Tab Organization:</b> Default tab contains all Reboot variant robots
 * and related items with localized titles. Spawn eggs also appear in vanilla
 * spawn eggs tab for discoverability.
 */
public class RebootGroups extends InternalGroups {

    // -- Variables --

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = createRegister(Reboot.MODID);

    /**
     * Default creative tab for Reboot variant items.
     * <p>
     * <b>Contents:</b> Robot cores, spawn eggs, and related items.
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DEFAULT_TAB = CREATIVE_MODE_TABS.register(
            LovelyConstant.DEFAULT_TAB,
            () -> createTabBuilder(
                    RebootIdentifier.getTabTranslation(LovelyConstant.DEFAULT_TAB),
                    () -> new ItemStack(RebootItems.ROBOT_CORE.get())
            )
                    .displayItems((parameters, output) -> {
                        output.accept(RebootItems.ROBOT_CORE.get());
                        output.accept(RebootItems.BUNNY_SPAWN.get());
                        output.accept(RebootItems.BUNNY2_SPAWN.get());
                        output.accept(RebootItems.DRAGON_SPAWN.get());
                        output.accept(RebootItems.HONEY_SPAWN.get());
                        output.accept(RebootItems.KITSUNE_SPAWN.get());
                        output.accept(RebootItems.NEKO_SPAWN.get());
                        output.accept(RebootItems.VANILLA_SPAWN.get());
                    })
                    .build()
    );

    // -- Registration Methods --

    /**
     * Registers creative tabs with NeoForge event bus.
     * <p>
     * <b>Timing:</b> Must be called during mod construction before registry events fire.
     *
     * @param eventBus the mod event bus
     */
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
        Reboot.LOGGER.info("Registering CreativeTabs: " + Reboot.MODID);
    } // register()

    /**
     * Registers spawn eggs to vanilla creative tabs.
     * <p>
     * <b>Cross-Tab Population:</b> Adds Reboot spawn eggs to vanilla spawn eggs
     * tab for better discoverability alongside vanilla mob eggs.
     *
     * @param eventBus the mod event bus
     */
    public static void registerItems(IEventBus eventBus) {
        eventBus.addListener(RebootGroups::addSpawnEggs);
    } // registerItems ()

    /**
     * Adds spawn eggs to vanilla spawn eggs creative tab.
     * <p>
     * <b>Event Handler:</b> Listens for BuildCreativeModeTabContentsEvent to
     * populate vanilla tabs with mod items.
     *
     * @param event the tab contents building event
     */
    private static void addSpawnEggs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(RebootItems.BUNNY_SPAWN);
            event.accept(RebootItems.BUNNY2_SPAWN);
            event.accept(RebootItems.DRAGON_SPAWN);
            event.accept(RebootItems.HONEY_SPAWN);
            event.accept(RebootItems.KITSUNE_SPAWN);
            event.accept(RebootItems.NEKO_SPAWN);
            event.accept(RebootItems.VANILLA_SPAWN);
        }
    } // addSpawnEggs ()

} // Class: RebootGroups