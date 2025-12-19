package net.heriazone.lovelylib.source.tribute;

import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.lovelylib.common.configs.*;

public class TributeConfigs extends LovelyConfigs {

    // -- Import --

    static {
        Default.clear();

        // BUNNY - Balanced all-rounder with moderate stats
        Default.put(LovelyConstant.VARIANT_BUNNY, new SharedConfigs.EntityConfigData(
                200,
                30,
                5,
                1.2F,
                5,
                0F,
                0.3F
        ));

        // BUNNY2 - Enhanced bunny with improved stats
        Default.put(LovelyConstant.VARIANT_BUNNY2, new SharedConfigs.EntityConfigData(
                200,
                30,
                5,
                1.2F,
                6,
                0F,
                0.3F
        ));

        // HONEY - Support type with healing focus
        Default.put(LovelyConstant.VARIANT_HONEY, new SharedConfigs.EntityConfigData(
                200,
                30,
                6,
                1.2F,
                5,
                0F,
                0.3F
        ));

        // VANILLA - Original balanced design
        Default.put(LovelyConstant.VARIANT_VANILLA, new SharedConfigs.EntityConfigData(
                200,
                30,
                5,
                1.2F,
                6,
                0F,
                0.3F
        ));
    }

} // Class: RebootConfigs