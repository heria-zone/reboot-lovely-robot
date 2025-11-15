package net.msymbios.rlovelyr.entity.internal;

import net.msymbios.rlovelyr.entity.enums.EntityState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public final class InternalAnimation {

    // -- Variables --
    public static final AnimationBuilder IDLE = new AnimationBuilder().addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP);
    public static final AnimationBuilder WALK = new AnimationBuilder().addAnimation("walk", ILoopType.EDefaultLoopTypes.LOOP);
    public static final AnimationBuilder SIT = new AnimationBuilder().addAnimation ("rest", ILoopType.EDefaultLoopTypes.LOOP);
    public static final AnimationBuilder ATTACK_SWING = new AnimationBuilder().addAnimation ("attack", ILoopType.EDefaultLoopTypes.PLAY_ONCE);

    // -- Methods --
    public static <T extends InternalEntity & IAnimatable> AnimationController<T> attackAnimation(T animatable) {
        return new AnimationController<>(animatable, "Attack", 5, event -> {
            if (animatable.swinging) {
                event.getController().setAnimation(ATTACK_SWING);
                return PlayState.CONTINUE;
            }
            event.getController().clearAnimationCache();
            return PlayState.STOP;
        });
    } // attackAnimation ()

    public static <T extends InternalEntity & IAnimatable> AnimationController<T> locomotionAnimation(T entity) {
        return new AnimationController<T>(entity, "Locomotion", 0, event -> {
            if (event.isMoving()) event.getController().setAnimation(WALK);
            else if(entity.getCurrentState() == EntityState.Standby) event.getController().setAnimation(SIT);
            else event.getController().setAnimation(IDLE);
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

} // Class InternalAnimation