package net.msymbios.rlovelyr;

import net.msymbios.rlovelyr.entity.ModEntities;
import net.msymbios.rlovelyr.item.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.geckolib.GeckoLib;

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

        modEventBus.addListener(ModItems::addCreativeModeTab);
    } // Constructor RLovelyRobotMod ()

    // -- Methods --
    private void commonSetup(final FMLCommonSetupEvent event) { } // commonSetup ()

} // Class LovelyRobotMod