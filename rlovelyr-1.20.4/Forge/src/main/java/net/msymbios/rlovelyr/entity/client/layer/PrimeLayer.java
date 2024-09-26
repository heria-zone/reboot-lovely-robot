package net.msymbios.rlovelyr.entity.client.layer;

import net.msymbios.rlovelyr.common.entity.InternalLayer;
import net.msymbios.rlovelyr.config.LovelyRobotResource;
import net.msymbios.rlovelyr.entity.custom.PrimeEntity;
import software.bernie.geckolib.renderer.GeoRenderer;

public class PrimeLayer extends InternalLayer<PrimeEntity> {

    // -- Constructor --

    public PrimeLayer(GeoRenderer<PrimeEntity> entityRendererIn) {
        super(entityRendererIn);
        layerBaseDefence = LovelyRobotResource.PRIME_LAYER_BASE_DEFENSE;
        layerAutoAttack = LovelyRobotResource.PRIME_LAYER_AUTO_ATTACK;
    } // Constructor PrimeLayer ()

} // Class PrimeLayer