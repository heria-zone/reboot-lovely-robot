package net.msymbios.rlovelyr;

import net.fabricmc.api.ModInitializer;
import net.msymbios.rlovelyr.config.LovelyRobotConfig;
import net.msymbios.rlovelyr.entity.LovelyRobotEntities;
import net.msymbios.rlovelyr.event.CraftHandler;
import net.msymbios.rlovelyr.event.ItemCraftCallback;
import net.msymbios.rlovelyr.event.LovelyRobotEvents;
import net.msymbios.rlovelyr.item.LovelyRobotItems;
import net.msymbios.rlovelyr.item.LovelyRobotItemsGroup;
import net.msymbios.rlovelyr.particles.LovelyRobotParticles;
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

		LovelyRobotConfig.register();
		LovelyRobotItemsGroup.register();

		LovelyRobotItems.register();
		LovelyRobotEvents.register();

		LovelyRobotEntities.registerAttribute();
		LovelyRobotParticles.register();
	} // onInitialize ()

} // Class LovelyRobot