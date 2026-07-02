package net.heriazone.lovelylib.source.tribute;

import net.heriazone.hzlib.api.animation.AnimationProfile;
import net.heriazone.lovelylib.common.entity.RobotFamily;
import net.heriazone.lovelylib.common.entity.definition.RobotVariant;

/**
 * {@link RobotFamily} subtype for the Tribute source variant.
 * <p>
 * <b>Architecture:</b> The only purpose of this subclass is to route
 * {@link #buildAnimatorProfile()} to {@link RobotFamily#TRIBUTE_PROFILE} instead of
 * {@link RobotFamily#ROBOT_BASE_PROFILE}. That single override ensures every
 * {@link net.heriazone.hzlib.framework.entity.variants.StandardAnimatorVariant}
 * registered for a Tribute family carries a profile with no {@code rest} or {@code sit}
 * slot — faithfully reproducing the original LovelyRobot behaviour where robots remain
 * in {@code idle} regardless of standby state and show {@code idle} (not {@code sit})
 * while riding a vehicle.
 * <p>
 * <b>Why a subclass and not a constructor parameter:</b>
 * {@link RobotFamily#configureVariants()} is called from {@code super()} during
 * construction — before any argument passed to a subclass constructor could be read.
 * An override hook is the only mechanism that can influence profile selection at that
 * point without restructuring the parent constructor chain.
 * <p>
 * <b>No other behaviour difference:</b> All variant registration, color-palette wiring,
 * schema, and migration logic is inherited unchanged from {@link RobotFamily}.
 *
 * @see TributeRobotFamilies
 * @see RobotFamily#TRIBUTE_PROFILE
 */
public class TributeRobotFamily extends RobotFamily {

    // -- Constructor --

    /**
     * Creates a Tribute robot family descriptor with the given key and variant.
     * <p>
     * Delegates entirely to {@link RobotFamily#RobotFamily(String, RobotVariant)}.
     * The {@link #buildAnimatorProfile()} override fires during the parent constructor
     * via {@link RobotFamily#configureVariants()}, so the correct
     * {@link RobotFamily#TRIBUTE_PROFILE} is embedded in the
     * {@link net.heriazone.hzlib.framework.entity.variants.StandardAnimatorVariant}
     * from the moment this object is created.
     *
     * @param key     unique registry identifier (e.g. {@code "bunny"})
     * @param variant entity variant determining resource paths
     */
    public TributeRobotFamily(String key, RobotVariant variant) {
        super(key, variant);
    } // Constructor: TributeRobotFamily ()

    // -- Profile Override --

    /**
     * Returns {@link RobotFamily#TRIBUTE_PROFILE} — no {@code rest} or {@code sit} slot.
     * <p>
     * Called by {@link RobotFamily#configureVariants()} during construction to select
     * which profile to embed in the {@link net.heriazone.hzlib.framework.entity.variants.StandardAnimatorVariant}.
     *
     * @return tribute animation profile; never {@code null}
     */
    @Override
    protected AnimationProfile buildAnimatorProfile() {
        return TRIBUTE_PROFILE;
    } // buildAnimatorProfile ()

} // Class: TributeRobotFamily
