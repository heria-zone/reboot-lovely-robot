package net.msymbios.rlovelyr.entity.client.layer;

import net.msymbios.rlovelyr.common.entity.InternalLayer;
import net.msymbios.rlovelyr.config.LovelyRobotResource;
import net.msymbios.rlovelyr.entity.custom.HyperionEntity;
import software.bernie.geckolib.renderer.GeoRenderer;

public class HyperionLayer extends InternalLayer<HyperionEntity> {

    // -- Constructor --

    public HyperionLayer(GeoRenderer<HyperionEntity> entityRendererIn) {
        super(entityRendererIn);
        layerBaseDefence = LovelyRobotResource.HYPERION_LAYER_BASE_DEFENSE;
        layerAutoAttack = LovelyRobotResource.HYPERION_LAYER_AUTO_ATTACK;
    } // Constructor HyperionLayer ()

} // Class HyperionLayer