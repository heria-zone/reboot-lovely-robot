package net.msymbios.llovelyr.lib.animation;

import net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity;
import net.msymbios.llovelyr.framework.entity.enums.EntityState;

/**
 * Manages animation state transitions and condition checking for robot entities.
 * <p>
 * <b>Architecture:</b> Centralizes animation decision logic that was previously
 * scattered across loader-specific animation controllers. Provides consistent
 * animation behavior across all loaders while keeping GeckoLib integration
 * in loader modules.
 * <p>
 * <b>Design Decision:</b> Static methods allow easy reuse without inheritance
 * complexity. Animation state logic is pure business logic that doesn't depend
 * on GeckoLib types or rendering context.
 */
public class AnimationStateManager {

    // -- Attack Animation Logic --

    /**
     * Determines if attack animation should be playing.
     * <p>
     * <b>Logic:</b> Attack animation plays when entity is actively swinging.
     * This is synchronized with damage application and provides visual feedback
     * for combat actions.
     *
     * @param entity robot entity to check
     * @return true if attack animation should play
     */
    public static boolean shouldPlayAttackAnimation(LovelyRobotEntity entity) {
        return entity.swinging;
    } // shouldPlayAttackAnimation()

    // -- Locomotion Animation Logic --

    /**
     * Determines the appropriate locomotion animation for current entity state.
     * <p>
     * <b>Architecture:</b> Handles priority order: moving > sitting (in standby) >
     * resting (in standby) > idle. State checks run every tick to ensure responsive
     * animation transitions.
     * <p>
     * <b>Standby Animation Flow:</b> When in standby mode, robot starts with REST
     * (standing idle). After configurable delay without movement, transitions to
     * SIT (sitting pose with smaller hitbox). Movement interrupts sitting and
     * returns to REST after stopping.
     *
     * @param entity robot entity to check
     * @param isMoving whether the entity is currently moving
     * @return animation name that should be playing
     */
    public static String getLocomotionAnimation(LovelyRobotEntity entity, boolean isMoving) {
        if (isMoving) {
            return AnimationDefinitions.WALK;
        } else if (entity.getCurrentState() == EntityState.Standby) {
            // In standby mode - check if should be sitting or resting
            if (entity.isInSittingPose()) {
                return AnimationDefinitions.SIT;
            } else {
                return AnimationDefinitions.REST;
            }
        } else {
            return AnimationDefinitions.IDLE;
        }
    } // getLocomotionAnimation()

    // -- Animation Transition Logic --

    /**
     * Calculates appropriate transition time between animations.
     * <p>
     * <b>Design Decision:</b> Different animation types use different transition
     * times. Attack animations need immediate response (0 ticks), while locomotion
     * animations benefit from smooth blending (2 ticks).
     *
     * @param fromAnimation current animation name
     * @param toAnimation target animation name
     * @param controllerType type of animation controller
     * @return transition time in ticks
     */
    public static int getTransitionTime(String fromAnimation, String toAnimation, AnimationDefinitions.ControllerType controllerType) {
        switch (controllerType) {
            case ATTACK:
                return AnimationDefinitions.Config.ATTACK_TRANSITION_TICKS;
            case LOCOMOTION:
                return AnimationDefinitions.Config.LOCOMOTION_TRANSITION_TICKS;
            default:
                return AnimationDefinitions.Config.DEFAULT_TRANSITION_TICKS;
        }
    } // getTransitionTime()

    /**
     * Determines if an animation should loop.
     * <p>
     * <b>Logic:</b> Most animations loop continuously (idle, walk, rest, sit).
     * Attack animations play once per attack action.
     *
     * @param animationName name of the animation
     * @return true if animation should loop
     */
    public static boolean shouldLoop(String animationName) {
        if (AnimationDefinitions.ATTACK.equals(animationName)) {
            return AnimationDefinitions.Config.ATTACK_LOOP;
        }
        return AnimationDefinitions.Config.DEFAULT_LOOP;
    } // shouldLoop()

} // Class: AnimationStateManager