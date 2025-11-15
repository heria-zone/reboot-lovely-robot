package net.msymbios.rlovelyr.entity.client;

import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.custom.Bunny2Entity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class Bunny2Model extends AnimatedGeoModel<Bunny2Entity> {

    // -- Methods --
    @Override
    public Identifier getModelLocation(Bunny2Entity animatable) {
        return animatable.getCurrentModel();
    } // getModelLocation ()

    @Override
    public Identifier getTextureLocation(Bunny2Entity animatable) {
        return animatable.getTexture();
    } // getTextureLocation ()

    @Override
    public Identifier getAnimationFileLocation(Bunny2Entity animatable) {
        return animatable.getCurrentAnimator();
    } // getAnimationFileLocation ()

} // Class Bunny2Model