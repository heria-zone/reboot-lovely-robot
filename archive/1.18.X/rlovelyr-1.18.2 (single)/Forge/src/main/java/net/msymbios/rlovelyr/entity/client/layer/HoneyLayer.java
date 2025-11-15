package net.msymbios.rlovelyr.entity.client.layer;

import net.msymbios.rlovelyr.entity.custom.HoneyEntity;
import net.msymbios.rlovelyr.entity.internal.InternalLayer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class HoneyLayer extends InternalLayer<HoneyEntity> {

    // -- Constructor --

    public HoneyLayer(IGeoRenderer<HoneyEntity> entityRendererIn) {
        super(entityRendererIn);
    } // Constructor HoneyLayer ()

} // Class HoneyLayer