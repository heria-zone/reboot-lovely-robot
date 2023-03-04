package net.msymbios.rlovelyr.entity.client;

import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.custom.Bunny2Entity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class Bunny2Model extends AnimatedGeoModel<Bunny2Entity> {

    // -- Methods --
    @Override
    public Identifier getModelLocation(Bunny2Entity instance){
        return instance.getCurrentModel();
    } // getModelLocation ()

    @Override
    public Identifier getTextureLocation(Bunny2Entity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

    @Override
    public Identifier getAnimationFileLocation(Bunny2Entity instance) {
        return instance.getCurrentAnimator();
    } // getAnimationFileLocation ()

} // Class Bunny2Model