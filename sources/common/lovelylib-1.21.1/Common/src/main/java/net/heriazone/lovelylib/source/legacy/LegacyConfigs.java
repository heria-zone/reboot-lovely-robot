package net.heriazone.lovelylib.source.legacy;

import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.lovelylib.common.configs.*;

public class LegacyConfigs extends LovelyConfigs {

    // -- Import --

    static {
        Default.clear();

        // BUNNY - Balanced all-rounder with moderate stats
        Default.put(LovelyConstant.VARIANT_BUNNY, new SharedConfigs.EntityConfigData(
                200,
                26,
                5,
                1.7F,
                4,
                0F,
                0.37F
        ));

        // BUNNY2 - Enhanced bunny with improved stats
        Default.put(LovelyConstant.VARIANT_BUNNY2, new SharedConfigs.EntityConfigData(
                200,
                27,
                6,
                1.8F,
                5,
                0F,
                0.36F
        ));

        // DRAGON - Tank with high HP and toughness
        Default.put(LovelyConstant.VARIANT_DRAGON, new SharedConfigs.EntityConfigData(
                200,
                30,
                8,
                1.0F,
                7,
                0F,
                0.3F
        ));

        // HONEY - Support type with healing focus
        Default.put(LovelyConstant.VARIANT_HONEY, new SharedConfigs.EntityConfigData(
                200,
                29,
                4,
                1.1F,
                5,
                0F,
                0.31F
        ));

        // KITSUNE - Agile with balanced offense/defense
        Default.put(LovelyConstant.VARIANT_KITSUNE, new SharedConfigs.EntityConfigData(
                200,
                28,
                2,
                1.3F,
                6,
                0F,
                0.33F
        ));

        // NEKO - High damage glass cannon
        Default.put(LovelyConstant.VARIANT_NEKO, new SharedConfigs.EntityConfigData(
                200,
                28,
                7,
                1.4F,
                5,
                0F,
                0.34F
        ));

        // VANILLA - Original balanced design
        Default.put(LovelyConstant.VARIANT_VANILLA, new SharedConfigs.EntityConfigData(
                200,
                25,
                5,
                1.3F,
                5,
                0F,
                0.32F
        ));
    }

} // Class: LegacyConfigs