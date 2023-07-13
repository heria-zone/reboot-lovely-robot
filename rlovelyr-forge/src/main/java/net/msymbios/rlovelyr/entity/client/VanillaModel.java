package net.msymbios.rlovelyr.entity.client;

import net.minecraft.util.ResourceLocation;
import net.msymbios.rlovelyr.entity.custom.VanillaEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class VanillaModel extends AnimatedGeoModel<VanillaEntity> {

    // -- Methods --
    @Override
    public ResourceLocation getModelLocation(VanillaEntity animatable) {
        return animatable.getCurrentModel();
    } // getModelLocation ()

    @Override
    public ResourceLocation getTextureLocation(VanillaEntity animatable) {
        return animatable.getTexture();
    } // getTextureLocation ()

    @Override
    public ResourceLocation getAnimationFileLocation(VanillaEntity animatable) {
        return animatable.getCurrentAnimator();
    } // getAnimationFileLocation ()

} // Class VanillaModel