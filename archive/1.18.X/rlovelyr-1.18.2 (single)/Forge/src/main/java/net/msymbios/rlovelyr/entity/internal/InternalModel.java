package net.msymbios.rlovelyr.entity.internal;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public abstract class InternalModel<T extends InternalEntity & IAnimatable> extends AnimatedGeoModel<T> {

    // -- Inherited Methods --
    @Override
    public ResourceLocation getModelLocation(T animatable) {
        return animatable.getCurrentModel();
    } // getModelLocation ()

    @Override
    public ResourceLocation getTextureLocation(T animatable) {
        return animatable.getTexture();
    } // getTextureLocation ()

    @Override
    public ResourceLocation getAnimationFileLocation(T animatable) {
        return animatable.getAnimator();
    } // getAnimationFileLocation ()

    @Override
    public void setLivingAnimations(T animatable, Integer uniqueID, AnimationEvent event){
        super.setLivingAnimations(animatable, uniqueID, event);
        InternalAnimation.headAnimation(this, event);
    } // setLivingAnimations ()

} // Class InternalModel