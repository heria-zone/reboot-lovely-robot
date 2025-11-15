package net.msymbios.rlovelyr.entity.client.layer;

import net.msymbios.rlovelyr.common.entity.InternalLayer;
import net.msymbios.rlovelyr.config.LovelyRobotResource;
import net.msymbios.rlovelyr.entity.custom.BunnyEntity;
import software.bernie.geckolib.renderer.GeoRenderer;

public class BunnyLayer extends InternalLayer<BunnyEntity> {

    // -- Constructor --

    public BunnyLayer(GeoRenderer<BunnyEntity> entityRendererIn) {
        super(entityRendererIn);
        layerIdle = LovelyRobotResource.BUNNY_LAYER_EMPTY;
        layerBaseDefence = LovelyRobotResource.BUNNY_LAYER_BASE_DEFENSE;
        layerAutoAttack = LovelyRobotResource.BUNNY_LAYER_AUTO_ATTACK;
    } // Constructor BunnyLayer ()

} // Class BunnyLayer