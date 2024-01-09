package net.msymbios.rlovelyr.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.entity.LovelyRobotEntities;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class LovelyRobotEvents {

    @Mod.EventBusSubscriber(modid = LovelyRobot.MODID)
    public static class ForgeEvents  { } // Class ForgeEvents

    @Mod.EventBusSubscriber(modid = LovelyRobot.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventBusEvents {

        @SubscribeEvent
        public static void onRegisterEntityAttribute(EntityAttributeCreationEvent event) {
            LovelyRobotEntities.registerAttributes(event);
        } // onRegisterEntityAttribute ()

    } // Class ModEventBusEvents

} // Class LovelyRobotEvents