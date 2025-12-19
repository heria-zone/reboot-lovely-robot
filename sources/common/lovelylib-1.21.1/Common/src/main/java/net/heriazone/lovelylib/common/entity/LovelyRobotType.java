package net.heriazone.lovelylib.common.entity;

import net.heriazone.lovelylib.common.entity.enums.EntityVariant;
import net.heriazone.lovelylib.hzlib.api.entity.features.LevelFeature;

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
 */
public class LovelyRobotType {

    // -- Registry --

    /**
     * List of all robot types for iteration.
     * <p>
     * <b>Usage:</b> Enables iteration over all robot types for registration,
     * rendering setup, or bulk operations.
     */
    public static final List<NativeEntityType> TYPES = new ArrayList<>();

    // -- Helper Methods --

    /**
     * Creates robot type with specified variant and combat stats.
     * <p>
     * <b>Implementation:</b> Creates NativeEntityType, configures combat stats,
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
     * @return configured NativeEntityType instance
     */
    protected static NativeEntityType create(EntityVariant variant,
                                           int maxLevel,
                                           float maxHealth,
                                           float baseAttack,
                                           float attackSpeed,
                                           float baseDefense,
                                           float baseToughness,
                                           float knockbackResistance,
                                           float moveSpeed) {
        // Create robot type
        NativeEntityType robotType = new NativeEntityType(variant.getName(), variant);

        // Configure combat stats
        robotType.withCombatStats(maxHealth, baseAttack, attackSpeed,
                baseDefense, baseToughness, knockbackResistance, moveSpeed);

        // Configure max level
        robotType.getFeature(LevelFeature.class).ifPresent(feature ->
                feature.setMaxLevel(maxLevel)
        );

        // Apply color palette
        robotType.withColorPalette(variant);

        // Add to registry
        TYPES.add(robotType);

        return robotType;
    } // create ()

    /**
     * Creates robot type with specified variant and combat stats.
     * <p>
     * <b>Implementation:</b> Creates NativeEntityType, configures combat stats,
     * applies color palette, and adds to registry.
     * <p>
     * <b>State Impact:</b> Adds created robot type to TYPES list for iteration.
     *
     * @param variant entity variant determining resource paths
     * @return configured NativeEntityType instance
     */
    protected static NativeEntityType create(EntityVariant variant) {
        // Create robot type
        NativeEntityType robotType = new NativeEntityType(variant.getName(), variant);

        // Apply color palette
        robotType.withColorPalette(variant);

        // Add to registry
        TYPES.add(robotType);

        return robotType;
    } // create ()

} // Class: LovelyRobotType