package net.msymbios.rlovelyr.entity.client.layer;

import net.msymbios.rlovelyr.entity.custom.VanillaEntity;
import net.msymbios.rlovelyr.entity.internal.InternalLayer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class VanillaLayer extends InternalLayer<VanillaEntity> {

    // -- Constructor --

    public VanillaLayer(IGeoRenderer<VanillaEntity> entityRendererIn) {
        super(entityRendererIn);
    } // Constructor VanillaLayer ()

} // Class VanillaLayer