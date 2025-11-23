package net.msymbios.llovelyr.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.util.Rarity;
import net.msymbios.llovelyr.LovelyLegacy;
import net.msymbios.llovelyr.common.item.InternalItems;
import net.msymbios.llovelyr.item.custom.LovelyCore;
import net.msymbios.llovelyr.item.custom.LovelySpawn;
import net.msymbios.llovelyr.source.configs.LovelyIdentifier;

/**
 * Centralizes item registration for Legacy variant robots.
 * <p>
 * <b>Architecture:</b> Uses Fabric's direct Registry system to register items
 * during mod initialization. Separates robot cores from spawn items while
 * maintaining consistent registration flow.
 * <p>
 * <b>Item Categories:</b> Robot cores (crafting materials) and spawn eggs
 * (entity summoning) with NBT-based customization support.
 */
public class LovelyItems extends InternalItems {

    // -- Variables --

    // MISCELLANEOUS
    public static final Item ROBOT_CORE = registerItem(LovelyIdentifier.ROBOT_CORE, Rarity.UNCOMMON, 1);

    // SPAWNS
    public static final Item VANILLA_SPAWN = registerItem(LovelyIdentifier.VANILLA_SPAWN, LovelyEntities.VANILLA, Rarity.RARE, 1);
    public static final Item BUNNY2_SPAWN = registerItem(LovelyIdentifier.BUNNY2_SPAWN, LovelyEntities.BUNNY2, Rarity.RARE, 1);

    // -- Methods --

    /**
     * Registers robot core item with specified properties.
     * <p>
     * <b>Usage:</b> For crafting materials and robot storage items.
     * 
     * @param name the registry name
     * @param rarity the item rarity level
     * @param stack the maximum stack size
     * @return the registered item instance
     */
    private static Item registerItem(String name, Rarity rarity, int stack) {
        return register(LovelyIdentifier.getId(name), new LovelyCore(new FabricItemSettings().rarity(rarity).fireproof().maxCount(stack)));
    } // registerItem()

    /**
     * Registers spawn egg item for robot entity.
     * <p>
     * <b>NBT Support:</b> Spawn eggs can store robot customization data (color,
     * level, name) which transfers to spawned entity.
     * 
     * @param name the registry name
     * @param mob the entity type
     * @param rarity the item rarity level
     * @param stack the maximum stack size
     * @return the registered spawn egg instance
     */
    private static Item registerItem(String name, EntityType<? extends MobEntity> mob, Rarity rarity, int stack) {
        return register(LovelyIdentifier.getId(name), new LovelySpawn(mob, new FabricItemSettings().rarity(rarity).fireproof().maxCount(stack)));
    } // registerItem()

    /**
     * Registers items and adds them to creative tabs.
     * <p>
     * <b>Timing:</b> Must be called during mod initialization.
     */
    public static void register() {
        LovelyLegacy.LOGGER.info("Registering Items: " + LovelyLegacy.MODID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(LovelyItems::spawnEggItemsEntry);
        ItemGroupEvents.modifyEntriesEvent(LovelyGroups.DEFAULT_KEY).register(LovelyItems::allItemsEntry);
    } // register()

    /**
     * Adds spawn eggs to vanilla spawn eggs creative tab.
     */
    private static void spawnEggItemsEntry(FabricItemGroupEntries entries) {
        entries.add(VANILLA_SPAWN);
        entries.add(BUNNY2_SPAWN);
    } // spawnEggItemsEntry()

    /**
     * Adds all items to mod's default creative tab.
     */
    private static void allItemsEntry(FabricItemGroupEntries entries) {
        entries.add(ROBOT_CORE);
        entries.add(VANILLA_SPAWN);
        entries.add(BUNNY2_SPAWN);
    } // allItemsEntry()

} // Class: LovelyItems