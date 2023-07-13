package net.msymbios.rlovelyr;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.msymbios.rlovelyr.entity.ModEntities;
import net.msymbios.rlovelyr.entity.client.BunnyRenderer;
import net.msymbios.rlovelyr.entity.client.Bunny2Renderer;
import net.msymbios.rlovelyr.entity.client.HoneyRenderer;
import net.msymbios.rlovelyr.entity.client.VanillaRenderer;
import net.msymbios.rlovelyr.item.ModItems;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.geckolib3.GeckoLib;

@Mod(LovelyRobotMod.MODID)
public class LovelyRobotMod {

    // -- Variables --
    public static final String MODID = "rlovelyr";

    // -- Constructor --
    public LovelyRobotMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);

        ModEntities.register(modEventBus);

        GeckoLib.initialize();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    } // Constructor RLovelyRobotMod ()

    // -- Methods --
    private void commonSetup(final FMLCommonSetupEvent event) { } // commonSetup ()

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            RenderingRegistry.registerEntityRenderingHandler(ModEntities.BUNNY.get(), BunnyRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(ModEntities.BUNNY2.get(), Bunny2Renderer::new);
            RenderingRegistry.registerEntityRenderingHandler(ModEntities.HONEY.get(), HoneyRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(ModEntities.VANILLA.get(), VanillaRenderer::new);
        } // onClientSetup ()
    } // ClientModEvents ()

} // Class LovelyRobotMod