package net.msymbios.llovelyr;

import net.fabricmc.api.ModInitializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LovelyLegacy implements ModInitializer {

	// -- Constants --

	public static final String MODID = "llovelyr";
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	// -- Inherited Methods --

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
	} // onInitialize ()

} // Class: LovelyLegacy