package net.msymbios.rlovelyr.entity.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.msymbios.rlovelyr.entity.client.layer.HoneyLayer;
import net.msymbios.rlovelyr.entity.client.model.HoneyModel;
import net.msymbios.rlovelyr.entity.custom.HoneyEntity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class HoneyRenderer extends GeoEntityRenderer<HoneyEntity> {

    // -- Constructor --
    public HoneyRenderer(EntityRendererManager renderManager) {
        super(renderManager, new HoneyModel());
        this.shadowRadius = InternalMetric.SHADOW_RADIUS.get();
        addLayer(new HoneyLayer(this));
    } // Constructor HoneyRenderer ()

    // -- Methods --
    @Override
    public ResourceLocation getTextureLocation(HoneyEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class HoneyRenderer