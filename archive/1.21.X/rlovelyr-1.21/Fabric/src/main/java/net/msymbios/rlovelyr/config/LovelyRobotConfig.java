package net.msymbios.rlovelyr.config;

import eu.midnightdust.lib.config.MidnightConfig;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.config.internal.Client;
import net.msymbios.rlovelyr.config.internal.Common;

public class LovelyRobotConfig {

    // -- Variables --

    public static Common COMMON;
    public static Client CLIENT;

    // -- Methods --

    public static void register() {
        MidnightConfig.init(LovelyRobot.MODID, Common.class);
        MidnightConfig.init(LovelyRobot.MODID, Client.class);
    } // register ()

} // Class LovelyRobotConfig