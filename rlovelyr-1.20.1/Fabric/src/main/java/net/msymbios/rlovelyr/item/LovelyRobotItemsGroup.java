package net.msymbios.rlovelyr.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.LovelyRobot;

public class LovelyRobotItemsGroup {

    // -- Variables --
    public static ItemGroup LOVELY_ROBOT = register("lovely_robot", register("itemGroup.lovely_robot"));

    // -- Methods --
    private static ItemGroup register(String name, ItemGroup itemGroup) {
        return Registry.register(Registries.ITEM_GROUP, new Identifier(LovelyRobot.MODID, name), itemGroup);
    } // register ()

    private static ItemGroup register(String translatable) {
        return FabricItemGroup.builder().displayName(Text.translatable(translatable)).icon(() -> new ItemStack(LovelyRobotItems.BUNNY_SPAWN)).entries(((displayContext, entries) -> {
            entries.add(LovelyRobotItems.ROBOT_CORE);
            entries.add(LovelyRobotItems.BUNNY_SPAWN);
            entries.add(LovelyRobotItems.BUNNY2_SPAWN);
            entries.add(LovelyRobotItems.DRAGON_SPAWN);
            entries.add(LovelyRobotItems.HONEY_SPAWN);
            entries.add(LovelyRobotItems.KITSUNE_SPAWN);
            entries.add(LovelyRobotItems.NEKO_SPAWN);
            entries.add(LovelyRobotItems.VANILLA_SPAWN);
        })).build();
    } // register ()

    public static void register () {
        LovelyRobot.LOGGER.info("Registering ItemGroups: " + LovelyRobot.MODID);
    } // register ()

} // Class LovelyRobotItemsGroup
