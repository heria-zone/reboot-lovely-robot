package net.heriazone.lovelylib.common.entity;

import net.heriazone.lovelylib.common.entity.enums.EntityVariant;
import net.heriazone.hzlib.api.animation.AnimationProfile;
import net.heriazone.hzlib.api.animation.LoopBehavior;
import net.heriazone.hzlib.api.entity.features.LevelFeature;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Registry of native robot types with config-driven values.<p>
 * <p>
 * <b>Architecture:</b> Provides static registry of robot types initialized from config.
 * Each robot type is configured with variant-specific stats, max levels, and XP strategies.
 * Supports runtime config reload for server administration.
 * <p>
 * <b>Design Decision:</b> Static initialization ensures robot types are available before
 * entity registration. Config values are loaded during static initialization and can be
 * reloaded at runtime via reloadFromConfig().
 * <p>
 * <b>Animation Profile:</b> All robot types share a single {@link AnimationProfile} instance
 * since they all use the same animation file ({@code default.animation.json}) with the same
 * five animation names. The profile is attached as a feature on each robot type via
 * {@link RobotFamily#withFeature}. The loader-specific {@code InternalAnimation} reads
 * this profile to resolve animation names instead of using hardcoded constants.
 */
public class LovelyRobotType {

    // -- Registry --

    /**
     * List of all robot types for iteration.
     * <p>
     * <b>Usage:</b> Enables iteration over all robot types for registration,
     * rendering setup, or bulk operations.
     */
    public static final List<RobotFamily> TYPES = new ArrayList<>();

    // -- Shared Animation Profile --

    /**
     * Shared animation profile for all robot types.
     * <p>
     * <b>Architecture:</b> All 7 robot types share one animation file with the same
     * five animation names. A single profile instance is registered on every robot type
     * via {@code withFeature(AnimationProfile.class, ROBOT_ANIMATION_PROFILE)}.
     * <p>
     * <b>Attack behavior:</b> The attack animation uses {@link LoopBehavior#INTERRUPT}
     * which maps to GeckoLib's {@code override_previous_animation: true}, ensuring the
     * attack animation overrides the locomotion controller mid-swing.
     */
    public static final AnimationProfile ROBOT_ANIMATION_PROFILE = AnimationProfile.builder()
            .idle("idle")
            .walk("walk")
            .rest("rest")
            .sit("sit")
            .attack(pool -> pool.add("attack", LoopBehavior.INTERRUPT))
            .build();

    // -- Helper Methods --

    /**
     * Creates robot type with specified variant and combat stats.
     * <p>
     * <b>Implementation:</b> Creates RobotFamily, configures combat stats,
     * applies color palette, and adds to registry.
     * <p>
     * <b>State Impact:</b> Adds created robot type to TYPES list for iteration.
     *
     * @param variant entity variant determining resource paths
     * @param maxLevel maximum level for this robot type
     * @param maxHealth maximum health points
     * @param baseAttack attack damage value
     * @param attackSpeed attack speed multiplier
     * @param baseDefense armour points
     * @param baseToughness amour toughness value
     * @param knockbackResistance knockback resistance (0.0 to 1.0)
     * @param moveSpeed movement speed multiplier
     * @return configured RobotFamily instance
     */
    protected static RobotFamily create(EntityVariant variant,
                                        int maxLevel,
                                        float maxHealth,
                                        float baseAttack,
                                        float attackSpeed,
                                        float baseDefense,
                                        float baseToughness,
                                        float knockbackResistance,
                                        float moveSpeed) {
        // Create robot type
        RobotFamily robotType = new RobotFamily(variant.getName(), variant);

        // Configure combat stats
        robotType.withCombatStats(maxHealth, baseAttack, attackSpeed,
                baseDefense, baseToughness, knockbackResistance, moveSpeed);

        // Configure max level
        robotType.getFeature(LevelFeature.class).ifPresent(feature ->
                feature.setMaxLevel(maxLevel)
        );

        // Apply color palette
        robotType.withColorPalette(variant);

        // Attach shared animation profile — all robots use the same animation file
        robotType.withFeature(AnimationProfile.class, ROBOT_ANIMATION_PROFILE);

        // Add to registry
        TYPES.add(robotType);

        return robotType;
    } // create ()

    /**
     * Creates robot type with specified variant and combat stats.
     * <p>
     * <b>Implementation:</b> Creates RobotFamily, configures combat stats,
     * applies color palette, and adds to registry.
     * <p>
     * <b>State Impact:</b> Adds created robot type to TYPES list for iteration.
     *
     * @param variant entity variant determining resource paths
     * @return configured RobotFamily instance
     */
    protected static RobotFamily create(EntityVariant variant) {
        // Create robot type
        RobotFamily robotType = new RobotFamily(variant.getName(), variant);

        // Apply color palette
        robotType.withColorPalette(variant);

        // Attach shared animation profile — all robots use the same animation file
        robotType.withFeature(AnimationProfile.class, ROBOT_ANIMATION_PROFILE);

        // Add to registry
        TYPES.add(robotType);

        return robotType;
    } // create ()

} // Class: LovelyRobotType