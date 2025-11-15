package net.msymbios.rlovelyr.common.entity;

import net.msymbios.rlovelyr.common.entity.enums.EntityState;
import net.msymbios.rlovelyr.common.util.internal.Utility;
import net.msymbios.rlovelyr.entity.internal.RobotEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;

import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class InternalAnimation {

    // -- Variables --

    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    public static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    public static final RawAnimation REST = RawAnimation.begin().thenLoop("rest");
    public static final RawAnimation ATTACK_SWING = RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE);

    // -- Methods --
    public static <T extends RobotEntity & GeoAnimatable> AnimationController<T> attackAnimation(T animatable) {
        return new AnimationController<>(animatable, "Attack", 5, state -> {
            if (animatable.swinging) return state.setAndContinue(ATTACK_SWING);
            state.getController().forceAnimationReset();
            return PlayState.STOP;
        });
    } // attackAnimation ()

    public static <T extends RobotEntity & GeoAnimatable> AnimationController<T> locomotionAnimation(T entity) {
        return new AnimationController<T>(entity, "Locomotion", 0, state -> {
            if (state.isMoving()) return state.setAndContinue(WALK);
            else if(entity.getCurrentState() == EntityState.Standby) return state.setAndContinue(REST);
            else return state.setAndContinue(IDLE);
        });
    } // locomotionAnimation ()

    public static <T extends RobotEntity & GeoAnimatable> void headAnimation(GeoModel renderer, AnimationState<T> event) {
        GeoBone head = renderer.getAnimationProcessor().getBone("head");
        if (head != null) {
            EntityModelData entityData = event.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * ((float) Utility.PI / 180F));
            head.setRotY(entityData.netHeadYaw() * ((float) Utility.PI / 180F));
        }
    } // headAnimation ()

    public static <T extends RobotEntity & GeoAnimatable> void tailConfigAnimation (RobotEntity entity, GeoModel renderer, AnimationState<T> event) {
        var maxLevel = entity.nativeEntity.getMaxLevel();
        var maxTails = 8;
        int levelPerTails = maxLevel / maxTails;
        boolean[] tailVisibility = new boolean[maxTails];

        if(entity.getCurrentLevel() < levelPerTails) {
            renderer.getAnimationProcessor().getBone("tail0").setHidden(false);
            for (int i = 0; i <= maxTails; i++)
                renderer.getAnimationProcessor().getBone("tail0" + (i + 1)).setHidden(true);
            return;
        }

        // Determine which tail bones to show based on the current level
        for (int i = 0; i < maxTails; i++) {
            int levelToUnlockTail = levelPerTails * i;
            if (entity.getCurrentLevel() >= levelToUnlockTail) tailVisibility[i] = true;
        }

        // Apply visibility to each tail bone
        renderer.getAnimationProcessor().getBone("tail0").setHidden(true);
        for (int i = 0; i < maxTails; i++)
            renderer.getAnimationProcessor().getBone("tail0" + (i + 1)).setHidden(!tailVisibility[i]);

        renderer.getAnimationProcessor().getBone("tail09").setHidden(entity.getCurrentLevel() < maxLevel);
    } // tailConfigAnimation ()

} // Class InternalAnimation