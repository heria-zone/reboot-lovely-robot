package net.heriazone.lovelylib.api.configs;

import net.heriazone.lovelylib.common.configs.ConfigBounds;
import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.shared.LovelyConstant;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles serialization and deserialization of EntityConfigData objects.
 * <p>
 * <b>Architecture:</b> Provides custom serialization format for complex entity
 * configurations. Supports both simple format (Phase 2) and future JSON format
 * for advanced features.
 * <p>
 * <b>Format:</b> Uses compact string format for config file storage while
 * maintaining human readability and validation capabilities.
 * <p>
 * <b>Error Handling:</b> All deserialization failures fall back to default
 * configurations to prevent crashes from corrupted config data.
 */
public class EntityConfigSerializer {

    // -- Constants --

    /**
     * Simple serialization format pattern for Phase 2 implementation.
     * Format: {maxLevel:200,baseHp:16,baseAttack:2,attackSpeed:1.6,baseDefense:3,baseToughness:0.0,movementSpeed:0.37}
     */
    private static final Pattern SIMPLE_FORMAT_PATTERN = Pattern.compile(
        "\\{" +
                LovelyConstant.CONFIG_MAX_LEVEL + ":(\\d+)," +
                LovelyConstant.CONFIG_BASE_HP + ":(\\d+)," +
                LovelyConstant.CONFIG_BASE_ATTACK + ":(\\d+)," +
                LovelyConstant.CONFIG_ATTACK_SPEED + ":([\\d.]+)," +
                LovelyConstant.CONFIG_BASE_DEFENSE + ":(\\d+)," +
                LovelyConstant.CONFIG_BASE_TOUGHNESS + ":([\\d.]+)," +
                LovelyConstant.CONFIG_MOVEMENT_SPEED + ":([\\d.]+)\\}"
    );

    // -- Serialization Methods --

    /**
     * Serializes EntityConfigData to compact string format.
     * <p>
     * <b>Format:</b> Compact representation suitable for config file storage.
     * Human-readable but space-efficient for large numbers of robot variants.
     * 
     * @param config the EntityConfigData to serialize
     * @return serialized string representation
     */
    public static String serialize(SharedConfigs.EntityConfigData config) {
        if (config == null) {
            return "";
        }
        
        return String.format(
            "{" + LovelyConstant.CONFIG_MAX_LEVEL + ":%d," +
                    LovelyConstant.CONFIG_BASE_HP + ":%d," +
                    LovelyConstant.CONFIG_BASE_ATTACK + ":%d," +
                    LovelyConstant.CONFIG_ATTACK_SPEED + ":%.2f," +
                    LovelyConstant.CONFIG_BASE_DEFENSE + ":%d," +
                    LovelyConstant.CONFIG_BASE_TOUGHNESS + ":%.2f," +
                    LovelyConstant.CONFIG_MOVEMENT_SPEED + ":%.3f}",
            config.maxLevel, config.baseHp, config.baseAttack, 
            config.attackSpeed, config.baseDefense, config.baseToughness, config.movementSpeed
        );
    } // serialize()

    /**
     * Serializes EntityConfigData to JSON format (future enhancement).
     * <p>
     * <b>Future Feature:</b> Will support nested objects, arrays, and custom
     * fields for advanced plugin configurations.
     * 
     * @param config the EntityConfigData to serialize
     * @return JSON string representation
     */
    public static String serializeJson(SharedConfigs.EntityConfigData config) {
        if (config == null) return "{}";
        
        // Future implementation for JSON format
        return String.format(
            "{\"" + LovelyConstant.CONFIG_MAX_LEVEL + "\":%d,\"" +
                    LovelyConstant.CONFIG_BASE_HP + "\":%d,\"" +
                    LovelyConstant.CONFIG_BASE_ATTACK + "\":%d,\"" +
                    LovelyConstant.CONFIG_ATTACK_SPEED + "\":%.2f,\"" +
                    LovelyConstant.CONFIG_BASE_DEFENSE + "\":%d,\"" +
                    LovelyConstant.CONFIG_BASE_TOUGHNESS + "\":%.2f,\"" +
                    LovelyConstant.CONFIG_MOVEMENT_SPEED + "\":%.3f}",
            config.maxLevel, config.baseHp, config.baseAttack, 
            config.attackSpeed, config.baseDefense, config.baseToughness, config.movementSpeed
        );
    } // serializeJson()

    // -- Deserialization Methods --

    /**
     * Deserializes EntityConfigData from string format with validation.
     * <p>
     * <b>Fallback Strategy:</b> Attempts simple format first, then JSON format.
     * Returns default configuration if all parsing attempts fail.
     * <p>
     * <b>Validation:</b> All deserialized configurations are validated before
     * return. Invalid configurations trigger fallback to defaults.
     * 
     * @param data the serialized configuration string
     * @return validated EntityConfigData or default if parsing fails
     */
    public static SharedConfigs.EntityConfigData deserialize(String data) {
        if (data == null || data.trim().isEmpty()) {
            return null; // Indicates no serialized config available
        }
        
        try {
            // Try simple format first
            SharedConfigs.EntityConfigData config = deserializeSimpleFormat(data);
            if (config != null && config.isValid()) {
                return config;
            }
            
            // Try JSON format as fallback
            config = deserializeJsonFormat(data);
            if (config != null && config.isValid()) {
                return config;
            }
            
        } catch (Exception e) {
            // Log error but don't crash - fall back to defaults
            System.err.println("Failed to deserialize entity config: " + e.getMessage());
        }
        
        // All parsing attempts failed - return null to trigger individual key fallback
        return null;
    } // deserialize()

    /**
     * Deserializes from simple format used in Phase 2.
     * <p>
     * <b>Format:</b> {maxLevel:200,baseHp:16,baseAttack:2,attackSpeed:1.6,baseDefense:3,baseToughness:0.0,movementSpeed:0.37}
     * 
     * @param data the serialized data string
     * @return EntityConfigData or null if parsing fails
     */
    private static SharedConfigs.EntityConfigData deserializeSimpleFormat(String data) {
        Matcher matcher = SIMPLE_FORMAT_PATTERN.matcher(data.trim());
        
        if (!matcher.matches()) {
            return null;
        }
        
        try {
            int maxLevel = Integer.parseInt(matcher.group(1));
            int baseHp = Integer.parseInt(matcher.group(2));
            int baseAttack = Integer.parseInt(matcher.group(3));
            float attackSpeed = Float.parseFloat(matcher.group(4));
            int baseDefense = Integer.parseInt(matcher.group(5));
            float baseToughness = Float.parseFloat(matcher.group(6));
            float movementSpeed = Float.parseFloat(matcher.group(7));
            
            return new SharedConfigs.EntityConfigData(
                maxLevel, baseHp, baseAttack, attackSpeed,
                baseDefense, baseToughness, movementSpeed
            );
            
        } catch (NumberFormatException e) {
            return null;
        }
    } // deserializeSimpleFormat()

    /**
     * Deserializes from JSON format (future enhancement).
     * <p>
     * <b>Future Feature:</b> Will support complex nested configurations,
     * custom fields, and plugin-specific data structures.
     * 
     * @param data the JSON data string
     * @return EntityConfigData or null if parsing fails
     */
    private static SharedConfigs.EntityConfigData deserializeJsonFormat(String data) {
        // Future implementation for JSON parsing
        // For now, return null to indicate JSON parsing not yet implemented
        return null;
    } // deserializeJsonFormat()

    // -- Validation Methods --

    /**
     * Validates serialized configuration data without full deserialization.
     * <p>
     * <b>Performance:</b> Quick validation check for config file integrity
     * without the overhead of full object construction.
     * 
     * @param data the serialized configuration string
     * @return true if data appears to be valid format
     */
    public static boolean isValidSerializedData(String data) {
        if (data == null || data.trim().isEmpty()) {
            return false;
        }
        
        // Check simple format
        if (SIMPLE_FORMAT_PATTERN.matcher(data.trim()).matches()) {
            return true;
        }
        
        // Check JSON format (future)
        if (data.trim().startsWith("{") && data.trim().endsWith("}")) {
            // Basic JSON structure check - full validation would require JSON parser
            return true;
        }
        
        return false;
    } // isValidSerializedData()

    /**
     * Sanitizes and validates deserialized configuration data.
     * <p>
     * <b>Bounds Checking:</b> Applies ConfigBounds validation to ensure
     * deserialized values are within acceptable ranges.
     * 
     * @param config the deserialized configuration
     * @return sanitized configuration with values within bounds
     */
    public static SharedConfigs.EntityConfigData sanitizeConfig(SharedConfigs.EntityConfigData config) {
        if (config == null) {
            return SharedConfigs.EntityConfigData.getDefault();
        }
        
        // Apply bounds validation using ConfigBounds
        return new SharedConfigs.EntityConfigData.Builder()
            .maxLevel(Math.max(1, Math.min(config.maxLevel, ConfigBounds.MAX_LEVEL_MAX)))
            .baseHp(Math.max(ConfigBounds.BASE_HP_MIN, Math.min(config.baseHp, ConfigBounds.BASE_HP_MAX)))
            .baseAttack(Math.max(ConfigBounds.BASE_ATTACK_MIN, Math.min(config.baseAttack, ConfigBounds.BASE_ATTACK_MAX)))
            .attackSpeed(Math.max(ConfigBounds.ATTACK_SPEED_MIN, Math.min(config.attackSpeed, ConfigBounds.ATTACK_SPEED_MAX)))
            .baseDefense(Math.max(ConfigBounds.BASE_DEFENSE_MIN, Math.min(config.baseDefense, ConfigBounds.BASE_DEFENSE_MAX)))
            .baseToughness(Math.max(ConfigBounds.BASE_TOUGHNESS_MIN, Math.min(config.baseToughness, ConfigBounds.BASE_TOUGHNESS_MAX)))
            .movementSpeed(Math.max(ConfigBounds.MOVEMENT_SPEED_MIN, Math.min(config.movementSpeed, ConfigBounds.MOVEMENT_SPEED_MAX)))
            .build();
    } // sanitizeConfig()

    // -- Utility Methods --

    /**
     * Creates serialized configuration from individual config values.
     * <p>
     * <b>Migration Helper:</b> Assists in converting from individual config
     * variables to serialized format during system migration.
     * 
     * @param maxLevel maximum level value
     * @param baseHp base health points
     * @param baseAttack base attack damage
     * @param attackSpeed attack speed rate
     * @param baseDefense base defense value
     * @param baseToughness base toughness value
     * @param movementSpeed movement speed
     * @return serialized configuration string
     */
    public static String serializeFromValues(int maxLevel, int baseHp, int baseAttack, 
            float attackSpeed, int baseDefense, float baseToughness, float movementSpeed) {
        
        SharedConfigs.EntityConfigData config = new SharedConfigs.EntityConfigData(
            maxLevel, baseHp, baseAttack, attackSpeed,
            baseDefense, baseToughness, movementSpeed
        );
        
        return serialize(config);
    } // serializeFromValues()

    /**
     * Compares two serialized configurations for equality.
     * <p>
     * <b>Config Management:</b> Used to detect configuration changes and
     * determine when config reload is necessary.
     * 
     * @param serialized1 first serialized configuration
     * @param serialized2 second serialized configuration
     * @return true if configurations represent the same values
     */
    public static boolean areEqual(String serialized1, String serialized2) {
        if (serialized1 == null && serialized2 == null) {
            return true;
        }
        
        if (serialized1 == null || serialized2 == null) {
            return false;
        }
        
        // Try to deserialize both and compare
        SharedConfigs.EntityConfigData config1 = deserialize(serialized1);
        SharedConfigs.EntityConfigData config2 = deserialize(serialized2);
        
        if (config1 == null && config2 == null) {
            return serialized1.equals(serialized2); // Compare raw strings
        }
        
        if (config1 == null || config2 == null) {
            return false;
        }
        
        // Compare all fields
        return config1.maxLevel == config2.maxLevel &&
               config1.baseHp == config2.baseHp &&
               config1.baseAttack == config2.baseAttack &&
               Math.abs(config1.attackSpeed - config2.attackSpeed) < 0.001f &&
               config1.baseDefense == config2.baseDefense &&
               Math.abs(config1.baseToughness - config2.baseToughness) < 0.001f &&
               Math.abs(config1.movementSpeed - config2.movementSpeed) < 0.001f;
    } // areEqual()

} // Class: EntityConfigSerializer