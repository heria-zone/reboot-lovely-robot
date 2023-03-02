package com.msymbios.rlovelyr.entity.client;

import com.msymbios.rlovelyr.entity.custom.Bunny2Entity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class Bunny2Model extends GeoModel<Bunny2Entity> {

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