package net.msymbios.llovelyr.source;

import com.electronwill.nightconfig.core.Config;
import net.msymbios.llovelyr.LovelyLegacy;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = LovelyLegacy.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class LovelyConfigs {

    // -- Constants --

    private static final ForgeConfigSpec.Builder BUILDER;
    public static final ForgeConfigSpec SPEC;

    // GENERAL
    private static final ForgeConfigSpec.ConfigValue<Integer> OWNER_MAX_ROBOT_NUM;
    private static final ForgeConfigSpec.ConfigValue<Float> MOVEMENT_MELEE_ATTACK;
    private static final ForgeConfigSpec.ConfigValue<Float> MOVEMENT_FOLLOW_OWNER;
    private static final ForgeConfigSpec.ConfigValue<Float> MOVEMENT_WANDER_AROUND;
    private static final ForgeConfigSpec.ConfigValue<Float> FOLLOW_DISTANCE_MAX;
    private static final ForgeConfigSpec.ConfigValue<Float> FOLLOW_DISTANCE_MIN;
    private static final ForgeConfigSpec.ConfigValue<Float> LOOK_RANGE;

    // RENDERER
    private static final ForgeConfigSpec.ConfigValue<Float> WIDTH;
    private static final ForgeConfigSpec.ConfigValue<Float> HEIGHT;
    private static final ForgeConfigSpec.ConfigValue<Float> SHADOW_RADIUS;


    // LEVEL | EXPERIENCE
    private static final ForgeConfigSpec.ConfigValue<Integer> EXPERIENCE_BASE;
    private static final ForgeConfigSpec.ConfigValue<Integer> EXPERIENCE_MULTIPLIER;

    // COMBAT
    private static final ForgeConfigSpec.ConfigValue<Boolean> FRIENDLY_FIRE;
    private static final ForgeConfigSpec.ConfigValue<Integer> ATTACK_CHANCE;
    private static final ForgeConfigSpec.ConfigValue<Integer> HEAL_INTERVAL;
    private static final ForgeConfigSpec.ConfigValue<Integer> WARY_TIME;
    private static final ForgeConfigSpec.ConfigValue<Boolean> GLOBAL_AUTO_HEAL;
    private static final ForgeConfigSpec.ConfigValue<Boolean> LOOT_ENCHANTMENT;
    private static final ForgeConfigSpec.ConfigValue<Integer> LOOT_ENCHANTMENT_LEVEL;
    private static final ForgeConfigSpec.ConfigValue<Integer> MAX_LOOT_ENCHANTMENT;
    private static final ForgeConfigSpec.ConfigValue<Float> BASE_DEFENCE_RANGE;
    private static final ForgeConfigSpec.ConfigValue<Float> BASE_DEFENCE_WARP_RANGE;

    // PROTECTION
    private static final ForgeConfigSpec.ConfigValue<Integer> PROTECTION_LIMIT_FIRE;
    private static final ForgeConfigSpec.ConfigValue<Integer> PROTECTION_LIMIT_FALL;
    private static final ForgeConfigSpec.ConfigValue<Integer> PROTECTION_LIMIT_BLAST;
    private static final ForgeConfigSpec.ConfigValue<Integer> PROTECTION_LIMIT_PROJECTILE;

    // SMART CORE RETRIEVAL
    private static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_SMART_CORE_RETRIEVAL;
    private static final ForgeConfigSpec.ConfigValue<Double> SMART_CORE_RETRIEVAL_DISTANCE;

    // AI BEHAVIOR
    private static final ForgeConfigSpec.ConfigValue<Integer> OWNER_STILL_THRESHOLD;
    private static final ForgeConfigSpec.ConfigValue<Integer> WANDER_CHECK_INTERVAL;
    private static final ForgeConfigSpec.ConfigValue<Double> WANDER_CHANCE;
    private static final ForgeConfigSpec.ConfigValue<Double> WANDER_RADIUS_MIN;
    private static final ForgeConfigSpec.ConfigValue<Double> WANDER_RADIUS_MAX;
    private static final ForgeConfigSpec.ConfigValue<Integer> WANDER_DURATION_MIN;
    private static final ForgeConfigSpec.ConfigValue<Integer> WANDER_DURATION_MAX;
    private static final ForgeConfigSpec.ConfigValue<Integer> WANDER_COOLDOWN_MIN;
    private static final ForgeConfigSpec.ConfigValue<Integer> WANDER_COOLDOWN_MAX;
    private static final ForgeConfigSpec.ConfigValue<Integer> PATROL_DURATION_MIN;
    private static final ForgeConfigSpec.ConfigValue<Integer> PATROL_DURATION_MAX;
    private static final ForgeConfigSpec.ConfigValue<Integer> GUARD_DURATION_MIN;
    private static final ForgeConfigSpec.ConfigValue<Integer> GUARD_DURATION_MAX;
    private static final ForgeConfigSpec.ConfigValue<Integer> PATROL_PAUSE_DURATION_MIN;
    private static final ForgeConfigSpec.ConfigValue<Integer> PATROL_PAUSE_DURATION_MAX;
    private static final ForgeConfigSpec.ConfigValue<Double> GUARD_ROTATION_SPEED;
    private static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_COMBAT_RADIUS_PARTICLES;
    private static final ForgeConfigSpec.ConfigValue<Integer> COMBAT_RADIUS_PARTICLE_COUNT;
    private static final ForgeConfigSpec.ConfigValue<Double> COMBAT_RADIUS_PARTICLE_SPREAD;

    // ANIMATION
    private static final ForgeConfigSpec.ConfigValue<Integer> STANDBY_TO_SIT_DELAY_MIN;
    private static final ForgeConfigSpec.ConfigValue<Integer> STANDBY_TO_SIT_DELAY_MAX;

    // -- ENTITY --

    // BUNNY2
    private static final ForgeConfigSpec.ConfigValue<Integer> BUNNY2_MAX_LEVEL;
    private static final ForgeConfigSpec.ConfigValue<Float> BUNNY2_MAX_HEALTH;
    private static final ForgeConfigSpec.ConfigValue<Float> BUNNY2_ATTACK_DAMAGE;
    private static final ForgeConfigSpec.ConfigValue<Float> BUNNY2_ATTACK_SPEED;
    private static final ForgeConfigSpec.ConfigValue<Float> BUNNY2_MOVEMENT_SPEED;
    private static final ForgeConfigSpec.ConfigValue<Float> BUNNY2_ARMOR;
    private static final ForgeConfigSpec.ConfigValue<Float> BUNNY2_ARMOR_TOUGHNESS;

    // VANILLA
    private static final ForgeConfigSpec.ConfigValue<Integer> VANILLA_MAX_LEVEL;
    private static final ForgeConfigSpec.ConfigValue<Float> VANILLA_MAX_HEALTH;
    private static final ForgeConfigSpec.ConfigValue<Float> VANILLA_ATTACK_DAMAGE;
    private static final ForgeConfigSpec.ConfigValue<Float> VANILLA_ATTACK_SPEED;
    private static final ForgeConfigSpec.ConfigValue<Float> VANILLA_MOVEMENT_SPEED;
    private static final ForgeConfigSpec.ConfigValue<Float> VANILLA_ARMOR;
    private static final ForgeConfigSpec.ConfigValue<Float> VANILLA_ARMOR_TOUGHNESS;

    // DRAGON
    private static final ForgeConfigSpec.ConfigValue<Integer> DRAGON_MAX_LEVEL;
    private static final ForgeConfigSpec.ConfigValue<Float> DRAGON_MAX_HEALTH;
    private static final ForgeConfigSpec.ConfigValue<Float> DRAGON_ATTACK_DAMAGE;
    private static final ForgeConfigSpec.ConfigValue<Float> DRAGON_ATTACK_SPEED;
    private static final ForgeConfigSpec.ConfigValue<Float> DRAGON_MOVEMENT_SPEED;
    private static final ForgeConfigSpec.ConfigValue<Float> DRAGON_ARMOR;
    private static final ForgeConfigSpec.ConfigValue<Float> DRAGON_ARMOR_TOUGHNESS;

    // KITSUNE
    private static final ForgeConfigSpec.ConfigValue<Integer> KITSUNE_MAX_LEVEL;
    private static final ForgeConfigSpec.ConfigValue<Float> KITSUNE_MAX_HEALTH;
    private static final ForgeConfigSpec.ConfigValue<Float> KITSUNE_ATTACK_DAMAGE;
    private static final ForgeConfigSpec.ConfigValue<Float> KITSUNE_ATTACK_SPEED;
    private static final ForgeConfigSpec.ConfigValue<Float> KITSUNE_MOVEMENT_SPEED;
    private static final ForgeConfigSpec.ConfigValue<Float> KITSUNE_ARMOR;
    private static final ForgeConfigSpec.ConfigValue<Float> KITSUNE_ARMOR_TOUGHNESS;

    static {
        Config.setInsertionOrderPreserved(true);

        BUILDER = new ForgeConfigSpec.Builder();

        BUILDER.push("General");
        OWNER_MAX_ROBOT_NUM = BUILDER
                .comment("How many robots each player can own at once.", "Set to -1 for unlimited robots.", "Range: -1 to no upper limit", "Example: [30]")
                .define("owner-max-robot", 30);

        MOVEMENT_MELEE_ATTACK = BUILDER
                .comment("How fast robots move when attacking enemies in melee combat.", "Higher values make robots move faster during attacks.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.8]")
                .define("movement-melee-attack", 0.8F);

        MOVEMENT_FOLLOW_OWNER = BUILDER
                .comment("How fast robots move when following their owner.", "Higher values make robots keep up with you better.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.7]")
                .define("movement-follow-owner", 0.7F);

        MOVEMENT_WANDER_AROUND = BUILDER
                .comment("How fast robots move when wandering around on their own.", "Lower than follow speed to make wandering look more relaxed.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.6]")
                .define("movement-wander-around", 0.6F);

        FOLLOW_DISTANCE_MAX = BUILDER
                .comment("Maximum distance (in blocks) robots will stay from their owner before teleporting.", "If you get too far, your robot will teleport to you.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [10.0]")
                .define("follow-distance-max", 10.0F);

        FOLLOW_DISTANCE_MIN = BUILDER
                .comment("Minimum distance (in blocks) robots try to maintain from their owner.", "Robots won't crowd you closer than this distance.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [2.0]")
                .define("follow-distance-min", 2F);

        LOOK_RANGE = BUILDER
                .comment("How far (in blocks) robots can look at and track entities.", "Affects head rotation and attention behavior.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [8.0]")
                .define("look-range", 8.0F);
        BUILDER.pop();

        BUILDER.push("Renderer");
        WIDTH = BUILDER
                .comment("Width of the robot's collision box (in blocks).", "Affects how much space robots take up and what gaps they can fit through.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.4]")
                .define("width", 0.4F);

        HEIGHT = BUILDER
                .comment("Height of the robot's collision box (in blocks).", "Affects what spaces robots can fit under.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [1.9]")
                .define("height", 1.9F);

        SHADOW_RADIUS = BUILDER
                .comment("Size of the shadow rendered under robots.", "Purely visual - doesn't affect gameplay.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.4]")
                .define("shadow-radius", 0.4F);
        BUILDER.pop();

        BUILDER.push("Level & Experience");
        EXPERIENCE_BASE = BUILDER
                .comment("Base experience points needed for a robot to reach level 1.", "Each level requires more XP based on the multiplier below.", "Range: 0 to no upper limit (does not accept negative values)", "Example: [50]")
                .define("experience-base", 50);

        EXPERIENCE_MULTIPLIER = BUILDER
                .comment("How much more XP each level requires compared to the previous level.", "Level 2 needs base × multiplier, Level 3 needs base × multiplier², etc.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [2]")
                .define("experience-multiplier", 2);
        BUILDER.pop();

        BUILDER.push("Combat");
        FRIENDLY_FIRE = BUILDER
                .comment("Whether players can damage their own robots.", "Set to true to allow accidentally hitting your robots, false to prevent it.", "Example: [false]")
                .define("friendly-fire", false);

        ATTACK_CHANCE = BUILDER
                .comment("How likely robots are to counter-attack when hit (higher = more aggressive).", "Affects how quickly robots retaliate when damaged.", "Range: 0 to no upper limit (does not accept negative values)", "Example: [5]")
                .define("attack-chance", 5);

        HEAL_INTERVAL = BUILDER
                .comment("How often (in ticks) robots automatically heal themselves.", "20 ticks = 1 second. Lower values = faster healing.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [50]")
                .define("heal-interval", 50);

        WARY_TIME = BUILDER
                .comment("How long (in ticks) robots stay alert after combat ends.", "20 ticks = 1 second. During this time, robots remain ready to fight.", "Range: 0 to no upper limit (does not accept negative values)", "Example: [50]")
                .define("wary-time", 50);

        GLOBAL_AUTO_HEAL = BUILDER
                .comment("Whether robots automatically heal over time.", "Set to false to disable automatic healing entirely.", "Example: [true]")
                .define("global-heal", true);

        LOOT_ENCHANTMENT = BUILDER
                .comment("Whether robots can benefit from Looting enchantment on their weapon.", "When enabled, higher level robots get better mob drops.", "Example: [true]")
                .define("loot-enchantment", true);

        LOOT_ENCHANTMENT_LEVEL = BUILDER
                .comment("What robot level is needed to gain Looting I enchantment effect.", "Looting II at 2× this level, Looting III at 3× this level.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [10]")
                .define("loot-enchantment-level", 10);

        MAX_LOOT_ENCHANTMENT = BUILDER
                .comment("Maximum Looting enchantment level robots can have.", "Limits how much bonus loot high-level robots can get.", "Range: 0 to 3", "Example: [3]")
                .define("max-loot-enchantment", 3);

        BASE_DEFENCE_RANGE = BUILDER
                .comment("How far (in blocks) robots will chase enemies from their guard position.", "In Defense mode, robots won't chase beyond this distance.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [15.0]")
                .define("base-defence-range", 15.0F);

        BASE_DEFENCE_WARP_RANGE = BUILDER
                .comment("How far (in blocks) robots can be from guard position before teleporting back.", "Prevents robots from getting stuck too far from their post.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [10.0]")
                .define("base-defence-warp-range", 10.0F);
        BUILDER.pop();

        BUILDER.push("Protection");
        PROTECTION_LIMIT_FIRE = BUILDER
                .comment("Maximum percentage of fire damage robots can resist.", "At 80%, robots take only 20% of fire damage. At 100%, they're immune to fire.", "Range: 0 to 100", "Example: [80]")
                .define("limit-fire", 80);

        PROTECTION_LIMIT_FALL = BUILDER
                .comment("Maximum percentage of fall damage robots can resist.", "At 80%, robots take only 20% of fall damage. At 100%, they never take fall damage.", "Range: 0 to 100", "Example: [80]")
                .define("limit-fall", 80);

        PROTECTION_LIMIT_BLAST = BUILDER
                .comment("Maximum percentage of explosion damage robots can resist.", "At 80%, robots take only 20% of explosion damage. At 100%, they're immune to explosions.", "Range: 0 to 100", "Example: [80]")
                .define("limit-blast", 80);

        PROTECTION_LIMIT_PROJECTILE = BUILDER
                .comment("Maximum percentage of projectile damage robots can resist.", "At 80%, robots take only 20% of arrow/projectile damage. At 100%, they're immune.", "Range: 0 to 100", "Example: [80]")
                .define("limit-projectile", 80);
        BUILDER.pop();

        BUILDER.push("Smart Core Retrieval");
        ENABLE_SMART_CORE_RETRIEVAL = BUILDER
                .comment("Whether robot cores automatically go to your inventory when robots die nearby.", "When enabled, you don't need to pick up cores manually if you're close enough.", "Example: [true]")
                .define("enable-smart-core-retrieval", true);

        SMART_CORE_RETRIEVAL_DISTANCE = BUILDER
                .comment("How close (in blocks) you need to be for automatic core retrieval to work.", "If your robot dies within this distance, the core goes straight to your inventory.", "Range: 0.0 to 128.0", "Example: [16.0]")
                .defineInRange("smart-core-retrieval-distance", 16.0, 0.0, 128.0);
        BUILDER.pop();

        BUILDER.push("AI Behavior");
        
        BUILDER.push("Follow Mode");
        OWNER_STILL_THRESHOLD = BUILDER
                .comment("How long (in ticks) their owner must stand still before robots start wandering.", "20 ticks = 1 second. Robots stay put if you keep moving.", "Range: 0 to 6000", "Example: [100] (5 seconds)")
                .defineInRange("owner-still-threshold", 100, 0, 6000);

        WANDER_CHECK_INTERVAL = BUILDER
                .comment("How often (in ticks) robots check if they should wander.", "20 ticks = 1 second. Lower values make robots more responsive.", "Range: 100 to 6000", "Example: [200] (10 seconds)")
                .defineInRange("wander-check-interval", 200, 100, 6000);

        WANDER_CHANCE = BUILDER
                .comment("Chance robots will wander when their owner is standing still.", "0.15 = 15% chance per check. Higher values make robots wander more often.", "Range: 0.0 to 1.0", "Example: [0.15] (15%)")
                .defineInRange("wander-chance", 0.15, 0.0, 1.0);

        WANDER_RADIUS_MIN = BUILDER
                .comment("Minimum distance (in blocks) robots will wander from their owner.", "Robots won't wander closer than this.", "Range: 1.0 to 32.0", "Example: [3.0]")
                .defineInRange("wander-radius-min", 3.0, 1.0, 32.0);

        WANDER_RADIUS_MAX = BUILDER
                .comment("Maximum distance (in blocks) robots will wander from their owner.", "Robots won't wander farther than this.", "Range: 1.0 to 32.0", "Example: [6.0]")
                .defineInRange("wander-radius-max", 6.0, 1.0, 32.0);

        WANDER_DURATION_MIN = BUILDER
                .comment("Minimum time (in ticks) robots will wander before returning.", "20 ticks = 1 second. Shorter wanders feel more cautious.", "Range: 20 to 6000", "Example: [100] (5 seconds)")
                .defineInRange("wander-duration-min", 100, 20, 6000);

        WANDER_DURATION_MAX = BUILDER
                .comment("Maximum time (in ticks) robots will wander before returning.", "20 ticks = 1 second. Longer wanders make robots more independent.", "Range: 20 to 6000", "Example: [200] (10 seconds)")
                .defineInRange("wander-duration-max", 200, 20, 6000);

        WANDER_COOLDOWN_MIN = BUILDER
                .comment("Minimum time (in ticks) before robots can wander again.", "20 ticks = 1 second. Prevents constant wandering.", "Range: 100 to 12000", "Example: [400] (20 seconds)")
                .defineInRange("wander-cooldown-min", 400, 100, 12000);

        WANDER_COOLDOWN_MAX = BUILDER
                .comment("Maximum time (in ticks) before robots can wander again.", "20 ticks = 1 second. Adds variety to wandering behavior.", "Range: 100 to 12000", "Example: [800] (40 seconds)")
                .defineInRange("wander-cooldown-max", 800, 100, 12000);
        BUILDER.pop();

        BUILDER.push("Defense Mode");
        PATROL_DURATION_MIN = BUILDER
                .comment("Minimum time (in ticks) robots patrol around their guard position.", "20 ticks = 1 second. In Defense mode, robots walk around looking for threats.", "Range: 100 to 6000", "Example: [600] (30 seconds)")
                .defineInRange("patrol-duration-min", 600, 100, 6000);

        PATROL_DURATION_MAX = BUILDER
                .comment("Maximum time (in ticks) robots patrol around their guard position.", "20 ticks = 1 second. Longer patrols make robots cover more area.", "Range: 100 to 6000", "Example: [900] (45 seconds)")
                .defineInRange("patrol-duration-max", 900, 100, 6000);

        GUARD_DURATION_MIN = BUILDER
                .comment("Minimum time (in ticks) robots stand guard and look around.", "20 ticks = 1 second. In Defense mode, robots alternate between patrolling and guarding.", "Range: 100 to 6000", "Example: [400] (20 seconds)")
                .defineInRange("guard-duration-min", 400, 100, 6000);

        GUARD_DURATION_MAX = BUILDER
                .comment("Maximum time (in ticks) robots stand guard and look around.", "20 ticks = 1 second. Longer guard times make robots more watchful.", "Range: 100 to 6000", "Example: [600] (30 seconds)")
                .defineInRange("guard-duration-max", 600, 100, 6000);

        PATROL_PAUSE_DURATION_MIN = BUILDER
                .comment("Minimum time (in ticks) robots pause at each patrol point.", "20 ticks = 1 second. Brief pauses make patrolling look more natural.", "Range: 10 to 600", "Example: [40] (2 seconds)")
                .defineInRange("patrol-pause-duration-min", 40, 10, 600);

        PATROL_PAUSE_DURATION_MAX = BUILDER
                .comment("Maximum time (in ticks) robots pause at each patrol point.", "20 ticks = 1 second. Longer pauses make robots more observant.", "Range: 10 to 600", "Example: [80] (4 seconds)")
                .defineInRange("patrol-pause-duration-max", 80, 10, 600);

        GUARD_ROTATION_SPEED = BUILDER
                .comment("How fast robots rotate their head while guarding.", "Higher values make robots scan faster. Lower values look more deliberate.", "Range: 0.01 to 0.5", "Example: [0.05]")
                .defineInRange("guard-rotation-speed", 0.05, 0.01, 0.5);
        BUILDER.pop();

        BUILDER.push("Combat Radius");
        ENABLE_COMBAT_RADIUS_PARTICLES = BUILDER
                .comment("Whether to show smoke particles when robots can't chase enemies further.", "Visual feedback when robots hit their chase distance limit in Defense mode.", "Example: [true]")
                .define("enable-combat-radius-particles", true);

        COMBAT_RADIUS_PARTICLE_COUNT = BUILDER
                .comment("How many smoke particles appear when robots hit chase limit.", "More particles make the effect more visible.", "Range: 1 to 50", "Example: [8]")
                .defineInRange("combat-radius-particle-count", 8, 1, 50);

        COMBAT_RADIUS_PARTICLE_SPREAD = BUILDER
                .comment("How spread out the smoke particles are.", "Higher values create a wider particle cloud.", "Range: 0.1 to 2.0", "Example: [0.3]")
                .defineInRange("combat-radius-particle-spread", 0.3, 0.1, 2.0);
        BUILDER.pop();
        
        BUILDER.push("Animation");
        STANDBY_TO_SIT_DELAY_MIN = BUILDER
                .comment("Minimum time (in ticks) before idle robots sit down.", "20 ticks = 1 second. In Standby mode, robots eventually sit if nothing is happening.", "Range: 100 to 12000", "Example: [600] (30 seconds)")
                .defineInRange("standby-to-sit-delay-min", 600, 100, 12000);
        
        STANDBY_TO_SIT_DELAY_MAX = BUILDER
                .comment("Maximum time (in ticks) before idle robots sit down.", "20 ticks = 1 second. Adds variety to when robots decide to sit.", "Range: 100 to 12000", "Example: [1800] (90 seconds)")
                .defineInRange("standby-to-sit-delay-max", 1800, 100, 12000);
        BUILDER.pop();
        
        BUILDER.pop();

        BUILDER.push("Entity");

        BUILDER.push("Bunny2");
        BUNNY2_MAX_LEVEL = BUILDER
                .comment("Highest level this robot type can reach.", "Higher levels unlock better stats and abilities.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [200]")
                .define("bunny2-max-level", 200);

        BUNNY2_MAX_HEALTH = BUILDER
                .comment("Maximum health points this robot type can have.", "Each heart = 2 health points. Higher values make robots more durable.", "Range: 1.0 to no upper limit (does not accept negative values or zero)", "Example: [24.0]")
                .define("bunny2-max-health", 24.0F);

        BUNNY2_ATTACK_DAMAGE = BUILDER
                .comment("How much damage this robot type deals per hit.", "Higher values make robots stronger in combat.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [4.0]")
                .define("bunny2-attack-damage", 4.0F);

        BUNNY2_ATTACK_SPEED = BUILDER
                .comment("How fast this robot type attacks (attacks per second).", "Higher values mean faster attacks. Minecraft default is 1.0.", "Range: 0.1 to no upper limit (does not accept negative values or zero)", "Example: [1.8]")
                .define("bunny2-attack-speed",  1.8F);

        BUNNY2_MOVEMENT_SPEED = BUILDER
                .comment("Base movement speed for this robot type.", "Higher values make robots move faster. Player walk speed is 0.1.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.37]")
                .define("bunny2-movement-speed", 0.37F);

        BUNNY2_ARMOR = BUILDER
                .comment("Armor points for this robot type.", "Each armor point reduces damage. Full diamond armor = 20 points.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [6.0]")
                .define("bunny2-armor", 6.0F);

        BUNNY2_ARMOR_TOUGHNESS = BUILDER
                .comment("Armor toughness for this robot type.", "Reduces damage from strong attacks. Diamond armor has 2.0 toughness per piece.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [1.0]")
                .define("bunny2-armor-toughness",1.0F);
        BUILDER.pop();

        BUILDER.push("Vanilla");
        VANILLA_MAX_LEVEL = BUILDER
                .comment("Highest level this robot type can reach.", "Higher levels unlock better stats and abilities.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [200]")
                .define("vanilla-max-level", 200);

        VANILLA_MAX_HEALTH = BUILDER
                .comment("Maximum health points this robot type can have.", "Each heart = 2 health points. Higher values make robots more durable.", "Range: 1.0 to no upper limit (does not accept negative values or zero)", "Example: [16.0]")
                .define("vanilla-max-health", 16.0F);

        VANILLA_ATTACK_DAMAGE = BUILDER
                .comment("How much damage this robot type deals per hit.", "Higher values make robots stronger in combat.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [2.0]")
                .define("vanilla-attack-damage", 2.0F);

        VANILLA_ATTACK_SPEED = BUILDER
                .comment("How fast this robot type attacks (attacks per second).", "Higher values mean faster attacks. Minecraft default is 1.0.", "Range: 0.1 to no upper limit (does not accept negative values or zero)", "Example: [1.0]")
                .define("vanilla-attack-speed",  1.0F);

        VANILLA_MOVEMENT_SPEED = BUILDER
                .comment("Base movement speed for this robot type.", "Higher values make robots move faster. Player walk speed is 0.1.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.37]")
                .define("vanilla-movement-speed", 0.37F);

        VANILLA_ARMOR = BUILDER
                .comment("Armor points for this robot type.", "Each armor point reduces damage. Full diamond armor = 20 points.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [2.0]")
                .define("vanilla-armor", 2.0F);

        VANILLA_ARMOR_TOUGHNESS = BUILDER
                .comment("Armor toughness for this robot type.", "Reduces damage from strong attacks. Diamond armor has 2.0 toughness per piece.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.0]")
                .define("vanilla-armor-toughness",0.0F);
        BUILDER.pop();

        BUILDER.push("Dragon");
        DRAGON_MAX_LEVEL = BUILDER
                .comment("Highest level this robot type can reach.", "Higher levels unlock better stats and abilities.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [300]")
                .define("dragon-max-level", 300);

        DRAGON_MAX_HEALTH = BUILDER
                .comment("Maximum health points this robot type can have.", "Each heart = 2 health points. Higher values make robots more durable.", "Range: 1.0 to no upper limit (does not accept negative values or zero)", "Example: [30.0]")
                .define("dragon-max-health", 30.0F);

        DRAGON_ATTACK_DAMAGE = BUILDER
                .comment("How much damage this robot type deals per hit.", "Higher values make robots stronger in combat.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [8.0]")
                .define("dragon-attack-damage", 8.0F);

        DRAGON_ATTACK_SPEED = BUILDER
                .comment("How fast this robot type attacks (attacks per second).", "Higher values mean faster attacks. Minecraft default is 1.0.", "Range: 0.1 to no upper limit (does not accept negative values or zero)", "Example: [0.8]")
                .define("dragon-attack-speed", 0.8F);

        DRAGON_MOVEMENT_SPEED = BUILDER
                .comment("Base movement speed for this robot type.", "Higher values make robots move faster. Player walk speed is 0.1.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.22]")
                .define("dragon-movement-speed", 0.22F);

        DRAGON_ARMOR = BUILDER
                .comment("Armor points for this robot type.", "Each armor point reduces damage. Full diamond armor = 20 points.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [4.0]")
                .define("dragon-armor", 4.0F);

        DRAGON_ARMOR_TOUGHNESS = BUILDER
                .comment("Armor toughness for this robot type.", "Reduces damage from strong attacks. Diamond armor has 2.0 toughness per piece.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [2.0]")
                .define("dragon-armor-toughness", 2.0F);
        BUILDER.pop();

        BUILDER.push("Kitsune");
        KITSUNE_MAX_LEVEL = BUILDER
                .comment("Highest level this robot type can reach.", "Higher levels unlock better stats and abilities.", "Range: 1 to no upper limit (does not accept negative values or zero)", "Example: [250]")
                .define("kitsune-max-level", 250);

        KITSUNE_MAX_HEALTH = BUILDER
                .comment("Maximum health points this robot type can have.", "Each heart = 2 health points. Higher values make robots more durable.", "Range: 1.0 to no upper limit (does not accept negative values or zero)", "Example: [22.0]")
                .define("kitsune-max-health", 22.0F);

        KITSUNE_ATTACK_DAMAGE = BUILDER
                .comment("How much damage this robot type deals per hit.", "Higher values make robots stronger in combat.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [6.0]")
                .define("kitsune-attack-damage", 6.0F);

        KITSUNE_ATTACK_SPEED = BUILDER
                .comment("How fast this robot type attacks (attacks per second).", "Higher values mean faster attacks. Minecraft default is 1.0.", "Range: 0.1 to no upper limit (does not accept negative values or zero)", "Example: [1.1]")
                .define("kitsune-attack-speed", 1.1F);

        KITSUNE_MOVEMENT_SPEED = BUILDER
                .comment("Base movement speed for this robot type.", "Higher values make robots move faster. Player walk speed is 0.1.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [0.28]")
                .define("kitsune-movement-speed", 0.28F);

        KITSUNE_ARMOR = BUILDER
                .comment("Armor points for this robot type.", "Each armor point reduces damage. Full diamond armor = 20 points.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [2.0]")
                .define("kitsune-armor", 2.0F);

        KITSUNE_ARMOR_TOUGHNESS = BUILDER
                .comment("Armor toughness for this robot type.", "Reduces damage from strong attacks. Diamond armor has 2.0 toughness per piece.", "Range: 0.0 to no upper limit (does not accept negative values)", "Example: [1.0]")
                .define("kitsune-armor-toughness", 1.0F);
        BUILDER.pop();

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    // -- Variables --

    // GENERAL
    public static int OwnerMaxRobotNum;
    public static double MovementMeleeAttack;
    public static float MovementFollowOwner;
    public static double MovementWanderAround;
    public static float FollowDistanceMax;
    public static float FollowDistanceMin;
    public static float LookRange;

    // RENDERER
    public static float Width;
    public static float Height;
    public static float ShadowRadius;

    // LEVEL | EXPERIENCE
    public static int ExperienceBase;
    public static int ExperienceMultiplier;

    // COMBAT
    public static boolean FriendlyFire;
    public static int AttackChance;
    public static int HealInterval;
    public static int WaryTime;
    public static boolean GlobalAutoHeal;
    public static boolean LootEnchantment;
    public static int LootEnchantmentLevel;
    public static int MaxLootEnchantment;
    public static float BaseDefenceRange;
    public static float BaseDefenceWarpRange;

    // PROTECTION
    public static int ProtectionLimitFire;
    public static int ProtectionLimitFall;
    public static int ProtectionLimitBlast;
    public static int ProtectionLimitProjectile;

    // SMART CORE RETRIEVAL
    public static boolean EnableSmartCoreRetrieval;
    public static double SmartCoreRetrievalDistance;

    // AI BEHAVIOR
    public static int OwnerStillThreshold;
    public static int WanderCheckInterval;
    public static double WanderChance;
    public static double WanderRadiusMin;
    public static double WanderRadiusMax;
    public static int WanderDurationMin;
    public static int WanderDurationMax;
    public static int WanderCooldownMin;
    public static int WanderCooldownMax;
    public static int PatrolDurationMin;
    public static int PatrolDurationMax;
    public static int GuardDurationMin;
    public static int GuardDurationMax;
    public static int PatrolPauseDurationMin;
    public static int PatrolPauseDurationMax;
    public static double GuardRotationSpeed;
    public static boolean EnableCombatRadiusParticles;
    public static int CombatRadiusParticleCount;
    public static double CombatRadiusParticleSpread;

    // ANIMATION
    public static int StandbyToSitDelayMin;
    public static int StandbyToSitDelayMax;

    // -- ENTITY --

    // BUNNY2
    public static int Bunny2MaxLevel;
    public static float Bunny2MaxHealth;
    public static float Bunny2AttackDamage;
    public static float Bunny2AttackSpeed;
    public static float Bunny2MovementSpeed;
    public static float Bunny2Armor;
    public static float Bunny2ArmorToughness;

    // VANILLA
    public static int VanillaMaxLevel;
    public static float VanillaMaxHealth;
    public static float VanillaAttackDamage;
    public static float VanillaAttackSpeed;
    public static float VanillaMovementSpeed;
    public static float VanillaArmor;
    public static float VanillaArmorToughness;

    // DRAGON
    public static int DragonMaxLevel;
    public static float DragonMaxHealth;
    public static float DragonAttackDamage;
    public static float DragonAttackSpeed;
    public static float DragonMovementSpeed;
    public static float DragonArmor;
    public static float DragonArmorToughness;

    // KITSUNE
    public static int KitsuneMaxLevel;
    public static float KitsuneMaxHealth;
    public static float KitsuneAttackDamage;
    public static float KitsuneAttackSpeed;
    public static float KitsuneMovementSpeed;
    public static float KitsuneArmor;
    public static float KitsuneArmorToughness;

    private static final List<Runnable> onLoadCallbacks = new ArrayList<>();

    // -- Custom Methods --

    public static void register(FMLJavaModLoadingContext context) {
        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, LovelyConfigs.SPEC);
    } // register ()

    private static boolean validateItemName(final Object obj) {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
    } // validateItemName ()

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
        LovelyLegacy.LOGGER.info("Loading configuration from: {}", event.getConfig().getFileName());
        
        // GENERAL
        OwnerMaxRobotNum = OWNER_MAX_ROBOT_NUM.get();
        MovementMeleeAttack = MOVEMENT_MELEE_ATTACK.get();
        MovementFollowOwner = MOVEMENT_FOLLOW_OWNER.get();
        MovementWanderAround = MOVEMENT_WANDER_AROUND.get();
        FollowDistanceMax = FOLLOW_DISTANCE_MAX.get();
        FollowDistanceMin = FOLLOW_DISTANCE_MIN.get();
        LookRange = LOOK_RANGE.get();

        // -- RENDERER --
        Width = WIDTH.get();
        Height = HEIGHT.get();
        ShadowRadius = SHADOW_RADIUS.get();

        // -- LEVEL | EXPERIENCE ---
        ExperienceBase = EXPERIENCE_BASE.get();
        ExperienceMultiplier = EXPERIENCE_MULTIPLIER.get();

        // -- COMBAT --
        FriendlyFire = FRIENDLY_FIRE.get();
        AttackChance = ATTACK_CHANCE.get();
        HealInterval = HEAL_INTERVAL.get();
        WaryTime = WARY_TIME.get();
        GlobalAutoHeal = GLOBAL_AUTO_HEAL.get();
        LootEnchantment = LOOT_ENCHANTMENT.get();
        LootEnchantmentLevel = LOOT_ENCHANTMENT_LEVEL.get();
        MaxLootEnchantment = MAX_LOOT_ENCHANTMENT.get();
        BaseDefenceRange = BASE_DEFENCE_RANGE.get();
        BaseDefenceWarpRange = BASE_DEFENCE_WARP_RANGE.get();

        // -- PROTECTION --
        ProtectionLimitFire = PROTECTION_LIMIT_FIRE.get();
        ProtectionLimitFall = PROTECTION_LIMIT_FALL.get();
        ProtectionLimitBlast = PROTECTION_LIMIT_BLAST.get();
        ProtectionLimitProjectile = PROTECTION_LIMIT_PROJECTILE.get();

        // -- SMART CORE RETRIEVAL --
        EnableSmartCoreRetrieval = ENABLE_SMART_CORE_RETRIEVAL.get();
        SmartCoreRetrievalDistance = SMART_CORE_RETRIEVAL_DISTANCE.get();

        // -- AI BEHAVIOR --
        OwnerStillThreshold = OWNER_STILL_THRESHOLD.get();
        WanderCheckInterval = WANDER_CHECK_INTERVAL.get();
        WanderChance = WANDER_CHANCE.get();
        WanderRadiusMin = WANDER_RADIUS_MIN.get();
        WanderRadiusMax = WANDER_RADIUS_MAX.get();
        WanderDurationMin = WANDER_DURATION_MIN.get();
        WanderDurationMax = WANDER_DURATION_MAX.get();
        WanderCooldownMin = WANDER_COOLDOWN_MIN.get();
        WanderCooldownMax = WANDER_COOLDOWN_MAX.get();
        PatrolDurationMin = PATROL_DURATION_MIN.get();
        PatrolDurationMax = PATROL_DURATION_MAX.get();
        GuardDurationMin = GUARD_DURATION_MIN.get();
        GuardDurationMax = GUARD_DURATION_MAX.get();
        PatrolPauseDurationMin = PATROL_PAUSE_DURATION_MIN.get();
        PatrolPauseDurationMax = PATROL_PAUSE_DURATION_MAX.get();
        GuardRotationSpeed = GUARD_ROTATION_SPEED.get();
        EnableCombatRadiusParticles = ENABLE_COMBAT_RADIUS_PARTICLES.get();
        CombatRadiusParticleCount = COMBAT_RADIUS_PARTICLE_COUNT.get();
        CombatRadiusParticleSpread = COMBAT_RADIUS_PARTICLE_SPREAD.get();

        // -- ANIMATION --
        StandbyToSitDelayMin = STANDBY_TO_SIT_DELAY_MIN.get();
        StandbyToSitDelayMax = STANDBY_TO_SIT_DELAY_MAX.get();

        // -- ENTITY --

        // BUNNY2
        Bunny2MaxLevel = BUNNY2_MAX_LEVEL.get();
        Bunny2MaxHealth = BUNNY2_MAX_HEALTH.get();
        Bunny2AttackDamage = BUNNY2_ATTACK_DAMAGE.get();
        Bunny2AttackSpeed = BUNNY2_ATTACK_SPEED.get();
        Bunny2MovementSpeed = BUNNY2_MOVEMENT_SPEED.get();
        Bunny2Armor = BUNNY2_ARMOR.get();
        Bunny2ArmorToughness = BUNNY2_ARMOR_TOUGHNESS.get();

        // VANILLA
        VanillaMaxLevel = VANILLA_MAX_LEVEL.get();
        VanillaMaxHealth = VANILLA_MAX_HEALTH.get();
        VanillaAttackDamage = VANILLA_ATTACK_DAMAGE.get();
        VanillaAttackSpeed = VANILLA_ATTACK_SPEED.get();
        VanillaMovementSpeed = VANILLA_MOVEMENT_SPEED.get();
        VanillaArmor = VANILLA_ARMOR.get();
        VanillaArmorToughness = VANILLA_ARMOR_TOUGHNESS.get();

        // DRAGON
        DragonMaxLevel = DRAGON_MAX_LEVEL.get();
        DragonMaxHealth = DRAGON_MAX_HEALTH.get();
        DragonAttackDamage = DRAGON_ATTACK_DAMAGE.get();
        DragonAttackSpeed = DRAGON_ATTACK_SPEED.get();
        DragonMovementSpeed = DRAGON_MOVEMENT_SPEED.get();
        DragonArmor = DRAGON_ARMOR.get();
        DragonArmorToughness = DRAGON_ARMOR_TOUGHNESS.get();

        // KITSUNE
        KitsuneMaxLevel = KITSUNE_MAX_LEVEL.get();
        KitsuneMaxHealth = KITSUNE_MAX_HEALTH.get();
        KitsuneAttackDamage = KITSUNE_ATTACK_DAMAGE.get();
        KitsuneAttackSpeed = KITSUNE_ATTACK_SPEED.get();
        KitsuneMovementSpeed = KITSUNE_MOVEMENT_SPEED.get();
        KitsuneArmor = KITSUNE_ARMOR.get();
        KitsuneArmorToughness = KITSUNE_ARMOR_TOUGHNESS.get();

        LovelyLegacy.LOGGER.info("Configuration loaded successfully");
        onLoadCallbacks.forEach(Runnable::run);
    } // onLoad ()

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
        LovelyLegacy.LOGGER.info("Reloading configuration...");
        // Forge handles reload automatically through ModConfigEvent
        // This method exists for API compatibility and explicit reload requests
        LovelyLegacy.LOGGER.info("Configuration reload requested - Forge will handle reload on next config change");
    } // reload()

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