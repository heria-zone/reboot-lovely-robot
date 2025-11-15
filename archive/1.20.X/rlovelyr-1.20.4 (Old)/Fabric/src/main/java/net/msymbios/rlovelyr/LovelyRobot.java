package net.msymbios.rlovelyr;

import net.fabricmc.api.ModInitializer;

import net.msymbios.rlovelyr.config.LovelyRobotConfig;
import net.msymbios.rlovelyr.entity.LovelyRobotEntities;
import net.msymbios.rlovelyr.event.LovelyRobotEvents;
import net.msymbios.rlovelyr.item.LovelyRobotItems;
import net.msymbios.rlovelyr.item.LovelyRobotItemsGroup;

import net.msymbios.rlovelyr.network.NetworkHandler;
import net.msymbios.rlovelyr.util.ObjectUtil;
import net.msymbios.rlovelyr.util.internal.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;

public class LovelyRobot implements ModInitializer {

	// -- Variables --
	/**
	 * The mod's ID, which is also the mod's namespace.
	 */
	public static final String MODID = "rlovelyr";

	/**
	 * The mod's version.
	 */
	public static final Version VERSION = new Version(ObjectUtil.coalesce(LovelyRobot.class.getPackage().getSpecificationVersion(), "99999.0.0.0"));

	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	/**
	 * This method is called when the mod is being initialized.
	 * It initializes GeckoLib, registers config, items, events, and entities for LovelyRobot.
	 */
	@Override
	public void onInitialize() {
		GeckoLib.initialize();

		LovelyRobotConfig.register();
		LovelyRobotItemsGroup.register();

		LovelyRobotItems.register();
		LovelyRobotEvents.register();

		NetworkHandler.register();

		LovelyRobotEntities.registerAttribute();
	} // onInitialize ()

} // Class LovelyRobot