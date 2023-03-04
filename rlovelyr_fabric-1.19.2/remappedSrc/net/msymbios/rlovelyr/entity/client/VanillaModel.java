package net.msymbios.rlovelyr.entity.client;

import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.custom.VanillaEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class VanillaModel extends AnimatedGeoModel<VanillaEntity> {

    // -- Methods --
    @Override
    public Identifier getModelResource(VanillaEntity animatable) {
        return animatable.getCurrentModel();
    } // getModelResource ()

    @Override
    public Identifier getTextureResource(VanillaEntity animatable) {
        return animatable.getTexture();
    } // getTextureResource ()

    @Override
    public Identifier getAnimationResource(VanillaEntity animatable) {
        return animatable.getCurrentAnimator();
    } // getAnimationResource ()

} // Class VanillaModel