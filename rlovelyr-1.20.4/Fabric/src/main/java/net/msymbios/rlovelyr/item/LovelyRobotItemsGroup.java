package net.msymbios.rlovelyr.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.registry.RegistryKey;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.common.item.InternalItemsGroup;
import net.msymbios.rlovelyr.config.LovelyRobotID;

public class LovelyRobotItemsGroup extends InternalItemsGroup {

    // -- Variables --

    // TAB
    public static final ItemGroup DEFAULT_TAB = register(LovelyRobotID.getId(LovelyRobotID.DEFAULT_TAB), registerGroup(LovelyRobotID.getTabTranslation(LovelyRobotID.DEFAULT_TAB), LovelyRobotItems.ROBOT_CORE));
    public static final ItemGroup ALDARIAN_TECH_TAB = register(LovelyRobotID.getId(LovelyRobotID.ALDARIAN_TECH_TAB), registerGroup(LovelyRobotID.getTabTranslation(LovelyRobotID.ALDARIAN_TECH_TAB), LovelyRobotItems.ROBOT_CORE_ALDARIAN));

    // KEY
    public static final RegistryKey<ItemGroup> DEFAULT_KEY = registerKey(LovelyRobotID.getId(LovelyRobotID.DEFAULT_TAB));
    public static final RegistryKey<ItemGroup> ALDARIAN_TECH_KEY = registerKey(LovelyRobotID.getId(LovelyRobotID.ALDARIAN_TECH_TAB));

    // -- Methods --

    public static void register () {
        LovelyRobot.LOGGER.info("Registering CreativeTabs for: " + LovelyRobot.MODID);
    } // register ()

} // Class LovelyRobotItemsGroup