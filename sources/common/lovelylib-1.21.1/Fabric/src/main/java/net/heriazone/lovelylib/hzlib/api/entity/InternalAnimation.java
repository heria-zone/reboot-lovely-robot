package net.heriazone.lovelylib.hzlib.api.entity;

import net.heriazone.hzlib.api.animation.AnimationPool;
import net.heriazone.hzlib.api.animation.AnimationProfile;
import net.heriazone.hzlib.api.animation.LoopBehavior;
import net.heriazone.lovelylib.api.animation.BoneTransformations;
import net.heriazone.lovelylib.api.animation.TailAnimationUtils;
import net.heriazone.lovelylib.common.entity.RobotEntity;
import net.heriazone.hzlib.framework.entity.enums.EntityState;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.Random;

/**
 * Manages GeckoLib animation controllers and bone transformations for robot entities.
 * <p>
 * <b>Architecture:</b> Profile-aware animation system that reads {@link AnimationProfile}
 * from the entity's native type feature, resolves animation names via pool selection,
 * then constructs GeckoLib {@code RawAnimation} objects. All animation name constants
 * and hardcoded switch statements have been removed — the profile is the single source
 * of truth for what animations exist and how they are selected.
 * <p>
 * <b>GeckoLib boundary:</b> This class is the only place where GeckoLib types are
 * constructed. {@link AnimationProfile} and {@link AnimationPool} in HZLib Common
 * return plain {@code String} names only.
 * <p>
 * <b>Fallback behavior:</b> If no profile is attached to the entity type (legacy
 * entities), the controller falls back to the hardcoded idle animation name to
 * prevent null pointer errors during migration.
 */
public class InternalAnimation {

    // -- Controller Name Constants --

    private static final String CONTROLLER_LOCOMOTION = "Locomotion";
    private static final String CONTROLLER_ATTACK      = "Attack";

    // -- Transition Tick Constants --

    private static final int LOCOMOTION_TRANSITION_TICKS = 2;
    private static final int ATTACK_TRANSITION_TICKS     = 0;

    // -- Fallback Animation Names --

    private static final String FALLBACK_IDLE   = "idle";
    private static final String FALLBACK_ATTACK = "attack";

    // -- Shared Random --

    private static final Random RANDOM = new Random();

    // -- Animation Controllers --

    /**
     * Creates the attack animation controller for combat actions.
     * <p>
     * <b>Profile integration:</b> Reads the attack pool from the entity's
     * {@link AnimationProfile}. If the pool has {@link LoopBehavior#INTERRUPT},
     * the {@code RawAnimation} is built with {@code override_previous_animation: true}
     * (GeckoLib's {@link Animation.LoopType#PLAY_ONCE} with forced override).
     * <p>
     * <b>Fallback:</b> Uses {@code "attack"} with PLAY_ONCE if no profile is present.
     *
     * @param animatable robot entity to animate
     * @param <T>        entity type extending RobotEntity and GeoAnimatable
     * @return configured attack animation controller
     */
    public static <T extends RobotEntity & GeoAnimatable> AnimationController<T> attackAnimation(T animatable) {
        return new AnimationController<>(animatable, CONTROLLER_ATTACK,
                ATTACK_TRANSITION_TICKS, state -> {
            if (!animatable.swinging) {
                state.getController().forceAnimationReset();
                return PlayState.STOP;
            }

            AnimationProfile profile = getProfile(animatable);
            String animName;
            LoopBehavior loopBehavior;

            if (profile != null && AnimationProfile.isUsable(profile.getAttack())) {
                animName     = profile.getAttack().selectNext(RANDOM);
                loopBehavior = profile.getAttack().getLoopBehavior(animName);
            } else {
                animName     = FALLBACK_ATTACK;
                loopBehavior = LoopBehavior.INTERRUPT;
            }

            RawAnimation raw = buildRawAnimation(animName, loopBehavior);
            return state.setAndContinue(raw);
        });
    } // attackAnimation ()

    /**
     * Creates the locomotion animation controller for movement and idle states.
     * <p>
     * <b>Priority chain (from profile):</b>
     * <pre>
     *   vehicle riding? → sit pool (robots use sit for vehicles)
     *   moving?         → walk pool ?? idle
     *   standby sitting → sit pool ?? rest ?? idle
     *   standby resting → rest pool ?? idle
     *   default         → idle pool
     * </pre>
     * <p>
     * <b>Base pose controller:</b> If the profile declares a {@code basePoseAnimation},
     * a separate parallel controller is NOT created here — call
     * {@link #basePoseAnimation(GeoAnimatable, String)} separately if needed.
     *
     * @param entity robot entity to animate
     * @param <T>    entity type extending RobotEntity and GeoAnimatable
     * @return configured locomotion animation controller
     */
    public static <T extends RobotEntity & GeoAnimatable> AnimationController<T> locomotionAnimation(T entity) {
        return new AnimationController<>(entity, CONTROLLER_LOCOMOTION,
                LOCOMOTION_TRANSITION_TICKS, state -> {
            AnimationProfile profile = getProfile(entity);
            boolean isMoving = state.isMoving();

            String animName = resolveLocomotionAnimation(entity, profile, isMoving);
            LoopBehavior loop = resolveLoopBehavior(profile, animName);
            RawAnimation raw = buildRawAnimation(animName, loop);

            return state.setAndContinue(raw);
        });
    } // locomotionAnimation ()

    /**
     * Creates an optional base pose animation controller that runs as a parallel layer.
     * <p>
     * <b>Usage:</b> Call this in {@code registerControllers()} only for entities whose
     * profile declares a {@code basePoseAnimation} (e.g., Gourdragora default).
     * Robots do not use a base pose layer.
     *
     * @param animatable    entity to animate
     * @param basePoseName  animation name for the base pose layer
     * @param <T>           entity type
     * @return base pose animation controller
     */
    public static <T extends GeoAnimatable> AnimationController<T> basePoseAnimation(T animatable, String basePoseName) {
        RawAnimation basePose = RawAnimation.begin().thenLoop(basePoseName);
        return new AnimationController<>(animatable, "BasePose", 0, state ->
                state.setAndContinue(basePose));
    } // basePoseAnimation ()

    // -- Locomotion Resolution --

    /**
     * Resolves the animation name for the current locomotion state using the profile's
     * priority chain. Falls back to idle at every step if a slot is null or empty.
     *
     * @param entity   robot entity
     * @param profile  animation profile (may be null for legacy entities)
     * @param isMoving whether the entity is currently moving
     * @return animation name to play
     */
    private static String resolveLocomotionAnimation(RobotEntity entity,
                                                     AnimationProfile profile,
                                                     boolean isMoving) {
        if (profile == null) {
            // Legacy fallback — no profile attached
            return legacyLocomotionFallback(entity, isMoving);
        }

        // Priority 1: Vehicle riding — robots use sit for vehicle state
        if (entity.getVehicle() != null) {
            return selectFromPool(profile.getSit(), profile.getIdle());
        }

        // Priority 2: Moving
        if (isMoving) {
            return selectFromPool(profile.getWalk(), profile.getIdle());
        }

        // Priority 3: Standby state
        if (entity.getCurrentState() == EntityState.Standby) {
            if (entity.isInSittingPose()) {
                return selectFromPool(profile.getSit(), profile.getRest(), profile.getIdle());
            } else {
                return selectFromPool(profile.getRest(), profile.getIdle());
            }
        }

        // Default: idle pool
        return selectFromPool(profile.getIdle());
    } // resolveLocomotionAnimation ()

    /**
     * Selects from the first non-empty pool in the provided list.
     * Returns {@code "idle"} as the final fallback if all pools are null or empty.
     *
     * @param pools pools to try in priority order
     * @return selected animation name
     */
    @SafeVarargs
    private static String selectFromPool(AnimationPool... pools) {
        for (AnimationPool pool : pools) {
            if (AnimationProfile.isUsable(pool)) {
                String name = pool.selectNext(RANDOM);
                if (name != null) return name;
            }
        }
        return FALLBACK_IDLE;
    } // selectFromPool ()

    /**
     * Resolves the loop behavior for the given animation name from the profile.
     * Returns {@link LoopBehavior#LOOP} as the default for locomotion animations.
     *
     * @param profile  animation profile (may be null)
     * @param animName animation name to look up
     * @return loop behavior for that animation
     */
    private static LoopBehavior resolveLoopBehavior(AnimationProfile profile, String animName) {
        if (profile == null) return LoopBehavior.LOOP;

        // Check each slot for the animation name
        AnimationPool[] slots = {
            profile.getIdle(), profile.getWalk(), profile.getRest(),
            profile.getSit(), profile.getRide(), profile.getAttack(), profile.getHurt()
        };
        for (AnimationPool pool : slots) {
            if (pool != null && !pool.isEmpty()) {
                LoopBehavior behavior = pool.getLoopBehavior(animName);
                if (behavior != LoopBehavior.LOOP || pool.getAnimations().stream()
                        .anyMatch(a -> a.getName().equals(animName))) {
                    return behavior;
                }
            }
        }
        return LoopBehavior.LOOP;
    } // resolveLoopBehavior ()

    /**
     * Legacy locomotion fallback for entities without an {@link AnimationProfile}.
     * Preserves the exact behavior of the old {@code AnimationStateManager}.
     *
     * @param entity   robot entity
     * @param isMoving whether the entity is currently moving
     * @return animation name
     */
    private static String legacyLocomotionFallback(RobotEntity entity, boolean isMoving) {
        if (entity.getVehicle() != null) return "sit";
        if (isMoving) return "walk";
        if (entity.getCurrentState() == EntityState.Standby) {
            return entity.isInSittingPose() ? "sit" : "rest";
        }
        return FALLBACK_IDLE;
    } // legacyLocomotionFallback ()

    // -- RawAnimation Construction --

    /**
     * Constructs a GeckoLib {@link RawAnimation} from an animation name and loop behavior.
     * <p>
     * <b>LoopBehavior mapping:</b>
     * <ul>
     *   <li>{@link LoopBehavior#LOOP} → {@code thenLoop(name)}</li>
     *   <li>{@link LoopBehavior#PLAY_ONCE} → {@code then(name, PLAY_ONCE)}</li>
     *   <li>{@link LoopBehavior#HOLD_LAST_FRAME} → {@code then(name, HOLD_ON_LAST_FRAME)}</li>
     *   <li>{@link LoopBehavior#INTERRUPT} → {@code then(name, PLAY_ONCE)} — caller must
     *       set {@code override_previous_animation} via controller configuration</li>
     *   <li>Others → {@code thenLoop(name)} as safe default</li>
     * </ul>
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

    // -- Profile Access --

    /**
     * Retrieves the {@link AnimationProfile} from the entity's native type feature map.
     * Returns {@code null} if the entity has no native type or no profile attached.
     *
     * @param entity robot entity
     * @return animation profile, or null
     */
    private static AnimationProfile getProfile(RobotEntity entity) {
        if (entity.nativeEntity == null) return null;
        return entity.nativeEntity.getFeature(AnimationProfile.class).orElse(null);
    } // getProfile ()

    // -- Bone Transformations --

    /**
     * Applies head rotation to follow entity's look direction.
     * <p>
     * <b>Architecture:</b> Delegates calculation to common BoneTransformations
     * while handling GeckoLib-specific bone manipulation.
     *
     * @param renderer GeoModel containing bone hierarchy
     * @param event    animation state with entity data
     * @param <T>      entity type extending RobotEntity and GeoAnimatable
     */
    public static <T extends RobotEntity & GeoAnimatable> void headAnimation(GeoModel renderer, AnimationState<T> event) {
        GeoBone head = renderer.getAnimationProcessor().getBone("head");
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

    /**
     * Configures tail visibility based on entity level for progressive unlocking.
     * <p>
     * <b>Architecture:</b> Delegates calculation to common BoneTransformations
     * while handling GeckoLib-specific bone visibility manipulation.
     *
     * @param entity   robot entity with level data
     * @param renderer GeoModel containing tail bones
     * @param event    animation state (unused but required by signature)
     * @param <T>      entity type extending RobotEntity and GeoAnimatable
     */
    public static <T extends RobotEntity & GeoAnimatable> void tailConfigAnimation(RobotEntity entity, GeoModel renderer, AnimationState<T> event) {
        TailAnimationUtils.TailVisibilityConfig config = TailAnimationUtils.calculateTailVisibility(entity);

        // Apply visibility to base tail
        GeoBone baseTail = renderer.getAnimationProcessor().getBone("tail0");
        if (baseTail != null) {
            baseTail.setHidden(!config.isBaseTailVisible());
        }

        // Apply visibility to numbered tails (tail01-tail08)
        for (int i = 1; i <= 8; i++) {
            GeoBone tail = renderer.getAnimationProcessor().getBone("tail0" + i);
            if (tail != null) {
                tail.setHidden(!config.isTailVisible(i));
            }
        }

        // Apply visibility to final tail (tail09)
        GeoBone finalTail = renderer.getAnimationProcessor().getBone("tail09");
        if (finalTail != null) {
            finalTail.setHidden(!config.isTail09Visible());
        }
    } // tailConfigAnimation ()

} // Class: InternalAnimation
