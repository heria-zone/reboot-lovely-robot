package net.msymbios.rlovelyr.entity.client.layer;

import net.msymbios.rlovelyr.common.entity.InternalLayer;
import net.msymbios.rlovelyr.entity.custom.DragonEntity;
import software.bernie.geckolib.renderer.GeoRenderer;

public class DragonLayer extends InternalLayer<DragonEntity> {

    // -- Constructor --

    public DragonLayer(GeoRenderer<DragonEntity> entityRendererIn) {
        super(entityRendererIn);
    } // Constructor DragonLayer ()

} // Class DragonLayer