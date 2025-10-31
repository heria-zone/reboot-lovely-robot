package net.msymbios.tlovelyr.source.events;

import net.msymbios.tlovelyr.LovelyTribute;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class Events {

    // -- Nested Classes --

    @Mod.EventBusSubscriber(modid = LovelyTribute.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER)
    public static class ServerEvents {

        // -- Custom Methods --

        @SubscribeEvent
        public void onServerStarting(ServerStartingEvent event) {
            // Do something when the server starts
            LovelyTribute.LOGGER.info("HELLO FROM SERVER: STARTING");
        } // onServerStarting ()

    } // Class: ServerEvents

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = LovelyTribute.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {

        // -- Custom Methods --

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LovelyTribute.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        } // onClientSetup ()

    } // Class: ClientEvents

} // Class: Events