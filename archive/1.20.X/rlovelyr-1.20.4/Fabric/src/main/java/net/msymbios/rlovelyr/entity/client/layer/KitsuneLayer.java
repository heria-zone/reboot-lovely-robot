package net.msymbios.rlovelyr.entity.client.layer;

import net.msymbios.rlovelyr.common.entity.InternalLayer;
import net.msymbios.rlovelyr.config.LovelyRobotResource;
import net.msymbios.rlovelyr.entity.custom.KitsuneEntity;
import software.bernie.geckolib.renderer.GeoRenderer;

public class KitsuneLayer extends InternalLayer<KitsuneEntity> {

    // -- Constructor --

    public KitsuneLayer(GeoRenderer<KitsuneEntity> entityRendererIn) {
        super(entityRendererIn);
        layerIdle = LovelyRobotResource.GENERAL_LAYER_EMPTY;
        layerBaseDefence = LovelyRobotResource.KITSUNE_LAYER_BASE_DEFENSE;
        layerAutoAttack = LovelyRobotResource.KITSUNE_LAYER_AUTO_ATTACK;
    } // Constructor KitsuneLayer ()

} // Class KitsuneLayer