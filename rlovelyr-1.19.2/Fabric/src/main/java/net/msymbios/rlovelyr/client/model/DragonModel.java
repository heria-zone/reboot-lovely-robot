package net.msymbios.rlovelyr.client.model;

import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.custom.DragonEntity;
import net.msymbios.rlovelyr.entity.internal.InternalAnimation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class DragonModel extends AnimatedGeoModel<DragonEntity> {

    // -- Methods --
    @Override
    public Identifier getModelResource(DragonEntity animatable) {
        return animatable.getCurrentModel();
    } // getModelResource ()

    @Override
    public Identifier getTextureResource(DragonEntity animatable) {
        return animatable.getTexture();
    } // getTextureResource ()

    @Override
    public Identifier getAnimationResource(DragonEntity animatable) {
        return animatable.getAnimator();
    } // getAnimationResource ()

    @Override
    public void setLivingAnimations(DragonEntity animatable, Integer uniqueID, AnimationEvent event){
        super.setLivingAnimations(animatable, uniqueID, event);
        InternalAnimation.headAnimation(this, event);
    } // setLivingAnimations ()

} // Class DragonModel