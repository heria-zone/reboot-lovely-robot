package net.heriazone.llovelyr.source;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.heriazone.llovelyr.Legacy;
import net.heriazone.llovelyr.LegacyIdentifier;
import net.heriazone.lovelylib.common.entity.definition.ModTarget;
import net.heriazone.lovelylib.common.entity.definition.RobotDefinitionRegistry;
import net.heriazone.lovelylib.common.entity.definition.RobotEntityDefinition;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.hzlib.api.groups.InternalGroups;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

/**
 * Creative tab management for Legacy variant items (Fabric).
 * <p>
 * <b>Architecture:</b> Both addSpawnEggs and allItemsEntry iterate
 * RobotDefinitionRegistry.getForMod(LEGACY). Adding a new Legacy entity
 * populates both tabs automatically — no change needed here.
 */
public class LegacyGroups extends InternalGroups {

    // -- State --

    public static final CreativeModeTab DEFAULT_TAB = register(
        LegacyIdentifier.getId(LovelyConstant.DEFAULT_TAB),
        registerGroup(LegacyIdentifier.getTabTranslation(LovelyConstant.DEFAULT_TAB),
            LegacyItems.ROBOT_CORE));

    public static final ResourceKey<CreativeModeTab> DEFAULT_KEY =
        registerKey(LegacyIdentifier.getId(LovelyConstant.DEFAULT_TAB));

    // -- Registration --

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS)
            .register(LegacyGroups::addSpawnEggs);
        ItemGroupEvents.modifyEntriesEvent(DEFAULT_KEY)
            .register(LegacyGroups::allItemsEntry);
        Legacy.LOGGER.info("Registering CreativeTabs for: " + Legacy.MODID);
    } // register()

    private static void addSpawnEggs(FabricItemGroupEntries entries) {
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
            entries.accept(LegacyItems.getSpawnItem(def.getVariant()));
        }
    } // addSpawnEggs()

    private static void allItemsEntry(FabricItemGroupEntries entries) {
        entries.accept(LegacyItems.ROBOT_CORE);
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
            entries.accept(LegacyItems.getSpawnItem(def.getVariant()));
        }
    } // allItemsEntry()

} // Class: LegacyGroups
