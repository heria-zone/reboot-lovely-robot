package net.msymbios.llovelyr.source.configs;

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

    static {
        Config.setInsertionOrderPreserved(true);

        BUILDER = new ForgeConfigSpec.Builder();

        BUILDER.push("General");
        OWNER_MAX_ROBOT_NUM = BUILDER
                .comment("Maximum number of robots owned by an owner.", "Note: [-1] means unlimited!", "Example: [30]")
                .define("owner-max-robot", 30);

        MOVEMENT_MELEE_ATTACK = BUILDER
                .comment("Movement speed when it is melee attacking.", "Example: [0.8]")
                .define("movement-melee-attack", 0.8F);

        MOVEMENT_FOLLOW_OWNER = BUILDER
                .comment("Movement speed when following player.", "Example: [0.7]")
                .define("movement-follow-owner", 0.7F);

        MOVEMENT_WANDER_AROUND = BUILDER
                .comment("Movement speed while it is wandering around.", "Example: [0.6]")
                .define("movement-wander-around", 0.6F);

        FOLLOW_DISTANCE_MAX = BUILDER
                .comment("Maximum distance allowed while following.", "Example: [10]")
                .define("follow-distance-max", 10.0F);

        FOLLOW_DISTANCE_MIN = BUILDER
                .comment("Minimum distance allowed while following.", "Example: [2]")
                .define("follow-distance-min", 2F);

        LOOK_RANGE = BUILDER
                .comment("How much should the head rotate while looking.", "Example: [8]")
                .define("look-range", 8.0F);
        BUILDER.pop();

        BUILDER.push("Renderer");
        WIDTH = BUILDER
                .comment("Entity hit-box width.", "Example: [0.4]")
                .define("width", 0.4F);

        HEIGHT = BUILDER
                .comment("Entity hit-box height.", "Example: [1.9]")
                .define("height", 1.9F);

        SHADOW_RADIUS = BUILDER
                .comment("Entity shadow, the cast size on the ground.", "Example: [0.4]")
                .define("shadow-radius", 0.4F);
        BUILDER.pop();

        BUILDER.push("Level & Experience");
        EXPERIENCE_BASE = BUILDER
                .comment("Basic experience required to level up.", "Example: [50]")
                .define("experience-base", 50);

        EXPERIENCE_MULTIPLIER = BUILDER
                .comment("Increase level experience multiplier.", "Example: [2]")
                .define("experience-multiplier", 2);
        BUILDER.pop();

        BUILDER.push("Combat");
        FRIENDLY_FIRE = BUILDER
                .comment("Enable/Disable Robots owners attack on their own robots.", "Example: [false]")
                .define("friendly-fire", false);

        ATTACK_CHANCE = BUILDER
                .comment("Probability of attacking when attacked.", "Example: [5]")
                .define("attack-chance", 5);

        HEAL_INTERVAL = BUILDER
                .comment("Automatic recovery interval.", "Example: [50]")
                .define("heal-interval", 50);

        WARY_TIME = BUILDER
                .comment("Time while being in combat mode.", "Example: [50]")
                .define("wary-time", 50);

        GLOBAL_AUTO_HEAL = BUILDER
                .comment("Enable/disable global robots healing.", "Example: [true/false]")
                .define("global-heal", true);

        LOOT_ENCHANTMENT = BUILDER
                .comment("Enable looting enchantments.", "Example: [true/false]")
                .define("loot-enchantment", true);

        LOOT_ENCHANTMENT_LEVEL = BUILDER
                .comment("Levels required for looting enchantments.", "Example: [10]")
                .define("loot-enchantment-level", 10);

        MAX_LOOT_ENCHANTMENT = BUILDER
                .comment("Maximum level of looting enchantments.", "Example: [3]")
                .define("max-loot-enchantment", 3);

        BASE_DEFENCE_RANGE = BUILDER
                .comment("Base range to defend.", "Example: [15F]")
                .define("base-defence-range", 15.0F);

        BASE_DEFENCE_WARP_RANGE = BUILDER
                .comment("Range till teleport back to base.", "Example: [10F]")
                .define("base-defence-warp-range", 10.0F);
        BUILDER.pop();

        BUILDER.push("Protection");
        PROTECTION_LIMIT_FIRE = BUILDER
                .comment("Fire protection upper limit.", "Example: [80]")
                .define("limit-fire", 80);

        PROTECTION_LIMIT_FALL = BUILDER
                .comment("Fall protection upper limit.", "Example: [80]")
                .define("limit-fall", 80);

        PROTECTION_LIMIT_BLAST = BUILDER
                .comment("Blast protection upper limit.", "Example: [80]")
                .define("limit-blast", 80);

        PROTECTION_LIMIT_PROJECTILE = BUILDER
                .comment("Projectile protection upper limit.", "Example: [80]")
                .define("limit-projectile", 80);
        BUILDER.pop();

        BUILDER.push("Smart Core Retrieval");
        ENABLE_SMART_CORE_RETRIEVAL = BUILDER
                .comment("Enable automatic core retrieval when robot dies near owner.", "Example: [true]")
                .define("enable-smart-core-retrieval", true);

        SMART_CORE_RETRIEVAL_DISTANCE = BUILDER
                .comment("Maximum distance for automatic core retrieval (in blocks).", "Example: [16.0]")
                .defineInRange("smart-core-retrieval-distance", 16.0, 0.0, 128.0);
        BUILDER.pop();

        BUILDER.push("AI Behavior");
        
        BUILDER.push("Follow Mode");
        OWNER_STILL_THRESHOLD = BUILDER
                .comment("Time (in ticks) owner must be stationary before robot considers wandering.", "Example: [100] (5 seconds)")
                .defineInRange("owner-still-threshold", 100, 0, 6000);

        WANDER_CHECK_INTERVAL = BUILDER
                .comment("Interval (in ticks) between wander chance checks.", "Example: [200] (10 seconds)")
                .defineInRange("wander-check-interval", 200, 100, 6000);

        WANDER_CHANCE = BUILDER
                .comment("Probability of wandering when owner is stationary (0.0-1.0).", "Example: [0.15] (15%)")
                .defineInRange("wander-chance", 0.15, 0.0, 1.0);

        WANDER_RADIUS_MIN = BUILDER
                .comment("Minimum wander radius from owner (in blocks).", "Example: [3.0]")
                .defineInRange("wander-radius-min", 3.0, 1.0, 32.0);

        WANDER_RADIUS_MAX = BUILDER
                .comment("Maximum wander radius from owner (in blocks).", "Example: [6.0]")
                .defineInRange("wander-radius-max", 6.0, 1.0, 32.0);

        WANDER_DURATION_MIN = BUILDER
                .comment("Minimum wander duration (in ticks).", "Example: [100] (5 seconds)")
                .defineInRange("wander-duration-min", 100, 20, 6000);

        WANDER_DURATION_MAX = BUILDER
                .comment("Maximum wander duration (in ticks).", "Example: [200] (10 seconds)")
                .defineInRange("wander-duration-max", 200, 20, 6000);

        WANDER_COOLDOWN_MIN = BUILDER
                .comment("Minimum cooldown between wanders (in ticks).", "Example: [400] (20 seconds)")
                .defineInRange("wander-cooldown-min", 400, 100, 12000);

        WANDER_COOLDOWN_MAX = BUILDER
                .comment("Maximum cooldown between wanders (in ticks).", "Example: [800] (40 seconds)")
                .defineInRange("wander-cooldown-max", 800, 100, 12000);
        BUILDER.pop();

        BUILDER.push("Defense Mode");
        PATROL_DURATION_MIN = BUILDER
                .comment("Minimum patrol duration (in ticks).", "Example: [600] (30 seconds)")
                .defineInRange("patrol-duration-min", 600, 100, 6000);

        PATROL_DURATION_MAX = BUILDER
                .comment("Maximum patrol duration (in ticks).", "Example: [900] (45 seconds)")
                .defineInRange("patrol-duration-max", 900, 100, 6000);

        GUARD_DURATION_MIN = BUILDER
                .comment("Minimum guard duration (in ticks).", "Example: [400] (20 seconds)")
                .defineInRange("guard-duration-min", 400, 100, 6000);

        GUARD_DURATION_MAX = BUILDER
                .comment("Maximum guard duration (in ticks).", "Example: [600] (30 seconds)")
                .defineInRange("guard-duration-max", 600, 100, 6000);

        PATROL_PAUSE_DURATION_MIN = BUILDER
                .comment("Minimum pause duration at patrol points (in ticks).", "Example: [40] (2 seconds)")
                .defineInRange("patrol-pause-duration-min", 40, 10, 600);

        PATROL_PAUSE_DURATION_MAX = BUILDER
                .comment("Maximum pause duration at patrol points (in ticks).", "Example: [80] (4 seconds)")
                .defineInRange("patrol-pause-duration-max", 80, 10, 600);

        GUARD_ROTATION_SPEED = BUILDER
                .comment("Rotation speed during guard phase (radians per tick).", "Example: [0.05]")
                .defineInRange("guard-rotation-speed", 0.05, 0.01, 0.5);
        BUILDER.pop();

        BUILDER.push("Combat");
        ENABLE_COMBAT_RADIUS_PARTICLES = BUILDER
                .comment("Enable smoke particles when robot cannot chase enemy beyond radius.", "Example: [true]")
                .define("enable-combat-radius-particles", true);

        COMBAT_RADIUS_PARTICLE_COUNT = BUILDER
                .comment("Number of smoke particles to spawn.", "Example: [8]")
                .defineInRange("combat-radius-particle-count", 8, 1, 50);

        COMBAT_RADIUS_PARTICLE_SPREAD = BUILDER
                .comment("Spread radius for smoke particles.", "Example: [0.3]")
                .defineInRange("combat-radius-particle-spread", 0.3, 0.1, 2.0);
        BUILDER.pop();
        
        BUILDER.pop();

        BUILDER.push("Entity");

        BUILDER.push("Bunny2");
        BUNNY2_MAX_LEVEL = BUILDER
                .comment("Maximum Level", "Example: [200]")
                .define("max-level", 200);

        BUNNY2_MAX_HEALTH = BUILDER
                .comment("Maximum Health", "Example: [30.0]")
                .define("max-health", 24.0F);

        BUNNY2_ATTACK_DAMAGE = BUILDER
                .comment("Attack Damage", "Example: [5.0]")
                .define("attack-damage", 4.0F);

        BUNNY2_ATTACK_SPEED = BUILDER
                .comment("Attack Speed", "Example: [ 1.2]")
                .define("attack-speed",  1.8F);

        BUNNY2_MOVEMENT_SPEED = BUILDER
                .comment("Movement Speed", "Example: [0.4]")
                .define("movement-speed", 0.37F);

        BUNNY2_ARMOR = BUILDER
                .comment("Armor", "Example: [0.0]")
                .define("armor", 6.0F);

        BUNNY2_ARMOR_TOUGHNESS = BUILDER
                .comment("Armor Toughness", "Example: [0.0]")
                .define("armor-toughness",1.0F);
        BUILDER.pop();

        BUILDER.push("Vanilla");
        VANILLA_MAX_LEVEL = BUILDER
                .comment("Maximum Level", "Example: [200.0]")
                .define("max-level", 200);

        VANILLA_MAX_HEALTH = BUILDER
                .comment("Maximum Health", "Example: [30.0]")
                .define("max-health", 16.0F);

        VANILLA_ATTACK_DAMAGE = BUILDER
                .comment("Attack Damage", "Example: [5.0]")
                .define("attack-damage", 2.0F);

        VANILLA_ATTACK_SPEED = BUILDER
                .comment("Attack Speed", "Example: [ 1.2]")
                .define("attack-speed",  1.0F);

        VANILLA_MOVEMENT_SPEED = BUILDER
                .comment("Movement Speed", "Example: [0.6]")
                .define("movement-speed", 0.37F); // 0.25F is too slow

        VANILLA_ARMOR = BUILDER
                .comment("Armor", "Example: [0.0]")
                .define("armor", 2.0F);

        VANILLA_ARMOR_TOUGHNESS = BUILDER
                .comment("Armor Toughness", "Example: [0.0]")
                .define("armor-toughness",0.0F);
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

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
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

        onLoadCallbacks.forEach(Runnable::run);
    } // onLoad ()

    public static void onLoadCallback(Runnable callback) {
        onLoadCallbacks.add(callback);
    } // onLoadCallback ()

} // Class: LovelyConfigs