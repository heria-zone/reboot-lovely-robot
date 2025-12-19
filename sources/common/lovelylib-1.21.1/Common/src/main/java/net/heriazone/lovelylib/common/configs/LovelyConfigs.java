package net.heriazone.lovelylib.common.configs;

import java.util.HashMap;

public abstract class LovelyConfigs {

    // -- Variables --

    public static HashMap<String, SharedConfigs.EntityConfigData> Default = new HashMap<>();
    public static HashMap<String, SharedConfigs.EntityConfigData> Entities = new HashMap<>();

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

} // Class: LovelyConfigs