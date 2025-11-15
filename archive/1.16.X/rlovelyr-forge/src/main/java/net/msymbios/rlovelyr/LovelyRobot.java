package net.msymbios.rlovelyr;

import net.msymbios.rlovelyr.config.LovelyRobotConfigs;
import net.msymbios.rlovelyr.entity.LovelyRobotEntities;
import net.msymbios.rlovelyr.event.CraftingHandler;
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
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        LovelyRobotConfigs.register();
        LovelyRobotItems.register(eventBus);
        LovelyRobotEntities.register(eventBus);

        eventBus.addListener(this::clientSetup);
        eventBus.addListener(this::commonSetup);

        GeckoLib.initialize();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new CraftingHandler());
    } // Constructor LovelyRobot ()

    // -- Methods --
    private void clientSetup(final FMLClientSetupEvent event) {
        LovelyRobotEntities.registerRender();
    } // clientSetup ()

    private void commonSetup(final FMLCommonSetupEvent event) { } // commonSetup ()

} // Class LovelyRobot