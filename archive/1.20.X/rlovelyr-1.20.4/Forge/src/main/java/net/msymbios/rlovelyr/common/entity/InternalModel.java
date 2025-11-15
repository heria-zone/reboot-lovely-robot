package net.msymbios.rlovelyr.common.entity;

import net.minecraft.resources.ResourceLocation;
import net.msymbios.rlovelyr.entity.internal.RobotEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public abstract class InternalModel<T extends RobotEntity & GeoEntity> extends GeoModel<T> {

    // -- Inherited Methods --
    @Override
    public ResourceLocation getModelResource(T animatable) {
        return animatable.getCurrentModel();
    } // getModelResource ()

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return animatable.getTexture();
    } // getTextureResource ()

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return animatable.getAnimator();
    } // getAnimationResource ()

    @Override
    public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> event){
        InternalAnimation.headAnimation(this, event);
    } // setCustomAnimations ()

} // Class InternalModel