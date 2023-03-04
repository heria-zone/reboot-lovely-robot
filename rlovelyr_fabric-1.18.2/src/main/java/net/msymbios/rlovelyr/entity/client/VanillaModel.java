package net.msymbios.rlovelyr.entity.client;

import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.custom.VanillaEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class VanillaModel extends AnimatedGeoModel<VanillaEntity> {

    // -- Methods --
    @Override
    public Identifier getModelLocation(VanillaEntity instance) {
        return instance.getCurrentModel();
    } // getModelLocation ()

    @Override
    public Identifier getTextureLocation(VanillaEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

    @Override
    public Identifier getAnimationFileLocation(VanillaEntity instance) {
        return instance.getCurrentAnimator();
    } // getAnimationFileLocation ()

} // Class VanillaModel