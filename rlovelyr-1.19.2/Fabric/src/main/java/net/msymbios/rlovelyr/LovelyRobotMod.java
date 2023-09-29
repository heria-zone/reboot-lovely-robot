package net.msymbios.rlovelyr;

import net.fabricmc.api.ModInitializer;
import net.msymbios.rlovelyr.config.ModConfigs;
import net.msymbios.rlovelyr.entity.ModEntities;
import net.msymbios.rlovelyr.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib3.GeckoLib;

public class LovelyRobotMod implements ModInitializer {

	// -- Variables --
	public static final String MODID = "rlovelyr";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	// -- Methods --
	@Override
	public void onInitialize() {
		ModConfigs.registerConfigs();
		ModItems.registerItems();

		GeckoLib.initialize();
		ModEntities.registerAttribute();
	} // onInitialize ()

} // Class LovelyRobotMod