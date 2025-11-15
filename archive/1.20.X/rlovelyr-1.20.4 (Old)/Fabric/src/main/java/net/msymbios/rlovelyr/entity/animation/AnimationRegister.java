package net.msymbios.rlovelyr.entity.animation;

import net.minecraft.entity.vehicle.BoatEntity;
import net.msymbios.rlovelyr.entity.animation.internal.Priority;
import net.msymbios.rlovelyr.entity.internal.InternalEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationState;

import java.util.function.BiPredicate;

public class AnimationRegister {

    // -- Variables --
    private static final double MIN_SPEED = 0.05;

    // -- Methods --

    public static void registerAnimationState() {
        //register("death", Animation.LoopType.PLAY_ONCE, Priority.HIGHEST, (maid, event) -> maid.isDead());
        //register("sleep", Priority.HIGHEST, (maid, event) -> maid.getPose() == EntityPose.SLEEPING);
        //register("swim", Priority.HIGHEST, (maid, event) -> maid.isSwimming());

        register("sit", Priority.NORMAL, (entity, event) -> entity.getVehicle() instanceof BoatEntity);
        register("sit", Priority.HIGH, (entity, event) -> entity.isInSittingPose() && entity.getSitPoseChangeTimer() == 50);
        register("rest", Priority.NORMAL, (entity, event) -> entity.isInSittingPose() && entity.getSitPoseChangeTimer() != 50);

        //register("swim_stand", Priority.NORMAL, (maid, event) -> maid.isTouchingWater());
        //register("attacked", Animation.LoopType.PLAY_ONCE, Priority.NORMAL, (maid, event) -> maid.hurtTime > 0);
        //register("attack", Animation.LoopType.LOOP, Priority.HIGH, (maid, event) -> maid.handSwinging);
        register("jump", Priority.NORMAL, (maid, event) -> !maid.isOnGround() && !maid.isSubmergedInWater());

        //register("run", Priority.LOW, (maid, event) -> maid.isOnGround() && maid.isSprinting());
        register("walk", Priority.LOW, (maid, event) -> maid.isOnGround() && event.getLimbSwingAmount() > MIN_SPEED);

        register("idle", Priority.LOWEST, (maid, event) -> true);
    } // registerAnimationState ()

    private static void register(String animationName, Animation.LoopType loopType, int priority, BiPredicate<InternalEntity, AnimationState<? extends GeoAnimatable>> predicate) {
        AnimationManager manager = AnimationManager.getInstance();
        manager.register(new AnimationStatus(animationName, loopType, priority, predicate));
    } // register ()

    private static void register(String animationName, int priority, BiPredicate<InternalEntity, AnimationState<? extends GeoAnimatable>> predicate) {
        register(animationName, Animation.LoopType.LOOP, priority, predicate);
    } // register ()

} // Class AnimationRegister