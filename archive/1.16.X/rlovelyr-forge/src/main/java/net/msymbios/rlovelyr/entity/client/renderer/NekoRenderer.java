package net.msymbios.rlovelyr.entity.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.msymbios.rlovelyr.entity.client.layer.NekoLayer;
import net.msymbios.rlovelyr.entity.client.model.NekoModel;
import net.msymbios.rlovelyr.entity.custom.NekoEntity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class NekoRenderer extends GeoEntityRenderer<NekoEntity> {

    // -- Constructor --
    public NekoRenderer(EntityRendererManager renderManager) {
        super(renderManager, new NekoModel());
        this.shadowRadius = InternalMetric.SHADOW_RADIUS.get();
        addLayer(new NekoLayer(this));
    } // Constructor NekoRenderer ()

    // -- Methods --
    @Override
    public ResourceLocation getTextureLocation(NekoEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class NekoRenderer