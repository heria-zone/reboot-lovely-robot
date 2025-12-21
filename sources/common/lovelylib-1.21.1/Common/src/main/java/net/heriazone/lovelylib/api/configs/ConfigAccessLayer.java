package net.heriazone.lovelylib.api.configs;

import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides unified access layer for entity configurations across all loaders.
 * <p>
 * <b>Architecture:</b> Abstracts loader-specific configuration access behind
 * common interface. Handles fallback chains and validation consistently.
 * <p>
 * <b>Fallback Strategy:</b> Serialized configs → Individual keys → Defaults
 * <p>
 * <b>Thread Safety:</b> All access methods are thread-safe for concurrent
 * config reading during gameplay.
 */
public class ConfigAccessLayer {

    // -- Storage Maps (Thread-Safe) --
    
    private static final Map<String, Integer> intConfigCache = new ConcurrentHashMap<>();
    private static final Map<String, Float> floatConfigCache = new ConcurrentHashMap<>();
    private static final Map<String, String> serializedConfigCache = new ConcurrentHashMap<>();

    // -- Public Access Methods --

    /**
     * Gets complete entity configuration for specified variant.
     * <p>
     * <b>Fallback Chain:</b>
     * 1. Try serialized configuration (Phase 2)
     * 2. Try individual configuration keys (Phase 1)
     * 3. Fall back to default configuration
     * 
     * @param variant the robot variant identifier
     * @return validated EntityConfigData instance
     */
    public static SharedConfigs.EntityConfigData getEntityConfig(String variant) {
        // Phase 2: Try serialized config
        SharedConfigs.EntityConfigData serializedConfig = tryGetSerializedConfig(variant);
        if (serializedConfig != null && serializedConfig.isValid()) {
            return serializedConfig;
        }
        
        // Phase 1: Try individual keys
        SharedConfigs.EntityConfigData individualConfig = tryGetIndividualConfig(variant);
        if (individualConfig != null && individualConfig.isValid()) {
            return individualConfig;
        }
        
        // Fallback: Use defaults
        return getDefaultEntityConfig(variant);
    } // getEntityConfig()

    /**
     * Gets integer configuration value for specific variant and config type.
     * 
     * @param variant the robot variant identifier
     * @param configType the configuration type identifier
     * @return configuration value or default if not found
     */
    public static int getEntityIntConfig(String variant, String configType) {
        String key = ConfigKeyGenerator.generateKey(variant, configType);
        return intConfigCache.getOrDefault(key, getDefaultIntValue(variant, configType));
    } // getEntityIntConfig()

    /**
     * Gets float configuration value for specific variant and config type.
     * 
     * @param variant the robot variant identifier
     * @param configType the configuration type identifier
     * @return configuration value or default if not found
     */
    public static float getEntityFloatConfig(String variant, String configType) {
        String key = ConfigKeyGenerator.generateKey(variant, configType);
        return floatConfigCache.getOrDefault(key, getDefaultFloatValue(variant, configType));
    } // getEntityFloatConfig()

    /**
     * Gets serialized configuration string for specific variant.
     * 
     * @param variant the robot variant identifier
     * @return serialized configuration string or empty if not found
     */
    public static String getSerializedConfig(String variant) {
        String key = variant + "-config";
        return serializedConfigCache.getOrDefault(key, "");
    } // getSerializedConfig()

    // -- Internal Helper Methods --

    /**
     * Attempts to load configuration from serialized format.
     * 
     * @param variant the robot variant identifier
     * @return EntityConfigData or null if not available/invalid
     */
    private static SharedConfigs.EntityConfigData tryGetSerializedConfig(String variant) {
        String serializedData = getSerializedConfig(variant);
        
        if (serializedData == null || serializedData.trim().isEmpty()) {
            return null;
        }
        
        try {
            SharedConfigs.EntityConfigData config = EntityConfigSerializer.deserialize(serializedData);
            if (config != null && config.isValid()) {
                return EntityConfigSerializer.sanitizeConfig(config);
            }
        } catch (Exception e) {
            // Log error but continue to fallback
            System.err.println("Failed to deserialize config for variant '" + variant + "': " + e.getMessage());
        }
        
        return null;
    } // tryGetSerializedConfig()

    /**
     * Attempts to load configuration from individual keys.
     * 
     * @param variant the robot variant identifier
     * @return EntityConfigData or null if not available/invalid
     */
    private static SharedConfigs.EntityConfigData tryGetIndividualConfig(String variant) {
        try {
            return new SharedConfigs.EntityConfigData.Builder()
                .maxLevel(getEntityIntConfig(variant, LovelyConstant.CONFIG_MAX_LEVEL))
                .attackSpeed(getEntityFloatConfig(variant, LovelyConstant.CONFIG_ATTACK_SPEED))
                .movementSpeed(getEntityFloatConfig(variant, LovelyConstant.CONFIG_MOVEMENT_SPEED))
                .baseToughness(getEntityFloatConfig(variant, LovelyConstant.CONFIG_BASE_TOUGHNESS))
                .baseHp(getEntityIntConfig(variant, LovelyConstant.CONFIG_BASE_HP))
                .baseAttack(getEntityIntConfig(variant, LovelyConstant.CONFIG_BASE_ATTACK))
                .baseDefense(getEntityIntConfig(variant, LovelyConstant.CONFIG_BASE_DEFENSE))
                .build();
        } catch (Exception e) {
            // Log error and return null to trigger fallback
            System.err.println("Failed to load individual configs for variant '" + variant + "': " + e.getMessage());
            return null;
        }
    } // tryGetIndividualConfig()

    /**
     * Gets default entity configuration for specified variant.
     * 
     * @param variant the robot variant identifier
     * @return default EntityConfigData instance
     */
    private static SharedConfigs.EntityConfigData getDefaultEntityConfig(String variant) {
        // Use SharedConfigs defaults based on variant
        return switch (variant) {
            case LovelyConstant.VARIANT_BUNNY -> new SharedConfigs.EntityConfigData(
                SharedConfigs.Common.BunnyMaxLevel,
                SharedConfigs.Common.BunnyBaseHp,
                SharedConfigs.Common.BunnyBaseAttack,
                SharedConfigs.Common.BunnyAttackSpeed,
                SharedConfigs.Common.BunnyBaseDefense,
                SharedConfigs.Common.BunnyBaseToughness,
                SharedConfigs.Common.BunnyMovementSpeed
            );
            
            case LovelyConstant.VARIANT_BUNNY2 -> new SharedConfigs.EntityConfigData(
                SharedConfigs.Common.Bunny2MaxLevel,
                SharedConfigs.Common.Bunny2BaseHp,
                SharedConfigs.Common.Bunny2BaseAttack,
                SharedConfigs.Common.Bunny2AttackSpeed,
                SharedConfigs.Common.Bunny2BaseDefense,
                SharedConfigs.Common.Bunny2BaseToughness,
                SharedConfigs.Common.Bunny2MovementSpeed
            );
            
            case LovelyConstant.VARIANT_DRAGON -> new SharedConfigs.EntityConfigData(
                SharedConfigs.Common.DragonMaxLevel,
                SharedConfigs.Common.DragonBaseHp,
                SharedConfigs.Common.DragonBaseAttack,
                SharedConfigs.Common.DragonAttackSpeed,
                SharedConfigs.Common.DragonBaseDefense,
                SharedConfigs.Common.DragonBaseToughness,
                SharedConfigs.Common.DragonMovementSpeed
            );
            
            case LovelyConstant.VARIANT_HONEY -> new SharedConfigs.EntityConfigData(
                SharedConfigs.Common.HoneyMaxLevel,
                SharedConfigs.Common.HoneyBaseHp,
                SharedConfigs.Common.HoneyBaseAttack,
                SharedConfigs.Common.HoneyAttackSpeed,
                SharedConfigs.Common.HoneyBaseDefense,
                SharedConfigs.Common.HoneyBaseToughness,
                SharedConfigs.Common.HoneyMovementSpeed
            );
            
            case LovelyConstant.VARIANT_KITSUNE -> new SharedConfigs.EntityConfigData(
                SharedConfigs.Common.KitsuneMaxLevel,
                SharedConfigs.Common.KitsuneBaseHp,
                SharedConfigs.Common.KitsuneBaseAttack,
                SharedConfigs.Common.KitsuneAttackSpeed,
                SharedConfigs.Common.KitsuneBaseDefense,
                SharedConfigs.Common.KitsuneBaseToughness,
                SharedConfigs.Common.KitsuneMovementSpeed
            );
            
            case LovelyConstant.VARIANT_NEKO -> new SharedConfigs.EntityConfigData(
                SharedConfigs.Common.NekoMaxLevel,
                SharedConfigs.Common.NekoBaseHp,
                SharedConfigs.Common.NekoBaseAttack,
                SharedConfigs.Common.NekoAttackSpeed,
                SharedConfigs.Common.NekoBaseDefense,
                SharedConfigs.Common.NekoBaseToughness,
                SharedConfigs.Common.NekoMovementSpeed
            );
            
            case LovelyConstant.VARIANT_VANILLA -> new SharedConfigs.EntityConfigData(
                SharedConfigs.Common.VanillaMaxLevel,
                SharedConfigs.Common.VanillaBaseHp,
                SharedConfigs.Common.VanillaBaseAttack,
                SharedConfigs.Common.VanillaAttackSpeed,
                SharedConfigs.Common.VanillaBaseDefense,
                SharedConfigs.Common.VanillaBaseToughness,
                SharedConfigs.Common.VanillaMovementSpeed
            );
            
            default -> SharedConfigs.EntityConfigData.getDefault();
        };
    } // getDefaultEntityConfig()

    /**
     * Gets default integer value for specific variant and config type.
     * 
     * @param variant the robot variant identifier
     * @param configType the configuration type identifier
     * @return default integer value
     */
    private static int getDefaultIntValue(String variant, String configType) {
        SharedConfigs.EntityConfigData defaultConfig = getDefaultEntityConfig(variant);
        
        return switch (configType) {
            case LovelyConstant.CONFIG_MAX_LEVEL -> defaultConfig.maxLevel;
            case LovelyConstant.CONFIG_BASE_HP -> defaultConfig.baseHp;
            case LovelyConstant.CONFIG_BASE_ATTACK -> defaultConfig.baseAttack;
            case LovelyConstant.CONFIG_BASE_DEFENSE -> defaultConfig.baseDefense;
            default -> 0;
        };
    } // getDefaultIntValue()

    /**
     * Gets default float value for specific variant and config type.
     * 
     * @param variant the robot variant identifier
     * @param configType the configuration type identifier
     * @return default float value
     */
    private static float getDefaultFloatValue(String variant, String configType) {
        SharedConfigs.EntityConfigData defaultConfig = getDefaultEntityConfig(variant);
        
        return switch (configType) {
            case LovelyConstant.CONFIG_ATTACK_SPEED -> defaultConfig.attackSpeed;
            case LovelyConstant.CONFIG_MOVEMENT_SPEED -> defaultConfig.movementSpeed;
            case LovelyConstant.CONFIG_BASE_TOUGHNESS -> defaultConfig.baseToughness;
            default -> 0.0f;
        };
    } // getDefaultFloatValue()

    // -- Cache Management Methods (Loader-Specific) --

    /**
     * Updates integer configuration cache (called by loader-specific implementations).
     * 
     * @param key the configuration key
     * @param value the configuration value
     */
    public static void updateIntConfigCache(String key, int value) {
        intConfigCache.put(key, value);
    } // updateIntConfigCache()

    /**
     * Updates float configuration cache (called by loader-specific implementations).
     * 
     * @param key the configuration key
     * @param value the configuration value
     */
    public static void updateFloatConfigCache(String key, float value) {
        floatConfigCache.put(key, value);
    } // updateFloatConfigCache()

    /**
     * Updates serialized configuration cache (called by loader-specific implementations).
     * 
     * @param key the configuration key
     * @param value the serialized configuration data
     */
    public static void updateSerializedConfigCache(String key, String value) {
        serializedConfigCache.put(key, value);
    } // updateSerializedConfigCache()

    /**
     * Clears all configuration caches (used during config reload).
     */
    public static void clearCaches() {
        intConfigCache.clear();
        floatConfigCache.clear();
        serializedConfigCache.clear();
    } // clearCaches()

    // -- Debug and Utility Methods --

    /**
     * Gets current cache statistics for debugging.
     * 
     * @return map containing cache size information
     */
    public static Map<String, Integer> getCacheStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("intConfigs", intConfigCache.size());
        stats.put("floatConfigs", floatConfigCache.size());
        stats.put("serializedConfigs", serializedConfigCache.size());
        return stats;
    } // getCacheStats()

    /**
     * Validates all cached configurations and reports issues.
     * 
     * @return map of variant to validation status
     */
    public static Map<String, Boolean> validateAllConfigs(String[] variants) {
        Map<String, Boolean> results = new HashMap<>();

        for (String variant : variants) {
            try {
                SharedConfigs.EntityConfigData config = getEntityConfig(variant);
                results.put(variant, config != null && config.isValid());
            } catch (Exception e) {
                results.put(variant, false);
            }
        }

        return results;
    } // validateAllConfigs()

    public static Map<String, Boolean> validateAllConfigs() {
        return validateAllConfigs(LovelyConstant.ALL_VARIANTS);
    } // validateAllConfigs()

} // Class: ConfigAccessLayer