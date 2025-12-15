package net.msymbios.llovelyr.shared.entity.kitsune;

import net.msymbios.llovelyr.lib.entity.InternalAnimation;
import net.msymbios.llovelyr.lib.entity.InternalModel;
import net.msymbios.llovelyr.shared.entity.RobotEntity;
import software.bernie.geckolib.animation.AnimationState;

/**
 * GeckoLib model provider for robot entities.
 * <p>
 * Delegates model/animation resolution to InternalModel, which queries entity's
 * NativeEntityType for variant-specific resources.
 */
public class KitsuneModel extends InternalModel<RobotEntity> {

    // -- Inherited Methods --

    @Override
    public void setCustomAnimations(RobotEntity animatable, long instanceId, AnimationState<RobotEntity> event) {
        super.setCustomAnimations(animatable, instanceId, event);
        InternalAnimation.tailConfigAnimation(animatable, this, event);
    } // setCustomAnimations ()

} // Class: KitsuneModel
