package net.heriazone.hzlib.api.animation;

/**
 * Provides bone transformation calculations for entity animations.
 * <p>
 * <b>Architecture:</b> Separates bone transformation business logic from GeckoLib-specific
 * bone manipulation. Calculations are pure math that can be applied to any bone
 * system, while actual bone updates remain in loader-specific code.
 * <p>
 * <b>Design Decision:</b> Static utility methods provide transformation values
 * that loader-specific code can apply to their bone systems. This allows sharing
 * complex calculations while maintaining the GeckoLib boundary — Common returns
 * values, loaders apply them to GeckoLib bones.
 */
public class BoneTransformations {

    // -- Head Rotation Calculations --

    /**
     * Calculates head rotation values for entity look direction.
     * <p>
     * <b>Architecture:</b> Transforms entity pitch/yaw into bone rotation values,
     * creating natural head tracking. Calculations are independent of bone system.
     * The loader-specific {@code InternalAnimation.headAnimation()} applies these
     * values to the GeckoLib bone directly.
     *
     * @param headPitch  entity head pitch in degrees
     * @param netHeadYaw entity net head yaw in degrees
     * @return rotation values as {@code [rotX, rotY]} in radians
     */
    public static float[] calculateHeadRotation(float headPitch, float netHeadYaw) {
        float rotX = headPitch * ((float) Math.PI / 180F);
        float rotY = netHeadYaw * ((float) Math.PI / 180F);
        return new float[]{rotX, rotY};
    } // calculateHeadRotation ()

} // Class: BoneTransformations