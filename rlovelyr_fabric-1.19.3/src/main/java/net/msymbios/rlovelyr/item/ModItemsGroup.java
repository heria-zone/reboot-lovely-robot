package net.msymbios.rlovelyr.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.LovelyRobotMod;

public class ModItemsGroup {

    // -- Variables --
    public static final ItemGroup LOVELY_ROBOT = FabricItemGroup.builder(new Identifier(LovelyRobotMod.MODID, "lovely_robot"))
            .icon(() -> new ItemStack(ModItems.VANILLA_SPAWN))
            .entries((enabledFeatures, entries, operatorEnabled) -> {
                entries.add(ModItems.VANILLA_SPAWN);
            }).build();

} // Class ModItemsGroup
