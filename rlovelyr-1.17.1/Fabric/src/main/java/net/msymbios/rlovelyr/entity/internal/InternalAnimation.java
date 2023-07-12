package net.msymbios.rlovelyr.entity.internal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;

public final class InternalAnimation {

    // -- Variables --
    public static final AnimationBuilder IDLE = new AnimationBuilder().addAnimation("animation.lovelyrobot.idle", true);
    public static final AnimationBuilder WALK = new AnimationBuilder().addAnimation("animation.lovelyrobot.walk", true);
    public static final AnimationBuilder SIT = new AnimationBuilder().addAnimation ("animation.lovelyrobot.sit", true);
    public static final AnimationBuilder ATTACK_SWING = new AnimationBuilder().addAnimation ("animation.lovelyrobot.attack", false);

    // -- Methods --
    public static <T extends LivingEntity & IAnimatable> AnimationController<T> attackAnimation(T animatable) {
        return new AnimationController<>(animatable, "Attack", 5, event -> {
            if (animatable.handSwinging) {
                event.getController().clearAnimationCache();
                event.getController().setAnimation(ATTACK_SWING);
                animatable.handSwinging = false;
                return PlayState.CONTINUE;
            }
            event.getController().clearAnimationCache();
            return PlayState.STOP;
        });
    } // attackAnimation ()

    public static <T extends TameableEntity & IAnimatable> AnimationController<T> locomotionAnimation(T entity) {
        return new AnimationController<T>(entity, "Walk/Idle/Sit", 0, event -> {
            if (event.isMoving()) event.getController().setAnimation(WALK);
            else if(entity.isSitting()) event.getController().setAnimation(SIT);
            else event.getController().setAnimation(IDLE);
            return PlayState.CONTINUE;
        });
    } // locomotionAnimation ()

} // Class InternalAnimation