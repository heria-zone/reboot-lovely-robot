package net.msymbios.rlovelyr.entity.client.model;

import net.msymbios.rlovelyr.common.entity.InternalAnimation;
import net.msymbios.rlovelyr.entity.custom.KitsuneEntity;
import net.msymbios.rlovelyr.common.entity.InternalModel;
import software.bernie.geckolib.core.animation.AnimationState;

public class KitsuneModel extends InternalModel<KitsuneEntity> {

    // -- Inherited Methods --

    @Override
    public void setCustomAnimations(KitsuneEntity animatable, long instanceId, AnimationState<KitsuneEntity> event) {
        super.setCustomAnimations(animatable, instanceId, event);
        InternalAnimation.tailConfigAnimation(animatable, this, event);
    } // setCustomAnimations ()

} // Class KitsuneModel