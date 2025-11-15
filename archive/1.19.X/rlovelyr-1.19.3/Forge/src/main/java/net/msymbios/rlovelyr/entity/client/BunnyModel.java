package net.msymbios.rlovelyr.entity.client;

import net.msymbios.rlovelyr.entity.custom.BunnyEntity;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.rlovelyr.entity.internal.InternalAnimation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class BunnyModel extends GeoModel<BunnyEntity> {

    // -- Methods --
    @Override
    public ResourceLocation getModelResource(BunnyEntity animatable) {
        return animatable.getCurrentModel();
    } // getModelResource ()

    @Override
    public ResourceLocation getTextureResource(BunnyEntity animatable) {
        return animatable.getTexture();
    } // getTextureResource ()

    @Override
    public ResourceLocation getAnimationResource(BunnyEntity animatable) {
        return animatable.getAnimator();
    } // getAnimationResource ()

    @Override
    public void setCustomAnimations(BunnyEntity animatable, long instanceId, AnimationState<BunnyEntity> event) {
        InternalAnimation.headAnimation(this, event);
    } // setCustomAnimations ()

} // Class BunnyModel