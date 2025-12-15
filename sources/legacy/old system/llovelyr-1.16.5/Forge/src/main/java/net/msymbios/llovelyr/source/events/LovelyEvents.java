package net.msymbios.llovelyr.source.events;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.msymbios.llovelyr.LovelyLegacy;

public class LovelyEvents {

    // -- Nested Classes --

    @Mod.EventBusSubscriber(modid = LovelyLegacy.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER)
    public static class ServerEvents {

        // -- Custom Methods --

        @SubscribeEvent
        public void onServerStarting(FMLServerStartingEvent event) {
            // Do something when the server starts
            LovelyLegacy.LOGGER.info("HELLO FROM SERVER: STARTING");
        } // onServerStarting ()

    } // Class: ServerEvents

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = LovelyLegacy.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {

        // -- Custom Methods --

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LovelyLegacy.LOGGER.info("HELLO FROM CLIENT: SETUP");
            LovelyLegacy.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        } // onClientSetup ()

    } // Class: ClientEvents

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        // -- Custom Methods --

        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LovelyLegacy.LOGGER.info("HELLO from Register Block");
        } // onBlocksRegistry ()

    } // Class: RegistryEvents

} // Class: LovelyEvents