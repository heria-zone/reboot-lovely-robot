package net.msymbios.rlovelyr.entity.client;

import net.msymbios.rlovelyr.entity.custom.HoneyEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HoneyModel extends GeoModel<HoneyEntity> {

    // -- Methods --
    @Override
    public ResourceLocation getModelResource(HoneyEntity animatable) {
        return animatable.getCurrentModel();
    } // getModelResource ()

    @Override
    public ResourceLocation getTextureResource(HoneyEntity animatable) {
        return animatable.getTexture();
    } // getTextureResource ()

    @Override
    public ResourceLocation getAnimationResource(HoneyEntity animatable) {
        return animatable.getCurrentAnimator();
    } // getAnimationResource ()

} // Class HoneyModel