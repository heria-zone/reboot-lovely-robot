package net.msymbios.rlovelyr.entity.client;

import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.custom.VanillaEntity;
import software.bernie.geckolib.model.GeoModel;

public class VanillaModel extends GeoModel<VanillaEntity> {

    // -- Methods --
    @Override
    public Identifier getModelResource(VanillaEntity animatable) {
        return animatable.getCurrentModel();
    } // getModelResource ()

    @Override
    public Identifier getTextureResource(VanillaEntity animatable) {
        return animatable.getCurrentTexture();
    } // getTextureResource ()

    @Override
    public Identifier getAnimationResource(VanillaEntity animatable) {
        return animatable.ENTITY_ANIMATION;
    } // getAnimationResource ()

} // Class VanillaModel