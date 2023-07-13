package net.msymbios.rlovelyr.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItemsTab {

    // -- Variables --
    public static final ItemGroup LOVELY_ROBOT = new ItemGroup("lovely_robot_tab") {
        @Override
        public ItemStack makeIcon() {return new ItemStack(ModItems.BUNNY2_SPAWN.get());}
    };

} // Class ModItemsTab