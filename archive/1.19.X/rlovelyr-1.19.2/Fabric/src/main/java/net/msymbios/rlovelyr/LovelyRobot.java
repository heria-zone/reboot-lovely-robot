package net.msymbios.rlovelyr;

import net.fabricmc.api.ModInitializer;
import net.msymbios.rlovelyr.config.LovelyRobotConfig;
import net.msymbios.rlovelyr.entity.LovelyRobotEntities;
import net.msymbios.rlovelyr.event.LovelyRobotEvents;
import net.msymbios.rlovelyr.item.LovelyRobotItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib3.GeckoLib;

public class LovelyRobot implements ModInitializer {

	// -- Variables --
	public static final String MODID = "rlovelyr";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	// -- Methods --
	@Override
	public void onInitialize() {
		GeckoLib.initialize();
		LovelyRobotConfig.register();
		LovelyRobotItems.register();
		LovelyRobotEvents.register();
		LovelyRobotEntities.registerAttribute();
	} // onInitialize ()

} // Class LovelyRobot