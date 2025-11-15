package net.msymbios.rlovelyr;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.msymbios.rlovelyr.block.LovelyRobotBlocks;
import net.msymbios.rlovelyr.common.util.ObjectUtil;
import net.msymbios.rlovelyr.common.util.internal.Version;
import net.msymbios.rlovelyr.config.LovelyRobotConfig;
import net.msymbios.rlovelyr.entity.LovelyRobotEntities;
import net.msymbios.rlovelyr.item.LovelyRobotItems;
import net.msymbios.rlovelyr.item.LovelyRobotItemsGroup;
import org.slf4j.Logger;

@Mod(LovelyRobot.MODID)
public class LovelyRobot {

    // -- Variables --

    /**
     * The mod's ID, which is also the mod's namespace.
     */
    public static final String MODID = "rlovelyr";

    /**
     * The mod's version.
     */
    public static final Version VERSION = new Version(ObjectUtil.coalesce(LovelyRobot.class.getPackage().getSpecificationVersion(), "99999.0.0.0"));

    /**
     * The mod's logger.
     */
    public static final Logger LOGGER = LogUtils.getLogger();

    // -- Constructor --

    public LovelyRobot(FMLJavaModLoadingContext context) {
        IEventBus event = context.getModEventBus();
        LovelyRobotConfig.register(context);

        event.addListener(this::commonSetup);
        event.addListener(this::clientSetup);

        LovelyRobotEntities.register(event);
        LovelyRobotBlocks.register(event);

        LovelyRobotItems.register(event);
        LovelyRobotItemsGroup.register(event);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        LovelyRobotItems.registerCreative(event);
    } // Constructor LovelyRobot ()

    // -- Methods --

    private void commonSetup(final FMLCommonSetupEvent event) {

    } // commonSetup ()

    private void clientSetup(final FMLClientSetupEvent event) {
        LovelyRobotEntities.registerRender();
        LovelyRobotItems.registerModel(event);
    } // clientSetup ()

} // Class LovelyRobot