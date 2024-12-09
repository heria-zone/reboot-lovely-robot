package net.msymbios.rlovelyr.entity.client.layer;

import net.msymbios.rlovelyr.common.entity.InternalLayer;
import net.msymbios.rlovelyr.entity.custom.StingerEntity;
import software.bernie.geckolib.renderer.GeoRenderer;

public class StingerLayer extends InternalLayer<StingerEntity> {

    // -- Constructor --

    public StingerLayer(GeoRenderer<StingerEntity> entityRendererIn) {
        super(entityRendererIn);
    } // Constructor StingerLayer ()

} // Class StingerLayer