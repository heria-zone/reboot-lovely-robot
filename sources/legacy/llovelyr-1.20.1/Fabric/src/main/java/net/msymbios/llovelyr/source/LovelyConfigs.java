package net.msymbios.llovelyr.source;

import com.mojang.datafixers.util.Pair;
import net.msymbios.llovelyr.LovelyLegacy;
import net.msymbios.llovelyr.config.internal.SimpleConfig;
import net.msymbios.llovelyr.config.internal.ConfigProvider;

/**
 * Configuration management for Legacy LovelyRobot mod.
 * <p>
 * <b>Architecture:</b> Uses SimpleConfig for file handling and ConfigProvider for
 * structured config generation. Provides static config values with automatic file
 * creation, validation, and runtime reload support.
 * <p>
 * <b>Config System:</b> ConfigProvider defines all entries with comments and defaults.
 * SimpleConfig handles file I/O, parsing, and type-safe retrieval with fallbacks.
 * <p>
 * <b>Config Location:</b> config/llovelyr.properties in game directory. Created
 * with organized sections and inline comments if missing.
 * <p>
 * <b>Error Handling:</b> Invalid values fall back to defaults with warning logs.
 * Parse errors use defaults and log errors. Missing file creates new with defaults.
 */
public class LovelyConfigs {

    // -- Constants --

    private static final String CONFIG_FILE_NAME = "llovelyr";
    private static SimpleConfig config;

    // -- Methods --

    /**
     * Loads configuration from file or creates default config if missing.
     * <p>
     * <b>Initialization:</b> Called during mod initialization to populate static
     * config fields. Uses ConfigProvider to define structure and SimpleConfig for
     * file operations.
     * <p>
     * <b>Error Recovery:</b> Missing file triggers creation with defaults. Invalid
     * values use defaults with warnings. Parse errors use defaults with error logs.
     */
    public static void register() {
        ConfigProvider provider = new ConfigProvider();
        buildConfigProvider(provider);
        
        config = SimpleConfig.of(CONFIG_FILE_NAME).provider(provider).request();
        
        loadConfigValues();
        
        LovelyLegacy.LOGGER.info("Configuration loaded successfully");
    } // register()

    /**
     * Builds ConfigProvider with all config entries organized into sections.
     * <p>
     * <b>Organization:</b> Groups related settings with section headers and inline
     * comments. Each entry includes description and default value for user reference.
     * <p>
     * <b>Maintainability:</b> Centralizes config definition for easy updates and
     * ensures consistency between code and config file.
     */
    private static void buildConfigProvider(ConfigProvider provider) {
        // ===== GENERAL SETTINGS =====
        provider.addComment("===== GENERAL SETTINGS =====");
        provider.addKeyValuePair(Pair.of("OwnerMaxRobotNum", Common.OwnerMaxRobotNum),
                "Maximum robots per owner");
        provider.addKeyValuePair(Pair.of("MovementMeleeAttack", Common.MovementMeleeAttack),
                "Movement speed during melee attack");
        provider.addKeyValuePair(Pair.of("MovementFollowOwner", Common.MovementFollowOwner),
                "Movement speed when following owner");
        provider.addKeyValuePair(Pair.of("MovementWanderAround", Common.MovementWanderAround),
                "Movement speed when wandering");
        provider.addKeyValuePair(Pair.of("FollowDistanceMax", Common.FollowDistanceMax),
                "Maximum follow distance in blocks");
        provider.addKeyValuePair(Pair.of("FollowDistanceMin", Common.FollowDistanceMin),
                "Minimum follow distance in blocks");
        provider.addKeyValuePair(Pair.of("LookRange", Common.LookRange),
                "Range for looking at entities");
        
        // ===== RENDERER SETTINGS =====
        provider.addComment("");
        provider.addComment("===== RENDERER SETTINGS =====");
        provider.addKeyValuePair(Pair.of("Width", Common.Width), "Entity hitbox width");
        provider.addKeyValuePair(Pair.of("Height", Common.Height), "Entity hitbox height");
        provider.addKeyValuePair(Pair.of("ShadowRadius", Client.ShadowRadius),
                "Shadow rendering radius");
        
        // ===== LEVEL AND EXPERIENCE =====
        provider.addComment("");
        provider.addComment("===== LEVEL AND EXPERIENCE =====");
        provider.addKeyValuePair(Pair.of("ExperienceBase", Common.ExperienceBase),
                "Base experience for level 1");
        provider.addKeyValuePair(Pair.of("ExperienceMultiplier", Common.ExperienceMultiplier),
                "Experience multiplier per level");
        
        // ===== COMBAT SETTINGS =====
        provider.addComment("");
        provider.addComment("===== COMBAT SETTINGS =====");
        provider.addKeyValuePair(Pair.of("FriendlyFire", Common.FriendlyFire),
                "Enable friendly fire between robots");
        provider.addKeyValuePair(Pair.of("AttackChance", Common.AttackChance),
                "Attack chance modifier");
        provider.addKeyValuePair(Pair.of("HealInterval", Common.HealInterval),
                "Ticks between heal attempts");
        provider.addKeyValuePair(Pair.of("WaryTime", Common.WaryTime),
                "Ticks to remain wary after combat");
        provider.addKeyValuePair(Pair.of("GlobalAutoHeal", Common.GlobalAutoHeal),
                "Enable automatic healing");
        provider.addKeyValuePair(Pair.of("LootEnchantment", Common.LootEnchantment),
                "Enable looting enchantment");
        provider.addKeyValuePair(Pair.of("LootEnchantmentLevel", Common.LootEnchantmentLevel),
                "Level requirement for looting enchantment");
        provider.addKeyValuePair(Pair.of("MaxLootEnchantment", Common.MaxLootEnchantment),
                "Maximum looting enchantment level");
        provider.addKeyValuePair(Pair.of("BaseDefenceRange", Common.BaseDefenceRange),
                "Base defense range in blocks");
        provider.addKeyValuePair(Pair.of("BaseDefenceWarpRange", Common.BaseDefenceWarpRange),
                "Base defense warp range in blocks");
        
        // ===== PROTECTION SETTINGS =====
        provider.addComment("");
        provider.addComment("===== PROTECTION SETTINGS =====");
        provider.addKeyValuePair(Pair.of("ProtectionLimitFire", Common.ProtectionLimitFire),
                "Fire protection limit");
        provider.addKeyValuePair(Pair.of("ProtectionLimitFall", Common.ProtectionLimitFall),
                "Fall protection limit");
        provider.addKeyValuePair(Pair.of("ProtectionLimitBlast", Common.ProtectionLimitBlast),
                "Blast protection limit");
        provider.addKeyValuePair(Pair.of("ProtectionLimitProjectile", Common.ProtectionLimitProjectile),
                "Projectile protection limit");
        
        // ===== SMART CORE RETRIEVAL =====
        provider.addComment("");
        provider.addComment("===== SMART CORE RETRIEVAL =====");
        provider.addKeyValuePair(Pair.of("EnableSmartCoreRetrieval", Common.EnableSmartCoreRetrieval),
                "Enable smart core retrieval feature");
        provider.addKeyValuePair(Pair.of("SmartCoreRetrievalDistance", Common.SmartCoreRetrievalDistance),
                "Smart core retrieval distance in blocks");
        
        // ===== AI BEHAVIOR SETTINGS =====
        provider.addComment("");
        provider.addComment("===== AI BEHAVIOR SETTINGS =====");
        provider.addComment("Follow Mode - Owner Stationary Detection");
        provider.addKeyValuePair(Pair.of("OwnerStillThreshold", Common.OwnerStillThreshold),
                "Ticks before owner considered stationary (5 seconds = 100 ticks)");
        
        provider.addComment("");
        provider.addComment("Follow Mode - Wander Behavior");
        provider.addKeyValuePair(Pair.of("WanderCheckInterval", Common.WanderCheckInterval),
                "Ticks between wander checks (10 seconds = 200 ticks)");
        provider.addKeyValuePair(Pair.of("WanderChance", Common.WanderChance),
                "Chance to wander (0.15 = 15%)");
        provider.addKeyValuePair(Pair.of("WanderRadiusMin", Common.WanderRadiusMin),
                "Minimum wander radius in blocks");
        provider.addKeyValuePair(Pair.of("WanderRadiusMax", Common.WanderRadiusMax),
                "Maximum wander radius in blocks");
        provider.addKeyValuePair(Pair.of("WanderDurationMin", Common.WanderDurationMin),
                "Minimum wander duration in ticks (5 seconds = 100 ticks)");
        provider.addKeyValuePair(Pair.of("WanderDurationMax", Common.WanderDurationMax),
                "Maximum wander duration in ticks (10 seconds = 200 ticks)");
        provider.addKeyValuePair(Pair.of("WanderCooldownMin", Common.WanderCooldownMin),
                "Minimum wander cooldown in ticks (20 seconds = 400 ticks)");
        provider.addKeyValuePair(Pair.of("WanderCooldownMax", Common.WanderCooldownMax),
                "Maximum wander cooldown in ticks (40 seconds = 800 ticks)");
        
        provider.addComment("");
        provider.addComment("Defense Mode - Patrol Behavior");
        provider.addKeyValuePair(Pair.of("PatrolDurationMin", Common.PatrolDurationMin),
                "Minimum patrol duration in ticks (30 seconds = 600 ticks)");
        provider.addKeyValuePair(Pair.of("PatrolDurationMax", Common.PatrolDurationMax),
                "Maximum patrol duration in ticks (45 seconds = 900 ticks)");
        provider.addKeyValuePair(Pair.of("GuardDurationMin", Common.GuardDurationMin),
                "Minimum guard duration in ticks (20 seconds = 400 ticks)");
        provider.addKeyValuePair(Pair.of("GuardDurationMax", Common.GuardDurationMax),
                "Minimum guard duration in ticks (30 seconds = 600 ticks)");
        provider.addKeyValuePair(Pair.of("PatrolPauseDurationMin", Common.PatrolPauseDurationMin),
                "Minimum patrol pause duration in ticks (2 seconds = 40 ticks)");
        provider.addKeyValuePair(Pair.of("PatrolPauseDurationMax", Common.PatrolPauseDurationMax),
                "Maximum patrol pause duration in ticks (4 seconds = 80 ticks)");
        provider.addKeyValuePair(Pair.of("GuardRotationSpeed", Common.GuardRotationSpeed),
                "Guard rotation speed in radians per tick");
        
        provider.addComment("");
        provider.addComment("Combat - Radius Enforcement");
        provider.addKeyValuePair(Pair.of("EnableCombatRadiusParticles", Common.EnableCombatRadiusParticles),
                "Enable combat radius particle effects");
        provider.addKeyValuePair(Pair.of("CombatRadiusParticleCount", Common.CombatRadiusParticleCount),
                "Number of particles for combat radius");
        provider.addKeyValuePair(Pair.of("CombatRadiusParticleSpread", Common.CombatRadiusParticleSpread),
                "Particle spread for combat radius");
        
        // ===== ANIMATION SETTINGS =====
        provider.addComment("");
        provider.addComment("===== ANIMATION SETTINGS =====");
        provider.addKeyValuePair(Pair.of("StandbyToSitDelayMin", Common.StandbyToSitDelayMin),
                "Minimum delay before sitting in ticks (30 seconds = 600 ticks)");
        provider.addKeyValuePair(Pair.of("StandbyToSitDelayMax", Common.StandbyToSitDelayMax),
                "Maximum delay before sitting in ticks (90 seconds = 1800 ticks)");
        
        // ===== ENTITY-SPECIFIC SETTINGS =====
        provider.addComment("");
        provider.addComment("===== ENTITY-SPECIFIC SETTINGS =====");
        
        // BUNNY2
        provider.addComment("");
        provider.addComment("BUNNY2 Robot");
        provider.addKeyValuePair(Pair.of("Bunny2MaxLevel", Common.Bunny2MaxLevel),
                "Maximum level for Bunny2");
        provider.addKeyValuePair(Pair.of("Bunny2MaxHealth", Common.Bunny2MaxHealth),
                "Maximum health for Bunny2");
        provider.addKeyValuePair(Pair.of("Bunny2AttackDamage", Common.Bunny2AttackDamage),
                "Attack damage for Bunny2");
        provider.addKeyValuePair(Pair.of("Bunny2AttackSpeed", Common.Bunny2AttackSpeed),
                "Attack speed for Bunny2");
        provider.addKeyValuePair(Pair.of("Bunny2MovementSpeed", Common.Bunny2MovementSpeed),
                "Movement speed for Bunny2");
        provider.addKeyValuePair(Pair.of("Bunny2Armor", Common.Bunny2Armor),
                "Armor value for Bunny2");
        provider.addKeyValuePair(Pair.of("Bunny2ArmorToughness", Common.Bunny2ArmorToughness),
                "Armor toughness for Bunny2");
        
        // VANILLA
        provider.addComment("");
        provider.addComment("VANILLA Robot");
        provider.addKeyValuePair(Pair.of("VanillaMaxLevel", Common.VanillaMaxLevel),
                "Maximum level for Vanilla");
        provider.addKeyValuePair(Pair.of("VanillaMaxHealth", Common.VanillaMaxHealth),
                "Maximum health for Vanilla");
        provider.addKeyValuePair(Pair.of("VanillaAttackDamage", Common.VanillaAttackDamage),
                "Attack damage for Vanilla");
        provider.addKeyValuePair(Pair.of("VanillaAttackSpeed", Common.VanillaAttackSpeed),
                "Attack speed for Vanilla");
        provider.addKeyValuePair(Pair.of("VanillaMovementSpeed", Common.VanillaMovementSpeed),
                "Movement speed for Vanilla");
        provider.addKeyValuePair(Pair.of("VanillaArmor", Common.VanillaArmor),
                "Armor value for Vanilla");
        provider.addKeyValuePair(Pair.of("VanillaArmorToughness", Common.VanillaArmorToughness),
                "Armor toughness for Vanilla");
        
        // DRAGON
        provider.addComment("");
        provider.addComment("DRAGON Robot");
        provider.addKeyValuePair(Pair.of("DragonMaxLevel", Common.DragonMaxLevel),
                "Maximum level for Dragon");
        provider.addKeyValuePair(Pair.of("DragonMaxHealth", Common.DragonMaxHealth),
                "Maximum health for Dragon");
        provider.addKeyValuePair(Pair.of("DragonAttackDamage", Common.DragonAttackDamage),
                "Attack damage for Dragon");
        provider.addKeyValuePair(Pair.of("DragonAttackSpeed", Common.DragonAttackSpeed),
                "Attack speed for Dragon");
        provider.addKeyValuePair(Pair.of("DragonMovementSpeed", Common.DragonMovementSpeed),
                "Movement speed for Dragon");
        provider.addKeyValuePair(Pair.of("DragonArmor", Common.DragonArmor),
                "Armor value for Dragon");
        provider.addKeyValuePair(Pair.of("DragonArmorToughness", Common.DragonArmorToughness),
                "Armor toughness for Dragon");
        
        // KITSUNE
        provider.addComment("");
        provider.addComment("KITSUNE Robot");
        provider.addKeyValuePair(Pair.of("KitsuneMaxLevel", Common.KitsuneMaxLevel),
                "Maximum level for Kitsune");
        provider.addKeyValuePair(Pair.of("KitsuneMaxHealth", Common.KitsuneMaxHealth),
                "Maximum health for Kitsune");
        provider.addKeyValuePair(Pair.of("KitsuneAttackDamage", Common.KitsuneAttackDamage),
                "Attack damage for Kitsune");
        provider.addKeyValuePair(Pair.of("KitsuneAttackSpeed", Common.KitsuneAttackSpeed),
                "Attack speed for Kitsune");
        provider.addKeyValuePair(Pair.of("KitsuneMovementSpeed", Common.KitsuneMovementSpeed),
                "Movement speed for Kitsune");
        provider.addKeyValuePair(Pair.of("KitsuneArmor", Common.KitsuneArmor),
                "Armor value for Kitsune");
        provider.addKeyValuePair(Pair.of("KitsuneArmorToughness", Common.KitsuneArmorToughness),
                "Armor toughness for Kitsune");
    } // buildConfigProvider()

    /**
     * Loads config values from SimpleConfig with type-safe retrieval.
     * <p>
     * <b>Type Safety:</b> Uses SimpleConfig's type-safe getters with automatic
     * fallback to defaults on parse failure. Logs warnings for invalid values.
     * <p>
     * <b>Validation:</b> SimpleConfig handles validation and error logging,
     * ensuring robust config loading without manual error handling.
     */
    private static void loadConfigValues() {
        // General settings
        Common.OwnerMaxRobotNum = config.getOrDefault("OwnerMaxRobotNum", Common.OwnerMaxRobotNum);
        Common.MovementMeleeAttack = config.getOrDefault("MovementMeleeAttack", Common.MovementMeleeAttack);
        Common.MovementFollowOwner = config.getOrDefault("MovementFollowOwner", Common.MovementFollowOwner);
        Common.MovementWanderAround = config.getOrDefault("MovementWanderAround", Common.MovementWanderAround);
        Common.FollowDistanceMax = config.getOrDefault("FollowDistanceMax", Common.FollowDistanceMax);
        Common.FollowDistanceMin = config.getOrDefault("FollowDistanceMin", Common.FollowDistanceMin);
        Common.LookRange = config.getOrDefault("LookRange", Common.LookRange);
        
        // Renderer settings
        Common.Width = config.getOrDefault("Width", Common.Width);
        Common.Height = config.getOrDefault("Height", Common.Height);
        Client.ShadowRadius = config.getOrDefault("ShadowRadius", Client.ShadowRadius);
        
        // Level/Experience settings
        Common.ExperienceBase = config.getOrDefault("ExperienceBase", Common.ExperienceBase);
        Common.ExperienceMultiplier = config.getOrDefault("ExperienceMultiplier", Common.ExperienceMultiplier);
        
        // Combat settings
        Common.FriendlyFire = config.getOrDefault("FriendlyFire", Common.FriendlyFire);
        Common.AttackChance = config.getOrDefault("AttackChance", Common.AttackChance);
        Common.HealInterval = config.getOrDefault("HealInterval", Common.HealInterval);
        Common.WaryTime = config.getOrDefault("WaryTime", Common.WaryTime);
        Common.GlobalAutoHeal = config.getOrDefault("GlobalAutoHeal", Common.GlobalAutoHeal);
        Common.LootEnchantment = config.getOrDefault("LootEnchantment", Common.LootEnchantment);
        Common.LootEnchantmentLevel = config.getOrDefault("LootEnchantmentLevel", Common.LootEnchantmentLevel);
        Common.MaxLootEnchantment = config.getOrDefault("MaxLootEnchantment", Common.MaxLootEnchantment);
        Common.BaseDefenceRange = config.getOrDefault("BaseDefenceRange", Common.BaseDefenceRange);
        Common.BaseDefenceWarpRange = config.getOrDefault("BaseDefenceWarpRange", Common.BaseDefenceWarpRange);
        
        // Protection settings
        Common.ProtectionLimitFire = config.getOrDefault("ProtectionLimitFire", Common.ProtectionLimitFire);
        Common.ProtectionLimitFall = config.getOrDefault("ProtectionLimitFall", Common.ProtectionLimitFall);
        Common.ProtectionLimitBlast = config.getOrDefault("ProtectionLimitBlast", Common.ProtectionLimitBlast);
        Common.ProtectionLimitProjectile = config.getOrDefault("ProtectionLimitProjectile", Common.ProtectionLimitProjectile);
        
        // Smart Core Retrieval
        Common.EnableSmartCoreRetrieval = config.getOrDefault("EnableSmartCoreRetrieval", Common.EnableSmartCoreRetrieval);
        Common.SmartCoreRetrievalDistance = config.getOrDefault("SmartCoreRetrievalDistance", Common.SmartCoreRetrievalDistance);
        
        // AI Behavior settings
        Common.OwnerStillThreshold = config.getOrDefault("OwnerStillThreshold", Common.OwnerStillThreshold);
        Common.WanderCheckInterval = config.getOrDefault("WanderCheckInterval", Common.WanderCheckInterval);
        Common.WanderChance = config.getOrDefault("WanderChance", Common.WanderChance);
        Common.WanderRadiusMin = config.getOrDefault("WanderRadiusMin", Common.WanderRadiusMin);
        Common.WanderRadiusMax = config.getOrDefault("WanderRadiusMax", Common.WanderRadiusMax);
        Common.WanderDurationMin = config.getOrDefault("WanderDurationMin", Common.WanderDurationMin);
        Common.WanderDurationMax = config.getOrDefault("WanderDurationMax", Common.WanderDurationMax);
        Common.WanderCooldownMin = config.getOrDefault("WanderCooldownMin", Common.WanderCooldownMin);
        Common.WanderCooldownMax = config.getOrDefault("WanderCooldownMax", Common.WanderCooldownMax);
        Common.PatrolDurationMin = config.getOrDefault("PatrolDurationMin", Common.PatrolDurationMin);
        Common.PatrolDurationMax = config.getOrDefault("PatrolDurationMax", Common.PatrolDurationMax);
        Common.GuardDurationMin = config.getOrDefault("GuardDurationMin", Common.GuardDurationMin);
        Common.GuardDurationMax = config.getOrDefault("GuardDurationMax", Common.GuardDurationMax);
        Common.PatrolPauseDurationMin = config.getOrDefault("PatrolPauseDurationMin", Common.PatrolPauseDurationMin);
        Common.PatrolPauseDurationMax = config.getOrDefault("PatrolPauseDurationMax", Common.PatrolPauseDurationMax);
        Common.GuardRotationSpeed = config.getOrDefault("GuardRotationSpeed", Common.GuardRotationSpeed);
        Common.EnableCombatRadiusParticles = config.getOrDefault("EnableCombatRadiusParticles", Common.EnableCombatRadiusParticles);
        Common.CombatRadiusParticleCount = config.getOrDefault("CombatRadiusParticleCount", Common.CombatRadiusParticleCount);
        Common.CombatRadiusParticleSpread = config.getOrDefault("CombatRadiusParticleSpread", Common.CombatRadiusParticleSpread);
        
        // Animation settings
        Common.StandbyToSitDelayMin = config.getOrDefault("StandbyToSitDelayMin", Common.StandbyToSitDelayMin);
        Common.StandbyToSitDelayMax = config.getOrDefault("StandbyToSitDelayMax", Common.StandbyToSitDelayMax);
        
        // Entity-specific settings - BUNNY2
        Common.Bunny2MaxLevel = config.getOrDefault("Bunny2MaxLevel", Common.Bunny2MaxLevel);
        Common.Bunny2MaxHealth = config.getOrDefault("Bunny2MaxHealth", Common.Bunny2MaxHealth);
        Common.Bunny2AttackDamage = config.getOrDefault("Bunny2AttackDamage", Common.Bunny2AttackDamage);
        Common.Bunny2AttackSpeed = config.getOrDefault("Bunny2AttackSpeed", Common.Bunny2AttackSpeed);
        Common.Bunny2MovementSpeed = config.getOrDefault("Bunny2MovementSpeed", Common.Bunny2MovementSpeed);
        Common.Bunny2Armor = config.getOrDefault("Bunny2Armor", Common.Bunny2Armor);
        Common.Bunny2ArmorToughness = config.getOrDefault("Bunny2ArmorToughness", Common.Bunny2ArmorToughness);
        
        // Entity-specific settings - VANILLA
        Common.VanillaMaxLevel = config.getOrDefault("VanillaMaxLevel", Common.VanillaMaxLevel);
        Common.VanillaMaxHealth = config.getOrDefault("VanillaMaxHealth", Common.VanillaMaxHealth);
        Common.VanillaAttackDamage = config.getOrDefault("VanillaAttackDamage", Common.VanillaAttackDamage);
        Common.VanillaAttackSpeed = config.getOrDefault("VanillaAttackSpeed", Common.VanillaAttackSpeed);
        Common.VanillaMovementSpeed = config.getOrDefault("VanillaMovementSpeed", Common.VanillaMovementSpeed);
        Common.VanillaArmor = config.getOrDefault("VanillaArmor", Common.VanillaArmor);
        Common.VanillaArmorToughness = config.getOrDefault("VanillaArmorToughness", Common.VanillaArmorToughness);
        
        // Entity-specific settings - DRAGON
        Common.DragonMaxLevel = config.getOrDefault("DragonMaxLevel", Common.DragonMaxLevel);
        Common.DragonMaxHealth = config.getOrDefault("DragonMaxHealth", Common.DragonMaxHealth);
        Common.DragonAttackDamage = config.getOrDefault("DragonAttackDamage", Common.DragonAttackDamage);
        Common.DragonAttackSpeed = config.getOrDefault("DragonAttackSpeed", Common.DragonAttackSpeed);
        Common.DragonMovementSpeed = config.getOrDefault("DragonMovementSpeed", Common.DragonMovementSpeed);
        Common.DragonArmor = config.getOrDefault("DragonArmor", Common.DragonArmor);
        Common.DragonArmorToughness = config.getOrDefault("DragonArmorToughness", Common.DragonArmorToughness);
        
        // Entity-specific settings - KITSUNE
        Common.KitsuneMaxLevel = config.getOrDefault("KitsuneMaxLevel", Common.KitsuneMaxLevel);
        Common.KitsuneMaxHealth = config.getOrDefault("KitsuneMaxHealth", Common.KitsuneMaxHealth);
        Common.KitsuneAttackDamage = config.getOrDefault("KitsuneAttackDamage", Common.KitsuneAttackDamage);
        Common.KitsuneAttackSpeed = config.getOrDefault("KitsuneAttackSpeed", Common.KitsuneAttackSpeed);
        Common.KitsuneMovementSpeed = config.getOrDefault("KitsuneMovementSpeed", Common.KitsuneMovementSpeed);
        Common.KitsuneArmor = config.getOrDefault("KitsuneArmor", Common.KitsuneArmor);
        Common.KitsuneArmorToughness = config.getOrDefault("KitsuneArmorToughness", Common.KitsuneArmorToughness);
    } // loadConfigValues()



    /**
     * Reloads configuration from file, validating new values before applying.
     * <p>
     * <b>Runtime Reload:</b> Allows server admins to update config without restart.
     * Re-reads config file from disk and validates all values before applying.
     * <p>
     * <b>Error Recovery:</b> On reload failure, preserves existing config values
     * and logs error details. Config remains in last known good state.
     * <p>
     * <b>Change Logging:</b> Logs reload start and completion for audit trail.
     */
    public static void reload() {
        LovelyLegacy.LOGGER.info("Starting configuration reload from disk...");
        
        // Store reference to current config for rollback
        SimpleConfig previousConfig = config;
        
        try {
            // Re-read config file by creating new SimpleConfig instance
            ConfigProvider provider = new ConfigProvider();
            buildConfigProvider(provider);
            
            SimpleConfig newConfig = SimpleConfig.of(CONFIG_FILE_NAME).provider(provider).request();
            
            // Check if new config loaded successfully
            if (newConfig.isBroken()) {
                LovelyLegacy.LOGGER.error("Config file is broken or failed to parse, preserving existing configuration values");
                return;
            }
            
            // Update the config reference
            config = newConfig;
            
            // Load all values from the new config
            loadConfigValues();
            
            LovelyLegacy.LOGGER.info("Configuration reload completed successfully");
        } catch (Exception e) {
            // Restore previous config on any error
            config = previousConfig;
            LovelyLegacy.LOGGER.error("Failed to reload configuration due to unexpected error, preserving existing configuration values: " + e.getMessage(), e);
        }
    } // reload()

    // -- Classes --

    /**
     * Client-side configuration options for rendering and visual effects.
     * <p>
     * <b>Scope:</b> Values in this class are only loaded and used on the client side.
     * Server does not access or validate these settings.
     * <p>
     * <b>Usage:</b> Primarily controls visual rendering parameters that have no
     * gameplay impact and should not be synchronized across client-server boundary.
     */
    public static class Client {

        // -- Variables --

        // -- RENDERER --

        public static float ShadowRadius = 0.4F;

    } // Class Client

    /**
     * Shared configuration options available to both client and server.
     * <p>
     * <b>Scope:</b> Values in this class affect gameplay mechanics and must be
     * consistent across client-server boundary. Server is authoritative for these values.
     * <p>
     * <b>Organization:</b> Settings are grouped by functional area (general, combat,
     * AI behavior, entity-specific) for maintainability and user comprehension.
     * <p>
     * <b>Synchronization:</b> These values are loaded from server config and should
     * match on all connected clients to ensure consistent gameplay behavior.
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