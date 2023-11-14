package net.msymbios.rlovelyr.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.LovelyRobotEntities;
import net.msymbios.rlovelyr.item.custom.RobotCoreItem;
import net.msymbios.rlovelyr.item.custom.SpawnItem;

public class LovelyRobotItems {

    // -- Variables --
    public static final Item ROBOT_CORE = register(LovelyRobotID.ROBOT_CORE, new RobotCoreItem(new FabricItemSettings().group(LovelyRobotItemsGroup.LOVELY_ROBOT).rarity(Rarity.UNCOMMON)));

    public static final Item BUNNY_SPAWN = register(LovelyRobotID.BUNNY_SPAWN, new SpawnItem(LovelyRobotEntities.BUNNY, new FabricItemSettings().group(LovelyRobotItemsGroup.LOVELY_ROBOT).rarity(Rarity.RARE)));
    public static final Item BUNNY2_SPAWN = register(LovelyRobotID.BUNNY2_SPAWN, new SpawnItem(LovelyRobotEntities.BUNNY2, new FabricItemSettings().group(LovelyRobotItemsGroup.LOVELY_ROBOT).rarity(Rarity.RARE)));
    public static final Item DRAGON_SPAWN = register(LovelyRobotID.DRAGON_SPAWN, new SpawnItem(LovelyRobotEntities.DRAGON, new FabricItemSettings().group(LovelyRobotItemsGroup.LOVELY_ROBOT).rarity(Rarity.RARE)));
    public static final Item HONEY_SPAWN = register(LovelyRobotID.HONEY_SPAWN, new SpawnItem(LovelyRobotEntities.HONEY, new FabricItemSettings().group(LovelyRobotItemsGroup.LOVELY_ROBOT).rarity(Rarity.RARE)));
    public static final Item KITSUNE_SPAWN = register(LovelyRobotID.KITSUNE_SPAWN, new SpawnItem(LovelyRobotEntities.KITSUNE, new FabricItemSettings().group(LovelyRobotItemsGroup.LOVELY_ROBOT).rarity(Rarity.RARE)));
    public static final Item NEKO_SPAWN = register(LovelyRobotID.NEKO_SPAWN, new SpawnItem(LovelyRobotEntities.NEKO, new FabricItemSettings().group(LovelyRobotItemsGroup.LOVELY_ROBOT).rarity(Rarity.RARE)));
    public static final Item VANILLA_SPAWN = register(LovelyRobotID.VANILLA_SPAWN, new SpawnItem(LovelyRobotEntities.VANILLA, new FabricItemSettings().group(LovelyRobotItemsGroup.LOVELY_ROBOT).rarity(Rarity.RARE)));

    // -- Methods --
    private static Item register(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(LovelyRobot.MODID, name), item);
    } // register ()

    public static void register() {
        //LovelyRobot.LOGGER.debug(LovelyRobot.MODID + ": Registering Items");
    } // register ()

} // Class LovelyRobotItems
