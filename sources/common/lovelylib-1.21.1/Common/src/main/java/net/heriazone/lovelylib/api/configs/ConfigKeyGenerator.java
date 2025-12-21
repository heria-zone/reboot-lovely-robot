package net.heriazone.lovelylib.api.configs;

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
    } // generateKey()

    /**
     * Generates all possible configuration keys for all variants.
     * <p>
     * <b>Usage:</b> Used during static initialization to pre-generate all
     * required configuration keys for Forge/NeoForge ConfigSpec building.
     * 
     * @return map of configuration keys to their formatted strings
     */
    public static Map<String, String> generateAllKeys(String[] variants) {
        Map<String, String> keys = new HashMap<>();

        for (String variant : variants) {
            for (String configType : ALL_CONFIG_TYPES) {
                String key = generateKey(variant, configType);
                keys.put(key, key);
            }
        }

        return keys;
    } // generateAllKeys()

    public static Map<String, String> generateAllKeys() {
        return generateAllKeys(LovelyConstant.ALL_VARIANTS);
    } // generateAllKeys()

    /**
     * Generates all integer configuration keys.
     * 
     * @return set of integer configuration keys
     */
    public static Set<String> generateIntKeys(String[] variants) {
        Set<String> keys = new HashSet<>();

        for (String variant : variants) {
            for (String configType : INT_CONFIG_TYPES) {
                keys.add(generateKey(variant, configType));
            }
        }

        return keys;
    } // generateIntKeys()

    public static Set<String> generateIntKeys() {
        return generateIntKeys(LovelyConstant.ALL_VARIANTS);
    } // generateIntKeys()

    /**
     * Generates all float configuration keys.
     * 
     * @return set of float configuration keys
     */
    public static Set<String> generateFloatKeys(String[] variants) {
        Set<String> keys = new HashSet<>();

        for (String variant : variants) {
            for (String configType : FLOAT_CONFIG_TYPES) {
                keys.add(generateKey(variant, configType));
            }
        }

        return keys;
    } // generateFloatKeys()

    public static Set<String> generateFloatKeys() {
        return generateFloatKeys(LovelyConstant.ALL_VARIANTS);
    } // generateFloatKeys()

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
    } // isIntConfigType()

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
    } // isFloatConfigType()

    /**
     * Validates that a configuration key follows the expected format.
     * 
     * @param key the configuration key to validate
     * @return true if key format is valid, false otherwise
     */
    public static boolean isValidKey(String key) {
        if (key == null || key.isEmpty()) return false;
        
        String[] parts = key.split("-", 2);
        if (parts.length != 2) return false;
        
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
        
        if (!validVariant) return false;
        
        // Check if config type exists
        for (String validType : ALL_CONFIG_TYPES) {
            if (validType.equals(configType)) return true;
        }
        
        return false;
    } // isValidKey()

    public static boolean isValidKey(String[] variants, String key) {
        if (key == null || key.isEmpty()) return false;

        String[] parts = key.split("-", 2);
        if (parts.length != 2) return false;

        String variant = parts[0];
        String configType = parts[1];

        // Check if variant exists
        boolean validVariant = false;
        for (String validVar : variants) {
            if (validVar.equals(variant)) {
                validVariant = true;
                break;
            }
        }

        if (!validVariant) return false;

        // Check if config type exists
        for (String validType : ALL_CONFIG_TYPES) {
            if (validType.equals(configType)) return true;
        }

        return false;
    } // isValidKey()

    /**
     * Extracts variant name from configuration key.
     * 
     * @param key the configuration key
     * @return variant name or null if invalid key
     */
    public static String extractVariant(String key) {
        if (!isValidKey(key)) return null;
        return key.split("-", 2)[0];
    } // extractVariant()

    /**
     * Extracts configuration type from configuration key.
     * 
     * @param key the configuration key
     * @return configuration type or null if invalid key
     */
    public static String extractConfigType(String key) {
        if (!isValidKey(key)) return null;
        return key.split("-", 2)[1];
    } // extractConfigType()

} // Class: ConfigKeyGenerator