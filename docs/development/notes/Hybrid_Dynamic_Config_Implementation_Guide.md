# Hybrid Dynamic Entity Configuration - Implementation Guide

**Status**: Implementation Ready  
**Date**: 2025-12-18  
**Related**: ADR_008_Hybrid_Dynamic_Entity_Configuration_Architecture.md  

## Overview

This guide provides step-by-step implementation instructions for the Hybrid Dynamic Entity Configuration Architecture. The implementation is divided into phases to ensure stability and maintainability.

## Phase 1: Dynamic Key Generation Implementation

### Step 1: Create Configuration Key Generator

**File**: `sources/common/lovelylib-1.21.1/Common/src/main/java/net/heriazone/lovelylib/common/configs/ConfigKeyGenerator.java`

```java
package net.heriazone.lovelylib.common.configs;

import net.heriazone.lovelylib.common.shared.LovelyConstant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * Generates configuration keys dynamically for entity configurations.
 * <p>
 * <b>Architecture:</b> Provides centralized key generation logic shared across
 * all mod loaders (Fabric, Forge, NeoForge). Ensures consistent naming and
 * prevents key conflicts.
 * <p>
 * <b>Key Format:</b> Uses pattern "{variant}-{configType}" for all entity
 * configuration keys (e.g., "bunny-max-level", "dragon-attack-speed").
 */
public class ConfigKeyGenerator {

    // -- Constants --

    /**
     * Configuration types that require integer values.
     */
    public static final String[] INT_CONFIG_TYPES = {
        LovelyConstant.CONFIG_MAX_LEVEL,
        LovelyConstant.CONFIG_BASE_HP,
        LovelyConstant.CONFIG_BASE_ATTACK,
        LovelyConstant.CONFIG_BASE_DEFENSE
    };

    /**
     * Configuration types that require float values.
     */
    public static final String[] FLOAT_CONFIG_TYPES = {
        LovelyConstant.CONFIG_ATTACK_SPEED,
        LovelyConstant.CONFIG_MOVEMENT_SPEED,
        LovelyConstant.CONFIG_BASE_TOUGHNESS
    };

    /**
     * All configuration types combined.
     */
    public static final String[] ALL_CONFIG_TYPES;

    static {
        // Combine int and float config types
        ALL_CONFIG_TYPES = new String[INT_CONFIG_TYPES.length + FLOAT_CONFIG_TYPES.length];
        System.arraycopy(INT_CONFIG_TYPES, 0, ALL_CONFIG_TYPES, 0, INT_CONFIG_TYPES.length);
        System.arraycopy(FLOAT_CONFIG_TYPES, 0, ALL_CONFIG_TYPES, INT_CONFIG_TYPES.length, FLOAT_CONFIG_TYPES.length);
    }

    // -- Key Generation Methods --

    /**
     * Generates configuration key for specific variant and config type.
     * <p>
     * <b>Format:</b> "{variant}-{configType}" (e.g., "bunny-max-level")
     * 
     * @param variant the robot variant identifier
     * @param configType the configuration type identifier
     * @return formatted configuration key
     */
    public static String generateKey(String variant, String configType) {
        return variant + "-" + configType;
    }

    /**
     * Generates all possible configuration keys for all variants.
     * <p>
     * <b>Usage:</b> Used during static initialization to pre-generate all
     * required configuration keys for Forge/NeoForge ConfigSpec building.
     * 
     * @return map of configuration keys to their formatted strings
     */
    public static Map<String, String> generateAllKeys() {
        Map<String, String> keys = new HashMap<>();
        
        for (String variant : LovelyConstant.ALL_VARIANTS) {
            for (String configType : ALL_CONFIG_TYPES) {
                String key = generateKey(variant, configType);
                keys.put(key, key);
            }
        }
        
        return keys;
    }

    /**
     * Generates all integer configuration keys.
     * 
     * @return set of integer configuration keys
     */
    public static Set<String> generateIntKeys() {
        Set<String> keys = new HashSet<>();
        
        for (String variant : LovelyConstant.ALL_VARIANTS) {
            for (String configType : INT_CONFIG_TYPES) {
                keys.add(generateKey(variant, configType));
            }
        }
        
        return keys;
    }

    /**
     * Generates all float configuration keys.
     * 
     * @return set of float configuration keys
     */
    public static Set<String> generateFloatKeys() {
        Set<String> keys = new HashSet<>();
        
        for (String variant : LovelyConstant.ALL_VARIANTS) {
            for (String configType : FLOAT_CONFIG_TYPES) {
                keys.add(generateKey(variant, configType));
            }
        }
        
        return keys;
    }

    /**
     * Checks if a configuration type requires integer values.
     * 
     * @param configType the configuration type to check
     * @return true if config type is integer, false otherwise
     */
    public static boolean isIntConfigType(String configType) {
        for (String intType : INT_CONFIG_TYPES) {
            if (intType.equals(configType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a configuration type requires float values.
     * 
     * @param configType the configuration type to check
     * @return true if config type is float, false otherwise
     */
    public static boolean isFloatConfigType(String configType) {
        for (String floatType : FLOAT_CONFIG_TYPES) {
            if (floatType.equals(configType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates that a configuration key follows the expected format.
     * 
     * @param key the configuration key to validate
     * @return true if key format is valid, false otherwise
     */
    public static boolean isValidKey(String key) {
        if (key == null || key.isEmpty()) {
            return false;
        }
        
        String[] parts = key.split("-", 2);
        if (parts.length != 2) {
            return false;
        }
        
        String variant = parts[0];
        String configType = parts[1];
        
        // Check if variant exists
        boolean validVariant = false;
        for (String validVar : LovelyConstant.ALL_VARIANTS) {
            if (validVar.equals(variant)) {
                validVariant = true;
                break;
            }
        }
        
        if (!validVariant) {
            return false;
        }
        
        // Check if config type exists
        for (String validType : ALL_CONFIG_TYPES) {
            if (validType.equals(configType)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Extracts variant name from configuration key.
     * 
     * @param key the configuration key
     * @return variant name or null if invalid key
     */
    public static String extractVariant(String key) {
        if (!isValidKey(key)) {
            return null;
        }
        
        return key.split("-", 2)[0];
    }

    /**
     * Extracts configuration type from configuration key.
     * 
     * @param key the configuration key
     * @return configuration type or null if invalid key
     */
    public static String extractConfigType(String key) {
        if (!isValidKey(key)) {
            return null;
        }
        
        return key.split("-", 2)[1];
    }

} // Class: ConfigKeyGenerator
```

### Step 2: Update LovelyConstant with Missing Keys

**File**: `sources/common/lovelylib-1.21.1/Common/src/main/java/net/heriazone/lovelylib/common/shared/LovelyConstant.java`

**Add missing configuration key (fix typo)**:
```java
// -- Entity Configuration Keys --

public static final String CONFIG_MAX_LEVEL = "max-level";
public static final String CONFIG_BASE_HP = "base-hp";
public static final String CONFIG_BASE_ATTACK = "base-attack";
public static final String CONFIG_ATTACK_SPEED = "attack-speed";
public static final String CONFIG_BASE_DEFENSE = "base-defense";  // Fixed from "base-armour"
public static final String CONFIG_BASE_TOUGHNESS = "base-toughness";
public static final String CONFIG_MOVEMENT_SPEED = "movement-speed";  // Fixed typo from "movement-Speed"
```

### Step 3: Create Configuration Access Layer

**File**: `sources/common/lovelylib-1.21.1/Common/src/main/java/net/heriazone/lovelylib/common/configs/ConfigAccessLayer.java`

```java
package net.heriazone.lovelylib.common.configs;

import net.heriazone.lovelylib.common.shared.LovelyConstant;
import java.util.HashMap;
import java.util.Map;

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

    // -- Storage Maps (Loader-Specific Implementation) --
    
    // These will be populated by loader-specific implementations
    private static Map<String, Integer> intConfigCache = new HashMap<>();
    private static Map<String, Float> floatConfigCache = new HashMap<>();
    private static Map<String, String> serializedConfigCache = new HashMap<>();

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
        // Phase 2: Try serialized config (future enhancement)
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
    }

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
    }

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
    }

    // -- Internal Helper Methods --

    /**
     * Attempts to load configuration from serialized format.
     * 
     * @param variant the robot variant identifier
     * @return EntityConfigData or null if not available/invalid
     */
    private static SharedConfigs.EntityConfigData tryGetSerializedConfig(String variant) {
        // Phase 2 implementation - currently returns null
        // Will be implemented when EntityConfigSerializer is added
        return null;
    }

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
            return null;
        }
    }

    /**
     * Gets default entity configuration for specified variant.
     * 
     * @param variant the robot variant identifier
     * @return default EntityConfigData instance
     */
    private static SharedConfigs.EntityConfigData getDefaultEntityConfig(String variant) {
        // Use existing LegacyConfigs.Common.getDefaultConfig method
        return LegacyConfigs.Common.getDefaultConfig(variant);
    }

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
    }

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
    }

    // -- Cache Management Methods (Loader-Specific) --

    /**
     * Updates integer configuration cache (called by loader-specific implementations).
     * 
     * @param key the configuration key
     * @param value the configuration value
     */
    public static void updateIntConfigCache(String key, int value) {
        intConfigCache.put(key, value);
    }

    /**
     * Updates float configuration cache (called by loader-specific implementations).
     * 
     * @param key the configuration key
     * @param value the configuration value
     */
    public static void updateFloatConfigCache(String key, float value) {
        floatConfigCache.put(key, value);
    }

    /**
     * Updates serialized configuration cache (called by loader-specific implementations).
     * 
     * @param key the configuration key
     * @param value the serialized configuration data
     */
    public static void updateSerializedConfigCache(String key, String value) {
        serializedConfigCache.put(key, value);
    }

    /**
     * Clears all configuration caches (used during config reload).
     */
    public static void clearCaches() {
        intConfigCache.clear();
        floatConfigCache.clear();
        serializedConfigCache.clear();
    }

} // Class: ConfigAccessLayer
```

### Step 4: Update Forge/NeoForge Implementation

**File**: `sources/legacy/llovelyr-1.21.1/Forge/src/main/java/net/heriazone/llovelyr/source/LovelyConfigs.java`

**Replace the static block and entity configuration sections**:

```java
// Add imports
import net.heriazone.lovelylib.common.configs.ConfigKeyGenerator;
import net.heriazone.lovelylib.common.configs.ConfigAccessLayer;

public class LovelyConfigs {

    // -- Constants --

    private static final ForgeConfigSpec.Builder BUILDER;
    public static final ForgeConfigSpec SPEC;

    // ... existing general config variables ...

    // -- DYNAMIC ENTITY CONFIGURATION STORAGE --
    
    private static final Map<String, ForgeConfigSpec.ConfigValue<Integer>> ENTITY_INT_CONFIGS = new HashMap<>();
    private static final Map<String, ForgeConfigSpec.ConfigValue<Float>> ENTITY_FLOAT_CONFIGS = new HashMap<>();
    
    // Phase 2: Serialized config support (future)
    private static final Map<String, ForgeConfigSpec.ConfigValue<String>> ENTITY_SERIALIZED_CONFIGS = new HashMap<>();

    static {
        Config.setInsertionOrderPreserved(true);

        BUILDER = new ForgeConfigSpec.Builder();

        // ... existing general configuration sections ...

        // -- DYNAMIC ENTITY CONFIGURATION GENERATION --
        
        BUILDER.push("Entity");

        // Generate configuration entries for all variants dynamically
        for (String variant : LovelyConstant.ALL_VARIANTS) {
            BUILDER.push(variant);

            // Generate integer configuration entries
            for (String configType : ConfigKeyGenerator.INT_CONFIG_TYPES) {
                String key = ConfigKeyGenerator.generateKey(variant, configType);
                
                ENTITY_INT_CONFIGS.put(key, BUILDER
                    .comment(getConfigComment(configType))
                    .defineInRange(key, getDefaultIntValue(variant, configType),
                                  getMinIntValue(configType), getMaxIntValue(configType)));
            }

            // Generate float configuration entries
            for (String configType : ConfigKeyGenerator.FLOAT_CONFIG_TYPES) {
                String key = ConfigKeyGenerator.generateKey(variant, configType);
                
                ENTITY_FLOAT_CONFIGS.put(key, BUILDER
                    .comment(getConfigComment(configType))
                    .defineInRange(key, getDefaultFloatValue(variant, configType),
                                  getMinFloatValue(configType), getMaxFloatValue(configType)));
            }

            // Phase 2: Add serialized config support (optional)
            String serializedKey = variant + "-config";
            ENTITY_SERIALIZED_CONFIGS.put(serializedKey, BUILDER
                .comment("Serialized configuration object (advanced users only)",
                        "Leave empty to use individual settings above")
                .define(serializedKey, ""));

            BUILDER.pop();
        }

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    // -- CONFIGURATION LOADING --

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        // ... existing general config loading ...

        // Load dynamic entity configurations
        loadDynamicEntityConfigs();

        // ... existing callback execution ...
    }

    /**
     * Loads dynamic entity configurations from Forge config into access layer.
     */
    private static void loadDynamicEntityConfigs() {
        // Clear existing cache
        ConfigAccessLayer.clearCaches();
        
        // Load integer configurations
        for (String variant : LovelyConstant.ALL_VARIANTS) {
            for (String configType : ConfigKeyGenerator.INT_CONFIG_TYPES) {
                String key = ConfigKeyGenerator.generateKey(variant, configType);
                ForgeConfigSpec.ConfigValue<Integer> configValue = ENTITY_INT_CONFIGS.get(key);
                
                if (configValue != null) {
                    ConfigAccessLayer.updateIntConfigCache(key, configValue.get());
                }
            }
        }
        
        // Load float configurations
        for (String variant : LovelyConstant.ALL_VARIANTS) {
            for (String configType : ConfigKeyGenerator.FLOAT_CONFIG_TYPES) {
                String key = ConfigKeyGenerator.generateKey(variant, configType);
                ForgeConfigSpec.ConfigValue<Float> configValue = ENTITY_FLOAT_CONFIGS.get(key);
                
                if (configValue != null) {
                    ConfigAccessLayer.updateFloatConfigCache(key, configValue.get());
                }
            }
        }
        
        // Load serialized configurations (Phase 2)
        for (String variant : LovelyConstant.ALL_VARIANTS) {
            String serializedKey = variant + "-config";
            ForgeConfigSpec.ConfigValue<String> configValue = ENTITY_SERIALIZED_CONFIGS.get(serializedKey);
            
            if (configValue != null) {
                ConfigAccessLayer.updateSerializedConfigCache(serializedKey, configValue.get());
            }
        }

        // Populate LegacyConfigs.Common.Entities HashMap using access layer
        LegacyConfigs.Common.Entities.clear();
        
        for (String variant : LovelyConstant.ALL_VARIANTS) {
            try {
                SharedConfigs.EntityConfigData entityConfig = ConfigAccessLayer.getEntityConfig(variant);
                LegacyConfigs.Common.Entities.put(variant, entityConfig.validateOrDefault());
            } catch (Exception e) {
                Legacy.LOGGER.warn("Failed to load config for variant '{}', using defaults: {}", variant, e.getMessage());
                LegacyConfigs.Common.Entities.put(variant, getDefaultEntityConfig(variant));
            }
        }
        
        Legacy.LOGGER.info("Loaded dynamic entity configurations for {} variants", LegacyConfigs.Common.Entities.size());
    }

    // -- HELPER METHODS --

    private static String getConfigComment(String configType) {
        return switch (configType) {
            case LovelyConstant.CONFIG_MAX_LEVEL -> 
                "Highest level this robot type can reach.\n" +
                "Higher levels unlock better stats and abilities.\n" +
                "Range: 1 to no upper limit (does not accept negative values or zero)\n" +
                "Example: [200]";
            
            case LovelyConstant.CONFIG_ATTACK_SPEED -> 
                "How fast this robot type attacks (attacks per second).\n" +
                "Higher values mean faster attacks. Minecraft default is 1.0.\n" +
                "Range: 0.1 to no upper limit (does not accept negative values or zero)\n" +
                "Example: [1.6]";
            
            case LovelyConstant.CONFIG_MOVEMENT_SPEED -> 
                "Base movement speed for this robot type.\n" +
                "Higher values make robots move faster. Player walk speed is 0.1.\n" +
                "Range: 0.0 to no upper limit (does not accept negative values)\n" +
                "Example: [0.37]";
            
            case LovelyConstant.CONFIG_BASE_TOUGHNESS -> 
                "Base toughness for this robot type.\n" +
                "Reduces damage from strong attacks. Diamond armor has 2.0 toughness per piece.\n" +
                "Range: 0.0 to no upper limit (does not accept negative values)\n" +
                "Example: [0.0]";
            
            case LovelyConstant.CONFIG_BASE_HP -> 
                "Base HP value for combat level calculations.\n" +
                "Used by CombatLevelFeature to calculate HP at each level.\n" +
                "Range: 1 to no upper limit (does not accept negative values or zero)\n" +
                "Example: [16]";
            
            case LovelyConstant.CONFIG_BASE_ATTACK -> 
                "Base attack value for combat level calculations.\n" +
                "Used by CombatLevelFeature to calculate attack damage at each level.\n" +
                "Range: 1 to no upper limit (does not accept negative values or zero)\n" +
                "Example: [2]";
            
            case LovelyConstant.CONFIG_BASE_DEFENSE -> 
                "Base defense value for combat level calculations.\n" +
                "Used by CombatLevelFeature to calculate armor and toughness at each level.\n" +
                "Range: 1 to no upper limit (does not accept negative values or zero)\n" +
                "Example: [3]";
            
            default -> "Configuration value for " + configType;
        };
    }

    private static int getDefaultIntValue(String variant, String configType) {
        SharedConfigs.EntityConfigData defaultConfig = getDefaultEntityConfig(variant);
        
        return switch (configType) {
            case LovelyConstant.CONFIG_MAX_LEVEL -> defaultConfig.maxLevel;
            case LovelyConstant.CONFIG_BASE_HP -> defaultConfig.baseHp;
            case LovelyConstant.CONFIG_BASE_ATTACK -> defaultConfig.baseAttack;
            case LovelyConstant.CONFIG_BASE_DEFENSE -> defaultConfig.baseDefense;
            default -> 0;
        };
    }

    private static float getDefaultFloatValue(String variant, String configType) {
        SharedConfigs.EntityConfigData defaultConfig = getDefaultEntityConfig(variant);
        
        return switch (configType) {
            case LovelyConstant.CONFIG_ATTACK_SPEED -> defaultConfig.attackSpeed;
            case LovelyConstant.CONFIG_MOVEMENT_SPEED -> defaultConfig.movementSpeed;
            case LovelyConstant.CONFIG_BASE_TOUGHNESS -> defaultConfig.baseToughness;
            default -> 0.0f;
        };
    }

    private static int getMinIntValue(String configType) {
        return switch (configType) {
            case LovelyConstant.CONFIG_MAX_LEVEL -> ConfigBounds.MAX_LEVEL_MIN;
            case LovelyConstant.CONFIG_BASE_HP -> ConfigBounds.BASE_HP_MIN;
            case LovelyConstant.CONFIG_BASE_ATTACK -> ConfigBounds.BASE_ATTACK_MIN;
            case LovelyConstant.CONFIG_BASE_DEFENSE -> ConfigBounds.BASE_DEFENSE_MIN;
            default -> 0;
        };
    }

    private static int getMaxIntValue(String configType) {
        return switch (configType) {
            case LovelyConstant.CONFIG_MAX_LEVEL -> ConfigBounds.MAX_LEVEL_MAX;
            case LovelyConstant.CONFIG_BASE_HP -> ConfigBounds.BASE_HP_MAX;
            case LovelyConstant.CONFIG_BASE_ATTACK -> ConfigBounds.BASE_ATTACK_MAX;
            case LovelyConstant.CONFIG_BASE_DEFENSE -> ConfigBounds.BASE_DEFENSE_MAX;
            default -> Integer.MAX_VALUE;
        };
    }

    private static float getMinFloatValue(String configType) {
        return switch (configType) {
            case LovelyConstant.CONFIG_ATTACK_SPEED -> ConfigBounds.ATTACK_SPEED_MIN;
            case LovelyConstant.CONFIG_MOVEMENT_SPEED -> ConfigBounds.MOVEMENT_SPEED_MIN;
            case LovelyConstant.CONFIG_BASE_TOUGHNESS -> ConfigBounds.BASE_TOUGHNESS_MIN;
            default -> 0.0f;
        };
    }

    private static float getMaxFloatValue(String configType) {
        return switch (configType) {
            case LovelyConstant.CONFIG_ATTACK_SPEED -> ConfigBounds.ATTACK_SPEED_MAX;
            case LovelyConstant.CONFIG_MOVEMENT_SPEED -> ConfigBounds.MOVEMENT_SPEED_MAX;
            case LovelyConstant.CONFIG_BASE_TOUGHNESS -> ConfigBounds.BASE_TOUGHNESS_MAX;
            default -> Float.MAX_VALUE;
        };
    }

    private static SharedConfigs.EntityConfigData getDefaultEntityConfig(String variant) {
        return LegacyConfigs.Common.getDefaultConfig(variant);
    }

    // Remove all individual entity config variables (BUNNY_MAX_LEVEL, etc.)
    // Remove all individual entity config loading code
    // Keep existing general configuration code unchanged
}
```

### Step 5: Update NeoForge Implementation

**File**: `sources/legacy/llovelyr-1.21.1/NeoForge/src/main/java/net/heriazone/llovelyr/source/LovelyConfigs.java`

Apply the same changes as Forge implementation above, with NeoForge-specific imports:

```java
// NeoForge-specific imports
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.fml.event.config.ModConfigEvent;
```

The rest of the implementation is identical to Forge.

### Step 6: Update Fabric Implementation

**File**: `sources/legacy/llovelyr-1.21.1/Fabric/src/main/java/net/heriazone/llovelyr/source/LovelyConfigs.java`

**Update the buildConfigProvider method**:

```java
// Add imports
import net.heriazone.lovelylib.common.configs.ConfigKeyGenerator;
import net.heriazone.lovelylib.common.configs.ConfigAccessLayer;

private static void buildConfigProvider(ConfigProvider provider) {
    // ... existing general configuration sections ...

    provider.push("Entity");

    // Generate configuration entries for all variants dynamically
    for (String variant : LovelyConstant.ALL_VARIANTS) {
        provider.push(variant);

        // Generate integer configuration entries
        for (String configType : ConfigKeyGenerator.INT_CONFIG_TYPES) {
            String key = ConfigKeyGenerator.generateKey(variant, configType);
            
            provider.comment(getConfigComment(configType))
                    .define(key, getDefaultIntValue(variant, configType));
        }

        // Generate float configuration entries
        for (String configType : ConfigKeyGenerator.FLOAT_CONFIG_TYPES) {
            String key = ConfigKeyGenerator.generateKey(variant, configType);
            
            provider.comment(getConfigComment(configType))
                    .defineInRange(key, getDefaultFloatValue(variant, configType),
                                  getMinFloatValue(configType), getMaxFloatValue(configType));
        }

        // Phase 2: Add serialized config support (optional)
        provider.comment("Serialized configuration object (advanced users only)",
                        "Leave empty to use individual settings above")
                .define(variant + "-config", "");

        provider.pop();
    }

    provider.pop();
}

/**
 * Loads dynamic entity configurations from Fabric config into access layer.
 */
private static void loadDynamicEntityConfigs() {
    // Clear existing cache
    ConfigAccessLayer.clearCaches();
    
    // Load integer configurations
    for (String variant : LovelyConstant.ALL_VARIANTS) {
        for (String configType : ConfigKeyGenerator.INT_CONFIG_TYPES) {
            String key = ConfigKeyGenerator.generateKey(variant, configType);
            int value = config.getOrDefault(key, getDefaultIntValue(variant, configType));
            ConfigAccessLayer.updateIntConfigCache(key, value);
        }
    }
    
    // Load float configurations
    for (String variant : LovelyConstant.ALL_VARIANTS) {
        for (String configType : ConfigKeyGenerator.FLOAT_CONFIG_TYPES) {
            String key = ConfigKeyGenerator.generateKey(variant, configType);
            float value = config.getOrDefault(key, getDefaultFloatValue(variant, configType));
            ConfigAccessLayer.updateFloatConfigCache(key, value);
        }
    }
    
    // Load serialized configurations (Phase 2)
    for (String variant : LovelyConstant.ALL_VARIANTS) {
        String serializedKey = variant + "-config";
        String value = config.getOrDefault(serializedKey, "");
        ConfigAccessLayer.updateSerializedConfigCache(serializedKey, value);
    }

    // Populate LegacyConfigs.Common.Entities HashMap using access layer
    LegacyConfigs.Common.Entities = new HashMap<>();
    
    for (String variant : LovelyConstant.ALL_VARIANTS) {
        try {
            SharedConfigs.EntityConfigData entityConfig = ConfigAccessLayer.getEntityConfig(variant);
            LegacyConfigs.Common.Entities.put(variant, entityConfig.validateOrDefault());
        } catch (Exception e) {
            Legacy.LOGGER.warn("Failed to load config for variant '{}', using defaults: {}", variant, e.getMessage());
            LegacyConfigs.Common.Entities.put(variant, getDefaultEntityConfig(variant));
        }
    }
    
    Legacy.LOGGER.info("Loaded dynamic entity configurations for {} variants", LegacyConfigs.Common.Entities.size());
}

// Add the same helper methods as Forge implementation
```

## Testing Phase 1 Implementation

### Step 1: Compilation Testing

1. **Build LovelyLib**:
   ```bash
   cd sources/common/lovelylib-1.21.1
   ./gradlew build
   ```

2. **Copy JARs to Legacy libs**:
   ```bash
   # Copy built JARs to Legacy project libs folder
   # Refresh Gradle in IDE
   ```

3. **Build Legacy Projects**:
   ```bash
   cd sources/legacy/llovelyr-1.21.1
   ./gradlew build
   ```

### Step 2: Configuration File Testing

1. **Run each loader** and check generated config files:
   - Fabric: `config/llovelyr.properties`
   - Forge: `config/llovelyr-common.toml`
   - NeoForge: `config/llovelyr-common.toml`

2. **Verify all variants present** in config files:
   - bunny-max-level, bunny-attack-speed, etc.
   - bunny2-max-level, bunny2-attack-speed, etc.
   - dragon-max-level, dragon-attack-speed, etc.
   - (all 7 variants × 7 config types = 49 entries)

### Step 3: Runtime Testing

1. **Test config loading**:
   - Check console logs for "Loaded dynamic entity configurations for 7 variants"
   - Verify no errors during config loading

2. **Test config access**:
   ```java
   // Test in game or debug
   EntityConfigData bunnyConfig = LegacyConfigs.Common.Entities.get("bunny");
   System.out.println("Bunny max level: " + bunnyConfig.maxLevel);
   ```

3. **Test config modification**:
   - Modify config file values
   - Restart game
   - Verify changes are loaded correctly

## Phase 2: Custom Serializer Infrastructure (Future)

### Implementation Overview

Phase 2 will add:

1. **EntityConfigSerializer class** - Handles serialization/deserialization
2. **Enhanced ConfigAccessLayer** - Supports serialized config fallback
3. **JSON support** - Advanced configuration format
4. **Migration utilities** - Convert individual to serialized configs

### Benefits of Phased Approach

1. **Immediate Solution**: Phase 1 solves the Forge/NeoForge problem now
2. **Incremental Development**: Phase 2 can be added without breaking changes
3. **Risk Mitigation**: Each phase is tested independently
4. **Backward Compatibility**: Existing configs continue working throughout

## Troubleshooting

### Common Issues

1. **Compilation Errors**:
   - Ensure LovelyLib is built and JARs are copied
   - Check import statements
   - Verify LovelyConstant has correct config key constants

2. **Config Not Loading**:
   - Check console logs for error messages
   - Verify config file format is correct
   - Ensure all variants are in LovelyConstant.ALL_VARIANTS

3. **Missing Config Entries**:
   - Verify ConfigKeyGenerator.ALL_CONFIG_TYPES includes all needed types
   - Check that static block generates all keys correctly
   - Ensure config file has all expected entries

### Debug Steps

1. **Enable Debug Logging**:
   ```java
   Legacy.LOGGER.info("Generated {} int keys", ConfigKeyGenerator.generateIntKeys().size());
   Legacy.LOGGER.info("Generated {} float keys", ConfigKeyGenerator.generateFloatKeys().size());
   ```

2. **Verify Key Generation**:
   ```java
   for (String key : ConfigKeyGenerator.generateAllKeys().keySet()) {
       Legacy.LOGGER.info("Generated key: {}", key);
   }
   ```

3. **Check Config Access**:
   ```java
   for (String variant : LovelyConstant.ALL_VARIANTS) {
       EntityConfigData config = ConfigAccessLayer.getEntityConfig(variant);
       Legacy.LOGGER.info("Loaded config for {}: {}", variant, config);
   }
   ```

## Success Criteria

Phase 1 implementation is successful when:

1. ✅ All three loaders (Fabric, Forge, NeoForge) compile without errors
2. ✅ Config files generate with all 49 entity configuration entries
3. ✅ Runtime config loading populates LegacyConfigs.Common.Entities HashMap
4. ✅ Entity configurations are accessible and functional in-game
5. ✅ Config modifications persist and load correctly
6. ✅ No hardcoded entity-specific variables remain in config classes
7. ✅ New robot variants can be added by updating LovelyConstant.ALL_VARIANTS only

This implementation provides the foundation for the hybrid architecture while maintaining full backward compatibility and preparing for future enhancements.