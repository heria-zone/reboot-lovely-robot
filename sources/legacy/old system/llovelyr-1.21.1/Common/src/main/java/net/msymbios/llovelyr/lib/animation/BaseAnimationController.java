package net.msymbios.llovelyr.lib.animation;

import net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity;

/**
 * Base class for animation controller logic with common functionality.
 * <p>
 * <b>Architecture:</b> Provides shared animation controller patterns while
 * allowing loader-specific implementations to handle GeckoLib integration.
 * Contains business logic for animation state management and transitions.
 * <p>
 * <b>Design Decision:</b> Abstract base class allows sharing common patterns
 * while maintaining flexibility for loader-specific animation systems.
 * GeckoLib-specific code remains in loader modules.
 */
public abstract class BaseAnimationController<T extends LovelyRobotEntity> {

    // -- Fields --

    protected final String controllerName;
    protected final AnimationDefinitions.ControllerType controllerType;
    protected final int transitionTicks;

    // -- Constructor --

    /**
     * Creates base animation controller with configuration.
     *
     * @param controllerName name of the animation controller
     * @param controllerType type of controller (attack, locomotion, etc.)
     * @param transitionTicks transition time between animations
     */
    protected BaseAnimationController(String controllerName, AnimationDefinitions.ControllerType controllerType, int transitionTicks) {
        this.controllerName = controllerName;
        this.controllerType = controllerType;
        this.transitionTicks = transitionTicks;
    } // Constructor: BaseAnimationController()

    // -- Animation Logic --

    /**
     * Determines the animation that should be playing for the given entity state.
     * <p>
     * <b>Architecture:</b> Delegates to AnimationStateManager for consistent
     * animation logic across all loaders. Subclasses implement loader-specific
     * animation playback.
     *
     * @param entity robot entity to animate
     * @param isMoving whether the entity is currently moving (for locomotion)
     * @return animation name that should be playing
     */
    protected String determineAnimation(T entity, boolean isMoving) {
        switch (controllerType) {
            case ATTACK:
                return AnimationStateManager.shouldPlayAttackAnimation(entity) 
                    ? AnimationDefinitions.ATTACK 
                    : null;
            case LOCOMOTION:
                return AnimationStateManager.getLocomotionAnimation(entity, isMoving);
            default:
                return AnimationDefinitions.IDLE;
        }
    } // determineAnimation()

    /**
     * Checks if the controller should reset animation state.
     * <p>
     * <b>Logic:</b> Attack controllers reset when not attacking to prevent
     * animation from getting stuck. Locomotion controllers don't reset.
     *
     * @param entity robot entity to check
     * @param currentAnimation currently playing animation
     * @return true if animation should be reset
     */
    protected boolean shouldResetAnimation(T entity, String currentAnimation) {
        if (controllerType == AnimationDefinitions.ControllerType.ATTACK) {
            return !AnimationStateManager.shouldPlayAttackAnimation(entity);
        }
        return false;
    } // shouldResetAnimation()

    // -- Getters --

    /**
     * Gets the controller name.
     *
     * @return controller name
     */
    public String getControllerName() {
        return controllerName;
    } // getControllerName()

    /**
     * Gets the controller type.
     *
     * @return controller type
     */
    public AnimationDefinitions.ControllerType getControllerType() {
        return controllerType;
    } // getControllerType()

    /**
     * Gets the transition time in ticks.
     *
     * @return transition time
     */
    public int getTransitionTicks() {
        return transitionTicks;
    } // getTransitionTicks()

    // -- Static Helper Methods for GeckoLib Integration --

    /**
     * Handles attack animation logic for GeckoLib animation controllers.
     * <p>
     * <b>Architecture:</b> Provides common attack animation logic that can be
     * used by loader-specific GeckoLib animation controllers.
     *
     * @param animatable robot entity to animate
     * @param state animation state from GeckoLib
     * @param <E> entity type extending LovelyRobotEntity
     * @return GeckoLib PlayState for animation controller
     */
    public static <E extends LovelyRobotEntity> Object handleAttackAnimation(E animatable, Object state) {
        // This method returns the appropriate GeckoLib PlayState
        // The actual implementation depends on the loader's GeckoLib integration
        if (AnimationStateManager.shouldPlayAttackAnimation(animatable)) {
            // Return setAndContinue with attack animation
            return invokeSetAndContinue(state, AnimationDefinitions.getAttackRawAnimation());
        }
        // Force animation reset and stop
        invokeForceAnimationReset(state);
        return getPlayStateStop();
    } // handleAttackAnimation()

    /**
     * Handles locomotion animation logic for GeckoLib animation controllers.
     * <p>
     * <b>Architecture:</b> Provides common locomotion animation logic that can be
     * used by loader-specific GeckoLib animation controllers.
     *
     * @param entity robot entity to animate
     * @param state animation state from GeckoLib
     * @param <E> entity type extending LovelyRobotEntity
     * @return GeckoLib PlayState for animation controller
     */
    public static <E extends LovelyRobotEntity> Object handleLocomotionAnimation(E entity, Object state) {
        // Check movement with animation state
        boolean isMoving = invokeIsMoving(state);
        
        String animationName = AnimationStateManager.getLocomotionAnimation(entity, isMoving);
        Object rawAnimation = AnimationDefinitions.getRawAnimationByName(animationName);
        
        return invokeSetAndContinue(state, rawAnimation);
    } // handleLocomotionAnimation()

    // -- GeckoLib Reflection Helpers --

    /**
     * Invokes setAndContinue method on animation state using reflection.
     * <p>
     * <i>Note:</i> Uses reflection to maintain loader independence while
     * providing GeckoLib integration.
     */
    private static Object invokeSetAndContinue(Object state, Object animation) {
        try {
            return state.getClass().getMethod("setAndContinue", Object.class).invoke(state, animation);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke setAndContinue", e);
        }
    } // invokeSetAndContinue()

    /**
     * Invokes forceAnimationReset method on animation controller using reflection.
     */
    private static void invokeForceAnimationReset(Object state) {
        try {
            Object controller = state.getClass().getMethod("getController").invoke(state);
            controller.getClass().getMethod("forceAnimationReset").invoke(controller);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke forceAnimationReset", e);
        }
    } // invokeForceAnimationReset()

    /**
     * Invokes isMoving method on animation state using reflection.
     */
    private static boolean invokeIsMoving(Object state) {
        try {
            return (Boolean) state.getClass().getMethod("isMoving").invoke(state);
        } catch (Exception e) {
            return false;
        }
    } // invokeIsMoving()

    /**
     * Gets PlayState.STOP using reflection for loader independence.
     */
    private static Object getPlayStateStop() {
        try {
            Class<?> playStateClass = Class.forName("software.bernie.geckolib.animation.PlayState");
            return playStateClass.getField("STOP").get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get PlayState.STOP", e);
        }
    } // getPlayStateStop()

} // Class: BaseAnimationController