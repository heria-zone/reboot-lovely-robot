package net.heriazone.llovelyr.source;

import net.heriazone.llovelyr.Legacy;
import net.heriazone.llovelyr.LegacyIdentifier;
import net.heriazone.lovelylib.common.entity.definition.ModTarget;
import net.heriazone.lovelylib.common.entity.definition.RobotDefinitionRegistry;
import net.heriazone.lovelylib.common.entity.definition.RobotEntityDefinition;
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
 * Creative tab management for Legacy variant items (NeoForge).
 * <p>
 * <b>Architecture:</b> Mirrors the Forge implementation using NeoForge's
 * DeferredHolder. Both displayItems and addSpawnEggs are registry-driven.
 */
public class LegacyGroups extends InternalGroups {

    // -- State --

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
        createRegister(Legacy.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DEFAULT_TAB =
        CREATIVE_MODE_TABS.register(LovelyConstant.DEFAULT_TAB,
            () -> createTabBuilder(
                    LegacyIdentifier.getTabTranslation(LovelyConstant.DEFAULT_TAB),
                    () -> new ItemStack(LegacyItems.ROBOT_CORE.get()))
                .displayItems((params, output) -> {
                    output.accept(LegacyItems.ROBOT_CORE.get());
                    for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
                        output.accept(LegacyItems.getSpawnItem(def.getVariant()).get());
                    }
                })
                .build());

    // -- Registration --

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
        Legacy.LOGGER.info("Registering CreativeTabs: " + Legacy.MODID);
    } // register()

    public static void registerItems(IEventBus eventBus) {
        eventBus.addListener(LegacyGroups::addSpawnEggs);
    } // registerItems()

    private static void addSpawnEggs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
                event.accept(LegacyItems.getSpawnItem(def.getVariant()));
            }
        }
    } // addSpawnEggs()

} // Class: LegacyGroups
