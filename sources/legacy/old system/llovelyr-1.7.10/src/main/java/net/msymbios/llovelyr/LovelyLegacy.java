package net.msymbios.llovelyr;

import cpw.mods.fml.common.Mod;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = LovelyLegacy.MODID, name = LovelyLegacy.NAME, version = LovelyLegacy.VERSION, acceptedMinecraftVersions = "[1.7.10]")
public class LovelyLegacy {

	// -- Constants --

    public static final String MODID = "llovelyr";
    public static final String NAME = "LovelyRobot Legacy";
    public static final String VERSION = "1.0";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

	// -- Event Methods --

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent preinit) {
        LOGGER.info("INITIALIZE: PRE");
    } // preinit ()

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("INITIALIZE: STARTING");
    } // init ()

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        LOGGER.info("INITIALIZE: POST");
    } // postInit ()

} // Class: LovelyLegacy