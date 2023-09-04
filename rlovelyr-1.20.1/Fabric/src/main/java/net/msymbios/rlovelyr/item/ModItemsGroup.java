package net.msymbios.rlovelyr.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.LovelyRobotMod;

public class ModItemsGroup {

    // -- Variables --
    public static ItemGroup LOVELY_ROBOT = Registry.register(Registries.ITEM_GROUP, new Identifier(LovelyRobotMod.MODID, "lovely_robot"), FabricItemGroup
            .builder()
            .icon(() -> new ItemStack(ModItems.BUNNY_SPAWN))
            .entries((enabledFeatures, entries) -> {
                entries.add(ModItems.ROBOT_CORE);
                entries.add(ModItems.BUNNY_SPAWN);
                entries.add(ModItems.BUNNY2_SPAWN);
                entries.add(ModItems.DRAGON_SPAWN);
                entries.add(ModItems.HONEY_SPAWN);
                entries.add(ModItems.KITSUNE_SPAWN);
                entries.add(ModItems.NEKO_SPAWN);
                entries.add(ModItems.VANILLA_SPAWN);
            }).build());

    // -- Methods --
    public static void registerItemsGroups() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
            entries.add(ModItems.BUNNY_SPAWN);
            entries.add(ModItems.BUNNY2_SPAWN);
            entries.add(ModItems.DRAGON_SPAWN);
            entries.add(ModItems.HONEY_SPAWN);
            entries.add(ModItems.KITSUNE_SPAWN);
            entries.add(ModItems.NEKO_SPAWN);
            entries.add(ModItems.VANILLA_SPAWN);
        });
    } // registerItemsGroups ()

} // Class ModItemsGroup
