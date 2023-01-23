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
import net.msymbios.rlovelyr.item.custom.RobotSpawnItem;

public class ModItems {

    // -- Variables --
    public static final Item VANILLA_SPAWN = registerItem("vanilla_spawn", new RobotSpawnItem(ModEntities.VANILLA, new FabricItemSettings()), ModItemsGroup.LOVELY_ROBOT);
    public static final Item ROBOT_CORE = registerItem("robot_core", new Item(new FabricItemSettings()), ModItemsGroup.LOVELY_ROBOT);

    // -- Methods --
    private static Item registerItem(String name, Item item, ItemGroup tab) {
        ItemGroupEvents.modifyEntriesEvent(tab).register(entries -> entries.add(item));
        return Registry.register(Registries.ITEM, new Identifier(LovelyRobotMod.MODID, name), item);
    } // registerItem ()

    public static void registerModItems() {
        LovelyRobotMod.LOGGER.debug(LovelyRobotMod.MODID + ": Registering Items");
    } // registerModItems ()

} // Class ModItems
