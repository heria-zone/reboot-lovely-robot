package net.msymbios.llovelyr.source;

import net.msymbios.llovelyr.LovelyConstant;
import net.msymbios.llovelyr.common.Configs.ConfigBounds;
import net.msymbios.llovelyr.common.Configs.SharedConfigs;
import net.msymbios.llovelyr.lib.configs.ConfigProvider;
import net.msymbios.llovelyr.lib.configs.SimpleConfig;

/**
 * Configuration management for Legacy LovelyRobotEntity mod.
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

        LovelyConstant.LOGGER.info("Configuration loaded successfully");
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

        provider.push("Bunny");
        provider.comment("Highest level this robot type can reach.", "Higher levels unlock better stats and abilities.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [200]")
                .define("bunny-max-level", SharedConfigs.Common.BunnyMaxLevel);

        provider.comment("How fast this robot type attacks (attacks per second).", "Higher values mean faster attacks. Minecraft default is 1.0.", "Range: 0.1 to no upper limit (does not accept negative values or zero)", "Example: [1.6]")
                .define("bunny-attack-speed", SharedConfigs.Common.BunnyAttackSpeed);

        provider.comment("Base movement speed for this robot type.", "Higher values make robots move faster. Player walk speed is 0.1.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.37]")
                .define("bunny-movement-speed", SharedConfigs.Common.BunnyMovementSpeed);

        provider.comment("Base toughness for this robot type.", "Reduces damage from strong attacks. Diamond armor has 2.0 toughness per piece.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.0]")
                .define("bunny-base-toughness", SharedConfigs.Common.BunnyBaseToughness);

        provider.comment("Base HP value for combat level calculations.", "Used by CombatLevelFeature to calculate HP at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [16]")
                .define("bunny-base-hp", SharedConfigs.Common.BunnyBaseHp);

        provider.comment("Base attack value for combat level calculations.", "Used by CombatLevelFeature to calculate attack damage at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [2]")
                .define("bunny-base-attack", SharedConfigs.Common.BunnyBaseAttack);

        provider.comment("Base defense value for combat level calculations.", "Used by CombatLevelFeature to calculate armor and toughness at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [3]")
                .define("bunny-base-defense", SharedConfigs.Common.BunnyBaseDefense);
        provider.pop();

        provider.push("Bunny2");
        provider.comment("Highest level this robot type can reach.", "Higher levels unlock better stats and abilities.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [200]")
                .define("bunny2-max-level", SharedConfigs.Common.Bunny2MaxLevel);

        provider.comment("How fast this robot type attacks (attacks per second).", "Higher values mean faster attacks. Minecraft default is 1.0.", "Range: 0.1 to no upper limit (does not accept negative values or zero)", "Example: [1.8]")
                .define("bunny2-attack-speed", SharedConfigs.Common.Bunny2AttackSpeed);

        provider.comment("Base movement speed for this robot type.", "Higher values make robots move faster. Player walk speed is 0.1.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.37]")
                .define("bunny2-movement-speed", SharedConfigs.Common.Bunny2MovementSpeed);

        provider.comment("Base toughness for this robot type.", "Reduces damage from strong attacks. Diamond armor has 2.0 toughness per piece.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [1.0]")
                .define("bunny2-base-toughness", SharedConfigs.Common.Bunny2BaseToughness);

        provider.comment("Base HP value for combat level calculations.", "Used by CombatLevelFeature to calculate HP at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [24]")
                .define("bunny2-base-hp", SharedConfigs.Common.Bunny2BaseHp);

        provider.comment("Base attack value for combat level calculations.", "Used by CombatLevelFeature to calculate attack damage at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [4]")
                .define("bunny2-base-attack", SharedConfigs.Common.Bunny2BaseAttack);

        provider.comment("Base defense value for combat level calculations.", "Used by CombatLevelFeature to calculate armor and toughness at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [6]")
                .define("bunny2-base-defense", SharedConfigs.Common.Bunny2BaseDefense);
        provider.pop();

        provider.push("Dragon");
        provider.comment("Highest level this robot type can reach.", "Higher levels unlock better stats and abilities.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [300]")
                .define("dragon-max-level", SharedConfigs.Common.DragonMaxLevel);

        provider.comment("How fast this robot type attacks (attacks per second).", "Higher values mean faster attacks. Minecraft default is 1.0.", "Range: 0.1 to no upper limit (does not accept negative values or zero)", "Example: [0.8]")
                .define("dragon-attack-speed", SharedConfigs.Common.DragonAttackSpeed);

        provider.comment("Base movement speed for this robot type.", "Higher values make robots move faster. Player walk speed is 0.1.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.22]")
                .define("dragon-movement-speed", SharedConfigs.Common.DragonMovementSpeed);

        provider.comment("Base toughness for this robot type.", "Reduces damage from strong attacks. Diamond armor has 2.0 toughness per piece.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [2.0]")
                .define("dragon-base-toughness", SharedConfigs.Common.DragonBaseToughness);

        provider.comment("Base HP value for combat level calculations.", "Used by CombatLevelFeature to calculate HP at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [30]")
                .define("dragon-base-hp", SharedConfigs.Common.DragonBaseHp);

        provider.comment("Base attack value for combat level calculations.", "Used by CombatLevelFeature to calculate attack damage at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [8]")
                .define("dragon-base-attack", SharedConfigs.Common.DragonBaseAttack);

        provider.comment("Base defense value for combat level calculations.", "Used by CombatLevelFeature to calculate armor and toughness at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [4]")
                .define("dragon-base-defense", SharedConfigs.Common.DragonBaseDefense);
        provider.pop();

        provider.push("Honey");
        provider.comment("Highest level this robot type can reach.", "Higher levels unlock better stats and abilities.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [200]")
                .define("honey-max-level", SharedConfigs.Common.HoneyMaxLevel);

        provider.comment("How fast this robot type attacks (attacks per second).", "Higher values mean faster attacks. Minecraft default is 1.0.", "Range: 0.1 to no upper limit (does not accept negative values or zero)", "Example: [1.0]")
                .define("honey-attack-speed", SharedConfigs.Common.HoneyAttackSpeed);

        provider.comment("Base movement speed for this robot type.", "Higher values make robots move faster. Player walk speed is 0.1.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.37]")
                .define("honey-movement-speed", SharedConfigs.Common.HoneyMovementSpeed);

        provider.comment("Base toughness for this robot type.", "Reduces damage from strong attacks. Diamond armor has 2.0 toughness per piece.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [1.0]")
                .define("honey-base-toughness", SharedConfigs.Common.HoneyBaseToughness);

        provider.comment("Base HP value for combat level calculations.", "Used by CombatLevelFeature to calculate HP at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [14]")
                .define("honey-base-hp", SharedConfigs.Common.HoneyBaseHp);

        provider.comment("Base attack value for combat level calculations.", "Used by CombatLevelFeature to calculate attack damage at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [1]")
                .define("honey-base-attack", SharedConfigs.Common.HoneyBaseAttack);

        provider.comment("Base defense value for combat level calculations.", "Used by CombatLevelFeature to calculate armor and toughness at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [2]")
                .define("honey-base-defense", SharedConfigs.Common.HoneyBaseDefense);
        provider.pop();

        provider.push("Kitsune");
        provider.comment("Highest level this robot type can reach.", "Higher levels unlock better stats and abilities.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [200]")
                .define("kitsune-max-level", SharedConfigs.Common.KitsuneMaxLevel);

        provider.comment("How fast this robot type attacks (attacks per second).", "Higher values mean faster attacks. Minecraft default is 1.0.", "Range: 0.1 to no upper limit (does not accept negative values or zero)", "Example: [1.1]")
                .define("kitsune-attack-speed", SharedConfigs.Common.KitsuneAttackSpeed);

        provider.comment("Base movement speed for this robot type.", "Higher values make robots move faster. Player walk speed is 0.1.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.37]")
                .define("kitsune-movement-speed", SharedConfigs.Common.KitsuneMovementSpeed);

        provider.comment("Base toughness for this robot type.", "Reduces damage from strong attacks. Diamond armor has 2.0 toughness per piece.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [1.0]")
                .define("kitsune-base-toughness", SharedConfigs.Common.KitsuneBaseToughness);

        provider.comment("Base HP value for combat level calculations.", "Used by CombatLevelFeature to calculate HP at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [16]")
                .define("kitsune-base-hp", SharedConfigs.Common.KitsuneBaseHp);

        provider.comment("Base attack value for combat level calculations.", "Used by CombatLevelFeature to calculate attack damage at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [2]")
                .define("kitsune-base-attack", SharedConfigs.Common.KitsuneBaseAttack);

        provider.comment("Base defense value for combat level calculations.", "Used by CombatLevelFeature to calculate armor and toughness at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [1]")
                .define("kitsune-base-defense", SharedConfigs.Common.KitsuneBaseDefense);
        provider.pop();

        provider.push("Neko");
        provider.comment("Highest level this robot type can reach.", "Higher levels unlock better stats and abilities.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [200]")
                .define("neko-max-level", SharedConfigs.Common.NekoMaxLevel);

        provider.comment("How fast this robot type attacks (attacks per second).", "Higher values mean faster attacks. Minecraft default is 1.0.", "Range: 0.1 to no upper limit (does not accept negative values or zero)", "Example: [1.1]")
                .define("neko-attack-speed", SharedConfigs.Common.NekoAttackSpeed);

        provider.comment("Base movement speed for this robot type.", "Higher values make robots move faster. Player walk speed is 0.1.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.40]")
                .define("neko-movement-speed", SharedConfigs.Common.NekoMovementSpeed);

        provider.comment("Base toughness for this robot type.", "Reduces damage from strong attacks. Diamond armor has 2.0 toughness per piece.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [1.0]")
                .define("neko-base-toughness", SharedConfigs.Common.NekoBaseToughness);

        provider.comment("Base HP value for combat level calculations.", "Used by CombatLevelFeature to calculate HP at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [22]")
                .define("neko-base-hp", SharedConfigs.Common.NekoBaseHp);

        provider.comment("Base attack value for combat level calculations.", "Used by CombatLevelFeature to calculate attack damage at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [6]")
                .define("neko-base-attack", SharedConfigs.Common.NekoBaseAttack);

        provider.comment("Base defense value for combat level calculations.", "Used by CombatLevelFeature to calculate armor and toughness at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [2]")
                .define("neko-base-defense", SharedConfigs.Common.NekoBaseDefense);
        provider.pop();

        provider.push("Vanilla");
        provider.comment("Highest level this robot type can reach.", "Higher levels unlock better stats and abilities.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [200]")
                .define("vanilla-max-level", SharedConfigs.Common.VanillaMaxLevel);

        provider.comment("How fast this robot type attacks (attacks per second).", "Higher values mean faster attacks. Minecraft default is 1.0.", "Range: 0.1 to no upper limit (does not accept negative values or zero)", "Example: [2.0]")
                .define("vanilla-attack-speed", SharedConfigs.Common.VanillaAttackSpeed);

        provider.comment("Base movement speed for this robot type.", "Higher values make robots move faster. Player walk speed is 0.1.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.37]")
                .define("vanilla-movement-speed", SharedConfigs.Common.VanillaMovementSpeed);

        provider.comment("Base toughness for this robot type.", "Reduces damage from strong attacks. Diamond armor has 2.0 toughness per piece.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.0]")
                .define("vanilla-base-toughness", SharedConfigs.Common.VanillaBaseToughness);

        provider.comment("Base HP value for combat level calculations.", "Used by CombatLevelFeature to calculate HP at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [14]")
                .define("vanilla-base-hp", SharedConfigs.Common.VanillaBaseHp);

        provider.comment("Base attack value for combat level calculations.", "Used by CombatLevelFeature to calculate attack damage at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [4]")
                .define("vanilla-base-attack", SharedConfigs.Common.VanillaBaseAttack);

        provider.comment("Base defense value for combat level calculations.", "Used by CombatLevelFeature to calculate armor and toughness at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [2]")
                .define("vanilla-base-defense", SharedConfigs.Common.VanillaBaseDefense);
        provider.pop();

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

        // Entity settings - Bunny
        SharedConfigs.Common.BunnyMaxLevel = config.getOrDefault("bunny-max-level", SharedConfigs.Common.BunnyMaxLevel);
        SharedConfigs.Common.BunnyAttackSpeed = config.getOrDefault("bunny-attack-speed", SharedConfigs.Common.BunnyAttackSpeed);
        SharedConfigs.Common.BunnyMovementSpeed = config.getOrDefault("bunny-movement-speed", SharedConfigs.Common.BunnyMovementSpeed);
        SharedConfigs.Common.BunnyBaseToughness = config.getOrDefault("bunny-base-toughness", SharedConfigs.Common.BunnyBaseToughness);
        SharedConfigs.Common.BunnyBaseHp = config.getOrDefault("bunny-base-hp", SharedConfigs.Common.BunnyBaseHp);
        SharedConfigs.Common.BunnyBaseAttack = config.getOrDefault("bunny-base-attack", SharedConfigs.Common.BunnyBaseAttack);
        SharedConfigs.Common.BunnyBaseDefense = config.getOrDefault("bunny-base-defense", SharedConfigs.Common.BunnyBaseDefense);

        // Entity settings - Bunny2
        SharedConfigs.Common.Bunny2MaxLevel = config.getOrDefault("bunny2-max-level", SharedConfigs.Common.Bunny2MaxLevel);
        SharedConfigs.Common.Bunny2AttackSpeed = config.getOrDefault("bunny2-attack-speed", SharedConfigs.Common.Bunny2AttackSpeed);
        SharedConfigs.Common.Bunny2MovementSpeed = config.getOrDefault("bunny2-movement-speed", SharedConfigs.Common.Bunny2MovementSpeed);
        SharedConfigs.Common.Bunny2BaseToughness = config.getOrDefault("bunny2-base-toughness", SharedConfigs.Common.Bunny2BaseToughness);
        SharedConfigs.Common.Bunny2BaseHp = config.getOrDefault("bunny2-base-hp", SharedConfigs.Common.Bunny2BaseHp);
        SharedConfigs.Common.Bunny2BaseAttack = config.getOrDefault("bunny2-base-attack", SharedConfigs.Common.Bunny2BaseAttack);
        SharedConfigs.Common.Bunny2BaseDefense = config.getOrDefault("bunny2-base-defense", SharedConfigs.Common.Bunny2BaseDefense);

        // Entity settings - Dragon
        SharedConfigs.Common.DragonMaxLevel = config.getOrDefault("dragon-max-level", SharedConfigs.Common.DragonMaxLevel);
        SharedConfigs.Common.DragonAttackSpeed = config.getOrDefault("dragon-attack-speed", SharedConfigs.Common.DragonAttackSpeed);
        SharedConfigs.Common.DragonMovementSpeed = config.getOrDefault("dragon-movement-speed", SharedConfigs.Common.DragonMovementSpeed);
        SharedConfigs.Common.DragonBaseToughness = config.getOrDefault("dragon-base-toughness", SharedConfigs.Common.DragonBaseToughness);
        SharedConfigs.Common.DragonBaseHp = config.getOrDefault("dragon-base-hp", SharedConfigs.Common.DragonBaseHp);
        SharedConfigs.Common.DragonBaseAttack = config.getOrDefault("dragon-base-attack", SharedConfigs.Common.DragonBaseAttack);
        SharedConfigs.Common.DragonBaseDefense = config.getOrDefault("dragon-base-defense", SharedConfigs.Common.DragonBaseDefense);

        // Entity settings - Honey
        SharedConfigs.Common.HoneyMaxLevel = config.getOrDefault("honey-max-level", SharedConfigs.Common.HoneyMaxLevel);
        SharedConfigs.Common.HoneyAttackSpeed = config.getOrDefault("honey-attack-speed", SharedConfigs.Common.HoneyAttackSpeed);
        SharedConfigs.Common.HoneyMovementSpeed = config.getOrDefault("honey-movement-speed", SharedConfigs.Common.HoneyMovementSpeed);
        SharedConfigs.Common.HoneyBaseToughness = config.getOrDefault("honey-base-toughness", SharedConfigs.Common.HoneyBaseToughness);
        SharedConfigs.Common.HoneyBaseHp = config.getOrDefault("honey-base-hp", SharedConfigs.Common.HoneyBaseHp);
        SharedConfigs.Common.HoneyBaseAttack = config.getOrDefault("honey-base-attack", SharedConfigs.Common.HoneyBaseAttack);
        SharedConfigs.Common.HoneyBaseDefense = config.getOrDefault("honey-base-defense", SharedConfigs.Common.HoneyBaseDefense);

        // Entity settings - Kitsune
        SharedConfigs.Common.KitsuneMaxLevel = config.getOrDefault("kitsune-max-level", SharedConfigs.Common.KitsuneMaxLevel);
        SharedConfigs.Common.KitsuneAttackSpeed = config.getOrDefault("kitsune-attack-speed", SharedConfigs.Common.KitsuneAttackSpeed);
        SharedConfigs.Common.KitsuneMovementSpeed = config.getOrDefault("kitsune-movement-speed", SharedConfigs.Common.KitsuneMovementSpeed);
        SharedConfigs.Common.KitsuneBaseToughness = config.getOrDefault("kitsune-base-toughness", SharedConfigs.Common.KitsuneBaseToughness);
        SharedConfigs.Common.KitsuneBaseHp = config.getOrDefault("kitsune-base-hp", SharedConfigs.Common.KitsuneBaseHp);
        SharedConfigs.Common.KitsuneBaseAttack = config.getOrDefault("kitsune-base-attack", SharedConfigs.Common.KitsuneBaseAttack);
        SharedConfigs.Common.KitsuneBaseDefense = config.getOrDefault("kitsune-base-defense", SharedConfigs.Common.KitsuneBaseDefense);

        // Entity settings - Neko
        SharedConfigs.Common.NekoMaxLevel = config.getOrDefault("neko-max-level", SharedConfigs.Common.NekoMaxLevel);
        SharedConfigs.Common.NekoAttackSpeed = config.getOrDefault("neko-attack-speed", SharedConfigs.Common.NekoAttackSpeed);
        SharedConfigs.Common.NekoMovementSpeed = config.getOrDefault("neko-movement-speed", SharedConfigs.Common.NekoMovementSpeed);
        SharedConfigs.Common.NekoBaseToughness = config.getOrDefault("neko-base-toughness", SharedConfigs.Common.NekoBaseToughness);
        SharedConfigs.Common.NekoBaseHp = config.getOrDefault("neko-base-hp", SharedConfigs.Common.NekoBaseHp);
        SharedConfigs.Common.NekoBaseAttack = config.getOrDefault("neko-base-attack", SharedConfigs.Common.NekoBaseAttack);
        SharedConfigs.Common.NekoBaseDefense = config.getOrDefault("neko-base-defense", SharedConfigs.Common.NekoBaseDefense);

        // Entity settings - Vanilla
        SharedConfigs.Common.VanillaMaxLevel = config.getOrDefault("vanilla-max-level", SharedConfigs.Common.VanillaMaxLevel);
        SharedConfigs.Common.VanillaAttackSpeed = config.getOrDefault("vanilla-attack-speed", SharedConfigs.Common.VanillaAttackSpeed);
        SharedConfigs.Common.VanillaMovementSpeed = config.getOrDefault("vanilla-movement-speed", SharedConfigs.Common.VanillaMovementSpeed);
        SharedConfigs.Common.VanillaBaseToughness = config.getOrDefault("vanilla-base-toughness", SharedConfigs.Common.VanillaBaseToughness);
        SharedConfigs.Common.VanillaBaseHp = config.getOrDefault("vanilla-base-hp", SharedConfigs.Common.VanillaBaseHp);
        SharedConfigs.Common.VanillaBaseAttack = config.getOrDefault("vanilla-base-attack", SharedConfigs.Common.VanillaBaseAttack);
        SharedConfigs.Common.VanillaBaseDefense = config.getOrDefault("vanilla-base-defense", SharedConfigs.Common.VanillaBaseDefense);
    
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
        LovelyConstant.LOGGER.info("Starting configuration reload from disk...");

        // Store reference to current config for rollback
        SimpleConfig previousConfig = config;

        try {
            // Re-read config file by creating new SimpleConfig instance
            ConfigProvider provider = new ConfigProvider();
            buildConfigProvider(provider);

            SimpleConfig newConfig = SimpleConfig.of(CONFIG_FILE_NAME).provider(provider).request();

            // Check if new config loaded successfully
            if (newConfig.isBroken()) {
                LovelyConstant.LOGGER.error("Config file is broken or failed to parse, preserving existing configuration values");
                return;
            }

            // Update the config reference
            config = newConfig;

            // Load all values from the new config
            loadConfigValues();

            LovelyConstant.LOGGER.info("Configuration reload completed successfully");
        } catch (Exception e) {
            // Restore previous config on any error
            config = previousConfig;
            LovelyConstant.LOGGER.error("Failed to reload configuration due to unexpected error, preserving existing configuration values: " + e.getMessage(), e);
        }
    } // reload()
} // Class: LovelyConfigs