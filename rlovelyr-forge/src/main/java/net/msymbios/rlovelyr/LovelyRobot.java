package net.msymbios.rlovelyr;

import net.msymbios.rlovelyr.entity.LovelyRobotEntities;
import net.msymbios.rlovelyr.item.LovelyRobotItems;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.geckolib3.GeckoLib;

@Mod(LovelyRobot.MODID)
public class LovelyRobot {

    // -- Variables --
    public static final String MODID = "rlovelyr";

    // -- Constructor --
    public LovelyRobot() {
        GeckoLib.initialize();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        LovelyRobotItems.register(modEventBus);
        LovelyRobotEntities.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    } // Constructor RLovelyRobotMod ()

    // -- Methods --
    private void commonSetup(final FMLCommonSetupEvent event) { } // commonSetup ()

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LovelyRobotEntities.registerRender();
        } // onClientSetup ()
    } // ClientModEvents ()

} // Class LovelyRobot