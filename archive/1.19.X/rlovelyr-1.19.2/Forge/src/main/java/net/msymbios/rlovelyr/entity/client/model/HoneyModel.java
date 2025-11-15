package net.msymbios.rlovelyr.entity.client.model;

import net.msymbios.rlovelyr.entity.custom.HoneyEntity;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.rlovelyr.entity.internal.InternalAnimation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class HoneyModel extends AnimatedGeoModel<HoneyEntity> {

    // -- Methods --
    @Override
    public ResourceLocation getModelResource(HoneyEntity animatable) {
        return animatable.getCurrentModel();
    } // getModelResource ()

    @Override
    public ResourceLocation getTextureResource(HoneyEntity animatable) {
        return animatable.getTexture();
    } // getTextureResource ()

    @Override
    public ResourceLocation getAnimationResource(HoneyEntity animatable) {
        return animatable.getAnimator();
    } // getAnimationResource ()

    @Override
    public void setLivingAnimations(HoneyEntity animatable, Integer uniqueID, AnimationEvent event){
        super.setLivingAnimations(animatable, uniqueID, event);
        InternalAnimation.headAnimation(this, event);
    } // setLivingAnimations ()

} // Class HoneyModel