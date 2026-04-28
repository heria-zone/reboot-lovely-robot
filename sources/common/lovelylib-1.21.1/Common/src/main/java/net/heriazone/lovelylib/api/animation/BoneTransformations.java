package net.heriazone.lovelylib.api.animation;

import net.heriazone.lovelylib.common.entity.RobotEntity;

/**
 * Provides bone transformation calculations for robot entity animations.
 * <p>
 * <b>Architecture:</b> Separates bone transformation business logic from GeckoLib-specific
 * bone manipulation. Calculations are pure math that can be applied to any bone
 * system, while actual bone updates remain in loader-specific code.
 * <p>
 * <b>Design Decision:</b> Static utility methods provide transformation values
 * that loader-specific code can apply to their bone systems. This allows sharing
 * complex calculations while maintaining GeckoLib boundary.
 */
public class BoneTransformations {

    // -- Head Rotation Calculations --

    /**
     * Calculates head rotation values for entity look direction.
     * <p>
     * <b>Architecture:</b> Transforms entity pitch/yaw into bone rotation values,
     * creating natural head tracking. Calculations are independent of bone system.
     *
     * @param headPitch entity head pitch in degrees
     * @param netHeadYaw entity net head yaw in degrees
     * @return rotation values as [rotX, rotY] in radians
     */
    public static float[] calculateHeadRotation(float headPitch, float netHeadYaw) {
        float rotX = headPitch * ((float) Math.PI / 180F);
        float rotY = netHeadYaw * ((float) Math.PI / 180F);
        return new float[]{rotX, rotY};
    } // calculateHeadRotation()

    // -- GeckoLib Integration Methods --

    /**
     * Applies head rotation to GeckoLib model using reflection.
     * <p>
     * <b>Architecture:</b> Uses reflection to apply head rotation calculations
     * to GeckoLib bones without direct GeckoLib dependency in Common module.
     *
     * @param renderer GeoModel containing bone hierarchy
     * @param event animation state with entity data
     * @param <T> entity type extending RobotEntity
     */
    public static <T extends RobotEntity> void applyHeadRotation(Object renderer, Object event) {
        try {
            // Get head bone using reflection
            Object animationProcessor = renderer.getClass().getMethod("getAnimationProcessor").invoke(renderer);
            Object head = animationProcessor.getClass().getMethod("getBone", String.class).invoke(animationProcessor, "head");

            if (head != null) {
                // Get entity data using reflection
                Class<?> dataTicketsClass = Class.forName("software.bernie.geckolib.constant.DataTickets");
                Object entityModelDataTicket = dataTicketsClass.getField("ENTITY_MODEL_DATA").get(null);
                Object entityData = event.getClass().getMethod("getData", Object.class).invoke(event, entityModelDataTicket);

                // Get pitch and yaw using reflection
                float headPitch = (Float) entityData.getClass().getMethod("headPitch").invoke(entityData);
                float netHeadYaw = (Float) entityData.getClass().getMethod("netHeadYaw").invoke(entityData);

                // Calculate rotation values
                float[] rotation = calculateHeadRotation(headPitch, netHeadYaw);

                // Apply rotation using reflection
                head.getClass().getMethod("setRotX", float.class).invoke(head, rotation[0]);
                head.getClass().getMethod("setRotY", float.class).invoke(head, rotation[1]);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to apply head rotation", e);
        }
    } // applyHeadRotation()

} // Class: BoneTransformations