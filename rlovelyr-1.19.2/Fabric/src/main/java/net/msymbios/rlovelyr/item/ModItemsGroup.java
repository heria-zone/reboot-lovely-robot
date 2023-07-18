package net.msymbios.rlovelyr.item;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.LovelyRobotMod;

public class ModItemsGroup {

    // -- Variables --
    public static final ItemGroup LOVELY_ROBOT = FabricItemGroupBuilder.build(new Identifier(LovelyRobotMod.MODID, "lovely_robot"), () -> new ItemStack(ModItems.BUNNY_SPAWN));

} // Class ModItemsGroup