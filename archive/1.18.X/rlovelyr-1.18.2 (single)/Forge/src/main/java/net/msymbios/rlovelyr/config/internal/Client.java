package net.msymbios.rlovelyr.config.internal;

import com.electronwill.nightconfig.core.Config;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Config options only available to each client.
 */
public class Client {

    // -- Variables --

    public final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // -- RENDERER --
    public final ForgeConfigSpec.ConfigValue<Double> shadowRadius;

    // -- Constructor --

    public Client() {
        Config.setInsertionOrderPreserved(true);

        BUILDER.push("Renderer");
        shadowRadius = BUILDER
                .comment("Entity shadow cast size on the ground.", "Example: [0.4]")
                .define("shadow-radius", 0.4);
        BUILDER.pop();

    } // Constructor Client ()

} // Class Client