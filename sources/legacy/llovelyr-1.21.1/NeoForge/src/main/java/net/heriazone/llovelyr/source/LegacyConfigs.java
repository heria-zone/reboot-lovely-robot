package net.heriazone.llovelyr.source;

import com.electronwill.nightconfig.core.Config;
import net.heriazone.llovelyr.Legacy;
import net.heriazone.lovelylib.common.configs.ConfigBounds;
import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.configs.ConfigKeyGenerator;
import net.heriazone.lovelylib.common.configs.ConfigAccessLayer;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration system for Legacy variant (NeoForge loader).
 * <p>
 * <b>Architecture:</b> Uses NeoForge's ModConfigSpec for type-safe configuration
 * with automatic validation, default values, and runtime reload support.
 * <p>
 * <b>Config Structure:</b> Organized into logical sections (General, Combat, Entity)
 * with detailed comments for each option. All values validated against defined ranges.
 */
@EventBusSubscriber(modid = Legacy.MODID, bus = EventBusSubscriber.Bus.MOD)
public class LegacyConfigs {

    // -- Constants --

    private static final ModConfigSpec.Builder BUILDER;
    public static final ModConfigSpec SPEC;

    // GENERAL
    private static final ModConfigSpec.ConfigValue<Integer> OWNER_MAX_ROBOT_NUM;
    private static final ModConfigSpec.ConfigValue<Double> MOVEMENT_MELEE_ATTACK;
    private static final ModConfigSpec.ConfigValue<Double> MOVEMENT_FOLLOW_OWNER;
    private static final ModConfigSpec.ConfigValue<Double> MOVEMENT_WANDER_AROUND;
    private static final ModConfigSpec.ConfigValue<Double> FOLLOW_DISTANCE_MAX;
    private static final ModConfigSpec.ConfigValue<Double> FOLLOW_DISTANCE_MIN;
    private static final ModConfigSpec.ConfigValue<Double> LOOK_RANGE;

    // RENDERER
    private static final ModConfigSpec.ConfigValue<Double> SHADOW_RADIUS;


    // LEVEL | EXPERIENCE
    private static final ModConfigSpec.ConfigValue<Integer> EXPERIENCE_BASE;
    private static final ModConfigSpec.ConfigValue<Integer> EXPERIENCE_MULTIPLIER;

    // COMBAT
    private static final ModConfigSpec.ConfigValue<Boolean> FRIENDLY_FIRE;
    private static final ModConfigSpec.ConfigValue<Integer> ATTACK_CHANCE;
    private static final ModConfigSpec.ConfigValue<Integer> HEAL_INTERVAL;
    private static final ModConfigSpec.ConfigValue<Integer> WARY_TIME;
    private static final ModConfigSpec.ConfigValue<Boolean> GLOBAL_AUTO_HEAL;
    private static final ModConfigSpec.ConfigValue<Boolean> LOOT_ENCHANTMENT;
    private static final ModConfigSpec.ConfigValue<Integer> LOOT_ENCHANTMENT_LEVEL;
    private static final ModConfigSpec.ConfigValue<Integer> MAX_LOOT_ENCHANTMENT;
    private static final ModConfigSpec.ConfigValue<Double> BASE_DEFENCE_RANGE;
    private static final ModConfigSpec.ConfigValue<Double> BASE_DEFENCE_WARP_RANGE;

    // PROTECTION
    private static final ModConfigSpec.ConfigValue<Integer> PROTECTION_LIMIT_FIRE;
    private static final ModConfigSpec.ConfigValue<Integer> PROTECTION_LIMIT_FALL;
    private static final ModConfigSpec.ConfigValue<Integer> PROTECTION_LIMIT_BLAST;
    private static final ModConfigSpec.ConfigValue<Integer> PROTECTION_LIMIT_PROJECTILE;
    private static final ModConfigSpec.ConfigValue<Boolean> ENABLE_ENCHANTED_BOOK_PROTECTION;
    private static final ModConfigSpec.ConfigValue<Double> ENCHANTED_BOOK_CONTRIBUTION;

    // SMART CORE RETRIEVAL
    private static final ModConfigSpec.ConfigValue<Boolean> ENABLE_SMART_CORE_RETRIEVAL;
    private static final ModConfigSpec.ConfigValue<Double> SMART_CORE_RETRIEVAL_DISTANCE;

    // AI BEHAVIOR
    private static final ModConfigSpec.ConfigValue<Integer> OWNER_STILL_THRESHOLD;
    private static final ModConfigSpec.ConfigValue<Integer> WANDER_CHECK_INTERVAL;
    private static final ModConfigSpec.ConfigValue<Double> WANDER_CHANCE;
    private static final ModConfigSpec.ConfigValue<Double> WANDER_RADIUS_MIN;
    private static final ModConfigSpec.ConfigValue<Double> WANDER_RADIUS_MAX;
    private static final ModConfigSpec.ConfigValue<Integer> WANDER_DURATION_MIN;
    private static final ModConfigSpec.ConfigValue<Integer> WANDER_DURATION_MAX;
    private static final ModConfigSpec.ConfigValue<Integer> WANDER_COOLDOWN_MIN;
    private static final ModConfigSpec.ConfigValue<Integer> WANDER_COOLDOWN_MAX;
    private static final ModConfigSpec.ConfigValue<Integer> PATROL_DURATION_MIN;
    private static final ModConfigSpec.ConfigValue<Integer> PATROL_DURATION_MAX;
    private static final ModConfigSpec.ConfigValue<Integer> GUARD_DURATION_MIN;
    private static final ModConfigSpec.ConfigValue<Integer> GUARD_DURATION_MAX;
    private static final ModConfigSpec.ConfigValue<Integer> PATROL_PAUSE_DURATION_MIN;
    private static final ModConfigSpec.ConfigValue<Integer> PATROL_PAUSE_DURATION_MAX;
    private static final ModConfigSpec.ConfigValue<Double> GUARD_ROTATION_SPEED;
    private static final ModConfigSpec.ConfigValue<Boolean> ENABLE_COMBAT_RADIUS_PARTICLES;
    private static final ModConfigSpec.ConfigValue<Integer> COMBAT_RADIUS_PARTICLE_COUNT;
    private static final ModConfigSpec.ConfigValue<Double> COMBAT_RADIUS_PARTICLE_SPREAD;

    // ANIMATION
    private static final ModConfigSpec.ConfigValue<Integer> STANDBY_TO_SIT_DELAY_MIN;
    private static final ModConfigSpec.ConfigValue<Integer> STANDBY_TO_SIT_DELAY_MAX;

    // -- DYNAMIC ENTITY CONFIGURATION STORAGE --
    
    private static final Map<String, ModConfigSpec.ConfigValue<Integer>> ENTITY_INT_CONFIGS = new HashMap<>();
    private static final Map<String, ModConfigSpec.ConfigValue<Double>> ENTITY_DOUBLE_CONFIGS = new HashMap<>();
    
    // Phase 2: Serialized config support
    private static final Map<String, ModConfigSpec.ConfigValue<String>> ENTITY_SERIALIZED_CONFIGS = new HashMap<>();

    static {
        Config.setInsertionOrderPreserved(true);

        BUILDER = new ModConfigSpec.Builder();

        BUILDER.push("General");
        OWNER_MAX_ROBOT_NUM = BUILDER
                .comment("How many robots each player can own at once.", "Set to -1 for unlimited robots.", "Range: -1 to no upper limit", "Example: [30]")
                .define("owner-max-robot", SharedConfigs.Common.OwnerMaxRobotNum);

        MOVEMENT_MELEE_ATTACK = BUILDER
                .comment("How fast robots move when attacking enemies in melee combat.", "Higher values make robots move faster during attacks.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.8]")
                .defineInRange("movement-melee-attack", SharedConfigs.Common.MovementMeleeAttack, ConfigBounds.MOVEMENT_SPEED_MIN, ConfigBounds.MOVEMENT_SPEED_MAX);

        MOVEMENT_FOLLOW_OWNER = BUILDER
                .comment("How fast robots move when following their owner.", "Higher values make robots keep up with you better.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.7]")
                .defineInRange("movement-follow-owner", SharedConfigs.Common.MovementFollowOwner, ConfigBounds.MOVEMENT_SPEED_MIN, ConfigBounds.MOVEMENT_SPEED_MAX);

        MOVEMENT_WANDER_AROUND = BUILDER
                .comment("How fast robots move when wandering around on their own.", "Lower than follow speed to make wandering look more relaxed.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.6]")
                .defineInRange("movement-wander-around", SharedConfigs.Common.MovementWanderAround, ConfigBounds.MOVEMENT_SPEED_MIN, ConfigBounds.MOVEMENT_SPEED_MAX);

        FOLLOW_DISTANCE_MAX = BUILDER
                .comment("Maximum distance (in blocks) robots will stay from their owner before teleporting.", "If you get too far, your robot will teleport to you.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [10.0]")
                .defineInRange("follow-distance-max", SharedConfigs.Common.FollowDistanceMax, ConfigBounds.FOLLOW_DISTANCE_MIN, ConfigBounds.FOLLOW_DISTANCE_MAX);

        FOLLOW_DISTANCE_MIN = BUILDER
                .comment("Minimum distance (in blocks) robots try to maintain from their owner.", "Robots won't crowd you closer than this distance.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [2.0]")
                .defineInRange("follow-distance-min", SharedConfigs.Common.FollowDistanceMin, ConfigBounds.FOLLOW_DISTANCE_MIN, ConfigBounds.FOLLOW_DISTANCE_MAX);

        LOOK_RANGE = BUILDER
                .comment("How far (in blocks) robots can look at and track entities.", "Affects head rotation and attention behavior.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [8.0]")
                .defineInRange("look-range", SharedConfigs.Common.LookRange, ConfigBounds.LOOK_RANGE_MIN, ConfigBounds.LOOK_RANGE_MAX);
        BUILDER.pop();

        BUILDER.push("Renderer");
        SHADOW_RADIUS = BUILDER
                .comment("Size of the shadow rendered under robots.", "Purely visual - doesn't affect gameplay.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.4]")
                .defineInRange("shadow-radius", SharedConfigs.Client.ShadowRadius, ConfigBounds.SHADOW_RADIUS_MIN, ConfigBounds.SHADOW_RADIUS_MAX);
        BUILDER.pop();

        BUILDER.push("Level & Experience");
        EXPERIENCE_BASE = BUILDER
                .comment("Base experience points needed for a robot to reach level 1.", "Each level requires more XP based on the multiplier below.", "Range: 0 to no upper limit (does not accept negative values)", "Example: [50]")
                .defineInRange("experience-base", SharedConfigs.Common.ExperienceBase, ConfigBounds.EXPERIENCE_BASE_MIN, ConfigBounds.EXPERIENCE_BASE_MAX);

        EXPERIENCE_MULTIPLIER = BUILDER
                .comment("How much more XP each level requires compared to the previous level.", "Level 2 needs base × multiplier, Level 3 needs base × multiplier², etc.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [2]")
                .defineInRange("experience-multiplier", SharedConfigs.Common.ExperienceMultiplier, ConfigBounds.EXPERIENCE_MULTIPLIER_MIN, ConfigBounds.EXPERIENCE_MULTIPLIER_MAX);
        BUILDER.pop();

        BUILDER.push("Combat");
        FRIENDLY_FIRE = BUILDER
                .comment("Whether players can damage their own robots.", "Set to true to allow accidentally hitting your robots, false to prevent it.", "Example: [false]")
                .define("friendly-fire", SharedConfigs.Common.FriendlyFire);

        ATTACK_CHANCE = BUILDER
                .comment("How likely robots are to counter-attack when hit (higher = more aggressive).", "Affects how quickly robots retaliate when damaged.", "Range: 0 to no upper limit (does not accept negative values)", "Example: [5]")
                .defineInRange("attack-chance", SharedConfigs.Common.AttackChance, ConfigBounds.ATTACK_CHANCE_MIN, ConfigBounds.ATTACK_CHANCE_MAX);

        HEAL_INTERVAL = BUILDER
                .comment("How often (in ticks) robots automatically heal themselves.", "20 ticks = 1 second. Lower values = faster healing.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [50]")
                .defineInRange("heal-interval", SharedConfigs.Common.HealInterval, ConfigBounds.HEAL_INTERVAL_MIN, ConfigBounds.HEAL_INTERVAL_MAX);

        WARY_TIME = BUILDER
                .comment("How long (in ticks) robots stay alert after combat ends.", "20 ticks = 1 second. During this time, robots remain ready to fight.", "Range: 0 to no upper limit (does not accept negative values)", "Example: [50]")
                .defineInRange("wary-time", SharedConfigs.Common.WaryTime, ConfigBounds.WARY_TIME_MIN, ConfigBounds.WARY_TIME_MAX);

        GLOBAL_AUTO_HEAL = BUILDER
                .comment("Whether robots automatically heal over time.", "Set to false to disable automatic healing entirely.", "Example: [true]")
                .define("global-heal", SharedConfigs.Common.GlobalAutoHeal);

        LOOT_ENCHANTMENT = BUILDER
                .comment("Whether robots can benefit from Looting enchantment on their weapon.", "When enabled, higher level robots get better mob drops.", "Example: [true]")
                .define("loot-enchantment", SharedConfigs.Common.LootEnchantment);

        LOOT_ENCHANTMENT_LEVEL = BUILDER
                .comment("What robot level is needed to gain Looting I enchantment effect.", "Looting II at 2× this level, Looting III at 3× this level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [10]")
                .defineInRange("loot-enchantment-level", SharedConfigs.Common.LootEnchantmentLevel, ConfigBounds.LOOT_ENCHANTMENT_LEVEL_MIN, ConfigBounds.LOOT_ENCHANTMENT_LEVEL_MAX);

        MAX_LOOT_ENCHANTMENT = BUILDER
                .comment("Maximum Looting enchantment level robots can have.", "Limits how much bonus loot high-level robots can get.", "Range: 0 to 3", "Example: [3]")
                .defineInRange("max-loot-enchantment", SharedConfigs.Common.MaxLootEnchantment, ConfigBounds.MAX_LOOT_ENCHANTMENT_MIN, ConfigBounds.MAX_LOOT_ENCHANTMENT_MAX);

        BASE_DEFENCE_RANGE = BUILDER
                .comment("How far (in blocks) robots will chase enemies from their guard position.", "In Defense mode, robots won't chase beyond this distance.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [15.0]")
                .defineInRange("base-defence-range", SharedConfigs.Common.BaseDefenceRange, ConfigBounds.DEFENSE_RANGE_MIN, ConfigBounds.DEFENSE_RANGE_MAX);

        BASE_DEFENCE_WARP_RANGE = BUILDER
                .comment("How far (in blocks) robots can be from guard position before teleporting back.", "Prevents robots from getting stuck too far from their post.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [10.0]")
                .defineInRange("base-defence-warp-range", SharedConfigs.Common.BaseDefenceWarpRange, ConfigBounds.DEFENSE_RANGE_MIN, ConfigBounds.DEFENSE_RANGE_MAX);
        BUILDER.pop();

        BUILDER.push("Protection");
        PROTECTION_LIMIT_FIRE = BUILDER
                .comment("Maximum percentage of fire damage robots can resist.", "At 80%, robots take only 20% of fire damage. At 100%, they're immune to fire.", "Range: 0 to 100", "Example: [80]")
                .defineInRange("limit-fire", SharedConfigs.Common.ProtectionLimitFire, ConfigBounds.PROTECTION_LIMIT_MIN, ConfigBounds.PROTECTION_LIMIT_MAX);

        PROTECTION_LIMIT_FALL = BUILDER
                .comment("Maximum percentage of fall damage robots can resist.", "At 80%, robots take only 20% of fall damage. At 100%, they never take fall damage.", "Range: 0 to 100", "Example: [80]")
                .defineInRange("limit-fall", SharedConfigs.Common.ProtectionLimitFall, ConfigBounds.PROTECTION_LIMIT_MIN, ConfigBounds.PROTECTION_LIMIT_MAX);

        PROTECTION_LIMIT_BLAST = BUILDER
                .comment("Maximum percentage of explosion damage robots can resist.", "At 80%, robots take only 20% of explosion damage. At 100%, they're immune to explosions.", "Range: 0 to 100", "Example: [80]")
                .defineInRange("limit-blast", SharedConfigs.Common.ProtectionLimitBlast, ConfigBounds.PROTECTION_LIMIT_MIN, ConfigBounds.PROTECTION_LIMIT_MAX);

        PROTECTION_LIMIT_PROJECTILE = BUILDER
                .comment("Maximum percentage of projectile damage robots can resist.", "At 80%, robots take only 20% of arrow/projectile damage. At 100%, they're immune.", "Range: 0.0 to 100", "Example: [80]")
                .defineInRange("limit-projectile", SharedConfigs.Common.ProtectionLimitProjectile, ConfigBounds.PROTECTION_LIMIT_MIN, ConfigBounds.PROTECTION_LIMIT_MAX);

        ENABLE_ENCHANTED_BOOK_PROTECTION = BUILDER
                .comment("Allow robots to consume enchanted books to increase protection values.", "Books with Fire/Blast/Feather Falling/Projectile Protection enchantments can be fed to robots.", "Formula: protectionGain = enchantmentLevel × contribution × maxProtection", "Example: Fire Protection II with 25% contribution and max=80 gives 40 points")
                .define("enable-enchanted-book-protection", SharedConfigs.Common.EnableEnchantedBookProtection);

        ENCHANTED_BOOK_CONTRIBUTION = BUILDER
                .comment("Percentage contribution per enchantment level (0.25 = 25%).", "Determines how much protection each enchantment level provides.", "Formula: protectionGain = level × percentage × maxProtection", "Example: Level II with 25% = 2 × 0.25 × 80 = 40 points", "Range: 0.01 to 1.0")
                .defineInRange("enchanted-book-contribution", SharedConfigs.Common.EnchantedBookContributionPercentage, 0.01, 1.0);
        BUILDER.pop();

        BUILDER.push("Smart Core Retrieval");
        ENABLE_SMART_CORE_RETRIEVAL = BUILDER
                .comment("Whether robot cores automatically go to your inventory when robots die nearby.", "When enabled, you don't need to pick up cores manually if you're close enough.", "Example: [true]")
                .define("enable-smart-core-retrieval", SharedConfigs.Common.EnableSmartCoreRetrieval);

        SMART_CORE_RETRIEVAL_DISTANCE = BUILDER
                .comment("How close (in blocks) you need to be for automatic core retrieval to work.", "If your robot dies within this distance, the core goes straight to your inventory.", "Range: 0.0 to 128.0", "Example: [16.0]")
                .defineInRange("smart-core-retrieval-distance", SharedConfigs.Common.SmartCoreRetrievalDistance, ConfigBounds.SMART_CORE_DISTANCE_MIN, ConfigBounds.SMART_CORE_DISTANCE_MAX);
        BUILDER.pop();

        BUILDER.push("AI Behavior");

        BUILDER.push("Follow Mode");
        OWNER_STILL_THRESHOLD = BUILDER
                .comment("How long (in ticks) their owner must stand still before robots start wandering.", "20 ticks = 1 second. Robots stay put if you keep moving.", "Range: 0 to 6000", "Example: [100] (5 seconds)")
                .defineInRange("owner-still-threshold", SharedConfigs.Common.OwnerStillThreshold, ConfigBounds.OWNER_STILL_THRESHOLD_MIN, ConfigBounds.OWNER_STILL_THRESHOLD_MAX);

        WANDER_CHECK_INTERVAL = BUILDER
                .comment("How often (in ticks) robots check if they should wander.", "20 ticks = 1 second. Lower values make robots more responsive.", "Range: 100 to 6000", "Example: [200] (10 seconds)")
                .defineInRange("wander-check-interval", SharedConfigs.Common.WanderCheckInterval, ConfigBounds.WANDER_CHECK_INTERVAL_MIN, ConfigBounds.WANDER_CHECK_INTERVAL_MAX);

        WANDER_CHANCE = BUILDER
                .comment("Chance robots will wander when their owner is standing still.", "0.15 = 15% chance per check. Higher values make robots wander more often.", "Range: 0.0 to 1.0", "Example: [0.15] (15%)")
                .defineInRange("wander-chance", SharedConfigs.Common.WanderChance, ConfigBounds.WANDER_CHANCE_MIN, ConfigBounds.WANDER_CHANCE_MAX);

        WANDER_RADIUS_MIN = BUILDER
                .comment("Minimum distance (in blocks) robots will wander from their owner.", "Robots won't wander closer than this.", "Range: 1.0 to 32.0", "Example: [3.0]")
                .defineInRange("wander-radius-min", SharedConfigs.Common.WanderRadiusMin, ConfigBounds.WANDER_RADIUS_MIN, ConfigBounds.WANDER_RADIUS_MAX);

        WANDER_RADIUS_MAX = BUILDER
                .comment("Maximum distance (in blocks) robots will wander from their owner.", "Robots won't wander farther than this.", "Range: 1.0 to 32.0", "Example: [6.0]")
                .defineInRange("wander-radius-max", SharedConfigs.Common.WanderRadiusMax, ConfigBounds.WANDER_RADIUS_MIN, ConfigBounds.WANDER_RADIUS_MAX);

        WANDER_DURATION_MIN = BUILDER
                .comment("Minimum time (in ticks) robots will wander before returning.", "20 ticks = 1 second. Shorter wanders feel more cautious.", "Range: 20 to 6000", "Example: [100] (5 seconds)")
                .defineInRange("wander-duration-min", SharedConfigs.Common.WanderDurationMin, ConfigBounds.WANDER_DURATION_MIN, ConfigBounds.WANDER_DURATION_MAX);

        WANDER_DURATION_MAX = BUILDER
                .comment("Maximum time (in ticks) robots will wander before returning.", "20 ticks = 1 second. Longer wanders make robots more independent.", "Range: 20 to 6000", "Example: [200] (10 seconds)")
                .defineInRange("wander-duration-max", SharedConfigs.Common.WanderDurationMax, ConfigBounds.WANDER_DURATION_MIN, ConfigBounds.WANDER_DURATION_MAX);

        WANDER_COOLDOWN_MIN = BUILDER
                .comment("Minimum time (in ticks) before robots can wander again.", "20 ticks = 1 second. Prevents constant wandering.", "Range: 100 to 12000", "Example: [400] (20 seconds)")
                .defineInRange("wander-cooldown-min", SharedConfigs.Common.WanderCooldownMin, ConfigBounds.WANDER_COOLDOWN_MIN, ConfigBounds.WANDER_COOLDOWN_MAX);

        WANDER_COOLDOWN_MAX = BUILDER
                .comment("Maximum time (in ticks) before robots can wander again.", "20 ticks = 1 second. Adds variety to wandering behavior.", "Range: 100 to 12000", "Example: [800] (40 seconds)")
                .defineInRange("wander-cooldown-max", SharedConfigs.Common.WanderCooldownMax, ConfigBounds.WANDER_COOLDOWN_MIN, ConfigBounds.WANDER_COOLDOWN_MAX);
        BUILDER.pop();

        BUILDER.push("Defense Mode");
        PATROL_DURATION_MIN = BUILDER
                .comment("Minimum time (in ticks) robots patrol around their guard position.", "20 ticks = 1 second. In Defense mode, robots walk around looking for threats.", "Range: 100 to 6000", "Example: [600] (30 seconds)")
                .defineInRange("patrol-duration-min", SharedConfigs.Common.PatrolDurationMin, ConfigBounds.PATROL_DURATION_MIN, ConfigBounds.PATROL_DURATION_MAX);

        PATROL_DURATION_MAX = BUILDER
                .comment("Maximum time (in ticks) robots patrol around their guard position.", "20 ticks = 1 second. Longer patrols make robots cover more area.", "Range: 100 to 6000", "Example: [900] (45 seconds)")
                .defineInRange("patrol-duration-max", SharedConfigs.Common.PatrolDurationMax, ConfigBounds.PATROL_DURATION_MIN, ConfigBounds.PATROL_DURATION_MAX);

        GUARD_DURATION_MIN = BUILDER
                .comment("Minimum time (in ticks) robots stand guard and look around.", "20 ticks = 1 second. In Defense mode, robots alternate between patrolling and guarding.", "Range: 100 to 6000", "Example: [400] (20 seconds)")
                .defineInRange("guard-duration-min", SharedConfigs.Common.GuardDurationMin, ConfigBounds.GUARD_DURATION_MIN, ConfigBounds.GUARD_DURATION_MAX);

        GUARD_DURATION_MAX = BUILDER
                .comment("Maximum time (in ticks) robots stand guard and look around.", "20 ticks = 1 second. Longer guard times make robots more watchful.", "Range: 100 to 6000", "Example: [600] (30 seconds)")
                .defineInRange("guard-duration-max", SharedConfigs.Common.GuardDurationMax, ConfigBounds.GUARD_DURATION_MIN, ConfigBounds.GUARD_DURATION_MAX);

        PATROL_PAUSE_DURATION_MIN = BUILDER
                .comment("Minimum time (in ticks) robots pause at each patrol point.", "20 ticks = 1 second. Brief pauses make patrolling look more natural.", "Range: 10 to 600", "Example: [40] (2 seconds)")
                .defineInRange("patrol-pause-duration-min", SharedConfigs.Common.PatrolPauseDurationMin, ConfigBounds.PATROL_PAUSE_DURATION_MIN, ConfigBounds.PATROL_PAUSE_DURATION_MAX);

        PATROL_PAUSE_DURATION_MAX = BUILDER
                .comment("Maximum time (in ticks) robots pause at each patrol point.", "20 ticks = 1 second. Longer pauses make robots more observant.", "Range: 10 to 600", "Example: [80] (4 seconds)")
                .defineInRange("patrol-pause-duration-max", SharedConfigs.Common.PatrolPauseDurationMax, ConfigBounds.PATROL_PAUSE_DURATION_MIN, ConfigBounds.PATROL_PAUSE_DURATION_MAX);

        GUARD_ROTATION_SPEED = BUILDER
                .comment("How fast robots rotate their head while guarding.", "Higher values make robots scan faster. Lower values look more deliberate.", "Range: 0.01 to 0.5", "Example: [0.05]")
                .defineInRange("guard-rotation-speed", SharedConfigs.Common.GuardRotationSpeed, ConfigBounds.GUARD_ROTATION_SPEED_MIN, ConfigBounds.GUARD_ROTATION_SPEED_MAX);
        BUILDER.pop();

        BUILDER.push("Combat Radius");
        ENABLE_COMBAT_RADIUS_PARTICLES = BUILDER
                .comment("Whether to show smoke particles when robots can't chase enemies further.", "Visual feedback when robots hit their chase distance limit in Defense mode.", "Example: [true]")
                .define("enable-combat-radius-particles", SharedConfigs.Common.EnableCombatRadiusParticles);

        COMBAT_RADIUS_PARTICLE_COUNT = BUILDER
                .comment("How many smoke particles appear when robots hit chase limit.", "More particles make the effect more visible.", "Range: 1 to 50", "Example: [8]")
                .defineInRange("combat-radius-particle-count", SharedConfigs.Common.CombatRadiusParticleCount, ConfigBounds.COMBAT_RADIUS_PARTICLE_COUNT_MIN, ConfigBounds.COMBAT_RADIUS_PARTICLE_COUNT_MAX);

        COMBAT_RADIUS_PARTICLE_SPREAD = BUILDER
                .comment("How spread out the smoke particles are.", "Higher values create a wider particle cloud.", "Range: 0.1 to 2.0", "Example: [0.3]")
                .defineInRange("combat-radius-particle-spread", SharedConfigs.Common.CombatRadiusParticleSpread, ConfigBounds.COMBAT_RADIUS_PARTICLE_SPREAD_MIN, ConfigBounds.COMBAT_RADIUS_PARTICLE_SPREAD_MAX);
        BUILDER.pop();

        BUILDER.push("Animation");
        STANDBY_TO_SIT_DELAY_MIN = BUILDER
                .comment("Minimum time (in ticks) before idle robots sit down.", "20 ticks = 1 second. In Standby mode, robots eventually sit if nothing is happening.", "Range: 100 to 12000", "Example: [600] (30 seconds)")
                .defineInRange("standby-to-sit-delay-min", SharedConfigs.Common.StandbyToSitDelayMin, ConfigBounds.STANDBY_TO_SIT_DELAY_MIN, ConfigBounds.STANDBY_TO_SIT_DELAY_MAX);

        STANDBY_TO_SIT_DELAY_MAX = BUILDER
                .comment("Maximum time (in ticks) before idle robots sit down.", "20 ticks = 1 second. Adds variety to when robots decide to sit.", "Range: 100 to 12000", "Example: [1800] (90 seconds)")
                .defineInRange("standby-to-sit-delay-max", SharedConfigs.Common.StandbyToSitDelayMax, ConfigBounds.STANDBY_TO_SIT_DELAY_MIN, ConfigBounds.STANDBY_TO_SIT_DELAY_MAX);
        BUILDER.pop();

        BUILDER.pop();

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

            // Generate double configuration entries (NeoForge uses Double instead of Float)
            for (String configType : ConfigKeyGenerator.FLOAT_CONFIG_TYPES) {
                String key = ConfigKeyGenerator.generateKey(variant, configType);
                
                ENTITY_DOUBLE_CONFIGS.put(key, BUILDER
                    .comment(getConfigComment(configType))
                    .defineInRange(key, (double) getDefaultFloatValue(variant, configType),
                                  (double) getMinFloatValue(configType), (double) getMaxFloatValue(configType)));
            }

            // Phase 2: Add serialized config support
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

    private static final List<Runnable> onLoadCallbacks = new ArrayList<>();

    // -- Helper Methods for Dynamic Configuration --

    /**
     * Gets configuration comment for specific config type.
     * 
     * @param configType the configuration type identifier
     * @return comment string for config file
     */
    private static String getConfigComment(String configType) {
        return switch (configType) {
            case LovelyConstant.CONFIG_MAX_LEVEL -> "Highest level this robot type can reach. Higher levels unlock better stats and abilities.";
            case LovelyConstant.CONFIG_ATTACK_SPEED -> "How fast this robot type attacks (attacks per second). Higher values mean faster attacks.";
            case LovelyConstant.CONFIG_MOVEMENT_SPEED -> "Base movement speed for this robot type. Higher values make robots move faster.";
            case LovelyConstant.CONFIG_BASE_TOUGHNESS -> "Base toughness for this robot type. Reduces damage from strong attacks.";
            case LovelyConstant.CONFIG_BASE_HP -> "Base HP value for combat level calculations. Used by CombatLevelFeature to calculate HP at each level.";
            case LovelyConstant.CONFIG_BASE_ATTACK -> "Base attack value for combat level calculations. Used by CombatLevelFeature to calculate attack damage at each level.";
            case LovelyConstant.CONFIG_BASE_DEFENSE -> "Base defense value for combat level calculations. Used by CombatLevelFeature to calculate armor and toughness at each level.";
            default -> "Configuration value for " + configType;
        };
    } // getConfigComment()

    /**
     * Gets default integer value for specific variant and config type.
     * 
     * @param variant the robot variant identifier
     * @param configType the configuration type identifier
     * @return default integer value
     */
    private static int getDefaultIntValue(String variant, String configType) {
        SharedConfigs.EntityConfigData data = net.heriazone.lovelylib.source.legacy.LegacyConfigs.getDefaultConfig(variant);
        return switch (configType) {
            case LovelyConstant.CONFIG_MAX_LEVEL -> data.maxLevel;
            case LovelyConstant.CONFIG_BASE_HP -> data.baseHp;
            case LovelyConstant.CONFIG_BASE_ATTACK -> data.baseAttack;
            case LovelyConstant.CONFIG_BASE_DEFENSE -> data.baseDefense;
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
        SharedConfigs.EntityConfigData data = net.heriazone.lovelylib.source.legacy.LegacyConfigs.getDefaultConfig(variant);
        return switch (configType) {
            case LovelyConstant.CONFIG_ATTACK_SPEED -> data.attackSpeed;
            case LovelyConstant.CONFIG_MOVEMENT_SPEED -> data.movementSpeed;
            case LovelyConstant.CONFIG_BASE_TOUGHNESS -> data.baseToughness;
            default -> 0;
        };
    } // getDefaultFloatValue()

    /**
     * Gets minimum integer value for specific config type.
     * 
     * @param configType the configuration type identifier
     * @return minimum integer value
     */
    private static int getMinIntValue(String configType) {
        return switch (configType) {
            case LovelyConstant.CONFIG_MAX_LEVEL -> ConfigBounds.MAX_LEVEL_MIN;
            case LovelyConstant.CONFIG_BASE_HP -> ConfigBounds.BASE_HP_MIN;
            case LovelyConstant.CONFIG_BASE_ATTACK -> ConfigBounds.BASE_ATTACK_MIN;
            case LovelyConstant.CONFIG_BASE_DEFENSE -> ConfigBounds.BASE_DEFENSE_MIN;
            default -> 0;
        };
    } // getMinIntValue()

    /**
     * Gets maximum integer value for specific config type.
     * 
     * @param configType the configuration type identifier
     * @return maximum integer value
     */
    private static int getMaxIntValue(String configType) {
        return switch (configType) {
            case LovelyConstant.CONFIG_MAX_LEVEL -> ConfigBounds.MAX_LEVEL_MAX;
            case LovelyConstant.CONFIG_BASE_HP -> ConfigBounds.BASE_HP_MAX;
            case LovelyConstant.CONFIG_BASE_ATTACK -> ConfigBounds.BASE_ATTACK_MAX;
            case LovelyConstant.CONFIG_BASE_DEFENSE -> ConfigBounds.BASE_DEFENSE_MAX;
            default -> Integer.MAX_VALUE;
        };
    } // getMaxIntValue()

    /**
     * Gets minimum float value for specific config type.
     * 
     * @param configType the configuration type identifier
     * @return minimum float value
     */
    private static float getMinFloatValue(String configType) {
        return switch (configType) {
            case LovelyConstant.CONFIG_ATTACK_SPEED -> ConfigBounds.ATTACK_SPEED_MIN;
            case LovelyConstant.CONFIG_MOVEMENT_SPEED -> ConfigBounds.MOVEMENT_SPEED_MIN;
            case LovelyConstant.CONFIG_BASE_TOUGHNESS -> ConfigBounds.BASE_TOUGHNESS_MIN;
            default -> 0.0f;
        };
    } // getMinFloatValue()

    /**
     * Gets maximum float value for specific config type.
     * 
     * @param configType the configuration type identifier
     * @return maximum float value
     */
    private static float getMaxFloatValue(String configType) {
        return switch (configType) {
            case LovelyConstant.CONFIG_ATTACK_SPEED -> ConfigBounds.ATTACK_SPEED_MAX;
            case LovelyConstant.CONFIG_MOVEMENT_SPEED -> ConfigBounds.MOVEMENT_SPEED_MAX;
            case LovelyConstant.CONFIG_BASE_TOUGHNESS -> ConfigBounds.BASE_TOUGHNESS_MAX;
            default -> Float.MAX_VALUE;
        };
    } // getMaxFloatValue()

    // -- Custom Methods --

    /**
     * Registers configuration with NeoForge mod container.
     * <p>
     * <b>Timing:</b> Called during mod construction to register config spec
     * with NeoForge's configuration system.
     *
     * @param modContainer the mod container for config registration
     */
    public static void register(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, LegacyConfigs.SPEC);
    } // register ()

    // -- Event Methods --

    /**
     * Loads configuration values when config file is loaded or reloaded.
     * <p>
     * <b>Initialization:</b> Called during mod initialization and on config reload
     * to populate static config fields. Forge automatically creates config file with
     * defaults if not present.
     * <p>
     * <b>Error Recovery:</b> Missing file triggers creation with defaults by Forge.
     * Invalid values use defaults defined in ForgeConfigSpec. Parse errors handled
     * by Forge's config validation with error logs.
     * <p>
     * <b>Validation:</b> ForgeConfigSpec validates numeric ranges and types. Falls
     * back to defaults for invalid entries automatically.
     *
     * @param event the mod config event (loading or reloading)
     */
    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        //Legacy.LOGGER.info("Loading configuration from: {}", event.getConfig().getFileName());

        // GENERAL
        SharedConfigs.Common.OwnerMaxRobotNum = ConfigBounds.validateOwnerMaxRobot(OWNER_MAX_ROBOT_NUM.get());
        SharedConfigs.Common.MovementMeleeAttack = MOVEMENT_MELEE_ATTACK.get().floatValue();
        SharedConfigs.Common.MovementFollowOwner = MOVEMENT_FOLLOW_OWNER.get().floatValue();
        SharedConfigs.Common.MovementWanderAround = MOVEMENT_WANDER_AROUND.get().floatValue();
        SharedConfigs.Common.FollowDistanceMax = FOLLOW_DISTANCE_MAX.get().floatValue();
        SharedConfigs.Common.FollowDistanceMin = FOLLOW_DISTANCE_MIN.get().floatValue();
        SharedConfigs.Common.LookRange = LOOK_RANGE.get().floatValue();

        // -- RENDERER --
        SharedConfigs.Client.ShadowRadius = SHADOW_RADIUS.get().floatValue();

        // -- LEVEL | EXPERIENCE ---
        SharedConfigs.Common.ExperienceBase = EXPERIENCE_BASE.get();
        SharedConfigs.Common.ExperienceMultiplier = EXPERIENCE_MULTIPLIER.get();

        // -- COMBAT --
        SharedConfigs.Common.FriendlyFire = FRIENDLY_FIRE.get();
        SharedConfigs.Common.AttackChance = ATTACK_CHANCE.get();
        SharedConfigs.Common.HealInterval = HEAL_INTERVAL.get();
        SharedConfigs.Common.WaryTime = WARY_TIME.get();
        SharedConfigs.Common.GlobalAutoHeal = GLOBAL_AUTO_HEAL.get();
        SharedConfigs.Common.LootEnchantment = LOOT_ENCHANTMENT.get();
        SharedConfigs.Common.LootEnchantmentLevel = LOOT_ENCHANTMENT_LEVEL.get();
        SharedConfigs.Common.MaxLootEnchantment = MAX_LOOT_ENCHANTMENT.get();
        SharedConfigs.Common.BaseDefenceRange = BASE_DEFENCE_RANGE.get().floatValue();
        SharedConfigs.Common.BaseDefenceWarpRange = BASE_DEFENCE_WARP_RANGE.get().floatValue();

        // -- PROTECTION --
        SharedConfigs.Common.ProtectionLimitFire = PROTECTION_LIMIT_FIRE.get();
        SharedConfigs.Common.ProtectionLimitFall = PROTECTION_LIMIT_FALL.get();
        SharedConfigs.Common.ProtectionLimitBlast = PROTECTION_LIMIT_BLAST.get();
        SharedConfigs.Common.ProtectionLimitProjectile = PROTECTION_LIMIT_PROJECTILE.get();
        SharedConfigs.Common.EnableEnchantedBookProtection = ENABLE_ENCHANTED_BOOK_PROTECTION.get();
        SharedConfigs.Common.EnchantedBookContributionPercentage = ENCHANTED_BOOK_CONTRIBUTION.get();

        // -- SMART CORE RETRIEVAL --
        SharedConfigs.Common.EnableSmartCoreRetrieval = ENABLE_SMART_CORE_RETRIEVAL.get();
        SharedConfigs.Common.SmartCoreRetrievalDistance = SMART_CORE_RETRIEVAL_DISTANCE.get();

        // -- AI BEHAVIOR --
        SharedConfigs.Common.OwnerStillThreshold = OWNER_STILL_THRESHOLD.get();
        SharedConfigs.Common.WanderCheckInterval = WANDER_CHECK_INTERVAL.get();
        SharedConfigs.Common.WanderChance = WANDER_CHANCE.get();
        SharedConfigs.Common.WanderRadiusMin = WANDER_RADIUS_MIN.get();
        SharedConfigs.Common.WanderRadiusMax = WANDER_RADIUS_MAX.get();
        SharedConfigs.Common.WanderDurationMin = WANDER_DURATION_MIN.get();
        SharedConfigs.Common.WanderDurationMax = WANDER_DURATION_MAX.get();
        SharedConfigs.Common.WanderCooldownMin = WANDER_COOLDOWN_MIN.get();
        SharedConfigs.Common.WanderCooldownMax = WANDER_COOLDOWN_MAX.get();
        SharedConfigs.Common.PatrolDurationMin = PATROL_DURATION_MIN.get();
        SharedConfigs.Common.PatrolDurationMax = PATROL_DURATION_MAX.get();
        SharedConfigs.Common.GuardDurationMin = GUARD_DURATION_MIN.get();
        SharedConfigs.Common.GuardDurationMax = GUARD_DURATION_MAX.get();
        SharedConfigs.Common.PatrolPauseDurationMin = PATROL_PAUSE_DURATION_MIN.get();
        SharedConfigs.Common.PatrolPauseDurationMax = PATROL_PAUSE_DURATION_MAX.get();
        SharedConfigs.Common.GuardRotationSpeed = GUARD_ROTATION_SPEED.get();
        SharedConfigs.Common.EnableCombatRadiusParticles = ENABLE_COMBAT_RADIUS_PARTICLES.get();
        SharedConfigs.Common.CombatRadiusParticleCount = COMBAT_RADIUS_PARTICLE_COUNT.get();
        SharedConfigs.Common.CombatRadiusParticleSpread = COMBAT_RADIUS_PARTICLE_SPREAD.get();

        // -- ANIMATION --
        SharedConfigs.Common.StandbyToSitDelayMin = STANDBY_TO_SIT_DELAY_MIN.get();
        SharedConfigs.Common.StandbyToSitDelayMax = STANDBY_TO_SIT_DELAY_MAX.get();

        // -- DYNAMIC ENTITY CONFIGURATION LOADING --
        
        // Clear ConfigAccessLayer caches
        ConfigAccessLayer.clearCaches();
        
        // Load dynamic entity configurations from NeoForge config
        for (String variant : LovelyConstant.ALL_VARIANTS) {
            // Load integer configurations
            for (String configType : ConfigKeyGenerator.INT_CONFIG_TYPES) {
                String key = ConfigKeyGenerator.generateKey(variant, configType);
                ModConfigSpec.ConfigValue<Integer> configValue = ENTITY_INT_CONFIGS.get(key);
                if (configValue != null) {
                    ConfigAccessLayer.updateIntConfigCache(key, configValue.get());
                }
            }
            
            // Load double configurations (NeoForge uses Double instead of Float)
            for (String configType : ConfigKeyGenerator.FLOAT_CONFIG_TYPES) {
                String key = ConfigKeyGenerator.generateKey(variant, configType);
                ModConfigSpec.ConfigValue<Double> configValue = ENTITY_DOUBLE_CONFIGS.get(key);
                if (configValue != null) {
                    ConfigAccessLayer.updateFloatConfigCache(key, configValue.get().floatValue());
                }
            }
            
            // Load serialized configurations (Phase 2)
            String serializedKey = variant + "-config";
            ModConfigSpec.ConfigValue<String> serializedValue = ENTITY_SERIALIZED_CONFIGS.get(serializedKey);
            if (serializedValue != null) {
                ConfigAccessLayer.updateSerializedConfigCache(serializedKey, serializedValue.get());
            }
        }
        
        // Populate LegacyConfigs.Entities HashMap using ConfigAccessLayer
        net.heriazone.lovelylib.source.legacy.LegacyConfigs.Entities.clear();
        for (String variant : LovelyConstant.ALL_VARIANTS) {
            SharedConfigs.EntityConfigData entityConfig = ConfigAccessLayer.getEntityConfig(variant);
            net.heriazone.lovelylib.source.legacy.LegacyConfigs.Entities.put(variant, entityConfig);
        }

        //Legacy.LOGGER.info("Configuration loaded successfully");
        onLoadCallbacks.forEach(Runnable::run);
    } // onLoad ()

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
        return new SharedConfigs.EntityConfigData.Builder()
                .maxLevel(getDefaultMaxLevel(variant))
                .attackSpeed(getDefaultAttackSpeed(variant))
                .movementSpeed(getDefaultMovementSpeed(variant))
                .baseToughness(getDefaultBaseToughness(variant))
                .baseHp(getDefaultBaseHp(variant))
                .baseAttack(getDefaultBaseAttack(variant))
                .baseDefense(getDefaultBaseDefense(variant))
                .build();
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

    public static void onLoadCallback(Runnable callback) {
        onLoadCallbacks.add(callback);
    } // onLoadCallback ()

    /**
     * Manually triggers config reload for runtime updates.
     * <p>
     * <b>Runtime Reload:</b> Allows server admins to update config without restart.
     * Forge automatically validates all values before applying to prevent invalid state.
     * <p>
     * <b>Change Logging:</b> Logs config reload event for audit trail.
     * <p>
     * <i>Note:</i> Forge's config system handles the actual reload through ModConfigEvent.
     * This method provides a programmatic way to trigger reload if needed.
     */
    public static void reload() {
        //Legacy.LOGGER.info("Reloading configuration...");
        // Forge handles reload automatically through ModConfigEvent
        // This method exists for API compatibility and explicit reload requests
        //Legacy.LOGGER.info("Configuration reload requested - Forge will handle reload on next config change");
    } // reload()

} // Class: LegacyConfigs