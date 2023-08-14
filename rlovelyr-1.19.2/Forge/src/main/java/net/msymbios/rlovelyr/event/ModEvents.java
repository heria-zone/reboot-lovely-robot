package net.msymbios.rlovelyr.event;

import net.msymbios.rlovelyr.entity.ModEntities;
import net.msymbios.rlovelyr.entity.custom.Bunny2Entity;
import net.msymbios.rlovelyr.entity.custom.BunnyEntity;
import net.msymbios.rlovelyr.entity.custom.HoneyEntity;
import net.msymbios.rlovelyr.entity.custom.VanillaEntity;
import net.msymbios.rlovelyr.LovelyRobotMod;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ModEvents {

    @Mod.EventBusSubscriber(modid = LovelyRobotMod.MODID)
    public static class ForgeEvents  {

    } // Class ForgeEvents

    @Mod.EventBusSubscriber(modid = LovelyRobotMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventBusEvents {

        @SubscribeEvent
        public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
            ModEntities.registerAttributes(event);
        } // entityAttributeEvent ()

    } // Class ModEventBusEvents

} // Class ModEvents