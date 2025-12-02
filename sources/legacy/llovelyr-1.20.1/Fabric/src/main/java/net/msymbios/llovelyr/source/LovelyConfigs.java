package net.msymbios.llovelyr.source;

import net.msymbios.llovelyr.LovelyLegacy;
import net.msymbios.llovelyr.config.internal.SimpleConfig;
import net.msymbios.llovelyr.config.internal.ConfigProvider;

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
        // General Settings
        provider.push("General");
        provider.comment("How many robots each player can own at once.", "Set to -1 for unlimited robots.", "Range: -1 to no upper limit", "Example: [30]")
                .define("owner-max-robot", Common.OwnerMaxRobotNum);
        
        provider.comment("How fast robots move when attacking enemies in melee combat.", "Higher values make robots move faster during attacks.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.8]")
                .define("movement-melee-attack", Common.MovementMeleeAttack);
        
        provider.comment("How fast robots move when following their owner.", "Higher values make robots keep up with you better.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.7]")
                .define("movement-follow-owner", Common.MovementFollowOwner);
        
        provider.comment("How fast robots move when wandering around on their own.", "Lower than follow speed to make wandering look more relaxed.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.6]")
                .define("movement-wander-around", Common.MovementWanderAround);
        
        provider.comment("Maximum distance (in blocks) robots will stay from their owner before teleporting.", "If you get too far, your robot will teleport to you.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [10.0]")
                .define("follow-distance-max", Common.FollowDistanceMax);
        
        provider.comment("Minimum distance (in blocks) robots try to maintain from their owner.", "Robots won't crowd you closer than this distance.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [2.0]")
                .define("follow-distance-min", Common.FollowDistanceMin);
        
        provider.comment("How far (in blocks) robots can look at and track entities.", "Affects head rotation and attention behavior.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [8.0]")
                .define("look-range", Common.LookRange);
        provider.pop();
        
        provider.push("Renderer");
        provider.comment("Width of the robot's collision box (in blocks).", "Affects how much space robots take up and what gaps they can fit through.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.4]")
                .define("width", Common.Width);
        
        provider.comment("Height of the robot's collision box (in blocks).", "Affects what spaces robots can fit under.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [1.9]")
                .define("height", Common.Height);
        
        provider.comment("Size of the shadow rendered under robots.", "Purely visual - doesn't affect gameplay.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.4]")
                .define("shadow-radius", Client.ShadowRadius);
        provider.pop();
        
        provider.push("Level & Experience");
        provider.comment("Base experience points needed for a robot to reach level 1.", "Each level requires more XP based on the multiplier below.", "Range: 0 to no upper limit (does not accept negative values)", "Example: [50]")
                .define("experience-base", Common.ExperienceBase);
        
        provider.comment("How much more XP each level requires compared to the previous level.", "Level 2 needs base × multiplier, Level 3 needs base × multiplier², etc.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [2]")
                .define("experience-multiplier", Common.ExperienceMultiplier);
        provider.pop();
        
        provider.push("Combat");
        provider.comment("Whether players can damage their own robots.", "Set to true to allow accidentally hitting your robots, false to prevent it.", "Example: [false]")
                .define("friendly-fire", Common.FriendlyFire);
        
        provider.comment("How likely robots are to counter-attack when hit (higher = more aggressive).", "Affects how quickly robots retaliate when damaged.", "Range: 0 to no upper limit (does not accept negative values)", "Example: [5]")
                .define("attack-chance", Common.AttackChance);
        
        provider.comment("How often (in ticks) robots automatically heal themselves.", "20 ticks = 1 second. Lower values = faster healing.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [50]")
                .define("heal-interval", Common.HealInterval);
        
        provider.comment("How long (in ticks) robots stay alert after combat ends.", "20 ticks = 1 second. During this time, robots remain ready to fight.", "Range: 0 to no upper limit (does not accept negative values)", "Example: [50]")
                .define("wary-time", Common.WaryTime);
        
        provider.comment("Whether robots automatically heal over time.", "Set to false to disable automatic healing entirely.", "Example: [true]")
                .define("global-heal", Common.GlobalAutoHeal);
        
        provider.comment("Whether robots can benefit from Looting enchantment on their weapon.", "When enabled, higher level robots get better mob drops.", "Example: [true]")
                .define("loot-enchantment", Common.LootEnchantment);
        
        provider.comment("What robot level is needed to gain Looting I enchantment effect.", "Looting II at 2× this level, Looting III at 3× this level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [10]")
                .define("loot-enchantment-level", Common.LootEnchantmentLevel);
        
        provider.comment("Maximum Looting enchantment level robots can have.", "Limits how much bonus loot high-level robots can get.", "Range: 0 to 3", "Example: [3]")
                .define("max-loot-enchantment", Common.MaxLootEnchantment);
        
        provider.comment("How far (in blocks) robots will chase enemies from their guard position.", "In Defense mode, robots won't chase beyond this distance.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [15.0]")
                .define("base-defence-range", Common.BaseDefenceRange);
        
        provider.comment("How far (in blocks) robots can be from guard position before teleporting back.", "Prevents robots from getting stuck too far from their post.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [10.0]")
                .define("base-defence-warp-range", Common.BaseDefenceWarpRange);
        provider.pop();
        
        provider.push("Protection");
        provider.comment("Maximum percentage of fire damage robots can resist.", "At 80%, robots take only 20% of fire damage. At 100%, they're immune to fire.", "Range: 0 to 100", "Example: [80]")
                .define("limit-fire", Common.ProtectionLimitFire);
        
        provider.comment("Maximum percentage of fall damage robots can resist.", "At 80%, robots take only 20% of fall damage. At 100%, they never take fall damage.", "Range: 0 to 100", "Example: [80]")
                .define("limit-fall", Common.ProtectionLimitFall);
        
        provider.comment("Maximum percentage of explosion damage robots can resist.", "At 80%, robots take only 20% of explosion damage. At 100%, they're immune to explosions.", "Range: 0 to 100", "Example: [80]")
                .define("limit-blast", Common.ProtectionLimitBlast);
        
        provider.comment("Maximum percentage of projectile damage robots can resist.", "At 80%, robots take only 20% of arrow/projectile damage. At 100%, they're immune.", "Range: 0 to 100", "Example: [80]")
                .define("limit-projectile", Common.ProtectionLimitProjectile);
        provider.pop();
        
        provider.push("Smart Core Retrieval");
        provider.comment("Whether robot cores automatically go to your inventory when robots die nearby.", "When enabled, you don't need to pick up cores manually if you're close enough.", "Example: [true]")
                .define("enable-smart-core-retrieval", Common.EnableSmartCoreRetrieval);
        
        provider.comment("How close (in blocks) you need to be for automatic core retrieval to work.", "If your robot dies within this distance, the core goes straight to your inventory.", "Range: 0.0 to 128.0", "Example: [16.0]")
                .define("smart-core-retrieval-distance", Common.SmartCoreRetrievalDistance);
        provider.pop();
        
        provider.push("AI Behavior");
        
        provider.push("Follow Mode");
        provider.comment("How long (in ticks) their owner must stand still before robots start wandering.", "20 ticks = 1 second. Robots stay put if you keep moving.", "Range: 0 to 6000", "Example: [100] (5 seconds)")
                .define("owner-still-threshold", Common.OwnerStillThreshold);
        
        provider.comment("How often (in ticks) robots check if they should wander.", "20 ticks = 1 second. Lower values make robots more responsive.", "Range: 100 to 6000", "Example: [200] (10 seconds)")
                .define("wander-check-interval", Common.WanderCheckInterval);
        
        provider.comment("Chance robots will wander when their owner is standing still.", "0.15 = 15% chance per check. Higher values make robots wander more often.", "Range: 0.0 to 1.0", "Example: [0.15] (15%)")
                .define("wander-chance", Common.WanderChance);
        
        provider.comment("Minimum distance (in blocks) robots will wander from their owner.", "Robots won't wander closer than this.", "Range: 1.0 to 32.0", "Example: [3.0]")
                .define("wander-radius-min", Common.WanderRadiusMin);
        
        provider.comment("Maximum distance (in blocks) robots will wander from their owner.", "Robots won't wander farther than this.", "Range: 1.0 to 32.0", "Example: [6.0]")
                .define("wander-radius-max", Common.WanderRadiusMax);
        
        provider.comment("Minimum time (in ticks) robots will wander before returning.", "20 ticks = 1 second. Shorter wanders feel more cautious.", "Range: 20 to 6000", "Example: [100] (5 seconds)")
                .define("wander-duration-min", Common.WanderDurationMin);
        
        provider.comment("Maximum time (in ticks) robots will wander before returning.", "20 ticks = 1 second. Longer wanders make robots more independent.", "Range: 20 to 6000", "Example: [200] (10 seconds)")
                .define("wander-duration-max", Common.WanderDurationMax);
        
        provider.comment("Minimum time (in ticks) before robots can wander again.", "20 ticks = 1 second. Prevents constant wandering.", "Range: 100 to 12000", "Example: [400] (20 seconds)")
                .define("wander-cooldown-min", Common.WanderCooldownMin);
        
        provider.comment("Maximum time (in ticks) before robots can wander again.", "20 ticks = 1 second. Adds variety to wandering behavior.", "Range: 100 to 12000", "Example: [800] (40 seconds)")
                .define("wander-cooldown-max", Common.WanderCooldownMax);
        provider.pop();
        
        provider.push("Defense Mode");
        provider.comment("Minimum time (in ticks) robots patrol around their guard position.", "20 ticks = 1 second. In Defense mode, robots walk around looking for threats.", "Range: 100 to 6000", "Example: [600] (30 seconds)")
                .define("patrol-duration-min", Common.PatrolDurationMin);
        
        provider.comment("Maximum time (in ticks) robots patrol around their guard position.", "20 ticks = 1 second. Longer patrols make robots cover more area.", "Range: 100 to 6000", "Example: [900] (45 seconds)")
                .define("patrol-duration-max", Common.PatrolDurationMax);
        
        provider.comment("Minimum time (in ticks) robots stand guard and look around.", "20 ticks = 1 second. In Defense mode, robots alternate between patrolling and guarding.", "Range: 100 to 6000", "Example: [400] (20 seconds)")
                .define("guard-duration-min", Common.GuardDurationMin);
        
        provider.comment("Maximum time (in ticks) robots stand guard and look around.", "20 ticks = 1 second. Longer guard times make robots more watchful.", "Range: 100 to 6000", "Example: [600] (30 seconds)")
                .define("guard-duration-max", Common.GuardDurationMax);
        
        provider.comment("Minimum time (in ticks) robots pause at each patrol point.", "20 ticks = 1 second. Brief pauses make patrolling look more natural.", "Range: 10 to 600", "Example: [40] (2 seconds)")
                .define("patrol-pause-duration-min", Common.PatrolPauseDurationMin);
        
        provider.comment("Maximum time (in ticks) robots pause at each patrol point.", "20 ticks = 1 second. Longer pauses make robots more observant.", "Range: 10 to 600", "Example: [80] (4 seconds)")
                .define("patrol-pause-duration-max", Common.PatrolPauseDurationMax);
        
        provider.comment("How fast robots rotate their head while guarding.", "Higher values make robots scan faster. Lower values look more deliberate.", "Range: 0.01 to 0.5", "Example: [0.05]")
                .define("guard-rotation-speed", Common.GuardRotationSpeed);
        provider.pop();
        
        provider.push("Combat Radius");
        provider.comment("Whether to show smoke particles when robots can't chase enemies further.", "Visual feedback when robots hit their chase distance limit in Defense mode.", "Example: [true]")
                .define("enable-combat-radius-particles", Common.EnableCombatRadiusParticles);
        
        provider.comment("How many smoke particles appear when robots hit chase limit.", "More particles make the effect more visible.", "Range: 1 to 50", "Example: [8]")
                .define("combat-radius-particle-count", Common.CombatRadiusParticleCount);
        
        provider.comment("How spread out the smoke particles are.", "Higher values create a wider particle cloud.", "Range: 0.1 to 2.0", "Example: [0.3]")
                .define("combat-radius-particle-spread", Common.CombatRadiusParticleSpread);
        provider.pop();
        
        provider.push("Animation");
        provider.comment("Minimum time (in ticks) before idle robots sit down.", "20 ticks = 1 second. In Standby mode, robots eventually sit if nothing is happening.", "Range: 100 to 12000", "Example: [600] (30 seconds)")
                .define("standby-to-sit-delay-min", Common.StandbyToSitDelayMin);
        
        provider.comment("Maximum time (in ticks) before idle robots sit down.", "20 ticks = 1 second. Adds variety to when robots decide to sit.", "Range: 100 to 12000", "Example: [1800] (90 seconds)")
                .define("standby-to-sit-delay-max", Common.StandbyToSitDelayMax);
        provider.pop();
        
        provider.pop();
        
        provider.push("Entity");
        
        provider.push("Bunny");
        provider.comment("Highest level this robot type can reach.", "Higher levels unlock better stats and abilities.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [200]")
                .define("bunny-max-level", Common.BunnyMaxLevel);
        
        provider.comment("How fast this robot type attacks (attacks per second).", "Higher values mean faster attacks. Minecraft default is 1.0.", "Range: 0.1 to no upper limit (does not accept negative values or zero)", "Example: [1.6]")
                .define("bunny-attack-speed", Common.BunnyAttackSpeed);
        
        provider.comment("Base movement speed for this robot type.", "Higher values make robots move faster. Player walk speed is 0.1.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.37]")
                .define("bunny-movement-speed", Common.BunnyMovementSpeed);
        
        provider.comment("Base toughness for this robot type.", "Reduces damage from strong attacks. Diamond armor has 2.0 toughness per piece.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.0]")
                .define("bunny-base-toughness", Common.BunnyBaseToughness);
        
        provider.comment("Base HP value for combat level calculations.", "Used by CombatLevelFeature to calculate HP at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [16]")
                .define("bunny-base-hp", Common.BunnyBaseHp);
        
        provider.comment("Base attack value for combat level calculations.", "Used by CombatLevelFeature to calculate attack damage at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [2]")
                .define("bunny-base-attack", Common.BunnyBaseAttack);
        
        provider.comment("Base defense value for combat level calculations.", "Used by CombatLevelFeature to calculate armor and toughness at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [3]")
                .define("bunny-base-defense", Common.BunnyBaseDefense);
        provider.pop();
        
        provider.push("Bunny2");
        provider.comment("Highest level this robot type can reach.", "Higher levels unlock better stats and abilities.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [200]")
                .define("bunny2-max-level", Common.Bunny2MaxLevel);
        
        provider.comment("How fast this robot type attacks (attacks per second).", "Higher values mean faster attacks. Minecraft default is 1.0.", "Range: 0.1 to no upper limit (does not accept negative values or zero)", "Example: [1.8]")
                .define("bunny2-attack-speed", Common.Bunny2AttackSpeed);
        
        provider.comment("Base movement speed for this robot type.", "Higher values make robots move faster. Player walk speed is 0.1.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.37]")
                .define("bunny2-movement-speed", Common.Bunny2MovementSpeed);
        
        provider.comment("Base toughness for this robot type.", "Reduces damage from strong attacks. Diamond armor has 2.0 toughness per piece.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [1.0]")
                .define("bunny2-base-toughness", Common.Bunny2BaseToughness);
        
        provider.comment("Base HP value for combat level calculations.", "Used by CombatLevelFeature to calculate HP at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [24]")
                .define("bunny2-base-hp", Common.Bunny2BaseHp);
        
        provider.comment("Base attack value for combat level calculations.", "Used by CombatLevelFeature to calculate attack damage at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [4]")
                .define("bunny2-base-attack", Common.Bunny2BaseAttack);
        
        provider.comment("Base defense value for combat level calculations.", "Used by CombatLevelFeature to calculate armor and toughness at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [6]")
                .define("bunny2-base-defense", Common.Bunny2BaseDefense);
        provider.pop();
        
        provider.push("Dragon");
        provider.comment("Highest level this robot type can reach.", "Higher levels unlock better stats and abilities.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [300]")
                .define("dragon-max-level", Common.DragonMaxLevel);
        
        provider.comment("How fast this robot type attacks (attacks per second).", "Higher values mean faster attacks. Minecraft default is 1.0.", "Range: 0.1 to no upper limit (does not accept negative values or zero)", "Example: [0.8]")
                .define("dragon-attack-speed", Common.DragonAttackSpeed);
        
        provider.comment("Base movement speed for this robot type.", "Higher values make robots move faster. Player walk speed is 0.1.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.22]")
                .define("dragon-movement-speed", Common.DragonMovementSpeed);
        
        provider.comment("Base toughness for this robot type.", "Reduces damage from strong attacks. Diamond armor has 2.0 toughness per piece.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [2.0]")
                .define("dragon-base-toughness", Common.DragonBaseToughness);
        
        provider.comment("Base HP value for combat level calculations.", "Used by CombatLevelFeature to calculate HP at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [30]")
                .define("dragon-base-hp", Common.DragonBaseHp);
        
        provider.comment("Base attack value for combat level calculations.", "Used by CombatLevelFeature to calculate attack damage at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [8]")
                .define("dragon-base-attack", Common.DragonBaseAttack);
        
        provider.comment("Base defense value for combat level calculations.", "Used by CombatLevelFeature to calculate armor and toughness at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [4]")
                .define("dragon-base-defense", Common.DragonBaseDefense);
        provider.pop();
        
        provider.push("Honey");
        provider.comment("Highest level this robot type can reach.", "Higher levels unlock better stats and abilities.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [200]")
                .define("honey-max-level", Common.HoneyMaxLevel);
        
        provider.comment("How fast this robot type attacks (attacks per second).", "Higher values mean faster attacks. Minecraft default is 1.0.", "Range: 0.1 to no upper limit (does not accept negative values or zero)", "Example: [1.0]")
                .define("honey-attack-speed", Common.HoneyAttackSpeed);
        
        provider.comment("Base movement speed for this robot type.", "Higher values make robots move faster. Player walk speed is 0.1.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.37]")
                .define("honey-movement-speed", Common.HoneyMovementSpeed);
        
        provider.comment("Base toughness for this robot type.", "Reduces damage from strong attacks. Diamond armor has 2.0 toughness per piece.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [1.0]")
                .define("honey-base-toughness", Common.HoneyBaseToughness);
        
        provider.comment("Base HP value for combat level calculations.", "Used by CombatLevelFeature to calculate HP at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [14]")
                .define("honey-base-hp", Common.HoneyBaseHp);
        
        provider.comment("Base attack value for combat level calculations.", "Used by CombatLevelFeature to calculate attack damage at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [1]")
                .define("honey-base-attack", Common.HoneyBaseAttack);
        
        provider.comment("Base defense value for combat level calculations.", "Used by CombatLevelFeature to calculate armor and toughness at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [2]")
                .define("honey-base-defense", Common.HoneyBaseDefense);
        provider.pop();
        
        provider.push("Kitsune");
        provider.comment("Highest level this robot type can reach.", "Higher levels unlock better stats and abilities.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [200]")
                .define("kitsune-max-level", Common.KitsuneMaxLevel);
        
        provider.comment("How fast this robot type attacks (attacks per second).", "Higher values mean faster attacks. Minecraft default is 1.0.", "Range: 0.1 to no upper limit (does not accept negative values or zero)", "Example: [1.1]")
                .define("kitsune-attack-speed", Common.KitsuneAttackSpeed);
        
        provider.comment("Base movement speed for this robot type.", "Higher values make robots move faster. Player walk speed is 0.1.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.37]")
                .define("kitsune-movement-speed", Common.KitsuneMovementSpeed);
        
        provider.comment("Base toughness for this robot type.", "Reduces damage from strong attacks. Diamond armor has 2.0 toughness per piece.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [1.0]")
                .define("kitsune-base-toughness", Common.KitsuneBaseToughness);
        
        provider.comment("Base HP value for combat level calculations.", "Used by CombatLevelFeature to calculate HP at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [16]")
                .define("kitsune-base-hp", Common.KitsuneBaseHp);
        
        provider.comment("Base attack value for combat level calculations.", "Used by CombatLevelFeature to calculate attack damage at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [2]")
                .define("kitsune-base-attack", Common.KitsuneBaseAttack);
        
        provider.comment("Base defense value for combat level calculations.", "Used by CombatLevelFeature to calculate armor and toughness at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [1]")
                .define("kitsune-base-defense", Common.KitsuneBaseDefense);
        provider.pop();
        
        provider.push("Neko");
        provider.comment("Highest level this robot type can reach.", "Higher levels unlock better stats and abilities.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [200]")
                .define("neko-max-level", Common.NekoMaxLevel);
        
        provider.comment("How fast this robot type attacks (attacks per second).", "Higher values mean faster attacks. Minecraft default is 1.0.", "Range: 0.1 to no upper limit (does not accept negative values or zero)", "Example: [1.1]")
                .define("neko-attack-speed", Common.NekoAttackSpeed);
        
        provider.comment("Base movement speed for this robot type.", "Higher values make robots move faster. Player walk speed is 0.1.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.40]")
                .define("neko-movement-speed", Common.NekoMovementSpeed);
        
        provider.comment("Base toughness for this robot type.", "Reduces damage from strong attacks. Diamond armor has 2.0 toughness per piece.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [1.0]")
                .define("neko-base-toughness", Common.NekoBaseToughness);
        
        provider.comment("Base HP value for combat level calculations.", "Used by CombatLevelFeature to calculate HP at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [22]")
                .define("neko-base-hp", Common.NekoBaseHp);
        
        provider.comment("Base attack value for combat level calculations.", "Used by CombatLevelFeature to calculate attack damage at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [6]")
                .define("neko-base-attack", Common.NekoBaseAttack);
        
        provider.comment("Base defense value for combat level calculations.", "Used by CombatLevelFeature to calculate armor and toughness at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [2]")
                .define("neko-base-defense", Common.NekoBaseDefense);
        provider.pop();
        
        provider.push("Vanilla");
        provider.comment("Highest level this robot type can reach.", "Higher levels unlock better stats and abilities.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [200]")
                .define("vanilla-max-level", Common.VanillaMaxLevel);
        
        provider.comment("How fast this robot type attacks (attacks per second).", "Higher values mean faster attacks. Minecraft default is 1.0.", "Range: 0.1 to no upper limit (does not accept negative values or zero)", "Example: [2.0]")
                .define("vanilla-attack-speed", Common.VanillaAttackSpeed);
        
        provider.comment("Base movement speed for this robot type.", "Higher values make robots move faster. Player walk speed is 0.1.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.37]")
                .define("vanilla-movement-speed", Common.VanillaMovementSpeed);
        
        provider.comment("Base toughness for this robot type.", "Reduces damage from strong attacks. Diamond armor has 2.0 toughness per piece.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.0]")
                .define("vanilla-base-toughness", Common.VanillaBaseToughness);
        
        provider.comment("Base HP value for combat level calculations.", "Used by CombatLevelFeature to calculate HP at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [14]")
                .define("vanilla-base-hp", Common.VanillaBaseHp);
        
        provider.comment("Base attack value for combat level calculations.", "Used by CombatLevelFeature to calculate attack damage at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [4]")
                .define("vanilla-base-attack", Common.VanillaBaseAttack);
        
        provider.comment("Base defense value for combat level calculations.", "Used by CombatLevelFeature to calculate armor and toughness at each level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [2]")
                .define("vanilla-base-defense", Common.VanillaBaseDefense);
        provider.pop();
        
        provider.pop();
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
        Common.OwnerMaxRobotNum = config.getOrDefault("owner-max-robot", Common.OwnerMaxRobotNum);
        Common.MovementMeleeAttack = config.getOrDefault("movement-melee-attack", Common.MovementMeleeAttack);
        Common.MovementFollowOwner = config.getOrDefault("movement-follow-owner", Common.MovementFollowOwner);
        Common.MovementWanderAround = config.getOrDefault("movement-wander-around", Common.MovementWanderAround);
        Common.FollowDistanceMax = config.getOrDefault("follow-distance-max", Common.FollowDistanceMax);
        Common.FollowDistanceMin = config.getOrDefault("follow-distance-min", Common.FollowDistanceMin);
        Common.LookRange = config.getOrDefault("look-range", Common.LookRange);
        
        // Renderer settings
        Common.Width = config.getOrDefault("width", Common.Width);
        Common.Height = config.getOrDefault("height", Common.Height);
        Client.ShadowRadius = config.getOrDefault("shadow-radius", Client.ShadowRadius);
        
        // Level/Experience settings
        Common.ExperienceBase = config.getOrDefault("experience-base", Common.ExperienceBase);
        Common.ExperienceMultiplier = config.getOrDefault("experience-multiplier", Common.ExperienceMultiplier);
        
        // Combat settings
        Common.FriendlyFire = config.getOrDefault("friendly-fire", Common.FriendlyFire);
        Common.AttackChance = config.getOrDefault("attack-chance", Common.AttackChance);
        Common.HealInterval = config.getOrDefault("heal-interval", Common.HealInterval);
        Common.WaryTime = config.getOrDefault("wary-time", Common.WaryTime);
        Common.GlobalAutoHeal = config.getOrDefault("global-heal", Common.GlobalAutoHeal);
        Common.LootEnchantment = config.getOrDefault("loot-enchantment", Common.LootEnchantment);
        Common.LootEnchantmentLevel = config.getOrDefault("loot-enchantment-level", Common.LootEnchantmentLevel);
        Common.MaxLootEnchantment = config.getOrDefault("max-loot-enchantment", Common.MaxLootEnchantment);
        Common.BaseDefenceRange = config.getOrDefault("base-defence-range", Common.BaseDefenceRange);
        Common.BaseDefenceWarpRange = config.getOrDefault("base-defence-warp-range", Common.BaseDefenceWarpRange);
        
        // Protection settings
        Common.ProtectionLimitFire = config.getOrDefault("limit-fire", Common.ProtectionLimitFire);
        Common.ProtectionLimitFall = config.getOrDefault("limit-fall", Common.ProtectionLimitFall);
        Common.ProtectionLimitBlast = config.getOrDefault("limit-blast", Common.ProtectionLimitBlast);
        Common.ProtectionLimitProjectile = config.getOrDefault("limit-projectile", Common.ProtectionLimitProjectile);
        
        // Smart Core Retrieval
        Common.EnableSmartCoreRetrieval = config.getOrDefault("enable-smart-core-retrieval", Common.EnableSmartCoreRetrieval);
        Common.SmartCoreRetrievalDistance = config.getOrDefault("smart-core-retrieval-distance", Common.SmartCoreRetrievalDistance);
        
        // AI Behavior settings
        Common.OwnerStillThreshold = config.getOrDefault("owner-still-threshold", Common.OwnerStillThreshold);
        Common.WanderCheckInterval = config.getOrDefault("wander-check-interval", Common.WanderCheckInterval);
        Common.WanderChance = config.getOrDefault("wander-chance", Common.WanderChance);
        Common.WanderRadiusMin = config.getOrDefault("wander-radius-min", Common.WanderRadiusMin);
        Common.WanderRadiusMax = config.getOrDefault("wander-radius-max", Common.WanderRadiusMax);
        Common.WanderDurationMin = config.getOrDefault("wander-duration-min", Common.WanderDurationMin);
        Common.WanderDurationMax = config.getOrDefault("wander-duration-max", Common.WanderDurationMax);
        Common.WanderCooldownMin = config.getOrDefault("wander-cooldown-min", Common.WanderCooldownMin);
        Common.WanderCooldownMax = config.getOrDefault("wander-cooldown-max", Common.WanderCooldownMax);
        Common.PatrolDurationMin = config.getOrDefault("patrol-duration-min", Common.PatrolDurationMin);
        Common.PatrolDurationMax = config.getOrDefault("patrol-duration-max", Common.PatrolDurationMax);
        Common.GuardDurationMin = config.getOrDefault("guard-duration-min", Common.GuardDurationMin);
        Common.GuardDurationMax = config.getOrDefault("guard-duration-max", Common.GuardDurationMax);
        Common.PatrolPauseDurationMin = config.getOrDefault("patrol-pause-duration-min", Common.PatrolPauseDurationMin);
        Common.PatrolPauseDurationMax = config.getOrDefault("patrol-pause-duration-max", Common.PatrolPauseDurationMax);
        Common.GuardRotationSpeed = config.getOrDefault("guard-rotation-speed", Common.GuardRotationSpeed);
        Common.EnableCombatRadiusParticles = config.getOrDefault("enable-combat-radius-particles", Common.EnableCombatRadiusParticles);
        Common.CombatRadiusParticleCount = config.getOrDefault("combat-radius-particle-count", Common.CombatRadiusParticleCount);
        Common.CombatRadiusParticleSpread = config.getOrDefault("combat-radius-particle-spread", Common.CombatRadiusParticleSpread);
        
        // Animation settings
        Common.StandbyToSitDelayMin = config.getOrDefault("standby-to-sit-delay-min", Common.StandbyToSitDelayMin);
        Common.StandbyToSitDelayMax = config.getOrDefault("standby-to-sit-delay-max", Common.StandbyToSitDelayMax);
        
        // Entity-specific settings - BUNNY
        Common.BunnyMaxLevel = config.getOrDefault("bunny-max-level", Common.BunnyMaxLevel);
        Common.BunnyAttackSpeed = config.getOrDefault("bunny-attack-speed", Common.BunnyAttackSpeed);
        Common.BunnyMovementSpeed = config.getOrDefault("bunny-movement-speed", Common.BunnyMovementSpeed);
        Common.BunnyBaseToughness = config.getOrDefault("bunny-base-toughness", Common.BunnyBaseToughness);
        Common.BunnyBaseHp = config.getOrDefault("bunny-base-hp", Common.BunnyBaseHp);
        Common.BunnyBaseAttack = config.getOrDefault("bunny-base-attack", Common.BunnyBaseAttack);
        Common.BunnyBaseDefense = config.getOrDefault("bunny-base-defense", Common.BunnyBaseDefense);
        
        // Entity-specific settings - BUNNY2
        Common.Bunny2MaxLevel = config.getOrDefault("bunny2-max-level", Common.Bunny2MaxLevel);
        Common.Bunny2AttackSpeed = config.getOrDefault("bunny2-attack-speed", Common.Bunny2AttackSpeed);
        Common.Bunny2MovementSpeed = config.getOrDefault("bunny2-movement-speed", Common.Bunny2MovementSpeed);
        Common.Bunny2BaseToughness = config.getOrDefault("bunny2-base-toughness", Common.Bunny2BaseToughness);
        Common.Bunny2BaseHp = config.getOrDefault("bunny2-base-hp", Common.Bunny2BaseHp);
        Common.Bunny2BaseAttack = config.getOrDefault("bunny2-base-attack", Common.Bunny2BaseAttack);
        Common.Bunny2BaseDefense = config.getOrDefault("bunny2-base-defense", Common.Bunny2BaseDefense);
        
        // Entity-specific settings - DRAGON
        Common.DragonMaxLevel = config.getOrDefault("dragon-max-level", Common.DragonMaxLevel);
        Common.DragonAttackSpeed = config.getOrDefault("dragon-attack-speed", Common.DragonAttackSpeed);
        Common.DragonMovementSpeed = config.getOrDefault("dragon-movement-speed", Common.DragonMovementSpeed);
        Common.DragonBaseToughness = config.getOrDefault("dragon-base-toughness", Common.DragonBaseToughness);
        Common.DragonBaseHp = config.getOrDefault("dragon-base-hp", Common.DragonBaseHp);
        Common.DragonBaseAttack = config.getOrDefault("dragon-base-attack", Common.DragonBaseAttack);
        Common.DragonBaseDefense = config.getOrDefault("dragon-base-defense", Common.DragonBaseDefense);
        
        // Entity-specific settings - HONEY
        Common.HoneyMaxLevel = config.getOrDefault("honey-max-level", Common.HoneyMaxLevel);
        Common.HoneyAttackSpeed = config.getOrDefault("honey-attack-speed", Common.HoneyAttackSpeed);
        Common.HoneyMovementSpeed = config.getOrDefault("honey-movement-speed", Common.HoneyMovementSpeed);
        Common.HoneyBaseToughness = config.getOrDefault("honey-base-toughness", Common.HoneyBaseToughness);
        Common.HoneyBaseHp = config.getOrDefault("honey-base-hp", Common.HoneyBaseHp);
        Common.HoneyBaseAttack = config.getOrDefault("honey-base-attack", Common.HoneyBaseAttack);
        Common.HoneyBaseDefense = config.getOrDefault("honey-base-defense", Common.HoneyBaseDefense);
        
        // Entity-specific settings - KITSUNE
        Common.KitsuneMaxLevel = config.getOrDefault("kitsune-max-level", Common.KitsuneMaxLevel);
        Common.KitsuneAttackSpeed = config.getOrDefault("kitsune-attack-speed", Common.KitsuneAttackSpeed);
        Common.KitsuneMovementSpeed = config.getOrDefault("kitsune-movement-speed", Common.KitsuneMovementSpeed);
        Common.KitsuneBaseToughness = config.getOrDefault("kitsune-base-toughness", Common.KitsuneBaseToughness);
        Common.KitsuneBaseHp = config.getOrDefault("kitsune-base-hp", Common.KitsuneBaseHp);
        Common.KitsuneBaseAttack = config.getOrDefault("kitsune-base-attack", Common.KitsuneBaseAttack);
        Common.KitsuneBaseDefense = config.getOrDefault("kitsune-base-defense", Common.KitsuneBaseDefense);
        
        // Entity-specific settings - NEKO
        Common.NekoMaxLevel = config.getOrDefault("neko-max-level", Common.NekoMaxLevel);
        Common.NekoAttackSpeed = config.getOrDefault("neko-attack-speed", Common.NekoAttackSpeed);
        Common.NekoMovementSpeed = config.getOrDefault("neko-movement-speed", Common.NekoMovementSpeed);
        Common.NekoBaseToughness = config.getOrDefault("neko-base-toughness", Common.NekoBaseToughness);
        Common.NekoBaseHp = config.getOrDefault("neko-base-hp", Common.NekoBaseHp);
        Common.NekoBaseAttack = config.getOrDefault("neko-base-attack", Common.NekoBaseAttack);
        Common.NekoBaseDefense = config.getOrDefault("neko-base-defense", Common.NekoBaseDefense);
        
        // Entity-specific settings - VANILLA
        Common.VanillaMaxLevel = config.getOrDefault("vanilla-max-level", Common.VanillaMaxLevel);
        Common.VanillaAttackSpeed = config.getOrDefault("vanilla-attack-speed", Common.VanillaAttackSpeed);
        Common.VanillaMovementSpeed = config.getOrDefault("vanilla-movement-speed", Common.VanillaMovementSpeed);
        Common.VanillaBaseToughness = config.getOrDefault("vanilla-base-toughness", Common.VanillaBaseToughness);
        Common.VanillaBaseHp = config.getOrDefault("vanilla-base-hp", Common.VanillaBaseHp);
        Common.VanillaBaseAttack = config.getOrDefault("vanilla-base-attack", Common.VanillaBaseAttack);
        Common.VanillaBaseDefense = config.getOrDefault("vanilla-base-defense", Common.VanillaBaseDefense);
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

        // BUNNY
        public static int BunnyMaxLevel = 200;
        public static float BunnyAttackSpeed = 1.6F;
        public static float BunnyMovementSpeed = 0.37F; // 0.30F
        public static float BunnyBaseToughness = 0F;
        public static int BunnyBaseHp = 16;
        public static int BunnyBaseAttack = 2;
        public static int BunnyBaseDefense = 3;

        // BUNNY2
        public static int Bunny2MaxLevel = 200;
        public static float Bunny2AttackSpeed = 1.8F;
        public static float Bunny2MovementSpeed = 0.37F; // 0.35F
        public static float Bunny2BaseToughness = 0F;
        public static int Bunny2BaseHp = 20;
        public static int Bunny2BaseAttack = 3;
        public static int Bunny2BaseDefense = 4;

        // DRAGON
        public static int DragonMaxLevel = 200;
        public static float DragonAttackSpeed = 1.5F;
        public static float DragonMovementSpeed = 0.37F; // 0.37F
        public static float DragonBaseToughness = 2F;
        public static int DragonBaseHp = 24;
        public static int DragonBaseAttack = 5;
        public static int DragonBaseDefense = 5;

        // HONEY
        public static int HoneyMaxLevel = 200;
        public static float HoneyAttackSpeed = 1.0F;
        public static float HoneyMovementSpeed = 0.37F;
        public static float HoneyBaseToughness = 1F;
        public static int HoneyBaseHp = 14;
        public static int HoneyBaseAttack = 1;
        public static int HoneyBaseDefense = 2;

        // KITSUNE
        public static int KitsuneMaxLevel = 200;
        public static float KitsuneAttackSpeed = 1.1F;
        public static float KitsuneMovementSpeed = 0.37F;
        public static float KitsuneBaseToughness = 1F;
        public static int KitsuneBaseHp = 16;
        public static int KitsuneBaseAttack = 2;
        public static int KitsuneBaseDefense = 1;

        // NEKO
        public static int NekoMaxLevel = 200;
        public static float NekoAttackSpeed = 1.1F;
        public static float NekoMovementSpeed = 0.40F;
        public static float NekoBaseToughness = 1F;
        public static int NekoBaseHp = 22;
        public static int NekoBaseAttack = 6;
        public static int NekoBaseDefense = 2;

        // VANILLA
        public static int VanillaMaxLevel = 200;
        public static float VanillaAttackSpeed = 2F;
        public static float VanillaMovementSpeed = 0.37F; // 0.25F is too slow
        public static float VanillaBaseToughness = 0F;
        public static int VanillaBaseHp = 14;
        public static int VanillaBaseAttack = 4;
        public static int VanillaBaseDefense = 2;

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