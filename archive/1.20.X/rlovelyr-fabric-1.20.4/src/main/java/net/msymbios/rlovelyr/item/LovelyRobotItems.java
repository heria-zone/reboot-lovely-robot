package net.msymbios.rlovelyr.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.LovelyRobotEntities;
import net.msymbios.rlovelyr.item.custom.RobotCoreItem;
import net.msymbios.rlovelyr.item.custom.SpawnItem;

public class LovelyRobotItems {

    // -- Variables --
    public static final Item ROBOT_CORE = register(LovelyRobotID.ROBOT_CORE, new RobotCoreItem(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()));

    public static final Item BUNNY_SPAWN = register(LovelyRobotID.BUNNY_SPAWN, new SpawnItem(LovelyRobotEntities.BUNNY_ENTITY, new FabricItemSettings().rarity(Rarity.RARE).fireproof()));
    public static final Item BUNNY2_SPAWN = register(LovelyRobotID.BUNNY2_SPAWN, new SpawnItem(LovelyRobotEntities.BUNNY2_ENTITY, new FabricItemSettings().rarity(Rarity.RARE).fireproof()));
    public static final Item DRAGON_SPAWN = register(LovelyRobotID.DRAGON_SPAWN, new SpawnItem(LovelyRobotEntities.DRAGON_ENTITY, new FabricItemSettings().rarity(Rarity.RARE).fireproof()));
    public static final Item HONEY_SPAWN = register(LovelyRobotID.HONEY_SPAWN, new SpawnItem(LovelyRobotEntities.HONEY_ENTITY, new FabricItemSettings().rarity(Rarity.RARE).fireproof()));
    public static final Item KITSUNE_SPAWN = register(LovelyRobotID.KITSUNE_SPAWN, new SpawnItem(LovelyRobotEntities.KITSUNE_ENTITY, new FabricItemSettings().rarity(Rarity.RARE).fireproof()));
    public static final Item NEKO_SPAWN = register(LovelyRobotID.NEKO_SPAWN, new SpawnItem(LovelyRobotEntities.NEKO_ENTITY, new FabricItemSettings().rarity(Rarity.RARE).fireproof()));
    public static final Item VANILLA_SPAWN = register(LovelyRobotID.VANILLA_SPAWN, new SpawnItem(LovelyRobotEntities.VANILLA_ENTITY, new FabricItemSettings().rarity(Rarity.RARE).fireproof()));

    // -- Methods --

    /**
     * Register an item with the specified name.
     *
     * @param  name  the name of the item
     * @param  item  the item to register
     * @return       the registered item
     */
    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, LovelyRobotID.getId(name), item);
    } // register ()

    /**
     * Register method to add items to the spawn eggs group.
     */
    public static void register() {
        LovelyRobot.LOGGER.info("Registering Items: " + LovelyRobot.MODID );
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(LovelyRobotItems::addItemsToSpawnEggsGroup);
    } // register ()

    /**
     * Adds items to the spawn eggs group.
     *
     * @param  entries   the Fabric item group entries to add items to
     */
    private static void addItemsToSpawnEggsGroup(FabricItemGroupEntries entries) {
        entries.add(LovelyRobotItems.BUNNY_SPAWN);
        entries.add(LovelyRobotItems.BUNNY2_SPAWN);
        entries.add(LovelyRobotItems.DRAGON_SPAWN);
        entries.add(LovelyRobotItems.HONEY_SPAWN);
        entries.add(LovelyRobotItems.KITSUNE_SPAWN);
        entries.add(LovelyRobotItems.NEKO_SPAWN);
        entries.add(LovelyRobotItems.VANILLA_SPAWN);
    } // addItemsToSpawnEggsGroup ()

} // Class LovelyRobotItems