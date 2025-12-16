package net.heriazone.lovelylib.api.animation;

import net.heriazone.lovelylib.common.entity.NativeEntityType;
import net.heriazone.lovelylib.common.entity.common.LovelyRobotEntity;

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

    // -- Tail Visibility Calculations --

    /**
     * Calculates tail visibility configuration for progressive unlocking system.
     * <p>
     * <b>Architecture:</b> Implements Kitsune's 9-tail progression system where tails
     * unlock as robot levels up. Each tail requires specific level threshold.
     * <p>
     * <b>Design Decision:</b> Level-based unlocking provides visual progression feedback
     * and gameplay incentive for leveling. Final tail (tail09) requires max level.
     * <p>
     * <i>Note:</i> Specific to Kitsune variant. Other robots should override or skip.
     *
     * @param entity robot entity with level data
     * @return tail visibility configuration
     */
    public static TailVisibilityConfig calculateTailVisibility(LovelyRobotEntity entity) {
        var maxLevel = (entity.nativeEntity instanceof NativeEntityType robotType)
                ? robotType.getMaxLevel()
                : 0;

        var maxTails = 8;
        int levelPerTails = maxLevel / maxTails;
        boolean[] tailVisibility = new boolean[maxTails + 1]; // +1 for tail09

        // Show only base tail if below first threshold
        if (entity.getCurrentLevel() < levelPerTails) {
            tailVisibility[0] = true; // tail0 visible
            for (int i = 1; i <= maxTails; i++) {
                tailVisibility[i] = false; // tail01-tail08 hidden
            }
            return new TailVisibilityConfig(tailVisibility, false); // tail09 hidden
        }

        // Calculate which tails should be visible based on level
        tailVisibility[0] = false; // Hide base tail when others are visible
        for (int i = 0; i < maxTails; i++) {
            int levelToUnlockTail = levelPerTails * i;
            tailVisibility[i + 1] = entity.getCurrentLevel() >= levelToUnlockTail;
        }

        // Final tail requires max level
        boolean tail09Visible = entity.getCurrentLevel() >= maxLevel;

        return new TailVisibilityConfig(tailVisibility, tail09Visible);
    } // calculateTailVisibility()

    // -- Tail Visibility Configuration --

    /**
     * Configuration for tail visibility in progressive unlocking system.
     */
    public static class TailVisibilityConfig {
        private final boolean[] tailVisibility;
        private final boolean tail09Visible;

        public TailVisibilityConfig(boolean[] tailVisibility, boolean tail09Visible) {
            this.tailVisibility = tailVisibility.clone();
            this.tail09Visible = tail09Visible;
        }

        /**
         * Gets visibility for base tail (tail0).
         */
        public boolean isBaseTailVisible() {
            return tailVisibility.length > 0 && tailVisibility[0];
        }

        /**
         * Gets visibility for numbered tail (tail01-tail08).
         *
         * @param tailIndex tail index (1-8)
         * @return true if tail should be visible
         */
        public boolean isTailVisible(int tailIndex) {
            return tailIndex > 0 && tailIndex < tailVisibility.length && tailVisibility[tailIndex];
        }

        /**
         * Gets visibility for final tail (tail09).
         */
        public boolean isTail09Visible() {
            return tail09Visible;
        }

        /**
         * Gets all tail visibility states.
         *
         * @return array of visibility states
         */
        public boolean[] getAllTailVisibility() {
            return tailVisibility.clone();
        }
    } // Class: TailVisibilityConfig

    // -- GeckoLib Integration Methods --

    /**
     * Applies head rotation to GeckoLib model using reflection.
     * <p>
     * <b>Architecture:</b> Uses reflection to apply head rotation calculations
     * to GeckoLib bones without direct GeckoLib dependency in Common module.
     *
     * @param renderer GeoModel containing bone hierarchy
     * @param event animation state with entity data
     * @param <T> entity type extending LovelyRobotEntity
     */
    public static <T extends LovelyRobotEntity> void applyHeadRotation(Object renderer, Object event) {
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

    /**
     * Configures tail visibility on GeckoLib model using reflection.
     * <p>
     * <b>Architecture:</b> Uses reflection to apply tail visibility calculations
     * to GeckoLib bones without direct GeckoLib dependency in Common module.
     *
     * @param entity robot entity with level data
     * @param renderer GeoModel containing tail bones
     * @param event animation state (unused but required by signature)
     * @param <T> entity type extending LovelyRobotEntity
     */
    public static <T extends LovelyRobotEntity> void configureTailVisibility(LovelyRobotEntity entity, Object renderer, Object event) {
        try {
            // Calculate tail visibility
            TailVisibilityConfig config = calculateTailVisibility(entity);

            // Get animation processor using reflection
            Object animationProcessor = renderer.getClass().getMethod("getAnimationProcessor").invoke(renderer);

            // Apply base tail visibility
            Object baseTail = animationProcessor.getClass().getMethod("getBone", String.class).invoke(animationProcessor, "tail0");
            if (baseTail != null) {
                baseTail.getClass().getMethod("setHidden", boolean.class).invoke(baseTail, !config.isBaseTailVisible());
            }

            // Apply numbered tail visibility (tail01-tail08)
            for (int i = 1; i <= 8; i++) {
                String tailName = "tail0" + i;
                Object tail = animationProcessor.getClass().getMethod("getBone", String.class).invoke(animationProcessor, tailName);
                if (tail != null) {
                    tail.getClass().getMethod("setHidden", boolean.class).invoke(tail, !config.isTailVisible(i));
                }
            }

            // Apply final tail visibility (tail09)
            Object finalTail = animationProcessor.getClass().getMethod("getBone", String.class).invoke(animationProcessor, "tail09");
            if (finalTail != null) {
                finalTail.getClass().getMethod("setHidden", boolean.class).invoke(finalTail, !config.isTail09Visible());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to configure tail visibility", e);
        }
    } // configureTailVisibility()

} // Class: BoneTransformations