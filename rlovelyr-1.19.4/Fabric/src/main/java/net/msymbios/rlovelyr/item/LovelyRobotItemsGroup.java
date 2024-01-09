package net.msymbios.rlovelyr.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.config.LovelyRobotID;

public class LovelyRobotItemsGroup {

    // -- Variables --
    public static final ItemGroup LOVELY_ROBOT = FabricItemGroup.builder(new Identifier(LovelyRobot.MODID, LovelyRobotID.TAB_GROUP))
            .icon(() -> new ItemStack(LovelyRobotItems.BUNNY_SPAWN)).build();

} // Class LovelyRobotItemsGroup
