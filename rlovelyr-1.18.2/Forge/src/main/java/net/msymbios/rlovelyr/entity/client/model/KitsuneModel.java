package net.msymbios.rlovelyr.entity.client.model;

import net.msymbios.rlovelyr.entity.custom.KitsuneEntity;
import net.msymbios.rlovelyr.entity.internal.InternalAnimation;
import net.msymbios.rlovelyr.entity.internal.InternalModel;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class KitsuneModel extends InternalModel<KitsuneEntity> {

    // -- Inherited Methods --

    @Override
    public void setLivingAnimations(KitsuneEntity animatable, Integer uniqueID, AnimationEvent event){
        super.setLivingAnimations(animatable, uniqueID, event);
        InternalAnimation.tailConfigAnimation(animatable, this, event);
    } // setLivingAnimations ()

} // Class KitsuneModel