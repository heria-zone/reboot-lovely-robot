package net.heriazone.hzlib.api.entity.features;

/**
 * Immutable binding of a bone name, an evaluated condition, and a hide/show polarity.
 * <p>
 * <b>Architecture:</b> The atom of the {@link BoneVisibilityFeature} system — one
 * bone, one condition, one action. {@link BoneVisibilityFeature} holds an ordered
 * list of rules; {@code NativeModel.applyBoneVisibility()} evaluates them in sequence
 * each render frame.
 * <p>
 * <b>Polarity semantics:</b>
 * <ul>
 *   <li>{@code hideWhenTrue = true} — bone is hidden when the condition passes
 *       (the common case: "hide this bone unless the player has unlocked it")</li>
 *   <li>{@code hideWhenTrue = false} — bone is shown (un-hidden) when the condition
 *       passes (used when the default geometry hides a bone and a condition reveals it)</li>
 * </ul>
 * <p>
 * Rules are created exclusively via {@link BoneVisibilityFeature.Builder#hideWhen} and
 * {@link BoneVisibilityFeature.Builder#showWhen} — never constructed directly.
 *
 * @param boneName     exact bone name as declared in the {@code .geo.json} file;
 *                     if the bone is absent from the loaded model the rule is silently skipped
 * @param condition    evaluated client-side each frame; must be allocation-free
 * @param hideWhenTrue {@code true} → hide bone when condition passes;
 *                     {@code false} → show bone when condition passes
 */
public record BoneRule(
        String boneName,
        BoneCondition condition,
        boolean hideWhenTrue
) {
    /**
     * Compact canonical constructor — validates that neither field is null.
     */
    public BoneRule {
        if (boneName == null || boneName.isBlank()) {
            throw new IllegalArgumentException("BoneRule boneName must not be null or blank");
        }
        if (condition == null) {
            throw new IllegalArgumentException("BoneRule condition must not be null");
        }
    } // Constructor: BoneRule ()

} // Record: BoneRule
