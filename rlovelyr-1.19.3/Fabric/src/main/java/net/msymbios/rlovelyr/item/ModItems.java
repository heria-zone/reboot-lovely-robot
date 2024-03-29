package net.msymbios.rlovelyr.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.LovelyRobotMod;
import net.msymbios.rlovelyr.entity.ModEntities;
import net.msymbios.rlovelyr.item.custom.RobotCoreItem;
import net.msymbios.rlovelyr.item.custom.SpawnItem;

public class ModItems {

    // -- Variables --
    public static final Item BUNNY_SPAWN = registerItem("bunny_spawn", new SpawnItem(ModEntities.BUNNY, new FabricItemSettings()), ModItemsGroup.LOVELY_ROBOT);
    public static final Item BUNNY2_SPAWN = registerItem("bunny2_spawn", new SpawnItem(ModEntities.BUNNY2, new FabricItemSettings()), ModItemsGroup.LOVELY_ROBOT);
    public static final Item DRAGON_SPAWN = registerItem("dragon_spawn", new SpawnItem(ModEntities.DRAGON, new FabricItemSettings()), ModItemsGroup.LOVELY_ROBOT);
    public static final Item HONEY_SPAWN = registerItem("honey_spawn", new SpawnItem(ModEntities.HONEY, new FabricItemSettings()), ModItemsGroup.LOVELY_ROBOT);
    public static final Item KITSUNE_SPAWN = registerItem("kitsune_spawn", new SpawnItem(ModEntities.KITSUNE, new FabricItemSettings()), ModItemsGroup.LOVELY_ROBOT);
    public static final Item NEKO_SPAWN = registerItem("neko_spawn", new SpawnItem(ModEntities.NEKO, new FabricItemSettings()), ModItemsGroup.LOVELY_ROBOT);
    public static final Item VANILLA_SPAWN = registerItem("vanilla_spawn", new SpawnItem(ModEntities.VANILLA, new FabricItemSettings()), ModItemsGroup.LOVELY_ROBOT);
    public static final Item ROBOT_CORE = registerItem("robot_core", new RobotCoreItem(new FabricItemSettings()), ModItemsGroup.LOVELY_ROBOT);

    // -- Methods --
    private static Item registerItem(String name, Item item, ItemGroup tab) {
        ItemGroupEvents.modifyEntriesEvent(tab).register(entries -> entries.add(item));
        return Registry.register(Registries.ITEM, new Identifier(LovelyRobotMod.MODID, name), item);
    } // registerItem ()

    public static void registerModItems() {
        LovelyRobotMod.LOGGER.debug(LovelyRobotMod.MODID + ": Registering Items");
    } // registerModItems ()

} // Class ModItems
