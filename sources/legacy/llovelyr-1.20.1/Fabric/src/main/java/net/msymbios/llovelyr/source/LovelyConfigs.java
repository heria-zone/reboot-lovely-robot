package net.msymbios.llovelyr.source;

import net.msymbios.llovelyr.LovelyLegacy;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Configuration management for Legacy LovelyRobot mod.
 * <p>
 * <b>Architecture:</b> Provides static config values loaded from properties file,
 * with automatic file creation and validation. Supports runtime reload for server
 * administration without restart.
 * <p>
 * <b>Config Location:</b> config/llovelyr.properties in game directory. Created
 * with defaults if missing.
 * <p>
 * <b>Error Handling:</b> Invalid values fall back to defaults with warning logs.
 * Parse errors use defaults and log errors. Missing file creates new with defaults.
 */
public class LovelyConfigs {

    // -- Constants --

    private static final String CONFIG_FILE_NAME = "llovelyr.properties";
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE_NAME);

    // -- Methods --

    /**
     * Loads configuration from file or creates default config if missing.
     * <p>
     * <b>Initialization:</b> Called during mod initialization to populate static
     * config fields. Creates config file with defaults if not present.
     * <p>
     * <b>Error Recovery:</b> Missing file triggers creation with defaults. Invalid
     * values use defaults with warnings. Parse errors use defaults with error logs.
     */
    public static void register() {
        loadConfig();
    } // register()

    /**
     * Loads config values from properties file with validation and error handling.
     * <p>
     * <b>File Handling:</b> Creates config with defaults if missing. Reads existing
     * config and validates each value. Falls back to defaults for invalid entries.
     * <p>
     * <b>Validation:</b> Checks numeric ranges, ensures positive values where required.
     * Logs warnings for invalid values, errors for parse failures.
     */
    private static void loadConfig() {
        Properties properties = new Properties();
        
        // Create config file with defaults if it doesn't exist
        if (!Files.exists(CONFIG_PATH)) {
            LovelyLegacy.LOGGER.info("Config file not found, creating with defaults: {}", CONFIG_PATH);
            saveDefaultConfig();
            return; // Defaults already set in static fields
        }
        
        // Load existing config file
        try (InputStream input = Files.newInputStream(CONFIG_PATH)) {
            properties.load(input);
            LovelyLegacy.LOGGER.info("Loading config from: {}", CONFIG_PATH);
            
            // Load and validate each config value
            loadCommonConfig(properties);
            loadClientConfig(properties);
            
        } catch (IOException e) {
            LovelyLegacy.LOGGER.error("Failed to load config file, using defaults", e);
        }
    } // loadConfig()

    /**
     * Loads common (shared) config values with validation.
     * <p>
     * <b>Validation Strategy:</b> Attempts to parse each value, falls back to default
     * on failure. Logs warnings for invalid values to aid troubleshooting.
     */
    private static void loadCommonConfig(Properties properties) {
        // General settings
        Common.OwnerMaxRobotNum = getIntProperty(properties, "OwnerMaxRobotNum", Common.OwnerMaxRobotNum);
        Common.MovementMeleeAttack = getDoubleProperty(properties, "MovementMeleeAttack", Common.MovementMeleeAttack);
        Common.MovementFollowOwner = getFloatProperty(properties, "MovementFollowOwner", Common.MovementFollowOwner);
        Common.MovementWanderAround = getDoubleProperty(properties, "MovementWanderAround", Common.MovementWanderAround);
        Common.FollowDistanceMax = getFloatProperty(properties, "FollowDistanceMax", Common.FollowDistanceMax);
        Common.FollowDistanceMin = getFloatProperty(properties, "FollowDistanceMin", Common.FollowDistanceMin);
        Common.LookRange = getFloatProperty(properties, "LookRange", Common.LookRange);
        
        // Renderer settings
        Common.Width = getFloatProperty(properties, "Width", Common.Width);
        Common.Height = getFloatProperty(properties, "Height", Common.Height);
        
        // Level/Experience settings
        Common.ExperienceBase = getIntProperty(properties, "ExperienceBase", Common.ExperienceBase);
        Common.ExperienceMultiplier = getIntProperty(properties, "ExperienceMultiplier", Common.ExperienceMultiplier);
        
        // Combat settings
        Common.FriendlyFire = getBooleanProperty(properties, "FriendlyFire", Common.FriendlyFire);
        Common.AttackChance = getIntProperty(properties, "AttackChance", Common.AttackChance);
        Common.HealInterval = getIntProperty(properties, "HealInterval", Common.HealInterval);
        Common.WaryTime = getIntProperty(properties, "WaryTime", Common.WaryTime);
        Common.GlobalAutoHeal = getBooleanProperty(properties, "GlobalAutoHeal", Common.GlobalAutoHeal);
        Common.LootEnchantment = getBooleanProperty(properties, "LootEnchantment", Common.LootEnchantment);
        Common.LootEnchantmentLevel = getIntProperty(properties, "LootEnchantmentLevel", Common.LootEnchantmentLevel);
        Common.MaxLootEnchantment = getIntProperty(properties, "MaxLootEnchantment", Common.MaxLootEnchantment);
        Common.BaseDefenceRange = getFloatProperty(properties, "BaseDefenceRange", Common.BaseDefenceRange);
        Common.BaseDefenceWarpRange = getFloatProperty(properties, "BaseDefenceWarpRange", Common.BaseDefenceWarpRange);
        
        // Protection settings
        Common.ProtectionLimitFire = getIntProperty(properties, "ProtectionLimitFire", Common.ProtectionLimitFire);
        Common.ProtectionLimitFall = getIntProperty(properties, "ProtectionLimitFall", Common.ProtectionLimitFall);
        Common.ProtectionLimitBlast = getIntProperty(properties, "ProtectionLimitBlast", Common.ProtectionLimitBlast);
        Common.ProtectionLimitProjectile = getIntProperty(properties, "ProtectionLimitProjectile", Common.ProtectionLimitProjectile);
        
        // Smart Core Retrieval
        Common.EnableSmartCoreRetrieval = getBooleanProperty(properties, "EnableSmartCoreRetrieval", Common.EnableSmartCoreRetrieval);
        Common.SmartCoreRetrievalDistance = getDoubleProperty(properties, "SmartCoreRetrievalDistance", Common.SmartCoreRetrievalDistance);
        
        // AI Behavior settings
        Common.OwnerStillThreshold = getIntProperty(properties, "OwnerStillThreshold", Common.OwnerStillThreshold);
        Common.WanderCheckInterval = getIntProperty(properties, "WanderCheckInterval", Common.WanderCheckInterval);
        Common.WanderChance = getDoubleProperty(properties, "WanderChance", Common.WanderChance);
        Common.WanderRadiusMin = getDoubleProperty(properties, "WanderRadiusMin", Common.WanderRadiusMin);
        Common.WanderRadiusMax = getDoubleProperty(properties, "WanderRadiusMax", Common.WanderRadiusMax);
        Common.WanderDurationMin = getIntProperty(properties, "WanderDurationMin", Common.WanderDurationMin);
        Common.WanderDurationMax = getIntProperty(properties, "WanderDurationMax", Common.WanderDurationMax);
        Common.WanderCooldownMin = getIntProperty(properties, "WanderCooldownMin", Common.WanderCooldownMin);
        Common.WanderCooldownMax = getIntProperty(properties, "WanderCooldownMax", Common.WanderCooldownMax);
        Common.PatrolDurationMin = getIntProperty(properties, "PatrolDurationMin", Common.PatrolDurationMin);
        Common.PatrolDurationMax = getIntProperty(properties, "PatrolDurationMax", Common.PatrolDurationMax);
        Common.GuardDurationMin = getIntProperty(properties, "GuardDurationMin", Common.GuardDurationMin);
        Common.GuardDurationMax = getIntProperty(properties, "GuardDurationMax", Common.GuardDurationMax);
        Common.PatrolPauseDurationMin = getIntProperty(properties, "PatrolPauseDurationMin", Common.PatrolPauseDurationMin);
        Common.PatrolPauseDurationMax = getIntProperty(properties, "PatrolPauseDurationMax", Common.PatrolPauseDurationMax);
        Common.GuardRotationSpeed = getDoubleProperty(properties, "GuardRotationSpeed", Common.GuardRotationSpeed);
        Common.EnableCombatRadiusParticles = getBooleanProperty(properties, "EnableCombatRadiusParticles", Common.EnableCombatRadiusParticles);
        Common.CombatRadiusParticleCount = getIntProperty(properties, "CombatRadiusParticleCount", Common.CombatRadiusParticleCount);
        Common.CombatRadiusParticleSpread = getDoubleProperty(properties, "CombatRadiusParticleSpread", Common.CombatRadiusParticleSpread);
        
        // Animation settings
        Common.StandbyToSitDelayMin = getIntProperty(properties, "StandbyToSitDelayMin", Common.StandbyToSitDelayMin);
        Common.StandbyToSitDelayMax = getIntProperty(properties, "StandbyToSitDelayMax", Common.StandbyToSitDelayMax);
        
        // Entity-specific settings - BUNNY2
        Common.Bunny2MaxLevel = getIntProperty(properties, "Bunny2MaxLevel", Common.Bunny2MaxLevel);
        Common.Bunny2MaxHealth = getFloatProperty(properties, "Bunny2MaxHealth", Common.Bunny2MaxHealth);
        Common.Bunny2AttackDamage = getFloatProperty(properties, "Bunny2AttackDamage", Common.Bunny2AttackDamage);
        Common.Bunny2AttackSpeed = getFloatProperty(properties, "Bunny2AttackSpeed", Common.Bunny2AttackSpeed);
        Common.Bunny2MovementSpeed = getFloatProperty(properties, "Bunny2MovementSpeed", Common.Bunny2MovementSpeed);
        Common.Bunny2Armor = getFloatProperty(properties, "Bunny2Armor", Common.Bunny2Armor);
        Common.Bunny2ArmorToughness = getFloatProperty(properties, "Bunny2ArmorToughness", Common.Bunny2ArmorToughness);
        
        // Entity-specific settings - VANILLA
        Common.VanillaMaxLevel = getIntProperty(properties, "VanillaMaxLevel", Common.VanillaMaxLevel);
        Common.VanillaMaxHealth = getFloatProperty(properties, "VanillaMaxHealth", Common.VanillaMaxHealth);
        Common.VanillaAttackDamage = getFloatProperty(properties, "VanillaAttackDamage", Common.VanillaAttackDamage);
        Common.VanillaAttackSpeed = getFloatProperty(properties, "VanillaAttackSpeed", Common.VanillaAttackSpeed);
        Common.VanillaMovementSpeed = getFloatProperty(properties, "VanillaMovementSpeed", Common.VanillaMovementSpeed);
        Common.VanillaArmor = getFloatProperty(properties, "VanillaArmor", Common.VanillaArmor);
        Common.VanillaArmorToughness = getFloatProperty(properties, "VanillaArmorToughness", Common.VanillaArmorToughness);
        
        // Entity-specific settings - DRAGON
        Common.DragonMaxLevel = getIntProperty(properties, "DragonMaxLevel", Common.DragonMaxLevel);
        Common.DragonMaxHealth = getFloatProperty(properties, "DragonMaxHealth", Common.DragonMaxHealth);
        Common.DragonAttackDamage = getFloatProperty(properties, "DragonAttackDamage", Common.DragonAttackDamage);
        Common.DragonAttackSpeed = getFloatProperty(properties, "DragonAttackSpeed", Common.DragonAttackSpeed);
        Common.DragonMovementSpeed = getFloatProperty(properties, "DragonMovementSpeed", Common.DragonMovementSpeed);
        Common.DragonArmor = getFloatProperty(properties, "DragonArmor", Common.DragonArmor);
        Common.DragonArmorToughness = getFloatProperty(properties, "DragonArmorToughness", Common.DragonArmorToughness);
        
        // Entity-specific settings - KITSUNE
        Common.KitsuneMaxLevel = getIntProperty(properties, "KitsuneMaxLevel", Common.KitsuneMaxLevel);
        Common.KitsuneMaxHealth = getFloatProperty(properties, "KitsuneMaxHealth", Common.KitsuneMaxHealth);
        Common.KitsuneAttackDamage = getFloatProperty(properties, "KitsuneAttackDamage", Common.KitsuneAttackDamage);
        Common.KitsuneAttackSpeed = getFloatProperty(properties, "KitsuneAttackSpeed", Common.KitsuneAttackSpeed);
        Common.KitsuneMovementSpeed = getFloatProperty(properties, "KitsuneMovementSpeed", Common.KitsuneMovementSpeed);
        Common.KitsuneArmor = getFloatProperty(properties, "KitsuneArmor", Common.KitsuneArmor);
        Common.KitsuneArmorToughness = getFloatProperty(properties, "KitsuneArmorToughness", Common.KitsuneArmorToughness);
    } // loadCommonConfig()

    /**
     * Loads client-only config values with validation.
     */
    private static void loadClientConfig(Properties properties) {
        Client.ShadowRadius = getFloatProperty(properties, "ShadowRadius", Client.ShadowRadius);
    } // loadClientConfig()

    /**
     * Creates default config file with all current values.
     * <p>
     * <b>File Creation:</b> Writes properties file with comments explaining each
     * section. Uses current static field values as defaults.
     */
    private static void saveDefaultConfig() {
        Properties properties = new Properties();
        
        // General settings
        properties.setProperty("OwnerMaxRobotNum", String.valueOf(Common.OwnerMaxRobotNum));
        properties.setProperty("MovementMeleeAttack", String.valueOf(Common.MovementMeleeAttack));
        properties.setProperty("MovementFollowOwner", String.valueOf(Common.MovementFollowOwner));
        properties.setProperty("MovementWanderAround", String.valueOf(Common.MovementWanderAround));
        properties.setProperty("FollowDistanceMax", String.valueOf(Common.FollowDistanceMax));
        properties.setProperty("FollowDistanceMin", String.valueOf(Common.FollowDistanceMin));
        properties.setProperty("LookRange", String.valueOf(Common.LookRange));
        
        // Renderer settings
        properties.setProperty("Width", String.valueOf(Common.Width));
        properties.setProperty("Height", String.valueOf(Common.Height));
        properties.setProperty("ShadowRadius", String.valueOf(Client.ShadowRadius));
        
        // Level/Experience
        properties.setProperty("ExperienceBase", String.valueOf(Common.ExperienceBase));
        properties.setProperty("ExperienceMultiplier", String.valueOf(Common.ExperienceMultiplier));
        
        // Combat
        properties.setProperty("FriendlyFire", String.valueOf(Common.FriendlyFire));
        properties.setProperty("AttackChance", String.valueOf(Common.AttackChance));
        properties.setProperty("HealInterval", String.valueOf(Common.HealInterval));
        properties.setProperty("WaryTime", String.valueOf(Common.WaryTime));
        properties.setProperty("GlobalAutoHeal", String.valueOf(Common.GlobalAutoHeal));
        properties.setProperty("LootEnchantment", String.valueOf(Common.LootEnchantment));
        properties.setProperty("LootEnchantmentLevel", String.valueOf(Common.LootEnchantmentLevel));
        properties.setProperty("MaxLootEnchantment", String.valueOf(Common.MaxLootEnchantment));
        properties.setProperty("BaseDefenceRange", String.valueOf(Common.BaseDefenceRange));
        properties.setProperty("BaseDefenceWarpRange", String.valueOf(Common.BaseDefenceWarpRange));
        
        // Protection
        properties.setProperty("ProtectionLimitFire", String.valueOf(Common.ProtectionLimitFire));
        properties.setProperty("ProtectionLimitFall", String.valueOf(Common.ProtectionLimitFall));
        properties.setProperty("ProtectionLimitBlast", String.valueOf(Common.ProtectionLimitBlast));
        properties.setProperty("ProtectionLimitProjectile", String.valueOf(Common.ProtectionLimitProjectile));
        
        // Smart Core Retrieval
        properties.setProperty("EnableSmartCoreRetrieval", String.valueOf(Common.EnableSmartCoreRetrieval));
        properties.setProperty("SmartCoreRetrievalDistance", String.valueOf(Common.SmartCoreRetrievalDistance));
        
        // AI Behavior
        properties.setProperty("OwnerStillThreshold", String.valueOf(Common.OwnerStillThreshold));
        properties.setProperty("WanderCheckInterval", String.valueOf(Common.WanderCheckInterval));
        properties.setProperty("WanderChance", String.valueOf(Common.WanderChance));
        properties.setProperty("WanderRadiusMin", String.valueOf(Common.WanderRadiusMin));
        properties.setProperty("WanderRadiusMax", String.valueOf(Common.WanderRadiusMax));
        properties.setProperty("WanderDurationMin", String.valueOf(Common.WanderDurationMin));
        properties.setProperty("WanderDurationMax", String.valueOf(Common.WanderDurationMax));
        properties.setProperty("WanderCooldownMin", String.valueOf(Common.WanderCooldownMin));
        properties.setProperty("WanderCooldownMax", String.valueOf(Common.WanderCooldownMax));
        properties.setProperty("PatrolDurationMin", String.valueOf(Common.PatrolDurationMin));
        properties.setProperty("PatrolDurationMax", String.valueOf(Common.PatrolDurationMax));
        properties.setProperty("GuardDurationMin", String.valueOf(Common.GuardDurationMin));
        properties.setProperty("GuardDurationMax", String.valueOf(Common.GuardDurationMax));
        properties.setProperty("PatrolPauseDurationMin", String.valueOf(Common.PatrolPauseDurationMin));
        properties.setProperty("PatrolPauseDurationMax", String.valueOf(Common.PatrolPauseDurationMax));
        properties.setProperty("GuardRotationSpeed", String.valueOf(Common.GuardRotationSpeed));
        properties.setProperty("EnableCombatRadiusParticles", String.valueOf(Common.EnableCombatRadiusParticles));
        properties.setProperty("CombatRadiusParticleCount", String.valueOf(Common.CombatRadiusParticleCount));
        properties.setProperty("CombatRadiusParticleSpread", String.valueOf(Common.CombatRadiusParticleSpread));
        
        // Animation
        properties.setProperty("StandbyToSitDelayMin", String.valueOf(Common.StandbyToSitDelayMin));
        properties.setProperty("StandbyToSitDelayMax", String.valueOf(Common.StandbyToSitDelayMax));
        
        // Entity-specific - BUNNY2
        properties.setProperty("Bunny2MaxLevel", String.valueOf(Common.Bunny2MaxLevel));
        properties.setProperty("Bunny2MaxHealth", String.valueOf(Common.Bunny2MaxHealth));
        properties.setProperty("Bunny2AttackDamage", String.valueOf(Common.Bunny2AttackDamage));
        properties.setProperty("Bunny2AttackSpeed", String.valueOf(Common.Bunny2AttackSpeed));
        properties.setProperty("Bunny2MovementSpeed", String.valueOf(Common.Bunny2MovementSpeed));
        properties.setProperty("Bunny2Armor", String.valueOf(Common.Bunny2Armor));
        properties.setProperty("Bunny2ArmorToughness", String.valueOf(Common.Bunny2ArmorToughness));
        
        // Entity-specific - VANILLA
        properties.setProperty("VanillaMaxLevel", String.valueOf(Common.VanillaMaxLevel));
        properties.setProperty("VanillaMaxHealth", String.valueOf(Common.VanillaMaxHealth));
        properties.setProperty("VanillaAttackDamage", String.valueOf(Common.VanillaAttackDamage));
        properties.setProperty("VanillaAttackSpeed", String.valueOf(Common.VanillaAttackSpeed));
        properties.setProperty("VanillaMovementSpeed", String.valueOf(Common.VanillaMovementSpeed));
        properties.setProperty("VanillaArmor", String.valueOf(Common.VanillaArmor));
        properties.setProperty("VanillaArmorToughness", String.valueOf(Common.VanillaArmorToughness));
        
        // Entity-specific - DRAGON
        properties.setProperty("DragonMaxLevel", String.valueOf(Common.DragonMaxLevel));
        properties.setProperty("DragonMaxHealth", String.valueOf(Common.DragonMaxHealth));
        properties.setProperty("DragonAttackDamage", String.valueOf(Common.DragonAttackDamage));
        properties.setProperty("DragonAttackSpeed", String.valueOf(Common.DragonAttackSpeed));
        properties.setProperty("DragonMovementSpeed", String.valueOf(Common.DragonMovementSpeed));
        properties.setProperty("DragonArmor", String.valueOf(Common.DragonArmor));
        properties.setProperty("DragonArmorToughness", String.valueOf(Common.DragonArmorToughness));
        
        // Entity-specific - KITSUNE
        properties.setProperty("KitsuneMaxLevel", String.valueOf(Common.KitsuneMaxLevel));
        properties.setProperty("KitsuneMaxHealth", String.valueOf(Common.KitsuneMaxHealth));
        properties.setProperty("KitsuneAttackDamage", String.valueOf(Common.KitsuneAttackDamage));
        properties.setProperty("KitsuneAttackSpeed", String.valueOf(Common.KitsuneAttackSpeed));
        properties.setProperty("KitsuneMovementSpeed", String.valueOf(Common.KitsuneMovementSpeed));
        properties.setProperty("KitsuneArmor", String.valueOf(Common.KitsuneArmor));
        properties.setProperty("KitsuneArmorToughness", String.valueOf(Common.KitsuneArmorToughness));
        
        try (OutputStream output = Files.newOutputStream(CONFIG_PATH)) {
            properties.store(output, "Legacy LovelyRobot Configuration");
            LovelyLegacy.LOGGER.info("Created default config file: {}", CONFIG_PATH);
        } catch (IOException e) {
            LovelyLegacy.LOGGER.error("Failed to create default config file", e);
        }
    } // saveDefaultConfig()

    /**
     * Reloads configuration from file, validating new values before applying.
     * <p>
     * <b>Runtime Reload:</b> Allows server admins to update config without restart.
     * Validates all values before applying to prevent invalid state.
     * <p>
     * <b>Change Logging:</b> Logs config reload event for audit trail.
     */
    public static void reload() {
        LovelyLegacy.LOGGER.info("Reloading configuration...");
        loadConfig();
        LovelyLegacy.LOGGER.info("Configuration reloaded successfully");
    } // reload()

    // -- Helper Methods --

    private static int getIntProperty(Properties properties, String key, int defaultValue) {
        try {
            String value = properties.getProperty(key);
            if (value != null) {
                return Integer.parseInt(value);
            }
        } catch (NumberFormatException e) {
            LovelyLegacy.LOGGER.warn("Invalid integer value for {}, using default: {}", key, defaultValue);
        }
        return defaultValue;
    } // getIntProperty()

    private static float getFloatProperty(Properties properties, String key, float defaultValue) {
        try {
            String value = properties.getProperty(key);
            if (value != null) {
                return Float.parseFloat(value);
            }
        } catch (NumberFormatException e) {
            LovelyLegacy.LOGGER.warn("Invalid float value for {}, using default: {}", key, defaultValue);
        }
        return defaultValue;
    } // getFloatProperty()

    private static double getDoubleProperty(Properties properties, String key, double defaultValue) {
        try {
            String value = properties.getProperty(key);
            if (value != null) {
                return Double.parseDouble(value);
            }
        } catch (NumberFormatException e) {
            LovelyLegacy.LOGGER.warn("Invalid double value for {}, using default: {}", key, defaultValue);
        }
        return defaultValue;
    } // getDoubleProperty()

    private static boolean getBooleanProperty(Properties properties, String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    } // getBooleanProperty()

    // -- Classes --

    /**
     * Config options only available to each client.
     */
    public static class Client {

        // -- Variables --

        // -- RENDERER --

        public static float ShadowRadius = 0.4F;

    } // Class Client

    /**
     * Config options shared by both the client and server.
     */
    public static class Common {

        // -- Variables --

        // -- GENERAL --
        public static int OwnerMaxRobotNum = 30;
        public static double MovementMeleeAttack = 0.8F;
        public static float MovementFollowOwner = 0.7F;
        public static double MovementWanderAround = 0.6F;
        public static float FollowDistanceMax = 10F;
        public static float FollowDistanceMin = 2F;
        public static float LookRange = 8F;

        // -- RENDERER --
        public static float Width = 0.4F;
        public static float Height = 1.9F;

        // -- LEVEL | EXPERIENCE ---
        public static int ExperienceBase = 50;
        public static int ExperienceMultiplier = 2;

        // -- COMBAT --
        public static boolean FriendlyFire = false;
        public static int AttackChance = 5;
        public static int HealInterval = 50;
        public static int WaryTime = 50;
        public static boolean GlobalAutoHeal = true;
        public static boolean LootEnchantment = true;
        public static int LootEnchantmentLevel = 10;
        public static int MaxLootEnchantment = 3;
        public static float BaseDefenceRange = 10;
        public static float BaseDefenceWarpRange = 15;

        // -- PROTECTION --
        public static int ProtectionLimitFire = 80;
        public static int ProtectionLimitFall = 80;
        public static int ProtectionLimitBlast = 80;
        public static int ProtectionLimitProjectile = 80;

        // -- SMART CORE RETRIEVAL --
        public static boolean EnableSmartCoreRetrieval = true;
        public static double SmartCoreRetrievalDistance = 16.0;

        // -- AI BEHAVIOR --
        
        // Follow Mode - Owner Stationary Detection
        public static int OwnerStillThreshold = 100; // ticks (5 seconds)
        
        // Follow Mode - Wander Behavior
        public static int WanderCheckInterval = 200; // ticks (10 seconds)
        public static double WanderChance = 0.15; // 15% chance
        public static double WanderRadiusMin = 3.0; // blocks
        public static double WanderRadiusMax = 6.0; // blocks
        public static int WanderDurationMin = 100; // ticks (5 seconds)
        public static int WanderDurationMax = 200; // ticks (10 seconds)
        public static int WanderCooldownMin = 400; // ticks (20 seconds)
        public static int WanderCooldownMax = 800; // ticks (40 seconds)
        
        // Defense Mode - Patrol Behavior
        public static int PatrolDurationMin = 600; // ticks (30 seconds)
        public static int PatrolDurationMax = 900; // ticks (45 seconds)
        public static int GuardDurationMin = 400; // ticks (20 seconds)
        public static int GuardDurationMax = 600; // ticks (30 seconds)
        public static int PatrolPauseDurationMin = 40; // ticks (2 seconds)
        public static int PatrolPauseDurationMax = 80; // ticks (4 seconds)
        public static double GuardRotationSpeed = 0.05; // radians per tick
        
        // Combat - Radius Enforcement
        public static boolean EnableCombatRadiusParticles = true;
        public static int CombatRadiusParticleCount = 8;
        public static double CombatRadiusParticleSpread = 0.3;

        // -- ANIMATION --
        public static int StandbyToSitDelayMin = 600; // ticks (30 seconds)
        public static int StandbyToSitDelayMax = 1800; // ticks (90 seconds)

        // -- ENTITY --

        // BUNNY2
        public static int Bunny2MaxLevel = 200;
        public static float Bunny2MaxHealth = 24F;
        public static float Bunny2AttackDamage = 4F;
        public static float Bunny2AttackSpeed = 1.8F;
        public static float Bunny2MovementSpeed = 0.37F;
        public static float Bunny2Armor = 6F;
        public static float Bunny2ArmorToughness = 1F;

        // VANILLA
        public static int VanillaMaxLevel = 200;
        public static float VanillaMaxHealth = 16F;
        public static float VanillaAttackDamage = 2F;
        public static float VanillaAttackSpeed = 1.0F;
        public static float VanillaMovementSpeed = 0.37F; // 0.25F is too slow
        public static float VanillaArmor = 2F;
        public static float VanillaArmorToughness = 0F;

        // DRAGON
        public static int DragonMaxLevel = 300;
        public static float DragonMaxHealth = 30F;
        public static float DragonAttackDamage = 8F;
        public static float DragonAttackSpeed = 0.8F;
        public static float DragonMovementSpeed = 0.22F;
        public static float DragonArmor = 4F;
        public static float DragonArmorToughness = 2F;

        // KITSUNE
        public static int KitsuneMaxLevel = 250;
        public static float KitsuneMaxHealth = 22F;
        public static float KitsuneAttackDamage = 6F;
        public static float KitsuneAttackSpeed = 1.1F;
        public static float KitsuneMovementSpeed = 0.28F;
        public static float KitsuneArmor = 2F;
        public static float KitsuneArmorToughness = 1F;

    } // Class Common

    /**
     * Static dimension constants for entity hitboxes.
     * <p>
     * <b>Architecture:</b> Defined as constants to ensure availability before config
     * loading completes. Entity constructors run before config events fire, requiring
     * fallback values for initial dimension setup.
     * <p>
     * <b>Usage:</b> Referenced by entity getDimensions() for hitbox sizing. Config
     * values override these at runtime, but these ensure entities spawn correctly.
     */
    public static class EntityDimensions {
        /** Default entity width (blocks). */
        public static final float DEFAULT_WIDTH = 0.6F;
        
        /** Default entity height (blocks). */
        public static final float DEFAULT_HEIGHT = 1.8F;
        
        /** Sitting pose width (blocks). */
        public static final float SITTING_WIDTH = 0.7F;
        
        /** Sitting pose height (blocks) - half of default height. */
        public static final float SITTING_HEIGHT = 1F;
    } // Class: EntityDimensions

} // Class: LovelyConfigs