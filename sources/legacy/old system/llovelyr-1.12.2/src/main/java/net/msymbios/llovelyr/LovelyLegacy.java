package net.msymbios.llovelyr;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = LovelyLegacy.MODID, name = LovelyLegacy.NAME, version = LovelyLegacy.VERSION)
public class LovelyLegacy {

	// -- Constants --

    public static final String MODID = "llovelyr";
    public static final String NAME = "LovelyRobot Legacy";
    public static final String VERSION = "1.0";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

	// -- Event Methods --

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent preinit) {
        LOGGER.info("Hello, world!");
    }

} // Class: LovelyLegacy