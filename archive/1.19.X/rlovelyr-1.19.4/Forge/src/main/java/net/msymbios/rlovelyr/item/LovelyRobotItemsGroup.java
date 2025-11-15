package net.msymbios.rlovelyr.item;

import net.minecraft.network.chat.Component;
import net.msymbios.rlovelyr.LovelyRobot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.msymbios.rlovelyr.config.LovelyRobotID;

@Mod.EventBusSubscriber(modid = LovelyRobot.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class LovelyRobotItemsGroup {

    // -- Variables --
    public static CreativeModeTab LOVELY_ROBOT;

    // -- Methods --
    @SubscribeEvent
    public static void registerCreativeModeTab(CreativeModeTabEvent.Register event) {
        LOVELY_ROBOT = event.registerCreativeModeTab(new ResourceLocation(LovelyRobot.MODID, LovelyRobotID.TAB_GROUP),
                builder -> builder.icon(() -> new ItemStack(LovelyRobotItems.BUNNY_SPAWN.get())).title(Component.translatable("itemGroup.lovely_robot")).build());
    } // registerCreativeModeTab

} // Class ModItemsTab