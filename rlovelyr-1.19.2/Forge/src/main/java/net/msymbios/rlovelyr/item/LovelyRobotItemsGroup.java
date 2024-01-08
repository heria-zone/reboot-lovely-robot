package net.msymbios.rlovelyr.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.msymbios.rlovelyr.config.LovelyRobotID;

public class LovelyRobotItemsGroup {

    // -- Variables --
    public static final CreativeModeTab LOVELY_ROBOT = new CreativeModeTab(LovelyRobotID.TAB_GROUP) { @Override public ItemStack makeIcon() {return new ItemStack(LovelyRobotItems.BUNNY_SPAWN.get());}};

} // Class ModItemsTab