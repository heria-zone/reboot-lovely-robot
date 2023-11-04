package net.msymbios.rlovelyr;

import net.fabricmc.api.ModInitializer;
import net.msymbios.rlovelyr.entity.LovelyRobotEntities;
import net.msymbios.rlovelyr.item.LovelyRobotItems;
import net.msymbios.rlovelyr.item.LovelyRobotItemsGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;

public class LovelyRobot implements ModInitializer {

	// -- Variables --
	public static final String MODID = "rlovelyr";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	// -- Methods --
	@Override
	public void onInitialize() {
		GeckoLib.initialize();
		
		LovelyRobotItemsGroup.register();
		LovelyRobotItems.register();
		LovelyRobotEntities.registerAttribute();
	} // onInitialize ()

} // Class LovelyRobot