package net.msymbios.rlovelyr.entity.client.layer;

import net.msymbios.rlovelyr.entity.custom.NekoEntity;
import net.msymbios.rlovelyr.entity.internal.InternalLayer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class NekoLayer extends InternalLayer<NekoEntity> {

    // -- Constructor --

    public NekoLayer(IGeoRenderer<NekoEntity> entityRendererIn) {
        super(entityRendererIn);
    } // Constructor NekoLayer ()

} // Class NekoLayer