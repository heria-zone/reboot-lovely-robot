package net.msymbios.rlovelyr.entity.client;

import net.msymbios.rlovelyr.entity.custom.VanillaEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class VanillaModel extends AnimatedGeoModel<VanillaEntity> {

    // -- Methods --
    @Override
    public ResourceLocation getModelResource(VanillaEntity animatable) {
        return animatable.getCurrentModel();
    } // getModelResource ()

    @Override
    public ResourceLocation getTextureResource(VanillaEntity animatable) {
        return animatable.getTexture();
    } // getTextureResource ()

    @Override
    public ResourceLocation getAnimationResource(VanillaEntity animatable) {
        return animatable.getCurrentAnimator();
    } // getAnimationResource ()

} // Class VanillaModel