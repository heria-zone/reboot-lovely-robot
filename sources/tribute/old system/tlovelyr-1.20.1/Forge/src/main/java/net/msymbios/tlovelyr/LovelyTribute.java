package net.msymbios.tlovelyr;

import net.msymbios.tlovelyr.source.blocks.DefaultBlocks;
import net.msymbios.tlovelyr.source.configuration.DefaultConfigs;
import net.msymbios.tlovelyr.source.groups.DefaultItemsGroup;
import net.msymbios.tlovelyr.source.items.DefaultItems;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(LovelyTribute.MODID)
public class LovelyTribute {

    // -- Constants --

    // Define mod id in a common place for everything to reference
    public static final String MODID = "tlovelyr";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // -- Constructor --

    public LovelyTribute(FMLJavaModLoadingContext context) {
        IEventBus eventBus = context.getModEventBus();

        // Register the setup method for modloading
        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::clientSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        DefaultBlocks.register(eventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        DefaultItems.register(eventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        DefaultItemsGroup.register(eventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        DefaultItemsGroup.registerItems(eventBus);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        DefaultConfigs.register(context);
    } // LovelyTribute ()

    // -- Custom Methods --

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON: SETUP");

        if (DefaultConfigs.logDirtBlock) LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(net.minecraft.world.level.block.Blocks.DIRT));
        LOGGER.info(DefaultConfigs.magicNumberIntroduction + DefaultConfigs.magicNumber);

        DefaultConfigs.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    } // commonSetup ()

    private void clientSetup(final FMLClientSetupEvent event) {
        // Some client setup code
        LOGGER.info("HELLO FROM CLIENT: SETUP");
    } // clientSetup ()

} // Class: LovelyTribute