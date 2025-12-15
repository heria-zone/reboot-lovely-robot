package net.msymbios.llovelyr.common.Configs;

/**
 * <p>Defines validation bounds for all configuration values.<p>
 * <p>
 * <b>Architecture:</b> Centralizes min/max constraints to prevent invalid config
 * values that could break mod functionality or crash the system.
 * <p>
 * <b>Design Decision:</b> Immutable constants ensure bounds cannot be modified at
 * runtime. Loader-specific configs reference these values for validation.
 * <p>
 * <b>Special Cases:</b> Some values allow -1 for "unlimited" (e.g., OwnerMaxRobotNum).
 * These are documented with their special semantics.
 */
public class ConfigBounds {

    // -- Private Constructor --

    /**
     * Private constructor prevents instantiation of utility class.
     */
    private ConfigBounds() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    } // Constructor: ConfigBounds ()

    // -- GENERAL BOUNDS --

    /**
     * Owner max robot count bounds.
     * <p>
     * <b>Special:</b> -1 = unlimited, 0 = disabled, 1+ = specific limit
     */
    public static final int OWNER_MAX_ROBOT_MIN = -1; // -1 for unlimited
    public static final int OWNER_MAX_ROBOT_MAX = Integer.MAX_VALUE;

    /** Movement speed bounds (blocks per tick). */
    public static final float MOVEMENT_SPEED_MIN = 0.01F; // Must be > 0 to allow movement
    public static final float MOVEMENT_SPEED_MAX = 2.0F; // Prevent excessive speed

    /** Follow distance bounds (blocks). */
    public static final float FOLLOW_DISTANCE_MIN = 1.0F; // Minimum 1 block
    public static final float FOLLOW_DISTANCE_MAX = 64.0F; // Maximum 64 blocks

    /** Look range bounds (blocks). */
    public static final float LOOK_RANGE_MIN = 1.0F;
    public static final float LOOK_RANGE_MAX = 32.0F;

    // -- RENDERER BOUNDS --

    /** Shadow radius bounds. */
    public static final float SHADOW_RADIUS_MIN = 0.0F; // Can be 0 (no shadow)
    public static final float SHADOW_RADIUS_MAX = 2.0F;

    // -- EXPERIENCE BOUNDS --

    /** Experience base bounds. */
    public static final int EXPERIENCE_BASE_MIN = 1; // Must be > 0
    public static final int EXPERIENCE_BASE_MAX = 10000;

    /** Experience multiplier bounds. */
    public static final int EXPERIENCE_MULTIPLIER_MIN = 1; // Must be >= 1
    public static final int EXPERIENCE_MULTIPLIER_MAX = 10;

    // -- COMBAT BOUNDS --

    /** Attack chance bounds. */
    public static final int ATTACK_CHANCE_MIN = 0; // 0 = never counter-attack
    public static final int ATTACK_CHANCE_MAX = 100;

    /** Heal interval bounds (ticks). */
    public static final int HEAL_INTERVAL_MIN = 1; // Must be > 0
    public static final int HEAL_INTERVAL_MAX = 1200; // Max 60 seconds

    /** Wary time bounds (ticks). */
    public static final int WARY_TIME_MIN = 0; // 0 = no wary period
    public static final int WARY_TIME_MAX = 1200; // Max 60 seconds

    /** Loot enchantment level bounds. */
    public static final int LOOT_ENCHANTMENT_LEVEL_MIN = 1; // Must be > 0
    public static final int LOOT_ENCHANTMENT_LEVEL_MAX = 100;

    /** Max loot enchantment bounds. */
    public static final int MAX_LOOT_ENCHANTMENT_MIN = 0; // 0 = no looting
    public static final int MAX_LOOT_ENCHANTMENT_MAX = 3; // Minecraft max

    /** Defense range bounds (blocks). */
    public static final float DEFENSE_RANGE_MIN = 1.0F;
    public static final float DEFENSE_RANGE_MAX = 64.0F;

    // -- PROTECTION BOUNDS --

    /** Protection limit bounds (percentage). */
    public static final int PROTECTION_LIMIT_MIN = 0; // 0% = no protection
    public static final int PROTECTION_LIMIT_MAX = 100; // 100% = immune

    // -- SMART CORE RETRIEVAL BOUNDS --

    /** Smart core retrieval distance bounds (blocks). */
    public static final double SMART_CORE_DISTANCE_MIN = 0.0; // 0 = disabled
    public static final double SMART_CORE_DISTANCE_MAX = 128.0; // Max render distance

    // -- AI BEHAVIOR BOUNDS --

    /** Owner still threshold bounds (ticks). */
    public static final int OWNER_STILL_THRESHOLD_MIN = 0; // 0 = instant
    public static final int OWNER_STILL_THRESHOLD_MAX = 6000; // Max 5 minutes

    /** Wander check interval bounds (ticks). */
    public static final int WANDER_CHECK_INTERVAL_MIN = 20; // Min 1 second
    public static final int WANDER_CHECK_INTERVAL_MAX = 6000; // Max 5 minutes

    /** Wander chance bounds (probability). */
    public static final double WANDER_CHANCE_MIN = 0.0; // 0% = never wander
    public static final double WANDER_CHANCE_MAX = 1.0; // 100% = always wander

    /** Wander radius bounds (blocks). */
    public static final double WANDER_RADIUS_MIN = 1.0;
    public static final double WANDER_RADIUS_MAX = 32.0;

    /** Wander duration bounds (ticks). */
    public static final int WANDER_DURATION_MIN = 20; // Min 1 second
    public static final int WANDER_DURATION_MAX = 6000; // Max 5 minutes

    /** Wander cooldown bounds (ticks). */
    public static final int WANDER_COOLDOWN_MIN = 20; // Min 1 second
    public static final int WANDER_COOLDOWN_MAX = 12000; // Max 10 minutes

    /** Patrol duration bounds (ticks). */
    public static final int PATROL_DURATION_MIN = 20; // Min 1 second
    public static final int PATROL_DURATION_MAX = 6000; // Max 5 minutes

    /** Guard duration bounds (ticks). */
    public static final int GUARD_DURATION_MIN = 20; // Min 1 second
    public static final int GUARD_DURATION_MAX = 6000; // Max 5 minutes

    /** Patrol pause duration bounds (ticks). */
    public static final int PATROL_PAUSE_DURATION_MIN = 10; // Min 0.5 seconds
    public static final int PATROL_PAUSE_DURATION_MAX = 600; // Max 30 seconds

    /** Guard rotation speed bounds (radians per tick). */
    public static final double GUARD_ROTATION_SPEED_MIN = 0.01;
    public static final double GUARD_ROTATION_SPEED_MAX = 0.5;

    /** Combat radius particle count bounds. */
    public static final int COMBAT_RADIUS_PARTICLE_COUNT_MIN = 1;
    public static final int COMBAT_RADIUS_PARTICLE_COUNT_MAX = 50;

    /** Combat radius particle spread bounds. */
    public static final double COMBAT_RADIUS_PARTICLE_SPREAD_MIN = 0.1;
    public static final double COMBAT_RADIUS_PARTICLE_SPREAD_MAX = 2.0;

    // -- ANIMATION BOUNDS --

    /** Standby to sit delay bounds (ticks). */
    public static final int STANDBY_TO_SIT_DELAY_MIN = 20; // Min 1 second
    public static final int STANDBY_TO_SIT_DELAY_MAX = 12000; // Max 10 minutes

    // -- COLLISION AVOIDANCE BOUNDS --

    /** Collision detection radius bounds (blocks). */
    public static final double COLLISION_DETECTION_RADIUS_MIN = 0.5;
    public static final double COLLISION_DETECTION_RADIUS_MAX = 10.0;

    /** Min robot spacing bounds (blocks). */
    public static final double MIN_ROBOT_SPACING_MIN = 0.5;
    public static final double MIN_ROBOT_SPACING_MAX = 10.0;

    /** Spacing offset bounds (multiplier). */
    public static final double SPACING_OFFSET_MIN = 0.1;
    public static final double SPACING_OFFSET_MAX = 5.0;

    /** Collision check interval bounds (ticks). */
    public static final int COLLISION_CHECK_INTERVAL_MIN = 1; // Min 1 tick
    public static final int COLLISION_CHECK_INTERVAL_MAX = 20; // Max 1 second

    // -- ENTITY STAT BOUNDS --

    /** Max level bounds. */
    public static final int MAX_LEVEL_MIN = 1; // Must be >= 1
    public static final int MAX_LEVEL_MAX = 1000; // Prevent excessive levels

    /** Attack speed bounds (attacks per second). */
    public static final float ATTACK_SPEED_MIN = 0.1F; // Must be > 0
    public static final float ATTACK_SPEED_MAX = 10.0F; // Prevent excessive speed

    /** Base toughness bounds. */
    public static final float BASE_TOUGHNESS_MIN = 0.0F; // Can be 0
    public static final float BASE_TOUGHNESS_MAX = 20.0F; // Prevent excessive toughness

    /** Base HP bounds. */
    public static final int BASE_HP_MIN = 1; // Must be >= 1
    public static final int BASE_HP_MAX = 1000; // Prevent excessive HP

    /** Base attack bounds. */
    public static final int BASE_ATTACK_MIN = 1; // Must be >= 1
    public static final int BASE_ATTACK_MAX = 100; // Prevent excessive attack

    /** Base defense bounds. */
    public static final int BASE_DEFENSE_MIN = 1; // Must be >= 1
    public static final int BASE_DEFENSE_MAX = 100; // Prevent excessive defense

    // -- VALIDATION METHODS --

    /**
     * Validates owner max robot count.
     * <p>
     * <b>Special:</b> -1 is valid (unlimited), 0 is invalid (disabled but not allowed),
     * positive values are valid.
     *
     * @param value value to validate
     * @return clamped value within bounds
     */
    public static int validateOwnerMaxRobot(int value) {
        if (value == -1) return -1; // Unlimited is valid
        if (value == 0) return 1; // 0 not allowed, default to 1
        return Math.max(1, Math.min(value, OWNER_MAX_ROBOT_MAX));
    } // validateOwnerMaxRobot ()

    /**
     * Validates float value within bounds.
     *
     * @param value value to validate
     * @param min minimum allowed value
     * @param max maximum allowed value
     * @return clamped value within bounds
     */
    public static float validateFloat(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    } // validateFloat ()

    /**
     * Validates double value within bounds.
     *
     * @param value value to validate
     * @param min minimum allowed value
     * @param max maximum allowed value
     * @return clamped value within bounds
     */
    public static double validateDouble(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    } // validateDouble ()

    /**
     * Validates integer value within bounds.
     *
     * @param value value to validate
     * @param min minimum allowed value
     * @param max maximum allowed value
     * @return clamped value within bounds
     */
    public static int validateInt(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    } // validateInt ()

} // Class: ConfigBounds
