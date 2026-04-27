package net.heriazone.hzlib.api.entity;

import net.heriazone.hzlib.api.animation.*;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

/**
 * Manages GeckoLib animation controllers and bone transformations for robot entities.
 * <p>
 * <b>Architecture:</b> Thin wrapper around common animation system that handles
 * GeckoLib-specific integration. Delegates business logic to common animation
 * classes while maintaining GeckoLib boundary.
 * <p>
 * <b>Design Decision:</b> Wrapper pattern preserves existing API while enabling
 * code reuse through common animation system. Animation logic is shared across
 * all loaders.
 */
public class InternalAnimation {

    // -- Animation Definitions (GeckoLib-specific) --

    /** Neutral standing animation, loops indefinitely. */
    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop(AnimationDefinitions.IDLE);

    /** Walking animation, loops while entity is moving. */
    public static final RawAnimation WALK = RawAnimation.begin().thenLoop(AnimationDefinitions.WALK);

    /** Standing idle animation in standby mode, loops indefinitely. */
    public static final RawAnimation REST = RawAnimation.begin().thenLoop(AnimationDefinitions.REST);

    /** Sitting/resting animation in standby mode after delay, loops indefinitely. */
    public static final RawAnimation SIT = RawAnimation.begin().thenLoop(AnimationDefinitions.SIT);

    /** Attack swing animation, plays once per attack. */
    public static final RawAnimation ATTACK_SWING = RawAnimation.begin().then(AnimationDefinitions.ATTACK, Animation.LoopType.PLAY_ONCE);

    // -- Animation Controllers --

    /**
     * Creates attack animation controller for combat actions.
     * <p>
     * <b>Architecture:</b> Delegates logic to common AnimationStateManager while
     * handling GeckoLib-specific animation creation and playback.
     *
     * @param animatable robot entity to animate
     * @param <T> entity type extending LovelyRobotEntity and GeoAnimatable
     * @return configured attack animation controller
     */
    public static <T extends InternalEntity & GeoAnimatable> AnimationController<T> attackAnimation(T animatable) {
        return new AnimationController<>(animatable, AnimationDefinitions.ControllerType.ATTACK.getName(),
                AnimationDefinitions.Config.ATTACK_TRANSITION_TICKS, state -> {
            if (AnimationStateManager.shouldPlayAttackAnimation(animatable)) {
                return state.setAndContinue(ATTACK_SWING);
            }
            state.getController().forceAnimationReset();
            return PlayState.STOP;
        });
    } // attackAnimation()

    /**
     * Creates locomotion animation controller for movement and idle states.
     * <p>
     * <b>Architecture:</b> Delegates logic to common AnimationStateManager while
     * handling GeckoLib-specific animation creation and playback. Supports vehicle
     * sitting animation when robot is riding boats, minecarts, or other entities.
     *
     * @param entity robot entity to animate
     * @param <T> entity type extending InternalEntity and GeoAnimatable
     * @return configured locomotion animation controller
     */
    public static <T extends InternalEntity & GeoAnimatable> AnimationController<T> locomotionAnimation(T entity) {
        return new AnimationController<T>(entity, AnimationDefinitions.ControllerType.LOCOMOTION.getName(),
                AnimationDefinitions.Config.LOCOMOTION_TRANSITION_TICKS, state -> {
            // Check movement with both animation state and velocity
            boolean isMoving = state.isMoving();

            String animationName = AnimationStateManager.getLocomotionAnimation(entity, isMoving);
            RawAnimation animation = getAnimationByName(animationName);

            return state.setAndContinue(animation);
        });
    } // locomotionAnimation()

    // -- Animation Mapping --

    /**
     * Maps animation name to GeckoLib RawAnimation.
     *
     * @param animationName animation name from common system
     * @return corresponding RawAnimation
     */
    private static RawAnimation getAnimationByName(String animationName) {
        if (animationName.equals(AnimationDefinitions.WALK)) {
            return WALK;
        } else if (animationName.equals(AnimationDefinitions.REST)) {
            return REST;
        } else if (animationName.equals(AnimationDefinitions.SIT)) {
            return SIT;
        } else if (animationName.equals(AnimationDefinitions.ATTACK)) {
            return ATTACK_SWING;
        } else {
            return IDLE;
        }
    } // getAnimationByName()

    // -- Bone Transformations --

    /**
     * Applies head rotation to follow entity's look direction.
     * <p>
     * <b>Architecture:</b> Delegates calculation to common BoneTransformations
     * while handling GeckoLib-specific bone manipulation.
     *
     * @param renderer GeoModel containing bone hierarchy
     * @param event animation state with entity data
     * @param <T> entity type extending InternalEntity and GeoAnimatable
     */
    public static <T extends InternalEntity & GeoAnimatable> void headAnimation(GeoModel renderer, AnimationState<T> event, String headBone) {
        GeoBone head = renderer.getAnimationProcessor().getBone(headBone);
        if (head != null) {
            EntityModelData entityData = event.getData(DataTickets.ENTITY_MODEL_DATA);
            float[] rotation = BoneTransformations.calculateHeadRotation(
                    entityData.headPitch(),
                    entityData.netHeadYaw()
            );
            head.setRotX(rotation[0]);
            head.setRotY(rotation[1]);
        }
    } // headAnimation()

} // Class: InternalAnimation