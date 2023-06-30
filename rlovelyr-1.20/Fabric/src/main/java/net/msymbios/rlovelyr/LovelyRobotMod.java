package net.msymbios.rlovelyr;

import net.fabricmc.api.ModInitializer;
import net.msymbios.rlovelyr.entity.ModEntities;
import net.msymbios.rlovelyr.item.ModItems;
import net.msymbios.rlovelyr.item.ModItemsGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;

public class LovelyRobotMod implements ModInitializer {

	// -- Variables --
	public static final String MODID = "rlovelyr";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	// -- Methods --
	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		// ModItemsGroup.registerItemsGroups();

		GeckoLib.initialize();

		ModEntities.registerEntityAttribute();
	} // onInitialize ()

} // Class LovelyRobotMod