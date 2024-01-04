package net.msymbios.rlovelyr.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class LovelyRobotClientConfigs {

    // -- Variables --
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    static {
        BUILDER.push("Reboot LovelyRobot Configs");

        // DEFINE THE CONFIG

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

} // Class LovelyRobotClientConfigs