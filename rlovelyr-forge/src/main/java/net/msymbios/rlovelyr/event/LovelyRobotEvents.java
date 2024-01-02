package net.msymbios.rlovelyr.event;

import net.msymbios.rlovelyr.entity.LovelyRobotEntities;
import net.msymbios.rlovelyr.LovelyRobot;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class LovelyRobotEvents {

    @Mod.EventBusSubscriber(modid = LovelyRobot.MODID)
    public static class ForgeEvents  {  } // Class ForgeEvents

    @Mod.EventBusSubscriber(modid = LovelyRobot.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class LovelyRobotEventBusEvents {

        @SubscribeEvent
        public static void onRegisterEntityAttribute(EntityAttributeCreationEvent event) {
            LovelyRobotEntities.registerAttribute(event);
        } // onRegisterEntityAttribute ()

    } // Class LovelyRobotEventBusEvents

} // Class LovelyRobotEvents