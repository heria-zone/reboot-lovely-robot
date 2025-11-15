package net.msymbios.rlovelyr.config;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.msymbios.rlovelyr.LovelyRobot;

public class LovelyRobotConfigs {

    // -- Methods --
    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, LovelyRobotClientConfigs.SPEC, LovelyRobot.MODID + "-client.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, LovelyRobotCommonConfigs.SPEC, LovelyRobot.MODID + "-common.toml");
    } // register ()

} // Class LovelyRobotConfigs