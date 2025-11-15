package net.msymbios.rlovelyr;

import net.fabricmc.api.ModInitializer;

import net.msymbios.rlovelyr.common.util.ObjectUtil;
import net.msymbios.rlovelyr.common.util.internal.Version;
import net.msymbios.rlovelyr.entity.LovelyRobotEntities;
import net.msymbios.rlovelyr.event.LovelyRobotEvents;
import net.msymbios.rlovelyr.item.LovelyRobotItems;
import net.msymbios.rlovelyr.item.LovelyRobotItemsGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	/**
	 * The mod's logger.
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	// -- Inherited Methods --

	@Override
	public void onInitialize() {
		LovelyRobotItemsGroup.register();
		LovelyRobotItems.register();
		LovelyRobotEvents.register();
		LovelyRobotEntities.registerAttribute();
	} // onInitialize ()

} // Class LovelyRobot