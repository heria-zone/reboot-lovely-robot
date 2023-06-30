package net.msymbios.rlovelyr.item;

import net.msymbios.rlovelyr.LovelyRobotMod;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LovelyRobotMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItemsTab {

    // -- Variables --
    public static CreativeModeTab LOVELY_ROBOT;

    // -- Methods --
    @SubscribeEvent
    public static void registerCreativeModeTab(CreativeModeTabEvent.Register event) {
        LOVELY_ROBOT = event.registerCreativeModeTab(new ResourceLocation(LovelyRobotMod.MODID, "lovely_robot_tab"),
                builder -> builder.icon(() -> new ItemStack(ModItems.BUNNY2_SPAWN.get())).title(Component.translatable("tab.rlovelyr.lovely_robot_tab")).build());
    } // registerCreativeModeTab

} // Class ModItemsTab