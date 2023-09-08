package net.msymbios.rlovelyr.client.model;

import net.minecraft.resources.ResourceLocation;
import net.msymbios.rlovelyr.entity.custom.NekoEntity;
import net.msymbios.rlovelyr.entity.internal.InternalAnimation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class NekoModel extends GeoModel<NekoEntity> {

    // -- Methods --
    @Override
    public ResourceLocation getModelResource(NekoEntity animatable) {
        return animatable.getCurrentModel();
    } // getModelResource ()

    @Override
    public ResourceLocation getTextureResource(NekoEntity animatable) {
        return animatable.getTexture();
    } // getTextureResource ()

    @Override
    public ResourceLocation getAnimationResource(NekoEntity animatable) {
        return animatable.getAnimator();
    } // getAnimationResource ()

    @Override
    public void setCustomAnimations(NekoEntity animatable, long instanceId, AnimationState<NekoEntity> event) {
        InternalAnimation.headAnimation(this, event);
    } // setCustomAnimations ()

} // Class NekoModel