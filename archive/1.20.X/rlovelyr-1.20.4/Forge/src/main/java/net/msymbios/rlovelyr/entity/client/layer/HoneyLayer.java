package net.msymbios.rlovelyr.entity.client.layer;

import net.msymbios.rlovelyr.entity.custom.HoneyEntity;
import net.msymbios.rlovelyr.common.entity.InternalLayer;
import software.bernie.geckolib.renderer.GeoRenderer;

public class HoneyLayer extends InternalLayer<HoneyEntity> {

    // -- Constructor --

    public HoneyLayer(GeoRenderer<HoneyEntity> entityRendererIn) {
        super(entityRendererIn);
    } // Constructor HoneyLayer ()

} // Class HoneyLayer