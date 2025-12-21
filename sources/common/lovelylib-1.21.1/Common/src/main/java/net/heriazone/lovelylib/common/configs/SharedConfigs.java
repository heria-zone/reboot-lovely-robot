package net.heriazone.lovelylib.common.configs;

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
        public static float MovementMeleeAttack = 0.8F;
        public static float MovementFollowOwner = 0.7F;
        public static float MovementWanderAround = 0.6F;
        public static float FollowDistanceMax = 10F;
        public static float FollowDistanceMin = 4F;
        public static float LookRange = 8F;

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

        // -- ENCHANTED BOOK PROTECTION --
        public static boolean EnableEnchantedBookProtection = true;
        public static double EnchantedBookContributionPercentage = 0.25;

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

        // -- COLLISION AVOIDANCE --

        public static boolean EnableCollisionAvoidance = true;
        public static double CollisionDetectionRadius = 3.0; // blocks
        public static double MinRobotSpacing = 2.0; // blocks
        public static double SpacingOffset = 1.5; // repulsion multiplier
        public static int CollisionCheckInterval = 5; // ticks (0.25s)

        // -- LEGACY INDIVIDUAL ENTITY CONFIGS (DEPRECATED) --
        // These will be removed once dynamic system is fully implemented

        // BUNNY
        public static int BunnyMaxLevel = 200;
        public static float BunnyAttackSpeed = 1.6F;
        public static float BunnyMovementSpeed = 0.37F;
        public static float BunnyBaseToughness = 0F;
        public static int BunnyBaseHp = 16;
        public static int BunnyBaseAttack = 2;
        public static int BunnyBaseDefense = 3;

        // BUNNY2
        public static int Bunny2MaxLevel = 200;
        public static float Bunny2AttackSpeed = 1.8F;
        public static float Bunny2MovementSpeed = 0.37F;
        public static int Bunny2BaseHp = 20;
        public static int Bunny2BaseAttack = 3;
        public static int Bunny2BaseDefense = 4;
        public static float Bunny2BaseToughness = 0F;

        // DRAGON
        public static int DragonMaxLevel = 200;
        public static float DragonAttackSpeed = 1.5F;
        public static float DragonMovementSpeed = 0.37F;
        public static int DragonBaseHp = 24;
        public static int DragonBaseAttack = 5;
        public static int DragonBaseDefense = 5;
        public static float DragonBaseToughness = 2F;

        // HONEY
        public static int HoneyMaxLevel = 200;
        public static float HoneyAttackSpeed = 1.0F;
        public static float HoneyMovementSpeed = 0.37F;
        public static int HoneyBaseHp = 14;
        public static int HoneyBaseAttack = 1;
        public static int HoneyBaseDefense = 2;
        public static float HoneyBaseToughness = 1F;

        // KITSUNE
        public static int KitsuneMaxLevel = 200;
        public static float KitsuneAttackSpeed = 1.1F;
        public static float KitsuneMovementSpeed = 0.37F;
        public static int KitsuneBaseHp = 16;
        public static int KitsuneBaseAttack = 2;
        public static int KitsuneBaseDefense = 1;
        public static float KitsuneBaseToughness = 1F;

        // NEKO
        public static int NekoMaxLevel = 200;
        public static float NekoAttackSpeed = 1.1F;
        public static float NekoMovementSpeed = 0.40F;
        public static int NekoBaseHp = 22;
        public static int NekoBaseAttack = 6;
        public static int NekoBaseDefense = 2;
        public static float NekoBaseToughness = 1F;

        // VANILLA
        public static int VanillaMaxLevel = 200;
        public static float VanillaAttackSpeed = 2F;
        public static float VanillaMovementSpeed = 0.37F;
        public static int VanillaBaseHp = 14;
        public static int VanillaBaseAttack = 4;
        public static int VanillaBaseDefense = 2;
        public static float VanillaBaseToughness = 0F;

    } // Class: Common

    /**
     * <p>Encapsulates entity configuration data with validation and builder support.<p>
     * <p>
     * <b>Architecture:</b> Provides type-safe configuration container with comprehensive
     * validation and fallback mechanisms. Supports builder pattern for flexible
     * configuration construction and runtime validation.
     * <p>
     * <b>Validation Strategy:</b> Invalid configurations automatically fall back to
     * sensible defaults, ensuring system stability even with corrupted config files.
     * <p>
     * <b>Thread Safety:</b> Immutable after construction, safe for concurrent access
     * across multiple threads during config reload operations.
     */
    public static class EntityConfigData {

        // -- Variables --

        public final int maxLevel;
        public final int baseHp;
        public final int baseAttack;
        public final float attackSpeed;
        public final int baseDefense;
        public final float baseToughness;
        public final float movementSpeed;

        // -- Constructor --

        /**
         * Creates entity configuration with specified parameters.
         * <p>
         * <b>Validation:</b> Constructor accepts any values - use validateOrDefault()
         * or Builder.build() for validated instances.
         * 
         * @param maxLevel maximum level this entity can reach
         * @param baseHp base health points for level calculations
         * @param baseAttack base attack damage for level calculations
         * @param attackSpeed attacks per second rate
         * @param baseDefense base defense value for level calculations
         * @param baseToughness base toughness for damage reduction
         * @param movementSpeed base movement speed in blocks per tick
         */
        public EntityConfigData(int maxLevel, int baseHp, int baseAttack, float attackSpeed, 
                               int baseDefense, float baseToughness, float movementSpeed) {
            this.maxLevel = maxLevel;
            this.baseHp = baseHp;
            this.baseAttack = baseAttack;
            this.attackSpeed = attackSpeed;
            this.baseDefense = baseDefense;
            this.baseToughness = baseToughness;
            this.movementSpeed = movementSpeed;
        } // Constructor: EntityConfigData ()

        // -- Validation Methods --

        /**
         * Validates configuration parameters against acceptable ranges.
         * <p>
         * <b>Validation Rules:</b>
         * - maxLevel > 0
         * - baseHp > 0
         * - baseAttack >= 0
         * - attackSpeed > 0
         * - baseDefense >= 0
         * - baseToughness >= 0
         * - movementSpeed > 0
         * 
         * @return true if all parameters are within acceptable ranges
         */
        public boolean isValid() {
            return maxLevel > 0 && 
                   baseHp > 0 && 
                   baseAttack >= 0 && 
                   attackSpeed > 0 && 
                   baseDefense >= 0 && 
                   baseToughness >= 0 && 
                   movementSpeed > 0;
        } // isValid()

        /**
         * Returns validated configuration or default if invalid.
         * <p>
         * <b>Fallback Strategy:</b> If current configuration fails validation,
         * returns default configuration instead of throwing exceptions.
         * 
         * @return this instance if valid, default configuration if invalid
         */
        public EntityConfigData validateOrDefault() {
            return isValid() ? this : getDefault();
        } // validateOrDefault()

        /**
         * Creates default entity configuration with balanced stats.
         * <p>
         * <b>Default Values:</b> Designed to provide reasonable baseline performance
         * for any robot type while maintaining game balance.
         * 
         * @return default entity configuration
         */
        public static EntityConfigData getDefault() {
            return new EntityConfigData(
                200,    // maxLevel - allows significant progression
                20,     // baseHp - moderate survivability
                3,      // baseAttack - balanced damage
                1.5F,   // attackSpeed - moderate attack rate
                3,      // baseDefense - basic protection
                0F,     // baseToughness - no special toughness
                0.37F   // movementSpeed - standard robot speed
            );
        } // getDefault()

        // -- Builder Pattern --

        /**
         * <p>Provides flexible configuration construction with validation.<p>
         * <p>
         * <b>Usage Pattern:</b> Allows step-by-step configuration building with
         * method chaining and automatic validation on build().
         * <p>
         * <b>Validation:</b> build() method validates all parameters and throws
         * IllegalStateException if any values are invalid.
         */
        public static class Builder {
            private int maxLevel = 200;
            private int baseHp = 20;
            private int baseAttack = 3;
            private float attackSpeed = 1.5F;
            private int baseDefense = 3;
            private float baseToughness = 0F;
            private float movementSpeed = 0.37F;

            public Builder maxLevel(int maxLevel) {
                this.maxLevel = maxLevel;
                return this;
            }

            public Builder baseHp(int baseHp) {
                this.baseHp = baseHp;
                return this;
            }

            public Builder baseAttack(int baseAttack) {
                this.baseAttack = baseAttack;
                return this;
            }

            public Builder attackSpeed(float attackSpeed) {
                this.attackSpeed = attackSpeed;
                return this;
            }

            public Builder baseDefense(int baseDefense) {
                this.baseDefense = baseDefense;
                return this;
            }

            public Builder baseToughness(float baseToughness) {
                this.baseToughness = baseToughness;
                return this;
            }

            public Builder movementSpeed(float movementSpeed) {
                this.movementSpeed = movementSpeed;
                return this;
            }

            /**
             * Builds and validates entity configuration.
             * <p>
             * <b>Validation:</b> Ensures all parameters meet minimum requirements
             * before creating configuration instance.
             * 
             * @return validated EntityConfigData instance
             * @throws IllegalStateException if any parameter fails validation
             */
            public EntityConfigData build() {
                EntityConfigData config = new EntityConfigData(
                    maxLevel, baseHp, baseAttack, attackSpeed, 
                    baseDefense, baseToughness, movementSpeed
                );
                
                if (!config.isValid()) {
                    throw new IllegalStateException("Invalid entity configuration parameters");
                }
                
                return config;
            } // build()

        } // Class: Builder

        // -- Utility Methods --

        /**
         * Creates configuration with default values and specified overrides.
         * <p>
         * <b>Override Strategy:</b> Starts with default configuration and applies
         * only the specified overrides, maintaining defaults for unspecified values.
         * 
         * @param variant robot variant name for logging
         * @return configuration with defaults and overrides applied
         */
        public static EntityConfigData withDefaults(String variant) {
            return new Builder().build(); // Uses default values from builder
        } // withDefaults()

        /**
         * Creates configuration from individual config values with validation.
         * <p>
         * <b>Migration Helper:</b> Assists in migrating from individual config
         * variables to dynamic system by providing validation and fallbacks.
         * 
         * @param variant robot variant for error logging
         * @param maxLevel maximum level value
         * @param baseHp base health points
         * @param baseAttack base attack damage
         * @param attackSpeed attack speed rate
         * @param baseDefense base defense value
         * @param baseToughness base toughness value
         * @param movementSpeed movement speed
         * @return validated configuration or default if invalid
         */
        public static EntityConfigData fromIndividualConfigs(String variant, int maxLevel, 
                int baseHp, int baseAttack, float attackSpeed, int baseDefense, 
                float baseToughness, float movementSpeed) {
            EntityConfigData config = new EntityConfigData(
                maxLevel, baseHp, baseAttack, attackSpeed, 
                baseDefense, baseToughness, movementSpeed
            );
            return config.validateOrDefault();
        } // fromIndividualConfigs()

        @Override
        public String toString() {
            return String.format("EntityConfigData{maxLevel=%d, baseHp=%d, baseAttack=%d, " +
                               "attackSpeed=%.2f, baseDefense=%d, baseToughness=%.2f, movementSpeed=%.3f}",
                               maxLevel, baseHp, baseAttack, attackSpeed, baseDefense, baseToughness, movementSpeed);
        } // toString()

    } // Class: EntityConfigData

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