package net.msymbios.rlovelyr.entity.client;

import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.custom.BunnyEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BunnyModel extends AnimatedGeoModel<BunnyEntity> {

    // -- Methods --
    @Override
    public Identifier getModelResource(BunnyEntity instance) {
        return instance.getCurrentModel();
    } // getModelResource ()

    @Override
    public Identifier getTextureResource(BunnyEntity instance) {
        return instance.getTexture();
    } // getTextureResource ()

    @Override
    public Identifier getAnimationResource(BunnyEntity instance) {
        return instance.getCurrentAnimator();
    } // getAnimationResource ()

} // Class BunnyModel