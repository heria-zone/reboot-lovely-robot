package net.msymbios.rlovelyr.entity.client;

import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.custom.BunnyEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BunnyModel extends AnimatedGeoModel<BunnyEntity> {

    // -- Methods --
    @Override
    public Identifier getModelLocation(BunnyEntity instance) {
        return instance.getCurrentModel();
    } // getModelLocation ()

    @Override
    public Identifier getTextureLocation(BunnyEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

    @Override
    public Identifier getAnimationFileLocation(BunnyEntity instance) {
        return instance.getCurrentAnimator();
    } // getAnimationFileLocation ()

} // Class BunnyModel