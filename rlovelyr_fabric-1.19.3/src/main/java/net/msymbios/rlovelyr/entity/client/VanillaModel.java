package net.msymbios.rlovelyr.entity.client;

import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.LovelyRobotMod;
import net.msymbios.rlovelyr.entity.custom.VanillaEntity;
import software.bernie.geckolib.model.GeoModel;

public class VanillaModel extends GeoModel<VanillaEntity> {

    // -- Methods --
    @Override
    public Identifier getModelResource(VanillaEntity animatable) {
        return new Identifier(LovelyRobotMod.MODID, "geo/vanilla.geo.json");
    } // getModelResource ()

    @Override
    public Identifier getTextureResource(VanillaEntity animatable) {
        return new Identifier(LovelyRobotMod.MODID, "textures/entity/vanilla/vanilla_00.png");
    } // getTextureResource ()

    @Override
    public Identifier getAnimationResource(VanillaEntity animatable) {
        return new Identifier(LovelyRobotMod.MODID, "animations/lovelyrobot.animation.json");
    } // getAnimationResource ()

} // Class VanillaModel