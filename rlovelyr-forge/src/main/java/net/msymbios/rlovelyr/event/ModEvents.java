package net.msymbios.rlovelyr.event;

import net.msymbios.rlovelyr.entity.LovelyRobotEntities;
import net.msymbios.rlovelyr.LovelyRobot;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ModEvents {

    @Mod.EventBusSubscriber(modid = LovelyRobot.MODID)
    public static class ForgeEvents  {  } // Class ForgeEvents

    @Mod.EventBusSubscriber(modid = LovelyRobot.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventBusEvents {

        @SubscribeEvent
        public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
            LovelyRobotEntities.registerAttribute(event);
        } // entityAttributeEvent ()

    } // Class ModEventBusEvents

} // Class ModEvents