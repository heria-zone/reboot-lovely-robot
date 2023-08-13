package net.msymbios.rlovelyr.entity.client;

import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.custom.KitsuneEntity;
import net.msymbios.rlovelyr.entity.internal.InternalAnimation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class KitsuneModel extends AnimatedGeoModel<KitsuneEntity> {

    // -- Methods --
    @Override
    public Identifier getModelResource(KitsuneEntity animatable) {
        return animatable.getCurrentModel();
    } // getModelResource ()

    @Override
    public Identifier getTextureResource(KitsuneEntity animatable) {
        return animatable.getTexture();
    } // getTextureResource ()

    @Override
    public Identifier getAnimationResource(KitsuneEntity animatable) {
        return animatable.getAnimator();
    } // getAnimationResource ()

    @Override
    public void setLivingAnimations(KitsuneEntity animatable, Integer uniqueID, AnimationEvent event){
        super.setLivingAnimations(animatable, uniqueID, event);
        InternalAnimation.headAnimation(this, event);
    } // setLivingAnimations ()

} // Class KitsuneModel