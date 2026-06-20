package net.heriazone.lovelylib.common.entity;

import net.heriazone.lovelylib.hzlib.api.entity.RobotAnimation;
import net.heriazone.hzlib.api.entity.NativeModel;
import software.bernie.geckolib.animation.AnimationState;

/**
 * GeckoLib model provider for robot entities.
 * <p>
 * Delegates model/animation resolution to NativeModel, which queries entity's
 * RobotFamily for variant-specific resources.
 */
public class KitsuneModel extends NativeModel<NativeRobotEntity> {

    // -- Inherited Methods --

    @Override
    public void setCustomAnimations(NativeRobotEntity animatable, long instanceId, AnimationState<NativeRobotEntity> event) {
        super.setCustomAnimations(animatable, instanceId, event);
        RobotAnimation.tailConfigAnimation(animatable, this, event);
    } // setCustomAnimations ()

} // Class: KitsuneModel