package net.heriazone.lovelylib.source.tribute;

import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.lovelylib.common.configs.*;

import java.util.HashMap;

public class TributeConfigs {

    // -- Variables --

    public static HashMap<String, SharedConfigs.EntityConfigData> Default = new HashMap<>();
    public static HashMap<String, SharedConfigs.EntityConfigData> Entities = new HashMap<>();

    // -- Import --

    static {
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

    // -- Methods --

    public static SharedConfigs.EntityConfigData getDefaultConfig(String variant) {
        SharedConfigs.EntityConfigData config = Default.get(variant);
        if (config == null) return SharedConfigs.EntityConfigData.getDefault();
        return config.validateOrDefault();
    } // getDefaultConfig()

    /**
     * Retrieves entity configuration for specified variant with fallback.
     * <p>
     * <b>Fallback Strategy:</b> Returns default configuration if variant
     * not found or configuration is invalid.
     *
     * @param variant robot variant identifier
     * @return validated entity configuration
     */
    public static SharedConfigs.EntityConfigData getEntityConfig(String variant) {
        SharedConfigs.EntityConfigData config = Entities.get(variant);
        if (config == null) return SharedConfigs.EntityConfigData.getDefault();
        return config.validateOrDefault();
    } // getEntityConfig()

    /**
     * Reloads entity configurations from current config values.
     * <p>
     * <b>Runtime Reload:</b> Called by config system when configurations
     * change at runtime. Rebuilds entity map with validated configurations.
     */
    public static void reloadEntityConfigs() {
        // This will be enhanced when dynamic config loading is implemented
        // For now, the static initialization handles the configuration
    } // reloadEntityConfigs()

} // Class: RebootConfigs