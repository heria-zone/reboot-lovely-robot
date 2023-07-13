package net.msymbios.rlovelyr.entity.client;

import net.minecraft.util.ResourceLocation;
import net.msymbios.rlovelyr.entity.custom.BunnyEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BunnyModel extends AnimatedGeoModel<BunnyEntity> {

    // -- Methods --
    @Override
    public ResourceLocation getModelLocation(BunnyEntity animatable) {
        return animatable.getCurrentModel();
    } // getModelLocation ()

    @Override
    public ResourceLocation getTextureLocation(BunnyEntity animatable) {
        return animatable.getTexture();
    } // getTextureLocation ()

    @Override
    public ResourceLocation getAnimationFileLocation(BunnyEntity animatable) {
        return animatable.getCurrentAnimator();
    } // getAnimationFileLocation ()

} // Class BunnyModel