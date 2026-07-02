package net.heriazone.lovelylib.source.tribute;

import net.heriazone.lovelylib.common.entity.definition.RobotVariant;
import net.heriazone.lovelylib.common.configs.*;

import java.util.HashMap;

public class TributeConfigs {

    // -- Variables --

    public static HashMap<String, SharedConfigs.EntityConfigData> Default  = new HashMap<>();
    public static HashMap<String, SharedConfigs.EntityConfigData> Entities = new HashMap<>();

    // -- Import --

    static {
        // Tribute has a fixed roster excluded from RobotDefinitionRegistry.
        // Variant keys are sourced from RobotVariant constants directly.
        Default.put(RobotVariant.Bunny.getName(), new SharedConfigs.EntityConfigData(
                200, 30, 5, 1.2F, 5, 0F, 0.3F));
        Default.put(RobotVariant.Bunny2.getName(), new SharedConfigs.EntityConfigData(
                200, 30, 5, 1.2F, 6, 0F, 0.3F));
        Default.put(RobotVariant.Honey.getName(), new SharedConfigs.EntityConfigData(
                200, 30, 6, 1.2F, 5, 0F, 0.3F));
        Default.put(RobotVariant.Vanilla.getName(), new SharedConfigs.EntityConfigData(
                200, 30, 5, 1.2F, 6, 0F, 0.3F));
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
     * <b>Fallback Chain:</b> Entities map (runtime-loaded) → Default map → generic
     * baseline. Mirrors LegacyConfigs / RebootConfigs fallback semantics for Forge
     * parity — on Forge/NeoForge, Entities is always empty so Default is the active tier.
     *
     * @param variant robot variant identifier
     * @return validated entity configuration
     */
    public static SharedConfigs.EntityConfigData getEntityConfig(String variant) {
        SharedConfigs.EntityConfigData config = Entities.get(variant);
        if (config == null) return getDefaultConfig(variant);
        return config.validateOrDefault();
    } // getEntityConfig()

    public static void reloadEntityConfigs() {
        // Populated by Fabric-side TributeConfigs.loadDynamicEntityConfigs()
    } // reloadEntityConfigs()

} // Class: TributeConfigs