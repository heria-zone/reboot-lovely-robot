package net.msymbios.rlovelyr.entity.client.renderer;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.client.layer.HoneyLayer;
import net.msymbios.rlovelyr.entity.client.model.HoneyModel;
import net.msymbios.rlovelyr.entity.custom.HoneyEntity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import software.bernie.geckolib3.renderer.geo.GeoEntityRenderer;

public class HoneyRenderer extends GeoEntityRenderer<HoneyEntity> {

    // -- Constructor --
    public HoneyRenderer(EntityRenderDispatcher renderManager, EntityRendererRegistry.Context context) {
        super(renderManager, new HoneyModel());
        this.shadowRadius = InternalMetric.SHADOW_RADIUS;
        addLayer(new HoneyLayer(this));
    } // Constructor HoneyRenderer ()

    // -- Inherited Methods --
    @Override
    public Identifier getTextureLocation(HoneyEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class HoneyRenderer