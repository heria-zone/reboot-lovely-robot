package net.heriazone.lovelylib.common.entity;

import net.heriazone.lovelylib.common.entity.definition.RobotVariant;
import net.heriazone.lovelylib.common.entity.enums.EntityTexture;
import net.heriazone.hzlib.api.animation.AnimationProfile;
import net.heriazone.hzlib.api.entity.features.LevelFeature;
import net.heriazone.lovelylib.source.tribute.TributeRobotFamily;

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
public class RobotFamilyRegistry {

    // -- Registry --

    /**
     * List of all robot types for iteration.
     * <p>
     * <b>Usage:</b> Enables iteration over all robot types for registration,
     * rendering setup, or bulk operations.
     */
    public static final List<RobotFamily> TYPES = new ArrayList<>();

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
    protected static RobotFamily create(RobotVariant variant,
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
        robotType.withFeature(AnimationProfile.class, RobotFamily.ROBOT_BASE_PROFILE);

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
    protected static RobotFamily create(RobotVariant variant) {
        // Create robot type
        RobotFamily robotType = new RobotFamily(variant.getName(), variant);

        // Apply color palette
        robotType.withColorPalette(variant);

        // Attach shared animation profile — all robots use the same animation file
        robotType.withFeature(AnimationProfile.class, RobotFamily.ROBOT_BASE_PROFILE);

        // Add to registry
        TYPES.add(robotType);

        return robotType;
    } // create ()

    /**
     * Creates robot type with specified variant and a restricted color palette.
     * <p>
     * <b>Design Decision:</b> Used for robot types that only support a subset of the
     * 16-dye palette (e.g., Bunny3 with 5 pastel colors). Passes the color list to
     * {@link RobotFamily#withColorPalette(RobotVariant, java.util.List)} so that only
     * active textures are registered and only active dyes trigger appearance changes.
     * <p>
     * <b>State Impact:</b> Adds created robot type to TYPES list for iteration.
     *
     * @param variant entity variant determining resource paths
     * @param colors  ordered list of active colors — must not be empty, must not contain RANDOM
     * @return configured RobotFamily instance
     */
    protected static RobotFamily create(RobotVariant variant, java.util.List<EntityTexture> colors) {
        // Create robot type
        RobotFamily robotType = new RobotFamily(variant.getName(), variant);

        // Apply restricted color palette
        robotType.withColorPalette(variant, colors);

        // Attach shared animation profile — all robots use the same animation file
        robotType.withFeature(AnimationProfile.class, RobotFamily.ROBOT_BASE_PROFILE);

        // Add to registry
        TYPES.add(robotType);

        return robotType;
    } // create ()

    /**
     * Creates robot type with a restricted color palette resolved from a custom namespace.
     * <p>
     * <b>Design Decision:</b> Allows a downstream mod (e.g., Tribute with namespace
     * {@code "lovely_robot"}) to register its own texture variants that point to resources
     * in its own namespace rather than {@code lovelylib:}. Delegates to
     * {@link RobotFamily#withColorPalette(RobotVariant, java.util.List, String)}.
     * <p>
     * <b>State Impact:</b> Adds created robot type to TYPES list for iteration.
     *
     * @param variant   entity variant determining resource paths
     * @param colors    ordered list of active colors — must not be empty, must not contain RANDOM
     * @param namespace mod namespace for texture {@link net.minecraft.resources.ResourceLocation}s
     * @return configured RobotFamily instance
     */
    protected static RobotFamily create(RobotVariant variant, java.util.List<EntityTexture> colors, String namespace) {
        // Create robot type
        RobotFamily robotType = new RobotFamily(variant.getName(), variant);

        // Apply restricted color palette with custom namespace
        robotType.withColorPalette(variant, colors, namespace);

        // Attach shared animation profile — all robots use the same animation file
        robotType.withFeature(AnimationProfile.class, RobotFamily.ROBOT_BASE_PROFILE);

        // Add to registry
        TYPES.add(robotType);

        return robotType;
    } // create ()

    /**
     * Creates a Tribute robot type with a restricted color palette resolved from a custom namespace.
     * <p>
     * <b>Architecture:</b> Instantiates {@link net.heriazone.lovelylib.source.tribute.TributeRobotFamily}
     * instead of the base {@link RobotFamily} so that {@link RobotFamily#buildAnimatorProfile()}
     * returns {@link RobotFamily#TRIBUTE_PROFILE} (no rest/sit slots) during
     * {@link RobotFamily#configureVariants()}. This is required because
     * {@code configureVariants()} fires from {@code super()} — the profile must be
     * determined by the subtype before the constructor body runs.
     * <p>
     * <b>State Impact:</b> Adds created robot type to TYPES list for iteration.
     *
     * @param variant   entity variant determining resource paths
     * @param colors    ordered list of active colors — must not be empty, must not contain RANDOM
     * @param namespace mod namespace for texture {@link net.minecraft.resources.ResourceLocation}s
     * @return configured {@link net.heriazone.lovelylib.source.tribute.TributeRobotFamily} instance
     */
    protected static RobotFamily createTribute(RobotVariant variant, java.util.List<EntityTexture> colors, String namespace) {
        RobotFamily robotType = new TributeRobotFamily(variant.getName(), variant);
        robotType.withColorPalette(variant, colors, namespace);
        robotType.withFeature(AnimationProfile.class, RobotFamily.TRIBUTE_PROFILE);
        TYPES.add(robotType);
        return robotType;
    } // createTribute ()

} // Class: RobotFamilyRegistry