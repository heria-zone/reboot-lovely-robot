package net.msymbios.rlovelyr.entity.client.layer;

import net.msymbios.rlovelyr.entity.custom.VanillaEntity;
import net.msymbios.rlovelyr.common.entity.InternalLayer;
import software.bernie.geckolib.renderer.GeoRenderer;

public class VanillaLayer extends InternalLayer<VanillaEntity> {

    // -- Constructor --

    public VanillaLayer(GeoRenderer<VanillaEntity> entityRendererIn) {
        super(entityRendererIn);
    } // Constructor VanillaLayer ()

} // Class VanillaLayer