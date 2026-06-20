package net.heriazone.hzlib.api.entity;

import net.heriazone.hzlib.api.animation.*;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.Random;

/**
 * Manages GeckoLib animation controllers and bone transformations for HZLib entities.
 * <p>
 * <b>Architecture:</b> Profile-aware animation system. Reads {@link AnimationProfile}
 * from the entity's current animator variant via {@link AnimationStateManager}, resolves
 * animation names via pool selection, then constructs GeckoLib {@code RawAnimation} objects.
 * <p>
 * <b>GeckoLib boundary:</b> This class is the only place in HZLib where GeckoLib types
 * are constructed. {@link AnimationProfile} and {@link AnimationPool} return plain
 * {@code String} names only — no GeckoLib dependency in Common.
 * <p>
 * <b>Fallback behavior:</b> If no profile is attached to the entity type, the controller
 * falls back to the standard animation name constants in {@link AnimationStateManager}.
 */
public class InternalAnimation {

    // -- Shared Random --

    private static final Random RANDOM = new Random();

    // -- Animation Controllers --

    /**
     * Creates the attack animation controller for combat actions.
     * <p>
     * <b>Profile integration:</b> Reads the attack pool from the entity's
     * {@link AnimationProfile}. {@link LoopBehavior#INTERRUPT} maps to
     * {@link Animation.LoopType#PLAY_ONCE} (overrides other controllers).
     *
     * @param animatable entity to animate
     * @param <T>        entity type extending NativeEntity and GeoAnimatable
     * @return configured attack animation controller
     */
    public static <T extends NativeEntity & GeoAnimatable> AnimationController<T> attackAnimation(T animatable) {
        return new AnimationController<>(animatable,
                AnimationStateManager.ControllerType.ATTACK.getName(),
                AnimationStateManager.ATTACK_TRANSITION_TICKS, state -> {
            if (!AnimationStateManager.shouldPlayAttackAnimation(animatable)) {
                // Return STOP without forceAnimationReset — let GeckoLib finish the current
                // swing naturally rather than aborting it mid-frame.
                return PlayState.STOP;
            }

            AnimationProfile profile = AnimationStateManager.resolveProfilePublic(animatable);
            String animName;
            LoopBehavior loopBehavior;

            if (profile != null && AnimationProfile.isUsable(profile.getAttack())) {
                animName     = profile.getAttack().selectNext(RANDOM);
                loopBehavior = profile.getAttack().getLoopBehavior(animName != null ? animName : AnimationStateManager.ATTACK);
            } else {
                animName     = AnimationStateManager.ATTACK;
                loopBehavior = LoopBehavior.INTERRUPT;
            }

            if (animName == null) animName = AnimationStateManager.ATTACK;
            return state.setAndContinue(buildRawAnimation(animName, loopBehavior));
        });
    } // attackAnimation ()

    /**
     * Creates the locomotion animation controller for movement and idle states.
     * <p>
     * <b>Priority chain:</b> vehicle riding → moving → standby sitting → standby resting → idle.
     * Delegates state resolution to {@link AnimationStateManager#getLocomotionAnimation}.
     *
     * @param entity entity to animate
     * @param <T>    entity type extending NativeEntity and GeoAnimatable
     * @return configured locomotion animation controller
     */
    public static <T extends NativeEntity & GeoAnimatable> AnimationController<T> locomotionAnimation(T entity) {
        return new AnimationController<>(entity,
                AnimationStateManager.ControllerType.LOCOMOTION.getName(),
                AnimationStateManager.LOCOMOTION_TRANSITION_TICKS, state -> {
            boolean isMoving = state.isMoving();
            String animName = AnimationStateManager.getLocomotionAnimation(entity, isMoving);
            LoopBehavior loop = resolveLoopBehavior(entity, animName);
            return state.setAndContinue(buildRawAnimation(animName, loop));
        });
    } // locomotionAnimation ()

    /**
     * Creates an optional base pose animation controller that runs as a parallel layer.
     * <p>
     * <b>Usage:</b> Call only for entities whose profile declares a
     * {@code basePoseAnimation} (e.g., Gourdragora default). Most entities do not need this.
     *
     * @param animatable   entity to animate
     * @param basePoseName animation name for the base pose layer
     * @param <T>          entity type
     * @return base pose animation controller
     */
    public static <T extends GeoAnimatable> AnimationController<T> basePoseAnimation(T animatable, String basePoseName) {
        RawAnimation basePose = RawAnimation.begin().thenLoop(basePoseName);
        return new AnimationController<>(animatable, "BasePose", 0, state ->
                state.setAndContinue(basePose));
    } // basePoseAnimation ()

    // -- RawAnimation Construction --

    /**
     * Constructs a GeckoLib {@link RawAnimation} from an animation name and loop behavior.
     *
     * @param animationName animation name
     * @param loopBehavior  desired loop behavior
     * @return constructed RawAnimation
     */
    private static RawAnimation buildRawAnimation(String animationName, LoopBehavior loopBehavior) {
        return switch (loopBehavior) {
            case PLAY_ONCE, INTERRUPT -> RawAnimation.begin().then(animationName, Animation.LoopType.PLAY_ONCE);
            case HOLD_LAST_FRAME      -> RawAnimation.begin().then(animationName, Animation.LoopType.HOLD_ON_LAST_FRAME);
            default                   -> RawAnimation.begin().thenLoop(animationName);
        };
    } // buildRawAnimation ()

    /**
     * Resolves the loop behavior for the given animation name from the entity's profile.
     * Returns {@link LoopBehavior#LOOP} as the default for locomotion animations.
     */
    private static LoopBehavior resolveLoopBehavior(NativeEntity entity, String animName) {
        AnimationProfile profile = AnimationStateManager.resolveProfilePublic(entity);
        if (profile == null) return LoopBehavior.LOOP;

        AnimationPool[] slots = {
            profile.getIdle(), profile.getWalk(), profile.getRest(),
            profile.getSit(), profile.getRide(), profile.getAttack(), profile.getHurt()
        };
        for (AnimationPool pool : slots) {
            if (pool != null && !pool.isEmpty()) {
                if (pool.getAnimations().stream().anyMatch(a -> a.getName().equals(animName))) {
                    return pool.getLoopBehavior(animName);
                }
            }
        }
        return LoopBehavior.LOOP;
    } // resolveLoopBehavior ()

    // -- Bone Transformations --

    /**
     * Applies head rotation to follow entity's look direction.
     *
     * @param renderer GeoModel containing bone hierarchy
     * @param event    animation state with entity data
     * @param headBone name of the head bone in the model
     * @param <T>      entity type extending NativeEntity and GeoAnimatable
     */
    public static <T extends NativeEntity & GeoAnimatable> void headAnimation(GeoModel renderer, AnimationState<T> event, String headBone) {
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
    } // headAnimation ()

} // Class: InternalAnimation