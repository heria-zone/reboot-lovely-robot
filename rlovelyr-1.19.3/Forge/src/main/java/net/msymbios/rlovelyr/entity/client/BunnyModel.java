package net.msymbios.rlovelyr.entity.client;

import net.msymbios.rlovelyr.entity.custom.BunnyEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BunnyModel extends GeoModel<BunnyEntity> {

    // -- Methods --
    @Override
    public ResourceLocation getModelResource(BunnyEntity animatable) {
        return animatable.getCurrentModel();
    } // getModelResource ()

    @Override
    public ResourceLocation getTextureResource(BunnyEntity animatable) {
        return animatable.getTexture();
    } // getTextureResource ()

    @Override
    public ResourceLocation getAnimationResource(BunnyEntity animatable) {
        return animatable.getCurrentAnimator();
    } // getAnimationResource ()

} // Class BunnyModel