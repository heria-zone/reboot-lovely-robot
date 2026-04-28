package net.heriazone.lovelylib.common.entity;

import net.heriazone.hzlib.api.entity.InternalModel;
import net.heriazone.lovelylib.hzlib.api.entity.InternalAnimation;
import software.bernie.geckolib.animation.AnimationState;

/**
 * GeckoLib model provider for robot entities.
 * <p>
 * Delegates model/animation resolution to InternalModel, which queries entity's
 * NativeEntityType for variant-specific resources.
 */
public class KitsuneModel extends InternalModel<NativeRobotEntity> {

    // -- Inherited Methods --

    @Override
    public void setCustomAnimations(NativeRobotEntity animatable, long instanceId, AnimationState<NativeRobotEntity> event) {
        super.setCustomAnimations(animatable, instanceId, event);
        InternalAnimation.tailConfigAnimation(animatable, this, event);
    } // setCustomAnimations ()

} // Class: KitsuneModel