package net.heriazone.hzlib.api.animation;


import net.heriazone.hzlib.framework.entity.enums.EntityAnimation;

/**
 * Centralized animation definitions for robot entities.
 * <p>
 * <b>Architecture:</b> Provides animation name constants and timing configurations
 * that are shared across all loaders. Separates animation business logic from
 * GeckoLib-specific animation creation and playback.
 * <p>
 * <b>Design Decision:</b> Static constants allow easy reference and ensure
 * consistency across loaders. Animation timing and loop settings are defined
 * here rather than scattered across renderer classes.
 */
public class AnimationDefinitions {

    // -- Animation Names --

    /** Neutral standing animation, loops indefinitely. */
    public static final String IDLE = EntityAnimation.Idle.getName();

    /** Walking animation, loops while entity is moving. */
    public static final String WALK = EntityAnimation.Walk.getName();

    /** Standing idle animation in standby mode, loops indefinitely. */
    public static final String REST = EntityAnimation.Rest.getName();

    /** Sitting/resting animation in standby mode after delay, loops indefinitely. */
    public static final String SIT = EntityAnimation.Sit.getName();

    /** Attack swing animation, plays once per attack. */
    public static final String ATTACK = EntityAnimation.Attack.getName();

    // -- Animation Configurations --

    /**
     * Animation configuration for different animation types.
     */
    public static class Config {

        /** Default transition time between animations (in ticks). */
        public static final int DEFAULT_TRANSITION_TICKS = 2;

        /** Attack animation transition time (in ticks). */
        public static final int ATTACK_TRANSITION_TICKS = 0;

        /** Locomotion animation transition time (in ticks). */
        public static final int LOCOMOTION_TRANSITION_TICKS = 2;

        /** Whether animations should loop by default. */
        public static final boolean DEFAULT_LOOP = true;

        /** Whether attack animations should loop. */
        public static final boolean ATTACK_LOOP = false;

    } // Class: Config

    // -- Animation Types --

    /**
     * Enumeration of animation controller types.
     */
    public enum ControllerType {
        ATTACK("Attack"),
        LOCOMOTION("Locomotion");

        private final String name;

        ControllerType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    } // Enum: ControllerType

    // -- GeckoLib Integration Helpers --

    /**
     * Gets attack animation as RawAnimation object using reflection.
     * <p>
     * <i>Note:</i> Uses reflection to create GeckoLib RawAnimation without
     * direct dependency on GeckoLib in Common module.
     *
     * @return RawAnimation for attack animation
     */
    public static Object getAttackRawAnimation() {
        return createRawAnimation(ATTACK, false); // Attack doesn't loop
    } // getAttackRawAnimation()

    /**
     * Gets RawAnimation by animation name using reflection.
     * <p>
     * <i>Note:</i> Uses reflection to create GeckoLib RawAnimation without
     * direct dependency on GeckoLib in Common module.
     *
     * @param animationName name of the animation
     * @return RawAnimation for the specified animation
     */
    public static Object getRawAnimationByName(String animationName) {
        boolean shouldLoop = !ATTACK.equals(animationName); // Only attack doesn't loop
        return createRawAnimation(animationName, shouldLoop);
    } // getRawAnimationByName()

    /**
     * Creates RawAnimation using reflection to avoid GeckoLib dependency.
     *
     * @param animationName name of the animation
     * @param shouldLoop whether the animation should loop
     * @return RawAnimation object
     */
    private static Object createRawAnimation(String animationName, boolean shouldLoop) {
        try {
            Class<?> rawAnimationClass = Class.forName("software.bernie.geckolib.animation.RawAnimation");
            Object rawAnimation = rawAnimationClass.getMethod("begin").invoke(null);

            if (shouldLoop) {
                return rawAnimationClass.getMethod("thenLoop", String.class).invoke(rawAnimation, animationName);
            } else {
                Class<?> loopTypeClass = Class.forName("software.bernie.geckolib.animation.Animation$LoopType");
                Object playOnce = loopTypeClass.getField("PLAY_ONCE").get(null);
                return rawAnimationClass.getMethod("then", String.class, loopTypeClass).invoke(rawAnimation, animationName, playOnce);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create RawAnimation for: " + animationName, e);
        }
    } // createRawAnimation()

} // Class: AnimationDefinitions