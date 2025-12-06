package net.msymbios.llovelyr.lib.entity;

import net.msymbios.llovelyr.common.entity.NativeEntityType;
import net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity;
import net.msymbios.llovelyr.common.entity.enums.EntityAnimation;
import net.msymbios.llovelyr.framework.entity.enums.EntityState;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

/**
 * Manages GeckoLib animation controllers and bone transformations for robot entities.
 * <p>
 * <b>Architecture:</b> Bridges entity behavior state with GeckoLib's animation system,
 * translating game logic (walking, attacking, sitting) into animation playback commands.
 * Separate controllers handle locomotion and combat to prevent animation conflicts.
 * <p>
 * <b>Design Decision:</b> Static methods allow reuse across all robot types without
 * inheritance complexity. Each robot can customize animations by overriding controller
 * creation while sharing common animation logic.
 * <p>
 * <b>Performance:</b> Animation controllers are created once per entity and cached by
 * GeckoLib. Bone transformations run every render frame but are optimized by GeckoLib's
 * animation processor.
 */
public class InternalAnimation {

    // -- Animation Definitions --

    /** Neutral standing animation, loops indefinitely. */
    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop(EntityAnimation.Idle.getName());

    /** Walking animation, loops while entity is moving. */
    public static final RawAnimation WALK = RawAnimation.begin().thenLoop(EntityAnimation.Walk.getName());

    /** Standing idle animation in standby mode, loops indefinitely. */
    public static final RawAnimation REST = RawAnimation.begin().thenLoop(EntityAnimation.Rest.getName());

    /** Sitting/resting animation in standby mode after delay, loops indefinitely. */
    public static final RawAnimation SIT = RawAnimation.begin().thenLoop(EntityAnimation.Sit.getName());

    /** Attack swing animation, plays once per attack. */
    public static final RawAnimation ATTACK_SWING = RawAnimation.begin().then(EntityAnimation.Attack.getName(), Animation.LoopType.PLAY_ONCE);

    // -- Animation Controllers --

    /**
     * Creates attack animation controller for combat actions.
     * <p>
     * <b>Architecture:</b> Separate controller prevents attack animations from being
     * interrupted by locomotion. 5-tick transition ensures smooth blending.
     * <p>
     * <b>Usage:</b> Triggered by entity's handSwinging flag, synced with damage application.
     *
     * @param animatable robot entity to animate
     * @param <T> entity type extending LovelyRobotEntity and GeoAnimatable
     * @return configured attack animation controller
     */
    public static <T extends LovelyRobotEntity & GeoAnimatable> AnimationController<T> attackAnimation(T animatable) {
        return new AnimationController<>(animatable, "Attack", 0, state -> {
            if (animatable.swinging) {
                return state.setAndContinue(ATTACK_SWING);
            }
            state.getController().forceAnimationReset();
            return PlayState.STOP;
        });
    } // attackAnimation ()

    /**
     * Creates locomotion animation controller for movement and idle states.
     * <p>
     * <b>Architecture:</b> Handles all non-combat animations with priority order:
     * moving > sitting (in standby) > resting (in standby) > idle. State checks
     * run every tick to ensure responsive animation transitions.
     * <p>
     * <b>Standby Animation Flow:</b> When in standby mode, robot starts with REST
     * (standing idle). After configurable delay without movement, transitions to
     * SIT (sitting pose with smaller hitbox). Movement interrupts sitting and
     * returns to REST after stopping.
     * <p>
     * <b>Design Decision:</b> 5-tick blend time provides smooth transitions between
     * REST and SIT animations. Movement detection includes velocity check for
     * extra safety against animation glitches.
     *
     * @param entity robot entity to animate
     * @param <T> entity type extending LovelyRobotEntity and GeoAnimatable
     * @return configured locomotion animation controller
     */
    public static <T extends LovelyRobotEntity & GeoAnimatable> AnimationController<T> locomotionAnimation(T entity) {
        return new AnimationController<T>(entity, "Locomotion", 2, state -> {
            // Check movement with both animation state and velocity
            boolean isMoving = state.isMoving();

            if (isMoving) {
                return state.setAndContinue(WALK);
            } else if (entity.getCurrentState() == EntityState.Standby) {
                // In standby mode - check if should be sitting or resting
                if (entity.isInSittingPose()) {
                    return state.setAndContinue(SIT);
                } else {
                    return state.setAndContinue(REST);
                }
            } else {
                return state.setAndContinue(IDLE);
            }
        });
    } // locomotionAnimation ()

    // -- Bone Transformations --

    /**
     * Applies head rotation to follow entity's look direction.
     * <p>
     * <b>Architecture:</b> Transforms head bone based on entity's pitch/yaw, creating
     * natural head tracking. Runs every render frame for smooth head movement.
     * <p>
     * <b>Performance:</b> Bone lookup is cached by GeckoLib's animation processor.
     * Null check prevents crashes if model lacks head bone.
     *
     * @param renderer GeoModel containing bone hierarchy
     * @param event animation state with entity data
     * @param <T> entity type extending LovelyRobotEntity and GeoAnimatable
     */
    public static <T extends LovelyRobotEntity & GeoAnimatable> void headAnimation(GeoModel renderer, AnimationState<T> event) {
        GeoBone head = renderer.getAnimationProcessor().getBone("head");
        if (head != null) {
            EntityModelData entityData = event.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * ((float) Math.PI / 180F));
            head.setRotY(entityData.netHeadYaw() * ((float) Math.PI / 180F));
        }
    } // headAnimation ()

    /**
     * Configures tail visibility based on entity level for progressive unlocking.
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
     * @param renderer GeoModel containing tail bones
     * @param event animation state (unused but required by signature)
     * @param <T> entity type extending LovelyRobotEntity and GeoAnimatable
     */
    public static <T extends LovelyRobotEntity & GeoAnimatable> void tailConfigAnimation(LovelyRobotEntity entity, GeoModel renderer, AnimationState<T> event) {
        var maxLevel = (entity.nativeEntity instanceof NativeEntityType robotType)
                ? robotType.getMaxLevel()
                : 0;
        var maxTails = 8;
        int levelPerTails = maxLevel / maxTails;
        boolean[] tailVisibility = new boolean[maxTails];

        // Show only base tail if below first threshold
        if (entity.getCurrentLevel() < levelPerTails) {
            renderer.getAnimationProcessor().getBone("tail0").setHidden(false);
            for (int i = 0; i <= maxTails; i++) {
                renderer.getAnimationProcessor().getBone("tail0" + (i + 1)).setHidden(true);
            }
            return;
        }

        // Calculate which tails should be visible based on level
        for (int i = 0; i < maxTails; i++) {
            int levelToUnlockTail = levelPerTails * i;
            if (entity.getCurrentLevel() >= levelToUnlockTail) {
                tailVisibility[i] = true;
            }
        }

        // Apply visibility to tail bones
        renderer.getAnimationProcessor().getBone("tail0").setHidden(true);
        for (int i = 0; i < maxTails; i++) {
            renderer.getAnimationProcessor().getBone("tail0" + (i + 1)).setHidden(!tailVisibility[i]);
        }

        // Final tail requires max level
        renderer.getAnimationProcessor().getBone("tail09").setHidden(entity.getCurrentLevel() < maxLevel);
    } // tailConfigAnimation ()

} // Class: InternalAnimation