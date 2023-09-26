package net.msymbios.rlovelyr.client.model;

import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.custom.BunnyEntity;
import net.msymbios.rlovelyr.entity.internal.InternalAnimation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BunnyModel extends AnimatedGeoModel<BunnyEntity> {

    // -- Methods --
    @Override
    public Identifier getModelResource(BunnyEntity animatable) {
        return animatable.getCurrentModel();
    } // getModelResource ()

    @Override
    public Identifier getTextureResource(BunnyEntity animatable) {
        return animatable.getTexture();
    } // getTextureResource ()

    @Override
    public Identifier getAnimationResource(BunnyEntity animatable) {
        return animatable.getAnimator();
    } // getAnimationResource ()

    @Override
    public void setLivingAnimations(BunnyEntity animatable, Integer uniqueID, AnimationEvent event){
        super.setLivingAnimations(animatable, uniqueID, event);
        InternalAnimation.headAnimation(this, event);
    } // setLivingAnimations ()

} // Class BunnyModel