package com.msymbios.rlovelyr;

import com.mojang.logging.LogUtils;
import com.msymbios.entity.ModEntities;
import com.msymbios.entity.client.BunnyRenderer;
import com.msymbios.entity.client.Bunny2Renderer;
import com.msymbios.entity.client.HoneyRenderer;
import com.msymbios.entity.client.VanillaRenderer;
import com.msymbios.item.ModItems;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

@Mod(LovelyRobotMod.MODID)
public class LovelyRobotMod {

    // -- Variables --
    public static final String MODID = "rlovelyr";
    private static final Logger LOGGER = LogUtils.getLogger();

    // -- Constructor --
    public LovelyRobotMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);

        ModEntities.register(modEventBus);

        GeckoLib.initialize();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(ModItems::addCreativeModeTab);
    } // Constructor RLovelyRobotMod ()

    // -- Methods --
    private void commonSetup(final FMLCommonSetupEvent event) {

    } // commonSetup ()

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(ModEntities.BUNNY.get(), BunnyRenderer::new);
            EntityRenderers.register(ModEntities.BUNNY2.get(), Bunny2Renderer::new);
            EntityRenderers.register(ModEntities.HONEY.get(), HoneyRenderer::new);
            EntityRenderers.register(ModEntities.VANILLA.get(), VanillaRenderer::new);
        } // onClientSetup ()
    } // ClientModEvents ()

} // Class LovelyRobotMod