package net.heriazone.lovelylib.common.entity.features;

import net.heriazone.hzlib.api.entity.features.BoneCondition;
import net.heriazone.lovelylib.common.entity.RobotEntity;
import net.heriazone.lovelylib.common.entity.RobotFamily;

/**
 * Factory methods for common {@link BoneCondition} predicates used by
 * {@code BoneVisibilityFeature} declarations in LovelyLib robot families.
 * <p>
 * <b>Architecture:</b> Eliminates boilerplate lambda duplication across
 * {@code LegacyRobotFamilies} and {@code RebootRobotFamilies}. Each factory
 * method returns a stateless, allocation-free {@link BoneCondition} lambda
 * safe for repeated per-frame evaluation in
 * {@code NativeModel.setCustomAnimations()}.
 * <p>
 * <b>Kitsune tail progression:</b> The 9-tail unlock system divides the family's
 * max level into 8 equal thresholds. {@code tail0} (base tail) is visible only
 * below the first threshold; {@code tail01}–{@code tail08} unlock progressively;
 * {@code tail09} requires max level.
 * <p>
 * <b>Sentry wing conflict:</b> {@code textureVariantIs} / {@code textureVariantIsNot}
 * cover the Sentry appearance-variant bone conflict — those declarations live in
 * the Sentry family when it is built (deferred per Sprint 13 notes).
 */
public final class BoneVisibilityConditions {

    // -- Constructor --

    private BoneVisibilityConditions() {} // Utility class — no instances

    // -- Kitsune Tail Conditions --

    /**
     * Returns a condition that is {@code true} when the robot's current level
     * is at or above the unlock threshold for the given tail index.
     * <p>
     * <b>Tail index mapping:</b>
     * <ul>
     *   <li>Index {@code 0} — base tail ({@code "tail0"}): visible when level
     *       is <em>below</em> the first per-tail threshold; pass index {@code 0}
     *       to {@link #baseTailVisible()} instead for clarity.</li>
     *   <li>Indices {@code 1}–{@code 8} — progressive tails ({@code "tail01"}–
     *       {@code "tail08"}): visible when {@code level >= (maxLevel / 8) * index}.</li>
     *   <li>Index {@code 9} — final tail ({@code "tail09"}): requires max level;
     *       use {@link #finalTailVisible()} for clarity.</li>
     * </ul>
     * <p>
     * <b>Performance:</b> Computes {@code maxLevel} from the family reference on
     * every frame. {@link RobotFamily#getMaxLevel()} is a simple accessor —
     * no allocation, no world query.
     *
     * @param tailIndex tail index (1–8); use dedicated helpers for 0 and 9
     * @return allocation-free {@link BoneCondition}
     */
    public static BoneCondition tailVisible(int tailIndex) {
        return entity -> {
            if (!(entity instanceof RobotEntity r)) return false;
            int maxLevel = (r.nativeEntity instanceof RobotFamily rf) ? rf.getMaxLevel() : 0;
            if (maxLevel <= 0) return false;
            int threshold = (maxLevel / 8) * tailIndex;
            return r.getCurrentLevel() >= threshold;
        };
    } // tailVisible ()

    /**
     * Returns a condition that is {@code true} when the base tail ({@code "tail0"})
     * should be visible — i.e. the robot's level is <em>below</em> the first
     * per-tail threshold.
     * <p>
     * <b>Design:</b> {@code tail0} is the single-tail resting state. Once the
     * first named tail unlocks, the base tail hides to avoid Z-fighting.
     *
     * @return allocation-free {@link BoneCondition}
     */
    public static BoneCondition baseTailVisible() {
        return entity -> {
            if (!(entity instanceof RobotEntity r)) return false;
            int maxLevel = (r.nativeEntity instanceof RobotFamily rf) ? rf.getMaxLevel() : 0;
            if (maxLevel <= 0) return true; // no level data — show base tail by default
            int firstThreshold = maxLevel / 8;
            return r.getCurrentLevel() < firstThreshold;
        };
    } // baseTailVisible ()

    /**
     * Returns a condition that is {@code true} when the final tail
     * ({@code "tail09"}) should be visible — i.e. the robot has reached its
     * maximum level.
     *
     * @return allocation-free {@link BoneCondition}
     */
    public static BoneCondition finalTailVisible() {
        return entity -> {
            if (!(entity instanceof RobotEntity r)) return false;
            int maxLevel = (r.nativeEntity instanceof RobotFamily rf) ? rf.getMaxLevel() : 0;
            if (maxLevel <= 0) return false;
            return r.getCurrentLevel() >= maxLevel;
        };
    } // finalTailVisible ()

    // -- Texture Variant Conditions --

    /**
     * Returns a condition that is {@code true} when the entity's active texture
     * variant key equals the given key.
     * <p>
     * <b>Usage — Sentry wing conflict:</b>
     * <pre>{@code
     * .hideWhen("UWings", BoneVisibilityConditions.textureVariantIs("sentry_dragon"))
     * }</pre>
     *
     * @param variantKey exact texture variant key to match
     * @return allocation-free {@link BoneCondition}
     */
    public static BoneCondition textureVariantIs(String variantKey) {
        return entity -> variantKey.equals(entity.getTextureVariant());
    } // textureVariantIs ()

    /**
     * Returns a condition that is {@code true} when the entity's active texture
     * variant key does <em>not</em> equal the given key.
     * <p>
     * <b>Usage — show a bone in all variants except one:</b>
     * <pre>{@code
     * .showWhen("UWingsno", BoneVisibilityConditions.textureVariantIsNot("sentry_honey"))
     * }</pre>
     *
     * @param variantKey exact texture variant key to exclude
     * @return allocation-free {@link BoneCondition}
     */
    public static BoneCondition textureVariantIsNot(String variantKey) {
        return entity -> !variantKey.equals(entity.getTextureVariant());
    } // textureVariantIsNot ()

} // Class: BoneVisibilityConditions
