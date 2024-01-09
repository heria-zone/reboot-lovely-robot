package net.msymbios.rlovelyr.entity.client.model;

import net.minecraft.resources.ResourceLocation;
import net.msymbios.rlovelyr.entity.custom.VanillaEntity;
import net.msymbios.rlovelyr.entity.internal.InternalAnimation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class VanillaModel extends GeoModel<VanillaEntity> {

    // -- Methods --
    @Override
    public ResourceLocation getModelResource(VanillaEntity animatable) {
        return animatable.getCurrentModel();
    } // getModelResource ()

    @Override
    public ResourceLocation getTextureResource(VanillaEntity animatable) {
        return animatable.getTexture();
    } // getTextureResource ()

    @Override
    public ResourceLocation getAnimationResource(VanillaEntity animatable) {
        return animatable.getAnimator();
    } // getAnimationResource ()

    @Override
    public void setCustomAnimations(VanillaEntity animatable, long instanceId, AnimationState<VanillaEntity> event) {
        InternalAnimation.headAnimation(this, event);
    } // setCustomAnimations ()

} // Class VanillaModel