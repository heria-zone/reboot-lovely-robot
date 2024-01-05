package net.msymbios.rlovelyr.config;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.msymbios.rlovelyr.LovelyRobot;

public class LovelyRobotConfig {

    // -- Methods --
    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, LovelyRobotClientConfig.SPEC, LovelyRobot.MODID + "-client.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, LovelyRobotCommonConfig.SPEC, LovelyRobot.MODID + "-common.toml");
    } // register ()

} // Class LovelyRobotConfig