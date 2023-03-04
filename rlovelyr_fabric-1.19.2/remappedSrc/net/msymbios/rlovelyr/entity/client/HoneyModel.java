package net.msymbios.rlovelyr.entity.client;

import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.custom.HoneyEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class HoneyModel extends AnimatedGeoModel<HoneyEntity> {

    // -- Methods --
    @Override
    public Identifier getModelResource(HoneyEntity animatable) {
        return animatable.getCurrentModel();
    } // getModelResource ()

    @Override
    public Identifier getTextureResource(HoneyEntity animatable) {
        return animatable.getTexture();
    } // getTextureResource ()

    @Override
    public Identifier getAnimationResource(HoneyEntity animatable) {
        return animatable.getCurrentAnimator();
    } // getAnimationResource ()

} // Class HoneyModel