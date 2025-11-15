package net.msymbios.rlovelyr;

import net.msymbios.rlovelyr.config.LovelyRobotConfig;
import net.msymbios.rlovelyr.entity.LovelyRobotEntities;
import net.msymbios.rlovelyr.item.LovelyRobotItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
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
        IEventBus event = FMLJavaModLoadingContext.get().getModEventBus();

        LovelyRobotConfig.register();
        LovelyRobotItems.register(event);
        LovelyRobotEntities.register(event);

        event.addListener(this::clientSetup);
        event.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    } // Constructor RLovelyRobotMod ()

    // -- Methods --
    private void clientSetup(final FMLClientSetupEvent event) {
        LovelyRobotEntities.registerRenderers();
    } // clientSetup ()

    private void commonSetup(final FMLCommonSetupEvent event) { } // commonSetup ()

} // Class LovelyRobot