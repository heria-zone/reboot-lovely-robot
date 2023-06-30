package net.msymbios.rlovelyr.entity.internal;

import net.minecraft.world.entity.TamableAnimal;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public final class InternalAnimation {

    // -- Variables --
    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.lovelyrobot.idle");
    public static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.lovelyrobot.walk");
    public static final RawAnimation SIT = RawAnimation.begin().thenPlayAndHold("animation.lovelyrobot.sit");
    public static final RawAnimation ATTACK_SWING = RawAnimation.begin().then("animation.lovelyrobot.attack", Animation.LoopType.PLAY_ONCE);

    // -- Methods --
    public static <T extends TamableAnimal & GeoAnimatable> AnimationController<T> attackAnimation(T animatable, RawAnimation attackAnimation) {
        return new AnimationController<>(animatable, "Attack", 5, state -> {
            if (animatable.swinging && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) return state.setAndContinue(attackAnimation);
            state.getController().forceAnimationReset();
            return PlayState.STOP;
        });
    } // attackAnimation ()

    public static <T extends TamableAnimal & GeoAnimatable> AnimationController<T> attackAnimation(T animatable) {
        return new AnimationController<>(animatable, "Attack", 5, state -> {
            if (animatable.swinging) return state.setAndContinue(ATTACK_SWING);
            state.getController().forceAnimationReset();
            return PlayState.STOP;
        });
    } // attackAnimation ()

    public static <T extends TamableAnimal & GeoAnimatable> AnimationController<T> locomotionAnimation(T entity) {
        return new AnimationController<T>(entity, "Walk/Run/Idle", 0, state -> {
            if (state.isMoving()) return state.setAndContinue(WALK);
            else if(entity.isOrderedToSit()) return state.setAndContinue(SIT);
            else return state.setAndContinue(IDLE);
        });
    } // locomotionAnimation ()

} // Class InternalAnimation