package net.msymbios.rlovelyr.entity.client.layer;

import net.msymbios.rlovelyr.entity.custom.DragonEntity;
import net.msymbios.rlovelyr.entity.internal.InternalLayer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class DragonLayer extends InternalLayer<DragonEntity> {

    // -- Constructor --

    public DragonLayer(IGeoRenderer<DragonEntity> entityRendererIn) {
        super(entityRendererIn);
    } // Constructor DragonLayer ()

} // Class DragonLayer