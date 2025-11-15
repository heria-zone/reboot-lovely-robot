package net.msymbios.rlovelyr.common.entity;

import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.internal.RobotEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public abstract class InternalModel<T extends RobotEntity & GeoEntity> extends GeoModel<T> {

    // -- Inherited Methods --
    @Override
    public Identifier getModelResource(T animatable) {
        return animatable.getCurrentModel();
    } // getModelResource ()

    @Override
    public Identifier getTextureResource(T animatable) {
        return animatable.getTexture();
    } // getTextureResource ()

    @Override
    public Identifier getAnimationResource(T animatable) {
        return animatable.getAnimator();
    } // getAnimationResource ()

    @Override
    public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> event){
        InternalAnimation.headAnimation(this, event);
    } // setCustomAnimations ()

} // Class InternalModel