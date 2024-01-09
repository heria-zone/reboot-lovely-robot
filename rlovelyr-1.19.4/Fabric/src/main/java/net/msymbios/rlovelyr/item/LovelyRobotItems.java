package net.msymbios.rlovelyr.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.LovelyRobotEntities;
import net.msymbios.rlovelyr.item.custom.RobotCoreItem;
import net.msymbios.rlovelyr.item.custom.SpawnItem;

public class LovelyRobotItems {

    // -- Variables --
    public static final Item ROBOT_CORE = register(LovelyRobotID.ROBOT_CORE, new RobotCoreItem(new FabricItemSettings().rarity(Rarity.UNCOMMON)), LovelyRobotItemsGroup.LOVELY_ROBOT);
    public static final Item BUNNY_SPAWN = register(LovelyRobotID.BUNNY_SPAWN, new SpawnItem(LovelyRobotEntities.BUNNY, new FabricItemSettings().rarity(Rarity.RARE)), LovelyRobotItemsGroup.LOVELY_ROBOT);
    public static final Item BUNNY2_SPAWN = register(LovelyRobotID.BUNNY2_SPAWN, new SpawnItem(LovelyRobotEntities.BUNNY2, new FabricItemSettings().rarity(Rarity.RARE)), LovelyRobotItemsGroup.LOVELY_ROBOT);
    public static final Item DRAGON_SPAWN = register(LovelyRobotID.DRAGON_SPAWN, new SpawnItem(LovelyRobotEntities.DRAGON, new FabricItemSettings().rarity(Rarity.RARE)), LovelyRobotItemsGroup.LOVELY_ROBOT);
    public static final Item HONEY_SPAWN = register(LovelyRobotID.HONEY_SPAWN, new SpawnItem(LovelyRobotEntities.HONEY, new FabricItemSettings().rarity(Rarity.RARE)), LovelyRobotItemsGroup.LOVELY_ROBOT);
    public static final Item KITSUNE_SPAWN = register(LovelyRobotID.KITSUNE_SPAWN, new SpawnItem(LovelyRobotEntities.KITSUNE, new FabricItemSettings().rarity(Rarity.RARE)), LovelyRobotItemsGroup.LOVELY_ROBOT);
    public static final Item NEKO_SPAWN = register(LovelyRobotID.NEKO_SPAWN, new SpawnItem(LovelyRobotEntities.NEKO, new FabricItemSettings().rarity(Rarity.RARE)), LovelyRobotItemsGroup.LOVELY_ROBOT);
    public static final Item VANILLA_SPAWN = register(LovelyRobotID.VANILLA_SPAWN, new SpawnItem(LovelyRobotEntities.VANILLA, new FabricItemSettings().rarity(Rarity.RARE)), LovelyRobotItemsGroup.LOVELY_ROBOT);

    // -- Methods --
    private static Item register(String name, Item item, ItemGroup tab) {
        ItemGroupEvents.modifyEntriesEvent(tab).register(entries -> entries.add(item));
        return Registry.register(Registries.ITEM, new Identifier(LovelyRobot.MODID, name), item);
    } // register ()

    public static void register() {
        LovelyRobot.LOGGER.debug(LovelyRobot.MODID + ": Registering Items");
    } // register ()

} // Class LovelyRobotItems
