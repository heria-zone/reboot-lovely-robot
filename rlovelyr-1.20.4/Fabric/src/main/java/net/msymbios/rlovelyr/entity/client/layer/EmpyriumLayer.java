package net.msymbios.rlovelyr.entity.client.layer;

import net.msymbios.rlovelyr.common.entity.InternalLayer;
import net.msymbios.rlovelyr.config.LovelyRobotResource;
import net.msymbios.rlovelyr.entity.custom.EmpyriumEntity;
import software.bernie.geckolib.renderer.GeoRenderer;

public class EmpyriumLayer extends InternalLayer<EmpyriumEntity> {

    // -- Constructor --

    public EmpyriumLayer(GeoRenderer<EmpyriumEntity> entityRendererIn) {
        super(entityRendererIn);
        layerBaseDefence = LovelyRobotResource.PRIME_LAYER_BASE_DEFENSE;
        layerAutoAttack = LovelyRobotResource.PRIME_LAYER_AUTO_ATTACK;
    } // Constructor EmpyriumLayer ()

} // Class EmpyriumLayer