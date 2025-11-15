package net.msymbios.rlovelyr.entity.client.layer;

import net.msymbios.rlovelyr.common.entity.InternalLayer;
import net.msymbios.rlovelyr.entity.custom.NekoEntity;
import software.bernie.geckolib.renderer.GeoRenderer;

public class NekoLayer extends InternalLayer<NekoEntity> {

    // -- Constructor --

    public NekoLayer(GeoRenderer<NekoEntity> entityRendererIn) {
        super(entityRendererIn);
    } // Constructor NekoLayer ()

} // Class NekoLayer