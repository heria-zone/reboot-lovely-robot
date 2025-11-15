package net.msymbios.rlovelyr.entity.client.layer;

import net.msymbios.rlovelyr.config.LovelyRobotResource;
import net.msymbios.rlovelyr.entity.custom.BunnyEntity;
import net.msymbios.rlovelyr.entity.internal.InternalLayer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class BunnyLayer extends InternalLayer<BunnyEntity> {

    // -- Constructor --

    public BunnyLayer(IGeoRenderer<BunnyEntity> entityRendererIn) {
        super(entityRendererIn);
        layerIdle = LovelyRobotResource.BUNNY_LAYER_EMPTY;
        layerBaseDefence = LovelyRobotResource.BUNNY_LAYER_BASE_DEFENSE;
        layerAutoAttack = LovelyRobotResource.BUNNY_LAYER_AUTO_ATTACK;
    } // Constructor BunnyLayer ()

} // Class BunnyLayer