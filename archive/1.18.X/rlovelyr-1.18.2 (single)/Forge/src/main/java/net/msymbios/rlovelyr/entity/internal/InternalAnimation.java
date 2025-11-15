package net.msymbios.rlovelyr.entity.internal;

import net.msymbios.rlovelyr.entity.internal.enums.EntityAnimation;
import net.msymbios.rlovelyr.entity.internal.enums.EntityState;
import net.msymbios.rlovelyr.item.utils.Utility;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class InternalAnimation {

    // -- Variables --
    public static final AnimationBuilder IDLE = new AnimationBuilder().addAnimation(EntityAnimation.Idle.getName(), ILoopType.EDefaultLoopTypes.LOOP);
    public static final AnimationBuilder WALK = new AnimationBuilder().addAnimation(EntityAnimation.Walk.getName(), ILoopType.EDefaultLoopTypes.LOOP);
    public static final AnimationBuilder REST = new AnimationBuilder().addAnimation(EntityAnimation.Rest.getName(), ILoopType.EDefaultLoopTypes.LOOP);
    public static final AnimationBuilder SIT = new AnimationBuilder().addAnimation(EntityAnimation.Sit.getName(), ILoopType.EDefaultLoopTypes.LOOP);
    public static final AnimationBuilder ATTACK = new AnimationBuilder().addAnimation(EntityAnimation.Attack.getName(), ILoopType.EDefaultLoopTypes.PLAY_ONCE);

    // -- Methods --
    public static <T extends InternalEntity & IAnimatable> AnimationController<T> attackAnimation(T animatable) {
        return new AnimationController<>(animatable, "Attack", 0, state -> {
            if (animatable.swinging) {
                state.getController().setAnimation(ATTACK);
                return PlayState.CONTINUE;
            }
            state.getController().clearAnimationCache();
            return PlayState.STOP;
        });
    } // attackAnimation ()

    public static <T extends InternalEntity & IAnimatable> AnimationController<T> locomotionAnimation(T entity) {
        return new AnimationController<T>(entity, "Locomotion", 0, state -> {
            if (entity.getVehicle() != null) state.getController().setAnimation(SIT);
            else if (state.isMoving()) state.getController().setAnimation(WALK);
            else if(entity.getCurrentState() == EntityState.Standby) state.getController().setAnimation(REST);
            else state.getController().setAnimation(IDLE);
            return PlayState.CONTINUE;
        });
    } // locomotionAnimation ()

    public static void headAnimation (AnimatedGeoModel renderer, AnimationEvent event) {
        IBone head = renderer.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) event.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationX(extraData.headPitch * ((float) Utility.PI / 180F));
            head.setRotationY(extraData.netHeadYaw * ((float) Utility.PI / 180F));
        }
    } // headAnimation ()

    public static void tailConfigAnimation (InternalEntity entity, AnimatedGeoModel renderer, AnimationEvent event) {
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