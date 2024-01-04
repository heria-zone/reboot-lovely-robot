package net.msymbios.rlovelyr.entity.client.model;

import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.custom.DragonEntity;
import net.msymbios.rlovelyr.entity.internal.InternalAnimation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class DragonModel extends AnimatedGeoModel<DragonEntity> {

    // -- Inherited Methods --
    @Override
    public Identifier getModelLocation(DragonEntity animatable) {
        return animatable.getCurrentModel();
    } // getModelLocation ()

    @Override
    public Identifier getTextureLocation(DragonEntity animatable) {
        return animatable.getTexture();
    } // getTextureLocation ()

    @Override
    public Identifier getAnimationFileLocation(DragonEntity animatable) {
        return animatable.getAnimator();
    } // getAnimationFileLocation ()

    @Override
    public void setLivingAnimations(DragonEntity animatable, Integer uniqueID, AnimationEvent event){
        super.setLivingAnimations(animatable, uniqueID, event);
        InternalAnimation.headAnimation(this, event);
    } // setLivingAnimations ()

} // Class DragonModel