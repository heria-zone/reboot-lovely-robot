package net.msymbios.rlovelyr.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.client.model.HoneyModel;
import net.msymbios.rlovelyr.entity.custom.HoneyEntity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HoneyRenderer extends GeoEntityRenderer<HoneyEntity> {

    // -- Constructor --
    public HoneyRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new HoneyModel());
        this.shadowRadius = InternalMetric.ShadowRadius;
    } // Constructor HoneyRenderer ()

    // -- Methods --
    @Override
    public Identifier getTextureLocation(HoneyEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class HoneyRenderer