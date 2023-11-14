package net.msymbios.rlovelyr.entity.client.model;

import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.custom.NekoEntity;
import net.msymbios.rlovelyr.entity.internal.InternalAnimation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class NekoModel extends AnimatedGeoModel<NekoEntity> {

    // -- Inherited Methods --
    @Override
    public Identifier getModelLocation(NekoEntity animatable) {
        return animatable.getCurrentModel();
    } // getModelLocation ()

    @Override
    public Identifier getTextureLocation(NekoEntity animatable) {
        return animatable.getTexture();
    } // getTextureLocation ()

    @Override
    public Identifier getAnimationFileLocation(NekoEntity animatable) {
        return animatable.getAnimator();
    } // getAnimationFileLocation ()

    @Override
    public void setLivingAnimations(NekoEntity animatable, Integer uniqueID, AnimationEvent event){
        super.setLivingAnimations(animatable, uniqueID, event);
        InternalAnimation.headAnimation(this, event);
    } // setLivingAnimations ()

} // Class NekoModel