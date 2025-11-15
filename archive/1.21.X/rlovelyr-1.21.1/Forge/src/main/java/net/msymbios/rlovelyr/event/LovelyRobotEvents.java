package net.msymbios.rlovelyr.event;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.entity.LovelyRobotEntities;

@Mod.EventBusSubscriber(modid = LovelyRobot.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class LovelyRobotEvents {

    // -- Methods --

    /**
     * Adds the additional attributes an entity needs to function.
     */
    @SubscribeEvent
    public static void onRegisterEntityAttribute(EntityAttributeCreationEvent event) {
        LovelyRobotEntities.registerAttribute(event);
    } // onRegisterEntityAttribute ()
    
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LovelyRobot.LOGGER.info("HELLO from server starting");
    } // onServerStarting ()

    // -- Classes --

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = LovelyRobot.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        // -- Methods --

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LovelyRobot.LOGGER.info("HELLO FROM CLIENT SETUP");
            LovelyRobot.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        } // onClientSetup ()

    } // Class ClientModEvents

} // Class LovelyRobotEvents