package net.heriazone.rlovelyr.source;

import net.heriazone.rlovelyr.Reboot;
import net.heriazone.rlovelyr.RebootIdentifier;
import net.heriazone.lovelylib.common.entity.definition.ModTarget;
import net.heriazone.lovelylib.common.entity.definition.RobotDefinitionRegistry;
import net.heriazone.lovelylib.common.entity.definition.RobotEntityDefinition;
import net.heriazone.lovelylib.common.entity.definition.RobotVariant;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.hzlib.api.groups.InternalGroups;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

/**
 * Creative tab management for Reboot robots (Forge).
 * <p>
 * <b>Two tabs:</b> DEFAULT_TAB holds the standard shared roster (Bunny, Dragon, etc.).
 * ALDARIAN_TECH_TAB holds Prime, Hyperion, and Empyrium exclusively, using
 * robot_core_aldarian as its icon to visually distinguish the tier.
 */
public class RebootGroups extends InternalGroups {

    // Aldarian-exclusive variants — only these three appear in the Aldarian Tech tab
    private static final Set<RobotVariant> ALDARIAN_VARIANTS = Set.of(
        RobotVariant.Prime, RobotVariant.Hyperion, RobotVariant.Empyrium
    );

    // -- State --

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
        createRegister(Reboot.MODID);

    public static final RegistryObject<CreativeModeTab> DEFAULT_TAB =
        CREATIVE_MODE_TABS.register(LovelyConstant.DEFAULT_TAB,
            () -> createTabBuilder(
                    RebootIdentifier.getTabTranslation(LovelyConstant.DEFAULT_TAB),
                    () -> new ItemStack(RebootItems.ROBOT_CORE.get()))
                .displayItems((params, output) -> {
                    output.accept(RebootItems.ROBOT_CORE.get());
                    for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.REBOOT)) {
                        if (!ALDARIAN_VARIANTS.contains(def.getVariant())) {
                            output.accept(RebootItems.getSpawnItem(def.getVariant()).get());
                        }
                    }
                })
                .build());

    /**
     * Aldarian Tech tab — exclusive to Prime, Hyperion, and Empyrium.
     * Icon uses robot_core_aldarian to visually mark the Aldarian tier.
     */
    public static final RegistryObject<CreativeModeTab> ALDARIAN_TECH_TAB =
        CREATIVE_MODE_TABS.register(LovelyConstant.ALDARIAN_TECH_TAB,
            () -> createTabBuilder(
                    RebootIdentifier.getTabTranslation(LovelyConstant.ALDARIAN_TECH_TAB),
                    () -> new ItemStack(RebootItems.ROBOT_CORE_ALDARIAN.get()))
                .displayItems((params, output) -> {
                    output.accept(RebootItems.ROBOT_CORE_ALDARIAN.get());
                    for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.REBOOT)) {
                        if (ALDARIAN_VARIANTS.contains(def.getVariant())) {
                            output.accept(RebootItems.getSpawnItem(def.getVariant()).get());
                        }
                    }
                })
                .build());

    // -- Registration --

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
