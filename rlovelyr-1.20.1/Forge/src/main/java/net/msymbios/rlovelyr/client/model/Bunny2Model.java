package net.msymbios.rlovelyr.client.model;

import net.minecraft.resources.ResourceLocation;
import net.msymbios.rlovelyr.entity.custom.Bunny2Entity;
import net.msymbios.rlovelyr.entity.internal.InternalAnimation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class Bunny2Model extends GeoModel<Bunny2Entity> {

    // -- Methods --
    @Override
    public ResourceLocation getModelResource(Bunny2Entity animatable) {
        return animatable.getCurrentModel();
    } // getModelResource ()

    @Override
    public ResourceLocation getTextureResource(Bunny2Entity animatable) {
        return animatable.getTexture();
    } // getTextureResource ()

    @Override
    public ResourceLocation getAnimationResource(Bunny2Entity animatable) {
        return animatable.getAnimator();
    } // getAnimationResource ()

    @Override
    public void setCustomAnimations(Bunny2Entity animatable, long instanceId, AnimationState<Bunny2Entity> event) {
        InternalAnimation.headAnimation(this, event);
    } // setCustomAnimations ()

} // Class Bunny2Model