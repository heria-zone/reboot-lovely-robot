package net.msymbios.rlovelyr.entity.internal;

import net.minecraft.entity.vehicle.VehicleEntity;
import net.msymbios.rlovelyr.entity.enums.EntityAnimation;
import net.msymbios.rlovelyr.entity.enums.EntityState;
import net.msymbios.rlovelyr.util.internal.Utility;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public final class InternalAnimation {

    // -- Variables --
    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop(EntityAnimation.Idle.getName());
    public static final RawAnimation WALK = RawAnimation.begin().thenLoop(EntityAnimation.Walk.getName());
    public static final RawAnimation REST = RawAnimation.begin().thenLoop(EntityAnimation.Rest.getName());
    public static final RawAnimation SIT = RawAnimation.begin().thenLoop(EntityAnimation.Sit.getName());
    public static final RawAnimation ATTACK = RawAnimation.begin().thenPlay(EntityAnimation.Attack.getName());

    // -- Methods --
    public static <T extends InternalEntity & GeoAnimatable> AnimationController<T> attackAnimation(T animatable) {
        return new AnimationController<>(animatable, "Attack", 0, state -> {
            if (animatable.handSwinging) return state.setAndContinue(ATTACK);
            state.getController().forceAnimationReset();
            return PlayState.STOP;
        });
    } // attackAnimation ()

    public static <T extends InternalEntity & GeoAnimatable> AnimationController<T> locomotionAnimation(T entity) {
        return new AnimationController<T>(entity, "Locomotion", 0, state -> {
            if (entity.getVehicle() instanceof VehicleEntity) return state.setAndContinue(SIT);
            if (state.isMoving()) return state.setAndContinue(WALK);
            else if(entity.getCurrentState() == EntityState.Standby) return state.setAndContinue(REST);
            else return state.setAndContinue(IDLE);
        });
    } // locomotionAnimation ()

    public static <T extends InternalEntity & GeoAnimatable> void headAnimation(GeoModel renderer, AnimationState<T> event) {
        CoreGeoBone head = renderer.getAnimationProcessor().getBone("head");

        // TODO: Develop a system to hide and show body parts, to be used for customization
        // head.setHidden(true);

        if (head != null) {
            EntityModelData entityData = event.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * ((float) Utility.PI / 180F));
            head.setRotY(entityData.netHeadYaw() * ((float) Utility.PI / 180F));
        }
    } // setCustomAnimations ()

} // Class InternalAnimation