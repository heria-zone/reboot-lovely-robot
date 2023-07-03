package net.msymbios.rlovelyr.entity.client;

import net.msymbios.rlovelyr.entity.custom.Bunny2Entity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class Bunny2Model extends AnimatedGeoModel<Bunny2Entity> {

    // -- Methods --
    @Override
    public ResourceLocation getModelResource(Bunny2Entity animatable) {
        return animatable.getCurrentModel();
    } // getModelResource ()

    @Override
    public ResourceLocation getTextureResource(Bunny2Entity animatable) {
        return animatable.getTexture();
    } // getTextureResource ()

    @Override
    public ResourceLocation getAnimationResource(Bunny2Entity animatable) {
        return animatable.getCurrentAnimator();
    } // getAnimationResource ()

} // Class Bunny2Model