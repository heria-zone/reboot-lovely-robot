package net.msymbios.llovelyr.common.Configs;

public class SharedConfigs {

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

        // -- RENDERER --

        public static float ShadowRadius = 0.4F;

    } // Class: Client

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

    } // Class: Common

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

} // Class: SharedConfigs