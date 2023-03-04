package net.msymbios.rlovelyr.entity.client;

import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.custom.Bunny2Entity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class Bunny2Model extends AnimatedGeoModel<Bunny2Entity> {

    // -- Methods --
    @Override
    public Identifier getModelResource(Bunny2Entity instance){
        return instance.getCurrentModel();
    } // getModelResource ()

    @Override
    public Identifier getTextureResource(Bunny2Entity instance) {
        return instance.getTexture();
    } // getTextureResource ()

    @Override
    public Identifier getAnimationResource(Bunny2Entity instance) {
        return instance.getCurrentAnimator();
    } // getRenderType ()

} // Class Bunny2Model