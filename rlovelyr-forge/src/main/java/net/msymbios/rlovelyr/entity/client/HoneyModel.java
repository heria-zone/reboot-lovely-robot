package net.msymbios.rlovelyr.entity.client;

import net.minecraft.util.ResourceLocation;
import net.msymbios.rlovelyr.entity.custom.HoneyEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class HoneyModel extends AnimatedGeoModel<HoneyEntity> {

    // -- Methods --
    @Override
    public ResourceLocation getModelLocation(HoneyEntity animatable) {
        return animatable.getCurrentModel();
    } // getModelLocation ()

    @Override
    public ResourceLocation getTextureLocation(HoneyEntity animatable) {
        return animatable.getTexture();
    } // getTextureLocation ()

    @Override
    public ResourceLocation getAnimationFileLocation(HoneyEntity animatable) {
        return animatable.getCurrentAnimator();
    } // getAnimationFileLocation ()

} // Class HoneyModel