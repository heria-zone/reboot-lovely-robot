package net.msymbios.rlovelyr.event;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.entity.LovelyRobotEntities;

import javax.annotation.Nonnull;

/**
 * Handles any mod event.s
 */
@Mod.EventBusSubscriber(modid = LovelyRobot.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class LovelyRobotEvents {

    // -- Methods --

    /**
     * Adds the additional attributes an entity needs to function.
     */
    @SubscribeEvent
    public static void addEntityAttributes(@Nonnull EntityAttributeCreationEvent event) {
        LovelyRobotEntities.registerAttribute(event);
    } // addEntityAttributes ()

} // Class LovelyRobotEvents