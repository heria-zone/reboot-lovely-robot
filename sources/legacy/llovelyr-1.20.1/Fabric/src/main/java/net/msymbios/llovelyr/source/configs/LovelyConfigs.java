package net.msymbios.llovelyr.source.configs;

public class LovelyConfigs {

    // -- Variables --


    // -- Methods --

    public static void register () {

    } // register ()

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
        public static final float DEFAULT_WIDTH = 0.6f;
        
        /** Default entity height (blocks). */
        public static final float DEFAULT_HEIGHT = 1.8f;
        
        /** Sitting pose width (blocks). */
        public static final float SITTING_WIDTH = 0.6f;
        
        /** Sitting pose height (blocks) - half of default height. */
        public static final float SITTING_HEIGHT = 0.9f;
    } // Class: EntityDimensions

} // Class: LovelyConfigs