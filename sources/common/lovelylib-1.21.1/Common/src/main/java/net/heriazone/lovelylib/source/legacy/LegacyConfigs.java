package net.heriazone.lovelylib.source.legacy;

import net.heriazone.lovelylib.common.configs.*;
import net.heriazone.lovelylib.common.entity.definition.ModTarget;
import net.heriazone.lovelylib.common.entity.definition.RobotDefinitionRegistry;
import net.heriazone.lovelylib.common.entity.definition.RobotEntityDefinition;

import java.util.HashMap;

public class LegacyConfigs {

    // -- Variables --

    public static HashMap<String, SharedConfigs.EntityConfigData> Default  = new HashMap<>();
    public static HashMap<String, SharedConfigs.EntityConfigData> Entities = new HashMap<>();

    // -- Import --

    static {
        // Populated from RobotDefinitionRegistry so no per-entity entries are ever needed here.
        // RobotDefinitionRegistry.seal() is guaranteed to have run before this class is loaded
        // because Lovely.onInitialize() is called first in every loader entry point.
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
            Default.put(def.getVariantKey(), def.getStats().toEntityConfigData());
        }
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
     * <b>Fallback Chain:</b> Entities map (runtime-loaded) → Default map
     * (variant-specific stat definitions) → generic baseline. The middle
     * tier is the critical one: on Forge/NeoForge, loadDynamicEntityConfigs()
     * never runs, so Entities is always empty. Without this fallback the
     * variant falls through to the generic baseline (baseHp=20, attackSpeed=1.5F),
     * silently ignoring the real per-variant stats. This was Bug 2 from ADR-023.
     *
     * @param variant robot variant identifier
     * @return validated entity configuration
     */
    public static SharedConfigs.EntityConfigData getEntityConfig(String variant) {
        SharedConfigs.EntityConfigData config = Entities.get(variant);
        if (config == null) return getDefaultConfig(variant);
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

} // Class: LegacyConfigs