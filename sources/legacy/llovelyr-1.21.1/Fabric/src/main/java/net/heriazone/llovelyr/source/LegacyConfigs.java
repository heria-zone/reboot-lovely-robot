package net.heriazone.llovelyr.source;

import net.heriazone.llovelyr.Legacy;
import net.heriazone.lovelylib.common.configs.*;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.hzlib.api.configs.*;

import java.util.HashMap;

/**
 * Configuration management for Legacy NativeRobotEntity mod.
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
public class LegacyConfigs {

    // -- Constants --

    private static final String CONFIG_FILE_NAME = Legacy.MODID;
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

        Legacy.LOGGER.info("Configuration loaded successfully");
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
        // General Settings
        provider.push("General");
        provider.comment("How many robots each player can own at once.", "Set to -1 for unlimited robots.", "Range: -1 to no upper limit", "Example: [30]")
                .define("owner-max-robot", SharedConfigs.Common.OwnerMaxRobotNum);

        provider.comment("How fast robots move when attacking enemies in melee combat.", "Higher values make robots move faster during attacks.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.8]")
                .define("movement-melee-attack", SharedConfigs.Common.MovementMeleeAttack);

        provider.comment("How fast robots move when following their owner.", "Higher values make robots keep up with you better.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.7]")
                .define("movement-follow-owner", SharedConfigs.Common.MovementFollowOwner);

        provider.comment("How fast robots move when wandering around on their own.", "Lower than follow speed to make wandering look more relaxed.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.6]")
                .define("movement-wander-around", SharedConfigs.Common.MovementWanderAround);

        provider.comment("Maximum distance (in blocks) robots will stay from their owner before teleporting.", "If you get too far, your robot will teleport to you.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [10.0]")
                .define("follow-distance-max", SharedConfigs.Common.FollowDistanceMax);

        provider.comment("Minimum distance (in blocks) robots try to maintain from their owner.", "Robots won't crowd you closer than this distance.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [2.0]")
                .define("follow-distance-min", SharedConfigs.Common.FollowDistanceMin);

        provider.comment("How far (in blocks) robots can look at and track entities.", "Affects head rotation and attention behavior.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [8.0]")
                .define("look-range", SharedConfigs.Common.LookRange);
        provider.pop();

        provider.push("Renderer");
        provider.comment("Size of the shadow rendered under robots.", "Purely visual - doesn't affect gameplay.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.4]")
                .defineInRange("shadow-radius", SharedConfigs.Client.ShadowRadius, ConfigBounds.SHADOW_RADIUS_MIN, ConfigBounds.SHADOW_RADIUS_MAX);
        provider.pop();

        provider.push("Level & Experience");
        provider.comment("Base experience points needed for a robot to reach level 1.", "Each level requires more XP based on the multiplier below.", "Range: 0 to no upper limit (does not accept negative values)", "Example: [50]")
                .define("experience-base", SharedConfigs.Common.ExperienceBase);

        provider.comment("How much more XP each level requires compared to the previous level.", "Level 2 needs base × multiplier, Level 3 needs base × multiplier², etc.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [2]")
                .define("experience-multiplier", SharedConfigs.Common.ExperienceMultiplier);
        provider.pop();

        provider.push("Combat");
        provider.comment("Whether players can damage their own robots.", "Set to true to allow accidentally hitting your robots, false to prevent it.", "Example: [false]")
                .define("friendly-fire", SharedConfigs.Common.FriendlyFire);

        provider.comment("How likely robots are to counter-attack when hit (higher = more aggressive).", "Affects how quickly robots retaliate when damaged.", "Range: 0 to no upper limit (does not accept negative values)", "Example: [5]")
                .define("attack-chance", SharedConfigs.Common.AttackChance);

        provider.comment("How often (in ticks) robots automatically heal themselves.", "20 ticks = 1 second. Lower values = faster healing.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [50]")
                .define("heal-interval", SharedConfigs.Common.HealInterval);

        provider.comment("How long (in ticks) robots stay alert after combat ends.", "20 ticks = 1 second. During this time, robots remain ready to fight.", "Range: 0 to no upper limit (does not accept negative values)", "Example: [50]")
                .define("wary-time", SharedConfigs.Common.WaryTime);

        provider.comment("Whether robots automatically heal over time.", "Set to false to disable automatic healing entirely.", "Example: [true]")
                .define("global-heal", SharedConfigs.Common.GlobalAutoHeal);

        provider.comment("Whether robots can benefit from Looting enchantment on their weapon.", "When enabled, higher level robots get better mob drops.", "Example: [true]")
                .define("loot-enchantment", SharedConfigs.Common.LootEnchantment);

        provider.comment("What robot level is needed to gain Looting I enchantment effect.", "Looting II at 2× this level, Looting III at 3× this level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [10]")
                .define("loot-enchantment-level", SharedConfigs.Common.LootEnchantmentLevel);

        provider.comment("Maximum Looting enchantment level robots can have.", "Limits how much bonus loot high-level robots can get.", "Range: 0 to 3", "Example: [3]")
                .define("max-loot-enchantment", SharedConfigs.Common.MaxLootEnchantment);

        provider.comment("How far (in blocks) robots will chase enemies from their guard position.", "In Defense mode, robots won't chase beyond this distance.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [15.0]")
                .define("base-defence-range", SharedConfigs.Common.BaseDefenceRange);

        provider.comment("How far (in blocks) robots can be from guard position before teleporting back.", "Prevents robots from getting stuck too far from their post.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [10.0]")
                .define("base-defence-warp-range", SharedConfigs.Common.BaseDefenceWarpRange);
        provider.pop();

        provider.push("Protection");
        provider.comment("Maximum percentage of fire damage robots can resist.", "At 80%, robots take only 20% of fire damage. At 100%, they're immune to fire.", "Range: 0 to 100", "Example: [80]")
                .define("limit-fire", SharedConfigs.Common.ProtectionLimitFire);

        provider.comment("Maximum percentage of fall damage robots can resist.", "At 80%, robots take only 20% of fall damage. At 100%, they never take fall damage.", "Range: 0 to 100", "Example: [80]")
                .define("limit-fall", SharedConfigs.Common.ProtectionLimitFall);

        provider.comment("Maximum percentage of explosion damage robots can resist.", "At 80%, robots take only 20% of explosion damage. At 100%, they're immune to explosions.", "Range: 0 to 100", "Example: [80]")
                .define("limit-blast", SharedConfigs.Common.ProtectionLimitBlast);

        provider.comment("Maximum percentage of projectile damage robots can resist.", "At 80%, robots take only 20% of arrow/projectile damage. At 100%, they're immune.", "Range: 0 to 100", "Example: [80]")
                .define("limit-projectile", SharedConfigs.Common.ProtectionLimitProjectile);

        provider.comment("Allow robots to consume enchanted books to increase protection values.", "Books with Fire/Blast/Feather Falling/Projectile Protection enchantments can be fed to robots.", "Formula: protectionGain = enchantmentLevel × contribution × maxProtection", "Example: Fire Protection II with 25% contribution and max=80 gives 40 points")
                .define("enable-enchanted-book-protection", SharedConfigs.Common.EnableEnchantedBookProtection);

        provider.comment("Percentage contribution per enchantment level (0.25 = 25%).", "Determines how much protection each enchantment level provides.", "Formula: protectionGain = level × percentage × maxProtection", "Example: Level II with 25% = 2 × 0.25 × 80 = 40 points", "Range: 0.01 to 1.0")
                .define("enchanted-book-contribution", SharedConfigs.Common.EnchantedBookContributionPercentage);
        provider.pop();

        provider.push("Smart Core Retrieval");
        provider.comment("Whether robot cores automatically go to your inventory when robots die nearby.", "When enabled, you don't need to pick up cores manually if you're close enough.", "Example: [true]")
                .define("enable-smart-core-retrieval", SharedConfigs.Common.EnableSmartCoreRetrieval);

        provider.comment("How close (in blocks) you need to be for automatic core retrieval to work.", "If your robot dies within this distance, the core goes straight to your inventory.", "Range: 0.0 to 128.0", "Example: [16.0]")
                .define("smart-core-retrieval-distance", SharedConfigs.Common.SmartCoreRetrievalDistance);
        provider.pop();

        provider.push("AI Behavior");

        provider.push("Follow Mode");
        provider.comment("How long (in ticks) their owner must stand still before robots start wandering.", "20 ticks = 1 second. Robots stay put if you keep moving.", "Range: 0 to 6000", "Example: [100] (5 seconds)")
                .define("owner-still-threshold", SharedConfigs.Common.OwnerStillThreshold);

        provider.comment("How often (in ticks) robots check if they should wander.", "20 ticks = 1 second. Lower values make robots more responsive.", "Range: 100 to 6000", "Example: [200] (10 seconds)")
                .define("wander-check-interval", SharedConfigs.Common.WanderCheckInterval);

        provider.comment("Chance robots will wander when their owner is standing still.", "0.15 = 15% chance per check. Higher values make robots wander more often.", "Range: 0.0 to 1.0", "Example: [0.15] (15%)")
                .define("wander-chance", SharedConfigs.Common.WanderChance);

        provider.comment("Minimum distance (in blocks) robots will wander from their owner.", "Robots won't wander closer than this.", "Range: 1.0 to 32.0", "Example: [3.0]")
                .define("wander-radius-min", SharedConfigs.Common.WanderRadiusMin);

        provider.comment("Maximum distance (in blocks) robots will wander from their owner.", "Robots won't wander farther than this.", "Range: 1.0 to 32.0", "Example: [6.0]")
                .define("wander-radius-max", SharedConfigs.Common.WanderRadiusMax);

        provider.comment("Minimum time (in ticks) robots will wander before returning.", "20 ticks = 1 second. Shorter wanders feel more cautious.", "Range: 20 to 6000", "Example: [100] (5 seconds)")
                .define("wander-duration-min", SharedConfigs.Common.WanderDurationMin);

        provider.comment("Maximum time (in ticks) robots will wander before returning.", "20 ticks = 1 second. Longer wanders make robots more independent.", "Range: 20 to 6000", "Example: [200] (10 seconds)")
                .define("wander-duration-max", SharedConfigs.Common.WanderDurationMax);

        provider.comment("Minimum time (in ticks) before robots can wander again.", "20 ticks = 1 second. Prevents constant wandering.", "Range: 100 to 12000", "Example: [400] (20 seconds)")
                .define("wander-cooldown-min", SharedConfigs.Common.WanderCooldownMin);

        provider.comment("Maximum time (in ticks) before robots can wander again.", "20 ticks = 1 second. Adds variety to wandering behavior.", "Range: 100 to 12000", "Example: [800] (40 seconds)")
                .define("wander-cooldown-max", SharedConfigs.Common.WanderCooldownMax);
        provider.pop();

        provider.push("Defense Mode");
        provider.comment("Minimum time (in ticks) robots patrol around their guard position.", "20 ticks = 1 second. In Defense mode, robots walk around looking for threats.", "Range: 100 to 6000", "Example: [600] (30 seconds)")
                .define("patrol-duration-min", SharedConfigs.Common.PatrolDurationMin);

        provider.comment("Maximum time (in ticks) robots patrol around their guard position.", "20 ticks = 1 second. Longer patrols make robots cover more area.", "Range: 100 to 6000", "Example: [900] (45 seconds)")
                .define("patrol-duration-max", SharedConfigs.Common.PatrolDurationMax);

        provider.comment("Minimum time (in ticks) robots stand guard and look around.", "20 ticks = 1 second. In Defense mode, robots alternate between patrolling and guarding.", "Range: 100 to 6000", "Example: [400] (20 seconds)")
                .define("guard-duration-min", SharedConfigs.Common.GuardDurationMin);

        provider.comment("Maximum time (in ticks) robots stand guard and look around.", "20 ticks = 1 second. Longer guard times make robots more watchful.", "Range: 100 to 6000", "Example: [600] (30 seconds)")
                .define("guard-duration-max", SharedConfigs.Common.GuardDurationMax);

        provider.comment("Minimum time (in ticks) robots pause at each patrol point.", "20 ticks = 1 second. Brief pauses make patrolling look more natural.", "Range: 10 to 600", "Example: [40] (2 seconds)")
                .define("patrol-pause-duration-min", SharedConfigs.Common.PatrolPauseDurationMin);

        provider.comment("Maximum time (in ticks) robots pause at each patrol point.", "20 ticks = 1 second. Longer pauses make robots more observant.", "Range: 10 to 600", "Example: [80] (4 seconds)")
                .define("patrol-pause-duration-max", SharedConfigs.Common.PatrolPauseDurationMax);

        provider.comment("How fast robots rotate their head while guarding.", "Higher values make robots scan faster. Lower values look more deliberate.", "Range: 0.01 to 0.5", "Example: [0.05]")
                .define("guard-rotation-speed", SharedConfigs.Common.GuardRotationSpeed);
        provider.pop();

        provider.push("Combat Radius");
        provider.comment("Whether to show smoke particles when robots can't chase enemies further.", "Visual feedback when robots hit their chase distance limit in Defense mode.", "Example: [true]")
                .define("enable-combat-radius-particles", SharedConfigs.Common.EnableCombatRadiusParticles);

        provider.comment("How many smoke particles appear when robots hit chase limit.", "More particles make the effect more visible.", "Range: 1 to 50", "Example: [8]")
                .define("combat-radius-particle-count", SharedConfigs.Common.CombatRadiusParticleCount);

        provider.comment("How spread out the smoke particles are.", "Higher values create a wider particle cloud.", "Range: 0.1 to 2.0", "Example: [0.3]")
                .define("combat-radius-particle-spread", SharedConfigs.Common.CombatRadiusParticleSpread);
        provider.pop();

        provider.push("Animation");
        provider.comment("Minimum time (in ticks) before idle robots sit down.", "20 ticks = 1 second. In Standby mode, robots eventually sit if nothing is happening.", "Range: 100 to 12000", "Example: [600] (30 seconds)")
                .define("standby-to-sit-delay-min", SharedConfigs.Common.StandbyToSitDelayMin);

        provider.comment("Maximum time (in ticks) before idle robots sit down.", "20 ticks = 1 second. Adds variety to when robots decide to sit.", "Range: 100 to 12000", "Example: [1800] (90 seconds)")
                .define("standby-to-sit-delay-max", SharedConfigs.Common.StandbyToSitDelayMax);
        provider.pop();

        provider.pop();

        provider.push("Entity");

        for (String variant : LovelyConstant.LEGACY_VARIANTS) {
            provider.push(variant);
            provider.comment("Highest level this robot type can reach.", "Higher levels unlock better stats and abilities.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [200]")
                    .define(variant + "-" + LovelyConstant.CONFIG_MAX_LEVEL, getDefaultMaxLevel(variant));

            provider.comment("How fast this robot type attacks (attacks per second).", "Higher values mean faster attacks. Minecraft default is 1.0.", "Range: 0.1 to no upper limit (does not accept negative values or zero)", "Example: [1.6]")
                    .define(variant + "-" + LovelyConstant.CONFIG_ATTACK_SPEED, getDefaultAttackSpeed(variant));

            provider.comment("Base movement speed for this robot type.", "Higher values make robots move faster. Player walk speed is 0.1.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.37]")
                    .define(variant + "-" + LovelyConstant.CONFIG_MOVEMENT_SPEED, getDefaultMovementSpeed(variant));

            provider.comment("Base toughness for this robot type.", "Reduces damage from strong attacks. Diamond armor has 2.0 toughness per piece.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.0]")
                    .define(variant + "-" + LovelyConstant.CONFIG_BASE_TOUGHNESS, getDefaultBaseToughness(variant));

            provider.comment("Base HP value for combat level calculations.", "Used by CombatLevelFeature to calculate HP at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [16]")
                    .define(variant + "-" + LovelyConstant.CONFIG_BASE_HP, getDefaultBaseHp(variant));

            provider.comment("Base attack value for combat level calculations.", "Used by CombatLevelFeature to calculate attack damage at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [2]")
                    .define(variant + "-" + LovelyConstant.CONFIG_BASE_ATTACK, getDefaultBaseAttack(variant));

            provider.comment("Base defense value for combat level calculations.", "Used by CombatLevelFeature to calculate armor and toughness at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [3]")
                    .define(variant + "-" + LovelyConstant.CONFIG_BASE_DEFENSE, getDefaultBaseDefense(variant));
            provider.pop();
        }

        provider.pop();
    } // buildConfigProvider()

    /**
     * Loads config values from SimpleConfig with type-safe retrieval and validation.
     * <p>
     * <b>Type Safety:</b> Uses SimpleConfig's type-safe getters with automatic
     * fallback to defaults on parse failure. Logs warnings for invalid values.
     * <p>
     * <b>Validation:</b> All values are validated against ConfigBounds to prevent
     * invalid configurations that could break mod functionality or crash the system.
     */
    private static void loadConfigValues() {
        // General settings
        SharedConfigs.Common.OwnerMaxRobotNum = config.getOrDefault("owner-max-robot", SharedConfigs.Common.OwnerMaxRobotNum);
        SharedConfigs.Common.MovementMeleeAttack = config.getOrDefault("movement-melee-attack", SharedConfigs.Common.MovementMeleeAttack);
        SharedConfigs.Common.MovementFollowOwner = config.getOrDefault("movement-follow-owner", SharedConfigs.Common.MovementFollowOwner);
        SharedConfigs.Common.MovementWanderAround = config.getOrDefault("movement-wander-around", SharedConfigs.Common.MovementWanderAround);
        SharedConfigs.Common.FollowDistanceMax = config.getOrDefault("follow-distance-max", SharedConfigs.Common.FollowDistanceMax);
        SharedConfigs.Common.FollowDistanceMin = config.getOrDefault("follow-distance-min", SharedConfigs.Common.FollowDistanceMin);
        SharedConfigs.Common.LookRange = config.getOrDefault("look-range", SharedConfigs.Common.LookRange);

        // Renderer settings
        SharedConfigs.Client.ShadowRadius = config.getOrDefault("shadow-radius", SharedConfigs.Client.ShadowRadius);

        // Level/Experience settings
        SharedConfigs.Common.ExperienceBase = config.getOrDefault("experience-base", SharedConfigs.Common.ExperienceBase);
        SharedConfigs.Common.ExperienceMultiplier = config.getOrDefault("experience-multiplier", SharedConfigs.Common.ExperienceMultiplier);

        // Combat settings
        SharedConfigs.Common.FriendlyFire = config.getOrDefault("friendly-fire", SharedConfigs.Common.FriendlyFire);
        SharedConfigs.Common.AttackChance = config.getOrDefault("attack-chance", SharedConfigs.Common.AttackChance);
        SharedConfigs.Common.HealInterval = config.getOrDefault("heal-interval", SharedConfigs.Common.HealInterval);
        SharedConfigs.Common.WaryTime = config.getOrDefault("wary-time", SharedConfigs.Common.WaryTime);
        SharedConfigs.Common.GlobalAutoHeal = config.getOrDefault("global-heal", SharedConfigs.Common.GlobalAutoHeal);
        SharedConfigs.Common.LootEnchantment = config.getOrDefault("loot-enchantment", SharedConfigs.Common.LootEnchantment);
        SharedConfigs.Common.LootEnchantmentLevel = config.getOrDefault("loot-enchantment-level", SharedConfigs.Common.LootEnchantmentLevel);
        SharedConfigs.Common.MaxLootEnchantment = config.getOrDefault("max-loot-enchantment", SharedConfigs.Common.MaxLootEnchantment);
        SharedConfigs.Common.BaseDefenceRange = config.getOrDefault("base-defence-range", SharedConfigs.Common.BaseDefenceRange);
        SharedConfigs.Common.BaseDefenceWarpRange = config.getOrDefault("base-defence-warp-range", SharedConfigs.Common.BaseDefenceWarpRange);

        // Protection settings
        SharedConfigs.Common.ProtectionLimitFire = config.getOrDefault("limit-fire", SharedConfigs.Common.ProtectionLimitFire);
        SharedConfigs.Common.ProtectionLimitFall = config.getOrDefault("limit-fall", SharedConfigs.Common.ProtectionLimitFall);
        SharedConfigs.Common.ProtectionLimitBlast = config.getOrDefault("limit-blast", SharedConfigs.Common.ProtectionLimitBlast);
        SharedConfigs.Common.ProtectionLimitProjectile = config.getOrDefault("limit-projectile", SharedConfigs.Common.ProtectionLimitProjectile);
        SharedConfigs.Common.EnableEnchantedBookProtection = config.getOrDefault("enable-enchanted-book-protection", SharedConfigs.Common.EnableEnchantedBookProtection);
        SharedConfigs.Common.EnchantedBookContributionPercentage = config.getOrDefault("enchanted-book-contribution", SharedConfigs.Common.EnchantedBookContributionPercentage);

        // Smart Core Retrieval
        SharedConfigs.Common.EnableSmartCoreRetrieval = config.getOrDefault("enable-smart-core-retrieval", SharedConfigs.Common.EnableSmartCoreRetrieval);
        SharedConfigs.Common.SmartCoreRetrievalDistance = config.getOrDefault("smart-core-retrieval-distance", SharedConfigs.Common.SmartCoreRetrievalDistance);

        // AI Behavior settings
        SharedConfigs.Common.OwnerStillThreshold = config.getOrDefault("owner-still-threshold", SharedConfigs.Common.OwnerStillThreshold);
        SharedConfigs.Common.WanderCheckInterval = config.getOrDefault("wander-check-interval", SharedConfigs.Common.WanderCheckInterval);
        SharedConfigs.Common.WanderChance = config.getOrDefault("wander-chance", SharedConfigs.Common.WanderChance);
        SharedConfigs.Common.WanderRadiusMin = config.getOrDefault("wander-radius-min", SharedConfigs.Common.WanderRadiusMin);
        SharedConfigs.Common.WanderRadiusMax = config.getOrDefault("wander-radius-max", SharedConfigs.Common.WanderRadiusMax);
        SharedConfigs.Common.WanderDurationMin = config.getOrDefault("wander-duration-min", SharedConfigs.Common.WanderDurationMin);
        SharedConfigs.Common.WanderDurationMax = config.getOrDefault("wander-duration-max", SharedConfigs.Common.WanderDurationMax);
        SharedConfigs.Common.WanderCooldownMin = config.getOrDefault("wander-cooldown-min", SharedConfigs.Common.WanderCooldownMin);
        SharedConfigs.Common.WanderCooldownMax = config.getOrDefault("wander-cooldown-max", SharedConfigs.Common.WanderCooldownMax);
        SharedConfigs.Common.PatrolDurationMin = config.getOrDefault("patrol-duration-min", SharedConfigs.Common.PatrolDurationMin);
        SharedConfigs.Common.PatrolDurationMax = config.getOrDefault("patrol-duration-max", SharedConfigs.Common.PatrolDurationMax);
        SharedConfigs.Common.GuardDurationMin = config.getOrDefault("guard-duration-min", SharedConfigs.Common.GuardDurationMin);
        SharedConfigs.Common.GuardDurationMax = config.getOrDefault("guard-duration-max", SharedConfigs.Common.GuardDurationMax);
        SharedConfigs.Common.PatrolPauseDurationMin = config.getOrDefault("patrol-pause-duration-min", SharedConfigs.Common.PatrolPauseDurationMin);
        SharedConfigs.Common.PatrolPauseDurationMax = config.getOrDefault("patrol-pause-duration-max", SharedConfigs.Common.PatrolPauseDurationMax);
        SharedConfigs.Common.GuardRotationSpeed = config.getOrDefault("guard-rotation-speed", SharedConfigs.Common.GuardRotationSpeed);
        SharedConfigs.Common.EnableCombatRadiusParticles = config.getOrDefault("enable-combat-radius-particles", SharedConfigs.Common.EnableCombatRadiusParticles);
        SharedConfigs.Common.CombatRadiusParticleCount = config.getOrDefault("combat-radius-particle-count", SharedConfigs.Common.CombatRadiusParticleCount);
        SharedConfigs.Common.CombatRadiusParticleSpread = config.getOrDefault("combat-radius-particle-spread", SharedConfigs.Common.CombatRadiusParticleSpread);

        // Animation settings
        SharedConfigs.Common.StandbyToSitDelayMin = config.getOrDefault("standby-to-sit-delay-min", SharedConfigs.Common.StandbyToSitDelayMin);
        SharedConfigs.Common.StandbyToSitDelayMax = config.getOrDefault("standby-to-sit-delay-max", SharedConfigs.Common.StandbyToSitDelayMax);

        // Load dynamic entity configurations
        loadDynamicEntityConfigs();
    } // loadConfigValues()

    /**
     * Loads dynamic entity configurations from config file into LegacyConfigs HashMap.
     * <p>
     * <b>Dynamic Loading:</b> Iterates through all robot variants and loads their
     * configuration from the config file. Creates EntityConfigData instances with
     * validation and fallback to defaults.
     * <p>
     * <b>Error Handling:</b> Invalid configurations fall back to defaults with
     * warning logs. Missing configurations use SharedConfigs defaults.
     * <p>
     * <b>Runtime Updates:</b> Called during config reload to update entity stats
     * immediately without requiring restart.
     */
    private static void loadDynamicEntityConfigs() {
        net.heriazone.lovelylib.source.legacy.LegacyConfigs.Entities = new HashMap<>();
        //LegacyConfigs.Entities.clear();

        //Legacy.LOGGER.warn("Loading variants '{}'", (long) LegacyRobotType.TYPES.stream().map(InternalEntityType::getKey).toList().size());
        //for (String variant : LegacyRobotType.TYPES.stream().map(InternalEntityType::getKey).toList())
        for (String variant : LovelyConstant.LEGACY_VARIANTS) {

            try {
                // Create and validate EntityConfigData
                SharedConfigs.EntityConfigData entityConfig = new SharedConfigs.EntityConfigData.Builder()
                        .maxLevel(config.getOrDefault(variant + "-" + LovelyConstant.CONFIG_MAX_LEVEL, getDefaultMaxLevel(variant)))
                        .attackSpeed(config.getOrDefault(variant + "-" + LovelyConstant.CONFIG_ATTACK_SPEED, getDefaultAttackSpeed(variant)))
                        .movementSpeed(config.getOrDefault(variant + "-" + LovelyConstant.CONFIG_MOVEMENT_SPEED, getDefaultMovementSpeed(variant)))
                        .baseToughness(config.getOrDefault(variant + "-" + LovelyConstant.CONFIG_BASE_TOUGHNESS, getDefaultBaseToughness(variant)))
                        .baseHp(config.getOrDefault(variant + "-" + LovelyConstant.CONFIG_BASE_HP, getDefaultBaseHp(variant)))
                        .baseAttack(config.getOrDefault(variant + "-" + LovelyConstant.CONFIG_BASE_ATTACK, getDefaultBaseAttack(variant)))
                        .baseDefense(config.getOrDefault(variant + "-" + LovelyConstant.CONFIG_BASE_DEFENSE, getDefaultBaseDefense(variant)))
                        .build();
                // Validate and store
                net.heriazone.lovelylib.source.legacy.LegacyConfigs.Entities.put(variant, entityConfig.validateOrDefault());
            } catch (Exception e) {
                Legacy.LOGGER.warn("Failed to load config for variant '{}', using defaults: {}", variant, e.getMessage());
                net.heriazone.lovelylib.source.legacy.LegacyConfigs.Entities.put(variant, getDefaultEntityConfig(variant));
            }
        }
        
        Legacy.LOGGER.info("Loaded dynamic entity configurations for {} variants", net.heriazone.lovelylib.source.legacy.LegacyConfigs.Entities.size());
    } // loadDynamicEntityConfigs()

    /**
     * Gets default configuration for a specific robot variant.
     * <p>
     * <b>Fallback System:</b> Provides default EntityConfigData when config loading
     * fails or variant is not found. Uses SharedConfigs individual values as source.
     * 
     * @param variant the robot variant identifier
     * @return default EntityConfigData for the variant
     */
    private static SharedConfigs.EntityConfigData getDefaultEntityConfig(String variant) {
        return net.heriazone.lovelylib.source.legacy.LegacyConfigs.getDefaultConfig(variant);
    } // getDefaultEntityConfig()

    // -- Default Value Getters --

    private static int getDefaultMaxLevel(String variant) {
        return net.heriazone.lovelylib.source.legacy.LegacyConfigs.getDefaultConfig(variant).maxLevel;
    } // getDefaultMaxLevel()

    private static float getDefaultAttackSpeed(String variant) {
        return net.heriazone.lovelylib.source.legacy.LegacyConfigs.getDefaultConfig(variant).attackSpeed;
    } // getDefaultAttackSpeed()

    private static float getDefaultMovementSpeed(String variant) {
        return net.heriazone.lovelylib.source.legacy.LegacyConfigs.getDefaultConfig(variant).movementSpeed;
    } // getDefaultMovementSpeed()

    private static float getDefaultBaseToughness(String variant) {
        return net.heriazone.lovelylib.source.legacy.LegacyConfigs.getDefaultConfig(variant).baseToughness;
    } // getDefaultBaseToughness()

    private static int getDefaultBaseHp(String variant) {
        return net.heriazone.lovelylib.source.legacy.LegacyConfigs.getDefaultConfig(variant).baseHp;
    } // getDefaultBaseHp()

    private static int getDefaultBaseAttack(String variant) {
        return net.heriazone.lovelylib.source.legacy.LegacyConfigs.getDefaultConfig(variant).baseAttack;
    } // getDefaultBaseAttack()

    private static int getDefaultBaseDefense(String variant) {
        return net.heriazone.lovelylib.source.legacy.LegacyConfigs.getDefaultConfig(variant).baseDefense;
    } // getDefaultBaseDefense()

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
        Legacy.LOGGER.info("Starting configuration reload from disk...");

        // Store reference to current config for rollback
        SimpleConfig previousConfig = config;

        try {
            // Re-read config file by creating new SimpleConfig instance
            ConfigProvider provider = new ConfigProvider();
            buildConfigProvider(provider);

            SimpleConfig newConfig = SimpleConfig.of(CONFIG_FILE_NAME).provider(provider).request();

            // Check if new config loaded successfully
            if (newConfig.isBroken()) {
                Legacy.LOGGER.error("Config file is broken or failed to parse, preserving existing configuration values");
                return;
            }

            // Update the config reference
            config = newConfig;

            // Load all values from the new config
            loadConfigValues();

            Legacy.LOGGER.info("Configuration reload completed successfully");
        } catch (Exception e) {
            // Restore previous config on any error
            config = previousConfig;
            Legacy.LOGGER.error("Failed to reload configuration due to unexpected error, preserving existing configuration values: " + e.getMessage(), e);
        }
    } // reload()

} // Class: LegacyConfigs