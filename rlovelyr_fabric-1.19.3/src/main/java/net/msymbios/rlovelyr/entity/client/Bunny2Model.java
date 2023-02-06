package net.msymbios.rlovelyr.entity.client;

import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.custom.Bunny2Entity;
import software.bernie.geckolib.model.GeoModel;

public class Bunny2Model extends GeoModel<Bunny2Entity> {

    // -- Methods --
    @Override
    public Identifier getModelResource(Bunny2Entity animatable) {
        return animatable.getCurrentModel();
    } // getModelResource ()

    @Override
    public Identifier getTextureResource(Bunny2Entity animatable) {
        return animatable.getCurrentTexture();
    } // getTextureResource ()

    @Override
    public Identifier getAnimationResource(Bunny2Entity animatable) {
        return animatable.getCurrentAnimator();
    } // getAnimationResource ()

} // Class Bunny2Model