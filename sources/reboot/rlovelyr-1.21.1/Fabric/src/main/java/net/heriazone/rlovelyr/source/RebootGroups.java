package net.heriazone.rlovelyr.source;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.heriazone.rlovelyr.Reboot;
import net.heriazone.rlovelyr.RebootIdentifier;
import net.heriazone.lovelylib.common.entity.definition.ModTarget;
import net.heriazone.lovelylib.common.entity.definition.RobotDefinitionRegistry;
import net.heriazone.lovelylib.common.entity.definition.RobotEntityDefinition;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.hzlib.api.groups.InternalGroups;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

/**
 * Creative tab management for Reboot variant items (Fabric).
 * <p>
 * <b>Architecture:</b> Mirrors LegacyGroups Fabric — only namespace
 * (RebootIdentifier) and ModTarget.REBOOT differ.
 */
public class RebootGroups extends InternalGroups {

    public static final CreativeModeTab DEFAULT_TAB = register(
        RebootIdentifier.getId(LovelyConstant.DEFAULT_TAB),
        registerGroup(RebootIdentifier.getTabTranslation(LovelyConstant.DEFAULT_TAB),
            RebootItems.ROBOT_CORE));

    public static final ResourceKey<CreativeModeTab> DEFAULT_KEY =
        registerKey(RebootIdentifier.getId(LovelyConstant.DEFAULT_TAB));

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS)
            .register(RebootGroups::addSpawnEggs);
        ItemGroupEvents.modifyEntriesEvent(DEFAULT_KEY)
            .register(RebootGroups::allItemsEntry);
        Reboot.LOGGER.info("Registering CreativeTabs for: " + Reboot.MODID);
    } // register()

    private static void addSpawnEggs(FabricItemGroupEntries entries) {
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.REBOOT)) {
            entries.accept(RebootItems.getSpawnItem(def.getVariant()));
        }
    } // addSpawnEggs()

    private static void allItemsEntry(FabricItemGroupEntries entries) {
        entries.accept(RebootItems.ROBOT_CORE);
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.REBOOT)) {
            entries.accept(RebootItems.getSpawnItem(def.getVariant()));
        }
    } // allItemsEntry()

} // Class: RebootGroups
