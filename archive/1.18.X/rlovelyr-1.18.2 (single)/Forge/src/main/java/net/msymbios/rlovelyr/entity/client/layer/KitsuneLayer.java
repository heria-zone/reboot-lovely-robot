package net.msymbios.rlovelyr.entity.client.layer;

import net.msymbios.rlovelyr.config.LovelyRobotResource;
import net.msymbios.rlovelyr.entity.custom.KitsuneEntity;
import net.msymbios.rlovelyr.entity.internal.InternalLayer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class KitsuneLayer extends InternalLayer<KitsuneEntity> {

    // -- Constructor --

    public KitsuneLayer(IGeoRenderer<KitsuneEntity> entityRendererIn) {
        super(entityRendererIn);
        layerIdle = LovelyRobotResource.GENERAL_LAYER_EMPTY;
        layerBaseDefence = LovelyRobotResource.KITSUNE_LAYER_BASE_DEFENSE;
        layerAutoAttack = LovelyRobotResource.KITSUNE_LAYER_AUTO_ATTACK;
    } // Constructor KitsuneLayer ()

} // Class KitsuneLayer