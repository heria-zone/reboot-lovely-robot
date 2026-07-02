package net.heriazone.rlovelyr.source;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.heriazone.rlovelyr.Reboot;
import net.heriazone.rlovelyr.RebootIdentifier;
import net.heriazone.lovelylib.common.entity.definition.ModTarget;
import net.heriazone.lovelylib.common.entity.definition.RobotDefinitionRegistry;
import net.heriazone.lovelylib.common.entity.definition.RobotEntityDefinition;
import net.heriazone.lovelylib.common.entity.definition.RobotVariant;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.hzlib.api.groups.InternalGroups;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

import java.util.Set;

/**
 * Creative tab management for Reboot robots (Fabric).
 * <p>
 * <b>Two tabs:</b> DEFAULT_TAB for the standard shared roster;
 * ALDARIAN_TECH_TAB for Prime, Hyperion, Empyrium with robot_core_aldarian icon.
 */
public class RebootGroups extends InternalGroups {

    private static final Set<RobotVariant> ALDARIAN_VARIANTS = Set.of(
        RobotVariant.Prime, RobotVariant.Hyperion, RobotVariant.Empyrium
    );

    public static final CreativeModeTab DEFAULT_TAB = register(
        RebootIdentifier.getId(LovelyConstant.DEFAULT_TAB),
        registerGroup(RebootIdentifier.getTabTranslation(LovelyConstant.DEFAULT_TAB),
            RebootItems.ROBOT_CORE));

    public static final ResourceKey<CreativeModeTab> DEFAULT_KEY =
        registerKey(RebootIdentifier.getId(LovelyConstant.DEFAULT_TAB));

    public static final CreativeModeTab ALDARIAN_TECH_TAB = register(
        RebootIdentifier.getId(LovelyConstant.ALDARIAN_TECH_TAB),
        registerGroup(RebootIdentifier.getTabTranslation(LovelyConstant.ALDARIAN_TECH_TAB),
            RebootItems.ROBOT_CORE_ALDARIAN));

    public static final ResourceKey<CreativeModeTab> ALDARIAN_KEY =
        registerKey(RebootIdentifier.getId(LovelyConstant.ALDARIAN_TECH_TAB));

    // -- Registration --

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS)
            .register(RebootGroups::addSpawnEggs);
        ItemGroupEvents.modifyEntriesEvent(DEFAULT_KEY)
            .register(RebootGroups::defaultTabEntry);
        ItemGroupEvents.modifyEntriesEvent(ALDARIAN_KEY)
            .register(RebootGroups::aldarianTabEntry);
        Reboot.LOGGER.info("Registering CreativeTabs for: " + Reboot.MODID);
    } // register()

    private static void addSpawnEggs(FabricItemGroupEntries entries) {
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.REBOOT)) {
            entries.accept(RebootItems.getSpawnItem(def.getVariant()));
        }
    } // addSpawnEggs()

    private static void defaultTabEntry(FabricItemGroupEntries entries) {
        entries.accept(RebootItems.ROBOT_CORE);
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.REBOOT)) {
            if (!ALDARIAN_VARIANTS.contains(def.getVariant())) {
                entries.accept(RebootItems.getSpawnItem(def.getVariant()));
            }
        }
    } // defaultTabEntry()

    private static void aldarianTabEntry(FabricItemGroupEntries entries) {
        entries.accept(RebootItems.ROBOT_CORE_ALDARIAN);
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.REBOOT)) {
            if (ALDARIAN_VARIANTS.contains(def.getVariant())) {
                entries.accept(RebootItems.getSpawnItem(def.getVariant()));
            }
        }
    } // aldarianTabEntry()

} // Class: RebootGroups
