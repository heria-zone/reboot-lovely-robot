package net.msymbios.rlovelyr;

import net.msymbios.rlovelyr.entity.ModEntities;
import net.msymbios.rlovelyr.entity.client.BunnyRenderer;
import net.msymbios.rlovelyr.entity.client.Bunny2Renderer;
import net.msymbios.rlovelyr.entity.client.HoneyRenderer;
import net.msymbios.rlovelyr.entity.client.VanillaRenderer;
import net.msymbios.rlovelyr.item.ModItems;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.msymbios.rlovelyr.item.ModItemsGroup;
import software.bernie.geckolib.GeckoLib;

@Mod(LovelyRobotMod.MODID)
public class LovelyRobotMod {

    // -- Variables --
    public static final String MODID = "rlovelyr";

    // -- Constructor --
    public LovelyRobotMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItemsGroup.register(modEventBus);

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