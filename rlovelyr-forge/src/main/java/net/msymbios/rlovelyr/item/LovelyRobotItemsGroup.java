package net.msymbios.rlovelyr.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class LovelyRobotItemsGroup {

    // -- Variables --
    public static final ItemGroup LOVELY_ROBOT = new ItemGroup("lovely_robot") {@Override public ItemStack makeIcon() {return new ItemStack(LovelyRobotItems.BUNNY_SPAWN.get());}};

} // Class LovelyRobotItemsGroup