package net.heriazone.lovelylib.api.animation;

import net.heriazone.lovelylib.common.entity.RobotEntity;
import net.heriazone.lovelylib.common.entity.NativeEntityType;
import net.heriazone.hzlib.api.entity.NativeEntity;

public class TailAnimationUtils {

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
    public static TailVisibilityConfig calculateTailVisibility(RobotEntity entity) {
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

    // -- Utils --

    /**
     * Configures tail visibility on GeckoLib model using reflection.
     * <p>
     * <b>Architecture:</b> Uses reflection to apply tail visibility calculations
     * to GeckoLib bones without direct GeckoLib dependency in Common module.
     *
     * @param entity robot entity with level data
     * @param renderer GeoModel containing tail bones
     * @param event animation state (unused but required by signature)
     * @param <T> entity type extending InternalEntity
     */
    public static <T extends NativeEntity> void configureTailVisibility(RobotEntity entity, Object renderer, Object event) {
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

} // Class: TailAnimationUtils