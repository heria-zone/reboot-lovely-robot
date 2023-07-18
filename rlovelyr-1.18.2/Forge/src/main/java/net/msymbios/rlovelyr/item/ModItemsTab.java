package net.msymbios.rlovelyr.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModItemsTab {

    // -- Variables --
    public static final CreativeModeTab LOVELY_ROBOT = new CreativeModeTab("lovely_robot") {@Override public ItemStack makeIcon() {return new ItemStack(ModItems.BUNNY2_SPAWN.get());}};

} // Class ModItemsTab