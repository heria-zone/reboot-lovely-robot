package net.heriazone.rlovelyr.source;

import net.heriazone.rlovelyr.Reboot;
import net.heriazone.rlovelyr.RebootIdentifier;
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
 * Creative tab management for Reboot variant items (NeoForge).
 * <p>
 * <b>Architecture:</b> Mirrors LegacyGroups NeoForge — only modid and
 * ModTarget.REBOOT differ. Registry-driven loops keep both tabs in sync.
 */
public class RebootGroups extends InternalGroups {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
        createRegister(Reboot.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DEFAULT_TAB =
        CREATIVE_MODE_TABS.register(LovelyConstant.DEFAULT_TAB,
            () -> createTabBuilder(
                    RebootIdentifier.getTabTranslation(LovelyConstant.DEFAULT_TAB),
                    () -> new ItemStack(RebootItems.ROBOT_CORE.get()))
                .displayItems((params, output) -> {
                    output.accept(RebootItems.ROBOT_CORE.get());
                    for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.REBOOT)) {
                        output.accept(RebootItems.getSpawnItem(def.getVariant()).get());
                    }
                })
                .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
        Reboot.LOGGER.info("Registering CreativeTabs: " + Reboot.MODID);
    } // register()

    public static void registerItems(IEventBus eventBus) {
        eventBus.addListener(RebootGroups::addSpawnEggs);
    } // registerItems()

    private static void addSpawnEggs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.REBOOT)) {
                event.accept(RebootItems.getSpawnItem(def.getVariant()));
            }
        }
    } // addSpawnEggs()

} // Class: RebootGroups
